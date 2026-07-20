package bg.belot.backend.game.card;

import bg.belot.backend.game.bidding.Announcement;

import java.util.Map;

public class CardRanking {
    private static final Map<Rank, Integer> TRUMP_STRENGTH = Map.of(
            Rank.JACK, 8,
            Rank.NINE, 7,
            Rank.ACE, 6,
            Rank.TEN, 5,
            Rank.KING, 4,
            Rank.QUEEN, 3,
            Rank.EIGHT, 2,
            Rank.SEVEN, 1
    );

    private static final Map<Rank, Integer> NORMAL_STRENGTH = Map.of(
            Rank.ACE, 8,
            Rank.TEN, 7,
            Rank.KING, 6,
            Rank.QUEEN, 5,
            Rank.JACK, 4,
            Rank.NINE, 3,
            Rank.EIGHT, 2,
            Rank.SEVEN, 1
    );

    public static int getStrength(Card card, Announcement announcement) {
        Map<Rank, Integer> strength = TrumpContext.isTrump(card, announcement) ? TRUMP_STRENGTH : NORMAL_STRENGTH;
        return strength.get(card.rank());
    }
}
