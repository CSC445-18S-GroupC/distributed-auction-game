package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.Communication.MessageForwarding;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.ProposalRequest;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A Paxos object is used to allow multiple machines to agree on the additions of values to a log and applying added
 * values to a state object as soon as consensus on the value is achieved.
 * <br><br>
 * <pre>
 * final Paxos&lt;GameState&gt; paxos = new Paxos&lt;GameState&gt;(0, 3, (step) -&gt; state.apply(step));
 *
 * paxos.run();
 *
 * paxos.proposeValue(nextStep);
 * </pre>
 */
public class Paxos<A extends Serializable> {
    private final int id;
    private final int numNodes;
    private final Consumer<A> applicationFunction;

    private final AtomicInteger largestKnownRound;

    private final LinkedBlockingQueue<Message> sendingQueue;
    private final LinkedBlockingQueue<Message> receivingQueue;

    private final LinkedBlockingQueue<Message> receiveQueueProposer;
    private final LinkedBlockingQueue<Message> receiveQueueAcceptor;
    private final LinkedBlockingQueue<Message> receiveQueueLearner;

    private final Proposer<A> proposer;
    private final Acceptor<A> acceptor;
    private final Learner<A> learner;

    /**
     * Creates an object to represent, control, and interact with a Paxos run.
     *
     * @param id The id of this Paxos node. [0, numNodes)
     * @param numNodes The number of nodes in the Paxos run.
     * @param applicationFunction The function to call each time a new value is committed.
     */
    public Paxos(final int id, final int numNodes, final Consumer<A> applicationFunction) {
        this.id = id;
        this.numNodes = numNodes;
        this.applicationFunction = applicationFunction;

        this.largestKnownRound = new AtomicInteger(0);

        this.sendingQueue = new LinkedBlockingQueue<>();
        this.receivingQueue = new LinkedBlockingQueue<>();

        this.receiveQueueProposer = new LinkedBlockingQueue<>();
        this.receiveQueueAcceptor = new LinkedBlockingQueue<>();
        this.receiveQueueLearner = new LinkedBlockingQueue<>();

        this.proposer = new Proposer<>(numNodes, id, receiveQueueProposer, sendingQueue, largestKnownRound);
        this.acceptor = new Acceptor<>(numNodes, id, receiveQueueAcceptor, sendingQueue, largestKnownRound);
        this.learner = new Learner<>(numNodes, id, receiveQueueLearner, sendingQueue, proposer, acceptor, largestKnownRound, applicationFunction);
    }

    /**
     * Returns the message sending queue for the Paxos run. This queue contains all of the messages from the different
     * Paxos roles that need to be sent to other nodes.
     * <br><br>
     * This is intended to be used by a multicast-based message sending thread, so that the message sending is decoupled
     * from the Paxos logic.
     *
     * @return The message sending queue for the Paxos run.
     */
    public LinkedBlockingQueue<Message> getSendingQueue() {
        return sendingQueue;
    }

    /**
     * Returns the message receiving queue for the Paxos run. This queue should be given all of the messages intended
     * for the Paxos roles.
     * <br><br>
     * This is intended to be used by a multicast-based message receiving thread, so that the message receiving is
     * decoupled from the Paxos logic.
     *
     * @return The message receiving queue for the Paxos run.
     */
    public LinkedBlockingQueue<Message> getReceivingQueue() {
        return receivingQueue;
    }

    /**
     * Starts up the run of Paxos. Must be run before proposing any values.
     * <br><br>
     * Each separate role of Paxos will be run in a different thread.
     */
    public void run() {
        onThread(() -> {
            try {
                proposer.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        onThread(() -> {
            try {
                acceptor.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        onThread(() -> {
            try {
                learner.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        onThread(() -> {
            try {
                MessageForwarding.run(id, receivingQueue, receiveQueueProposer, receiveQueueAcceptor, receiveQueueLearner);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Proposes the given value to be appended to the log during the current Paxos round.
     * <br><br>
     * Note that this does <b>not</b> guarantee that the value will be committed to the log, only that it will be
     * proposed and possibly be committed.
     *
     * @param value The value to propose.
     * @throws InterruptedException If interrupted while trying to make the proposal.
     */
    public void proposeStep(final A value) throws InterruptedException {
        receivingQueue.put(new ProposalRequest<>(value));
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
