package bg.belot.backend.game.rules;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.bidding.AnnouncementType;
import bg.belot.backend.game.card.Card;
import bg.belot.backend.game.card.CardRanking;
import bg.belot.backend.game.card.Suit;
import bg.belot.backend.game.player.Player;
import bg.belot.backend.game.trick.Trick;

import java.util.List;

public class MoveValidator {
    public static boolean isValidMove(Trick trick, Player player, Card card, Player currentLeader) {
        List<Card> playedSoFar = trick.getPlayedCards().values().stream().toList();
        if (playedSoFar.isEmpty()) {
            return true;
        }
        Suit leadSuit = playedSoFar.getFirst().suit();
        Announcement announcement = trick.getAnnouncement();
        List<Card> hand = player.getHand();

        boolean hasLeadSuit = hand.stream().anyMatch(c -> c.suit() == leadSuit);

        if (hasLeadSuit) {
            return isValidFollowingLead(hand, card, leadSuit, announcement, playedSoFar);
        }

        return isValidWhenOutOfLeadSuit(player, hand, card, announcement, playedSoFar, currentLeader);

    }

    private static boolean isValidFollowingLead(List<Card> hand, Card card, Suit leadSuit,
                                                Announcement announcement, List<Card> playedSoFar) {
        boolean leadIsTrump = TrumpContext.isTrump(playedSoFar.getFirst(), announcement);
        boolean mustOvertrumpHere = announcement.type() == AnnouncementType.ALL_TRUMP || leadIsTrump;

        if (!mustOvertrumpHere) {
            return card.suit() == leadSuit;
        }

        List<Card> leadSuitCards = hand.stream().filter(c -> c.suit() == leadSuit).toList();

        int highestTrumpPlayed = highestTrumpStrength(playedSoFar, announcement);

        boolean canOvertrump = leadSuitCards.stream()
                .anyMatch(c -> CardRanking.getStrength(c, announcement) > highestTrumpPlayed);

        if (!canOvertrump) {
            return card.suit() == leadSuit;
        }
        return card.suit() == leadSuit && CardRanking.getStrength(card, announcement) > highestTrumpPlayed;
    }

    private static boolean isValidWhenOutOfLeadSuit(Player player, List<Card> hand, Card card,
                                                    Announcement announcement, List<Card> playedSoFar,
                                                    Player currentLeader) {
        boolean mustPlayTrump = isTrumpRequired(player, announcement, currentLeader);
        if (!mustPlayTrump) {
            return true;
        }

        boolean hasTrump = hand.stream().anyMatch(c -> TrumpContext.isTrump(c, announcement));
        if (!hasTrump) {
            return true;
        }

        if (!TrumpContext.isTrump(card, announcement)) {
            return false;
        }

        int highestTrumpPlayed = highestTrumpStrength(playedSoFar, announcement);
        if (highestTrumpPlayed == -1) {
            return true;
        }

        boolean canOvertrump = hand.stream()
                .filter(c -> TrumpContext.isTrump(c, announcement))
                .anyMatch(c -> CardRanking.getStrength(c, announcement) > highestTrumpPlayed);

        if (!canOvertrump) {
            return true;
        }

        return CardRanking.getStrength(card, announcement) > highestTrumpPlayed;
    }

    private static int highestTrumpStrength(List<Card> playedSoFar, Announcement announcement) {
        return playedSoFar.stream()
                .filter(c -> TrumpContext.isTrump(c, announcement))
                .mapToInt(c -> CardRanking.getStrength(c, announcement))
                .max()
                .orElse(-1);
    }

    private static boolean isTrumpRequired(Player player, Announcement announcement, Player currentLeader) {
        return switch (announcement.type()) {
            case NO_TRUMP -> false;
            case ALL_TRUMP -> true;
            case SUIT_TRUMP -> !player.getTeam().hasPlayer(currentLeader);
            case PASS -> throw new IllegalStateException("PASS cannot be the winning announcement");
        };
    }
}
