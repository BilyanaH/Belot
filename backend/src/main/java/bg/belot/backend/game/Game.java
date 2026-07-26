package bg.belot.backend.game;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.bidding.BiddingRound;
import bg.belot.backend.game.card.Card;
import bg.belot.backend.game.card.Deck;
import bg.belot.backend.game.player.Player;
import bg.belot.backend.game.player.Team;
import bg.belot.backend.game.trick.Trick;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private static final int PLAYERS_COUNT = 4;
    private static final int CARDS_PER_PLAYER = 8;
    private static final int TOTAL_CARDS = 32;
    private static final int TOTAL_TRICKS = 8;

    private final List<Player> players;
    private final Deck deck;
    @Getter
    private final BiddingRound biddingRound;
    private final List<Trick> tricks;
    @Getter
    private Announcement winningAnnouncement;

    public Game(List<Player> players, Deck deck) {
        if (players == null || players.size() != PLAYERS_COUNT) {
            throw new IllegalArgumentException("Players must be exactly 4");
        }
        if (deck == null || deck.remainingCount() < TOTAL_CARDS) {
            throw new IllegalArgumentException("Deck must have 32 cards");
        }
        this.players = players;
        this.deck = deck;
        for (Player player : players) {
            for (int i = 0; i < CARDS_PER_PLAYER; i++) {
                player.addCard(deck.draw());
            }
        }
        this.biddingRound = new BiddingRound(players);
        this.tricks = new ArrayList<>();
    }

    public void finalizeBidding() {
        if (winningAnnouncement != null) {
            throw new IllegalStateException("Bidding already finalized");
        }
        this.winningAnnouncement = biddingRound.getWinningAnnouncement();
    }

    public Player getCurrentTrickLeader() {
        if (tricks.isEmpty()) {
            return players.getFirst();
        }
        Trick lastTrick = tricks.getLast();
        if (lastTrick.isComplete()) {
            return lastTrick.getWinner();
        }
        return lastTrick.getWinner();
    }

    public Trick getCurrentTrick() {
        if (winningAnnouncement == null) {
            throw new IllegalStateException("Bidding must be finalized before playing tricks");
        }
        if (tricks.isEmpty() || tricks.getLast().isComplete()) {
            Player leadPlayer = tricks.isEmpty() ? players.getFirst() : tricks.getLast().getWinner();
            tricks.add(new Trick(winningAnnouncement, leadPlayer));
        }
        return tricks.getLast();
    }

    public void playCard(Player player, Card card) {
        if (winningAnnouncement == null) {
            throw new IllegalStateException("Bidding must be finalized before playing cards");
        }
        if (isFinished()) {
            throw new IllegalStateException("Game is already finished");
        }
        Trick currentTrick = getCurrentTrick();
        currentTrick.playCard(player, card);
        player.playCard(card);
    }

    public boolean isFinished() {
        return tricks.size() == TOTAL_TRICKS && tricks.getLast().isComplete();
    }

    public List<Card> getCardsWonByTeam(Team team) {
        List<Card> won = new ArrayList<>();

        for (Trick trick : tricks) {

            if (trick.isComplete() && team.hasPlayer(trick.getWinner())) {
                won.addAll(trick.getPlayedCards().values());
            }
        }
        return won;
    }

    public List<Trick> getTricks() {
        return List.copyOf(tricks);
    }
}