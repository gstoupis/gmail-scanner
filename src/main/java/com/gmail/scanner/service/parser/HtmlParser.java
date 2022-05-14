package com.gmail.scanner.service.parser;

import com.gmail.scanner.service.model.Prescription;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlParser {

  private static final String FROM_DATE_PRE_TEXT = "ΑΠΟ:";
  private static final String TO_DATE_PRE_TEXT = "ΕΩΣ:";
  private static final String ID_PRETEXT = "Η συνταγή με barcode";
  public static final String CLAIMED_TEXT = "εκτελέστηκε";

  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

  public Prescription parsePrescriptionEmailHtml(String prescriptionEmailBody) {

    Document document = Jsoup.parse(prescriptionEmailBody);
    Elements paragraphs = document.getElementsByTag("p");

    Optional<String> from =
        paragraphs.stream()
            .filter(p -> p.text().startsWith(FROM_DATE_PRE_TEXT))
            .findFirst()
            .map(x -> x.text().replace(FROM_DATE_PRE_TEXT, "").trim());

    Optional<String> to =
        paragraphs.stream()
            .filter(p -> p.text().startsWith(TO_DATE_PRE_TEXT))
            .findFirst()
            .map(x -> x.text().replace(TO_DATE_PRE_TEXT, "").trim());

    Optional<String> id =
        document.getElementsByTag("h2").stream()
            .filter(h -> h.text().startsWith(ID_PRETEXT))
            .findFirst()
            .map(x -> x.text().replace(ID_PRETEXT, "").trim().split(" ")[0].trim());

    //TODO: This sucks
    if (from.isEmpty() || to.isEmpty() || id.isEmpty()) {
      return null;
    }

    LocalDate fromDate = LocalDate.parse(from.get(), dtf);
    LocalDate toDate = LocalDate.parse(to.get(), dtf);

    return new Prescription(id.get(), fromDate, toDate);
  }

  public String parseClaimedPrescriptionId(String snippet) {
    return snippet.startsWith(ID_PRETEXT) && snippet.contains(CLAIMED_TEXT) ?
        snippet.substring(snippet.indexOf(ID_PRETEXT) + ID_PRETEXT.length(), snippet.indexOf(CLAIMED_TEXT)).trim() : null;
  }
}
