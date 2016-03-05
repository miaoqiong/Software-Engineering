package Json;

import static Servlets.ProcessSmartphoneOveruse.calculateAverageGameTime;
import static Servlets.ProcessSmartphoneOveruse.calculateAverageHourlyUsage;
import static Servlets.ProcessSmartphoneOveruse.calculateAverageUsageTime;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dao.AppUsageDAO;
import dao.UserDAO;
import entity.User;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.BootstrapValidation;
import utilities.DateFormatter;

@WebServlet(name = "JsonSmartphoneOveruse", urlPatterns = {"/json/overuse-report"})
public class JsonSmartphoneOveruse extends HttpServlet {

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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet JsonSmartphoneOveruse</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet JsonSmartphoneOveruse at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        String token = request.getParameter("token");
        String start = request.getParameter("startdate");
        String end = request.getParameter("enddate");
        String macAddress = request.getParameter("macaddress");

        TreeSet<String> errorMsgs = new TreeSet<String>();
        final SimpleDateFormat DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date startDate = null;
        Date endDate = null;

        boolean commonValidation = true;
        //Common validation
        //start date 
        if (start == null) {
            errorMsgs.add("missing startdate");
            commonValidation = false;
        } else if (start.isEmpty()) {
            errorMsgs.add("blank startdate");
            commonValidation = false;
        }

        if (start != null && (!start.isEmpty())) {
            start += " 00:00:00";
            if (!BootstrapValidation.isTimeStampValid(start)) {
                errorMsgs.add("invalid startdate");
                commonValidation = false;
            }
        }

        //end date
        if (end == null) {
            errorMsgs.add("missing enddate");
            commonValidation = false;
        } else if (end.isEmpty()) {
            errorMsgs.add("blank enddate");
            commonValidation = false;
        }

        if (end != null && (!end.isEmpty())) {
            end += " 23:59:59";
            if (!BootstrapValidation.isTimeStampValid(end)) {
                errorMsgs.add("invalid enddate");
                commonValidation = false;
            }
        }

        //macaddress
        if (macAddress == null) {
            errorMsgs.add("missing macaddress");
            commonValidation = false;
        } else if (macAddress.isEmpty()) {
            errorMsgs.add("blank macaddress");
            commonValidation = false;
        }

        //token
        String validatedUser = null;
        if (token == null) {
            errorMsgs.add("missing token");
            commonValidation = false;
        } else if (token.isEmpty()) {
            errorMsgs.add("blank token");
            commonValidation = false;
        } else {
            try {
                validatedUser = JWTUtility.verify(token, sharedSecret.get());
                if (validatedUser == null) {
                    errorMsgs.add("invalid token");
                    commonValidation = false;
                }
            } catch (JWTException e) {
                errorMsgs.add("invalid token");
                commonValidation = false;
            }
        }

        boolean allValid = true;

