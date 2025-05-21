# IT Conference 2025 💻

Manage your IT Conference events & locations! Admins organize, users view & save favorites.

## ✨ Features

### 🗓️ Event Overview
* All events sorted by date/time.
* Details: Name, Speaker(s) (max 3), Location, Date/Time.
* Admins: "Add/Manage Events" button.
* Users: Link to "My Favorites" list.

### 🎫 Event Details
* Detailed event info: Name, Description, Speaker(s), Location & Capacity, Date/Time.
* Users: "Add to Favorites" (max 5) button. Disappears if limit reached or already favorited.
* Admins: "Edit Event" button.

### ➕ Add/Edit Event (Admin)
* Form fields: Name (starts with letter), Description, Speaker(s) (1-3, no duplicates), Location, Date/Time (future/present, within 2025-05-18 to 2025-12-31), Beamer Code (4-digit) & Check (code % 97), Price (€9.99 to €99.99).
* 🚫 **Validation**: No duplicate events at same time/location. No duplicate event names on same day.

### 🏢 Manage Locations (Admin)
* Add locations: Name (letter + 3 digits, e.g., A123, unique), Capacity (1-50).
* Confirmation: "Room with X seats was added." ✅

### ❤️ Favorites List (User)
* View all favorited events.
* Sorted by time (ascending), then title (alphabetical).

### 🔒 Security & Logout
* Login required for roles (Admin/User).
* "Logout" button & current role displayed on every screen.

### 🗣️ Localization
* Supports Dutch (nl) 🇳🇱 or English (en) 🇬🇧.
* All validation messages & date formats from resource bundles.

## 🛠️ Tech Stack
* **Backend**: Spring Boot, Spring Security (AuthN/AuthZ), JPA (MySQL)
* **Frontend**: Thymeleaf, CSS
* **Validation**: Jakarta Validation with custom annotations (`@ValidBeamerCheck`, `@ValidConferenceDate`, `@ValidEventConstraints`, `@ValidSpeakerList`)
* **APIs**: REST API (Events by Date, Location Capacity) + Reactive Web Client
* **Testing**: Unit Tests for Controllers, Validation, Security
* **Error Handling**: Custom error pages for invalid URLs & exceptions.

## 🚀 Get Started

1.  **Clone**: `git clone [repository_url]`
2.  **DB Setup**: Update `src/main/resources/application.properties` with your MySQL details.
3.  **Build**: `./mvnw clean install`
4.  **Run**: `./mvnw spring-boot:run`
5.  **Access**: `http://localhost:8080`

## 🔑 Credentials
* **Admin**: `admin` / `admin`
* **User**: `user` / `user`
