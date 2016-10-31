/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
var refreshRate = 2000; //miliseconds
var ButtonsSelected = [];

$(function () {
    $.ajaxSetup({cache: false});
    setGameRoomName();
    setInterval(ajaxUpdate, refreshRate);
    ajaxPlayerInfoUpdate()
})

function ajaxPlayerInfoUpdate() {
    ajaxPlayerData();
    //ajaxGameBoard();
}

function buildBoard(height,width){
    createAndNullButtonsSelectedArray(height, width);
    var board = $('#GameBoardArea');
    for(var i = 0; i < height; i++){
        var row = document.createElement("div");
        row.className = "row";
        for(var x = 0; x < width; x++){
            var cell = document.createElement("div");
            cell.className = "gridsquare";
            var button = $('<button>').click(function () {
                if(ButtonsSelected[i][x] == null) {
                    ButtonsSelected[i][x] = this;
                    //this.toggleClass('buttonSelected');
                }
                else{
                    ButtonsSelected[i][x] = null;
                    //this.removeClass('buttonSelected');
                }
                this.toggleClass('buttonSelected');
            })
            button.addClass('Undefined');
            cell.appendChild(button);
            row.appendChild(cell);
        }
        board.appendChild(row);
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

alert(outterArray);

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