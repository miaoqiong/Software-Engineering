package Servlets;

import dao.AppUsageDAO;
import entity.DiurnalAppUsage;
import java.io.IOException;
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

@WebServlet(name = "ProcessDiurnal", urlPatterns = {"/ProcessDiurnal"})
/**
 * Process basic APP usage report breakdown by diurnal pattern of APP usage time
 */
public class ProcessDiurnal extends HttpServlet {

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
        String startDate = (String) request.getParameter("startdate");

        if (startDate.equals("")) {
            RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageDiurnal.jsp");
            request.setAttribute("errorMsg", "The date cannot be blank!");
            rd.forward(request, response);
            return;
        }
        String starttime = startDate + " 00:00:00";

        String year = (String) request.getParameter("year");
        String gender = (String) request.getParameter("gender");
        String school = (String) request.getParameter("school");

        if (year.equals("a") && gender.equals("a") && school.equals("a")) {
            LinkedHashMap<Integer, Integer> resultList = diurnalReport(starttime);
            request.setAttribute("result", resultList);
            request.setAttribute("startDate", startDate);
            request.setAttribute("statement", "testing purpose");
            RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageDiurnalResult.jsp");
            rd.forward(request, response);
            return;

        } else {
            String yearSelect = " and email LIKE '%" + year + "%' ";
            String genderSelect = " and gender = '" + gender + "' ";
            String schoolSelect = " and email LIKE '%" + school + "%' ";
            String stmt = "";
            
            if (!year.equals("a")) {
                yearSelect = yearSelect.toLowerCase();
                stmt = stmt + yearSelect;
            }

            if (!gender.equals("a")) {
                genderSelect = genderSelect.toLowerCase();
                stmt = stmt + genderSelect;
            }

            if (!school.equals("a")) {
                schoolSelect = schoolSelect.toLowerCase();
                stmt = stmt + schoolSelect;
            }

            LinkedHashMap<Integer, Integer> resultList = diurnalReport2(starttime, stmt);
            request.setAttribute("filter", "filter");
            request.setAttribute("statement", stmt);
            request.setAttribute("result", resultList);
            request.setAttribute("startDate", startDate);
            RequestDispatcher rd = request.getRequestDispatcher("basicAppUsageDiurnalResult.jsp");
            rd.forward(request, response);
        }

    }
    /**
     * Process diurnal pattern of average APP usage time given a specific day with filters 
     * @param startTime input the specified day
     * @param order filters that user specified
     * @return LinkedHashMap, key is hourly basis name, value is averaged APP usage time
     */
    public static LinkedHashMap<Integer, Integer> diurnalReport2(String startTime, String order) {
        LinkedHashMap<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        AppUsageDAO dao = new AppUsageDAO();

        Date first = utilities.DateFormatter.stringToDate(startTime);
        Date next = utilities.DateFormatter.addHour(first);

        Date nextDay = utilities.DateFormatter.addDays(first, 1);
        HashMap<String, DiurnalAppUsage> totaluser = dao.retrieveDiurnalFilter(first, nextDay, order);
        int num = totaluser.size();

        for (int i = 1; i < 25; i++) {
            HashMap<String, DiurnalAppUsage> retrieveuser = dao.retrieveDiurnalFilter(first, next, order);

   
            long total = 0;
            String nextString = utilities.DateFormatter.formatDate(next);
            for (Map.Entry<String, DiurnalAppUsage> entry : retrieveuser.entrySet()) {
                total = total + entry.getValue().getTotalUsageTime(entry.getValue().getTimeStamps(), nextString);
            }
            first = next;
            next = utilities.DateFormatter.addHour(next);

            double average = (total * 1.0) / num;
            long averageTime = Math.round(average);
            result.put(i, (int) averageTime);
        }

        return result;
    }

    /**
     * Process diurnal pattern of average APP usage time given a specific day without filters
     * @param startTime input the specified day
     * @return LinkedHashMap, key is hourly basis name, value is averaged APP usage time
     */
    public static LinkedHashMap<Integer, Integer> diurnalReport(String startTime) {
        LinkedHashMap<Integer, Integer> result = new LinkedHashMap<Integer, Integer>();
        AppUsageDAO dao = new AppUsageDAO();

        Date first = utilities.DateFormatter.stringToDate(startTime);
        Date next = utilities.DateFormatter.addHour(first);

        Date nextDay = utilities.DateFormatter.addDays(first, 1);
        HashMap<String, DiurnalAppUsage> totaluser = dao.retrieveDiurnalReport(first, nextDay);
        int num = totaluser.size();

        for (int i = 1; i < 25; i++) {
            HashMap<String, DiurnalAppUsage> retrieveuser = dao.retrieveDiurnalReport(first, next);

            long total = 0;
            String nextString = utilities.DateFormatter.formatDate(next);
            for (Map.Entry<String, DiurnalAppUsage> entry : retrieveuser.entrySet()) {
                total = total + entry.getValue().getTotalUsageTime(entry.getValue().getTimeStamps(), nextString);
            }
            first = next;
            next = utilities.DateFormatter.addHour(next);

            double average = (total * 1.0) / num;
            long averageTime = Math.round(average);

            result.put(i, (int) averageTime);

        }

        return result;
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
