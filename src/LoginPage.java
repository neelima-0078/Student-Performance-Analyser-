import javax.swing.*;
import java.awt.*;
import java.io.*;

public class LoginPage extends JPanel {
    private Main mainApp;
    private static final String DATA_FOLDER = "student_data";
    private static final String USERS_FILE = DATA_FOLDER + "/users.txt";

    public LoginPage(Main app) {
        this.mainApp = app;
        setLayout(null);
        setBackground(new Color(240, 248, 255));

        JLabel title = new JLabel("Student Performance Analyzer");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(25, 118, 210));
        title.setBounds(200, 120, 450, 40);
        add(title);

        JLabel subtitle = new JLabel("Enter your credentials to continue");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setBounds(270, 170, 300, 30);
        add(subtitle);

        JLabel nameLabel = new JLabel("Username:");
        nameLabel.setBounds(250, 230, 100, 25);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(350, 230, 200, 30);
        add(nameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(250, 280, 100, 25);
        add(passLabel);

        JPasswordField passField = new JPasswordField();
        passField.setBounds(350, 280, 200, 30);
        add(passField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(270, 340, 120, 40);
        loginBtn.setBackground(new Color(25, 118, 210));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword());

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password!");
            } else {
                if (authenticateUser(name, password)) {
                    mainApp.setUserName(name);
                    mainApp.loadAllData();
                    mainApp.showScreen("DASHBOARD");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid credentials! Please register first.",
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(loginBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(410, 340, 120, 40);
        registerBtn.setBackground(new Color(76, 175, 80));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String password = new String(passField.getPassword());

            if (name.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password!");
            } else {
                if (registerUser(name, password)) {
                    JOptionPane.showMessageDialog(this,
                            "Registration successful! You can now login.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Registration failed! Username may already exist.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        add(registerBtn);

        JLabel infoLabel = new JLabel("Data is saved to files automatically");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoLabel.setForeground(Color.GRAY);
        infoLabel.setBounds(270, 400, 300, 20);
        add(infoLabel);
    }

    private boolean registerUser(String username, String password) {
        if (userExists(username)) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            writer.println(username + "|" + password);
            System.out.println("User registered to file: " + username);
            return true;
        } catch (IOException e) {
            System.err.println("Error registering user to file: " + e.getMessage());
            return false;
        }
    }

    private boolean userExists(String username) {
        File file = new File(USERS_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 1 && parts[0].equals(username)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.err.println("Error checking user: " + e.getMessage());
        }
        return false;
    }

    private boolean authenticateUser(String username, String password) {
        File file = new File(USERS_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    if (parts[0].equals(username) && parts[1].equals(password)) {
                        System.out.println("User authenticated from file: " + username);
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error authenticating user: " + e.getMessage());
        }
        return false;
    }
}
