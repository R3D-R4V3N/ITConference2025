// service/FavoriteServiceImpl.java
package service;

import domain.Event;
import domain.MyUser;
import exceptions.UserNotFoundException;
import exceptions.EventNotFoundException;
import repository.FavoriteRepository;
// Verwijder de import van MyUserRepository
// import repository.MyUserRepository;
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

    private static final int MAX_FAVORITES_PER_USER = 5;

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MyUserService myUserService; // Injecteer de nieuwe service interface
    @Autowired
    private EventRepository eventRepository;

    @Override
    public List<Event> findFavoriteEventsByUsername(String username) {
        return favoriteRepository.findFavoriteEventsByUsernameOrderByDatumTijdAscNaamAsc(username);
    }

    @Override
    @Transactional
    public void addFavoriteEvent(String username, Long eventId) {
        MyUser user = myUserService.findByUsername(username); // Gebruik de service
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
            // Je moet de user opslaan, maar de myUserService heeft geen save methode, dit moet nog toegevoegd worden
            myUserService.saveUser(user); // Dit is een nieuwe methode die we moeten toevoegen in MyUserService
        }
    }

    @Override
    @Transactional
    public void removeFavoriteEvent(String username, Long eventId) {
        MyUser user = myUserService.findByUsername(username); // Gebruik de service
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
            myUserService.saveUser(user); // Dit is een nieuwe methode die we moeten toevoegen in MyUserService
        }
    }

    @Override
    public boolean isEventFavoriteForUser(String username, Long eventId) {
        return favoriteRepository.countFavoriteEventByUsernameAndEventId(username, eventId) > 0;
    }

    @Override
    public long getNumberOfFavoriteEventsForUser(String username) {
        MyUser user = myUserService.findByUsername(username); // Gebruik de service
        if (user == null) {
            return 0;
        }
        return user.getFavoriteEvents().size();
    }
}