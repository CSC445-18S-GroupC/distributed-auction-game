package csc445.groupc.distauction;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 4/13/18.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        final String group = "230.1.1.1";
        final int sendPort = 5323;
        final int receivePort = 5921;

        final int numNodes = 3;
        final int id = 0;

        final LinkedBlockingQueue<Integer> sendQueue = new LinkedBlockingQueue<>();

        final LinkedBlockingQueue<Integer> receiveQueueProposer = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Integer> receiveQueueAcceptor = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Integer> receiveQueueLearner = new LinkedBlockingQueue<>();

        final Proposer proposer = new Proposer(numNodes, id, receiveQueueProposer, sendQueue);
        final Acceptor acceptor = new Acceptor(receiveQueueAcceptor, sendQueue);
        final Learner learner = new Learner(numNodes, receiveQueueLearner, sendQueue);

        onThread(() -> {
            try {MessageReceiving.run(group, receivePort, receiveQueueProposer);} catch (Exception e) {}
        });
        onThread(() -> {
            try {MessageSending.run(group, sendPort, receivePort, sendQueue);} catch (Exception e) {}
        });
        onThread(() -> {
            try {proposer.run();} catch (Exception e) {}
        });
        onThread(() -> {
            try {acceptor.run();} catch (Exception e) {}
        });
        onThread(() -> {
            try {learner.run();} catch (Exception e) {}
        });
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
