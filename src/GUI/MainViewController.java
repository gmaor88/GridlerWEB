package GUI;

/**
 * Created by dan on 9/5/2016.
 */

import Logic.*;
import Utils.GameLoadException;
import Utils.GameLoader;
import Utils.JaxBGridlerClassGenerator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import jaxb.GameDescriptor;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainViewController implements Initializable {
    private Stage m_Stage;
    private ArrayList<GamePlayer> m_Players = new ArrayList<>();
    private GameBoard m_LoadedBoard;
    private GamePlayer m_CurrentPlayer;
    private int m_CurrentPlayerIndex;
    private HashMap<Pair<Integer, Integer>, Button> m_ButtonsSelected = new HashMap();
    private MoveSet m_CurrentMove;
    private ArrayList<ArrayList<Label>> m_HorizontalBlocksLabel = new ArrayList<>();
    private ArrayList<ArrayList<Label>> m_VerticalBlocksLabel = new ArrayList<>();
    private ArrayList<ArrayList<Button>> m_GameBoardButtons = new ArrayList<>();
    private ArrayList<MenuItem> m_PlayersBoardsMenuItems = new ArrayList<>();
    private Timer timer;
    private boolean m_IsGameTypeSinglePlayer;
    private boolean m_IsGameInEndPhase;
    private final String k_ButtonSelectedStyleClass = "buttonSelected";
    private final String k_UndefCellStyleId = "undefCell";
    private final String k_BlackedCellStyleId = "blackedCell";
    private final String k_ClearedCellStyleId = "clearedCell";
    private final String k_BoardButtonStyleClass = "boardButton";
    private final String k_IncompleteBlockStyleId = "incompleteBlock";
    private final String k_PerfectBlockStyleId = "perfectBlock";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initPlayerDataLabel();
    }

    public void init(Stage i_Stage) {
        m_Stage = i_Stage;
        m_Stage.setOnCloseRequest((event) -> StopTimer());
    }

    private void StopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @FXML
    private MenuItem loadGameMenuItem;
    @FXML
    private MenuItem startGameMenuItem;
    @FXML
    private MenuItem endGameMenuItem;
    @FXML
    private MenuItem UndoMenuItem;
    @FXML
    private MenuItem RedoMenuItem;
    @FXML
    private MenuItem instructionMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private Label playersNameLabel;
    @FXML
    private Label IDLabel;
    @FXML
    private Label movesLeftInTurnLabel;
    @FXML
    private Label turnsLeftInGameLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label timerLabel;
    @FXML
    private RadioButton blackRadioButton;
    @FXML
    private RadioButton clearedRadioButton;
    @FXML
    private RadioButton undefinedRadioButton;
    @FXML
    private TextArea commentTextArea;
    @FXML
    private Button makeMoveButton;
    @FXML
    private Button endTurnButton;
    @FXML
    private GridPane BoardGridPane;
    @FXML
    private MenuItem showStatisticsMenuItem;
    @FXML
    private BorderPane mainBoarderPane;
    @FXML
    private Menu PlayersBoardsMenu;
    @FXML
    private RadioMenuItem defaultSkinRadioMenuItem;
    @FXML
    private RadioMenuItem sunsetSkinRadioMenuItem;
    @FXML
    private RadioMenuItem oceanSkinRadioMenuItem;
    @FXML
    private MenuItem ShowMovesListMenuItem;
    @FXML
    private MenuItem player1BoardMenuItem;
    @FXML
    private Menu NavigatorMenu;
    @FXML
    private MenuItem navigateToTheStartMenuItem;
    @FXML
    private MenuItem navigateToTheEndMenuItem;
    @FXML
    private MenuItem navigateBackMenuItem;
    @FXML
    private MenuItem navigateForwardMenuItem;

    @FXML
    private void navigateToTheStartOnClick() {
        m_CurrentPlayer.moveToStart();
        showBoard(m_CurrentPlayer);
        enableDisableControlButtons(true);
    }

    @FXML
    private void navigateToTheEndOnClick() {
        m_CurrentPlayer.moveToEnd();
        showBoard(m_CurrentPlayer);
        enableDisableControlButtons(true);
    }

    @FXML
    private void navigateBackOnClick() {
        m_CurrentPlayer.backwards();
        showBoard(m_CurrentPlayer);
        enableDisableControlButtons(true);
    }

    @FXML
    private void navigateForwardOnClick() {
        m_CurrentPlayer.forwards();
        showBoard(m_CurrentPlayer);
        enableDisableControlButtons(true);
    }

    @FXML
    private void ShowMovesListMenuItemOnClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/ShowMoveList.fxml"));
            Parent root = fxmlLoader.load();
            ShowMoveListController controller = (ShowMoveListController) fxmlLoader.getController();
            ObservableList<String> items = FXCollections.observableList(m_CurrentPlayer.getMoveList());
            controller.getMoveListListView().setItems(items);
            setNewWindowModalStage(root, m_CurrentPlayer.getName() + "  Move list");
        } catch (IOException e) {
            showErrorMsg("FXML loading error", "statistics fxml could not be loaded");
        }
    }

    public void setNewWindowModalStage(Parent i_root, String i_header) {
        Stage stage = new Stage();

        stage.setTitle(i_header);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.initOwner(m_Stage);
        stage.setScene(new Scene(i_root, 400, 400));
        stage.show();
    }

    @FXML
    private void PlayersBoardsMenuOnClick() {

    }

    @FXML
    private void defaultSkinRadioMenuItemOnClick() {
        Scene scene = m_Stage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(getClass().getResource("CSS/defaultSkin.css").toExternalForm());
    }

    @FXML
    private void sunsetSkinRadioMenuItemOnClick() {
        Scene scene = m_Stage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(getClass().getResource("CSS/SunsetSkin.css").toExternalForm());
    }

    @FXML
    private void oceanSkinRadioMenuItemOnClick() {
        Scene scene = m_Stage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().addAll(getClass().getResource("CSS/OceanSkin.css").toExternalForm());
    }

    @FXML
    public void endTurnOnClick() {
        enableDisableControlButtons(true);
        setForNextTurnOrMove();
        timer.cancel();
        m_CurrentPlayer.endTurn();
        m_CurrentPlayerIndex++;
        if (m_CurrentPlayerIndex >= m_Players.size()) {
            m_CurrentPlayerIndex = 0;
        }

        m_CurrentPlayer = m_Players.get(m_CurrentPlayerIndex);
        if (!m_CurrentPlayer.checkIfPlayerHasTurnLeft()) {
            updatePlayerDataLabels();
            showBoard(m_CurrentPlayer);
            victoryTieHandler();
            return;
        }

        if (!m_CurrentPlayer.getIsHuman()) {
            clearBoard();
            startTimer();
            m_CurrentPlayer.AiPlay();
            if (m_CurrentPlayer.getScore() == 100) {
                victoryTieHandler();
            } else {
                endTurnOnClick();
            }
        } else {
            updatePlayerDataLabels();
            showBoard(m_CurrentPlayer);
            enableDisableControlButtons(false);
            redoUndoMenuItemsAvailabilityModifier();
        }

    }

    @FXML
    public void loadGameOnClick() {
        FileChooser fileChooser = new FileChooser();
        GameLoader gameLoader = new GameLoader();
        fileChooser.setTitle("Open XML File");
        startGameMenuItem.setDisable(true);
        runningGameButtonsDisable(true);
        loadGameMenuItem.setDisable(false);
        initPlayerDataLabel();
        File file = fileChooser.showOpenDialog(m_Stage);
        if (file != null) {
            try {// use task bar and threads fo bonuses
                GameDescriptor gameDescriptor = JaxBGridlerClassGenerator.FromXmlFileToObject(file.getAbsolutePath());
                m_LoadedBoard = gameLoader.loadBoard(gameDescriptor);
                m_Players = gameLoader.loadPlayer(gameDescriptor);
                m_IsGameTypeSinglePlayer = gameLoader.isGameTypeSinglePlayer(gameDescriptor);
                startGameMenuItem.setDisable(false);
                m_CurrentPlayerIndex = 0;
                m_CurrentPlayer = null;
                clearArrayLists();
                buildBoard();
                createPlayersBoardMenu();
                enableDisableControlButtons(true);
                showStatisticsMenuItem.setDisable(true);
                ShowMovesListMenuItem.setDisable(true);
            } catch (JAXBException e) {
                showErrorMsg("FIle loading error", "Illegal file");
            } catch (GameLoadException ex) {
                showErrorMsg("FIle loading error", ex.getErorMsg());
            }
        }
    }

    private void createPlayersBoardMenu() {
        int i = 0;
        initPlayersBoardMenu();

        for (GamePlayer player : m_Players) {
            final MenuItem playerBoardMenuItem = new MenuItem();
            playerBoardMenuItem.setText(player.getName());
            playerBoardMenuItem.setId(player.getId());//Test
            playerBoardMenuItem.setOnAction((event) -> playerBoardMenuItemClicked(player));
            PlayersBoardsMenu.getItems().add(playerBoardMenuItem);
            m_PlayersBoardsMenuItems.add(i, playerBoardMenuItem);
            playerBoardMenuItem.setDisable(true);
            i++;
        }

        PlayersBoardsMenu.setDisable(false);
        openForWatchPcPlayersBoard();
    }

    private void initPlayersBoardMenu() {
        m_PlayersBoardsMenuItems.clear();
        PlayersBoardsMenu.getItems().clear();
    }

    private void openForWatchPcPlayersBoard() {
        int i = 0;

        for (GamePlayer player : m_Players) {
            if (!player.getIsHuman()) {
                m_PlayersBoardsMenuItems.get(i).setDisable(false);
            }

            i++;
        }
    }

    private void openForWatchAllPlayersBoard() {
        for (MenuItem playerBoard : m_PlayersBoardsMenuItems) {
            playerBoard.setDisable(false);
        }
    }

    private void playerBoardMenuItemClicked(GamePlayer i_Player) {
        if (m_IsGameInEndPhase) {
            m_CurrentPlayer = i_Player;
            showBoard(m_CurrentPlayer);
            enableDisableControlButtons(true);
            updatePlayerDataLabels();
            timer.cancel();
            return;
        }

        showBoard(i_Player);
        if (!i_Player.getIsHuman()) {
            for (MenuItem item : PlayersBoardsMenu.getItems()) {
                if (item.getText().equalsIgnoreCase(m_CurrentPlayer.getName())) {
                    item.setDisable(false);
                }
            }
        } else if (i_Player == m_CurrentPlayer) {
            for (MenuItem item : PlayersBoardsMenu.getItems()) {
                if (item.getText().equalsIgnoreCase(m_CurrentPlayer.getName())) {
                    item.setDisable(true);
                }
            }
        }
    }

    private void clearArrayLists() {
        m_HorizontalBlocksLabel.clear();
        m_VerticalBlocksLabel.clear();
        m_GameBoardButtons.clear();
    }

    @FXML
    public void makeMoveOnClick() {
        Square.eSquareSign sign = Square.eSquareSign.UNDEFINED;

        if (blackRadioButton.isSelected()) {
            sign = Square.eSquareSign.BLACKED;
        } else if (clearedRadioButton.isSelected()) {
            sign = Square.eSquareSign.CLEARED;
        }

        m_CurrentMove = new MoveSet(commentTextArea.getText());
        for (Map.Entry<Pair<Integer, Integer>, Button> entry : m_ButtonsSelected.entrySet()) {
            m_CurrentMove.AddNewPoint(entry.getKey().getKey(), entry.getKey().getValue(), sign);
            setBoardButtonStyle(entry.getValue(), sign);
        }

        if (m_CurrentMove.getPointsList().isEmpty()) {
            showInformationMsg("Make move", "Please mark squares before pressing on Make move button");
        } else {
            m_CurrentPlayer.preformPlayerMove(m_CurrentMove);
            redoUndoMenuItemsAvailabilityModifier();
            makeMoveHandler();
            if (!m_IsGameTypeSinglePlayer) {
                makeMoveButton.setDisable(!m_CurrentPlayer.checkIfPlayerHasMovesLeft());
            }

            setForNextTurnOrMove();
            if (m_CurrentPlayer.getScore() == 100) {
                victoryTieHandler();
            }
        }
    }

    private void makeMoveHandler() {
        m_CurrentPlayer.updateBlocks();
        scoreLabel.setText(((Integer) (int) m_CurrentPlayer.getScore()).toString());
        for (int i = 0; i < m_LoadedBoard.getBoardHeight(); i++) {
            updateBlocks(m_HorizontalBlocksLabel.get(i), m_CurrentPlayer.getHorizontalSlice(i));
        }

        for (int j = 0; j < m_LoadedBoard.getBoardWidth(); j++) {
            updateBlocks(m_VerticalBlocksLabel.get(j), m_CurrentPlayer.getVerticalSlice(j));
        }

        if (!m_IsGameTypeSinglePlayer) {
            movesLeftInTurnLabel.setText(((Integer) (2 - m_CurrentPlayer.getNumOfMovesMade())).toString());
        }
    }

    private void setForNextTurnOrMove() {
        for (Map.Entry<Pair<Integer, Integer>, Button> entry : m_ButtonsSelected.entrySet()) {
            entry.getValue().getStyleClass().remove(k_ButtonSelectedStyleClass);
        }

        m_ButtonsSelected.clear();
        commentTextArea.clear();
    }

    private void setBoardButtonStyle(Button i_Button, Square.eSquareSign i_Sign) {
        String buttonId = k_UndefCellStyleId;

        if (i_Sign == Square.eSquareSign.BLACKED) {
            buttonId = k_BlackedCellStyleId;
        } else if (i_Sign == Square.eSquareSign.CLEARED) {
            buttonId = k_ClearedCellStyleId;
        }

        i_Button.setId(buttonId);
    }

    private void redoUndoMenuItemsAvailabilityModifier() {
        UndoMenuItem.setDisable(!m_CurrentPlayer.isUndoAvailable());
        RedoMenuItem.setDisable(!m_CurrentPlayer.isRedoAvailable() || !m_CurrentPlayer.checkIfPlayerHasMovesLeft());
    }

    @FXML
    public void startGameOnClick() {
        setBoardsOnPlayers();
        m_CurrentPlayer = m_Players.get(m_CurrentPlayerIndex);
        startGameMenuItem.setDisable(true);
        loadGameMenuItem.setDisable(true);
        endGameMenuItem.setDisable(false);
        showStatisticsMenuItem.setDisable(false);
        ShowMovesListMenuItem.setDisable(false);
        if (m_CurrentPlayer.getIsHuman()) {
            showBoard(m_CurrentPlayer);
            updatePlayerDataLabels();
            RedoMenuItem.setDisable(true);
            UndoMenuItem.setDisable(true);
            endTurnButton.setDisable(m_IsGameTypeSinglePlayer);
        } else {
            startTimer();
            m_CurrentPlayer.AiPlay();
            if (m_CurrentPlayer.getScore() == 100) {
                victoryTieHandler();
            } else {
                endTurnOnClick();
            }
        }
    }

    private void victoryTieHandler() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        String headerText = null, contentText = null;
        Boolean victoryOrTieFound = false;

        if (m_CurrentPlayer.getScore() == 100) { //victory
            headerText = "Victory";
            contentText = m_CurrentPlayer.getName() + "has Won!";
            victoryOrTieFound = true;
        } else if (!m_CurrentPlayer.checkIfPlayerHasTurnLeft()) { //tie
            headerText = "Game ended";
            contentText = "all moves were used!";
            victoryOrTieFound = true;
        }

        if (victoryOrTieFound) {
            alert.setHeaderText(headerText);
            alert.setContentText(contentText);
            alert.show();
            timer.cancel();
            enableDisableControlButtons(true);
            runningGameButtonsDisable(false);
            openForWatchAllPlayersBoard();
            endGameMenuItem.setDisable(true);
        }
    }

    private void setBoardsOnPlayers() {
        for (GamePlayer player : m_Players) {
            player.setGameBoard(m_LoadedBoard);
        }
    }

    private void buildBoard() {
        BoardGridPane = new GridPane();
        ScrollPane scrollPane = new ScrollPane(BoardGridPane);
        int j;
        mainBoarderPane.setCenter(scrollPane);
        BoardGridPane.setAlignment(Pos.CENTER);
        BoardGridPane.setPadding(new Insets(40, 20, 20, 40));
        for (int i = 0; i < m_LoadedBoard.getBoardHeight(); i++) {
            m_GameBoardButtons.add(new ArrayList<Button>());
            m_HorizontalBlocksLabel.add(new ArrayList<>());
            for (j = 0; j < m_LoadedBoard.getBoardWidth(); j++) {
                final int column = j;
                final int row = i;
                final Button bSquare = new Button();
                bSquare.setDisable(true);
                bSquare.setOnAction((event) -> buttonClicked(row, column, bSquare));
                bSquare.setAlignment(Pos.CENTER);
                bSquare.setId(k_UndefCellStyleId);
                bSquare.getStyleClass().add(k_BoardButtonStyleClass);
                m_GameBoardButtons.get(i).add(bSquare);
                BoardGridPane.add(bSquare, j, i);
                BoardGridPane.setMargin(bSquare, new Insets(0, 0, 2, 1));
            }

            for (Block block : m_LoadedBoard.getHorizontalSlice(i)) {//was i -1
                addBlockLabel(BoardGridPane, j, i, block.toString(), m_HorizontalBlocksLabel.get(i));
                j++;
            }
        }

        for (int i = 0; i < m_LoadedBoard.getBoardWidth(); i++) {
            j = m_LoadedBoard.getBoardHeight();
            m_VerticalBlocksLabel.add(new ArrayList<>());
            for (Block block : m_LoadedBoard.getVerticalSlice(i)) { //was i - 1
                addBlockLabel(BoardGridPane, i, j, block.toString(), m_VerticalBlocksLabel.get(i));
                j++;
            }
        }
    }

    private void buttonClicked(int i_ColumnIndex, int i_RowIndex, Button i_Button) {
        Pair<Integer, Integer> pair = new Pair<>(i_ColumnIndex, i_RowIndex);

        if (!m_ButtonsSelected.containsKey(pair)) {
            m_ButtonsSelected.put(pair, i_Button);
            i_Button.getStyleClass().add(k_ButtonSelectedStyleClass);
        } else {
            m_ButtonsSelected.remove(pair);
            i_Button.getStyleClass().remove(k_ButtonSelectedStyleClass);
        }
    }

    private void addBlockLabel(GridPane i_GridPane, int i_ColumnIndex, int i_RowIndex, String i_blockSize, ArrayList<Label> i_SliceLabels) {
        Label lBlock = new Label();
        lBlock.setText(i_blockSize);
        lBlock.setId(k_IncompleteBlockStyleId);
        i_SliceLabels.add(lBlock);
        i_GridPane.add(lBlock, i_ColumnIndex, i_RowIndex);
        i_GridPane.setMargin(lBlock, new Insets(0, 0, 0, 5));
    }

    @FXML
    public void endGameOnClick() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit confirmation");
        alert.setContentText("Are you sure you want to quit?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) { // Quit game - end player's game
            timer.cancel();
            enableDisableControlButtons(true);
            runningGameButtonsDisable(false);
            openForWatchAllPlayersBoard();
            endGameMenuItem.setDisable(true);
        } else {// do nothing...
        }
    }

    private void runningGameButtonsDisable(boolean i_Disable) {
        m_IsGameInEndPhase = !i_Disable;
        navigateToTheEndMenuItem.setDisable(i_Disable);
        navigateToTheStartMenuItem.setDisable(i_Disable);
        navigateBackMenuItem.setDisable(i_Disable);
        navigateForwardMenuItem.setDisable(i_Disable);
        NavigatorMenu.setDisable(i_Disable);
        loadGameMenuItem.setDisable(i_Disable);
    }

    @FXML
    public void undoMoveOnClick() {
        m_CurrentPlayer.undo();
        showBoard(m_CurrentPlayer);
        redoUndoMenuItemsAvailabilityModifier();
        makeMoveButton.setDisable(!m_CurrentPlayer.checkIfPlayerHasMovesLeft());
        movesLeftInTurnLabel.setText(((Integer) (2 - m_CurrentPlayer.getNumOfMovesMade())).toString());
        scoreLabel.setText(((Integer) (int) m_CurrentPlayer.getScore()).toString());
    }

    @FXML
    public void redoMoveOnClick() {
        m_CurrentPlayer.redo();
        showBoard(m_CurrentPlayer);
        redoUndoMenuItemsAvailabilityModifier();
        makeMoveButton.setDisable(!m_CurrentPlayer.checkIfPlayerHasMovesLeft());
        movesLeftInTurnLabel.setText(((Integer) (2 - m_CurrentPlayer.getNumOfMovesMade())).toString());
        scoreLabel.setText(((Integer) (int) m_CurrentPlayer.getScore()).toString());
    }

    @FXML
    public void instructionOnClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instructions");
        alert.setHeaderText("Gridler 2.0");
        alert.setContentText("Please Read the ReadMe file :)");
        alert.showAndWait();
    }

    @FXML
    public void aboutOnClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Gridler JavaFX");
        alert.setHeaderText("Gridler 2.0");
        alert.setContentText("Have Fun!");
        alert.showAndWait();
    }

    @FXML
    public void showStatisticsOnClick() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXML/ShowStatistics.fxml"));
            Parent root = fxmlLoader.load();
            ShowStatisticsController controller = (ShowStatisticsController) fxmlLoader.getController();
            controller.getNumberOfMovesPlayedLabel().setText(m_CurrentPlayer.getTotalMovesMadeInGame().toString());
            controller.getNumberOfRedoPlayedLabel().setText(m_CurrentPlayer.getNumOfRedoMade().toString());
            controller.getNumberOfUndoPlayedLabel().setText(m_CurrentPlayer.getNumOfUndoMade().toString());
            setNewWindowModalStage(root, m_CurrentPlayer.getName() + " Statistics");
        } catch (IOException e) {
            showErrorMsg("FXML loading error", "statistics fxml could not be loaded");
        }
    }

    private void showErrorMsg(String i_MsgHeather, String i_ErrorMsg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(i_MsgHeather);
        alert.setHeaderText("ERROR!");
        alert.setContentText(i_ErrorMsg);
        alert.showAndWait();
    }

    private void showInformationMsg(String i_MsgHeather, String i_InfoMsg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Be advised!");
        alert.setHeaderText(i_MsgHeather);
        alert.setContentText(i_InfoMsg);
        alert.showAndWait();
    }

    private void showBoard(GamePlayer i_Player) {
        if (i_Player != m_CurrentPlayer && i_Player.getIsHuman()) {
            showInformationMsg("Board viewing:", "during a game can only view AI players boards.");
            return;
        }

        i_Player.updateBlocks();// use in progress thread if we have time
        for (int i = 0; i < m_LoadedBoard.getBoardHeight(); i++) {
            updateBlocks(m_HorizontalBlocksLabel.get(i), i_Player.getHorizontalSlice(i));
            for (int j = 0; j < m_LoadedBoard.getBoardWidth(); j++) {
                setBoardButtonStyle(m_GameBoardButtons.get(i).get(j), i_Player.getGameBoardSquareSign(i, j));
                m_GameBoardButtons.get(i).get(j).setDisable(!i_Player.getId().equalsIgnoreCase(m_CurrentPlayer.getId()));
            }
        }

        for (int j = 0; j < m_LoadedBoard.getBoardWidth(); j++) {
            updateBlocks(m_VerticalBlocksLabel.get(j), i_Player.getVerticalSlice(j));
        }

        enableDisableControlButtons(!i_Player.getId().equalsIgnoreCase(m_CurrentPlayer.getId()));
    }

    private void enableDisableControlButtons(boolean i_Disable) {// if true the buttons disabled
        makeMoveButton.setDisable(i_Disable);
        endTurnButton.setDisable(i_Disable);
        RedoMenuItem.setDisable(i_Disable);
        UndoMenuItem.setDisable(i_Disable);
    }

    private void clearBoard() {
        for (int i = 0; i < m_LoadedBoard.getBoardHeight(); i++) {
            clearSlice(m_HorizontalBlocksLabel.get(i));
            for (int j = 0; j < m_LoadedBoard.getBoardWidth(); j++) {
                m_GameBoardButtons.get(i).get(j).setId(k_UndefCellStyleId);
            }
        }

        for (int j = 0; j < m_LoadedBoard.getBoardWidth(); j++) {
            clearSlice(m_VerticalBlocksLabel.get(j));
        }
    }

    private void clearSlice(ArrayList<Label> i_Labels) {
        for (Label label : i_Labels) {
            label.setId(k_IncompleteBlockStyleId);
        }
    }

    private void updateBlocks(ArrayList<Label> i_Labels, ArrayList<Block> i_Blocks) {
        int i = 0;

        for (Label label : i_Labels) {
            if (i_Blocks.get(i).isMarked()) {
                label.setId(k_PerfectBlockStyleId);
            } else {
                label.setId(k_IncompleteBlockStyleId);
            }

            i++;
        }
    }

    private void updatePlayerDataLabels() {
        playersNameLabel.setText(m_CurrentPlayer.getName());
        scoreLabel.setText(((Integer) ((int) m_CurrentPlayer.getScore())).toString());
        IDLabel.setText(m_CurrentPlayer.getId());
        turnsLeftInGameLabel.setText(((Integer) (m_CurrentPlayer.getTurnLimit() - m_CurrentPlayer.getTurnNumber())).toString());
        movesLeftInTurnLabel.setText(((Integer) (2 - m_CurrentPlayer.getNumOfMovesMade())).toString());
        if (m_IsGameTypeSinglePlayer) {
            turnsLeftInGameLabel.setText("\u221E");
            movesLeftInTurnLabel.setText("\u221E");
            PlayersBoardsMenu.setDisable(m_IsGameTypeSinglePlayer);
        }
        startTimer();
    }

    private void initPlayerDataLabel() {
        playersNameLabel.setText("");
        scoreLabel.setText("");
        IDLabel.setText("");
        turnsLeftInGameLabel.setText("");
        movesLeftInTurnLabel.setText("");
        timerLabel.setText("");
    }

    private void startTimer() {
        timer = new java.util.Timer();

        timer.schedule(new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        m_CurrentPlayer.incrementTime();
                        timerLabel.setText(((Long) m_CurrentPlayer.getTimer()).toString());
                    }
                });
            }
        }, 1000, 1000);//delay, period
    }
}