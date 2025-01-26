package com.dinidu.lk.pmt.utils.notification;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.model.TeamAssignmentModel;
import com.dinidu.lk.pmt.utils.DateUtil;
import com.dinidu.lk.pmt.utils.MessageType;
import com.dinidu.lk.pmt.utils.mail.MailUtil;
import lombok.Getter;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.time.LocalDateTime;

public class NotificationManager {
    @Getter
    private static final NotificationManager instance = new NotificationManager();
    private final ScheduledExecutorService scheduler;
    private final Map<String, ScheduledFuture<?>> taskDeadlineReminders;
    private final Map<String, TaskInfo> taskRegistry;
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    static UserBO userBO = (UserBO) BOFactory.getInstance().
                getBO(BOFactory.
                        BOTypes.USER);

    private NotificationManager() {
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.taskDeadlineReminders = new ConcurrentHashMap<>();
        this.taskRegistry = new ConcurrentHashMap<>();
    }

    private static class TaskInfo {
        String taskId;
        String taskName;
        String assigneeEmail;
        String assigneeName;
        Date deadline;
        boolean isCompleted;

        TaskInfo(String taskId, String taskName, String assigneeEmail,
                 String assigneeName, Date deadline) {
            this.taskId = taskId;
            this.taskName = taskName;
            this.assigneeEmail = assigneeEmail;
            this.assigneeName = assigneeName;
            this.deadline = deadline;
            this.isCompleted = false;
        }
    }

    public void scheduleDeadlineReminder(String taskId, String taskName,
                                         String assigneeEmail, String assigneeName, Date deadline) {
        TaskInfo taskInfo = new TaskInfo(taskId, taskName, assigneeEmail, assigneeName, deadline);
        taskRegistry.put(taskId, taskInfo);

        long delay = deadline.getTime() - System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3);
        if (delay > 0) {
            ScheduledFuture<?> reminderTask = scheduler.schedule(() -> {
                if (!taskInfo.isCompleted) {
                    sendUpcomingDeadlineReminder(taskInfo);
                }
            }, delay, TimeUnit.MILLISECONDS);
            taskDeadlineReminders.put(taskId + "_3d", reminderTask);
        }

