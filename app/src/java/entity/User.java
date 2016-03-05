package entity;

/**
 * User represents a user of the system, and stores all the details of a
 * user/student.
 * <p>
 * Details include the user's mac-address (unique), full name, email, gender,
 * cca and password.
 */
public class User {

    private String macAddress;
    private String fullName;
    private String email;
    private String password;
    private String gender;
    private String cca;

    /**
     * Creates and instantiates a User
     *
     * @param macAddress the unique mac-address
     * @param fullName the user's full name
     * @param password the user's password
     * @param email the user's email
     * @param gender the user's gender (M/F)
     * @param cca the user's cca
     */
    public User(String macAddress, String fullName, String password, String email, String gender, String cca) {
        this.macAddress = macAddress;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.gender = gender;
        this.cca = cca;
    }

    /**
     * Retrieves the user's unique mac-address
     *
     * @return the mac-address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Retrieves the user's email
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Retrieves the user's password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the user's full name
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Retrieves the user's gender
     *
     * @return the gender
     */
    public String getGender() {
        return gender;
    }
    /**
     * Retrieves the user's cca
     * @return the cca
     */
    public String getCCA() {
        return cca;
    }
}
