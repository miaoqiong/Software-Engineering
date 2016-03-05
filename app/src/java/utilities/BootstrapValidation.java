package utilities;

import java.io.*;
import java.util.*;
import com.opencsv.CSVReader;
import dao.AppDAO;
import dao.AppUsageDAO;
import dao.UserDAO;
import entity.App;
import entity.AppUsage;
import entity.User;
import java.text.SimpleDateFormat;


/**
 *Validation for CSV files 
 */
public class BootstrapValidation {

    private static ArrayList<String> errorMsg = new ArrayList<>();

    /**
     * perform both common and specific validation for demographics.csv 
     * @param userData raw data from demographics.csv
     * @param fileName  
     * @return HashMap in which the validated demographics data were stored. key: user's mac address, value: user object
     */
    public static HashMap<String, User> validateDemographics(HashMap<Integer, String[]> userData, String fileName) {
        fileName = "demographics.csv";
        HashMap<String, User> userMap = new HashMap<>();
        String[] header = new String[6];
        for (Map.Entry<Integer, String[]> entry : userData.entrySet()) {
            header = entry.getValue();
            break;

        }
        int macAddIndex = -1;
        int nameIndex = -1;
        int pwdIndex = -1;
        int genderIndex = -1;
        int emailIndex = -1;
        int ccaIndex = -1;

        for (int i = 0; i < header.length; i++) {
            if (header[i].equals("mac-address")) {
                macAddIndex = i;
            } else if (header[i].equals("name")) {
                nameIndex = i;
            } else if (header[i].equals("password")) {
                pwdIndex = i;
            } else if (header[i].equals("gender")) {
                genderIndex = i;
            } else if (header[i].equals("email")) {
                emailIndex = i;
            } else if (header[i].equals("cca")) {
                ccaIndex = i;
            }

        }

        int lineNumber = 1;
        for (Map.Entry<Integer, String[]> entry : userData.entrySet()) {
            boolean blankField = false;
            String[] temp = entry.getValue();
            String msg = fileName + " Line Number: " + lineNumber;
            int errorLocation = 0;
            for (int k = 0; k < temp.length; k++) {
                temp[k] = temp[k].trim();
                if (temp[k].isEmpty() || temp[k] == null) {
                    errorLocation = k + 1;
                    blankField = true;
                    msg += ", blank " + header[errorLocation - 1] ;

                }
            }

            if (blankField) {
                errorMsg.add(msg);
                lineNumber++;
            } else {
                String macAddress = temp[macAddIndex].toLowerCase();
                String password = temp[pwdIndex];
                String email = temp[emailIndex];
                String gender = temp[genderIndex];
                String cca = temp[ccaIndex];

                if (!macAddress.matches("[a-fA-F0-9]{40}")) {
                    msg += ", invalid mac address";
                }
                if (password.length() < 8 || password.indexOf(" ") != -1) {
                    msg += ", invalid password";
                }
                if (!emailValidation(email.toLowerCase())) {
                    msg += ", invalid email";
                }
                if (!gender.toUpperCase().equals("M") && !gender.toUpperCase().equals("F")) {
                    msg += ", invalid gender";
                }
                if (cca.length() > 63) {
                    msg += ", cca record too long";
                }

                if (!msg.contains("invalid") && !(msg.contains("cca"))) {
                    User user = new User(macAddress.toLowerCase(), temp[nameIndex], password, email.toLowerCase(), gender, cca);
                    userMap.put(macAddress.toLowerCase(), user);
                  
                    lineNumber++;
                } else {
                    errorMsg.add(msg);
                    lineNumber++;

                }

            }

        }

        return userMap;

    }

