package bg.belot.backend.room;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomManager {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom() {
        Room room = new Room();
        rooms.put(room.getRoomCode(), room);
        return room;
    }

    public Room getRoom(String roomCode) {
        Room room = rooms.get(roomCode);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomCode);
        }
        return room;
    }

    public void removeRoom(String roomCode) {
        rooms.remove(roomCode);
    }
}
