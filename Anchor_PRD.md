# Product Requirements Document (PRD)
## Student Life Dashboard — Windows Desktop Application

**Version:** 1.0
**Author:** [Your Name]
**Date:** September 2026
**Status:** Draft

---

## 1. Overview

### 1.1 Product Summary
Student Life Dashboard is a personal Windows desktop application built in Java (JavaFX) that unifies four core aspects of student life — study sessions, tasks, expenses, and habits — into a single tool. It gives the user a consolidated view of their daily consistency and productivity through a unified history and dashboard.

### 1.2 Problem Statement
Students currently rely on 3–4 separate apps (a timer app, a to-do app, an expense tracker, a habit tracker) to manage their daily routine. This fragmentation makes it hard to see the full picture of how consistent and productive a day/week/month actually was. There's no single place that ties study time, task completion, spending, and habits together.

### 1.3 Goals
- Provide one native Windows app instead of four disconnected tools.
- Make daily logging fast enough that it doesn't feel like a chore.
- Surface consistency and trends over time (not just raw logs).
- Make the app visually pleasant enough that the user *wants* to open it daily.

### 1.4 Non-Goals (v1)
- No multi-user support / accounts / cloud sync.
- No mobile app (Windows desktop only for v1).
- No social features (sharing, leaderboards, etc.).
- No AI-based recommendations (may be a future consideration).

---

## 2. Target User

A single student (the developer/user themself) who wants to:
- Track focused study time.
- Manage daily/weekly tasks and deadlines.
- Log and understand personal spending.
- Build and maintain daily habits.
- See how all of the above connect to their overall consistency.

---

## 3. Core Modules & Requirements

### 3.1 Study Timer (Pomodoro-style)
**Purpose:** Track focused study sessions.

**Requirements:**
- Start / pause / stop a timer session.
- Configurable session length (default 25 min) and break length (default 5 min).
- Auto-log completed sessions with: date, start time, duration, subject/tag (optional).
- Daily/weekly total study time view.
- Sound or notification when a session ends.

### 3.2 To-Do List
**Purpose:** Manage tasks and deadlines.

**Requirements:**
- Add, edit, delete, and mark tasks complete.
- Fields: title, due date, priority (Low/Medium/High), optional category/subject.
- Sort/filter by due date, priority, or completion status.
- Visual indicator for overdue tasks.
- Basic reminder/highlight for tasks due today or tomorrow.

### 3.3 Expense Tracker
**Purpose:** Log and understand personal spending.

**Requirements:**
- Add an expense: amount, category (Food, Transport, Books, Subscriptions, Other — customizable), date, optional note.
- View spending by day/week/month.
- Category-wise breakdown (e.g., simple bar/pie chart).
- Running monthly total, with an optional monthly budget limit and warning when exceeded.

### 3.4 Habit Tracker
**Purpose:** Track recurring daily habits and build streaks.

**Requirements:**
- Define custom habits (e.g., Sleep on time, Exercise, Read, No junk food).
- Daily checkbox-style marking (done/not done) per habit.
- Streak counter per habit (current streak + longest streak).
- Weekly/monthly view showing completion percentage per habit.

### 3.5 Unified Dashboard / Consistency View
**Purpose:** Tie all modules together into one consistency snapshot.

**Requirements:**
- Daily summary: study time logged, tasks completed vs pending, money spent, habits completed.
- Weekly/monthly trend charts (e.g., study hours per day, habit completion %, spending trend).
- A simple "consistency score" combining activity across all four modules (exact formula to be defined during design — e.g., weighted % of daily targets hit).
- Calendar-style heatmap (like GitHub's contribution graph) showing daily activity intensity.

---

## 4. Technical Requirements

| Area | Choice | Reason |
|---|---|---|
| Language | Java | Core requirement/skill-building goal |
| UI Framework | JavaFX | Modern look, good charting/calendar support, still pure Java |
| Storage | SQLite (local `.db` file) | Serverless, file-based, relational — good for linking modules |
| Packaging | `jpackage` (JDK built-in) | Produces a native Windows `.exe` installer, no separate JDK install needed for end use |
| Charts | JavaFX Charts or a lightweight charting lib | Needed for dashboard trends |

### 4.1 Data Model (high-level)
- `study_sessions` (id, date, start_time, duration_minutes, subject)
- `tasks` (id, title, due_date, priority, category, is_complete)
- `expenses` (id, date, amount, category, note)
- `habits` (id, name, created_date)
- `habit_logs` (id, habit_id, date, is_done)

All tables share a `date` field, which is what powers the unified dashboard queries.

### 4.2 Platform
- Windows 10/11 desktop, distributed as a standalone installable `.exe`.
- No internet connection required (fully offline, local storage only).

---

## 5. User Experience Requirements

- Sidebar navigation: Dashboard / Timer / To-Dos / Expenses / Habits.
- Consistent visual theme across all modules (not four different "apps" glued together).
- Should feel fast to log an entry — ideally 2–3 clicks max for any log action.
- Visually calming/motivating design (progress bars, streaks, gentle color coding) rather than a plain data-entry form.

---

## 6. Success Criteria (for a personal-use project)

- App is used consistently for at least 2–3 weeks without abandoning it.
- All four modules are functional and share the same date-based data model.
- Dashboard accurately reflects data from all modules with no manual cross-checking needed.
- App installs and runs as a native Windows application (not run through an IDE).

---

## 7. Milestones / Build Plan

| Phase | Scope | Est. Time |
|---|---|---|
| 1 | Project setup: JavaFX shell + sidebar nav + SQLite connection | 1–2 days |
| 2 | Study Timer module | 1–2 days |
| 3 | To-Do List module | 1–2 days |
| 4 | Expense Tracker module | 1–2 days |
| 5 | Habit Tracker module | 1–2 days |
| 6 | Unified Dashboard (charts, heatmap, consistency score) | 2–3 days |
| 7 | UI polish + styling pass | 1–2 days |
| 8 | Packaging with jpackage into Windows `.exe` | 0.5–1 day |

**Estimated total:** ~1.5–2 weeks of casual daily development.

---

## 8. Future Considerations (post-v1)
- Notifications/reminders (Windows system tray integration).
- Export data (CSV/PDF weekly or monthly report).
- Customizable "consistency score" weighting.
- Optional AI study assistant module (summarization, quiz generation from notes).
- Cloud backup/sync (optional, low priority — this is a personal offline tool by design).

---

## 9. Open Questions
- Exact formula for the "consistency score" — needs to be defined before Phase 6.
- Should habits and to-dos be merged conceptually (a habit is really a recurring to-do), or kept as separate modules? (Recommendation: keep separate for v1 — simpler mental model.)
- Default categories for expenses — finalize list before Phase 4.
