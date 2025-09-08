import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();

public static void main(String[] args) {
    int port = 9090;
    if (args.length > 0) port = Integer.parseInt(args[0]);

    try (ServerSocket serverSocket = new ServerSocket(port)) {
        System.out.println("Server started on port " + port);

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(new ClientHandler(socket)).start();
        }
    } catch (IOException e) {
        e.printStackTrace();
    }
}

private static class ClientHandler implements Runnable {
    private final Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String nick;

    ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            // First line must be NICK:<nick>
            String first = in.readLine();
            if (first == null || !first.startsWith("NICK:")) {
                socket.close();
                return;
            }

            String requestedNick = first.substring(5).trim();
            if (requestedNick.isEmpty() || clients.containsKey(requestedNick)) {
                sendInfo("Nickname invalid or already in use.");
                socket.close();
                return;
            }

            nick = requestedNick;
            clients.put(nick, this);
            out.println("NICK_OK:" + nick);
            broadcastInfo(nick + " joined the chat.");
            log("[INFO] " + nick + " connected.");

            String line;
            while ((line = in.readLine()) != null) handleCommand(line);

        } catch (IOException e) {
            log("[INFO] Connection lost for " + nick);
        } finally {
            if (nick != null) {
                clients.remove(nick);
                broadcastInfo(nick + " left the chat.");
                log("[INFO] " + nick + " disconnected.");
            }
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void handleCommand(String line) {
        if (line == null || line.trim().isEmpty()) return;

        if (line.startsWith("MSG:")) {
            String b64 = line.substring(4);
            String text = decodeSafe(b64);
            log(nick + ": " + text);
            broadcast("MSG:" + nick + ":" + b64);
        } else if (line.startsWith("PVT:")) {
            String rest = line.substring(4);
            int idx = rest.indexOf(':');
            if (idx > 0) {
                String target = rest.substring(0, idx);
                String b64 = rest.substring(idx + 1);
                String text = decodeSafe(b64);
                log("[Private] " + nick + " -> " + target + ": " + text);
                sendPrivate(target, b64);
            }
        } else if (line.equals("QUIT")) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void send(String msg) {
        out.println(msg);
    }

    private void broadcast(String msg) {
        for (ClientHandler c : clients.values()) if (c != this) c.send(msg);
    }

    private void sendPrivate(String target, String b64) {
        ClientHandler dest = clients.get(target);
        if (dest != null) {
            dest.send("PVT:" + nick + ":" + b64);
            send("PVT_SENT:" + target + ":" + b64);
        } else sendInfo("User " + target + " not found.");
    }

    private void broadcastInfo(String text) {
        log("[INFO] " + text);
        String b64 = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        for (ClientHandler c : clients.values()) c.send("INFO:" + b64);
    }

    private void sendInfo(String text) {
        log("[INFO to " + nick + "] " + text);
        String b64 = Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
        send("INFO:" + b64);
    }

    private void log(String msg) {
        System.out.println(msg);
    }

    private String decodeSafe(String b64) {
        try {
            return new String(Base64.getDecoder().decode(b64), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return "<invalid base64>";
        }
    }
}
