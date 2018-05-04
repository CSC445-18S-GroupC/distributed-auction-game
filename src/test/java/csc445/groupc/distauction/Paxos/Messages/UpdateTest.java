package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class UpdateTest {
    @Test
    public void toFromBytesEveryone() throws IOException, ClassNotFoundException {
        final Update<Integer> initial = new Update<>(25, 9, 2, Optional.empty(), PaxosMessage.PROPOSER);

        final byte[] encoded = initial.toByteArray();

        final Update<Integer> decoded = Update.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytes() throws IOException, ClassNotFoundException {
        final Update<Integer> initial = new Update<>(25, 9, 2, Optional.of(1), PaxosMessage.PROPOSER);

        final byte[] encoded = initial.toByteArray();

        final Update<Integer> decoded = Update.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
}
