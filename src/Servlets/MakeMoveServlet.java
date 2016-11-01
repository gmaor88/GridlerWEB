package Servlets;

import Logic.GameManager;
import Logic.GameRoom;
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
 * Created by Maor Gershkovitch on 11/1/2016.
 */
@WebServlet(name = "MakeMoveServlet", urlPatterns = "/MakeMoveServlet")
public class MakeMoveServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            GameManager gameManager = ServletUtils.getGameManager(getServletContext());
            GameRoom gameRoom = gameManager.getGameRoomByName(SessionUtils.getChosenGame(request));
            String playerName = SessionUtils.getUsername(request);
            String ColorToChange = request.getParameter("choice");
            String ButtonsToChange = request.getParameter("data");
            gameRoom.getGamePlayerByName(playerName).preformPlayerMove(ButtonsToChange,ColorToChange);
            response.setStatus(200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
