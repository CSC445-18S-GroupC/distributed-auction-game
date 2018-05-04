package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class AcceptTest {
    @Test
    public void toFromBytesEveryone() throws IOException, ClassNotFoundException {
        final Accept<Integer> initial = new Accept<>(25, 9, Optional.empty(), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final Accept<Integer> decoded = Accept.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytes() throws IOException, ClassNotFoundException {
        final Accept<Integer> initial = new Accept<>(25, 9, Optional.of(1), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final Accept<Integer> decoded = Accept.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
}
