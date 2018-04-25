package csc445.groupc.distauction;

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

        final LinkedBlockingQueue<Integer> sendQueue = new LinkedBlockingQueue<>();
        final LinkedBlockingQueue<Integer> receiveQueue = new LinkedBlockingQueue<>();

        onThread(() -> {
            try {MessageReceiving.run(group, receivePort, receiveQueue);} catch (Exception e) {}
        });
        onThread(() -> {
            try {MessageSending.run(group, sendPort, receivePort, sendQueue);} catch (Exception e) {}
        });

        final Scanner kb = new Scanner(System.in);
        while (true) {
            System.out.print("> ");

            final int message = kb.nextByte();
            System.out.println("Queued:   " + message);

            sendQueue.put(message);

            final int receivedMessage = receiveQueue.take();
            System.out.println("Pulled:   " + receivedMessage);
        }
    }

    private static void onThread(final Runnable runnable) {
        new Thread(runnable).start();
    }
}
