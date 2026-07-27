package bg.belot.backend.room;

import bg.belot.backend.game.Match;
import bg.belot.backend.game.player.Player;
import bg.belot.backend.game.player.Team;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {
    private static final int MAX_PLAYERS = 4;

    @Getter
    private final String roomCode;
    @Getter
    private final  List<Player> players;
    @Getter
    private Match match;

    public Room() {
        this.roomCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.players = new ArrayList<>();
    }

    public boolean isFull() {
        return players.size() == MAX_PLAYERS;
    }

    public void addPlayer(Player player){
        if (isFull()) {
            throw new IllegalStateException("Room is full");
        }
        players.add(player);
    }

    public void startMatch() {
        if (!isFull()) {
            throw new IllegalStateException("Room must have 4 players to start");
        }
        Team team1 = new Team(players.get(0), players.get(2));
        Team team2 = new Team(players.get(1), players.get(3));
        match = new Match(team1, team2, players);
    }
}
