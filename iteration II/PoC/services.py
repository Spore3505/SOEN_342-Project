from datetime import datetime, date, time, timedelta
from models import (
    Patient,
    ProfessionalSchedule,
    Appointment,
    WaitlistEntry,
    AppointmentOffer
)

class PatientService:
    def __init__(self):
        self.patients = {}

    def register_patient(self, patient):
        if patient.patient_id in self.patients:
            print("Patient ID already exists.")
            return False

        self.patients[patient.patient_id] = patient
        return True

    def find_patient(self, patient_id):
        return self.patients.get(patient_id)


class AvailabilityService:
    def __init__(self):
        self.schedules = []

    def add_schedule(self, schedule):
        self.schedules.append(schedule)

    def search_availability(self, professional_id):
        available_slots = []

        for schedule in self.schedules:
            if (schedule.professional_id == professional_id
                    and schedule.is_available()):
                available_slots.append(schedule)

        return available_slots

    
    def make_recurring_availability(
        self, professional_id, start_date, end_date,
        weekdays, start_time, end_time, slot_minutes=30
    ):
        if start_date > end_date:
            raise ValueError("Start date must be before end date.")

        if start_time >= end_time or slot_minutes <= 0:
            raise ValueError("Invalid time range or slot duration.")

        current_date = start_date
        created_slots = []

        while current_date <= end_date:

            if current_date.weekday() in weekdays:

                slot_start = datetime.combine(
                    current_date, start_time
                )

                day_end = datetime.combine(
                    current_date, end_time
                )

                while slot_start + timedelta(minutes=slot_minutes) <= day_end:

                    slot_end = slot_start + timedelta(
                        minutes=slot_minutes
                    )

                    # Prevent overlapping appointment slots
                    overlap = any(
                        s.professional_id == professional_id
                        and slot_start < s.end_datetime
                        and slot_end > s.start_datetime
                        for s in self.schedules
                    )

                    if not overlap:
                        schedule_id = f"S{len(self.schedules) + 1:03d}"

                        schedule = ProfessionalSchedule(
                            schedule_id,
                            professional_id,
                            slot_start,
                            slot_end
                        )

                        self.add_schedule(schedule)
                        created_slots.append(schedule)

                    slot_start = slot_end

            current_date += timedelta(days=1)

        return created_slots

    def make_monthly_availability(
        self, professional_id, start_date, end_date,
        day_of_month, start_time, end_time, slot_minutes=30
    ):
        if start_date > end_date:
            raise ValueError("Invalid date range.")

        if not 1 <= day_of_month <= 31:
            raise ValueError("Day of month must be between 1 and 31.")

        if start_time >= end_time or slot_minutes <= 0:
            raise ValueError("Invalid time range or slot duration.")

        created_slots = []
        year, month = start_date.year, start_date.month

        while (year, month) <= (end_date.year, end_date.month):
            # Some dates (e.g., February 30) do not exist.
            try:
                current_date = date(year, month, day_of_month)
            except ValueError:
                current_date = None

            if current_date is not None and start_date <= current_date <= end_date:
                slot_start = datetime.combine(current_date, start_time)
                day_end = datetime.combine(current_date, end_time)

                while slot_start + timedelta(minutes=slot_minutes) <= day_end:
                    slot_end = slot_start + timedelta(minutes=slot_minutes)

                    # Avoid overlapping slots for the same doctor.
                    overlap = any(
                        s.professional_id == professional_id
                        and slot_start < s.end_datetime
                        and slot_end > s.start_datetime
                        for s in self.schedules
                    )

                    if not overlap:
                        schedule_id = f"S{len(self.schedules) + 1:03d}"
                        schedule = ProfessionalSchedule(
                            schedule_id,
                            professional_id,
                            slot_start,
                            slot_end
                        )
                        self.add_schedule(schedule)
                        created_slots.append(schedule)

                    slot_start = slot_end

            # Advance to the following calendar month.
            if month == 12:
                year += 1
                month = 1
            else:
                month += 1

        return created_slots


class AppointmentService:
    def __init__(self, availability_service):
        self.availability_service = availability_service
        self.appointments = []

    def book_appointment(self, patient_id, schedule_id, offer_id=None):

        for schedule in self.availability_service.schedules:

            if schedule.schedule_id == schedule_id:

                
                if schedule.booked:
                    print("This appointment slot is already booked.")
                    return None

                if (
                    schedule.pending_offer_id is not None
                    and schedule.pending_offer_id != offer_id
                ):
                    print("This slot is reserved for a pending phone offer.")
                    return None


                appointment_id = f"A{len(self.appointments) + 1:03d}"

                appointment = Appointment(
                    appointment_id,
                    patient_id,
                    schedule.professional_id,
                    schedule.start_datetime,
                    schedule.end_datetime
                )

                schedule.booked = True
                schedule.pending_offer_id = None
                self.appointments.append(appointment)

                return appointment

        print("Appointment slot not found.")
        return None
    
    def cancel_appointment(self, appointment_id):
        for appointment in self.appointments:

            if (appointment.appointment_id == appointment_id
                    and appointment.status == "Booked"):

                for schedule in self.availability_service.schedules:

                    if (
                        schedule.professional_id == appointment.professional_id
                        and schedule.start_datetime == appointment.start_datetime
                        and schedule.end_datetime == appointment.end_datetime
                    ):
                        schedule.booked = False
                        appointment.status = "Cancelled"

                        return schedule

        return None



