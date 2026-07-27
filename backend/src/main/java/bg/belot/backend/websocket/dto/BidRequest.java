package bg.belot.backend.websocket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidRequest {
    private String roomCode;
    private String playerName;
    private String type;
    private String trumpSuit;
}