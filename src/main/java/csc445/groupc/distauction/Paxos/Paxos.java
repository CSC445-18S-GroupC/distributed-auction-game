package csc445.groupc.distauction.Paxos;

import csc445.groupc.distauction.Communication.MessageForwarding;
import csc445.groupc.distauction.GameLogic.GameState;
import csc445.groupc.distauction.GameLogic.GameStep;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.ProposalRequest;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chris on 5/3/18.
 */
public class Paxos {
    private final int id;
    private final int numNodes;
    private final GameState gameState;

    private final AtomicInteger largestKnownRound;

    private final LinkedBlockingQueue<Message> sendingQueue;
    private final LinkedBlockingQueue<Message> receivingQueue;

    private final LinkedBlockingQueue<Message> receiveQueueProposer;
    private final LinkedBlockingQueue<Message> receiveQueueAcceptor;
    private final LinkedBlockingQueue<Message> receiveQueueLearner;

    private final Proposer proposer;
    private final Acceptor acceptor;
    private final Learner learner;

    public Paxos(final int id, final int numNodes, final GameState gameState) {
        this.id = id;
        this.numNodes = numNodes;
        this.gameState = gameState;

        this.largestKnownRound = new AtomicInteger(0);

        this.sendingQueue = new LinkedBlockingQueue<>();
        this.receivingQueue = new LinkedBlockingQueue<>();

        this.receiveQueueProposer = new LinkedBlockingQueue<>();
        this.receiveQueueAcceptor = new LinkedBlockingQueue<>();
        this.receiveQueueLearner = new LinkedBlockingQueue<>();

        this.proposer = new Proposer(numNodes, id, receiveQueueProposer, sendingQueue, largestKnownRound);
        this.acceptor = new Acceptor(numNodes, id, receiveQueueAcceptor, sendingQueue, largestKnownRound);
        this.learner = new Learner(numNodes, id, receiveQueueLearner, sendingQueue, proposer, acceptor, largestKnownRound, gameState);
    }

    public LinkedBlockingQueue<Message> getSendingQueue() {
        return sendingQueue;
    }

    public LinkedBlockingQueue<Message> getReceivingQueue() {
        return receivingQueue;
    }

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

    public void proposeStep(final GameStep gameStep) throws InterruptedException {
        receivingQueue.put(new ProposalRequest(gameStep));
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
