// small helper function for selecting element by id
let id = id => document.getElementById(id);

const urlParams = new URLSearchParams(window.location.search);
const chatId = urlParams.get("chat_id")
const fromUser = urlParams.get("from_user")
const toUser = urlParams.get("to_user")

//Establish the WebSocket connection and set up event handlers
let ws = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/" + chatId + "+" + fromUser + "+" + toUser + "?user_id=" + fromUser);
ws.onmessage = msg => updateChat(msg);
ws.onclose = () => alert("WebSocket connection closed");

// Add event listeners to button and input field
id("send").addEventListener("click", () => sendAndClear(id("message").value));
id("message").addEventListener("keypress", function (e) {
    if (e.keyCode === 13) { // Send message if enter is pressed in input field
        sendAndClear(e.target.value);
    }
});

function sendAndClear(message) {
    if (message !== "") {
        ws.send(message);
        id("message").value = "";
    }
}

function updateChat(msg) { // Update chat-panel and list of connected users
    let data = JSON.parse(msg.data)

    let fromUserText
    let fromClass
    if ( !data.from || data.from == -1 ) {
        fromUserText = "Server"
        fromClass = "server-message"
    } else if ( data.from == fromUser ) {
        fromUserText = "Yo"
        fromClass = "my-message"
    } else {
        fromUserText = "Otr@"
        fromClass = "them-message"
    }

    id("chat").insertAdjacentHTML("afterbegin", `
    <article class="${fromClass}">
        <b>${fromUserText}</b>
        <span class="timestamp">${data.timestamp}</span>
        <p>${JSON.stringify(data.message)}</p>
    </article>
    `);
    // id("userlist").innerHTML = data.userlist.map(user => "<li>" + user + "</li>").join("");
    return false
}
