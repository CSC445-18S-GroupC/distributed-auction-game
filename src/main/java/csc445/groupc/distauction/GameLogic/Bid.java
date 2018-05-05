package csc445.groupc.distauction.GameLogic;

/**
 * Represents a bid in the game.
 *
 * Created by chris on 4/17/18.
 */
public class Bid extends GameStep {
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

    @Override
    public String toString() {
        return "Bid(bidder = " + bidder + ", bidAmount = " + bidAmount + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Bid bid = (Bid) o;

        if (Float.compare(bid.bidAmount, bidAmount) != 0) return false;
        return bidder.equals(bid.bidder);
    }

    @Override
    public int hashCode() {
        int result = bidder.hashCode();
        result = 31 * result + (bidAmount != +0.0f ? Float.floatToIntBits(bidAmount) : 0);
        return result;
    }
}
