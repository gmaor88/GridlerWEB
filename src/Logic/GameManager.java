package Logic;

import javafx.util.Pair;

import java.util.*;

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

    public ArrayList<Pair<String,String>> getPlayersNamesAndIsHuman() {
        ArrayList<Pair<String,String>> playersToReturn = new ArrayList<>();

        for(Map.Entry<String,GamePlayer> entry: m_OnlinePlayers.entrySet()){
            playersToReturn.add(new Pair<>(entry.getKey(),playerDesignation(entry.getValue())));
        }

        return playersToReturn;
    }

    private String playerDesignation(GamePlayer i_Player) {
        String result = "Human";

        if(!i_Player.getIsHuman()){
            result = "PC";
        }

        return result;
    }
}
