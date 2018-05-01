package csc445.groupc.distauction.Paxos;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by chris on 4/28/18.
 */
public class Acceptor {
    private static final Integer REQUEST = 9;
    private static final Integer PREPARE = 0;
    private static final Integer PROMISE_NO_VALUE = 1;
    private static final Integer PROMISE_WITH_VALUE = 2;
    private static final Integer ACCEPT_REQUEST = 3;
    private static final Integer ACCEPT = 4;

    // TODO: Change to actual messages
    private final LinkedBlockingQueue<Integer> messageQueue;
    private final LinkedBlockingQueue<Integer> sendQueue;

    /**
     * The proposal id of the largest proposal that the Acceptor has promised.
     */
    private int promisedProposalId;

    /**
     * A value indicating if the Acceptor should continue running. It is set to true when run() is called, and then is
     * set to be false when shutdown() is called, in order to stop the run() loop.
     */
    private final AtomicBoolean running;

    public Acceptor(final LinkedBlockingQueue<Integer> messageQueue, final LinkedBlockingQueue<Integer> sendQueue) {
        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.running = new AtomicBoolean(false);

        this.promisedProposalId = -1;
    }

    public void run() throws InterruptedException {
        running.set(true);

        while (running.get()) {
            // TODO: Change to use real messages
            final Integer message = messageQueue.take();

            // TODO: Update to work with actual messages
            if (message.equals(PREPARE)) {
                final int proposalId = 5;

                if (!proposalIsObsolete(proposalId)) {
                    if (alreadyHaveAProposal()) {
                        sendPromiseWithPreviousValue(proposalId);
                    } else {
                        sendRegularPromise(proposalId);
                    }

                    promisedProposalId = proposalId;
                }
            } else if (message.equals(ACCEPT_REQUEST)) {
                final int proposalId = 5;

                if (!proposalIsObsolete(proposalId)) {
                    sendAcceptToProposer(proposalId);
                    sendAcceptToAllLearners(proposalId);
                }
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }

    private boolean proposalIsObsolete(final int proposalId) {
        return proposalId < promisedProposalId;
    }

    private boolean alreadyHaveAProposal() {
        // TODO: Implement
        return false;
    }

    private void sendPromiseWithPreviousValue(final int proposalId) {
        // TODO: Implement
    }

    private void sendRegularPromise(final int proposalId) {
        // TODO: Implement
    }

    private void sendAcceptToProposer(final int proposalId) {
        // TODO: Implement
    }

    private void sendAcceptToAllLearners(final int proposalId) {
        // TODO: Implement
    }
}
