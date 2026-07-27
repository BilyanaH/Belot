package bg.belot.backend.websocket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayCardRequest {
    private String roomCode;
    private String playerName;
    private String suit;
    private String rank;
}