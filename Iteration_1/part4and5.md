4.  Three fully-dressed scenarios for three critical use-cases, with at least one success and one failure case for each

Use Case 1 (Success):

Primary actor:

Clinic Staff
Stakeholders and Interest:

Patient: 

Wants minimal wait time, wants a secure system to protect confidential info 

Clinic Staff: 

Wants fast patient lookup, fast checks for doctor availability, schedule constraints 

And communication with receiving referrals between different clinics

Clinic Doctors:  

Accurate non-complicated software to input days off and prevent bookings on those days. 

Quick referral sending between clinics.

Preconditions:

-   Clinic Staff or Clinic Doctors: are securely logged in with editing access to patient information or scheduling times.

-   Clinic Doctors: want their schedules to be easily added to the system already.

Success Guarantee (Postconditions):

-   An appointment is created and confirmed with a date, time, doctor and room.

-   The patient is texted a link to the confirmed appointment with all the info clearly displayed.

-   The updated system schedule now blocks off the confirmed time for the duration of the booked appointment.

-   If referred the referral is connected to the booked appointment.
Main Success Scenario: 

1.  Patient requests an appointment at the clinic

2.  Clinic staff search for patient using the search criteria SearchPatient(SearchCriteria)

3.  The system returns a matching patient.

4.  Clinic staff ask when the patient is available for an appointment. (checkAppointmentAvailability(patientID, professionalID, locationID, serviceID, requestedDateTime))

5.  Booking an appointment checks that the doctor is available for the appointment and if there is need for a referral that it exists.

6.  System returns availability is confirmed and referral requirements are completed. (availabilityConfirmed, referralRequirementsSatisfied)

7.  Clinic Staff books the appointment with the details. (bookAppointment(patientID, professionalID, locationID, serviceID, requestedDateTime))

8.  The system records the appointment and returns the confirmation. (appointmentID, bookingConfirmation) it sets the status to "Confirmed", books the slot in the calendar and if needed it connects the referral.

Extensions

-   3a. Patient is not found

-   System returns there is no patient related to the search 

-   Staff can then register a new patient

-   3b. Multiple Patients

-   Staff clicks correct patient from found list

-   5a. Requests a doctor that does not work at the clinic

-   System shows an error for the booking schedule 

-   Staff then sees the location of the requested doctor and informs the patient of the new location 

-   5b. Referral missing or invalid (Failure case 3)

-   5c. Schedule conflict or unavailable doctor

Special Requirements:

-   Access: administrative staff only see registration and appointment data, no patient diagnosis information.

-   Response time: under 5 seconds per operation.\
Technology and Data Variations list:

-   searchCriteria may be any:

-   patient ID;

-   insurance number (RAMQ or private);

-   date of birth;

-   phone number;

-   First or last name

-   Patients can be covered by RAMQ or private insurance or can have no insurance. All patient booking processes are the same regardless of insurance status only invoicing is modified. 

-   Referrals may be done internally or externally.  

Open Issues:

-   Unsure how far away appointments can be booked.

-   For cancellations, must it be at least 24 hrs in advance?

Use Case 1 (Failure):

Primary actor:
Clinic Staff

Stakeholders and Interest:

-   Patient: Wants minimal wait time and wants a secure system to protect confidential info.

-   Clinic Staff: Wants fast patient lookup, fast checks for doctor availability, schedule constraints and communication with receiving referrals between different clinics.

-   Clinic Doctors: Accurate non-complicated software to input days off and prevent bookings on those days. Quick referral sending between clinics.

Preconditions:

-   Clinic Staff or Clinic Doctors: are securely logged in with editing access to patient information or scheduling times.

-   Clinic Doctors: want their schedules to be easily added to the system already.

Failure Guarantee (Postconditions):

-   No appointment is created.

-   The system schedule is not changed and the requested time is not blocked off.

-   No referral is connected to an appointment.

-   The rejected booking attempt is logged.

Main Scenario:

1.  The patient requests an appointment at the clinic.

2.  Clinic staff search for patients using the search criteria SearchPatient(SearchCriteria).

3.  The system returns a matching patient.

4.  Clinic staff ask when the patient is available for an appointment. (checkAppointmentAvailability(patientID, professionalID, locationID, serviceID, requestedDateTime))

5.  The system checks if the doctor is available for the appointment and finds the doctor is not available at the requested time.

6.  System returns the doctor is unavailable. (professionalUnavailable)

