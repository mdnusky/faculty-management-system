package model.dao;

import com.faculty.model.DatabaseConnection;
import com.faculty.model.Student;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, d.name as degree_name FROM students s LEFT JOIN degrees d ON s.degree_id = d.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Student student = new Student(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("student_id_str"),
                        rs.getString("full_name"),
                        rs.getInt("degree_id"),
                        rs.getString("email"),
                        rs.getString("mobile_number")
                );
                student.setDegreeName(rs.getString("degree_name"));
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    public Student getStudentByUserId(int userId) {
        String sql = "SELECT s.*, d.name as degree_name FROM students s LEFT JOIN degrees d ON s.degree_id = d.id WHERE s.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("student_id_str"),
                            rs.getString("full_name"),
                            rs.getInt("degree_id"),
                            rs.getString("email"),
                            rs.getString("mobile_number")
                    );
                    student.setDegreeName(rs.getString("degree_name"));
                    return student;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (user_id, student_id_str, full_name, degree_id, email, mobile_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (student.getUserId() == 0) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, student.getUserId());
            }
            pstmt.setString(2, student.getStudentIdStr());
            pstmt.setString(3, student.getFullName());
            if (student.getDegreeId() == 0) {
                pstmt.setNull(4, Types.INTEGER);
            } else {
                pstmt.setInt(4, student.getDegreeId());
            }
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getMobileNumber());
            pstmt.executeUpdate();
        }
    }

    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET full_name=?, degree_id=?, email=?, mobile_number=?, student_id_str=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, student.getFullName());
            if (student.getDegreeId() == 0) {
                pstmt.setNull(2, Types.INTEGER);
            } else {
                pstmt.setInt(2, student.getDegreeId());
            }
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getMobileNumber());
            pstmt.setString(5, student.getStudentIdStr());
            pstmt.setInt(6, student.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteStudent(String studentIdStr) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id_str=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentIdStr);
            pstmt.executeUpdate();
        }
    }
}





