package csc445.groupc.distauction;

import org.junit.Test;

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
        final GameState gs = new GameState(42);

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        scores.put("abc", 123);

        assertEquals(1, scores.size());
        assertEquals(0, gs.getPlayerScores().size());
    }

    @Test
    public void getBidHistory() {
        final GameState gs = new GameState(42);

        final ArrayList<Bid> bidHistory = gs.getBidHistory();

        bidHistory.add(new Bid("abc", 123.5f));

        assertEquals(1, bidHistory.size());
        assertEquals(0, gs.getBidHistory().size());
    }

    @Test
    public void getMostRecentBidEmpty() {
        final GameState gs = new GameState(42);

        assertEquals(Optional.empty(), gs.getMostRecentBid());
    }

    @Test
    public void getMostRecentBidTwo() {
        final GameState gs = new GameState(42);

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 12.01f);

        gs.makeBid(bidA);
        gs.makeBid(bidB);

        assertEquals(Optional.of(bidB), gs.getMostRecentBid());
    }

    @Test
    public void makeBidSuccessful() {
        final GameState gs = new GameState(42);

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 12.01f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertTrue(gs.makeBid(bidB));

        assertEquals(2, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
        assertEquals(bidB, gs.getBidHistory().get(1));
    }

    @Test
    public void makeBidRepeat() {
        final GameState gs = new GameState(42);

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Alice", 12.01f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertFalse(gs.makeBid(bidB));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
    }

    @Test
    public void makeBidLower() {
        final GameState gs = new GameState(42);

        final Bid bidA = new Bid("Alice", 5.52f);
        final Bid bidB = new Bid("Bob", 1.20f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertFalse(gs.makeBid(bidB));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
    }

    @Test
    public void makeBidRoundOver() {
        final GameState gs = new GameState(42);

        final Bid bidA = new Bid("Alice", 105.52f);
        final Bid bidB = new Bid("Bob", 110.20f);

        assertEquals(0, gs.getBidHistory().size());

        assertTrue(gs.makeBid(bidA));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));

        assertFalse(gs.makeBid(bidB));

        assertEquals(1, gs.getBidHistory().size());
        assertEquals(bidA, gs.getBidHistory().get(0));
    }

    @Test
    public void isRoundOverStart() {
        final GameState gs = new GameState(42);

        assertFalse(gs.isRoundOver());
    }

    @Test
    public void isRoundOverEnd() {
        final GameState gs = new GameState(42);

        assertFalse(gs.isRoundOver());

        final Bid bidA = new Bid("Alice", 105.52f);
        assertTrue(gs.makeBid(bidA));

        assertTrue(gs.isRoundOver());
    }

    @Test
    public void getNewBidAmount() {
        final GameState gs = new GameState(42);

        for (int i = 0; i < 100; i++) {
            final float amount = gs.getNewBidAmount();

            assertTrue(amount >= 0.01f);
            assertTrue(amount <= 10.00f);
        }

        final float aliceAmount = 09.20f;
        final Bid bidA = new Bid("Alice", aliceAmount);
        assertTrue(gs.makeBid(bidA));

        for (int i = 0; i < 100; i++) {
            final float amount = gs.getNewBidAmount();

            assertTrue(amount >= 0.01f + aliceAmount);
            assertTrue(amount <= 10.00f + aliceAmount);
        }
    }
}
