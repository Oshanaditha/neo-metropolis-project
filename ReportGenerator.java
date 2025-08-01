import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat hourFormat = new SimpleDateFormat("HH");
    private SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String generateReport() {
        EmergencyHandler handler = EmergencyHandler.getInstance();
        List<EmergencyRequest> requests = handler.getAllRequests();
        
        if (requests.isEmpty()) {
            return "No emergency requests found for reporting.";
        }

        // Get today's date for filtering
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        // Filter today's requests
        List<EmergencyRequest> todaysRequests = requests.stream()
            .filter(req -> req.createdTime.after(today.getTime()) && 
                          req.createdTime.before(tomorrow.getTime()))
            .collect(Collectors.toList());

        StringBuilder report = new StringBuilder();
        report.append("=== EMERGENCY SYSTEM REPORT ===\n\n");
        report.append("Report Date: ").append(dayFormat.format(new Date())).append("\n\n");
        
        // 1. Summary Statistics
        report.append("=== SUMMARY STATISTICS ===\n");
        report.append(generateSummary(requests));
        
        // 2. Priority Analysis
        report.append("\n=== PRIORITY ANALYSIS ===\n");
        report.append(generatePriorityAnalysis(requests));
        
        // 3. Status Analysis
        report.append("\n=== STATUS ANALYSIS ===\n");
        report.append(generateStatusAnalysis(requests));
        
        // 4. Today's Busiest Hours
        report.append("\n=== TODAY'S BUSIEST HOURS ===\n");
        if (todaysRequests.isEmpty()) {
            report.append("No requests today.\n");
        } else {
            report.append(generateBusiestHours(todaysRequests));
        }
        
        // 5. Agent Performance
        report.append("\n=== AGENT PERFORMANCE ===\n");
        report.append(generateAgentPerformance(requests));
        
        // 6. Detailed Request Log
        report.append("\n=== DETAILED REQUEST LOG ===\n");
        report.append(generateRequestLog(requests));
        
        return report.toString();
    }

    private String generateBusiestHours(List<EmergencyRequest> todaysRequests) {
        Map<Integer, Long> hourCounts = todaysRequests.stream()
            .collect(Collectors.groupingBy(
                r -> Integer.parseInt(hourFormat.format(r.createdTime)),
                Collectors.counting()
            ));
        
        StringBuilder sb = new StringBuilder();
        sb.append("Hour | Count | Percentage\n");
        sb.append("-------------------------\n");
        
        hourCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(5)
            .forEach(e -> {
                sb.append(String.format("%4d | %5d | %8.1f%%\n",
                    e.getKey(), 
                    e.getValue(), 
                    percentage(e.getValue(), todaysRequests.size())));
            });
        
        return sb.toString();
    }

    private String generateSummary(List<EmergencyRequest> requests) {
        long total = requests.size();
        long resolved = requests.stream().filter(r -> "Resolved".equals(r.status)).count();
        long pending = requests.stream().filter(r -> "Pending".equals(r.status)).count();
        long inProgress = requests.stream().filter(r -> "In Progress".equals(r.status)).count();
        
        return String.format(
            "Total Requests: %d\n" +
            "Resolved: %d (%.1f%%)\n" + 
            "Pending: %d (%.1f%%)\n" +
            "In Progress: %d (%.1f%%)\n",
            total,
            resolved, percentage(resolved, total),
            pending, percentage(pending, total),
            inProgress, percentage(inProgress, total)
        );
    }

    private String generatePriorityAnalysis(List<EmergencyRequest> requests) {
        Map<Integer, Long> counts = requests.stream()
            .collect(Collectors.groupingBy(r -> r.priority, Collectors.counting()));
        
        StringBuilder sb = new StringBuilder();
        sb.append("Priority | Count | Percentage\n");
        sb.append("----------------------------\n");
        
        counts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> {
                sb.append(String.format("%8d | %5d | %8.1f%%\n",
                    e.getKey(), e.getValue(), percentage(e.getValue(), requests.size())));
            });
        
        return sb.toString();
    }

    private String generateStatusAnalysis(List<EmergencyRequest> requests) {
        Map<String, Long> counts = requests.stream()
            .collect(Collectors.groupingBy(r -> r.status, Collectors.counting()));
        
        StringBuilder sb = new StringBuilder();
        sb.append("Status      | Count | Percentage\n");
        sb.append("-------------------------------\n");
        
        counts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> {
                sb.append(String.format("%-11s | %5d | %8.1f%%\n",
                    e.getKey(), e.getValue(), percentage(e.getValue(), requests.size())));
            });
        
        return sb.toString();
    }

    private String generateAgentPerformance(List<EmergencyRequest> requests) {
        Map<String, Long> agentCounts = requests.stream()
            .filter(r -> r.assignedAgent != null)
            .collect(Collectors.groupingBy(r -> r.assignedAgent, Collectors.counting()));
        
        StringBuilder sb = new StringBuilder();
        sb.append("Agent  | Requests | Percentage\n");
        sb.append("----------------------------\n");
        
        agentCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .forEach(e -> {
                sb.append(String.format("%-6s | %8d | %8.1f%%\n",
                    e.getKey(), e.getValue(), percentage(e.getValue(), requests.size())));
            });
        
        return sb.toString();
    }

    private String generateRequestLog(List<EmergencyRequest> requests) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID     | Description           | Priority | Created At          | Status      | Agent\n");
        sb.append("------------------------------------------------------------------------------------\n");
        
        requests.stream()
            .sorted(Comparator.comparing(r -> r.createdTime))
            .forEach(r -> {
                sb.append(String.format("%-6s | %-20s | %8d | %19s | %-11s | %s\n",
                    r.requestId,
                    truncate(r.description, 20),
                    r.priority,
                    dateFormat.format(r.createdTime),
                    r.status,
                    r.assignedAgent != null ? r.assignedAgent : "N/A"));
            });
        
        return sb.toString();
    }

    private double percentage(long part, long total) {
        return total > 0 ? (part * 100.0 / total) : 0;
    }

    private String truncate(String str, int length) {
        return str.length() > length ? str.substring(0, length-3) + "..." : str;
    }
}