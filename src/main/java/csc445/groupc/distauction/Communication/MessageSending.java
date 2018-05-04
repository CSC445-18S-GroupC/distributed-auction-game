package csc445.groupc.distauction.Communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 4/22/18.
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
    public static void run(final String sendGroup, final int sendPort, final int receivePort, final LinkedBlockingQueue<Integer> queue) throws IOException, InterruptedException {
        final InetAddress sendGroupAddress = InetAddress.getByName(sendGroup);

        final MulticastSocket socket = new MulticastSocket(sendPort);
        try {
            socket.joinGroup(sendGroupAddress);

            while (true) {
                // TODO: Change to use real Messages
                final Integer message = queue.take();

                sendMessage(socket, sendGroupAddress, receivePort, message);
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
    private static void sendMessage(final MulticastSocket socket, final InetAddress address, final int receivePort, final Integer message) throws IOException {
        // TODO: Change to a real message -> packet conversion
        final byte[] messageBuffer = new byte[]{ (byte) message.intValue() };

        final DatagramPacket packet = new DatagramPacket(messageBuffer, messageBuffer.length);
        packet.setAddress(address);
        packet.setPort(receivePort);

        socket.send(packet);
    }
}
