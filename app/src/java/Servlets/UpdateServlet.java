package Servlets;


import dao.AppUsageDAO;
import dao.UserDAO;
import entity.AppUsage;
import entity.User;
import utilities.Unzip;
import utilities.BootstrapValidation;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.BootstrapUpload;


@WebServlet("/updateServlet")
/**
 * Process update files
 */
public class UpdateServlet extends HttpServlet {

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
        //response.setContentType("text/html;charset=UTF-8");
            /* TODO output your page here. You may use following sample code. */
        //String dir = request.getServletContext().getRealPath("/updates");
        String dir = System.getenv("OPENSHIFT_DATA_DIR") + "updates";
        File folder = new File(dir);

        File[] dataFiles = folder.listFiles();

        if (dataFiles == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        for (File file : dataFiles) {
            //check that it does not unzip a directory
            if (file.isFile()) {
                Unzip.unzipFile(file, dir + File.separator + "data");
            }
        }

        dir += File.separator + "data";

        File dataFolder = new File(dir);

        ArrayList<String> combine = new ArrayList<>();
        HashMap<String, User> validDemoData = new HashMap<>();
        LinkedHashMap<String, AppUsage> appUsageLinkedHashMap = new LinkedHashMap<>();
        String dir1 = "";
        String dir3 = "";
        int demo = 0;

        for (File file : dataFolder.listFiles()) {

            if (file.getName().equals("demographics.csv")) {
                dir1 = dir + File.separator + "demographics.csv";
                HashMap<Integer, String[]> demographicData = BootstrapValidation.readFileAll(dir1);
                validDemoData = BootstrapValidation.validateDemographics(demographicData, dir1);
            }

        }
        if (validDemoData.size() != 0) {
            demo = UserDAO.upload(validDemoData);
        }
        for (File file : dataFolder.listFiles()) {
            if (file.getName().equals("app.csv")) {
                dir3 = dir + File.separator + "app.csv";

                LinkedHashMap<Integer, String[]> lmp = AppUsageDAO.readFile(dir3);
                appUsageLinkedHashMap = BootstrapValidation.validateAppusageDataWithDB(lmp, dir3);
            }
        }

        int appusage = AppUsageDAO.upload(appUsageLinkedHashMap);
        ArrayList<Integer> num = new ArrayList<Integer>();
        num.add(demo);
        num.add(appusage);
        BootstrapUpload.cleanAll2();

        
        combine = BootstrapValidation.retrieveErrors();
        request.setAttribute("totalNum", num);
        request.setAttribute("error", combine);
        BootstrapValidation.clearErrors();
        RequestDispatcher rd = request.getRequestDispatcher("updateDisplay.jsp");
        rd.forward(request, response);

     
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
