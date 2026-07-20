package bg.belot.backend.game.card;

import bg.belot.backend.game.bidding.Announcement;

import java.util.Map;

public class CardPoints {
    private static final Map<Rank, Integer> TRUMP_POINTS = Map.of(
            Rank.JACK, 20,
            Rank.NINE, 14,
            Rank.ACE, 11,
            Rank.TEN, 10,
            Rank.KING, 4,
            Rank.QUEEN, 3,
            Rank.EIGHT, 0,
            Rank.SEVEN, 0
    );

    private static final Map<Rank, Integer> NORMAL_POINTS = Map.of(
            Rank.ACE, 11,
            Rank.TEN, 10,
            Rank.KING, 4,
            Rank.QUEEN, 3,
            Rank.JACK, 2,
            Rank.NINE, 0,
            Rank.EIGHT, 0,
            Rank.SEVEN, 0
    );

    public static int getPoints(Card card, Announcement announcement) {
        Map<Rank, Integer> strength = TrumpContext.isTrump(card, announcement) ? TRUMP_POINTS : NORMAL_POINTS;
        return strength.get(card.rank());
    }
}
