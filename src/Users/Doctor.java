public class Doctor extends User {
    private int age;

    public Doctor(String userID, String name, String gender,int age) {
        super(userID, name, Role.DOCTOR, gender);
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
        System.out.println("Displaying doctor menu.");
    }

    @Override
    public void toStrings() {
        System.out.println("Staff ID: " + super.getUserID() + ", Name: " + super.getName() + ", Role: " + super.getRole() + ", Gender: " + super.getGender() + ", Age: " + age);
    }


}
