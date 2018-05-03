package csc445.groupc.distauction;

import csc445.groupc.distauction.GameLogic.Bid;
import csc445.groupc.distauction.GameLogic.GameState;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by chris on 4/17/18.
 */
public class GameStateTest {
    @Test
    public void getPlayerScores() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        scores.put("abc", 123);

        assertEquals(4, scores.size());
        assertEquals(3, gs.getPlayerScores().size());
    }

    @Test
    public void getBidHistory() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final ArrayList<Bid> bidHistory = gs.getBidHistory();

        bidHistory.add(new Bid("abc", 123.5f));

        assertEquals(1, bidHistory.size());
        assertEquals(0, gs.getBidHistory().size());
    }

    @Test
    public void getMostRecentBidEmpty() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertEquals(Optional.empty(), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidTwo() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 12.01f);

        gs.makeBid(LocalDateTime.now(), bidA);
        gs.makeBid(LocalDateTime.now(), bidB);

        assertEquals(Optional.of(bidB), gs.getMostRecentBid());
    }

    @Test
    public void makeBidSuccessful() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 12.01f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertTrue(gs.makeBid(LocalDateTime.now(), bidB));

        assertEquals(2, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
        assertEquals(bidB, gs.getBidHistory().get(1));
    }

    @Test
    public void makeBidRepeat() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Alice", 12.01f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertFalse(gs.makeBid(LocalDateTime.now(), bidB));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
    }

    @Test
    public void makeBidLower() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 1.20f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertFalse(gs.makeBid(LocalDateTime.now(), bidB));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
    }

    @Test
    public void makeBidRoundOver() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        final Bid bidA = new Bid("Alice", 105.52f);
        final Bid bidB = new Bid("Bob", 110.20f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertFalse(gs.makeBid(LocalDateTime.now(), bidB));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
    }

    @Test
    public void isRoundOverStart() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertFalse(gs.isRoundOver(LocalDateTime.now()));
    }

    @Test
    public void isRoundOverWinning() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertFalse(gs.isRoundOver(LocalDateTime.now()));

        final Bid bidA = new Bid("Alice", 105.52f);
        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertTrue(gs.isRoundOver(LocalDateTime.now()));

        assertEquals(Optional.of(GameState.RoundEndType.WINNING_BID),
                gs.getRoundEndType(LocalDateTime.now()));
    }

    @Test
    public void isRoundOverTimeout() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertFalse(gs.isRoundOver(LocalDateTime.now()));

        assertTrue(gs.isRoundOver(LocalDateTime.now().plus(6, ChronoUnit.MINUTES)));
        assertEquals(Optional.of(GameState.RoundEndType.TIMEOUT),
                gs.getRoundEndType(LocalDateTime.now().plus(6, ChronoUnit.MINUTES)));
    }

    @Test
    public void getNewBidAmount() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        for (int i = 0; i < 100; i++) {
            final float amount = gs.getNewBidAmount();

            assertTrue(amount >= 0.01f);
            assertTrue(amount <= 10.00f);
        }

        final float aliceAmount = 09.20f;
        final Bid bidA = new Bid("Alice", aliceAmount);
        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        for (int i = 0; i < 100; i++) {
            final float amount = gs.getNewBidAmount();

            assertTrue(amount >= 0.01f + aliceAmount);
            assertTrue(amount <= 10.00f + aliceAmount);
        }
    }

    @Test
    public void startNewRoundStillRunning() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertEquals(1, gs.getRound());
        assertFalse(gs.startNewRound(LocalDateTime.now()));
        assertEquals(1, gs.getRound());
    }

    @Test
    public void startNewRoundWinningBid() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertEquals(1, gs.getRound());

        final Bid bidA = new Bid("Alice", 105.52f);
        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertTrue(gs.startNewRound(LocalDateTime.now()));
        assertEquals(2, gs.getRound());

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        assertEquals(100, (int) scores.get("Alice"));
        assertEquals(-35, (int) scores.get("Bob"));
        assertEquals(-35, (int) scores.get("Jane"));
    }

    @Test
    public void startNewRoundTimeoutWithBid() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertEquals(1, gs.getRound());

        final Bid bidA = new Bid("Alice", 1.52f);
        assertTrue(gs.makeBid(LocalDateTime.now(), bidA));

        assertTrue(gs.startNewRound(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        assertEquals(2, gs.getRound());

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        assertEquals(-20, (int) scores.get("Alice"));
        assertEquals(-50, (int) scores.get("Bob"));
        assertEquals(-50, (int) scores.get("Jane"));
    }

    @Test
    public void startNewRoundTimeoutNoBids() {
        final GameState gs = new GameState(42, LocalDateTime.now(), new String[] {"Alice", "Bob", "Jane"});

        assertEquals(1, gs.getRound());

        assertTrue(gs.startNewRound(LocalDateTime.now().plus(5, ChronoUnit.MINUTES)));
        assertEquals(2, gs.getRound());

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        assertEquals(-50, (int) scores.get("Alice"));
        assertEquals(-50, (int) scores.get("Bob"));
        assertEquals(-50, (int) scores.get("Jane"));
    }
}
