# Overzicht van Annotaties in ITConference Project

Dit document biedt een uitgebreide samenvatting van de belangrijkste Java- en Spring-annotaties die zijn gebruikt in het `ITConference` project. Voor elke annotatie wordt de betekenis uitgelegd en wordt beschreven hoe deze specifiek in dit project is toegepast. Dit overzicht is bedoeld om te dienen als leidraad voor het begrijpen en uitleggen van de projectstructuur en -functionaliteit.

---

## 1. Spring Core & Boot Annotaties

Deze annotaties vormen de basis van elke Spring Boot applicatie en zorgen voor de configuratie en component scanning.

### `@SpringBootApplication`
* **Betekenis**: Een gemakkelijke manier om een Spring Boot applicatie te starten. Het is een combinatie van `@Configuration`, `@EnableAutoConfiguration` en `@ComponentScan`.
* **Gebruik in project**: Toegepast op `ItConferenceApplication.java`. Dit markeert de hoofdklasse van de applicatie en schakelt automatische configuratie, component scanning en het definiëren van beans in.

### `@Configuration`
* **Betekenis**: Geeft aan dat een klasse een bron van bean-definities is. Spring IoC-container kan deze klassen verwerken en de gedefinieerde beans genereren.
* **Gebruik in project**: Toegepast op `SecurityConfig.java`, wat aangeeft dat deze klasse Spring Security-gerelateerde beans configureert.

### `@ComponentScan`
* **Betekenis**: Configureert Spring om automatisch componenten, configuraties en services te detecteren in de opgegeven pakketten.
* **Gebruik in project**: Toegepast op `ItConferenceApplication.java`. Het specificeert de basispakketten (`com.hogent.ewdj.itconference`, `domain`, `exceptions`, `perform`, `repository`, `service`, `validator`) die Spring moet scannen om alle componenten (controllers, services, repositories, etc.) te vinden.

### `@Autowired`
* **Betekenis**: Voert automatische dependency injection uit. Spring zoekt naar een bean van het vereiste type en injecteert deze. Kan gebruikt worden op velden, constructors of setter-methoden.
* **Gebruik in project**: Overal toegepast om afhankelijkheden te injecteren, zoals repositories in services, services in controllers, en de `PasswordEncoder` in `InitDataConfig`. Voorbeelden zijn te vinden in `EventController.java` (injecteert services), `EventServiceImpl.java` (injecteert repositories en andere services) en `InitDataConfig.java` (injecteert services en `PasswordEncoder`).

### `@Bean`
* **Betekenis**: Een methode-level annotatie die aangeeft dat een methode een bean produceert die beheerd moet worden door de Spring IoC-container.
* **Gebruik in project**:
    * In `ItConferenceApplication.java` om `PasswordEncoder` en `LocaleResolver` beans te definiëren.
    * In `SecurityConfig.java` om de `SecurityFilterChain` bean te definiëren.

### `@Component`
* **Betekenis**: Een generieke stereotype-annotatie die aangeeft dat een Java-klasse een "component" is van de Spring-applicatie, wat betekent dat het een bean is die automatisch wordt gedetecteerd door component scanning.
* **Gebruik in project**: Toegepast op `InitDataConfig.java`, `MyUserDetailsService.java`, `PerformRestITConference.java` en validatoren zoals `EventConstraintsValidator.java`. Dit zorgt ervoor dat deze klassen door Spring worden beheerd en hun functionaliteit kunnen uitvoeren.

### `@Order`
* **Betekenis**: Geeft de volgorde van de uitvoering van componenten aan wanneer meerdere componenten van hetzelfde type aanwezig zijn (bijv. meerdere `CommandLineRunner`s). Een lagere waarde betekent een hogere prioriteit.
* **Gebruik in project**: Toegepast op `InitDataConfig.java` (`@Order(1)`), wat ervoor zorgt dat de data-initialisatie plaatsvindt voordat andere `CommandLineRunner`s (indien aanwezig) worden uitgevoerd.

