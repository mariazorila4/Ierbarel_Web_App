# Ierbărel 🌿

Ierbărel is a full-stack web application designed for nature enthusiasts and plant lovers. It provides a simple way to identify unknown plant species from photos, manage a personal digital herbarium, explore a shared global catalog, view community discoveries on interactive maps, and receive botanical advice through an AI assistant.

---

## Key Features

- **Hybrid Plant Identification:** Visual plant identification combining Pl@ntNet API for species matching and Google Gemini AI for automated generation of structured botanical technical sheets in Romanian (including family, species description, and natural habitat).

- **Cloud Image Persistence:** Direct client-side Base64 conversion (FileReader) sent to server-side Cloudinary storage, rendering persistent, secure HTTPS URLs across user sessions.

- **Interactive Community Maps:** Integrated Leaflet.js maps displaying plant discovery locations tagged by community members using geocoding.

- **Personal & Global Herbarium:** Save identified plants into a personal collection, toggle public/private visibility, or explore the global database with real-time search and filter capabilities.

- **Interactive AI Assistant ("Ghiocel"):** Get instant care advice, watering tips, and botanical insights powered by Gemini AI, configured with resilient retry mechanisms (503/429 handling).

- **Daily Botanical Trivia:** Automated daily service delivering unique botanical curiosities to users.

- **Admin Management Panel:** Comprehensive dashboard for managing user accounts, system statistics, and expanding the core plant database with detailed species traits.

---

## Key Architectural Decisions & Justifications

### 1. Transition from YOLO to Pl@ntNet API + Gemini AI
- **Broad Taxonomic Coverage:** Replacing local YOLO model inference with Pl@ntNet API grants access to a vast, professionally curated dataset covering tens of thousands of plant species worldwide without maintaining local ML model weights.

- **Structured Botanical Intelligence:** Gemini AI complements Pl@ntNet by transforming raw species identifiers into complete, dynamic Romanian botanical sheets (natural habitat, flowering period, growth cycle, leaf/stem characteristics).

### 2. Cloud Storage (Cloudinary) vs. Local Disk / Database BLOBs
- **Database Performance & Size Limits:** Storing binary image data (BLOB/BYTEA) directly in PostgreSQL rapidly exceeds free-tier storage limits (e.g., Neon Tech) and degrades SELECT query performance. Storing lightweight VARCHAR URLs keeps queries fast.

- **Session-Independent Persistence:** Browser-generated blob: URLs exist only in temporary client RAM. Converting images to Base64 and uploading them to Cloudinary guarantees permanent HTTPS access across user logouts, without cluttering the local server disk repository.

## Tech Stack

### Frontend
- **Framework:** Vue 3 (Composition API)
- **Tooling & Routing:** Vite, Vue Router
- **HTTP Client:** Axios
- **Interactive Maps:** Leaflet.js (OpenStreetMap)
- **UI & Styling:** Custom CSS with Glassmorphism design and responsive grid layouts

### Backend
- **Framework:** Java 21, Spring Boot 3
- **Security:** Spring Security, JWT authentication
- **Database:** PostgreSQL (managed via Spring JdbcTemplate for direct, high-performance SQL execution)
- **Integrations & Cloud Services:**
   - Pl@ntNet API (Visual Identification)
   - Google Gemini API (Botanical Metadata & Chatbot)
   - Cloudinary Java SDK (Cloud Image Storage)

---

## Repository Structure

```text
Ierbarel_Web_App/
├── frontend_web/         # Vue 3 application (UI, views, components, maps, assets)
└── backend_spring/       # Spring Boot application (Controllers, Services, JdbcTemplate Repositories)
```

## Project Origin & Setup Note
This project was initially developed locally starting August 3, 2026. As of August 10, 2026, it has been pushed to GitHub as an open-source repository to streamline personal side-project development across different environments and allow seamless contribution outside work hours.

## Getting started

### Environment Properties Setup
Before running the backend, update ```src/main/resources/application.properties``` with your credentials:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://your-db-host:5432/ierbarel_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# API Keys
gemini.api.key=your_gemini_api_key
plantnet.api.key=your_plantnet_api_key

# Cloudinary Storage
cloudinary.cloud-name=your_cloud_name
cloudinary.api-key=your_cloudinary_api_key
cloudinary.api-secret=your_cloudinary_api_secret
```

### Backend Setup
Run the Spring Boot application:
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
