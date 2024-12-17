package utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import configuration.DataRepository;
import datasources.FileManager;
import datasources.RandomDataGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Gmail setup:
 * https://console.developers.google.com/apis/
 * Enable Gmail API
 * Credentials
 * Create OAuth client ID
 */
public class GmailClient {

    private static final int TIMEOUT_BETWEEN_ATTEMPTS = 5000;
    public static final String MAIL_ADDRESSES_SEPARATOR = ";";
    public Logger LOGGER = LoggerFactory.getLogger(GmailClient.class);

    private static final String APPLICATION_NAME = "Verilife Automation";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "src/main/resources/credentials/gmail"; // Directory to store user credentials.

    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);
    private static final String CLIENT_CREDENTIALS_FILE = "src/main/resources/credentials/gmail/client_secret.json";//client_secret.json";

    private static final String DEFAULT_EMAIL = "oleksandr.diachuk@@fortegrp.com";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        InputStream in = new FileInputStream(CLIENT_CREDENTIALS_FILE);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

// Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

    // TODO remove
    public static void main(String... args) throws IOException, GeneralSecurityException, javax.mail.MessagingException {
        // Build a new authorized API client service.
        /*
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

        // Print the labels in the user's account.
        String user = "me";
        ListMessagesResponse listResponse = service.users().messages().list(user).execute();
        List<Message> mails = listResponse.getMessages();
        if (mails.isEmpty()) {
        System.out.println("No messages found.");
        } else {
        System.out.println("Mails: ");
        for (Message mail : mails) {
        System.out.printf("- %s\n", mail.getRaw());
        }
        }
        */

        GmailClient gh = new GmailClient();
        //gh.sendEmailWithContentTo("","");
        /* final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
        .setApplicationName(APPLICATION_NAME)
        .build();

        listMessagesMatchingQuery(service, "me", "from:atomadmin");
        */
    }

    public static List<Message> listMessagesMatchingQuery(Gmail service, String userId,
                                                          String query) throws IOException, javax.mail.MessagingException {
        ListMessagesResponse response = service.users().messages().list(userId).setQ(query).execute();

        List<Message> messages = new ArrayList<Message>();
        while (response.getMessages() != null) {
            messages.addAll(response.getMessages());
            if (response.getNextPageToken() != null) {
                String pageToken = response.getNextPageToken();
                response = service.users().messages().list(userId).setQ(query)
                        .setPageToken(pageToken).execute();
            } else {
                break;
            }
        }

        return messages;
    }

    public static String getMimeMessage(Gmail service, String userId, String messageId)
            throws IOException, javax.mail.MessagingException {
        Message message = service.users().messages().get(userId, messageId).setFormat("raw").execute();

        byte[] emailBytes = Base64.decodeBase64(message.getRaw());

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session, new ByteArrayInputStream(emailBytes));

        String result = new BufferedReader(new InputStreamReader(email.getInputStream()))
                .lines().collect(Collectors.joining("\n"));

        return result;
    }

    public void deleteMessage(Gmail service, String userId, String messageId) {
        try {
            Gmail.Users.Messages.Delete del = service.users().messages().delete(userId, messageId);
            del.execute(); // actually deletes a message
            LOGGER.info("Message deleted: " + del);
        } catch (IOException io) {
            LOGGER.info("Failed to delete ((");
        }
    }


    public String getAttachmentFromMessage(Gmail service, String userId, String messageId, String expectedNamePart)
            throws IOException, MessagingException {

        Message message = service.users().messages().get(userId,messageId).execute();

            List<MessagePart> parts= message.getPayload().getParts();
            for(MessagePart part: parts) {
                if(part.getFilename()!=null && part.getFilename().length()>0 && part.getFilename().contains(expectedNamePart)) {

                    String attId = part.getBody().getAttachmentId();
                    MessagePartBody attachPart = service.users().messages().attachments().
                            get(userId, messageId, attId).execute();

                    byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());

                    String result = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteArray), Charset.forName("ISO-8859-1")))
                            .lines().collect(Collectors.joining("\n"));

                    String fileLocation = FileManager.OUTPUT_DIR + "/" + RandomDataGenerator.getCurDateTime() + "_" + part.getFilename().replace("/","-");
                    FileManager.createFile( fileLocation, result);
//
//
//                    result = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteArray), StandardCharsets.UTF_8))
//                            .lines().collect(Collectors.joining("\n"));
//
//                    fileLocation = FileManager.OUTPUT_DIR + "/utf8_" + RandomDataGenerator.getCurDateTime() + "_" + part.getFilename().replace("/","-");
//                    FileManager.createFile( fileLocation, result);
//
//                    result = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteArray), StandardCharsets.ISO_8859_1))
//                            .lines().collect(Collectors.joining("\n"));
//
//                    fileLocation = FileManager.OUTPUT_DIR + "/iso_" + RandomDataGenerator.getCurDateTime() + "_" + part.getFilename().replace("/","-");
//                    FileManager.createFile( fileLocation, result);

                    return  fileLocation;
                }
            }
            return "";
    }

    public void sendEmailWithContentTo(String to, String subject, String body) throws GeneralSecurityException, IOException, MessagingException {

        final NetHttpTransport HTTP_TRANSPORT;
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        Gmail service = null;
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        sendMessage(service, "me", createEmail(to,DEFAULT_EMAIL, subject, body));
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }


    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        for(String mail : to.split(MAIL_ADDRESSES_SEPARATOR)) {
            if(!mail.equals(""))
                email.addRecipient(javax.mail.Message.RecipientType.TO,
                        new InternetAddress(mail));
        }
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public Gmail getSession() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT;
        HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
}