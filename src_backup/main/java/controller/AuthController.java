package com.faculty.controller;

import com.faculty.model.User;
import model.dao.UserDAO;
import com.faculty.view.auth.AuthFrame;
import com.faculty.view.dashboard.DashboardFrame;

import javax.swing.*;

public class AuthController {
    private AuthFrame authFrame;
    private UserDAO userDAO;

    public AuthController(AuthFrame authFrame) {
        this.authFrame = authFrame;
        this.userDAO = new UserDAO();
    }

    public void handleLogin(String username, String password, String role) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(authFrame, "Please enter username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user != null) {
            if (user.getRole().equals(role)) {
                JOptionPane.showMessageDialog(authFrame, "Login successful as " + role + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                authFrame.dispose();
                
                // Open dashboard and pass the logged-in user
                SwingUtilities.invokeLater(() -> {
                    DashboardFrame dashboard = new DashboardFrame(user);
                    dashboard.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(authFrame, "Role mismatch. You are not a " + role + ".", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(authFrame, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void handleRegistration(String username, String password, String role) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(authFrame, "Please enter username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(0, username, password, role);
        boolean success = userDAO.registerUser(newUser);
        
        if (success) {
            JOptionPane.showMessageDialog(authFrame, "Registration successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(authFrame, "Registration failed. Username might already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}