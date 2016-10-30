/**
 * Created by Maor Gershkovitch on 10/30/2016.
 */
var refreshRate = 2000; //miliseconds

$(function () {
    $.ajaxSetup({cache: false});
    setGameRoomName();
    setInterval(ajaxUpdate, refreshRate);
})

function ajaxUpdate(){
    ajaxPlayerData();
    ajaxPlayersList();
    ajaxGameBoard();
}

function setGameRoomName() {
    $.ajax({
        url: "GetGameNameServlet",
        dataType: 'json',
        success: function(gameRoomName) {
            var name = gameRoomName['name'];
            $('#GameRoomName').text(name);
        }
}