import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TimetablePage extends JPanel {
    private Main mainApp;
    private JTextArea timetableDisplay;
    private static final String DATA_FOLDER = "student_data";

    public TimetablePage(Main app) {
        this.mainApp = app;
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Timetable Generator");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(280, 20, 300, 30);
        add(title);

        timetableDisplay = new JTextArea();
        timetableDisplay.setEditable(false);
        timetableDisplay.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(timetableDisplay);
        scroll.setBounds(50, 70, 700, 380);
        add(scroll);

        JButton generateBtn = new JButton("Generate Timetable");
        generateBtn.setBounds(150, 470, 180, 40);
        generateBtn.setBackground(new Color(255, 152, 0));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.addActionListener(e -> generateTimetable());
        add(generateBtn);

        JButton saveBtn = new JButton("Save to File");
        saveBtn.setBounds(350, 470, 150, 40);
        saveBtn.setBackground(new Color(76, 175, 80));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> {
            if (!timetableDisplay.getText().isEmpty()) {
                saveTimetableToFile(timetableDisplay.getText());
                JOptionPane.showMessageDialog(this, "Timetable saved to: " + getTimetableFile());
            } else {
                JOptionPane.showMessageDialog(this, "Generate a timetable first!");
            }
        });
        add(saveBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(520, 470, 100, 40);
        backBtn.setBackground(new Color(96, 125, 139));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainApp.showScreen("DASHBOARD"));
        add(backBtn);
    }

    private String getTimetableFile() {
        return DATA_FOLDER + "/" + mainApp.getUserName() + "/timetable.txt";
    }

    private String getUserFolder() {
        return DATA_FOLDER + "/" + mainApp.getUserName();
    }

    private void generateTimetable() {
        String startTimeStr = JOptionPane.showInputDialog("Enter start time (hour in 24h format, e.g., 6 for 6 AM):");
        if (startTimeStr == null || startTimeStr.trim().isEmpty()) return;

        int startHour;
        try {
            startHour = Integer.parseInt(startTimeStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            return;
        }

        String endTimeStr = JOptionPane.showInputDialog("Enter end time (hour in 24h format, e.g., 18 for 6 PM):");
        if (endTimeStr == null || endTimeStr.trim().isEmpty()) return;

        int endHour;
        try {
            endHour = Integer.parseInt(endTimeStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            return;
        }

        if (endHour <= startHour) {
            JOptionPane.showMessageDialog(this, "End time must be after start time!");
            return;
        }

        int totalMinutes = (endHour - startHour) * 60;
        String numSubjectsStr = JOptionPane.showInputDialog("How many subjects do you want to study?");
        if (numSubjectsStr == null || numSubjectsStr.trim().isEmpty()) return;

        int numSubjects;
        try {
            numSubjects = Integer.parseInt(numSubjectsStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            return;
        }

        ArrayList<String> userSubjects = new ArrayList<>();
        for (int i = 0; i < numSubjects; i++) {
            String subject = JOptionPane.showInputDialog("Enter subject " + (i + 1) + " name:");
            if (subject != null && !subject.trim().isEmpty()) {
                userSubjects.add(subject.trim());
            }
        }

        if (userSubjects.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No subjects entered!");
            return;
        }

        int breakMinutes = 10;
        int totalBreaks = (numSubjects - 1) * breakMinutes;
        int studyMinutes = (totalMinutes - totalBreaks) / numSubjects;

        int currentHour = startHour;
        int currentMinute = 0;

        timetableDisplay.setText("========== DAILY STUDY TIMETABLE ==========\n\n");

        for (int i = 0; i < userSubjects.size(); i++) {
            int endHourSubj = currentHour + (currentMinute + studyMinutes) / 60;
            int endMinuteSubj = (currentMinute + studyMinutes) % 60;

            timetableDisplay.append(userSubjects.get(i) + " - " + formatTime(currentHour, currentMinute)
                    + " to " + formatTime(endHourSubj, endMinuteSubj) + "\n");

            currentHour = endHourSubj;
            currentMinute = endMinuteSubj + breakMinutes;
            if (currentMinute >= 60) {
                currentHour += currentMinute / 60;
                currentMinute = currentMinute % 60;
            }

            if (i < userSubjects.size() - 1) {
                timetableDisplay.append("Break - 10 min\n\n");
            } else {
                timetableDisplay.append("\n");
            }
        }
    }

    private String formatTime(int hour24, int minute) {
        String period = (hour24 >= 12) ? "PM" : "AM";
        int hour12 = hour24 % 12;
        if (hour12 == 0) hour12 = 12;
        return String.format("%02d:%02d %s", hour12, minute, period);
    }

    private void saveTimetableToFile(String timetableContent) {
        File userFolder = new File(getUserFolder());
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (FileWriter writer = new FileWriter(getTimetableFile())) {
            writer.write("Generated on: " + LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            writer.write("\n\n");
            writer.write(timetableContent);
            System.out.println("Timetable saved to file");
        } catch (IOException e) {
            System.err.println("Error saving timetable: " + e.getMessage());
        }
    }
}