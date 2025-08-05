package calculadormovimiento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AnimationPanel extends JPanel {
    private List<SimulationData> simulationData;
    private int currentIndex = 0;
    private Timer animationTimer;
    private final int FRAME_DELAY = 100; // Delay in milliseconds between frames

    public AnimationPanel() {
        setBackground(Color.WHITE);
    }

    public void startAnimation(List<SimulationData> simulationData) {
        this.simulationData = simulationData;
        this.currentIndex = 0;

        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(FRAME_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentIndex < simulationData.size()) {
                    repaint(); // Calls paintComponent to draw the next frame
                    currentIndex++;
                } else {
                    animationTimer.stop(); // Stops the animation when finished
                }
            }
        });

        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (simulationData == null || currentIndex >= simulationData.size()) {
            return;
        }

        SimulationData data = simulationData.get(currentIndex);

        // Parse the position values from the simulation data
        String[] position = data.position.replace("(", "").replace(")", "").split(", ");
        double x = Double.parseDouble(position[0]);
        double y = Double.parseDouble(position[1]);

        // Convert simulation coordinates to panel coordinates (invert y-axis)
        int panelX = (int) (x * 10); // Adjust scaling factor as needed
        int panelY = getHeight() - (int) (y * 10);

        // Draw the object (a circle) representing the motion
        g.setColor(Color.RED);
        g.fillOval(panelX - 5, panelY - 5, 10, 10); // Draw a small circle centered on (x, y)
    }

    public void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }
}