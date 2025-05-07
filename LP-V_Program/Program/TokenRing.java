import java.util.Scanner;

class Process extends Thread {
    private int id;
    private boolean hasToken;
    private boolean wantsToEnterCS;
    private Process next;

    // Static counter to stop the ring after a few rounds (for simulation)
    private static int maxRounds = 10;
    private static int currentRound = 0;

    public Process(int id) {
        this.id = id;
        this.hasToken = false;
        this.wantsToEnterCS = false;
    }

    public void setNext(Process next) {
        this.next = next;
    }

    public void requestCS() {
        wantsToEnterCS = true;
    }

    public void receiveToken() {
        hasToken = true;
        new Thread(this).start(); // ✅ Start a new thread
    }

    @Override
    public void run() {
        if (hasToken) {
            System.out.println("Process " + id + " has the token.");

            if (wantsToEnterCS) {
                enterCriticalSection();
                wantsToEnterCS = false;
            } else {
                System.out.println("Process " + id + " does not want to enter CS.");
            }

            // Limit the number of token passes
            synchronized (Process.class) {
                if (currentRound >= maxRounds) {
                    System.out.println("Maximum token rounds reached. Ending simulation.");
                    return;
                }
                currentRound++;
            }

            passToken();
        }
    }

    private void enterCriticalSection() {
        System.out.println("Process " + id + " is entering the Critical Section.");
        try {
            Thread.sleep(1000); // Simulate some work in CS
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Process " + id + " is leaving the Critical Section.");
    }

    private void passToken() {
        hasToken = false;
        System.out.println("Process " + id + " passes the token to Process " + next.id + ".\n");
        next.receiveToken();
    }
}

public class TokenRing {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();

        Process[] processes = new Process[n];

        // Initialize processes
        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i);
        }

        // Set up the ring
        for (int i = 0; i < n; i++) {
            processes[i].setNext(processes[(i + 1) % n]);
        }

        // Ask which processes want to enter CS
        for (int i = 0; i < n; i++) {
            System.out.print("Does process " + i + " want to enter the critical section? (yes/no): ");
            String response = sc.next().toLowerCase();
            if (response.equals("yes")) {
                processes[i].requestCS();
            }
        }

        System.out.println("\nStarting Token Ring Simulation...\n");

        // Initially, process 0 has the token
        processes[0].receiveToken();

        sc.close();
    }
}
