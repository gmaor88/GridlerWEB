package Logic;

import java.util.ArrayList;

/**
 * Created by Maor Gershkovitch on 10/19/2016.
 */
public class GameRoom {
    private ArrayList<GamePlayer> m_Players;
    private final GameBoard f_Board;
    private final Integer f_MaxNumOfPlayers;
    private Boolean m_IsGameRunning = false;

    public GameRoom(GameBoard i_Board, Integer i_MaxNumOfPlayers){
        f_MaxNumOfPlayers = i_MaxNumOfPlayers;
        f_Board = i_Board;
        m_Players = new ArrayList<>(f_MaxNumOfPlayers);
    }

    public void InsertPlayerToGameRoom(GamePlayer i_Player){
        if(m_Players.size() >= f_MaxNumOfPlayers){
            return;
        }
        else if(m_Players.contains(i_Player)){
            return;
        }

        i_Player.setGameBoard(f_Board);
        m_Players.add(i_Player);
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
