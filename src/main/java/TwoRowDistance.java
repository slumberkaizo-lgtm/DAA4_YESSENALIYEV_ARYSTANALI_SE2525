public class TwoRowDistance implements EditDistance {
    private long operations;

    public int distance(String a, String b) {
        operations = 0;
        int m = b.length();
        int[] previous = new int[m + 1];
        int[] current = new int[m + 1];
        for (int j = 0; j <= m; j++) previous[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            current[0] = i;
            for (int j = 1; j <= m; j++) {
                operations++;
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                current[j] = Math.min(previous[j - 1] + cost,
                        1 + Math.min(previous[j], current[j - 1]));
            }
            int[] temp = previous;
            previous = current;
            current = temp;
        }
        return previous[m];
    }

    public long getOperations() {
        return operations;
    }
}
