# Anchor — Student Life Dashboard

A unified Windows desktop app for tracking study sessions, tasks, expenses, and habits — with a consistency dashboard that ties them all together.

Built with **Java 17** and **JavaFX 21**. Stores data locally in **SQLite**. Fully offline — no accounts, no cloud, no internet needed.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Environment Setup](#environment-setup)
4. [Dependency Installation](#dependency-installation)
5. [Configuration](#configuration)
6. [Execution](#execution)
7. [How to Use the App](#how-to-use-the-app)
8. [Build a Windows Installer](#build-a-windows-installer)
9. [Project Structure](#project-structure)
10. [Tech Stack](#tech-stack)
11. [Data Storage](#data-storage)
12. [Consistency Score](#consistency-score)
13. [Roadmap](#roadmap)

---

## Overview

Students use 3–4 separate apps to manage their daily routine — a study timer, a to-do list, an expense tracker, and a habit tracker. This fragmentation makes it hard to see how consistent you actually are.

**Anchor** puts all four into one app. They share the same date-based data model, and the dashboard aggregates them into a single **consistency score** with weekly charts and a 60-day activity heatmap.

---

## Features

- **Study Timer** — Pomodoro-style with configurable session and break lengths; auto-logs completed sessions
- **To-Do List** — Tasks with due dates, priorities, categories, and overdue highlighting
- **Expense Tracker** — Log spending with categories; view monthly totals and a category pie chart
- **Habit Tracker** — Create habits, tick them daily, and build streaks
- **Unified Dashboard** — Consistency score, weekly bar/line charts, and a GitHub-style activity heatmap

---

## Environment Setup

Before running Anchor, install these two tools on your machine.

### 1. Java Development Kit (JDK) 17 or newer

Download and install the **Eclipse Temurin JDK 21 (LTS)**:

- **Download link:** https://adoptium.net/temurin/releases/?version=21
- Choose: **Operating System → Windows**, **Architecture → x64**, **Package Type → JDK**, **Version → 21 — LTS**
- Download the `.msi` installer and run it
- During installation, enable **"Add to PATH"** and **"Set JAVA_HOME variable"**

**Verify the installation** — open Command Prompt and run:

```cmd
java -version
```

You should see output like:

```
openjdk version "21.0.x" ...
```

### 2. Apache Maven 3.8 or newer

- **Download link:** https://maven.apache.org/download.cgi
- Download the **Binary zip** (e.g., `apache-maven-3.9.x-bin.zip`)
- Extract to `C:\Program Files\Apache\maven`
- Add `C:\Program Files\Apache\maven\bin` to your system **PATH** environment variable

**Verify the installation:**

```cmd
mvn -version
```

You should see output like:

```
Apache Maven 3.9.x
Maven home: C:\Program Files\Apache\maven
Java version: 21.0.x
```

### 3. Git (only if you want to clone the repository)

If Git isn't installed, download it from https://git-scm.com/download/win

---

## Dependency Installation

Anchor uses three external libraries, all managed automatically by Maven. **You do not need to install them manually.**

| Dependency | Purpose | Version |
|------------|---------|---------|
| `org.openjfx:javafx-controls` | JavaFX UI components | 21.0.2 |
| `org.openjfx:javafx-fxml` | JavaFX FXML support | 21.0.2 |
| `org.xerial:sqlite-jdbc` | SQLite database driver | 3.45.1.0 |

When you run Maven for the first time, it automatically downloads these from Maven Central into your local `~/.m2/repository` folder. **This requires an internet connection for the first build only.** After that, the project builds offline.

**To verify the dependencies download correctly:**

```cmd
mvn dependency:resolve
```

You should see each library listed, and the build should end with:

```
BUILD SUCCESS
```

---

## Configuration

Anchor requires **no manual configuration**. The following defaults apply out of the box:

| Setting | Default Value |
|---------|--------------|
| Database location | `C:\Users\<your-username>\.anchor\anchor.db` |
| Default session length | 25 minutes |
| Default break length | 5 minutes |
| Default expense categories | Food, Transport, Books, Subscriptions, Other |
| Theme | Dark (Apple HIG-inspired) |
| Window mode | Maximized |
| Focus/break notifications | In-app dialog |

The database file and its parent folder are created automatically on first launch. No environment variables, no config files, no API keys.

---

## Execution

### Step 1: Clone the repository

```cmd
git clone https://github.com/Jazz1-6/Anchor.git
cd Anchor
```

(If you don't have Git, download the ZIP from the repository page and extract it.)

### Step 2: Compile and run

From the project root folder (where `pom.xml` lives):

```cmd
mvn clean javafx:run
```

**What this does:**

1. `clean` — removes any previous build artifacts
2. `javafx:run` — compiles all sources, resolves dependencies, and launches the app

The first run may take 30–90 seconds while Maven downloads JavaFX and SQLite. Subsequent runs are much faster (5–10 seconds).

### Step 3: The app opens

A window launches in maximized mode. The sidebar shows five modules: **Dashboard, Timer, To-Dos, Expenses, Habits**.

That's it. No further setup needed.

### Step 4 (optional): Verify build success

If you want to confirm compilation works without launching the UI:

```cmd
mvn clean package
```

This produces `target/anchor-1.0.jar` — a fat JAR containing the app and all dependencies. Look for `BUILD SUCCESS` at the end.

---

## How to Use the App

### Study Timer

1. Click **Timer** in the sidebar
2. (Optional) Type what you're studying
3. Set session and break lengths using the spinners
4. Click **Start** — the timer counts down and shows a "Focusing" status
5. When the session ends, it auto-logs and prompts for a break
6. Click **Stop & Save** to log a partial session early

### To-Do List

1. Click **To-Dos** in the sidebar
2. Enter a task title
3. (Optional) Pick a due date, priority, and category
4. Click **Add Task**
5. Check the box next to a task to mark it complete
6. Select a task → **Delete** to remove (with Undo option in the toast)

### Expense Tracker

1. Click **Expenses** in the sidebar
2. Enter amount, category, date, and (optionally) a note
3. Click **Add Expense**
4. The pie chart and monthly total update instantly
5. Select an expense → **Delete Selected** to remove it

### Habit Tracker

1. Click **Habits** in the sidebar
2. Type a habit name (e.g., "Read 20 pages")
3. Click **Add Habit**
4. Tick the checkbox daily to mark it complete
5. Streak counter updates automatically
6. Click **Remove** on a habit to delete it and its history

### Dashboard

1. Click **Dashboard** in the sidebar
2. See today's consistency score and stat cards for all modules
3. Scroll down for weekly charts and the 60-day activity heatmap

### Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl + 1` | Dashboard |
| `Ctrl + 2` | Timer |
| `Ctrl + 3` | To-Dos |
| `Ctrl + 4` | Expenses |
| `Ctrl + 5` | Habits |
| `Ctrl + Z` | Undo last delete |
| `Ctrl + Q` | Quit |

---

## Build a Windows Installer

Builds a standalone `.exe` that installs the app and bundles its own Java runtime — end users do NOT need Java installed.

**Requirements:**

- JDK 21 (LTS) — for `jpackage`
- [WiX Toolset v3](https://github.com/wixtoolset/wix3/releases) — required by jpackage on Windows

### Steps

```cmd
mvn clean package
mkdir staging
copy target\anchor-1.0.jar staging\
jpackage --type exe --name Anchor ^
  --input staging ^
  --main-jar anchor-1.0.jar ^
  --main-class com.studentdashboard.Launcher ^
  --app-version 1.0 --vendor "Student" ^
  --win-shortcut --win-menu ^
  --dest dist
```

The installer is created at `dist\Anchor-1.0.exe`. Double-click it to install.

**Note on `--main-class`:** The entry point is `com.studentdashboard.Launcher`, not `Main`. This is intentional — see [Project Structure](#project-structure) for the reason.

---

## Project Structure

```
Anchor/
├── pom.xml
├── README.md
├── .gitignore
└── src/
    └── main/
        ├── java/com/studentdashboard/
        │   ├── Launcher.java              # JVM entry point
        │   ├── Main.java                  # JavaFX Application class
        │   ├── dao/                       # SQLite queries
        │   │   ├── DashboardDAO.java
        │   │   ├── ExpenseDAO.java
        │   │   ├── HabitDAO.java
        │   │   ├── StudyDAO.java
        │   │   └── TaskDAO.java
        │   ├── db/
        │   │   └── Database.java          # Schema and connection
        │   ├── model/                     # Data classes
        │   │   ├── Expense.java
        │   │   ├── Habit.java
        │   │   ├── HabitLog.java
        │   │   ├── StudySession.java
        │   │   └── Task.java
        │   ├── ui/                        # JavaFX views
        │   │   ├── AppleAlert.java
        │   │   ├── DashboardView.java
        │   │   ├── ExpenseComponent.java
        │   │   ├── HabitComponent.java
        │   │   ├── MainWindow.java
        │   │   ├── TimerComponent.java
        │   │   └── TodoComponent.java
        │   └── util/
        │       ├── ConsistencyScore.java
        │       ├── DateUtil.java
        │       ├── Toast.java
        │       └── UndoManager.java
        └── resources/
            └── styles.css                 # Application theme
```

**Why `Launcher` and `Main` are separate:** When a shaded JAR's main class extends `javafx.application.Application`, the JVM throws the error `JavaFX runtime components are missing`. Using a plain `Launcher` class (that doesn't extend `Application`) as the entry point bypasses this check. This is a well-known pattern for shaded JavaFX applications.

---

## Tech Stack

| Layer | Choice |
|-------|--------|
| Language | Java 17 |
| UI Framework | JavaFX 21.0.2 |
| Charts | JavaFX Charts |
| Storage | SQLite (via `sqlite-jdbc`) |
| Build | Apache Maven + `maven-shade-plugin` |
| Packaging | JDK's `jpackage` |
| Version Control | Git |

---

## Data Storage

All data is stored in a single SQLite file:

```
C:\Users\<your-username>\.anchor\anchor.db
```

**Backup:** Copy this file somewhere safe.

**Reset:** Delete this file. The app recreates it fresh on next launch.

**Transfer:** Copy this file to the same path on another machine.

The app is fully offline. Nothing is transmitted anywhere.

---

## Consistency Score

Each day gets a score from 0 to 100, computed from your activity:

| Module | Weight | Target for full points |
|--------|--------|----------------------|
| Study | 30% | 60 minutes |
| Tasks | 20% | 3 completed |
| Spending | 20% | Under ₹500 |
| Habits | 30% | All habits done |

**Formula:**

```
Score = (Study% × 30) + (Tasks% × 20) + (Spending% × 20) + (Habits% × 30)
```

Where each percentage is capped at 100% and calculated relative to its target.

---

## Roadmap

Planned for future versions:

- CSV / PDF data export
- System tray notifications
- Customizable consistency weights
- Cross-platform builds (macOS, Linux)
- Optional encrypted cloud backup
- AI study assistant (note summarization, quiz generation)

---
