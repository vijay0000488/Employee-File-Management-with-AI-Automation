import os
import requests
from tika import parser  # pip install tika
import json

OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "phi3"  # or "llama3"
EMPLOYEE_API_URL = "http://localhost:8080/api/employees/{}"

def extract_text(filepath):
    parsed = parser.from_file(filepath)
    return parsed.get("content", "")

def analyze_with_ollama(text, user_query, model=MODEL):
    prompt = f"{user_query}\n{text}"
    data = {
        "model": model,
        "prompt": prompt
    }
    response = requests.post(OLLAMA_URL, json=data, timeout=120, stream=True)
    output = ""
    for line in response.iter_lines():
        if not line:
            continue
        try:
            chunk = line.decode("utf-8")
            obj = json.loads(chunk)
            output += obj.get("response", "")
        except Exception as e:
            print("Error reading Ollama response line:", e)
    return output

def get_employee_json(employee_id):
    url = EMPLOYEE_API_URL.format(employee_id)
    response = requests.get(url)
    if response.status_code == 200:
        return response.json()
    return None

def main():
    employee_id = input("Enter Employee ID: ").strip()
    employee = get_employee_json(employee_id)
    if not employee:
        print("Employee not found.")
        return

    user_query = input("Enter your query for the AI about this employee:\n").strip()
    if not user_query:
        user_query = "Extract all skills, qualifications, and work experience from this resume:"

    if "role" in user_query.lower() or "position" in user_query.lower():
        print("Role:", employee.get("role", "Not found"))
        return

    # For other queries, process resume/document files
    test_folder = "./ENGINEERING"
    for filename in os.listdir(test_folder):
        filepath = os.path.join(test_folder, filename)
        print(f"\nProcessing: {filename}")
        try:
            text = extract_text(filepath)
            if not text or not text.strip():
                print("No text extracted. Skipping.")
                continue
            result = analyze_with_ollama(text, user_query)
            print("AI Analysis:\n", result)
        except Exception as e:
            print(f"Failed to process {filename}: {e}")

if __name__ == "__main__":
    main()