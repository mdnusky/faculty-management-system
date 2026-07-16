package model.dao;

import com.faculty.model.DatabaseConnection;
import com.faculty.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, l.full_name as lecturer_name, d.name as degree_name " +
                     "FROM courses c " +
                     "LEFT JOIN lecturers l ON c.lecturer_id = l.id " +
                     "LEFT JOIN degrees d ON c.degree_id = d.id";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Course course = new Course(
                        rs.getInt("id"),
                        rs.getString("course_code"),
                        rs.getString("course_name"),
                        rs.getInt("credits"),
                        rs.getInt("lecturer_id"),
                        rs.getInt("academic_year"),
                        rs.getInt("semester"),
                        rs.getInt("degree_id")
                );
                course.setLecturerName(rs.getString("lecturer_name"));
                course.setDegreeName(rs.getString("degree_name"));
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Course> getCoursesByStudentUserId(int userId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, e.grade FROM courses c " +
                     "JOIN enrollments e ON c.id = e.course_id " +
                     "JOIN students s ON e.student_id = s.id " +
                     "WHERE s.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("lecturer_id"),
                            rs.getInt("academic_year"),
                            rs.getInt("semester"),
                            rs.getInt("degree_id")
                    );
                    course.setGrade(rs.getString("grade"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Course> getCoursesByLecturerId(int lecturerId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, l.full_name as lecturer_name FROM courses c LEFT JOIN lecturers l ON c.lecturer_id = l.id WHERE c.lecturer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, lecturerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("lecturer_id"),
                            rs.getInt("academic_year"),
                            rs.getInt("semester"),
                            rs.getInt("degree_id")
                    );
                    course.setLecturerName(rs.getString("lecturer_name"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, course_name, credits, lecturer_id, academic_year, semester, degree_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            if (course.getLecturerId() == 0) pstmt.setNull(4, Types.INTEGER); else pstmt.setInt(4, course.getLecturerId());
            pstmt.setInt(5, course.getAcademicYear());
            pstmt.setInt(6, course.getSemester());
            if (course.getDegreeId() == 0) pstmt.setNull(7, Types.INTEGER); else pstmt.setInt(7, course.getDegreeId());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    course.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_code=?, course_name=?, credits=?, lecturer_id=?, academic_year=?, semester=?, degree_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseCode());
            pstmt.setString(2, course.getCourseName());
            pstmt.setInt(3, course.getCredits());
            if (course.getLecturerId() == 0) pstmt.setNull(4, Types.INTEGER); else pstmt.setInt(4, course.getLecturerId());
            pstmt.setInt(5, course.getAcademicYear());
            pstmt.setInt(6, course.getSemester());
            if (course.getDegreeId() == 0) pstmt.setNull(7, Types.INTEGER); else pstmt.setInt(7, course.getDegreeId());
            pstmt.setInt(8, course.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteCourse(String courseCode) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_code=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, courseCode);
            pstmt.executeUpdate();
        }
    }
    
    public List<Course> getCoursesByYearAndSemester(int year, int semester) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, l.full_name as lecturer_name FROM courses c LEFT JOIN lecturers l ON c.lecturer_id = l.id WHERE c.academic_year = ? AND c.semester = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, year);
            pstmt.setInt(2, semester);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("lecturer_id"),
                            rs.getInt("academic_year"),
                            rs.getInt("semester"),
                            rs.getInt("degree_id")
                    );
                    course.setLecturerName(rs.getString("lecturer_name"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public List<Course> getCoursesByDegreeYearAndSemester(int degreeId, int year, int semester) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT c.*, l.full_name as lecturer_name FROM courses c LEFT JOIN lecturers l ON c.lecturer_id = l.id WHERE c.degree_id = ? AND c.academic_year = ? AND c.semester = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, degreeId);
            pstmt.setInt(2, year);
            pstmt.setInt(3, semester);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Course course = new Course(
                            rs.getInt("id"),
                            rs.getString("course_code"),
                            rs.getString("course_name"),
                            rs.getInt("credits"),
                            rs.getInt("lecturer_id"),
                            rs.getInt("academic_year"),
                            rs.getInt("semester"),
                            rs.getInt("degree_id")
                    );
                    course.setLecturerName(rs.getString("lecturer_name"));
                    courses.add(course);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    public void enrollStudent(int studentId, int courseId) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, grade) VALUES (?, ?, 'Pending')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, courseId);
            pstmt.executeUpdate();
        }
    }
}





