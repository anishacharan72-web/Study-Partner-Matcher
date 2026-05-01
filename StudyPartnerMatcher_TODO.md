# 📚 Study Partner Matcher — Project To-Do List

> **Platform:** Android (Java + Firebase / MySQL)
> **Total Sprints:** 5 × 2-week sprints (~10 weeks)
> **Design System:** Soft Minimalism / Organic Warmth (Inter typeface, warm blush palette)
> **Status:** 🔴 Not Started

---

## 🗂️ Legend
- `[ ]` Not started
- `[x]` Done
- `🔴` High priority
- `🟡` Medium priority
- `🟢` Low priority

---

## 🏗️ SPRINT 1 — Foundation & Auth
> Goal: Project setup, authentication, and core profile creation

### ⚙️ Project Setup
- [ ] 🔴 Create new Android project (Java, min API 26 / Android 8.0)
- [ ] 🔴 Set up Gradle dependencies (Firebase BOM, Spring Boot client libs, Retrofit)
- [ ] 🔴 Configure Firebase project (Auth, Realtime DB, Cloud Storage, FCM)
- [ ] 🔴 Set up MySQL 8.x database on Google Cloud SQL
- [ ] 🔴 Initialise Spring Boot REST API project (Java 17+)
- [ ] 🔴 Set up GCP App Engine for API hosting
- [ ] 🔴 Configure HTTPS / TLS 1.2+ for all API endpoints
- [ ] 🔴 Set up Git repository and branching strategy (main / develop / feature/*)
- [ ] 🔴 Create base package structure (`auth`, `profile`, `match`, `chat`, `rating`, `ui`)

### 🎨 Design System Setup (from Design Spec)
- [ ] 🔴 Define colour resources in `colors.xml`
  - [ ] Background Blush `#FAF0EB`
  - [ ] Surface White `#FFFFFF`
  - [ ] Card Warm `#F5F0F0`
  - [ ] Deep Navy `#1E2A3A`
  - [ ] Warm Coral / Accent `#D4856A`
  - [ ] Lavender `#C5BFF0`, Yellow `#F5E6A3`, Forest Green `#3A5C3A`
  - [ ] Salmon `#E8897A`, Teal `#8FBCB6`, Olive `#8A9A5B`
- [ ] 🔴 Add Inter font family to project (`res/font/`)
- [ ] 🔴 Define `dimens.xml` spacing tokens (4, 8, 12, 16, 24, 32, 48, 64 dp)
- [ ] 🔴 Define `styles.xml` / `themes.xml` — base app theme with blush background
- [ ] 🔴 Create reusable shape drawables (card radius 20 dp, button 14 dp, input 12 dp, pill 100 dp)
- [ ] 🟡 Export and import all icon assets (SVG → XML vector drawables)
  - [ ] App icon (@1x @2x @3x)
  - [ ] Navigation icons (Home, Explore, Chat, Profile) — filled + outlined
  - [ ] Avatar placeholder SVG

### 🔐 F-001 — User Authentication (Sprint 1)
- [ ] 🔴 Create `SplashActivity` with app logo and warm blush background
- [ ] 🔴 Build `OnboardingActivity` — 3 slides (asset placeholders)
- [ ] 🔴 Build `RegisterActivity` layout
  - [ ] Full Name, Email, Password (min 8 chars), Institution fields
  - [ ] Input style: 52 dp height, 14 dp corner radius, `#F0EBE8` fill, 1 dp Warm Coral on focus
  - [ ] Visible labels above each field (not placeholder-only)
  - [ ] Min 44 × 44 dp touch targets on all inputs
- [ ] 🔴 Wire Firebase Email/Password registration
- [ ] 🔴 Implement email verification flow (account inactive until verified)
- [ ] 🔴 Build `LoginActivity` layout (email + password)
- [ ] 🔴 Implement Google OAuth login via Firebase
- [ ] 🔴 Implement password reset (email link, sent within 30 seconds)
- [ ] 🔴 Implement session token persistence (30-day expiry, auto-logout)
- [ ] 🔴 Hash passwords with bcrypt (cost factor ≥ 12) on the API layer
- [ ] 🔴 Write unit tests for auth flow

### 👤 F-002 — Profile Creation (Sprint 1)
- [ ] 🔴 Create `users` table in MySQL
  - [ ] `user_id`, `name`, `email`, `institution`, `level`, `goal`, `mode_preference`, `bio`, `created_at`, `rating_avg`, `rating_count`
- [ ] 🔴 Build `ProfileSetupActivity` — multi-step onboarding flow
  - [ ] Step 1: Full Name + Institution (Text, max 80 / 120 chars)
  - [ ] Step 2: Academic Level dropdown (High School / UG / PG / PhD)
  - [ ] Step 3: Study Goal single-select (Exam Prep / Assignments / General)
  - [ ] Step 4: Preferred Mode toggle (Online / In-Person / Both)
  - [ ] Step 5: Bio text area (optional, max 200 chars)
- [ ] 🔴 Implement profile save → Spring Boot API → MySQL
- [ ] 🔴 Implement profile edit screen (reuse setup layout)
- [ ] 🔴 Show profile completion progress indicator (percentage, dot grid style)
- [ ] 🟡 Write API endpoint: `POST /api/v1/profile`, `PUT /api/v1/profile/{id}`

### 🏷️ F-003 — Subject & Level Selection (Sprint 1)
- [ ] 🔴 Create `user_subjects` table in MySQL (`id`, `user_id`, `subject_name`, `proficiency_level`)
- [ ] 🔴 Build subject multi-select tag picker UI
  - [ ] Multi-select tags — pill shape (100 dp corner radius), Warm Coral selected state
  - [ ] Min 1 subject required, max 10 subjects
  - [ ] Min 44 × 44 dp touch target per tag
- [ ] 🔴 Seed subject list (common university subjects)
- [ ] 🔴 Allow custom subject entry
- [ ] 🔴 Save selected subjects via API
- [ ] 🟡 Write API endpoints: `POST /api/v1/subjects`, `GET /api/v1/subjects/{userId}`

---

## 📅 SPRINT 2 — Availability & Matching
> Goal: Time slot setup, match algorithm, and match suggestions UI

### 🕐 F-004 — Time Availability Setup (Sprint 2)
- [ ] 🔴 Create `user_availability` table in MySQL (`id`, `user_id`, `day_of_week`, `slot_start`, `slot_end`)
- [ ] 🔴 Build weekly availability grid UI (days × 1-hour slots)
  - [ ] 7 columns (Mon–Sun), time slots from 6 AM – 11 PM
  - [ ] Warm Coral fill for selected slots, Card Warm for unselected
  - [ ] Min 3 slots required before saving
  - [ ] Grid scrollable vertically
- [ ] 🔴 Implement save/update availability → API → MySQL
- [ ] 🔴 Show stale availability warning if not updated in 7+ days
- [ ] 🟡 Write API endpoints: `POST /api/v1/availability`, `GET /api/v1/availability/{userId}`

### 🤝 F-005 — Match Algorithm Core (Sprint 2)
- [ ] 🔴 Create `matches` table in MySQL (`match_id`, `user_a_id`, `user_b_id`, `score`, `status`, `created_at`)
- [ ] 🔴 Implement composite scoring engine in Spring Boot service
  - [ ] Shared subjects score: (common subjects / total subjects) × 40
  - [ ] Schedule overlap score: (overlapping slots / user's total slots) × 30
  - [ ] Academic level score: exact match = 15, adjacent = 7, else = 0
  - [ ] Study goal score: exact match = 10, partial = 5, else = 0
  - [ ] Partner rating score: (avg rating / 5) × 5
  - [ ] Only surface matches with composite score ≥ 50
  - [ ] Return top 10 matches sorted by descending score
- [ ] 🔴 Normalise subject weighting to prevent bias toward common subjects
- [ ] 🔴 Write unit tests for scoring logic (edge cases: no subjects, no overlap, new user)
- [ ] 🟡 Write API endpoint: `GET /api/v1/matches/{userId}`

### 🎯 F-006 — Match Suggestions UI (Sprint 2)
- [ ] 🔴 Build `MatchesFragment` / `MatchesActivity`
  - [ ] Top App Bar: Deep Navy, logo icon left, search icon right (24 dp)
  - [ ] Screen background: Blush `#FAF0EB`
  - [ ] Match cards in vertical list, full 350 dp content width
- [ ] 🔴 Design match card component
  - [ ] Card surface: White `#FFFFFF`, corner radius 20 dp, padding 16 dp all sides
  - [ ] Avatar circle (32 dp), name (20 sp SemiBold), institution (12 sp muted)
  - [ ] Subject tags (pill shape, Lavender fill, 11 sp label)
  - [ ] Compatibility score badge (Warm Coral background, white text)
  - [ ] Accept / Decline buttons (min 44 × 44 dp touch targets)
- [ ] 🔴 Implement Accept / Reject match action → API call → update `matches.status`
- [ ] 🔴 Show empty state illustration when no matches (SVG asset)
- [ ] 🔴 Show loading state (skeleton cards) while fetching
- [ ] 🟡 Implement pull-to-refresh

---

## 💬 SPRINT 3 — Chat & Rating
> Goal: Real-time messaging and partner rating

### 💬 F-007 — In-App Chat System (Sprint 3)
- [ ] 🔴 Set up Firebase Realtime Database security rules (no cross-user access)
- [ ] 🔴 Build `ChatListFragment` — list of active conversations
  - [ ] Row: Avatar, partner name (20 sp SemiBold), last message preview (14 sp muted), timestamp (10 sp)
  - [ ] Unread badge: Warm Coral circle, white count text
- [ ] 🔴 Build `ChatDetailActivity`
  - [ ] Sent messages: Warm Coral bubble, right-aligned, white text
  - [ ] Received messages: Card Warm bubble, left-aligned, body grey text
  - [ ] Corner radius: 20 dp all, 4 dp on sender corner
  - [ ] Input field: 52 dp height, 12 dp corner radius, send arrow icon (Warm Coral, 22 dp)
  - [ ] Max message length: 1,000 characters (show counter near limit)
  - [ ] Double-tick delivery indicators: Sent → Delivered → Read
- [ ] 🔴 Lock chat entry point — only available after both users accept the match
- [ ] 🔴 Chat history retention: 90 days (set Firebase TTL rules)
- [ ] 🔴 Implement manual conversation delete
- [ ] 🔴 Send FCM push notification on new message (app backgrounded)
- [ ] 🟡 Implement online / last-seen status indicator

### ⭐ F-008 — Partner Rating System (Sprint 3)
- [ ] 🔴 Create `ratings` table in MySQL (`rating_id`, `rater_id`, `ratee_id`, `match_id`, `stars 1–5`, `review_text`, `created_at`)
- [ ] 🔴 Trigger rating prompt 1 hour after scheduled session end time (FCM or local notification)
- [ ] 🔴 Build rating bottom sheet UI
  - [ ] 5-star selector (40 × 40 dp stars, Warm Coral filled / grey outlined)
  - [ ] Optional review text area (max 150 chars)
  - [ ] Submit button (Warm Coral, 14 dp corner radius, min 52 dp height)
  - [ ] Haptic feedback on star tap (light impact)
- [ ] 🔴 Display average rating and count on partner profile card
- [ ] 🔴 Auto-flag users with avg rating < 2.0 after 10+ ratings for admin review
- [ ] 🟡 Write API endpoints: `POST /api/v1/ratings`, `GET /api/v1/ratings/{userId}`

---

## 🔔 SPRINT 4 — Notifications & Scheduling
> Goal: Push notifications and study session scheduling

### 🔔 F-009 — Push Notifications (Sprint 4)
- [ ] 🟡 Configure FCM server-side in Spring Boot (`FirebaseMessagingService`)
- [ ] 🟡 Implement notification: **New match found** (deep link → Matches screen)
- [ ] 🟡 Implement notification: **New message received** (deep link → Chat)
- [ ] 🟡 Implement notification: **Rating prompt** (1h after session)
- [ ] 🟡 Implement notification: **Availability reminder** (weekly, if stale)
- [ ] 🟡 Build in-app notification preferences screen (toggle per type)
- [ ] 🟡 Handle notification tap → correct screen navigation

### 📆 F-010 — Study Session Scheduling (Sprint 4)
- [ ] 🟡 Build session scheduling UI within chat (date + time picker)
  - [ ] Time picker respects both users' saved availability slots
  - [ ] Warm Coral for available slots, muted grey for unavailable
- [ ] 🟡 Create `sessions` table in MySQL (`session_id`, `match_id`, `scheduled_at`, `duration_minutes`, `status`, `location_note`)
- [ ] 🟡 Write API endpoints: `POST /api/v1/sessions`, `GET /api/v1/sessions/{userId}`
- [ ] 🟡 Show upcoming session card on home screen
- [ ] 🟡 Send reminder notification 30 min before session start
- [ ] 🟡 Mark session as complete → trigger rating prompt flow

---

## 🖼️ SPRINT 5 — Polish & Future Features
> Goal: Profile photos, accessibility pass, testing, and launch prep

### 📸 F-011 — Profile Photo Upload (Sprint 5)
- [ ] 🟢 Set up Firebase Cloud Storage bucket and security rules
- [ ] 🟢 Build photo picker (camera + gallery) with permission handling
- [ ] 🟢 Implement image crop to circle (32 dp avatar size)
- [ ] 🟢 Upload photo to Cloud Storage, save URL to MySQL user record
- [ ] 🟢 Display avatar on profile, match cards, chat header, nav bar
- [ ] 🟢 Write API endpoint: `PUT /api/v1/profile/{id}/photo`

### 🏠 F-012 — Group Study Rooms (Sprint 5 — Future)
- [ ] 🟢 Design group room UI (3+ participants, card layout)
- [ ] 🟢 Extend `matches` schema for multi-user rooms
- [ ] 🟢 Implement group chat in Firebase Realtime DB
- [ ] 🟢 Build room invite flow

---

## ♿ Accessibility Checklist (All Sprints)
- [ ] 🔴 All text: WCAG AA contrast ratio ≥ 4.5:1 (body), ≥ 3:1 (large text ≥ 18 sp)
- [ ] 🔴 All interactive elements: min 44 × 44 dp touch target
- [ ] 🔴 All images / icons: `contentDescription` set for screen readers
- [ ] 🔴 Colour is never the sole differentiator — icons/labels accompany all coloured data
- [ ] 🟡 Dynamic Type: test font scaling up to 200% without layout breaking
- [ ] 🟡 Respect OS `Reduce Motion` preference — fade-only animation fallback
- [ ] 🟡 All form inputs carry visible labels when focused (not placeholder-only)

---

## 🧪 Testing Checklist
- [ ] 🔴 Unit tests — Match scoring algorithm (all weight combinations)
- [ ] 🔴 Unit tests — Auth flow (register, login, reset, token expiry)
- [ ] 🔴 Integration tests — Spring Boot API endpoints (REST Assured)
- [ ] 🔴 Firebase security rules tests (cross-user access blocked)
- [ ] 🟡 UI tests — Profile setup flow (Espresso)
- [ ] 🟡 UI tests — Chat send/receive flow
- [ ] 🟡 Performance test — Match results load < 2 seconds on 4G
- [ ] 🟡 Performance test — Chat delivery < 500ms
- [ ] 🟡 API load test — p95 response ≤ 300ms under 200 concurrent users
- [ ] 🟡 Firebase Test Lab — automated UI tests on 5 device profiles (min API 26)
- [ ] 🟢 App crash rate monitoring — target < 0.5% (Firebase Crashlytics)

---

## 🗄️ Database Setup Checklist
- [ ] 🔴 Create `users` table
- [ ] 🔴 Create `user_subjects` table
- [ ] 🔴 Create `user_availability` table
- [ ] 🔴 Create `matches` table
- [ ] 🔴 Create `ratings` table
- [ ] 🟡 Create `sessions` table
- [ ] 🟡 Set up Cloud SQL read replica for scalability
- [ ] 🟡 Add indexes: `user_id` on all FK columns, `score DESC` on matches
- [ ] 🟡 Configure automated daily backups on Cloud SQL

---

## 🚀 Launch Checklist
- [ ] 🟡 Configure GCP App Engine auto-scaling (target: 50,000 concurrent users)
- [ ] 🟡 Set Firebase budget alert thresholds
- [ ] 🟡 Enable 99.5% uptime SLA monitoring (Cloud Monitoring)
- [ ] 🟡 Set up maintenance window schedule (2 AM – 4 AM)
- [ ] 🟡 Create Play Store listing (screenshots, description, privacy policy)
- [ ] 🟡 Complete Google Play app review submission
- [ ] 🟢 Plan beta launch with seed users from partner institutions
- [ ] 🟢 Set up support ticket tracking (target: < 2% of DAU)
- [ ] 🟢 Prepare onboarding completion funnel analytics

---

## ❓ Open Questions to Resolve
- [ ] Should study sessions require a location input for in-person mode?
- [ ] English-only at launch, or add i18n support from the start?
- [ ] Moderation policy for 1-star reviews — auto-flag threshold or manual review queue?
- [ ] Should rejected match history be persisted or discarded after session?

---

## 📊 Sprint Summary

| Sprint | Focus | Key Deliverables | Status |
|--------|-------|-----------------|--------|
| Sprint 1 | Foundation & Auth | Project setup, Auth, Profile, Subjects | 🔴 Not Started |
| Sprint 2 | Availability & Match | Time slots, Algorithm, Match UI | 🔴 Not Started |
| Sprint 3 | Chat & Rating | Real-time chat, Rating system | 🔴 Not Started |
| Sprint 4 | Notifications & Schedule | FCM, Session scheduling | 🔴 Not Started |
| Sprint 5 | Polish & Future | Photo upload, Group rooms, Launch prep | 🔴 Not Started |

---

*Generated from Study Partner Matcher PRD v1.0 & Mental Wellness App Design Spec v1.0*
