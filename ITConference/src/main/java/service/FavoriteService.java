package service;

import domain.Event;
import domain.MyUser;

import java.util.List;

public interface FavoriteService {
    List<Event> findFavoriteEventsByUsername(String username);
    void addFavoriteEvent(String username, Long eventId);
    void removeFavoriteEvent(String username, Long eventId);
    boolean isEventFavoriteForUser(String username, Long eventId);
    long getNumberOfFavoriteEventsForUser(String username);

    // Nieuwe methode voor het verwijderen van alle favorieten voor een specifiek event
    void deleteFavoritesByEventId(Long eventId);
}