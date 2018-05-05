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
public class GameStateTest {
    @Test
    public void getPlayerScores() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        scores.put("abc", 123);

        assertEquals(4, scores.size());
        assertEquals(3, gs.getPlayerScores().size());
    }

    @Test
    public void getMostRecentBidEmpty() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        assertEquals(Optional.empty(), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidTwo() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 2.01f);

        gs.applyStep(bidA);
        gs.applyStep(bidB);

        assertEquals(Optional.of(new Bid(bidB.getBidder(), bidA.getBidAmount() + bidB.getBidAmount())), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidWin() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        assertEquals(1, gs.getRound());

        for (int i = 0; i < 10;i++) {
            final Bid bidA = new Bid("Alice", 5.02f);
            final Bid bidB = new Bid("Bob", 5.01f);

            gs.applyStep(bidA);
            gs.applyStep(bidB);
        }

        assertEquals(2, gs.getRound());

        assertEquals(Optional.empty(), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidTwice() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Alice", 2.01f);

        gs.applyStep(bidA);
        gs.applyStep(bidB);

        assertEquals(Optional.of(bidA), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidTimeout() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Alice", 2.01f);

        gs.applyStep(bidA);
        gs.applyStep(bidB);

        final Timeout timeout = new Timeout(1);

        gs.applyStep(timeout);

        assertEquals(Optional.empty(), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidOldTimeout() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        assertEquals(1, gs.getRound());

        for (int i = 0; i < 10;i++) {
            final Bid bidA = new Bid("Alice", 5.02f);
            final Bid bidB = new Bid("Bob", 5.01f);

            gs.applyStep(bidA);
            gs.applyStep(bidB);
        }

        assertEquals(2, gs.getRound());

        final Bid bidC = new Bid("Alice", 2.01f);

        gs.applyStep(bidC);

        assertEquals(2, gs.getRound());

        final Timeout timeout = new Timeout(1);

        gs.applyStep(timeout);

        assertEquals(2, gs.getRound());

        assertEquals(Optional.of(bidC), gs.getMostRecentBid());
    }

    @Test
    public void generateRandomBidBidder() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final String bidder = "Alice";
        for (int i = 0; i < 100; i++) {
            assertEquals(bidder, gs.generateRandomBid(bidder).getBidder());
        }
    }

    @Test
    public void generateRandomBidAmountRange() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final String bidder = "Alice";
        for (int i = 0; i < 1000; i++) {
            final float amount = gs.generateRandomBid(bidder).getBidAmount();
            assertTrue(amount >= 0.01);
            assertTrue(amount <= 10.0);
        }
    }

    @Test
    public void toStringWithBid() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Jane", 2.01f);

        gs.applyStep(bidA);
        gs.applyStep(bidB);

        final String expected = "GameState(round = 1, amount = 7.5299997, topBid = Bid(bidder = Jane, bidAmount = 2.01), { Bob: 0 Alice: 0 Jane: 0 })";

        assertEquals(expected, gs.toString());
    }

    @Test
    public void toStringNoBid() {
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"}, (s) -> {});

        final String expected = "GameState(round = 1, amount = 0.0, topBid = None, { Bob: 0 Alice: 0 Jane: 0 })";

        assertEquals(expected, gs.toString());
    }

    @Test
    public void updateFunctionCall() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final GameState gs = new GameState(LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"},
                (s) -> { countDownLatch.countDown(); });

        final Bid bidA = new Bid("Alice", 5.52f);

        gs.applyStep(bidA);

        try {
            countDownLatch.await(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            fail("updateFunction was not called");
        }
    }
}
