function openPopupAddFriend(){
    let popUp= document.getElementById('popup-new-friend')
    popUp.classList.add('open')
    return
}

function closePopupAddFriend(){
    let popUp= document.getElementById('popup-new-friend')
    let exception = document.getElementById('exception-new-friend')
    exception.innerHTML = ''
    popUp.classList.remove('open')
    return
}

function closePopupNewGroupChat(){
    let popUp= document.getElementById('popup-new-group-chat')
    popUp.classList.remove('open')
    return
}

function closePopupAddUser(){
    let popUp= document.getElementById('popup-add-user')
    let exception = document.getElementById('exception-add-user')
        exception.innerHTML = ''
    popUp.classList.remove('open')
    return
}