package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class PrepareTest {
    @Test
    public void toFromBytesEveryone() throws IOException {
        final Prepare initial = new Prepare(25, Optional.empty(), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final Prepare decoded = Prepare.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }

    @Test
    public void toFromBytes() throws IOException {
        final Prepare initial = new Prepare(25, Optional.of(1), PaxosMessage.PROPOSER, 2);

        final byte[] encoded = initial.toByteArray();

        final Prepare decoded = Prepare.fromByteArray(encoded);

        assertEquals(initial, decoded);
    }
}
