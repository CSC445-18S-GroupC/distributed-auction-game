package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class PromiseTest {
    @Test
    public void toFromBytesEveryoneNoAccepted() throws IOException, ClassNotFoundException {
        final Promise<Integer> initial = new Promise<>(23, Optional.empty(), PaxosMessage.PROPOSER, 5);

        final byte[] encoded = initial.toByteArray();

        final Promise<Integer> decoded = Promise.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytesNoAccepted() throws IOException, ClassNotFoundException {
        final Promise<Integer> initial = new Promise<>(23, Optional.of(1), PaxosMessage.PROPOSER, 5);

        final byte[] encoded = initial.toByteArray();

        final Promise<Integer> decoded = Promise.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
    @Test
    public void toFromBytesEveryoneWithAccepted() throws IOException, ClassNotFoundException {
        final Promise<Integer> initial = new Promise<>(23, 21, 9, Optional.empty(), PaxosMessage.PROPOSER, 5);

        final byte[] encoded = initial.toByteArray();

        final Promise<Integer> decoded = Promise.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytesWithAccepted() throws IOException, ClassNotFoundException {
        final Promise<Integer> initial = new Promise<>(23, 21, 9, Optional.of(1), PaxosMessage.PROPOSER, 5);

        final byte[] encoded = initial.toByteArray();

        final Promise<Integer> decoded = Promise.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
}
