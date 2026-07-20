package bg.belot.backend.game.player;

import bg.belot.backend.game.card.Card;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(of = "name")
public class Player {
    @Getter
    private final String name;
    private final List<Card> hand;

    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        hand.add(card);
    }

    public void playCard(Card card) {
        if (!hand.remove(card)) {
            throw new IllegalStateException("Card must be in the hand");
        }
    }

    public List<Card> getHand() {
        return List.copyOf(hand);
    }

}
