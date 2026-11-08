import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Experiment {
    private static volatile int result;

    public static void main(String[] args) throws IOException {
        Files.createDirectories(Path.of("results"));
        try (PrintWriter csv = new PrintWriter(Files.newBufferedWriter(Path.of("results/results.csv")))) {
            csv.println("algorithm,input,n,m,run,time_ms,operations,comparisons,result");
            EditDistance[] distances = {new RecursiveDistance(), new TableDistance(), new TwoRowDistance()};
            for (EditDistance distance : distances) {
                InputGenerator generator = new InputGenerator(2525);
                int[] sizes = distance instanceof RecursiveDistance
                        ? new int[]{3, 6, 9, 12} : new int[]{128, 256, 512, 1024};
                for (int n : sizes) {
                    String a = generator.randomString(n);
                    measureDistance(csv, distance, "random", a, generator.randomString(n));
                    measureDistance(csv, distance, "different", "A".repeat(n), "B".repeat(n));
                }
            }
            InputGenerator generator = new InputGenerator(2525);
            for (int n : new int[]{2000, 4000, 8000, 16000}) {
                int m = n / 8;
                String text = generator.randomString(n);
                String pattern = generator.randomString(m);
                for (StringSearch search : new StringSearch[]{new NaiveSearch(), new KmpSearch()}) {
                    measureSearch(csv, search, "random", text, pattern);
                    measureSearch(csv, search, "repeated", "A".repeat(n), "A".repeat(m - 1) + "B");
                }
            }
        }
        System.out.println("Saved results/results.csv");
    }

    private static void measureDistance(PrintWriter csv, EditDistance algorithm, String input,
                                        String a, String b) {
        result = algorithm.distance(a, b);
        for (int run = 1; run <= 5; run++) {
            long start = System.nanoTime();
            result = algorithm.distance(a, b);
            double time = (System.nanoTime() - start) / 1_000_000.0;
            csv.printf(Locale.US, "%s,%s,%d,%d,%d,%.6f,%d,0,%d%n",
                    algorithm.getClass().getSimpleName(), input, a.length(), b.length(),
                    run, time, algorithm.getOperations(), result);
        }
    }

    private static void measureSearch(PrintWriter csv, StringSearch algorithm, String input,
                                      String text, String pattern) {
        result = algorithm.search(text, pattern).size();
        for (int run = 1; run <= 5; run++) {
            long start = System.nanoTime();
            List<Integer> matches = algorithm.search(text, pattern);
            double time = (System.nanoTime() - start) / 1_000_000.0;
            result = matches.size();
            csv.printf(Locale.US, "%s,%s,%d,%d,%d,%.6f,0,%d,%d%n",
                    algorithm.getClass().getSimpleName(), input, text.length(), pattern.length(),
                    run, time, algorithm.getComparisons(), result);
        }
    }
}
