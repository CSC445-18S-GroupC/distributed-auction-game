package csc445.groupc.distauction.Paxos.Messages;

import java.io.Serializable;
import java.util.Optional;

/**
 * Created by chris on 5/2/18.
 */
public class Update<A extends Serializable> extends PaxosMessage {
    private final int entryId;
    private final A value;

    public Update(final int entryId, final A value, final Optional<Integer> receiver, final byte receiverRole) {
        super(receiver, receiverRole, NO_SPECIFIC_ROUND);
        this.entryId = entryId;
        this.value = value;
    }

    public int getEntryId() {
        return entryId;
    }

    public A getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Update(entryId = " + entryId + ", value = " + value + super.toString() + ")";
    }
}
