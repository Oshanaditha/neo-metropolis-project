import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class CriminalManagerGUI {
    private static CriminalManager manager = new CriminalManager();
    private static JTextArea displayArea;
    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> showWindow());
    }

    public static void showWindow() {
        frame = new JFrame("Criminal Management System");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 750);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(new Color(18, 18, 18));

        // Dark header panel
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(30, 30, 30));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Add subtle red accent
                g2d.setColor(new Color(200, 0, 0, 30));
                g2d.fillRect(0, getHeight()-2, getWidth(), 2);
            }
        };
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 70));
        JLabel title = new JLabel("CRIMINAL RECORDS DATABASE", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(new Color(220, 53, 69));
        headerPanel.add(title, BorderLayout.CENTER);

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        contentPanel.setBackground(new Color(18, 18, 18));

        // Form Panel with dark theme
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(new Color(30, 30, 30));

        JTextField nameField = createDarkTextField();
        JTextField crimeField = createDarkTextField();
        JTextField severityField = createDarkTextField();

        formPanel.add(createDarkLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createDarkLabel("Crime:"));
        formPanel.add(crimeField);
        formPanel.add(createDarkLabel("Severity (1-10):"));
        formPanel.add(severityField);

        // Display Panel with dark theme
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        displayPanel.setBackground(new Color(30, 30, 30));

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        displayArea.setForeground(new Color(200, 200, 200));
        displayArea.setBackground(new Color(40, 40, 40));
        displayArea.setCaretColor(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(null);
        displayPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel with glowing buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(18, 18, 18));
        
        JButton addBtn = createGlowingButton("Add Record", new Color(220, 53, 69));
        JButton searchBtn = createGlowingButton("Search", new Color(13, 110, 253));
        JButton deleteBtn = createGlowingButton("Delete", new Color(108, 117, 125));
        JButton viewAllBtn = createGlowingButton("View All", new Color(25, 135, 84));
        JButton clearBtn = createGlowingButton("Clear", new Color(255, 193, 7));

        // Action Listeners (unchanged from original)
        addBtn.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String crime = crimeField.getText().trim();
                String severityText = severityField.getText().trim();

                if (name.isEmpty() || crime.isEmpty() || severityText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int severity = Integer.parseInt(severityText);
                if (severity < 1 || severity > 10) {
                    JOptionPane.showMessageDialog(frame, "Severity must be between 1-10", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                CriminalRecord record = new CriminalRecord(name, crime, severity);
                if (manager.addCriminal(record)) {
                    JOptionPane.showMessageDialog(frame, "Record added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    updateDisplay();
                    clearFields(nameField, crimeField, severityField);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Severity must be a number", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name to search", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<CriminalRecord> records = manager.getCriminalsByName(name);
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "No criminals found with that name", 
                    "Not Found", 
                    JOptionPane.WARNING_MESSAGE);
            } else {
                StringBuilder message = new StringBuilder("<html><pre>");
                for (CriminalRecord record : records) {
                    message.append(String.format("Name:      %s<br>Crime:     %s<br>Severity:  %d<br><br>", 
                        record.name, record.crime, record.severityLevel));
                }
                message.append("</pre></html>");
                
                JOptionPane.showMessageDialog(frame, 
                    message.toString(),
                    "Criminal Records", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a name to delete", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<CriminalRecord> records = manager.getCriminalsByName(name);
            if (records.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No criminals found with that name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (records.size() == 1) {
                int confirm = JOptionPane.showConfirmDialog(frame, 
                    "Are you sure you want to delete this record?\n" +
                    "Name: " + records.get(0).name + "\n" +
                    "Crime: " + records.get(0).crime + "\n" +
                    "Severity: " + records.get(0).severityLevel,
                    "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    if (manager.removeCriminal(records.get(0))) {
                        JOptionPane.showMessageDialog(frame, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateDisplay();
                        clearFields(nameField, crimeField, severityField);
                    }
                }
            } else {
                String[] options = new String[records.size()];
                for (int i = 0; i < records.size(); i++) {
                    options[i] = "Crime: " + records.get(i).crime + " | Severity: " + records.get(i).severityLevel;
                }
                
                String selected = (String) JOptionPane.showInputDialog(frame,
                    "Multiple records found. Select one to delete:",
                    "Select Record to Delete",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
                
                if (selected != null) {
                    for (int i = 0; i < options.length; i++) {
                        if (options[i].equals(selected)) {
                            int confirm = JOptionPane.showConfirmDialog(frame, 
                                "Are you sure you want to delete this record?\n" +
                                "Name: " + records.get(i).name + "\n" +
                                "Crime: " + records.get(i).crime + "\n" +
                                "Severity: " + records.get(i).severityLevel,
                                "Confirm Delete", 
                                JOptionPane.YES_NO_OPTION);
                            
                            if (confirm == JOptionPane.YES_OPTION) {
                                if (manager.removeCriminal(records.get(i))) {
                                    JOptionPane.showMessageDialog(frame, "Record deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    updateDisplay();
                                    clearFields(nameField, crimeField, severityField);
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });

        viewAllBtn.addActionListener(e -> updateDisplay());
        clearBtn.addActionListener(e -> {
            clearFields(nameField, crimeField, severityField);
            displayArea.setText("");
        });

        buttonPanel.add(addBtn);
        buttonPanel.add(searchBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewAllBtn);
        buttonPanel.add(clearBtn);

        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(displayPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(contentPanel, BorderLayout.CENTER);
        
        updateDisplay();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createGlowingButton(String text, Color color) {
    JButton button = new JButton(text) {
        private boolean hovered = false;
        private float glow = 0f;
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw glow effect
            if (hovered) {
                glow = Math.min(1f, glow + 0.1f);
            } else {
                glow = Math.max(0f, glow - 0.1f);
            }
            
            if (glow > 0) {
                g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(50 * glow)));
                g2.fillRoundRect(-5, -5, getWidth()+10, getHeight()+10, 25, 25);
            }
            
            // Draw button
            if (getModel().isPressed()) {
                g2.setColor(color.darker());
            } else {
                g2.setColor(color);
            }
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            
            // Draw text
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
            return new Dimension(150, 40);
        }
    };
    
button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
button.setBorder(BorderFactory.createEmptyBorder());
button.setContentAreaFilled(false);
button.setFocusPainted(false);
button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

// Create a final reference to the button for use in the listener
final JButton finalButton = button;

button.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseEntered(java.awt.event.MouseEvent evt) {
        try {
            // Access the hovered field through reflection
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
            // Access the hovered field through reflection
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

    private static JTextField createDarkTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(60, 60, 60));
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private static JLabel createDarkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        label.setForeground(new Color(200, 200, 200));
        return label;
    }

    private static void updateDisplay() {
        try {
            Collection<CriminalRecord> records = manager.getAllCriminals();
            displayArea.setText("");
            
            if (records.isEmpty()) {
                displayArea.setText("No criminal records found.");
                return;
            }
            
            displayArea.append(String.format("%-20s %-25s %s\n", "Name", "Crime", "Severity"));
            displayArea.append("--------------------------------------------------\n");
            
            for (CriminalRecord record : records) {
                displayArea.append(String.format("%-20s %-25s %d\n", 
                    record.name, record.crime, record.severityLevel));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error displaying records: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void clearFields(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }
}