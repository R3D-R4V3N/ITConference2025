package service;

import domain.Event;
import domain.Room;
import domain.Speaker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventService {

    List<Event> findAllEvents();

    Event saveEvent(Event event);

    Optional<Event> findEventById(Long id);

    List<Event> findEventsByDatumTijdAndRoom(LocalDateTime datumTijd, Room room);

    List<Event> findEventsByNaamAndDatum(String naam, LocalDate datum);

    List<Event> findEventsByDate(LocalDate date);

    List<Room> findAllRooms();

    List<Speaker> findAllSpeakers();

    void deleteEventById(Long id);
}