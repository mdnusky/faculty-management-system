package model.dao;

import com.faculty.model.DatabaseConnection;
import com.faculty.model.Degree;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DegreeDAO {
    public List<Degree> getAllDegrees() {
        List<Degree> degrees = new ArrayList<>();
        // Join with departments to get department name
        String sql = "SELECT d.id, d.name, d.department_id, d.no_of_students, dep.name as dep_name " +
                     "FROM degrees d LEFT JOIN departments dep ON d.department_id = dep.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Degree degree = new Degree(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("department_id"),
                        rs.getInt("no_of_students")
                );
                degree.setDepartmentName(rs.getString("dep_name"));
                degrees.add(degree);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return degrees;
    }

    public void addDegree(Degree degree) throws SQLException {
        String sql = "INSERT INTO degrees (name, department_id, no_of_students) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, degree.getName());
            if (degree.getDepartmentId() == 0) pstmt.setNull(2, Types.INTEGER); else pstmt.setInt(2, degree.getDepartmentId());
            pstmt.setInt(3, degree.getNoOfStudents());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    degree.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateDegree(Degree degree) throws SQLException {
        String sql = "UPDATE degrees SET name=?, department_id=?, no_of_students=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, degree.getName());
            if (degree.getDepartmentId() == 0) pstmt.setNull(2, Types.INTEGER); else pstmt.setInt(2, degree.getDepartmentId());
            pstmt.setInt(3, degree.getNoOfStudents());
            pstmt.setInt(4, degree.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteDegree(int id) throws SQLException {
        String sql = "DELETE FROM degrees WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}





