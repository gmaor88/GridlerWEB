package Logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by Maor Gershkovitch on 10/19/2016.
 */
public class GameRoom {
    private LinkedList<GamePlayer> m_Players;
    private ArrayList<GamePlayer> m_Spectators;
    private final GameBoard f_Board;
    private final Integer f_MaxNumOfPlayers;
    private Boolean m_IsGameRunning = false;
    private Boolean m_IsGameAtEndPhase = false;
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

    public void InsertSpectatorToGameRoom(GamePlayer i_Player){
        m_Spectators.add(i_Player);
    }

    public void DeleteSpectatorFromGameRoom(String i_PlayerToDelete){
        for(GamePlayer player : m_Spectators){
            if( player.getName().equalsIgnoreCase(i_PlayerToDelete)){
                m_Spectators.remove(player);
                return;
            }
        }
    }

    public void setIsGameAtEndPhase(Boolean i_IsGameAtEndPhase){
        m_IsGameAtEndPhase = i_IsGameAtEndPhase;
    }

    public void EndTurn(){
        m_CurrentPlayer.endTurn();
        m_Index++;
        if(m_Index >= m_Players.size()){
            m_Index = 0;
        }

        m_CurrentPlayer = m_Players.get(m_Index);
    }

    public Boolean IsGameRunning() {
        return m_IsGameRunning;
    }

    public void setIsGameRunning(Boolean i_IsGameRunning) {
        m_IsGameAtEndPhase = m_IsGameRunning && !i_IsGameRunning && m_Players.size() > 0;
        m_IsGameRunning = i_IsGameRunning;
    }

    public Boolean hasPlayerWon(String i_PlayerName){
        return hasPlayerWon(getGamePlayerByName(i_PlayerName));
    }

    public Boolean hasPlayerWon(GamePlayer i_player) {
        Boolean result = i_player.getScore() == 100;

        if(!result){
            result = m_Players.size() == 1 && f_MaxNumOfPlayers > 1;
        }

        return result;
    }

    public Boolean isDraw(){
        return !m_Players.getLast().checkIfPlayerHasTurnLeft();
    }

    public Boolean hasPlayerLost(String i_PlayerName){
        Boolean result = false;

        for(GamePlayer player: m_Players){
            if(hasPlayerWon(player)){
                result = !player.getName().equalsIgnoreCase(i_PlayerName);
                break;
            }
        }

        return result;
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
        String currentPlayerName = "";

        if(m_CurrentPlayer != null){
            currentPlayerName = m_CurrentPlayer.getName();
        }

        GameRoomData gameRoomData = new GameRoomData(m_Players, currentPlayerName);

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

     void addPlayerToGameRoom(GamePlayer i_PlayerToAdd) throws IOException{
        if(m_Players.size() >= f_MaxNumOfPlayers || m_Players.contains(i_PlayerToAdd) || m_IsGameRunning){
            throw new IOException("Unable to login to game room. (Game room is full/running or, player is already registered to it)");
        }
        else if(m_IsGameAtEndPhase){
            throw new IOException("Unable to login to game room. Game Room is unavailable");
        }

        i_PlayerToAdd.init();
        i_PlayerToAdd.setGameBoard(f_Board);
        i_PlayerToAdd.setMoveLimit(f_TurnLimit);
         m_Players.addLast(i_PlayerToAdd);
         m_CurrentPlayer = m_Players.getFirst();
        m_IsGameRunning = m_Players.size() == f_MaxNumOfPlayers;
    }

     void removePlayerFromGameRoom(GamePlayer i_PlayerToRemove) {
         m_Players.remove(i_PlayerToRemove);
         if(m_Players.size() < 1){
             m_IsGameAtEndPhase = false;
         }
     }

    public GamePlayer getGamePlayerByName(String i_PlayerName){
        GamePlayer result = null;

        for (GamePlayer player : m_Players){
            if(player.getName().equalsIgnoreCase(i_PlayerName)){
                result = player;
                break;
            }
        }

        return result;
    }

    public PlayerData getGamePlayerData(String i_PlayerName) {
        PlayerData playerData = new PlayerData(getGamePlayerByName(i_PlayerName));

        return playerData;
    }

    public GamePlayer.BoardData getGamePlayerBoardData(String i_PlayerName){
        return getGamePlayerByName(i_PlayerName).getGameBoardData();
    }

    ///For GameRoomPlayerListServlet ////
    class GameRoomData{
        private final ArrayList<PlayerInfo> players = new ArrayList<>();
        private final String CurrentPlayer;

         GameRoomData(LinkedList<GamePlayer> i_PlayerInGameRoom, String i_CurrentPlayer){
            PlayerInfo playerInfo;

            CurrentPlayer = i_CurrentPlayer;
            for(GamePlayer player : i_PlayerInGameRoom){
                playerInfo = new PlayerInfo();
                playerInfo.setIsHuman(player.getIsHuman());
                playerInfo.setPlayerName(player.getName());
                playerInfo.setScore(player.getScore());
                players.add(playerInfo);
            }
        }

        class PlayerInfo{
            private String Name;
            private Double Score;
            private String PlayerType;



            void setPlayerName(String i_PlayerName) {
                this.Name = i_PlayerName;
            }

            void setScore(Double i_Score) {
                this.Score = i_Score;
            }

            void setIsHuman(Boolean i_IsHuman) {
                PlayerType = "Human";

                if(!i_IsHuman){
                    PlayerType = "PC";
                }
            }

        }

    }

    ///For PlayerDataServlet ////
    class PlayerData{
        private String PlayerName;
        private Double Score;
        private Integer MovesLeftInTurn;
        private Integer TurnLeftInGame;
        private Boolean IsHumanPlayer;
        private Boolean IsUndoAvailable;
        private Boolean IsRedoAvailable;

        public PlayerData(GamePlayer i_Player){
            PlayerName = i_Player.getName();
            Score = i_Player.getScore();
            TurnLeftInGame = i_Player.getTurnLimit() - i_Player.getTurnNumber();
            MovesLeftInTurn = 2 - i_Player.getNumOfMovesMade();
            IsHumanPlayer = i_Player.getIsHuman();
            IsUndoAvailable = i_Player.isUndoAvailable();
            IsRedoAvailable = i_Player.isRedoAvailable();
        }
    }
}