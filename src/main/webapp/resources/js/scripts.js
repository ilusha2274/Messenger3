let stompClient = null;
let arr = null;
let messageId = null;
let scrollEvent = true;

$(document).ready(function() {
    let chatID = document.querySelector('#chatID');
    if (chatID != null){
        console.log("Index page is ready");
        connect();
    }
    $("#send").click(function() {
        sendMessage();
    });

});

function connect() {
    let socket = new SockJS('/our-websocket');
    stompClient = Stomp.over(socket);
    let chatID = document.querySelector('#chatID').value;
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/messages/chat/' + chatID, function (message) {
            showMessage(JSON.parse(message.body));
        });

    });
//    arr = document.querySelectorAll('#messageId');
//    if (arr.length > 0){
//        messageId = arr[arr.length - 1].value;
//        addListenerOnLastElement(arr);
//    }
    $('#messages2').scrollTop(921);
    let messages2 = document.getElementById('messages2');
    messages2.addEventListener('scroll', function() {
                      let messages2 = document.getElementById('messages2');
                      if(messages2.scrollTop <=250 && scrollEvent){
                        scrollEvent = false;
                        load();
                      }
                    });
}

function showMessage(message) {
    console.log('Got message: ' + message.content);
    let userId = document.querySelector('#userId');
    if (userId.value == message.userId){
    let date = document.getElementById('date');
    date.innerHTML = message.time;
    }else{
    $("#messages").append(" <div class= \"media w-50 mb-3\"> " +
        " <div class= \"media-body ml-3\"> " +
        " <h6> " + message.nameAuthor + " </h6> "  +
        " <div class= \"bg-light rounded py-2 px-3 mb-2\"> " +
        " <p class= \"text-small mb-0 text-muted\"> " + message.content + " </p> " +
        " </div> " +
        " <p class=\"small text-muted\"> " + message.time + " </p> " +
        " </div> " +
        " </div> ");
    }
}

function sendMessage() {
    let chatID = document.querySelector('#chatID');
    let messageContent = document.querySelector('#message');
    let nameAuthor = document.querySelector('#name');
    let userId = document.querySelector('#userId');
    let chatMessage = {
    nameAuthor: nameAuthor.value,
    userId: userId.value,
    content: messageContent.value,
    idChat: chatID.value
    };
    showMessageAuthor(chatMessage);
    stompClient.send("/ws/chat/" + chatID.value, {}, JSON.stringify(chatMessage));
    console.log('Got post message: ' + messageContent.value );
    messageContent.value = '';
}

function showMessageAuthor(message) {
    console.log('Got message: ' + message.content);
    $("#messages").append(" <div class= \"media w-50 ml-auto col-md-3 offset-md-6\"> " +
    " <div class= \"media-body\"> " +
    " <div class= \"bg-primary rounded py-2 px-3 mb-2\"> " +
    " <p class= \"text-small mb-0 text-white text-right\"> " + message.content + " </p> " +
    " </div> " +
    "<p id=\"date\" class=\"small text-muted\">" + "---" + "</p>" +
    " </div> " +
    " </div> ");
}

//function addListenerOnLastElement(array){
//    array[array.length -1].parentNode.addEventListener("mouseover", load);
//}

function printNext20Message(message) {

    if (message.author){
    $("#messages").append(" <div class= \"media w-50 ml-auto col-md-3 offset-md-6\"> " +
        " <div class= \"media-body\"> " +
        " <input class = \"messageId\" type=\"hidden\" value=\"" + message.messageId + "\"> " +
        " <div class= \"bg-primary rounded py-2 px-3 mb-2\"> " +
        " <p class= \"text-small mb-0 text-white text-right\"> " + message.message + " </p> " +
        " </div> " +
        "<p id=\"date\" class=\"small text-muted\">" + message.date + "</p>" +
        " </div> " +
        " </div> ");
    }else{
    $("#messages").append(" <div class= \"media w-50 mb-3\"> " +
        " <div class= \"media-body ml-3\"> " +
        " <input class = \"messageId\" type=\"hidden\" value=\"" + message.messageId + "\"> " +
        " <h6> " + message.nameAuthor + " </h6> "  +
        " <div class= \"bg-light rounded py-2 px-3 mb-2\"> " +
        " <p class= \"text-small mb-0 text-muted\"> " + message.message + " </p> " +
        " </div> " +
        " <p class=\"small text-muted\"> " + message.date + " </p> " +
        " </div> " +
        " </div> ");
    }
}

function load(){
//    arr[arr.length -1].parentNode.removeEventListener("mouseover", load);
//    arr = document.querySelectorAll('#messageId');
    messageId = $('.messageId').last()[0].value;
//    messageId = arr[arr.length - 1].value;
    let xhr = new XMLHttpRequest();
    let chatID = document.querySelector('#chatID').value;
    let url = "http://localhost:8080/chat/" + chatID + "/" + messageId;
    xhr.open("GET", url);
    xhr.setRequestHeader("Context-type", "application/json");

    xhr.onload = function (ev){
        let jsonResponse = JSON.parse(xhr.responseText);
        if(jsonResponse != null && jsonResponse.length > 0){
            for (let i =0;i < jsonResponse.length;i++){
                printNext20Message(jsonResponse[i])
            }
            scrollEvent = true;
        }
    };
    xhr.send();

};