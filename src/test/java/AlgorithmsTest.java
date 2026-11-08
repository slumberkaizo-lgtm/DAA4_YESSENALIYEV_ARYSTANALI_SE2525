import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class AlgorithmsTest {
    private EditDistance[] distances() {
        return new EditDistance[]{new RecursiveDistance(), new TableDistance(), new TwoRowDistance()};
    }

    private StringSearch[] searches() {
        return new StringSearch[]{new NaiveSearch(), new KmpSearch()};
    }

    @Test
    void referenceDistances() {
        for (EditDistance distance : distances()) {
            assertEquals(3, distance.distance("kitten", "sitting"));
            assertEquals(2, distance.distance("flaw", "lawn"));
            assertEquals(5, distance.distance("intention", "execution"));
            assertEquals(3, distance.distance("", "abc"));
            assertEquals(3, distance.distance("abc", ""));
            assertEquals(0, distance.distance("", ""));
            assertEquals(0, distance.distance("abc", "abc"));
        }
    }

    @Test
    void randomDistancesAgree() {
        InputGenerator generator = new InputGenerator(42);
        Random lengths = new Random(43);
        for (int i = 0; i < 100; i++) {
            String a = generator.randomString(lengths.nextInt(7));
            String b = generator.randomString(lengths.nextInt(7));
            int expected = new TableDistance().distance(a, b);
            for (EditDistance distance : distances()) assertEquals(expected, distance.distance(a, b));
        }
    }

    @Test
    void referenceLps() {
        KmpSearch kmp = new KmpSearch();
        assertArrayEquals(new int[]{0, 1, 2, 0, 1, 2, 3, 3}, kmp.lps("AAACAAAA"));
        assertArrayEquals(new int[]{0, 0, 1, 2, 0, 1, 2, 3, 4}, kmp.lps("ABABCABAB"));
        assertArrayEquals(new int[]{}, kmp.lps(""));
        assertArrayEquals(new int[]{0}, kmp.lps("A"));
    }

    @Test
    void referenceMatches() {
        for (StringSearch search : searches()) {
            assertEquals(List.of(10), search.search("ABABDABACDABABCABAB", "ABABCABAB"));
            assertEquals(List.of(0, 1, 2, 3), search.search("AAAAA", "AA"));
            assertEquals(List.of(0, 2, 4), search.search("ABABABA", "ABA"));
            assertEquals(List.of(), search.search("ABCDE", "ZZ"));
            assertEquals(List.of(0), search.search("ABC", "ABC"));
        }
    }

    @Test
    void randomSearchesAgree() {
        InputGenerator generator = new InputGenerator(44);
        Random lengths = new Random(45);
        for (int i = 0; i < 100; i++) {
            String text = generator.randomString(lengths.nextInt(80));
            String pattern = generator.randomString(1 + lengths.nextInt(10));
            assertEquals(new NaiveSearch().search(text, pattern), new KmpSearch().search(text, pattern));
        }
    }

    @Test
    void searchEdgeCases() {
        for (StringSearch search : searches()) {
            assertThrows(IllegalArgumentException.class, () -> search.search("abc", ""));
            assertThrows(IllegalArgumentException.class, () -> search.search("", ""));
            assertEquals(List.of(), search.search("a", "abc"));
            assertEquals(List.of(), search.search("", "a"));
        }
    }

    @Test
    void countersResetAndIncludeLps() {
        for (EditDistance distance : distances()) {
            distance.distance("ab", "cd");
            long operations = distance.getOperations();
            assertTrue(operations > 0);
            distance.distance("ab", "cd");
            assertEquals(operations, distance.getOperations());
        }
        NaiveSearch naive = new NaiveSearch();
        naive.search("AAAAA", "AB");
        assertEquals(8, naive.getComparisons());
        KmpSearch kmp = new KmpSearch();
        kmp.search("AAAAA", "AB");
        assertEquals(10, kmp.getComparisons());
        kmp.search("AAAAA", "AB");
        assertEquals(10, kmp.getComparisons());
    }
}
