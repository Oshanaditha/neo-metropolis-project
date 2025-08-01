import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class CitizenManagerGUI {
    private static CitizenManager manager = new CitizenManager();

    public static void showWindow() {
        JFrame frame = new JFrame("Citizen Management");
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.getContentPane().setBackground(new Color(248, 249, 250));

        // Header panel with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(13, 71, 161), 0, getHeight(), new Color(21, 101, 192));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 60));
        JLabel title = new JLabel("CITIZEN MANAGEMENT SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(248, 249, 250));

        // Form Panel with card-like design
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        formPanel.setBackground(Color.WHITE);

        JTextField idField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JTextField ageField = createStyledTextField();
        JTextField nationalityField = createStyledTextField();
        JTextField dobField = createStyledTextField();

        formPanel.add(createFormLabel("ID:"));
        formPanel.add(idField);
        formPanel.add(createFormLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(createFormLabel("Age:"));
        formPanel.add(ageField);
        formPanel.add(createFormLabel("Nationality:"));
        formPanel.add(nationalityField);
        formPanel.add(createFormLabel("Date of Birth (YYYY-MM-DD):"));
        formPanel.add(dobField);

        // List Display Area with card-like design
        JTextArea listArea = new JTextArea();
        listArea.setEditable(false);
        listArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        listArea.setBackground(new Color(253, 253, 253));
        JScrollPane scrollPane = new JScrollPane(listArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)), 
                "Citizen List",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(66, 66, 66)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Button Panel with floating buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        
        JButton addBtn = createFloatingActionButton("Add Citizen", new Color(46, 125, 50));
        JButton updateBtn = createFloatingActionButton("Update", new Color(21, 101, 192));
        JButton deleteBtn = createFloatingActionButton("Delete", new Color(198, 40, 40));
        JButton displayBtn = createFloatingActionButton("Display", new Color(123, 31, 162));
        JButton listBtn = createFloatingActionButton("Refresh List", new Color(255, 160, 0));
        JButton clearBtn = createFloatingActionButton("Clear Form", new Color(120, 144, 156));
        JButton backBtn = createFloatingActionButton("Back", new Color(69, 90, 100));

        // Action Listeners
        addBtn.addActionListener(e -> {
            try {
                Citizen citizen = new Citizen(
                    idField.getText(),
                    nameField.getText(),
                    Integer.parseInt(ageField.getText()),
                    nationalityField.getText(),
                    dobField.getText()
                );
                if (manager.addCitizen(citizen)) {
                    JOptionPane.showMessageDialog(frame, "Citizen added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    clearForm(idField, nameField, ageField, nationalityField, dobField);
                    refreshList(listArea);
                }
            } catch (Exception ex) {
                showError(frame, "Invalid input: " + ex.getMessage());
            }
        });

        updateBtn.addActionListener(e -> {
            try {
                if (manager.updateCitizen(
                    idField.getText(),
                    nameField.getText(),
                    Integer.parseInt(ageField.getText()))
                ) {
                    JOptionPane.showMessageDialog(frame, "Citizen updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refreshList(listArea);
                }
            } catch (Exception ex) {
                showError(frame, "Error updating: " + ex.getMessage());
            }
        });

        deleteBtn.addActionListener(e -> {
            if (manager.deleteCitizen(idField.getText())) {
                JOptionPane.showMessageDialog(frame, "Citizen deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm(idField, nameField, ageField, nationalityField, dobField);
                refreshList(listArea);
            }
        });

        displayBtn.addActionListener(e -> {
            String id = idField.getText();
            if (!id.isEmpty()) {
                Citizen citizen = manager.getCitizen(id);
                if (citizen != null) {
                    idField.setText(citizen.getId());
                    nameField.setText(citizen.getName());
                    ageField.setText(String.valueOf(citizen.getAge()));
                    nationalityField.setText(citizen.getNationality());
                    dobField.setText(citizen.getDob());
                } else {
                    showError(frame, "Citizen not found!");
                }
            }
        });

        listBtn.addActionListener(e -> refreshList(listArea));
        clearBtn.addActionListener(e -> clearForm(idField, nameField, ageField, nationalityField, dobField));
        backBtn.addActionListener(e -> frame.dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(displayBtn);
        buttonPanel.add(listBtn);
        buttonPanel.add(clearBtn);
        buttonPanel.add(backBtn);

        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(headerPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        
        // Initial list refresh
        refreshList(listArea);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JButton createFloatingActionButton(String text, Color color) {
        JButton button = new JButton(text) {
            private boolean hovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (this.hovered) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Add shadow effect
                if (!getModel().isPressed()) {
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(0, 3, getWidth(), getHeight(), 25, 25);
                }
                
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD));
                
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (getWidth() - (int) r.getWidth()) / 2;
                int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();
                
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(140, 40);
            }
        };
        
button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
button.setBorder(BorderFactory.createEmptyBorder());
button.setContentAreaFilled(false);
button.setFocusPainted(false);
button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

// Create a final reference to access in the MouseAdapter
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

    private static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        field.setBackground(new Color(253, 253, 253));
        return field;
    }

    private static JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI Semibold", Font.PLAIN, 14));
        label.setForeground(new Color(66, 66, 66));
        return label;
    }

    private static void refreshList(JTextArea listArea) {
        List<Citizen> citizens = manager.getAllCitizens();
        StringBuilder sb = new StringBuilder();
        if (citizens.isEmpty()) {
            sb.append("No citizens in database");
        } else {
            sb.append(String.format("%-10s %-20s %-5s %-15s %-12s\n", 
                "ID", "Name", "Age", "Nationality", "DOB"));
            sb.append("------------------------------------------------------------\n");
            for (Citizen c : citizens) {
                sb.append(String.format("%-10s %-20s %-5d %-15s %-12s\n", 
                    c.getId(), c.getName(), c.getAge(), c.getNationality(), c.getDob()));
            }
        }
        listArea.setText(sb.toString());
    }

    private static void clearForm(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private static void showError(JFrame parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}