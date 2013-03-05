package op.system;

import entity.files.SYSFilesTools;
import op.OPDE;
import op.settings.PnlSystemSettings;
import op.threads.DisplayMessage;
import op.tools.Pair;
import op.tools.SYSTools;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: tloehr
 * Date: 04.03.13
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
public class EMailSystem {
    public static final String KEY_HOST = "mail.smtp.host";
    public static String KEY_PORT = "mail.smtp.port";
    public static String KEY_PROTOCOL = "mail.transport.protocol";
    public static String KEY_AUTH = "mail.smtp.auth";
    public static String KEY_STARTTLS = "mail.smtp.starttls.enable";
    public static String KEY_TLS = "mail.smtp.tls";
    public static String KEY_USER = "mail.smtp.user";
    public static String KEY_PASSWORD = "mail.password";
    public static String KEY_SENDER = "mail.sender";
    public static String KEY_RECIPIENT = "mail.recipient";
    public static String KEY_SENDER_PERSONAL = "mail.sender.personal";
    public static String KEY_RECIPIENT_PERSONAL = "mail.recipient.personal";
    public static String KEY_MAILSYSTEM_ACTIVE = "mail.system.active";


    public static boolean isMailsystemActive() {
        return OPDE.getProps().containsKey(KEY_MAILSYSTEM_ACTIVE) && OPDE.getProps().getProperty(KEY_MAILSYSTEM_ACTIVE).equalsIgnoreCase("true");
    }

    /**
     * Sends a standard error email to the predefined recipient, but only if the EMail System is activated.
     *
     * @param throwable
     * @return false, if there was an mail exception. true otherwise.
     */
    public static boolean sendErrorMail(Throwable throwable) {
        if (!isMailsystemActive()) {
            return false;
        }

        String html = SYSTools.getThrowableAsHTML(throwable);
        File temp = SYSFilesTools.print(html, false);
        Pair<String, String> pair = new Pair<String, String>(OPDE.getProps().getProperty(KEY_RECIPIENT), OPDE.getProps().getProperty(KEY_RECIPIENT_PERSONAL));
        Pair<String, String>[] pairs = new Pair[]{pair};

        InetAddress localMachine = null;
        try {
            localMachine = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
            OPDE.error(uhe);
        }

        String bodyText = OPDE.lang.getString("mail.errormail.line1") + "\n" +
                OPDE.lang.getString("mail.errormail.line2") + ": " + localMachine != null ? localMachine.getHostName() : "??" + "\n" +
                OPDE.lang.getString("mail.errormail.line3") + ": " + localMachine != null ? localMachine.getHostAddress() : "??" + "\n" +
                OPDE.lang.getString("mail.errormail.line4") + ": " + OPDE.getLogin().getUser().getUID() + "\n" +
                OPDE.lang.getString("mail.errormail.line5") + ": " + DateFormat.getDateTimeInstance().format(new Date()) + "\n\n\n" +
                OPDE.lang.getString("mail.errormail.line6");

        return send(OPDE.lang.getString("mail.errormail.subject") + ": " + throwable.getMessage(), bodyText, pairs, new File[]{temp}, OPDE.getProps());

    }

    public static boolean sendMail(String subject, String bodyText, Pair<String, String>[] recipients, File[] attach) {
        if (!isMailsystemActive()) {
            return false;
        }
        return send(subject, bodyText, recipients, attach, OPDE.getProps());
    }

    public static boolean sendTestmail(Pair<String, String>[] recipients, File[] attach, Properties props) {
            return send(OPDE.lang.getString(PnlSystemSettings.internalClassID+".global.mail.testsubject"), OPDE.lang.getString(PnlSystemSettings.internalClassID+".global.mail.testbody"), recipients, attach, props);
        }

    private static boolean send(String subject, String bodyText, Pair<String, String>[] recipients, File[] attach, final Properties mailProps) {

        boolean success;

        try {

            javax.mail.Authenticator auth = new javax.mail.Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailProps.getProperty(KEY_USER), mailProps.getProperty(KEY_PASSWORD));
                }
            };

            Session session = Session.getInstance(mailProps, auth);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailProps.getProperty(KEY_SENDER), mailProps.getProperty(KEY_SENDER_PERSONAL)));

            for (Pair<String, String> recipient : recipients) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient.getFirst(), recipient.getSecond()));
            }

            msg.setSubject(subject);


            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(bodyText);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            if (attach != null && attach.length > 0) {
                for (File file : attach) {
                    messageBodyPart = new MimeBodyPart();
                    DataSource source = new FileDataSource(file);
                    messageBodyPart.setDataHandler(new DataHandler(source));
                    messageBodyPart.setFileName(file.getName());
                    multipart.addBodyPart(messageBodyPart);
                }
            }

            msg.setContent(multipart);
            msg.saveChanges();

            Transport.send(msg);
            success = true;
//            OPDE.getDisplayManager().clearSubMessages();
        } catch (MessagingException e1) {
            OPDE.info(e1);
            e1.printStackTrace();
//            OPDE.info("Mail-System is not configured");
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(e1.getMessage(), DisplayMessage.IMMEDIATELY));
            success = false;
        } catch (UnsupportedEncodingException e1) {
            OPDE.info(e1);
            e1.printStackTrace();
            OPDE.getDisplayManager().addSubMessage(new DisplayMessage(e1.getMessage(), DisplayMessage.IMMEDIATELY));
            e1.printStackTrace();
            success = false;
        }
        return success;
    }

}
