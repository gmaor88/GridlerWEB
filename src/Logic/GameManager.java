package Logic;

import javafx.util.Pair;

import java.io.IOException;
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

    public void addGameRoom(String i_GameRoomName, GameRoom i_GameRoom){
        m_GameRooms.put(i_GameRoomName, i_GameRoom);
    }

    public GamePlayer getGamePlayerByName(String i_Name){
        return m_OnlinePlayers.get(i_Name);
    }

    public GameRoom getGameRoomByName(String i_Name){
        return m_GameRooms.get(i_Name);
    }

    public void addUserToGameRoom(String i_PlayerName, String i_GameRoomName) throws  IOException{
        GamePlayer playerToAdd = m_OnlinePlayers.get(i_PlayerName);
        GameRoom gameRoomRequested = m_GameRooms.get(i_GameRoomName);

        gameRoomRequested.addPlayerToGameRoom(playerToAdd);
    }

    public void RemoveUserFromGameRoom(String i_PlayerName, String i_GameRoomName){
        GamePlayer playerToRemove = m_OnlinePlayers.get(i_PlayerName);
        GameRoom gameRoomRequested = m_GameRooms.get(i_GameRoomName);

        gameRoomRequested.removePlayerFromGameRoom(playerToRemove);
    }

    public void InsertSpectatorToGameRoom(String i_SpectatorName, String i_GameRoomName)throws IOException{
        GamePlayer spectatorToAdd = m_OnlinePlayers.get(i_SpectatorName);
        GameRoom gameRoomRequested = m_GameRooms.get(i_GameRoomName);

        if(!gameRoomRequested.IsGameRunning()){
            throw new IOException("Invalid action. spectate allowed only when game is running");
        }

        gameRoomRequested.InsertSpectatorToGameRoom(spectatorToAdd);
    }

    public void DeleteSpectatorFromGameRoom(String i_SpectatorName, String i_GameRoomName){
        GamePlayer spectatorToRemove = m_OnlinePlayers.get(i_SpectatorName);
        GameRoom gameRoomRequested = m_GameRooms.get(i_GameRoomName);

        gameRoomRequested.DeleteSpectatorFromGameRoom(spectatorToRemove);
    }
}
