import java.util.Random;

// This class generates a random ID
//It uses a string of characters and a length to generate the desired ID
public class RandomIDGenerator {
    private static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static int ID_LENGTH = 8;
    private static final Random random = new Random();

    // This method generates a random ID based on the default values
    public static String generateRandomId() {
        StringBuilder sb = new StringBuilder(ID_LENGTH);
        for (int i = 0; i < ID_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    // This method generates a random ID with the given length and characters
    public static String generateRandomId(String characters, int idLength) {
        StringBuilder sb = new StringBuilder(idLength);
        for (int i = 0; i < idLength; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }

}
