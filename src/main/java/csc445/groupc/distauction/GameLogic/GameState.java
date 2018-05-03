package csc445.groupc.distauction.GameLogic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

/**
 * Represents the current state of the game. It mutates as the game progresses.
 *
 * All references to the current time are taken in as parameters in order to
 * separate out the getting of the current time in order to make the code
 * easier to test.
 *
 * Created by chris on 4/17/18.
 */
public class GameState {
    /**
     * The amount of money that once a bid exceeds, the current round of the
     * game ends.
     */
    private static final float ROUND_WIN_BID_AMOUNT = 100f;

    /* The maximum and minimum possible increases in the amount placed in new bids */
    private static final float MAX_BID_INCREASE = 10.00f;
    private static final float MIN_BID_INCREASE = 0.01f;

    /* The amount of time that a game can run for before it times out */
    private static final float TIMEOUT_LENGTH = 2;
    private static final TemporalUnit TIMEOUT_UNIT = ChronoUnit.MINUTES;

    /* Player score change amounts associated with the different game end types */
    private static final int WINNING_BID_LEADER_SCORE_CHANGE = 100;
    private static final int WINNING_BID_LOSER_SCORE_CHANGE = -35;

    private static final int TIMEOUT_LEADER_SCORE_CHANGE = -20;
    private static final int TIMEOUT_LOSER_SCORE_CHANGE = -50;

    private static final int STARTING_SCORE = 0;

    /**
     * Indicates the cause of a round ending.
     *
     * TIMEOUT     - The round ended due to the timeout period elapsing
     * WINNING_BID - The round ended due to a player making a bid larger than
     *               the win threshold.
     */
    public enum RoundEndType {TIMEOUT, WINNING_BID}

    /**
     * The current round of the game that is going on. You can tell when a new
     * round has begun, as the round number will have incremented.
     */
    private int round;

    /**
     * The time at which the current round started. This is used in determining
     * when to timeout the game if it runs too long.
     */
    private LocalDateTime roundStartTime;

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
    private final ArrayList<Bid> bidHistory;

    /**
     * A random number generator used for generating the new bid amounts.
     */
    private final Random random;

    /**
     * Creates a new game object.
     *
     * @param seed A seed for the random number generator.
     * @param currentTime The current time.
     * @param players The usernames of the players.
     */
    public GameState(final long seed, final LocalDateTime currentTime, final String[] players) {
        this.round = 1;
        this.playerScores = new HashMap<>();
        this.bidHistory = new ArrayList<>();

        this.random = new Random(seed);
        this.roundStartTime = currentTime;

        for (final String p : players) {
            this.playerScores.put(p, STARTING_SCORE);
        }
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
     * @param currentTime The current time.
     * @param bid The bid to be placed.
     * @return True if the bid succeeded, or false if it failed.
     */
    public boolean makeBid(final LocalDateTime currentTime, final Bid bid) {
        // The bid cannot be placed if the round is already over
        if (isRoundOver(currentTime)) {
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
     * Returns the cause of the current round of the game ending, if the
     * current round has ended.
     *
     * @param currentTime The current time.
     * @return The cause of the round ending, or an empty optional if it has
     * not yet ended.
     */
    public Optional<RoundEndType> getRoundEndType(final LocalDateTime currentTime) {
        final Optional<Bid> lastBid = getMostRecentBid();

        if (roundStartTime.until(currentTime, TIMEOUT_UNIT) >= TIMEOUT_LENGTH) {
            return Optional.of(RoundEndType.TIMEOUT);
        } else if (lastBid.isPresent() && lastBid.get().getBidAmount() >= ROUND_WIN_BID_AMOUNT) {
            return Optional.of(RoundEndType.WINNING_BID);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns True if the current round is over.
     *
     * @param currentTime The current time.
     * @return True if the round is over, or False otherwise.
     */
    public boolean isRoundOver(final LocalDateTime currentTime) {
        return getRoundEndType(currentTime).isPresent();
    }

    /**
     * Returns a random bid amount to use in the player's next bid.
     *
     * @return A random bid amount.
     */
    public float getNewBidAmount() {
        final float prevAmount = getMostRecentBid().map(Bid::getBidAmount).orElse(0f);
        final float increase = random.nextFloat() * (MAX_BID_INCREASE - MIN_BID_INCREASE) + MIN_BID_INCREASE;

        return prevAmount + increase;
    }

    /**
     * Attempts to start a new round of the game.
     *
     * Fails if the current round is not yet over.
     *
     * @param currentTime The current time.
     * @return True if the new round was successfully started, or False otherwise.
     */
    public boolean startNewRound(final LocalDateTime currentTime) {
        // A new round cannot be started if the current round is still running
        if (!isRoundOver(currentTime)) {
            return false;
        }

        ++round;

        updatePlayerScores(getRoundEndType(currentTime).get());
        bidHistory.clear();

        roundStartTime = currentTime;

        return true;
    }

    /**
     * Updates the scores of the players based on how the current round of the
     * game ended.
     *
     * @param roundEndType The cause of the current round ending.
     */
    private void updatePlayerScores(final RoundEndType roundEndType) {
        final int leaderChange;
        final int loserChange;
        if (roundEndType == RoundEndType.WINNING_BID) {
            leaderChange = WINNING_BID_LEADER_SCORE_CHANGE;
            loserChange = WINNING_BID_LOSER_SCORE_CHANGE;
        } else {
            leaderChange = TIMEOUT_LEADER_SCORE_CHANGE;
            loserChange = TIMEOUT_LOSER_SCORE_CHANGE;
        }

        final Optional<String> leadingPlayer = getMostRecentBid().map(Bid::getBidder);

        leadingPlayer.map(p -> playerScores.put(p, playerScores.get(p) + leaderChange));

        playerScores.keySet().stream()
                .filter(p -> !(leadingPlayer.isPresent() && p.equals(leadingPlayer.get())))
                .forEach(p -> playerScores.put(p, playerScores.get(p) + loserChange));
    }
}
