package dao;


import entity.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Retrieve applookup data from database
 */

public class AppDAO {

    private static final String TBLNAME = "applookup";

    private void handleSQLException(SQLException ex, String sql, String... parameters) {
        String msg = "Unable to access data; SQL=" + sql + "\n";
        for (String parameter : parameters) {
            msg += "," + parameter;
        }
        Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, msg, ex);
        throw new RuntimeException(msg, ex);
    }

    /**
     *delete applookup data in from database
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
     * @param data HashMap that stores validated applookup data 
     * @return number of records uploaded 
     */
    public static int upload(HashMap<String, App> data) {
        int totalUploaded = 0;
        if (data == null || data.size() == 0) {
            return totalUploaded;
        } else {

            Connection conn = null;
            PreparedStatement preStmt = null;
            try {
                conn = ConnectionManager.getConnection();
                conn.setAutoCommit(false);
                String sql = "insert into applookup (appid, appname, appcategory) values (?, ?, ?)";
                preStmt = conn.prepareStatement(sql);

                //preStmt.executeUpdate();
                final int batchSize = 1000;
                int count = 0;

                for (Map.Entry<String, App> entry : data.entrySet()) {
                    App app = entry.getValue();
                    String appid = app.getAppId();
                    String appname = app.getAppName();
                    String appcategory = app.getAppCategory();

                    preStmt.setString(1, appid);
                    preStmt.setString(2, appname);
                    preStmt.setString(3, appcategory);
                    preStmt.addBatch();

                    if (++count % batchSize == 0) {
                        totalUploaded+= preStmt.executeBatch().length;
                    }
                }
                
                totalUploaded+= preStmt.executeBatch().length; // insert remaining records
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
     * retrieve data from database
     * @return HashMap has appid as key, app object as value
     */
    public static HashMap<String, App> retrieveAll() {
        HashMap<String, App> appArr = new HashMap<>();

        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement preStmt = null;
        try {
            conn = ConnectionManager.getConnection();
            String sql = "Select appid, appname, appcategory from applookup";
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();

            while (rs.next()) {
                String[] record = new String[3];
                record[0] = rs.getString("appId");
                record[1] = rs.getString("appname");
                record[2] = rs.getString("appcategory");
                appArr.put(record[0], new App(record[0], record[1], record[2]));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            ConnectionManager.close(conn, preStmt, rs);
            return appArr;
        }
    }

}
