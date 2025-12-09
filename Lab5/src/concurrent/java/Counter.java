

import java.util.concurrent.Semaphore;

public class Counter {
    private int numero;
    private Semaphore mutex;

    public Counter(int numero) {
        this.numero = numero;
        this.mutex = new Semaphore(1);
    }

    public void decrementa() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        numero -= 1;
        mutex.release();
    }

    public int get() {
        return numero;
    }
}
