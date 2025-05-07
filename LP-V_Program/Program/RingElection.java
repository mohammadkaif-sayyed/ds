import java.util.*;

class Process {
    int id;
    int index;
    boolean isActive;

    public Process(int id, int index) {
        this.id = id;
        this.index = index;
        this.isActive = true; // Initially, all are active
    }
}

public class RingElection {
    static Scanner sc = new Scanner(System.in);
    static Process[] processes;

    // Start election from the given index
    public static void startElection(int initiatorIndex) {
        int n = processes.length;
        int current = initiatorIndex;
        ArrayList<Integer> electionIds = new ArrayList<>();

        System.out.println("\nElection initiated by Process with ID: " + processes[current].id);

        // Ring traversal
        do {
            if (processes[current].isActive) {
                electionIds.add(processes[current].id);
                System.out.println("Process " + processes[current].id + " passes the message.");
            }
            current = (current + 1) % n;
        } while (current != initiatorIndex);

        // Determine new coordinator (highest ID)
        int newCoordinatorId = Collections.max(electionIds);

        // Announce new coordinator
        System.out.println("\nProcess with ID " + newCoordinatorId + " is selected as the new coordinator.");
    }

    public static void main(String[] args) {
        System.out.print("Enter number of processes: ");
        int num = sc.nextInt();
        processes = new Process[num];

        // 3. Getting input
        for (int i = 0; i < num; i++) {
            System.out.print("Enter ID for Process " + (i + 1) + ": ");
            int pid = sc.nextInt();
            processes[i] = new Process(pid, i);
        }

        // 4. Sort processes based on ID
        Arrays.sort(processes, Comparator.comparingInt(p -> p.id));

        // 5. Make the last process inactive
        processes[num - 1].isActive = false;
        System.out.println("Process with ID " + processes[num - 1].id + " is made inactive (simulated crash).");

        // Menu
        while (true) {
            System.out.println("\nMenu:\n1. Start Election\n2. Exit");
            System.out.print("Choose an option: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter initiator process ID: ");
                    int initiatorId = sc.nextInt();
                    boolean found = false;
                    for (int i = 0; i < num; i++) {
                        if (processes[i].id == initiatorId) {
                            if (!processes[i].isActive) {
                                System.out.println("This process is inactive and cannot initiate the election.");
                            } else {
                                startElection(i);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("Invalid process ID.");
                    }
                    break;

                case 2:
                    System.out.println("Exiting.");
                    System.exit(0);

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
