import java.util.*;
import java.util.concurrent.*;

public class PacketGenerator {
    public interface PacketListener {
        void onPacket(Packet p);
    }

    private final long cardId;
    private final PacketListener listener;
    private long timeFrame = 0;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public PacketGenerator(long cardId, PacketListener listener) {
        this.cardId = cardId;
        this.listener = listener;
    }

    public void start() {
        executor.scheduleAtFixedRate(() -> {
            double temperature = Math.random() * 40 - 10; // -10 à 30
            double latitude = Math.random() * 180 - 90;
            double longitude = Math.random() * 360 - 180;
            Packet p = new Packet(cardId, ++timeFrame, temperature, latitude, longitude);
            listener.onPacket(p);
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        executor.shutdownNow();
    }
}

