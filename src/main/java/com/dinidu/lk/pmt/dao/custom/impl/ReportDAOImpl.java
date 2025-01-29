package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.ReportsDAO;
import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.entity.Report;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ReportDAOImpl implements ReportsDAO {
    QueryDAO queryDAO = new QueryDAOImpl();

    @Override
    public Map<Long, TaskReportData> getAllTaskReportData() throws SQLException, ClassNotFoundException {
        return queryDAO.getAllTaskReportData();
    }

    @Override
    public boolean insert(Report report) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("INSERT INTO reports (project_id, user_id, \" +\n" +
                        "                \"report_type, content, created_at, updated_at) \" +\n" +
                        "                \"VALUES (?, ?, ?, ?, ?, ?)",
                report.getProjectId(),
                report.getUserId(),
                report.getReportType().name(),
                report.getContent(),
                report.getCreatedAt(),
                report.getUpdatedAt());
    }

    @Override
    public boolean save(Report dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Report dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Report> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<Report> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