    /**
     * perform both common and specific validation for app-lookup.csv
     * @param applookupData raw data from app-lookup.csv
     * @param fileName
     * @return HashMap in which the validated app-lookup data were stored. key: app-id,value: app object 
     */
    public static HashMap<String, App> validateAppLookUp(HashMap<Integer, String[]> applookupData, String fileName) {
        HashMap<String, App> appLookupMap = new HashMap<>();
        fileName = "app-lookup.csv";
        String[] header = new String[3];
        for (Map.Entry<Integer, String[]> entry : applookupData.entrySet()) {
            header = entry.getValue();
            break;

        }
        int appidIndex = -1;
        int appNameIndex = -1;
        int appCatIndex = -1;

        for (int i = 0; i < header.length; i++) {
            if (header[i].equals("app-id")) {
                appidIndex = i;
            } else if (header[i].equals("app-name")) {
                appNameIndex = i;
            } else if (header[i].equals("app-category")) {
                appCatIndex = i;
            }

        }

        int lineNumber = 1;
        for (Map.Entry<Integer, String[]> entry : applookupData.entrySet()) {
            
            boolean blankField = false;
            String[] temp = entry.getValue();;
            String msg = fileName + " Line Number: " + lineNumber;
            int errorLocation = 0;
            for (int k = 0; k < temp.length; k++) {
                temp[k] = temp[k].trim();
                if (temp[k].isEmpty() || temp[k] == null) {
                    errorLocation = k + 1;
                    blankField = true;
                    msg += ", blank " + header[errorLocation - 1];

                }
            }

            if (blankField) {
                errorMsg.add(msg);
                lineNumber++;
            } else {
                int appId = 0;
                try {
                    appId = Integer.parseInt(temp[appidIndex]);
                    if (appId <= 0) {
                        msg += ", invalid app id";

                    }
                } catch (NumberFormatException e) {
                    msg += ", invalid app id";
                }
                String appCategory = temp[appCatIndex];

                if (!validateAppCategory(appCategory)) {
                    msg += ", invalid app category";
                }
                if (!msg.contains("invalid")) {
                   
                    App applookup = new App(temp[appidIndex], temp[appNameIndex], appCategory);
                    appLookupMap.put(temp[appidIndex], applookup);
                   
                    lineNumber++;
                } else {
                    errorMsg.add(msg);
                    lineNumber++;

                }

            }
        }

        return appLookupMap;

    }

    /**
     * perform both common and specific validation for app.csv
     * @param data raw data from app.csv
     * @param demoData raw data from demographics.csv
     * @param applookupData raw data from app-lookup.csv
     * @param fileName
     * @return LinkedHashMap in which the validated app-usage data were stored. key:macaddress and timestamp, value: appusage object
     */
    public static LinkedHashMap<String, AppUsage> validateAppusageData(HashMap<Integer, String[]> data, HashMap<String, User> demoData, HashMap<String, App> applookupData, String fileName) {
        fileName = "app.csv";
        LinkedHashMap<String, AppUsage> appUsageLmp = new LinkedHashMap<>();
        ArrayList<String> error = new ArrayList<>();
        String[] header = data.get(1);
        Set<String> set1 = demoData.keySet();
        Set<String> set2 = applookupData.keySet();
        
        int appidIndex = -1;
        int macAddIndex = -1;
        int timeStampIndex = -1;

        for (int i = 0; i < header.length; i++) {
            if (header[i].equals("app-id")) {
                appidIndex = i;
            } else if (header[i].equals("mac-address")) {
                macAddIndex = i;
            } else if (header[i].equals("timestamp")) {
                timeStampIndex = i;
            }

        }

        AppUsage h = new AppUsage(header[timeStampIndex], header[macAddIndex], header[appidIndex]);
        appUsageLmp.put(header[timeStampIndex] + header[macAddIndex], h);
        for (int i = data.size(); i > 1; i--) {
            boolean blankField = false;
            String[] temp = data.get(i);
      
            String msg = fileName + " Line Number: " + i;
            int errorLocation = 0;
            for (int k = 0; k < temp.length; k++) {
                temp[k] = temp[k].trim();
                if (temp[k].isEmpty() || temp[k] == null) {
                    errorLocation = k + 1;
                    blankField = true;
                    msg += ", blank " + header[errorLocation - 1];

                }
            }

            if (blankField) {
                error.add(msg);

            } else {

                String date = temp[timeStampIndex];
                String macAdd = temp[macAddIndex].toLowerCase();
                String appId = temp[appidIndex];

                if (!isTimeStampValid(date)) {
                    msg += ", invalid timestamp";
                }

                if (!macAdd.matches("[a-fA-F0-9]{40}")) {
                    msg += ", invalid mac address";

                }
                if (!msg.contains("invalid")) {

                    if (!set2.contains(appId)) {
                        msg += ", invalid app";

                    }
                    if (!set1.contains(macAdd.toLowerCase())) {
                        msg += ", no matching mac address";
                    }
                }
                if (!msg.contains("invalid") && !msg.contains("no matching mac address")) {
                    AppUsage result = new AppUsage(date, macAdd, appId);

                    if (appUsageLmp.get(date + macAdd) != null) {
                        msg += ", duplicate row";
                        error.add(msg);

                    } else {

                        appUsageLmp.put(date + macAdd, result);

                    }

                } else {
                    error.add(msg);

                }
            }
        }
        ArrayList<String> reverseArray = reverseMsg(error);
        for (int i = 0; i < reverseArray.size(); i++) {
            errorMsg.add(reverseArray.get(i));
        }
        appUsageLmp.remove("timestampmac-address");
        return appUsageLmp;
    }

