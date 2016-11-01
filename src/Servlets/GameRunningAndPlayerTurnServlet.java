package Servlets;

import Logic.GameManager;
import Logic.GameRoom;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;
import javafx.util.Pair;

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
@WebServlet(name = "GameRunningAndPlayerTurnServlet", urlPatterns = "/GameRunningAndPlayerTurnServlet")
public class GameRunningAndPlayerTurnServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GameManager gameManager = ServletUtils.getGameManager(getServletContext());
            GameRoom gameRoom = gameManager.getGameRoomByName(SessionUtils.getChosenGame(request));
            String playerName = SessionUtils.getUsername(request);
            String currentPlayerTurn = gameRoom.getCurrentPlayerName();
            Boolean gameRunning = gameRoom.IsGameRunning();
            Pair<Boolean,Boolean> data = new Pair<>(currentPlayerTurn.equalsIgnoreCase(playerName),gameRunning);
            String json = gson.toJson(data);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
