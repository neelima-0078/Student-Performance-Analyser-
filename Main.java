import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class Main extends JFrame {
    // User data
    private String userName = "Student";

    // Card Layout for switching screens
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Page instances
    private LoginPage loginPage;
    private GoalsPage goalsPage;
    private CGPAPlannerPage cgpaPage;
    private TimetablePage timetablePage;
    private SubjectTrackerPage subjectPage;
    private WellnessPage wellnessPage;

    // File handling constants
    private static final String DATA_FOLDER = "student_data";

    public Main() {
        setTitle("Student Performance Analyzer");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize file handling
        initializeFileSystem();

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Create page instances
        loginPage = new LoginPage(this);
        goalsPage = new GoalsPage(this);
        cgpaPage = new CGPAPlannerPage(this);
        timetablePage = new TimetablePage(this);
        subjectPage = new SubjectTrackerPage(this);
        wellnessPage = new WellnessPage(this);

        // Add all screens
        mainPanel.add(loginPage, "LOGIN");
        mainPanel.add(createDashboard(), "DASHBOARD");
        mainPanel.add(goalsPage, "GOALS");
        mainPanel.add(cgpaPage, "CGPA");
        mainPanel.add(timetablePage, "TIMETABLE");
        mainPanel.add(subjectPage, "SUBJECTS");
        mainPanel.add(wellnessPage, "WELLNESS");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");

        // Add window listener to save data on close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveAllData();
            }
        });

        setVisible(true);
    }

    private void initializeFileSystem() {
        try {
            File dataFolder = new File(DATA_FOLDER);
            if (!dataFolder.exists()) {
                boolean created = dataFolder.mkdirs();
                if (created) {
                    System.out.println("Data folder created successfully: " + dataFolder.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating data folder: " + e.getMessage());
        }
    }

    // Dashboard screen
    private JPanel createDashboard() {
        JPanel panel = new JPanel(null);
        panel.setBackground(Color.WHITE);

        JPanel welcomePanel = new JPanel();
        welcomePanel.setBounds(0, 0, 800, 100);
        welcomePanel.setBackground(new Color(25, 118, 210));
        welcomePanel.setLayout(null);

        JLabel welcome = new JLabel("Hello! Welcome Back");
        welcome.setFont(new Font("Arial", Font.BOLD, 24));
        welcome.setForeground(Color.WHITE);
        welcome.setBounds(270, 20, 300, 30);
        welcomePanel.add(welcome);

        JLabel nameLabel = new JLabel(userName);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(350, 55, 200, 30);
        nameLabel.setName("userName");
        welcomePanel.add(nameLabel);

        panel.add(welcomePanel);

        // Dashboard buttons
        JButton goalsBtn = createButton("Set Goals", 100, 150, new Color(76, 175, 80));
        goalsBtn.addActionListener(e -> showScreen("GOALS"));
        panel.add(goalsBtn);

        JButton cgpaBtn = createButton("CGPA Planner", 420, 150, new Color(33, 150, 243));
        cgpaBtn.addActionListener(e -> showScreen("CGPA"));
        panel.add(cgpaBtn);

        JButton timetableBtn = createButton("Timetable Generator", 100, 280, new Color(255, 152, 0));
        timetableBtn.addActionListener(e -> showScreen("TIMETABLE"));
        panel.add(timetableBtn);

        JButton subjectBtn = createButton("Subject Tracker", 420, 280, new Color(156, 39, 176));
        subjectBtn.addActionListener(e -> showScreen("SUBJECTS"));
        panel.add(subjectBtn);

        JButton wellnessBtn = createButton("Wellness Tools", 100, 410, new Color(233, 30, 99));
        wellnessBtn.addActionListener(e -> showScreen("WELLNESS"));
        panel.add(wellnessBtn);

        JButton logoutBtn = createButton("Logout", 420, 410, new Color(244, 67, 54));
        logoutBtn.addActionListener(e -> logout());
        panel.add(logoutBtn);

        return panel;
    }

    private JButton createButton(String text, int x, int y, Color color) {
        JButton btn = new JButton(text);
        btn.setBounds(x, y, 280, 100);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        return btn;
    }

    // Public methods for navigation
    public void showScreen(String screenName) {
        cardLayout.show(mainPanel, screenName);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
        updateDashboardName();
    }

    private void updateDashboardName() {
        Component[] components = mainPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                for (Component c : p.getComponents()) {
                    if (c instanceof JPanel) {
                        JPanel inner = (JPanel) c;
                        for (Component ic : inner.getComponents()) {
                            if (ic instanceof JLabel && ic.getName() != null && ic.getName().equals("userName")) {
                                ((JLabel) ic).setText(userName);
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadAllData() {
        goalsPage.loadData();
        subjectPage.loadData();
    }

    public void saveAllData() {
        if (userName != null && !userName.equals("Student")) {
            goalsPage.saveData();
            subjectPage.saveData();
        }
    }

    private void logout() {
        saveAllData();
        goalsPage.clearData();
        subjectPage.clearData();
        userName = "Student";
        showScreen("LOGIN");
    }

    public static void main(String[] args) {
        new Main();
    }
}
