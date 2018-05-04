/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 *
 * @author bolen
 */
public abstract class PaxosMessage extends Message implements Serializable {
    public static final byte PROPOSER = 0;
    public static final byte ACCEPTOR = 1;
    public static final byte LEARNER = 2;

    public static final int NO_SPECIFIC_ROUND = -1;

    protected static final int PREPARE_OP = 0;
    protected static final int PROMISE_WITHOUT_OP = 1;
    protected static final int PROMISE_WITH_OP = 2;
    protected static final int ACCEPT_REQUEST_OP = 3;
    protected static final int ACCEPT_OP = 4;
    protected static final int UPDATE_REQUEST_OP = 5;
    protected static final int UPDATE_OP = 6;

    public static final Optional<Integer> EVERYONE = Optional.empty();

    protected static final int EVERYONE_RECEIVES = -1;

    protected final Optional<Integer> receiver;
    protected final byte receiverRole;
    protected final int paxosRound;

    public PaxosMessage(final Optional<Integer> receiver, final byte receiverRole, final int paxosRound) {
        this.receiver = receiver;
        this.receiverRole = receiverRole;
        this.paxosRound = paxosRound;
    }

    public Optional<Integer> getReceiver() {
        return receiver;
    }

    public byte getReceiverRole() {
        return receiverRole;
    }

    public int getPaxosRound() {
        return paxosRound;
    }

    private String getReceiverString() {
        if (receiver.isPresent()) {
            return receiver.get().toString();
        } else {
            return "Everyone";
        }
    }

    private String getReceiverRoleString() {
        if (receiverRole == PROPOSER) {
            return "Proposer";
        } else if (receiverRole == ACCEPTOR) {
            return "Acceptor";
        } else if (receiverRole == LEARNER) {
            return "Acceptor";
        } else {
            return "Invalid role";
        }
    }

    public abstract byte[] toByteArray() throws IOException, ClassNotFoundException;

    public static <B extends Serializable> PaxosMessage fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.rewind();

        final int opCode = buffer.getInt();

        if (opCode == PREPARE_OP) {
            return Prepare.fromByteArray(bytes);
        } else if (opCode == PROMISE_WITH_OP || opCode == PROMISE_WITHOUT_OP) {
            return Promise.fromByteArray(bytes);
        } else if (opCode == ACCEPT_REQUEST_OP) {
            return AcceptRequest.fromByteArray(bytes);
        } else if (opCode == ACCEPT_OP) {
            return Accept.fromByteArray(bytes);
        } else if (opCode == UPDATE_REQUEST_OP) {
            return UpdateRequest.fromByteArray(bytes);
        } else if (opCode == UPDATE_OP) {
            return Update.fromByteArray(bytes);
        }
        return null;    // This should not run unless the message bytes have an invalid op code
    }

    @Override
    public String toString() {
        return ", receiver = " + getReceiverString() + ", receiverRole = " + getReceiverRoleString() + ", paxosRound = " + paxosRound;
    }

    protected static <B extends Serializable> byte[] objectToBytes(final B object) throws IOException {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            final ObjectOutput out = new ObjectOutputStream(byteOut);
            out.writeObject(object);
            out.flush();

            return byteOut.toByteArray();
        } finally {
            byteOut.close();
        }
    }

    protected static <B extends Serializable> B objectFromBytes(final byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes);
        try {
            final ObjectInput in = new ObjectInputStream(byteIn);
            try {
                return  (B) in.readObject();
            } finally {
                in.close();
            }
        } finally {
            byteIn.close();
        }
    }
}
