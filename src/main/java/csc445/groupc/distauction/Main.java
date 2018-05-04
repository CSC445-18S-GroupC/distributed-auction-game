package csc445.groupc.distauction;

import csc445.groupc.distauction.Communication.MessageReceiving;
import csc445.groupc.distauction.Communication.MessageSending;
import csc445.groupc.distauction.Communication.MulticastSimulator;
import csc445.groupc.distauction.GameLogic.Bid;
import csc445.groupc.distauction.GameLogic.GameState;
import csc445.groupc.distauction.GameLogic.GameStep;
import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Paxos;
import csc445.groupc.distauction.View.LoginView;

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
//        /*final String group = "230.1.1.1";
//        final int sendPort = 5323;
//        final int receivePort = 5921;
//
//        final int numNodes = 3;
//
//        /*onThread(() -> {
//            try {MessageReceiving.run(group, receivePort, receiveQueueProposer);} catch (Exception e) {}
//        });
//        onThread(() -> {
//            try {MessageSending.run(group, sendPort, receivePort, sendQueue);} catch (Exception e) {}
//        });*/
//        //final List<LinkedBlockingQueue<Message>> allSendingQueues = new ArrayList<>();
//        //final List<LinkedBlockingQueue<Message>> allReceivingQueues = new ArrayList<>();
//        final List<Paxos<GameStep>> paxosHandlers = new ArrayList<>();
//
//        final String[] players = new String[]{"Hi", "Sally", "Jane"};
//        //for (int i = 0; i < numNodes; i++) {
//            final int id = Integer.parseInt(args[0]);
//            final GameState gameState = new GameState(LocalDateTime.now(), players, (gs) -> {
//                System.out.println(gs);
//            });
//
//            final Paxos<GameStep> paxos = new Paxos<>(id, numNodes, gameState::applyStep);
//            paxosHandlers.add(paxos);
//
//            paxos.run();
//
//            //allSendingQueues.add(paxos.getSendingQueue());
//            //allReceivingQueues.add(paxos.getReceivingQueue());
//        //}
//
//        //final MulticastSimulator multicastSimulator = new MulticastSimulator(allSendingQueues, allReceivingQueues);
//
//        //onThread(() -> {
//        //    try {multicastSimulator.run();} catch (Exception e) {}
//        //});
//
//        onThread(() -> {
//            try {
//                MessageReceiving.run(group, receivePort, paxos.getReceivingQueue());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        onThread(() -> {
//            try {
//                MessageSending.run(group, sendPort, receivePort, paxos.getSendingQueue());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//
//        final Scanner kb = new Scanner(System.in);
//        //final Random rand = new Random();
//        while (true) {
//            kb.nextLine();
//
//            //final int i = rand.nextInt(players.length);
//
//            //allReceivingQueues.get(i).put(new ProposalRequest(new Bid(players[i], 10.05f)));
//            paxos.proposeStep(gameState.generateRandomBid(players[id]));
//            //paxosHandlers.get(id).proposeStep(gameState.generateRandomBid(players[id]));
//            //System.out.println("Sent proposal request");
//        }
        LoginView.main();

    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
