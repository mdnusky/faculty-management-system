package model.dao;

import com.faculty.model.DatabaseConnection;
import com.faculty.model.Lecturer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LecturerDAO {
    public List<Lecturer> getAllLecturers() {
        List<Lecturer> lecturers = new ArrayList<>();
        String sql = "SELECT l.*, d.name as dep_name FROM lecturers l LEFT JOIN departments d ON l.department_id = d.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Lecturer lecturer = new Lecturer(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getInt("department_id"),
                        rs.getString("email"),
                        rs.getString("mobile_number")
                );
                lecturer.setDepartmentName(rs.getString("dep_name"));
                
                // Get courses teaching
                lecturer.setCoursesTeaching(getCoursesForLecturer(conn, rs.getInt("id")));
                
                lecturers.add(lecturer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lecturers;
    }
    
    private String getCoursesForLecturer(Connection conn, int lecturerId) throws SQLException {
        String sql = "SELECT course_code FROM courses WHERE lecturer_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lecturerId);
            ResultSet rs = stmt.executeQuery();
            List<String> courses = new ArrayList<>();
            while(rs.next()) {
                courses.add(rs.getString("course_code"));
            }
            return String.join(", ", courses);
        }
    }

    public Lecturer getLecturerByUserId(int userId) {
        String sql = "SELECT l.*, d.name as dep_name FROM lecturers l LEFT JOIN departments d ON l.department_id = d.id WHERE l.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Lecturer lecturer = new Lecturer(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("full_name"),
                            rs.getInt("department_id"),
                            rs.getString("email"),
                            rs.getString("mobile_number")
                    );
                    lecturer.setDepartmentName(rs.getString("dep_name"));
                    lecturer.setCoursesTeaching(getCoursesForLecturer(conn, rs.getInt("id")));
                    return lecturer;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addLecturer(Lecturer lecturer) throws SQLException {
        String sql = "INSERT INTO lecturers (user_id, full_name, department_id, email, mobile_number) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (lecturer.getUserId() == 0) pstmt.setNull(1, Types.INTEGER); else pstmt.setInt(1, lecturer.getUserId());
            pstmt.setString(2, lecturer.getFullName());
            if (lecturer.getDepartmentId() == 0) pstmt.setNull(3, Types.INTEGER); else pstmt.setInt(3, lecturer.getDepartmentId());
            pstmt.setString(4, lecturer.getEmail());
            pstmt.setString(5, lecturer.getMobileNumber());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lecturer.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateLecturer(Lecturer lecturer) throws SQLException {
        String sql = "UPDATE lecturers SET full_name=?, department_id=?, email=?, mobile_number=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lecturer.getFullName());
            if (lecturer.getDepartmentId() == 0) pstmt.setNull(2, Types.INTEGER); else pstmt.setInt(2, lecturer.getDepartmentId());
            pstmt.setString(3, lecturer.getEmail());
            pstmt.setString(4, lecturer.getMobileNumber());
            pstmt.setInt(5, lecturer.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteLecturer(int id) throws SQLException {
        String sql = "DELETE FROM lecturers WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}





