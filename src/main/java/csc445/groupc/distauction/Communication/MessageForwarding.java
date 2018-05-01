package csc445.groupc.distauction.Communication;

import csc445.groupc.distauction.Paxos.Messages.Message;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 5/1/18.
 */
public abstract class MessageForwarding {
    public static void run(final LinkedBlockingQueue<Message> receivingQueue, final LinkedBlockingQueue<Message> receiveQueueProposer,
                           final LinkedBlockingQueue<Message> receiveQueueAcceptor, final LinkedBlockingQueue<Message> receiveQueueLearner) throws InterruptedException {
        for (;;) {
            final Message message = receivingQueue.take();

            System.out.println(message);

            // TODO: Forward message to corresponding queue
        }
    }
}
