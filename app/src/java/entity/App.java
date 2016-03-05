package entity;
/**
 * An App object stores the details of an application as listed in the system. 
 * Each App object stores the the application id, application name and application category.
 */
public class App {
    
    private String appid;
    private String appname;
    private String appcategory;
    /**
     * Creates and instantiates an App object which stores an application's id, name and category.
     * @param appid  the unique id of an App
     * @param appname  the name of an App 
     * @param appcategory  the category of an App 
     */
    public App(String appid, String appname, String appcategory){
        this.appid = appid;
        this.appname = appname;
        this.appcategory = appcategory;
        
    }
    /**
     * Returns the unique ID of the application
     * @return the unique id
     */
    public String getAppId(){
        return appid;
        
    }
    /**
     * Returns the application name
     * @return  the name of the app
     */
    public String getAppName(){
        return appname;
    }
    /**
     * Returns the application's category
     * @return  the category of the app
     */
    public String getAppCategory(){
        return appcategory;
    }
    
}
