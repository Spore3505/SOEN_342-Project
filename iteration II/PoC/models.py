from datetime import datetime, date

class Patient:
    def __init__(self, patient_id, first_name, last_name,
                 date_of_birth, phone_number):
        self.patient_id = patient_id
        self.first_name = first_name
        self.last_name = last_name
        self.date_of_birth = date_of_birth
        self.phone_number = phone_number

    def __str__(self):
        return f"{self.patient_id}: {self.first_name} {self.last_name}"


class HealthcareProfessional:
    def __init__(self, professional_id, name,
                 professional_information, specialty):
        self.professional_id = professional_id
        self.name = name
        self.professional_information = professional_information
        self.specialty = specialty

    def __str__(self):
        return f"{self.professional_id}: {self.name} ({self.specialty})"


class ProfessionalSchedule:
    def __init__(self, schedule_id, professional_id,
                 start_datetime, end_datetime):
        if end_datetime <= start_datetime:
            raise ValueError("End time must be after start time.")

        self.schedule_id = schedule_id
        self.professional_id = professional_id
        self.start_datetime = start_datetime
        self.end_datetime = end_datetime
        self.booked = False
        self.pending_offer_id = None

    def is_available(self):
       return not self.booked and self.pending_offer_id is None

    def __str__(self):
        status = "Available" if self.is_available() else "Booked"

        return (
            f"{self.schedule_id}: {self.professional_id} | "
            f"{self.start_datetime:%Y-%m-%d %H:%M} - "
            f"{self.end_datetime:%H:%M} | {status}"
        )


class Appointment:
    def __init__(self, appointment_id, patient_id, professional_id,
                 start_datetime, end_datetime):
        self.appointment_id = appointment_id
        self.patient_id = patient_id
        self.professional_id = professional_id
        self.start_datetime = start_datetime
        self.end_datetime = end_datetime
        self.status = "Booked"

    def __str__(self):
        return (
            f"{self.appointment_id}: Patient {self.patient_id} "
            f"with Doctor {self.professional_id} | "
            f"{self.start_datetime:%Y-%m-%d %H:%M} | "
            f"{self.status}"
        )


class WaitlistEntry:
    def __init__(self, entry_id, patient_id,
                 preferred_professional_id=None,
                 preferred_specialty=None,
                 requested_start_date=None,
                 requested_end_date=None,
                 priority=1):

        if (requested_start_date is not None
                and requested_end_date is not None
                and requested_start_date > requested_end_date):
            raise ValueError("Invalid requested date range.")

        self.entry_id = entry_id
        self.patient_id = patient_id
        self.preferred_professional_id = preferred_professional_id
        self.preferred_specialty = preferred_specialty
        self.requested_start_date = requested_start_date
        self.requested_end_date = requested_end_date
        self.priority = priority
        self.status = "Waiting"

    def __str__(self):
        return (
            f"{self.entry_id}: Patient {self.patient_id} | "
            f"Doctor: {self.preferred_professional_id or 'Any'} | "
            f"Specialty: {self.preferred_specialty or 'Any'} | "
            f"Priority: {self.priority} | "
            f"Status: {self.status}"
        )


class AppointmentOffer:
    def __init__(self, offer_id, entry_id, schedule_id, offered_datetime):
        self.offer_id = offer_id
        self.entry_id = entry_id
        self.schedule_id = schedule_id
        self.offered_datetime = offered_datetime
        self.outcome = "Pending"

    def accept(self):
        self.outcome = "Accepted"

    def decline(self):
        self.outcome = "Declined"

    def __str__(self):
        return (
            f"{self.offer_id}: Waiting List {self.entry_id} | "
            f"Slot: {self.schedule_id} | "
            f"Date: {self.offered_datetime:%Y-%m-%d %H:%M} | "
            f"Outcome: {self.outcome}"
        )

