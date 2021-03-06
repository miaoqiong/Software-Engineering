package Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.opencsv.CSVReader;
import dao.AppUsageDAO;
import dao.UserDAO;
import entity.AppUsage;
import entity.User;
import is203.JWTException;
import is203.JWTUtility;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import utilities.BootstrapValidation;


@WebServlet(name = "update", urlPatterns = {"/json/update"})
@MultipartConfig
public class Update extends HttpServlet {

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
        response.setContentType("application/JSON");
        PrintWriter out = response.getWriter();

        //creates a new gson object
        //by instantiating a new factory object, set pretty printing, then calling the create method
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //creats a new display object for printing the desired display output
        JsonObject jsonOutput = new JsonObject();

        // Verify Token 
        String token = request.getParameter("token");
        String validatedUser = null;
        if (token == null) {
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("message", "missing token");
            out.println(gson.toJson(jsonOutput));
            out.close();
            return;
        } else if (token.isEmpty()) {
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("message", "blank token");
            out.println(gson.toJson(jsonOutput));
            out.close();
            return;
        } else {
            try {
                validatedUser = JWTUtility.verify(token, sharedSecret.get());
                if (validatedUser == null || !validatedUser.equals("admin")) {
                    jsonOutput.addProperty("status", "error");
                    jsonOutput.addProperty("message", "invalid token");
                    out.println(gson.toJson(jsonOutput));
                    out.close();
                    return;
                }
            } catch (JWTException e) {
                jsonOutput.addProperty("status", "error");
                jsonOutput.addProperty("message", "invalid token");
                out.println(gson.toJson(jsonOutput));
                out.close();
                return;
            }
        }

        // extract zip file content & store into map****      
        Part filepart = request.getPart("bootstrap-file");

        //verify folder type (zip file?!)
     Boolean invalidFileType = false;
        // System.out.println("**********************"+filepart.getContentType() +"    *********************      ");
         if (filepart.getContentType().equals("application/x-zip-compressed") || filepart.getContentType().equals("application/octet-stream")
         || filepart.getContentType().equals("application/zip")) {
         invalidFileType = true;
         }

