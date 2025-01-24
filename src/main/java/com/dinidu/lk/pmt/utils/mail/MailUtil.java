package com.dinidu.lk.pmt.utils.mail;

import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.DateUtil;
import com.dinidu.lk.pmt.utils.EmailHelper;
import com.dinidu.lk.pmt.utils.MessageType;
import javafx.application.Platform;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MailUtil {
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String LOGO_PATH = "D:/Java/PMT/src/main/resources/asserts/icons/PN.png";
    private static final String TEMPLATE_BASE_PATH = "D:/Java/PMT/src/main/resources/templates/";

    private static String myAccountEmail;
    private static String appPassword;

    static {
        loadEmailProperties();
    }

    private static class EmailConfig {
        final String recipient;
        final String subject;
        final Map<String, String> templateVariables;
        final String templateName;

        EmailConfig(String recipient, String subject, String templateName, Map<String, String> templateVariables) {
            this.recipient = recipient;
            this.subject = subject;
            this.templateName = templateName;
            this.templateVariables = templateVariables;
        }
    }

    private static void loadEmailProperties() {
        Properties properties = new Properties();
        try (InputStream input = MailUtil.class.getClassLoader().getResourceAsStream("env.properties")) {
            if (input == null) {
                throw new IOException("Unable to find env.properties file.");
            }
            properties.load(input);
            myAccountEmail = properties.getProperty("EMAIL");
            appPassword = properties.getProperty("APP_PASSWORD");

            if (myAccountEmail == null || myAccountEmail.isEmpty() || appPassword == null || appPassword.isEmpty()) {
                throw new IOException("Missing or empty email credentials in env.properties file.");
            }
        } catch (IOException e) {
            Platform.runLater(() ->
                    CustomErrorAlert.showAlert("ERROR", "Error reading env.properties file: " + e.getMessage()));
        }
    }

    public static void sendEmail(String recipient, MessageType messageType, Map<String, String> variables) {
        validateEmailCredentials();
        validateRecipient(recipient);

        EmailConfig config = createEmailConfig(recipient, messageType, variables);
        sendEmailWithConfig(config);
    }

    private static void validateEmailCredentials() {
        if (myAccountEmail == null || myAccountEmail.isEmpty() || appPassword == null || appPassword.isEmpty()) {
            throw new IllegalStateException("Email credentials are not configured properly.");
        }
    }

    private static void validateRecipient(String recipient) {
        if (recipient == null || recipient.isEmpty()) {
            throw new IllegalArgumentException("Recipient email is null or empty.");
        }
    }

    private static EmailConfig createEmailConfig(String recipient, MessageType messageType, Map<String, String> variables) {
        String subject = getSubject(messageType, variables);
        String templateName = getTemplateName(messageType);
        return new EmailConfig(recipient, subject, templateName, variables);
    }

    private static String getSubject(MessageType messageType, Map<String, String> variables) {
        return switch (messageType) {
            case OTP -> "Your OTP for Project Nexus Access";
            case SIGN_UP -> "Welcome to Project Nexus, " + variables.getOrDefault("userName", "") + "!";
            case PASSWORD_CHANGE -> "IMPORTANT: Password Changed for Project Nexus";
            case TASK_ASSIGNMENT_NOTIFICATION -> "You have been assigned a new task in Project Nexus";
            case ISSUE_ASSIGNMENT_NOTIFICATION -> "You have been assigned a new issue in Project Nexus";
            case UPCOMING_TASK_DEADLINE_REMINDER -> "Upcoming Deadline Reminder for Task in Project Nexus";
            case OVERDUE_TASK_NOTIFICATION -> "Overdue Task Notification in Project Nexus";
            case TEAM_COLLABORATION_NEW_ASSIGNEE -> "New Assignee Added to Team in Project Nexus";
            case TEAM_COLLABORATION_TEAM_MEMBERS -> "Team Members Updated in Project Nexus";
            case CHECKLIST_MILESTONE_REACHED -> "Checklist Milestone Reached in Project Nexus";
            case ISSUE_ATTACHMENTS_NOTIFICATION -> "New Attachments Added to Issue in Project Nexus";
        };
    }

    private static String getTemplateName(MessageType messageType) {
        return switch (messageType) {
            case OTP -> "otp_email_template.html";
            case SIGN_UP -> "signup_email_template.html";
            case PASSWORD_CHANGE -> "change-password-notify.html";
            case TASK_ASSIGNMENT_NOTIFICATION -> "task_assignment_notification.html";
            case ISSUE_ASSIGNMENT_NOTIFICATION -> "issue_assignment_notification.html";
            case UPCOMING_TASK_DEADLINE_REMINDER -> "upcoming_task_deadline_reminder.html";
            case OVERDUE_TASK_NOTIFICATION -> "overdue_task_notification.html";
            case TEAM_COLLABORATION_NEW_ASSIGNEE -> "team_collaboration_new_assignee.html";
            case TEAM_COLLABORATION_TEAM_MEMBERS -> "team_collaboration_team_members.html";
            case CHECKLIST_MILESTONE_REACHED -> "checklist_milestone_notification.html";
            case ISSUE_ATTACHMENTS_NOTIFICATION -> "issue_attachments_notification.html";
        };
    }

    private static void sendEmailWithConfig(EmailConfig config) {
        try {
            Session session = createMailSession();
            Message message = createMessage(session, config);
            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (Exception ex) {
            Platform.runLater(() ->
                    CustomErrorAlert.showAlert("ERROR", "Failed to send email: " + ex.getMessage()));
            ex.printStackTrace();
        }
    }

    private static Session createMailSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(myAccountEmail, appPassword);
            }
        });
    }

    private static Message createMessage(Session session, EmailConfig config) throws MessagingException, IOException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(myAccountEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(config.recipient));
        message.setSubject(config.subject);

        String templatePath = TEMPLATE_BASE_PATH + config.templateName;
        String emailContent = loadAndReplaceTemplate(templatePath, config.templateVariables);
        MimeMultipart mimeMultipart = EmailHelper.createEmailWithInlineImage(emailContent, LOGO_PATH, "projectNexusLogo");
        message.setContent(mimeMultipart);

        return message;
    }

    private static String loadAndReplaceTemplate(String templatePath, Map<String, String> variables) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(templatePath)));
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String replacement = entry.getValue();

            if (replacement == null) {
                replacement = "";
                System.out.println("Warning: Replacement for " + entry.getKey() + " is null, using default value.");
            }

            content = content.replace(placeholder, replacement);
        }
        return content;
    }

    public static void sendOTPMail(String userEmail, int otp) {
        Map<String, String> variables = new HashMap<>();
        variables.put("otp", String.valueOf(otp));
        sendEmail(userEmail, MessageType.OTP, variables);
    }

    public static void signUpConfirmation(String userName, String userEmail) {
        Map<String, String> variables = new HashMap<>();
        variables.put("userName", userName);
        sendEmail(userEmail, MessageType.SIGN_UP, variables);
    }

    public static void notifyPasswordChange(String userEmail) {
        Map<String, String> variables = new HashMap<>();
        variables.put("currentDateTime",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        sendEmail(userEmail, MessageType.PASSWORD_CHANGE, variables);
    }

    public static void taskAssignment(String receiverEmail, String taskName, String receiverName,
                                      Date date, String taskCreatorName) {
        String formattedDate = DateUtil.formatDate(date);
        System.out.printf("====Task Assignment Email====\n" +
                        "receiverEmail: %s\n" +
                        "taskName: %s\n" +
                        "receiverName: %s\n" +
                        "date: %s\n" +
                        "taskCreatorName: %s\n" +
                        "=============================%n",
                receiverEmail, taskName, receiverName, formattedDate, taskCreatorName);

        Map<String, String> variables = new HashMap<>();
        variables.put("recipientName", receiverName);
        variables.put("taskName", taskName);
        variables.put("assignedBy", taskCreatorName);
        variables.put("dueDate", formattedDate);
        sendEmail(receiverEmail, MessageType.TASK_ASSIGNMENT_NOTIFICATION, variables);
    }

    public static void issueAssignmentNotification(String receiverEmail, String issueCreatorName, String recipientName, Date date, String issueDescription) {
        String formattedDate = com.dinidu.lk.pmt.utils.DateUtil.formatDate(date);
        System.out.printf("====Issue Assignment Email====\n" +
                        "receiverEmail: %s\n" +
                        "issueDescription: %s\n" +
                        "receiverName: %s\n" +
                        "date: %s\n" +
                        "issueCreatorName: %s\n" +
                        "=============================%n",
                receiverEmail, issueDescription, recipientName, formattedDate, issueCreatorName);

        Map<String, String> variables = new HashMap<>();
        variables.put("recipientName", recipientName);
        variables.put("issueTitle", issueDescription);
        variables.put("assignedBy", issueCreatorName);
        variables.put("dueDate", formattedDate);
        sendEmail(receiverEmail, MessageType.ISSUE_ASSIGNMENT_NOTIFICATION, variables);
    }
}