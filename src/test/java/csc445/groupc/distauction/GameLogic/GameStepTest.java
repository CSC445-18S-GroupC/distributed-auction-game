package csc445.groupc.distauction.GameLogic;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Created by chris on 4/17/18.
 */
public class GameStepTest {
    @Test
    public void fromStringBid() {
        final GameStep expected = new Bid("ABC354", 0.03442f);

        final String encoded = expected.toString();

        final GameStep actual = GameStep.fromString(encoded);

        assertEquals(expected, actual);
    }
    @Test
    public void fromStringTimeout() {
        final GameStep expected = new Timeout(2);

        final String encoded = expected.toString();

        final GameStep actual = GameStep.fromString(encoded);

        assertEquals(expected, actual);
    }
}
