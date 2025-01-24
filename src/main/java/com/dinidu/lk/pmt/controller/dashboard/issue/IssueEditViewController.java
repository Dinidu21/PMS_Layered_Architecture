package com.dinidu.lk.pmt.controller.dashboard.issue;

import com.dinidu.lk.pmt.dto.IssueDTO;
import com.dinidu.lk.pmt.model.IssueModel;
import com.dinidu.lk.pmt.model.UserModel;
import com.dinidu.lk.pmt.utils.*;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomDeleteAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;
import com.dinidu.lk.pmt.utils.listeners.IssueDeletionHandler;
import com.dinidu.lk.pmt.utils.listeners.IssueUpdateListener;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class IssueEditViewController implements Initializable {
    public Button saveIssueBtn;
    public Button cancelProjectBtn;
    public Button deleteIssueBtn;
    public ComboBox<String> selectTaskNameComboBox;
    public ComboBox<String> selectMemberNameComboBox;
    public ComboBox<String> selectProjectNameComboBox;
    public DatePicker dueDate;
    @Setter
    private IssueDeletionHandler deletionHandler;
    @Setter
    private IssueUpdateListener updateListener;
    @FXML
    private Label backToMain;
    @FXML
    private AnchorPane projectEdit;
    @FXML
    private ComboBox<IssueStatus> issueStatusComboBox;
    @FXML
    private ComboBox<IssuePriority> issuePriorityComboBox;
    @FXML
    private TextField issueDescriptionField;
    private static IssueDTO currentIssue;

    public static void setIssue(IssueDTO issueDTO) {
        currentIssue = issueDTO;
    }

    @FXML
    public void saveIssue() {
        System.out.println("Saving Issue...");

        if (issueDescriptionField.getText().isEmpty()) {
            CustomErrorAlert.showAlert("Error", "Issue description cannot be empty.");
            return;
        }

        currentIssue.setDescription(issueDescriptionField.getText());
        currentIssue.setStatus(issueStatusComboBox.getValue());
        currentIssue.setPriority(issuePriorityComboBox.getValue());

        try {
            currentIssue.setProjectId(IssueModel.getProjectIdByName(selectProjectNameComboBox.getValue()));
        } catch (SQLException e) {
            System.out.println("Error retrieving project ID: " + e.getMessage());
        }

        try {
            String selectedTaskName = selectTaskNameComboBox.getValue();
            if (selectedTaskName != null) {
                Long taskId = IssueModel.getTaskIdByName(selectedTaskName);
                if (taskId != null) {
                    currentIssue.setTaskId(taskId);
                } else {
                    CustomErrorAlert.showAlert("Error", "Selected task does not exist.");
                    return;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving task ID: " + e.getMessage());
        }

        currentIssue.setAssignedTo(UserModel.getUserIdByFullName(selectMemberNameComboBox.getValue()));

        if (dueDate.getValue() != null) {
            currentIssue.setDueDate(Date.valueOf(dueDate.getValue()));
        } else {
            currentIssue.setDueDate(null);
        }

        currentIssue.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        currentIssue.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        try {
            boolean issueUpdated = IssueModel.updateIssue(currentIssue);

            if (issueUpdated) {
                System.out.println("Issue saved successfully.");

                if (updateListener != null) {
                    System.out.println("Updating listener...");
                    updateListener.onIssueUpdated(currentIssue);
                }

                System.out.println("Saving issue to database...");
                CustomAlert.showAlert("Success", "Issue saved successfully.");
                System.out.println("Returning to main...");
                backToMain();
            } else {
                System.out.println("Failed to save issue.");
                CustomErrorAlert.showAlert("Error", "Failed to save issue.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeProjectComboBox();
        initializeTaskComboBox();
        initializeMemberComboBox();
        issueStatusComboBox.getItems().setAll(IssueStatus.values());
        issuePriorityComboBox.getItems().setAll(IssuePriority.values());

        if (currentIssue == null) {
            List<IssueDTO> issues = null;

            try {
                issues = IssueModel.getAllIssues();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (issues != null && !issues.isEmpty()) {
                currentIssue = issues.get(0);
            } else {
                System.out.println("No issues available.");
                return;
            }
        }
        loadIssueData();

        dueDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && (date.isBefore(LocalDate.now()) || date.isEqual(LocalDate.now()))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        issueDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateFields());
    }

    private void initializeProjectComboBox() {
        ObservableList<String> projectNames = FXCollections.observableArrayList();
        try {
            ResultSet rs = IssueModel.getActiveProjectNames();
            while (rs.next()) {
                projectNames.add(rs.getString("name"));
            }
            selectProjectNameComboBox.setItems(projectNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initializeTaskComboBox() {
        selectProjectNameComboBox.setOnAction(event -> {
            String selectedProject = selectProjectNameComboBox.getValue();
            if (selectedProject != null) {
                ObservableList<String> taskNames = FXCollections.observableArrayList();
                try {
                    ResultSet rs = IssueModel.getTasksByProject(selectedProject);
                    while (rs.next()) {
                        taskNames.add(rs.getString("name"));
                    }
                    selectTaskNameComboBox.setItems(taskNames);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initializeMemberComboBox() {
        ObservableList<String> memberNames = FXCollections.observableArrayList();
        try {
            ResultSet rs = IssueModel.getActiveMembers();
            while (rs.next()) {
                memberNames.add(rs.getString("full_name"));
            }
            selectMemberNameComboBox.setItems(memberNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadIssueData() {
        System.out.println("Loading Issue data...");

        if (currentIssue == null) {
            System.out.println("currentIssue is null");
            return;
        }

        issueDescriptionField.setText(currentIssue.getDescription() != null ? currentIssue.getDescription() : "");
        issueStatusComboBox.setValue(currentIssue.getStatus());
        issuePriorityComboBox.setValue(currentIssue.getPriority());

        try {
            String projectId = currentIssue.getProjectId();
            if (projectId != null) {
                String projectName = IssueModel.getProjectNameById(projectId);
                selectProjectNameComboBox.setValue(projectName);
            }

            long taskId = currentIssue.getTaskId();
            if (taskId != 0) {
                String taskName = IssueModel.getTaskNameById(taskId);
                selectTaskNameComboBox.setValue(taskName);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving project or task data: " + e.getMessage());
        }

        long assignedTo = currentIssue.getAssignedTo();
        if (assignedTo != 0) {
            String assigneeName = UserModel.getUserFullNameById(assignedTo);
            selectMemberNameComboBox.setValue(assigneeName);
        }

        if (currentIssue.getDueDate() != null) {
            dueDate.setValue(currentIssue.getDueDate().toLocalDate());
        } else {
            dueDate.setValue(null);
        }
    }


    private void validateFields() {
        boolean isValid = !issueDescriptionField.getText().isEmpty();
        saveIssueBtn.setDisable(!isValid);
    }

    @FXML
    public void backToMain() {
        System.out.println("Returning to main...");
        ((Stage) backToMain.getScene().getWindow()).close();
    }

    @FXML
    public void cancelIssueBtnOnClick(ActionEvent actionEvent) {
        System.out.println("Cancelling project edit...");
        ((Stage) projectEdit.getScene().getWindow()).close();
    }

    @FXML
    public void deleteIssue() {
        String username = SessionUser.getLoggedInUsername();
        System.out.println("Deleting Issue for user: " + username);

        if (username == null) {
            System.out.println("User not logged in. username: " + username);
        }

        UserRole userRoleByUsername = UserModel.getUserRoleByUsername(username);
        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + userRoleByUsername);
            return;
        }

        System.out.println("User role: " + userRoleByUsername);

        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.PROJECT_MANAGER &&
                userRoleByUsername != UserRole.PRODUCT_OWNER) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can delete Issue.");
            CustomErrorAlert.showAlert("Access Denied", "You do not have permission to delete Issue.");
            deleteIssueBtn.setVisible(false);
            return;
        }

        boolean confirmed = CustomDeleteAlert.showAlert(
                (Stage) projectEdit.getScene().getWindow(),
                "Confirm Deletion",
                "Are you sure you want to delete this issue? "
        );

        if (confirmed) {
            System.out.println("Deleting issue...");
            boolean isDeleted = IssueModel.deleteIssue(currentIssue.getId());
            if (isDeleted) {
                System.out.println("Issue deleted successfully.");
                CustomAlert.showAlert("Success", "Issue deleted successfully.");

                Stage currentStage = (Stage) projectEdit.getScene().getWindow();
                currentStage.close();

                if (deletionHandler != null) {
                    deletionHandler.onIssueDeleted();
                }
            } else {
                System.out.println("Issue deletion failed.");
                CustomErrorAlert.showAlert("Error", "Failed to delete issue.");
            }
        } else {
            System.out.println("Issue deletion canceled by user.");
        }
    }

    @FXML
    public void editStatus(ActionEvent actionEvent) {
        System.out.println("Editing project status...");
    }

    @FXML
    public void editPriority(ActionEvent actionEvent) {
        System.out.println("Editing project priority...");
    }

    @FXML
    public void editIssueDesc(ActionEvent actionEvent) {
        System.out.println("Editing project description...");
    }

    public void editTaskName(ActionEvent actionEvent) {
        System.out.println("Editing task name...");
    }

    public void editMember(ActionEvent actionEvent) {
        System.out.println("Editing member...");
    }

    public void editProjectName(ActionEvent actionEvent) {
        System.out.println("Editing project name...");
    }

    public void setDueDate(ActionEvent actionEvent) {
        System.out.println("Setting due date...");
    }
}
