package dao;

import com.opencsv.CSVReader;
import entity.AppUsage;
import entity.DiurnalAppUsage;
import entity.TopkUsage;
import entity.User;
import entity.UserAppUsage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Retrieve APP date from database
 */
public class AppUsageDAO {

    private static final String TBLNAME = "app";

    private void handleSQLException(SQLException ex, String sql, String... parameters) {
        String msg = "Unable to access data; SQL=" + sql + "\n";
        for (String parameter : parameters) {
            msg += "," + parameter;
        }
        Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, msg, ex);
        throw new RuntimeException(msg, ex);
    }

    /**
     * Read raw data from app.csv
     * @param fileName 
     * @return LinkedHashMap has integer as key, string[] as value
     */
    public static LinkedHashMap<Integer, String[]> readFile(String fileName) {
        LinkedHashMap<Integer, String[]> appusageMap = new LinkedHashMap();
        int i = 0;
        try {
            CSVReader reader = new CSVReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"), ',', '"');

            String[] header = reader.readNext();
            appusageMap.put(++i, header);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {

                appusageMap.put(++i, nextLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return appusageMap;
    }

    /**
     * Delete data from database
     */
    public static void deleteAll() {
        Connection conn = null;
        PreparedStatement preStmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = "delete from " + TBLNAME;
            preStmt = conn.prepareStatement(sql);
            preStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, preStmt);
        }

    }

    /**
     *Upload appusage data into database
     * @param data validated appusage data
     * @return number of records uploaded
     */
    public static int upload(LinkedHashMap<String, AppUsage> data) {

        int totalUploaded = 0;
        if (data == null || data.size() == 0) {
            return totalUploaded;
        } else {

            Connection conn = null;
            PreparedStatement preStmt = null;
            try {
                conn = ConnectionManager.getConnection();
                conn.setAutoCommit(false);
                String sql = "insert into app (timestamp, macaddress, appid) values (?, ?, ?)";
                preStmt = conn.prepareStatement(sql);

                //preStmt.executeUpdate();
                final int batchSize = 1000;
                int count = 0;

                for (Map.Entry<String, AppUsage> entry : data.entrySet()) {
                    AppUsage appU = entry.getValue();

                    String timestamp = appU.getTime();
                    String macaddress = appU.getMacAddress();
                    String appid = appU.getID();

                    preStmt.setString(1, timestamp);
                    preStmt.setString(2, macaddress);
                    preStmt.setString(3, appid);
                    preStmt.addBatch();

                    if (++count % batchSize == 0) {
                        totalUploaded += preStmt.executeBatch().length;

                    }
                }
                totalUploaded += preStmt.executeBatch().length; // insert remaining records
                conn.commit();
                preStmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                ConnectionManager.close(conn);
            }

        }
        return totalUploaded;
    }

    /**
     * Retrieve appusage data from database
     * @return LinkedHashMap has mac-address as key, appusage object as value
     */
    public static LinkedHashMap<String, AppUsage> retrieveAll() {
        LinkedHashMap<String, AppUsage> appArr = new LinkedHashMap<>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preStmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = "Select * from app";
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();

            while (rs.next()) {
                String[] record = new String[3];
                String time = rs.getDate(1).toString() + " " + rs.getTime(1).toString();
                record[0] = time;

                record[1] = rs.getString("macaddress");
                record[2] = rs.getString("appid");
                //appArr.add(record);
                appArr.put(record[0] + record[1], new AppUsage(record[0], record[1], record[2]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, preStmt, rs);
            return appArr;
        }
    }

    /**
     *Retrieve appusage data from database, create UserAppUsage object
     * @param startDate start date
     * @param endDate end date
     * @return HashMap, list of user appusage, key is mac-address, value is UserAppUsage object
     */
    public HashMap<String, UserAppUsage> retrieveUsageReport(String startDate, String endDate) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, UserAppUsage> resultList = new HashMap<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select SUBSTRING_INDEX(SUBSTRING_INDEX(email, '@', 1), '.', -1) as year, gender, SUBSTRING_INDEX(SUBSTRING_INDEX(email, '@', -1),'.',1) as school, demo.macaddress, timestamp, cca from app, demo where app.macaddress = demo.macaddress and timestamp >= ? and timestamp <= ? order by demo.macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String year = rs.getString(1);
                String gender = rs.getString(2);
                String school = rs.getString(3);
                String macAddress = rs.getString(4);
                String dateTime = rs.getDate(5).toString() + " " + rs.getTime(5).toString();
                String cca = rs.getString(6);

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    UserAppUsage uap2 = new UserAppUsage(year, gender, school, macAddress, userTimeStamps, cca, endDate);
                    resultList.put(macAddress, uap2);
                } else { //user's mac address is already in list
                    UserAppUsage uap2 = resultList.get(macAddress);
                    uap2.addTimeStamp(dateTime);
                    resultList.put(macAddress, uap2);
                }

            }

            for (Map.Entry<String, UserAppUsage> entry : resultList.entrySet()) {
                UserAppUsage val = entry.getValue();
                val.setTotalUsageTime();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return resultList;
    }

    //report 3 : by app category
    /**
     *Retrieve appusage data from database for basic APP usage report breakdown by app category, create UserAppUsage object 
     * @param startDate start date
     * @param endDate end date
     * @return HashMap, list of user appusage, key is mac-address, value is UserAppUsage object
     */
        public HashMap<String, UserAppUsage> retrieveAppCategoryUsageReport(String startDate, String endDate) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, UserAppUsage> resultList = new HashMap<>();

        // format for order: year, gender, school,    /   gender, year
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select macaddress, timestamp, appcategory from app, applookup where applookup.appid = app.appid and timestamp >= ? and timestamp <= ? order by macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String macAddress = rs.getString(1);
                String dateTime = rs.getDate(2).toString() + " " + rs.getTime(2).toString();
                String appCategory = rs.getString(3);

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    ArrayList<String> userAppcategory = new ArrayList<>();
                    userAppcategory.add(appCategory);
                    UserAppUsage uap2 = new UserAppUsage(macAddress, userTimeStamps, userAppcategory, endDate);
                    resultList.put(macAddress, uap2);
                } else { //user's mac address is already in list
                    UserAppUsage uap2 = resultList.get(macAddress);
                    //ArrayList<String> targetTimeStamps = uap2.getTimeStamps();
                    //targetTimeStamps.add(dateTime);
                    uap2.addTimeStamp(dateTime);
                    uap2.addAppCategory(appCategory);
                    resultList.put(macAddress, uap2);
                }

            }

            for (Map.Entry<String, UserAppUsage> entry : resultList.entrySet()) {
                UserAppUsage val = entry.getValue();
                val.setTotalUsageTime();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return resultList;
    }

    //report 4 : dirunal usage no filter

    /**
     *Retrieve appusage data from database for basic APP usage report breakdown by diurnal pattern of app usage time (without filter specified), create DirunalAppUsage object 
     * @param start start date
     * @param end end date
     * @return HashMap, list of user appusage, key is mac-address, value is DiurnalAppUsage obejct
     */
        public HashMap<String, DiurnalAppUsage> retrieveDiurnalReport(Date start, Date end) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, DiurnalAppUsage> resultList = new HashMap<>();
        String startDate = utilities.DateFormatter.formatDate(start);
        String endDate = utilities.DateFormatter.formatDate(end);

        // format for order: year, gender, school,    /   gender, year
        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select macaddress, timestamp from app where timestamp >= ? and timestamp < ? order by macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String macAddress = rs.getString(1);
                String dateTime = rs.getDate(2).toString() + " " + rs.getTime(2).toString();

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    DiurnalAppUsage uap2 = new DiurnalAppUsage(macAddress, userTimeStamps);
                    resultList.put(macAddress, uap2);
                } else { //user's mac address is already in list
                    DiurnalAppUsage uap2 = resultList.get(macAddress);
                    uap2.addTimeStamp(dateTime);
                    resultList.put(macAddress, uap2);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return resultList;
    }

    //report 4: diurnal with filter

    /**
     *Retrieve appusage data from database for basic APP usage report breakdown by diurnal pattern of app usage time (with filter specified), create DirunalAppUsage object 
     * @param start start date
     * @param end end date
     * @param order filters user specified
     * @return HashMap, list of user appusage, key is mac-address, value is DiurnalAppUsage obejct
     */
        public HashMap<String, DiurnalAppUsage> retrieveDiurnalFilter(Date start, Date end, String order) {
        
        String startDate = utilities.DateFormatter.formatDate(start);
        String endDate = utilities.DateFormatter.formatDate(end);

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, DiurnalAppUsage> resultList = new HashMap<>();

        try {

            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select SUBSTRING_INDEX(SUBSTRING_INDEX(email, '@', 1), '.', -1) as year, gender, SUBSTRING_INDEX(SUBSTRING_INDEX(email, '@', -1),'.',1) as school, demo.macaddress, timestamp from app, demo where app.macaddress = demo.macaddress  " + order + " and timestamp >= ? and timestamp < ? order by demo.macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String yearResult = rs.getString(1);
                String genderResult = rs.getString(2);
                String schoolResult = rs.getString(3);
                String macAddress = rs.getString(4);
                String dateTime = rs.getDate(5).toString() + " " + rs.getTime(5).toString();

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    DiurnalAppUsage uap2 = new DiurnalAppUsage(macAddress, userTimeStamps, yearResult, genderResult, schoolResult);
                    resultList.put(macAddress, uap2);
                } else { //user's mac address is already in list
                    DiurnalAppUsage uap2 = resultList.get(macAddress);
                    uap2.addTimeStamp(dateTime);
                    resultList.put(macAddress, uap2);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return resultList;
    }
    


    //smartphone1st metrics

    /**
     * Retrieve all the timestamps between start date and end date for one user
     * @param start start date
     * @param end end date
     * @param user target user
     * @return hashmap with date as key and all the timestamps on that date as values
     */
        public HashMap<Date, ArrayList<String>> retrieveSmartphoneUsageTime(Date start, Date end, User user) {

        String startDate = utilities.DateFormatter.formatDate(start);
        String endDate = utilities.DateFormatter.formatDate(end);
        String userName = user.getFullName();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<Date, ArrayList<String>> map = new HashMap<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select timestamp from app, demo where app.macaddress = demo.macaddress and timestamp >= ? and timestamp <= ? and name = ? order by timestamp");
            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            pstmt.setString(3, userName);

            ArrayList<String> timestamps = null;
            ArrayList<Date> dates = new ArrayList<>();

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Date date = rs.getDate(1);
                String dateTime = rs.getDate(1).toString() + " " + rs.getTime(1).toString();

                if (map.get(date) == null) {
                    timestamps = new ArrayList<>();
                    timestamps.add(dateTime);
                    map.put(date, timestamps);
                    dates.add(date);
                } else {
                    timestamps = map.get(date);
                    timestamps.add(dateTime);
                    map.put(date, timestamps);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return map;
    }

    //smartphone 2nd metrics

    /**
     * Retrieve arraylist of all the timestamps and arraylist of all respective app category
     * @param start start date
     * @param end end date
     * @param user target user
     * @return arraylist of hashmaps with dates as keys and values of 
     * arraylist of timestamps and arraylist of respective app categories
     */
        public ArrayList<HashMap<Date, ArrayList<String>>> retrieveSmartphoneGameTime(Date start, Date end, User user) {

        String startDate = utilities.DateFormatter.formatDate(start);
        String endDate = utilities.DateFormatter.formatDate(end);
        String userName = user.getFullName();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList<HashMap<Date, ArrayList<String>>> mapList = new ArrayList<>();

        try {

            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select timestamp, appcategory from app, demo, applookup where app.appid = applookup.appid and app.macaddress = demo.macaddress and timestamp >= ? and timestamp <= ? and name = ? order by timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            pstmt.setString(3, userName);

            HashMap<Date, ArrayList<String>> map = new HashMap<>();
            HashMap<Date, ArrayList<String>> mapCat = new HashMap<>();
            ArrayList<String> timestamps = null;
            ArrayList<String> appcategories = null;
            ArrayList<Date> dates = new ArrayList<>();

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Date date = rs.getDate(1);
                String dateTime = rs.getDate(1).toString() + " " + rs.getTime(1).toString();
                String appcategory = rs.getString(2);

                if (map.get(date) == null) {
                    timestamps = new ArrayList<>();
                    timestamps.add(dateTime);
                    appcategories = new ArrayList<>();
                    appcategories.add(appcategory);
                    map.put(date, timestamps);
                    mapCat.put(date, appcategories);
                    dates.add(date);
                } else {
                    timestamps = map.get(date);
                    timestamps.add(dateTime);
                    appcategories.add(appcategory);
                    map.put(date, timestamps);
                    mapCat.put(date, appcategories);
                }
            }

            mapList.add(map);
            mapList.add(mapCat);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return mapList;

    }

    //smartphone 3rd metrics

    /**
     * Return all the timestamps between start date and end date
     * @param start start date
     * @param end end date
     * @param user target user
     * @return hashmap with start of an hour as keys and all the timestamps of that hour as values
     */
        public HashMap<String, ArrayList<String>> retrieveSmartphoneSessionNumber(Date start, Date end, User user) {
        String startDate = utilities.DateFormatter.formatDate(start);
        String endDate = utilities.DateFormatter.formatDate(end);
        String userName = user.getFullName();

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, ArrayList<String>> map = new HashMap<>();

        try {

            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select timestamp from app, demo, applookup where app.appid = applookup.appid and app.macaddress = demo.macaddress and timestamp >= ? and timestamp <= ? and name = ? order by timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);
            pstmt.setString(3, userName);

            ArrayList<String> timeStamps;
            //start hour string as key, timestamp in that hour as value

            rs = pstmt.executeQuery();
            while (rs.next()) {
                String dateTime = rs.getDate(1).toString() + " " + rs.getTime(1).toString();   //next timestamp

                int location = dateTime.indexOf(":");
                String dateHour = dateTime.substring(0, location) + ":00:00"; //start of an hour

                if (map.get(dateHour) == null) {
                    timeStamps = new ArrayList<>();
                    timeStamps.add(dateTime);
                    map.put(dateHour, timeStamps);
                } else {
                    timeStamps = map.get(dateHour);
                    timeStamps.add(dateTime);
                    map.put(dateHour, timeStamps);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return map;
    }

    /**
     *Retrieve appusage data from database for Top-k most used apps (given a school), create TopkUsage object
     * @param startDate start date
     * @param endDate end date
     * @param school school user specified
     * @return HashMap, a list of user top-k usage, key is mac-address, value is TopkUsage object.
     */
        public HashMap<String, TopkUsage> retrieveTopkbyschoolreport(String startDate, String endDate, String school) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, TopkUsage> resultList = new HashMap<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select demo.macaddress, timestamp, appname from app, applookup, demo where app.appid = applookup.appid and demo.macaddress = app.macaddress and " + school + " and timestamp >= ? and timestamp <= ? order by macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String macAddress = rs.getString(1);
                String dateTime = rs.getDate(2).toString() + " " + rs.getTime(2).toString();
                String appname = rs.getString(3);

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    ArrayList<String> userAppName = new ArrayList<>();
                    userAppName.add(appname);
                    TopkUsage uap2 = new TopkUsage(macAddress, userTimeStamps, userAppName, endDate);
                    resultList.put(macAddress, uap2);
                } else { //user's mac address is already in list
                    TopkUsage uap2 = resultList.get(macAddress);
                    uap2.addTimeStamp(dateTime);
                    uap2.addAppName(appname);
                    resultList.put(macAddress, uap2);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        //testing
        System.out.println(resultList.size());
        return resultList;

    }

    /**
     *Retrieve appusage data from database for Top-k students with most used apps (given an app category), create TopkUsage object
     * @param startDate start date
     * @param endDate end date
     * @return HashMap, a list of user top-k usage, key is mac-address, value is TopkUsage
     */
        public HashMap<String, TopkUsage> retrieveTopkstudentreport(String startDate, String endDate) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, TopkUsage> resultList = new HashMap<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select demo.macaddress, timestamp, appcategory, name from app, applookup, demo where app.appid = applookup.appid and demo.macaddress = app.macaddress and timestamp >= ? and timestamp <= ? order by macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String macAddress = rs.getString(1);
                String dateTime = rs.getDate(2).toString() + " " + rs.getTime(2).toString();
                String appcategory = rs.getString(3);
                String studentname = rs.getString(4);

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    ArrayList<String> userAppCategory = new ArrayList<>();
                    userAppCategory.add(appcategory);
                    TopkUsage uap2 = new TopkUsage(macAddress, userTimeStamps, userAppCategory, studentname, endDate);
                    resultList.put(macAddress, uap2);
                } else { 
                    TopkUsage uap2 = resultList.get(macAddress);
                    uap2.addTimeStamp(dateTime);
                    uap2.addAppCategory(appcategory);
                    resultList.put(macAddress, uap2);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return resultList;
    }

    /**
     *Retrieve appusage data from database for Top-k schools with most used apps (given an app category), create TopkUsage object
     * @param startDate start date
     * @param endDate end date
     * @return HashMap, a list of user top-k usage, key is mac-address, value is TopkUsage
     */
        public HashMap<String, TopkUsage> retrieveTopkschoolreport(String startDate, String endDate) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        HashMap<String, TopkUsage> resultList = new HashMap<>();

        try {
            conn = ConnectionManager.getConnection();
            pstmt = conn.prepareStatement("select demo.macaddress, timestamp, appcategory, name, SUBSTRING_INDEX(SUBSTRING_INDEX(email, '@', -1),'.',1) as school from app, applookup, demo where app.appid = applookup.appid and demo.macaddress = app.macaddress and timestamp >= ? and timestamp <= ? order by macaddress, timestamp");

            pstmt.setString(1, startDate);
            pstmt.setString(2, endDate);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String macAddress = rs.getString(1);
                String dateTime = rs.getDate(2).toString() + " " + rs.getTime(2).toString();
                String appcategory = rs.getString(3);
                String studentname = rs.getString(4);
                String school = rs.getString(5);

                if (resultList.get(macAddress) == null) { //if targetMac address is not in HashMap
                    ArrayList<String> userTimeStamps = new ArrayList<>();
                    userTimeStamps.add(dateTime);
                    ArrayList<String> userAppCategory = new ArrayList<>();
                    userAppCategory.add(appcategory);
                    TopkUsage uap2 = new TopkUsage(macAddress, userTimeStamps, userAppCategory, studentname, school, endDate);
                    resultList.put(macAddress, uap2);
                } else {
                    TopkUsage uap2 = resultList.get(macAddress);
                    uap2.addTimeStamp(dateTime);
                    uap2.addAppCategory(appcategory);
                    resultList.put(macAddress, uap2);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, pstmt, rs);
        }
        return resultList;
    }

}
