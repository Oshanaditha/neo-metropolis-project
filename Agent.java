public class Agent {
    private String agentId;
    private boolean isAvailable;
    
    public Agent(String agentId) {
        this.agentId = agentId;
        this.isAvailable = true;
    }
    
    // Getters and setters
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    public String getAgentId() { return agentId; }
    
    @Override
    public String toString() {
        return agentId + (isAvailable ? " (Available)" : " (Busy)");
    }
}