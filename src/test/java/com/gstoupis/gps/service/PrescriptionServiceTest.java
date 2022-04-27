package com.gstoupis.gps.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PrescriptionServiceTest {

  private static PrescriptionService service;

  @BeforeAll
  static void setup() throws GeneralSecurityException, IOException {
    service = new PrescriptionService();
  }

  @Test
  void getActivePrescriptions() throws IOException {

    System.out.println(service.getActivePrescriptions());

  }
}