public class CardReader {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java CardReader <cardId>");
            return;
        }

        long cardId = Long.parseLong(args[0]);
        PacketGenerator.PacketListener listener = p -> System.out.println(p.toString());
        PacketGenerator generator = new PacketGenerator(cardId, listener);
        generator.start();

        System.out.println("CardReader started for card " + cardId + ". Press Ctrl+C to stop.");
    }
}

