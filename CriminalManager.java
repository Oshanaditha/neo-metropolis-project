import java.util.HashMap;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;

class CriminalRecord {
    String name;
    String crime;
    int severityLevel; 

    public CriminalRecord(String name, String crime, int severityLevel) {
        this.name = name;
        this.crime = crime;
        this.severityLevel = severityLevel;
    }
}

public class CriminalManager {
    private HashMap<String, List<CriminalRecord>> criminals = new HashMap<>();

    // Add a new  criminal record  to database
    public boolean addCriminal(CriminalRecord record) {
        if (!criminals.containsKey(record.name)) {
            criminals.put(record.name, new ArrayList<>());
        }
        criminals.get(record.name).add(record);
        return true;
    }

    // Get criminal details by name 
    public List<CriminalRecord> getCriminalsByName(String name) {
        return criminals.getOrDefault(name, new ArrayList<>());
    }

    // Remove  criminal record
    public boolean removeCriminal(CriminalRecord record) {
        if (!criminals.containsKey(record.name)) {
            return false;
        }
        boolean removed = criminals.get(record.name).remove(record);
        if (criminals.get(record.name).isEmpty()) {
            criminals.remove(record.name);
        }
        return removed;
    }

    // Get all criminals
    public Collection<CriminalRecord> getAllCriminals() {
        List<CriminalRecord> allRecords = new ArrayList<>();
        for (List<CriminalRecord> records : criminals.values()) {
            allRecords.addAll(records);
        }
        return allRecords;
    }
}