    /**
     * perform both common and specific validation for app.csv 
     * @param data raw data from app.csv
     * @param fileName
     * @return LinkedHashMap in which the validated app-usage data were stored. key: macaddress and timestamp, value: appusage object 
     */
    public static LinkedHashMap<String, AppUsage> validateAppusageDataWithDB(LinkedHashMap<Integer, String[]> data, String fileName) {
        ArrayList<String> error = new ArrayList<>();
        fileName = "app.csv";
        HashMap<String, User> demoData = UserDAO.retrieveAll();
        HashMap<String, App> applookupData = AppDAO.retrieveAll();

        LinkedHashMap<String, AppUsage> returnLmp = new LinkedHashMap<>();

        String[] header = data.get(1);
        LinkedHashMap<String, AppUsage> appusageMapDB = AppUsageDAO.retrieveAll();
        int appidIndex = -1;
        int macAddIndex = -1;
        int timeStampIndex = -1;

        for (int i = 0; i < header.length; i++) {
            if (header[i].equals("app-id")) {
                appidIndex = i;
            } else if (header[i].equals("mac-address")) {
                macAddIndex = i;
            } else if (header[i].equals("timestamp")) {
                timeStampIndex = i;
            }

        }

        int lineNumber = data.size();
        AppUsage h = new AppUsage(header[timeStampIndex], header[macAddIndex], header[appidIndex]);
        returnLmp.put(header[timeStampIndex] + header[macAddIndex], h);

        for (int i = data.size(); i > 0; i--) {
            boolean blankField = false;
            String[] temp = data.get(i);
            String msg = fileName + " Line Number: " + lineNumber;
            int errorLocation = 0;
            for (int k = 0; k < temp.length; k++) {
                temp[k] = temp[k].trim();
                if (temp[k].isEmpty() || temp[k] == null) {
                    errorLocation = k + 1;
                    blankField = true;
                    msg += ", blank " + header[errorLocation - 1];

                }
            }

            if (blankField) {
                error.add(msg);
                lineNumber--;
            } else {

                String date = temp[timeStampIndex];
                String macAdd = temp[macAddIndex].toLowerCase();
                String appId = temp[appidIndex];

                if (!isTimeStampValid(date)) {
                    msg += ", invalid timestamp";
                }

                if (!macAdd.matches("[a-fA-F0-9]{40}")) {
                    msg += ", invalid mac address";

                }

                if (!msg.contains("invalid")) {

                    if (!applookupData.keySet().contains(appId)) {
                        msg += ", invalid app";

                    }
                    if (!demoData.keySet().contains(macAdd.toLowerCase())) {
                        msg += ", no matching mac address";
                    }
                }
                if (!msg.contains("invalid") && !msg.contains("no matching mac address")) {
                    AppUsage result = new AppUsage(date, macAdd, appId);
                    if (returnLmp.get(date + macAdd) != null) {
                        msg += ", duplicate row";
                        error.add(msg);
                        lineNumber--;
                    } else {
                        if (appusageMapDB.size() == 0) {
                            appusageMapDB.put(header[timeStampIndex] + header[macAddIndex], h);
                        }
                        if (appusageMapDB.get(date + macAdd) != null) {
                            msg += ", duplicate row";
                            error.add(msg);
                        } else {
                            returnLmp.put(date + macAdd, result);

                        }
                        lineNumber--;
                    }

                } else {
                    error.add(msg);
                    lineNumber--;
                }

            }

        }

        returnLmp.remove(header[timeStampIndex] + header[macAddIndex]);
        ArrayList<String> errors = reverseMsg(error);
        for (int i = 0; i < errors.size(); i++) {
            errorMsg.add(errors.get(i));
        }
        return returnLmp;

    }

