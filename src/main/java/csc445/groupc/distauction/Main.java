package csc445.groupc.distauction;

import csc445.groupc.distauction.Communication.MessageForwarding;
import csc445.groupc.distauction.Communication.MulticastSimulator;
import csc445.groupc.distauction.GameLogic.Bid;
import csc445.groupc.distauction.GameLogic.GameState2;
import csc445.groupc.distauction.Paxos.Acceptor;
import csc445.groupc.distauction.Paxos.Learner;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.ProposalRequest;
import csc445.groupc.distauction.Paxos.Proposer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

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

        final String[] players = new String[]{"Hi", "Sally", "Jane"};
        for (int i = 0; i < numNodes; i++) {
            final int id = i;
            final AtomicInteger largestKnownRound = new AtomicInteger(0);
            final GameState2 gameState = new GameState2(LocalDateTime.now(), players, (gs) -> {
                System.out.println(gs);
            });

            final LinkedBlockingQueue<Message> sendQueue = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<Message> receivingQueue = new LinkedBlockingQueue<>();

            allSendingQueues.add(sendQueue);
            allReceivingQueues.add(receivingQueue);

            final LinkedBlockingQueue<Message> receiveQueueProposer = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<Message> receiveQueueAcceptor = new LinkedBlockingQueue<>();
            final LinkedBlockingQueue<Message> receiveQueueLearner = new LinkedBlockingQueue<>();

            final Proposer proposer = new Proposer(numNodes, id, receiveQueueProposer, sendQueue, largestKnownRound);
            final Acceptor acceptor = new Acceptor(numNodes, id, receiveQueueAcceptor, sendQueue, largestKnownRound);
            final Learner learner = new Learner(numNodes, id, receiveQueueLearner, sendQueue, proposer, acceptor, largestKnownRound, gameState);

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
                    MessageForwarding.run(id, receivingQueue, receiveQueueProposer, receiveQueueAcceptor, receiveQueueLearner);} catch (Exception e) {}
            });
        }

        final MulticastSimulator multicastSimulator = new MulticastSimulator(allSendingQueues, allReceivingQueues);

        onThread(() -> {
            try {multicastSimulator.run();} catch (Exception e) {}
        });

        final Scanner kb = new Scanner(System.in);
        final Random rand = new Random();
        while (true) {
            kb.nextLine();

            final int i = rand.nextInt(players.length);

            allReceivingQueues.get(i).put(new ProposalRequest(new Bid(players[i], 10.05f)));
            System.out.println("Sent proposal request");
        }
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
