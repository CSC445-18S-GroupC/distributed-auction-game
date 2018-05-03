package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.Paxos.Messages.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Created by chris on 4/28/18.
 */
public class Learner<A extends Serializable> {
    private static final boolean DEBUG = false;

    private static final long TIMEOUT = 10;
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    private static final int UPDATE_CHECK_POINT = 100;

    /**
     * The total number of nodes in the Paxos run.
     */
    private final int numNodes;

    /**
     * The id of this Learner node. Should be within [0, numNodes).
     */
    private final int id;

    /**
     * The number of nodes needed to form a majority.
     */
    private final int majority;

    private final LinkedBlockingQueue<Message> messageQueue;
    private final LinkedBlockingQueue<Message> sendQueue;

    /**
     * A value indicating if the Learner should continue running. It is set to true when run() is called, and then is
     * set to be false when shutdown() is called, in order to stop the run() loop.
     */
    private final AtomicBoolean running;

    private final HashMap<Integer, Integer> messageAcceptances;

    private final Proposer proposer;
    private final Acceptor acceptor;

    private int paxosRound;
    private final ReentrantLock processMessageLock;
    private final AtomicInteger largestKnownRound;

    private final ArrayList<A> log;
    private final Consumer<A> appicationFunction;

    private int updateWaitCount;

    public Learner(final int numNodes, final int id, final LinkedBlockingQueue<Message> messageQueue, final LinkedBlockingQueue<Message> sendQueue, final Proposer proposer, final Acceptor acceptor, final AtomicInteger largestKnownRound, final Consumer<A> appicationFunction) {
        this.numNodes = numNodes;
        this.majority = (numNodes / 2) + 1;

        this.id = id;
        this.messageQueue = messageQueue;
        this.sendQueue = sendQueue;

        this.running = new AtomicBoolean(false);
        this.messageAcceptances = new HashMap<>();

        this.proposer = proposer;
        this.acceptor = acceptor;

        this.paxosRound = 1;
        this.processMessageLock = new ReentrantLock();
        this.largestKnownRound = largestKnownRound;

        this.log = new ArrayList<>();
        this.appicationFunction = appicationFunction;

        this.updateWaitCount = 0;
    }

    public void run() throws InterruptedException {
        running.set(true);

        while (running.get()) {
            final Message message = messageQueue.poll(TIMEOUT, TIMEOUT_UNIT);

            if (largestKnownRound.get() > paxosRound || updateWaitCount > UPDATE_CHECK_POINT) {
                sendUpdateRequestToAllLearners(paxosRound);
                updateWaitCount = 0;
            } else {
                ++updateWaitCount;
            }

            if (message == null) {
                continue;
            }

            if (DEBUG) System.out.println(this + " received " + message);

            processMessageLock.lock();
            try {
                if (messageFromDifferentRound(message)) {
                    if (DEBUG) System.out.println(this + " ignored outdated message " + message);
                    continue;
                }

                if (message instanceof Accept) {
                    final Accept<A> accept = (Accept<A>) message;

                    final int proposalId = accept.getProposalID();
                    final A value = accept.getProposalValue();

                    incrementAccepts(proposalId);

                    if (majorityJustReached(proposalId)) {
                        consensus(value);
                    }
                } else if (message instanceof UpdateRequest) {
                    final UpdateRequest updateRequest = (UpdateRequest) message;
                    final int entryId = updateRequest.getEntryId();

                    if (entryId < paxosRound) {
                        final A value = log.get(entryId - 1);
                        sendValueToBehindLearners(entryId, value);
                    }
                } else if (message instanceof Update) {
                    final Update<A> update = (Update<A>) message;
                    final int entryId = update.getEntryId();
                    final A value = update.getValue();

                    if (DEBUG) System.out.println(this + " processed update " + update);

                    if (entryId == paxosRound) {
                        consensus(value);
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

    private void sendUpdateRequestToAllLearners(final int entryId) throws InterruptedException {
        final UpdateRequest updateRequest = new UpdateRequest(entryId, PaxosMessage.EVERYONE, PaxosMessage.LEARNER);

        if (DEBUG) System.out.println(this + " sent " + updateRequest);

        sendQueue.put(updateRequest);
    }

    private void sendValueToBehindLearners(final int entryId, final A value) throws InterruptedException {
        final Update<A> update = new Update<>(entryId, value, PaxosMessage.EVERYONE, PaxosMessage.LEARNER);

        if (DEBUG) System.out.println(this + " sent " + update);

        sendQueue.put(update);
    }

    private void incrementAccepts(final int proposalId) {
        if (messageAcceptances.containsKey(proposalId)) {
            messageAcceptances.compute(proposalId, (key, prev) -> prev + 1);
        } else {
            messageAcceptances.put(proposalId, 1);
        }
        if (DEBUG) System.out.println(this + " accepts " + messageAcceptances.get(proposalId) + "/" + majority);
    }

    private boolean majorityJustReached(final int proposalId) {
        return messageAcceptances.get(proposalId) == majority;
    }

    private void consensus(final A value) {
        if (DEBUG) System.out.println(this + " reached majority on " + value);

        final int newRound = paxosRound + 1;
        proposer.newRound(newRound);
        acceptor.newRound(newRound);
        this.newRound(newRound);

        log.add(value);

        appicationFunction.accept(value);
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
            messageAcceptances.clear();

            if (DEBUG) System.out.println(this + " started round " + newRound);
        } finally {
            processMessageLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Learner[" + id + "] @" + paxosRound;
    }
}
