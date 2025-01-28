package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.custom.TeamAssignmentDAO;
import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;
import com.dinidu.lk.pmt.entity.TeamAssignment;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class TeamAssignmentDAOImpl implements TeamAssignmentDAO {
    @Override
    public List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<Long> getTeamMemberIdsByTask(long l) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public List<TeamAssignment> getAssignmentsByTaskId(Long taskId) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public boolean save(TeamAssignment dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(TeamAssignment dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(TeamAssignment teamAssignment) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<TeamAssignment> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<TeamAssignment> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
