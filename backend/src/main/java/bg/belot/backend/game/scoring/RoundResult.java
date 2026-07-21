package bg.belot.backend.game.scoring;

public record RoundResult(Outcome outcome, int announcingTeamScore, int opponentTeamScore) {
}
