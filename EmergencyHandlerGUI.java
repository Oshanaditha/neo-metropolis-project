import javax.swing.*;

public class EmergencyHandlerGUI {
    public static void showWindow() {
        SwingUtilities.invokeLater(() -> {
            EmergencyDashboard dashboard = new EmergencyDashboard();
            dashboard.show();
        });
    }
}