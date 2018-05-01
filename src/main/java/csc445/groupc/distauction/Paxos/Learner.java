package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.GameStep;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by chris on 4/28/18.
 */
public class Learner {
    private static final Integer REQUEST = 9;
    private static final Integer PREPARE = 0;
    private static final Integer PROMISE_NO_VALUE = 1;
    private static final Integer PROMISE_WITH_VALUE = 2;
    private static final Integer ACCEPT_REQUEST = 3;
    private static final Integer ACCEPT = 4;

    /**
     * The total number of nodes in the Paxos run.
     */
    private final int numNodes;

    /**
     * The number of nodes needed to form a majority.
     */
    private final int majority;

    // TODO: Change to actual messages
    private final LinkedBlockingQueue<Integer> messageQueue;
    private final LinkedBlockingQueue<Integer> sendQueue;

    /**
     * A value indicating if the Learner should continue running. It is set to true when run() is called, and then is
     * set to be false when shutdown() is called, in order to stop the run() loop.
     */
    private final AtomicBoolean running;

    private final HashMap<Integer, Integer> messageAcceptances;

    public Learner(final int numNodes, final LinkedBlockingQueue<Integer> messageQueue, final LinkedBlockingQueue<Integer> sendQueue) {
        this.numNodes = numNodes;
        this.majority = (numNodes / 2) + 1;

        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.running = new AtomicBoolean(false);
        this.messageAcceptances = new HashMap<>();
    }

    public void run() throws InterruptedException {
        running.set(true);

        // TODO: Add method to update game state when behind

        while (running.get()) {
            // TODO: Change to use real messages
            final Integer message = messageQueue.take();


            // TODO: Update to work with actual messages
            if (message.equals(ACCEPT)) {
                final int proposalId = 5;
                final GameStep value = new GameStep();

                incrementAccepts(proposalId);

                if (majorityJustReached(proposalId)) {
                    consensus(value);
                }
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }

    private void incrementAccepts(final int proposalId) {
        if (messageAcceptances.containsKey(proposalId)) {
            messageAcceptances.compute(proposalId, (key, prev) -> prev + 1);
        } else {
            messageAcceptances.put(proposalId, 1);
        }
    }

    private boolean majorityJustReached(final int proposalId) {
        return messageAcceptances.get(proposalId) == majority;
    }

    private void consensus(final GameStep gs) {
        // TODO: Implement
    }
}
