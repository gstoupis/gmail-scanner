package com.gstoupis.gps.model;

import java.util.Objects;
import java.util.stream.Stream;

public class Prescription {

    private String id;
    private String from;
    private String to;

    public String getId() {
        return id;
    }

    public boolean isValid() {
        return Stream.of(this.id, this.from, this.to).noneMatch(Objects::isNull);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "Prescription{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
