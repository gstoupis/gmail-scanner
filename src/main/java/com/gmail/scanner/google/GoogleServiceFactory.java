package com.gmail.scanner.google;

import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.gmail.Gmail;
import java.io.IOException;
import java.security.GeneralSecurityException;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;

public class GoogleServiceFactory {

  /**
   * Application name.
   */
  private static final String APPLICATION_NAME = "gmail-scanner";
  /**
   * Global instance of the JSON factory.
   */
  private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();


  private GoogleServiceFactory() {

  }

  private static Gmail gmail;
  private static Calendar calendar;

  public static AbstractGoogleJsonClient getService(GoogleServiceType serviceType, OAuth2AuthorizedClient client) throws GeneralSecurityException, IOException {

    return switch (serviceType) {
      case GMAIL -> getGmailInstance(client);
      case CALENDAR -> getCalendarInstance(client);
    };
  }

  private static Gmail getGmailInstance(OAuth2AuthorizedClient client) throws IOException, GeneralSecurityException {
    if (gmail == null) {
      // Build a new authorized API client service.
      final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      gmail = new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(client)).setApplicationName(APPLICATION_NAME).build();
    }

    return gmail;
  }

  private static Calendar getCalendarInstance(OAuth2AuthorizedClient client) throws GeneralSecurityException, IOException {

    if (calendar == null) {
      // Build a new authorized API client service.
      final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      calendar = new Calendar.Builder(httpTransport, JSON_FACTORY, getCredentials(client)).setApplicationName(APPLICATION_NAME).build();
    }

    return calendar;
  }

  private static Credential getCredentials(final OAuth2AuthorizedClient client) {

    Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod());
    credential.setAccessToken(client.getAccessToken().getTokenValue());
    return credential;
  }
}
