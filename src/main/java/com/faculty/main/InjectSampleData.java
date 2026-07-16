package com.faculty.main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.faculty.model.DatabaseConnection;

public class InjectSampleData {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Find student 'Lakindu'
            int studentId = -1;
            int degreeId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT s.id, s.degree_id FROM students s JOIN users u ON s.user_id = u.id WHERE u.username = 'Lakindu'")) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        studentId = rs.getInt("id");
                        degreeId = rs.getInt("degree_id");
                    }
                }
            }

            if (studentId == -1) {
                System.out.println("Lakindu not found, skipping enrollments injection.");
                return;
            }

            // Fix courses that have degree_id = NULL
            try (java.sql.Statement updateStmt = conn.createStatement()) {
                updateStmt.executeUpdate("UPDATE courses SET degree_id = 1 WHERE course_code LIKE 'ETEC%'");
                updateStmt.executeUpdate("UPDATE courses SET degree_id = 2 WHERE course_code LIKE 'ITEC%'");
                updateStmt.executeUpdate("UPDATE courses SET degree_id = 3 WHERE course_code LIKE 'CSEC%'");
                updateStmt.executeUpdate("UPDATE courses SET degree_id = 4 WHERE course_code LIKE 'BTEC%'");
                System.out.println("Updated courses with correct degree_ids.");
            }

            System.out.println("Found Lakindu with Student ID: " + studentId + " and Degree ID: " + degreeId);

            try (java.sql.Statement stmt = conn.createStatement()) {
                // Insert some courses for degree_id = 1 (Engineering Technology)
                stmt.executeUpdate("INSERT IGNORE INTO courses (course_code, course_name, credits, degree_id, academic_year, semester) VALUES ('ETEC 1101', 'Engineering Mathematics I', 3, 1, 1, 1)");
                stmt.executeUpdate("INSERT IGNORE INTO courses (course_code, course_name, credits, degree_id, academic_year, semester) VALUES ('ETEC 1102', 'Engineering Mechanics', 2, 1, 1, 1)");
                stmt.executeUpdate("INSERT IGNORE INTO courses (course_code, course_name, credits, degree_id, academic_year, semester) VALUES ('ETEC 1201', 'Engineering Mathematics II', 3, 1, 1, 2)");
                stmt.executeUpdate("INSERT IGNORE INTO courses (course_code, course_name, credits, degree_id, academic_year, semester) VALUES ('ETEC 2101', 'Thermodynamics', 2, 1, 2, 1)");
                stmt.executeUpdate("INSERT IGNORE INTO courses (course_code, course_name, credits, degree_id, academic_year, semester) VALUES ('ETEC 1103', 'Engineering Ethics', 2, 1, 1, 1)");
                System.out.println("Inserted ETEC courses including 1103 for registration testing.");

                // Enroll Lakindu in all courses matching his degree EXCEPT ETEC 1103
                try (ResultSet rs = stmt.executeQuery("SELECT id, course_code FROM courses WHERE degree_id = 1")) {
                    try (PreparedStatement enrollStmt = conn.prepareStatement("INSERT IGNORE INTO enrollments (student_id, course_id, grade) VALUES (?, ?, 'A')")) {
                        while (rs.next()) {
                            int courseId = rs.getInt("id");
                            String courseCode = rs.getString("course_code");
                            if (!courseCode.equals("ETEC 1103")) {
                                enrollStmt.setInt(1, studentId);
                                enrollStmt.setInt(2, courseId);
                                enrollStmt.executeUpdate();
                                System.out.println("Enrolled Lakindu in course ID: " + courseId + " (" + courseCode + ")");
                            }
                        }
                    }
                }
            }
            
            System.out.println("Successfully injected sample courses and enrollments.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
