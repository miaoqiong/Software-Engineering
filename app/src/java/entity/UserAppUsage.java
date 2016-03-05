package entity;

import utilities.DateFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * UserAppUsage tracks a user's details and application usage by storing a list
 * of app usage time stamps as well as the corresponding app categories of all
 * the applications used.
 * <p>
 * The total usage time is calculated based on a daily basis, within the
 * specified time period.
 */
public class UserAppUsage {

    private String year;
    private String gender;
    private String school;
    private String cca;
    private String macAddress;
    private ArrayList<String> timeStamps;
    private long totalUsageTime;
    private String endDate;
    private ArrayList<String> appCategory;

    /**
     * Creates and instantiates a UserAppUsage object
     *
     * @param year the user's year of admission
     * @param gender the user's gender
     * @param school the user's faculty/school
     * @param macAddress the user's unique mac-address
     * @param timeStamps a list of app usage time stamps
     * @param cca the user's cca
     * @param endDate the specified time period queried
     */
    public UserAppUsage(String year, String gender, String school, String macAddress, ArrayList<String> timeStamps, String cca, String endDate) {
        this.year = year;
        this.gender = gender;
        this.school = school;
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.endDate = endDate;
        totalUsageTime = getTotalUsageTime(timeStamps, endDate);
        this.cca = cca;
    }

    /**
     * Creates and instantiates a UserAppUsage object
     *
     * @param year the user's year of admission
     * @param gender the user's gender
     * @param school the user's faculty/school
     * @param macAddress the user's unique mac-address
     * @param timeStamps a list of app usage time stamps
     * @param endDate the specified time period queried
     */
    public UserAppUsage(String year, String gender, String school, String macAddress, ArrayList<String> timeStamps, String endDate) {
        this.year = year;
        this.gender = gender;
        this.school = school;
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.endDate = endDate;
        totalUsageTime = getTotalUsageTime(timeStamps, endDate);
    }

    /**
     * Creates and instantiates a UserAppUsage object
     *
     * @param macAddress the user's unique mac-address
     * @param timeStamps a list of app usage time stamps
     * @param appCategory the list of app categories being used
     * @param endDate the specified time period queried
     */
    public UserAppUsage(String macAddress, ArrayList<String> timeStamps, ArrayList<String> appCategory, String endDate) {
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.appCategory = appCategory;
        this.endDate = endDate;
        totalUsageTime = getTotalUsageTime(timeStamps, endDate);
    }

    /**
     * Creates and instantiates a UserAppUsage object
     *
     * @param timeStamps a list of app usage time stamps
     * @param endDate the specified time period queried
     */
    public UserAppUsage(ArrayList<String> timeStamps, String endDate) {
        this.timeStamps = timeStamps;
        this.endDate = endDate;
        totalUsageTime = getTotalUsageTime(timeStamps, endDate);
    }

    /**
     * Retrieves the categories of apps used
     *
     * @return the list of app categories used
     */
    public ArrayList<String> getappCategory() {
        return appCategory;
    }

    /**
     * Retrieves the user's year
     *
     * @return the user's year
     */
    public String getYear() {
        return year;
    }

    /**
     * Retrieves the user's gender
     *
     * @return the user's gender
     */
    public String getGender() {
        return gender;
    }

    /**
     * Retrieves the user's school
     *
     * @return the user's school/faculty
     */
    public String getSchool() {
        return school;
    }

    /**
     * Retrieves the corresponding value specified by the input string.
     *
     * @param s the queried value (year/gender/school/cca)
     * @return the corresponding value of the user
     */
    public String getAttribute(String s) {
        if (s.equals("year")) {
            return year;
        } else if (s.equals("gender")) {
            return gender;
        } else if (s.equals("school")) {
            return school;
        } else if (s.equals("cca")) {
            return cca;
        }
        return null;
    }

