package bg.belot.backend.game.scoring;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.bidding.AnnouncementType;
import bg.belot.backend.game.meld.Meld;
import bg.belot.backend.game.player.Team;
import bg.belot.backend.game.trick.Trick;

import java.util.List;

public class RoundScorer {
    private static final int CAPOT_BONUS_POINTS = 90;
    private static final int LAST_TRICK_BONUS_POINTS = 10;
    private static final int NO_TRUMP_MULTIPLIER = 2;
    private static final float TENS_DIVISOR = 10.0f;

    public static int getRawPoints(Team team, List<Trick> tricks, Announcement announcement) {
        int points = 0;
        for (Trick trick : tricks) {
            if (team.hasPlayer(trick.getWinner())) {
                points += trick.getTotalPoints();
            }
        }
        return points;
    }

    public static int getBonusPoints(Team team, List<Trick> tricks) {
        int points = 0;
        boolean isCapot = tricks.stream().allMatch(t -> team.hasPlayer(t.getWinner()));

        if (isCapot) {
            points += CAPOT_BONUS_POINTS;
        }
        if (team.hasPlayer(tricks.getLast().getWinner())) {
            points += LAST_TRICK_BONUS_POINTS;
        }
        return points;
    }

    public static int applyMultiplier(int points, Announcement announcement) {
        return announcement.type() == AnnouncementType.NO_TRUMP ? points * NO_TRUMP_MULTIPLIER : points;
    }

    public static RoundResult score(Team announcingTeam, Team opponentTeam,
                                    List<Trick> tricks,
                                    Announcement announcement, int multiplier,
                                    List<Meld> announcingTeamMelds, List<Meld> opponentTeamMelds) {

        int announcingTeamPoints = totalPoints(announcingTeam, tricks, announcement, multiplier, announcingTeamMelds);
        int opponentTeamPoints = totalPoints(opponentTeam, tricks, announcement, multiplier, opponentTeamMelds);

        if (announcingTeamPoints > opponentTeamPoints) {
            return new RoundResult(Outcome.OUT, roundToTens(announcingTeamPoints), roundToTens(opponentTeamPoints));
        } else if (announcingTeamPoints < opponentTeamPoints) {
            return new RoundResult(Outcome.IN, 0, roundToTens(announcingTeamPoints + opponentTeamPoints));
        }
        return new RoundResult(Outcome.HANGING, 0, 0);
    }

    private static int roundToTens(int points) {
        return Math.round(points / TENS_DIVISOR);
    }

    public static int getMeldPoints(List<Meld> melds) {
        return melds.stream()
                .mapToInt(m -> m.type().getPoints())
                .sum();
    }

    private static int totalPoints(Team team, List<Trick> tricks, Announcement announcement,
                                   int multiplier, List<Meld> melds) {
        int cardPoints = getRawPoints(team, tricks, announcement) + getBonusPoints(team, tricks);
        cardPoints = applyMultiplier(cardPoints, announcement);
        cardPoints *= multiplier;

        int meldPoints = getMeldPoints(melds);

        return cardPoints + meldPoints;
    }
}
