package Servlets;

import Logic.GameManager;
import Utils.Constants;
import Utils.ServletUtils;
import Utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static Utils.Constants.IS_HUMAN;
import static Utils.Constants.USERNAME;

/**
 * Created by Maor Gershkovitch on 10/18/2016.
 */
@WebServlet(name = "GameLoginServlet", urlPatterns = {"/Login"})
public class GameLoginServlet extends HttpServlet {

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
        String usernameFromSession = SessionUtils.getUsername(request);
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        if (usernameFromSession == null) {
            //user is not logged in yet
            String usernameFromParameter = request.getParameter(USERNAME);
            if (usernameFromParameter == null) {
                //no username in session and no username in parameter -
                //redirect back to the index page
                //this return an HTTP code back to the browser telling it to load
                //the given URL (in this case: "index.jsp")
                response.sendRedirect("Login.html");
            } else {
                //normalize the username value
                usernameFromParameter = usernameFromParameter.trim();
                if (gameManager.m_OnlinePlayers.containsKey("usernameFromSession")) {
                    String errorMessage = "Username " + usernameFromParameter + " already exists. Please enter a different username.";
                    //username already exists, forward the request back to index.jsp
                    //with a parameter that indicates that an error should be displayed
                    request.setAttribute(Constants.USER_NAME_ERROR, errorMessage);
                    getServletContext().getRequestDispatcher("/Login.jsp").forward(request, response);
                } else {
                    //add the new user to the users list
                    Boolean isHuman = false;
                    String userInput = request.getParameter(IS_HUMAN);
                    if (userInput!=null){
                        isHuman = userInput.equalsIgnoreCase("on") ? true : false;
                    }
                    //set the username in a session so it will be available on each request
                    //the true parameter means that if a session object does not exists yet
                    //create a new one
                    gameManager.AddUser(usernameFromParameter, isHuman);
                    request.getSession(true).setAttribute(Constants.USERNAME, usernameFromParameter);
                    request.getSession(true).setAttribute(IS_HUMAN, isHuman.toString());

                    //redirect the request to the chat room - in order to actually change the URL
                    response.sendRedirect("GameLobby.html");
                }
            }
        } else {
            //user is already logged in
            response.sendRedirect("GameLobby.html");
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
