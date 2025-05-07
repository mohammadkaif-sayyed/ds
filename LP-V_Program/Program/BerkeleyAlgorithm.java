import java.util.*;

public class BerkeleyAlgorithm {

    static class Node {
        String name;
        double clockTime;  // Simulated clock time

        Node(String name, double clockTime) {
            this.name = name;
            this.clockTime = clockTime;
        }
    }

    // Simulate Cristian's Algorithm: add half of the RTT
    static double getAdjustedTime(double time) {
        double rtt = 0.1 + new Random().nextDouble() * 0.2;  // RTT between 0.1s - 0.3s
        return time + rtt / 2;
    }

    public static void main(String[] args) {
        Random rand = new Random();

        // Master Node
        double masterTime = System.currentTimeMillis() / 1000.0; // seconds
        Node master = new Node("Master", masterTime);

        // Create slave nodes with ±5s time drift
        List<Node> slaves = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            double drift = rand.nextInt(11) - 5; // -5 to +5
            slaves.add(new Node("Slave" + i, masterTime + drift));
        }

        // Print initial clocks
        System.out.println("Initial Clocks:");
        System.out.printf("%s: %.3f\n", master.name, master.clockTime);
        for (Node s : slaves) {
            System.out.printf("%s: %.3f\n", s.name, s.clockTime);
        }

        // Step 1: Master polls slaves and gets adjusted time
        List<Double> adjustedTimes = new ArrayList<>();
        Map<String, Double> originalSlaveClocks = new HashMap<>();

        for (Node slave : slaves) {
            double adjusted = getAdjustedTime(slave.clockTime);
            adjustedTimes.add(adjusted);
            originalSlaveClocks.put(slave.name, slave.clockTime);
        }

        adjustedTimes.add(master.clockTime); // Include master

        // Step 2: Filter outliers (difference > 3s)
        double avg = adjustedTimes.stream().mapToDouble(d -> d).average().orElse(0.0);
        List<Double> filtered = new ArrayList<>();
        for (double t : adjustedTimes) {
            if (Math.abs(t - avg) <= 3) {
                filtered.add(t);
            }
        }

        double finalAverage = filtered.stream().mapToDouble(d -> d).average().orElse(0.0);
        System.out.printf("\nCalculated Average Time (after filtering outliers): %.3f\n\n", finalAverage);

        // Step 3: Send offsets
        System.out.println("Offsets to apply:");
        for (Node slave : slaves) {
            double offset = finalAverage - originalSlaveClocks.get(slave.name);
            System.out.printf("%s: %.3f seconds\n", slave.name, offset);
        }

        double masterOffset = finalAverage - master.clockTime;
        System.out.printf("Master: %.3f seconds (can apply or just broadcast)\n", masterOffset);
    }
}
