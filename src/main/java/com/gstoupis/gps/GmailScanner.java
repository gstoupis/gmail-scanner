package com.gstoupis.gps;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.gstoupis.gps.gmail.GmailServiceProvider;
import com.gstoupis.gps.model.Prescription;
import com.gstoupis.gps.parser.HtmlParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.List;

public class GmailScanner {


    public static void main(String... args) throws IOException, GeneralSecurityException {

        Gmail service = GmailServiceProvider.getService();

        //TODO: parameterize the below
        String user = "me";
        String query = "from:Hs-no.reply@e-prescription.gr AND subject:Έκδοση";
        Long maxResults = 100L;

        // Read messages
        ListMessagesResponse listMessagesResponse = service.users().messages().list(user)
                .setQ(query)
                .setMaxResults(maxResults)
                .execute();
        List<Message> messages = listMessagesResponse.getMessages();
        System.out.println("Got " + messages.size() + " messages");

        HtmlParser htmlParser = new HtmlParser();
        for (Message message : messages) {
            Message detailedMessage = service.users().messages().get(user, message.getId()).execute();
            byte[] data = detailedMessage.getPayload().getBody().decodeData();
            String dataString = new String(data, StandardCharsets.UTF_8);
            Prescription prescription = htmlParser.parsePrescriptionEmailHtml(dataString);
            System.out.println(prescription + " - isValid:" + prescription.isValid());
        }

    }
}