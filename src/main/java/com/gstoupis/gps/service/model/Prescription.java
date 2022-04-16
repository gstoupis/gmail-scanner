package com.gstoupis.gps.service.model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Stream;

public record Prescription(String id, LocalDate from, LocalDate to) {

  public boolean isValid() {
    return Stream.of(id, from, to).noneMatch(Objects::isNull);
  }

  public boolean isActive() {
    return to.isAfter(LocalDate.now());
  }

  public boolean isUnclaimed() {
    //TODO: implement logic
    return false;
  }

}
