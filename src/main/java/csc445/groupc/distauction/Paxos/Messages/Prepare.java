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
public class Prepare extends PaxosMessage {
    
    private static final byte PREPARE_OPCODE = 0;
    byte receiver;
    private final int proposalID;
    
    public Prepare(final int proposalID, final Optional<Integer> receiver, final byte receiverRole, final int paxosRound){
        super(receiver, receiverRole, paxosRound);

        this.proposalID = proposalID;
    }
    
    public int getProposalID(){
        return this.proposalID;
    }
    
    public byte[] toByteArray(){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            out.close();
            bos.close();
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }
        return bos.toByteArray();
    }
    
    public static Prepare fromByteArray(byte[] array){
        Prepare prepare = null;
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(array);
            ObjectInputStream in = new ObjectInputStream(bis);
            prepare = (Prepare)in.readObject();
            in.close();
            bis.close();
        }catch(IOException | ClassNotFoundException ex){
            System.out.println(ex.toString());
        }
        return prepare;
    }

    @Override
    public String toString() {
        return "Prepare(" + "proposalId = " + proposalID + super.toString() + ")";
    }
}
