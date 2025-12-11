USE life_analytics_db;

-- 1. Insert Habits
INSERT INTO habit (name, category, target_per_week, created_at) VALUES 
('Morning Run', 'HEALTH', 3, NOW()),
('Read 30 Mins', 'LEARNING', 5, NOW()),
('Meditation', 'MINDFULNESS', 7, NOW()),
('Code Project', 'PRODUCTIVITY', 4, NOW());

-- 2. Insert Health Metrics (Last 3 days)
INSERT INTO health_metric (recorded_at, sleep_hours, mood_score, stress_level, energy_level, note) VALUES 
(DATE_SUB(NOW(), INTERVAL 2 DAY), 7.5, 8, 3, 7, 'Felt great, good sleep.'),
(DATE_SUB(NOW(), INTERVAL 1 DAY), 6.0, 5, 7, 4, 'Stressed about work, tired.'),
(NOW(), 8.0, 9, 2, 9, 'Amazing sleep, ready to conquer the day!');

-- 3. Insert Habit Logs (assuming IDs 1-4 for habits)
-- Note: If you have existing data, IDs might differ.
INSERT INTO habit_log (habit_id, log_date, value, note, created_at) VALUES 
(1, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, '5km run', NOW()),
(2, DATE_SUB(CURDATE(), INTERVAL 2 DAY), 1, 'Chapter 4', NOW()),
(3, DATE_SUB(CURDATE(), INTERVAL 1 DAY), 1, '10 mins calm', NOW()),
(1, CURDATE(), 1, '3km easy jog', NOW()),
(4, CURDATE(), 1, 'Fixed backend bugs', NOW());
