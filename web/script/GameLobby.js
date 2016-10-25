/**
 * Created by Maor Gershkovitch on 10/24/2016.
 */
var chatVersion = 0;
var refreshRate = 2000; //miliseconds

function triggerAjaxGameRoomsContent() {
    setTimeout(ajaxChatContent, refreshRate);
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

function ajaxChatContent() {
    $.ajax({
        url: "chat",
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function(data) {
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxGameRoomsContent();
        },
        error: function(error) {
            triggerAjaxGameRoomsContent();
        }
    });
}

function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");
    $.each(entries || [], appendChatEntry);
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();

    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

