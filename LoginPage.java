import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginPage {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private EmergencyHandler handler;

    public LoginPage() {
        this.handler = new EmergencyHandler();
        initializeUI();
    }

private void initializeUI() {
    frame = new JFrame("Agent Login");
    frame.setSize(500, 500);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.getContentPane().setBackground(new Color(240, 245, 249));

    // Background panel with animated particles
    JPanel backgroundPanel = new JPanel(new BorderLayout()) {
        private java.util.List<Particle> particles = new java.util.ArrayList<>();
        
        {
            for (int i = 0; i < 100; i++) {
                particles.add(new Particle(getWidth(), getHeight()));
            }
            new Timer(30, e -> repaint()).start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw gradient background
            GradientPaint gp = new GradientPaint(0, 0, new Color(12, 83, 148), 
                          0, getHeight(), new Color(25, 118, 210));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Draw particles
            for (Particle p : particles) {
                p.update();
                p.draw(g2d);
            }
        }
        
        class Particle {
            float x, y, size, speed, alpha;
            Color color;
            
            Particle(int width, int height) {
                x = (float)(Math.random() * width);
                y = (float)(Math.random() * height);
                size = (float)(Math.random() * 4 + 1);
                speed = (float)(Math.random() * 1 + 0.5);
                alpha = (float)(Math.random() * 0.5 + 0.1);
                color = new Color(255, 255, 255, (int)(alpha * 255));
            }
            
            void update() {
                y -= speed;
                if (y < -10) {
                    y = getHeight() + 10;
                    x = (float)(Math.random() * getWidth());
                }
            }
            
            void draw(Graphics2D g2d) {
                g2d.setColor(color);
                g2d.fill(new java.awt.geom.Ellipse2D.Float(x, y, size, size));
            }
        }
    };

        // Login card panel
        JPanel loginPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw card with shadow
                g2d.setColor(new Color(255, 255, 255, 220));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Draw subtle border
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
        };
        loginPanel.setLayout(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        loginPanel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title label
        JLabel titleLabel = new JLabel("AGENT LOGIN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        // Username field
        JLabel userLabel = new JLabel("Agent ID:");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        userLabel.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(userLabel, gbc);
        
        usernameField = new JTextField(20);
        styleTextField(usernameField);
        gbc.gridx = 1;
        loginPanel.add(usernameField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passLabel.setForeground(new Color(60, 60, 60));
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passLabel, gbc);
        
        passwordField = new JPasswordField(20);
        styleTextField(passwordField);
        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        // Login button
JButton loginBtn = new JButton("Login") {
    private boolean hovered = false;
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw button
        if (hovered) {
            g2.setColor(new Color(25, 118, 210));
        } else {
            g2.setColor(new Color(12, 83, 148));
        }
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        
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
        return new Dimension(120, 40);
    }
};
        loginBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        loginBtn.setBorder(BorderFactory.createEmptyBorder());
        loginBtn.setContentAreaFilled(false);
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginBtn.addActionListener(this::performLogin);
        
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.repaint();
            }
        });
        
        passwordField.addActionListener(this::performLogin);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        loginPanel.add(loginBtn, gbc);

        // Center the login panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(loginPanel);

        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        frame.add(backgroundPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(30, 30, 40));
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel footerLabel = new JLabel("NeoMetropolis Emergency System v1.0", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(180, 180, 180));
        footerPanel.add(footerLabel);
        frame.add(footerPanel, BorderLayout.SOUTH);
    }

    private void styleTextField(JComponent field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(new Color(250, 250, 250));
    }

    private void performLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Simple authentication - accept any non-empty credentials
        frame.dispose();
        SwingUtilities.invokeLater(() -> {
            MainDashboard mainDashboard = new MainDashboard(handler);
            mainDashboard.showMainDashboard();
        });
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginPage loginPage = new LoginPage();
            loginPage.show();
        });
    }
}