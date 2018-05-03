package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.Paxos.Messages.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chris on 4/23/18.
 */
public class Proposer<A extends Serializable> {
    private static final boolean DEBUG = false;

    private static final long TIMEOUT = 1000;
    private static final long TIMEOUT_VARIATION = 100;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private static final int ASSUMED_OUT_OF_DATE = 5;

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

    private final LinkedBlockingQueue<Message> messageQueue;
    private final LinkedBlockingQueue<Message> sendQueue;

    /**
     * A value indicating if the Proposer should continue running. It is set to true when run() is called, and then is
     * set to be false when shutdown() is called, in order to stop the run() loop.
     */
    private final AtomicBoolean running;

    /**
     * The id of the last proposal sent. Should follow (lastProposalId % numNodes) == id
     */
    private int lastProposalId;

    /**
     * The value for the newest proposal made.
     */
    private Optional<A> newestProposalValue;

    /**
     * The number of promises received from Acceptors for the last proposal made.
     */
    private final HashMap<Integer, Integer> promiseCounts;
    private final HashMap<Integer, Integer> acceptCounts;

    private final HashMap<Integer, Boolean> promiseMajorities;
    private boolean reachedAcceptMajority;

    private int paxosRound;
    private final ReentrantLock processMessageLock;
    private final AtomicInteger largestKnownRound;
    private final ThreadLocalRandom rand;

    public Proposer(final int numNodes, final int id, final LinkedBlockingQueue<Message> messageQueue,
                    final LinkedBlockingQueue<Message> sendQueue, final AtomicInteger largestKnownRound) {
        this.numNodes = numNodes;
        this.majority = (numNodes / 2) + 1;

        this.id = id;
        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.lastProposalId = id - numNodes;        // Subtracts numNodes, so that the next proposalId will be id

        this.running = new AtomicBoolean(false);
        this.newestProposalValue = Optional.empty();
        this.reachedAcceptMajority = false;

        this.promiseCounts = new HashMap<>();
        this.acceptCounts = new HashMap<>();
        this.promiseMajorities = new HashMap<>();

        this.paxosRound = 1;
        this.processMessageLock = new ReentrantLock();
        this.largestKnownRound = largestKnownRound;
        this.rand = ThreadLocalRandom.current();
    }

    private int getNextProposalId() {
        lastProposalId += numNodes;
        reachedAcceptMajority = false;
        return lastProposalId;
    }

    public void run() throws InterruptedException {
        running.set(true);

        while (running.get()) {
            Message message = messageQueue.poll(TIMEOUT + (rand.nextLong(TIMEOUT_VARIATION) - TIMEOUT_VARIATION / 2),
                    TIMEOUT_UNIT);

            if (message == null) {
                if (newestProposalValue.isPresent()) {
                    message = new ProposalRequest<>(newestProposalValue.get());

                    if (DEBUG) System.out.println(this + " requeued timed-out proposal " + message);

                    if (lastProposalId > ASSUMED_OUT_OF_DATE * numNodes) {
                        if (DEBUG) System.out.println(this + " assumed it is out of date");
                        final int prevLargest = largestKnownRound.get();
                        largestKnownRound.compareAndSet(prevLargest, prevLargest + 1);
                    }
                } else {
                    continue;
                }
            }

            if (DEBUG) System.out.println(this + " polled " + message);

            processMessageLock.lock();
            try {
                if (messageFromDifferentRound(message)) {
                    if (DEBUG) System.out.println(this + " ignored outdated message " + message);
                    continue;
                }

                if (message instanceof ProposalRequest) {
                    final ProposalRequest<A> proposalRequest = (ProposalRequest<A>) message;

                    newestProposalValue = Optional.of(proposalRequest.getValue());
                    sendRequestToAllAcceptors(getNextProposalId());
                } else if (message instanceof Promise) {
                    final Promise<A> promise = (Promise<A>) message;
                    final int proposalId = promise.getProposalID();

                    if (!promiseMajorities.getOrDefault(proposalId, false)) {
                        incrementCount(promiseCounts, proposalId);
                        if (promise.hasAcceptedValue()) {
                            // TODO: Double check that this works
                            final boolean receivedProposalIdBetter = promise.getAcceptedID() > proposalId;
                            if (receivedProposalIdBetter) {
                                newestProposalValue = Optional.of(promise.getAcceptedValue());
                            }
                        }

                        if (DEBUG) System.out.println(this + " promises (" + proposalId + ") " + promiseCounts.get(proposalId) + "/" + majority);
                        if (promiseCounts.get(proposalId) >= majority) {
                            promiseMajorities.put(proposalId, true);
                            sendAcceptRequestToAllAcceptors(proposalId, newestProposalValue.get());
                        }
                    }
                } else if (message instanceof Accept) { // TODO: Is this really needed if Learner will reset rounds?
                    final Accept<A> accept = (Accept<A>) message;
                    final int proposalId = accept.getProposalID();

                    if (!reachedAcceptMajority) {
                        incrementCount(acceptCounts, proposalId);

                        if (DEBUG) System.out.println(this + " acceptances (" + proposalId + ") " + acceptCounts.get(proposalId) + "/" + majority);
                        if (acceptCounts.get(proposalId) >= majority) {
                            reachedAcceptMajority = true;

                            if (DEBUG) System.out.println(this + " reached majority on " + accept.getProposalValue());
                        }
                    }
                }
            } finally {
                processMessageLock.unlock();
            }
        }
    }

    public void newRound(final int newRound) {
        processMessageLock.lock();
        try {
            paxosRound = newRound;
            newestProposalValue = Optional.empty();
            promiseCounts.clear();
            acceptCounts.clear();
            promiseMajorities.clear();

            lastProposalId = id - numNodes;     // Subtracts numNodes, so that the next proposalId will be id
            reachedAcceptMajority = false;

            if (DEBUG) System.out.println(this + " started round " + newRound);
        } finally {
            processMessageLock.unlock();
        }
    }

    public void shutdown() {
        running.set(false);
    }

    private void sendRequestToAllAcceptors(final int proposalId) throws InterruptedException {
        final Prepare prepare = new Prepare(proposalId, PaxosMessage.EVERYONE, PaxosMessage.ACCEPTOR, paxosRound);

        if (DEBUG) System.out.println(this + " sent " + prepare);

        sendQueue.put(prepare);
    }

    private void sendAcceptRequestToAllAcceptors(final int proposalId, final A value) throws InterruptedException {
        final AcceptRequest<A> acceptRequest = new AcceptRequest<>(proposalId, value, PaxosMessage.EVERYONE, PaxosMessage.ACCEPTOR, paxosRound);

        if (DEBUG) System.out.println(this + " sent " + acceptRequest);

        sendQueue.put(acceptRequest);
    }

    private void incrementCount(final HashMap<Integer, Integer> countTable, final int proposalId) {
        if (countTable.containsKey(proposalId)) {
            countTable.compute(proposalId, (key, prev) -> prev + 1);
        } else {
            countTable.put(proposalId, 1);
        }
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

    @Override
    public String toString() {
        return "Proposer[" + id + "] @" + paxosRound;
    }

    /**
     * Uses the proposalId of a message to determine which node sent the message.
     *
     * @param proposalId The proposalId of the node to identify.
     * @param numNodes The number of nodes in the Paxos run.
     * @return The id of the node.
     */
    public static int computeNodeId(final int proposalId, final int numNodes) {
        return proposalId % numNodes;
    }
}