        ScheduledFuture<?> overdueTask = scheduler.scheduleWithFixedDelay(
                () -> checkOverdueTask(taskInfo),
                deadline.getTime() - System.currentTimeMillis(),
                TimeUnit.HOURS.toMillis(24),
                TimeUnit.MILLISECONDS
        );
        taskDeadlineReminders.put(taskId + "_overdue", overdueTask);
    }

    private void sendUpcomingDeadlineReminder(TaskInfo taskInfo) {
        System.out.println("Upcoming deadline reminder for task: " + taskInfo.taskId);
        Map<String, String> variables = new HashMap<>();
        variables.put("recipientName", taskInfo.assigneeName);
        variables.put("taskName", taskInfo.taskName);
        variables.put("dueDate", DateUtil.formatDate(taskInfo.deadline));
        variables.put("hoursRemaining", "72");

        MailUtil.sendEmail(taskInfo.assigneeEmail,
                MessageType.UPCOMING_TASK_DEADLINE_REMINDER, variables);
        System.out.println("Upcoming deadline reminder sent for task: " + taskInfo.taskId);
    }

    private void checkOverdueTask(TaskInfo taskInfo) {
        System.out.println("Checking overdue task: " + taskInfo.taskId);
        if (!taskInfo.isCompleted && taskInfo.deadline.before(new Date())) {
            Map<String, String> variables = new HashMap<>();
            variables.put("recipientName", taskInfo.assigneeName);
            variables.put("taskName", taskInfo.taskName);
            variables.put("dueDate", DateUtil.formatDate(taskInfo.deadline));
            variables.put("daysOverdue",
                    String.valueOf(TimeUnit.MILLISECONDS.toDays(
                            System.currentTimeMillis() - taskInfo.deadline.getTime())));

            MailUtil.sendEmail(taskInfo.assigneeEmail,
                    MessageType.OVERDUE_TASK_NOTIFICATION, variables);
            System.out.println("Overdue task notification sent for task: " + taskInfo.taskId);
        }
    }

    public static void sendTeamCollaborationNotification(
            String projectName,
            String taskName,
            String newAssigneeEmail,
            List<String> existingTeamEmails,
            String assignedBy,
            Date dueDate,
            String priority,
            String taskUrl) {

        executor.submit(() -> {
            try {
                validateEmails(newAssigneeEmail, existingTeamEmails);
                List<String> teamMemberNames = TeamAssignmentModel.getAllTeamMembersNamesByTask(taskName);

                Map<String, String> baseVariables = new HashMap<>();
                baseVariables.put("projectName", projectName);
                baseVariables.put("taskName", taskName);
                baseVariables.put("assignedBy", assignedBy);
                baseVariables.put("dueDate", String.valueOf(dueDate));
                baseVariables.put("priority", priority != null ? priority : "Normal");
                baseVariables.put("timestamp", DateUtils.getCurrentTimestamp());
                baseVariables.put("taskUrl", taskUrl != null ? taskUrl : "#");

                if (!newAssigneeEmail.isEmpty()) {
                    Map<String, String> newAssigneeVariables = new HashMap<>(baseVariables);
                    newAssigneeVariables.put("recipientName", getNameFromEmail(newAssigneeEmail));
                    newAssigneeVariables.put("teamMembers", String.join(", ", teamMemberNames));

                    MailUtil.sendEmail(newAssigneeEmail, MessageType.TEAM_COLLABORATION_NEW_ASSIGNEE, newAssigneeVariables);
                }

                if (existingTeamEmails != null && !existingTeamEmails.isEmpty()) {
                    for (String teamMemberEmail : existingTeamEmails) {
                        if (!teamMemberEmail.equals(newAssigneeEmail)) {
                            Map<String, String> teamMemberVariables = new HashMap<>(baseVariables);
                            teamMemberVariables.put("recipientName", getNameFromEmail(teamMemberEmail));
                            teamMemberVariables.put("newMemberName", getNameFromEmail(newAssigneeEmail));

                            MailUtil.sendEmail(teamMemberEmail, MessageType.TEAM_COLLABORATION_TEAM_MEMBERS, teamMemberVariables);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error sending email: " + e.getMessage());
            }
        });
    }

    public static String getNameFromEmail(String email) {
        try {
            return userBO.getUserNameByEmail(email);
        } catch (SQLException e) {
            System.out.println("Error fetching name from email: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "Unknown";
    }

    private static void validateEmails(String primaryEmail, List<String> additionalEmails) {
        if (primaryEmail == null || primaryEmail.isEmpty()) {
            throw new IllegalArgumentException("Primary email cannot be null or empty");
        }
        if (additionalEmails == null || additionalEmails.isEmpty()) {
            throw new IllegalArgumentException("Additional emails list cannot be null or empty");
        }
    }

    private static class DateUtils {
        private static final DateTimeFormatter DATE_TIME_FORMATTER =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public static String getCurrentTimestamp() {
            return LocalDateTime.now().format(DATE_TIME_FORMATTER);
        }
    }


    public void sendChecklistMilestoneNotification(String projectName, String milestoneName,
                                                   Set<String> teamEmails, int completionPercentage) {
        for (String email : teamEmails) {
            Map<String, String> variables = new HashMap<>();
            variables.put("projectName", projectName);
            variables.put("milestoneName", milestoneName);
            variables.put("completionPercentage", String.valueOf(completionPercentage));
            variables.put("timestamp", LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            MailUtil.sendEmail(email, MessageType.CHECKLIST_MILESTONE_REACHED, variables);
        }
    }

    public void sendIssueAttachmentsNotification(String issueId, String issueTitle,
                                                 String uploaderName, List<String> attachmentNames, Set<String> watchers) {
        for (String watcherEmail : watchers) {
            Map<String, String> variables = new HashMap<>();
            variables.put("issueId", issueId);
            variables.put("issueTitle", issueTitle);
            variables.put("uploaderName", uploaderName);
            variables.put("attachmentCount", String.valueOf(attachmentNames.size()));
            variables.put("attachmentList", String.join(", ", attachmentNames));
            variables.put("timestamp", LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            MailUtil.sendEmail(watcherEmail, MessageType.ISSUE_ATTACHMENTS_NOTIFICATION, variables);
        }
    }

    public void markTaskComplete(String taskId) {
        TaskInfo taskInfo = taskRegistry.get(taskId);
        if (taskInfo != null) {
            taskInfo.isCompleted = true;

            ScheduledFuture<?> reminder24h = taskDeadlineReminders.remove(taskId + "_24h");
            if (reminder24h != null) {
                reminder24h.cancel(false);
            }

            ScheduledFuture<?> overdueReminder = taskDeadlineReminders.remove(taskId + "_overdue");
            if (overdueReminder != null) {
                overdueReminder.cancel(false);
            }
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}