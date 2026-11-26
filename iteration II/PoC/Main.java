import java.time.*;
import java.time.format.*;
import java.util.*;
import java.io.*;

public class Main {
    static final Scanner input = new Scanner(System.in);

    static String ask(String prompt) {
        System.out.print(prompt);
        return input.nextLine();
    }

    static String trimmed(String prompt) {
        return ask(prompt).trim();
    }

    static String upper(String prompt) {
        return trimmed(prompt).toUpperCase(Locale.ROOT);
    }

    static String optional(String prompt) {
        String s = trimmed(prompt);
        return s.isEmpty() ? null : s;
    }

    static LocalDate optionalDate(String prompt) {
        String s = optional(prompt);
        return s == null ? null : LocalDate.parse(s);
    }

    static int integer(String prompt) {
        return Integer.parseInt(trimmed(prompt));
    }

    static LocalTime parseTime(String s) {
        return LocalTime.parse(s, DateTimeFormatter.ofPattern("H:m").withResolverStyle(ResolverStyle.STRICT));
    }

    static void show(Collection<?> items) {
        for (var item : items) {
            System.out.println(item);
        }
    }

    static boolean registered(PatientService p, String id) {
        if (p.findPatient(id) != null) {
            return true;
        }
        System.out.println("Patient is not registered.\nPlease register the patient using option 8.");
        return false;
    }

