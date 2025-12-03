import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SubjectTrackerPage extends JPanel {
    private Main mainApp;
    private ArrayList<String> subjectsList = new ArrayList<>();
    private JTextArea subjectsDisplay;
    private static final String DATA_FOLDER = "student_data";

    public SubjectTrackerPage(Main app) {
        this.mainApp = app;
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Subject Tracker");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(310, 20, 200, 30);
        add(title);

        subjectsDisplay = new JTextArea("No subjects yet! Click 'Add Subject' to create one.");
        subjectsDisplay.setEditable(false);
        subjectsDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(subjectsDisplay);
        scroll.setBounds(50, 70, 700, 350);
        add(scroll);

        JButton addBtn = new JButton("Add Subject");
        addBtn.setBounds(200, 450, 150, 40);
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addSubject());
        add(addBtn);

        JButton completeBtn = new JButton("Mark Complete");
        completeBtn.setBounds(370, 450, 150, 40);
        completeBtn.setBackground(new Color(33, 150, 243));
        completeBtn.setForeground(Color.WHITE);
        completeBtn.addActionListener(e -> markComplete());
        add(completeBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(540, 450, 100, 40);
        backBtn.setBackground(new Color(96, 125, 139));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainApp.showScreen("DASHBOARD"));
        add(backBtn);
    }

    private String getSubjectsFile() {
        return DATA_FOLDER + "/" + mainApp.getUserName() + "/subjects.txt";
    }

    private String getUserFolder() {
        return DATA_FOLDER + "/" + mainApp.getUserName();
    }

    public void loadData() {
        File file = new File(getSubjectsFile());
        if (!file.exists()) return;

        try (FileInputStream fis = new FileInputStream(file);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {

            String line;
            subjectsList.clear();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    subjectsList.add(line);
                }
            }
            System.out.println("Subjects loaded from file: " + subjectsList.size() + " subjects");
            displaySubjects();
        } catch (IOException e) {
            System.err.println("Error loading subjects: " + e.getMessage());
        }
    }

    public void saveData() {
        File userFolder = new File(getUserFolder());
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (FileOutputStream fos = new FileOutputStream(getSubjectsFile());
             OutputStreamWriter osw = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(osw)) {

            for (String subject : subjectsList) {
                writer.write(subject);
                writer.newLine();
            }
            System.out.println("Subjects saved to file: " + subjectsList.size() + " subjects");
        } catch (IOException e) {
            System.err.println("Error saving subjects: " + e.getMessage());
        }
    }

    public void clearData() {
        subjectsList.clear();
        subjectsDisplay.setText("No subjects yet! Click 'Add Subject'.");
    }

    private void addSubject() {
        String name = JOptionPane.showInputDialog("Enter subject name:");
        if (name == null || name.trim().isEmpty()) return;

        String chapters = JOptionPane.showInputDialog("Enter total chapters:");
        if (chapters == null) return;

        try {
            int totalChapters = Integer.parseInt(chapters);
            subjectsList.add(name + " | Total: " + chapters + " | Completed: 0 | Progress: 0%");
            saveData();
            displaySubjects();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
        }
    }

    private void markComplete() {
        if (subjectsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No subjects to update!");
            return;
        }

        String[] subjects = new String[subjectsList.size()];
        for (int i = 0; i < subjectsList.size(); i++) {
            subjects[i] = (i + 1) + ". " + subjectsList.get(i);
        }

        String selected = (String) JOptionPane.showInputDialog(this, "Select subject:",
                "Mark Complete", JOptionPane.QUESTION_MESSAGE, null, subjects, subjects[0]);

        if (selected != null) {
            int dotIndex = selected.indexOf(".");
            int index = Integer.parseInt(selected.substring(0, dotIndex)) - 1;

            String completed = JOptionPane.showInputDialog("Enter chapters completed:");

            if (completed != null) {
                try {
                    String[] parts = subjectsList.get(index).split("\\|");
                    int total = Integer.parseInt(parts[1].split(":")[1].trim());
                    int done = Integer.parseInt(completed);
                    int progress = (done * 100) / total;

                    subjectsList.set(index, parts[0] + "| Total: " + total +
                            " | Completed: " + done + " | Progress: " + progress + "%");

                    saveData();
                    displaySubjects();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number!");
                }
            }
        }
    }

    private void displaySubjects() {
        subjectsDisplay.setText("");
        if (subjectsList.isEmpty()) {
            subjectsDisplay.setText("No subjects yet! Click 'Add Subject'.");
        } else {
            for (int i = 0; i < subjectsList.size(); i++) {
                subjectsDisplay.append((i + 1) + ". " + subjectsList.get(i) + "\n\n");
            }
        }
    }
}