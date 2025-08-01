import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;

public class ReportGeneratorGUI {
    private JFrame frame;
    private JTextArea reportArea;

    public ReportGeneratorGUI() {
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Emergency System Report Generator");
        frame.setSize(1000, 800);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(245, 248, 250));

        // Gradient header panel
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(12, 83, 148), 0, getHeight(), new Color(25, 118, 210));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 60));
        JLabel title = new JLabel("REPORT GENERATOR", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        headerPanel.add(title);

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportArea.setBackground(new Color(253, 253, 253));
        reportArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 227, 232), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        controlPanel.setBackground(new Color(245, 248, 250));

        JButton generateBtn = createModernButton("Generate Report", new Color(46, 125, 50));
        JButton exportBtn = createModernButton("Export to File", new Color(21, 101, 192));
        JButton backBtn = createModernButton("Back", new Color(120, 144, 156));

        generateBtn.addActionListener(this::generateReport);
        exportBtn.addActionListener(this::exportReport);
        backBtn.addActionListener(e -> frame.dispose());

        controlPanel.add(generateBtn);
        controlPanel.add(exportBtn);
        controlPanel.add(backBtn);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
    }

private JButton createModernButton(String text, Color color) {
    JButton button = new JButton(text) {
        private boolean hovered = false;
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            if (getModel().isPressed()) {
                g2.setColor(color.darker());
            } else if (hovered) {
                g2.setColor(color.brighter());
            } else {
                g2.setColor(color);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD));
            
            FontMetrics fm = g2.getFontMetrics();
            java.awt.geom.Rectangle2D r = fm.getStringBounds(getText(), g2);
            int x = (getWidth() - (int) r.getWidth()) / 2;
            int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
            
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(180, 40);
        }
    };
    
    button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    button.setBorder(BorderFactory.createEmptyBorder());
    button.setContentAreaFilled(false);
    button.setFocusPainted(false);
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    
    // Create a final reference to access in MouseAdapter
    final JButton finalButton = button;
    
    button.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseEntered(java.awt.event.MouseEvent evt) {
            try {
                // Access hovered field through reflection
                java.lang.reflect.Field hoveredField = finalButton.getClass().getDeclaredField("hovered");
                hoveredField.setAccessible(true);
                hoveredField.setBoolean(finalButton, true);
                finalButton.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent evt) {
            try {
                // Access hovered field through reflection
                java.lang.reflect.Field hoveredField = finalButton.getClass().getDeclaredField("hovered");
                hoveredField.setAccessible(true);
                hoveredField.setBoolean(finalButton, false);
                finalButton.repaint();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    });
    
    return button;
}

    private void generateReport(ActionEvent e) {
        ReportGenerator reporter = new ReportGenerator();
        reportArea.setText(reporter.generateReport());
    }

    private void exportReport(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        fileChooser.setApproveButtonText("Export");
        
        if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.write(reportArea.getText());
                JOptionPane.showMessageDialog(frame, "Report exported successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "Error exporting report: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void showWindow() {
        SwingUtilities.invokeLater(() -> {
            ReportGeneratorGUI gui = new ReportGeneratorGUI();
            gui.show();
        });
    }
}