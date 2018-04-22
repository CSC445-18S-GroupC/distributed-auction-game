package csc445.groupc.distauction;

import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 4/13/18.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        final String group = "203.0.113.0";
        final int sendPort = 5323;
        final int receivePort = 5921;

        final LinkedBlockingQueue<Integer> sendQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Integer> receiveQueue = new LinkedBlockingQueue<>();

        onThread(() -> {
            try {
                MessageReceiving.run(group, receivePort, receiveQueue);} catch (Exception e) {}
        });
        onThread(() -> {
            try {MessageSending.run(group, sendPort, receivePort, sendQueue);} catch (Exception e) {}
        });

        final Scanner kb = new Scanner(System.in);
        while (true) {
            final int message = kb.nextInt();
            System.out.println("Sent:     " + message);

            sendQueue.put(message);

            final int receivedMessage = receiveQueue.take();
            System.out.println("Received: " + receivedMessage);
        }
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
