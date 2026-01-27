import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// Classe que mantem as estatisticas
class WebStatsAtomic {
    private AtomicLong totalAccess = new AtomicLong();
    private AtomicInteger totalPurchases = new AtomicInteger();
    private AtomicInteger totalFailures = new AtomicInteger();
    private AtomicInteger totalNothing = new AtomicInteger();
    private AtomicInteger onlineUsers = new AtomicInteger();

    // Usuario acessa o sistema
    public void access() {
        totalAccess.getAndIncrement();
        onlineUsers.getAndIncrement();
    }

    // Usuario realiza uma compra
    public void purchase() {
        totalPurchases.getAndIncrement();
    }

    // Ocorreu uma falha
    public void failure() {
        totalFailures.getAndIncrement();
    }

    // Usuario nem compra nem falha
    public void nothing() {
        totalNothing.getAndIncrement();
    }

    // Usuario faz logout
    public void logout() {
        onlineUsers.getAndDecrement();
    }

    // Impressao das estatisticas atuais
    public void printStats() {
        System.out.println("========= Estatisticas do Sistema =========");
        System.out.println("Total de Acessos: " + totalAccess);
        System.out.println("Total de Compras: " + totalPurchases);
        System.out.println("Total de Falhas: " + totalFailures);
        System.out.println("Total de acessos sem compras ou falhas: " + totalNothing);
        System.out.println("Usuarios Online: " + onlineUsers);
        System.out.println("=======================================================");
    }
}

// Classe que simula acoes de um usuario no sistema
class UserSimulationAtomic implements Runnable {
    private WebStatsAtomic stats;
    private Random random;

    public UserSimulationAtomic(WebStatsAtomic stats) {
        this.stats = stats;
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            // Usuario acessa o sistema
            stats.access();

            // Simula tempo navegando
            Thread.sleep(random.nextInt(300));

            // Decide se faz compra, falha ou apenas navega
            int action = random.nextInt(3); // 0 = compra, 1 = falha, 2 = nada
            if (action == 0) {
                stats.purchase();
            } else if (action == 1) {
                stats.failure();
            } else {
                stats.nothing();
            }

            // Simula tempo antes de logout
            Thread.sleep(random.nextInt(200));

            // Usuario sai do sistema
            stats.logout();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Classe principal que executa a simulacao concorrente
public class WebStatsAtmVarMain {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: java WebStatsMain number_users");
            System.exit(1);
        }

        int numUsers = Integer.valueOf(args[0]); // quantidade de threads (usuarios simultaneos)

        WebStatsAtomic stats = new WebStatsAtomic();
        Thread[] users = new Thread[numUsers];

        // Criacao e inicializacao das threads
        for (int i = 0; i < numUsers; i++) {
            users[i] = new Thread(new UserSimulationAtomic(stats));
            users[i].start();
        }

        // Aguarda todas as threads terminarem
        for (int i = 0; i < numUsers; i++) {
            try {
                users[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Imprime estatisticas finais (possivelmente incorretas)
        stats.printStats();
    }
}