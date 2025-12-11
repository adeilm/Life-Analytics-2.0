# Implementation Plan: AI Insights (AI Coach)

## 1. Choose AI Provider
Decide between Google Gemini .
-  Google Gemini (Free tier available, high performance).

## 2. Install Dependencies
Install the Python client for the chosen provider.
```bash
pip install google-generativeai
# OR
pip install openai
```

## 3. Secure Configuration
Store the API key in Streamlit secrets to avoid hardcoding.
- File: `frontend/.streamlit/secrets.toml`
- Content:
  ```toml
  GEMINI_API_KEY = "your_api_key_here"
  ```

## 4. Data Preparation
Format the existing dashboard data into a text prompt for the AI.
- **Inputs**: Weekly habit logs and health metric trends.
- **Format**: JSON or summarized text (e.g., "Sleep avg: 6.5h, Water: 4/7 days").

## 5. UI Integration
Add the feature to `frontend/app.py`.
- Add an "AI Insights" section.
- Add a "Generate Report" button.
- Display the AI's advice (e.g., "Try to improve sleep consistency...").
