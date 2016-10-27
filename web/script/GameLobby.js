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

function validateFileFormat(file,event) {
    var ext = file.split(".");
    ext = ext[ext.length-1].toLowerCase();
    var extensionsAllowed = "xml";

    if (extensionsAllowed != ext) {
        $('#errorMsg').text("Wrong extension type! It has to be xml.");
        $('#fileRequested').val("");
    }
    else{
        $('#errorMsg').text("");
        var input = $(event.target);
        var formData = new FormData();
        var file = event.target.files[0];
        formData.append(file.name, file);
        if (file != null) {
            $.ajax({
                url: "GameUploadServlet",
                contentType: false,
                processData: false,
                data: formData,
                type: 'POST',
                success: function() {
                    $("#errorMsg").empty();
                    refreshGameRoomsList();
                },
                error: function (xhr, status, error) {
                    if (xhr.status === 400) {
                        $('#errorMsg').text(xhr.getResponseHeader("errorText"));
                    }
                }
            });
            input.replaceWith(input.val(""));
        }
    }
}

