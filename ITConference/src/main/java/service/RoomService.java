package service;

import domain.Room;

import java.util.List;
import java.util.Optional;

public interface RoomService {

    List<Room> findAllRooms();

    Room saveRoom(Room room);

    Optional<Room> findRoomById(Long id);

    Room findRoomByName(String name);

    boolean existsRoomByName(String name);

    void deleteRoomById(Long id);
}
