package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.dto.TimesheetDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;

import java.sql.SQLException;

public class TimeSheetModel {
    public static boolean createTimeSheet(TimesheetDTO timesheetDTO) throws SQLException {
        String sql = "INSERT INTO timesheet (user_id, project_id, task_id, hours, work_date, description) VALUES (?, ?, ?, ?, ?, ?)";
        int affectedRows = CrudUtil.execute(sql,
                timesheetDTO.getUserId(),
                timesheetDTO.getProjectId(),
                timesheetDTO.getTaskId(),
                timesheetDTO.getHours(),
                timesheetDTO.getWorkDate(),
                timesheetDTO.getDescription());
        return affectedRows > 0;
    }
}
