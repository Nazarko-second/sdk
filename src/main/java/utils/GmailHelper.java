package utils;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import configuration.SetupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class GmailHelper {

    GmailClient gmail = new GmailClient();
    private static final Logger LOGGER = LoggerFactory.getLogger(GmailHelper.class);
    private static final int TIMEOUT_BETWEEN_ATTEMPTS = 5000;
    private static final int MAX_ATTEMPTS = 24;
    private static final String senderEmailQuery = SetupConfiguration.ENVIRONMENT_EMAIL_ADDRESS;

    /**
     * Get temp password from email sent to user
     *
     * @param email email address
     */
    public String getResetPasswordUrlFromEmail(String email) {

        String email_template = "(?s).*(https.*token=\\w+).*";
        String query = senderEmailQuery + email + " subject:Verilife password reset";
        //" after:" + LocalDateTime.now().minusMinutes(10).format(DateTimeFormatter.ofPattern("yyyy/M/dd")) ;

        String results = null;
        try {
            results = getSomethingDescribedByRegexpFromEmail(email, email_template, query).replace("amp;", "");
        } catch (GeneralSecurityException | MessagingException | IOException e) {
        }
        LOGGER.info("Reset URL link is: " + results);
        return results;
    }


    /**
     * Get substring from email content
     *
     * @param email          email address
     * @param email_template regexp that describe substring from expected email
     * @param query          Gmail-like query for email search (example: from:paris-noreply@comptiaglobal.org to:username@server.com subject:Nito Delivery System)
     * @return
     * @throws IOException
     * @throws MessagingException
     * @throws GeneralSecurityException
     */
    public String getSomethingDescribedByRegexpFromEmail(String email, String email_template, String query) throws GeneralSecurityException, IOException, MessagingException {

        LOGGER.info("Email: " + email);
        LOGGER.info("Email Template: " + email_template);
        LOGGER.info("Email Query: " + query);

        String results = null;

        List<Message> messages = new ArrayList<>();
        int attempt = 1;

        Gmail service = gmail.getSession();

        try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (attempt <= MAX_ATTEMPTS) {
            messages = gmail.listMessagesMatchingQuery(service, "me", query);
            if (messages.size() > 0) break;

            LOGGER.info("NO MESSAGES FOUND FROM GMAIL. Attempt: " + attempt);
            try {
                Thread.sleep(TIMEOUT_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempt++;
        }

        LOGGER.info("Number of messages " + messages.size());
        for (Message message : messages) {
            results = (gmail.getMimeMessage(service, "me", message.getId())).replaceAll(email_template, "$1");
            if (results != null)
                return results;
        }

        return results;
    }

    private List<Message> getAllMessages(String query) throws GeneralSecurityException, IOException, MessagingException {
        List<Message> messages = new ArrayList<>();
        int attempt = 1;

        Gmail service = gmail.getSession();

        try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (attempt <= MAX_ATTEMPTS) {
            messages = gmail.listMessagesMatchingQuery(service, "me", query);
            if (messages.size() > 0) break;

            LOGGER.info("NO MESSAGES FOUND FROM GMAIL. Attempt: " + attempt);
            try {
                Thread.sleep(TIMEOUT_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempt++;
        }
        return messages;
    }

    public void deleteResetPasswordEmails(String email) {
        LOGGER.info("Trying to delete messages");
        String subjectPartOfQuery = " subject:Verilife password reset";

        try {
            deleteEmailBySubject(email, subjectPartOfQuery);
        } catch (GeneralSecurityException | MessagingException | IOException e) {
        }

    }


    public boolean isConfirmOrderEmailReceived(String email, String store) {
        LOGGER.info("Checking if order confirmation email has arrived");
        String subject = String.format(" subject:Your %s order confirmation", store);
        String query = senderEmailQuery + email + subject;
        List<Message> allMessages = new ArrayList<>();
        try {
            allMessages = getAllMessages(query);
        } catch (MessagingException | GeneralSecurityException | IOException e){LOGGER.info("Exception in GmailHelper.isConfirmOrderEmailReceived method");}

        return allMessages.size() > 0;
    }

    private void deleteEmailBySubject(String email, String subject) throws GeneralSecurityException, IOException, MessagingException {
        String query = senderEmailQuery + email + subject;
        int counter = 0;
        LOGGER.info("Email: " + email);
        LOGGER.info("Email Query: " + query);

        Gmail service = gmail.getSession();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Message> messages = gmail.listMessagesMatchingQuery(service, "me", query);
        if (messages.size() == 0) LOGGER.info("No messages to delete");

        for (Message message : messages) {
//            LOGGER.info(message.toPrettyString());
            gmail.deleteMessage(service, "me", message.getId());
            counter++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info(counter + " email(s) deleted");
    }

}
