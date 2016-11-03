/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
var refreshRate = 2000; //miliseconds
var ButtonsSelected = [];
var BoardButtons = [];
var HorizontalBlocks = [];
var VerticalBlocks =[];
var IsMyTurn = false;
var IsGameRunning = false;

$(function () {
    $.ajaxSetup({cache: false});
    setGameRoomName();
    $('#BlackRadioButtonOption').prop("checked", true);
    setInterval(ajaxUpdate, refreshRate);
    ajaxPlayerInfoUpdate();
    getAndShowGameBoard();
});

function ajaxPlayerInfoUpdate() {
    ajaxPlayerData();
}

function updateGame() {
    $.ajax({
        url: "GetBoardServlet",
        type: "POST",
        dataType: 'json',
        success: function (gameBoardData) {
            updateBoard(gameBoardData);
        },
        complete: function () {
            ajaxPlayerData();
            initButtonSelectedArray();
        }
    });
    //ajaxPlayerData();
    //initButtonSelectedArray();
}

function updateBoard(gameBoardData) {
   $.each(gameBoardData['Board'] || [],function (index,row) {
        $.each(row || [],function (jndex, square) {
            BoardButtons[index][jndex].className = "BoardButton";
            if(square['m_CurrentSquareSign'] == "BLACKED"){
                BoardButtons[index][jndex].classList.add("BLACKED");
                //BoardButtons[index][jndex].className += "BLACKED";
            }
            else if(square['m_CurrentSquareSign'] == "CLEARED"){
                BoardButtons[index][jndex].classList.add("CLEARED");
                //BoardButtons[index][jndex].className += "CLEARED";
            }
            else{
                BoardButtons[index][jndex].classList.add("UNDEFINED");
                //BoardButtons[index][jndex].className += "UNDEFINED";
            }
        })
    })
    updateCompletedBlockLabels(gameBoardData);
}

function updateCompletedBlockLabels(gameBoardData) {
    updateCompletedBlocksInSlices(gameBoardData['VerticalSlices'], VerticalBlocks);
    updateCompletedBlocksInSlices(gameBoardData['HorizontalSlices'], HorizontalBlocks);
}

function updateCompletedBlocksInSlices (i_SlicesData, i_SliceLabels) {
    $.each(i_SlicesData || [], function (index, block) {
        $.each(i_SlicesData[index] || [], function (jndex, Block) {
            i_SliceLabels[index][jndex].className = "incompleteBlock";
            if(Block['m_Marked']==true){
                i_SliceLabels[index][jndex].className = "completeBlock";
            }
        });
    });
}

function getAndShowGameBoard() {
    $.ajax({
        url: "GetBoardServlet",
        dataType: 'json',
        success: function(gameBoardData) {
            buildBoard(gameBoardData['Height'], gameBoardData['Width'],gameBoardData['HorizontalSlices'],gameBoardData['VerticalSlices']);
        }
    });
}

function buildBoard(height,width,horizontalSlices,verticalSlices){
    createAndNullButtonsSelectedArray(height, width);
    createAndNullLabelSlicesArrays(height,horizontalSlices, width,verticalSlices);
    var board = $('#GameBoardArea');
    for(var i = 0; i < height; i++){
        var row = document.createElement("div");
        row.className = "BoardRow";
        for(var x = 0; x < width; x++){
            var cell = document.createElement("div");
            cell.className = "gridSquare";
            var button = document.createElement("button");
            const j = i; //for the click
            const k = x; //for the click
            button.addEventListener("click",function () {
                event.preventDefault();
                if(ButtonsSelected[j][k] == null) {
                    ButtonsSelected[j][k] = this;
                    this.classList.add("buttonSelected");
                }
                else{
                    ButtonsSelected[j][k] = null;
                    this.classList.remove("buttonSelected");
                }
                //this.classList.toggle('buttonSelected');
            });
            //button.className = "BoardButton";
            //button.className += "UNDEFINED";
            button.classList.add("BoardButton","UNDEFINED");
            BoardButtons[i][x] = button;
            cell.appendChild(button);
            row.append(cell);
        }
        $.each(horizontalSlices[i] || [], function(index, block) {
            cell = document.createElement("div");
            cell.className = "gridSquare";
            cell.appendChild(HorizontalBlocks[i][index]);
            row.appendChild(cell);
        });
        board.append(row);
    }
    row = document.createElement("div");
    row.className = "BoardCol";
    $.each(verticalSlices || [], function (index, block) {
        var col = document.createElement("div");
        col.className = "BoardCol";
        $.each(verticalSlices[index] || [], function (jndex, Block) {
            cell = document.createElement("div");
            cell.className = "gridSquare2";
            cell.appendChild(VerticalBlocks[index][jndex]);
            col.append(cell);
        });
        row.append(col);
        //board.append(col);
    });
    board.append(row);
    //document.getElementById("code").innerText = board.innerHTML;
}

