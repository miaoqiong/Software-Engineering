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

@WebServlet(name = "ProcessTopkStudent", urlPatterns = {"/ProcessTopkStudent"})
/**
 * Process Top-k students with most used APPs (given an APP category)
 */
public class ProcessTopkStudent extends HttpServlet {

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
        String appCategory = request.getParameter("appCategory");
        if (startDate == null && endDate == null && kValue == null && appCategory == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if (!kValue.equals("a") && !appCategory.equals("a")) {
            startDate += " 00:00:00";
            endDate += " 23:59:59";
            Date start = DateFormatter.stringToDate(startDate);
            Date end = DateFormatter.stringToDate(endDate);
            if (start.compareTo(end) > 0) {
                RequestDispatcher rd = request.getRequestDispatcher("topkAppUsageReportStudent.jsp");
                request.setAttribute("errormessage", "End date cannot be earlier than start date!");
                rd.forward(request, response);
                return;
            } else {
                int topk = Integer.parseInt(kValue);
                AppUsageDAO dao = new AppUsageDAO();
                HashMap<String, TopkUsage> test = dao.retrieveTopkstudentreport(startDate, endDate);
                HashMap<String, Long> allstudent = givenappCategoryAllstudent(test, appCategory);
                LinkedHashMap<String, Long> topkstudent = topkstudent(allstudent);

                LinkedHashMap<Integer, TopkUsageResult> finalresult = rankResutl(topkstudent, topk);
                RequestDispatcher rd = request.getRequestDispatcher("topkStudentResult.jsp");
                request.setAttribute("result", finalresult);
                request.setAttribute("k", kValue);
                rd.forward(request, response);
                return;
            }
        } else {
            String errormessage = "Please input K value/App Category";
            request.setAttribute("errormessage", errormessage);
            RequestDispatcher rd = request.getRequestDispatcher("topkAppUsageReportStudent.jsp");
            rd.forward(request, response);
            return;
        }

    }

    /**
     * Process individual student total APP (given an APP category) usage time
     * @param list HashMap, key is student mac-address, value is TopkUsage object
     * @param appCategory APP category that user specified
     * @return HashMap, key is student mac-address and name, value is total APP usage time
     */
    public static HashMap<String, Long> givenappCategoryAllstudent(HashMap<String, TopkUsage> list, String appCategory) {

        HashMap<String, Long> resultList = new HashMap<>();

        for (Map.Entry<String, TopkUsage> entry : list.entrySet()) {
            String macaddress = entry.getKey();
            String studentname = entry.getValue().getStudentname();
            String newKey = macaddress + "," + studentname;
            HashMap<String, Long> result = entry.getValue().getTotalUsageTimeByAppCate();

            for (Map.Entry<String, Long> entry2 : result.entrySet()) {
                if (entry2.getKey().equalsIgnoreCase(appCategory)) {
                    resultList.put(newKey, entry2.getValue());
                }
            }
        }
        return resultList;
    }
    /**
     * Process top 10 students based on the result of total APP usage time
     * @param list HashMap, key is student mac-address and name, value is total APP usage time
     * @return LinkedHashMap, key is school student mac-address and name, value is total APP usage time
     */

    public static LinkedHashMap topkstudent(HashMap<String, Long> list) {
        LinkedHashMap<String, Long> resultList = new LinkedHashMap<String, Long>();

        int listSize = list.size();

        if (listSize >= 10) {
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
        } else {
            for (int i = 1; i < listSize; i++) {
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

        }

        return resultList;
    }
    /**
     * Process ranking for top k students in the given way
     * @param list LinkedHashMap, key is school student mac-address and name, value is total APP usage time
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
        int listSize = list.size();

        if (listSize >= k) {

            for (Map.Entry<Long, ArrayList<String>> entry : sortedlist.entrySet()) {
                TopkUsageResult tur = new TopkUsageResult(entry.getValue(), entry.getKey());
                int length = tur.getResultName().size();
                resultList.put(counter, tur);
                counter = counter + length;
                if (counter > k) {
                    break;
                }
            }
        } else {
            for (Map.Entry<Long, ArrayList<String>> entry : sortedlist.entrySet()) {
                TopkUsageResult tur = new TopkUsageResult(entry.getValue(), entry.getKey());
                int length = tur.getResultName().size();
                resultList.put(counter, tur);
                counter = counter + length;
                if (counter == listSize) {
                    break;
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
