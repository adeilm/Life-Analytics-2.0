import streamlit as st
import requests
from datetime import date

# Configuration
API_BASE_URL = "http://localhost:8080/api"

st.set_page_config(
    page_title="Life Analytics 2.0",
    page_icon="🧘",
    layout="wide"
)

# --- Helper Functions ---
def check_backend():
    try:
        response = requests.get(f"{API_BASE_URL}/health", timeout=2)
        return response.status_code == 200
    except:
        return False

def get_habits():
    try:
        return requests.get(f"{API_BASE_URL}/habits").json()
    except:
        return []

def create_habit(name, category, target):
    payload = {"name": name, "category": category, "targetPerWeek": target, "active": True}
    return requests.post(f"{API_BASE_URL}/habits", json=payload)

def log_habit(habit_id, note=""):
    payload = {"value": 1, "note": note, "logDate": date.today().isoformat()}
    return requests.post(f"{API_BASE_URL}/habits/{habit_id}/logs", json=payload)

def log_health(sleep, mood, stress, energy, note):
    payload = {
        "sleepHours": sleep,
        "moodScore": mood,
        "stressLevel": stress,
        "energyLevel": energy,
        "note": note,
        "recordedAt": date.today().isoformat()
    }
    return requests.post(f"{API_BASE_URL}/health-metrics", json=payload)

def get_health_metrics():
    try:
        return requests.get(f"{API_BASE_URL}/health-metrics").json()
    except:
        return []

# --- UI Layout ---

st.title("🧘 Life Analytics 2.0")

# Sidebar Status
if check_backend():
    st.sidebar.success("Backend Connected 🟢")
else:
    st.sidebar.error("Backend Disconnected 🔴")
    st.sidebar.warning("Ensure Spring Boot is running on port 8080.")

st.sidebar.header("Navigation")
page = st.sidebar.radio("Go to", ["Dashboard", "Habits", "Health Metrics"])

# --- Pages ---

if page == "Dashboard":
    st.header("📊 Dashboard")
    
    if st.button("Refresh Data"):
        st.rerun()

    # 1. Health Trends
    st.subheader("Health Trends (Averages)")
    try:
        trends = requests.get(f"{API_BASE_URL}/analytics/health/trend").json()
        if trends:
            col1, col2, col3, col4 = st.columns(4)
            with col1:
                st.metric("Avg Sleep", f"{trends.get('avgSleep', 0):.1f} hrs")
            with col2:
                st.metric("Avg Mood", f"{trends.get('avgMood', 0):.1f}/10")
            with col3:
                st.metric("Avg Stress", f"{trends.get('avgStress', 0):.1f}/10")
            with col4:
                st.metric("Avg Energy", f"{trends.get('avgEnergy', 0):.1f}/10")
        else:
            st.info("No health data available yet.")
    except Exception as e:
        st.error(f"Could not load health trends. {e}")

    # 2. Weekly Habit Progress
    st.subheader("Weekly Habit Progress")
    try:
        weekly = requests.get(f"{API_BASE_URL}/analytics/habits/weekly").json()
        # Expected format: {"Run": 3, "Read": 5} or similar
        if weekly:
            st.bar_chart(weekly)
        else:
            st.info("No habit logs for this week.")
    except Exception as e:
        st.error(f"Could not load weekly data. {e}")

elif page == "Habits":
    st.header("✅ Habit Tracker")
    
    tab1, tab2 = st.tabs(["My Habits", "Create New"])
    
    with tab1:
        habits = get_habits()
        if not habits:
            st.info("No habits found. Create one in the next tab!")
        else:
            for habit in habits:
                with st.expander(f"{habit['name']} ({habit['category']})"):
                    st.write(f"**Target:** {habit['targetPerWeek']} times/week")
                    
                    col1, col2 = st.columns([1, 3])
                    with col1:
                        if st.button(f"Check-in (+1)", key=f"btn_{habit['id']}"):
                            res = log_habit(habit['id'])
                            if res.status_code in [200, 201]:
                                st.success("Logged!")
                                st.rerun()
                            else:
                                st.error("Failed to log.")
                    with col2:
                        st.caption("Log a completion for today.")

    with tab2:
        st.subheader("Create a New Habit")
        with st.form("new_habit"):
            name = st.text_input("Habit Name", placeholder="e.g., Morning Run")
            category = st.selectbox("Category", ["HEALTH", "PRODUCTIVITY", "MINDFULNESS", "LEARNING", "OTHER"])
            target = st.slider("Target per week", 1, 7, 5)
            submitted = st.form_submit_button("Create Habit")
            
            if submitted:
                if name:
                    res = create_habit(name, category, target)
                    if res.status_code in [200, 201]:
                        st.success(f"Created habit: {name}!")
                        st.rerun()
                    else:
                        st.error("Error creating habit.")
                else:
                    st.warning("Please enter a habit name.")

elif page == "Health Metrics":
    st.header("❤️ Health Metrics")
    
    tab1, tab2 = st.tabs(["Log Today", "History"])
    
    with tab1:
        st.subheader("Daily Check-in")
        with st.form("health_log"):
            col1, col2 = st.columns(2)
            with col1:
                sleep = st.number_input("Sleep (hours)", 0.0, 24.0, 7.0, step=0.5)
                stress = st.slider("Stress Level (1-10)", 1, 10, 3)
            with col2:
                mood = st.slider("Mood (1-10)", 1, 10, 7)
                energy = st.slider("Energy Level (1-10)", 1, 10, 6)
                
            note = st.text_area("Notes (Optional)", placeholder="How are you feeling?")
            
            submitted = st.form_submit_button("Log Metrics")
            if submitted:
                res = log_health(sleep, mood, stress, energy, note)
                if res.status_code in [200, 201]:
                    st.success("Health metrics logged successfully!")
                else:
                    st.error("Failed to log metrics.")
    
    with tab2:
        st.subheader("Recent Logs")
        metrics = get_health_metrics()
        if metrics:
            st.table(metrics)
        else:
            st.info("No health metrics recorded yet.")
