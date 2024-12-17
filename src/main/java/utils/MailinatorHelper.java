package utils;

import com.manybrain.mailinator.client.message.Message;
import configuration.DataRepository;
import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MailinatorHelper {
    public MailinatorClientLocal mailinator = new MailinatorClientLocal();
    private static final Logger LOGGER = LoggerFactory.getLogger(MailinatorHelper.class);
    private static final int TIMEOUT_BETWEEN_ATTEMPTS = 5000;
    private static final int MAX_ATTEMPTS = 12;
    static final int MAX_SECONDS_AGO = 30;


    public void deleteAllResetPasswordEmails(String email) {
        LOGGER.info("Deleting reset password emails");
        String domain = "@" + SetupConfiguration.MAILINATOR_DOMAIN;
        String inbox = email.split("@")[0];
        int count = mailinator.deleteAllMessagesForInbox(domain, inbox);
        LOGGER.info("Deleted {} reset password emails", count);
    }

    public void deleteAllGuestOrderConfirmationEmails(String email) {
        LOGGER.info("Deleting Guest order confirmation emails");
//        String domain = "@" + SetupConfiguration.MAILINATOR_DOMAIN;
        String domain = email.split("@")[1];
        String inbox = email.split("@")[0];
        int count = mailinator.deleteAllMessagesForInbox(domain, inbox);
        LOGGER.info("Deleted {} reset password emails", count);
    }

//    public String getResetPasswordUrlFromEmail(String receiver) {
//        LOGGER.info("Trying to get Reset password link from email");
//        String sender = SetupConfiguration.MAILINATOR_RESET_PWD_SENDER;
//        String resetSubject = SetupConfiguration.RESET_PWD_EMAIL_SUBJECT;
//        String resetUrlRegex = DataRepository.Instance.getParametersForTest("EmailTest").get("resetUrlRegex");
//
//        String messageBody = getMessageBody(resetSubject, sender, receiver);
//        if (messageBody.isEmpty()) {
//            LOGGER.info("Reset password email was not received");
//        }
//        return messageBody.replaceAll(resetUrlRegex, "$1").replace("amp;", "");
//    }

//    public boolean isPasswordUpdatedEmailReceived() {
//        LOGGER.info("Checking if update password email was received");
//        String sender = SetupConfiguration.MAILINATOR_UPDATE_PWD_SENDER;
//        String receiver = SetupConfiguration.UPDATE_PWD_RECEIVER;
//        String subject = SetupConfiguration.UPDATE_PWD_EMAIL_SUBJECT;
//
//        String msgBody = getMessageBody(subject, sender, receiver);
//
//        boolean result = msgBody.contains("Your password has been updated");
//        if (result) {
//            LOGGER.info("Password update email has been received");
//        } else {
//            LOGGER.info("Password update email has NOT been received");
//        }
//        return result;
//    }


    public String getMessageBody(String emailSubject, String expectedSender, String orderEmailAddress) {
        LOGGER.info("Trying to get message id");
        String messageId = "";
        int attempt = 1;

        List<Message> messageSummaries;

        while (attempt <= MAX_ATTEMPTS) {
            LOGGER.info("Trying to find message. Attempt: " + attempt);

            messageSummaries = mailinator.getMessageSummaries(orderEmailAddress);

            for (Message m : messageSummaries) {
                String subj = m.getSubject();
                String id = m.getId();
                Long receivedSecondsAgo = m.getSecondsAgo();
                String sender = m.getOrigfrom();
                if (Objects.equals(subj.toLowerCase(), emailSubject.toLowerCase()) && Objects.equals(sender.toLowerCase(), expectedSender.toLowerCase()) && receivedSecondsAgo < MAX_SECONDS_AGO) {
                    messageId = id;
                    break;
                }
            }
            if (!Objects.equals(messageId, "")) break;
            LOGGER.info("Message has not been found.");
            try {
                Thread.sleep(TIMEOUT_BETWEEN_ATTEMPTS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempt++;
        }

        if (Objects.equals(messageId, "")) return "";

        LOGGER.info("Message id: " + messageId);

        return mailinator.getMessageById(messageId, orderEmailAddress);
    }

//    public String getWelcomeEmail(String email, String store) {
//        LOGGER.info(String.format("Getting welcome email for %s in %s", email, store));
//        String from = SetupConfiguration.MAILINATOR_WELCOME_SENDER;
//        String subjectBase = SetupConfiguration.WELCOME_EMAIL_SUBJECT;
//
//        String expectedSubject = String.format("%s %s", subjectBase, store);
//
//        return getMessageBody(expectedSubject, from, email);
//    }

}
