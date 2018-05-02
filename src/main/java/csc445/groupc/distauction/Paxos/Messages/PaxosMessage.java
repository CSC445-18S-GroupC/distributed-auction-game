/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Optional;

/**
 *
 * @author bolen
 */
public abstract class PaxosMessage extends Message implements Serializable {
    // from byte array method because you dont know what message it is
    // read opcode then call correct static method
//    public static PaxosMessage fromByteArray(byte[] array){
//        
//        return null;
//    }
    
    
    // every subclass has another field in header
    //
    
    public static final byte PROPOSER = 0;
    public static final byte ACCEPTOR = 1;
    public static final byte LEARNER = 2;

    public static final Optional<Integer> EVERYONE = Optional.empty();

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

    public static <A extends Serializable> PaxosMessage fromByteArray(byte[] array) throws IOException, ClassNotFoundException{
        byte opcode = 3;
        ByteArrayInputStream bis = new ByteArrayInputStream(array);
        //ObjectInputStream in = new ObjectInputStream(bis);
        //opcode = array[17];
        
        System.out.println(opcode);
        //in.close();
        bis.close();
        
        System.out.println();
        switch(opcode){
            case 0:
                return Prepare.fromByteArray(array);
            case 1:
                return Promise.fromByteArray(array);
            case 2:
                return Promise.fromByteArray(array);
            case 3:
                return AcceptRequest.fromByteArray(array);
            case 4:
                return Accept.fromByteArray(array);
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return ", receiver = " + getReceiverString() + ", receiverRole = " + getReceiverRoleString() + ", paxosRound = " + paxosRound;
    }
}
