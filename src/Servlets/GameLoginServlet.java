package Servlets;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Maor Gershkovitch on 10/18/2016.
 */
@WebServlet(name = "GameLoginServlet", urlPatterns = {"/GameLoginServlet"})



public class GameLoginServlet extends HttpServlet {

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String playersJson = "";
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            List<PlayerDetails> players;
            RummikubWebService rummikubWS = (RummikubWebService)getServletContext().getAttribute(RUMMIKUB_WS);
            String gameName = request.getParameter(GAME_NAME);
            try {
                players = rummikubWS.getPlayersDetails(gameName);
                playersJson = new Gson().toJson(players);
                out.print(playersJson);
            } catch (GameDoesNotExists_Exception ex) {
                out.print("<font size='3' color='red'>" + ex.getMessage() + "</font>");
            }
        }
    }
}
