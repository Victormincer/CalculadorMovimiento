package calculadormovimiento;

import javax.swing.*;
import java.awt.*;
import static java.lang.Double.parseDouble;
import javax.swing.table.DefaultTableModel;

public class PhysicsMotionSimulator extends JFrame {

    private JComboBox<String> motionTypeComboBox;
    private JTextField initialVelocityField, finalVelocityField, timeField, angleField, distanceField;
    private JButton calculateButton, resetButton;
    private JTable dataTable;
    private JTextArea calculationStepsArea, calculationResultArea;
    private JPanel parabolicInputsPanel, muaInputsPanel;

    public PhysicsMotionSimulator() {
        setTitle("Motion Calculator version 1.0");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
        addListeners();

        setVisible(true);
    }

    private void initComponents() {
        motionTypeComboBox = new JComboBox<>();
        motionTypeComboBox.addItem("Movimiento Uniformemente Acelerado (MUA)");
        motionTypeComboBox.addItem("Movimiento Uniforme (MU)");
        motionTypeComboBox.addItem("Caída libre");
        motionTypeComboBox.addItem("Movimiento Parabólico");

        initialVelocityField = new JTextField(10);
        finalVelocityField = new JTextField(10);
        timeField = new JTextField(10);
        angleField = new JTextField(10);
        distanceField = new JTextField(10);

        calculateButton = new JButton("Iniciar cálculo");
        resetButton = new JButton("Resetear datos");

        dataTable = new JTable(new DefaultTableModel(new Object[]{"Tiempo (s)", "Posición (m)", "Velocidad (m/s)"}, 0));
        calculationStepsArea = new JTextArea(10, 30);
        calculationResultArea = new JTextArea(5, 30);

        parabolicInputsPanel = new JPanel(new FlowLayout());
        muaInputsPanel = new JPanel(new FlowLayout());
    }

