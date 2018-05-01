package csc445.groupc.distauction;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Multicast Simulator is a class that is used to simulate the multicast communication that Paxos will be using for
 * sending messages between nodes. This simulator will allow all of the Paxos logic to be tested without worrying about
 * packet drops or odd behaviour with multicast.
 */
public class MulticastSimulator {
    private final List<LinkedBlockingQueue<Integer>> sendingQueues;
    private final List<LinkedBlockingQueue<Integer>> receivingQueues;

    private boolean running;

    public MulticastSimulator(final List<LinkedBlockingQueue<Integer>> sendingQueues, final List<LinkedBlockingQueue<Integer>> receivingQueues) {
        this.sendingQueues = sendingQueues;
        this.receivingQueues = receivingQueues;

        this.running = false;
    }

    public void run() throws InterruptedException {
        running = true;
        while (running) {
            for (final LinkedBlockingQueue<Integer> sender : sendingQueues) {
                if (!sender.isEmpty()) {
                    final Integer message = sender.take();
                    for (final LinkedBlockingQueue<Integer> receiver : receivingQueues) {
                        receiver.put(message);
                    }
                }
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}
