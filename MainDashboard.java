import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
// import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Rectangle2D;

public class MainDashboard {
    private JFrame frame;

    public MainDashboard(EmergencyHandler handler) {
    }

    public void showMainDashboard() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("NeoMetropolis Emergency System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 800);
            frame.setLayout(new BorderLayout());
            frame.getContentPane().setBackground(new Color(240, 245, 249));

            try {
                frame.setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
            } catch (Exception e) {
                System.out.println("Icon not found, using default");
            }

            // Glass panel for blur effect
            JPanel glassPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(new Color(255, 255, 255, 150));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            glassPanel.setOpaque(false);
            glassPanel.setVisible(false);
            frame.setGlassPane(glassPanel);

            // Header panel with animated gradient
            JPanel headerPanel = new JPanel() {
                private float gradientPos = 0;

                {
                    new Timer(50, e -> {
                        gradientPos += 0.01f;
                        if (gradientPos > 1)
                            gradientPos = 0;
                        repaint();
                    }).start();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Animated gradient
                    float x = gradientPos * getWidth();
                    GradientPaint gp = new GradientPaint(
                            x, 0, new Color(12, 83, 148),
                            x + getWidth() / 2, getHeight(), new Color(25, 118, 210),
                            true);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Add subtle noise texture
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                    for (int i = 0; i < getWidth(); i += 2) {
                        for (int j = 0; j < getHeight(); j += 2) {
                            int c = (int) (Math.random() * 255);
                            g2d.setColor(new Color(c, c, c));
                            g2d.fillRect(i, j, 1, 1);
                        }
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            };

            headerPanel.setLayout(new BorderLayout());
            headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
            headerPanel.setPreferredSize(new Dimension(frame.getWidth(), 100));

            JLabel header = new JLabel("CITY MANAGEMENT DASHBOARD", SwingConstants.CENTER);
            header.setFont(new Font("Segoe UI", Font.BOLD, 32));
            header.setForeground(Color.WHITE);
            header.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
            headerPanel.add(header, BorderLayout.CENTER);

            // Footer panel with subtle animation
            JPanel footerPanel = new JPanel() {
                private float glowPhase = 0;

                {
                    new Timer(50, e -> {
                        glowPhase += 0.05f;
                        repaint();
                    }).start();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(new Color(30, 30, 40));
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    float glowIntensity = (float) ((Math.sin(glowPhase) + 1) / 4 + 0.5f);
                    g2d.setColor(new Color(100, 200, 255, (int) (glowIntensity * 30)));
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            };

            footerPanel.setLayout(new BorderLayout());
            JLabel footer = new JLabel("Developed by Aditha Wickramasingha", SwingConstants.CENTER);
            footer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            footer.setForeground(new Color(200, 200, 200));
            footer.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            footerPanel.add(footer);
            footerPanel.setPreferredSize(new Dimension(frame.getWidth(), 40));

            // Main button panel with floating effect
            JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 40, 40)) {
                private float time = 0;

                {
                    new Timer(30, e -> {
                        time += 0.05f;
                        repaint();
                    }).start();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Draw grid lines
                    g2d.setColor(new Color(230, 235, 240));
                    Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                            0, new float[] { 5f, 5f }, 0);
                    g2d.setStroke(dashed);

                    for (int x = 0; x < getWidth(); x += 50) {
                        g2d.drawLine(x, 0, x, getHeight());
                    }
                    for (int y = 0; y < getHeight(); y += 50) {
                        g2d.drawLine(0, y, getWidth(), y);
                    }

                    // Draw floating circles
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                    for (int i = 0; i < 4; i++) {
                        float x = (float) (Math.sin(time + i) * 50 + getWidth() / 2);
                        float y = (float) (Math.cos(time + i) * 50 + getHeight() / 2);
                        float size = (float) (Math.sin(time + i) * 20 + 100);

                        Color[] colors = {
                                new Color(12, 83, 148, 50),
                                new Color(25, 118, 210, 50),
                                new Color(100, 200, 255, 50),
                                new Color(200, 230, 255, 50)
                        };

                        g2d.setColor(colors[i]);
                        g2d.fill(new Ellipse2D.Float(x - size / 2, y - size / 2, size, size));
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                }
            };

            buttonPanel.setBorder(BorderFactory.createEmptyBorder(80, 100, 80, 100));
            buttonPanel.setBackground(new Color(240, 245, 249));

            // Create and add buttons
            JButton citizenBtn = createHolographicButton("Citizen Manager", new Color(25, 118, 210));
            JButton criminalBtn = createHolographicButton("Criminal Manager", new Color(220, 53, 69));
            JButton emergencyBtn = createHolographicButton("Emergency Handler", new Color(255, 193, 7));
            JButton reportBtn = createHolographicButton("Reports", new Color(25, 135, 84));

            // Add button actions
            citizenBtn.addActionListener(e -> CitizenManagerGUI.showWindow());
            criminalBtn.addActionListener(e -> CriminalManagerGUI.showWindow());
            emergencyBtn.addActionListener(e -> EmergencyHandlerGUI.showWindow());
            reportBtn.addActionListener(e -> ReportGeneratorGUI.showWindow());

            buttonPanel.add(citizenBtn);
            buttonPanel.add(criminalBtn);
            buttonPanel.add(emergencyBtn);
            buttonPanel.add(reportBtn);

            frame.add(headerPanel, BorderLayout.NORTH);
            frame.add(buttonPanel, BorderLayout.CENTER);
            frame.add(footerPanel, BorderLayout.SOUTH);

            // Fade-in effect
            setupFadeInEffect();

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private JButton createHolographicButton(String text, Color baseColor) {
        JButton button = new JButton(text) {
            private boolean hovered = false;
            private float glow = 0;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Update glow effect
                if (hovered) {
                    glow = Math.min(1, glow + 0.1f);
                } else {
                    glow = Math.max(0, glow - 0.1f);
                }

                // Draw outer glow
                if (glow > 0) {
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
                            (int) (100 * glow)));
                    g2.fillRoundRect(-10, -10, width + 20, height + 20, 30, 30);
                }

                // Draw button base
                Color topColor = baseColor.brighter();
                Color bottomColor = baseColor.darker();

                GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, width, height, 20, 20);

                // Draw holographic effect
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                for (int i = 0; i < 5; i++) {
                    float y = (float) (Math.sin(System.currentTimeMillis() / 500.0 + i) * 10 + height / 2);
                    g2.setColor(new Color(255, 255, 255, 100 - i * 20));
                    g2.fillRoundRect(5, (int) y - 1, width - 10, 2, 2, 2);
                }
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

                // Draw border
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, width - 1, height - 1, 20, 20);

                // Draw text
                FontMetrics fm = g2.getFontMetrics();
                Rectangle2D r = fm.getStringBounds(getText(), g2);
                int x = (width - (int) r.getWidth()) / 2;
                int y = (height - (int) r.getHeight()) / 2 + fm.getAscent();

                // Text shadow
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawString(getText(), x + 2, y + 2);

                // Main text
                g2.setColor(Color.WHITE);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                return new Dimension(Math.max(250, size.width + 40), size.height + 30);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 20));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Create a final reference to the button for the MouseAdapter
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

    private void setupFadeInEffect() {
        JPanel fadePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        fadePanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        fadePanel.setLayout(null);
        frame.setGlassPane(fadePanel);
        fadePanel.setVisible(true);

        new Timer(20, new ActionListener() {
            private int alpha = 255;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (alpha > 0) {
                    alpha -= 5;
                    if (alpha < 0)
                        alpha = 0;
                    fadePanel.setBackground(new Color(0, 0, 0, alpha));
                    fadePanel.repaint();
                } else {
                    ((Timer) e.getSource()).stop();
                    fadePanel.setVisible(false);
                }
            }
        }).start();
    }
}
