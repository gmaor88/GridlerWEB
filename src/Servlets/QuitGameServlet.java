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

/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
@WebServlet(name = "QuitGameServlet", urlPatterns = "/QuitGameServlet")
public class QuitGameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        GameManager gameManager = ServletUtils.getGameManager(getServletContext());
        String usernameFromSession = SessionUtils.getUsername(request);
        String gameRoomFromSession = SessionUtils.getChosenGame(request);

        //gameManager.RemoveUserFromGameRoom(usernameFromSession, gameRoomFromSession);
        //response.setStatus(200);
        //try{
            request.getSession(true).setAttribute(Constants.CHOSEN_GAME, "");
            //response.sendRedirect("GameLobby.html");
            gameManager.RemoveUserFromGameRoom(usernameFromSession, gameRoomFromSession);
            response.setStatus(200);
        //}
        //catch (IOException e){
            //e.printStackTrace();
        //}
    }
}
