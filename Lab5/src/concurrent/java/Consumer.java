import java.util.concurrent.Semaphore;

class Consumer implements Runnable {
    private final Buffer buffer;
    private final int sleepTime;
    private final int id;
    private final Semaphore bufferCheio;
    private final Semaphore bufferVazio;    
    private final Semaphore mutex;
    private final Counter counter;
    
    public Consumer(int id, Buffer buffer, int sleepTime, Semaphore bufferCheio, Semaphore bufferVazio, Semaphore mutex, Counter counter) {
        this.id = id;
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.bufferCheio = bufferCheio;
        this.mutex = mutex;
        this.bufferVazio = bufferVazio;
        this.counter = counter;
    }
    
    public void process() {
        while (true) {
            
            try { bufferVazio.acquire(); } catch (InterruptedException e) {e.printStackTrace();}
        
            if (counter.get() == 0) {
                bufferVazio.release();
            }

            try { mutex.acquire(); } catch (InterruptedException e) {e.printStackTrace();}

            int item = buffer.remove();

            bufferCheio.release();
            mutex.release();
            
            if (item == -1) {
                break;
            }
            System.out.println(" -- Consumer " + id + " consumed item " + item);
            
            try {Thread.sleep(sleepTime);} catch (InterruptedException e) {Thread.currentThread().interrupt();}
            
        }
    }

    @Override
    public void run() {
        process();
    }
}