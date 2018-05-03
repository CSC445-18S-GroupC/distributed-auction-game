/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

import csc445.groupc.distauction.GameLogic.GameStep;

/**
 *
 * @author bolen
 */
public class ProposalRequest extends Message {
    final GameStep value;

    public ProposalRequest(final GameStep value) {
        this.value = value;
    }

    public GameStep getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ProposalRequest(" + value + ")";
    }
}
