package csc445.groupc.distauction.Communication;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 5/1/18.
 */
public abstract class MessageForwarding {
    public static void run(final LinkedBlockingQueue<Integer> receivingQueue, final LinkedBlockingQueue<Integer> receiveQueueProposer,
                           final LinkedBlockingQueue<Integer> receiveQueueAcceptor, final LinkedBlockingQueue<Integer> receiveQueueLearner) throws InterruptedException {
        for (;;) {
            final Integer message = receivingQueue.take();

            System.out.println(message);

            // TODO: Forward message to corresponding queue
        }
    }
}
