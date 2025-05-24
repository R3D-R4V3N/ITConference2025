package service;

import domain.Event;
import domain.Room;
import domain.Speaker;
import repository.EventRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private SpeakerService speakerService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Event> findAllEvents() {
        return eventRepository.findAllByOrderByDatumTijdAsc();
    }

    @Override
    @Transactional
    public Event saveEvent(@Valid Event event) {
        if (event.getSpeakers() != null) {
            for (int i = 0; i < event.getSpeakers().size(); i++) {
                Speaker speaker = event.getSpeakers().get(i);
                if (speaker.getId() == null) {
                    Speaker existingSpeaker = speakerService.findSpeakerByName(speaker.getName());
                    if (existingSpeaker != null) {
                        event.getSpeakers().set(i, existingSpeaker);
                    } else {
                        speaker = speakerService.saveSpeaker(speaker);
                        event.getSpeakers().set(i, speaker);
                    }
                } else {
                    Optional<Speaker> managedSpeaker = speakerService.findSpeakerById(speaker.getId());
                    if (managedSpeaker.isPresent()) {
                        event.getSpeakers().set(i, managedSpeaker.get());
                    } else {
                        String msg = messageSource.getMessage(
                                "speaker.invalid_id",
                                new Object[]{speaker.getId()},
                                LocaleContextHolder.getLocale());
                        throw new IllegalArgumentException(msg);
                    }
                }
            }
        }

        if (event.getRoom() != null && event.getRoom().getId() != null) {
            roomService.findRoomById(event.getRoom().getId()).ifPresent(event::setRoom);
        }

        return eventRepository.save(event);
    }

    @Override
    public Optional<Event> findEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public List<Event> findEventsByDatumTijdAndRoom(LocalDateTime datumTijd, Room room) {
        return eventRepository.findByDatumTijdAndRoom(datumTijd, room);
    }

    @Override
    public List<Event> findEventsByNaamAndDatum(String naam, LocalDate datum) {
        return eventRepository.findByNaamAndDatum(naam, datum);
    }

    @Override
    public List<Event> findEventsByDate(LocalDate date) {
        return eventRepository.findByDatum(date);
    }

    @Override
    public List<Room> findAllRooms() {
        return roomService.findAllRooms();
    }

    @Override
    public List<Speaker> findAllSpeakers() {
        return speakerService.findAllSpeakers();
    }

    @Override
    @Transactional
    public void deleteEventById(Long id) {
        favoriteService.deleteFavoritesByEventId(id);

        eventRepository.deleteById(id);
    }
}