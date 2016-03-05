package Servlets;

import dao.AppUsageDAO;
import entity.TopkUsage;
import entity.TopkUsageResult;
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
import utilities.DateFormatter;

@WebServlet(name = "ProcessTopkUsageReport", urlPatterns = {"/ProcessTopkUsageReport"})
/**
 * Process Top-k most used APPs (given a school)
 */
public class ProcessTopkUsageReport extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        String startDate = request.getParameter("startdate");
        String endDate = request.getParameter("enddate");

        String kValue = request.getParameter("kvalue");
        String school = request.getParameter("school");

        if (startDate == null && endDate == null && kValue == null && school == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (!kValue.equals("a") && !school.equals("a")) {
            startDate += " 00:00:00";
            endDate += " 23:59:59";
            Date start = DateFormatter.stringToDate(startDate);
            Date end = DateFormatter.stringToDate(endDate);
            if (start.compareTo(end) > 0) {
                RequestDispatcher rd = request.getRequestDispatcher("topkAppUsageReport.jsp");
                request.setAttribute("errormessage", "End date cannot be earlier than start date!");
                rd.forward(request, response);
                return;
            } else {
                int topk = Integer.parseInt(kValue);

                String email = "email LIKE '%" + school + "%'";
                AppUsageDAO dao = new AppUsageDAO();
                HashMap<String, TopkUsage> test = dao.retrieveTopkbyschoolreport(startDate, endDate, email);
                HashMap<String, Long> allUsersapptimelist = combineAllusersappName(test);
                LinkedHashMap<String, Long> toptenRecord = sortHashMapByValues(allUsersapptimelist);
                LinkedHashMap<Integer, TopkUsageResult> finalresult = rankResutl(toptenRecord, topk);

                RequestDispatcher rd = request.getRequestDispatcher("topkResult.jsp");
                request.setAttribute("result", finalresult);
                request.setAttribute("k", kValue);
                rd.forward(request, response);
            }
        } else {
            String errormessage = "Please input K value/School";
            request.setAttribute("errormessage", errormessage);
            RequestDispatcher rd = request.getRequestDispatcher("topkAppUsageReport.jsp");
            rd.forward(request, response);
            return;
        }

    }

    /**
     * Process most used APP usage time (given a school) 
     * @param list  HashMap, key is student mac-address, value is TopkUsage object
     * @return HashMap, key is APP name, value is total usage time
     */
    public static HashMap<String, Long> combineAllusersappName(HashMap<String, TopkUsage> list) {

        HashMap<String, Long> resultList = new HashMap<>();

        //for all users
        for (Map.Entry<String, TopkUsage> entry : list.entrySet()) {
            //for each user return a hashmap
            //key is the app name and value is the total time he use for that app
            HashMap<String, Long> result = entry.getValue().getTotalUsageTimeByApp();
            //for each user:
            for (Map.Entry<String, Long> entry2 : result.entrySet()) {
                String appname = entry2.getKey();
                if (resultList.get(appname) == null) {
                    resultList.put(appname, entry2.getValue());
                } else {
                    long usageTime = resultList.get(appname) + entry2.getValue();
                    resultList.put(appname, usageTime);
                }
            }
        }
        System.out.println(resultList.size());
        return resultList;
    }
    /**
     * Process top 10 APPs based on the result of total usage time
     * @param list HashMap, key is APP name, value is total usage time
     * @return LinkedHashMap, key is APP name, value is total usage time
     */

    public static LinkedHashMap sortHashMapByValues(HashMap<String, Long> list) {
        LinkedHashMap<String, Long> resultList = new LinkedHashMap<String, Long>();
        for (int i = 1; i < 11; i++) {
            long counter = 0;
            String newKey = "";
            long newValue = 0;
            for (Map.Entry<String, Long> entry : list.entrySet()) {
                String key = entry.getKey();
                long value = entry.getValue();
                if (counter <= value) {
                    newKey = key;
                    newValue = value;
                    counter = value;
                }
            }
            list.remove(newKey);
            resultList.put(newKey, newValue);
        }
        return resultList;

    }
    /**
     * Process ranking for top k APPs in the given way
     * @param list LinkedHashMap, key is APP name, value is total usage time
     * @param k user input value, range from 1 to 10 (both inclusive, in integer increments)
     * @return LinkedHashMap, key is rank, value is a TopkUsageResult object
     */

    public static LinkedHashMap rankResutl(LinkedHashMap<String, Long> list, int k) {
        //key is the rank and value is an object (many v 1)
        LinkedHashMap<Integer, TopkUsageResult> resultList = new LinkedHashMap<Integer, TopkUsageResult>();

        LinkedHashMap<Long, ArrayList<String>> sortedlist = new LinkedHashMap<Long, ArrayList<String>>();

        for (Map.Entry<String, Long> entry : list.entrySet()) {
            long a = entry.getValue();
            String b = entry.getKey();
            if (sortedlist.get(a) == null) {
                ArrayList<String> usagetimes = new ArrayList<String>();
                usagetimes.add(b);
                sortedlist.put(a, usagetimes);
            } else {
                sortedlist.get(a).add(b);
            }
        }

        int counter = 1;

        for (Map.Entry<Long, ArrayList<String>> entry : sortedlist.entrySet()) {
            TopkUsageResult tur = new TopkUsageResult(entry.getValue(), entry.getKey());
            int length = tur.getResultName().size();
            resultList.put(counter, tur);
            counter = counter + length;
            if (counter > k) {
                break;
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
