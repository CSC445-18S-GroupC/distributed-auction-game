package csc445.groupc.distauction;

import csc445.groupc.distauction.Communication.MulticastSimulator;
import csc445.groupc.distauction.GameLogic.Bid;
import csc445.groupc.distauction.GameLogic.GameState;
import csc445.groupc.distauction.GameLogic.GameStep;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Paxos;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
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
        final List<Paxos<GameStep>> paxosHandlers = new ArrayList<>();

        final String[] players = new String[]{"Hi", "Sally", "Jane"};
        for (int i = 0; i < numNodes; i++) {
            final int id = i;
            final GameState gameState = new GameState(LocalDateTime.now(), players, (gs) -> {
                System.out.println(gs);
            });

            final Paxos<GameStep> paxos = new Paxos<>(id, numNodes, gameState::applyStep);
            paxosHandlers.add(paxos);

            paxos.run();

            allSendingQueues.add(paxos.getSendingQueue());
            allReceivingQueues.add(paxos.getReceivingQueue());
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

            //allReceivingQueues.get(i).put(new ProposalRequest(new Bid(players[i], 10.05f)));
            paxosHandlers.get(i).proposeStep(new Bid(players[i], 10.05f));
            System.out.println("Sent proposal request");
        }
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
