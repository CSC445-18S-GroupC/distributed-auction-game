/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 *
 * @author bolen
 */
public class Prepare extends PaxosMessage {
    private final int proposalID;
    
    public Prepare(final int proposalID, final Optional<Integer> receiver, final byte receiverRole, final int paxosRound){
        super(receiver, receiverRole, paxosRound);

        this.proposalID = proposalID;
    }
    
    public int getProposalID(){
        return this.proposalID;
    }

    public byte[] toByteArray() throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES * 4 + Byte.BYTES);

        byteBuffer.putInt(PREPARE_OP);
        byteBuffer.putInt(proposalID);

        if (receiver.isPresent()) {
            byteBuffer.putInt(receiver.get());
        } else {
            byteBuffer.putInt(EVERYONE_RECEIVES);
        }

        byteBuffer.put(receiverRole);
        byteBuffer.putInt(paxosRound);

        return byteBuffer.array();
    }

    public static Prepare fromByteArray(final byte[] bytes) throws IOException {
        final ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);

        byteBuffer.put(bytes);
        byteBuffer.rewind();

        byteBuffer.getInt();     // Move past OP code

        final int proposalId = byteBuffer.getInt();
        final int possibleReceiver = byteBuffer.getInt();
        final byte receiverRole = byteBuffer.get();
        final int paxosRound = byteBuffer.getInt();

        final Optional<Integer> receiver = (possibleReceiver != EVERYONE_RECEIVES) ? Optional.of(possibleReceiver) : Optional.empty();

        return new Prepare(proposalId, receiver, receiverRole, paxosRound);
    }

    @Override
    public String toString() {
        return "Prepare(" + "proposalId = " + proposalID + super.toString() + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Prepare) {
            final Prepare other = (Prepare) o;

            return this.proposalID == other.proposalID &&
                    this.receiver.equals(other.receiver) &&
                    this.receiverRole == other.receiverRole &&
                    this.paxosRound == other.paxosRound;
        }
        return false;
    }
}