class WaitlistService:
    def __init__(self):
        self.entries = []
        self.next_id = 1
        self.offers = []
        self.next_offer_id = 1

    def add_to_waitlist(
        self, patient_id, preferred_professional_id=None,
        preferred_specialty=None,
        requested_start_date=None,
        requested_end_date=None,
        priority=1
    ):
        if priority < 1:
            raise ValueError("Priority must be at least 1.")

        entry_id = f"W{self.next_id:03d}"

        entry = WaitlistEntry(
            entry_id,
            patient_id,
            preferred_professional_id,
            preferred_specialty,
            requested_start_date,
            requested_end_date,
            priority
        )

        self.entries.append(entry)
        self.next_id += 1

        return entry

    
    def view_waitlist(self):
        active_entries = [
            entry for entry in self.entries
            if entry.status in ("Waiting", "Offered")
        ]

        return sorted(
            active_entries,
            key=lambda entry: entry.priority
        )


    def change_priority(self, entry_id, new_priority):
        if new_priority < 1:
            raise ValueError("Priority must be at least 1.")

        for entry in self.entries:
            if entry.entry_id == entry_id:
                entry.priority = new_priority
                return True

        return False
    
    def find_eligible_entries(self, schedule, doctor_specialty=None):
        eligible = []

        for entry in self.entries:

            if entry.status != "Waiting":
                continue

            # Check preferred doctor
            if (
                entry.preferred_professional_id is not None
                and entry.preferred_professional_id != schedule.professional_id
            ):
                continue

            # Check preferred specialty
            if entry.preferred_specialty is not None:
                if (
                    not doctor_specialty
                    or entry.preferred_specialty.strip().lower()
                    != doctor_specialty.strip().lower()
                ):
                    continue

            # Check requested start date
            if (
                entry.requested_start_date is not None
                and schedule.start_datetime.date()
                < entry.requested_start_date
            ):
                continue

            # Check requested end date
            if (
                entry.requested_end_date is not None
                and schedule.start_datetime.date()
                > entry.requested_end_date
            ):
                continue

            eligible.append(entry)

        return sorted(eligible, key=lambda entry: entry.priority)

    
    def create_phone_offer(
        self, entry_id, schedule, doctor_specialty=None
    ):
        # Check whether the appointment slot is available
        if not schedule.is_available():
            print("This appointment slot is not available.")
            return None

        # Prevent multiple pending offers for the same slot
        for existing_offer in self.offers:
            if (
                existing_offer.schedule_id == schedule.schedule_id
                and existing_offer.outcome == "Pending"
            ):
                print("This slot already has a pending offer.")
                return None

        # Find eligible waiting-list patients
        eligible_entries = self.find_eligible_entries(
            schedule, doctor_specialty
        )

        # Check whether the selected entry is eligible
        selected_entry = None

        for entry in eligible_entries:
            if entry.entry_id == entry_id:
                selected_entry = entry
                break

        if selected_entry is None:
            print("Waiting-list patient is not eligible.")
            return None

        # Create the appointment offer
        offer_id = f"O{self.next_offer_id:03d}"

        offer = AppointmentOffer(
            offer_id,
            entry_id,
            schedule.schedule_id,
            schedule.start_datetime
        )

        self.offers.append(offer)
        self.next_offer_id += 1

        schedule.pending_offer_id = offer.offer_id
        selected_entry.status = "Offered"

        return offer

    
    def record_offer_outcome(
        self, offer_id, accepted, appointment_service,
        remove_if_declined=False
    ):
        # Find the appointment offer
        offer = next(
            (o for o in self.offers if o.offer_id == offer_id),
            None
        )

        if offer is None or offer.outcome != "Pending":
            print("Pending appointment offer not found.")
            return None

        # Find the waiting-list patient
        entry = next(
            (e for e in self.entries if e.entry_id == offer.entry_id),
            None
        )

        if entry is None or entry.status != "Offered":
            print("Waiting-list entry not found or not offered.")
            return None

        if accepted:
            # Book the appointment for the patient
            appointment = appointment_service.book_appointment(
                entry.patient_id,
                offer.schedule_id,
                offer_id=offer.offer_id
            )

            if appointment is None:
                print("Could not accept the offer.")
                return None

            offer.accept()
            entry.status = "Booked"

            return appointment

        else:
            # Patient declines the appointment
            offer.decline()

            # Release the slot reserved by this pending offer.
            for schedule in appointment_service.availability_service.schedules:
                if (
                    schedule.schedule_id == offer.schedule_id
                    and schedule.pending_offer_id == offer.offer_id
                ):
                    schedule.pending_offer_id = None
                    break

            if remove_if_declined:
                entry.status = "Removed"
            else:
                entry.status = "Waiting"

            return offer