---

## 2. Spring MVC Annotaties

Deze annotaties worden gebruikt voor het bouwen van webapplicaties met Spring MVC.

### `@Controller`
* **Betekenis**: Geeft aan dat een klasse een Spring MVC controller is. Het verwerkt inkomende webverzoeken en retourneert een viewnaam of data.
* **Gebruik in project**: Toegepast op alle controllerklassen zoals `EventController.java`, `LokaalController.java`, `FavoriteController.java`, `LoginController.java`, `LocaleController.java`, en `CustomErrorController.java`.

### `@RequestMapping`
* **Betekenis**: Mapt webverzoeken naar specifieke handler-methoden of klassen. Kan worden gebruikt op klasse- of methodeniveau om een basis-URL of specifieke paden te definiëren.
* **Gebruik in project**:
    * Op klasseniveau, bijv. `@RequestMapping("/events")` in `EventController.java`, om alle methoden in die controller te prefixen met `/events`.
    * Op methodeniveau in combinatie met `@GetMapping`, `@PostMapping` etc.

### `@GetMapping`
* **Betekenis**: Een gespecialiseerde versie van `@RequestMapping` die de HTTP GET-methode mapt.
* **Gebruik in project**: Overal gebruikt voor methoden die data ophalen en views renderen, zoals `showEventOverview()` in `EventController.java` (`@GetMapping`).

### `@PostMapping`
* **Betekenis**: Een gespecialiseerde versie van `@RequestMapping` die de HTTP POST-methode mapt.
* **Gebruik in project**: Overal gebruikt voor methoden die data verzenden of wijzigen (bijv. formulierinzendingen), zoals `processAddEventForm()` in `EventController.java` (`@PostMapping("/add")`).

### `@PathVariable`
* **Betekenis**: Bindt een variabele uit de URI-pad naar een methode-parameter.
* **Gebruik in project**: Gebruikt in methoden zoals `showEventDetail(@PathVariable("id") Long id)` in `EventController.java` om het ID van een event uit de URL te halen.

### `@RequestParam`
* **Betekenis**: Bindt een webverzoekparameter aan een methode-parameter.
* **Gebruik in project**: Gebruikt in methoden zoals `addFavoriteEvent(@RequestParam("eventId") Long eventId)` in `FavoriteController.java` om een event-ID uit een URL-queryparameter te halen. Ook gebruikt in `LoginController` voor `error` en `logout` parameters.

### `@ModelAttribute`
* **Betekenis**: Bindt de gegevens van een webformulier aan een Java-object. Het kan ook worden gebruikt om een attribuut toe te voegen aan het model.
* **Gebruik in project**: Gebruikt in `EventController.java` en `LokaalController.java` in methoden zoals `processAddEventForm(@ModelAttribute("event") Event event)` om formuliergegevens te binden aan het `Event` of `Lokaal` object.

### `@RestController`
* **Betekenis**: Een combinatie van `@Controller` en `@ResponseBody`. Het geeft aan dat de controller RESTful web services bouwt en dat de methoden direct data (bijv. JSON of XML) retourneren in plaats van viewnamen.
* **Gebruik in project**: Toegepast op `ITConferenceRestController.java`, waardoor de methoden daarin direct JSON-responses retourneren.

### `@ResponseBody`
* **Betekenis**: Geeft aan dat het retourtype van een methode direct moet worden gebonden aan de body van de webrespons. Dit wordt automatisch geïmpliceerd door `@RestController`.
* **Gebruik in project**: Impliciet gebruikt via `@RestController` in `ITConferenceRestController.java`.

### `@RequestBody`
* **Betekenis**: Bindt de body van het webverzoek (bijv. JSON of XML) aan een Java-object.
* **Gebruik in project**: Hoewel niet direct gebruikt in de geanalyseerde REST GET-endpoints in dit project, is het concept relevant voor REST APIs die data ontvangen in de request body (bijv. voor POST/PUT operaties).

