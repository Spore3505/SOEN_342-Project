
from datetime import datetime

from services import (
    AvailabilityService,
    AppointmentService,
    WaitlistService,
    PatientService
)
from models import Patient
from csv_service import CSVService


def manage_waitlist(waitlist_service, availability_service, appointment_service, patient_service):
    while True:
        print("\n===== Patient Waiting List =====")
        print("1. Add Patient to Waiting List")
        print("2. View Waiting List")
        print("3. Change Patient Priority")
        print("4. Create Phone Offer")
        print("5. Accept or Decline Phone Offer")
        print("6. Return to Main Menu")

        choice = input("\nEnter your choice: ")

        if choice == "1":
            patient_id = input("Enter patient ID: ").strip().upper()

            if patient_service.find_patient(patient_id) is None:
                print("Patient is not registered.")
                print("Please register the patient using option 8.")
                continue

            doctor_id = input(
                "Preferred doctor ID (Enter for any): "
            ).strip() or None

            specialty = input(
                "Preferred specialty (Enter for any): "
            ).strip() or None

            try:
                start_input = input(
                    "Requested start date YYYY-MM-DD (Enter to skip): "
                ).strip()

                end_input = input(
                    "Requested end date YYYY-MM-DD (Enter to skip): "
                ).strip()

                start_date = (
                    datetime.strptime(start_input, "%Y-%m-%d").date()
                    if start_input else None
                )

                end_date = (
                    datetime.strptime(end_input, "%Y-%m-%d").date()
                    if end_input else None
                )

                priority = int(
                    input("Enter priority (1 = highest): ")
                )

                entry = waitlist_service.add_to_waitlist(
                    patient_id=patient_id,
                    preferred_professional_id=doctor_id,
                    preferred_specialty=specialty,
                    requested_start_date=start_date,
                    requested_end_date=end_date,
                    priority=priority
                )

                print("\nPatient added successfully!")
                print(entry)

            except ValueError as error:
                print(f"Invalid input: {error}")

        elif choice == "2":
            entries = waitlist_service.view_waitlist()

            if not entries:
                print("The waiting list is empty.")
            else:
                print("\nCurrent Waiting List:")

                for entry in entries:
                    print(entry)

        elif choice == "3":
            entry_id = input(
                "Enter waiting list entry ID: "
            ).strip()

            try:
                new_priority = int(
                    input("Enter new priority: ")
                )

                changed = waitlist_service.change_priority(
                    entry_id, new_priority
                )

                if changed:
                    print("Priority updated successfully!")
                else:
                    print("Waiting list entry not found.")

            except ValueError as error:
                print(f"Invalid input: {error}")

        elif choice == "4":
            schedule_id = input(
                "Enter available schedule ID: "
            ).strip()

            schedule = next(
                (
                    s for s in availability_service.schedules
                    if s.schedule_id == schedule_id
                ),
                None
            )

            if schedule is None or not schedule.is_available():
                print("Appointment slot not found or unavailable.")
                continue

            doctor_specialty = input(
                "Enter doctor's specialty (Enter if unknown): "
            ).strip() or None

            eligible = waitlist_service.find_eligible_entries(
                schedule,
                doctor_specialty
            )

            if not eligible:
                print("No eligible waiting-list patients found.")
                continue

            print("\nEligible Patients (Priority Order):")

            for entry in eligible:
                print(entry)

            entry_id = input(
                "\nEnter waiting-list entry ID to offer: "
            ).strip()

            offer = waitlist_service.create_phone_offer(
                entry_id,
                schedule,
                doctor_specialty
            )

            if offer:
                print("\nPhone offer recorded successfully!")
                print(offer)

        elif choice == "5":
            pending_offers = [
                offer for offer in waitlist_service.offers
                if offer.outcome == "Pending"
            ]

            if not pending_offers:
                print("No pending phone offers.")
                continue

            print("\nPending Phone Offers:")

            for offer in pending_offers:
                print(offer)

            offer_id = input(
                "\nEnter offer ID: "
            ).strip().upper()

            response = input(
                "Did the patient accept or decline? (A/D): "
            ).strip().lower()

            if response not in ("a", "d"):
                print("Invalid response. Enter A or D.")
                continue

            accepted = response == "a"
            remove_if_declined = False

            if not accepted:
                remove = input(
                    "Remove patient from waiting list? (Y/N): "
                ).strip().lower()

                remove_if_declined = remove in ("y", "yes")

            result = waitlist_service.record_offer_outcome(
                offer_id=offer_id,
                accepted=accepted,
                appointment_service=appointment_service,
                remove_if_declined=remove_if_declined
            )

            if result:
                if accepted:
                    print("\nPhone offer accepted!")
                    print("Appointment booked successfully:")
                    print(result)
                else:
                    print("\nPhone offer declined!")
                    print(result)

                    if remove_if_declined:
                        print("Patient removed from waiting list.")
                    else:
                        print("Patient remains on waiting list.")

        elif choice == "6":
            print("Returning to Main Menu...")
            break

        else:
            print("Invalid choice. Please try again.")


