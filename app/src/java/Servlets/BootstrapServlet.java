package Servlets;

import dao.AppDAO;
import dao.AppUsageDAO;
import dao.UserDAO;
import entity.App;
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


@WebServlet("/adminDisplay")
/**
 * Process Bootstrap
 */
public class BootstrapServlet extends HttpServlet {

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

        //for local testing
//        String dir = request.getServletContext().getRealPath("/uploads");
//        File folder = new File(dir);
        //for openshift
        String dir = System.getenv("OPENSHIFT_DATA_DIR") + "uploads";
        File folder = new File(dir);
        File[] dataFiles = folder.listFiles();

        for (File file : dataFiles) {
            //check that it does not unzip a directory
            if (file.isFile()) {
                    Unzip.unzipFile(file, dir + File.separator + "data");
             
            }

        }

    dir += File.separator + "data" ;

    File dataFolder = new File(dir);

   
    ArrayList<String> combine = new ArrayList<>();
    HashMap<String, App> validApplookupData = new HashMap<>();
    HashMap<String, User> validDemoData = new HashMap<>();
    LinkedHashMap<String, AppUsage> validAppusageData = new LinkedHashMap<>();
    String dir1 = "";
    String dir2 = "";
    String dir3 = "";

    for (File file

    : dataFolder.listFiles () 
        ) {

            if (file.getName().equals("app-lookup.csv")) {
            
            dir2 = dir + File.separator + "app-lookup.csv";
            HashMap<Integer, String[]> applookupData = BootstrapValidation.readFileAll(dir2);
            validApplookupData = BootstrapValidation.validateAppLookUp(applookupData, dir2);


                   
        }
        if (file.getName().equals("demographics.csv")) {
          
            dir1 = dir + File.separator + "demographics.csv";
            HashMap<Integer, String[]> demographicData = BootstrapValidation.readFileAll(dir1);
            validDemoData = BootstrapValidation.validateDemographics(demographicData, dir1);

        }

    }

    for (File file

    : dataFolder.listFiles () 
        ) {
            if (file.getName().equals("app.csv")) {
            dir3 = dir + File.separator + "app.csv";
            LinkedHashMap<Integer, String[]> appData = AppUsageDAO.readFile(dir3);
            //After validataion put into Linked Hash Map
            validAppusageData = BootstrapValidation.validateAppusageData(appData, validDemoData, validApplookupData, dir3);
        }
    }

    AppUsageDAO.deleteAll ();

    UserDAO.deleteAll ();

    AppDAO.deleteAll ();
    int demo = UserDAO.upload(validDemoData);
    int applookup = AppDAO.upload(validApplookupData);
    int appusage = AppUsageDAO.upload(validAppusageData);
    ArrayList<Integer> num = new ArrayList<Integer>();

    num.add (demo);

    num.add (applookup);

    num.add (appusage);
    BootstrapUpload.cleanAll();

    combine  = BootstrapValidation.retrieveErrors();

    request.setAttribute ("totalNum", num);
    request.setAttribute ("error", combine);
    BootstrapValidation.clearErrors ();
    RequestDispatcher rd = request.getRequestDispatcher("adminDisplay.jsp");

    rd.forward (request, response);
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