7.  Clinic Staff still tries to book the appointment with the details. (bookAppointment(patientID, professionalID, locationID, serviceID, requestedDateTime))

8.  The system rejects the booking and returns the reason. (bookingRejected, professionalUnavailable) No appointment is created.

Extensions:

-   3a. Patient is not found

-   System returns there is no patient related to the search.

-   Staff can then register a new patient.

-   3b. Multiple Patients

-   Staff clicks correct patient from found list.

-   5a. Requests a doctor that does not work at the clinic

-   System shows an error for the booking schedule.

-   Staff then sees the location of the requested doctor and informs the patient of the new location.

Special Requirements:

-   Access: administrative staff only see registration and appointment data, no patient diagnosis information.

-   Response time: under 5 seconds per operation.

Technology and Data Variations list:

-   searchCriteria may be any:

-   patient ID;

-   insurance number (RAMQ or private);

-   date of birth;

-   phone number;

-   First or last name

-   Patients can be covered by RAMQ or private insurance or can have no insurance. All patient booking processes are the same regardless of insurance status only invoicing is modified.

-   Referrals may be done internally or externally.

Open Issues:

-   Unsure how far away appointments can be booked.

-   For cancellations, must it be at least 24 hrs in advance?

Use Case 2 (Success):

Primary actor:
Healthcare Professional

Stakeholders and Interest:

-   Patient: Wants a quick consultation with minimal wait time. Wants their clinical information recorded accurately and kept confidential.

-   Healthcare Professional: Wants quick access to the appointment and patient info. Wants simple, fast recording of observations, diagnoses, treatment recommendations and follow-ups.

-   Clinic Staff: Wants to know when a consultation has started and finished, without seeing clinical information.

-   Clinic: Wants complete, accurate clinical records and a log of who accessed or recorded patient information.

Preconditions:

-   Healthcare Professional: is securely logged in with access to clinical information.

-   The appointment is confirmed and assigned to the Healthcare Professional.

-   Clinic Staff has confirmed the patient's arrival.

Success Guarantee (Postconditions):

-   A consultation is created and linked to the appointment and the patient.

-   The clinical information (observations, diagnoses, treatment recommendations, follow-up requirements) is saved in the patient's clinical record.

-   The appointment status is updated to "Completed".

-   The access to and recording of clinical information is logged.

Main Success Scenario:

1.  Healthcare Professional opens the patient's appointment. (getAppointmentDetails(appointmentID))

2.  The system returns the appointment details and patient information. (appointmentDetails, patientInformation)

3.  Healthcare Professional starts the consultation. (startConsultation(appointmentID))

4.  The system creates the consultation and returns its ID. (consultationID)

5.  Healthcare Professional conducts the consultation with the patient.

6.  Healthcare Professional records the clinical information. (recordClinicalInformation(consultationID, observations, diagnoses, treatmentRecommendations, followUpRequirements))

7.  The system saves the clinical record, links it to the consultation and patient, sets the appointment to "Completed", and logs the activity. (clinicalRecordSaved)

Extensions:

-   1a. Appointment not found

-   The system returns no appointment with that ID or time.

-   Healthcare Professional re-enters the appointment ID or closes the view.

-   1b. Patient arrival not confirmed

-   The system returns that the patient has not arrived.

-   The Healthcare Professional asks Clinic Staff to confirm the arrival.

-   3a. Consultation already started for this appointment

-   The system returns the existing consultationID.

-   Healthcare Professional continues at step 5.

-   5a. Prescription needed

-   Healthcare Professional issues a prescription, then resumes at step 6.

-   5b. Follow-up appointment needed

-   Healthcare Professional records it in followUpRequirements. The follow-up is booked following the procedure from use Case 1.

-   6a. Clinical information missing

-   The system shows an error listing the missing fields.

-   Healthcare Professional fills them in and resumes at step 6.

Special Requirements:

-   Access: only authorized Healthcare Professionals can view and record clinical information. Clinic Staff see no patient diagnosis information.

-   Response time: under 5 seconds per operation.

-   Every view and save of clinical information is logged.

Technology and Data Variations list:

-   Clinical information include:

-   observations

-   diagnoses

-   treatment recommendations

-   follow-up requirements

-   Electronic prescriptions to pharmacies and 

Open Issues:

-   Can a clinical record be edited after it is saved, and by whom?

-   Which informational fields are required versus optional?

-   What happens to the appointment if the patient never arrives (no-show)?

