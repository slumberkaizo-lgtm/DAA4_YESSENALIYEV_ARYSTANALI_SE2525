import java.util.Random;

public class InputGenerator {
    private final Random random;

    public InputGenerator(long seed) {
        random = new Random(seed);
    }

    public String randomString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append((char) ('A' + random.nextInt(4)));
        }
        return result.toString();
    }
}
