package bg.belot.backend.game.meld;

import bg.belot.backend.game.card.Card;
import bg.belot.backend.game.card.Rank;
import bg.belot.backend.game.card.Suit;
import bg.belot.backend.game.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MeldFinder {
    private static final List<Rank> SEQUENCE_ORDER = List.of(
            Rank.SEVEN, Rank.EIGHT, Rank.NINE, Rank.TEN, Rank.JACK, Rank.QUEEN, Rank.KING, Rank.ACE
    );

    private static final int TIERCE_LENGTH = 3;
    private static final int QUADTREE_LENGTH = 4;
    private static final int FOUR_OF_A_KIND_COUNT = 4;

    public static List<Meld> findSequences(Player player) {
        List<Meld> result = new ArrayList<>();
        List<Card> hand = player.getHand();

        for (Suit suit : Suit.values()) {
            Set<Card> suitSequence = new HashSet<>();
            Set<Rank> suitRanks = hand.stream()
                    .filter(c -> c.suit() == suit)
                    .map(Card::rank)
                    .collect(Collectors.toSet());

            List<Card> currentRun = new ArrayList<>();

            for (Rank rank : SEQUENCE_ORDER) {
                if (suitRanks.contains(rank)) {
                    currentRun.add(new Card(suit, rank));
                } else {
                    addSequenceMeld(result, player, currentRun);
                    currentRun = new ArrayList<>();
                }
            }
            addSequenceMeld(result, player, currentRun);
        }

        return result;
    }

    private static void addSequenceMeld(List<Meld> result, Player player, List<Card> currentRun) {
        if (currentRun.size() < TIERCE_LENGTH) {
            return;
        }
        MeldType type = currentRun.size() == TIERCE_LENGTH ? MeldType.TIERCE
                : currentRun.size() == QUADTREE_LENGTH ? MeldType.QUADTREE
                : MeldType.QUINTE;
        result.add(new Meld(player, type, new ArrayList<>(currentRun)));
    }

    public static List<Meld> findFours(Player player) {
        List<Meld> result = new ArrayList<>();
        List<Card> hand = player.getHand();
        for (Rank rank : SEQUENCE_ORDER) {
            if (rank == Rank.SEVEN || rank == Rank.EIGHT) continue;
            if (hand.stream().filter(c -> c.rank() == rank).count() == FOUR_OF_A_KIND_COUNT) {
                List<Card> meldList = new ArrayList<>(FOUR_OF_A_KIND_COUNT);
                for (Suit suit : Suit.values()) {
                    meldList.add(new Card(suit, rank));
                }
                switch (rank) {
                    case Rank.JACK -> result.add(new Meld(player, MeldType.FOUR_JACKS, meldList));
                    case Rank.NINE -> result.add(new Meld(player, MeldType.FOUR_NINES, meldList));
                    default -> result.add(new Meld(player, MeldType.FOUR_OF_A_KIND, meldList));
                }
            }
        }
        return result;
    }

    public static Meld findBelote(Player player, Suit trumpSuit) {
        Card trumpQueen = new Card(trumpSuit, Rank.QUEEN);
        Card trumpKing = new Card(trumpSuit, Rank.KING);

        if (player.getHand().contains(trumpQueen)
                && player.getHand().contains(trumpKing)) {
            return new Meld(player, MeldType.BELOTE, List.of(trumpQueen, trumpKing));
        }

        return null;
    }

    public static List<Meld> findAll(Player player) {
        List<Meld> all = new ArrayList<>(findSequences(player));
        all.addAll(findFours(player));
        return List.copyOf(all);
    }
}
