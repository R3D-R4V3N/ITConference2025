# 💻 IT-Conferentie 2025: Geavanceerd Evenementbeheer met Spring Boot

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-red)](https://www.oracle.com/java/technologies/downloads/#jdk21-windows)
[![JPA](https://img.shields.io/badge/JPA-3.1-blue)](https://jakarta.ee/specifications/persistence/3.1/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2.0-orange)](https://spring.io/projects/spring-security)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1.2-005C0F)](https://www.thymeleaf.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Lombok](https://img.shields.io/badge/Lombok-1.18.30-purple)](https://projectlombok.org/)
[![Build Status](https://github.com/jouwgebruikersnaam/ITConference/actions/workflows/ci.yml/badge.svg)](https://github.com/jouwgebruikersnaam/ITConference/actions/workflows/ci.yml)

---

## ✨ Overzicht

Welkom bij het **IT-Conferentie 2025** project! Dit is een robuuste Spring Boot applicatie, ontworpen om het beheer van IT-conferentie-evenementen en bijbehorende lokalen te stroomlijnen. De applicatie is gebouwd met de nieuwste Java- en Spring-technologieën en implementeert een reeks geavanceerde functionaliteiten, waaronder een gedetailleerd rollenbeheer, complexe validatieregels en een volwaardige REST API.

Dit project is ontwikkeld als een examenopdracht voor het vak *Enterprise Web Development - Java* en volgt de geleerde methodieken strikt.

---

## 🚀 Functionaliteiten

De applicatie biedt de volgende kernfunctionaliteiten, verdeeld over verschillende gebruikersrollen:

### **Algemeen (Niet-ingelogd, User & Admin)**
* **Evenementen Overzicht**: Een gesorteerd overzicht van alle evenementen met details zoals naam, sprekers (max. 3), lokaal, datum en tijd[cite: 1190].
* **Evenement Details**: Een specifieke pagina per evenement met uitgebreide details, inclusief een beschrijving, capaciteit van het lokaal, en een knop voor favorieten (voor ingelogde gebruikers)[cite: 1193].
* **Internationalisatie (i18n)**: Volledige ondersteuning voor Nederlands en Engels, met alle teksten en foutmeldingen afkomstig uit resource bundles[cite: 1207, 1208].
* **Robuuste Foutafhandeling**: Custom error pages en adviezen voor zowel MVC- als REST-endpoints om gebruikersvriendelijke foutmeldingen te tonen[cite: 1212].

### **Admin-rol**
* **Evenementen Beheren**:
    * **Toevoegen & Bewerken**: Admins kunnen nieuwe evenementen toevoegen of bestaande bewerken via een uitgebreid formulier[cite: 1196].
    * **Validatieregels voor Evenementen**:
        * Naam: Verplicht, start met een letter[cite: 1197], uniek op dezelfde dag[cite: 1200].
        * Sprekers: Verplicht (1-3 sprekers), geen duplicaat sprekers binnen één evenement[cite: 1196, 1198].
        * Lokaal: Verplicht[cite: 1196].
        * Datum & Tijd: Verplicht, in de toekomst of heden[cite: 1197], en binnen de conferentieperiode (bijv. 18/05/2025 - 31/12/2025)[cite: 1197].
        * Beamercode: Viercijferig, met een correcte tweecijferige checksum (rest bij deling door 97)[cite: 1196, 1197].
        * Prijs: Verplicht, tussen €9.99 en €99.99 (geformatteerd als valuta)[cite: 1197].
        * Geen dubbele evenementen op hetzelfde tijdstip in hetzelfde lokaal[cite: 1199].
    * **Verwijderen**: Mogelijkheid om evenementen te verwijderen.
* **Lokalen Beheren**:
    * **Toevoegen & Bewerken**: Admins kunnen nieuwe lokalen toevoegen of bestaande bewerken[cite: 1201].
    * **Validatieregels voor Lokalen**:
        * Naam: Verplicht, begint met een letter gevolgd door 3 cijfers[cite: 1202], uniek[cite: 1202].
        * Capaciteit: Verplicht, tussen 1 en 50[cite: 1203].
    * **Verwijderen**: Mogelijkheid om lokalen te verwijderen, mits er geen gekoppelde evenementen zijn.

### **User-rol**
* **Favorietenlijst**: Gebruikers kunnen een gepersonaliseerde lijst van favoriete evenementen bijhouden en bekijken[cite: 1204].
    * **Beheer**: Evenementen toevoegen [cite: 1193] of verwijderen uit favorieten.
    * **Limiet**: Maximum van 5 favoriete evenementen per gebruiker[cite: 1195].
    * **Sortering**: Favorieten zijn gesorteerd op datum/tijd oplopend, daarna op titel alfabetisch[cite: 1205].

### **Security & Authenticatie/Autorisatie**
* **Rollen**: Twee rollen: `ADMIN` en `USER`[cite: 1188].
* **Login**: Custom loginpagina met gebruikersnaam (`admin`/`admin` en `user`/`user`)[cite: 1206].
* **Autorisatie**: Toegang tot functionaliteiten is strikt gebaseerd op de rol van de ingelogde gebruiker (bijv. Admin-specifieke pagina's zijn alleen toegankelijk voor Admins)[cite: 1188].
* **Logout**: Uitlogfunctionaliteit beschikbaar op elk scherm[cite: 1206].
* **Wachtwoordcodering**: Wachtwoorden worden veilig opgeslagen met `BCryptPasswordEncoder`.

### **REST API**
* **Evenementen op Datum**: Endpoint om alle evenementen op een specifieke datum op te halen[cite: 1212].
* **Lokaal Capaciteit**: Endpoint om de capaciteit van een lokaal op te vragen via de lokaalnaam[cite: 1212].
* **Testen**: Geautomatiseerde tests van de REST API met een Reactive Web Client bij het opstarten van de applicatie[cite: 1213].

---

## 🛠️ Technologie Stack

* **Spring Boot 3.2.5**: Het framework voor de snelle ontwikkeling van stand-alone, productieklare Spring-applicaties.
* **Spring MVC**: Voor het bouwen van de webapplicatie (controllers, views).
* **Spring Security 6.2.0**: Robuust framework voor authenticatie en autorisatie.
* **Spring Data JPA**: Voor eenvoudige en efficiënte data-access met de database.
* **Hibernate**: De JPA-implementatie.
* **MySQL 8.0**: Relationele database voor data-opslag.
* **Thymeleaf 3.1.2**: Server-side Java template engine voor het renderen van HTML-views.
* **Jakarta Bean Validation 3.1**: Voor declaratatieve en custom inputvalidatie.
* **Project Lombok 1.18.30**: Voor het verminderen van boilerplate code (getters, setters, constructors, builders).
* **Spring WebFlux (Reactive WebClient)**: Voor het testen van de REST API met een niet-blokkerende, reactieve client.
* **Maven**: Build automation tool voor projectbeheer.

---

## ⚙️ Lokale Installatie & Setup

Volg deze stappen om het project lokaal op te zetten en uit te voeren:

### **Vereisten**
* Java Development Kit (JDK) 21 of hoger
* Apache Maven 3.x
* MySQL Server 8.0 of hoger
* Een IDE naar keuze (bijv. Spring Tool Suite 4, IntelliJ IDEA, Eclipse)

### **Stappen**
1.  **Clone de repository**:
    ```bash
    git clone [https://github.com/jouwgebruikersnaam/ITConference.git](https://github.com/jouwgebruikersnaam/ITConference.git)
    cd ITConference
    ```
    *(Vergeet niet `jouwgebruikersnaam` aan te passen naar je eigen GitHub gebruikersnaam)*

2.  **Database Configuratie**:
    * Maak een nieuwe MySQL database aan. Standaard naam in `application.properties` is `itconference_db`.
        ```sql
        CREATE DATABASE IF NOT EXISTS itconference_db;
        ```
    * Werk de database-instellingen bij in `src/main/resources/application.properties`:
        ```properties
        spring.datasource.url=jdbc:mysql://localhost:3306/itconference_db?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC
        spring.datasource.username=your_mysql_username
        spring.datasource.password=your_mysql_password
        spring.jpa.hibernate.ddl-auto=create-drop
        ```
        *(Vergeet niet `your_mysql_username` en `your_mysql_password` aan te passen)*

3.  **Build het project**:
    ```bash
    mvn clean install
    ```

4.  **Voer de applicatie uit**:
    ```bash
    mvn spring-boot:run
    ```
    De applicatie zal starten op `http://localhost:8080`.

### **Standaard Gebruikers**
Bij de eerste opstart worden de volgende gebruikers automatisch aangemaakt in de database:
* **Admin**: `username: admin`, `password: admin`
* **User**: `username: user`, `password: user`

---

## 🧪 Tests

Het project omvat een uitgebreide set van unit- en integratietests om de correcte werking van de applicatie te garanderen. Testen omvatten:

* **Controller Tests**: Voor zowel MVC- als REST-controllers.
* **Domain Tests**: Validatie en gedrag van de entiteitsklassen.
* **Validator Tests**: Unit tests voor custom validatoren.
* **Security Tests**: Testen van authenticatie en autorisatieregels.
* **REST Client Tests**: Geautomatiseerde calls naar de REST API.

Voer alle tests uit met Maven:
​```bash
mvn test
​```
