import java.time.*;
import java.util.*;

class PatientService {
    final Map<String, Patient> patients = new HashMap<>();

    boolean registerPatient(Patient p) {
        if (patients.containsKey(p.patientId)) {
            System.out.println("Patient ID already exists.");
            return false;
        }
        patients.put(p.patientId, p);
        return true;
    }

    Patient findPatient(String id) {
        return patients.get(id);
    }
}

class AvailabilityService {
    final List<ProfessionalSchedule> schedules = new ArrayList<>();

    void addSchedule(ProfessionalSchedule s) {
        schedules.add(s);
    }

    ProfessionalSchedule findSchedule(String id) {
        for (var s : schedules) {
            if (s.scheduleId.equals(id)) {
                return s;
            }
        }
        return null;
    }

    List<ProfessionalSchedule> searchAvailability(String doctor) {
        var result = new ArrayList<ProfessionalSchedule>();
        for (var s : schedules) {
            if (s.professionalId.equals(doctor) && s.isAvailable()) {
                result.add(s);
            }
        }
        return result;
    }

    boolean overlaps(String doctor, LocalDateTime start, LocalDateTime end) {
        for (var s : schedules) {
            if (s.professionalId.equals(doctor) && start.isBefore(s.endDatetime) && end.isAfter(s.startDatetime)) {
                return true;
            }
        }
        return false;
    }

    void validate(LocalDate start, LocalDate end, LocalTime from, LocalTime to, int minutes) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Invalid date range.");
        }
        if (!from.isBefore(to) || minutes <= 0) {
            throw new IllegalArgumentException("Invalid time range or slot duration.");
        }
    }

    void createDay(
            String doctor,
            LocalDate day,
            LocalTime from,
            LocalTime to,
            int minutes,
            List<ProfessionalSchedule> result
    ) {
        LocalDateTime start = day.atTime(from);
        LocalDateTime end = day.atTime(to);
        while (!start.plusMinutes(minutes).isAfter(end)) {
            var next = start.plusMinutes(minutes);
            if (!overlaps(doctor, start, next)) {
                var s = new ProfessionalSchedule(Models.id("S", schedules.size() + 1), doctor, start, next);
                addSchedule(s);
                result.add(s);
            }
            start = next;
        }
    }

    List<ProfessionalSchedule> makeRecurringAvailability(
            String doctor,
            LocalDate start,
            LocalDate end,
            List<Integer> weekdays,
            LocalTime from,
            LocalTime to,
            int minutes
    ) {
        validate(start, end, from, to, minutes);
        var result = new ArrayList<ProfessionalSchedule>();
        for (var day = start; !day.isAfter(end); day = day.plusDays(1)) {
            if (weekdays.contains(day.getDayOfWeek().getValue() - 1)) {
                createDay(doctor, day, from, to, minutes, result);
            }
        }
        return result;
    }

    List<ProfessionalSchedule> makeMonthlyAvailability(
            String doctor,
            LocalDate start,
            LocalDate end,
            int day,
            LocalTime from,
            LocalTime to,
            int minutes
    ) {
        validate(start, end, from, to, minutes);
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("Day of month must be between 1 and 31.");
        }
        var result = new ArrayList<ProfessionalSchedule>();
        for (var month = YearMonth.from(start); !month.isAfter(YearMonth.from(end)); month = month.plusMonths(1)) {
            if (day > month.lengthOfMonth()) {
                continue;
            }
            var date = month.atDay(day);
            if (!date.isBefore(start) && !date.isAfter(end)) {
                createDay(doctor, date, from, to, minutes, result);
            }
        }
        return result;
    }
}

class AppointmentService {
    final AvailabilityService availabilityService;
    final List<Appointment> appointments = new ArrayList<>();

    AppointmentService(AvailabilityService a) {
        availabilityService = a;
    }

    Appointment bookAppointment(String patient, String schedule) {
        return bookAppointment(patient, schedule, null);
    }

    Appointment bookAppointment(String patient, String schedule, String offer) {
        var s = availabilityService.findSchedule(schedule);
        if (s == null) {
            System.out.println("Appointment slot not found.");
            return null;
        }
        if (s.booked) {
            System.out.println("This appointment slot is already booked.");
            return null;
        }
        if (s.pendingOfferId != null && !s.pendingOfferId.equals(offer)) {
            System.out.println("This slot is reserved for a pending phone offer.");
            return null;
        }
        var a = new Appointment(Models.id("A", appointments.size() + 1), patient, s.professionalId, s.startDatetime, s.endDatetime);
        s.booked = true;
        s.pendingOfferId = null;
        appointments.add(a);
        return a;
    }

