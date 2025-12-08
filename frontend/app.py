import streamlit as st
import requests

# Configuration
API_BASE_URL = "http://localhost:8080/api"

st.set_page_config(page_title="Wellness Tracker", page_icon="🧘")

st.title("🧘 Wellness Tracker")

# Sidebar
st.sidebar.header("Navigation")
page = st.sidebar.radio("Go to", ["Dashboard", "Log Habits", "Log Health"])

# Health Check
try:
    response = requests.get(f"{API_BASE_URL}/health")
    if response.status_code == 200:
        st.sidebar.success("Backend Online ✅")
    else:
        st.sidebar.error("Backend Status Unknown ⚠️")
except requests.exceptions.ConnectionError:
    st.sidebar.error("Backend Offline ❌")

if page == "Dashboard":
    st.header("Dashboard")
    st.write("Welcome to your personal wellness dashboard.")
    
    # Placeholder for fetching dashboard data
    if st.button("Refresh Data"):
        try:
            res = requests.get(f"{API_BASE_URL}/analytics/dashboard")
            if res.status_code == 200:
                data = res.json()
                st.json(data)
            else:
                st.error("Failed to fetch dashboard data")
        except Exception as e:
            st.error(f"Error: {e}")

elif page == "Log Habits":
    st.header("Log Habits")
    st.write("Habit logging form will go here.")

elif page == "Log Health":
    st.header("Log Health Metrics")
    st.write("Health metrics form will go here.")