def main():
    availability_service = AvailabilityService()
    csv_service = CSVService(availability_service)
    appointment_service = AppointmentService(availability_service)
    waitlist_service = WaitlistService()
    patient_service = PatientService()

    while True:
        print("\n===== Appointment Tracking System =====")
        print("1. Book Appointment")
        print("2. Search Availability")
        print("3. Make Availability")
        print("4. Import CSV")
        print("5. Export CSV")
        print("6. Manage Patient Waiting List")
        print("7. Cancel Appointment")
        print("8. Register Patient")
        print("9. Exit")

        choice = input("\nEnter your choice: ")

        if choice == "1":
            patient_id = input("Enter patient ID: ").strip().upper()

            if patient_service.find_patient(patient_id) is None:
                print("Patient is not registered.")
                print("Please register the patient using option 8.")
                continue

            schedule_id = input("Enter schedule ID: ").strip().upper()

            appointment = appointment_service.book_appointment(
                patient_id, schedule_id
            )

            if appointment:
                print("\nBooking successful!")
                print(appointment)

        elif choice == "2":
            professional_id = input(
                "Enter doctor ID: "
            ).strip()

            slots = availability_service.search_availability(
                professional_id
            )

            if slots:
                print("\nAvailable appointments:")
                for slot in slots:
                    print(slot)
            else:
                print("No available appointments found.")

        elif choice == "3":
            try:
                professional_id = input(
                    "Enter doctor ID: "
                ).strip()

                start_date = datetime.strptime(
                    input("Enter start date (YYYY-MM-DD): "),
                    "%Y-%m-%d"
                ).date()

                end_date = datetime.strptime(
                    input("Enter end date (YYYY-MM-DD): "),
                    "%Y-%m-%d"
                ).date()

                print("\nSelect repetition type:")
                print("1. Weekly")
                print("2. Monthly")

                repetition = input("Enter your choice: ").strip()

                if repetition == "1":
                    print("\nDays: 0=Monday, 1=Tuesday, 2=Wednesday,")
                    print("3=Thursday, 4=Friday, 5=Saturday, 6=Sunday")

                    weekdays = [
                        int(day.strip())
                        for day in input(
                            "Enter weekdays (e.g. 0,2,4): "
                        ).split(",")
                    ]

                    if any(day < 0 or day > 6 for day in weekdays):
                        raise ValueError(
                            "Weekdays must be between 0 and 6."
                        )

                elif repetition == "2":
                    day_of_month = int(
                        input("Enter day of month (1-31): ")
                    )

                else:
                    print("Invalid repetition type.")
                    continue

                start_time = datetime.strptime(
                    input("Enter start time (HH:MM): "),
                    "%H:%M"
                ).time()

                end_time = datetime.strptime(
                    input("Enter end time (HH:MM): "),
                    "%H:%M"
                ).time()

                slot_minutes = int(
                    input("Enter slot duration in minutes: ")
                )

                if repetition == "1":
                    new_slots = (
                        availability_service.make_recurring_availability(
                            professional_id,
                            start_date,
                            end_date,
                            weekdays,
                            start_time,
                            end_time,
                            slot_minutes
                        )
                    )
                else:
                    new_slots = (
                        availability_service.make_monthly_availability(
                            professional_id,
                            start_date,
                            end_date,
                            day_of_month,
                            start_time,
                            end_time,
                            slot_minutes
                        )
                    )

                print(
                    f"\nSuccessfully created {len(new_slots)} slots!"
                )

                for slot in new_slots:
                    print(slot)

            except ValueError as error:
                print(f"Invalid input: {error}")

        elif choice == "4":
            filename = input(
                "Enter CSV filename "
                "(press Enter for availability.csv): "
            ).strip()

            if not filename:
                filename = "availability.csv"

            try:
                count = csv_service.import_availability(
                    filename
                )

                print(
                    f"Successfully imported {count} slots."
                )

            except (FileNotFoundError, KeyError, ValueError) as error:
                print(f"Import failed: {error}")

        elif choice == "5":
            filename = input(
                "Enter export filename "
                "(press Enter for exported_availability.csv): "
            ).strip()

            if not filename:
                filename = "exported_availability.csv"

            try:
                count = csv_service.export_availability(
                    filename
                )

                print(
                    f"Successfully exported {count} slots "
                    f"to {filename}."
                )

            except OSError as error:
                print(f"Export failed: {error}")

        elif choice == "6":
            manage_waitlist(
                waitlist_service,
                availability_service,
                appointment_service,
                patient_service
            )

        elif choice == "7":
            appointment_id = input(
                "Enter appointment ID to cancel: "
            ).strip()

            cancelled_slot = appointment_service.cancel_appointment(
                appointment_id
            )

            if cancelled_slot:
                print("\nAppointment cancelled successfully!")
                print("The appointment slot is available again.")

                doctor_specialty = input(
                    "Enter doctor's specialty (Enter if unknown): "
                ).strip() or None

                eligible = waitlist_service.find_eligible_entries(
                    cancelled_slot,
                    doctor_specialty
                )

                if eligible:
                    print("\nEligible Waiting-List Patients:")

                    for entry in eligible:
                        print(entry)

                    print(
                        "\nAdministrative staff can contact "
                        "these patients by phone in priority order."
                    )

                else:
                    print("\nNo eligible waiting-list patients found.")

            else:
                print("Booked appointment not found.")

        elif choice == "8":
            print("\n===== Register New Patient =====")

            patient_id = input("Enter patient ID: ").strip().upper()
            first_name = input("Enter first name: ").strip()
            last_name = input("Enter last name: ").strip()
            dob_input = input(
                "Enter date of birth (YYYY-MM-DD): "
            ).strip()
            phone_number = input("Enter phone number: ").strip()

            if not all([
                patient_id, first_name, last_name,
                dob_input, phone_number
            ]):
                print("All fields are required.")
                continue

            try:
                date_of_birth = datetime.strptime(
                    dob_input, "%Y-%m-%d"
                ).date()

                patient = Patient(
                    patient_id,
                    first_name,
                    last_name,
                    date_of_birth,
                    phone_number
                )

                registered = patient_service.register_patient(patient)

                if registered:
                    print("\nPatient registered successfully!")
                    print(patient)

            except ValueError as error:
                print(f"Invalid input: {error}")

        elif choice == "9":
            print("Goodbye!")
            break

        else:
            print("Invalid choice. Please try again.")


if __name__ == "__main__":
    main()