    ProfessionalSchedule cancelAppointment(String id) {
        for (var a : appointments) {
            if (a.appointmentId.equals(id) && a.status.equals("Booked")) {
                for (var s : availabilityService.schedules) {
                    if (s.professionalId.equals(a.professionalId) && s.startDatetime.equals(a.startDatetime) && s.endDatetime.equals(a.endDatetime)) {
                        s.booked = false;
                        a.status = "Cancelled";
                        return s;
                    }
                }
            }
        }
        return null;
    }
}

class WaitlistService {
    final List<WaitlistEntry> entries = new ArrayList<>();
    final List<AppointmentOffer> offers = new ArrayList<>();
    int nextId = 1;
    int nextOfferId = 1;

    WaitlistEntry addToWaitlist(
            String patient,
            String doctor,
            String specialty,
            LocalDate start,
            LocalDate end,
            int priority
    ) {
        checkPriority(priority);
        var e = new WaitlistEntry(Models.id("W", nextId), patient, doctor, specialty, start, end, priority);
        entries.add(e);
        nextId++;
        return e;
    }

    void checkPriority(int p) {
        if (p < 1) {
            throw new IllegalArgumentException("Priority must be at least 1.");
        }
    }

    List<WaitlistEntry> viewWaitlist() {
        var result = new ArrayList<WaitlistEntry>();
        for (var e : entries) {
            if (e.status.equals("Waiting") || e.status.equals("Offered")) {
                result.add(e);
            }
        }
        result.sort(Comparator.comparingInt(e -> e.priority));
        return result;
    }

    boolean changePriority(String id, int p) {
        checkPriority(p);
        for (var e : entries) {
            if (e.entryId.equals(id)) {
                e.priority = p;
                return true;
            }
        }
        return false;
    }

    List<WaitlistEntry> findEligibleEntries(ProfessionalSchedule s, String specialty) {
        var result = new ArrayList<WaitlistEntry>();
        for (var e : entries) {
            if (!e.status.equals("Waiting")) {
                continue;
            }
            if (e.preferredProfessionalId != null && !e.preferredProfessionalId.equals(s.professionalId)) {
                continue;
            }
            if (e.preferredSpecialty != null && (specialty == null || !e.preferredSpecialty.trim().equalsIgnoreCase(specialty.trim()))) {
                continue;
            }
            var day = s.startDatetime.toLocalDate();
            if (e.requestedStartDate != null && day.isBefore(e.requestedStartDate)) {
                continue;
            }
            if (e.requestedEndDate != null && day.isAfter(e.requestedEndDate)) {
                continue;
            }
            result.add(e);
        }
        result.sort(Comparator.comparingInt(e -> e.priority));
        return result;
    }

    AppointmentOffer createPhoneOffer(String id, ProfessionalSchedule s, String specialty) {
        if (!s.isAvailable()) {
            System.out.println("This appointment slot is not available.");
            return null;
        }
        for (var o : offers) {
            if (o.scheduleId.equals(s.scheduleId) && o.outcome.equals("Pending")) {
                System.out.println("This slot already has a pending offer.");
                return null;
            }
        }
        for (var e : findEligibleEntries(s, specialty)) {
            if (e.entryId.equals(id)) {
                var o = new AppointmentOffer(Models.id("O", nextOfferId++), id, s.scheduleId, s.startDatetime);
                offers.add(o);
                s.pendingOfferId = o.offerId;
                e.status = "Offered";
                return o;
            }
        }
        System.out.println("Waiting-list patient is not eligible.");
        return null;
    }

    Object recordOfferOutcome(String id, boolean accepted, AppointmentService service, boolean remove) {
        AppointmentOffer offer = null;
        for (var o : offers) {
            if (o.offerId.equals(id)) {
                offer = o;
                break;
            }
        }
        if (offer == null || !offer.outcome.equals("Pending")) {
            System.out.println("Pending appointment offer not found.");
            return null;
        }
        WaitlistEntry entry = null;
        for (var e : entries) {
            if (e.entryId.equals(offer.entryId)) {
                entry = e;
                break;
            }
        }
        if (entry == null || !entry.status.equals("Offered")) {
            System.out.println("Waiting-list entry not found or not offered.");
            return null;
        }
        if (accepted) {
            var a = service.bookAppointment(entry.patientId, offer.scheduleId, offer.offerId);
            if (a == null) {
                System.out.println("Could not accept the offer.");
                return null;
            }
            offer.accept();
            entry.status = "Booked";
            return a;
        }
        offer.decline();
        var s = service.availabilityService.findSchedule(offer.scheduleId);
        if (s != null && offer.offerId.equals(s.pendingOfferId)) {
            s.pendingOfferId = null;
        }
        entry.status = remove ? "Removed" : "Waiting";
        return offer;
    }
}
