public class Citizen {
    private String id;
    private String name;
    private int age;
    private String nationality;
    private String dob;

    public Citizen(String id, String name, int age, String nationality, String dob) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.nationality = nationality;
        this.dob = dob;
    }

    // Add getter methods
    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getNationality() { return nationality; }
    public String getDob() { return dob; }

    // Add setter methods
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
}