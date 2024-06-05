package first_project;

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

        /*
         *
         * If user is admin, admin can:
         * 1. see total bank money
         * 2. see all users and their accounts
         * 3. find accountID
         * 4. close another account
         * 5. take money from account to account
         * 6. see his own account info
         */


}
