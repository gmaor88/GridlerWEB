package Servlets;

import Logic.GameManager;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
@WebServlet(name = "GameRoomPlayersListServlet", urlPatterns = "/GameRoomPlayersListServlet")
public class GameRoomPlayersListServlet extends HttpServlet {
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
            String json = gson.toJson(gameRoom.getPlayersNamesAndIsHuman());
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class GameRoomData{
        private final ArrayList<GameRoomListServlet.GameRoomsData.GameInfo> players = new ArrayList<>();
        private final String ChosenGame;

        public GameRoomData(HashMap<String, GameRoom> i_GameRooms, String i_ChosenGame){
            GameRoomListServlet.GameRoomsData.GameInfo gameInfo;

            ChosenGame = i_ChosenGame;
            for(Map.Entry<String,GameRoom> entry : i_GameRooms.entrySet()){
                gameInfo = new GameRoomListServlet.GameRoomsData.GameInfo();
                gameInfo.setBoardSize(entry.getValue().getBoardHeight(), entry.getValue().getBoardWidth());
                gameInfo.setCurrentNumOfPlayers(entry.getValue().getCurrentNumOfPlayers());
                gameInfo.setGameCreator(entry.getValue().getCreatorName());
                gameInfo.setMaxNumOfPlayers(entry.getValue().getMaxNumOfPlayers());
                gameInfo.setTurnLimit(entry.getValue().getTurnLimit());
                gameInfo.setGameName(entry.getKey());
                GameRooms.add(gameInfo);
            }
        }

        class PlayerInfo{
            private String Name;
            private Integer Score;
            private Boolean IsHuman;



            public void setCurrentNumOfPlayers(Integer i_CurrentNumOfPlayers) {
                CurrentNumOfPlayers = i_CurrentNumOfPlayers;
            }

            public void setPlayerName(String i_PlayerName) {
                this.Name = i_PlayerName;
            }

            public void setMaxNumOfPlayers(Integer i_Score) {
                this.MaxNumOfPlayers = i_MaxNumOfPlayers;
            }

            public void setTurnLimit(Integer i_TurnLimit) {
                this.TurnLimit = i_TurnLimit;
            }

            public void setGameName(String m_GameName) {
                this.GameName = m_GameName;
            }
        }

    }
}
