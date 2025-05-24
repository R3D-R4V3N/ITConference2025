package service;

import domain.Speaker;

import java.util.List;
import java.util.Optional;

public interface SpeakerService {

    List<Speaker> findAllSpeakers();

    Speaker saveSpeaker(Speaker speaker);

    Optional<Speaker> findSpeakerById(Long id);

    Speaker findSpeakerByName(String name);

    boolean existsSpeakerByName(String name);
}