    private void layoutComponents() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Tipos de movimientos:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(motionTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Velocidad Inicial (m/s):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(initialVelocityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Velocidad Final (m/s):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(finalVelocityField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Tiempo (s):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(timeField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        inputPanel.add(new JLabel("Distancia (m):"), gbc);
        gbc.gridx = 1;
        inputPanel.add(distanceField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        inputPanel.add(muaInputsPanel, gbc);

        parabolicInputsPanel.add(new JLabel("Ángulo (grados):"));
        parabolicInputsPanel.add(angleField);

        inputPanel.add(parabolicInputsPanel, gbc);

        gbc.gridy++;
        inputPanel.add(calculateButton, gbc);
        inputPanel.add(resetButton);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);

        JPanel calculationPanel = new JPanel(new BorderLayout());
        calculationPanel.add(new JScrollPane(calculationStepsArea), BorderLayout.CENTER);
        calculationPanel.add(calculationResultArea, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
        add(calculationPanel, BorderLayout.SOUTH);
    }

    private void addListeners() {
        motionTypeComboBox.addActionListener(e -> updateInputVisibility());
        calculateButton.addActionListener(e -> startSimulation());
        resetButton.addActionListener(e -> resetSimulation());
    }

    private void updateInputVisibility() {
        String motionType = (String) motionTypeComboBox.getSelectedItem();
        boolean isParabolic = motionType.contains("Parabólico");
        boolean isMUA = motionType.contains("Acelerado");
        boolean isFreeFall = motionType.contains("Caída libre");

        parabolicInputsPanel.setVisible(isParabolic);
        muaInputsPanel.setVisible(isMUA || isFreeFall);
        dataTable.setVisible(motionType.contains("Uniforme") || motionType.contains("Acelerado"));
    }

    private void resetSimulation() {
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        model.setRowCount(0);
        calculationStepsArea.setText("");
        calculationResultArea.setText("");
    }

    private void startSimulation() {
        String motionType = (String) motionTypeComboBox.getSelectedItem();

        try {
            double v0 = parseDouble(initialVelocityField.getText());
            double vf = parseDouble(finalVelocityField.getText());
            double t = parseDouble(timeField.getText());
            double x = parseDouble(distanceField.getText());

            if (t == 0) {
                JOptionPane.showMessageDialog(this, "El tiempo no puede ser cero.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (motionType.contains("Acelerado") && x == 0) {
                JOptionPane.showMessageDialog(this, "La distancia no puede ser cero para Movimiento Uniformemente Acelerado.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                return;
            }

            resetSimulation();

            if (motionType.contains("Acelerado")) {
                simulateMUA(v0, vf, t, x);
            } else if (motionType.contains("Uniforme")) {
                simulateMU(v0, t, x);
            } else if (motionType.contains("Caída libre")) {
                simulateFreeFall(v0, vf, 9.81, t);
            } else if (motionType.contains("Parabólico")) {
                double angle = parseDouble(angleField.getText());
                simulateParabolic(v0, angle, t);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese datos válidos en todos los campos requeridos.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ocurrió un error durante la simulación. Revise los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simulateMUA(double v0, double vf, double t, double x) {
        calculationStepsArea.setText("Ecuaciones de Movimiento Uniformemente Acelerado:\n"
                + "v = v₀ + at\n"
                + "x = x₀ + v₀t + ½at²\n"
                + "a = (vf² - v₀²) / (2x)");

        double a = (vf * vf - v0 * v0) / (2 * x);
        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        for (double i = 0; i <= t; i += 0.1) {
            double xi = v0 * i + 0.5 * a * i * i;
            double vi = v0 + a * i;
            model.addRow(new Object[]{String.format("%.1f", i), String.format("%.2f", xi), String.format("%.2f", vi)});
        }
        calculationResultArea.setText(String.format(
                "Aceleración: a = (%.2f² - %.2f²) / (2 * %.2f) = %.2f m/s²\n"
                + "Distancia: x = %.2f * %.2f + ½ * %.2f * %.2f² = %.2f m\n"
                + "Velocidad final: vf = %.2f m/s",
                vf, v0, x, a, v0, t, a, t, (v0 * t + 0.5 * a * t * t), vf
        ));
    }

    private void simulateMU(double v0, double t, double x) {
        calculationStepsArea.setText("Ecuaciones de Movimiento Uniforme:\n"
                + "x = v₀t\n"
                + "v = d/t\n"
                + "t = d/v₀");

        DefaultTableModel model = (DefaultTableModel) dataTable.getModel();
        for (double i = 0; i <= t; i += 0.1) {
            double xi = v0 * i;
            model.addRow(new Object[]{String.format("%.1f", i), String.format("%.2f", xi), String.format("%.2f", v0)});
        }

        calculationResultArea.setText(String.format(
                "Distancia: x = %.2f * %.2f = %.2f m\n"
                + "Velocidad: v = %.2f / %.2f = %.2f m/s\n"
                + "Tiempo: t = %.2f / %.2f = %.2f s",
                v0, t, x, x, t, v0, x, v0, t
        ));
    }

    private void simulateFreeFall(double v0, double vf, double g, double t) {
        calculationStepsArea.setText("Ecuaciones de caída libre:\n"
                + "v = v₀ + gt\n"
                + "y = v₀t + ½gt²\n"
                + "t = (vf - v₀) / g");

        double y = v0 * t + 0.5 * g * t * t;
        calculationResultArea.setText(String.format(
                "Tiempo de caída: t = (%.2f - %.2f) / %.2f = %.2f s\n"
                + "Velocidad final: v = %.2f + %.2f * %.2f = %.2f m/s\n"
                + "Distancia recorrida: y = %.2f * %.2f + ½ * %.2f * %.2f² = %.2f m",
                vf, v0, g, t, v0, g, t, (v0 + g * t), v0, t, g, t, y
        ));
    }

    private void simulateParabolic(double v0, double angle, double t) {
        double g = 9.81;
        double angleRad = Math.toRadians(angle);
        double vx = v0 * Math.cos(angleRad);
        double vy = v0 * Math.sin(angleRad);

        double tf = (2 * vy) / g;
        double R = v0 * v0 * Math.sin(2 * angleRad) / g;
        double hMax = (v0 * v0 * Math.sin(angleRad) * Math.sin(angleRad)) / (2 * g);

        calculationStepsArea.setText("Ecuaciones de movimiento parabólico:\n"
                + "x = v₀ cos(θ) * t\n"
                + "y = h₀ + v₀ sin(θ) * t - ½gt²\n"
                + "tf = 2 v₀² * sen(θ) / g\n"
                + "R = v₀² * sen(2θ) / g\n"
                + "hMax = v₀² * sen²(θ) / (2g)");

        calculationResultArea.setText(String.format(
                "Tiempo de vuelo: tf = %.2f s\n"
                + "Alcance: R = %.2f m\n"
                + "Altura máxima: hMax = %.2f m\n"
                + "Componentes de velocidad Inicial:\n"
                + "v₀x = %.2f m/s\n"
                + "v₀y = %.2f m/s",
                tf, R, hMax, vx, vy
        ));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PhysicsMotionSimulator::new);
    }
}