### `@ControllerAdvice`
* **Betekenis**: Een specialisatie van `@Component` die `@ExceptionHandler`, `@InitBinder` of `@ModelAttribute` methoden overal toepasbaar maakt, over meerdere controllers heen.
* **Gebruik in project**: Toegepast op `ITConferenceErrorAdvice.java` voor centrale foutafhandeling in MVC-controllers en op `ITConferenceRestErrorAdvice.java` voor centrale foutafhandeling in REST-controllers.

### `@ExceptionHandler`
* **Betekenis**: Methode-level annotatie die aangeeft dat de methode uitzonderingen van een specifiek type afhandelt.
* **Gebruik in project**: Gebruikt in `ITConferenceErrorAdvice.java` en `ITConferenceRestErrorAdvice.java` om specifieke uitzonderingen (`EventNotFoundException`, `LokaalNotFoundException`, `UserNotFoundException`, `IllegalStateException`, etc.) op te vangen en een passende foutrespons te genereren.

### `@ResponseStatus`
* **Betekenis**: Configureert de HTTP-statuscode die moet worden geretourneerd wanneer een methode (of een uitzondering die wordt afgehandeld door `@ExceptionHandler`) wordt aangeroepen.
* **Gebruik in project**: Gebruikt in `ITConferenceRestErrorAdvice.java` in combinatie met `@ExceptionHandler` om de juiste HTTP-statuscode (bijv. `HttpStatus.NOT_FOUND`, `HttpStatus.BAD_REQUEST`) te retourneren voor REST API-fouten.

### `WebMvcConfigurer`
* **Betekenis**: Een interface die callbacks biedt om de Spring MVC-configuratie aan te passen.
* **Gebruik in project**: Geïmplementeerd door `ItConferenceApplication.java` om methoden zoals `addViewControllers()` en `addInterceptors()` aan te passen.

### `ViewControllerRegistry`
* **Betekenis**: Gebruikt om eenvoudige, statische view controllers te registreren, zonder dat een `@Controller` klasse nodig is.
* **Gebruik in project**: Gebruikt in `ItConferenceApplication.java` (`addViewControllers()` methode) om een redirect van de root URL (`/`) naar `/events` te configureren.

### `InterceptorRegistry`
* **Betekenis**: Gebruikt om `HandlerInterceptor`s te registreren, die webverzoeken kunnen onderscheppen voor pre-processing en post-processing.
* **Gebruik in project**: Gebruikt in `ItConferenceApplication.java` (`addInterceptors()` methode) om de `LocaleChangeInterceptor` te registreren voor taalwisseling.

---

## 3. Spring Data JPA Annotaties

Deze annotaties worden gebruikt voor object-relationele mapping (ORM) en data-access lagen met JPA.

### `@Entity`
* **Betekenis**: Markert een Java-klasse als een JPA-entiteit, wat betekent dat het een tabel in de database vertegenwoordigt.
* **Gebruik in project**: Toegepast op `Event.java`, `Lokaal.java`, `Spreker.java` en `MyUser.java`.

### `@Table`
* **Betekenis**: Specificeert de naam van de databasetabel waarnaar de entiteit moet worden gemapt. Optioneel als de tabelnaam overeenkomt met de klassenaam.
* **Gebruik in project**: Toegepast op `Event.java` (`@Table(name = "events")`), `Lokaal.java` (`@Table(name = "lokalen")`), `Spreker.java` (`@Table(name = "sprekers")`) en `MyUser.java` (`@Table(name = "users")`).

### `@Id`
* **Betekenis**: Markert het primaire sleutelveld van een entiteit.
* **Gebruik in project**: Toegepast op het `id`-veld in alle domein-entiteiten (`Event`, `Lokaal`, `Spreker`, `MyUser`).

