package Json;

import static Servlets.ProcessTopkSchool.givenappCategoryAllschool;
import static Servlets.ProcessTopkSchool.rankResutl;
import static Servlets.ProcessTopkSchool.topkschool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dao.AppUsageDAO;
import entity.TopkUsage;
import entity.TopkUsageResult;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.DateFormatter;

@WebServlet(name = "topkmostusedschools", urlPatterns = {"/json/top-k-most-used-schools"})
public class topkmostusedschools extends HttpServlet {

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

        String startdate = request.getParameter("startdate");
        String enddate = request.getParameter("enddate");
        String appcategory = request.getParameter("appcategory");
        String token = request.getParameter("token");
        String kvalue = request.getParameter("k");

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = new JsonObject();

        TreeSet<String> errorMsg = new TreeSet<String>();
        boolean commonValidation = true;
        int topk = 3;

        //------------------------ Common validation
        //start date 
        if (startdate == null) {
            errorMsg.add("missing startdate");
            commonValidation = false;
        } else if (startdate.isEmpty()) {
            errorMsg.add("blank startdate");
            commonValidation = false;
        }

        //end date
        if (enddate == null) {
            errorMsg.add("missing enddate");
            commonValidation = false;
        } else if (enddate.isEmpty()) {
            errorMsg.add("blank enddate");
            commonValidation = false;
        }

        //appcategory
        if (appcategory == null) {
            errorMsg.add("missing app category");
            commonValidation = false;
        } else if (appcategory.isEmpty()) {
            errorMsg.add("blank app category");
            commonValidation = false;
        }

        //token
        String validatedUser = null;
        if (token == null) {
            errorMsg.add("missing token");
            commonValidation = false;
        } else if (token.isEmpty()) {
            errorMsg.add("blank token");
            commonValidation = false;
        } else {
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

        //k value
        if (kvalue != null) {
            if (kvalue.isEmpty()) {
                errorMsg.add("blank k");
                commonValidation = false;
            }
        }

        boolean allValid = true;

        // -------------------- 2nd validation
        if (commonValidation) {

            ArrayList<String> appcategoryList = new ArrayList<String>(Arrays.asList("Books", "Social", "Education", "Entertainment", "Information", "Library", "Local", "Tools", "Fitness", "Games", "Others"));

            int counter = 0;
            for (int i = 0; i < appcategoryList.size(); i++) {
                if (appcategory.equalsIgnoreCase(appcategoryList.get(i))) {
                    counter++;
                }
            }

            if (counter == 0) {
                errorMsg.add("invalid app category");
                allValid = false;
            }

            if (!DateFormatter.isDateValid(enddate)) {
                errorMsg.add("invalid enddate");
                allValid = false;
            }

            if (!DateFormatter.isDateValid(startdate)) {
                errorMsg.add("invalid startdate");
                allValid = false;
            } else { //check if start date is before end date
                String startdate2 = startdate + " 00:00:00";
                String enddate2 = enddate + " 23:59:59";
                Date start = DateFormatter.stringToDate(startdate2);
                Date end = DateFormatter.stringToDate(enddate2);
                if (!start.before(end)) {
                    errorMsg.add("invalid startdate");
                    allValid = false;
                }
            }

            if (kvalue != null) {
                int k = Integer.parseInt(kvalue);
                if (0 < k && k < 11) {
                    topk = k;
                } else {
                    errorMsg.add("invalid k");
                    allValid = false;
                }
            }
        }
        if (commonValidation && allValid) {
            startdate = startdate + " 00:00:00";
            enddate = enddate + " 23:59:59";

            AppUsageDAO dao = new AppUsageDAO();

            HashMap<String, TopkUsage> test = dao.retrieveTopkschoolreport(startdate, enddate);
            HashMap<String, Long> allschool = givenappCategoryAllschool(test, appcategory);
            LinkedHashMap<String, Long> topkschool = topkschool(allschool);
            LinkedHashMap<Integer, TopkUsageResult> finalresult = rankResutl(topkschool, topk);

            JsonArray result = new JsonArray();

            for (Map.Entry<Integer, TopkUsageResult> entry : finalresult.entrySet()) {
                // a list of school name
                ArrayList<String> schoolList = entry.getValue().getResultName();

                for (int i = 0; i < schoolList.size(); i++) {
                    if (entry.getValue().getUsageTime() != 0) {
                        // rank
                        int key = entry.getKey();

                        //usage time
                        long usagetime = entry.getValue().getUsageTime();

                        JsonObject subArr = new JsonObject();
                        Collections.sort(schoolList);
                        subArr.addProperty("rank", key);
                        subArr.addProperty("school", schoolList.get(i));
                        subArr.addProperty("duration", usagetime);
                        result.add(subArr);
                    }
                }

            }

            obj.addProperty("status", "success");
            obj.add("results", result);

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
