package Json;

import static Servlets.ProcessBasicAppUsage.calculateAverageTime;
import static Servlets.ProcessBasicAppUsage.getTotalUsers;
import static Servlets.ProcessBasicAppUsage.sortByFilter1;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dao.AppUsageDAO;
import entity.UserAppUsage;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.DateFormatter;

@WebServlet(name = "basicUseTimeReport", urlPatterns = {"/json/basic-usetime-report"})
public class basicUseTimeReport extends HttpServlet {

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
        response.setContentType("application/json");

        TreeSet<String> errorMsg = new TreeSet<String>();

        String token = request.getParameter("token");
        String startDate = request.getParameter("startdate");
        String endDate = request.getParameter("enddate");

        JsonObject obj = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        boolean commonValidation = true;
        //check if inputs are missing : common validation
        if (token == null) {
            errorMsg.add("missing token");
            commonValidation = false;
        } else {
            if (token.equals("")) {
                errorMsg.add("blank token");
                commonValidation = false;
            } else {
                String validatedUser = null;
                try {
                    validatedUser = JWTUtility.verify(token, sharedSecret.get());
                    if (validatedUser == null) {
                        errorMsg.add("invalid token");
                        commonValidation = false;
                    }
                } catch (JWTException e) {
                    errorMsg.add("invalid token");
                    commonValidation = false;
                }
            }
        }

        if (startDate == null) {
            errorMsg.add("missing startdate");
            commonValidation = false;
        } else {
            if (startDate.equals("")) {
                errorMsg.add("blank startdate");
                commonValidation = false;
            }
        }

        if (endDate == null) {
            errorMsg.add("missing enddate");
            commonValidation = false;
        } else {
            if (endDate.equals("")) {
                errorMsg.add("blank enddate");
                commonValidation = false;
            }
        }

        boolean allValid = true;
        //specific validation
        if (commonValidation) {

            if (!DateFormatter.isDateValid(endDate)) {
                errorMsg.add("invalid enddate");
                allValid = false;
            }
            //check validaty of date parsed in
            if (!DateFormatter.isDateValid(startDate)) {
                errorMsg.add("invalid startdate");
                allValid = false;
            } else { //check if start date is before end date
                startDate = startDate + " 00:00:00";
                endDate = endDate + " 23:59:59";
                Date start = DateFormatter.stringToDate(startDate);
                Date end = DateFormatter.stringToDate(endDate);
                if (!start.before(end)) {
                    errorMsg.add("invalid startdate");
                    allValid = false;
                }
            }

        }

        if (commonValidation && allValid) {
            //if all input fields are valid
            startDate = startDate + " 00:00:00";
            endDate = endDate + " 23:59:59";
            Date start = DateFormatter.stringToDate(startDate);
            Date end = DateFormatter.stringToDate(endDate);

            AppUsageDAO dao = new AppUsageDAO();

            HashMap<String, UserAppUsage> test = dao.retrieveUsageReport(startDate, endDate);
            HashMap<String, ArrayList<UserAppUsage>> catList = calculateAverageTime(test, start, end);
            int totalUsers = getTotalUsers(catList);
            int[][] resultList = sortByFilter1(catList, null);

            JsonArray result = new JsonArray();

            //iterate through resultList and get values
            for (int i = 0; i < resultList.length; i++) {
                for (int j = 1; j < resultList[i].length; j++) {
                    JsonObject subArr = new JsonObject();
                    int value = resultList[i][j];
                    double percentage = ((double) value / totalUsers) * 100;
                    long num = Math.round(percentage);

                    switch (j) {
                        case 1:
                            subArr.addProperty("intense-count", value);
                            subArr.addProperty("intense-percent", num);
                            break;
                        case 2:
                            subArr.addProperty("normal-count", value);
                            subArr.addProperty("normal-percent", num);
                            break;
                        case 3:
                            subArr.addProperty("mild-count", value);
                            subArr.addProperty("mild-percent", num);
                            break;
                    }
                 
                    result.add(subArr);

                }
            }

            obj.addProperty("status", "success");
            obj.add("breakdown", result);

        } else {
            if (errorMsg.size() > 0) { // errors returned
                JsonArray errorList = new JsonArray();
                //add error messages in to errorList
                Iterator<String> iter = errorMsg.iterator();
                while (iter.hasNext()) {
                    errorList.add(new JsonPrimitive(iter.next()));
                }

                obj.addProperty("status", "error");
                obj.add("message", errorList);
            }
        }

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println(gson.toJson(obj));

        }

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
