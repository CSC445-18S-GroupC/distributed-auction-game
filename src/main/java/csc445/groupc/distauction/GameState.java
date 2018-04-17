package csc445.groupc.distauction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Represents the current state of the game. It mutates as the game progresses.
 *
 * Created by chris on 4/17/18.
 */
public class GameState {
    /**
     * The current round of the game that is going on. You can tell when a new
     * round has begun, as the round number will have incremented.
     */
    private int round;

    /**
     * Stores the scores of each of the players. Maps from a player name to
     * that player's score.
     *
     * Scores can be negative.
     */
    private final HashMap<String, Integer> playerScores;

    /**
     * A list of all of the bids that have been made in the current round of
     * the game. The last item in the list is the most recent bid.
     */
    private ArrayList<Bid> bidHistory;

    public GameState() {
        this.round = 1;
        this.playerScores = new HashMap<>();
        this.bidHistory = new ArrayList<>();
    }

    public int getRound() {
        return round;
    }

    public HashMap<String, Integer> getPlayerScores() {
        return (HashMap<String, Integer>) playerScores.clone();
    }

    public ArrayList<Bid> getBidHistory() {
        return (ArrayList<Bid>) bidHistory.clone();
    }

    /**
     * Attempts to return the most recent bid successfully placed by a player.
     *
     * @return The most recent bid. If no bid has been made yet, then an empty
     * Optional is returned.
     */
    public Optional<Bid> getMostRecentBid() {
        final int numBids = bidHistory.size();

        if (numBids == 0) {
            return Optional.empty();
        } else {
            return Optional.of(bidHistory.get(numBids - 1));
        }
    }
}
