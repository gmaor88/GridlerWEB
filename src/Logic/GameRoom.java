package Logic;

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

    public GameRoom(GameBoard i_Board, Integer i_MaxNumOfPlayers, Integer i_TurnLimit){
        f_MaxNumOfPlayers = i_MaxNumOfPlayers;
        f_TurnLimit = i_TurnLimit;
        f_Board = i_Board;
        m_Players = new LinkedList<>();
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
    }

    public void DeletePlayerFromGameRoom(String i_PlayerToDelete){
        for(GamePlayer player : m_Players){
            if( player.getName().equalsIgnoreCase(i_PlayerToDelete)){
                m_Players.remove(player);
                return;
            }
        }
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
}
