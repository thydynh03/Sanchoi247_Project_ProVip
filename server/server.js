// WebSocket server logic
const WebSocket = require('ws');
const mysql = require('mysql');
const jwt = require('jsonwebtoken');

const JWT_SECRET = Buffer.from('sanchoi247SecureKeyForJWTSigningXyZ123456789abcdefghijklmnopqrstuv', 'utf-8');

// Setup MySQL connection
const db = mysql.createConnection({
    host: 'localhost',
    user: 'root',
    password: 'thithithi0305', // Replace with your MySQL password
    database: 'SanChoi247'
});

db.connect((err) => {
    if (err) {
        console.error('Database connection failed: ' + err.stack);
        return;
    }
    console.log('Connected to MySQL database.');
});

// Create WebSocket server
const wss = new WebSocket.Server({ port: 8081, perMessageDeflate: false });

// Store user connections
const clients = new Map();

wss.on('connection', (ws, req) => {
    console.log('A new client connected!');

    const token = req.headers['sec-websocket-protocol'];

    if (!token) {
        ws.send(JSON.stringify({ type: 'error', message: 'No token provided.' }));
        ws.close();
        return;
    }

    let decoded;
    try {
        decoded = jwt.verify(token, JWT_SECRET);
    } catch (error) {
        console.error('Token verification failed:', error);
        ws.send(JSON.stringify({ type: 'error', message: 'Invalid token.' }));
        ws.close();
        return;
    }

    const username = decoded.sub;

    // Fetch user details from the database (get uid, name, avatar)
    const query = 'SELECT uid, name, avatar FROM users WHERE username = ?';
    db.query(query, [username], (err, results) => {
        if (err) {
            console.error('Error fetching user data: ', err);
            ws.send(JSON.stringify({ type: 'error', message: 'Database error occurred' }));
            return;
        }

        if (results.length === 0) {
            ws.send(JSON.stringify({ type: 'error', message: 'User not found.' }));
            ws.close();
            return;
        }

        const user = results[0];
        clients.set(ws, { uid: user.uid, name: user.name, avatar: user.avatar });
        ws.send(JSON.stringify({ type: 'success' }));

        const welcomeMessage = `${user.name} has joined the chat!`;

        // Broadcast welcome message
        wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN && client !== ws) {
                client.send(JSON.stringify({
                    type: 'serverMessage',
                    name: user.name,
                    message: welcomeMessage,
                    avatar: user.avatar // Include avatar in the welcome message
                }));
            }
        });
    });

    ws.on('message', (data) => {
        const message = JSON.parse(data);
    
        if (message.type === 'chat') {
            const senderInfo = clients.get(ws);
            if (senderInfo) {
                const chatMessage = message.text;
    
                // Lưu tin nhắn vào cơ sở dữ liệu
                const messageToSave = {
                    senderUid: senderInfo.uid,
                    receiverUid: null,
                    content: chatMessage,
                    timestamp: new Date().toISOString(),
                    senderName: senderInfo.name,
                    senderAvatar: senderInfo.avatar,
                };
    
                fetch('http://localhost:8080/api/messages', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(messageToSave)
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    console.log('Message saved to database');
                })
                .catch(err => {
                    console.error('Error saving message:', err);
                });
    
                // Gửi thông điệp đến tất cả client
                wss.clients.forEach((client) => {
                    if (client.readyState === WebSocket.OPEN) {
                        client.send(JSON.stringify({
                            type: 'chat',
                            sender: senderInfo.name, // Lấy name từ senderInfo
                            avatar: senderInfo.avatar, // Lấy avatar từ senderInfo
                            message: chatMessage
                        }));
                    }
                });
            }
        }
    });

    ws.on('close', () => {
        const userInfo = clients.get(ws);
        if (userInfo) {
            clients.delete(ws);

            wss.clients.forEach((client) => {
                if (client.readyState === WebSocket.OPEN) {
                    client.send(JSON.stringify({
                        type: 'serverMessage',
                        message: `${userInfo.name} has left the chat.`
                    }));
                }
            });
            console.log(`${userInfo.name} disconnected`);
        }
    });
});

console.log('WebSocket server is running on ws://localhost:8081');
