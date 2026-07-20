package bg.belot.backend.game.bidding;

import bg.belot.backend.game.card.Suit;
import bg.belot.backend.game.player.Player;

public record Announcement(Player player, AnnouncementType type, Suit trumpSuit) {
    public Announcement {
        if (type == AnnouncementType.SUIT_TRUMP && trumpSuit == null) {
            throw new IllegalArgumentException("SUIT_TRUMP announcement requires a trump suit");
        }
        if (type != AnnouncementType.SUIT_TRUMP && trumpSuit != null) {
            throw new IllegalArgumentException("Only SUIT_TRUMP announcements can have a trump suit");
        }
    }

    public Announcement(Player player, AnnouncementType type) {
        this(player, type, null);
    }
}
