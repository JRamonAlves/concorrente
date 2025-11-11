import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;
import java.io.FileReader;

public class DnaConcurrentMain {

    static Contador total = new Contador();

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
            threads[i] = new Thread(new ContaArquivo(files[i], pattern, total), "Arquivo " + i);
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

        System.out.println("Sequência " + pattern + " foi encontrada " + total.conta + " vezes.");

    }
}

class ContaArquivo implements Runnable {

    private File file;
    private String pattern;
    protected Contador total;
    protected Contador totalArquivo = new Contador();
    protected List<Thread> threadsLinhas = new ArrayList<>();

    ContaArquivo(File file, String pattern, Contador total) {
        this.file = file;
        this.pattern = pattern;
        this.total = total;
    }

    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int iLinha = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    Thread thr = new Thread(new ContaLinha(line, pattern, totalArquivo),
                            "Arquivo " + file.getName() + " Conta Linha " + iLinha);
                    threadsLinhas.add(thr);
                    thr.start();
                }
                iLinha++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivos: " + e.getMessage());
            System.exit(4);
        }

        for (Thread t : threadsLinhas) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("catchou na Linha");
            }
        }

        total.soma(totalArquivo.conta);

    }

}

class ContaLinha implements Runnable {

    private String sequence;
    private String pattern;
    protected Contador totalArquivo;

    ContaLinha(String sequence, String pattern, Contador totalArquivo) {
        this.sequence = sequence;
        this.pattern = pattern;
        this.totalArquivo = totalArquivo;
    }

    @Override
    public void run() {
        if (sequence == null || pattern == null)
            return;

        int n = sequence.length();
        int m = pattern.length();
        if (m == 0 || n < m)
            return;

        for (int i = 0; i <= n - m; i++) {
            if (sequence.regionMatches(false, i, pattern, 0, m))
                totalArquivo.incrementa();
        }

    }

}

class Contador {
    int conta = 0;
    private Semaphore mutex = new Semaphore(1);

    public void incrementa() {
        try {
            mutex.acquire();
            conta++;
            mutex.release();
        } catch (InterruptedException e) {
        }
    }

    public void soma(int receba) {
        try {
            mutex.acquire();
            conta += receba;
            mutex.release();
        } catch (InterruptedException e) {
        }
    }
}