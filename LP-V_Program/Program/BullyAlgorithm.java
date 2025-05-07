import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Process extends Thread {
    int processId;
    boolean isCoordinator = false;
    List<Process> processes = new ArrayList<>();
    
    // Constructor
    public Process(int processId) {
        this.processId = processId;
    }
    
    // Set the processes list (peers)
    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }
    
    // Election logic for the Bully Algorithm
    public void run() {
        try {
            while (true) {
                // If this process detects the coordinator has failed
                if (!isCoordinator) {
                    initiateElection();
                }
                
                // Simulate waiting (for simplicity)
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    // Process failure detection and election initiation
    private void initiateElection() {
        System.out.println("Process " + processId + " detects coordinator failure and initiates election...");
        
        // Send election messages to processes with higher IDs
        for (Process process : processes) {
            if (process.processId > this.processId) {
                process.receiveElectionMessage(this.processId);
            }
        }
    }
    
    // Process receives an election message
    public void receiveElectionMessage(int senderId) {
        System.out.println("Process " + processId + " received election message from Process " + senderId);
        
        // If this process has a higher ID, it responds
        if (this.processId > senderId) {
            System.out.println("Process " + processId + " sends OK to Process " + senderId);
            // Send back OK message (simulate responding)
        }
    }
    
    // Declaring a process as coordinator
    public void declareCoordinator() {
        this.isCoordinator = true;
        System.out.println("Process " + processId + " is now the coordinator.");
    }
}

public class BullyAlgorithm {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of processes: ");
        int numProcesses = scanner.nextInt();
        
        List<Process> processes = new ArrayList<>();
        
        // Create processes
        for (int i = 1; i <= numProcesses; i++) {
            processes.add(new Process(i));
        }
        
        // Set the list of processes for each process
        for (Process process : processes) {
            process.setProcesses(processes);
        }
        
        // Start all processes
        for (Process process : processes) {
            process.start();
        }
        
        // Simulate a failure of the coordinator (e.g., after 5 seconds)
        try {
            Thread.sleep(5000);
            System.out.println("Simulating failure of coordinator (Process 5)...");
            processes.get(4).isCoordinator = false;
            processes.get(4).interrupt(); // Interrupt to simulate failure
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        scanner.close();
    }
}
