package Servlets;


import dao.AppUsageDAO;
import entity.User;
import entity.UserAppUsage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utilities.DateFormatter;

@WebServlet(name = "ProcessSmartphoneOveruse", urlPatterns = {"/ProcessSmartphoneOveruse"})
/**
 * Process smart phone over user report
 */
public class ProcessSmartphoneOveruse extends HttpServlet {

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

        ArrayList<String> results = new ArrayList<>();
        ArrayList<String> numResults = new ArrayList<>();

        String startDate = (String) request.getParameter("startdate");
        String endDate = (String) request.getParameter("enddate");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (startDate == null && endDate == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        if (startDate.equals("") || endDate.equals("")) {
            session.setAttribute("error", "Please enter start date and end date");
            response.sendRedirect("smartphoneOveruseReport.jsp");
        } else {

            startDate = startDate + " 00:00:00";
            endDate = endDate + " 23:59:59";
            Date start = DateFormatter.stringToDate(startDate);
            Date end = DateFormatter.stringToDate(endDate);

            if (start.compareTo(end) > 0) {
                session.setAttribute("error", "End date cannot be earlier than start date!");
                response.sendRedirect("smartphoneOveruseReport.jsp");
            } else {

                AppUsageDAO dao = new AppUsageDAO();

                HashMap<Date, ArrayList<String>> map = dao.retrieveSmartphoneUsageTime(start, end, user);
                double returnTime = calculateAverageUsageTime(map, startDate, endDate);

                if (returnTime >= 0) {
                    if (returnTime < 10800) {  //less than 3 hours
                        results.add("Light");
                    } else if (returnTime >= 18000) {  //more than 5 hours
                        results.add("Severe");
                    } else {
                        results.add("Moderate");
                    }
                } else {
                    results.add("BUG");
                }

                //double returnTimeHour = returnTime/3600.00;
                int returnTimeInt = (int) Math.round(returnTime);
                String returnTimeStr = "" + returnTimeInt;
                numResults.add(returnTimeStr);

                ArrayList<HashMap<Date, ArrayList<String>>> mapList = dao.retrieveSmartphoneGameTime(start, end, user);
                HashMap<Date, ArrayList<String>> mapGame = mapList.get(0);
                HashMap<Date, ArrayList<String>> mapCat = mapList.get(1);
                double gameTime = calculateAverageGameTime(mapGame, mapCat, startDate, endDate);

                if (gameTime >= 0) {
                    if (gameTime < 3600) {  //less than 1 hours
                        results.add("Light");
                    } else if (gameTime >= 7200) {  //more than 2 hrs
                        results.add("Severe");
                    } else {
                        results.add("Moderate");
                    }
                } else {
                    results.add("BUG");
                }

                //double gameTimeHour = gameTime/3600.00;
                int gameTimeInt = (int) Math.round(gameTime);
                String gameTimeStr = "" + gameTimeInt;
                numResults.add(gameTimeStr);

                HashMap<String, ArrayList<String>> mapUsage = dao.retrieveSmartphoneSessionNumber(start, end, user);
                double num = calculateAverageHourlyUsage(mapUsage, startDate, endDate);
                num = Math.round(num * 100.0) / 100.0;

                if (num >= 0) {
                    if (num < 3) {  //less than 3 session/hour
                        results.add("Light");
                    } else if (num >= 5) {  //more than 5
                        results.add("Severe");
                    } else {
                        results.add("Moderate");
                    }
                } else {
                    results.add("BUG");
                }

                String numStr = String.format("%.2f", num);
                numResults.add(numStr);

                session.setAttribute("results", results);
                session.setAttribute("numResults", numResults);
                RequestDispatcher rd = request.getRequestDispatcher("smartphoneOveruseResult.jsp");
                rd.forward(request, response);
            }
        }
    }

