package model.dao;

import com.faculty.model.DatabaseConnection;
import com.faculty.model.Leave;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {
    
    public void requestLeave(Leave leave) throws SQLException {
        String sql = "INSERT INTO leaves (lecturer_id, leave_date, reason, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, leave.getLecturerId());
            pstmt.setDate(2, leave.getLeaveDate());
            pstmt.setString(3, leave.getReason());
            pstmt.setString(4, leave.getStatus() != null ? leave.getStatus() : "Pending");
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    leave.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public List<Leave> getLeavesByLecturer(int lecturerId) {
        List<Leave> leaves = new ArrayList<>();
        String sql = "SELECT * FROM leaves WHERE lecturer_id = ? ORDER BY leave_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lecturerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    leaves.add(new Leave(
                        rs.getInt("id"),
                        rs.getInt("lecturer_id"),
                        rs.getDate("leave_date"),
                        rs.getString("reason"),
                        rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leaves;
    }

    public void updateLeave(Leave leave) throws SQLException {
        String sql = "UPDATE leaves SET leave_date=?, reason=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, leave.getLeaveDate());
            pstmt.setString(2, leave.getReason());
            pstmt.setInt(3, leave.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteLeave(int id) throws SQLException {
        String sql = "DELETE FROM leaves WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}





