package bg.belot.backend.websocket;

import bg.belot.backend.game.bidding.Announcement;
import bg.belot.backend.game.bidding.AnnouncementType;
import bg.belot.backend.game.card.Card;
import bg.belot.backend.game.card.Rank;
import bg.belot.backend.game.card.Suit;
import bg.belot.backend.game.player.Player;
import bg.belot.backend.room.Room;
import bg.belot.backend.room.RoomManager;
import bg.belot.backend.websocket.dto.BidRequest;
import bg.belot.backend.websocket.dto.CreateRoomRequest;
import bg.belot.backend.websocket.dto.JoinRoomRequest;
import bg.belot.backend.websocket.dto.PlayCardRequest;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class GameController {
    private static final int PLAYERS_COUNT = 4;

    private final RoomManager roomManager;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(SimpMessagingTemplate messagingTemplate) {
        this.roomManager = new RoomManager();
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/room/create")
    public void createRoom(CreateRoomRequest request) {
        Room room = roomManager.createRoom();
        Player player = new Player(request.getPlayerName());
        room.addPlayer(player);

        sendToRoom(room.getRoomCode(), Map.of(
                "type", "ROOM_CREATED",
                "roomCode", room.getRoomCode(),
                "players", room.getPlayers().stream().map(Player::getName).toList()
        ));
    }

    @MessageMapping("/room/join")
    public void joinRoom(JoinRoomRequest request) {
        Room room = roomManager.getRoom(request.getRoomCode());
        Player player = new Player(request.getPlayerName());
        room.addPlayer(player);

        if (room.isFull()) {
            room.startMatch();
            sendGameState(room);
        } else {
            sendToRoom(room.getRoomCode(), Map.of(
                    "type", "PLAYER_JOINED",
                    "players", room.getPlayers().stream().map(Player::getName).toList()
            ));
        }
    }

    @MessageMapping("/game/bid")
    public void placeBid(BidRequest request) {
        Room room = roomManager.getRoom(request.getRoomCode());
        Player player = findPlayer(room, request.getPlayerName());

        AnnouncementType type = AnnouncementType.valueOf(request.getType());
        Suit trumpSuit = request.getTrumpSuit() != null ? Suit.valueOf(request.getTrumpSuit()) : null;

        Announcement announcement = type == AnnouncementType.SUIT_TRUMP
                ? new Announcement(player, type, trumpSuit)
                : new Announcement(player, type);

        room.getMatch().getCurrentGame().getBiddingRound().addAnnouncement(announcement);

        if (room.getMatch().getCurrentGame().getBiddingRound().isFinished()) {
            room.getMatch().getCurrentGame().finalizeBidding();
        }

        sendGameState(room);
    }

    @MessageMapping("/game/playCard")
    public void playCard(PlayCardRequest request) {
        Room room = roomManager.getRoom(request.getRoomCode());
        Player player = findPlayer(room, request.getPlayerName());
        Card card = new Card(Suit.valueOf(request.getSuit()), Rank.valueOf(request.getRank()));

        room.getMatch().getCurrentGame().playCard(player, card);
        sendGameState(room);
    }

    private Player findPlayer(Room room, String playerName) {
        return room.getPlayers().stream()
                .filter(p -> p.getName().equals(playerName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + playerName));
    }

    private void sendGameState(Room room) {
        sendToRoom(room.getRoomCode(), Map.of(
                "type", "GAME_STATE",
                "players", room.getPlayers().stream().map(Player::getName).toList(),
                "currentPlayer", room.getMatch().getCurrentGame().getCurrentTrick()
                        .getPlayedCards().size() < PLAYERS_COUNT
                        ? "waiting" : "complete"
        ));
    }

    private void sendToRoom(String roomCode, Object message) {
        messagingTemplate.convertAndSend("/topic/room/" + roomCode, message);
    }
}