function createAndNullButtonsSelectedArray(Height, Width) {
    for(var i = 0; i < Height; i++) {
        var innerArray = [];
        var BoardButtonsInnerArray = [];
        for(var j = 0; j < Width; j++){
            innerArray.push(null);
            BoardButtonsInnerArray.push(null);
        }

        ButtonsSelected.push(innerArray);
        BoardButtons.push(BoardButtonsInnerArray);
    }
}

function createAndNullLabelSlicesArrays(height,horizontalSlices, width,verticalSlices) {
    createAndNullLabelSlices(height, horizontalSlices, HorizontalBlocks);
    createAndNullLabelSlices(width, verticalSlices, VerticalBlocks);
}

function createAndNullLabelSlices(length,Slices, ArrayToInsert){
    for(var i = 0; i < length; i++) {
        var innerArray = [];
        $.each(Slices[i] || [],function (index,block) {
            var Label = document.createElement("label");
            Label.innerText = block['f_Size'];
            Label.className = "incompleteBlock";
            innerArray.push(Label);
        });

        ArrayToInsert.push(innerArray);
    }
}

function initButtonSelectedArray() {
    $.each(ButtonsSelected || [],function (index, buttons) {
        $.each(buttons || [],function (jndex, slot) {
            if(slot != null){
                //slot.classList.toggle('buttonSelected');
                slot.classList.remove("buttonSelected");
            }

            //slot = null;
            ButtonsSelected[index][jndex] = null;
        })
    })
}

function makeMoveButtonClicked() {
    var choice = $("input[name='radioGroup']:checked").val();
    var data;
    data = prepDataToSend();
    $.ajax({
        url: "MakeMoveServlet",
        data: {'choice':choice, 'data':data},
        type: 'POST',
        success: function (){},
        complete: function () {
            updateGame();
            checkIfWon();
        },
        error: function (xhr, status, error) {
            if (xhr.status === 400) {
                $('#errorMsg').text(xhr.responseText);
            }
        }
    });
}

function QuitButtonClicked() {
    endTurnButtonClicked();
    $.ajax({
        url: "QuitGameServlet",
        type: "POST",
        dataType: 'json',
        success: function (){},
        complete: function () {
            window.location = "GameLobby.html";
        }
        });
}

function checkIfWon() {
    if(IsGameRunning && IsMyTurn){
        $.ajax({
            url: "VictoryServlet",
            dataType: 'json',
            success: function (answer) {
                if(answer == true) {
                    alert("You WIN!!!");
                    IsGameRunning = false;
                    disableGameButtons();
                    $('#QuitButton').prop("disabled", false);
                }
            }});
    }
}

function endTurnButtonClicked() {
    $.ajax({
        url: "EndTurnServlet",
        type: 'POST',
        success: function(){
            disableGameButtons();
            IsMyTurn = false;
        }
    });
}

function UndoButtonClicked() {
    $.ajax({
        url: "UndoServlet",
        type: 'POST',
        dataType: 'json',
        success: function (){},
        complete: function () {
            updateGame();
        }});
}

function RedoButtonClicked() {
    $.ajax({
        url: "RedoServlet",
        type: 'POST',
        dataType: 'json',
        success: function (){},
        complete: function () {
            updateGame();
        }});
}

function prepDataToSend() {
    var data = "";
    $.each(ButtonsSelected || [],function (index,Buttons) {
        $.each(Buttons || [],function (jndex, Button) {
            if(Button != null) {
                data += index + "," + jndex + "-";
            }
        })
    });
    data = data.substring(0, data.length - 1);
    return data;
}

