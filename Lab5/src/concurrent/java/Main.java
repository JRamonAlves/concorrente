import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Main {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Use: java Main <num_producers> <max_items_per_producer> <producing_time> <num_consumers> <consuming_time>");
            return;
        }
        
        int numProducers = Integer.parseInt(args[0]);
        int maxItemsPerProducer = Integer.parseInt(args[1]);
        int producingTime = Integer.parseInt(args[2]);
        int numConsumers = Integer.parseInt(args[3]);
        int consumingTime = Integer.parseInt(args[4]);

        int totalProducts = numConsumers * maxItemsPerProducer;
        
        Counter counter = new Counter(totalProducts);
        Buffer buffer = new Buffer();
        
        Semaphore bufferCheio = new Semaphore(50);
        Semaphore bufferVazio = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);

        List<Thread> threadList = new ArrayList<>();
        
        for (int i = 1; i <= numProducers; i++) {
            Thread producer = new Thread(new Producer(i, buffer, maxItemsPerProducer, producingTime, bufferCheio, bufferVazio, mutex, counter));
            threadList.add(producer);
            producer.start();
        }
        
        for (int i = 1; i <= numConsumers; i++) {
            Thread consumer = new Thread( new Consumer(i, buffer, consumingTime, bufferCheio, bufferVazio, mutex, counter));
            threadList.add(consumer);
            consumer.start();
        }

        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}

