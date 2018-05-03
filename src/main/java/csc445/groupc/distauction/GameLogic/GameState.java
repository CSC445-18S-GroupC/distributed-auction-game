package csc445.groupc.distauction.GameLogic;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

/**
 * A GameState object is used to represent the current state of the game. It acts as a deterministic finite state
 * machine that takes in inputs that are GameSteps in order to change the state of the game. This allows the game state
 * to be communicated and reconstructed for use in distributed consensus algorithms like Paxos.
 * <br><br>
 * Every time the state of the game is changed, the updateFunction, supplied as a parameter to the constructor, is
 * called in order to allow for the game's UI to be updated to reflect the new state of the game.
 * <br><br>
 * <pre>
 * final GameState gameState = new GameState(LocalDateTime.now(), new String[]{ "Jane", "Alice", "Bob" }, (s) -&gt; {})
 *
 * final GameStep bid = gameState.generateRandomBid("Jane");
 *
 * gameState.applyStep(bid);
 *
 * System.out.println(gameState.getMostRecentBid().get());
 * System.out.println(gameState);
 * </pre>
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
    public static final float TIMEOUT_LENGTH = 2;
    public static final TemporalUnit TIMEOUT_UNIT = ChronoUnit.MINUTES;

    /* Player score change amounts associated with the different game end types */
    private static final int WINNING_BID_LEADER_SCORE_CHANGE = 100;
    private static final int WINNING_BID_LOSER_SCORE_CHANGE = -55;

    private static final int TIMEOUT_LEADER_SCORE_CHANGE = -30;
    private static final int TIMEOUT_LOSER_SCORE_CHANGE = -90;

    private static final int STARTING_SCORE = 0;
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
     * A random number generator used for generating the new bid amounts.
     */
    private final Random random;

    /**
     * The most recent bud placed by a player.
     */
    private Optional<Bid> topBid;

    /**
     * The money amount that the bidding is currently up to.
     */
    private float amount;

    /**
     * The function that is called whenever the game state is updated. For example, this can be used to update a GUI
     * display for the game whenever the state changes.
     */
    private final Consumer<GameState> updateFunction;

    /**
     * Creates a new game state object.
     *
     * @param currentTime The current time.
     * @param players The usernames of the players.
     * @param updateFunction A function to call whenever the game state is changed.
     */
    public GameState(final LocalDateTime currentTime, final String[] players, final Consumer<GameState> updateFunction) {
        this.round = 1;
        this.playerScores = new HashMap<>();
        this.updateFunction = updateFunction;

        this.random = new Random();

        this.topBid = Optional.empty();
        this.amount = 0.0f;

        for (final String p : players) {
            this.playerScores.put(p, STARTING_SCORE);
        }
    }

    /**
     * Returns a copy of the scores of all of the players in the game.
     *
     * @return The scores of the players.
     */
    public HashMap<String, Integer> getPlayerScores() {
        return (HashMap<String, Integer>) playerScores.clone();
    }

    /**
     * Returns the most recent bid successfully placed by a player, if any.
     *
     * @return The most recent bid. If no bid has been made yet, then an empty
     * Optional is returned.
     */
    public Optional<Bid> getMostRecentBid() {
        return topBid;
    }

    private void onUpdate() {
        updateFunction.accept(this);
    }

    /**
     * Generates a Bid object for the given player. Uses a randomly selected bid amount.
     *
     * @param bidder The player to create a Bid for.
     * @return The created Bid.
     */
    public Bid generateRandomBid(final String bidder) {
        final float increase = random.nextFloat() * (MAX_BID_INCREASE - MIN_BID_INCREASE) + MIN_BID_INCREASE;

        return new Bid(bidder, increase);
    }

    /**
     * Applies the given GameStep to alter the game state accordingly.
     *
     * @param gameStep The game step to apply.
     */
    public void applyStep(final GameStep gameStep) {
        if (gameStep instanceof Bid) {
            applyBid((Bid) gameStep);
        } else if (gameStep instanceof Timeout) {
            applyTimeout();
        }

        onUpdate();
    }

    private void applyBid(final Bid bid) {
        if (!topBid.isPresent() || !topBid.get().getBidder().equals(bid.getBidder())) {
            topBid = Optional.of(bid);
            amount += bid.getBidAmount();

            if (amount >= ROUND_WIN_BID_AMOUNT) {
                startNextRound(WINNING_BID_LEADER_SCORE_CHANGE, WINNING_BID_LOSER_SCORE_CHANGE);
            }
        }
    }

    private void applyTimeout() {
        startNextRound(TIMEOUT_LEADER_SCORE_CHANGE, TIMEOUT_LOSER_SCORE_CHANGE);
    }

    private void startNextRound(final int leaderChange, final int loserChange) {
        ++round;
        topBid = Optional.empty();
        amount = 0;

        final Optional<String> leadingPlayer = topBid.map(Bid::getBidder);

        leadingPlayer.map(p -> playerScores.put(p, playerScores.get(p) + leaderChange));

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
        for (final Map.Entry<String, Integer> playerEntry : playerScores.entrySet()) {
            sb.append(playerEntry.getKey() + ": " + playerEntry.getValue() + " ");
        }

        sb.append("})");

        return sb.toString();
    }
}
