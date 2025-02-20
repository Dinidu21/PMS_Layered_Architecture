package com.dinidu.lk.pmt.controller.dashboard;

import com.dinidu.lk.pmt.bo.BOFactory;
import com.dinidu.lk.pmt.bo.custom.ProjectsBO;
import com.dinidu.lk.pmt.bo.custom.ReportsBO;
import com.dinidu.lk.pmt.bo.custom.UserBO;
import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.dto.ReportDTO;
import com.dinidu.lk.pmt.utils.customAlerts.CustomAlert;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.SessionUser;
import com.dinidu.lk.pmt.utils.reportTypes.ReportType;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;


import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class ReportViewController implements Initializable {

    public ImageView backButton;
    public AnchorPane createRp;
    public HBox openToFixedThisWeekView;
    public HBox noOFTaskPerAssigneeView;
    public TextField reportNameField;
    public TextArea descriptionIdField;
    public ComboBox<ReportType> selectReportTypeComboBox;
    public ComboBox<String> selectProjectNameComboBox;
    @FXML
    private AnchorPane cardContainer;

    @FXML
    private AnchorPane mainContentPane;

    private boolean isCardContainerVisible = false;

    UserBO userBO= (UserBO)
            BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.USER);
    ProjectsBO projectBO =
            (ProjectsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.PROJECTS);
    ReportsBO reportBO =
            (ReportsBO) BOFactory.getInstance().
                    getBO(BOFactory.BOTypes.REPORTS);

    @FXML
    public void toggleCardContainer() {
        if (isCardContainerVisible) {
            hideCardContainer();
        } else {
            showCardContainer();
        }
    }

    private void showCardContainer() {
        fadeIn(cardContainer);
        cardContainer.setVisible(true);
        TranslateTransition moveContent = new TranslateTransition(Duration.millis(500), mainContentPane);
        moveContent.setFromX(0);
        moveContent.setToX(334);
        moveContent.play();
        isCardContainerVisible = true;
    }

    private void hideCardContainer() {
        fadeOut(cardContainer);
        TranslateTransition moveContent = new TranslateTransition(Duration.millis(500), mainContentPane);
        moveContent.setFromX(334);
        moveContent.setToX(0);
        moveContent.play();
        isCardContainerVisible = false;
    }

    private void fadeIn(AnchorPane node) {
        node.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void fadeOut(AnchorPane node) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), node);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> node.setVisible(false));
        fadeOut.play();
    }

    public void noOFTaskPerAssigneeViewOnClicked(MouseEvent mouseEvent) {
        ProjectViewController.bindNavigation(createRp, "/view/nav-buttons/report/report-task-view.fxml");
    }

    public void openToFixedThisWeekViewOnClick(MouseEvent mouseEvent) {
        ProjectViewController.bindNavigation(createRp, "/view/nav-buttons/report/open-to-fixed-this-week-view.fxml");
    }

    public void cancelOnClick(ActionEvent actionEvent) {
        reportNameField.clear();
        descriptionIdField.clear();
        selectReportTypeComboBox.setValue(null);
        selectProjectNameComboBox.setValue(null);
    }

    public void createReportClick(ActionEvent actionEvent) {
        String reportName = reportNameField.getText();
        String description = descriptionIdField.getText();
        ReportType selectedReportType = selectReportTypeComboBox.getValue();
        String selectedProjectName = selectProjectNameComboBox.getValue();

        if (reportName == null || reportName.isEmpty() || description == null || description.isEmpty() || selectedReportType == null || selectedProjectName == null) {
            System.out.println("Please fill in all fields.");
            CustomErrorAlert.showAlert("ERROR", "Please fill in all fields.");
            return;
        }

        String id;
        try {
            id = projectBO.getProjectIdByName(selectedProjectName);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Project id: " + id);
        System.out.println("Project name: " + selectedProjectName);
        if (id == null) {
            System.out.println("Project not found.");
            return;
        }

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setProjectId(id);

        String username = SessionUser.getLoggedInUsername();

        if (username == null) {
            System.out.println("User not logged in. username: " + null);
        }

        Long userIdByUsername;
        try {
            userIdByUsername = userBO.getUserIdByUsername(username);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        reportDTO.setUserId(userIdByUsername);
        reportDTO.setReportType(selectedReportType);
        reportDTO.setContent(description);
        reportDTO.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        reportDTO.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        boolean isSuccess;
        try {
            isSuccess = reportBO.insertReport(reportDTO);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

        if (isSuccess) {
            try {
                JasperReport jasperReport = JasperCompileManager.compileReport(getClass().getResourceAsStream("/reports/GeneralReport.jrxml"));

                Connection connection = DBConnection.getInstance().getConnection();

                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, connection);

                File outputFile = new File("ReportOutput.docx");
                JRDocxExporter exporter = new JRDocxExporter();

                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));

                exporter.exportReport();

                System.out.println("Report saved successfully as DOCX!");
                CustomAlert.showAlert("SUCCESS", "Report saved successfully !");
                clearContent();
            } catch (JRException e) {
                e.printStackTrace();
                System.out.println("Failed to generate report: " + e.getMessage());
                CustomErrorAlert.showAlert("Error", "Failed to generate report");
            }
        } else {
            System.out.println("Failed to create report.");
            CustomErrorAlert.showAlert("ERROR", "Failed to create report.");
        }
    }

    private void clearContent() {
        reportNameField.clear();
        descriptionIdField.clear();
        selectReportTypeComboBox.setValue(null);
        selectProjectNameComboBox.setValue(null);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<ProjectDTO> allProjects;
        try {
            allProjects = projectBO.getAllProjects();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> projectNames = FXCollections.observableArrayList();
        assert allProjects != null;
        for (ProjectDTO project : allProjects) {
            projectNames.add(project.getName());
        }
        selectProjectNameComboBox.setItems(projectNames);
        selectReportTypeComboBox.getItems().addAll(ReportType.values());
    }
}
