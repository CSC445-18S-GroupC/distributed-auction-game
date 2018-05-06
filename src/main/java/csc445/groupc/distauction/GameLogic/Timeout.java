package csc445.groupc.distauction.GameLogic;

import java.util.Objects;

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

    @Override
    public String toString() {
        return "Timeout(gameRound = " + gameRound + ")";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Timeout timeout = (Timeout) o;
        return gameRound == timeout.gameRound;
    }

    @Override
    public int hashCode() {

        return Objects.hash(gameRound);
    }
}
