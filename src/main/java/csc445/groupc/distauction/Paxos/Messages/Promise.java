/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Optional;

/**
 *
 * @author bolen
 */
public class Promise<A extends Serializable> extends PaxosMessage { 
    
    private static final byte PROMISE_NONACCEPT_OPCODE = 1;
    private static final byte PROMISE_ACCEPT_OPCODE = 2;
    byte receiver;
    private final Optional<Integer> acceptedID;
    private final Optional<A> acceptedValue;
    private final int proposalID;
    
    public Promise(final int proposalID, final Optional<Integer> receiver){
        super(receiver);

        this.proposalID = proposalID;
        this.acceptedID = Optional.empty();
        this.acceptedValue = Optional.empty();
    }
    
    public Promise(final int proposalID, final Integer acceptedID, final A acceptedValue, final Optional<Integer> receiver){
        super(receiver);

        this.proposalID = proposalID;
        this.acceptedID = Optional.of(acceptedID);
        this.acceptedValue = Optional.of(acceptedValue);
    }

    public boolean hasAcceptedValue() {
        return acceptedID.isPresent();
    }
    
    //don't guess size of value
    public byte[] toByteArray(){
        int pID = this.proposalID;
        ByteBuffer buf = ByteBuffer.allocate(8 + MAX_VALUE_SIZE + 1);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        byte[] valueBytes = new byte[MAX_VALUE_SIZE];
        if (this.acceptedID.isPresent()) {
            int aID = this.acceptedID.get();
            A value = this.acceptedValue.get();
            buf.put(Promise.PROMISE_ACCEPT_OPCODE);
            buf.putInt(pID);
            buf.putInt(aID);
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(value);
                out.flush();
                out.close();
                bos.close();
                valueBytes = bos.toByteArray();
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
            buf.put(valueBytes);
            return buf.array();
        }else{
            buf.put(Promise.PROMISE_NONACCEPT_OPCODE);
            buf.putInt(pID);
            return buf.array();
        }
    }
    
    public static <A extends Serializable> Promise fromByteArray(byte[] array) {
        Promise promise = null;  
        ByteBuffer buf = ByteBuffer.wrap(array);
        int pID = buf.getInt(1);
        if (array[0] == PROMISE_ACCEPT_OPCODE) {
            byte[] valueBytes = new byte[MAX_VALUE_SIZE];  
            buf.get(valueBytes, 9, array.length);
            ByteArrayInputStream bis = new ByteArrayInputStream(valueBytes);
            int aID = buf.getInt(5);
            //deserialize value from stream
            A value = null;
            try {
                ObjectInput in = new ObjectInputStream(bis);
                value = (A) in.readObject();
            }catch(IOException | ClassNotFoundException ex){
                System.out.println(ex.toString());
            }
            promise = new Promise(pID, aID, value, EVERYONE);
            return promise;
        }else{
            promise = new Promise(pID, EVERYONE);
            return promise;
        }
    }

    @Override
    public String toString() {
        if (hasAcceptedValue()) {
            return "Promise(" + "proposalId = " + proposalID + ", acceptedId = " + acceptedID.get() +
                    ", acceptedValue = " + acceptedValue.get() + ")";
        } else {
            return "Promise(" + "proposalId = " + proposalID + ")";
        }
    }
}
