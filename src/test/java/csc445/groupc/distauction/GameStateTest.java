package csc445.groupc.distauction;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by chris on 4/17/18.
 */
class GameStateTest {
    @Test
    void getPlayerScores() {
        final GameState gs = new GameState();

        final HashMap<String, Integer> scores = gs.getPlayerScores();

        scores.put("abc", 123);

        assertEquals(1, scores.size());
        assertEquals(0, gs.getPlayerScores().size());
    }

    @Test
    void getBidHistory() {
        final GameState gs = new GameState();

        final ArrayList<Bid> bidHistory = gs.getBidHistory();

        bidHistory.add(new Bid("abc", 123.5f));

        assertEquals(1, bidHistory.size());
        assertEquals(0, gs.getBidHistory().size());
    }

    @Test
    void getMostRecentBidEmpty() {
        final GameState gs = new GameState();

        assertEquals(Optional.empty(), gs.getMostRecentBid());
    }

}
