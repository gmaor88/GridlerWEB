package Servlets;

import Logic.GameManager;
import Logic.GamePlayer;
import Logic.GameRoom;
import Utils.ServletUtils;
import Utils.SessionUtils;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
@WebServlet(name = "PlayerDataServlet", urlPatterns = "/PlayerDataServlet")
public class PlayerDataServlet extends HttpServlet {
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
            GamePlayer player = gameManager.getGamePlayerByName(playerName);
            if(gameRoom.IsSpectator(player)){
                playerName = gameRoom.getCurrentPlayerName();
            }

            String json = gson.toJson(gameRoom.getGamePlayerData(playerName));
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
