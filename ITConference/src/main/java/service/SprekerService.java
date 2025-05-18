package service;

import domain.Spreker;
import repository.SprekerRepository; // Importeer de SprekerRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SprekerService {

    @Autowired
    private SprekerRepository sprekerRepository;

    /**
     * Haalt alle sprekers op, gesorteerd op naam.
     * @return Een lijst van Spreker objecten.
     */
    public List<Spreker> findAllSprekers() {
        return sprekerRepository.findAllByOrderByNaamAsc();
    }

    /**
     * Slaat een nieuwe spreker op of werkt een bestaande spreker bij.
     * @param spreker Het op te slaan of bij te werken Spreker object.
     * @return Het opgeslagen of bijgewerkte Spreker object.
     */
    @Transactional
    public Spreker saveSpreker(Spreker spreker) {
        // Hier kun je eventuele business logica voor sprekers toevoegen.
        // Zoals controleren of een spreker met dezelfde naam al bestaat,
        // hoewel de unique constraint in de database en existsByNaam in de repo dit ook afhandelen.
        return sprekerRepository.save(spreker);
    }

    /**
     * Zoekt een spreker op basis van het ID.
     * @param id Het ID van de te zoeken spreker.
     * @return Een Optional met het gevonden Spreker object, of een lege Optional als het niet gevonden is.
     */
    public Optional<Spreker> findSprekerById(Long id) {
        return sprekerRepository.findById(id);
    }


    /**
     * Zoekt een spreker op basis van de naam.
     * @param naam De naam van de te zoeken spreker.
     * @return Het gevonden Spreker object, of null als het niet gevonden is.
     */
    public Spreker findSprekerByNaam(String naam) {
        return sprekerRepository.findByNaam(naam);
    }

    /**
     * Controleert of een spreker met een gegeven naam al bestaat.
     * @param naam De naam om te controleren.
     * @return true als een spreker met deze naam bestaat, anders false.
     */
    public boolean existsSprekerByNaam(String naam) {
        return sprekerRepository.existsByNaam(naam);
    }

    // Eventuele methoden voor het verwijderen van sprekers kunnen hier komen
    // public void deleteSpreker(Long id) { ... }
}