package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.GameStep;
import csc445.groupc.distauction.Paxos.Messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by chris on 4/28/18.
 */
public class Learner {
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
    private final ArrayList<GameStep> log;

    public Learner(final int numNodes, final int id, final LinkedBlockingQueue<Message> messageQueue, final LinkedBlockingQueue<Message> sendQueue, final Proposer proposer, final Acceptor acceptor) {
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
        this.log = new ArrayList<>();
    }

    public void run() throws InterruptedException {
        running.set(true);

        // TODO: Add method to update game state when behind

        while (running.get()) {
            final Message message = messageQueue.take();

            processMessageLock.lock();
            try {
                if (messageFromPreviousRound(message)) {
                    continue;
                }

                if (message instanceof Accept) {
                    final Accept<GameStep> accept = (Accept<GameStep>) message;

                    final int proposalId = accept.getProposalID();
                    final GameStep value = accept.getProposalValue();

                    incrementAccepts(proposalId);

                    if (majorityJustReached(proposalId)) {
                        consensus(value);
                    }
                } else if (message instanceof UpdateRequest) {
                    final UpdateRequest updateRequest = (UpdateRequest) message;
                    final int entryId = updateRequest.getEntryId();

                    // TODO: Confirm that this works
                    if (entryId < paxosRound) {
                        final GameStep value = log.get(entryId - 1);
                        sendValueToBehindLearners(entryId, value);
                    }
                } else if (message instanceof Update) {
                    // TODO: Implement
                }
            } finally {
                processMessageLock.unlock();
            }
        }
    }

    public void shutdown() {
        running.set(false);
    }

    private void sendValueToBehindLearners(final int entryId, final GameStep value) throws InterruptedException {
        final Update<GameStep> update = new Update<>(entryId, value, PaxosMessage.EVERYONE, PaxosMessage.LEARNER, PaxosMessage.NO_SPECIFIC_ROUND);

        System.out.println(this + " sent " + update);

        sendQueue.put(update);
    }

    private void incrementAccepts(final int proposalId) {
        if (messageAcceptances.containsKey(proposalId)) {
            messageAcceptances.compute(proposalId, (key, prev) -> prev + 1);
        } else {
            messageAcceptances.put(proposalId, 1);
        }
        System.out.println(this + " accepts " + messageAcceptances.get(proposalId) + "/" + majority);
    }

    private boolean majorityJustReached(final int proposalId) {
        return messageAcceptances.get(proposalId) == majority;
    }

    private void consensus(final GameStep value) {
        System.out.println(this + " reached majority on " + value);

        final int newRound = paxosRound + 1;
        proposer.newRound(newRound);
        acceptor.newRound(newRound);
        this.newRound(newRound);

        System.out.println(this + " started round " + newRound);

        log.add(value);

        // TODO: Finish implementing (apply to Game State)
    }

    private boolean messageFromPreviousRound(final Message message) {
        return message instanceof PaxosMessage &&
                ((PaxosMessage) message).getPaxosRound() != paxosRound &&
                ((PaxosMessage) message).getPaxosRound() != PaxosMessage.NO_SPECIFIC_ROUND;
    }

    public void newRound(final int newRound) {
        processMessageLock.lock();
        try {
            paxosRound = newRound;
            messageAcceptances.clear();
        } finally {
            processMessageLock.unlock();
        }
    }

    @Override
    public String toString() {
        return "Learner[" + id + "]";
    }
}