function ajaxPlayerData() {
    $.ajax({
        url: "PlayerDataServlet",
        dataType: 'json',
        success: function(playerData) {
            refreshPlayerData(playerData);
        }
    });
}

function ajaxUpdate(){
    if(IsGameRunning){
        loseDrawCheckAjax();
        //checkIfWon();
    }

    checkIfWon();
    getIfGameRunningAndIfMyTurn();
    ajaxPlayersList();
}

function loseDrawCheckAjax() {
    $.ajax({
        url: "LoseDrawServlet",
        dataType: 'json',
        success: function (answer) {
            if(answer.value == true) {
                alert("You Lose!!!");
                IsGameRunning = false;
            }
            else if(answer.key == true){
                alert("The Game Ended In A Draw!!!");
                IsGameRunning = false;
            }
        }});
}

function getIfGameRunningAndIfMyTurn() {
    $.ajax({
        url: "GameRunningAndPlayerTurnServlet",
        dataType: 'json',
        success: function (answer) {
            if(answer.value == true){
                if(IsGameRunning == false){
                    alert ("Game Started!!!");
                    IsGameRunning = true;
                }

                if(answer.key == true && IsMyTurn == false){
                    alert ("Your Turn");
                    IsMyTurn = true;
                    $('#EndTurnButton').prop("disabled", false);
                    $('#MakeMoveButton').prop("disabled", false);
                    updateGame();
                }
            }
            else{
                IsGameRunning = false;
                disableGameButtons();
                $('#QuitButton').prop("disabled", false);
            }
    }});
}

function setGameRoomName() {
    $.ajax({
        url: "GetGameNameServlet",
        dataType: 'json',
        success: function (gameRoomName) {
            var name = gameRoomName['name'];
            $('#GameRoomName').text(name);
        }
    })
}

function ajaxPlayersList() {
    $.ajax({
        url: "GameRoomPlayersListServlet",
        dataType: 'json',
        success: function(playersList) {
            refreshGameRoomPlayersList(playersList);
        }
    });
}

function refreshPlayerData(playerData) {
    $('#PlayersNameLabel').text(playerData['PlayerName']);
    $('#ScoreLabel').text(playerData['Score']);
    $('#MovesLeftInTurnLabel').text(playerData['MovesLeftInTurn']);
    $('#TurnsLeftInGameLabel').text(playerData['TurnLeftInGame']);
    if(playerData['IsHumanPlayer']) {
        $('#MakeMoveButton').prop("disabled", playerData['MovesLeftInTurn'] <= 0 || !IsMyTurn || !IsGameRunning);
        $('#UndoMoveButton').prop("disabled", !playerData['IsUndoAvailable'] || !IsMyTurn || !IsGameRunning);
        $('#RedoMoveButton').prop("disabled", !playerData['IsRedoAvailable'] || !IsMyTurn || !IsGameRunning);
        $('#QuitButton').prop("disabled", !IsMyTurn);
    }
    else{
        disableGameButtons();
        if(IsMyTurn && IsGameRunning){
            AiPlay();
            endTurnButtonClicked();
        }
    }
}

function AiPlay() {
    $.ajax({
        url: "AIPlayServlet",
        type: 'POST',
        dataType: 'json',
        success: function (){},
        complete: function () {
            updateGame();
        }});
}

function disableGameButtons() {
    $('#MakeMoveButton').prop("disabled", true);
    $('#UndoMoveButton').prop("disabled", true);
    $('#RedoMoveButton').prop("disabled", true);
    $('#EndTurnButton').prop("disabled", true);
    $('#QuitButton').prop("disabled", true);
}

function refreshGameRoomPlayersList(playersList) {
    //clear all current gameRooms
    $("#GamePlayersTableBody").empty();
    var i=1;
    $.each(playersList['players'] || [], function(index, player) {
        var tr = $('<tr>' + '<td>' + i + '<td>' + player['Name'] + '</td>' +
            '<td>'+ player['PlayerType']  +'</td>' +'<td>'+ player['Score'] +'</td>' + '</tr>' );
        if(player['Name'] == playersList['CurrentPlayer']){
            tr.className = "diffColor";
        }

        $("#GamePlayersTableBody").append(tr);
        i++;
    });
}