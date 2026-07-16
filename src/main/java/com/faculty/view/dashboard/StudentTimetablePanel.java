package com.faculty.view.dashboard;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import com.faculty.view.components.StyledTable;
import com.faculty.view.components.RoundedComboBox;
import com.faculty.model.User;
import com.faculty.model.Student;
import com.faculty.controller.CourseController;
import model.dao.StudentDAO;

public class StudentTimetablePanel extends JPanel {
    
    private StyledTable table;
    private DefaultTableModel tableModel;
    private CourseController controller;
    private int studentDegreeId = 0;
    private RoundedComboBox<String> cmbYear;
    private RoundedComboBox<String> cmbSem;
    
    public StudentTimetablePanel(User currentUser) {
        this.controller = new CourseController();
        StudentDAO sdao = new StudentDAO();
        Student student = sdao.getStudentByUserId(currentUser.getId());
        if (student != null) {
            this.studentDegreeId = student.getDegreeId();
        }

        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 247, 250)); // Very light blue/grey
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Top Banner
        com.faculty.view.components.GradientPanel banner = new com.faculty.view.components.GradientPanel(new Color(20, 61, 89), new Color(20, 61, 89), false, 20);
        banner.setLayout(new BorderLayout());
        banner.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        JLabel title = new JLabel("Timetable");
        title.setFont(new Font("Montserrat", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        
        JLabel subtitle = new JLabel("View your weekly lecture schedule and academic timetable.");
        subtitle.setFont(new Font("Montserrat", Font.PLAIN, 14));
        subtitle.setForeground(new Color(220, 235, 255));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitle);
        
        banner.add(titlePanel, BorderLayout.WEST);

        // Controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        controlPanel.setOpaque(false);
        
        cmbYear = new RoundedComboBox<>(20);
        cmbYear.addItem("Year 1"); cmbYear.addItem("Year 2"); cmbYear.addItem("Year 3"); cmbYear.addItem("Year 4");
        cmbYear.setPreferredSize(new Dimension(100, 35));

        cmbSem = new RoundedComboBox<>(20);
        cmbSem.addItem("Semester 1"); cmbSem.addItem("Semester 2");
        cmbSem.setPreferredSize(new Dimension(120, 35));

        controlPanel.add(cmbYear);
        controlPanel.add(cmbSem);
        banner.add(controlPanel, BorderLayout.SOUTH);
        
        JPanel bannerWrapper = new JPanel(new BorderLayout());
        bannerWrapper.setOpaque(false);
        bannerWrapper.add(banner, BorderLayout.CENTER);
        
        add(bannerWrapper, BorderLayout.NORTH);

        String[] columns = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new StyledTable() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                if (getRowCount() > 2) {
                    Rectangle rect = getCellRect(2, 0, true);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Montserrat", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    String text = "Interval";
                    int textWidth = fm.stringWidth(text);
                    int x = (getWidth() - textWidth) / 2;
                    int y = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(text, x, y);
                    g2d.dispose();
                }
            }
        };
        table.setModel(tableModel);
        
        ((javax.swing.table.DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        TableCellRenderer existingRenderer = table.getDefaultRenderer(Object.class);
        table.setDefaultRenderer(Object.class, new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = existingRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);
                }
                
                if (row == 2) { // Interval row
                    c.setBackground(new Color(20, 61, 89)); // Blue color
                    c.setForeground(Color.WHITE);
                    if (c instanceof JLabel) { ((JLabel)c).setText(""); }
                    c.setFont(new Font("Montserrat", Font.BOLD, 16));
                } else {
                    c.setForeground(new Color(50, 50, 50));
                    c.setFont(new Font("Montserrat", Font.PLAIN, 14));
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        com.faculty.view.components.CardPanel tableContainer = new com.faculty.view.components.CardPanel(20, Color.WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        
        JPanel mainContent = new JPanel(new BorderLayout(0, 10));
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        mainContent.add(tableContainer, BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);

        cmbYear.addActionListener(e -> loadTimetable());
        cmbSem.addActionListener(e -> loadTimetable());
        loadTimetable();
    }

    private void loadTimetable() {
        tableModel.setRowCount(0);
        int year = cmbYear.getSelectedIndex() + 1;
        int sem = cmbSem.getSelectedIndex() + 1;

        List<com.faculty.model.Course> courses;
        if (studentDegreeId > 0) {
            courses = controller.getCoursesByDegreeYearAndSemester(studentDegreeId, year, sem);
        } else {
            courses = controller.getCoursesByYearAndSemester(year, sem);
        }

        String c1 = courses.size() > 0 ? courses.get(0).getCourseCode() : "-";
        String c2 = courses.size() > 1 ? courses.get(1).getCourseCode() : "-";
        String c3 = courses.size() > 2 ? courses.get(2).getCourseCode() : "-";
        String c4 = courses.size() > 3 ? courses.get(3).getCourseCode() : "-";

        Object[][] data = {
            {"08.00", c1, c2, c1, c2, c1},
            {"10.00", c2, c1, c2, c1, c2},
            {"INTERVAL", "Interval", "Interval", "Interval", "Interval", "Interval"},
            {"01.00", c3, c4, c3, c4, c3},
            {"03.00", c4, c3, c4, c3, c4}
        };

        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
}





