import java.util.*;
import java.util.stream.Collectors;

class EmergencyRequest implements Comparable<EmergencyRequest> {
    String requestId;
    String description;
    int priority;
    String status;
    Date createdTime;
    Date assignedTime;
    Date resolvedTime;
    String assignedAgent;

    public EmergencyRequest(String requestId, String description, int priority) {
        this.requestId = requestId;
        this.description = description;
        this.priority = priority;
        this.status = "Pending";
        this.createdTime = new Date();
        this.assignedTime = null;
        this.resolvedTime = null;
        this.assignedAgent = null;
    }

    @Override
    public int compareTo(EmergencyRequest other) {
        if (this.priority != other.priority) {
            return Integer.compare(this.priority, other.priority);
        }
        return this.createdTime.compareTo(other.createdTime);
    }
}

public class EmergencyHandler {
    private static EmergencyHandler instance;
    private PriorityQueue<EmergencyRequest> emergencyQueue = new PriorityQueue<>();
    private Map<String, EmergencyRequest> allRequests = new HashMap<>();
    private Map<String, Agent> agents = new HashMap<>();

    EmergencyHandler() {
        initializeSampleData();
    }

    public static EmergencyHandler getInstance() {
        if (instance == null) {
            instance = new EmergencyHandler();
        }
        return instance;
    }

    private void initializeSampleData() {
        registerAgent(new Agent("AG001"));
        registerAgent(new Agent("AG002"));
        registerAgent(new Agent("AG003"));
        
        addRequest(new EmergencyRequest("REQ001", "Fire outbreak", 1));
        addRequest(new EmergencyRequest("REQ002", "Medical emergency", 2));
        addRequest(new EmergencyRequest("REQ003", "Power outage", 3));
    }

    public void registerAgent(Agent agent) {
        agents.put(agent.getAgentId(), agent);
    }
    
    public Map<String, Agent> getAgents() {
        return agents;
    }
    
    public Agent getAgent(String agentId) {
        return agents.get(agentId);
    }

    public boolean addRequest(EmergencyRequest request) {
        if (request == null || request.requestId == null || request.description == null) {
            return false;
        }
        
        if (allRequests.containsKey(request.requestId)) {
            return false;
        }
        
        emergencyQueue.add(request);
        allRequests.put(request.requestId, request);
        return true;
    }

    public EmergencyRequest processNextRequest() {
        if (emergencyQueue.isEmpty()) {
            return null;
        }
        return emergencyQueue.peek();
    }

    public EmergencyRequest getRequest(String requestId) {
        return allRequests.get(requestId);
    }

    public boolean updateStatus(String requestId, String newStatus) {
        EmergencyRequest req = allRequests.get(requestId);
        if (req == null) {
            return false;
        }
        
        if (!Arrays.asList("Pending", "In Progress", "Resolved").contains(newStatus)) {
            return false;
        }
        
        if ("In Progress".equals(newStatus)) {
            req.assignedTime = new Date();
        } 
        else if ("Resolved".equals(newStatus)) {
            req.resolvedTime = new Date();
            if (req.assignedAgent != null) {
                Agent agent = agents.get(req.assignedAgent);
                if (agent != null) {
                    agent.setAvailable(true);
                }
            }
            req.status = newStatus;
            return true;
        }
        else if ("Pending".equals(newStatus)) {
            if (req.assignedAgent != null) {
                Agent agent = agents.get(req.assignedAgent);
                if (agent != null) {
                    agent.setAvailable(true);
                }
                req.assignedAgent = null;
            }
            req.assignedTime = null;
            req.resolvedTime = null;
            if (!emergencyQueue.contains(req)) {
                emergencyQueue.add(req);
            }
        }
        
        req.status = newStatus;
        return true;
    }

    public boolean assignAgent(String requestId, String agentId) {
        EmergencyRequest req = allRequests.get(requestId);
        Agent agent = agents.get(agentId);
        
        if (req == null || agent == null) {
            return false;
        }
        
        if (!agent.isAvailable()) {
            return false;
        }
        
        if (!"Pending".equals(req.status)) {
            return false;
        }
        
        req.assignedAgent = agentId;
        req.status = "In Progress";
        req.assignedTime = new Date();
        agent.setAvailable(false);
        emergencyQueue.remove(req);
        return true;
    }

    public List<EmergencyRequest> getAllRequests() {
        return new ArrayList<>(allRequests.values());
    }

    public List<EmergencyRequest> getPendingRequests() {
        return allRequests.values().stream()
                .filter(req -> "Pending".equals(req.status))
                .sorted()
                .collect(Collectors.toList());
    }
}