    /**
     * Retrieves the user's unique mac-address
     *
     * @return the user's mac-address
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Retrieves the list of the user's app usage time stamps
     *
     * @return the list of app usage time stamps
     */
    public ArrayList<String> getTimeStamps() {
        return timeStamps;
    }

    /**
     * Returns the user's total usage time
     *
     * @return the total app usage time
     */
    public long getTotalTime() {
        return totalUsageTime;
    }

    /**
     * Adds a time stamp into the user's list of app usage time stamps
     *
     * @param timeStamp the time stamp to be added
     */
    public void addTimeStamp(String timeStamp) {
        timeStamps.add(timeStamp);
    }

    /**
     * Sets the user's total usage time
     */
    public void setTotalUsageTime() {
        totalUsageTime = getTotalUsageTime(timeStamps, endDate);
    }

    /**
     * Adds an app category into the user's list of app categories used
     *
     * @param appCategorys the app category to be added
     */
    public void addAppCategory(String appCategorys) {
        appCategory.add(appCategorys);
    }

    /**
     * Sets the user's CCA
     *
     * @param cca the CCA to be set
     */
    public void setCCA(String cca) {
        this.cca = cca;
    }

    /**
     * Retrieves the user's CCA
     *
     * @return the user's CCA
     */
    public String getCCA() {
        return cca;
    }

