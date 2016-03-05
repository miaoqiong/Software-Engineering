package entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import utilities.DateFormatter;

/**
 * TopkUsage records the details of Top-k usage details.
 *
 */
public class TopkUsage {

    private String macAddress;
    private ArrayList<String> timeStamps;
    private ArrayList<String> appName;
    private String endDate;
    private String studentName;
    private ArrayList<String> appCategory;
    private String school;

    /**
     * Construct a TopkUsage object with attribute set to given parameter
     *
     * @param macAddress mac-address of student
     * @param timeStamps ArrayList of timestamps
     * @param appName ArrayList of APP names
     * @param endDate end date that specified
     */
    public TopkUsage(String macAddress, ArrayList<String> timeStamps, ArrayList<String> appName, String endDate) {
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.appName = appName;
        this.endDate = endDate;

    }

    /**
     * Construct a TopkUsage object with attribute set to given parameter
     *
     * @param macAddress mac-address of student
     * @param timeStamps ArrayList of timestamps
     * @param appCategory ArrayList of APP categories
     * @param studentName name of student
     * @param endDate end date that specified
     */
    public TopkUsage(String macAddress, ArrayList<String> timeStamps, ArrayList<String> appCategory, String studentName, String endDate) {
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.appCategory = appCategory;
        this.studentName = studentName;
        this.endDate = endDate;

    }

    /**
     * Construct a TopkUsage object with attribute set to given parameter
     *
     * @param macAddress mac-address of student
     * @param timeStamps ArrayList of timestamps
     * @param appCategory ArrayList of APP categories
     * @param studentName name of student
     * @param school school name
     * @param endDate end date that specified
     */
    public TopkUsage(String macAddress, ArrayList<String> timeStamps, ArrayList<String> appCategory, String studentName, String school, String endDate) {
        this.macAddress = macAddress;
        this.timeStamps = timeStamps;
        this.appCategory = appCategory;
        this.studentName = studentName;
        this.school = school;
        this.endDate = endDate;

    }

    /**
     * Get the name of student
     *
     * @return student name
     */
    public String getStudentname() {
        return studentName;
    }

    /**
     * Get mac-address
     *
     * @return macAddress
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * Get school
     *
     * @return school
     */
    public String getSchool() {
        return school;
    }

    /**
     * Get timestamps
     *
     * @return ArrayList of timestamps
     */
    public ArrayList<String> getTimeStamps() {
        return timeStamps;
    }

    /**
     * Get APP name
     *
     * @return ArrayList of APP names
     */
    public ArrayList<String> getAppName() {
        return appName;
    }

    /**
     * Get APP categories
     *
     * @return ArrayList of APP categories
     */
    public ArrayList<String> getAppCategory() {
        return appCategory;
    }

    /**
     * Add new timestamp into timestamps
     *
     * @param timeStamp new timestamp
     */
    public void addTimeStamp(String timeStamp) {
        timeStamps.add(timeStamp);
    }

    /**
     * Add new APP name into appName
     *
     * @param appNames new APP name
     */
    public void addAppName(String appNames) {
        appName.add(appNames);

    }

    /**
     * Add new APP category into appCategory
     *
     * @param appCategorys new APP category
     */
    public void addAppCategory(String appCategorys) {
        appCategory.add(appCategorys);
    }

    /**
     * Calculate total APP usage time
     *
     * @return HashMap, key is APP name, value is total usage time
     */
    public HashMap<String, Long> getTotalUsageTimeByApp() {

        HashMap<String, Long> resultList = new HashMap<>();
        ArrayList<String> timestamps = this.timeStamps;
        ArrayList<String> appName = this.appName;
        endDate = this.endDate;

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
            resultList.put(appName.get(0), sum);
        } else {
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

                    if (resultList.get(appName.get(i)) == null) {
                        resultList.put(appName.get(i), sum2);
                    } else {
                        long usageTime = resultList.get(appName.get(i)) + sum2;
                        resultList.put(appName.get(i), usageTime);
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

                    if (resultList.get(appName.get(i)) == null) {
                        resultList.put(appName.get(i), sum2);
                    } else {
                        long usageTime = resultList.get(appName.get(i)) + sum2;
                        resultList.put(appName.get(i), usageTime);
                    }
                }

            }

        }

        return resultList;
    }

    /**
     * Calculate total usage time by different APP categories
     *
     * @return HashMap, key is APP category names, value is total usage time
     */
    public HashMap<String, Long> getTotalUsageTimeByAppCate() {
        HashMap<String, Long> resultList = new HashMap<>();
        ArrayList<String> timestamps = this.timeStamps;
        ArrayList<String> appCategory = this.appCategory;
        endDate = this.endDate;

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
        } else {
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
                        String nextDay = timeStamps.get(i);
                        nextDay = nextDay.substring(0, nextDay.indexOf(' ')) + " 00:00:00";
                        Date next = DateFormatter.stringToDate(nextDay);

                        long difference = next.getTime() - firstTime.getTime();
                        difference /= 1000;

                        if (difference > 120) {
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
                } else if (i + 1 == timeStamps.size()) {
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

}
