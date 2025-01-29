package com.dinidu.lk.pmt.dao.custom.impl;
import com.dinidu.lk.pmt.dao.QueryDAO;
import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.TeamAssignmentDAO;
import com.dinidu.lk.pmt.entity.TeamAssignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamAssignmentDAOImpl implements TeamAssignmentDAO {

    QueryDAO queryDAO=new QueryDAOImpl();
    // Working
    @Override
    public List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException, ClassNotFoundException {
        return queryDAO.getTeamMemberEmailsByTask(taskId);
    }
    // Working
    @Override
    public List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException, ClassNotFoundException {
        return queryDAO.getAllTeamMembersNamesByTask(taskName);
    }
    // Working
    @Override
    public List<Long> getTeamMemberIdsByTask(long l) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT user_id FROM team_assignments WHERE task_id = ?", l);
        List<Long> teamMemberIds=new ArrayList<>();
        while (rs.next()) {
            teamMemberIds.add(rs.getLong("user_id"));
        }
        return teamMemberIds;
    }
    // Working
    @Override
    public List<TeamAssignment> getAssignmentsByTaskId(Long taskId) throws SQLException, ClassNotFoundException {
        return queryDAO.getAssignmentsByTaskId(taskId);
    }
    // Working
    @Override
    public boolean save(TeamAssignment teamAssignment) throws SQLException, ClassNotFoundException {
        String checkSql = "SELECT id FROM team_assignments WHERE task_id = ? AND user_id = ?";
        ResultSet rs = SQLUtil.execute(checkSql, teamAssignment.getTaskId(), teamAssignment.getUserId());

        if (rs != null && rs.next()) {
            return true;
        }
        String insertSql = "INSERT INTO team_assignments (task_id, user_id, assigned_at) VALUES (?, ?, ?)";
        return SQLUtil.execute(insertSql,
                teamAssignment.getTaskId(),
                teamAssignment.getUserId(),
                teamAssignment.getAssignedAt()
        );
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
