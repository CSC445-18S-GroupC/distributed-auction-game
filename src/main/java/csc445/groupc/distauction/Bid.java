package csc445.groupc.distauction;

/**
 * Represents a bid in the game.
 *
 * Created by chris on 4/17/18.
 */
public class Bid {
    private final String bidder;
    private final float bidAmount;

    public Bid(final String bidder, final float bidAmount) {
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }

    public float getBidAmount() {
        return bidAmount;
    }

    /**
     * Returns the username of the player who made the bid.
     *
     * @return The username of the player who made the bid.
     */
    public String getBidder() {
        return bidder;
    }
}
