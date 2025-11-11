import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class DnaConcurrentMain {

    static long total = 0;

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Uso: java DnaSerialMain DIRETORIO_ARQUIVOS PADRAO");
            System.err.println("Exemplo: java DnaSerialMain dna_inputs CGTAA");
            System.exit(1);
        }

        String dirName = args[0];
        String pattern = args[1];

        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            System.err.println("Caminho não é um diretório: " + dirName);
            System.exit(2);
        }

        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files == null || files.length == 0) {
            System.err.println("Nenhum arquivo .txt encontrado em: " + dirName);
            System.exit(3);
        }

        Thread[] threads = new Thread[files.length];
        for (int i = 0; i < files.length; i++) {
            threads[i] = new Thread(new ContaArquivo(files[i], pattern), "Arquivo " + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("catchou");
            }
        }

        System.out.println("Sequência " + pattern + " foi encontrada " + total + " vezes.");

    }
}

class ContaArquivo implements Runnable {

    private File file;
    private String pattern;
    protected long totalArquivo = 0;
    protected List<Thread> threadsLinhas = new ArrayList<>();

    ContaArquivo(File file, String pattern) {
        this.file = file;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    threadsLinhas.add(new Thread(new ContaLinha(line, pattern)));
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivos: " + e.getMessage());
            System.exit(4);
        }
    }

}

class ContaLinha implements Runnable {

    private String sequence;
    private String pattern;

    ContaLinha(String sequence, String pattern) {
        this.sequence = sequence;
        this.pattern = pattern;
    }

    @Override
    public void run() {
        if (sequence == null || pattern == null)
            return;

        int n = sequence.length();
        int m = pattern.length();
        if (m == 0 || n < m)
            return;

        long count = 0;
        for (int i = 0; i <= n - m; i++) {
            if (sequence.regionMatches(false, i, pattern, 0, m))
                count++;
        }

    }

}
