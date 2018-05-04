package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class UpdateRequestTest {
    @Test
    public void toFromBytesEveryone() throws IOException {
        final UpdateRequest initial = new UpdateRequest(25, Optional.empty(), PaxosMessage.PROPOSER);

        final byte[] encoded = initial.toByteArray();

        final UpdateRequest decoded = UpdateRequest.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytes() throws IOException {
        final UpdateRequest initial = new UpdateRequest(25, Optional.of(1), PaxosMessage.PROPOSER);

        final byte[] encoded = initial.toByteArray();

        final UpdateRequest decoded = UpdateRequest.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
}
