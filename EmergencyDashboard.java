import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class EmergencyDashboard {
    private EmergencyHandler handler;
    private JFrame frame;
    private JTable requestsTable;
    private EmergencyTableModel tableModel;

    public EmergencyDashboard() {
        this.handler = EmergencyHandler.getInstance();
        initializeUI();
    }

    private void initializeUI() {
        frame = new JFrame("Emergency Dashboard");
        frame.setSize(1200, 800);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new EmergencyTableModel();
        requestsTable = new JTable(tableModel);
        requestsTable.setRowHeight(30);
        requestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        requestsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(requestsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton refreshBtn = new JButton("Refresh");
        JButton processBtn = new JButton("Show Next");
        JButton assignBtn = new JButton("Assign Agent");
        JButton updateStatusBtn = new JButton("Update Status");
        JButton addBtn = new JButton("Add Request");
        JButton addAgentBtn = new JButton("Add Agent");
        JButton backBtn = new JButton("Back");

        for (JButton btn : new JButton[]{refreshBtn, processBtn, assignBtn, updateStatusBtn, addBtn, addAgentBtn, backBtn}) {
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setFocusPainted(false);
        }

        refreshBtn.addActionListener(this::refreshTable);
        processBtn.addActionListener(this::showNextRequest);
        assignBtn.addActionListener(this::assignAgent);
        updateStatusBtn.addActionListener(this::updateStatus);
        addBtn.addActionListener(this::addNewRequest);
        addAgentBtn.addActionListener(this::addNewAgent);
        backBtn.addActionListener(e -> frame.dispose());

        controlPanel.add(refreshBtn);
        controlPanel.add(processBtn);
        controlPanel.add(assignBtn);
        controlPanel.add(updateStatusBtn);
        controlPanel.add(addBtn);
        controlPanel.add(addAgentBtn);
        controlPanel.add(backBtn);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);

        refreshTable(null);
    }

    private void refreshTable(ActionEvent e) {
        tableModel.setRequests(handler.getAllRequests());
    }

    private void showNextRequest(ActionEvent e) {
        EmergencyRequest next = handler.processNextRequest();
        if (next != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String createdTime = sdf.format(next.createdTime);
            
            JOptionPane.showMessageDialog(frame, 
                "Next highest priority request:\n" +
                "ID: " + next.requestId + "\n" +
                "Description: " + next.description + "\n" +
                "Priority: " + next.priority + "\n" +
                "Created: " + createdTime,
                "Next Request", 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(frame, 
                "No pending requests available.", 
                "Information", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void assignAgent(ActionEvent e) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a request first.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String requestId = (String) tableModel.getValueAt(selectedRow, 0);
        EmergencyRequest request = handler.getRequest(requestId);
        
        if (!"Pending".equals(request.status)) {
            JOptionPane.showMessageDialog(frame, 
                "Only pending requests can be assigned.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Agent> availableAgents = handler.getAgents().values().stream()
            .filter(Agent::isAvailable)
            .collect(Collectors.toList());
        
        if (availableAgents.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                "No available agents.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JComboBox<Agent> agentCombo = new JComboBox<>(availableAgents.toArray(new Agent[0]));
        agentCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Agent) {
                    setText(value.toString());
                }
                return this;
            }
        });
        
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select Agent:"));
        panel.add(agentCombo);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, 
            "Assign Agent", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            Agent selectedAgent = (Agent) agentCombo.getSelectedItem();
            if (handler.assignAgent(requestId, selectedAgent.getAgentId())) {
                JOptionPane.showMessageDialog(frame, "Agent assigned successfully!");
                refreshTable(null);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Failed to assign agent.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStatus(ActionEvent e) {
        int selectedRow = requestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, 
                "Please select a request first.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String requestId = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);
        
        String newStatus = (String) JOptionPane.showInputDialog(frame, 
            "Select new status:", 
            "Update Status", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            new String[]{"Pending", "In Progress", "Resolved"}, 
            currentStatus);

        if (newStatus != null && !newStatus.equals(currentStatus)) {
            if (handler.updateStatus(requestId, newStatus)) {
                JOptionPane.showMessageDialog(frame, "Status updated successfully!");
                refreshTable(null);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Failed to update status.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addNewRequest(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        JTextField idField = new JTextField();
        JTextField descField = new JTextField();
        JComboBox<String> priorityCombo = new JComboBox<>(new String[]{"High (1)", "Medium (2)", "Low (3)"});
        
        panel.add(new JLabel("Request ID:"));
        panel.add(idField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Priority:"));
        panel.add(priorityCombo);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Request", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String requestId = idField.getText().trim();
            String description = descField.getText().trim();
            int priority = priorityCombo.getSelectedIndex() + 1;
            
            if (requestId.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Request ID and Description cannot be empty feild.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            EmergencyRequest request = new EmergencyRequest(requestId, description, priority);
            if (handler.addRequest(request)) {
                JOptionPane.showMessageDialog(frame, "Request added successfully!");
                refreshTable(null);
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Failed to add request. ID may already exist.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void addNewAgent(ActionEvent e) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        
        JTextField idField = new JTextField();
        
        panel.add(new JLabel("Agent ID:"));
        panel.add(idField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Add New Agent", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String agentId = idField.getText().trim();
            
            if (agentId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, 
                    "Agent ID cannot be empty.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (handler.getAgent(agentId) != null) {
                JOptionPane.showMessageDialog(frame, 
                    "This Agent ID already exists in system.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Agent agent = new Agent(agentId);
            handler.registerAgent(agent);
            JOptionPane.showMessageDialog(frame, "Agent added successfully!");
        }
    }

    public void show() {
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

class EmergencyTableModel extends AbstractTableModel {
    private List<EmergencyRequest> requests = new ArrayList<>();
    private String[] columnNames = {"Request ID", "Description", "Priority", "Created Time", 
                                  "Status", "Assigned Agent", "Assigned Time", "Resolved Time"};
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void setRequests(List<EmergencyRequest> requests) {
        this.requests = requests;
        fireTableDataChanged();
    }

    @Override
    public int getRowCount() {
        return requests.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EmergencyRequest req = requests.get(rowIndex);
        switch (columnIndex) {
            case 0: return req.requestId;
            case 1: return req.description;
            case 2: return req.priority;
            case 3: return sdf.format(req.createdTime);
            case 4: return req.status;
            case 5: return req.assignedAgent != null ? req.assignedAgent : "Unassigned";
            case 6: return req.assignedTime != null ? sdf.format(req.assignedTime) : "";
            case 7: return req.resolvedTime != null ? sdf.format(req.resolvedTime) : "";
            default: return null;
        }
    }
}