Use Case 2 (Failure):

Primary actor:
Administrative Staff

Stakeholders and Interest:

-   Patient: Wants a quick consultation with minimal wait time. Wants their clinical information recorded accurately and kept confidential.

-   Healthcare Professional: Wants quick access to the appointment and patient info. Wants simple, fast recording of observations, diagnoses, treatment recommendations and follow-ups.

-   Clinic Staff: Wants to know when a consultation has started and finished, without seeing clinical information.

-   Clinic: Wants complete, accurate clinical records and a log of who accessed or recorded patient information.

Preconditions:

-   Administrative Staff: is securely logged in without access to clinical information.

-   The appointment is confirmed and assigned to the Healthcare Professional.

-   Clinic Staff has confirmed the patient's arrival.

Failure Guarantee (Postconditions):

-   No clinical record is created or modified.

-   The appointment status is not changed.

-   The denied attempt to record clinical information is logged.

Main Scenario:

1.  Administrative Staff opens the patient's appointment. (getAppointmentDetails(appointmentID))

2.  The system returns the appointment details only, with no clinical information. (appointmentDetails)

3.  Administrative Staff tries to record clinical information. (recordClinicalInformation(consultationID, observations, diagnoses, treatmentRecommendations, followUpRequirements))

4.  The system checks permissions, denies access, and logs the attempt. (accessDenied, insufficientPermissions) No clinical record is created or modified.

Extensions:

-   1a. Appointment not found

-   The system returns no appointment with that ID or time.

-   Administrative Staff re-enters the appointment ID or closes the view.

Special Requirements:

-   Access: only authorized Healthcare Professionals can view and record clinical information. Clinic Staff see no patient diagnosis information.

-   Response time: under 5 seconds per operation.

-   Every view and save of clinical information is logged.

Technology and Data Variations list:

-   Clinical information include:

-   observations

-   diagnoses

-   treatment recommendations

-   follow-up requirements

-   Electronic prescriptions to pharmacies 

Open Issues:

-   Who has the access to edit a clinical record after it is saved?

-   Which informational fields are required versus optional?

-   What happens to the appointment if the patient never arrives?

Use Case 3 (Success):

Primary actor:\
Healthcare Professional

Stakeholders and Interest:

-   Patient: Wants to be referred quickly to the right specialist. Wants their clinical reason kept confidential.

-   Healthcare Professional: Wants fast patient lookup and quick referral sending.

-   Receiving Specialist: Wants clear referrals with the clinical reason and priority.

-   Clinic Staff: Wants valid referrals in the system so appointments that require one can be booked.

-   Clinic: Wants accurate referral records and a log of who issued them.

Preconditions:

-   Healthcare Professional: is securely logged in with access to clinical information.

-   The patient is registered in the system.

Success Guarantee (Postconditions):

-   A referral is created and linked to the patient and the referring professional.

-   The requested specialty and/or professional, clinical reason, and priority are recorded.

-   The referral status is set to "Pending" (not yet linked to an appointment).

-   The referral is logged.

Main Success Scenario:

1.  Healthcare Professional searches for the patient. (searchPatient(searchCriteria))

2.  The system returns the matching patient information. (patientInformation)

3.  Healthcare Professional issues the referral. (issueReferral(patientID, referringProfessionalID, requestedSpecialtyID, requestedProfessionalID, clinicalReason, priority))

4.  The system creates the referral, sets its status, logs it, and returns the confirmation. (referralID, referralStatus, referralConfirmation)

Extensions:

-   2a. Patient is not found

-   The system returns no patient related to the search.

-   Clinic Staff registers the patient, then Healthcare Professional resumes at step 1.

-   2b. Multiple Patients

-   Healthcare Professional selects the correct patient from the list.

-   3a. No specific professional requested

-   Healthcare Professional leaves requestedProfessionalID empty and only the specialty is recorded.

-   3b. Required referral information missing

-   The system shows an error listing the missing fields.

-   Healthcare Professional fills them in and resumes at step 3.

Special Requirements:

-   Access: only authorized Healthcare Professionals can issue referrals.

-   Response time: under 5 seconds per operation.

-   Every referral issued is logged.

Technology and Data Variations list:

-   searchCriteria may be any:

-   patient ID

-   insurance number (RAMQ or private)

-   date of birth

-   phone number

-   First or last name

-   Referring professionals may be internal or external to the clinic.

-   Priority levels.

Open Issues:

-   How long is a referral valid?

