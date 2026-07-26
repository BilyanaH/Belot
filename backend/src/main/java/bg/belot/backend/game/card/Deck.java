package bg.belot.backend.game.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    public void cut() {
        if (deck.size() < 2) {
            throw new IllegalStateException("Cannot cut a deck with fewer than 2 cards");
        }
        int cutPoint = 1 + (int) (Math.random() * (deck.size() - 1));
        List<Card> bottom = new ArrayList<>(deck.subList(0, cutPoint));
        List<Card> top = new ArrayList<>(deck.subList(cutPoint, deck.size()));
        deck.clear();
        deck.addAll(top);
        deck.addAll(bottom);
    }

    public Card draw() {
        if (deck.isEmpty()) {
            throw new IllegalStateException("Deck is empty, cannot draw a card");
        }
        return deck.removeLast();
    }

    public int remainingCount() {
        return deck.size();
    }

    public void returnCards(List<Card> cardsWonByTeam) {
        deck.addAll(cardsWonByTeam);
    }
}
