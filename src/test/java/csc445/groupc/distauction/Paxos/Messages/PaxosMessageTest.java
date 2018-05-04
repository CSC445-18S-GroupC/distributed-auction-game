package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by chris on 4/17/18.
 */
public class PaxosMessageTest {
    @Test
    public void objectToFromBytes() throws IOException, ClassNotFoundException {
        final Integer initial = 5;

        final byte[] encoded = PaxosMessage.objectToBytes(initial);

        final Integer decoded = PaxosMessage.objectFromBytes(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void acceptToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new Accept<>(25, 9, Optional.of(1), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof Accept);
    }

    @Test
    public void acceptRequestToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new AcceptRequest<>(25, 9, Optional.of(1), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof AcceptRequest);
    }

    @Test
    public void prepareToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new Prepare(25, Optional.of(1), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof Prepare);
    }

    @Test
    public void promiseWithToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new Promise<>(23, 21, 9, Optional.of(1), PaxosMessage.PROPOSER, 5);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof Promise);
    }

    @Test
    public void promiseWithoutToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new Promise<>(23, Optional.of(1), PaxosMessage.PROPOSER, 5);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof Promise);
    }

    @Test
    public void updateToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new Update<>(25, 9, 2, Optional.of(1), PaxosMessage.PROPOSER);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof Update);
    }

    @Test
    public void updateRequestToFromBytes() throws IOException, ClassNotFoundException {
        final PaxosMessage initial = new UpdateRequest(25, Optional.of(1), PaxosMessage.PROPOSER);

        final byte[] encoded = initial.toByteArray();

        final PaxosMessage decoded = PaxosMessage.fromByteArray(encoded);

        assertEquals(initial, decoded);
        assertTrue(decoded instanceof UpdateRequest);
    }
}
