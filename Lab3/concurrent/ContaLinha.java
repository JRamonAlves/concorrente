import java.io.File;

public class ContaLinha implements Runnable {

    private String sequence;
    private String pattern;

    ContaLinha(String sequence, String pattern) {
        this.sequence = sequence;
        this.pattern = pattern;
    }

    public long countInSequence() {
        if (sequence == null || pattern == null) {
            return 0;
        }
        int n = sequence.length();
        int m = pattern.length();
        if (m == 0 || n < m)
            return 0;

        long count = 0;
        for (int i = 0; i <= n - m; i++) {
            if (sequence.regionMatches(false, i, pattern, 0, m))
                count++;
        }

        return count;
    }

    @Override
    public void run() {
    }

    

}
