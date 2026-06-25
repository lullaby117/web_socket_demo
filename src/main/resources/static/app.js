// 全局变量
let stompClient = null;
let username = null;

/**
 * 连接WebSocket并加入聊天室
 */
function connect() {
    username = document.getElementById('username').value.trim();
    if (!username) {
        alert('请输入昵称！');
        return;
    }

    // 1. 创建SockJS连接（兼容WebSocket不可用的情况）
    const socket = new SockJS('http://localhost:8080/chat');

    // 2. 创建STOMP客户端
    stompClient = Stomp.over(socket);

    // 3. 连接并订阅消息
    stompClient.connect({},
        // 连接成功回调
        function() {
            console.log('WebSocket连接成功！');

            // 订阅公共频道（群聊）
            stompClient.subscribe('/topic/public', function(message) {
                const chatMessage = JSON.parse(message.body);
                displayMessage(chatMessage);
            });

            // 订阅私聊队列（/user/用户名/queue/private）
            stompClient.subscribe('/user/queue/private', function(message) {
                const chatMessage = JSON.parse(message.body);
                chatMessage.private = true;
                displayMessage(chatMessage);
            });

            // 通知服务器有新用户加入
            const joinMessage = {
                sender: username,
                type: 'JOIN',
                content: username + ' 加入了聊天室'
            };
            stompClient.send('/app/chat.addUser', {}, JSON.stringify(joinMessage));

            // 切换UI
            document.getElementById('loginForm').style.display = 'none';
            document.getElementById('chatArea').style.display = 'block';
            document.getElementById('userStatus').textContent = '✅ 当前用户：' + username;
            document.getElementById('messageInput').focus();
        },
        // 连接失败回调
        function(error) {
            console.error('连接失败：', error);
            alert('连接失败，请检查服务器是否启动！');
        }
    );
}

/**
 * 发送群聊消息
 */
function sendMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();

    if (!content) {
        alert('请输入消息内容！');
        return;
    }

    const chatMessage = {
        sender: username,
        content: content,
        type: 'CHAT'
    };

    // 发送到服务器（地址：/app/chat.sendMessage）
    stompClient.send('/app/chat.sendMessage', {}, JSON.stringify(chatMessage));
    input.value = '';
}

/**
 * 发送私聊消息
 */
function sendPrivateMessage() {
    const input = document.getElementById('messageInput');
    const content = input.value.trim();
    const targetUser = document.getElementById('targetUser').value.trim();

    if (!content) {
        alert('请输入消息内容！');
        return;
    }
    if (!targetUser) {
        alert('请输入私聊目标用户！');
        return;
    }
    if (targetUser === username) {
        alert('不能给自己发私聊！');
        return;
    }

    const chatMessage = {
        sender: username,
        content: '💌 ' + content, // 私聊标识
        type: 'PRIVATE',
        targetUser: targetUser
    };

    // 发送私聊消息（地址：/app/chat.privateMessage）
    stompClient.send('/app/chat.privateMessage', {}, JSON.stringify(chatMessage));
    input.value = '';

    // 在本地显示已发送的私聊消息
    chatMessage.private = true;
    chatMessage.content = '【私聊 → ' + targetUser + '】' + chatMessage.content;
    displayMessage(chatMessage);
}

/**
 * 断开连接
 */
function disconnect() {
    if (stompClient) {
        // 通知服务器用户离开
        const leaveMessage = {
            sender: username,
            type: 'LEAVE',
            content: username + ' 离开了聊天室'
        };
        stompClient.send('/app/chat.addUser', {}, JSON.stringify(leaveMessage));

        // 断开连接
        stompClient.disconnect();
        stompClient = null;
    }

    // 刷新页面回到登录状态
    location.reload();
}

/**
 * 显示消息到界面
 */
function displayMessage(message) {
    const area = document.getElementById('messageArea');
    const msgDiv = document.createElement('div');
    msgDiv.className = 'message';

    // 系统通知（加入/离开）
    if (message.type === 'JOIN' || message.type === 'LEAVE') {
        msgDiv.className = 'message system';
        msgDiv.textContent = message.content;
        msgDiv.style.fontStyle = 'italic';
    } else {
        // 普通消息
        const senderSpan = document.createElement('span');
        senderSpan.className = 'sender';
        senderSpan.textContent = message.sender + '：';
        msgDiv.appendChild(senderSpan);

        const contentSpan = document.createElement('span');
        contentSpan.textContent = message.content;
        msgDiv.appendChild(contentSpan);

        // 私聊标记
        if (message.private) {
            msgDiv.style.borderLeft = '3px solid #48bb78';
            msgDiv.style.backgroundColor = '#f0fff4';
        }

        // 时间戳
        const timeSpan = document.createElement('span');
        timeSpan.className = 'time';
        timeSpan.textContent = message.timestamp || '';
        msgDiv.appendChild(timeSpan);
    }

    area.appendChild(msgDiv);
    // 自动滚动到底部
    area.scrollTop = area.scrollHeight;
}

/**
 * 支持回车键发送消息
 */
document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('messageInput').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    document.getElementById('username').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            connect();
        }
    });
});