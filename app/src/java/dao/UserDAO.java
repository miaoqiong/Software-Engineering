package dao;


import entity.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Objects for User objects
 */
public class UserDAO {

    private static final String TBLNAME = "demo";

    private void handleSQLException(SQLException ex, String sql, String... parameters) {
        String msg = "Unable to access data; SQL=" + sql + "\n";
        for (String parameter : parameters) {
            msg += "," + parameter;
        }
        Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, msg, ex);
        throw new RuntimeException(msg, ex);
    }

    /**
     * Retrieves the User object corresponding to the name and password
     * @param name name of user
     * @param password password of user
     * @return a User object with specific name and password
     */
    public User retrieve(String name, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "";
        User resultUser = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            sql = "select macaddress, name, password, email, gender, cca from " + TBLNAME + " where name = ? and password = binary?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, password);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String macAddress = rs.getString(1);
                String studentName = rs.getString(2);
                String correctPassword = rs.getString(3);
                String email = rs.getString(4);
                String gender = rs.getString(5);
                String cca = rs.getString(6);

                resultUser = new User(macAddress, studentName, correctPassword, email, gender, cca);
            }
            //return resultUser;

        } catch (SQLException ex) {
            handleSQLException(ex, sql, "User={" + resultUser + "}");
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return resultUser;
    }

    /**
     * Retrieves the User object corresponding tot the email id and password
     * @param emailID email ID of user
     * @param password password of user
     * @return a User object with specific emailID and password
     */
    public User retrieveSingleUser(String emailID, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "";
        User resultUser = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            sql = "select macaddress, name, password, email, gender, cca from " + TBLNAME + " where email like '" + emailID + "%smu.edu.sg' and password = binary?";
            stmt = conn.prepareStatement(sql);
            //stmt.setString(1, emailID);
            stmt.setString(1, password);

            rs = stmt.executeQuery();

            while (rs.next()) {
                String macAddress = rs.getString(1);
                String studentName = rs.getString(2);
                String correctPassword = rs.getString(3);
                String email = rs.getString(4);
                String gender = rs.getString(5);
                String cca = rs.getString(6);

                resultUser = new User(macAddress, studentName, correctPassword, email, gender, cca);
            }
            //return resultUser;

        } catch (SQLException ex) {
            handleSQLException(ex, sql, "User={" + resultUser + "}");
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return resultUser;
    }

    /**
     * Deletes demographics data from database.
     */
    public static void deleteAll() {
        Connection conn = null;
        PreparedStatement preStmt = null;

        try {
            conn = ConnectionManager.getConnection();
            String sql = "DELETE FROM " + TBLNAME;
            preStmt = conn.prepareStatement(sql);
            preStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, preStmt);
        }

    }

    /**
     *
     * @param data validated demographics data
     * @return number of records uploaded 
     */
    public static int upload(HashMap<String, User> data) {
        int totalUploaded = 0;
        if (data == null || data.size() == 0) {
            return totalUploaded;
        } else {

            Connection conn = null;
            PreparedStatement preStmt = null;
            try {
                conn = ConnectionManager.getConnection();
                conn.setAutoCommit(false);
                String sql = "insert into demo (macaddress,name,password,email,gender,cca) values (?, ?, ?, ?, ?,?)";
                preStmt = conn.prepareStatement(sql);

                //preStmt.executeUpdate();
                final int batchSize = 1000;
                int count = 0;

                for (Map.Entry<String, User> entry : data.entrySet()) {
                    User user = entry.getValue();
                    String macaddress = user.getMacAddress();
                    String name = user.getFullName();
                    String password = user.getPassword();
                    String email = user.getEmail();
                    String gender = user.getGender();
                    String cca = user.getCCA();

                    preStmt.setString(1, macaddress);
                    preStmt.setString(2, name);
                    preStmt.setString(3, password);
                    preStmt.setString(4, email);
                    preStmt.setString(5, gender);
                    preStmt.setString(6, cca);
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
     * retrieve demographics data from database
     * @return HashMap has macaddress as key, user object as value
     */
    public static HashMap<String, User> retrieveAll() {
        HashMap<String, User> userArr = new HashMap<>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preStmt = null;

        try {
            conn = ConnectionManager.getConnection();
            String sql = "Select macaddress, name, password, email, gender,cca from demo";
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();
            while (rs.next()) {
                String[] record = new String[6];
                record[0] = rs.getString(1);
                record[1] = rs.getString(2);
                record[2] = rs.getString(3);
                record[3] = rs.getString(4);
                record[4] = rs.getString(5);
                record[5] = rs.getString(6);
                userArr.put(record[0], new User(record[0], record[1], record[2], record[3], record[4], record[5]));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, preStmt, rs);
            return userArr;
        }
    }

    /**
     * retrieve distinct cca from database
     * @return ArrayList that stores retrieved cca
     */
    public static ArrayList<String> retrieveAllCCA() {
        ArrayList<String> ccaList = new ArrayList<String>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preStmt = null;

        try {
            conn = ConnectionManager.getConnection();
            String sql = "select distinct cca from demo";
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();

            while (rs.next()) {
                String cca = rs.getString(1);
                ccaList.add(cca);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, preStmt, rs);
        }

        return ccaList;
    }

    /**
     *
     * @return
     */
    public static HashMap<String, String[]> retrieveFilters() {
        String[] gender = {"F", "M"};
        String[] school = {"accountancy", "business", "economics", "law", "sis", "socsc"};
        String[] year = {"2011", "2012", "2013", "2014", "2015"};

        ArrayList<String> ccaList = retrieveAllCCA();
        Collections.sort(ccaList);
        String[] cca = ccaList.toArray(new String[ccaList.size()]);
        
        
        
        HashMap<String, String[]> map = new HashMap<>();
        map.put("gender", gender);
        map.put("school", school);
        map.put("year", year);
        map.put("cca", cca);

        return map;
    }

    /**
     *
     * @param macaddress macaddress of user
     * @return an user object with specific macaddress
     */
    public static User retrieveSingleUserByMacAddress(String macaddress) {
        Connection conn = null;
        PreparedStatement stmt = null;
        String sql = "";
        User resultUser = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            sql = "select macaddress, name, password, email, gender, cca from " + TBLNAME + " where macaddress = '" + macaddress + "' ";
            stmt = conn.prepareStatement(sql);
 
            rs = stmt.executeQuery();

            while (rs.next()) {
                String macAddress = rs.getString(1);
                String studentName = rs.getString(2);
                String correctPassword = rs.getString(3);
                String email = rs.getString(4);
                String gender = rs.getString(5);
                String cca = rs.getString(6);

                resultUser = new User(macAddress, studentName, correctPassword, email, gender, cca);
            }
            

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return resultUser;
    }

}
