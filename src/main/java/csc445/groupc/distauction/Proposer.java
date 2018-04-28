package csc445.groupc.distauction;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by chris on 4/23/18.
 */
public class Proposer {
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

    /**
     * The id of this Proposer node. Should be within [0, numNodes).
     */
    private final int id;

    // TODO: Change to actual messages
    private final LinkedBlockingQueue<Integer> messageQueue;
    private final LinkedBlockingQueue<Integer> sendQueue;

    /**
     * A value indicating if the Proposer should continue running. It is set to true when run() is called, and then is
     * set to be false when shutdown() is called, in order to stop the run() loop.
     */
    private final AtomicBoolean running;

    /**
     * The id of the last proposal sent. Should follow (lastProposalId % numNodes) == id
     */
    private long lastProposalId;

    /**
     * The value for the newest proposal made.
     */
    private Optional<GameStep> newestProposalValue;

    /**
     * The number of promises received from Acceptors for the last proposal made.
     */
    private int lastProposalPromises;
    private int lastProposalAccepts;

    private boolean reachedPromiseMajority;
    private boolean reachedAcceptMajority;

    public Proposer(final int numNodes, final int id, final LinkedBlockingQueue<Integer> messageQueue,
                    final LinkedBlockingQueue<Integer> sendQueue) {
        this.numNodes = numNodes;
        this.majority = (numNodes / 2) + 1;

        this.id = id;
        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.lastProposalId = id - numNodes;        // Subtracts numNodes, so that the next proposalId will be id

        this.running = new AtomicBoolean(false);
        this.newestProposalValue = Optional.empty();
        this.lastProposalPromises = 0;
        this.reachedPromiseMajority = false;
        this.reachedAcceptMajority = false;
    }

    private long getNextProposalId() {
        lastProposalId += numNodes;
        lastProposalPromises = 0;
        reachedPromiseMajority = false;
        reachedAcceptMajority = false;
        return lastProposalId;
    }

    public void run() throws InterruptedException {
        running.set(true);

        while (running.get()) {
            // TODO: Change to use real messages
            final Integer message = messageQueue.take();

            // TODO: Update to work with actual messages
            if (message.equals(REQUEST)) {
                newestProposalValue = Optional.of(new GameStep());   // TODO: Change to use the correct game step
                sendRequestToAllAcceptors(getNextProposalId());
            } else if (message.equals(PROMISE_NO_VALUE) || message.equals(PROMISE_WITH_VALUE)) {
                // TODO: Add a condition to check if the received promise is for the latest proposal
                final boolean isLastProposal = true;
                if (!reachedPromiseMajority && isLastProposal) {
                    ++lastProposalPromises;
                    if (message.equals(PROMISE_WITH_VALUE)) {
                        final boolean receivedProposalIdBetter = true;
                        if (receivedProposalIdBetter) {
                            // TODO: Change to the value contained in the promise
                            newestProposalValue = Optional.of(new GameStep());
                        }
                    }

                    if (lastProposalPromises >= majority) {
                        reachedPromiseMajority = true;
                        sendAcceptRequestToAllAcceptors(lastProposalId, newestProposalValue.get());
                    }
                }
            } else if (message.equals(ACCEPT)) {
                final boolean isLastProposal = true;
                if (!reachedAcceptMajority && isLastProposal) {
                    ++lastProposalAccepts;

                    if (lastProposalAccepts >= majority) {
                        reachedAcceptMajority = true;
                    }
                }
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }

    private void sendRequestToAllAcceptors(final long proposalId) {
        // TODO: Implement this
    }

    private void sendAcceptRequestToAllAcceptors(final long proposalId, final GameStep value) {
        // TODO: Implement this
    }
}
