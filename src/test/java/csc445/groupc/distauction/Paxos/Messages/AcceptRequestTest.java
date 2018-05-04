package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class AcceptRequestTest {
    @Test
    public void toFromBytesEveryone() throws IOException, ClassNotFoundException {
        final AcceptRequest<Integer> initial = new AcceptRequest<>(25, 9, Optional.empty(), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final AcceptRequest<Integer> decoded = AcceptRequest.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytes() throws IOException, ClassNotFoundException {
        final AcceptRequest<Integer> initial = new AcceptRequest<>(25, 9, Optional.of(1), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final AcceptRequest<Integer> decoded = AcceptRequest.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
}
