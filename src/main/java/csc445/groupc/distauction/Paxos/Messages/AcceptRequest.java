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
import java.util.Optional;

/**
 *
 * @author bolen
 */
public class AcceptRequest<A> extends PaxosMessage {
    
    private static final byte ACCEPTREQUEST_OPCODE = 3;
    byte receiver;
    private final int proposalID;
    private final A proposalValue;
    
    public AcceptRequest(final int proposalID, final A proposalValue, final Optional<Integer> receiver, final byte receiverRole){
        super(receiver, receiverRole);

        this.proposalID = proposalID;
        this.proposalValue = proposalValue;
    }
    
    public int getProposalID(){
        return this.proposalID;
    }
    
    public A getProposalValue(){
        return this.proposalValue;
    }
    
    public byte[] toByteArray(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            //out.writeByte(3);
            out.writeObject(this);
            out.flush();
            out.close();
            bos.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return bos.toByteArray();
    }
    
    public static AcceptRequest fromByteArray(byte[] array){
        AcceptRequest acceptRequest = null;
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(array);
            ObjectInputStream in = new ObjectInputStream(bis);
            acceptRequest = (AcceptRequest)in.readObject();
            in.close();
            bis.close();
        }catch(IOException | ClassNotFoundException ex){
            System.out.println(ex.toString());
        }
        return acceptRequest;
    }

    @Override
    public String toString() {
        return "AcceptRequest(" + "proposalId = " + proposalID + ", proposalValue = " + proposalValue + super.toString() + ")";
    }
}
