/**
 * Created by Maor Gershkovitch on 10/24/2016.
 */
var chatVersion = 0;
var refreshRate = 2000; //miliseconds

function triggerAjaxGameRoomsContent() {
    setTimeout(ajaxGameRoomsContent, refreshRate);
}

//activate the timer calls after the page is loaded
$((function(){

    //prevent IE from caching ajax calls
    $.ajaxSetup({cache: false});

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxGameRoomsContent();
}));

function ajaxUsersList() {
    $.ajax({
        url: "OnlineUsersListServlet",
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

function refreshUsersList(users) {
    //clear all current users
    $("#OnlinePlayersTableBody").empty();
    var i=1;

    $.each(users || [], function(index, user) {
        console.log("Adding user #" + i + ": " + user.key + " " + user.value);
        $('<tr>' + '<td>' + i + '<td>' + user.key + '</td>' + '<td>'+ user.value +'</td>' + '</tr>' ).appendTo($("#OnlinePlayersTableBody"));
        i++;
    });
}

function ajaxGameRoomsContent() {
    $.ajax({
        url: "GameRoomListServlet",
        dataType: 'json',
        success: function(gameRooms) {
            refreshGameRoomsList(gameRooms);
        }
    });
}

function refreshGameRoomsList(gameRooms) {
    //clear all current users
    $("#GameRoomsTableBody").empty();
    var i=1;

    $.each(gameRooms || [], function(index, gameRoom) {
        console.log("Adding GameRoom #" + i + ": " + gameRoom.key + " " + gameRoom.value);
        $('<tr>' + '<td>' + i + '<td>' + gameRoom.key + '</td>' + '<td>'+ gameRoom.value +'</td>' + '</tr>' ).appendTo($("#GameRoomsTableBody"));
        i++;
    });
}

/*$('#LogoutButton').click(function () {
    $.ajax({
        url: "LogoutServlet",
        success: function () {}
    })
})*/


