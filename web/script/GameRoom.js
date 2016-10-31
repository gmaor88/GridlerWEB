/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
var refreshRate = 2000; //miliseconds
var ButtonsSelected = [];

$(function () {
    $.ajaxSetup({cache: false});
    setGameRoomName();
    setInterval(ajaxUpdate, refreshRate);
    ajaxPlayerInfoUpdate();
    getAndShowGameBoard();
})

function ajaxPlayerInfoUpdate() {
    ajaxPlayerData();
}

function getAndShowGameBoard() {
    $.ajax({
        url: "GetBoardServlet",
        dataType: 'json',
        success: function(gameBoardData) {
            buildBoard(gameBoardData['Height'], gameBoardData['Width']);
        }
    });
}

function buildBoard(height,width){
    createAndNullButtonsSelectedArray(height, width);
    var board = $('#GameBoardArea');
    for(var i = 0; i < height; i++){
        var row = document.createElement("div");
        //row.className = "row";
        row.className = "BoardRow";
        for(var x = 0; x < width; x++){
            var cell = document.createElement("div");
            cell.className = "gridSquare";
            var button = document.createElement("button");
            const j = i;
            const k = x;
            button.addEventListener("click",function () {
                event.preventDefault();
                if(ButtonsSelected[j][k] == null) {
                    ButtonsSelected[j][k] = this;
                    //this.toggleClass('buttonSelected');
                }
                else{
                    ButtonsSelected[j][k] = null;
                    //this.removeClass('buttonSelected');
                }
                this.classList.toggle('buttonSelected');
            });
            button.className = 'BoardButton';
            cell.appendChild(button);
            row.appendChild(cell);
        }
        board.append(row);
    }
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