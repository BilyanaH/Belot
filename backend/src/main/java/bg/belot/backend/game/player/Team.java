package bg.belot.backend.game.player;

import lombok.Getter;

import java.util.List;

public class Team {
    private final Player player1;
    private final Player player2;
    @Getter
    private int score;

    public Team(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public List<Player> getPlayers() {
        return List.of(player1, player2);
    }

    public void addScore(int points) {
        score += points;
    }

    public boolean hasPlayer(Player player) {
        return player.equals(player1) || player.equals(player2);
    }
}