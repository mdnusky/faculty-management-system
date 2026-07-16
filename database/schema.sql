-- Create the database
CREATE DATABASE IF NOT EXISTS fms_db;
USE fms_db;

-- Users table for authentication
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Admin', 'Student', 'Lecturer') NOT NULL
);

-- Departments table
CREATE TABLE IF NOT EXISTS departments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    hod_name VARCHAR(100),
    no_of_staff INT DEFAULT 0
);

-- Degrees table
CREATE TABLE IF NOT EXISTS degrees (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department_id INT,
    no_of_students INT DEFAULT 0,
    CONSTRAINT fk_degrees_department 
        FOREIGN KEY (department_id) REFERENCES departments(id) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Lecturers table
CREATE TABLE IF NOT EXISTS lecturers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    full_name VARCHAR(100) NOT NULL,
    department_id INT,
    email VARCHAR(100),
    mobile_number VARCHAR(20),
    CONSTRAINT fk_lecturers_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lecturers_department 
        FOREIGN KEY (department_id) REFERENCES departments(id) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Students table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    student_id_str VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    degree_id INT,
    email VARCHAR(100),
    mobile_number VARCHAR(20),
    CONSTRAINT fk_students_user 
        FOREIGN KEY (user_id) REFERENCES users(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_students_degree 
        FOREIGN KEY (degree_id) REFERENCES degrees(id) 
        ON DELETE SET NULL ON UPDATE CASCADE
);

-- Courses table
CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) UNIQUE NOT NULL,
    course_name VARCHAR(150) NOT NULL,
    credits INT(11) NOT NULL,
    lecturer_id INT(11) DEFAULT NULL,
    academic_year INT(11) DEFAULT 1,
    semester INT(11) DEFAULT 1,
    degree_id INT,
    CONSTRAINT fk_courses_lecturer 
        FOREIGN KEY (lecturer_id) REFERENCES lecturers(id) 
        ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT fk_courses_degree
        FOREIGN KEY (degree_id) REFERENCES degrees(id)
        ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Enrollments table
CREATE TABLE IF NOT EXISTS enrollments (
    student_id INT,
    course_id INT,
    grade VARCHAR(5),
    PRIMARY KEY (student_id, course_id),
    CONSTRAINT fk_enrollments_student 
        FOREIGN KEY (student_id) REFERENCES students(id) 
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_enrollments_course 
        FOREIGN KEY (course_id) REFERENCES courses(id) 
        ON DELETE CASCADE ON UPDATE CASCADE
);
