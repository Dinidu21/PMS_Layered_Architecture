package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.custom.TimeSheetDAO;
import com.dinidu.lk.pmt.entity.Timesheet;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TimeSheetDAOImpl implements TimeSheetDAO {
    @Override
    public boolean save(Timesheet dto) throws SQLException, ClassNotFoundException {
        return false;
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
