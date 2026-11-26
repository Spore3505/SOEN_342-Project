import java.time.*;
import java.time.format.DateTimeFormatter;

class Models {
    static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
    static final DateTimeFormatter TM = DateTimeFormatter.ofPattern("HH:mm");

    static String id(String prefix, int n) {
        return String.format(java.util.Locale.ROOT, "%s%03d", prefix, n);
    }
}

class Patient {
    final String patientId;
    final String firstName;
    final String lastName;
    final String phoneNumber;
    final LocalDate dateOfBirth;

    Patient(String id, String first, String last, LocalDate dob, String phone) {
        patientId = id;
        firstName = first;
        lastName = last;
        dateOfBirth = dob;
        phoneNumber = phone;
    }

    public String toString() {
        return patientId + ": " + firstName + " " + lastName;
    }
}

class HealthcareProfessional {
    final String professionalId;
    final String name;
    final String professionalInformation;
    final String specialty;

    HealthcareProfessional(String id, String name, String info, String specialty) {
        professionalId = id;
        this.name = name;
        professionalInformation = info;
        this.specialty = specialty;
    }

    public String toString() {
        return professionalId + ": " + name + " (" + specialty + ")";
    }
}

class ProfessionalSchedule {
    final String scheduleId;
    final String professionalId;
    final LocalDateTime startDatetime;
    final LocalDateTime endDatetime;
    boolean booked;
    String pendingOfferId;

    ProfessionalSchedule(String id, String doctor, LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time.");
        }

        scheduleId = id;
        professionalId = doctor;
        startDatetime = start;
        endDatetime = end;
    }

    boolean isAvailable() {
        return !booked && pendingOfferId == null;
    }

    public String toString() {
        return scheduleId + ": " + professionalId + " | "
                + startDatetime.format(Models.DT) + " - "
                + endDatetime.format(Models.TM) + " | "
                + (isAvailable() ? "Available" : "Booked");
    }
}

class Appointment {
    final String appointmentId;
    final String patientId;
    final String professionalId;
    final LocalDateTime startDatetime;
    final LocalDateTime endDatetime;
    String status = "Booked";

    Appointment(String id, String patient, String doctor, LocalDateTime start, LocalDateTime end) {
        appointmentId = id;
        patientId = patient;
        professionalId = doctor;
        startDatetime = start;
        endDatetime = end;
    }

    public String toString() {
        return appointmentId + ": Patient " + patientId
                + " with Doctor " + professionalId + " | "
                + startDatetime.format(Models.DT) + " | " + status;
    }
}

class WaitlistEntry {
    final String entryId;
    final String patientId;
    final String preferredProfessionalId;
    final String preferredSpecialty;
    final LocalDate requestedStartDate;
    final LocalDate requestedEndDate;
    int priority;
    String status = "Waiting";

    WaitlistEntry(String id, String patient, String doctor, String specialty,
                  LocalDate start, LocalDate end, int priority) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid requested date range.");
        }

        entryId = id;
        patientId = patient;
        preferredProfessionalId = doctor;
        preferredSpecialty = specialty;
        requestedStartDate = start;
        requestedEndDate = end;
        this.priority = priority;
    }

    public String toString() {
        return entryId + ": Patient " + patientId
                + " | Doctor: " + (preferredProfessionalId == null ? "Any" : preferredProfessionalId)
                + " | Specialty: " + (preferredSpecialty == null ? "Any" : preferredSpecialty)
                + " | Priority: " + priority
                + " | Status: " + status;
    }
}

class AppointmentOffer {
    final String offerId;
    final String entryId;
    final String scheduleId;
    final LocalDateTime offeredDatetime;
    String outcome = "Pending";

    AppointmentOffer(String id, String entry, String schedule, LocalDateTime date) {
        offerId = id;
        entryId = entry;
        scheduleId = schedule;
        offeredDatetime = date;
    }

    void accept() {
        outcome = "Accepted";
    }

    void decline() {
        outcome = "Declined";
    }

    public String toString() {
        return offerId + ": Waiting List " + entryId
                + " | Slot: " + scheduleId
                + " | Date: " + offeredDatetime.format(Models.DT)
                + " | Outcome: " + outcome;
    }
}
