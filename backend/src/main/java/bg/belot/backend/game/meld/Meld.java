package bg.belot.backend.game.meld;

import bg.belot.backend.game.card.Card;
import bg.belot.backend.game.player.Player;

import java.util.List;

public record Meld(Player player, MeldType type, List<Card> cards) {
}
