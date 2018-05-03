/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc445.groupc.distauction.Paxos.Messages;

/**
 *
 * @author bolen
 */
public class ProposalRequest<A> extends Message {
    final A value;

    public ProposalRequest(final A value) {
        this.value = value;
    }

    public A getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ProposalRequest(" + value + ")";
    }
}
