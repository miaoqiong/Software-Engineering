package Servlets;

import utilities.DateFormatter;
import dao.AppUsageDAO;
import dao.UserDAO;
import entity.UserAppUsage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProcessBasicAppUsage", urlPatterns = {"/ProcessBasicAppUsage"})
/**
 * Processes the Basic Application Usage Breakdown.
 */
public class ProcessBasicAppUsage extends HttpServlet {

    private static final boolean DEBUG = true;
    //private static int totalUsers;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reportType = (String) request.getSession().getAttribute("basicApp");

        String fc = (String) request.getParameter("fc");
        String sc = (String) request.getParameter("sc");
        String tc = (String) request.getParameter("tc");
        String lc = (String) request.getParameter("lc");
        String order = null;
        if (!fc.equals("")) {

            fc = fc.toLowerCase();
            sc = sc.toLowerCase();
            tc = tc.toLowerCase();
            order = fc;
            if (!sc.equals("")) {
                order = order + "," + sc;
                if (!tc.equals("")) {
                    order = order + "," + tc;
                    if (!lc.equals("")) {
                        order = order + "," + lc;
                    }
                }

            }
        }

        // check if filter selected is invalid
        if (order != null || fc.equals("")) {

            ArrayList<String> filterCheck = new ArrayList<String>();
            filterCheck.add(fc);
            filterCheck.add(sc);
            filterCheck.add(tc);
            filterCheck.add(lc);

            for (int i = 1; i < filterCheck.size(); i++) {
                String prevFilter = filterCheck.get(i - 1);
                String nextFilter = filterCheck.get(i);

                if (prevFilter.equals("") && !nextFilter.equals("")) {
                    RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageReportDemo.jsp");
                    request.setAttribute("errorMsg", "Filters selected is invalid!");
                    rd.forward(request, response);
                    return;
                }
            }
        }
        String startDate = (String) request.getParameter("startdate");

        String endDate = (String) request.getParameter("enddate");
        if (startDate == null && endDate == null && fc == null && sc == null && tc == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (startDate.equals("") || endDate.equals("")) {
            if (reportType.equals("basic")) {
                RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageReport.jsp");
                request.setAttribute("errorMsg", "Start Date/End Date cannot be blank!");
                rd.forward(request, response);
                return;
            }
            if (reportType.equals("filter")) {
                RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageReportDemo.jsp");
                request.setAttribute("errorMsg", "Start Date/End Date cannot be blank!");
                rd.forward(request, response);
                return;
            }

        }

        UserDAO userDAO = new UserDAO();
        HashMap<String, String[]> map = userDAO.retrieveFilters();

        request.setAttribute("order", order);

        startDate = startDate + " 00:00:00";
        endDate = endDate + " 23:59:59";
        Date start = DateFormatter.stringToDate(startDate);
        Date end = DateFormatter.stringToDate(endDate);

        if (end != null && start != null) {
            if (end.before(start)) {
                if (reportType.equals("basic")) {
                    RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageReport.jsp");
                    request.setAttribute("errorMsg", "Start Date cannot be later than End Date!");
                    rd.forward(request, response);
                    return;
                }
                if (reportType.equals("filter")) {
                    RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageReportDemo.jsp");
                    request.setAttribute("errorMsg", "Start Date cannot be later than End Date!");
                    rd.forward(request, response);
                    return;
                }
            }
        }

        AppUsageDAO dao = new AppUsageDAO();

        HashMap<String, UserAppUsage> test = dao.retrieveUsageReport(startDate, endDate);

        HashMap<String, ArrayList<UserAppUsage>> catList = calculateAverageTime(test, start, end);

        int totalUsers = getTotalUsers(catList);
        //String order = "gender, school";

        if (order == null) {
            int[][] resultList = sortByFilter1(catList, null);
            request.setAttribute("result", resultList);
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("startDate", startDate);
            request.setAttribute("endDate", endDate);
            request.setAttribute("filter", 0);
            RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageResult.jsp");
            rd.forward(request, response);
            //response.sendRedirect("basicAppUsageResult.jsp");

        } else {
            String[] str = order.split(",");
            if (str.length == 1) {
                int[][] resultList = sortByFilter1(catList, order);
                request.setAttribute("result", resultList);
                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("filter", 1);
                RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageResult.jsp");
                rd.forward(request, response);
                //response.sendRedirect("basicAppUsageResult.jsp");
            }

            if (str.length == 2) {
                int[][][] resultList = sortByFilter2(catList, order);
                request.setAttribute("result", resultList);
                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("filter", 2);
                RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageResult.jsp");
                rd.forward(request, response);
                //response.sendRedirect("basicAppUsageResult.jsp");
            }
            if (str.length == 3) {
                int[][][][] resultList = sortByFilter3(catList, order);
                request.setAttribute("result", resultList);
                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("filter", 3);
                RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageResult.jsp");
                rd.forward(request, response);
                //response.sendRedirect("basicAppUsageResult.jsp");
            }
            if (str.length == 4) {
                int[][][][][] resultList = sortByFilter4(catList, order);
                request.setAttribute("result", resultList);
                request.setAttribute("totalUsers", totalUsers);
                request.setAttribute("startDate", startDate);
                request.setAttribute("endDate", endDate);
                request.setAttribute("filter", 4);
                RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageResult.jsp");
                rd.forward(request, response);
            }
        }
    }

