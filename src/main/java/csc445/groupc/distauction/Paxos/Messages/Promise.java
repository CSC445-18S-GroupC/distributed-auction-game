/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author bolen
 */
public class Promise<A extends Serializable> extends PaxosMessage {
    private static final byte PROMISE_NONACCEPT_OPCODE = 1;
    private static final byte PROMISE_ACCEPT_OPCODE = 2;

    private final Optional<Integer> acceptedID;
    private final Optional<A> acceptedValue;
    private final int proposalID;
    
    public Promise(final int proposalID, final Optional<Integer> receiver, final byte receiverRole, final int paxosRound){
        super(receiver, receiverRole, paxosRound);

        this.proposalID = proposalID;
        this.acceptedID = Optional.empty();
        this.acceptedValue = Optional.empty();
    }
    
    public Promise(final int proposalID, final Integer acceptedID, final A acceptedValue, final Optional<Integer> receiver, final byte receiverRole, final int paxosRound){
        super(receiver, receiverRole, paxosRound);

        this.proposalID = proposalID;
        this.acceptedID = Optional.of(acceptedID);
        this.acceptedValue = Optional.of(acceptedValue);
    }

    public boolean hasAcceptedValue() {
        return acceptedID.isPresent();
    }

    public int getAcceptedID() {
        return acceptedID.get();
    }

    public A getAcceptedValue() {
        return acceptedValue.get();
    }

    public int getProposalID() {
        return proposalID;
    }

    public byte[] toByteArray() throws IOException {
        final int numBytes;
        final byte[] valueBytes;

        final boolean hasAccepted = hasAcceptedValue();

        if (hasAccepted) {
            valueBytes = objectToBytes(acceptedValue.get());

            numBytes = Integer.BYTES * 6 + valueBytes.length;
        } else {
            valueBytes = new byte[0];

            numBytes = Integer.BYTES * 5;
        }

        final ByteBuffer byteBuffer = ByteBuffer.allocate(numBytes);

        if (hasAccepted) {
            byteBuffer.putInt(PROMISE_ACCEPT_OPCODE);
        } else {
            byteBuffer.putInt(PROMISE_NONACCEPT_OPCODE);
        }

        byteBuffer.putInt(proposalID);

        if (receiver.isPresent()) {
            byteBuffer.putInt(receiver.get());
        } else {
            byteBuffer.putInt(EVERYONE_RECEIVES);
        }

        byteBuffer.put(receiverRole);
        byteBuffer.putInt(paxosRound);

        if (hasAccepted) {
            byteBuffer.putInt(acceptedID.get());
            byteBuffer.put(valueBytes);
        }

        return byteBuffer.array();
    }

    public static <B extends Serializable> Promise<B> fromByteArray(final byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);

        byteBuffer.put(bytes);
        byteBuffer.rewind();

        final boolean hasAccepted;
        if (byteBuffer.getInt() == PROMISE_ACCEPT_OPCODE) {
            hasAccepted = true;
        } else {
            hasAccepted = false;
        }

        final int proposalId = byteBuffer.getInt();

        final int possibleReceiver = byteBuffer.getInt();
        final byte receiverRole = byteBuffer.get();
        final int paxosRound = byteBuffer.getInt();

        final Optional<Integer> receiver = (possibleReceiver != EVERYONE_RECEIVES) ? Optional.of(possibleReceiver) : Optional.empty();

        if (hasAccepted) {
            final int acceptedId = byteBuffer.getInt();

            final byte[] encodedValue = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.array().length);
            final B acceptedValue = objectFromBytes(encodedValue);

            return new Promise<>(proposalId, acceptedId, acceptedValue, receiver, receiverRole, paxosRound);
        } else {
            return new Promise<>(proposalId, receiver, receiverRole, paxosRound);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Promise) {
            final Promise<A> other = (Promise<A>) o;

            return this.proposalID == other.proposalID &&
                    this.acceptedID.equals(other.acceptedID) &&
                    this.acceptedValue.equals(other.acceptedValue) &&
                    this.receiver.equals(other.receiver) &&
                    this.receiverRole == other.receiverRole &&
                    this.paxosRound == other.paxosRound;
        }
        return false;
    }

    @Override
    public String toString() {
        if (hasAcceptedValue()) {
            return "Promise(" + "proposalId = " + proposalID + ", acceptedId = " + acceptedID.get() +
                    ", acceptedValue = " + acceptedValue.get() + super.toString() + ")";
        } else {
            return "Promise(" + "proposalId = " + proposalID + super.toString() + ")";
        }
    }
}
