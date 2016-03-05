package utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Format date
 */
public class DateFormatter {
    
    /**
     * Convert Date to String format 
     * @param date input Date
     * @return date String format ("yyyy-MM-dd HH:mm:ss")
     */
    public static String formatDate(Date date) {
        String formattedDate = "";
        DateFormat dmf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            formattedDate = dmf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }
    
    /**
     * Convert String format to Date
     * @param str input date String format
     * @return Date format ("yyyy-MM-dd HH:mm:ss")
     */
    public static Date stringToDate(String str) {
        Date newDate = null;
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            newDate = dateFormat.parse(str);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }
    /**
     * Check if date is in a valid format
     * @param date input date
     * @return true, if date follow ("yyyy-MM-dd"). else, return false
     */
    public static boolean isDateValid(String date) {
        if (date.length() != 10) {
            return false;
        }
        Date newDate = null;
        String format = "yyyy-MM-dd";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dateFormat.setLenient(false);
            newDate = dateFormat.parse(date);

        } catch (Exception e) {
            return false;
        }
        return true;

    }
    
    /**
     * Increment date by the specified numbers
     * @param date input date
     * @param days numbers
     * @return  new Date
     */
    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    
    
    /**
     * Increment time by 1 hour
     * @param date input date
     * @return new Date
     */
    public static Date addHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 1);
        return cal.getTime();
    }
    
    /**
     * Calculate the difference between two Dates
     * @param date1 input Date1
     * @param date2 input Date2
     * @return number of days
     */
    public static int getDayDifference(Date date1, Date date2) {
        String firstDate = formatDate(date1);
        String secondDate = formatDate(date2);

        String year1 = firstDate.substring(0, 5);
        String year2 = secondDate.substring(0, 5);

        String month1 = firstDate.substring(5, 8);
        String month2 = secondDate.substring(5, 8);

        String day1 = firstDate.substring(8, firstDate.indexOf(' '));
        String day2 = secondDate.substring(8, firstDate.indexOf(' '));

        if (year1.equals(year2) && month1.equals(month2) && !day1.equals(day2)) {

            int dayValue1 = Integer.parseInt(day1);
            int dayValue2 = Integer.parseInt(day2);
            return dayValue2 - dayValue1;
        }

        return 0;
    }
}
