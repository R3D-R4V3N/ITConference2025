package service;

import domain.Lokaal;
import repository.LokaalRepository; // Importeer de LokaalRepository
import org.springframework.beans.factory.annotation.Autowired; // Nodig voor injectie van de repository
import org.springframework.stereotype.Service; // Markeer deze klasse als een Spring Service

import java.util.List;
import java.util.Optional;

@Service // Spring detecteert deze klasse als een component in de servicelaag
public class LokaalService {

    @Autowired // Injecteer de LokaalRepository om data-operaties uit te voeren
    private LokaalRepository lokaalRepository;

    /**
     * Haalt alle lokalen op, gesorteerd op naam.
     * @return Een lijst van Lokaal objecten.
     */
    public List<Lokaal> findAllLokalen() {
        // De repository biedt al de functionaliteit om alles te vinden en te sorteren op naam
        return lokaalRepository.findAllByOrderByNaamAsc();
    }

    /**
     * Slaat een nieuw lokaal op of werkt een bestaand lokaal bij.
     * Bean Validation wordt typisch al uitgevoerd voordat deze methode wordt aangeroepen via @Valid in de controller.
     * Extra business logica of validaties kunnen hier nog worden toegevoegd indien nodig.
     * @param lokaal Het op te slaan of bij te werken Lokaal object.
     * @return Het opgeslagen of bijgewerkte Lokaal object.
     */
    public Lokaal saveLokaal(Lokaal lokaal) {
        // Hier zouden eventuele complexere validaties kunnen plaatsvinden die niet via annotaties kunnen
        // of business regels die specifiek zijn voor het opslaan van een lokaal.
        // Voor deze opdracht volstaat het om de repository methode aan te roepen.
        return lokaalRepository.save(lokaal);
    }

    /**
     * Zoekt een lokaal op basis van het ID.
     * @param id Het ID van het te zoeken lokaal.
     * @return Een Optional met het gevonden Lokaal object, of een lege Optional als het niet gevonden is.
     */
    public Optional<Lokaal> findLokaalById(Long id) {
        return lokaalRepository.findById(id);
    }

    /**
     * Zoekt een lokaal op basis van de naam.
     * Dit kan bijvoorbeeld nuttig zijn voor validatie of om te controleren of een lokaal al bestaat.
     * @param naam De naam van het te zoeken lokaal.
     * @return Het gevonden Lokaal object, of null als het niet gevonden is.
     */
    public Lokaal findLokaalByNaam(String naam) {
        return lokaalRepository.findByNaam(naam);
    }

    /**
     * Controleert of een lokaal met een gegeven naam al bestaat in de database.
     * @param naam De naam om te controleren.
     * @return true als een lokaal met deze naam bestaat, anders false.
     */
    public boolean existsLokaalByNaam(String naam) {
        return lokaalRepository.existsByNaam(naam);
    }

    // Eventuele methoden voor het verwijderen van lokalen kunnen hier komen
    // public void deleteLokaal(Long id) { ... }
}