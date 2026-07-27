package bg.belot.backend.websocket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRoomRequest {
    private String playerName;
    private String roomCode;
}