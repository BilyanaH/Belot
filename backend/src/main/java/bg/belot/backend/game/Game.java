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
    private final List<Player> players;
    private final Deck deck;
    @Getter
    private final BiddingRound biddingRound;
    private final List<Trick> tricks;
    private Announcement winningAnnouncement;

    public Game(List<Player> players, Deck deck) {
        this.players = players;
        this.deck = deck;
        for (Player player : players) {
            for (int i = 0; i < 8; i++) {
                player.addCard(deck.draw());
            }
        }
        this.biddingRound = new BiddingRound(players);
        this.tricks = new ArrayList<>();
    }

    public void finalizeBidding() {
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
        Trick currentTrick = getCurrentTrick();
        currentTrick.playCard(player, card);
        player.playCard(card);
    }

    public boolean isFinished() {
        return tricks.size() == 8 && tricks.getLast().isComplete();
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
}