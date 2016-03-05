package entity;
import java.util.Date;

/**
 * AppUsage records the details of an application and its usage details. 
 * <p>Each AppUsage object has a mac-address which corresponds to a user and their usage timestamp.
 * <p>The time stamp records the date and time which the app was used, in the format (YYYY/MM/DD HH:mm).
 */
public class AppUsage {

    private String macAddress;
    private int appId;
    private Date timeStamp;
    private String time;
    private String id;
    /**
     * Creates and instantiates an AppUsage object
     * @param timeStamp  the time stamp that the app was used
     * @param macAddress  the mac-address corresponding to a user
     * @param appId  the unique id of the app
     */
    public AppUsage(Date timeStamp, String macAddress, int appId) {
        this.macAddress = macAddress;
        this.appId = appId;
        this.timeStamp = timeStamp;
    }
    /**
     * Creates and instantiates an AppUsage object
     * <p>(Inputs recorded as String)
     * @param time  the time stamp that the app was used
     * @param macAddress  the mac-address corresponding to a user
     * @param id  the unique id of the app
     */
    public AppUsage(String time, String macAddress, String id) {
        this.time = time;
        this.macAddress = macAddress;
        this.id = id;
    }
    /**
     * Retrieves the time stamp
     * @return  returns the time (in String format)
     */
    public String getTime() {
        return time;
    }
    /**
     * Retrieves the id of the App
     * @return  returns the id (in String format)
     */
    public String getID() {
        return id;
    }
    /**
     * Retrieves the mac-address
     * @return  returns the mac-address
     */
    public String getMacAddress() {
        return macAddress;
    }
    /**
     * Retrieves the unique id of the application
     * @return  the id of the corresponding app 
     */
    public int getAppId() {
        return appId;
    }
    /**
     * Retrieves the time stamp at which the app was used
     * @return timestamp
     */
    public Date getTimeStamp() {
        return timeStamp;
    }
}
