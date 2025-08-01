import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class CitizenManager {
    private HashMap<String, Citizen> citizens = new HashMap<>();

    // Add this method to get a single citizen
    public Citizen getCitizen(String id) {
        return citizens.get(id);
    }

    // Add this method to get all citizens
    public List<Citizen> getAllCitizens() {
        return new ArrayList<>(citizens.values());
    }

    public boolean addCitizen(Citizen citizen) {
        if (citizens.containsKey(citizen.getId())) {
            System.err.println("Error: Citizen ID not found " + citizen.getId() + " This is already exists.");
            return false;
        }
        citizens.put(citizen.getId(), citizen);
        return true;
    }

    public boolean updateCitizen(String id, String newName, int newAge) { 
        if (!citizens.containsKey(id)) {
            System.err.println("Error: Citizen ID not found.");
            return false;
        }
        Citizen citizen = citizens.get(id);
        citizen.setName(newName);
        citizen.setAge(newAge);
        return true;
    }

    public boolean deleteCitizen(String id) {
        if (!citizens.containsKey(id)) {
            System.err.println("Error: Citizen ID not found.");
            return false;
        }
        citizens.remove(id);
        return true;
    }

    public void displayCitizen(String id) {
        Citizen citizen = citizens.get(id);
        if (citizen == null) {
            System.err.println("Error: Citizen not found.");
        } else {
            System.out.println("Citizen Details:");
            System.out.println("ID: " + citizen.getId());
            System.out.println("Name: " + citizen.getName());
            System.out.println("Age: " + citizen.getAge());
            System.out.println("Nationality: " + citizen.getNationality());
            System.out.println("DOB: " + citizen.getDob());
        }
    }
}