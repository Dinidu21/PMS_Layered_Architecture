package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProjectsBO;
import com.dinidu.lk.pmt.bo.custom.TasksBO;
import com.dinidu.lk.pmt.controller.BaseController;
import com.dinidu.lk.pmt.controller.dashboard.task.CreateTaskSuccessViewController;
import com.dinidu.lk.pmt.controller.dashboard.task.TaskEditViewController;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.dto.TasksDTO;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;



public class TaskViewController extends BaseController implements Initializable {
    public TextField searchBox;
    public ListView<String> suggestionList;
    public Button createTaskBtn;
    public ImageView searchImg;
    public Label noTaskLabel;
    public Label createLabel;
    public ScrollPane scrollPane;
    public VBox taskCardContainer;
    public ComboBox<TaskStatus> sortByStatus;
    public ComboBox<TaskPriority> priorityDropdown;
    public AnchorPane tasksPage;
    public Label noTasksFoundLabel;
    public Button resetBTN;

    QueryDAO queryDAO = new QueryDAOImpl();

    ProjectsBO projectsBO= (ProjectsBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROJECTS);

    TasksBO tasksBO = (TasksBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.TASKS);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        resetBTN.setVisible(false);
        noTasksFoundLabel.setVisible(false);
        sortByStatus.getItems().addAll(TaskStatus.values());
        priorityDropdown.getItems().addAll(TaskPriority.values());

        sortByStatus.setOnAction(event -> {
            updateTaskView();
            resetBTN.setVisible(true);
        });

        priorityDropdown.setOnAction(event -> {
            updateTaskView();
            resetBTN.setVisible(true);
        });

