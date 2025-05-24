package service;

import domain.Lokaal;
import repository.LokaalRepository;
import repository.EventRepository;
import exceptions.LokaalNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LokaalServiceImpl implements LokaalService {

    @Autowired
    private LokaalRepository lokaalRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<Lokaal> findAllLokalen() {
        return lokaalRepository.findAllByOrderByNaamAsc();
    }

    @Override
    @Transactional
    public Lokaal saveLokaal(Lokaal lokaal) {
        return lokaalRepository.save(lokaal);
    }

    @Override
    public Optional<Lokaal> findLokaalById(Long id) {
        return lokaalRepository.findById(id);
    }

    @Override
    public Lokaal findLokaalByNaam(String naam) {
        return lokaalRepository.findByNaam(naam);
    }

    @Override
    public boolean existsLokaalByNaam(String naam) {
        return lokaalRepository.existsByNaam(naam);
    }

    @Override
    @Transactional
    public void deleteLokaalById(Long id) {
        Lokaal lokaal = lokaalRepository.findById(id)
                .orElseThrow(() -> {
                    String msg = messageSource.getMessage(
                            "lokaal.notfound.id",
                            new Object[]{id},
                            LocaleContextHolder.getLocale());
                    return new LokaalNotFoundException(msg);
                });

        if (eventRepository.countByLokaal(lokaal) > 0) {
            String msg = messageSource.getMessage(
                    "lokaal.delete.error.has_linked_events",
                    null,
                    LocaleContextHolder.getLocale());
            throw new IllegalStateException(msg);
        }

        lokaalRepository.deleteById(id);
    }
}