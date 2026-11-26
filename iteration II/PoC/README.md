# Appointment Tracking System — Proof of Concept

## SOEN 342 — Iteration II

This project is a Java-based proof of concept (PoC) for a healthcare Appointment Tracking System.

The system allows administrative staff to manage patient appointments, healthcare professional availability, and patient waiting lists.

## Features

1. **Patient Registration:** Register patients and validate patient IDs.
2. **Book Appointments:** Book available appointment slots for registered patients.
3. **Search Availability:** Search available appointments by healthcare professional.
4. **Create Availability:** Generate weekly or monthly recurring appointment schedules.
5. **Import Availability:** Import appointment availability from CSV files.
6. **Export Availability:** Export the complete availability dataset to CSV.
7. **Cancel Appointments:** Cancel bookings and release appointment slots.
8. **Patient Waiting List:** Add patients with preferred professionals, specialties, and date ranges.
9. **Priority Management:** Change patient priorities in the waiting list.
10. **Phone Offers:** Offer available appointments to waiting-list patients and record acceptance or rejection.
11. **Reservation Protection:** Prevent other patients from booking appointment slots reserved for pending phone offers.

## Project Files

- `Main.java` — Command-line interface and main menu.
- `Models.java` — Patient, healthcare professional, appointment, schedule, waiting-list, and offer classes.
- `Services.java` — Business logic for appointments, availability, patients, and waiting lists.
- `CSVService.java` — CSV import and export functionality.
- `availability.csv` — Sample availability data.
- `exported_availability.csv` — Original example export.
- `SmokeTest.java` — Automated smoke tests.

## How to Run

**Requirements:** JDK 11 or newer. No external Java libraries are required.

Open a terminal in the PoC folder and run:

```sh
javac *.java
java Main
```

The system displays a menu where administrative staff can access its features.

## Data Storage

This proof of concept uses in-memory storage for patients, appointments, waiting lists, and offers.

Availability schedules can be imported and exported using CSV files.

Registered patients, appointments, and waiting-list records are not automatically saved when the program closes.

## Testing

The included smoke test covers the following workflows. Run `java -ea SmokeTest` after compilation.

- Booking and preventing double-booking.
- Weekly and monthly availability generation.
- CSV import and export.
- Appointment cancellation.
- Waiting-list eligibility and priority management.
- Accepting and declining phone offers.
- Reserving slots for pending offers.
- Patient registration and ID validation.

## Limitations

This is a command-line proof of concept, not a production healthcare application.

It does not include a database, automatic phone calls, or persistent patient records. Phone offers are recorded by administrative staff.

## Course

SOEN 342 — Software Requirements and Design  
Concordia University  
Iteration II — Appointment Tracking System
## Java conversion notes

The Java version retains the command-line menus and in-memory design. Java camelCase names replace Python snake_case names. As in the Python version, CSV exports persist booked status only, not pending offers or appointment identities; importing a booked slot does not create a cancellable appointment record. A pending offer is shown as Booked in the schedule display, matching the original. Java date input uses ISO dates.
