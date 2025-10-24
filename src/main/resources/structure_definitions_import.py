import requests
import json
import glob

ID_FILTER = ['HD', 'ID', 'SI', 'ST']

def post_json_file(api_url, json_file_path):
    # Read the JSON file content
    try:
        with open(json_file_path, 'r') as file:
            json_data = json.load(file)
    except FileNotFoundError:
        print(f"Error: The file '{json_file_path}' was not found.")
        return
    except json.JSONDecodeError as e:
        print(f"Error decoding JSON in file '{json_file_path}': {e}")
        return
    
    # Extract 'code' and 'url' from the JSON data
    code = json_data.get('id')
    url = json_data.get('url')

    if not code or not url:
        print(f"Error: 'id' or 'url' not found in '{json_file_path}'. Skipping this file.")
        return
    
    if code not in ID_FILTER:
        print(f"Skipping file '{json_file_path}' with id '{code}' as it's not in the ID_FILTER.")
        return

    # Convert the JSON data to a string, preserving formatting
    json_string = json.dumps(json_data, indent=2)
    
    # Build the POST payload as specified
    payload = {
        "contentType": "logical",
        "contentFormat": "json",
        "code": code,
        "url": url,
        "content": json_string
    }
    
    # Send POST request to the API with the payload
    try:
        response = requests.post(api_url, json=payload)
        response.raise_for_status()
    except requests.exceptions.RequestException as e:
        print(f"HTTP Request failed for file '{json_file_path}': {e}")
        return
    
    # Print the response from the server
    print(f"POST request successful for file '{json_file_path}'. Status code: {response.status_code}")

# URL of the API
api_url = "https://dev.termx.org/api/structure-definitions/"

# Get a list of all JSON files in the current directory
json_files = glob.glob('st/*.json')

if not json_files:
    print("No JSON files found in the current directory.")
else:
    # Loop over each JSON file and call the post_json_file function
    for json_file_path in json_files:
        print(f"Processing file: {json_file_path}")
        post_json_file(api_url, json_file_path)
