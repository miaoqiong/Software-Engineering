package Json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dao.UserDAO;
import entity.User;
import is203.JWTUtility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet(name = "authenticate", urlPatterns = {"/json/authenticate"})
public class authenticate extends HttpServlet {

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
        
                response.setContentType("application/json");

        //ArrayList<String> errorMsg = new ArrayList<String>();
        TreeSet<String> errorMsg = new TreeSet<String>();

        String userName = request.getParameter("username");
        String passWord = request.getParameter("password");

        //common validation for input parameters
        if (userName == null) {
            errorMsg.add("missing username");
        } else {
            if (userName.equals("")) {
                errorMsg.add("blank username");
            }
        }
        if (passWord == null) {
            errorMsg.add("missing password");
        } else {
            if (passWord.equals("")) {
                errorMsg.add("blank password");
            }
        }

        JsonObject obj = new JsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        if (errorMsg.size() > 0) { // errors returned
            JsonArray errorList = new JsonArray();
            //add error messages in to errorList
            Iterator<String> iter = errorMsg.iterator();
            while (iter.hasNext()) {
                errorList.add(new JsonPrimitive(iter.next()));
            }

            obj.addProperty("status", "error");
            obj.add("message", errorList);
        } else { //errors returned
            if (userName.equals("admin") && passWord.equals("password")) {
                String token = JWTUtility.sign(sharedSecret.get(), "admin");
                obj.addProperty("status", "success");
                obj.addProperty("token", token);
            } else {
                UserDAO dao = new UserDAO();
                User targetUser = dao.retrieveSingleUser(userName, passWord);
                if (targetUser == null) {
                    obj.addProperty("status", "error");
                    JsonArray arr = new JsonArray();
                    arr.add(new JsonPrimitive("invalid username/password"));
                    obj.add("messages", arr);
                } else {
                    String token = JWTUtility.sign(sharedSecret.get(), "student");
                    obj.addProperty("status", "success");
                    obj.addProperty("token", token);
                }
            }

        }

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println(gson.toJson(obj));

        }
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
