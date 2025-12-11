import requests
from datetime import date

class APIClient:
    def __init__(self, base_url="http://localhost:8080/api"):
        self.base_url = base_url

    def _handle_response(self, response):
        try:
            response.raise_for_status()
            # If content exists, return JSON. If empty (e.g. 204), return True.
            return response.json() if response.content else True
        except requests.exceptions.RequestException as e:
            print(f"API Error: {e}")
            return None

    def check_health(self):
        try:
            response = requests.get(f"{self.base_url}/health", timeout=2)
            return response.status_code == 200
        except:
            return False

    def get_habits(self):
        data = self._handle_response(requests.get(f"{self.base_url}/habits"))
        return data if isinstance(data, list) else []

    def create_habit(self, name, category, target):
        payload = {"name": name, "category": category, "targetPerWeek": target, "active": True}
        return self._handle_response(requests.post(f"{self.base_url}/habits", json=payload))

    def log_habit(self, habit_id, note=""):
        payload = {"value": 1, "note": note, "logDate": date.today().isoformat()}
        return self._handle_response(requests.post(f"{self.base_url}/habits/{habit_id}/logs", json=payload))

    def get_habit_logs(self, habit_id):
        data = self._handle_response(requests.get(f"{self.base_url}/habits/{habit_id}/logs"))
        return data if isinstance(data, list) else []

    def log_health(self, sleep, mood, stress, energy, note):
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
        data = self._handle_response(requests.get(f"{self.base_url}/health-metrics"))
        return data if isinstance(data, list) else []

    def get_health_trends(self):
        data = self._handle_response(requests.get(f"{self.base_url}/analytics/health/trend"))
        return data if isinstance(data, dict) else {}

    def get_weekly_habits(self):
        data = self._handle_response(requests.get(f"{self.base_url}/analytics/habits/weekly"))
        return data if isinstance(data, dict) else {}
