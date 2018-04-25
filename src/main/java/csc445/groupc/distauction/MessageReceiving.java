package csc445.groupc.distauction;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by chris on 4/22/18.
 */
public abstract class MessageReceiving {
    /**
     * The maximum size of UDP message that the run method will receive.
     */
    private static final int MAX_MESSAGE_SIZE = 512;

    /**
     * Starts receiving PaxosMessages from the given multicast group and port
     * and places those messages in the given message queue.
     *
     * @param group The multicast group to join.
     * @param port The port to listen for message on.
     * @param queue The message queue to place the incoming messages on.
     * @throws IOException If the port is already being used.
     * @throws InterruptedException If the program is interrupted while a
     * message is waiting to be placed on the message queue.
     */
    public static void run(final String group, final int port, final LinkedBlockingQueue<Integer> queue) throws IOException, InterruptedException {
        final InetAddress groupAddress = InetAddress.getByName(group);

        final MulticastSocket socket = new MulticastSocket(port);
        try {
            socket.joinGroup(groupAddress);

            while (true) {
                handlePacket(socket, queue);
            }
        } finally {
            socket.leaveGroup(groupAddress);
            socket.close();
        }
    }

    /**
     * Handles the next incoming UDP packet and places it into the queue as
     * necessary.
     *
     * @param socket The multicast socket to listen for incoming packets on.
     * @param queue The message queue to place the incoming messages on.
     * @throws IOException If weird circumstances occur with the message
     * receiving. See the DatagramSocket class' receive method for more
     * information.
     * @throws InterruptedException If the program is interrupted while it is
     * waiting to place the message on the message queue.
     */
    private static void handlePacket(final MulticastSocket socket, final LinkedBlockingQueue<Integer> queue) throws IOException, InterruptedException {
        final byte[] buffer = new byte[MAX_MESSAGE_SIZE];
        final DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

        // TODO: Do the packet -> message conversion here
        final Integer message = Byte.toUnsignedInt(packet.getData()[0]);

        queue.put(message);
    }
}
