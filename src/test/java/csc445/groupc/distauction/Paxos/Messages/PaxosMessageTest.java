package csc445.groupc.distauction.Paxos.Messages;

import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by chris on 4/17/18.
 */
public class PaxosMessageTest {
    @Test
    public void toFromBytes() throws IOException, ClassNotFoundException {
        final Integer initial = 5;

        final byte[] encoded = PaxosMessage.objectToBytes(initial);

        final Integer decoded = PaxosMessage.objectFromBytes(encoded);

        assertEquals(initial, decoded);
    }
}
