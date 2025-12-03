import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CGPAPlannerPage extends JPanel {
    private Main mainApp;
    private JTextField currentCGPA, targetCGPA, completedSemesters, remainingSemesters;
    private JTextArea cgpaResult;
    private static final String DATA_FOLDER = "student_data";

    public CGPAPlannerPage(Main app) {
        this.mainApp = app;
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("CGPA Planner");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(320, 20, 200, 30);
        add(title);

        JLabel label1 = new JLabel("Current CGPA:");
        label1.setBounds(100, 80, 150, 25);
        add(label1);

        currentCGPA = new JTextField();
        currentCGPA.setBounds(250, 80, 150, 30);
        add(currentCGPA);

        JLabel label2 = new JLabel("Target CGPA:");
        label2.setBounds(450, 80, 150, 25);
        add(label2);

        targetCGPA = new JTextField();
        targetCGPA.setBounds(570, 80, 150, 30);
        add(targetCGPA);

        JLabel label3 = new JLabel("Completed Semesters:");
        label3.setBounds(100, 130, 150, 25);
        add(label3);

        completedSemesters = new JTextField();
        completedSemesters.setBounds(250, 130, 150, 30);
        add(completedSemesters);

        JLabel label4 = new JLabel("Remaining Semesters:");
        label4.setBounds(450, 130, 150, 25);
        add(label4);

        remainingSemesters = new JTextField();
        remainingSemesters.setBounds(570, 130, 150, 30);
        add(remainingSemesters);

        JButton calculateBtn = new JButton("Calculate");
        calculateBtn.setBounds(300, 190, 200, 40);
        calculateBtn.setBackground(new Color(76, 175, 80));
        calculateBtn.setForeground(Color.WHITE);
        calculateBtn.addActionListener(e -> calculateCGPA());
        add(calculateBtn);

        cgpaResult = new JTextArea();
        cgpaResult.setEditable(false);
        cgpaResult.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(cgpaResult);
        scroll.setBounds(100, 250, 600, 180);
        add(scroll);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(350, 460, 100, 40);
        backBtn.setBackground(new Color(96, 125, 139));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainApp.showScreen("DASHBOARD"));
        add(backBtn);
    }

    private String getCgpaRecordsFile() {
        return DATA_FOLDER + "/" + mainApp.getUserName() + "/cgpa_records.txt";
    }

    private String getUserFolder() {
        return DATA_FOLDER + "/" + mainApp.getUserName();
    }

    private void calculateCGPA() {
        try {
            double current = Double.parseDouble(currentCGPA.getText());
            double target = Double.parseDouble(targetCGPA.getText());
            int completedSems = Integer.parseInt(completedSemesters.getText());
            int remainingSems = Integer.parseInt(remainingSemesters.getText());

            // Validate inputs
            if (current < 0 || current > 10 || target < 0 || target > 10) {
                JOptionPane.showMessageDialog(this, "CGPA values must be between 0 and 10!");
                return;
            }
            if (completedSems < 0 || remainingSems <= 0) {
                JOptionPane.showMessageDialog(this, "Semesters must be positive values!");
                return;
            }
            if (target < current) {
                JOptionPane.showMessageDialog(this, "Target CGPA cannot be less than current CGPA!");
                return;
            }

            // Correct formula: Required SGPA = (Target × Total Sems - Current × Completed Sems) / Remaining Sems
            int totalSemesters = completedSems + remainingSems;
            double required = (target * totalSemesters - current * completedSems) / remainingSems;

            cgpaResult.setText("CGPA CALCULATION RESULTS\n\n");
            cgpaResult.append("Current CGPA: " + current + "\n");
            cgpaResult.append("Target CGPA: " + target + "\n");
            cgpaResult.append("Completed Semesters: " + completedSems + "\n");
            cgpaResult.append("Remaining Semesters: " + remainingSems + "\n\n");
            cgpaResult.append("Required Average SGPA: " + String.format("%.2f", required) + "\n\n");

            if (required > 10) {
                cgpaResult.append("⚠️ Target not achievable! (Required SGPA > 10.0)\n");
                cgpaResult.append("Consider a more realistic target CGPA.\n");
            } else if (required > 9) {
                cgpaResult.append("📚 Need excellent grades! (A+ in most subjects)\n");
            } else if (required > 8) {
                cgpaResult.append("📖 Need very good grades! (Mostly A grades)\n");
            } else if (required > 7) {
                cgpaResult.append("✓ Target achievable with good effort!\n");
            } else {
                cgpaResult.append("✓ Target easily achievable!\n");
            }

            saveCGPARecord(current, target, completedSems, remainingSems, required);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
        }
    }

    private void saveCGPARecord(double currentCgpa, double targetCgpa, int completedSems,
                                int remainingSems, double requiredSgpa) {
        File userFolder = new File(getUserFolder());
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(getCgpaRecordsFile(), true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + " | Current: " + currentCgpa + " | Target: " + targetCgpa +
                    " | Completed: " + completedSems + " | Remaining: " + remainingSems +
                    " | Required SGPA: " + String.format("%.2f", requiredSgpa));
            System.out.println("CGPA record saved to file");
        } catch (IOException e) {
            System.err.println("Error saving CGPA record: " + e.getMessage());
        }
    }
}