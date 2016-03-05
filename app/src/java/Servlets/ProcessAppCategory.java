package Servlets;

import dao.AppUsageDAO;
import entity.UserAppUsage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ProcessAppCategory", urlPatterns = {"/ProcessAppCategory"})
/**
 * Process basic APP usage report breakdown by app category
 */
public class ProcessAppCategory extends HttpServlet {

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

        String startDate = (String) request.getParameter("startdate");
        String endDate = (String) request.getParameter("enddate");

        if (startDate.equals("") || endDate.equals("")) {
            RequestDispatcher rd = request.getRequestDispatcher("basicAppCategory.jsp");
            request.setAttribute("errorMsg", "Start Date/End Date cannot be blank!");
            rd.forward(request, response);
            return;
        }

        if (startDate == null && endDate == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        //testing
        String newEndDate = endDate + " 00:00:00";

        startDate = startDate + " 00:00:00";
        endDate = endDate + " 23:59:59";

        //testing
        Date newEndDate2 = utilities.DateFormatter.stringToDate(newEndDate);

        Date start = utilities.DateFormatter.stringToDate(startDate);
        Date end = utilities.DateFormatter.stringToDate(endDate);

        Date newEnd = utilities.DateFormatter.addDays(newEndDate2, 1);
        String newEnd2 = utilities.DateFormatter.formatDate(newEnd);

        if (start.compareTo(end) > 0) {
            RequestDispatcher rd = request.getRequestDispatcher("basicAppCategory.jsp");
            request.setAttribute("errorMsg", "End date cannot be earlier than start date!");
            rd.forward(request, response);
            return;
        } else {
            AppUsageDAO dao = new AppUsageDAO();
            HashMap<String, UserAppUsage> resultList = dao.retrieveAppCategoryUsageReport(startDate, endDate);
            HashMap<String, Long> combine = combineAllUsersAppCategory(resultList);

            LinkedHashMap<String, Double> result = calculateAppAverageTime(combine, start, end);
            ArrayList<Double> percentage = calculateAppPercentage(result);

            request.setAttribute("result", result);
            request.setAttribute("percentage", percentage);
            request.setAttribute("startDate", startDate);
            RequestDispatcher rd = request.getRequestDispatcher("basicAppCategoryResult.jsp");
            rd.forward(request, response);
        }
    }
    
    /**
     * For each APP category, process average daily usage percentage of the total (average duration per category / the sum of the average duration across all categories)
     * @param list  Linkedhashmap, key is APP category name, value is its average usage time 
     * @return  daily usage percentage for each APP category
     */

    public static ArrayList<Double> calculateAppPercentage(LinkedHashMap<String, Double> list) {
        ArrayList<Double> result = new ArrayList<>();
        double total = 0.0;
        for (Map.Entry<String, Double> entry : list.entrySet()) {
            total += entry.getValue();
        }
        for (Map.Entry<String, Double> entry2 : list.entrySet()) {
            double percentage = (entry2.getValue() * 100.0) / total;
            result.add(percentage);
        }
        return result;
    }
    
    /**
     * For each APP category, process average daily APP usage time
     * Based on start date and end date, calculate the number of days
     * Loop through different APP categories, total usage time / number of days, get the average usage time 
     * @param list HashMap, key is APP category name, value is its total usage time
     * @param startDate user input start date, time 00:00:00
     * @param endDate user input end date, time 23:59:59
     * @return LinkedHashMap, key is APP category name, value is its average usage time
     */

    public static LinkedHashMap calculateAppAverageTime(HashMap<String, Long> list, Date startDate, Date endDate) {
        int noOfDays = 0;
        while (startDate.compareTo(endDate) < 0) {
            startDate = utilities.DateFormatter.addDays(startDate, 1);
            noOfDays++;
        }
        LinkedHashMap<String, Double> resultList = new LinkedHashMap<>();
        for (Map.Entry<String, Long> entry : list.entrySet()) {
            //testing
            System.out.println(">>>>>>>>>>>>>>" + entry.getValue());

            double newValue = entry.getValue() * 1.0 / noOfDays;

            System.out.println(">>>>>>>>>>>>>>" + newValue);
            resultList.put(entry.getKey(), newValue);
        }

        return resultList;
    }
    /**
     * Combine all target users" APP usage time
     * Create 11 (different APP categories) HashMap, key is APP category name, value is its total usage time
     * Loop through all target users and put their usage time in corresponding APP category
     * @param list HashMap, key is mac-address, value is an UserAppUsage object
     * @return HashMap, key is APP category name, value is its total usage time
     */

    public static HashMap<String, Long> combineAllUsersAppCategory(HashMap<String, UserAppUsage> list) {
        String[] appCategory = {"Books", "Social", "Education", "Entertainment", "Information", "Library", "Local", "Tools", "Fitness", "Games", "Others"};

        HashMap<String, Long> resultList = new HashMap<>();

        long l = 0;
        for (int j = 0; j < appCategory.length; j++) {
            resultList.put(appCategory[j], l);
        }
        for (Map.Entry<String, UserAppUsage> entry : list.entrySet()) {
            HashMap<String, Long> result = entry.getValue().getTotalUsageTimeByApp();

            for (int i = 0; i < appCategory.length; i++) {
                if (result.get(appCategory[i]) != null) {
                    if (resultList.get(appCategory[i]) == 0) {
                        resultList.put(appCategory[i], result.get(appCategory[i]));

                    } else {

                        long usageTime = resultList.get(appCategory[i]) + result.get(appCategory[i]);
                        resultList.put(appCategory[i], usageTime);
                    }
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