    /**
     * retrieve all error messages
     * @return ArrayList that stores all validation errors 
     */
    public static ArrayList<String> retrieveErrors() {
        return errorMsg;
    }

    /**
     * clean all validation errors
     */
    public static void clearErrors() {
        errorMsg = new ArrayList<>();
    }

    /**
     * reverse the order of input parameter
     * @param input
     * @return reversed ArrayList
     */
    public static ArrayList<String> reverseMsg(ArrayList<String> input) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = input.size() - 1; i >= 0; i--) {
            String s = input.get(i);
            result.add(s);

        }
        return result;
    }


    /**
     * read contents from csv file 
     * @param fileName
     * @return HashMap in which the csv contents were stored 
     */
    public static HashMap<Integer, String[]> readFileAll(String fileName) {
        HashMap<Integer, String[]> result = new HashMap<>();

        int i = 0;
        try {
            CSVReader reader = new CSVReader(
                    new InputStreamReader(new FileInputStream(fileName), "UTF-8"), ',', '"');

            String[] header = reader.readNext();
            result.put(++i, header);

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                result.put(++i, nextLine);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * perform validation for timestamp 
     * @param date
     * @return true or false
     */
    public static boolean isTimeStampValid(String date) {

        Date newDate = null;
        String format = "yyyy-MM-dd HH:mm:ss";
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
     * perform validation for app category  
     * @param appCategory
     * @return true or false 
     */
    public static boolean validateAppCategory(String appCategory) {
        String validateApp = appCategory.toLowerCase();
        String[] categories = {"books", "social", "education", "entertainment", "information", "library", "local", "tools", "fitness", "games", "others"};
        for (String s : categories) {
            if (s.equalsIgnoreCase(validateApp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * perform validation for email
     * @param email
     * @return true or false
     */
    public static boolean emailValidation(String email) {
        int atIndex = email.indexOf('@');
        String secondHalfEmail;
        String userName;
        String year;
        String school;
        String domain;
        char dot1;

        try {
            secondHalfEmail = email.substring(atIndex + 1);
            userName = email.substring(0, atIndex - 5);
            year = email.substring(atIndex - 4, atIndex);
            school = secondHalfEmail.substring(0, secondHalfEmail.indexOf("."));
            domain = secondHalfEmail.substring(secondHalfEmail.indexOf(".") + 1);
            dot1 = email.charAt(atIndex - 5);
        } catch (StringIndexOutOfBoundsException e) {
            return false;
        }

        if (atIndex != -1) {
            if (userName.matches("[A-Za-z0-9.]")) {
                return false;
            }
            if (!domain.equals("smu.edu.sg")) {
                return false;
            }
            if (!yearValidation(year)) {
                return false;
            }
            if (!schoolValidation(school)) {
                return false;
            }
            if (dot1 != '.') {
                return false;
            }

        } else {
            return false;
        }
        return true;
    }

    /**
     * perform validation for school 
     * @param school
     * @return true or false
     */
    public static boolean schoolValidation(String school) {
        String[] schools = {"business", "accountancy", "sis", "economics", "law", "socsc"};
        for (String s : schools) {
            if (s.equals(school)) {
                return true;
            }
        }
        return false;
    }

    /**
     * perform validation for year
     * @param year
     * @return true or false
     */
    public static boolean yearValidation(String year) {
        String[] years = {"2011", "2012", "2013", "2014", "2015"};
        for (String s : years) {
            if (s.equals(year)) {
                return true;
            }
        }
        return false;
    }

   
    /**
     * convert ArrayList into HashMap
     * @param aList stores String[]
     * @return HashMap has integer as key, String[] as value
     */
    public static HashMap<Integer, String[]> convertToHashmap(ArrayList<String[]> aList) {
        HashMap<Integer, String[]> dataMap = new HashMap<>();
        int i = 0;
        for (String[] sArr : aList) {
            dataMap.put(++i, sArr);
        }

        return dataMap;

    }

    /**
     * convert ArrayList into LinkedHashMap
     * @param aList stores String[]
     * @return LinkedHashMap has integer as key, String[] as value
     */
    public static LinkedHashMap<Integer, String[]> convertToLinkedHashmap(ArrayList<String[]> aList) {
        LinkedHashMap<Integer, String[]> dataMap = new LinkedHashMap<>();
        int i = 0;
        for (String[] sArr : aList) {
            dataMap.put(++i, sArr);
        }

        return dataMap;

    }

}
