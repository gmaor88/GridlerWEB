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
 * Created by Maor Gershkovitch on 11/3/2016.
 */
@WebServlet(name = "LoseDrawServlet", urlPatterns = "/LoseDrawServlet")
public class LoseDrawServlet extends HttpServlet {
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
            Pair<Boolean,Boolean> drawLose = new Pair<>(gameRoom.isDraw(),gameRoom.hasPlayerLost(playerName));
            if(drawLose.getKey() || drawLose.getValue()){
                gameRoom.setIsGameRunning(false);
            }
            String json = gson.toJson(drawLose);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
