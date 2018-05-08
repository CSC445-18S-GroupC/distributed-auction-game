package csc445.groupc.distauction.Communication;

import csc445.groupc.distauction.Paxos.Messages.Message;
import csc445.groupc.distauction.Paxos.Messages.PaxosMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * MessageSending is used for sending messages through multicast as they are placed into a concurrent queue.
 */
public abstract class MessageSending {
    /**
     * Starts sending PaxosMessage as they are queued to be sent.
     *
     * @param sendGroup The multicast group to send messages to.
     * @param sendPort The port to send messages out on.
     * @param receivePort The port to send messages to.
     * @param queue The queue to listen on for messages to send.
     * @throws IOException If the port is already being used.
     * @throws InterruptedException If the program is interrupted while it is
     * waiting for a message.
     */
    public static void run(final String sendGroup, final int sendPort, final int receivePort, final LinkedBlockingQueue<Message> queue) throws IOException, InterruptedException {
        final InetAddress sendGroupAddress = InetAddress.getByName(sendGroup);

        final MulticastSocket socket = new MulticastSocket(sendPort);
        try {
            socket.joinGroup(sendGroupAddress);

            while (true) {
                final Message message = queue.take();

                if (message instanceof PaxosMessage) {
                    sendMessage(socket, sendGroupAddress, receivePort, (PaxosMessage) message);
                }
            }
        } finally {
            socket.leaveGroup(sendGroupAddress);
            socket.close();
        }
    }

    /**
     * Sends the given message on the given port.
     *
     * @param socket The multicast socket to send the message out on.
     * @param address The multicast address to send the message to.
     * @param receivePort The port to send the message to.
     * @param message The message to send.
     * @throws IOException If there is an issue while sending the message.
     */
    private static void sendMessage(final MulticastSocket socket, final InetAddress address, final int receivePort, final PaxosMessage message) throws IOException {
        final byte[] messageBuffer = message.toByteArray();

        final DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length);
        packet.setAddress(address);
        packet.setPort(receivePort);

        socket.send(packet);
    }
}
