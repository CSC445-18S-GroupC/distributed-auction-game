package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.Paxos.Messages.*;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chris on 4/28/18.
 */
public class Acceptor<A extends Serializable> {
    private static final boolean DEBUG = false;

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

    private Optional<A> acceptedValue;

    private int paxosRound;
    private final ReentrantLock processMessageLock;
    private final AtomicInteger largestKnownRound;

    public Acceptor(final int numNodes, final int id, final LinkedBlockingQueue<Message> messageQueue, final LinkedBlockingQueue<Message> sendQueue, final AtomicInteger largestKnownRound) {
        this.numNodes = numNodes;

        this.id = id;
        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.running = new AtomicBoolean(false);

        this.promisedProposalId = -1;
        this.acceptedValue = Optional.empty();

        this.paxosRound = 1;
        this.processMessageLock = new ReentrantLock();
        this.largestKnownRound = largestKnownRound;
    }

    public void run() throws InterruptedException {
        running.set(true);

        while (running.get()) {
            final Message message = messageQueue.take();

            if (DEBUG) System.out.println(this + " polled " + message);

            processMessageLock.lock();
            try {
                if (messageFromDifferentRound(message)) {
                    if (DEBUG) System.out.println(this + " ignored outdated message " + message);
                    continue;
                }

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
                    } else {
                        if (DEBUG) System.out.println(this + " ignored prepare " + proposalId + " due to already promising " + promisedProposalId);
                    }
                } else if (message instanceof AcceptRequest) {
                    final AcceptRequest<A> acceptRequest = (AcceptRequest<A>) message;

                    final int proposalId = acceptRequest.getProposalID();
                    final A value = acceptRequest.getProposalValue();

                    if (!proposalIsObsolete(proposalId)) {
                        acceptedValue = Optional.of(value);

                        sendAcceptToProposer(proposalId, value);
                        sendAcceptToAllLearners(proposalId, value);
                    } else {
                        if (DEBUG) System.out.println(this + " ignored accept request " + proposalId + " due to already promising " + promisedProposalId);
                    }
                }
            } finally {
                processMessageLock.unlock();
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
        final Promise<A> promise = new Promise<>(proposalId, promisedProposalId, acceptedValue.get()
                , Optional.of(recipient), PaxosMessage.PROPOSER, paxosRound);

        if (DEBUG) System.out.println(this + " sent " + promise);

        sendQueue.put(promise);
    }

    private void sendRegularPromise(final int proposalId) throws InterruptedException {
        final int recipient = Proposer.computeNodeId(proposalId, numNodes);
        final Promise<A> promise = new Promise<>(proposalId, Optional.of(recipient), PaxosMessage.PROPOSER, paxosRound);

        if (DEBUG) System.out.println(this + " sent " + promise);

        sendQueue.put(promise);
    }

    private void sendAcceptToProposer(final int proposalId, final A value) throws InterruptedException {
        final int recipient = Proposer.computeNodeId(proposalId, numNodes);
        final Accept<A> accept = new Accept<>(proposalId, value, Optional.of(recipient), PaxosMessage.PROPOSER, paxosRound);

        if (DEBUG) System.out.println(this + " sent " + accept);

        sendQueue.put(accept);
    }

    private void sendAcceptToAllLearners(final int proposalId, final A value) throws InterruptedException {
        final Accept<A> accept = new Accept<>(proposalId, value, PaxosMessage.EVERYONE, PaxosMessage.LEARNER, paxosRound);

        if (DEBUG) System.out.println(this + " sent " + accept);

        sendQueue.put(accept);
    }

    private boolean messageFromDifferentRound(final Message message) {
        if (message instanceof PaxosMessage && ((PaxosMessage) message).getPaxosRound() != PaxosMessage.NO_SPECIFIC_ROUND) {
            final int messageRound = ((PaxosMessage) message).getPaxosRound();
            final int prevLargest = largestKnownRound.get();

            if (messageRound > prevLargest) {
                largestKnownRound.compareAndSet(prevLargest, messageRound);
            }

            if (messageRound != paxosRound) {
                return true;
            }
        }
        return false;
    }

    public void newRound(final int newRound) {
        processMessageLock.lock();
        try {
            paxosRound = newRound;

            promisedProposalId = -1;
            acceptedValue = Optional.empty();

            if (DEBUG) System.out.println(this + " started round " + newRound);
        } finally {
            processMessageLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Acceptor[" + id + "] @" + paxosRound;
    }
}
