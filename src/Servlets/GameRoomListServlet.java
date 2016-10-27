package Servlets;

import Logic.GameManager;
import Logic.GameRoom;
import Utils.ServletUtils;
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
 * Created by Maor Gershkovitch on 10/25/2016.
 */
@WebServlet(name = "GameRoomListServlet", urlPatterns = {"/GameRoomListServlet"})
public class GameRoomListServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GameManager gameManager = ServletUtils.getGameManager(getServletContext());
            GameRoomsData gameRoomData = new GameRoomsData(gameManager.m_GameRooms);
            String json = gson.toJson(gameRoomData);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    class GameRoomsData{
        private final ArrayList<GameInfo> GameRooms = new ArrayList<>();

        public GameRoomsData(HashMap<String, GameRoom> i_GameRooms){
            GameInfo gameInfo;
            for(Map.Entry<String,GameRoom> entry : i_GameRooms.entrySet()){

                gameInfo = new GameInfo();
                gameInfo.setBoardSize(entry.getValue().getBoardHeight(), entry.getValue().getBoardWidth());
                gameInfo.setCurrentNumOfPlayers(entry.getValue().getCurrentNumOfPlayers());
                gameInfo.setGameCreator(entry.getValue().getCreatorName());
                gameInfo.setMaxNumOfPlayers(entry.getValue().getMaxNumOfPlayers());
                gameInfo.setTurnLimit(entry.getValue().getTurnLimit());
                gameInfo.setGameName(entry.getKey());
                GameRooms.add(gameInfo);
            }
        }

        class GameInfo{
            private String GameCreator;
            private Integer MaxNumOfPlayers;
            private Integer CurrentNumOfPlayers;
            private Boolean IsGameRunning;
            private String BoardSize;
            private Integer TurnLimit;
            private String GameName;


            public void setBoardSize(Integer i_Rows, Integer i_Cols) {
                BoardSize = i_Rows + " X " + i_Cols;
            }

            public void setCurrentNumOfPlayers(Integer i_CurrentNumOfPlayers) {
                CurrentNumOfPlayers = i_CurrentNumOfPlayers;
            }

            public void setGameCreator(String i_GameCreator) {
                this.GameCreator = i_GameCreator;
            }

            public void setMaxNumOfPlayers(Integer i_MaxNumOfPlayers) {
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