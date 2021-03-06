/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

/**
 *
 * @author bolen
 */
public class AcceptRequest<A extends Serializable> extends PaxosMessage {
    private final int proposalID;
    private final A proposalValue;
    
    public AcceptRequest(final int proposalID, final A proposalValue, final Optional<Integer> receiver, final byte receiverRole, final int paxosRound){
        super(receiver, receiverRole, paxosRound);

        this.proposalID = proposalID;
        this.proposalValue = proposalValue;
    }
    
    public int getProposalID(){
        return this.proposalID;
    }
    
    public A getProposalValue(){
        return this.proposalValue;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        final byte[] valueBytes = objectToBytes(proposalValue);

        final ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES * 4 + Byte.BYTES + valueBytes.length);

        byteBuffer.putInt(ACCEPT_REQUEST_OP);
        byteBuffer.putInt(proposalID);

        if (receiver.isPresent()) {
            byteBuffer.putInt(receiver.get());
        } else {
            byteBuffer.putInt(EVERYONE_RECEIVES);
        }

        byteBuffer.put(receiverRole);
        byteBuffer.putInt(paxosRound);

        byteBuffer.put(valueBytes);

        return byteBuffer.array();
    }

    public static <B extends Serializable> AcceptRequest<B> fromByteArray(final byte[] bytes) throws IOException, ClassNotFoundException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);

        byteBuffer.put(bytes);
        byteBuffer.rewind();

        byteBuffer.getInt();     // Move past OP code

        final int proposalId = byteBuffer.getInt();
        final int possibleReceiver = byteBuffer.getInt();
        final byte receiverRole = byteBuffer.get();
        final int paxosRound = byteBuffer.getInt();

        final byte[] encodedValue = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.array().length);
        final B proposalValue = objectFromBytes(encodedValue);

        final Optional<Integer> receiver = (possibleReceiver != EVERYONE_RECEIVES) ? Optional.of(possibleReceiver) : Optional.empty();

        return new AcceptRequest<B>(proposalId, proposalValue, receiver, receiverRole, paxosRound);
    }

    @Override
    public String toString() {
        return "AcceptRequest(" + "proposalId = " + proposalID + ", proposalValue = " + proposalValue + super.toString() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AcceptRequest<?> that = (AcceptRequest<?>) o;

        if (proposalID != that.proposalID) return false;
        return proposalValue.equals(that.proposalValue);
    }

    @Override
    public int hashCode() {
        int result = proposalID;
        result = 31 * result + proposalValue.hashCode();
        return result;
    }
}
