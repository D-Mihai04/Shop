package Model;

public class Client {
    private int id;
    private int age;
    private String email;
    private String first_name;
    private String last_name;

    public Client(){}

    public Client(int id, String firstName, String lastName, String email, int age) {
        this.id = id;
        this.age = age;
        this.email = email;
        this.first_name = firstName;
        this.last_name = lastName;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String firstName) {
        this.first_name = firstName;
    }

    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String lastName) {
        this.last_name = lastName;
    }

    @Override
    public String toString() {
        return first_name + " " + last_name;
    }
}