    /**
     * Calculates and returns the User's total app usage time based on app
     * category.
     * <p>
     * The total usage time is calculated on a daily basis based on the
     * specified end date and broken down by each App Category
     *
     * @return a result list containing a user's app usage time for each app
     * category
     */
    public HashMap<String, Long> getTotalUsageTimeByApp() {

        HashMap<String, Long> resultList = new HashMap<>();
        ArrayList<String> timestamps = this.timeStamps;
        ArrayList<String> appCategory = this.appCategory;
        endDate = this.endDate;
        System.out.println(timeStamps.size());
        //timestamp = 1, add in directly
        if (timeStamps.size() == 1) {
            long sum = 0;
            Date time = DateFormatter.stringToDate(timeStamps.get(0));
            Date nextTime = DateFormatter.stringToDate(endDate);

            if (DateFormatter.getDayDifference(time, nextTime) == 0) {
                long difference = nextTime.getTime() - time.getTime();
                difference = difference / 1000;

                if (difference >= 119) {
                    sum += 10;
                } else {
                    sum = sum + difference + 1;
                }
            } else {
                String nextDay = timeStamps.get(0);
                nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                Date next = DateFormatter.stringToDate(nextDay);
                Date newNext = DateFormatter.addDays(next, 1);

                long difference = newNext.getTime() - time.getTime();
                difference /= 1000;

                if (difference >= 120) {
                    sum += 10;
                } else {
                    sum += difference;
                }
            }
            resultList.put(appCategory.get(0), sum);
        } //timestamp size != 1
        else {
            for (int i = 0; i < timeStamps.size(); i++) {
                long sum2 = 0;
                if (i + 1 != timeStamps.size()) {
                    Date firstTime = DateFormatter.stringToDate(timeStamps.get(i));
                    Date nextTime = DateFormatter.stringToDate(timeStamps.get(i + 1));

                    if (DateFormatter.getDayDifference(firstTime, nextTime) == 0) {
                        long difference = nextTime.getTime() - firstTime.getTime();
                        difference = difference / 1000;

                        if (difference > 120) {
                            sum2 += 10;
                        } else {
                            sum2 += difference;
                        }
                    } else {
                        String nextDay = timeStamps.get(i + 1);
                        nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                        Date next = DateFormatter.stringToDate(nextDay);

                        long difference = next.getTime() - firstTime.getTime();
                        difference /= 1000;

                        if (difference >= 120) {
                            sum2 += 10;
                        } else {
                            sum2 += difference;
                        }
                    }
                    if (resultList.get(appCategory.get(i)) == null) {
                        resultList.put(appCategory.get(i), sum2);
                    } else {
                        long usageTime = resultList.get(appCategory.get(i)) + sum2;
                        resultList.put(appCategory.get(i), usageTime);
                    }
                }
                if (i + 1 == timeStamps.size()) {
                    Date firstTime = DateFormatter.stringToDate(timeStamps.get(i));
                    Date nextTime = DateFormatter.stringToDate(endDate);

                    if (DateFormatter.getDayDifference(firstTime, nextTime) == 0) {
                        long difference = nextTime.getTime() - firstTime.getTime();
                        difference = difference / 1000;

                        if (difference >= 119) {
                            sum2 += 10;
                        } else {
                            sum2 = sum2 + difference + 1;
                        }
                    } else {
                        String nextDay = timeStamps.get(i);
                        nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                        Date next = DateFormatter.stringToDate(nextDay);
                        Date newNext = DateFormatter.addDays(next, 1);

                        long difference = newNext.getTime() - firstTime.getTime();
                        difference /= 1000;

                        if (difference >= 120) {
                            sum2 += 10;
                        } else {
                            sum2 += difference;
                        }
                    }

                    if (resultList.get(appCategory.get(i)) == null) {
                        resultList.put(appCategory.get(i), sum2);
                    } else {
                        long usageTime = resultList.get(appCategory.get(i)) + sum2;
                        resultList.put(appCategory.get(i), usageTime);
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * Calculates and returns the User's total app usage time.
     * <p>
     * The total usage time is calculated on a daily basis based on the
     * specified end date
     *
     * @param timeStamps the list of user's app usage time stamps
     * @param endDate the specified end date of query
     * @return
     */
    public long getTotalUsageTime(ArrayList<String> timeStamps, String endDate) {
        long sum = 0;

        if (timeStamps.size() == 1) {
            Date time = DateFormatter.stringToDate(timeStamps.get(0));
            Date nextTime = DateFormatter.stringToDate(endDate);

            if (DateFormatter.getDayDifference(time, nextTime) == 0) {
                long difference = nextTime.getTime() - time.getTime();
                difference = difference / 1000;

                if (difference >= 119) {
                    sum += 10;
                } else {
                    sum = sum + difference + 1;
                }
            } else {
                String nextDay = timeStamps.get(0);
                nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                Date next = DateFormatter.stringToDate(nextDay);
                Date newNext = DateFormatter.addDays(next, 1);

                long difference = newNext.getTime() - time.getTime();
                difference /= 1000;

                if (difference >= 120) {
                    sum += 10;
                } else {
                    sum += difference;
                }

            }

        } else {

            String firstDate = timeStamps.get(0);
            firstDate = firstDate.substring(0, firstDate.indexOf(' ')) + " 00:00:00";
            Date date1 = DateFormatter.stringToDate(firstDate);

            for (int i = 1; i < timeStamps.size(); i++) {

                Date firstTime = DateFormatter.stringToDate(timeStamps.get(i - 1));
                Date nextTime = DateFormatter.stringToDate(timeStamps.get(i));

                if (DateFormatter.getDayDifference(firstTime, nextTime) == 0) { //same day
                    long difference = nextTime.getTime() - firstTime.getTime();
                    difference = difference / 1000;

                    if (difference > 120) {
                        sum += 10;
                    } else {
                        sum += difference;
                    }
                } else {
                    String nextDay = timeStamps.get(i);
                    nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                    Date next = DateFormatter.stringToDate(nextDay);

                    long difference = next.getTime() - firstTime.getTime();
                    difference /= 1000;

                    if (difference >= 120) {
                        sum += 10;
                    } else {
                        sum += difference;
                    }

                }

                if (i + 1 == timeStamps.size()) {
                    //last entry in timeStamps
                    Date endTime = DateFormatter.stringToDate(endDate);
                    if (DateFormatter.getDayDifference(nextTime, endTime) == 0) { //same day
                        long difference = endTime.getTime() - nextTime.getTime();
                        difference = difference / 1000;

                        if (difference >= 119) {
                            sum += 10;
                        } else {
                            sum = sum + difference + 1;
                        }
                    } else {

                        endDate = endDate.substring(0, endDate.indexOf(' ')) + " 00:00:00";
                        Date next = DateFormatter.stringToDate(endDate);
                        next = DateFormatter.addDays(next, 1);

                        long difference = next.getTime() - nextTime.getTime();
                        difference /= 1000;

                        if (difference >= 120) {
                            sum += 10;
                        } else {
                            sum += difference;
                        }

                    }
                }
            }
        }

        return sum;
    }

    /**
     * Calculates and returns the User's total game usage time.
     * <p>
     * The total usage time is calculated on a daily basis based on the
     * specified end date
     * <p>
     * A game is an application with the category "Game"
     *
     * @param timeStamps the list of user time stamps
     * @param appcategories the list of app categories
     * @param endDate the specified end date of query
     * @return
     */
    public long getTotalGameUsageTime(ArrayList<String> timeStamps, ArrayList<String> appcategories, String endDate) {
        long sum = 0;

        if (timeStamps.size() == 1) {
            Date time = DateFormatter.stringToDate(timeStamps.get(0));
            String category = appcategories.get(0);
            long difference = DateFormatter.stringToDate(endDate).getTime() - time.getTime();
            difference = (difference / 1000);
            if (category.equals("Games")) {
                if (difference > 120) {
                    sum += 10;
                } else {
                    sum += difference;
                }
            }

        } else {
            String firstDate = timeStamps.get(0);
            firstDate = firstDate.substring(0, firstDate.indexOf(' ')) + " 00:00:00";
            Date date1 = DateFormatter.stringToDate(firstDate);

            for (int i = 1; i < timeStamps.size(); i++) {

                Date firstTime = DateFormatter.stringToDate(timeStamps.get(i - 1));
                Date nextTime = DateFormatter.stringToDate(timeStamps.get(i));

                String category = appcategories.get(i - 1);

                if (category.equals("Games")) {
                    if (DateFormatter.getDayDifference(firstTime, nextTime) == 0) { //same day
                        long difference = nextTime.getTime() - firstTime.getTime();
                        difference = difference / 1000;

                        if (difference > 120) {
                            sum += 10;
                        } else {
                            sum += difference;
                        }
                    } else {
                        String nextDay = timeStamps.get(i);
                        nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                        Date next = DateFormatter.stringToDate(nextDay);

                        long difference = next.getTime() - firstTime.getTime();
                        difference /= 1000;

                        if (difference > 120) {
                            sum += 10;
                        } else {
                            sum += difference;
                        }

                    }

                }
                if (i + 1 == timeStamps.size()) {
                    String lastCategory = appcategories.get(timeStamps.size() - 1);

                    if (lastCategory.equals("Games")) {
                        long diff = DateFormatter.stringToDate(endDate).getTime() - nextTime.getTime();
                        diff = (diff / 1000);
                        //System.out.println(diff + " ««<");
                        if (diff > 120) {
                            sum += 10;
                        } else {
                            sum += diff;
                        }
                    }

                }
            }
        }

        return sum;
    }

    /**
     * Calculates and returns the User's total app usage time per hour.
     * <p>
     * The total usage time is calculated on an hourly basis based on the
     * specified end date
     *
     * @param timeStamps the list of user's app usage time stamps
     * @param startDate the specified start date of query
     * @param endDate the specified end date of query
     * @return
     */
    public int getHourlyUsage(ArrayList<String> timeStamps, String startDate, String endDate) {
        int num = 1;

        if (timeStamps.size() == 1) {
            return num;

        } else {
            for (int i = 1; i < timeStamps.size(); i++) {
                Date firstTime = DateFormatter.stringToDate(timeStamps.get(i - 1));
                Date nextTime = DateFormatter.stringToDate(timeStamps.get(i));

                long difference = nextTime.getTime() - firstTime.getTime();
                difference = difference / 1000;

                if (difference > 120) {
                    num++;
                }
            }
        }
        return num;
    }
}
