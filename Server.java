import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private final int port;
    private final List<Long> cardIds;
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    public Server(int port, List<Long> cardIds) {
        this.port = port;
        this.cardIds = cardIds;
    }

    public void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        System.out.println("Listening for client connections...");

        for (long id : cardIds) {
            PacketGenerator gen = new PacketGenerator(id, p -> broadcast(p.toString()));
            gen.start();
            System.out.println("Started generator for card " + id);
        }

        new Thread(() -> {
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(clientSocket);
                    clients.add(handler);
                    handler.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void broadcast(String line) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                c.send(line);
            }
        }
    }

    class ClientHandler extends Thread {
        private final Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void send(String line) {
            if (out != null) {
                out.println(line);
                out.flush();
            }
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("quit")) break;
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java Server <port> <cardId1> [cardId2 ...]");
            return;
        }

        int port = Integer.parseInt(args[0]);
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            ids.add(Long.parseLong(args[i]));
        }

        Server s = new Server(port, ids);
        s.startServer();
    }
}



