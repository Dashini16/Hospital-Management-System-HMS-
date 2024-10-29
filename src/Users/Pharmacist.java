public class Pharmacist extends User {
    private int age;
    public Pharmacist(String userID, String name, String gender,int age,String password) {
        super(userID, name, Role.PHARMACIST, gender,password);
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {   
        this.age = age;
    }

    @Override
    public void displayMenu() {
        System.out.println("Displaying pharmacist menu.");
    }

    @Override
    public void toStrings() {
        System.out.println("Staff ID: " + super.getUserID() + ", Name: " + super.getName() + ", Role: " + super.getRole() + ", Gender: " + super.getGender() + ", Age: " + age);
    }
}