        //2nd validation
        if (commonValidation) {

            //validate dates
            if (start.equals("")) {
                errorMsgs.add("invalid startdate");
                allValid = false;
            }
            if (end.equals("")) {
                errorMsgs.add("invalid enddate");
                allValid = false;
            } else {
                try {
                    //startDate = DATEFORMAT.parse(start + " 00:00:00");
                    startDate = DATEFORMAT.parse(start);
                } catch (Exception e) {
                    errorMsgs.add("invalid startdate");
                    allValid = false;
                }

                try {
                    //endDate = DATEFORMAT.parse(end + " 23:59:59");
                    endDate = DATEFORMAT.parse(end);
                } catch (Exception e) {
                    errorMsgs.add("invalid enddate");
                    allValid = false;
                }

                if (startDate != null && endDate != null) {
                    boolean endBeforeStart = endDate.before(startDate);
                    if (endBeforeStart) {
                        errorMsgs.add("invalid startdate");
                        allValid = false;
                    }
                }
            }

            //validate macAddress
            if (!macAddress.matches("[a-fA-F0-9]{40}")) {
                errorMsgs.add("invalid macaddress");
                allValid = false;
            }

            UserDAO userDAO = new UserDAO();
            User user = userDAO.retrieveSingleUserByMacAddress(macAddress);
            if (user == null) {
                errorMsgs.add("invalid macaddress");
                allValid = false;
            }

        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject obj = new JsonObject();

        if (commonValidation && allValid) {
            obj.addProperty("status", "success");
            JsonObject results = new JsonObject();

            //retrieve usage time and index
            String index = "";
            AppUsageDAO dao = new AppUsageDAO();
            UserDAO userDAO = new UserDAO();
            User user = userDAO.retrieveSingleUserByMacAddress(macAddress);

            HashMap<Date, ArrayList<String>> map = dao.retrieveSmartphoneUsageTime(startDate, endDate, user);
            System.out.println("map size" + map.size());

            double returnTime = calculateAverageUsageTime(map, DateFormatter.formatDate(startDate), DateFormatter.formatDate(endDate));
            System.out.println("return time: " + returnTime);
            if (returnTime >= 0) {
                if (returnTime < 10800) {  //less than 3 hours
                    index = "Light";
                } else if (returnTime >= 18000) {  //more than 5 hours
                    index = "Severe";
                } else {
                    index = "Moderate";
                }
            }
            int returnTimeInt = (int) Math.round(returnTime);
            //String returnTimeStr = "" + returnTimeInt;

            //retrieve usage time and index
            String gamingIndex = "";
            ArrayList<HashMap<Date, ArrayList<String>>> mapList = dao.retrieveSmartphoneGameTime(startDate, endDate, user);
            HashMap<Date, ArrayList<String>> mapGame = mapList.get(0);
            HashMap<Date, ArrayList<String>> mapCat = mapList.get(1);
            double gameTime = calculateAverageGameTime(mapGame, mapCat, DateFormatter.formatDate(startDate), DateFormatter.formatDate(endDate));

            if (gameTime >= 0) {
                if (gameTime < 3600) {  //less than 1 hours
                    gamingIndex = "Light";
                } else if (gameTime >= 7200) {  //more than 2 hrs
                    gamingIndex = "Severe";
                } else {
                    gamingIndex = "Moderate";
                }
            }

            int gameTimeInt = (int) Math.round(gameTime);
            //String gameTimeStr = "" + gameTimeInt;

            //retrieve usage time and index
            String frequencyIndex = "";
            HashMap<String, ArrayList<String>> mapUsage = dao.retrieveSmartphoneSessionNumber(startDate, endDate, user);
            double num = calculateAverageHourlyUsage(mapUsage, DateFormatter.formatDate(startDate), DateFormatter.formatDate(endDate));
            num = Math.round(num * 100.0) / 100.0;

            if (num >= 0) {
                if (num < 3) {  //less than 3 session/hour
                    frequencyIndex = "Light";
                } else if (num >= 5) {  //more than 5
                    frequencyIndex = "Severe";
                } else {
                    frequencyIndex = "Moderate";
                }
            }

            String numStr = String.format("%.2f", num);
            double numDouble = Double.parseDouble(numStr);

            //get index
            String overuseIndex = "";
            if (index.equals("Light") && gamingIndex.equals("Light") && frequencyIndex.equals("Light")) {
                overuseIndex = "Normal";
            } else if (index.equals("Severe") || gamingIndex.equals("Severe") || frequencyIndex.equals("Severe")) {
                overuseIndex = "Overusing";
            } else {
                overuseIndex = "ToBeCautious";
            }
            results.addProperty("overuse-index", overuseIndex);

            JsonArray metrics = new JsonArray();
            JsonObject usage = new JsonObject();

            usage.addProperty("usage-category", index); //usage index
            usage.addProperty("usage-duration", returnTimeInt); //usage time

            JsonObject gaming = new JsonObject();

            gaming.addProperty("gaming-category", gamingIndex); //usage index
            gaming.addProperty("gaming-duration", gameTimeInt); //usage time

            JsonObject accessfrequency = new JsonObject();
            accessfrequency.addProperty("accessfrequency-category", frequencyIndex); //usage index
            accessfrequency.addProperty("accessfrequency", numDouble); //usage time

            metrics.add(usage);
            metrics.add(gaming);
            metrics.add(accessfrequency);
            results.add("metrics", metrics);
            obj.add("results", results);
        } else {
            //String errorString = "";
            JsonArray messages = new JsonArray();
            for (String error : errorMsgs) {
                messages.add(new JsonPrimitive(error));
            }
            //errorString += errorMsgs.get(errorMsgs.size() - 1);
            obj.addProperty("status", "error");
            obj.add("messages", messages);
        }

        String json = gson.toJson(obj);
        response.getWriter().print(json);

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
