package com.gstoupis.gps.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.gstoupis.gps.service.model.Prescription;
import com.gstoupis.gps.service.parser.HtmlParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionService {

  private Gmail gmail;
  private Calendar calendar;

  public PrescriptionService(Gmail gmail, Calendar calendar) {
    this.gmail = gmail;
    this.calendar = calendar;
  }

  public List<Prescription> getActivePrescriptions() throws IOException {

    //TODO: parameterize the below
    String user = "me";
    String query = "from:Hs-no.reply@e-prescription.gr AND subject:Έκδοση";
    Long maxResults = 100L;

    // Read messages
    ListMessagesResponse listMessagesResponse = gmail.users().messages().list(user).setQ(query).setMaxResults(maxResults).execute();
    List<Message> messages = listMessagesResponse.getMessages();
    System.out.println("Got " + messages.size() + " messages");

    HtmlParser htmlParser = new HtmlParser();
    List<Prescription> prescriptions = new ArrayList<>();
    for (Message message : messages) {
      Message detailedMessage = gmail.users().messages().get(user, message.getId()).execute();
      byte[] data = detailedMessage.getPayload().getBody().decodeData();
      String dataString = new String(data, StandardCharsets.UTF_8);
      Prescription prescription = htmlParser.parsePrescriptionEmailHtml(dataString);

      //Non-expired prescriptions only
      //TODO: Add check for successful prescription claim - do not include already claimed prescriptions
      if (prescription.isValid() && prescription.isActive() && prescription.isUnclaimed()) {
        prescriptions.add(prescription);
      }
    }

    return prescriptions;
  }

  public List<Event> createEventsForPrescriptions(List<Prescription> prescriptions) {

    List<Event> events = new ArrayList<>();

    prescriptions.forEach(prescription -> events.add(this.translatePrescriptionToEvent(prescription)));

    return events;
  }

  private Event translatePrescriptionToEvent(Prescription prescription) {
    Event event = new Event();

    event.setSummary("Prescription: " + prescription.id());

    EventDateTime start = new EventDateTime();
    start.setDate(new DateTime(prescription.from().format(DateTimeFormatter.ISO_DATE)));
    event.setStart(start);

    EventDateTime end = new EventDateTime();
    end.setDate(new DateTime(prescription.to().format(DateTimeFormatter.ISO_DATE)));
    event.setEnd(end);

    return event;
  }

}