         if (!invalidFileType) {
         jsonOutput.addProperty("status", "error");
         jsonOutput.addProperty("Message", "Please upload a zip file!");
         out.println(gson.toJson(jsonOutput));
         out.close();
         return;

         } else {
        InputStream is = filepart.getInputStream();
        if (is.available() == 0) {
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("message", "Please choose a zip file to upload");
            out.println(gson.toJson(jsonOutput));
            out.close();
            return;

        }
        ZipInputStream zis = new ZipInputStream(is);
        ZipEntry ze = null;
        Map<String, ArrayList<String[]>> zipFileContents = new HashMap<String, ArrayList<String[]>>(); // to store file name and its content

        while ((ze = zis.getNextEntry()) != null) {
            String extension = ze.getName().substring(ze.getName().lastIndexOf(".") + 1);
            if (extension.equals("csv")) {
                CSVReader reader = new CSVReader(new InputStreamReader(zis), ',', '"');
                ArrayList<String[]> currentCSVFile = (ArrayList<String[]>) reader.readAll();
                // System.out.println("sssssssssssss>>>>>>>>>>>>>>>>>>>>>>>  " + currentCSVFile.size());
                zipFileContents.put(ze.getName(), currentCSVFile);
                // System.out.println(zipFileContents.size() + ">>>>>>>>>>");
            }/* 
             else {
             jsonOutput.addProperty("status", "error");
             jsonOutput.addProperty("Message", "Unknown file type contained in zip folder");
             out.println(gson.toJson(jsonOutput));
             out.close();
             return;

             }*/

        }

        // zip file content validation
        String zipError = "";
        List<String> combine = new ArrayList<>();
        // ArrayList<Integer> num = new ArrayList<>();
        HashMap<String, Integer> num = new HashMap<>();
        for (String filename : zipFileContents.keySet()) {
            switch (filename) {
                case "demographics.csv":
                    break;
                case "app.csv":
                    break;
                case "app-lookup.csv":
                    break;
                case "location.csv":
                    break;
                case "location-lookup.csv":
                    break;
                default:
                    if (zipError.isEmpty()) {
                        zipError += "Unknown CSV file(s):" + filename;
                    } else {
                        zipError += ", " + filename;
                    }
            }
        }
        JsonArray recordLoaded = new JsonArray();
        if (!zipError.isEmpty()) {
            jsonOutput.addProperty("status", "error");
            jsonOutput.addProperty("Message", zipError);
            out.println(gson.toJson(jsonOutput));
            out.close();
            return;
        } else {
            // convert arrayList<String[]> to hashmap<Integer,String[]>
            if (zipFileContents.containsKey("demographics.csv")) {
                HashMap<Integer, String[]> userMap = BootstrapValidation.convertToHashmap(zipFileContents.get("demographics.csv"));
                HashMap<String, User> validDemoData = new HashMap<>();
                validDemoData = BootstrapValidation.validateDemographics(userMap, "demographics.csv");
                int demo = UserDAO.upload(validDemoData);
                num.put("demographics.csv", demo);
                //num.add(demo);

            }

            if (zipFileContents.containsKey("app.csv")) {
                LinkedHashMap<Integer, String[]> appUsageMap = BootstrapValidation.convertToLinkedHashmap(zipFileContents.get("app.csv"));
                LinkedHashMap<String, AppUsage> validAppusageData = new LinkedHashMap<>();
                validAppusageData = BootstrapValidation.validateAppusageDataWithDB(appUsageMap, "app.csv");
                int appusage = AppUsageDAO.upload(validAppusageData);
                num.put("app.csv", appusage);

            }

            combine = BootstrapValidation.retrieveErrors();

        }

        JsonArray jsonArrayError = new JsonArray();
        if (!combine.isEmpty()) { // error msg arrayList is not empty
            for (String error : combine) {
                if (!error.contains("Line Number: 1,")) {
                    JsonObject jo = new JsonObject();
                    String[] msg = error.split(",");
                    //jo.addProperty("file", msg[0].substring(0, msg[0].indexOf("Line"))); // string
                    jo.addProperty("file", msg[0].substring(0, msg[0].indexOf("Line")).trim()); // string

                    // jo.addProperty("line", msg[0].substring(error.indexOf(":") + 1));   // string
                    jo.addProperty("line", Integer.parseInt(msg[0].substring(error.indexOf(":") + 1).trim()));   // string

                    JsonArray ja = new JsonArray();
                    for (int i = 1; i < msg.length; i++) {
                        //ja.add(new JsonPrimitive(msg[i]));
                        ja.add(new JsonPrimitive(msg[i].trim()));
                    }

                    jo.add("message", ja); //array
                    jsonArrayError.add(jo);
                }

            }
        }

        if (num.containsKey("app.csv")) {
            JsonObject joApp = new JsonObject();
            joApp.addProperty("app.csv", num.get("app.csv"));
            recordLoaded.add(joApp);

        }
        if (num.containsKey("demographics.csv")) {
            JsonObject joDemo = new JsonObject();
            joDemo.addProperty("demographics.csv", num.get("demographics.csv"));
            recordLoaded.add(joDemo);

        }

        BootstrapValidation.clearErrors();
        num = new HashMap<>();

        if (jsonArrayError.size() != 0) {
            jsonOutput.addProperty("status", "error");
            jsonOutput.add("num-record-loaded", recordLoaded);
            jsonOutput.add("error", jsonArrayError);

        } else {
            jsonOutput.addProperty("status", "success");
            jsonOutput.add("num-record-loaded", recordLoaded);

        }
        }

        //writes the output as a response (but not html)
        try {
            out.println(gson.toJson(jsonOutput));
        } finally {
            out.close();
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
