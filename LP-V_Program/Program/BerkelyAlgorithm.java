
import java.text.*;
import java.util.*;

public class BerkelyAlgorithm {

    public static void main(String[] args) throws ParseException {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of clients in your network: ");
        int clientCount = sc.nextInt();
        sc.nextLine();

        String[] timeString = new String[1 + clientCount];  // 1 server + clientCount clients

        for (int i = 0; i < timeString.length; i++) {
            if (i == 0) {
                System.out.print("Enter time displayed in Server (HH:mm): ");
            } else {
                System.out.print("Enter time displayed in Client " + i + " (HH:mm): ");
            }

            String time = sc.nextLine();
            timeString[i] = appendCurrentDateToTime(time);
        }

        System.out.println("\nBefore Synchronization");
        displayTime(timeString, "");

        berkeleyAlgorithm(timeString);

        System.out.println("\nAfter Synchronization");
        displayTime(timeString, "Synchronized ");

        sc.close();
    }

    public static void berkeleyAlgorithm(String[] timeString) throws ParseException {
        int n = timeString.length;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm | yyyy-MM-dd");

        // Converting time to milliseconds
        long[] timeInMilliseconds = new long[n];
        for (int i = 0; i < n; i++) {
            timeInMilliseconds[i] = simpleDateFormat.parse(timeString[i]).getTime();
        }

        // Calculating time difference w.r.t. server
        long serverTime = timeInMilliseconds[0];
        long[] differenceInTimeWithServer = new long[n];
        for (int i = 0; i < n; i++) {
            differenceInTimeWithServer[i] = timeInMilliseconds[i] - serverTime;
        }

        // Calculating Fault tolerant average
        long avg = 0;
        for (int i = 0; i < n; i++) {
            avg += differenceInTimeWithServer[i];
        }
        avg /= n;
        System.out.println("Fault tolerant average = " + avg / (1000 * 60));    // Displaying fault-tolerant average in minutes

        // Adjusting the time in Server and Clients
        for (int i = 0; i < n; i++) {
            long offset = avg - differenceInTimeWithServer[i];
            timeInMilliseconds[i] += offset;
            if (i == 0) {
                continue;
            }
            System.out.println("Clock " + i + " adjustment = " + offset / (1000 * 60)); // Displaying adjustment value in minutes
        }

        // Converting milliseconds to actual time
        for (int i = 0; i < n; i++) {
            timeString[i] = simpleDateFormat.format(new Date(timeInMilliseconds[i]));
        }
    }

    private static void displayTime(String[] time, String prefix) {
        System.out.println(prefix + "Server Clock:\t" + time[0]);
        for (int i = 1; i < time.length; i++) {
            System.out.println(prefix + "Client " + i + " Clock:\t" + time[i]);
        }
        System.out.println();
    }

    private static String appendCurrentDateToTime(String time) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return time + " | " + year + "-" + month + "-" + day;
    }
}



// The Berkeley algorithm is a decentralized algorithm for synchronizing the clocks of computers in a distributed system. It was developed at the University of California, Berkeley, hence the name. The main goal of the Berkeley algorithm is to adjust the local clocks of all computers within the system so that they are closely aligned with each other, minimizing the time differences between them.

// Here's a detailed explanation of the steps involved in the Berkeley algorithm:

// 1. **Initialization**: Initially, each node in the distributed system maintains its own local clock time. These local clock times are not synchronized and may drift apart due to differences in clock speeds and other factors.

// 2. **Election of a Time Coordinator**: One of the nodes in the system is designated as the time coordinator. This node acts as the reference point for synchronizing the clocks of all other nodes. The selection of the time coordinator can be done using various methods, such as leader election algorithms or manual configuration.

// 3. **Measurement of Clock Skew**: Each node in the system measures the difference between its own local clock time and the time reported by the time coordinator. This difference is known as the clock skew or offset.

// 4. **Calculation of Average Clock Skew**: Once each node has measured its clock skew with respect to the time coordinator, the average clock skew across all nodes is calculated. This average skew represents the amount by which the local clocks of all nodes need to be adjusted to achieve synchronization.

// 5. **Adjustment of Local Clocks**: Based on the average clock skew calculated in the previous step, each node adjusts its local clock time by adding or subtracting the calculated skew value. This adjustment brings the local clocks of all nodes closer to each other and helps in achieving synchronization.

// 6. **Stabilization and Iteration**: After adjusting their local clocks, the nodes continue to operate and communicate within the distributed system. Periodically, the synchronization process is repeated to account for any further drift in clock times. The algorithm may iterate through steps 3 to 5 at regular intervals to maintain synchronization over time.

// 7. **Handling Failures**: The Berkeley algorithm should also include mechanisms to handle failures, such as the failure of the time coordinator or network partitions. In case of failure, a new time coordinator may need to be elected, and the synchronization process restarted.

// Overall, the Berkeley algorithm provides a simple and effective method for synchronizing the clocks of computers in distributed systems, enabling coordinated actions and accurate timestamping of events across the network. However, it's worth noting that the algorithm assumes a stable and reliable network environment and may need to be supplemented with additional techniques to handle more complex scenarios and improve accuracy.