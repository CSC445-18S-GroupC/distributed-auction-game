package csc445.groupc.distauction.Paxos.Messages;

import java.util.Optional;

/**
 * Created by chris on 5/2/18.
 */
public class UpdateRequest extends PaxosMessage {
    private final int entryId;

    public UpdateRequest(final int entryId, final Optional<Integer> receiver, final byte receiverRole) {
        super(receiver, receiverRole, NO_SPECIFIC_ROUND);
        this.entryId = entryId;
    }

    public int getEntryId() {
        return entryId;
    }

    @Override
    public String toString() {
        return "UpdateRequest(entryId = " + entryId + super.toString() + ")";
    }
}