-   How many appointments can one referral be used for?

-   How are external professionals' referrals entered into the system?

Use Case 3 (Failure):

Primary actor:
Healthcare Professional

Stakeholders and Interest:

-   Patient: Wants to be referred quickly to the right specialist. Wants their clinical reason kept confidential.

-   Healthcare Professional: Wants fast patient lookup and quick referral sending.

-   Receiving Specialist: Wants clear referrals with the reason and priority.

-   Clinic Staff: Wants valid referrals in the system so appointments that require one can be booked.

-   Clinic: Wants accurate referral records and a log of who issued them.

Preconditions:

-   Healthcare Professional: is securely logged in with access to clinical information.

-   The patient is registered in the system.

Failure Guarantee (Postconditions):

-   No referral is created.

-   Nothing is linked to the patient or the referring professional.

-   The rejected referral attempt is logged.

Main Scenario:

1.  Healthcare Professional searches for the patient. (searchPatient(searchCriteria))

2.  The system returns the matching patient information. (patientInformation)

3.  Healthcare Professional issues the referral without a requested specialty or a requested professional. (issueReferral(patientID, referringProfessionalID, requestedSpecialtyID, requestedProfessionalID, clinicalReason, priority))

4.  The system rejects the referral, logs the attempt, and returns the reason. (referralRejected, missingRequiredInformation) No referral is created.

Extensions:

-   2a. Patient is not found

-   The system returns no patient related to the search.

-   Clinic Staff registers the patient, then Healthcare Professional resumes at step 1.

-   2b. Multiple Patients

-   Healthcare Professional selects the correct patient from the list.

-   4a. Healthcare Professional corrects the referral

-   Healthcare Professional requests the patient to get a referral or for the healthcare specialist to resubmit the referral.

-   The referral continues as Use Case 3 (Success).

Special Requirements:

-   Access: only authorized Healthcare Professionals can issue referrals.

-   Response time: under 5 seconds per operation.

-   Every referral issued is logged.

Technology and Data Variations list:

-   searchCriteria may be any:

-   patient ID

-   insurance number (RAMQ or private)

-   date of birth

-   phone number

-   First or last name

-   Referring professionals may be internal or external to the clinic.

-   Priority levels.

Open Issues:

-   How long is a referral valid?

-   How many appointments can one referral be used for?

-   How are external professionals' referrals entered into the system?

5\. System operations and operation contracts

System Operations

UC1: Book Appointment

-   searchPatient(searchCriteria)

-   checkAppointmentAvailability(patientID, professionalID, locationID, serviceID, requestedDateTime)

-   bookAppointment(patientID, professionalID, locationID, serviceID, requestedDateTime)

UC2: Conduct Consultation

-   getAppointmentDetails(appointmentID)

-   startConsultation(appointmentID)

-   recordClinicalInformation(consultationID, observations, diagnoses, treatmentRecommendations, followUpRequirements)

UC3: Issue Referral

-   searchPatient(searchCriteria)

-   issueReferral(patientID, referringProfessionalID, requestedSpecialtyID, requestedProfessionalID, clinicalReason, priority)


System Interface:

System
----------------------------------------------------------------------------------------------
-   searchPatient(searchCriteria)

-   checkAppointmentAvailability(patientID, professionalID, locationID, serviceID, requestedDateTime)      
         
-   bookAppointment(patientID, professionalID, locationID, serviceID, requestedDateTime)      

-   getAppointmentDetails(appointmentID) 

-   startConsultation(appointmentID)  

-   recordClinicalInformation(consultationID, observations, diagnoses, treatmentRecommendations, followUpRequirements)

-   issueReferral(patientID, referringProfessionalID, requestedSpecialtyID, requestedProfessionalID, clinicalReason, priority) 


Operation Contracts:

Contract CO1: searchPatient

-   Operation: 

-   searchPatient(searchCriteria: SearchCriteria)

-   Cross References: 

-   Use Cases: UC1 Book Appointment, UC3 Issue Referral

-   Preconditions: 

-   The user is logged in.

-   Postconditions: 

-   None.

Contract CO2: checkAppointmentAvailability

-   Operation: 

-   checkAppointmentAvailability(patientID: PatientID, professionalID: ProfessionalID, locationID: LocationID, serviceID: ServiceID, requestedDateTime: DateTime)

-   Cross References: 

-   Use Case: UC1 Book Appointment

-   Preconditions:

-   Patient exists.

