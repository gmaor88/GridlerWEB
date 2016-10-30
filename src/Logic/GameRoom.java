package Logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Maor Gershkovitch on 10/19/2016.
 */
public class GameRoom {
    private LinkedList<GamePlayer> m_Players;
    private final GameBoard f_Board;
    private final Integer f_MaxNumOfPlayers;
    private Boolean m_IsGameRunning = false;
    private final Integer f_TurnLimit;
    private final String f_CreatorName;
    private GamePlayer m_CurrentPlayer;
    private Integer m_Index = 0;

    public GameRoom(GameBoard i_Board, Integer i_MaxNumOfPlayers, Integer i_TurnLimit, String i_CreatorName){
        f_MaxNumOfPlayers = i_MaxNumOfPlayers;
        f_TurnLimit = i_TurnLimit;
        f_Board = i_Board;
        m_Players = new LinkedList<>();
        f_CreatorName = i_CreatorName;
    }

    public void InsertPlayerToGameRoom(GamePlayer i_Player){
        if(m_Players.size() >= f_MaxNumOfPlayers){
            return;
        }
        else if(m_Players.contains(i_Player)){
            return;
        }

        i_Player.setGameBoard(f_Board);
        m_Players.addLast(i_Player);
        m_CurrentPlayer = m_Players.getFirst();
    }

    public void DeletePlayerFromGameRoom(String i_PlayerToDelete){
        for(GamePlayer player : m_Players){
            if( player.getName().equalsIgnoreCase(i_PlayerToDelete)){
                m_Players.remove(player);
                return;
            }
        }
    }

    public void EndTurn(){
        m_Index++;
        if(m_Index >= m_Players.size()){
            m_Index = 0;
        }

        m_CurrentPlayer = m_Players.get(m_Index);
    }

    public Boolean getIsGameRunning() {
        return m_IsGameRunning;
    }

    public void setIsGameRunning(Boolean m_IsGameRunning) {
        this.m_IsGameRunning = m_IsGameRunning;
    }

    public Integer getMaxNumOfPlayers() {
        return f_MaxNumOfPlayers;
    }

    public String getCreatorName() {
        return f_CreatorName;
    }

    public Integer getTurnLimit() {
        return f_TurnLimit;
    }

    public Integer getCurrentNumOfPlayers(){
        return m_Players.size();
    }

    public GameRoomData getGameRoomData() {
        GameRoomData gameRoomData = new GameRoomData(m_Players, m_CurrentPlayer.getName());

        return gameRoomData;
    }

    public Integer getBoardWidth(){
        return f_Board.getBoardWidth();
    }

    public Integer getBoardHeight(){
        return f_Board.getBoardHeight();
    }

    public String getCurrentPlayerName(){
        return m_CurrentPlayer.getName();
    }

    public void addPlayerToGameRoom(GamePlayer i_PlayerToAdd) throws IOException{
        if(m_Players.size() >= f_MaxNumOfPlayers || m_Players.contains(i_PlayerToAdd) || m_IsGameRunning){
            throw new IOException("Unable to login to game room. (Game room is full/running or, player is already registered to it)");
        }

        i_PlayerToAdd.init();
        i_PlayerToAdd.setGameBoard(f_Board);
        i_PlayerToAdd.setMoveLimit(f_TurnLimit);
        m_Players.add(i_PlayerToAdd);
    }

    ///For GameRoomPlayerListServlet ////
    class GameRoomData{
        private final ArrayList<PlayerInfo> players = new ArrayList<>();
        private final String CurrentPlayer;

        public GameRoomData(LinkedList<GamePlayer> i_PlayerInGameRoom, String i_CurrentPlayer){
            PlayerInfo playerInfo;

            CurrentPlayer = i_CurrentPlayer;
            for(GamePlayer player : i_PlayerInGameRoom){
                playerInfo = new PlayerInfo();
                playerInfo.setIsHuman(player.getIsHuman());
                playerInfo.setPlayerName(player.getName());
                playerInfo.setScore(player.getTurnLimit());
                players.add(playerInfo);
            }
        }

        class PlayerInfo{
            private String Name;
            private Integer Score;
            private Boolean IsHuman;



            public void setPlayerName(String i_PlayerName) {
                this.Name = i_PlayerName;
            }

            public void setScore(Integer i_Score) {
                this.Score = i_Score;
            }

            public void setIsHuman(Boolean i_IsHuman) {
                this.IsHuman = i_IsHuman;
            }

        }

    }
}