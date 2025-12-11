import requests
from datetime import date

class APIClient:
    """
    Client for interacting with the Life Analytics Backend API.
    Handles all HTTP requests, error catching, and data formatting.
    """
    def __init__(self, base_url="http://localhost:8080/api"):
        self.base_url = base_url

    def _handle_response(self, response):
        """Internal helper to parse responses and handle errors gracefully."""
        try:
            response.raise_for_status()
            # If content exists, return JSON. If empty (e.g. 204), return True.
            return response.json() if response.content else True
        except requests.exceptions.RequestException as e:
            print(f"API Error: {e}")
            return None

    def check_health(self):
        """Checks if the backend is reachable."""
        try:
            response = requests.get(f"{self.base_url}/health", timeout=2)
            return response.status_code == 200
        except:
            return False

    def get_habits(self):
        """Fetches all active habits."""
        data = self._handle_response(requests.get(f"{self.base_url}/habits"))
        return data if isinstance(data, list) else []

    def create_habit(self, name, category, target):
        """Creates a new habit definition."""
        payload = {"name": name, "category": category, "targetPerWeek": target, "active": True}
        return self._handle_response(requests.post(f"{self.base_url}/habits", json=payload))

    def log_habit(self, habit_id, note=""):
        """Logs a completion for a specific habit for today."""
        payload = {"value": 1, "note": note, "logDate": date.today().isoformat()}
        return self._handle_response(requests.post(f"{self.base_url}/habits/{habit_id}/logs", json=payload))

    def get_habit_logs(self, habit_id):
        """Fetches history logs for a specific habit."""
        data = self._handle_response(requests.get(f"{self.base_url}/habits/{habit_id}/logs"))
        return data if isinstance(data, list) else []

    def log_health(self, sleep, mood, stress, energy, note):
        """Logs daily health metrics (sleep, mood, etc.)."""
        payload = {
            "sleepHours": sleep,
            "moodScore": mood,
            "stressLevel": stress,
            "energyLevel": energy,
            "note": note,
            "recordedAt": date.today().isoformat()
        }
        return self._handle_response(requests.post(f"{self.base_url}/health-metrics", json=payload))

    def get_health_metrics(self):
        """Fetches all recorded health metrics."""
        data = self._handle_response(requests.get(f"{self.base_url}/health-metrics"))
        return data if isinstance(data, list) else []

    def get_health_trends(self):
        """Fetches aggregated health trends (averages) for the dashboard."""
        data = self._handle_response(requests.get(f"{self.base_url}/analytics/health/trend"))
        return data if isinstance(data, dict) else {}

    def get_weekly_habits(self):
        """Fetches weekly habit completion stats for the dashboard."""
        data = self._handle_response(requests.get(f"{self.base_url}/analytics/habits/weekly"))
        return data if isinstance(data, dict) else {}

    def get_ai_insights(self):
        """Fetches AI-generated insights from the backend."""
        data = self._handle_response(requests.get(f"{self.base_url}/analytics/ai-insights"))
        return data.get("insight") if isinstance(data, dict) else "Unable to generate insights."
