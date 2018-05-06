package csc445.groupc.distauction.GameLogic;

import java.io.Serializable;

/**
 * Created by chris on 4/23/18.
 */
public abstract class GameStep implements Serializable {
    public static GameStep fromString(final String s) {
        if (s.startsWith("Bid(")) {
            final String[] parts = s.split(",");

            final String bidder = parts[0].substring(13);
            final float bidAmount = Float.parseFloat(parts[1].substring(13, parts[1].length() - 1));

            return new Bid(bidder, bidAmount);
        } else {
            final int gameRound = Integer.parseInt(s.substring(20, s.length() - 1));

            return new Timeout(gameRound);
        }
    }
}
