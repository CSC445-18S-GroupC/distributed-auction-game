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
     * The amount of money that once a bid exceeds, the current round of the
     * game ends.
     */
    private static final float ROUND_WIN_BID_AMOUNT = 100f;

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

    /**
     * Appends the given Bid to the bid history.
     *
     * Fails if the player placing the bid was also the player who placed the
     * previous bid, or if the bid amount is lower than the previous bid, or if
     * the round is already over.
     *
     * @param bid The bid to be placed.
     * @return True if the bid succeeded, or false if it failed.
     */
    public boolean makeBid(final Bid bid) {
        // The bid cannot be placed if the round is already over
        if (isRoundOver()) {
            return false;
        }

        final Optional<String> lastBidder = getMostRecentBid().map(Bid::getBidder);
        final Optional<Float> prevAmount = getMostRecentBid().map(Bid::getBidAmount);

        if (lastBidder.isPresent() && lastBidder.get().equals(bid.getBidder())) {
            // Bid fails if the bidder was also the bidder who placed the previous bid
            return false;
        } else if (prevAmount.isPresent() && prevAmount.get() >= bid.getBidAmount()) {
            // Bid fails if the new bid amount is less than or equal to the previous bid's amount
            return false;
        } else {
            bidHistory.add(bid);
            return true;
        }
    }

    /**
     * Returns True if the current round is over.
     *
     * @return True if the round is over, or False otherwise.
     */
    public boolean isRoundOver() {
        final Optional<Bid> lastBid = getMostRecentBid();

        return lastBid.isPresent() && lastBid.get().getBidAmount() >= ROUND_WIN_BID_AMOUNT;
    }
}
