import os
import requests

def get_api_key():
    try:
        with open("secrets.local.bat", "r") as f:
            for line in f:
                if line.startswith("set GEMINI_API_KEY="):
                    return line.strip().split("=")[1]
    except FileNotFoundError:
        print("secrets.local.bat not found.")
        return None
    return None

def list_models():
    api_key = get_api_key()
    if not api_key:
        print("Could not find API Key.")
        return

    url = f"https://generativelanguage.googleapis.com/v1beta/models?key={api_key}"
    response = requests.get(url)
    
    if response.status_code == 200:
        models = response.json().get('models', [])
        print("Available Models:")
        for m in models:
            if 'generateContent' in m.get('supportedGenerationMethods', []):
                print(f"- {m['name']}")
    else:
        print(f"Error: {response.status_code}")
        print(response.text)

if __name__ == "__main__":
    list_models()
