package Utils;

import Logic.GameBoard;
import Logic.GamePlayer;
import Logic.Square;
import javafx.util.Pair;
import jaxb.GameDescriptor;

import java.util.ArrayList;

/**
 * Created by dan on 8/19/2016.
 * Class for handling the parsing and of the given xml.
 * Verifies the data from the xml is in the right form.
 */


public class GameLoader {
    public GameBoard loadBoard(GameDescriptor i_GameDescriptor) throws GameLoadException{
        int columns, rows, blocks[], numberOfSlices, slicesId, numberOfBlackSquares;
        int columnIndex, rowIndex;
        GameBoard board;

        //get basic data from xml and look for exceptions
        if(!i_GameDescriptor.getGameType().equalsIgnoreCase("singleplayer") && !i_GameDescriptor.getGameType().equalsIgnoreCase("multiplayer")){
            throw new GameLoadException("Invalid game type format");
        }

        columns = i_GameDescriptor.getBoard().getDefinition().getColumns().intValue();
        rows = i_GameDescriptor.getBoard().getDefinition().getRows().intValue();
        numberOfSlices = i_GameDescriptor.getBoard().getDefinition().getSlices().getSlice().size();
        numberOfBlackSquares = i_GameDescriptor.getBoard().getSolution().getSquare().size();
        if(rows + columns != numberOfSlices){
            throw new GameLoadException("Invalid number of slices");
        }
        else if(rows*columns < numberOfBlackSquares){
            throw new GameLoadException("Invalid number of black squares");
        }

        board = new GameBoard(rows,columns);

        //gets slices from xml file while also looking for exceptions
        for(int i = 0; i < numberOfSlices ; i++){
            blocks = getBlocks(i_GameDescriptor.getBoard().getDefinition().getSlices().getSlice().get(i).getBlocks());
            slicesId = i_GameDescriptor.getBoard().getDefinition().getSlices().getSlice().get(i).getId().intValue() - 1;
            if(i_GameDescriptor.getBoard().getDefinition().getSlices().getSlice().get(i).getOrientation().equalsIgnoreCase("row")){
                if(getNumberOfBlackSquare(blocks) > columns){
                    throw new GameLoadException("Invalid number of blocks in column "+ slicesId);
                }

                board.setHorizontalSlice(slicesId,blocks);
            }
            else if(i_GameDescriptor.getBoard().getDefinition().getSlices().getSlice().get(i).getOrientation().equalsIgnoreCase("column")){
                if(getNumberOfBlackSquare(blocks) > rows){
                    throw new GameLoadException("Invalid number of blocks in row "+ slicesId);
                }

                board.setVerticalSlice(slicesId, blocks);
            }
            else{
                throw new GameLoadException("Invalid orientation");
            }
        }

        //inserts true value to designated squares
        ArrayList<Pair<Integer, Integer>> squares = new ArrayList<>();
        for(int i = 0; i < numberOfBlackSquares; i++) {
            rowIndex = i_GameDescriptor.getBoard().getSolution().getSquare().get(i).getRow().intValue();
            columnIndex = i_GameDescriptor.getBoard().getSolution().getSquare().get(i).getColumn().intValue();
            Pair<Integer, Integer> square = new Pair<>(rowIndex, columnIndex);
            if(squares.contains(square)){
                throw new GameLoadException("Solution given holds two or more of the same Square.");
            }

            try {
                squares.add(square);
                board.getSquare(rowIndex - 1, columnIndex - 1).setTrueSquareSignValue(Square.eSquareSign.BLACKED); //was without -1
            }
            catch (ArrayIndexOutOfBoundsException e){
                throw new GameLoadException(e.getMessage());
            }
        }

        return board;
    }

    public ArrayList<GamePlayer> loadPlayer(GameDescriptor i_GameDescriptor) throws GameLoadException{
        if(i_GameDescriptor.getGameType().equalsIgnoreCase("multiplayer")){
            return loadMultiPlayer(i_GameDescriptor);
        }
        else if(i_GameDescriptor.getGameType().equalsIgnoreCase("singleplayer")){
            return loadSinglePlayer();
        }
        else{
            throw new GameLoadException("Invalid game type");
        }
    }

    private ArrayList<GamePlayer> loadSinglePlayer() {
        ArrayList<GamePlayer> players = new ArrayList<>();

        players.add(new GamePlayer(true,"",""));

        return players;
    }

    private ArrayList<GamePlayer> loadMultiPlayer(GameDescriptor i_GameDescriptor) throws GameLoadException{
        ArrayList<GamePlayer> players = new ArrayList<>();
        String playerId,playerName;
        Integer maxNumberOfMoves, numberOfPlayers;
        boolean humanPlayer;
        //gives you the basic option to load player data from xml file


        numberOfPlayers = i_GameDescriptor.getMultiPlayers().getPlayers().getPlayer().size();
        if(numberOfPlayers < 1){
            throw new GameLoadException("Invalid number of players");
        }

        for(int i = 0; i < numberOfPlayers; i++) {
            playerId = i_GameDescriptor.getMultiPlayers().getPlayers().getPlayer().get(i).getId().toString();
            for (GamePlayer player : players){
                if(playerId.equalsIgnoreCase(player.getId())){
                    throw new GameLoadException("2 different players with the same ID.");
                }
            }
            playerName = i_GameDescriptor.getMultiPlayers().getPlayers().getPlayer().get(i).getName();
            humanPlayer = i_GameDescriptor.getMultiPlayers().getPlayers().getPlayer().get(i).getPlayerType().equalsIgnoreCase("Human");
            players.add(new GamePlayer(humanPlayer, playerName, playerId));
        }

        if (!Tools.tryParseInt(i_GameDescriptor.getMultiPlayers().getMoves())) {
            throw new GameLoadException("Invalid move limit");
        }

        maxNumberOfMoves = Integer.parseInt(i_GameDescriptor.getMultiPlayers().getMoves());
        for (int i = 0; i < numberOfPlayers; i++) {
            players.get(i).setMoveLimit(maxNumberOfMoves);
        }


        return  players;
    }

    public boolean isGameTypeSinglePlayer(GameDescriptor i_GameDescriptor){
        return i_GameDescriptor.getGameType().equalsIgnoreCase("singleplayer");
    }

    private int getNumberOfBlackSquare(int[] blocks) {
        int sum = 0;

        for(int i:blocks){
            sum += i + 1;
        }

        return  sum - 1;
    }

    private int[] getBlocks(String i_Blocks) throws GameLoadException {
        int blocks[];
        char ch;
        ArrayList<Integer> intermediate = new ArrayList<>(1);
        //first we trim any whitespace
        //then we split the string in to smaller ones with the , separator
        //then intermediate gets the numbers from the sub strings

        i_Blocks = i_Blocks.replaceAll(" ","");
        for(int i = 0; i < i_Blocks.length(); i++){
            ch = i_Blocks.charAt(i);
            if(('9' < ch || ch < '0') && ch != ','){
                throw new GameLoadException("Invalid string of blocks");
            }
        }

        for(String str: i_Blocks.split(",")){
            if(Tools.tryParseInt(str)) {
                intermediate.add(Integer.parseInt(str));
            }
        }

        blocks = new int[intermediate.size()];
        for(int i = 0; i < intermediate.size(); i++){
            blocks[i] = intermediate.get(i);
        }

        return blocks;
    }
}
