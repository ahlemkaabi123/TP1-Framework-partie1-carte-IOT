import java.io.Serializable;

public class Packet implements Serializable {
    public long cardId;
    public long timeFrame;
    public double temperature;
    public double latitude;
    public double longitude;
    public long checksum;

    public Packet(long cardId, long timeFrame, double temperature, double latitude, double longitude) {
        this.cardId = cardId;
        this.timeFrame = timeFrame;
        this.temperature = temperature;
        this.latitude = latitude;
        this.longitude = longitude;
        this.checksum = computeChecksum();
    }

    public long computeChecksum() {
        return (long) (cardId ^ timeFrame ^ (int) temperature ^ (int) latitude ^ (int) longitude);
    }

    public boolean isValid() {
        return computeChecksum() == checksum;
    }

    @Override
    public String toString() {
        return cardId + "|" + timeFrame + "|" + temperature + "|" + latitude + "|" + longitude + "|" + checksum;
    }

    public static Packet fromString(String s) {
        try {
            String[] parts = s.split("\\|");
            if (parts.length != 6) return null;

            long cardId = Long.parseLong(parts[0]);
            long timeFrame = Long.parseLong(parts[1]);

            // Corrige les virgules venant du serveur
            double temperature = Double.parseDouble(parts[2].replace(',', '.'));
            double latitude = Double.parseDouble(parts[3].replace(',', '.'));
            double longitude = Double.parseDouble(parts[4].replace(',', '.'));
            long checksum = Long.parseLong(parts[5]);

            Packet p = new Packet(cardId, timeFrame, temperature, latitude, longitude);
            p.checksum = checksum;

            if (p.isValid()) return p;
            else return null;

        } catch (Exception e) {
            System.out.println("Invalid packet or checksum failed: " + e.getMessage());
            return null;
        }
    }
}