### `@GeneratedValue`
* **Betekenis**: Specificeert dat de waarde van de primaire sleutel automatisch door de database wordt gegenereerd. `GenerationType.IDENTITY` gebruikt de auto-increment kolom van de database.
* **Gebruik in project**: Toegepast op het `id`-veld in alle domein-entiteiten (`Event`, `Lokaal`, `Spreker`, `MyUser`).

### `@Column`
* **Betekenis**: Specificeert de mapping van een entiteitsveld naar een kolom in de databasetabel. Attributen zoals `nullable` en `unique` kunnen worden ingesteld.
* **Gebruik in project**: Overal gebruikt op velden in entiteiten om kolomdetails te definiëren (bijv. `nullable = false`, `unique = true`, `length`).

### `@Lob`
* **Betekenis**: Geeft aan dat een persistent veld of property moet worden opgeslagen als een large object (LOB) in de database, typisch voor grote strings of binaire data.
* **Gebruik in project**: Toegepast op het `beschrijving`-veld in `Event.java`.

### `@Transient`
* **Betekenis**: Markert een veld dat niet persistent moet worden gemaakt in de database. Het wordt genegeerd door JPA.
* **Gebruik in project**: Toegepast op het `beamercheck`-veld in `Event.java` omdat dit een berekend veld is en niet direct in de database wordt opgeslagen.

### `@ManyToOne`
* **Betekenis**: Definieert een veel-op-één relatie tussen entiteiten (bijv. veel events kunnen aan één lokaal worden toegewezen).
* **Gebruik in project**: Toegepast op het `lokaal`-veld in `Event.java` om de relatie met `Lokaal` aan te geven. `WorkspaceType.LAZY` wordt gebruikt voor lazy loading.

### `@OneToMany`
* **Betekenis**: Definieert een één-op-veel relatie tussen entiteiten. Dit is de inverse zijde van een `@ManyToOne` relatie.
* **Gebruik in project**: Niet expliciet gedefinieerd in dit project (relatie is unidirectioneel of ManyToOne/ManyToMany owning side).

### `@ManyToMany`
* **Betekenis**: Definieert een veel-op-veel relatie tussen entiteiten (bijv. veel events kunnen meerdere sprekers hebben, en een spreker kan in meerdere events spreken).
* **Gebruik in project**:
    * Toegepast op het `sprekers`-veld in `Event.java` (owning side) en `mappedBy = "sprekers"` op het `events`-veld in `Spreker.java` (inverse side).
    * Toegepast op het `favoriteEvents`-veld in `MyUser.java` voor de veel-op-veel relatie met `Event`.

### `@JoinColumn`
* **Betekenis**: Specificeert de foreign key-kolom voor een relatie.
* **Gebruik in project**: Gebruikt met `@ManyToOne` in `Event.java` (`lokaalId`) en binnen `@JoinTable` voor `@ManyToMany` relaties.

### `@JoinTable`
* **Betekenis**: Definieert een tussentabel voor `@ManyToMany` relaties.
* **Gebruik in project**: Gebruikt voor de `event_spreker` tabel in `Event.java` en voor de `user_favorite_events` tabel in `MyUser.java`.

### `WorkspaceType.LAZY` / `WorkspaceType.EAGER`
* **Betekenis**: Bepaalt wanneer gerelateerde entiteitsgegevens moeten worden geladen. `LAZY` (standaard voor `@OneToMany` en `@ManyToMany`) laadt data alleen wanneer deze wordt opgevraagd. `EAGER` (standaard voor `@ManyToOne` en `@OneToOne`) laadt data direct mee.
* **Gebruik in project**: Expliciet ingesteld op `WorkspaceType.LAZY` voor het `lokaal`-veld in `Event.java` en impliciet voor `ManyToMany` relaties, wat efficiënt is.

