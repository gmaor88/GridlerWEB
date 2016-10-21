package Logic;

import java.util.HashMap;

/**
 * Created by Maor Gershkovitch on 10/19/2016.
 */
public class GameManager {
    public static HashMap<String, GameRoom> m_GameRooms = new HashMap<>();
    public static HashMap<String, GamePlayer> m_OnlinePlayers = new HashMap<>();

    public void AddUser(String i_userName, Boolean i_isHuman){
        m_OnlinePlayers.put(i_userName, new GamePlayer(i_isHuman, i_userName));
    }

    public void RemoveUser(String i_userName){
        m_OnlinePlayers.remove(i_userName);
    }
}
