package service;

import domain.Event;
import domain.MyUser;
import exceptions.UserNotFoundException;
import exceptions.EventNotFoundException;
import repository.FavoriteRepository;
import repository.EventRepository;
import service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private static final int MAX_FAVORITES_PER_USER = 5;

    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MyUserService myUserService;
    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Event> findFavoriteEventsByUsername(String username) {
        return favoriteRepository.findFavoriteEventsByUsernameOrderByDatumTijdAscNaamAsc(username);
    }

    @Override
    @Transactional
    public void addFavoriteEvent(String username, Long eventId) {
        MyUser user = myUserService.findByUsername(username);
        if (user == null) {
            String msg = messageSource.getMessage(
                    "user.notfound",
                    new Object[]{username},
                    LocaleContextHolder.getLocale());
            throw new UserNotFoundException(msg);
        }

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            String msg = messageSource.getMessage(
                    "event.notfound",
                    new Object[]{eventId},
                    LocaleContextHolder.getLocale());
            throw new EventNotFoundException(msg);
        }
        Event event = eventOptional.get();

        Set<Event> favoriteEvents = user.getFavoriteEvents();

        if (favoriteEvents.size() >= MAX_FAVORITES_PER_USER) {
            String msg = messageSource.getMessage(
                    "favorite.max_reached",
                    new Object[]{MAX_FAVORITES_PER_USER},
                    LocaleContextHolder.getLocale());
            throw new IllegalStateException(msg);
        }

        if (!favoriteEvents.contains(event)) {
            favoriteEvents.add(event);
            myUserService.saveUser(user);
        }
    }

    @Override
    @Transactional
    public void removeFavoriteEvent(String username, Long eventId) {
        MyUser user = myUserService.findByUsername(username);
        if (user == null) {
            String msg = messageSource.getMessage(
                    "user.notfound",
                    new Object[]{username},
                    LocaleContextHolder.getLocale());
            throw new UserNotFoundException(msg);
        }

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            String msg = messageSource.getMessage(
                    "event.notfound",
                    new Object[]{eventId},
                    LocaleContextHolder.getLocale());
            throw new EventNotFoundException(msg);
        }
        Event event = eventOptional.get();

        Set<Event> favoriteEvents = user.getFavoriteEvents();
        if (favoriteEvents.remove(event)) {
            myUserService.saveUser(user);
        }
    }

    @Override
    public boolean isEventFavoriteForUser(String username, Long eventId) {
        return favoriteRepository.countFavoriteEventByUsernameAndEventId(username, eventId) > 0;
    }

    @Override
    public long getNumberOfFavoriteEventsForUser(String username) {
        MyUser user = myUserService.findByUsername(username);
        if (user == null) {
            return 0;
        }
        return user.getFavoriteEvents().size();
    }

    @Override
    @Transactional
    public void deleteFavoritesByEventId(Long eventId) {
        List<MyUser> usersWithFavoriteEvent = favoriteRepository.findUsersByFavoriteEventId(eventId);

        for (MyUser user : usersWithFavoriteEvent) {
            user.getFavoriteEvents().removeIf(event -> event.getId().equals(eventId));
            myUserService.saveUser(user);
        }
    }
}