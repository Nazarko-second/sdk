package utils;


import configuration.ProjectConfiguration;
import configuration.SetupConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reporting.ReporterManager;

import javax.security.auth.login.Configuration;
import java.util.HashMap;

public class EmailHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailHelper.class);
    public static final ReporterManager reporter = ReporterManager.Instance;
    private static final String emailClient = SetupConfiguration.EMAIL_CLIENT;

    /**
     * Get temp reset password link from email sent to user
     *
     * @param email email address
     * @return url ro reset password
     */
    public String getResetPasswordUrlFromEmail(String email) {
        reporter.info("Getting reset password URL from email");
        switch (emailClient) {
            case "gmail":
                return new GmailHelper().getResetPasswordUrlFromEmail(email);
            case "mailinator":
                return new MailinatorHelper().getResetPasswordUrlFromEmail(email);
        }
        return "";
    }

    /**
     * Delete all Reset password emails in the inbox
     *
     * @param email from where messages should be deleted
     */
    public void deleteAllResetPasswordEmails(String email) {

        switch (emailClient) {
            case "gmail":
                new GmailHelper().deleteResetPasswordEmails(email);
            case "mailinator":
                new MailinatorHelper().deleteAllResetPasswordEmails(email);
        }
    }

    /**
     * Delete all Guest order confirmation emails in the inbox
     *
     * @param email from where messages should be deleted
     */
    public void deleteAllGuestOrderConfirmationEmails(String email) {

        switch (emailClient) {
            case "gmail":
//                new GmailHelper().deleteResetPasswordEmails(email);
            case "mailinator":
                new MailinatorHelper().deleteAllGuestOrderConfirmationEmails(email);
        }
    }

    /**
     * Delete all Order Confirmation emails in the inbox
     *
     * @param email from where messages should be deleted
     */
    public void deleteAllOrderConfirmationEmails(String email) {
//TODO
//        switch (emailClient) {
//            case "gmail":
//                new GmailHelper().deleteResetPasswordEmails(email);
//            case "mailinator": TODO
//                return new MailinatorClientLocal().deleteResetPasswordEmails(email);
//        }
    }


    public boolean isConfirmOrderEmailReceived(String store, String orderConfirmationEmail) {
        // TODO move reading from config to Mailinator/Gmail Helper class
//        String orderConfirmationEmail = SetupConfiguration.MAILINATOR_ORDER_EMAIL;
        String from = SetupConfiguration.MAILINATOR_ORDER_CONFIRMATION_SENDER;
        String expectedSubject = String.format(SetupConfiguration.ORDER_CONFIRMATION_SUBJECT, store);
//        String expectedSubject = String.format(ProjectConfiguration.getConfigProperty("order_confirmation_email_subject"), store);
        reporter.info("Checking if order confirmation email has arrived.");
        switch (emailClient) {
            case "mailinator":
                MailinatorHelper mailinator = new MailinatorHelper();
                String msgBody = mailinator.getMessageBody(expectedSubject, from, orderConfirmationEmail);
                return msgBody.length() > 0;
            case "gmail":
                return new GmailHelper().isConfirmOrderEmailReceived(orderConfirmationEmail, store);
        }
        LOGGER.info("Email client name has not been found");
        return false;
    }

    public String getWelcomeEmail(HashMap<String, String> userData) {
        String welcomeEmail = userData.get("randomEmail");
//        String store = trimTypeFromStoreName(userData.get("store"));
        String store = userData.get("store");

        switch (emailClient) {
            case "mailinator":
                return new MailinatorHelper().getWelcomeEmail(welcomeEmail, store);
            case "gmail":
                // TODO if needed
        }
        LOGGER.info("Email client name has not been found");
        return "";
    }


    public String getResetPasswordEmail(String email) {
        LOGGER.info("Getting Reset Password email");
        String from = ProjectConfiguration.getConfigProperty("mailinator_reset_password_sender");
        String expectedSubject = ProjectConfiguration.getConfigProperty("reset_password_subject");

        switch (emailClient) {
            case "mailinator":
                MailinatorHelper mailinator = new MailinatorHelper();
                String msgBody = mailinator.getMessageBody(expectedSubject, from, email);
//                return msgBody.length() > 0;
                return msgBody;
            case "gmail":
                // TODO if needed
//                return new GmailHelper().isConfirmOrderEmailReceived(orderConfirmationEmail, store);
        }
        LOGGER.info("Email Client name has not been found");
        return "";
    }


    public boolean isPasswordUpdatedEmailReceived() {
        LOGGER.info("Trying to get Updated Password email");
//        String expectedSubject = ProjectConfiguration.getConfigProperty("password_update_subject");
        switch (emailClient) {
            case "mailinator":
                LOGGER.info("Using Mailinator");
                return new MailinatorHelper().isPasswordUpdatedEmailReceived();
            case "gmail":
                LOGGER.info("Using Gmail");
                // TODO if needed
        }
        LOGGER.info("Password update email has not been found");
        return false;
    }


    private String trimTypeFromStoreName(String storeFull) {
        String[] storeParts = storeFull.split(" ");
        StringBuilder store = new StringBuilder();
        for (int i = 0; i < storeParts.length - 1; i++) {
            store.append(storeParts[i]).append(" ");
        }
        return store.toString().trim();
    }

}
