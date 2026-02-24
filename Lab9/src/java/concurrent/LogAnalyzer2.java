import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Semaphore;

public class LogAnalyzer2 {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java LogAnalyzer <arquivos_de_log>");
            System.exit(1);
        }

        Contador c200 = new Contador();
        Contador c500 = new Contador();

        Thread[] threads = new Thread[args.length];

        for (int i = 0; i < args.length; i++) {
            System.out.println("Processando arquivo: " + args[i]);
            threads[i] = new Thread(new FileProcesser(args[i], c200, c500));
            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("===== RESULTADO FINAL =====");
        System.out.println("Total 200: " + c200.get());
        System.out.println("Total 500: " + c500.get());
    }

    // public static void processFile(String fileName) {
	// try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
    //         String line;
    //         while ((line = br.readLine()) != null) {
    //             String[] parts = line.split(" ");
    //             if (parts.length == 3) {
    //                 String code = parts[2];
    //                 if (code.equals("200")) {
    //                     total200++;
    //                 } else if (code.equals("500")) {
    //                     total500++;
    //                 }
    //             }
    //         }
	// } catch (IOException e) {
    //         System.err.println("Erro ao ler arquivo: " + fileName);
    //         e.printStackTrace();
    //     }
    // }
}

class FileProcesser implements Runnable {

    private String fileName;
    Contador c200;
    Contador c500;

    FileProcesser(String fileName, Contador c200, Contador c500) {
        this.fileName = fileName;
        this.c200 = c200;
        this.c500 = c500;
    }


    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String code = parts[2];
                    if (code.equals("200")) {
                        c200.increment();
                    } else if (code.equals("500")) {
                        c500.increment();
                    }
                }
            }
	} catch (IOException e) {
            System.err.println("Erro ao ler arquivo: " + fileName);
            e.printStackTrace();
        }
    }
}

class Contador {
    private int n;
    private Semaphore mutex;

    Contador() {
        n = 0;
        mutex = new Semaphore(1);
    }

    public void increment() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        n++;
        mutex.release();
    }

    public int get() {
        return n;
    }
}