    /**
     * Breakdown the result set by the 4 selected filters in the order of
     * selection.
     * <p>
     * Filters include Year/Gender/School/CCA
     *
     * @param catList the list of users and their respective app usage times
     * @param order the selected filters in their order (in the format
     * "year,gender,school,cca")
     * @return returns the resultant array
     */
    public static int[][][][][] sortByFilter4(HashMap<String, ArrayList<UserAppUsage>> catList, String order) {
        UserDAO userDAO = new UserDAO();
        HashMap<String, String[]> map = userDAO.retrieveFilters();

        String[] str = order.split(",");

        String filter1 = str[0].trim();
        String filter2 = str[1].trim();
        String filter3 = str[2].trim();
        String filter4 = str[3].trim();

        int[][][][][] resultList = new int[map.get(filter1).length + 1][map.get(filter2).length + 1][map.get(filter3).length + 1][map.get(filter4).length + 1][4];

        //iterate throught for loop from filter map
        String[] firstFilter = map.get(filter1);
        String[] secondFilter = map.get(filter2);
        String[] thirdFilter = map.get(filter3);
        String[] forthFilter = map.get(filter4);

        for (int i = 1; i <= firstFilter.length; i++) {
            String target = firstFilter[i - 1];
            int total3 = 0;
            for (int j = 1; j <= secondFilter.length; j++) {
                String target1 = secondFilter[j - 1];
                int total2 = 0;
                for (int k = 1; k <= thirdFilter.length; k++) {
                    String target2 = thirdFilter[k - 1];
                    int total = 0;
                    for (int m = 1; m <= forthFilter.length; m++) {
                        String target3 = forthFilter[m - 1];
                        for (Map.Entry<String, ArrayList<UserAppUsage>> entry : catList.entrySet()) {
                            String key = entry.getKey();
                            ArrayList<UserAppUsage> val = entry.getValue();

                            for (UserAppUsage u : val) {
                                if (u.getAttribute(filter1).equals(target) && u.getAttribute(filter2).equals(target1) && u.getAttribute(filter3).equals(target2) && u.getAttribute(filter4).equals(target3)) {
                                    if (key.equals("intense")) {
                                        resultList[i][j][k][m][1] += 1;
                                    } else if (key.equals("normal")) {
                                        resultList[i][j][k][m][2] += 1;
                                    } else if (key.equals("mild")) {
                                        resultList[i][j][k][m][3] += 1;
                                    }
                                }
                            }
                        }
                        int sum = resultList[i][j][k][m][1] + resultList[i][j][k][m][2] + resultList[i][j][k][m][3];
                        resultList[i][j][k][m][0] = sum;
                        total += sum;

                    }
                    resultList[i][j][k][0][0] = total;
                    total2 += total;
                }
                resultList[i][j][0][0][0] = total2;
                total3 += total2;
            }
            resultList[i][0][0][0][0] = total3;
        }

        return resultList;
    }

