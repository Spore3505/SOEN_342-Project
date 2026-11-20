# SOEN 342 — Appointment Tracking System

**Concordia University | Fall 2026**

## Team Members

- Muhammad Mobin Syed (40299024)
- Jotnivaas Gill (40260752)
- Elias Salgar-Moshonas (40292962)

## Project Overview

This project focuses on the analysis, design, and development of an **Appointment Tracking System** for healthcare clinics.

The system is designed to support patient registration, appointment scheduling, healthcare professional availability, consultations, referrals, prescriptions, and billing.

The project is developed over multiple iterations, using UML models to analyze system requirements and design the software.

---

## Iteration I — Requirements Analysis

Iteration I focuses on identifying system requirements and creating UML models to describe the system's functionality.

### Deliverables

1. UML Use Case Diagram
2. UML Domain Model
3. System Sequence Diagrams (SSDs)
4. Three Fully-Dressed Critical Use Cases
5. System Operations and Operation Contracts

### Critical Use Cases

- **UC1:** Book Appointment
- **UC2:** Conduct Consultation
- **UC3:** Issue Referral

### Iteration I Files

[View Iteration I Deliverables](Iteration_1/)

[View Iteration I Final Report](Iteration_1/SOEN342_Iteration1_Final.pdf)

---

## Iteration II — System Design and Proof of Concept

Iteration II extends the existing system by introducing additional requirements, including patient waiting lists and resource allocation.

This iteration also includes updated UML models, object-oriented design diagrams, and a working Python proof of concept.

### Deliverables

1. Updated UML Use Case Diagram
2. Updated UML Domain Model
3. Updated System Sequence Diagrams
4. UML Interaction Diagrams
5. UML Class Diagram
6. Python Proof of Concept (PoC)

### UML Diagrams

- [UML Use Case Diagram](iteration%20II/UML%20Use%20case%20Diagram/)
- [UML Domain Model](iteration%20II/UML%20Domain%20Model/)
- [Updated System Sequence Diagrams](iteration%20II/Updated%20SSDs/)
- [UML Interaction Diagrams](iteration%20II/UML%20interaction%20diagrams/)
- [UML Class Diagram](iteration%20II/UML%20Class%20Diagram/)

[View All Iteration II Deliverables](iteration%20II/)

---

## Python Proof of Concept

A command-line application developed in Python demonstrates the main appointment scheduling functionality.

### Core Features

1. **Book Appointments** — Book available appointment slots for registered patients.
2. **Search Availability** — Search available appointments by healthcare professional.
3. **Create Availability** — Generate recurring appointment schedules with weekly or monthly repetition.
4. **Import Availability** — Import healthcare professional schedules from CSV files.
5. **Export Availability** — Export the complete availability dataset to CSV.

### Additional Features

- Patient registration and ID validation
- Appointment cancellation
- Patient waiting-list management
- Preferred healthcare professional and specialty selection
- Requested appointment date ranges
- Waiting-list priority management
- Identification of eligible patients after cancellations
- Appointment offers recorded by administrative staff
- Acceptance and rejection of phone offers
- Temporary reservation of appointment slots while offers are pending
- Prevention of double-booking

### PoC Project Structure

```text
iteration II/
└── PoC/
    ├── main.py
    ├── models.py
    ├── services.py
    ├── csv_service.py
    ├── availability.csv
    ├── exported_availability.csv
    └── README.md
```

### How to Run the PoC

**Requirements:**

- Python 3
- No external Python libraries required

**Step 1:** Clone the repository.

```bash
git clone https://github.com/Spore3505/SOEN_342-Project.git
```

**Step 2:** Navigate to the PoC folder.

```bash
cd SOEN_342-Project
cd "iteration II/PoC"
```

**Step 3:** Run the application.

```bash
python main.py
```

The application displays an interactive menu allowing administrative staff to access the system's features.

For additional information, see the [PoC README](iteration%20II/PoC/README.md).

---

## Testing

The Python PoC has been tested for the following functionality:

- Patient registration and validation
- Appointment booking
- Availability searching
- Weekly and monthly schedule generation
- CSV import and export
- Appointment cancellation
- Waiting-list registration and priority changes
- Identification of eligible waiting-list patients
- Acceptance and rejection of phone offers
- Prevention of double-booking and booking of reserved slots

## Implementation Details

The proof of concept uses object-oriented programming in Python.

The application is organized into separate classes and services responsible for patient management, appointment booking, availability scheduling, waiting-list management, and CSV operations.

### Data Storage

The PoC stores patients, appointments, waiting-list entries, and phone offers in memory.

Healthcare professional availability can be imported from and exported to CSV files.

The application does not use a database, and in-memory records are not preserved after the program closes.

### Limitations

The PoC is intended to demonstrate selected features rather than implement the complete healthcare system.

Consultations, prescriptions, referrals, billing, and resource allocation are not implemented in the Python prototype.

Phone offers are recorded manually by administrative staff rather than through an automated calling service.

---

## Technologies Used

- **Python 3** — Proof of Concept implementation
- **PlantUML** — UML diagram creation
- **Git and GitHub** — Version control and collaboration
- **Visual Studio Code** — Python development

## Repository Organization

- [Iteration I](Iteration_1/) — Requirements analysis and UML models
- [Iteration II](iteration%20II/) — Updated UML models, object-oriented design, and Python PoC
- [Python PoC](iteration%20II/PoC/) — Source code, sample CSV data, and execution instructions

---

## Course Information

**Course:** SOEN 342 — Software Requirements and Deployment

**Institution:** Concordia University

**Semester:** Fall 2026
