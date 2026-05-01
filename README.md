# 📚 Study Partner Matcher

> Android app to match students based on subjects, schedule, academic level, and study goals.

## Tech Stack
| Layer | Technology |
|-------|-----------|
| Android App | Java, min API 26 |
| Backend API | Spring Boot 3.3 (Java 17) |
| Database | MySQL 8 (H2 for local dev) |
| Auth | Firebase Authentication |
| Real-time Chat | Firebase Realtime Database |
| Storage | Firebase Cloud Storage |
| Push | Firebase Cloud Messaging (FCM) |
| Networking | Retrofit 2 + OkHttp |
| Images | Glide |

## Project Structure
```
Study_Matcher/
├── android/          ← Android App (Java)
├── backend/          ← Spring Boot REST API
├── database.rules.json  ← Firebase RTDB security rules
└── StudyPartnerMatcher_TODO.md
```

## Getting Started

### Prerequisites
- Android Studio 2025.3+
- JDK 17 (Eclipse Temurin)
- MySQL 8.x (local) or Cloud SQL
- Firebase project: `study-partner-matcher`

### Android App Setup
1. Open `android/` in Android Studio
2. Place `google-services.json` in `android/app/`
3. Download Inter font from [Google Fonts](https://fonts.google.com/specimen/Inter) → place TTF files in `android/app/src/main/res/font/`
4. Run on emulator or device (min API 26)

### Spring Boot API Setup
1. Open `backend/` in IntelliJ IDEA
2. Run `mvn spring-boot:run` (uses H2 in-memory DB by default)
3. API available at `http://localhost:8080`
4. H2 Console: `http://localhost:8080/h2-console`
5. For MySQL: update `application.properties` (uncomment MySQL section)
6. Place Firebase service account JSON at `backend/src/main/resources/firebase-service-account.json`

### Firebase Setup
- Auth: Enable Email/Password + Google in Firebase Console
- Realtime Database: Rules are in `database.rules.json` — deploy with `firebase deploy --only database`
- Storage: Enable Cloud Storage

### Run Tests
```bash
cd backend
mvn test
```

## Match Algorithm
Composite score (0–100):
- **Shared subjects**: (common / total) × 40
- **Schedule overlap**: (overlapping slots / user slots) × 30
- **Academic level**: exact=15, adjacent=7, else=0
- **Study goal**: exact=10, partial=5, else=0
- **Partner rating**: (avg / 5) × 5

Minimum score to surface: **50** | Max results: **10**

## Sprint Progress
| Sprint | Status |
|--------|--------|
| Sprint 1 — Foundation & Auth | 🟡 In Progress |
| Sprint 2 — Availability & Match | 🔴 Not Started |
| Sprint 3 — Chat & Rating | 🔴 Not Started |
| Sprint 4 — Notifications & Schedule | 🔴 Not Started |
| Sprint 5 — Polish & Launch | 🔴 Not Started |
