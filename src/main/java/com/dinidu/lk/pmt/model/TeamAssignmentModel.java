package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamAssignmentModel {
    public static List<String> getAllTeamMembersNamesByTask(String taskName) throws SQLException {
        List<String> teamMembersNames = new ArrayList<>();

        String sql = "SELECT u.full_name FROM users u " +
                "JOIN team_assignments ta ON u.id = ta.user_id " +
                "JOIN tasks t ON ta.task_id = t.id " +
                "WHERE t.name = ?";

        try (ResultSet resultSet = CrudUtil.execute(sql, taskName)) {

            while (resultSet != null && resultSet.next()) {
                String teamMemberName = resultSet.getString("full_name");
                teamMembersNames.add(teamMemberName);
            }
        }

        return teamMembersNames;
    }

    public static List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException {
        String sql = "SELECT u.email FROM users u " +
                "JOIN team_assignments ta ON u.id = ta.user_id " +
                "JOIN tasks t ON ta.task_id = t.id " +
                "WHERE t.id = ?";

        List<String> emails = new ArrayList<>();

        try (ResultSet resultSet = CrudUtil.execute(sql, taskId)) {
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                emails.add(email);
            }
        }
        return emails;
    }

    public static List<Long> getTeamMemberIdsByTask(long l) throws SQLException {
        String sql = "SELECT user_id FROM team_assignments WHERE task_id = ?";
        List<Long> teamMemberIds = new ArrayList<>();
        try (ResultSet resultSet = CrudUtil.execute(sql, l)) {
            while (resultSet.next()) {
                teamMemberIds.add(resultSet.getLong("user_id"));
            }
        }
        return teamMemberIds;
    }

    public boolean saveAssignment(TeamAssignmentDTO teamAssignment) throws SQLException {
        String checkSql = "SELECT id FROM team_assignments WHERE task_id = ? AND user_id = ?";
        try (ResultSet resultSet = CrudUtil.execute(checkSql, teamAssignment.getTaskId(), teamAssignment.getUserId())) {

            if (resultSet != null && resultSet.next()) {
                return true;
            }
        }

        String insertSql = "INSERT INTO team_assignments (task_id, user_id, assigned_at) VALUES (?, ?, ?)";
        return CrudUtil.execute(insertSql,
                teamAssignment.getTaskId(),
                teamAssignment.getUserId(),
                teamAssignment.getAssignedAt()
        );
    }

    public static List<TeamAssignmentDTO> getAssignmentsByTaskId(Long taskId) {
        String sql = "SELECT * FROM team_assignments ta " +
                "JOIN users u ON ta.user_id = u.id " +
                "WHERE ta.task_id = ? AND u.status = 'ACTIVE'";
        List<TeamAssignmentDTO> assignments = new ArrayList<>();

        try {
            try (ResultSet resultSet = CrudUtil.execute(sql, taskId)) {

                while (resultSet.next()) {
                    TeamAssignmentDTO dto = new TeamAssignmentDTO();
                    dto.setId(resultSet.getLong("id"));
                    dto.setTaskId(resultSet.getLong("task_id"));
                    dto.setUserId(resultSet.getLong("user_id"));
                    dto.setAssignedAt(resultSet.getTimestamp("assigned_at"));
                    assignments.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return assignments;
    }
}
