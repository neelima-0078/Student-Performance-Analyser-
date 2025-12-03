import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WellnessPage extends JPanel {
    private Main mainApp;
    private javax.swing.Timer timer;
    private int timeLeft = 1500;
    private JLabel timerLabel;
    private static final String DATA_FOLDER = "student_data";

    public WellnessPage(Main app) {
        this.mainApp = app;
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Wellness Tools");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(310, 20, 200, 30);
        add(title);

        // Pomodoro Timer
        JPanel pomodoroPanel = new JPanel(null);
        pomodoroPanel.setBounds(100, 80, 600, 150);
        pomodoroPanel.setBackground(new Color(244, 67, 54));
        pomodoroPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel pomodoroTitle = new JLabel("Pomodoro Timer (25 minutes)");
        pomodoroTitle.setFont(new Font("Arial", Font.BOLD, 18));
        pomodoroTitle.setForeground(Color.WHITE);
        pomodoroTitle.setBounds(180, 10, 300, 25);
        pomodoroPanel.add(pomodoroTitle);

        timerLabel = new JLabel("25:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 48));
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setBounds(240, 45, 150, 50);
        pomodoroPanel.add(timerLabel);

        JButton startBtn = new JButton("Start");
        startBtn.setBounds(150, 105, 100, 35);
        startBtn.setBackground(new Color(76, 175, 80));
        startBtn.setForeground(Color.WHITE);
        startBtn.addActionListener(e -> startTimer());
        pomodoroPanel.add(startBtn);

        JButton pauseBtn = new JButton("Pause");
        pauseBtn.setBounds(260, 105, 100, 35);
        pauseBtn.setBackground(new Color(255, 152, 0));
        pauseBtn.setForeground(Color.WHITE);
        pauseBtn.addActionListener(e -> pauseTimer());
        pomodoroPanel.add(pauseBtn);

        JButton resetBtn = new JButton("Reset");
        resetBtn.setBounds(370, 105, 100, 35);
        resetBtn.setBackground(new Color(33, 150, 243));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> resetTimer());
        pomodoroPanel.add(resetBtn);

        add(pomodoroPanel);

        // Mood Tracker
        JButton moodBtn = new JButton("Mood Tracker");
        moodBtn.setBounds(100, 260, 180, 50);
        moodBtn.setBackground(new Color(76, 175, 80));
        moodBtn.setForeground(Color.WHITE);
        moodBtn.setFont(new Font("Arial", Font.BOLD, 14));
        moodBtn.addActionListener(e -> trackMood());
        add(moodBtn);

        // Mood History
        JButton moodHistoryBtn = new JButton("Mood History");
        moodHistoryBtn.setBounds(300, 260, 180, 50);
        moodHistoryBtn.setBackground(new Color(0, 150, 136));
        moodHistoryBtn.setForeground(Color.WHITE);
        moodHistoryBtn.setFont(new Font("Arial", Font.BOLD, 14));
        moodHistoryBtn.addActionListener(e -> {
            String history = readMoodHistory();
            JOptionPane.showMessageDialog(this, history, "Mood History", JOptionPane.INFORMATION_MESSAGE);
        });
        add(moodHistoryBtn);

        // Study Stats
        JButton statsBtn = new JButton("Study Stats");
        statsBtn.setBounds(500, 260, 180, 50);
        statsBtn.setBackground(new Color(103, 58, 183));
        statsBtn.setForeground(Color.WHITE);
        statsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        statsBtn.addActionListener(e -> {
            String stats = getStudyStatistics();
            JOptionPane.showMessageDialog(this, stats, "Study Statistics", JOptionPane.INFORMATION_MESSAGE);
        });
        add(statsBtn);

        // Study Tips
        JButton tipsBtn = new JButton("Study Tips");
        tipsBtn.setBounds(100, 330, 180, 50);
        tipsBtn.setBackground(new Color(255, 152, 0));
        tipsBtn.setForeground(Color.WHITE);
        tipsBtn.setFont(new Font("Arial", Font.BOLD, 14));
        tipsBtn.addActionListener(e -> showTips());
        add(tipsBtn);

        // Motivational Quotes
        JButton quotesBtn = new JButton("Motivational Quotes");
        quotesBtn.setBounds(300, 330, 180, 50);
        quotesBtn.setBackground(new Color(156, 39, 176));
        quotesBtn.setForeground(Color.WHITE);
        quotesBtn.setFont(new Font("Arial", Font.BOLD, 14));
        quotesBtn.addActionListener(e -> showQuotes());
        add(quotesBtn);

        // Breathing Exercise
        JButton breathingBtn = new JButton("Breathing Exercise");
        breathingBtn.setBounds(500, 330, 180, 50);
        breathingBtn.setBackground(new Color(33, 150, 243));
        breathingBtn.setForeground(Color.WHITE);
        breathingBtn.setFont(new Font("Arial", Font.BOLD, 14));
        breathingBtn.addActionListener(e -> breathingExercise());
        add(breathingBtn);

        JButton backBtn = new JButton("Back");
        backBtn.setBounds(350, 420, 100, 40);
        backBtn.setBackground(new Color(96, 125, 139));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> mainApp.showScreen("DASHBOARD"));
        add(backBtn);
    }

    private String getMoodLogFile() {
        return DATA_FOLDER + "/" + mainApp.getUserName() + "/mood_log.txt";
    }

    private String getStudySessionsFile() {
        return DATA_FOLDER + "/" + mainApp.getUserName() + "/study_sessions.txt";
    }

    private String getUserFolder() {
        return DATA_FOLDER + "/" + mainApp.getUserName();
    }

    private void startTimer() {
        if (timer == null) {
            timer = new javax.swing.Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    timeLeft--;
                    timerLabel.setText(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));

                    if (timeLeft <= 0) {
                        timer.stop();
                        saveStudySession(25);
                        JOptionPane.showMessageDialog(null, "Time's up! Take a break!\nSession saved.");
                        timeLeft = 1500;
                        timerLabel.setText("25:00");
                    }
                }
            });
        }
        timer.start();
    }

    private void pauseTimer() {
        if (timer != null) timer.stop();
    }

    private void resetTimer() {
        if (timer != null) timer.stop();
        timeLeft = 1500;
        timerLabel.setText("25:00");
    }

    private void trackMood() {
        String[] moods = {"😊 Happy", "😔 Sad", "😰 Stressed", "😌 Calm", "😤 Frustrated"};
        String mood = (String) JOptionPane.showInputDialog(this, "How are you feeling?",
                "Mood Tracker", JOptionPane.QUESTION_MESSAGE, null, moods, moods[0]);

        if (mood != null) {
            logMoodToFile(mood);
            JOptionPane.showMessageDialog(this, "Mood saved! Take care 💚");
        }
    }

    private void showTips() {
        String tips = "📚 STUDY TIPS\n\n";
        tips += "1. Take regular breaks\n";
        tips += "2. Study in a quiet place\n";
        tips += "3. Review notes regularly\n";
        tips += "4. Get enough sleep\n";
        tips += "5. Stay hydrated\n";
        tips += "6. Set achievable goals\n";
        JOptionPane.showMessageDialog(this, tips);
    }

    private void showQuotes() {
        String[] quotes = {
                "Success is not final, failure is not fatal.",
                "Believe you can and you're halfway there.",
                "The expert in anything was once a beginner.",
                "Education is the most powerful weapon."
        };
        Random r = new Random();
        JOptionPane.showMessageDialog(this, "💡 " + quotes[r.nextInt(quotes.length)] + " 💡");
    }

    private void breathingExercise() {
        String info = "🧘 4-7-8 BREATHING\n\n";
        info += "1. Breathe IN for 4 seconds\n";
        info += "2. HOLD for 7 seconds\n";
        info += "3. Breathe OUT for 8 seconds\n\n";
        info += "Repeat 3-4 times!";
        JOptionPane.showMessageDialog(this, info);
    }

    private void logMoodToFile(String mood) {
        File userFolder = new File(getUserFolder());
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(getMoodLogFile(), true))) {
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.println(timestamp + " | " + mood);
            System.out.println("Mood logged to file");
        } catch (IOException e) {
            System.err.println("Error logging mood: " + e.getMessage());
        }
    }

    private String readMoodHistory() {
        StringBuilder history = new StringBuilder();
        File file = new File(getMoodLogFile());

        if (!file.exists()) {
            return "No mood history found.";
        }

        try (Scanner scanner = new Scanner(file)) {
            history.append("=== Mood History ===\n\n");
            while (scanner.hasNextLine()) {
                history.append(scanner.nextLine()).append("\n");
            }
        } catch (FileNotFoundException e) {
            return "Error reading mood history: " + e.getMessage();
        }

        return history.length() > 25 ? history.toString() : "No mood history found.";
    }

    private void saveStudySession(int durationMinutes) {
        File userFolder = new File(getUserFolder());
        if (!userFolder.exists()) {
            userFolder.mkdirs();
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(getStudySessionsFile(), true))) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            writer.println(timestamp + " | " + date + " | " + durationMinutes + " minutes");
            System.out.println("Study session saved to file");
        } catch (IOException e) {
            System.err.println("Error saving study session to file: " + e.getMessage());
        }
    }

    private String getStudyStatistics() {
        File file = new File(getStudySessionsFile());
        if (!file.exists()) {
            return "=== Study Statistics ===\n\nNo study sessions recorded yet.\nComplete a Pomodoro session to start tracking!";
        }

        int totalSessions = 0;
        int totalMinutes = 0;
        int longestSession = 0;
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int todaySessions = 0;
        int todayMinutes = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    totalSessions++;

                    String durationPart = parts[2].trim().replace(" minutes", "");
                    int duration = Integer.parseInt(durationPart);
                    totalMinutes += duration;

                    if (duration > longestSession) {
                        longestSession = duration;
                    }

                    String sessionDate = parts[1].trim();
                    if (sessionDate.equals(today)) {
                        todaySessions++;
                        todayMinutes += duration;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error reading study sessions: " + e.getMessage());
        }

        if (totalSessions == 0) {
            return "=== Study Statistics ===\n\nNo study sessions recorded yet.\nComplete a Pomodoro session to start tracking!";
        }

        double avgDuration = (double) totalMinutes / totalSessions;
        int totalHours = totalMinutes / 60;
        int remainingMinutes = totalMinutes % 60;

        StringBuilder stats = new StringBuilder();
        stats.append("=== Study Statistics ===\n\n");
        stats.append("📊 Overall Stats:\n");
        stats.append("   Total Sessions: ").append(totalSessions).append("\n");
        stats.append("   Total Study Time: ").append(totalHours).append("h ").append(remainingMinutes).append("m (").append(totalMinutes).append(" minutes)\n");
        stats.append("   Average Session: ").append(String.format("%.1f", avgDuration)).append(" minutes\n");
        stats.append("   Longest Session: ").append(longestSession).append(" minutes\n\n");
        stats.append("📅 Today's Stats:\n");
        stats.append("   Sessions Today: ").append(todaySessions).append("\n");
        stats.append("   Time Today: ").append(todayMinutes).append(" minutes\n");

        if (todaySessions >= 4) {
            stats.append("\n🏆 Amazing! You're on fire today!");
        } else if (todaySessions >= 2) {
            stats.append("\n💪 Great progress! Keep going!");
        } else if (todaySessions >= 1) {
            stats.append("\n👍 Good start! Try to complete a few more sessions.");
        } else {
            stats.append("\n⏰ No sessions today yet. Start a Pomodoro!");
        }

        return stats.toString();
    }
}