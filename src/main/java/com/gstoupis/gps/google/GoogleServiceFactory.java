package com.gstoupis.gps.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

public class GoogleServiceFactory {

  /**
   * Application name.
   */
  private static final String APPLICATION_NAME = "gmail-prescription-scanner";
  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
  /**
   * Directory to store authorization tokens for this application.
   */
  private static final String TOKENS_DIRECTORY_PATH = "tokens";

  private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

  /**
   * Global instance of the scopes required by this quickstart. If modifying these scopes, delete your previously saved tokens/ folder.
   */
  private static final List<String> SCOPES = Arrays.asList(GmailScopes.GMAIL_READONLY, CalendarScopes.CALENDAR);

  private static Gmail gmail;
  private static Calendar calendar;

  public static AbstractGoogleJsonClient getService(GoogleServiceType serviceType) throws GeneralSecurityException, IOException {

    return switch (serviceType) {
      case GMAIL -> getGmailInstance();
      case CALENDAR -> getCalendarInstance();
    };
  }

  private static Gmail getGmailInstance() throws IOException, GeneralSecurityException {
    if (gmail == null) {
      // Build a new authorized API client service.
      final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      gmail = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport)).setApplicationName(APPLICATION_NAME).build();
    }

    return gmail;
  }

  private static Calendar getCalendarInstance() throws GeneralSecurityException, IOException {

    if (calendar == null) {
      // Build a new authorized API client service.
      final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      calendar = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport)).setApplicationName(APPLICATION_NAME).build();
    }

    return calendar;
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param httpTransport The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException If the credentials.json file cannot be found.
   */
  private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
    // Load client secrets.
    InputStream in = GoogleServiceFactory.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
    if (in == null) {
      throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
    }
    GoogleClientSecrets clientSecrets =
        GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

    // Build flow and trigger user authorization request.
    GoogleAuthorizationCodeFlow flow =
        new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build();
    LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
    return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
  }
}
