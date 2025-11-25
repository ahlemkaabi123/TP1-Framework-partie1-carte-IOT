import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java Client <server> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Socket socket = new Socket(host, port);

        System.out.println("Connecté au serveur " + host + ":" + port);
        System.out.println("Tape 'filter <cardId>' pour filtrer, 'nofilter' pour tout voir, 'quit' pour quitter.");

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

        final Long[] filter = {null};

        Thread reader = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    Packet p = Packet.fromString(line);
                    if (p != null) {
                        if (filter[0] == null || p.cardId == filter[0]) {
                            System.out.printf("[Card %d] Temp=%.2f°C GPS=(%.6f, %.6f)%n",
                                    p.cardId, p.temperature, p.latitude, p.longitude);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        reader.start();

        String cmd;
        while ((cmd = console.readLine()) != null) {
            if (cmd.startsWith("filter ")) {
                filter[0] = Long.parseLong(cmd.split(" ")[1]);
                System.out.println("Filtre activé pour la carte " + filter[0]);
            } else if (cmd.equals("nofilter")) {
                filter[0] = null;
                System.out.println("Aucun filtre.");
            } else if (cmd.equals("quit")) {
                socket.close();
                break;
            }
        }
    }
}





