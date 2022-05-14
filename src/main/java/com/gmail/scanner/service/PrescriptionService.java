package com.gmail.scanner.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.gmail.scanner.google.GoogleServiceFactory;
import com.gmail.scanner.google.GoogleServiceType;
import com.gmail.scanner.service.model.Prescription;
import com.gmail.scanner.service.parser.HtmlParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.util.StringUtils;

public class PrescriptionService {

  private static final Logger LOG = LoggerFactory.getLogger(PrescriptionService.class);

  private final Gmail gmail;
  private final Calendar calendar;

  public PrescriptionService(OAuth2AuthorizedClient client) throws IOException, GeneralSecurityException {
    this.gmail = (Gmail) GoogleServiceFactory.getService(GoogleServiceType.GMAIL, client);
    this.calendar = (Calendar) GoogleServiceFactory.getService(GoogleServiceType.CALENDAR, client);
  }

  public List<Prescription> getActivePrescriptions() throws IOException {

    //TODO: parameterize the below
    String user = "me";
    String query = "from:Hs-no.reply@e-prescription.gr AND (subject:Έκδοση OR subject:Επιτυχής)";
    Long maxResults = 100L;

    // Read messages
    ListMessagesResponse listMessagesResponse = gmail.users().messages().list(user).setQ(query).setMaxResults(maxResults).execute();
    List<Message> messages = listMessagesResponse.getMessages();
    LOG.info("Got {} prescription emails", messages.size());

    HtmlParser htmlParser = new HtmlParser();
    List<Prescription> prescriptions = new ArrayList<>();
    Set<String> ignoreList = new HashSet<>();
    for (Message message : messages) {
      Message detailedMessage = gmail.users().messages().get(user, message.getId()).execute();

      //Executed - need to ignore it
      String claimedPrescriptionId = htmlParser.parseClaimedPrescriptionId(detailedMessage.getSnippet());
      if (StringUtils.hasText(claimedPrescriptionId)) {
        ignoreList.add(claimedPrescriptionId);
        continue;
      }

      byte[] data = detailedMessage.getPayload().getBody().decodeData();
      String dataString = new String(data, StandardCharsets.UTF_8);
      Prescription prescription = htmlParser.parsePrescriptionEmailHtml(dataString);

      //Non-expired prescriptions only
      if (!ignoreList.contains(prescription.id()) && prescription.isValid() && prescription.isActive()) {
        prescriptions.add(prescription);
      }
    }

    return prescriptions;
  }

  public void createEventsForPrescriptions(List<Prescription> prescriptions) {

    List<Event> events = new ArrayList<>();

    prescriptions.forEach(prescription -> events.add(this.translatePrescriptionToEvent(prescription)));

    for (Event event : events) {
      LOG.info("Creating event: {} ({})", event.getSummary(), event.getStart().getDate());
//      calendar.events().insert("primary", event).execute();
    }

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