    /**
     * Returns the smartphone average daily usage duration taken by the user
     *
     * @param map hashmap with dates as keys and all the smartphone timestamps on that date
     * @param startDate the start date choosen by user
     * @param endDate the end date choosen by user
     * @return the smartphone average daily usage duration by the user
     */
    public static double calculateAverageUsageTime(HashMap<Date, ArrayList<String>> map, String startDate, String endDate) {

        double returnTime = 0;
        Set dateSet = map.keySet();
        Object[] dates = dateSet.toArray();
        int days = map.keySet().size();
        if (days == 1) {
            UserAppUsage u = new UserAppUsage(map.get(dates[0]), endDate);
            returnTime = u.getTotalUsageTime(map.get(dates[0]), endDate);
            System.out.println(dates[0].toString());
        } else {
            for (Object dateObject : dates) {
                try {
                    Date date = (Date) dateObject;
                    String theEndDate = date.toString() + "23:59:59";
                    UserAppUsage u = new UserAppUsage(map.get(date), theEndDate);
                    Long totalTime = u.getTotalUsageTime(map.get(date), theEndDate);
                    System.out.println(totalTime);
                    returnTime += totalTime;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        Date start = DateFormatter.stringToDate(startDate);
        Date end = DateFormatter.stringToDate(endDate);
        long diff = end.getTime() - start.getTime();
        int numDays = ((int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);    //calculate total days
        //System.out.println(numDays);
        returnTime = returnTime / numDays;

        return returnTime;
    }

    /**
     * Returns the smartphone average daily gaming duration taken by the user
     *
     * @param map hashmap with dates as keys and all the timestamps on that date
     * @param startDate the start date choosen by user
     * @param endDate the end date choosen by user
     * @return the smartphone average daily gaming duration by the user
     */
    public static double calculateAverageGameTime(HashMap<Date, ArrayList<String>> map, HashMap<Date, ArrayList<String>> mapCat, String startDate, String endDate) {
        double returnTime = 0;
        Set dateSet = map.keySet();
        Object[] dates = dateSet.toArray();
        int days = map.keySet().size();
        if (days == 1) {
            UserAppUsage u = new UserAppUsage(map.get(dates[0]), endDate);
            System.out.println();
            returnTime = u.getTotalGameUsageTime(map.get(dates[0]), mapCat.get(dates[0]), endDate);
            //System.out.println(dates.get(0).toString());
            //System.out.println(map.get(dates.get(0)).size());
            //System.out.println(mapCat.get(dates.get(0)).size());
        } else {
            //long sum = 0;
            for (Object dateObject : dates) {
                try {
                    Date date = (Date) dateObject;

                    String theEndDate = date.toString() + "23:59:59";
                    UserAppUsage u = new UserAppUsage(map.get(date), theEndDate);
                    Long totalTime = u.getTotalGameUsageTime(map.get(date), mapCat.get(date), theEndDate);
                    //System.out.println(totalTime);
                    returnTime += totalTime;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        Date start = DateFormatter.stringToDate(startDate);
        Date end = DateFormatter.stringToDate(endDate);
        long diff = end.getTime() - start.getTime();
        int numDays = ((int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);    //calculate total days
        //System.out.println(numDays);
        returnTime = returnTime / numDays;
        return returnTime;
    }

    /**
     * Returns the smartphone access frequency of the user
     *
     * @param map hashmap with dates as keys and all the timestamps on that date
     * @param startDate the start date choosen by user
     * @param endDate the end date choosen by user
     * @return the smartphone access frequency of the user
     */
    public static double calculateAverageHourlyUsage(HashMap<String, ArrayList<String>> map, String startDate, String endDate) {
        double num = 0;
        Set dateHourSet = map.keySet();
        Iterator iter = dateHourSet.iterator();

        while (iter.hasNext()) {
            String dateHour = (String) iter.next();
            ArrayList<String> timestamps = map.get(dateHour);

            Date startHour = utilities.DateFormatter.stringToDate(dateHour);    //start hout in date format
            String startHourStr = utilities.DateFormatter.formatDate(startHour);
            Date endHour = utilities.DateFormatter.addHour(startHour);  //end hour in date format
            String endHourStr = utilities.DateFormatter.formatDate(endHour);

            UserAppUsage u = new UserAppUsage(timestamps, endHourStr);
            num += u.getHourlyUsage(timestamps, startHourStr, endHourStr);
            System.out.println("Daily session number: " + num);
        }

        Date start = DateFormatter.stringToDate(startDate);
        Date end = DateFormatter.stringToDate(endDate);
        long difference = end.getTime() - start.getTime();
        difference = difference / 1000 / 3600;
        int i = (int) difference + 1;
        num = num / i;
        return num;
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
