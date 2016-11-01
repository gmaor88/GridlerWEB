/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
var refreshRate = 2000; //miliseconds
var ButtonsSelected = [];
var HorizontalBlocks = [];
var VerticalBlocks =[];

$(function () {
    $.ajaxSetup({cache: false});
    setGameRoomName();
    setInterval(ajaxUpdate, refreshRate);
    ajaxPlayerInfoUpdate();
    getAndShowGameBoard();
});

function ajaxPlayerInfoUpdate() {
    ajaxPlayerData();
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
                }
                else{
                    ButtonsSelected[j][k] = null;
                }
                this.classList.toggle('buttonSelected');
            });
            button.className = 'BoardButton';
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
        for(var j = 0; j < Width; j++){
            innerArray.push(null);
        }

        ButtonsSelected.push(innerArray);
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
            slot = null;
        })
    })
}

function makeMoveButtonClicked() {
    var choice = $('.radioGroup:checked').value;
    var data;
    //data = choice + "|";
    prepDataToSend(data);
    $.ajax({
        url: "MakeMoveServlet",
        data: {'choice':choice, 'data':data},
        type: 'POST',
        success: function Redirect() {

        },
        error: function (xhr, status, error) {
            if (xhr.status === 400) {
                $('#errorMsg').text(xhr.responseText);
            }
        }
    });
}

function prepDataToSend(data) {
    $.each(ButtonsSelected || [],function (index,Buttons) {
        $.each(Buttons || [],function (jndex, Button) {
            if(Button != null) {
                data += index + "," + jndex + ".";
            }
        })
    })
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
    ajaxPlayersList();
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
}

function refreshGameRoomPlayersList(playersList) {
    //clear all current gameRooms
    $("#GamePlayersTableBody").empty();
    var i=1;
    $.each(playersList['players'] || [], function(index, player) {
        var tr = $('<tr>' + '<td>' + i + '<td>' + player['Name'] + '</td>' +
            '<td>'+ player['PlayerType']  +'</td>' +'<td>'+ player['Score'] +'</td>' + '</tr>' );
        if(player['Name'] == playersList['CurrentPlayer']){
            tr.toggleClass('diffColor');
        }

        $("#GamePlayersTableBody").append(tr);
        i++;
    });
}