    static void manageWaitlist(WaitlistService w, AvailabilityService a, AppointmentService b, PatientService p) {
        while (true) {
            System.out.println(
                    "\n===== Patient Waiting List ====="
                            + "\n1. Add Patient to Waiting List"
                            + "\n2. View Waiting List"
                            + "\n3. Change Patient Priority"
                            + "\n4. Create Phone Offer"
                            + "\n5. Accept or Decline Phone Offer"
                            + "\n6. Return to Main Menu");
            String choice = ask("\nEnter your choice: ");
            try {
                switch (choice) {
                    case "1": {
                        String id = upper("Enter patient ID: ");
                        if (!registered(p, id)) {
                            break;
                        }
                        String doctor = optional("Preferred doctor ID (Enter for any): ");
                        String specialty = optional("Preferred specialty (Enter for any): ");
                        LocalDate start = optionalDate("Requested start date YYYY-MM-DD (Enter to skip): ");
                        LocalDate end = optionalDate("Requested end date YYYY-MM-DD (Enter to skip): ");
                        var e = w.addToWaitlist(id, doctor, specialty, start, end, integer("Enter priority (1 = highest): "));
                        System.out.println("\nPatient added successfully!\n" + e);
                        break;
                    }
                    case "2": {
                        var entries = w.viewWaitlist();
                        if (entries.isEmpty()) {
                            System.out.println("The waiting list is empty.");
                        } else {
                            System.out.println("\nCurrent Waiting List:");
                            show(entries);
                        }
                        break;
                    }
                    case "3": {
                        String id = trimmed("Enter waiting list entry ID: ");
                        boolean changed = w.changePriority(id, integer("Enter new priority: "));
                        System.out.println(changed ? "Priority updated successfully!" : "Waiting list entry not found.");
                        break;
                    }
                    case "4": {
                        var s = a.findSchedule(trimmed("Enter available schedule ID: "));
                        if (s == null || !s.isAvailable()) {
                            System.out.println("Appointment slot not found or unavailable.");
                            break;
                        }
                        String specialty = optional("Enter doctor's specialty (Enter if unknown): ");
                        var eligible = w.findEligibleEntries(s, specialty);
                        if (eligible.isEmpty()) {
                            System.out.println("No eligible waiting-list patients found.");
                            break;
                        }
                        System.out.println("\nEligible Patients (Priority Order):");
                        show(eligible);
                        var o = w.createPhoneOffer(trimmed("\nEnter waiting-list entry ID to offer: "), s, specialty);
                        if (o != null) {
                            System.out.println("\nPhone offer recorded successfully!\n" + o);
                        }
                        break;
                    }
                    case "5": {
                        var pending = new ArrayList<AppointmentOffer>();
                        for (var o : w.offers) {
                            if (o.outcome.equals("Pending")) {
                                pending.add(o);
                            }
                        }
                        if (pending.isEmpty()) {
                            System.out.println("No pending phone offers.");
                            break;
                        }
                        System.out.println("\nPending Phone Offers:");
                        show(pending);
                        String id = upper("\nEnter offer ID: ");
                        String response = trimmed("Did the patient accept or decline? (A/D): ").toLowerCase(Locale.ROOT);
                        if (!response.equals("a") && !response.equals("d")) {
                            System.out.println("Invalid response. Enter A or D.");
                            break;
                        }
                        boolean accepted = response.equals("a");
                        boolean remove = false;
                        if (!accepted) {
                            String r = trimmed("Remove patient from waiting list? (Y/N): ");
                            remove = r.equalsIgnoreCase("y") || r.equalsIgnoreCase("yes");
                        }
                        Object result = w.recordOfferOutcome(id, accepted, b, remove);
                        if (result != null) {
                            if (accepted) {
                                System.out.println("\nPhone offer accepted!\nAppointment booked successfully:\n" + result);
                            } else {
                                System.out.println("\nPhone offer declined!\n" + result + "\n" + (remove ? "Patient removed from waiting list." : "Patient remains on waiting list."));
                            }
                        }
                        break;
                    }
                    case "6":
                        System.out.println("Returning to Main Menu...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        var a = new AvailabilityService();
        var csv = new CSVService(a);
        var b = new AppointmentService(a);
        var w = new WaitlistService();
        var p = new PatientService();
        while (true) {
            System.out.println(
                    "\n===== Appointment Tracking System ====="
                            + "\n1. Book Appointment"
                            + "\n2. Search Availability"
                            + "\n3. Make Availability"
                            + "\n4. Import CSV"
                            + "\n5. Export CSV"
                            + "\n6. Manage Patient Waiting List"
                            + "\n7. Cancel Appointment"
                            + "\n8. Register Patient"
                            + "\n9. Exit");
            if (!input.hasNextLine()) {
                return;
            }
            String choice = ask("\nEnter your choice: ");
            try {
                switch (choice) {
                    case "1": {
                        String id = upper("Enter patient ID: ");
                        if (!registered(p, id)) {
                            break;
                        }
                        var appointment = b.bookAppointment(id, upper("Enter schedule ID: "));
                        if (appointment != null) {
                            System.out.println("\nBooking successful!\n" + appointment);
                        }
                        break;
                    }
                    case "2": {
                        var slots = a.searchAvailability(trimmed("Enter doctor ID: "));
                        if (slots.isEmpty()) {
                            System.out.println("No available appointments found.");
                        } else {
                            System.out.println("\nAvailable appointments:");
                            show(slots);
                        }
                        break;
                    }
                    case "3": {
                        String doctor = trimmed("Enter doctor ID: ");
                        LocalDate start = LocalDate.parse(ask("Enter start date (YYYY-MM-DD): "));
                        LocalDate end = LocalDate.parse(ask("Enter end date (YYYY-MM-DD): "));
                        System.out.println("\nSelect repetition type:\n1. Weekly\n2. Monthly");
                        String repetition = trimmed("Enter your choice: ");
                        var weekdays = new ArrayList<Integer>();
                        int day = 0;
                        if (repetition.equals("1")) {
                            System.out.println("\nDays: 0=Monday, 1=Tuesday, 2=Wednesday,\n3=Thursday, 4=Friday, 5=Saturday, 6=Sunday");
                            for (String token : ask("Enter weekdays (e.g. 0,2,4): ").split(",", -1)) {
                                int d = Integer.parseInt(token.trim());
                                if (d < 0 || d > 6) {
                                    throw new IllegalArgumentException("Weekdays must be between 0 and 6.");
                                }
                                weekdays.add(d);
                            }
                        } else if (repetition.equals("2")) {
                            day = integer("Enter day of month (1-31): ");
                        } else {
                            System.out.println("Invalid repetition type.");
                            break;
                        }
                        LocalTime from = parseTime(ask("Enter start time (HH:MM): "));
                        LocalTime to = parseTime(ask("Enter end time (HH:MM): "));
                        int minutes = integer("Enter slot duration in minutes: ");
                        var slots = repetition.equals("1")
                                ? a.makeRecurringAvailability(doctor, start, end, weekdays, from, to, minutes)
                                : a.makeMonthlyAvailability(doctor, start, end, day, from, to, minutes);
                        System.out.println("\nSuccessfully created " + slots.size() + " slots!");
                        show(slots);
                        break;
                    }
                    case "4": {
                        String filename = optional("Enter CSV filename (press Enter for availability.csv): ");
                        if (filename == null) {
                            filename = "availability.csv";
                        }
                        try {
                            System.out.println("Successfully imported " + csv.importAvailability(filename) + " slots.");
                        } catch (IOException | IllegalArgumentException | DateTimeException e) {
                            System.out.println("Import failed: " + e.getMessage());
                        }
                        break;
                    }
                    case "5": {
                        String filename = optional("Enter export filename (press Enter for exported_availability.csv): ");
                        if (filename == null) {
                            filename = "exported_availability.csv";
                        }
                        try {
                            System.out.println("Successfully exported " + csv.exportAvailability(filename) + " slots to " + filename + ".");
                        } catch (IOException e) {
                            System.out.println("Export failed: " + e.getMessage());
                        }
                        break;
                    }
                    case "6":
                        manageWaitlist(w, a, b, p);
                        break;
                    case "7": {
                        var s = b.cancelAppointment(trimmed("Enter appointment ID to cancel: "));
                        if (s == null) {
                            System.out.println("Booked appointment not found.");
                            break;
                        }
                        System.out.println("\nAppointment cancelled successfully!\nThe appointment slot is available again.");
                        var eligible = w.findEligibleEntries(s, optional("Enter doctor's specialty (Enter if unknown): "));
                        if (eligible.isEmpty()) {
                            System.out.println("\nNo eligible waiting-list patients found.");
                        } else {
                            System.out.println("\nEligible Waiting-List Patients:");
                            show(eligible);
                            System.out.println("\nAdministrative staff can contact these patients by phone in priority order.");
                        }
                        break;
                    }
                    case "8": {
                        System.out.println("\n===== Register New Patient =====");
                        String id = upper("Enter patient ID: ");
                        String first = trimmed("Enter first name: ");
                        String last = trimmed("Enter last name: ");
                        String dob = trimmed("Enter date of birth (YYYY-MM-DD): ");
                        String phone = trimmed("Enter phone number: ");
                        if (id.isEmpty() || first.isEmpty() || last.isEmpty() || dob.isEmpty() || phone.isEmpty()) {
                            System.out.println("All fields are required.");
                            break;
                        }
                        var patient = new Patient(id, first, last, LocalDate.parse(dob), phone);
                        if (p.registerPatient(patient)) {
                            System.out.println("\nPatient registered successfully!\n" + patient);
                        }
                        break;
                    }
                    case "9":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (IllegalArgumentException | DateTimeException e) {
                System.out.println("Invalid input: " + e.getMessage());
            }
        }
    }
}
