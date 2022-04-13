package com.gstoupis.gps.parser;

import com.gstoupis.gps.model.Prescription;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HtmlParser {

    private static final String FROM_DATE_PRE_TEXT = "ΑΠΟ:";
    private static final String TO_DATE_PRE_TEXT = "ΕΩΣ:";
    private static final String ID_PRETEXT = "Η συνταγή με barcode";

    public Prescription parsePrescriptionEmailHtml(String prescriptionEmailBody) {

        Prescription prescription = new Prescription();

        Document document = Jsoup.parse(prescriptionEmailBody);
        Elements paragraphs = document.getElementsByTag("p");

        paragraphs.stream()
                .filter(p -> p.text().startsWith(FROM_DATE_PRE_TEXT))
                .findFirst()
                .ifPresent(x -> prescription.setFrom(x.text().replace(FROM_DATE_PRE_TEXT, "").trim()));

        paragraphs.stream()
                .filter(p -> p.text().startsWith(TO_DATE_PRE_TEXT))
                .findFirst()
                .ifPresent(x -> prescription.setTo(x.text().replace(TO_DATE_PRE_TEXT, "").trim()));

        document.getElementsByTag("h2").stream()
                .filter(h -> h.text().startsWith(ID_PRETEXT))
                .findFirst()
                .ifPresent(x -> prescription.setId(x.text().replace(ID_PRETEXT, "").trim().split(" ")[0].trim()));

        return prescription;
    }

}