### `@Enumerated`
* **Betekenis**: Specificeert hoe een Java `enum` type moet worden gemapt aan een databasetabel. `EnumType.STRING` (aanbevolen) slaat de naam van de enum-waarde op.
* **Gebruik in project**: Toegepast op het `role`-veld in `MyUser.java` (`@Enumerated(EnumType.STRING)`).

### `JpaRepository` / `CrudRepository`
* **Betekenis**: Interfaces van Spring Data JPA die basis CRUD-operaties bieden voor entiteiten. `JpaRepository` breidt `CrudRepository` uit en voegt meer JPA-specifieke functionaliteiten toe (zoals paginering en batchverwijdering).
* **Gebruik in project**: Alle repository-interfaces (`EventRepository`, `LokaalRepository`, `SprekerRepository`, `MyUserRepository`, `FavoriteRepository`) extenden `JpaRepository`, waardoor automatisch een groot aantal data-access methoden beschikbaar is.

### `@Query`
* **Betekenis**: Gebruikt om aangepaste JPQL- (Java Persistence Query Language) of native SQL-queries direct in de repository-interface te definiëren.
* **Gebruik in project**: Gebruikt in `EventRepository.java` en `FavoriteRepository.java` voor complexere queries zoals het vinden van events op naam en datum, of het ophalen van favoriete events met specifieke sorteerregels.

### `@Param`
* **Betekenis**: Bindt een methode-parameter aan een benoemde parameter in een `@Query`-annotatie.
* **Gebruik in project**: Gebruikt in `@Query` methoden in `EventRepository.java` en `FavoriteRepository.java` om waarden door te geven aan de gedefinieerde JPQL-queries.

---

## 4. Validatie & Formattering Annotaties (Jakarta Validation)

Deze annotaties worden gebruikt voor het valideren van inputgegevens en het formatteren van velden.

### `@Valid`
* **Betekenis**: Activeert de validatie van een object of methode-parameter volgens de Bean Validation-regels die op de klasse zijn gedefinieerd.
* **Gebruik in project**: Gebruikt in controller-methoden zoals `processAddEventForm()` in `EventController.java` (`@Valid Event event`) om de validatie van het `Event`-object te triggeren.

### `@Validated`
* **Betekenis**: Een Spring-specifieke variant van `@Valid` die naast objectvalidatie ook groepsvalidatie mogelijk maakt en op klasniveau kan worden gebruikt om validatie op alle methoden te activeren.
* **Gebruik in project**: Gebruikt in `EventController.java` (`@Validated @ModelAttribute("event") Event event`).

### `@NotBlank`
* **Betekenis**: Valideert dat een String-veld niet `null` is en ten minste één niet-whitespace karakter bevat.
* **Gebruik in project**: Gebruikt op `naam` velden in `Event.java`, `Lokaal.java`, `Spreker.java`.

### `@Pattern`
* **Betekenis**: Valideert dat een String-veld voldoet aan een opgegeven reguliere expressie.
* **Gebruik in project**: Gebruikt op het `naam`-veld in `Event.java` (moet beginnen met een letter) en `Lokaal.java` (moet beginnen met een letter gevolgd door 3 cijfers).

### `@NotNull`
* **Betekenis**: Valideert dat een veld niet `null` is.
* **Gebruik in project**: Gebruikt op `lokaal`, `datumTijd`, `prijs` in `Event.java` en `capaciteit` in `Lokaal.java`.

### `@Size`
* **Betekenis**: Valideert dat de grootte van een collectie, array of String binnen een gespecificeerd bereik valt.
* **Gebruik in project**: Gebruikt op het `sprekers`-veld in `Event.java` (`min = 1, max = 3`).

### `@Min` / `@Max`
* **Betekenis**: Valideert dat een numeriek veld (of een numerieke representatie) groter dan of gelijk is aan (`@Min`) of kleiner dan of gelijk is aan (`@Max`) een opgegeven waarde.
* **Gebruik in project**: Gebruikt op `beamercode` in `Event.java` en `capaciteit` in `Lokaal.java`.

