package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.GameStep;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.PaxosMessage;
import csc445.groupc.distauction.Paxos.Messages.Prepare;
import csc445.groupc.distauction.Paxos.Messages.Promise;

import java.util.Optional;
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
    private final LinkedBlockingQueue<Message> messageQueue;
    private final LinkedBlockingQueue<Message> sendQueue;

    /**
     * The total number of nodes in the Paxos run.
     */
    private final int numNodes;

    /**
     * The id of this Acceptor node. Should be within [0, numNodes).
     */
    private final int id;

    /**
     * The proposal id of the largest proposal that the Acceptor has promised.
     */
    private int promisedProposalId;

    /**
     * A value indicating if the Acceptor should continue running. It is set to true when run() is called, and then is
     * set to be false when shutdown() is called, in order to stop the run() loop.
     */
    private final AtomicBoolean running;

    private Optional<GameStep> acceptedValue;

    public Acceptor(final int numNodes, final int id, final LinkedBlockingQueue<Message> messageQueue, final LinkedBlockingQueue<Message> sendQueue) {
        this.numNodes = numNodes;

        this.id = id;
        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.running = new AtomicBoolean(false);

        this.promisedProposalId = -1;
        this.acceptedValue = Optional.empty();
    }

    public void run() throws InterruptedException {
        running.set(true);

        while (running.get()) {
            final Message message = messageQueue.take();

            System.out.println(this + " polled " + message);

            if (message instanceof Prepare) {
                final Prepare prepare = (Prepare) message;

                final int proposalId = prepare.getProposalID();

                if (!proposalIsObsolete(proposalId)) {
                    if (alreadyAcceptedAProposal()) {
                        sendPromiseWithPreviousValue(proposalId);
                    } else {
                        sendRegularPromise(proposalId);
                    }

                    promisedProposalId = proposalId;
                }
            } else if (message.equals(ACCEPT_REQUEST)) {
                // TODO: Update to work with actual messages
                final int proposalId = 5;
                final GameStep value = new GameStep();

                if (!proposalIsObsolete(proposalId)) {
                    acceptedValue = Optional.of(value);

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

    private boolean alreadyAcceptedAProposal() {
        return acceptedValue.isPresent();
    }

    private void sendPromiseWithPreviousValue(final int proposalId) throws InterruptedException {
        final int recipient = Proposer.computeNodeId(proposalId, numNodes);
        final Promise<GameStep> promise = new Promise<>(proposalId, promisedProposalId, acceptedValue.get()
                , Optional.of(recipient), PaxosMessage.PROPOSER);

        System.out.println(this + " sent " + promise);

        sendQueue.put(promise);
    }

    private void sendRegularPromise(final int proposalId) throws InterruptedException {
        final int recipient = Proposer.computeNodeId(proposalId, numNodes);
        final Promise<GameStep> promise = new Promise<>(proposalId, Optional.of(recipient), PaxosMessage.PROPOSER);

        System.out.println(this + " sent " + promise);

        sendQueue.put(promise);
    }

    private void sendAcceptToProposer(final int proposalId) {
        // TODO: Implement
    }

    private void sendAcceptToAllLearners(final int proposalId) {
        // TODO: Implement
    }

    @Override
    public String toString() {
        return "Acceptor[" + id + "]";
    }
}
