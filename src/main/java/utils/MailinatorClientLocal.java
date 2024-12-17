package utils;

import com.manybrain.mailinator.client.MailinatorClient;
import com.manybrain.mailinator.client.message.*;
import configuration.ProjectConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class MailinatorClientLocal {

    private final String MAILINATOR_API_TOKEN = System.getenv("MAILINATOR_KEY");
    private static final Logger LOGGER = LoggerFactory.getLogger(MailinatorClientLocal.class);
    private static final int TIMEOUT_BETWEEN_ATTEMPTS = 5000;
    private static final int MAX_ATTEMPTS = 1;
    MailinatorClient mailinatorClient = new MailinatorClient(MAILINATOR_API_TOKEN);

    /**
     * Delete all emails from specific inbox
     * @param domain "private" or specific domain (for ex. 'pharmatest.testinator.com')
     * @param inbox inbox name
     * @return count of deleted emails
     */
    public int deleteAllMessagesForInbox(String domain, String inbox) {
        DeletedMessages deletedMessages = mailinatorClient.request(new DeleteInboxMessagesRequest(domain, inbox));
        return deletedMessages.getCount();
    }

    /**
     * Retrieves a list of messages summaries. You can retrieve a list by inbox, inboxes, or entire domain
     * @param email - inbox email (ex. 'order@pharmatest.testinator.com')
     * @return list of all inbox message subjects
     */
    public List<Message> getMessageSummaries(String email) {
        String[] emailParts = email.split("@");
        List<Message> messages = new ArrayList<>();
        int attempt = 1;

        try {
            Thread.sleep(1000);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (attempt <= MAX_ATTEMPTS) {
            Inbox inbox = mailinatorClient.request(new GetInboxRequest(emailParts[1]));
            messages = inbox.getMsgs();

            if (messages.size() > 0) break;

            LOGGER.info("NO MESSAGES FOUND FROM MAILINATOR. Attempt: " + attempt);
            try {
                Thread.sleep(TIMEOUT_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempt++;
        }
        return messages;
    }

    /**
     * Get message body by message id
     * @param id of a message
     * @param orderEmail - our inbox
     * @return body of the message as plain text
     */
    public String getMessageById(String id, String orderEmail) {
        LOGGER.info("Trying to get message body by message id");

        String[] emailParts = orderEmail.split("@");
        String messageBody = "";

        Message m = mailinatorClient.request(
                new GetMessageRequest(emailParts[1], emailParts[0], id));

        List<Part> parts = m.getParts();
        if(parts.size() > 0) {
            messageBody = parts.get(0).getBody();
        }
        return messageBody;
    }

}
