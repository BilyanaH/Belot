package bg.belot.backend.game;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.card.Deck;
import bg.belot.backend.game.meld.Meld;
import bg.belot.backend.game.player.Player;
import bg.belot.backend.game.player.Team;
import bg.belot.backend.game.scoring.RoundResult;
import bg.belot.backend.game.scoring.RoundScorer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Match {
    private static final int WINNING_SCORE = 151;
    private static final int REQUIRED_PLAYERS = 4;

    private final Team team1;
    private final Team team2;
    private final List<Player> players;
    private final Deck deck;

    @Getter
    private Game currentGame;
    private int hangingPoints;
    private int dealerIndex;

    public Match(Team team1, Team team2, List<Player> players) {
        this.team1 = team1;
        this.team2 = team2;
        if (players == null || players.size() != REQUIRED_PLAYERS) {
            throw new IllegalArgumentException("Players must be exactly 4");
        }
        this.players = players;
        this.deck = new Deck();
        deck.shuffle();
        dealerIndex = 0;
        hangingPoints = 0;
        startNewGame();
    }

    public void finishCurrentGame(List<Meld> team1Melds, List<Meld> team2Melds) {
        if (!currentGame.isFinished()) {
            throw new IllegalStateException("Current game is not finished yet");
        }
        if (isMatchFinished()) {
            throw new IllegalStateException("Match is already finished");
        }
        Announcement winningAnnouncement = currentGame.getWinningAnnouncement();
        Team announcingTeam = winningAnnouncement.player().getTeam();
        Team opponentTeam = announcingTeam == team1 ? team2 : team1;

        List<Meld> announcingMelds = announcingTeam == team1 ? team1Melds : team2Melds;
        List<Meld> opponentMelds = announcingTeam == team1 ? team2Melds : team1Melds;

        RoundResult result  = RoundScorer.score(announcingTeam, opponentTeam, currentGame.getTricks(),
                winningAnnouncement, currentGame.getBiddingRound().getMultiplier(), announcingMelds, opponentMelds);

        applyResult(result, announcingTeam, opponentTeam);
        collectCards();
        dealerIndex = (dealerIndex + 1) % players.size();

        if (!isMatchFinished()) {
            startNewGame();
        }
    }

    private void applyResult(RoundResult result, Team announcingTeam, Team opponentTeam) {
        switch (result.outcome()) {
            case OUT -> {
                announcingTeam.addScore(result.announcingTeamScore() + hangingPoints);
                opponentTeam.addScore(result.opponentTeamScore());
                hangingPoints = 0;
            }
            case IN -> {
                opponentTeam.addScore(result.opponentTeamScore() + hangingPoints);
                hangingPoints = 0;
            }
            case HANGING -> {
                hangingPoints += result.announcingTeamScore() + result.opponentTeamScore();
            }
        }
    }

    public boolean isMatchFinished() {
        return team1.getScore() >= WINNING_SCORE || team2.getScore() >= WINNING_SCORE;
    }

    public Team getWinner() {
        if (isMatchFinished()) {
            return team1.getScore() > WINNING_SCORE ? team1 : team2;
        }
        return null;
    }

    private void startNewGame() {
        deck.cut();
        currentGame = new Game(orderedPlayersFromDealer(), deck);
    }

    private List<Player> orderedPlayersFromDealer() {
        List<Player> orderedPlayers = new ArrayList<>();
        int index = (dealerIndex + 1) % players.size();
        while (orderedPlayers.size() != REQUIRED_PLAYERS) {
            orderedPlayers.add(players.get(index));
            index = (index + 1) % players.size();
        }
        return orderedPlayers;
    }

    private void collectCards() {
        deck.returnCards(currentGame.getCardsWonByTeam(team1));
        deck.returnCards(currentGame.getCardsWonByTeam(team2));
        deck.cut();
    }
}
