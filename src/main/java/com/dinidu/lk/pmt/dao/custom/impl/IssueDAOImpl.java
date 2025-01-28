package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.custom.IssueDAO;
import com.dinidu.lk.pmt.entity.Issue;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class IssueDAOImpl implements IssueDAO {
    @Override
    public boolean deleteIssue(Long id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean save(Issue dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Issue dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(Issue issue) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Issue> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<Issue> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
