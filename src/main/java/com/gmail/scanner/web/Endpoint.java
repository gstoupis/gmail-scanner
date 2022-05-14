package com.gmail.scanner.web;

import com.gmail.scanner.google.GoogleServiceProvider;
import com.gmail.scanner.security.OAuth2AuthorizedClientProvider;
import com.gmail.scanner.service.PrescriptionService;
import com.gmail.scanner.service.model.Prescription;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO:
 * 1. Add unit tests
 * 2. Decide where this will run & how frequently
 */
@RestController
public class Endpoint {

  private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);

  private final GoogleServiceProvider googleServiceProvider;
  private final OAuth2AuthorizedClientProvider oauth2AuthorizedClientProvider;

  public Endpoint(GoogleServiceProvider googleServiceProvider, OAuth2AuthorizedClientProvider oauth2AuthorizedClientProvider) {
    this.googleServiceProvider = googleServiceProvider;
    this.oauth2AuthorizedClientProvider = oauth2AuthorizedClientProvider;
  }

  @GetMapping("/")
  public String index() {
    return "Greetings from Spring Boot!";
  }

  @GetMapping("/scan")
  public String scan() throws IOException, GeneralSecurityException {

    PrescriptionService prescriptionService = new PrescriptionService(googleServiceProvider, oauth2AuthorizedClientProvider);

    List<Prescription> prescriptions = prescriptionService.getActivePrescriptions();
    LOG.info("Active prescriptions: {}", prescriptions);

    prescriptionService.createEventsForPrescriptions(prescriptions);
    return "done";
  }
}