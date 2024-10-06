import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScreenOverlay {
    public static void main(String[] args) {
        // Show initial message in a dialog box
        JOptionPane.showMessageDialog(null, "Close this box to show overlay", "Info", JOptionPane.INFORMATION_MESSAGE);

        // Create a new JFrame for the overlay
        JFrame overlay = new JFrame();

        // Set the overlay to be undecorated and full screen
        overlay.setUndecorated(true);
        overlay.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set the background color to be transparent
        overlay.setBackground(new Color(0, 0, 0, 0)); // Fully transparent

        // Create a JPanel with a semi-transparent background
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
        panel.setLayout(new BorderLayout());

        // Add prompt message to the overlay
        JLabel label = new JLabel("Click anywhere to get coordinates", SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        panel.add(label, BorderLayout.CENTER);

        // Add the panel to the frame
        overlay.add(panel);

        // Show the overlay
        overlay.setVisible(true);

        // Add mouse listener to capture clicks
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getXOnScreen(); // Get x coordinate on screen
                int y = e.getYOnScreen(); // Get y coordinate on screen

                // Show a message dialog with the coordinates
                JOptionPane.showMessageDialog(overlay, "Coordinates: (" + x + ", " + y + ")");

                // Close the overlay after showing the coordinates
                overlay.dispose();
            }
        });

    }
}