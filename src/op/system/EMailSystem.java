package op.system;

import entity.system.SYSPropsTools;
import entity.system.Users;
import op.OPDE;
import op.threads.DisplayMessage;
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
import java.util.ArrayList;
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

    public static boolean isMailsystemActive() {
        return OPDE.getProps().containsKey(SYSPropsTools.KEY_MAIL_SYSTEM_ACTIVE) && OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_SYSTEM_ACTIVE).equalsIgnoreCase("true");
    }

    /**
     * Sends a standard error email to the predefined recipient, but only if the EMail System is activated.
     *
     * @return false, if there was an mail exception. true otherwise.
     */
    public static boolean sendErrorMail(String message, File attachment) {
        if (!isMailsystemActive()) {
            return false;
        }

        Recipient errorMailRecipient = new Recipient(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT), OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL));

//        Pair<String, String> pair = new Pair<String, String>(OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT), OPDE.getProps().getProperty(SYSPropsTools.KEY_MAIL_RECIPIENT_PERSONAL));
//        Pair<String, String>[] pairs = new Pair[]{pair};

        InetAddress localMachine = null;
        try {
            localMachine = InetAddress.getLocalHost();
        } catch (java.net.UnknownHostException uhe) {
            OPDE.error(uhe);
        }

        String bodyText = SYSTools.xx("mail.errormail.line1") + "\n" +
                SYSTools.xx("mail.errormail.line2") + ": " + localMachine != null ? localMachine.getHostName() : "??" + "\n" +
                SYSTools.xx("mail.errormail.line3") + ": " + localMachine != null ? localMachine.getHostAddress() : "??" + "\n" +
                SYSTools.xx("mail.errormail.line4") + ": " + OPDE.getLogin().getUser().getUID() + "\n" +
                SYSTools.xx("mail.errormail.line5") + ": " + DateFormat.getDateTimeInstance().format(new Date()) + "\n\n\n" +
                SYSTools.xx("mail.errormail.line6");

        return sendMail(SYSTools.xx("mail.errormail.subject") + ": " + message, bodyText, new Recipient[]{errorMailRecipient}, new File[]{attachment}, OPDE.getProps());

    }

    /**
     * sends an email message. but only if the EMail System is active and working.
     *
     * @param subject    the text for the subject line
     * @param bodyText   the text for the mail body
     * @param recipients this is an array of recipients.
     * @param attach     array of files to attach
     * @return true if sent successfully, false if not
     */
//    public static boolean sendMail(String subject, String bodyText, Pair<String, String>[] recipients, File[] attach) {
//        if (!isMailsystemActive()) {
//            return false;
//        }
//        return send(subject, bodyText, recipients, attach, OPDE.getProps());
//    }
    public static boolean sendMail(String subject, String bodyText, Users user, File[] attach) {


        return sendMail(subject, bodyText, new Recipient[]{new Recipient(user)}, attach, OPDE.getProps());
    }

    public static boolean sendMail(String subject, String bodyText, Recipient recipient, File[] attach) {
//        if (!isMailsystemActive()) {
//            return false;
//        }

        return sendMail(subject, bodyText, new Recipient[]{recipient}, attach, OPDE.getProps());
    }

    public static boolean sendMail(String subject, String bodyText, Users[] users, File[] attach) {
//        if (!isMailsystemActive()) {
//            return false;
//        }

        ArrayList<Recipient> recipients = new ArrayList<>();
        for (Users user : users) {
            recipients.add(new Recipient(user));
        }

        return sendMail(subject, bodyText, recipients.toArray(new Recipient[]{}), attach, OPDE.getProps());
    }


    public static boolean sendMail(String subject, String bodyText, Recipient[] recipients, File[] attach, final Properties mailProps) {

        boolean success;

        try {

            javax.mail.Authenticator auth = new javax.mail.Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(mailProps.getProperty(SYSPropsTools.KEY_MAIL_USER), mailProps.getProperty(SYSPropsTools.KEY_MAIL_PASSWORD));
                }
            };

            Session session = Session.getInstance(mailProps, auth);

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailProps.getProperty(SYSPropsTools.KEY_MAIL_SENDER), mailProps.getProperty(SYSPropsTools.KEY_MAIL_SENDER_PERSONAL)));


            for (Recipient recipient : recipients) {
                msg.addRecipient(Message.RecipientType.TO, recipient.getInternetAddress());
            }

            msg.setSubject(subject);


            BodyPart messageBodyPart = new MimeBodyPart();


            String sendText = bodyText;
            if (!SYSTools.catchNull(mailProps.getProperty(SYSPropsTools.KEY_MAIL_SPAMFILTER_KEY)).isEmpty()) {
                sendText += "<br/>--<br/>" + SYSTools.xx("opde.settings.global.mail.recipient.spamfilter") + ": " + mailProps.getProperty(SYSPropsTools.KEY_MAIL_SPAMFILTER_KEY);
            }

            messageBodyPart.setContent("<html>" + sendText + "</html>", "text/html; charset=utf-8");

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


    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

}
