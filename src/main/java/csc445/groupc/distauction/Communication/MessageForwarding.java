package csc445.groupc.distauction.Communication;

import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.PaxosMessage;
import csc445.groupc.distauction.Paxos.Messages.ProposalRequest;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 5/1/18.
 */
public abstract class MessageForwarding {
    public static void run(final int nodeId, final LinkedBlockingQueue<Message> receivingQueue, final LinkedBlockingQueue<Message> receiveQueueProposer,
                           final LinkedBlockingQueue<Message> receiveQueueAcceptor, final LinkedBlockingQueue<Message> receiveQueueLearner) throws InterruptedException {
        for (;;) {
            final Message message = receivingQueue.take();

            if (message instanceof ProposalRequest) {
                receiveQueueProposer.put(message);
            } else if (message instanceof PaxosMessage) {
                final PaxosMessage paxosMessage = (PaxosMessage) message;

                if (!paxosMessage.getReceiver().isPresent() ||
                        paxosMessage.getReceiver().get() == nodeId) {
                    forwardToRespectiveQueue(paxosMessage, receiveQueueProposer, receiveQueueAcceptor, receiveQueueLearner);
                }
            }
        }
    }

    private static void forwardToRespectiveQueue(final PaxosMessage message, final LinkedBlockingQueue<Message> receiveQueueProposer,
                                                 final LinkedBlockingQueue<Message> receiveQueueAcceptor, final LinkedBlockingQueue<Message> receiveQueueLearner) throws InterruptedException {
        final byte receiverRole = message.getReceiverRole();
        if (receiverRole == PaxosMessage.PROPOSER) {
            System.out.println("Sent to Proposer: " + message);
            receiveQueueProposer.put(message);
        } else if (receiverRole == PaxosMessage.ACCEPTOR) {
            System.out.println("Sent to Acceptor: " + message);
            receiveQueueAcceptor.put(message);
        } else if (receiverRole == PaxosMessage.LEARNER) {
            System.out.println("Sent to Learner: " + message);
            receiveQueueLearner.put(message);
        }
    }
}
