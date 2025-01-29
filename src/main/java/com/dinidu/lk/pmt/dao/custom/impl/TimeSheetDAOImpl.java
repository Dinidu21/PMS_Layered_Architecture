package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.TimeSheetDAO;
import com.dinidu.lk.pmt.entity.Timesheet;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TimeSheetDAOImpl implements TimeSheetDAO {

    @Override
    public boolean save(Timesheet timesheet) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO timesheet (user_id, project_id, task_id, hours, " +
                "work_date, description) VALUES (?, ?, ?, ?, ?, ?)",timesheet.getUserId(),
                timesheet.getProjectId(),
                timesheet.getTaskId(),
                timesheet.getHours(),
                timesheet.getWorkDate(),
                timesheet.getDescription());
    }

    @Override
    public boolean update(Timesheet dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(Timesheet timesheet) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Timesheet> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<Timesheet> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
