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
import java.io.PrintWriter;

/**
 * Created by Maor Gershkovitch on 10/29/2016.
 */
@WebServlet(name = "JoinGameServlet", urlPatterns = "/JoinGameServlet")
public class JoinGameServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        try {
            GameManager gameManager = ServletUtils.getGameManager(getServletContext());
            String usernameFromSession = SessionUtils.getUsername(request);
            String gameRoomRequested = request.getParameter("game");

            gameManager.addUserToGameRoom(usernameFromSession, gameRoomRequested);
            request.getSession(true).setAttribute(Constants.CHOSEN_GAME, gameRoomRequested);
            response.setStatus(200);
            response.sendRedirect("GameRoom.html");
        } catch (IOException e) {
            try (PrintWriter out = response.getWriter()) {
                response.setStatus(400);
                out.print(e.getMessage().toString());
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
