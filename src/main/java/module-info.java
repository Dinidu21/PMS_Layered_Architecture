module com.dinidu.lk.pmt {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires java.mail;
    requires java.sql;
    requires jbcrypt;
    requires org.apache.commons.csv;
    requires org.apache.poi.ooxml;
    requires net.sf.jasperreports.core;
    requires javafx.media;
    requires activation;
    requires org.apache.commons.lang3;


    opens com.dinidu.lk.pmt.controller to javafx.fxml;
    exports com.dinidu.lk.pmt;
    opens com.dinidu.lk.pmt.controller.dashboard to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.project to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.forgetpassword to javafx.fxml;
    opens com.dinidu.lk.pmt.utils to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.task to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.report to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.issue to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.task.checklist to javafx.fxml;
    opens com.dinidu.lk.pmt.utils.mail to javafx.fxml;
    opens com.dinidu.lk.pmt.utils.notification to javafx.fxml;
    opens com.dinidu.lk.pmt.utils.listeners to javafx.fxml;
    opens com.dinidu.lk.pmt.utils.customAlerts to javafx.fxml;
    opens com.dinidu.lk.pmt.controller.dashboard.timesheet to javafx.fxml;
}