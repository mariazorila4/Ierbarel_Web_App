# Ierbărel 🌿

Ierbărel is a full-stack web application designed for nature enthusiasts and plant lovers. It provides a simple way to identify unknown plant species from photos, manage a personal digital herbarium, explore a shared global catalog, and receive botanical advice through an AI assistant.

---

## Key Features

- **Plant Identification:** Upload photos to discover unknown species on the spot.
- **Personal & Global Herbarium:** Save identified plants into a personal collection or search the global catalog by common and scientific names.
- **Interactive AI Assistant ("Ghiocel"):** Get instant care advice, watering tips, and botanical insights powered by Gemini AI.
- **Admin Management Panel:** Dashboard for managing user accounts, system statistics, and expanding the plant database.

---

## Tech Stack

### Frontend
- **Framework:** Vue 3 (Composition API)
- **Tooling & Routing:** Vite, Vue Router
- **HTTP Client:** Axios
- **UI & Styling:** Custom CSS with Glassmorphism design and responsive grid layouts

### Backend
- **Framework:** Java 21, Spring Boot 3
- **Security:** Spring Security, JWT authentication
- **Database:** PostgreSQL (with Spring Data JPA / JdbcTemplate)
- **Integrations:** Google Gemini API for the AI chat assistant

---

## Repository Structure

```text
Ierbarel_Web_App/
├── frontend_web/        # Vue 3 application (UI, views, components, assets)
└── backend_spring/      # Spring Boot application (REST controllers, services, database models)
```

## Project Origin & Setup Note
This project was initially developed locally starting August 3, 2026. As of August 10, 2026, it has been pushed to GitHub as an open-source repository to streamline personal side-project development across different environments and allow seamless contribution outside work hours.

## Getting started

### Backend Setup
Ensure PostgreSQL is running and update src/main/resources/application.properties with your database credentials and Gemini API key, then run:
```bash
cd backend_spring
mvn spring-boot:run
```

### Frontend Setup
Before running the frontend, make sure to install all dependencies (which will generate the required node_modules directory locally, as it is excluded from source control):
```bash
cd frontend_web
npm install
npm run dev
```

## Contributors

 Maria Cristina Zorila - [@mariazorila4](https://github.com/mariazorila4/) & [@mariacristinazorila4](https://github.com/mariacristinazorila4)

 *Note: Any additional GitHub accounts listed as contributors belong to the same project owner, used across different development environments.*