### `@DecimalMin` / `@DecimalMax`
* **Betekenis**: Valideert dat een numeriek veld (typisch `BigDecimal`) groter dan of gelijk is aan (`@DecimalMin`) of kleiner dan of gelijk is aan (`@DecimalMax`) een opgegeven decimale waarde.
* **Gebruik in project**: Gebruikt op `prijs` in `Event.java` om het bereik te valideren.

### `@FutureOrPresent`
* **Betekenis**: Valideert dat een datum- of tijdveld in de toekomst of het heden ligt.
* **Gebruik in project**: Gebruikt op `datumTijd` in `Event.java`.

### `@DateTimeFormat`
* **Betekenis**: Formatteert `java.util.Date`, `java.util.Calendar`, `java.time.LocalDate`, `java.time.LocalDateTime` en andere datum/tijd-types.
* **Gebruik in project**: Gebruikt op `datumTijd` in `Event.java` (`pattern = "yyyy-MM-dd'T'HH:mm"`) voor webformulieren, en in `ITConferenceRestController.java` (`iso = DateTimeFormat.ISO.DATE`) voor REST request parameters.

### `@NumberFormat`
* **Betekenis**: Formatteert numerieke velden (bijv. `BigDecimal`, `Double`, `Integer`).
* **Gebruik in project**: Gebruikt op `prijs` in `Event.java` (`style = NumberFormat.Style.CURRENCY`) om een valutateken en twee decimalen af te dwingen.

### Custom Validatie Annotaties (`@ValidBeamerCheck`, `@ValidConferenceDate`, `@ValidEventConstraints`, `@ValidSpeakerList`)
* **Betekenis**: Deze annotaties zijn door jezelf gedefinieerd om complexe, applicatie-specifieke validatieregels uit te voeren die niet worden gedekt door de standaard Bean Validation annotaties. Ze zijn gekoppeld aan een `ConstraintValidator` implementatie.
* **Gebruik in project**:
    * `@ValidBeamerCheck`: Voor het valideren van de beamercode checksum in `Event.java`.
    * `@ValidConferenceDate`: Voor het valideren of een eventdatum binnen een vooraf gedefinieerde conferentieperiode valt in `Event.java`.
    * `@ValidEventConstraints`: Voor het valideren van unieke event-tijd/lokaal combinaties en unieke event-namen op dezelfde dag in `Event.java`.
    * `@ValidSpeakerList`: Voor het valideren dat er geen dubbele sprekers in de lijst van sprekers van een event voorkomen in `Event.java`.

---

## 5. Spring Security Annotaties

Deze annotaties worden gebruikt voor authenticatie en autorisatie.

### `@EnableWebSecurity`
* **Betekenis**: Schakelt Spring Security's webbeveiligingsintegratie in, wat leidt tot het aanmaken van een `FilterChainProxy` bean.
* **Gebruik in project**: Toegepast op `SecurityConfig.java` om de Spring Security configuratie te activeren.

### `@EnableMethodSecurity`
* **Betekenis**: Schakelt methode-level beveiliging in met behulp van annotaties zoals `@PreAuthorize`.
* **Gebruik in project**: Toegepast op `SecurityConfig.java`, waardoor `@PreAuthorize`-annotaties in controllers kunnen worden gebruikt.

### `@PreAuthorize`
* **Betekenis**: Een methode-level autorisatie-annotatie die een Spring Expression Language (SpEL) uitdrukking evalueert *voordat* de methode wordt uitgevoerd. Als de expressie `false` is, wordt de methode-aanroep geweigerd.
* **Gebruik in project**: Gebruikt in `EventController.java` en `LokaalController.java` op methoden zoals `showAddEventForm()` (`@PreAuthorize("hasRole('ADMIN')")`) om ervoor te zorgen dat alleen gebruikers met de `ADMIN`-rol toegang hebben tot administratieve functionaliteiten.

---

## 6. Lombok Annotaties

