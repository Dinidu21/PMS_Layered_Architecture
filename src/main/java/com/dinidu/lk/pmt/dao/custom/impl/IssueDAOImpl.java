package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.IssueDAO;
import com.dinidu.lk.pmt.dto.IssueDTO;
import com.dinidu.lk.pmt.entity.Issue;
import com.dinidu.lk.pmt.utils.issuesTypes.IssuePriority;
import com.dinidu.lk.pmt.utils.issuesTypes.IssueStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueDAOImpl implements IssueDAO {
    @Override
    public List<Issue> searchByName(String issueName) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM issues WHERE description LIKE ?";
        List<Issue> issues;
        try (ResultSet resultSet = SQLUtil.execute(query, issueName + "%")) {
            issues = new ArrayList<>();

            while (resultSet.next()) {
                Issue issue = new Issue();
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
    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        String query = "SELECT id FROM tasks WHERE name = ?";
        try (ResultSet rs = SQLUtil.execute(query, taskName)) {
            return rs.next() ? rs.getLong("id") : null;
        }
    }
    @Override
    public String getTaskNameById(Long taskId) throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM tasks WHERE id = ?";
        try (ResultSet rs = SQLUtil.execute(query, taskId)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    @Override
    public boolean deleteIssue(Long id) throws SQLException, ClassNotFoundException {
        String deleteQuery = "DELETE FROM issues WHERE id = ?";
        return SQLUtil.execute(deleteQuery, id);
    }

    @Override
    public String getProjectNameById(String projectId) throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM projects WHERE id = ?";
        try (ResultSet rs = SQLUtil.execute(query, projectId)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    @Override
    public String getProjectIdByName(String projectName) throws SQLException, ClassNotFoundException {
        String query = "SELECT id FROM projects WHERE name = ?";
        try (ResultSet rs = SQLUtil.execute(query, projectName)) {
            return rs.next() ? rs.getString("id") : null;
        }
    }

    @Override
    public Long getUserIdByName(String userName) throws SQLException, ClassNotFoundException {
        String query = "SELECT id FROM users WHERE full_name = ?";
        try (ResultSet rs = SQLUtil.execute(query, userName)) {
            return rs.next() ? rs.getLong("id") : null;
        }
    }

    @Override
    public List<String> getActiveProjectNames() throws SQLException, ClassNotFoundException {
        String query = "SELECT name FROM projects WHERE status != 'CANCELLED'";
        List<String> projectNames = new ArrayList<>();

        try (ResultSet rs = SQLUtil.execute(query)) {
            while (rs.next()) {
                projectNames.add(rs.getString("name"));
            }
        }

        return projectNames;
    }


    @Override
    public List<String> getTasksByProject(String projectName) throws SQLException, ClassNotFoundException {
        String query = """
             SELECT t.name
             FROM tasks t
             JOIN projects p ON t.project_id = p.id
             WHERE p.name = ? AND p.status != 'CANCELLED'
            """;
        List<String> taskNames = new ArrayList<>();
        try (ResultSet rs = SQLUtil.execute(query, projectName)) {
            while (rs.next()) {
                taskNames.add(rs.getString("name"));
            }
        }
        return taskNames;
    }


    @Override
    public List<String> getActiveMembers() throws SQLException, ClassNotFoundException {
        String query = """
             SELECT u.full_name
             FROM users u
             JOIN roles r ON u.role_id = r.id
             WHERE u.status = 'ACTIVE'
             AND r.id NOT IN (1, 2, 3, 7)
            """;
        List<String> memberNames = new ArrayList<>();

        try (ResultSet rs = SQLUtil.execute(query)) {
            while (rs.next()) {
                memberNames.add(rs.getString("full_name"));
            }
        }

        return memberNames;
    }

    @Override
    public List<Issue> fetchAll() throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM issues ORDER BY created_at DESC";
        List<Issue> issues;
        try (ResultSet resultSet = SQLUtil.execute(query)) {
            issues = new ArrayList<>();

            while (resultSet.next()) {
                Issue issue = new Issue();
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








    @Override
    public boolean update(Issue currentIssue) throws SQLException, ClassNotFoundException {
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
            return SQLUtil.execute(updateQuery, parameters.toArray());
    }

    @Override
    public boolean insert(Issue issue) throws SQLException, ClassNotFoundException {
        String insertQuery = """
                     INSERT INTO issues (project_id,\s
                     task_id, description, reported_by, assigned_to, status, priority)
                     VALUES (?, ?, ?, ?, ?, ?, ?)
                \s""";

        return SQLUtil.execute(insertQuery, issue.getProjectId(), issue.getTaskId(), issue.getDescription(), issue.getReportedBy(), issue.getAssignedTo(), issue.getStatus().toString(), issue.getPriority().toString());
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public boolean save(Issue dto) throws SQLException, ClassNotFoundException {
        return false;
    }


}
