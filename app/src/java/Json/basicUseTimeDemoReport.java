package Json;

import static Servlets.ProcessBasicAppUsage.calculateAverageTime;
import static Servlets.ProcessBasicAppUsage.getTotalUsers;
import static Servlets.ProcessBasicAppUsage.sortByFilter1;
import static Servlets.ProcessBasicAppUsage.sortByFilter2;
import static Servlets.ProcessBasicAppUsage.sortByFilter3;
import static Servlets.ProcessBasicAppUsage.sortByFilter4;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dao.AppUsageDAO;
import dao.UserDAO;
import entity.UserAppUsage;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.DateFormatter;

@WebServlet(name = "basicUseTimeDemoReport", urlPatterns = {"/json/basic-usetime-demographics-report"})
public class basicUseTimeDemoReport extends HttpServlet {

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
        String order = request.getParameter("order");

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

        if (order == null) {
            errorMsg.add("missing order");
            commonValidation = false;
        } else {
            if (order.equals("")) {
                errorMsg.add("blank order");
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

                //check if order is valid
            //split order fields by commas
            String[] orderFields = order.split(",");

            ArrayList<String> fieldChecker = new ArrayList<String>();
            fieldChecker.add("year");
            fieldChecker.add("gender");
            fieldChecker.add("school");
            fieldChecker.add("cca");

            boolean orderValid = true;

            if (order.charAt(order.length() - 1) == ',' || order.charAt(0) == ',') {
                orderValid = false;
            }

            for (String s : orderFields) {
                s = s.toLowerCase();
                //check is fields input by user is valid
                if (!fieldChecker.contains(s)) {
                    orderValid = false;
                    break;
                }
                if (s.length() == 0) {
                    orderValid = false;
                    break;
                }

            }
            //check if fields are duplicate
            HashSet<String> duplicateChecker = new HashSet<String>();
            for (String arrayElement : orderFields) {
                if (!duplicateChecker.add(arrayElement)) {
                    orderValid = false;
                    break;
                }
            }

            if (!orderValid) {
                errorMsg.add("invalid order");
                allValid = false;
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

            String[] str = order.split(",");
            JsonArray result = new JsonArray();

            if (str.length == 1) {
                int[][] resultList = sortByFilter1(catList, order);

                String filter1 = str[0];

                HashMap<String, String[]> filters = UserDAO.retrieveFilters();
                for (int i = 0; i < resultList.length; i++) {
                    String[] map = filters.get(filter1);

                    JsonObject eachFilter = new JsonObject(); //object for each year/gender/school

                    JsonArray inner = new JsonArray(); //breakdown for each year/ each gender/ each school

                    for (int j = 0; j < resultList[i].length; j++) {
                        if (j != 0) {

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

                            inner.add(subArr);
                        }
                    }
                    eachFilter.addProperty(filter1, map[i]);
                    eachFilter.addProperty("count", resultList[i][0]);
                    int value2 = resultList[i][0];
                    double percentage2 = ((double) value2 / totalUsers) * 100;
                    long num2 = Math.round(percentage2);
                    eachFilter.addProperty("percent", num2);
                    eachFilter.add("breakdown", inner);
                    result.add(eachFilter);
                }
            }

            if (str.length == 2) {
                int[][][] resultList = sortByFilter2(catList, order);
                String filter1 = str[0];
                String filter2 = str[1];
                HashMap<String, String[]> filters = UserDAO.retrieveFilters();
                for (int i = 0; i < resultList.length; i++) {
                    String[] map = filters.get(filter1);
                    JsonObject eachSub = new JsonObject();
                    JsonArray outer = new JsonArray();

                    for (int j = 0; j < resultList[i].length; j++) {
                        if (j != 0) {
                            String[] map2 = filters.get(filter2);
                            JsonObject eachFilter = new JsonObject(); //object for each year/gender/school
                            JsonArray inner = new JsonArray(); //breakdown for each year/ each gender/ each school

                            for (int k = 0; k < resultList[i][j].length; k++) {
                                if (k != 0) {

                                    JsonObject subArr = new JsonObject();

                                    int value = resultList[i][j][k];
                                    double percentage = ((double) value / totalUsers) * 100;
                                    long num = Math.round(percentage);

                                    switch (k) {
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

                                    inner.add(subArr);
                                }

                            }
                            eachFilter.addProperty(filter2, map2[j - 1]);
                            eachFilter.addProperty("count", resultList[i][j][0]);
                            int value2 = resultList[i][j][0];
                            double percentage2 = ((double) value2 / totalUsers) * 100;
                            long num2 = Math.round(percentage2);
                            eachFilter.addProperty("percent", num2);
                            eachFilter.add("breakdown", inner);
                            outer.add(eachFilter);

                        }
                    }
                    eachSub.addProperty(filter1, map[i]);
                    eachSub.addProperty("count", resultList[i][0][0]);
                    int value2 = resultList[i][0][0];
                    double percentage2 = ((double) value2 / totalUsers) * 100;
                    long num2 = Math.round(percentage2);
                    eachSub.addProperty("percent", num2);
                    eachSub.add("breakdown", outer);
                    result.add(eachSub);

                }
            }
            if (str.length == 3) {
                int[][][][] resultList = sortByFilter3(catList, order);

                String filter1 = str[0];
                String filter2 = str[1];
                String filter3 = str[2];
                HashMap<String, String[]> filters = UserDAO.retrieveFilters();

                for (int i = 0; i < resultList.length; i++) {
                    if (i != 0) {

                        String[] map1 = filters.get(filter1);
                        JsonObject eachSubMain = new JsonObject(); //object for each year/gender/school
                        JsonArray outer = new JsonArray(); //breakdown for each year/ each gender/ each school

                        for (int j = 0; j < resultList[i].length; j++) {
                            if (j != 0) {
                                String[] map2 = filters.get(filter2);
                                JsonObject eachSub = new JsonObject(); //object for each year/gender/school
                                JsonArray mid = new JsonArray(); //breakdown for each year/ each gender/ each school

                                for (int k = 0; k < resultList[i][j].length; k++) {
                                    if (k != 0) {
                                        String[] map3 = filters.get(filter3);
                                        JsonObject eachFilter = new JsonObject(); //object for each year/gender/school
                                        JsonArray inner = new JsonArray(); //breakdown for each year/ each gender/ each school

                                        for (int m = 0; m < resultList[i][j][k].length; m++) {
                                            if (m != 0) {

                                                JsonObject subArr = new JsonObject();

                                                int value = resultList[i][j][k][m];
                                                double percentage = ((double) value / totalUsers) * 100;
                                                long num = Math.round(percentage);

                                                switch (m) {
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

                                                inner.add(subArr);
                                            }
                                        }

                                        eachFilter.addProperty(filter3, map3[k - 1]);
                                        eachFilter.addProperty("count", resultList[i][j][k][0]);
                                        int value2 = resultList[i][j][k][0];
                                        double percentage2 = ((double) value2 / totalUsers) * 100;
                                        long num2 = Math.round(percentage2);
                                        eachFilter.addProperty("percent", num2);
                                        eachFilter.add("breakdown", inner);
                                        mid.add(eachFilter);
                                    }

                                }
                                eachSub.addProperty(filter2, map2[j - 1]);
                                eachSub.addProperty("count", resultList[i][j][0][0]);
                                int value2 = resultList[i][j][0][0];
                                double percentage2 = ((double) value2 / totalUsers) * 100;
                                long num2 = Math.round(percentage2);
                                eachSub.addProperty("percent", num2);
                                eachSub.add("breakdown", mid);
                                outer.add(eachSub);
                            }
                        }
                        eachSubMain.addProperty(filter1, map1[i - 1]);
                        eachSubMain.addProperty("count", resultList[i][0][0][0]);
                        int value2 = resultList[i][0][0][0];
                        double percentage2 = ((double) value2 / totalUsers) * 100;
                        long num2 = Math.round(percentage2);
                        eachSubMain.addProperty("percent", num2);
                        eachSubMain.add("breakdown", outer);
                        result.add(eachSubMain);
                    }
                }

            }
            if (str.length == 4) {
                int[][][][][] resultList = sortByFilter4(catList, order);

                String filter1 = str[0];
                String filter2 = str[1];
                String filter3 = str[2];
                String filter4 = str[3];
                HashMap<String, String[]> filters = UserDAO.retrieveFilters();

                for (int i = 0; i < resultList.length; i++) {
                    if (i != 0) {
                        String[] map1 = filters.get(filter1);
                        JsonObject eachSubMain = new JsonObject(); //object for each year/gender/school
                        JsonArray outer = new JsonArray(); //breakdown for each year/ each gender/ each school

                        for (int j = 0; j < resultList[i].length; j++) {
                            if (j != 0) {
                                String[] map2 = filters.get(filter2);
                                JsonObject eachSubMid1 = new JsonObject(); //object for each year/gender/school
                                JsonArray mid1 = new JsonArray(); //breakdown for each year/ each gender/ each school

                                for (int k = 0; k < resultList[i][j].length; k++) {
                                    if (k != 0) {
                                        String[] map3 = filters.get(filter3);
                                        JsonObject eachSubMid2 = new JsonObject(); //object for each year/gender/school
                                        JsonArray mid2 = new JsonArray(); //breakdown for each year/ each gender/ each school

                                        for (int m = 0; m < resultList[i][j][k].length; m++) {
                                            if (m != 0) {
                                                String[] map4 = filters.get(filter4);
                                                JsonObject eachFilter = new JsonObject(); //object for each year/gender/school
                                                JsonArray inner = new JsonArray(); //breakdown for each year/ each gender/ each school

                                                for (int n = 0; n < resultList[i][j][k][m].length; n++) {
                                                    if (n != 0) {
                                                        JsonObject subArr = new JsonObject();

                                                        int value = resultList[i][j][k][m][n];
                                                        double percentage = ((double) value / totalUsers) * 100;
                                                        long num = Math.round(percentage);

                                                        switch (n) {
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

                                                        inner.add(subArr);
                                                    }
                                                }
                                                eachFilter.addProperty(filter4, map4[m - 1]);
                                                eachFilter.addProperty("count", resultList[i][j][k][m][0]);
                                                int value2 = resultList[i][j][k][m][0];
                                                double percentage2 = ((double) value2 / totalUsers) * 100;
                                                long num2 = Math.round(percentage2);
                                                eachFilter.addProperty("percent", num2);
                                                eachFilter.add("breakdown", inner);
                                                mid2.add(eachFilter);
                                            }
                                        }
                                        eachSubMid2.addProperty(filter3, map3[k - 1]);
                                        eachSubMid2.addProperty("count", resultList[i][j][k][0][0]);
                                        int value2 = resultList[i][j][k][0][0];
                                        double percentage2 = ((double) value2 / totalUsers) * 100;
                                        long num2 = Math.round(percentage2);
                                        eachSubMid2.addProperty("percent", num2);
                                        eachSubMid2.add("breakdown", mid2);
                                        mid1.add(eachSubMid2);
                                    }
                                }
                                eachSubMid1.addProperty(filter2, map2[j - 1]);
                                eachSubMid1.addProperty("count", resultList[i][j][0][0][0]);
                                int value2 = resultList[i][j][0][0][0];
                                double percentage2 = ((double) value2 / totalUsers) * 100;
                                long num2 = Math.round(percentage2);
                                eachSubMid1.addProperty("percent", num2);
                                eachSubMid1.add("breakdown", mid1);
                                outer.add(eachSubMid1);
                            }
                        }
                        eachSubMain.addProperty(filter1, map1[i - 1]);
                        eachSubMain.addProperty("count", resultList[i][0][0][0][0]);
                        int value2 = resultList[i][0][0][0][0];
                        double percentage2 = ((double) value2 / totalUsers) * 100;
                        long num2 = Math.round(percentage2);
                        eachSubMain.addProperty("percent", num2);
                        eachSubMain.add("breakdown", outer);
                        result.add(eachSubMain);
                    }

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
