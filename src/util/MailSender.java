package util;

/*
 * ESTGF - Escola Superior de Tecnologia e Gestão de Felgueiras */
/* IPP - Instituto Politécnico do Porto */
/* LEI - Licenciatura em Engenharia Informática*/
/* Projeto Final 2013/2014 /*
 */

import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Esta classe permite enviar emails 
 *
 * @author Luís Sousa - 8090228
 */
public class MailSender {

    private String fromEmail;
    private String password;
    private String toEmail;
    private String subject;
    private String body;
    private String attachmentPath;

    /**
     * Método construtor que permite instanciar a classe MailSender
     */
    public MailSender() {
    }

    /**
     * Método construtor que permite instanciar a classe MailSender
     * @param fromEmail
     * @param password
     * @param toEmail
     * @param subject
     * @param body
     * @param filePath 
     */
    public MailSender(String fromEmail, String password, String toEmail, String subject, String body, String filePath) {
        this.fromEmail = fromEmail;
        this.password = password;
        this.toEmail = toEmail;
        this.subject = subject;
        this.body = body;
        this.attachmentPath = filePath;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    /**
     * Método que permite enviar um email
     * @return se tudo correu bem
     */
    public boolean send() {
        boolean result = false;
        try {
            
            System.out.println("SSLEmail Start");
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
            props.put("mail.smtp.socketFactory.port", "465"); //SSL Port
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory"); //SSL Factory Class
            props.put("mail.smtp.auth", "true"); //Enabling SMTP Authentication
            //props.put("mail.debug", "true");
            props.put("mail.smtp.port", "465"); //SMTP Port

            Authenticator auth = new Authenticator() {
                //override the getPasswordAuthentication method
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            };


            Session mailSession = Session.getInstance(props, auth);
            //System.out.println("Session created");

            //mailSession.setDebug(true); 
            result = sendAttachmentEmail(mailSession, toEmail, subject, body, attachmentPath);
        } catch (Exception ex) {
            result = false;
        }
        return result;
    }

    /**
     * Método que permite enviar um email com anexo
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     * @param filePath
     * @return se tudo correu bem
     */
    private boolean sendAttachmentEmail(Session session, String toEmail, String subject, String body, String filePath) {
        boolean result = false;
        try {
            MimeMessage msg = new MimeMessage(session);
            
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.setFrom(new InternetAddress(fromEmail));
            InternetAddress[] toAddresses = {new InternetAddress(toEmail)};
            msg.setRecipients(Message.RecipientType.TO, toAddresses);
            msg.setSubject(subject, "UTF-8");
            msg.setSentDate(new Date());

            msg.setContent(body, "text/html; charset=utf-8");

   
            BodyPart messageBodyPart1 = new MimeBodyPart();
            messageBodyPart1.setText(body);

    
            MimeBodyPart messageBodyPart2 = new MimeBodyPart();

            String filename = filePath;
            DataSource source = new FileDataSource(filename);
            messageBodyPart2.setDataHandler(new DataHandler(source));
            messageBodyPart2.setFileName(filename);

   
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart1);
            multipart.addBodyPart(messageBodyPart2);

            msg.setContent(multipart);

            Transport.send(msg); //envio
            result = true;
        } catch (MessagingException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

}
