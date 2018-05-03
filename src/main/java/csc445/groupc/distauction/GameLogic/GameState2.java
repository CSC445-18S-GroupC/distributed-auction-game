package csc445.groupc.distauction.GameLogic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Optional;
import java.util.Random;

/**
 * Created by chris on 5/2/18.
 */
public class GameState2 {
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
    private static final int WINNING_BID_LOSER_SCORE_CHANGE = -55;//-35;

    private static final int TIMEOUT_LEADER_SCORE_CHANGE = -30;
    private static final int TIMEOUT_LOSER_SCORE_CHANGE = -90;//-50;

    private static final int STARTING_SCORE = 0;
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
     * A random number generator used for generating the new bid amounts.
     */
    private final Random random;

    private Optional<Bid> topBid;
    private float amount;

    /**
     * Creates a new game object.
     *
     * @param currentTime The current time.
     * @param players The usernames of the players.
     */
    public GameState2(final LocalDateTime currentTime, final String[] players) {
        this.round = 1;
        this.playerScores = new HashMap<>();

        this.random = new Random();
        this.roundStartTime = currentTime;

        this.topBid = Optional.empty();
        this.amount = 0.0f;

        for (final String p : players) {
            this.playerScores.put(p, STARTING_SCORE);
        }
    }

    public Bid generateRandomBid(final String bidder) {
        final float increase = random.nextFloat() * (MAX_BID_INCREASE - MIN_BID_INCREASE) + MIN_BID_INCREASE;

        return new Bid(bidder, increase);
    }

    public void applyStep(final GameStep gameStep) {
        if (gameStep instanceof Bid) {
            applyBid((Bid) gameStep);
        } else if (gameStep instanceof Timeout) {
            applyTimeout();
        }

        // TODO: Call onUpdate
    }

    private void applyBid(final Bid bid) {
        if (!topBid.isPresent() || !topBid.get().getBidder().equals(bid.getBidder())) {
            topBid = Optional.of(bid);
            amount += bid.getBidAmount();

            System.out.println("amount = " + amount);

            if (amount >= ROUND_WIN_BID_AMOUNT) {
                System.out.println("new round");

                updatePlayerScores(WINNING_BID_LEADER_SCORE_CHANGE, WINNING_BID_LOSER_SCORE_CHANGE);

                ++round;
                topBid = Optional.empty();
                amount = 0;
            }
        }
    }

    private void applyTimeout() {
        // TODO: Implement
    }

    private void updatePlayerScores(final int leaderChange, final int loserChange) {
        System.out.println("start updating");
        final Optional<String> leadingPlayer = topBid.map(Bid::getBidder);

        System.out.println(leadingPlayer.get());
        leadingPlayer.map(p -> playerScores.put(p, playerScores.get(p) + leaderChange));

        System.out.println(2);
        playerScores.keySet().stream()
                .filter(p -> !(leadingPlayer.isPresent() && p.equals(leadingPlayer.get())))
                .forEach(p -> playerScores.put(p, playerScores.get(p) + loserChange));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        sb.append("GameState(");

        sb.append("round = " + round);
        sb.append(", amount = " + amount);
        sb.append(", topBid = ");

        if (topBid.isPresent()) {
            sb.append(topBid.get());
        } else {
            sb.append("None");
        }

        sb.append(", { ");
        for (final String player : playerScores.keySet()) {
            sb.append(player + ": " + playerScores.get(player) + " ");
        }

        sb.append("})");

        return sb.toString();
    }
}
