package com.gstoupis.gps;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.gmail.Gmail;
import com.gstoupis.gps.google.GoogleServiceType;
import com.gstoupis.gps.google.GoogleServiceFactory;
import com.gstoupis.gps.service.PrescriptionService;
import com.gstoupis.gps.service.model.Prescription;
import java.util.List;

/**
 * TODO:
 * 1. Add prescription.isClaimed() check
 * 2. Add logback
 * 3. Add unit tests
 * 4. Decide where this will run & how frequently
 */
public class GmailScanner {

  public static void main(String... args) throws Exception {

    Gmail gmail = (Gmail) GoogleServiceFactory.getService(GoogleServiceType.GMAIL);
    Calendar calendar = (Calendar) GoogleServiceFactory.getService(GoogleServiceType.CALENDAR);

    PrescriptionService prescriptionService = new PrescriptionService(gmail, calendar);
    List<Prescription> prescriptions = prescriptionService.getActivePrescriptions();
    System.out.println("Active prescriptions: " + prescriptions);

    List<Event> events = prescriptionService.createEventsForPrescriptions(prescriptions);
    for (Event event : events) {
      System.out.println("Creating event: " + event.getSummary() + "(" + event.getStart().getDate() + ")");
//      calendar.events().insert("primary", event).execute();
    }
  }
}