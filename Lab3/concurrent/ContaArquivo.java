import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ContaArquivo implements Runnable {

    private File file;
    private String pattern;

    ContaArquivo(File file, String pattern) {
        this.file = file;
        this.pattern = pattern;
    }

    public long countInFile() throws IOException {
        long totalLocal = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    totalLocal += countInSequence(line, pattern);
                }
            }
        }
        return totalLocal;
    }

    @Override
    public void run() {

    }

}

class ContaLinha implements Runnable {

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
