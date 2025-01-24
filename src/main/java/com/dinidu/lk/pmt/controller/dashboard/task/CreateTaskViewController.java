package com.dinidu.lk.pmt.controller.dashboard.task;

import com.dinidu.lk.pmt.controller.dashboard.ProjectViewController;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.dto.TaskDTO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.model.ProjectModel;
import com.dinidu.lk.pmt.model.TaskModel;
import com.dinidu.lk.pmt.model.UserModel;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.mail.MailUtil;
import com.dinidu.lk.pmt.utils.notification.NotificationManager;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class CreateTaskViewController {

    @FXML
    private AnchorPane taskCreatePage;
    public ComboBox<String> selectMemberNameComboBox;
    public ComboBox<String> selectProjectNameComboBox;
    @FXML
    private DatePicker taskDeadline;

    @FXML
    private TextField taskNameField;

    @FXML
    private TextArea descriptionIdField;

    public void initialize() {
        List<UserDTO> allActiveMembers = UserModel.getAllActiveMembersNames();
        List<ProjectDTO> allProjects = ProjectModel.getAllProjects();

        ObservableList<String> memberNames = FXCollections.observableArrayList();
        ObservableList<String> projectNames = FXCollections.observableArrayList();

        for (UserDTO member : allActiveMembers) {
            memberNames.add(member.getFull_name());
        }

        assert allProjects != null;
        for (ProjectDTO project : allProjects) {
            projectNames.add(project.getName());
        }

        selectMemberNameComboBox.setItems(memberNames);
        selectProjectNameComboBox.setItems(projectNames);

        taskDeadline.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    public void createTaskClick() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Logged in username Inside Create Project: " + username);
        UserRole userRole = UserModel.getUserRoleByUsername(username);

        if ((userRole != UserRole.ADMIN &&
                userRole != UserRole.PROJECT_MANAGER &&
                userRole != UserRole.PRODUCT_OWNER)) {
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to create projects.");
            return;
        }

        String loggedInUsername = SessionUser.getLoggedInUsername();
        Long userIdByUsername = UserModel.getUserIdByUsername(loggedInUsername);

        if (userIdByUsername == null) {
            System.out.println("User ID is null for username: " + loggedInUsername);
            CustomErrorAlert.showAlert("User not found", "User not found. Please login again.");
            return;
        }

        if (validateInputFields()) {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.nameProperty().set(taskNameField.getText());
            taskDTO.descriptionProperty().set(descriptionIdField.getText());

            String selectedProjectName = selectProjectNameComboBox.getValue();
            String projectId = ProjectModel.getProjectIdByName(selectedProjectName);
            taskDTO.projectIdProperty().set(projectId);

            String selectedMemberName = selectMemberNameComboBox.getValue();
            Long assignedTo = UserModel.getUserIdByFullName(selectedMemberName);
            taskDTO.assignedToProperty().set(assignedTo);

            taskDTO.priorityProperty().set(TaskPriority.MEDIUM);
            taskDTO.statusProperty().set(TaskStatus.NOT_STARTED);

            taskDTO.createdAtProperty().set(Date.from(Instant.now()));

            if (taskDeadline.getValue() != null) {
                taskDTO.dueDateProperty().set(Date.from(taskDeadline.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }

            TaskModel taskModel = new TaskModel();
            boolean isSaved;
            try {
                isSaved = taskModel.insertTask(taskDTO);
                if (isSaved) {
                    Date deadline = taskDTO.dueDateProperty().get();
                    NotificationManager notificationManager = NotificationManager.getInstance();

                    String Email = UserModel.getUserEmailById(taskDTO.assignedToProperty().get());
                    String Name = UserModel.getUserFullNameById(taskDTO.assignedToProperty().get());

                    notificationManager.scheduleDeadlineReminder(
                            String.valueOf(taskDTO.idProperty().get()),
                            taskDTO.nameProperty().get(),
                            Email,
                            Name,
                            deadline
                    );

                    new Thread(() -> {
                        String receiverName = UserModel.getUserFullNameById(taskDTO.assignedToProperty().get());
                        String uName = SessionUser.getLoggedInUsername();
                        Long idByUsername = UserModel.getUserIdByUsername(uName);
                        String taskCreatorName = UserModel.getUserFullNameById(idByUsername);
                        System.out.println("Task Created By : " + taskCreatorName);
                        System.out.println("Task Assigned To : " + receiverName);
                        String receiverEmail = null;

                        try {
                            receiverEmail = UserModel.getUserEmailById(taskDTO.assignedToProperty().get());
                        } catch (SQLException e) {
                            System.out.println("Error getting receiver email: " + e.getMessage());
                        }

                        System.out.println("Receiver Email : " + receiverEmail);
                        if (receiverEmail == null) {
                            System.out.println("Email is " + null);
                            return;
                        }
                        try {
                            MailUtil.taskAssignment(receiverEmail, taskDTO.nameProperty().get(), receiverName, taskDTO.dueDateProperty().get(), taskCreatorName);
                            Thread.sleep(1000);
                            Platform.runLater(() -> {
                                System.out.println("Task created successfully!");
                                CustomAlert.showAlert("Task created", "Task created successfully!");
                                ProjectViewController.bindNavigation(taskCreatePage, "/view/nav-buttons/task-view.fxml");
                                clearContent();
                            });
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }).start();
                } else {
                    System.out.println("Error saving task.");
                    CustomErrorAlert.showAlert("Error saving task", "Failed to save the task.");
                }
            } catch (Exception e) {
                System.out.println("Error saving task: " + e.getMessage());
                CustomErrorAlert.showAlert("Error saving task", "Error saving task: " + e.getMessage());
            }
        } else {
            System.out.println("Please fill all the required fields.");
            CustomErrorAlert.showAlert("Invalid Input", "Please fill all the required fields.");
        }
    }

    public void cancelOnClick() {
        if (areFieldsCleared()) {
            ProjectViewController.bindNavigation(taskCreatePage, "/view/nav-buttons/task-view.fxml");
        } else {
            clearContent();
        }
    }

    private boolean areFieldsCleared() {
        return taskNameField.getText().isEmpty() &&
                descriptionIdField.getText().isEmpty() &&
                selectMemberNameComboBox.getSelectionModel().isEmpty() &&
                selectProjectNameComboBox.getSelectionModel().isEmpty() &&
                taskDeadline.getValue() == null;
    }

    private void clearContent() {
        taskNameField.clear();
        descriptionIdField.clear();
        selectMemberNameComboBox.getSelectionModel().clearSelection();
        selectProjectNameComboBox.getSelectionModel().clearSelection();
        taskDeadline.setValue(null);
    }

    private boolean validateInputFields() {
        return !taskNameField.getText().isEmpty() &&
                !descriptionIdField.getText().isEmpty() &&
                !selectMemberNameComboBox.getSelectionModel().isEmpty() &&
                !selectProjectNameComboBox.getSelectionModel().isEmpty() &&
                taskDeadline.getValue() != null;
    }
}
