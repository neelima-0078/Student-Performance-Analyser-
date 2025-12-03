import javax.swing.*;
import java.awt.*;

/**
 * LogoutPage class handles logout functionality.
 * Note: Logout is implemented directly in Main.java as a button action
 * that saves data, clears user data, and returns to login screen.
 * This class is created to fulfill the requirement of 8 separate files.
 */
public class LogoutPage {

    /**
     * Performs logout operation
     * @param mainApp Reference to the main application
     */
    public static void performLogout(Main mainApp) {
        // Save all user data before logout
        mainApp.saveAllData();

        // Show confirmation message
        int response = JOptionPane.showConfirmDialog(
                mainApp,
                "Are you sure you want to logout? All data will be saved.",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            // Navigate to login screen
            mainApp.showScreen("LOGIN");

            // Display logout success message
            JOptionPane.showMessageDialog(
                    mainApp,
                    "Logged out successfully! Your data has been saved.",
                    "Logout",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    /**
     * Creates a logout confirmation dialog
     * @param parent Parent component
     * @return true if user confirms logout, false otherwise
     */
    public static boolean confirmLogout(Component parent) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                "Do you want to save your changes before logging out?",
                "Save Changes",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.CANCEL_OPTION) {
            return false; // User cancelled logout
        }

        return true; // User confirmed logout
    }

    /**
     * Display goodbye message
     * @param parent Parent component
     * @param userName Name of the user logging out
     */
    public static void showGoodbyeMessage(Component parent, String userName) {
        String message = "Goodbye, " + userName + "!\n\n" +
                "Your progress has been saved.\n" +
                "See you next time!";

        JOptionPane.showMessageDialog(
                parent,
                message,
                "Logout Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}