package bg.belot.backend.game.bidding;

import bg.belot.backend.game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class BiddingRound {
    private static final int CONSECUTIVE_PASSES_TO_END = 3;
    private static final int CONSECUTIVE_PASSES_TO_REDEAL = 4;

    private final List<Player> players;
    private final List<Announcement> announcements;

    public BiddingRound(List<Player> players) {
        this.players = players;
        this.announcements = new ArrayList<>();
    }

    public void addAnnouncement(Announcement announcement) {
        if (isFinished()) {
            throw new IllegalStateException("Bidding round is already finished");
        }
        announcements.add(announcement);
    }

    public Player getCurrentPlayer() {
        int currentPlayerIndex = announcements.size() % players.size();
        return players.get(currentPlayerIndex);
    }

    public boolean isFinished() {
        if (announcements.size() < CONSECUTIVE_PASSES_TO_END) {
            return false;
        }
        return isPass(announcements.getLast())
                && isPass(announcements.get(announcements.size() - 2))
                && isPass(announcements.get(announcements.size() - 3));
    }

    private boolean isPass(Announcement announcement) {
        if (announcements.size() < CONSECUTIVE_PASSES_TO_REDEAL) {
            return false;
        }
        return announcement.type() == AnnouncementType.PASS;
    }

    public boolean isRedeal() {
        return isPass(announcements.getLast())
                && isPass(announcements.get(announcements.size() - 2))
                && isPass(announcements.get(announcements.size() - 3))
                && isPass(announcements.get(announcements.size() - 4));
    }

    public Announcement getWinningAnnouncement() {
        if (!isFinished()) {
            throw new IllegalStateException("Bidding round is not finished yet");
        }
        if (isRedeal()) {
            throw new IllegalStateException("Bidding round ended in a redeal; there is no winning announcement");
        }
        for (int i = announcements.size() - 1; i >= 0; i--) {
            Announcement announcement = announcements.get(i);
            if (!isPass(announcement)) {
                return announcement;
            }
        }
        throw new IllegalStateException("No winning announcement found");
    }
}
