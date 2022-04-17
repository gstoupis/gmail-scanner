package com.gstoupis.gps.web;

import com.gstoupis.gps.service.PrescriptionService;
import com.gstoupis.gps.service.model.Prescription;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO:
 * 1. Add prescription.isClaimed() check
 * 2. Add logback
 * 3. Add unit tests
 * 4. Decide where this will run & how frequently
 */
@RestController("api/v1")
public class Endpoint {

  private static final Logger LOG = LoggerFactory.getLogger(Endpoint.class);

  @GetMapping("/")
  public String index() {
    return "Greetings from Spring Boot!";
  }

  @GetMapping("/scan")
  public String scan() throws IOException, GeneralSecurityException {
    PrescriptionService prescriptionService = new PrescriptionService();

    List<Prescription> prescriptions = prescriptionService.getActivePrescriptions();
    LOG.info("Active prescriptions: {}", prescriptions);

    prescriptionService.createEventsForPrescriptions(prescriptions);
    return "done";
  }
}