    /**
     * Breakdown the result set by the 3 selected filters in the order of
     * selection.
     * <p>
     * Filters include Year/Gender/School/CCA
     *
     * @param catList the list of users and their respective app usage times
     * @param order the selected filters in their order (in the format
     * "year,gender,cca")
     * @return returns the resultant array
     */
    public static int[][][][] sortByFilter3(HashMap<String, ArrayList<UserAppUsage>> catList, String order) {
        UserDAO userDAO = new UserDAO();
        HashMap<String, String[]> map = userDAO.retrieveFilters();

        //
        String[] str = order.split(",");

        String filter1 = str[0].trim(); //school
        String filter2 = str[1].trim(); //year
        String filter3 = str[2].trim(); //gender

        int[][][][] resultList = new int[map.get(filter1).length + 1][map.get(filter2).length + 1][map.get(filter3).length + 1][4];

        //iterate throught for loop from filter map
        String[] firstFilter = map.get(filter1);
        String[] secondFilter = map.get(filter2);
        String[] thirdFilter = map.get(filter3);

        for (int i = 1; i <= firstFilter.length; i++) {
            String target = firstFilter[i - 1];
            int total2 = 0;
            for (int j = 1; j <= secondFilter.length; j++) {
                String target2 = secondFilter[j - 1];
                int total = 0;
                for (int k = 1; k <= thirdFilter.length; k++) {
                    String target3 = thirdFilter[k - 1];

                    for (Map.Entry<String, ArrayList<UserAppUsage>> entry : catList.entrySet()) {
                        String key = entry.getKey();
                        ArrayList<UserAppUsage> val = entry.getValue();

                        for (UserAppUsage u : val) {
                            if (u.getAttribute(filter1).equals(target) && u.getAttribute(filter2).equals(target2) && u.getAttribute(filter3).equals(target3)) {
                                if (key.equals("intense")) {
                                    resultList[i][j][k][1] += 1;
                                } else if (key.equals("normal")) {
                                    resultList[i][j][k][2] += 1;
                                } else if (key.equals("mild")) {
                                    resultList[i][j][k][3] += 1;
                                }
                            }
                        }
                    }
                    int sum = resultList[i][j][k][1] + resultList[i][j][k][2] + resultList[i][j][k][3];
                    resultList[i][j][k][0] = sum;
                    total += sum;
                }
                resultList[i][j][0][0] = total;
                total2 += total;
            }
            resultList[i][0][0][0] = total2;
        }

        return resultList;
    }

