-- Password: admin123 (BCrypt encoded)
INSERT INTO users (username, email, password, role, enabled, created_at, updated_at)
SELECT 'admin', 'admin@education.com', '$2a$12$5lngP8o2bF.9jNvK7PLQ9OplE.nDohfK1EN9bq2Xc03jL9UQNK8o.', 'ADMIN', true, NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'admin' OR email = 'admin@education.com'
);

INSERT INTO spaces (code, name, type, capacity, building, floor, description, status, created_at, updated_at)
SELECT 'ROOM-101', 'Classroom 101', 'CLASSROOM', 40, 'Main Building', '1st Floor', 'Standard classroom', 'AVAILABLE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM spaces WHERE code = 'ROOM-101');

INSERT INTO spaces (code, name, type, capacity, building, floor, description, status, created_at, updated_at)
SELECT 'LAB-201', 'Computer Lab 1', 'LABORATORY', 30, 'Tech Building', '2nd Floor', 'Computer lab with 30 workstations', 'AVAILABLE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM spaces WHERE code = 'LAB-201');

INSERT INTO spaces (code, name, type, capacity, building, floor, description, status, created_at, updated_at)
SELECT 'STUDY-301', 'Study Room A', 'STUDY_ROOM', 8, 'Library', '3rd Floor', 'Quiet study room', 'AVAILABLE', NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM spaces WHERE code = 'STUDY-301');
