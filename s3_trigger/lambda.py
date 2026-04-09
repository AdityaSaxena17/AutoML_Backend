import json
import urllib.parse
import boto3
import urllib3

s3 = boto3.client('s3')
http = urllib3.PoolManager()

def lambda_handler(event, context):
    # 1. Get bucket and key from the event
    bucket = event['Records'][0]['s3']['bucket']['name']
    key = urllib.parse.unquote_plus(event['Records'][0]['s3']['object']['key'], encoding='utf-8')

    # 2. Only process if it's the output.json file
    if not key.endswith('output.json'):
        print(f"Skipping file: {key}")
        return

    try:
        # 3. Read the JSON content from S3
        response = s3.get_object(Bucket=bucket, Key=key)
        json_content = response['Body'].read().decode('utf-8')
        job_data = json.loads(json_content)
        
        print(f"Processing Job ID: {job_data.get('jobid')}")

        # 4. Send the data to your local Spring Boot StateService
        ngrok_url = "https://your-ngrok-id.ngrok-free.app/api/state/update"
        
        encoded_data = json.dumps(job_data).encode('utf-8')
        
        resp = http.request(
            'POST', 
            ngrok_url, 
            body=encoded_data,
            headers={'Content-Type': 'application/json'}
        )

        print(f"Status Service Response: {resp.status}")
        return {
            'statusCode': 200,
            'body': json.dumps('Status updated successfully')
        }

    except Exception as e:
        print(f"Error: {e}")
        raise e