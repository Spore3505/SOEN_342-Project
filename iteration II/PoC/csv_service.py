
import csv
from datetime import datetime

from models import ProfessionalSchedule


class CSVService:
    def __init__(self, availability_service):
        self.availability_service = availability_service

    def import_availability(self, filename):
        count = 0

        with open(filename, "r", newline="", encoding="utf-8-sig") as file:
            reader = csv.DictReader(file)

            for row in reader:
                professional_id = row["professional_id"]

                start = datetime.strptime(
                    row["date"] + " " + row["start"],
                    "%Y-%m-%d %H:%M"
                )

                end = datetime.strptime(
                    row["date"] + " " + row["end"],
                    "%Y-%m-%d %H:%M"
                )

                if end <= start:
                    raise ValueError("End time must be after start time.")

                # Skip duplicate or overlapping slots
                overlap = any(
                    s.professional_id == professional_id
                    and start < s.end_datetime
                    and end > s.start_datetime
                    for s in self.availability_service.schedules
                )

                if overlap:
                    continue

                schedule_id = (
                    f"S{len(self.availability_service.schedules) + 1:03d}"
                )

                
                schedule = ProfessionalSchedule(
                    schedule_id,
                    professional_id,
                    start,
                    end
                )

                # Restore availability status from CSV
                status = (row.get("status") or "Available").strip().lower()

                if status not in ("available", "booked"):
                    raise ValueError(f"Invalid status: {status}")

                schedule.booked = (status == "booked")

                self.availability_service.add_schedule(schedule)
                count += 1


        return count
    
    def export_availability(self, filename):
        with open(filename, "w", newline="", encoding="utf-8") as file:
            writer = csv.writer(file)

            writer.writerow([
                "professional_id", "date", "start", "end", "status"
            ])

            for schedule in self.availability_service.schedules:
                writer.writerow([
                    schedule.professional_id,
                    schedule.start_datetime.strftime("%Y-%m-%d"),
                    schedule.start_datetime.strftime("%H:%M"),
                    schedule.end_datetime.strftime("%H:%M"),
                    "Booked" if schedule.booked else "Available"
                ])

        return len(self.availability_service.schedules)



