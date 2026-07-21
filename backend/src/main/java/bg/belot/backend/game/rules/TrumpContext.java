package bg.belot.backend.game.rules;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.bidding.AnnouncementType;
import bg.belot.backend.game.card.Card;

public class TrumpContext {
    public static boolean isTrump(Card card, Announcement announcement) {
        return announcement.type() == AnnouncementType.ALL_TRUMP
                || card.suit() == announcement.trumpSuit();
    }
}
