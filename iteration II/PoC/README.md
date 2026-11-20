# Appointment Tracking System — Proof of Concept

## SOEN 342 — Iteration II

This project is a Python-based proof of concept (PoC) for a healthcare Appointment Tracking System.

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

- `main.py` — Command-line interface and main menu.
- `models.py` — Patient, healthcare professional, appointment, schedule, waiting-list, and offer classes.
- `services.py` — Business logic for appointments, availability, patients, and waiting lists.
- `csv_service.py` — CSV import and export functionality.
- `availability.csv` — Sample availability data.

## How to Run

**Requirements:** Python 3. No external Python libraries are required.

Open a terminal in the PoC folder and run:

`python main.py`

The system displays a menu where administrative staff can access its features.

## Data Storage

This proof of concept uses in-memory storage for patients, appointments, waiting lists, and offers.

Availability schedules can be imported and exported using CSV files.

Registered patients, appointments, and waiting-list records are not automatically saved when the program closes.

## Testing

The following workflows have been tested:

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