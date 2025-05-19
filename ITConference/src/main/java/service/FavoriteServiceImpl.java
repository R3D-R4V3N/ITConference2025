// Begin creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/service/impl/FavoriteServiceImpl.java
package service;

import domain.Event;
import domain.MyUser;
import exceptions.UserNotFoundException; // We gaan deze exception later aanmaken
import exceptions.EventNotFoundException; // We gaan deze exception later aanmaken
import repository.FavoriteRepository;
import repository.MyUserRepository;
import repository.EventRepository;
import service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final int MAX_FAVORITES_PER_USER = 5; // Definieer het maximum aantal favorieten

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MyUserRepository myUserRepository;
    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> findFavoriteEventsByUsername(String username) {
        return favoriteRepository.findFavoriteEventsByUsernameOrderByDatumTijdAscNaamAsc(username);
    }

    @Override
    @Transactional
    public void addFavoriteEvent(String username, Long eventId) {
        MyUser user = myUserRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Gebruiker met gebruikersnaam " + username + " niet gevonden.");
        }

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException("Evenement met ID " + eventId + " niet gevonden.");
        }
        Event event = eventOptional.get();

        Set<Event> favoriteEvents = user.getFavoriteEvents();

        if (favoriteEvents.size() >= MAX_FAVORITES_PER_USER) {
            throw new IllegalStateException("U heeft het maximale aantal van " + MAX_FAVORITES_PER_USER + " favoriete evenementen bereikt.");
        }

        if (!favoriteEvents.contains(event)) {
            favoriteEvents.add(event);
            myUserRepository.save(user); // Sla de bijgewerkte gebruiker op
        }
    }

    @Override
    @Transactional
    public void removeFavoriteEvent(String username, Long eventId) {
        MyUser user = myUserRepository.findByUsername(username);
        if (user == null) {
            throw new UserNotFoundException("Gebruiker met gebruikersnaam " + username + " niet gevonden.");
        }

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new EventNotFoundException("Evenement met ID " + eventId + " niet gevonden.");
        }
        Event event = eventOptional.get();

        Set<Event> favoriteEvents = user.getFavoriteEvents();
        if (favoriteEvents.remove(event)) {
            myUserRepository.save(user); // Sla de bijgewerkte gebruiker op
        }
    }

    @Override
    public boolean isEventFavoriteForUser(String username, Long eventId) {
        return favoriteRepository.countFavoriteEventByUsernameAndEventId(username, eventId) > 0;
    }

    @Override
    public long getNumberOfFavoriteEventsForUser(String username) {
        MyUser user = myUserRepository.findByUsername(username);
        if (user == null) {
            return 0; // Of gooi een UserNotFoundException, afhankelijk van de gewenste afhandeling
        }
        return user.getFavoriteEvents().size();
    }
}
// Einde creatie van r3d-r4v3n/itconference2025/ITConference2025-7b1337b477e4fe2130cc11934b3ba32ccae06e35/ITConference/src/main/java/service/impl/FavoriteServiceImpl.java