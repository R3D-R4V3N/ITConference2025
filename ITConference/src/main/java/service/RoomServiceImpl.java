package service;

import domain.Room;
import repository.RoomRepository;
import repository.EventRepository;
import exceptions.RoomNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Room> findAllRooms() {
        return roomRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional
    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Optional<Room> findRoomById(Long id) {
        return roomRepository.findById(id);
    }

    @Override
    public Room findRoomByName(String name) {
        return roomRepository.findByName(name);
    }

    @Override
    public boolean existsRoomByName(String name) {
        return roomRepository.existsByName(name);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage(
                            "lokaal.notfound.id",
                            new Object[]{id},
                            LocaleContextHolder.getLocale());
                    return new RoomNotFoundException(msg);
                });

        if (eventRepository.countByRoom(room) > 0) {
            String msg = messageSource.getMessage(
                    "lokaal.delete.error.has_linked_events",
                    null,
                    LocaleContextHolder.getLocale());
            throw new IllegalStateException(msg);
        }

        roomRepository.deleteById(id);
    }
}