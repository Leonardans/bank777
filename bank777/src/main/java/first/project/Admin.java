package first.project;

public class Admin {
    private int userID;
    private String password;

    
    public Admin(int userID, String password) {
        this.userID = userID;
        this.password = password;
        System.out.println("Admin " + userID + " was added.");
    }
    
    public int getUserID() {
        return userID;
    }
   
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin [userID=" + userID + "]";
    }

}