        updateTaskView();

        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            searchImg.setVisible(newValue.isEmpty());
            if (!newValue.isEmpty()) {
                showSearchSuggestions(newValue);
            } else {
                suggestionList.setVisible(false);
                updateTaskView();
            }
        });

        searchBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String taskName = searchBox.getText().trim();
                if (!taskName.isEmpty()) {
                    try {
                        tasksBO.searchTasksByName(taskName);
                    } catch (SQLException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    event.consume();
                }
            }
        });

        suggestionList.setOnMouseClicked(event -> {
            String selectedTaskName = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedTaskName != null) {
                searchBox.setText(selectedTaskName);
                suggestionList.setVisible(false);
                List<TasksDTO> filteredTasks;
                try {
                    filteredTasks = tasksBO.searchTasksByName(selectedTaskName);
                } catch (SQLException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (filteredTasks.isEmpty()) {
                    noTasksFoundLabel.setVisible(true);
                } else {
                    noTasksFoundLabel.setVisible(false);
                }
                displayTasks(filteredTasks);
            }
        });

        String username = SessionUser.getLoggedInUsername();

        if (username == null) {
            System.out.println("User not logged in. username: " + null);
        }
        UserRole userRoleByUsername;
        try {
            userRoleByUsername = queryDAO.getUserRoleByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + null);
        }
        System.out.println("User role: " + userRoleByUsername);
        if (userRoleByUsername != UserRole.ADMIN && userRoleByUsername != UserRole.PROJECT_MANAGER && userRoleByUsername != UserRole.PRODUCT_OWNER) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can create Tasks.");
            createTaskBtn.setDisable(true);
            createLabel.setVisible(false);
        }
    }

    private void updateTaskView() {
        List<TasksDTO> tasks;
        try {
            tasks = tasksBO.getAllTasks();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("dto size:" + tasks.size());

        for (TasksDTO task : tasks) {
            System.out.println(task);
        }

        if (tasks.isEmpty()) {
            createLabel.setVisible(true);
            scrollPane.setVisible(false);

            System.out.println("No tasks found.");
            taskCardContainer.setVisible(false);
            return;
        }

        TaskStatus selectedStatus = sortByStatus.getValue();
        TaskPriority selectedPriority = priorityDropdown.getValue();

        noTaskLabel.setVisible(false);
        createLabel.setVisible(false);
        noTasksFoundLabel.setVisible(false);
        taskCardContainer.setVisible(true);


        List<TasksDTO> filteredTasks = tasks.stream().filter(task -> (selectedStatus == null || task.getStatus() == selectedStatus) && (selectedPriority == null || task.getPriority() == selectedPriority)).collect(Collectors.toList());

        if (filteredTasks.isEmpty()) {
            noTasksFoundLabel.setVisible(true);
            System.out.println("No tasks found after filtering.");
        } else {
            noTasksFoundLabel.setVisible(false);
        }

        displayTasks(filteredTasks);
    }

    private void showSearchSuggestions(String query) {
        List<TasksDTO> filteredTasks;
        try {
            filteredTasks = tasksBO.searchTasksByName(query);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (!filteredTasks.isEmpty()) {
            suggestionList.getItems().clear();
            for (TasksDTO task : filteredTasks) {
                suggestionList.getItems().add(task.getName().get());
            }
            suggestionList.setVisible(true);
        } else {
            suggestionList.setVisible(false);
        }
    }

    private void displayTasks(List<TasksDTO> tasks) {
        taskCardContainer.getChildren().clear();
        taskCardContainer.getStyleClass().add("task-card-container");

        for (TasksDTO task : tasks) {
            AnchorPane taskCard = new AnchorPane();
            taskCard.getStyleClass().add("task-card");
            taskCard.setPrefHeight(120.0);
            taskCard.setPrefWidth(1322.0);

            StackPane taskIcon = new StackPane();
            taskIcon.setPrefWidth(60.0);
            taskIcon.setPrefHeight(60.0);
            taskIcon.setLayoutX(20);
            taskIcon.setLayoutY(30);
            taskIcon.getStyleClass().add("task-icon");

            ProjectViewController.backgroundColor = ProjectViewController.generateRandomColor();
            taskIcon.setStyle(String.format("-fx-background-color: %s;", ProjectViewController.backgroundColor));
            String taskName = task.getName().get();
            String[] nameParts = taskName.split(" ");
            StringBuilder initials = new StringBuilder();

            for (String part : nameParts) {
                if (!part.isEmpty()) {
                    initials.append(part.charAt(0));
                }
            }

            String initialsString = initials.toString().toUpperCase();
            if (initialsString.length() > 3) {
                initialsString = initialsString.substring(0, 3);
            }

            System.out.println("Initials: " + initialsString);
            Label initialLabel = new Label(initialsString);
            initialLabel.getStyleClass().add("icon-text");
            taskIcon.getChildren().add(initialLabel);

            VBox taskDetails = new VBox(5);
            taskDetails.setLayoutX(100);
            taskDetails.setLayoutY(35);

            Label nameLabel = new Label(task.getName().get());
            nameLabel.getStyleClass().add("task-name");

            List<ProjectDTO> projectById;
            try {
                projectById = projectsBO.getProjectById(task.getProjectId().get());
                if(projectById.isEmpty()){
                    System.out.println("Project not found");
                    CustomErrorAlert.showAlert("Not Found", "Project not found");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            Label idLabel = new Label(projectById.get(0).getName());
            idLabel.getStyleClass().add("project-name");

            taskDetails.getChildren().addAll(nameLabel, idLabel);

            HBox actionButtons = new HBox(10);
            actionButtons.setLayoutX(800);
            actionButtons.setLayoutY(40);
            actionButtons.getStyleClass().add("action-buttons");

            ProjectViewController.addHoverEffect(taskCard);

            taskCard.setOnMouseClicked(e -> {
                ProjectViewController.playClickAnimation(taskCard);
                openTask(task);
                System.out.println("clicked task : " + task);
            });

            taskCard.getChildren().addAll(taskIcon, taskDetails, actionButtons);

            ProjectViewController.playEntranceAnimation(taskCard);

            taskCardContainer.getChildren().add(taskCard);
            taskCardContainer.setPrefWidth(1522.0);
        }
    }

    private void openTask(TasksDTO task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/task/task-create-success-view.fxml"));
            Parent root = loader.load();

            CreateTaskSuccessViewController controller = loader.getController();
            controller.updateTaskView(task);

            TaskEditViewController.setTask(task);

            FXMLLoader editLoader = new FXMLLoader(getClass().getResource("/view/nav-buttons/task/task-edit-view.fxml"));
            Parent editRoot = editLoader.load();
            TaskEditViewController editController = editLoader.getController();

            editController.setUpdateListener(controller);

            tasksPage.getChildren().setAll(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startCreateLabelOnClick(MouseEvent mouseEvent) {
        ProjectViewController.bindNavigation(tasksPage, "/view/nav-buttons/task/task-create-view.fxml");
    }

    public void createTaskOnClick(ActionEvent actionEvent) {
        ProjectViewController.bindNavigation(tasksPage, "/view/nav-buttons/task/task-create-view.fxml");
    }

    public void searchTaskOnClick(ActionEvent actionEvent) {
        searchImg.setDisable(true);
    }

    public void resetBtnClick(ActionEvent actionEvent) {
        sortByStatus.getSelectionModel().clearSelection();
        priorityDropdown.getSelectionModel().clearSelection();
        ProjectViewController.bindNavigation(tasksPage, "/view/nav-buttons/task-view.fxml");
        searchBox.clear();
        updateTaskView();
    }
}
