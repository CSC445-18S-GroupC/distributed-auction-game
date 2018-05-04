package csc445.groupc.distauction.Communication;

import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.ProposalRequest;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Multicast Simulator is a class that is used to simulate the multicast communication that Paxos will be using for
 * sending messages between nodes. This simulator will allow all of the Paxos logic to be tested without worrying about
 * packet drops or odd behaviour with multicast.
 */
public class MulticastSimulator {
    private static final boolean DEBUG = false;
    private static final Optional<Integer> DROP_CHANCE = Optional.empty();//Optional.of(10);

    private final List<LinkedBlockingQueue<Message>> sendingQueues;
    private final List<LinkedBlockingQueue<Message>> receivingQueues;

    private boolean running;
    private final Random rand;

    public MulticastSimulator(final List<LinkedBlockingQueue<Message>> sendingQueues, final List<LinkedBlockingQueue<Message>> receivingQueues) {
        this.sendingQueues = sendingQueues;
        this.receivingQueues = receivingQueues;

        this.running = false;
        this.rand = new Random();
    }

    public void run() throws InterruptedException {
        running = true;
        while (running) {
            for (final LinkedBlockingQueue<Message> sender : sendingQueues) {
                if (!sender.isEmpty()) {
                    final Message message = sender.take();
                    for (final LinkedBlockingQueue<Message> receiver : receivingQueues) {
                        if (!DROP_CHANCE.isPresent() || message instanceof ProposalRequest || rand.nextInt(DROP_CHANCE.get()) != 0) {
                            receiver.put(message);
                        } else {
                            if (DEBUG) System.out.println("Dropped " + message + " to a receiver");
                        }
                    }
                }
            }
        }
    }

    public void shutdown() {
        running = false;
    }
}
