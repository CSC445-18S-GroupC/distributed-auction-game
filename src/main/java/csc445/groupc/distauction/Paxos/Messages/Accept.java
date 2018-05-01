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
public class Accept<A> extends PaxosMessage {
    
    private static final byte ACCEPT_OPCODE = 4;
    byte receiver;
    private final int proposalID;
    private final A proposalValue;
    
    public Accept(final int proposalID, final A proposalValue, final Optional<Integer> receiver){
        super(receiver);

        this.proposalID = proposalID;
        this.proposalValue = proposalValue;
    }
    
    public void setReceiver(byte rec){
        this.receiver = rec;
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
            out.writeByte(Accept.ACCEPT_OPCODE);//write this.opcode, then id, then value
            out.writeByte(this.receiver);
            out.writeInt(this.proposalID);
            out.writeObject(this.proposalValue);
            //out.writeObject(Accept.ACCEPT_OPCODE);
            out.flush();
            out.close();
            bos.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return bos.toByteArray();
    }
    
    public static Accept fromByteArray(byte[] array){
        Accept accept = null;
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(array);
            ObjectInputStream in = new ObjectInputStream(bis);
            accept.receiver = array[1];
            
            accept = (Accept)in.readObject();
            in.close();
            bis.close();
        }catch(IOException | ClassNotFoundException ex){
            System.out.println(ex.toString());
        }
        return accept;
    }

    @Override
    public String toString() {
        return "Accept(" + "proposalId = " + proposalID + ", proposalValue = " + proposalValue + ")";
    }
}
