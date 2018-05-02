package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.GameStep;
import csc445.groupc.distauction.Paxos.Messages.Accept;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.PaxosMessage;
import csc445.groupc.distauction.Paxos.Messages.Promise;

import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
    }

    public void run() throws InterruptedException {
        running.set(true);

        // TODO: Add method to update game state when behind

        while (running.get()) {
            final Message message = messageQueue.take();

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

        // TODO: Finish implementing (apply to Game State)
    }

    private boolean messageFromPreviousRound(final Message message) {
        return message instanceof PaxosMessage && ((PaxosMessage) message).getPaxosRound() != paxosRound;
    }

    public void newRound(final int newRound) {
        // TODO: Do this concurrency safe with run() method
        paxosRound = newRound;
        messageAcceptances.clear();
    }

    @Override
    public String toString() {
        return "Learner[" + id + "]";
    }
}
