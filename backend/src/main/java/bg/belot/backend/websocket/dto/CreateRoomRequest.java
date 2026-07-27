package bg.belot.backend.websocket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoomRequest {
    private String playerName;
}