Deze annotaties van de Project Lombok-bibliotheek verminderen boilerplate code.

### `@Data`
* **Betekenis**: Een handige snelkoppeling voor `@ToString`, `@EqualsAndHashCode`, `@Getter` (op alle velden), `@Setter` (op alle niet-finale velden) en `@RequiredArgsConstructor`.
* **Gebruik in project**: Toegepast op de meeste domeinklassen (`Event.java`, `Lokaal.java`, `Spreker.java`, `MyUser.java`).

### `@NoArgsConstructor`
* **Betekenis**: Genereert een constructor zonder argumenten. Vereist door JPA voor entiteitsklassen.
* **Gebruik in project**: Toegepast op alle domeinklassen.

### `@AllArgsConstructor`
* **Betekenis**: Genereert een constructor met argumenten voor alle velden in de klasse.
* **Gebruik in project**: Toegepast op alle domeinklassen.

### `@Builder`
* **Betekenis**: Genereert een builder-patroon voor de klasse, waardoor objecten op een meer leesbare en flexibele manier kunnen worden gecreëerd.
* **Gebruik in project**: Toegepast op `MyUser.java`, wat een nette manier biedt om `MyUser`-objecten te construeren (bijv. in `InitDataConfig`).

### `@ToString.Exclude`
* **Betekenis**: Specificeert dat een veld moet worden uitgesloten van de automatisch gegenereerde `toString()` methode door Lombok. Dit is nuttig om oneindige lussen bij bidirectionele relaties te voorkomen.
* **Gebruik in project**: Toegepast op het `sprekers`-veld in `Event.java` en het `events`-veld in `Spreker.java` om recursieve afdrukken te voorkomen.

---

## 7. JSON Serialisatie Annotaties (Jackson)

Deze annotaties van de Jackson-bibliotheek (gebruikt door Spring voor JSON-conversie) helpen bij het omgaan met objectrelaties tijdens serialisatie/deserialisatie.

### `@JsonManagedReference`
* **Betekenis**: Markert de "managed" (voorwaartse) zijde van een bidirectionele relatie. Dit veld wordt geserialiseerd als onderdeel van het object.
* **Gebruik in project**: Toegepast op het `sprekers`-veld in `Event.java`.

### `@JsonBackReference`
* **Betekenis**: Markert de "back" (omgekeerde) zijde van een bidirectionele relatie. Dit veld wordt genegeerd tijdens serialisatie om oneindige lussen te voorkomen.
* **Gebruik in project**: Toegepast op het `events`-veld in `Spreker.java`.

### `@JsonIgnoreProperties`
* **Betekenis**: Negeert specifieke eigenschappen tijdens JSON serialisatie en deserialisatie. Wordt vaak gebruikt om lazy-geladen velden die nog niet geïnitialiseerd zijn te negeren, om fouten te voorkomen.
* **Gebruik in project**: Toegepast op het `lokaal`-veld in `Event.java` om `hibernateLazyInitializer` en `handler` eigenschappen te negeren die door JPA-proxies worden toegevoegd.

---

## 8. Overige Belangrijke Annotaties

### `Serializable`
* **Betekenis**: Een marker-interface die aangeeft dat een klasse kan worden geserialiseerd, wat betekent dat objecten van deze klasse kunnen worden omgezet in een bytestroom en later weer kunnen worden geconstrueerd.
* **Gebruik in project**: Geïmplementeerd door alle domein-entiteiten (`Event`, `Lokaal`, `Spreker`, `MyUser`).

### `CommandLineRunner`
* **Betekenis**: Een interface die kan worden geïmplementeerd door een Spring Bean om code uit te voeren wanneer de applicatie opstart, nadat de Spring `ApplicationContext` is geladen.
* **Gebruik in project**: Geïmplementeerd door `InitDataConfig.java` om initiële data in de database te plaatsen en door `PerformRestITConference.java` om de REST API te testen bij het opstarten.