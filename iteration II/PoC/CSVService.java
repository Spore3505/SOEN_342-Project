import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;

class CSVService {

    final AvailabilityService availabilityService;

    CSVService(AvailabilityService a) {
        availabilityService = a;
    }

    // Parse CSV records, including quoted fields, escaped quotes and line breaks.
    static List<List<String>> parse(String text) {
        var rows = new ArrayList<List<String>>();
        var row = new ArrayList<String>();
        var field = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (quoted) {
                if (c == '"') {
                    if (i + 1 < text.length() && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        quoted = false;
                    }
                } else {
                    field.append(c);
                }
            } else if (c == '"' && field.length() == 0) {
                quoted = true;
            } else if (c == ',') {
                row.add(field.toString());
                field.setLength(0);
            } else if (c == '\n' || c == '\r') {
                if (c == '\r' && i + 1 < text.length() && text.charAt(i + 1) == '\n') {
                    i++;
                }

                if (!row.isEmpty() || field.length() > 0) {
                    row.add(field.toString());
                    rows.add(row);
                }

                row = new ArrayList<>();
                field.setLength(0);
            } else {
                field.append(c);
            }
        }

        if (quoted) {
            throw new IllegalArgumentException("Unclosed CSV quote.");
        }

        if (!row.isEmpty() || field.length() > 0) {
            row.add(field.toString());
            rows.add(row);
        }

        return rows;
    }

    static String get(List<String> row, List<String> header, String name) {
        int i = header.indexOf(name);

        if (i < 0 || i >= row.size()) {
            throw new IllegalArgumentException("Missing CSV field: " + name);
        }

        return row.get(i);
    }

    int importAvailability(String filename) throws IOException {
        String text = Files.readString(Path.of(filename), StandardCharsets.UTF_8);

        if (text.startsWith("\uFEFF")) {
            text = text.substring(1);
        }

        var rows = parse(text);

        if (rows.isEmpty()) {
            return 0;
        }

        var header = rows.get(0);
        int count = 0;

        for (int i = 1; i < rows.size(); i++) {
            var row = rows.get(i);
            String doctor = get(row, header, "professional_id");
            var date = LocalDate.parse(get(row, header, "date"));
            var start = date.atTime(Main.parseTime(get(row, header, "start")));
            var end = date.atTime(Main.parseTime(get(row, header, "end")));

            if (!end.isAfter(start)) {
                throw new IllegalArgumentException("End time must be after start time.");
            }

            if (availabilityService.overlaps(doctor, start, end)) {
                continue;
            }

            int si = header.indexOf("status");
            String status = si < 0 || si >= row.size() || row.get(si).isEmpty()
                    ? "available"
                    : row.get(si).trim().toLowerCase(Locale.ROOT);

            if (!status.equals("available") && !status.equals("booked")) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }

            var s = new ProfessionalSchedule(
                    Models.id("S", availabilityService.schedules.size() + 1),
                    doctor,
                    start,
                    end
            );

            s.booked = status.equals("booked");
            availabilityService.addSchedule(s);
            count++;
        }

        return count;
    }

    static String escape(String s) {
        return s.contains(",") || s.contains("\"") || s.contains("\r") || s.contains("\n")
                ? "\"" + s.replace("\"", "\"\"") + "\""
                : s;
    }

    int exportAvailability(String filename) throws IOException {
        try (var out = Files.newBufferedWriter(Path.of(filename), StandardCharsets.UTF_8)) {
            out.write("professional_id,date,start,end,status\r\n");

            for (var s : availabilityService.schedules) {
                out.write(String.join(",",
                        escape(s.professionalId),
                        s.startDatetime.toLocalDate().toString(),
                        s.startDatetime.format(Models.TM),
                        s.endDatetime.format(Models.TM),
                        s.booked ? "Booked" : "Available"
                ));

                out.write("\r\n");
            }
        }

        return availabilityService.schedules.size();
    }
}
