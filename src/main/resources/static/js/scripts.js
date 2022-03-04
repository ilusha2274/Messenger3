let stompClient = null;
let arr = null;
let messageId = null;
let scrollEvent = true;
let files = [];
const preview = document.createElement('div');

preview.classList.add('preview');

$(document).ready(function() {
    let chatID = document.querySelector('#chatID');
    if (chatID != null){
        console.log("Index page is ready");
        connect();
    }
    $("#send").click(function() {
        sendMessage();
    });
    $("#file").on('change', changeHandler);
    $("#open").on('click', function() {
        $("#file").click();
    });
    preview.addEventListener('click',removeHandler);
});

const removeHandler = event =>{
    if (!event.target.dataset.name) {
          return
    }

    const {name} = event.target.dataset
    files = files.filter(file => file.name !== name)

    const block = preview.querySelector(`[data-name="${name}"]`).closest('.preview-image')
    block.remove()
}

const changeHandler = event => {
    if(!event.target.files.length){
        return;
    }

    preview.innerHTML = '';

    const message = document.getElementById("message");
    message.insertAdjacentElement('afterend', preview);

    files = Array.from(event.target.files);
    const reader = new FileReader();

    reader.onload = ev => {
        const src = ev.target.result;
        preview.insertAdjacentHTML('afterbegin', `
            <div class="preview-image">
            <div class="preview-remove"  data-name="${files[0].name}">&times;</div>
                <img style="width: auto; height: 70px" src="${src}"/>
                <div class="preview-info" style="height: 25px; position: absolute;bottom: 0;font-size: .8rem;background: rgba(255, 255, 255, .5);display: flex;align-items: center;justify-content: space-between;padding: 0 5px;">
                    ${bytesToSize(files[0].size)}
                </div>
            </div>
        `)
    }

    reader.readAsDataURL(files[0]);
}

function bytesToSize(bytes) {
  const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB']
  if (!bytes) {
    return '0 Byte'
  }
  const i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)))
  return Math.round(bytes / Math.pow(1024, i)) + ' ' + sizes[i]
}

$(document).keydown(function(e){
    $('#message').keydown(function(e){
        let messageContent = document.querySelector('#message');
        if(e.which == 13 && messageContent.value != ''){
           sendMessage();
        }
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

    $('#messages').scrollTop($('#messages')[0].scrollHeight);
    $('#messages').on('scroll', function() {
        if($('#messages').scrollTop() <=250 && scrollEvent){
            scrollEvent = false;
            load();
        }
    });
}

function showMessage(message) {
    console.log('Got message: ' + message.content);
    let userId = document.querySelector('#userId');
    if (userId.value == message.userId){
    $("#date").html(message.time);
    }else{
    $("#messages").prepend(" <div class=\"block-message\"> " +
         " <div class=\"block-message-sender\"> " +
         " <p class=\"sender-message\"> " + message.content + " </p> " +
         " <p class=\"sender-message-author\"> " + message.nameAuthor + " </p> " +
         " <p id=\"date\" class=\"sender-message-date\"> " + message.time + " </p> " +
         " </div> " +
         " </div> ");
    }
}

function fileUpload(file) {
    let chatMessage;
    const reader = new FileReader();
        reader.onload = ev => {
            const src = ev.target.result;
            let chatID = document.querySelector('#chatID');
            let nameAuthor = document.querySelector('#name');
            let userId = document.querySelector('#userId');
            chatMessage = {
                nameAuthor: nameAuthor.value,
                userId: userId.value,
                content: src,
                idChat: chatID.value,
                haveFile: true
            };
        }
        reader.readAsDataURL(file);
        var formData = new FormData();
        formData.append("file", files[0]);
        formData.append('ad', new Blob(JSON.stringify(chatMessage), {
                        type: "application/json"
                    }));

//            showMessageAuthor(chatMessage);
//            let res = JSON.stringify(chatMessage);
        stompClient.send("/ws/chat2/" + chatID.value, {}, JSON.stringify(formData));
//        stompClient.send("/ws/chat2/" + chatID.value, {}, JSON.stringify(chatMessage));
}

function sendMessage() {
    if (files[0] != null){
        fileUpload(files[0]);
    }else{
        let chatID = document.querySelector('#chatID');
        let messageContent = document.querySelector('#message');
        let nameAuthor = document.querySelector('#name');
        let userId = document.querySelector('#userId');
        let chatMessage = {
            nameAuthor: nameAuthor.value,
            userId: userId.value,
            content: messageContent.value,
            idChat: chatID.value,
            haveFile: false
        };
        showMessageAuthor(chatMessage);
        stompClient.send("/ws/chat/" + chatID.value, {}, JSON.stringify(chatMessage));
        console.log('Got post message: ' + messageContent.value );
        messageContent.value = '';
    }
}

function showMessageAuthor(message) {
    console.log('Got message: ' + message.content);
    $("#messages").prepend(" <div align=\"right\" class=\"block-message\"> " +
       " <div class=\"block-message-receiver\"> " +
       " <p class=\"receiver-message\"> " + message.content + " </p> " +
       " <p id=\"date\" class=\"receiver-message-date\"> " + message.time + " </p> " +
       " </div> " +
       " </div> ");

    $('#messages').scrollTop($('#messages')[0].scrollHeight);
}

function printNext20Message(message) {

    if (message.author){
    $("#messages").append(" <div align=\"right\" class=\"block-message\"> " +
         " <div class=\"block-message-receiver\"> " +
         " <input class = \"messageId\" type=\"hidden\" value=\"" + message.messageId + "\"> " +
         " <p class=\"receiver-message\"> " + message.message + " </p> " +
         " <p id=\"date\" class=\"receiver-message-date\"> " + message.date + " </p> " +
         " </div> " +
         " </div> ");
    }else{
    $("#messages").append(" <div class=\"block-message\"> " +
          " <div class=\"block-message-sender\"> " +
          " <input class = \"messageId\" type=\"hidden\" value=\"" + message.messageId + "\"> " +
          " <p class=\"sender-message\"> " + message.message + " </p> " +
          " <p class=\"sender-message-author\"> " + message.nameAuthor + " </p> " +
          " <p id=\"date\" class=\"sender-message-date\"> " + message.date + " </p> " +
          " </div> " +
          " </div> ");
    }
}

function load(){
    messageId = $('.messageId').last()[0].value;
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
        }else{
            $('#messages').off('scroll');
        }
    };
    xhr.send();

};