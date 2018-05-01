package csc445.groupc.distauction;

import csc445.groupc.distauction.Communication.MessageForwarding;
import csc445.groupc.distauction.Communication.MulticastSimulator;
import csc445.groupc.distauction.Paxos.Acceptor;
import csc445.groupc.distauction.Paxos.Learner;
import csc445.groupc.distauction.Paxos.Messages.Accept;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.PaxosMessage;
import csc445.groupc.distauction.Paxos.Proposer;

import java.util.ArrayList;
import java.util.List;
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

        /*onThread(() -> {
            try {MessageReceiving.run(group, receivePort, receiveQueueProposer);} catch (Exception e) {}
        });
        onThread(() -> {
            try {MessageSending.run(group, sendPort, receivePort, sendQueue);} catch (Exception e) {}
        });*/
        final List<LinkedBlockingQueue<Message>> allSendingQueues = new ArrayList<>();
        final List<LinkedBlockingQueue<Message>> allReceivingQueues = new ArrayList<>();

        for (int i = 0; i < numNodes; i++) {
            final int id = i;

            final LinkedBlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<Message> receivingQueue = new LinkedBlockingQueue<>();

            allSendingQueues.add(sendQueue);
            allReceivingQueues.add(receivingQueue);

            final LinkedBlockingQueue<Message> receiveQueueProposer = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<Message> receiveQueueAcceptor = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<Message> receiveQueueLearner = new LinkedBlockingQueue<>();

            final Proposer proposer = new Proposer(numNodes, id, receiveQueueProposer, sendQueue);
            final Acceptor acceptor = new Acceptor(receiveQueueAcceptor, sendQueue);
            final Learner learner = new Learner(numNodes, receiveQueueLearner, sendQueue);

            onThread(() -> {
                try {
                    proposer.run();
                } catch (Exception e) {
                }
            });
            onThread(() -> {
                try {
                    acceptor.run();
                } catch (Exception e) {
                }
            });
            onThread(() -> {
                try {
                    learner.run();
                } catch (Exception e) {
                }
            });
            onThread(() -> {
                try {
                    MessageForwarding.run(id, receivingQueue, receiveQueueProposer, receiveQueueAcceptor, receiveQueueLearner);} catch (Exception e) {}
            });
        }

        final MulticastSimulator multicastSimulator = new MulticastSimulator(allSendingQueues, allReceivingQueues);

        onThread(() -> {
            try {multicastSimulator.run();} catch (Exception e) {}
        });

        allSendingQueues.get(0).put(new Accept<Integer>(0, 100042, PaxosMessage.EVERYONE, PaxosMessage.PROPOSER));
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
