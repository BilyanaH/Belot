package bg.belot.backend.game.meld;

import lombok.Getter;

@Getter
public enum MeldType {
    TIERCE(20),
    QUADTREE(50),
    QUINTE(100),
    FOUR_OF_A_KIND(100),
    FOUR_NINES(150),
    FOUR_JACKS(200),
    BELOTE(20);

    private final int points;

    MeldType(int points) {
        this.points = points;
    }

}
