import os
import json
import boto3
import joblib
import pandas as pd
from urllib.parse import urlparse
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import LabelEncoder, StandardScaler
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score

def parse_s3_path(s3_path):
    """Utility to split s3://bucket/key"""
    parsed = urlparse(s3_path)
    return parsed.netloc, parsed.path.lstrip('/')

def run_glassbox_task():
    # 1. Capture Environment Variables
    job_id = os.environ.get('JOB_ID', 'unknown-job')
    user_id = os.environ.get('USER_ID', 'unknown-user')
    input_s3_path = os.environ.get('INPUT_S3_PATH')
    output_s3_prefix = os.environ.get('OUTPUT_S3_PREFIX')

    # Initial result state
    results_data = {
        "jobid": job_id,
        "userid": user_id,
        "status": "jobFailed",
        "modeltype": "LogisticRegression"
    }

    print(f"--- Cloud Task Started: {job_id} ---")
    s3_client = boto3.client('s3')

    try:
        # 2. Download from S3
        bucket, input_key = parse_s3_path(input_s3_path)
        local_csv = "input_data.csv"
        s3_client.download_file(bucket, input_key, local_csv)

        # 3. Generic Processing
        df = pd.read_csv(local_csv)
        X = df.iloc[:, :-1]
        y = df.iloc[:, -1]

        # Defensive Encoding
        le = LabelEncoder()
        if y.dtype == 'object' or str(y.dtype) == 'category':
            y = le.fit_transform(y)
            classes = [str(c) for c in le.classes_]
        else:
            classes = [str(c) for c in y.unique()]

        # 4. Split and Scale
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        scaler = StandardScaler()
        X_train_scaled = scaler.fit_transform(X_train)
        X_test_scaled = scaler.transform(X_test)

        # 5. Train
        model = LogisticRegression(max_iter=1000)
        model.fit(X_train_scaled, y_train)
        y_pred = model.predict(X_test_scaled)

        # 6. Save and Upload Model (.pkl)
        local_model_path = "model.pkl"
        joblib.dump(model, local_model_path)
        
        out_bucket, out_prefix = parse_s3_path(output_s3_prefix)
        pkl_key = f"{out_prefix}model.pkl"
        s3_client.upload_file(local_model_path, out_bucket, pkl_key)

        # 7. Update results_data for Success
        results_data.update({
            "status": "jobCompleted",
            "s3path": f"s3://{out_bucket}/{pkl_key}",
            "results": {
                "accuracy": round(accuracy_score(y_test, y_pred), 4),
                "precision": round(precision_score(y_test, y_pred, average='macro'), 4),
                "recall": round(recall_score(y_test, y_pred, average='macro'), 4),
                "f1_score": round(f1_score(y_test, y_pred, average='macro'), 4),
                "model_details": {
                    "features": list(X.columns),
                    "classes": classes,
                    "observations": len(df)
                }
            }
        })

    except Exception as e:
        print(f"!!! Task Error: {str(e)}")
    
    finally:
        # 8. Always upload JSON status
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