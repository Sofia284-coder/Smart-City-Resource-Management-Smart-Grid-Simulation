# Smart City Management System

A Java Swing desktop application for managing city infrastructure across multiple resource types — transport, power, and emergency services — with smart grid energy tracking, city zone management, reporting, and file-based persistence.

---

## Table of Contents

1. [Overview](#overview)
2. [Features](#features)
3. [Project Structure](#project-structure)
4. [Architecture](#architecture)
5. [Class Reference](#class-reference)
6. [Prerequisites](#prerequisites)
7. [How to Run](#how-to-run)
8. [Using the Application](#using-the-application)
9. [Persistence](#persistence)
10. [GitHub Setup — Step by Step](#github-setup--step-by-step)

---

## Overview

The Smart City Management System lets city administrators manage all urban resources from a single desktop interface. Resources are organised into categories (transport, power stations, emergency services), assigned to geographic city zones, and tracked through a smart energy grid. All data is saved to disk between sessions.

**Technology:** Pure Java 11+ with Swing — no external libraries or build tools required.

---

## Features

| Tab | What you can do |
|---|---|
| **Resource Management** | Add, update, and delete Buses, Trains, Power Stations, Police units, Fire Departments, and Ambulances via table-backed CRUD forms. Click any table row to load it into the form for editing. |
| **Smart Grid** | Track live energy production vs. consumption. Add household and industry consumers. One-click grid balancing automatically adjusts power station output to match demand. |
| **City Zones** | Create and remove geographic zones. Click any zone to see all transport, power, and emergency resources assigned to it. |
| **Reports** | Generate a full usage report across all resource types, or a per-resource maintenance cost breakdown with a running total. |
| **Alerts** | Real-time red-text feed of emergency alerts. Triggering a power station outage automatically alerts all emergency services in that zone and logs them here. |
| **File → Save / Load** | Persist all data to `.ser` files and reload across sessions. |

---

## Project Structure

```
SmartCityManagement/
├── README.md
├── .gitignore
└── src/
    └── main/
        └── java/
            └── com/
                └── smartcity/
                    │
                    ├── SmartCityApp.java
                    │
                    ├── interfaces/
                    │   ├── Alertable.java
                    │   └── Reportable.java
                    │
                    ├── model/
                    │   ├── CityResource.java
                    │   │
                    │   ├── transport/
                    │   │   ├── TransportUnit.java
                    │   │   ├── Bus.java
                    │   │   └── Train.java
                    │   │
                    │   ├── emergency/
                    │   │   ├── EmergencyService.java
                    │   │   ├── Police.java
                    │   │   ├── FireDepartment.java
                    │   │   └── Ambulance.java
                    │   │
                    │   ├── energy/
                    │   │   ├── PowerStation.java
                    │   │   ├── Consumer.java
                    │   │   ├── HouseholdConsumer.java
                    │   │   ├── IndustryConsumer.java
                    │   │   └── SmartGrid.java
                    │   │
                    │   └── zone/
                    │       ├── CityZone.java
                    │       └── ResourceHub.java
                    │
                    ├── repository/
                    │   └── CityRepository.java
                    │
                    └── gui/
                        ├── GuiContext.java
                        ├── DisplayUpdater.java
                        ├── SmartCityGUI.java
                        │
                        └── panels/
                            ├── BasePanel.java
                            ├── ResourceManagementPanel.java
                            ├── BusPanel.java
                            ├── TrainPanel.java
                            ├── PowerStationPanel.java
                            ├── PolicePanel.java
                            ├── FireDepartmentPanel.java
                            ├── AmbulancePanel.java
                            ├── SmartGridPanel.java
                            ├── HouseholdConsumerPanel.java
                            ├── IndustryConsumerPanel.java
                            ├── CityZonesPanel.java
                            ├── ReportsPanel.java
                            └── AlertsPanel.java
```

---

## Architecture

The codebase is divided into four layers that only ever communicate downward — the GUI knows about the model, but the model knows nothing about the GUI.

```
┌─────────────────────────────────────────────────┐
│                   gui/panels/                   │  ← One file per tab/sub-tab
│  BusPanel  TrainPanel  PowerStationPanel  ...   │
│               extend BasePanel                  │
└────────────────────┬────────────────────────────┘
                     │ reads/writes via
          ┌──────────▼──────────┐
          │     GuiContext      │  ← Single shared state object
          │  DisplayUpdater     │  ← All screen-refresh logic
          │  SmartCityGUI       │  ← Window orchestrator + Save/Load
          └──────────┬──────────┘
                     │ calls
          ┌──────────▼──────────┐
          │    repository/      │  ← Generic CRUD + serialization
          │  CityRepository<T>  │
          └──────────┬──────────┘
                     │ stores
          ┌──────────▼──────────┐
          │      model/         │  ← Pure Java, zero GUI imports
          │  interfaces/        │
          └─────────────────────┘
```

### Key design decisions

**`GuiContext`** is the single source of truth for shared mutable state — the repository, SmartGrid, zones map, all table models, and all text areas. Every panel receives a `GuiContext` reference. No panel holds its own copy of the data.

**`DisplayUpdater`** owns every screen-refresh method. After any data mutation, a panel calls `updater.refreshAll()` and every other panel's display automatically reflects the change. No panel calls another panel directly.

**`BasePanel`** is the superclass of every CRUD panel. It provides shared layout helpers (`buildFormPanel`, `buildButtonPanel`), `clearFields`, and the three error dialogs (`showInputError`, `showUpdateError`, `showDeleteError`), eliminating duplication across 10+ panels.

**`SmartCityGUI`** is an orchestrator only — it creates the context, instantiates panels, wires tab-change listeners, and handles Save/Load. No business logic lives there.

**The model layer** has zero Swing imports. `CityResource`, `SmartGrid`, `CityZone`, and `CityRepository` are plain Java and could be reused in a web or CLI context without modification.

---

## Class Reference

### `interfaces/`

| Class | Purpose |
|---|---|
| `Alertable` | Contract for resources that can send an emergency alert string (`sendEmergencyAlert()`) |
| `Reportable` | Contract for resources that can generate a usage report string (`generateUsageReport()`) |

---

### `model/`

#### `CityResource` *(abstract)*
Base class for every city-managed resource. Holds `resourceID`, `location`, and `status`. Declares `calculateMaintenanceCost()` as abstract so each subtype defines its own cost formula. Implements `Serializable` for file persistence.

#### Transport

| Class | Extends | Key field | Maintenance formula |
|---|---|---|---|
| `TransportUnit` *(abstract)* | `CityResource`, `Reportable` | `route` | — |
| `Bus` | `TransportUnit` | `numberOfPassengers` | `passengers × 5.0` |
| `Train` | `TransportUnit` | `numberOfCoaches` | `coaches × 20.0` |

#### Emergency Services

| Class | Extends | Key field | Maintenance formula |
|---|---|---|---|
| `EmergencyService` *(abstract)* | `CityResource`, `Reportable`, `Alertable` | — | — |
| `Police` | `EmergencyService` | `numberOfOfficers` | `officers × 100.0` |
| `FireDepartment` | `EmergencyService` | `numberOfFireTrucks` | `trucks × 150.0` |
| `Ambulance` | `EmergencyService` | `numberOfMedics` | `medics × 120.0` |

#### Energy

| Class | Purpose |
|---|---|
| `PowerStation` | Extends `CityResource`, implements `Reportable` and `Alertable`. Holds a `productionRate` and a reference to its `CityZone`. `triggerOutage()` sets status to `"Outage"` and calls `alertEmergencyServices()` on the zone. |
| `Consumer` *(abstract)* | Base for energy consumers. Holds `consumerID`, `type`, and `consumptionRate`. |
| `HouseholdConsumer` | Extends `Consumer`. Additional field: `familySize`. |
| `IndustryConsumer` | Extends `Consumer`. Additional field: `industryType`. |
| `SmartGrid` | Manages lists of `PowerStation` and `Consumer`. Tracks `totalEnergyProduced` and `totalEnergyConsumed`. `balanceGrid()` redistributes production rates across all stations to match demand. |

#### Zone

| Class | Purpose |
|---|---|
| `ResourceHub` | Groups `TransportUnit`, `PowerStation`, and `EmergencyService` lists for one zone. |
| `CityZone` | Represents a geographic zone. Owns a `ResourceHub`. `alertEmergencyServices()` broadcasts an alert from every emergency unit in the hub. |

---

### `repository/`

#### `CityRepository<T extends CityResource>`
Generic in-memory store with full CRUD and serialization-based persistence.

| Method | Description |
|---|---|
| `addResource(T)` | Adds a resource to the in-memory list |
| `removeResourceById(String)` | Removes by ID, returns `true` if found |
| `getResourceById(String)` | Returns the resource or `null` |
| `updateResource(T)` | Replaces the matching resource in place |
| `getAllResources()` | Returns a defensive copy of the full list |
| `getResourcesByLocation(String)` | Filters by location string |
| `getResourcesByStatus(String)` | Filters by status string |
| `countResources()` | Returns the number of stored resources |
| `saveToFile()` | Serializes the list to `city_resources.ser` |
| `loadFromFile()` | Deserializes from file, replacing the in-memory list |

---

### `gui/`

#### `GuiContext`
Passed to every panel on construction. Contains:
- `repository` — the `CityRepository` instance
- `smartGrid` — the `SmartGrid` instance
- `cityZonesMap` — `Map<String, CityZone>` of all zones
- All eight `DefaultTableModel` instances (one per resource/consumer type)
- All five `JTextArea` instances shared across tabs
- `tabbedPane` — injected after construction for change-listener hooks

#### `DisplayUpdater`
All display-refresh methods in one place. Panels must not refresh other panels directly.

| Method | What it refreshes |
|---|---|
| `refreshAll()` | Resource tables, all-resources text area, smart grid display, zone list |
| `refreshSmartGridDisplay()` | Energy stats text area and consumer table models |

#### `SmartCityGUI`
Thin orchestrator (~120 lines). Creates `GuiContext` and `DisplayUpdater`, instantiates all panels, builds the `JTabbedPane`, attaches a tab-change listener to refresh the zone combo box in `PowerStationPanel`, and handles the File → Save/Load menu actions.

---

### `gui/panels/`

| Class | Tab | Description |
|---|---|---|
| `BasePanel` | — | Abstract superclass. Provides `buildFormPanel()`, `buildButtonPanel()`, `clearFields()`, and three error-dialog helpers. |
| `ResourceManagementPanel` | Resource Management | Hosts Bus, Train, Power Station, and Emergency Services sub-tabs. Carries a `public powerStationPanel` field so `SmartCityGUI` can trigger zone combo refreshes. |
| `BusPanel` | → Buses | Full CRUD for `Bus`. Table row click loads the form. |
| `TrainPanel` | → Trains | Full CRUD for `Train`. |
| `PowerStationPanel` | → Power Stations | Full CRUD for `PowerStation` + Trigger Outage button. Zone combo box populated from `cityZonesMap`. |
| `PolicePanel` | → Police | Full CRUD for `Police`. |
| `FireDepartmentPanel` | → Fire Department | Full CRUD for `FireDepartment`. |
| `AmbulancePanel` | → Ambulance | Full CRUD for `Ambulance`. |
| `SmartGridPanel` | Smart Grid | Live energy stats, Household and Industry consumer sub-tabs, Refresh and Balance Grid buttons. |
| `HouseholdConsumerPanel` | → Household Consumers | Full CRUD for `HouseholdConsumer` via `SmartGrid`. |
| `IndustryConsumerPanel` | → Industry Consumers | Full CRUD for `IndustryConsumer` via `SmartGrid`. |
| `CityZonesPanel` | City Zones | Zone list with Add/Remove. Selecting a zone shows all its resources in the detail area. |
| `ReportsPanel` | Reports | Generates usage reports (per resource type) or maintenance cost breakdown. |
| `AlertsPanel` | Alerts | Displays all emergency alert strings in red. Clear button resets the feed. |

---

## Prerequisites

- **Java 11 or higher** (Java 17 LTS recommended)
- No external libraries or build tools needed — pure Java SE + Swing

Check your version:
```bash
java -version
```

---

## How to Run

### Command line

```bash
# 1. Go to the source root
cd SmartCityManagement/src/main/java

# 2. Compile every .java file into an out/ directory
javac -d out $(find . -name "*.java")

# 3. Run
java -cp out com.smartcity.SmartCityApp
```

### IntelliJ IDEA

1. **File → Open** → select the `SmartCityManagement` folder.
2. Right-click `src/main/java` → **Mark Directory as → Sources Root**.
3. Open `SmartCityApp.java` → click the green ▶ button.

### VS Code

1. Install the **Extension Pack for Java** from the marketplace.
2. Open the `SmartCityManagement` folder.
3. Open `SmartCityApp.java` → click **Run** above the `main` method.

---

## Using the Application

### Adding a resource
1. Select the relevant sub-tab (e.g. **Buses**).
2. Fill in the form fields at the top.
3. Click **Add Bus** (or the equivalent button for the resource type).
4. The new entry appears immediately in the table and in the **All City Resources** sidebar.

### Editing a resource
1. Click any row in the table — the form auto-fills with that row's data.
2. Change the fields you want to update.
3. Click **Update**.

### Deleting a resource
1. Either click a table row or type the ID manually into the ID field.
2. Click **Delete**.

### Triggering a power outage
1. Go to **Resource Management → Power Stations**.
2. Select or type the ID of a power station.
3. Click **Trigger Outage**.
4. The station's status changes to `Outage`, and all emergency services in its assigned zone send alerts that appear in the **Alerts** tab.

### Balancing the smart grid
1. Go to the **Smart Grid** tab.
2. The energy stats panel at the top shows current production, consumption, and net energy.
3. Click **Balance Grid** — production rates across all power stations are automatically adjusted to match total consumption.

### Managing zones
1. Go to **City Zones**.
2. Click **Add Zone** and enter a name.
3. When adding a Power Station, select the zone from the dropdown.
4. Transport and emergency resources are assigned to a zone by setting their **Location** to the zone's name.
5. Click any zone in the list to see all its assigned resources.

### Generating reports
1. Go to the **Reports** tab.
2. **Generate All Reports** — lists usage summaries for every resource.
3. **Generate Maintenance Cost Report** — shows the per-resource cost and the city-wide total.

---

## Persistence

Data is saved to three files in the working directory (the folder you run the app from):

| File | Contents |
|---|---|
| `city_resources.ser` | All `CityResource` objects (buses, trains, stations, emergency units) |
| `city_zones.ser` | All `CityZone` objects and zone names |
| `smart_grid_consumers.ser` | All `Consumer` objects registered with the Smart Grid |

Use **File → Save All Data** to write the files, and **File → Load All Data** to restore them. The app also attempts to load automatically on startup.

These `.ser` files are excluded from Git via `.gitignore` — they are local data, not source code.

---

## GitHub Setup — Step by Step

### 1. Install Git
Download from https://git-scm.com/downloads and run the installer.

```bash
git --version   # verify
```

### 2. Configure your identity (one-time)
```bash
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

### 3. Create the repository on GitHub
1. Go to https://github.com → click **+** (top right) → **New repository**.
2. Set the name to `SmartCityManagement`.
3. Leave **"Add a README"** unticked — you already have one.
4. Click **Create repository**.
5. Copy the HTTPS URL shown (e.g. `https://github.com/YOUR-USERNAME/SmartCityManagement.git`).

### 4. Initialise Git and make the first commit
```bash
cd path/to/SmartCityManagement

git init
git add .
git commit -m "Initial commit: Smart City Management System"
```

### 5. Push to GitHub
```bash
git remote add origin https://github.com/YOUR-USERNAME/SmartCityManagement.git
git branch -M main
git push -u origin main
```

### 6. Future updates
```bash
git add .
git commit -m "Short description of what changed"
git push
```

---

## OOP Concepts Demonstrated

| Concept | Where |
|---|---|
| **Inheritance** | `Bus` and `Train` extend `TransportUnit`; `Police`, `FireDepartment`, `Ambulance` extend `EmergencyService`; all extend `CityResource` |
| **Abstract classes** | `CityResource`, `TransportUnit`, `EmergencyService`, `Consumer` — enforce `calculateMaintenanceCost()` in every leaf class |
| **Interfaces** | `Alertable` and `Reportable` applied to appropriate resource types independently of the inheritance hierarchy |
| **Generics** | `CityRepository<T extends CityResource>` — one repository class handles every resource type |
| **Polymorphism** | `CityRepository` stores all resources as `CityResource`; reports iterate the list and cast at runtime |
| **Encapsulation** | All model fields private with getters/setters; `CityRepository.getAllResources()` returns a defensive copy |
| **Composition** | `CityZone` owns a `ResourceHub`; `SmartGrid` owns lists of `PowerStation` and `Consumer` |
| **Serialization** | `CityResource`, `Consumer`, `CityZone`, and `ResourceHub` implement `Serializable` for file persistence |
