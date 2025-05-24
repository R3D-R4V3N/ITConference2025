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

    void deleteFavoritesByEventId(Long eventId);
}