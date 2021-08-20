package plugins.wnplugin.util;

import plugins.wnplugin.config.EmailConfig;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeUtility;
import java.util.Date;
import java.util.Properties;

public class MailUtil {
    public static String EmailAccount;
    public static String EmailPassword;
    public static String EmailSMTPHost;
    public static String smtpPort;

    static {
        EmailAccount = EmailConfig.getEmailAccount();
        EmailPassword = EmailConfig.getEmailPwd();
        EmailSMTPHost = EmailConfig.getMailHost();
        smtpPort = EmailConfig.getEmailSmtpPort();
    }

    public static void reload() {
        EmailAccount = EmailConfig.getEmailAccount();
        EmailPassword = EmailConfig.getEmailPwd();
        EmailSMTPHost = EmailConfig.getMailHost();
        smtpPort = EmailConfig.getEmailSmtpPort();
    }

    public static boolean send(String receiveMailAccount, String sender, String subject, String content) {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", EmailSMTPHost);
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);
        Session session = Session.getDefaultInstance(props);
        session.setDebug(EmailConfig.getEnableDebug());
        MimeMessage message = null;

        try {
            message = createMimeMessage(session, EmailAccount, receiveMailAccount, sender, subject, content);
        } catch (Exception var13) {
            var13.printStackTrace();
            return false;
        }

        Transport transport = null;

        try {
            transport = session.getTransport();
        } catch (NoSuchProviderException var12) {
            var12.printStackTrace();
            return false;
        }

        try {
            transport.connect(EmailAccount, EmailPassword);
        } catch (MessagingException var11) {
            var11.printStackTrace();
            return false;
        }

        try {
            transport.sendMessage(message, message.getAllRecipients());
        } catch (MessagingException var10) {
            var10.printStackTrace();
            return false;
        }

        try {
            transport.close();
            return true;
        } catch (MessagingException var9) {
            var9.printStackTrace();
            return false;
        }
    }

    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail, String sender, String subject, String content) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sendMail, sender, "utf-8"));
        message.setRecipient(RecipientType.TO, new InternetAddress(receiveMail, receiveMail, "utf-8"));
        message.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
        message.setContent(content, "text/html;charset=utf-8");
        message.setSentDate(new Date());
        message.saveChanges();
        return message;
    }
}
