package Json;

import Servlets.ProcessDiurnal;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dao.UserDAO;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet(name = "basicDiurnalPatternReport", urlPatterns = {"/json/basic-diurnalpattern-report"})
public class basicDiurnalPatternReport extends HttpServlet {

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
        String date = request.getParameter("date");
        String yearFilter = request.getParameter("yearfilter");
        String genderFilter = request.getParameter("genderfilter");
        String schoolFilter = request.getParameter("schoolfilter");

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

        if (date == null) {
            errorMsg.add("missing date");
            commonValidation = false;
        } else {
            if (date.equals("")) {
                errorMsg.add("blank date");
                commonValidation = false;
            }
        }

        if (yearFilter == null) {
            errorMsg.add("missing year filter");
            commonValidation = false;

        } else {
            if (yearFilter.equals("")) {
                errorMsg.add("blank year filter");
                commonValidation = false;
            }
        }

        if (genderFilter == null) {
            errorMsg.add("missing gender filter");
            commonValidation = false;

        } else {
            if (genderFilter.equals("")) {
                errorMsg.add("blank gender filter");
                commonValidation = false;
            }
        }

        if (schoolFilter == null) {
            errorMsg.add("missing school filter");
            commonValidation = false;

        } else {
            if (schoolFilter.equals("")) {
                errorMsg.add("blank school filter");
                commonValidation = false;
            }
        }

        //specific validation
        boolean specValid = true;
        if (commonValidation) {

            //check if filters are valid
            HashMap<String, String[]> filterList = UserDAO.retrieveFilters();

            if (yearFilter != null && !yearFilter.equalsIgnoreCase("NA")) {
                String[] yearList = filterList.get("year");
                boolean contains = false;
                for (String s : yearList) {
                    if (s.equals(yearFilter)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    errorMsg.add("invalid year filter");
                    specValid = false;
                }
            }
            if (genderFilter != null && !genderFilter.equalsIgnoreCase("NA")) {
                String[] genderList = filterList.get("gender");
                boolean contains = false;

                genderFilter = genderFilter.toLowerCase();
                for (String s : genderList) {
                    s = s.toLowerCase();
                    if (s.equals(genderFilter)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    errorMsg.add("invalid gender filter");
                    specValid = false;
                }
            }
            if (schoolFilter != null && !schoolFilter.equalsIgnoreCase("NA")) {
                String[] schoolList = filterList.get("school");
                boolean contains = false;

                schoolFilter = schoolFilter.toLowerCase();
                for (String s : schoolList) {
                    if (s.equals(schoolFilter)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    errorMsg.add("invalid school filter");
                    specValid = false;
                }
            }

            //check if date is valid
            if (!DateFormatter.isDateValid(date)) {
                errorMsg.add("invalid date");
                specValid = false;
            }

        }
        //if inputs pass both common and special validation
        if (commonValidation && specValid) {
            JsonArray result = new JsonArray();
            //if no filters selected
            if (yearFilter == null && genderFilter == null && schoolFilter == null) {
                String startDate = date + " 00:00:00";
                LinkedHashMap<Integer, Integer> resultList = ProcessDiurnal.diurnalReport(startDate);

                for (Map.Entry<Integer, Integer> entry : resultList.entrySet()) {
                    JsonObject eachEntry = new JsonObject();
                    int key = entry.getKey();
                    int value = entry.getValue();
                    int beforeKey = key - 1;
                    String lowerLimit = "" + beforeKey + ":00";
                    String upperLimit = "" + key + ":00";

                    if (beforeKey < 10) {
                        lowerLimit = "0" + lowerLimit;
                    }
                    if (key < 10) {
                        upperLimit = "0" + upperLimit;
                    }

                    String time = lowerLimit + " --- " + upperLimit;

                    eachEntry.addProperty("period", time);
                    eachEntry.addProperty("duration", value);

                    result.add(eachEntry);
                }

            } else { //filters selected 
                String yearSelect = " and email LIKE '%" + yearFilter + "%' ";
                String genderSelect = " and gender = '" + genderFilter + "' ";
                String schoolSelect = " and email LIKE '%" + schoolFilter + "%' ";
                String stmt = "";

                if (yearFilter != null && !yearFilter.equalsIgnoreCase("NA")) {
                    yearSelect = yearSelect.toLowerCase();
                    stmt = stmt + yearSelect;
                }

                if (genderFilter != null && !genderFilter.equalsIgnoreCase("NA")) {
                    genderSelect = genderSelect.toLowerCase();
                    stmt = stmt + genderSelect;
                }

                if (schoolFilter != null && !schoolFilter.equalsIgnoreCase("NA")) {
                    schoolSelect = schoolSelect.toLowerCase();
                    stmt = stmt + schoolSelect;
                }

                LinkedHashMap<Integer, Integer> resultList = ProcessDiurnal.diurnalReport2(date + " 00:00:00", stmt);

                for (Map.Entry<Integer, Integer> entry : resultList.entrySet()) {
                    JsonObject eachEntry = new JsonObject();
                    int key = entry.getKey();
                    int value = entry.getValue();
                    int beforeKey = key - 1;
                    String lowerLimit = "" + beforeKey + ":00";
                    String upperLimit = "" + key + ":00";

                    if (upperLimit.equals("24:00")) {
                        upperLimit = "00:00";
                    }

                    if (beforeKey < 10) {
                        lowerLimit = "0" + lowerLimit;
                    }
                    if (key < 10) {
                        upperLimit = "0" + upperLimit;
                    }

                    String time = lowerLimit + "-" + upperLimit;

                    eachEntry.addProperty("period", time);
                    eachEntry.addProperty("duration", value);

                    result.add(eachEntry);
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
