import java.util.concurrent.Semaphore;

class Producer implements Runnable {
    private final Buffer buffer;
    private final int maxItems;
    private final int sleepTime;
    private final int id;
    private final Semaphore bufferCheio;
    private final Semaphore mutex;
    private final Semaphore bufferVazio;
    private final Counter counter;

    public Producer(int id, Buffer buffer, int maxItems, int sleepTime, Semaphore bufferCheio, Semaphore bufferVazio, Semaphore mutex, Counter counter) {
    this.id = id;
        this.buffer = buffer;
        this.maxItems = maxItems;
        this.sleepTime = sleepTime;
        this.bufferCheio = bufferCheio;
        this.mutex = mutex;
        this.bufferVazio = bufferVazio;
        this.counter = counter;
    }
    
    public void produce() {
        for (int i = 0; i < maxItems; i++) {

            try { bufferCheio.acquire(); } catch (InterruptedException e) { e.printStackTrace(); }

            try { Thread.sleep(sleepTime); } catch (InterruptedException e) {Thread.currentThread().interrupt(); }

            try { mutex.acquire(); } catch (InterruptedException e) {e.printStackTrace();}

                int item = (int) (Math.random() * 100);
                System.out.println("++ Producer " + id + " produced item " + item);
                buffer.put(item);
                counter.decrementa();
            
            mutex.release();
            bufferVazio.release();
        }
    }

    @Override
    public void run() {

        produce();

    }
}
