/*
package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.dto.IssueDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;
import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IssueModel {
*/
/*
    public static boolean createIssue(IssueDTO issueDTO) throws SQLException {
        String insertQuery = """
                     INSERT INTO issues (project_id,\s
                     task_id, description, reported_by, assigned_to, status, priority)
                     VALUES (?, ?, ?, ?, ?, ?, ?)
                \s""";

        return CrudUtil.execute(insertQuery, issueDTO.getProjectId(), issueDTO.getTaskId(), issueDTO.getDescription(), issueDTO.getReportedBy(), issueDTO.getAssignedTo(), issueDTO.getStatus().toString(), issueDTO.getPriority().toString());
    }
*//*


*/
/*
    public static String getProjectNameById(String projectId) throws SQLException {
        String query = "SELECT name FROM projects WHERE id = ?";
        try (ResultSet rs = CrudUtil.execute(query, projectId)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }
*//*


*/
/*    public static String getTaskNameById(Long taskId) throws SQLException {
        String query = "SELECT name FROM tasks WHERE id = ?";
        try (ResultSet rs = CrudUtil.execute(query, taskId)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }*//*


*/
/*
    public static List<IssueDTO> getAllIssues() throws SQLException {
        String query = "SELECT * FROM issues ORDER BY created_at DESC";
        List<IssueDTO> issues;
        try (ResultSet resultSet = CrudUtil.execute(query)) {
            issues = new ArrayList<>();

            while (resultSet.next()) {
                IssueDTO issue = new IssueDTO();
                issue.setId(resultSet.getLong("id"));
                issue.setProjectId(resultSet.getString("project_id"));
                issue.setTaskId(resultSet.getLong("task_id"));
                issue.setDescription(resultSet.getString("description"));
                issue.setReportedBy(resultSet.getLong("reported_by"));
                issue.setAssignedTo(resultSet.getLong("assigned_to"));
                issue.setStatus(IssueStatus.valueOf(resultSet.getString("status")));
                issue.setPriority(IssuePriority.valueOf(resultSet.getString("priority")));
                issue.setDueDate(resultSet.getDate("due_date"));
                issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                issue.setUpdatedAt(resultSet.getTimestamp("updated_at"));

                issues.add(issue);
            }
        }
        return issues;
    }
*//*


*/
/*
    public static List<IssueDTO> searchIssuesByName(String issueName) throws SQLException {
        String query = "SELECT * FROM issues WHERE description LIKE ?";
        List<IssueDTO> issues;
        try (ResultSet resultSet = CrudUtil.execute(query, issueName + "%")) {
            issues = new ArrayList<>();

            while (resultSet.next()) {
                IssueDTO issue = new IssueDTO();
                issue.setId(resultSet.getLong("id"));
                issue.setProjectId(resultSet.getString("project_id"));
                issue.setTaskId(resultSet.getLong("task_id"));
                issue.setDescription(resultSet.getString("description"));
                issue.setReportedBy(resultSet.getLong("reported_by"));
                issue.setAssignedTo(resultSet.getLong("assigned_to"));
                issue.setStatus(IssueStatus.valueOf(resultSet.getString("status")));
                issue.setPriority(IssuePriority.valueOf(resultSet.getString("priority")));
                issue.setDueDate(resultSet.getDate("due_date"));
                issue.setCreatedAt(resultSet.getTimestamp("created_at"));
                issue.setUpdatedAt(resultSet.getTimestamp("updated_at"));

                issues.add(issue);
            }
        }
        return issues;
    }
*//*


*/
/*
    public static boolean updateIssue(IssueDTO currentIssue) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder("UPDATE issues SET ");
        List<Object> parameters = new ArrayList<>();

        if (currentIssue.getProjectId() != null) {
            queryBuilder.append("project_id = ?, ");
            parameters.add(currentIssue.getProjectId());
        }
        if (currentIssue.getTaskId() != 0) {
            queryBuilder.append("task_id = ?, ");
            parameters.add(currentIssue.getTaskId());
        }
        if (currentIssue.getDescription() != null) {
            queryBuilder.append("description = ?, ");
            parameters.add(currentIssue.getDescription());
        }
        if (currentIssue.getReportedBy() != 0) {
            queryBuilder.append("reported_by = ?, ");
            parameters.add(currentIssue.getReportedBy());
        }
        if (currentIssue.getAssignedTo() != 0) {
            queryBuilder.append("assigned_to = ?, ");
            parameters.add(currentIssue.getAssignedTo());
        }
        if (currentIssue.getStatus() != null) {
            queryBuilder.append("status = ?, ");
            parameters.add(currentIssue.getStatus().toString());
        }
        if (currentIssue.getPriority() != null) {
            queryBuilder.append("priority = ?, ");
            parameters.add(currentIssue.getPriority().toString());
        }
        if (currentIssue.getDueDate() != null) {
            queryBuilder.append("due_date = ?, ");
            parameters.add(currentIssue.getDueDate());
        }

        queryBuilder.append("updated_at = CURRENT_TIMESTAMP ");

        queryBuilder.append("WHERE id = ?");
        parameters.add(currentIssue.getId());

        String updateQuery = queryBuilder.toString();
        return CrudUtil.execute(updateQuery, parameters.toArray());
    }
*//*


*/
/*
    public static boolean deleteIssue(Long id) {
        String deleteQuery = "DELETE FROM issues WHERE id = ?";
        return CrudUtil.execute(deleteQuery, id);
    }
*//*


*/
/*    public static String getProjectIdByName(String projectName) throws SQLException {
        String query = "SELECT id FROM projects WHERE name = ?";
        try (ResultSet rs = CrudUtil.execute(query, projectName)) {
            return rs.next() ? rs.getString("id") : null;
        }
    }*//*


*/
/*    public static Long getTaskIdByName(String taskName) throws SQLException {
        String query = "SELECT id FROM tasks WHERE name = ?";
        try (ResultSet rs = CrudUtil.execute(query, taskName)) {
            return rs.next() ? rs.getLong("id") : null;
        }
    }*//*


*/
/*    public static Long getUserIdByName(String userName) throws SQLException {
        String query = "SELECT id FROM users WHERE full_name = ?";
        try (ResultSet rs = CrudUtil.execute(query, userName)) {
            return rs.next() ? rs.getLong("id") : null;
        }
    }*//*

*/
/*
    public static ResultSet getActiveProjectNames() throws SQLException {
        String query = "SELECT name FROM projects WHERE status != 'CANCELLED'";
        return CrudUtil.execute(query);
    }*//*


*/
/*    public static ResultSet getTasksByProject(String projectName) throws SQLException {
        String query = """
                 SELECT t.name\s
                 FROM tasks t\s
                 JOIN projects p ON t.project_id = p.id\s
                 WHERE p.name = ? AND p.status != 'CANCELLED'
                \s""";
        return CrudUtil.execute(query, projectName);
    }*//*


    public static ResultSet getActiveMembers() throws SQLException {
        String query = """
                 SELECT u.full_name\s
                 FROM users u
                 JOIN roles r ON u.role_id = r.id
                 WHERE u.status = 'ACTIVE'\s
                 AND r.id NOT IN (1, 2, 3, 7)
                \s""";
        return CrudUtil.execute(query);
    }
}
*/
