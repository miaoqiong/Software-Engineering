package entity;

import java.util.ArrayList;
import java.util.Date;
import utilities.DateFormatter;
/**
 * DiurnalAppUsage records the details of diurnal pattern report usage details. 
 * 
 */
public class DiurnalAppUsage {

    private String macAddress;
    private ArrayList<String> timeStamps;
    private String year;
    private String gender;
    private String school;
    
    /**
     * Construct a diurnalAppUsage object with attribute set to given parameter
     * @param macAddress mac-address of student
     * @param timeStamps ArrayList of timestamps
     */
    public DiurnalAppUsage(String macAddress, ArrayList<String> timeStamps) {
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
    }
    
    /**
     * Construct a diurnalAppUsage object with attribute set to given parameter
     * @param macAddress mac-address of student
     * @param timeStamps ArrayList of tiemstamps
     * @param year  matriculated year of student
     * @param gender gender of student
     * @param school school that student belongs to   
     */
    public DiurnalAppUsage(String macAddress, ArrayList<String> timeStamps, String year, String gender, String school) {
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.year = year;
        this.gender = gender;
        this.school = school;
    }
    
    /**
     * Get mac-address
     * @return macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }
    
    /**
     * Get timestamps
     * @return ArrayList of timestamps
     */
    public ArrayList<String> getTimeStamps() {
        return timeStamps;
    }
    /**
     * Get the matriculated year
     * @return year
     */
    public String getYear() {
        return year;
    }
    
    /**
     * Get gender
     * @return gender
     */
    public String getGender() {
        return gender;
    }
    
    /**
     * Get school
     * @return school
     */
    public String getSchool() {
        return school;
    }
    
    /**
     * Add new timestamp into timestamps
     * @param timeStamp new timestamp
     */
    public void addTimeStamp(String timeStamp) {
        timeStamps.add(timeStamp);

    }
    
    /**
     * Calculate total usage time
     * @param timeStamps ArrayList of timestamps
     * @param endDate end Date that specified
     * @return total usage time
     */
    public long getTotalUsageTime(ArrayList<String> timeStamps, String endDate) {
        long sum = 0;
        if (timeStamps.size() == 1) {

            Date time = DateFormatter.stringToDate(timeStamps.get(0));
            long difference = DateFormatter.stringToDate(endDate).getTime() - time.getTime();
            difference = (difference / 1000);
            if (difference >= 120) {
                sum += 10;
            } else {
                sum += difference;
            }

        } else {

            for (int i = 0; i < timeStamps.size(); i++) {
                if (i + 1 != timeStamps.size()) {
                    Date firstTime = DateFormatter.stringToDate(timeStamps.get(i));
                    Date nextTime = DateFormatter.stringToDate(timeStamps.get(i + 1));

                    long difference = nextTime.getTime() - firstTime.getTime();
                    difference = difference / 1000;

                    if (difference > 120) {
                        sum += 10;
                    } else {
                        sum += difference;
                    }
                }
                if (i + 1 == timeStamps.size()) {
                    Date firstTime = DateFormatter.stringToDate(timeStamps.get(i));
                    Date nextTime = DateFormatter.stringToDate(endDate);
                    long diff = nextTime.getTime() - firstTime.getTime();
                    diff = (diff / 1000);
                    //System.out.println(diff + " <<<<<");
                    if (diff >= 120) {
                        sum += 10;
                    } else {
                        sum += diff;
                    }
                }
            }
        }

        return sum;
    }

}
