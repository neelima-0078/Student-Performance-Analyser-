import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class GoalsPage extends JPanel {
    private Main mainApp;
    private ArrayList<String> goalsList = new ArrayList<>();
    private JTextArea goalsDisplay;
    private static final String DATA_FOLDER = "student_data";

    public GoalsPage(Main app) {
        this.mainApp = app;
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Set Goals");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(330, 20, 200, 30);
        add(title);

        goalsDisplay = new JTextArea("No goals yet! Click 'Add Goal' to create one.");
        goalsDisplay.setEditable(false);
        goalsDisplay.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(goalsDisplay);
        scroll.setBounds(50, 70, 700, 320);
        add(scroll);

        JButton addBtn = new JButton("Add Goal");
        addBtn.setBounds(50, 410, 120, 40);
        addBtn.setBackground(new Color(76, 175, 80));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addGoal());
        add(addBtn);

        JButton updateBtn = new JButton("Update Progress");
        updateBtn.setBounds(190, 410, 150, 40);
        updateBtn.setBackground(new Color(255, 152, 0));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.addActionListener(e -> updateGoal());
        add(updateBtn);

        JButton exportBtn = new JButton("Export CSV");
        exportBtn.setBounds(360, 410, 120, 40);
        exportBtn.setBackground(new Color(33, 150, 243));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.addActionListener(e -> exportGoalsToCSV());
        add(exportBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(500, 410, 100, 40);
        backBtn.setBackground(new Color(96, 125, 139));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainApp.showScreen("DASHBOARD"));
        add(backBtn);
    }

    private String getGoalsFile() {
        return DATA_FOLDER + "/" + mainApp.getUserName() + "/goals.txt";
    }

    private String getUserFolder() {
        return DATA_FOLDER + "/" + mainApp.getUserName();
    }

    public void loadData() {
        File file = new File(getGoalsFile());
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            goalsList.clear();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    goalsList.add(line);
                }
            }
            System.out.println("Goals loaded from file: " + goalsList.size() + " goals");
            displayGoals();
        } catch (IOException e) {
            System.err.println("Error loading goals: " + e.getMessage());
        }
    }

    public void saveData() {
        File userFolder = new File(getUserFolder());
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getGoalsFile()))) {
            for (String goal : goalsList) {
                writer.write(goal);
                writer.newLine();
            }
            System.out.println("Goals saved to file: " + goalsList.size() + " goals");
        } catch (IOException e) {
            System.err.println("Error saving goals: " + e.getMessage());
        }
    }

    public void clearData() {
        goalsList.clear();
        goalsDisplay.setText("No goals yet! Click 'Add Goal' to create one.");
    }

    private void addGoal() {
        String title = JOptionPane.showInputDialog("Enter goal title:");
        if (title == null || title.trim().isEmpty()) return;

        String deadline = JOptionPane.showInputDialog("Enter deadline (YYYY-MM-DD):");
        if (deadline == null || deadline.trim().isEmpty()) return;

        String totalTasksStr = JOptionPane.showInputDialog("Enter total number of tasks/milestones for this goal:");
        if (totalTasksStr == null || totalTasksStr.trim().isEmpty()) return;

        int totalTasks;
        try {
            totalTasks = Integer.parseInt(totalTasksStr);
            if (totalTasks <= 0) {
                JOptionPane.showMessageDialog(this, "Total tasks must be a positive number!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number!");
            return;
        }

        goalsList.add(title + " | Due: " + deadline + " | Tasks: 0/" + totalTasks + " | Progress: 0%");
        saveData();
        displayGoals();
    }

    private void updateGoal() {
        if (goalsList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No goals to update!");
            return;
        }

        String[] goals = new String[goalsList.size()];
        for (int i = 0; i < goalsList.size(); i++) {
            goals[i] = (i + 1) + ". " + goalsList.get(i);
        }

        String selected = (String) JOptionPane.showInputDialog(this, "Select goal:",
                "Update", JOptionPane.QUESTION_MESSAGE, null, goals, goals[0]);

        if (selected != null) {
            int dotIndex = selected.indexOf(".");
            int index = Integer.parseInt(selected.substring(0, dotIndex)) - 1;

            String currentGoal = goalsList.get(index);
            String[] parts = currentGoal.split("\\|");
            String goalTitle = parts[0].trim();
            String deadline = parts[1].trim();

            if (currentGoal.contains("Tasks:")) {
                String tasksPart = parts[2].trim();
                String tasksNumbers = tasksPart.replace("Tasks:", "").trim();
                String[] taskSplit = tasksNumbers.split("/");
                int completedTasks = Integer.parseInt(taskSplit[0].trim());
                int totalTasks = Integer.parseInt(taskSplit[1].trim());

                String[] options = {"Add Completed Tasks", "Set Completed Tasks", "Mark Goal Complete"};
                String choice = (String) JOptionPane.showInputDialog(this,
                        "Current: " + completedTasks + "/" + totalTasks + " tasks completed\nWhat would you like to do?",
                        "Update Progress", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                if (choice == null) return;

                int newCompleted = completedTasks;

                if (choice.equals("Add Completed Tasks")) {
                    String addStr = JOptionPane.showInputDialog("How many more tasks did you complete?");
                    if (addStr == null || addStr.trim().isEmpty()) return;
                    try {
                        int toAdd = Integer.parseInt(addStr);
                        newCompleted = Math.min(completedTasks + toAdd, totalTasks);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number!");
                        return;
                    }
                } else if (choice.equals("Set Completed Tasks")) {
                    String setStr = JOptionPane.showInputDialog("Enter total tasks completed (out of " + totalTasks + "):");
                    if (setStr == null || setStr.trim().isEmpty()) return;
                    try {
                        newCompleted = Integer.parseInt(setStr);
                        if (newCompleted < 0 || newCompleted > totalTasks) {
                            JOptionPane.showMessageDialog(this, "Please enter a number between 0 and " + totalTasks);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid number!");
                        return;
                    }
                } else if (choice.equals("Mark Goal Complete")) {
                    newCompleted = totalTasks;
                }

                int progress = (newCompleted * 100) / totalTasks;
                goalsList.set(index, goalTitle + " | " + deadline + " | Tasks: " + newCompleted + "/" + totalTasks + " | Progress: " + progress + "%");

                if (progress == 100) {
                    JOptionPane.showMessageDialog(this, "🎉 Congratulations! Goal completed!", "Goal Complete", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Progress updated: " + progress + "% (" + newCompleted + "/" + totalTasks + " tasks)", "Updated", JOptionPane.INFORMATION_MESSAGE);
                }

                saveData();
            } else {
                JOptionPane.showMessageDialog(this, "This goal uses old format. Let's update it with task tracking.");
                String totalTasksStr = JOptionPane.showInputDialog("Enter total number of tasks for this goal:");
                if (totalTasksStr == null || totalTasksStr.trim().isEmpty()) return;

                String completedStr = JOptionPane.showInputDialog("Enter number of tasks already completed:");
                if (completedStr == null || completedStr.trim().isEmpty()) return;

                try {
                    int totalTasks = Integer.parseInt(totalTasksStr);
                    int completed = Integer.parseInt(completedStr);

                    if (totalTasks <= 0 || completed < 0 || completed > totalTasks) {
                        JOptionPane.showMessageDialog(this, "Invalid numbers entered!");
                        return;
                    }

                    int progress = (completed * 100) / totalTasks;
                    goalsList.set(index, goalTitle + " | " + deadline + " | Tasks: " + completed + "/" + totalTasks + " | Progress: " + progress + "%");
                    saveData();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers!");
                    return;
                }
            }

            displayGoals();
        }
    }

    private void displayGoals() {
        goalsDisplay.setText("");
        if (goalsList.isEmpty()) {
            goalsDisplay.setText("No goals yet! Click 'Add Goal' to create one.");
        } else {
            for (int i = 0; i < goalsList.size(); i++) {
                goalsDisplay.append((i + 1) + ". " + goalsList.get(i) + "\n\n");
            }
        }
    }

    private void exportGoalsToCSV() {
        String csvFile = getUserFolder() + "/goals_export.csv";
        try (PrintWriter writer = new PrintWriter(new File(csvFile))) {
            writer.println("Goal,Deadline,Tasks,Progress");

            for (String goal : goalsList) {
                String[] parts = goal.split("\\|");
                if (parts.length >= 4) {
                    String title = parts[0].trim();
                    String deadline = parts[1].replace("Due:", "").trim();
                    String tasks = parts[2].replace("Tasks:", "").trim();
                    String progress = parts[3].replace("Progress:", "").trim();
                    writer.println("\"" + title + "\",\"" + deadline + "\",\"" + tasks + "\",\"" + progress + "\"");
                } else if (parts.length >= 3) {
                    String title = parts[0].trim();
                    String deadline = parts[1].replace("Due:", "").trim();
                    String progress = parts[2].replace("Progress:", "").trim();
                    writer.println("\"" + title + "\",\"" + deadline + "\",\"\",\"" + progress + "\"");
                }
            }
            JOptionPane.showMessageDialog(this, "Goals exported to: " + csvFile);
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Error exporting goals: " + e.getMessage());
        }
    }
}