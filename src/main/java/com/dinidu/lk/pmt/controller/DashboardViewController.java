package com.dinidu.lk.pmt.controller;

import com.dinidu.lk.pmt.controller.dashboard.NotifyViewController;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.dto.TaskDTO;
import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.model.ProjectModel;
import com.dinidu.lk.pmt.model.ReportModel;
import com.dinidu.lk.pmt.model.TaskModel;
import com.dinidu.lk.pmt.model.UserModel;
import com.dinidu.lk.pmt.utils.ProfileImageManager;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DashboardViewController extends BaseController {

    public ImageView settingId;
    public ImageView mainIcon;
    public AnchorPane projectsDisplayCard;
    public AnchorPane pieChartDisplayCard;
    public AnchorPane taskDisplayCard;
    public PieChart assigneesPieChart;
    public VBox legendContainer;
    public ImageView refreshButton;
    public ImageView refreshButtonForTasks;
    public VBox tasksContainer;
    public ScrollPane scrollPane;
    public Label totalProjectsLabel;
    public VBox ongoingProjectsContainer;
    public ImageView refreshButtonForProjects;
    @FXML
    private ImageView profileImageIcon;
    public AnchorPane dashboardPage;
    public AnchorPane contentPane;
    private final List<Button> navButtons = new ArrayList<>();
    private Button activeButton;
    private AnchorPane currentNotification;
    private boolean isNotificationVisible = false;
    TaskModel taskModel;
    ProjectModel projectModel;

    public void mainIconOnClick() {
        contentPane.getChildren().clear();
        contentPane.getChildren().addAll(projectsDisplayCard,taskDisplayCard,pieChartDisplayCard);
        showCardContainers();
        setupRefreshButtonForTasks();
        loadUnresolvedTasks();
        loadOngoingProjects();
    }

    private void showCardContainers() {
        Timeline timeline = new Timeline(
                new javafx.animation.KeyFrame(Duration.ZERO, e -> fadeIn(projectsDisplayCard)),
                new javafx.animation.KeyFrame(Duration.millis(700), e -> fadeIn(taskDisplayCard)),
                new javafx.animation.KeyFrame(Duration.millis(1400), e -> fadeIn(pieChartDisplayCard))
        );
        timeline.play();
    }

    public static void fadeIn(Node node) {
        node.setVisible(true);
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(700), node);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.play();
    }

    private void hideCardContainers() {
        projectsDisplayCard.setVisible(false);
        pieChartDisplayCard.setVisible(false);
        taskDisplayCard.setVisible(false);
    }

    public void initialize() {
        projectModel = new ProjectModel();
        setupScrollPane();
        loadOngoingProjects();
        taskModel = new TaskModel();
        setupTasksView();
        setupRefreshButtonForTasks();
        loadUnresolvedTasks();
        mainIconOnClick();
        navButtonStyle();
        setupPieChart();
        setupRefreshButtonForPieChart();
        setupRefreshButtonForProjects();
        
        ProfileImageManager.addListener(newImage -> {
            profileImageIcon.setImage(newImage);
            Circle clip = new Circle(36.5, 36.5, 36.5);
            profileImageIcon.setClip(clip);
        });

        userAccessController();
    }

    private void loadOngoingProjects() {
        List<ProjectDTO> ongoingProjects = null;
        try {
            ongoingProjects = projectModel.getProjectsByStatus(ProjectStatus.IN_PROGRESS);
        } catch (SQLException e) {
            System.out.println("Error loading ongoing projects: " + e.getMessage());
        }

        ongoingProjectsContainer.getChildren().clear();

        assert ongoingProjects != null;
        for (ProjectDTO project : ongoingProjects) {
            HBox projectCard = createProjectCard(project);
            ongoingProjectsContainer.getChildren().add(projectCard);
        }

        totalProjectsLabel.setText("Total Ongoing Projects: " + ongoingProjects.size());
        totalProjectsLabel.getStyleClass().add("total-projects-label");
    }

    private void setupScrollPane() {
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("custom-scroll-pane");
    }

    private HBox createProjectCard(ProjectDTO project) {
        if(project == null){
            System.out.println("ProjectDTO is null");
        }

        HBox card = new HBox();
        card.getStyleClass().add("project-dashboard-card");

        assert project != null;
        String priority1 = String.valueOf(project.getPriority());
        System.out.println("Priority of Project: project.getPriority() : " + priority1);
        ProjectPriority priority = project.priorityProperty().get();
        System.out.println("Priority of Project: project.priorityProperty().get() : " + priority);
        if (priority != null) {
            String priorityClass = priority.toString().toLowerCase() + "-priority";
            card.getStyleClass().add(priorityClass);
            System.out.println("Applied priority class: " + priorityClass);
        } else {
            card.getStyleClass().add("unknown-priority");
            System.out.println("Default priority class applied for Projects : unknown-priority");
        }

        Label nameLabel = new Label(project.getName() != null ? project.getName() : "Unnamed Project");
        nameLabel.getStyleClass().add("project-dashboard-name");

        Label idLabel = new Label(project.getId() != null ? project.getId() : "Unknown ID");
        idLabel.getStyleClass().add("project-dashboard-id");

        VBox projectInfo = new VBox(nameLabel, idLabel);
        projectInfo.getStyleClass().add("project-info");

        card.getChildren().add(projectInfo);

        return card;
    }

    private void setupTasksView() {
        tasksContainer.setSpacing(8);
        tasksContainer.setPadding(new Insets(8));
    }

    private void loadUnresolvedTasks() {
        tasksContainer.getChildren().clear();

        List<TaskDTO> unresolvedTasks = null;
        try {
            unresolvedTasks = taskModel.getTasksByStatus(TaskStatus.NOT_STARTED);
            System.out.println("Unresolved tasks: " + unresolvedTasks);
        } catch (SQLException e) {
            System.out.println("Error loading unresolved tasks: " + e.getMessage());
            CustomErrorAlert.showAlert("Error loading unresolved tasks", e.getMessage());
        }

        assert unresolvedTasks != null;
        for (TaskDTO task : unresolvedTasks) {
            HBox taskItem = createTaskItem(task);
            tasksContainer.getChildren().add(taskItem);
        }
    }

    private HBox createTaskItem(TaskDTO task) {
        HBox taskItem = new HBox();
        taskItem.getStyleClass().add("task-item");
        taskItem.setAlignment(Pos.CENTER_LEFT);
        taskItem.setSpacing(12);

        Region priorityIndicator = new Region();
        priorityIndicator.getStyleClass().add("priority-indicator");

        String priorityClass = "priority-task-" + task.getPriority().toString().toLowerCase();
        priorityIndicator.getStyleClass().add(priorityClass);

        System.out.println("Applied priority classes to Task: " + priorityIndicator.getStyleClass());

        VBox taskDetails = new VBox(4);
        Label taskName = new Label(task.nameProperty().get());
        taskName.getStyleClass().add("task-name");

        String PROJECT_ID = null;
        try {
            PROJECT_ID = ProjectModel.getProjectIdByTaskId(task.idProperty().get());
        } catch (SQLException e) {
            System.out.println("Error fetching project ID: " + e.getMessage());
        }

        System.out.println("Project ID: " + PROJECT_ID);
        Label taskId = new Label(PROJECT_ID);
        taskId.getStyleClass().add("task-id");

        taskDetails.getChildren().addAll(taskName, taskId);

        taskItem.getChildren().addAll(priorityIndicator, taskDetails);

        taskItem.setOnMouseClicked(e -> handleTaskClick(task));

        return taskItem;
    }

    private void handleTaskClick(TaskDTO task) {
        System.out.println("Task clicked: " + task.getName());
    }

    private void setupRefreshButtonForPieChart() {
        refreshButton.setOnMouseClicked(event -> {
            System.out.println("Refresh button clicked for pie chart");
            setupPieChart();
        });
    }

    private void setupRefreshButtonForProjects(){
        refreshButtonForProjects.setOnMouseClicked(event -> {
            System.out.println("Refresh button clicked for Projects");
            Timeline timeline = new Timeline(new javafx.animation.KeyFrame(Duration.millis(700), z -> fadeIn(projectsDisplayCard)));
            timeline.play();
            loadUnresolvedTasks();
        });
    }

    private void setupRefreshButtonForTasks() {
        refreshButtonForTasks.setOnMouseClicked(e -> {
            System.out.println("Refresh button clicked for tasks");
            Timeline timeline = new Timeline(new javafx.animation.KeyFrame(Duration.millis(700), z -> fadeIn(taskDisplayCard)));
            timeline.play();
            loadUnresolvedTasks();
        });
    }

    private void setupPieChart() {
        Map<Long, TaskReportData> reportDataMap = ReportModel.getAllTaskReportData();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        String[] colors = {
                "#ff69b4",
                "#20b2aa",
                "#ffd700",
                "#4169e1",
                "#ff6347",
                "#32cd32",
                "#8a2be2",
                "#ff1493"
        };

        for (TaskReportData data : reportDataMap.values()) {
            String memberName = data.getAssigneeName();
            int totalTaskCount = data.getTaskCount();
            PieChart.Data pieData = new PieChart.Data(memberName, totalTaskCount);
            pieChartData.add(pieData);

        }

        assigneesPieChart.setData(pieChartData);
        assigneesPieChart.setStartAngle(90);

        Platform.runLater(() -> {
            int index = 0;
            for (PieChart.Data pieData : pieChartData) {
                if (pieData.getNode() != null) {
                    pieData.getNode().setStyle("-fx-pie-color: " + colors[index % colors.length]);
                }
                index++;
            }
        });

        createLegendItems(pieChartData);
    }

    private void createLegendItems(ObservableList<PieChart.Data> data) {
        legendContainer.getChildren().clear();
        String[] colors = {
                "#ff69b4", "#20b2aa", "#ffd700", "#4169e1", "#ff6347", "#32cd32", "#8a2be2", "#ff1493"
        };

        int colorIndex = 0;

        for (PieChart.Data pieData : data) {
            HBox legendItem = new HBox();
            legendItem.getStyleClass().add("legend-item");

            Circle colorIndicator = new Circle(4);
            colorIndicator.setStyle("-fx-fill: " + colors[colorIndex % colors.length]);
            colorIndicator.getStyleClass().add("legend-color-indicator");

            Label nameLabel = new Label(pieData.getName());
            nameLabel.getStyleClass().add("legend-name");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label valueLabel = new Label(String.valueOf((int) pieData.getPieValue()));
            valueLabel.getStyleClass().add("legend-value");

            legendItem.getChildren().addAll(colorIndicator, nameLabel, spacer, valueLabel);
            legendContainer.getChildren().add(legendItem);

            colorIndex++;
        }
    }

    private void navButtonStyle() {
        navButtons.add((Button) dashboardPage.lookup("#projectsButton"));
        navButtons.add((Button) dashboardPage.lookup("#issuesButton"));
        navButtons.add((Button) dashboardPage.lookup("#timesheetsButton"));
        navButtons.add((Button) dashboardPage.lookup("#taskButton"));
        navButtons.add((Button) dashboardPage.lookup("#reportsButton"));
        navButtons.add((Button) dashboardPage.lookup("#dashboardButton"));
    }

    private void handleButtonClick(Button clickedButton) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("active");
        }

        clickedButton.getStyleClass().add("active");
        activeButton = clickedButton;
    }

    private void userAccessController() {
/*        String username = "dinidu"; //dev purpose
        System.out.println("Username is logged in Now in Dashboard: " + username);
        SessionUser.initializeSession(username);*/

        String username = SessionUser.getLoggedInUsername();
        System.out.println("Username is logged in Now in Dashboard: " + username);
        SessionUser.initializeSession(username);

        String u = SessionUser.getLoggedInUsername();
        if (username == null) {
            System.out.println("User not logged in. username: " + u);
        }

        UserRole userRoleByUsername = UserModel.getUserRoleByUsername(u);

        if (userRoleByUsername == null) {
            System.out.println("User not logged in. userRoleByUsername: " + null);
        }

        System.out.println("User role: " + userRoleByUsername);
        if (userRoleByUsername != UserRole.ADMIN &&
                userRoleByUsername != UserRole.PROJECT_MANAGER &&
                userRoleByUsername != UserRole.PRODUCT_OWNER) {
            System.out.println("Access denied: Only Admin, Project Manager, or Product Owner can create projects.");
            settingId.setDisable(true);
        }
    }

    public void notifyClick() {
        if (NotifyViewController.isCurrentlyAnimating()) {
            return;
        }

        try {
            if (isNotificationVisible && currentNotification != null) {
                dismissCurrentNotification();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/nav-buttons/notify-view.fxml"));
            currentNotification = loader.load();

            if (contentPane != null) {
                contentPane.getChildren().add(currentNotification);
                isNotificationVisible = true;
                setupAutoDismiss();
            }
        } catch (IOException e) {
            System.out.println("Error loading notification view: " + e.getMessage());
        }
    }

    private void dismissCurrentNotification() {
        if (currentNotification != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentNotification);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                contentPane.getChildren().remove(currentNotification);
                currentNotification = null;
                isNotificationVisible = false;
            });
            fadeOut.play();
        }
    }

    private void setupAutoDismiss() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.seconds(30), event -> {
                    if (isNotificationVisible && currentNotification != null) {
                        dismissCurrentNotification();
                    }
                })
        );
        timeline.play();
    }

    public void myProfile() {
        hideCardContainers();
        navigateTo("/view/nav-buttons/profile-view.fxml");
    }

    public void clickOnReports(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/report-view.fxml");
    }

    public void clickOnTask(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/task-view.fxml");
    }

    public void clickOnDashboard(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/dashboard-expand-view.fxml");
    }

    public void clickOnProjects(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/project-view.fxml");
    }

    public void clickOnTimesheets(ActionEvent actionEvent) {
        handleButtonClick((Button) actionEvent.getSource());
        hideCardContainers();
        navigateTo("/view/nav-buttons/timesheet-view.fxml");
    }

    public void settingsClick() {
        hideCardContainers();
        navigateTo("/view/nav-buttons/settings-view.fxml");
    }

    public void navigateTo(String fxmlPath) {
        try {
            contentPane.getChildren().clear();
            AnchorPane load = FXMLLoader.load(getClass().getResource(fxmlPath));

            load.prefWidthProperty().bind(contentPane.widthProperty());
            load.prefHeightProperty().bind(contentPane.heightProperty());

            FadeTransition fadeIn = new FadeTransition(Duration.millis(750), load);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

            contentPane.getChildren().add(load);

        } catch (IOException e) {
            System.out.println("Failed to load page: " + e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Failed to load page!").show();
        }
    }

    public void clickOnIssues() {
        handleButtonClick((Button) dashboardPage.lookup("#issuesButton"));
        hideCardContainers();
        navigateTo("/view/nav-buttons/issues-view.fxml");
    }
}
