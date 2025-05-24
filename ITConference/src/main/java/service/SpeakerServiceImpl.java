package service;

import domain.Speaker;
import repository.SpeakerRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpeakerServiceImpl implements SpeakerService {

    @Autowired
    private SpeakerRepository speakerRepository;

    @Override
    public List<Speaker> findAllSpeakers() {
        return speakerRepository.findAllByOrderByNameAsc();
    }

    @Override
    @Transactional
    public Speaker saveSpeaker(Speaker speaker) {
        return speakerRepository.save(speaker);
    }

    @Override
    public Optional<Speaker> findSpeakerById(Long id) {
        return speakerRepository.findById(id);
    }

    @Override
    public Speaker findSpeakerByName(String name) {
        return speakerRepository.findByName(name);
    }

    @Override
    public boolean existsSpeakerByName(String name) {
        return speakerRepository.existsByName(name);
    }
}
