import os
import json
import boto3
import joblib
import pandas as pd
from urllib.parse import urlparse
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.svm import SVC
from sklearn.neural_network import MLPClassifier
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score, silhouette_score

def parse_s3_path(s3_path):
    parsed = urlparse(s3_path)
    return parsed.netloc, parsed.path.lstrip('/')

def run_glassbox_task():
    # 1. Capture Environment Variables
    job_id = os.environ.get('JOB_ID', 'unknown-job')
    user_id = os.environ.get('USER_ID', 'unknown-user')
    input_s3_path = os.environ.get('INPUT_S3_PATH')
    output_s3_prefix = os.environ.get('OUTPUT_S3_PREFIX')
    model_type = os.environ.get('MODEL_TYPE', 'LogisticRegression') # The new key variable

    results_data = {
        "jobid": job_id,
        "userid": user_id,
        "status": "jobFailed",
        "modeltype": model_type
    }

    print(f"--- Cloud Task Started: {job_id} | Model: {model_type} ---")
    s3_client = boto3.client('s3')

    try:
        # 2. Download from S3
        bucket, input_key = parse_s3_path(input_s3_path)
        local_csv = "input_data.csv"
        s3_client.download_file(bucket, input_key, local_csv)

        # 3. Load & Clean Data
        df = pd.read_csv(local_csv)
        cols_to_drop = [c for c in df.columns if str(c).lower() in ['id', 'unnamed: 0']]
        if cols_to_drop:
            df = df.drop(columns=cols_to_drop)

        # 4. Slicing & Preprocessing
        if model_type == "KMeans":
            X = df.iloc[:, :-1]
            scaler = StandardScaler()
            X_final_scaled = scaler.fit_transform(X)
        else:
            X = df.iloc[:, :-1]
            y = df.iloc[:, -1]

            le = LabelEncoder()
            if y.dtype == 'object' or str(y.dtype) == 'category':
                y = le.fit_transform(y)
                classes = [str(c) for c in le.classes_]
            else:
                classes = [str(c) for c in y.unique()]

            X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
            scaler = StandardScaler()
            X_train_scaled = scaler.fit_transform(X_train)
            X_test_scaled = scaler.transform(X_test)

        # 5. Train Selected Model
        if model_type == "LogisticRegression":
            model = LogisticRegression(max_iter=1000)
            model.fit(X_train_scaled, y_train)
        elif model_type == "SVM":
            model = SVC(kernel='rbf', probability=True, random_state=42)
            model.fit(X_train_scaled, y_train)
        elif model_type == "ANN":
            model = MLPClassifier(hidden_layer_sizes=(100,), max_iter=2000, random_state=42)
            model.fit(X_train_scaled, y_train)
        elif model_type == "KMeans":
            model = KMeans(n_clusters=3, random_state=42, n_init=10)
            clusters = model.fit_predict(X_final_scaled)
        else:
            raise ValueError(f"Unsupported model: {model_type}")

        # 6. Evaluate
        if model_type == "KMeans":
            results_payload = {
                "silhouette_score": round(float(silhouette_score(X_final_scaled, clusters)), 4),
                "inertia": round(float(model.inertia_), 2),
                "model_details": {"features": list(X.columns), "clusters": 3, "observations": len(df)}
            }
        else:
            y_pred = model.predict(X_test_scaled)
            results_payload = {
                "accuracy": round(float(accuracy_score(y_test, y_pred)), 4),
                "precision": round(float(precision_score(y_test, y_pred, average='macro')), 4),
                "recall": round(float(recall_score(y_test, y_pred, average='macro')), 4),
                "f1_score": round(float(f1_score(y_test, y_pred, average='macro')), 4),
                "model_details": {"features": list(X.columns), "classes": classes, "observations": len(df)}
            }

        # 7. Save and Upload Model (.pkl)
        local_model_path = "model.pkl"
        joblib.dump(model, local_model_path)
        
        out_bucket, out_prefix = parse_s3_path(output_s3_prefix)
        pkl_key = f"{out_prefix}model.pkl"
        s3_client.upload_file(local_model_path, out_bucket, pkl_key)

        # 8. Update JSON
        results_data.update({
            "status": "jobCompleted",
            "s3path": f"s3://{out_bucket}/{pkl_key}",
            "results": results_payload
        })

    except Exception as e:
        print(f"!!! Task Error: {str(e)}")
    
    finally:
        # 9. Always upload output.json
        try:
            local_json = "output.json"
            with open(local_json, 'w') as f:
                json.dump(results_data, f, indent=2)
            
            out_bucket, out_prefix = parse_s3_path(output_s3_prefix)
            s3_client.upload_file(local_json, out_bucket, f"{out_prefix}output.json")
            print(f"Final Status '{results_data['status']}' synced to S3.")
        except Exception as se:
            print(f"Failed to upload final status: {se}")

if __name__ == "__main__":
    run_glassbox_task()