package bg.belot.backend.game.card;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void testShuffle() {
        Deck deck = new Deck();
        List<Card> before = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            before.add(deck.draw());
        }

        Deck shuffledDeck = new Deck();
        shuffledDeck.shuffle();
        List<Card> after = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            after.add(shuffledDeck.draw());
        }

        assertEquals(32, before.size());
        assertEquals(32, after.size());
        assertTrue(before.containsAll(after));
        assertNotEquals(before, after);
    }

    @Test
    void testCut() {
        Deck deck = new Deck();
        List<Card> before = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            before.add(deck.draw());
        }

        Deck cutDeck = new Deck();
        cutDeck.cut();
        List<Card> after = new ArrayList<>();
        for (int i = 0; i < 32; i++) {
            after.add(cutDeck.draw());
        }

        assertEquals(before.size(), after.size());
        assertTrue(before.containsAll(after));
        assertNotEquals(before, after);
    }

    @Test
    void testDraw() {
        Deck deck = new Deck();
        int initialCount = deck.remainingCount();

        Card card = deck.draw();

        assertNotNull(card);
        assertEquals(initialCount - 1, deck.remainingCount());
    }

    @Test
    void testDrawEmptyThrows() {
        Deck deck = new Deck();
        for (int i = 0; i < 32; i++) {
            deck.draw();
        }

        assertThrows(IllegalStateException.class, deck::draw);
    }

    @Test
    void testRemainingCount() {
        Deck deck = new Deck();
        assertEquals(32, deck.remainingCount());

        deck.draw();
        assertEquals(31, deck.remainingCount());
    }
}