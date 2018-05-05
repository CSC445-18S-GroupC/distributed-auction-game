package csc445.groupc.distauction.GameLogic;

/**
 * Created by chris on 5/2/18.
 */
public class Timeout extends GameStep {
    private final int gameRound;

    public Timeout(final int gameRound) {
        this.gameRound = gameRound;
    }

    public int getGameRound() {
        return gameRound;
    }
}