-   HealthcareProfessional exists.

-   ClinicLocation exists.

-   Service exists.

-   Postconditions: None (query operation).

Contract CO3: bookAppointment

-   Operation: 

-   bookAppointment(patientID: PatientID, professionalID: ProfessionalID, locationID: LocationID, serviceID: ServiceID, requestedDateTime: DateTime)

-   Cross References: 

-   Use Case: UC1 Book Appointment

-   Preconditions:

-   Patient exists.

-   HealthcareProfessional exists and practises at the ClinicLocation.

-   HealthcareProfessional is available at requestedDateTime.

-   A valid Referral exists if the Service requires one.

-   Postconditions:

-   An Appointment instance (apt) was created. (instance creation)

-   apt.dateTime was set to requestedDateTime. (attribute modification)

-   apt.status was set to "Confirmed". (attribute modification)

-   apt was associated with the Patient, based on patientID match. (association formed)

-   apt was associated with the HealthcareProfessional, based on professionalID match. (association formed)

-   apt was associated with the ClinicLocation, based on locationID match. (association formed)

-   apt was associated with the Service, based on serviceID match. (association formed)

-   apt was associated with the Referral, if the Service requires one. (association formed)

-   The time slot in the HealthcareProfessional's Schedule was set to booked. (attribute modification)

Contract CO4: getAppointmentDetails

-   Operation: 

-   getAppointmentDetails(appointmentID: AppointmentID)

-   Cross References: 

-   Use Case: UC2 Conduct Consultation

-   Preconditions: 

-   Appointment exists.

-   Postconditions: 

-   None (query operation).

Contract CO5: startConsultation

-   Operation: 

-   startConsultation(appointmentID: AppointmentID)

-   Cross References: 

-   Use Case: UC2 Conduct Consultation

-   Preconditions:

-   Appointment exists with status "Confirmed".

-   Appointment is assigned to the HealthcareProfessional.

-   Patient arrival has been confirmed.

-   Postconditions:

-   A Consultation instance consult was created. (instance creation)

-   consult.startTime was set to the current date and time. (attribute modification)

-   consult was associated with the Appointment, based on appointmentID match. (association formed)

-   consult was associated with the HealthcareProfessional. (association formed)

-   Appointment.status was set to "In Progress". (attribute modification)

Contract CO6: recordClinicalInformation

-   Operation: 

-   recordClinicalInformation(consultationID: ConsultationID, observations: String, diagnoses: String, treatmentRecommendations: String, followUpRequirements: String)

-   Cross References: 

-   Use Case: UC2 Conduct Consultation

-   Preconditions:

-   Consultation exists.

-   The user is an authorized HealthcareProfessional.

-   Postconditions:

-   A ClinicalRecord instance cr was created. (instance creation)

-   cr.observations was set to observations. (attribute modification)

-   cr.diagnoses was set to diagnoses. (attribute modification)

-   cr.treatmentRecommendations was set to treatmentRecommendations. (attribute modification)

-   cr.followUpRequirements was set to followUpRequirements. (attribute modification)

-   cr was associated with the Consultation, based on consultationID match. (association formed)

-   cr was associated with the Patient. (association formed)

-   Appointment.status was set to "Completed". (attribute modification)

Contract CO7: issueReferral

-   Operation: issueReferral(patientID: PatientID, referringProfessionalID: ProfessionalID, requestedSpecialtyID: SpecialtyID, requestedProfessionalID: ProfessionalID, clinicalReason: String, priority: Priority)

-   Cross References: Use Case: UC3 Issue Referral

-   Preconditions:

-   Patient exists.

-   Referring HealthcareProfessional exists.

-   A requested Specialty or requested HealthcareProfessional is specified.

-   Postconditions:

-   A instance referral was created. (instance creation)

-   referral.clinicalReason was set to clinicalReason. (attribute modification)

-   referral.priority was set to priority. (attribute modification)

-   referral.status was set to "Pending". (attribute modification)

-   referral was associated with the Patient, based on patientID match. (association formed)

-   referral was associated with the referring HealthcareProfessional, based on referringProfessionalID match. (association formed)

-   referral was associated with the requested Specialty, based on requestedSpecialtyID match, if specified. (association formed)

-   referral was associated with the requested HealthcareProfessional, based on requestedProfessionalID match, if specified. (association formed)

*Note (wrote parts 4 and 5 in docs then had to use a markdown converter because format could not be copied to the .md file)