package entity;
/**
 * Creates an Admin object. The Admin has access to system features such as Bootstrapping
 * and Uploading of additional data.
 * 
 */
public class Admin {
    private String name;
    private String password;
    /**This method creates and instantiates a valid Admin object.
     * 
     * @param name  The username of the system administrator. Default is set as "Admin"
     * @param password  The password of the system administrator. 
     */
    public Admin(String name, String password){
        this.name = name;
        this.password = password;
    }
    /**
     * Retuns the username of Admin.
     * @return username of the Admin
     */
    public String getAdminName(){
        return name;
        
    }
    /**
     * Returns the password of Admin
     * @return password of the Admin
     */
    public String getAdminPassword(){
        return password;
    }
    
    
}
