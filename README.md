**Java Chat Application**

A simple real-time peer-to-peer chat system built with Java Sockets, Threads, and JavaFX.
It supports group messaging, private messaging, user nicknames, and connection logs. Messages are Base64-encoded for basic encryption.

Features

- Multi-client support (Server handles many clients at once).
- Group chat – messages are broadcast to all connected clients.
- Private chat – use /w <nick> <message> to send a private message.
- Nicknames – users pick a nickname when joining.
- Logs – server terminal shows all joins, messages, and disconnects.
- Basic Encryption – messages are Base64 encoded before transmission.
- JavaFX GUI – user-friendly client interface.

Tools & Technologies

- Java (JDK 11+ recommended)
- JavaFX (for client GUI)
- Socket Programming (ServerSocket, Socket)
- Threads (for concurrency)

How to Run
1. Compile: 
   javac Chat_Server.java Chat_Client.java

2. Start Server:
   java Chat_Server (java Chat_Server 9090)

3. Start Client(s)
   java Chat_Client (java Chat_Client)


- Enter server host (localhost if running locally).
- Enter server port (e.g., 9090).
- Choose a nickname.


Usage

- Send a group message: type normally in the input box.
- Send a private message:
 /w <nickname> <message>
- Quit: close the client window or press X.

Security

- Messages are Base64-encoded before sending over the network.
- This is not strong encryption, but it prevents raw-text snooping.
- For real security, integrate AES/RSA encryption.

Next Steps / Improvements

- Add online user list in the client GUI.
- Implement AES encryption for stronger security.
- Store chat logs in a database.
- Add file-sharing support.

Screenshots

<img width="1919" height="1023" alt="Screenshot 2025-09-08 163718" src="https://github.com/user-attachments/assets/53926dc7-5213-4171-9682-de51207d0cd2" />


<img width="1918" height="1015" alt="Screenshot 2025-09-08 164735" src="https://github.com/user-attachments/assets/fbe10db5-a569-4100-9972-8adb3bb08212" />


<img width="1919" height="1018" alt="Screenshot 2025-09-08 164744" src="https://github.com/user-attachments/assets/e7f223d3-d164-4b92-8847-a4cdc15a401f" />
 
Author
- Aashi Vishwakarma
  (Java Developer)
