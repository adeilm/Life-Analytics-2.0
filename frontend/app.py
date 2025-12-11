import streamlit as st
from api_client import APIClient

# Initialize API Client
api = APIClient()

st.set_page_config(
    page_title="Life Analytics 2.0",
    page_icon="🧘",
    layout="wide"
)

# --- UI Layout ---

st.title("🧘 Life Analytics 2.0")

# Sidebar Status
if api.check_health():
    st.sidebar.success("Backend Connected 🟢")
else:
    st.sidebar.error("Backend Disconnected 🔴")
    st.sidebar.warning("Ensure Spring Boot is running on port 8080.")

st.sidebar.header("Navigation")
page = st.sidebar.radio("Go to", ["Dashboard", "Habits", "Health Metrics", "AI Coach", "Database Viewer"])

# --- Pages ---

if page == "Database Viewer":
    st.header("🗄️ Database Viewer")
    st.write("View raw data from the database tables.")

    tab1, tab2, tab3 = st.tabs(["Habits", "Habit Logs", "Health Metrics"])

    with tab1:
        st.subheader("Habits Table")
        habits = api.get_habits()
        if habits:
            st.dataframe(habits)
        else:
            st.info("No habits found.")

    with tab2:
        st.subheader("Habit Logs Table")
        all_logs = []
        habits = api.get_habits()
        if habits:
            for habit in habits:
                logs = api.get_habit_logs(habit['id'])
                for log in logs:
                    log['habitName'] = habit['name']
                all_logs.extend(logs)
            
            if all_logs:
                st.dataframe(all_logs)
            else:
                st.info("No habit logs found.")
        else:
            st.info("No habits found.")

    with tab3:
        st.subheader("Health Metrics Table")
        metrics = api.get_health_metrics()
        if metrics:
            st.dataframe(metrics)
        else:
            st.info("No health metrics found.")

elif page == "Dashboard":
    st.header("📊 Dashboard")
    
    if st.button("Refresh Data"):
        st.rerun()

    # 1. Health Trends
    st.subheader("Health Trends (Averages)")
    trends = api.get_health_trends()
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

    # 2. Weekly Habit Progress
    st.subheader("Weekly Habit Progress")
    weekly_response = api.get_weekly_habits()
    
    if weekly_response and "habits" in weekly_response:
        chart_data = {h["habitName"]: h["completedThisWeek"] for h in weekly_response["habits"]}
        if chart_data:
            st.bar_chart(chart_data)
        else:
            st.info("No active habits found.")
    else:
        st.info("No habit data available.")

elif page == "Habits":
    st.header("✅ Habit Tracker")
    
    tab1, tab2 = st.tabs(["My Habits", "Create New"])
    
    with tab1:
        habits = api.get_habits()
        if not habits:
            st.info("No habits found. Create one in the next tab!")
        else:
            for habit in habits:
                with st.expander(f"{habit['name']} ({habit['category']})"):
                    st.write(f"**Target:** {habit['targetPerWeek']} times/week")
                    
                    col1, col2 = st.columns([1, 3])
                    with col1:
                        if st.button(f"Check-in (+1)", key=f"btn_{habit['id']}"):
                            res = api.log_habit(habit['id'])
                            if res:
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
                    res = api.create_habit(name, category, target)
                    if res:
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
                res = api.log_health(sleep, mood, stress, energy, note)
                if res:
                    st.success("Health metrics logged successfully!")
                else:
                    st.error("Failed to log metrics.")
    
    with tab2:
        st.subheader("Recent Logs")
        metrics = api.get_health_metrics()
        if metrics:
            st.dataframe(metrics)
        else:
            st.info("No health metrics recorded yet.")

elif page == "AI Coach":
    st.header("🤖 AI Wellness Coach")
    st.write("Get personalized insights based on your weekly data.")
    
    if st.button("Generate Insights"):
        with st.spinner("Analyzing your data..."):
            insight = api.get_ai_insights()
            st.markdown("### 💡 Your Insights")
            st.write(insight)
