-- Insert a default admin user (password is 'admin123' for demonstration)
INSERT IGNORE INTO users (username, password_hash, role) VALUES ('admin', 'admin123', 'Admin');

-- Dummy data for testing UI
INSERT IGNORE INTO departments (name, hod_name, no_of_staff) VALUES 
('Applied Computing', 'Kumar Sanga', 15),
('Software Engineering', 'Kumar Sanga', 17),
('Computer Systems Engineering', 'Kumar Sanga', 12);

INSERT IGNORE INTO degrees (name, department_id, no_of_students) VALUES 
('Engineering Technology', 1, 375),
('Information Technology', 2, 375),
('Computer Science', 3, 325),
('Bio Systems Technology', 1, 75);

INSERT IGNORE INTO courses (course_code, course_name, credits, degree_id, academic_year, semester) VALUES
('ETEC 1101', 'Engineering Mathematics I', 3, 1, 1, 1),
('ETEC 1102', 'Engineering Mechanics', 2, 1, 1, 1),
('ETEC 1201', 'Engineering Mathematics II', 3, 1, 1, 2),
('ETEC 2101', 'Thermodynamics', 2, 1, 2, 1),
('ITEC 1101', 'Programming Fundamentals', 3, 2, 1, 1),
('ITEC 1102', 'Computer Systems', 2, 2, 1, 1),
('ITEC 1201', 'Object Oriented Programming', 3, 2, 1, 2),
('ITEC 2101', 'Data Structures', 3, 2, 2, 1);