    /**
     * Breakdown the result set by the 2 selected filters in the order of
     * selection.
     * <p>
     * Filters include Year/Gender/School/CCA
     *
     * @param catList  the list of users and their respective app usage times
     * @param order  the selected filters in their order (in the format
     * "year,gender")
     * @return  the resultant array
     */
    public static int[][][] sortByFilter2(HashMap<String, ArrayList<UserAppUsage>> catList, String order) {
        //get the filter
        int totalUsers = getTotalUsers(catList);

        UserDAO userDAO = new UserDAO();
        HashMap<String, String[]> map = userDAO.retrieveFilters();

        //ORDER CAN CHANGE FROM USER INPUT
        //order = "gender, year";
        String[] str = order.split(",");

        String filter1 = str[0].trim(); //gender
        String filter2 = str[1].trim(); //year

        int[][][] resultList = new int[map.get(filter1).length][map.get(filter2).length + 1][4];
        for (int i = 0; i < map.get(filter1).length; i++) { //gender , length :2
            String[] targetField = map.get(filter1);
            String target = targetField[i];

            int total = 0;
            for (int j = 0; j < map.get(filter2).length + 1; j++) {
                if (j != 0) {

                    String[] targetField2 = map.get(filter2); //year, length 5
                    String target2 = targetField2[j - 1];
                    for (Map.Entry<String, ArrayList<UserAppUsage>> entry : catList.entrySet()) {
                        String key = entry.getKey();
                        ArrayList<UserAppUsage> val = entry.getValue();

                        for (UserAppUsage u : val) {
                            //System.out.println(u.getAttribute(filter1) + " , " + u.getAttribute(filter2));
                            if (u.getAttribute(filter1).equals(target) && u.getAttribute(filter2).equals(target2)) {
                                if (key.equals("intense")) {
                                    resultList[i][j][1] += 1;

                                } else if (key.equals("normal")) {
                                    resultList[i][j][2] += 1;

                                } else if (key.equals("mild")) {
                                    resultList[i][j][3] += 1;

                                }

                            }
                        }
                        resultList[i][j][0] = resultList[i][j][1] + resultList[i][j][2] + resultList[i][j][3];
                    }
                    System.out.println(resultList[i][j][0] + ", " + total);
                    total += resultList[i][j][0];
                }
            }
            resultList[i][0][0] = total;
        }
        return resultList;
    }
    /**
     * Breakdown the result set by the selected filter.
     * <p>
     * Filters include Year/Gender/School/CCA
     *
     * @param catList  the list of users and their respective app usage times
     * @param order  the selected filters in their order (in the format
     * "year,gender")
     * @return  the resultant array
     */
    public static int[][] sortByFilter1(HashMap<String, ArrayList<UserAppUsage>> catList, String order) {
        //get the filter
        if (order == null) {
            int[][] resultList = new int[1][4];
            int total = 0;
            for (Map.Entry<String, ArrayList<UserAppUsage>> entry : catList.entrySet()) {
                String key = entry.getKey();
                ArrayList<UserAppUsage> value = entry.getValue();
                if (key.equals("intense")) {
                    resultList[0][1] = value.size();
                    total += value.size();
                } else if (key.equals("normal")) {
                    resultList[0][2] = value.size();
                    total += value.size();
                } else if (key.equals("mild")) {
                    resultList[0][3] = value.size();
                    total += value.size();
                }
            }
            resultList[0][0] = total;

            return resultList;
        } else {
            UserDAO userDAO = new UserDAO();
            HashMap<String, String[]> map = userDAO.retrieveFilters();

            String filter1 = order.trim();

            int[][] resultList = new int[map.get(filter1).length][4];
            for (int i = 0; i < map.get(filter1).length; i++) { //gender , length :2
                String[] targetField = map.get(filter1);
                String target = targetField[i];
                int totalPerStrata = 0;
                for (Map.Entry<String, ArrayList<UserAppUsage>> entry : catList.entrySet()) {
                    String key = entry.getKey();
                    ArrayList<UserAppUsage> val = entry.getValue();

                    for (UserAppUsage u : val) {
                        if (u.getAttribute(filter1).equals(target)) {
                            if (key.equals("intense")) {
                                resultList[i][1] += 1;
                                totalPerStrata += 1;
                            } else if (key.equals("normal")) {
                                resultList[i][2] += 1;
                                totalPerStrata += 1;
                            } else if (key.equals("mild")) {
                                resultList[i][3] += 1;
                                totalPerStrata += 1;
                            }
                        }
                    }
                }
                resultList[i][0] = totalPerStrata;

            }
            return resultList;
        }
    }
/**
 * Represents the breakdown value as a percentage of the of the total users in the query period.
 * @param value  the specified breakdown value
 * @param totalUsers  the total number of users
 * @return 
 */
    public static long calculatePercentage(int value, int totalUsers) {
        double percentage = ((double) value / totalUsers) * 100;

        return Math.round(percentage);
    }
/**
 * Retrieves the total users within the query period.
 * @param list  the list containing all the users and their respective app time usage
 * @return  the number of users
 */
    public static int getTotalUsers(HashMap<String, ArrayList<UserAppUsage>> list) {
        int totalUsers = 0;

        for (Map.Entry<String, ArrayList<UserAppUsage>> entry : list.entrySet()) {

            ArrayList<UserAppUsage> val = entry.getValue();
            for (UserAppUsage u : val) {
                totalUsers += 1;
            }

        }
        return totalUsers;
    }
/**
 * Retrieves the breakdown of users by their usage pattern.
 * <p>If a user's total usage time within the query period is less than 1 hr, he is considered a 'mild' user. If the usage time is more
 * than 1 hr but within 5 hrs, he is considered a 'normal user'. Else, he is classified as an 'intense' user.
 * @param list  the list of users and their app usage times
 * @param startDate  the start date of query period
 * @param endDate  the end date of query period
 * @return  the list containing the category type and the respective users 
 */
    public static HashMap<String, ArrayList<UserAppUsage>> calculateAverageTime(HashMap<String, UserAppUsage> list, Date startDate, Date endDate) {
        //userTimeLog parsed i has key: mac-address, value: total usage time
        //get difference in days

        int noOfDays = 0;
        while (startDate.compareTo(endDate) < 0) {
            startDate = DateFormatter.addDays(startDate, 1);
            noOfDays++;
        }

        int[][] resultSet = new int[3][2]; // assume index 0: intense, 1: normal, 2: mild  
        HashMap<String, ArrayList<UserAppUsage>> resultList = new HashMap<>();

        //iterate through the hashmap by mac-address (individual users) and check if average time is intense, normal, mild, 
        for (Map.Entry<String, UserAppUsage> entry : list.entrySet()) {

            long totalUserTime = entry.getValue().getTotalTime();
            long averageTime = totalUserTime / noOfDays;

            if (averageTime < 3600) { // if Avg Time less than 1hr
                if (resultList.get("mild") == null) {
                    ArrayList<UserAppUsage> uap = new ArrayList<>();
                    uap.add(entry.getValue());
                    resultList.put("mild", uap);
                } else {
                    ArrayList<UserAppUsage> uap = resultList.get("mild");
                    uap.add(entry.getValue());
                    resultList.put("mild", uap);
                }

            } else if (averageTime >= 3600 && averageTime < 18000) { // if Avg Time < 5hrs & > 1hr
                if (resultList.get("normal") == null) {
                    ArrayList<UserAppUsage> uap = new ArrayList<>();
                    uap.add(entry.getValue());
                    resultList.put("normal", uap);
                } else {
                    ArrayList<UserAppUsage> uap = resultList.get("normal");
                    uap.add(entry.getValue());
                    resultList.put("normal", uap);
                }
            } else { //more than 5 hours
                if (resultList.get("intense") == null) {
                    ArrayList<UserAppUsage> uap = new ArrayList<>();
                    uap.add(entry.getValue());
                    resultList.put("intense", uap);
                } else {
                    ArrayList<UserAppUsage> uap = resultList.get("intense");
                    uap.add(entry.getValue());
                    resultList.put("intense", uap);
                }
            }
        }

        return resultList;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
