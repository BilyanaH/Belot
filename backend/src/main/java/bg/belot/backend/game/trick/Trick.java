package bg.belot.backend.game.trick;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.card.Card;
import bg.belot.backend.game.card.CardPoints;
import bg.belot.backend.game.card.CardRanking;
import bg.belot.backend.game.card.Suit;
import bg.belot.backend.game.rules.TrumpContext;
import bg.belot.backend.game.player.Player;
import lombok.Getter;

import java.util.LinkedHashMap;
import java.util.Map;

public class Trick {
    @Getter
    private final Announcement announcement;
    private final Map<Player, Card> playedCards;
    private final Player leadPlayer;

    public Trick(Announcement announcement, Player leadPlayer) {
        this.announcement = announcement;
        this.playedCards = new LinkedHashMap<>();
        this.leadPlayer = leadPlayer;
    }

    public Map<Player, Card> getPlayedCards() {
        return Map.copyOf(playedCards);
    }

    public void playCard(Player player, Card card) {
        if (isComplete()) {
            throw new IllegalStateException("Card can not be played when the trick is completed!");
        }
        if (playedCards.containsKey(player)) {
            throw new IllegalStateException("Player has already played a card in this trick");
        }
        playedCards.put(player, card);
    }

    public boolean isComplete() {
        return playedCards.size() == 4;
    }

    public Player getWinner() {
        if (!isComplete()) {
            throw new IllegalStateException("The trick must be completed before computing the winner!");
        }
        boolean trickHasTrump = playedCards.values().stream()
                .anyMatch(card -> TrumpContext.isTrump(card, announcement));

        Suit leadSuit = playedCards.get(leadPlayer).suit();

        Player winner = null;
        int bestStrength = -1;

        for (var entry : playedCards.entrySet()) {
            Card card = entry.getValue();
            boolean isTrump = TrumpContext.isTrump(card, announcement);
            boolean followsLead = card.suit() == leadSuit;

            boolean eligible = trickHasTrump ? isTrump : followsLead;
            if (!eligible) {
                continue;
            }

            int strength = CardRanking.getStrength(card, announcement);
            if (strength > bestStrength) {
                bestStrength = strength;
                winner = entry.getKey();
            }
        }
        return winner;
    }

    public int getTotalPoints() {
        if (!isComplete()) {
            throw new IllegalStateException("The trick must be completed before computing the total points!");
        }
        int totalPoints = 0;
        for (Card card : playedCards.values()) {
            totalPoints += CardPoints.getPoints(card, announcement);
        }
        return totalPoints;
    }
}
