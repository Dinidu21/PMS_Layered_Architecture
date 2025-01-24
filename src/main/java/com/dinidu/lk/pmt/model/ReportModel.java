package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.dto.ReportDTO;
import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.utils.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportModel {

    public static boolean insertReport(ReportDTO reportDTO) {
        String sql = "INSERT INTO reports (project_id, user_id, " +
                "report_type, content, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Object[] params = new Object[]{
                reportDTO.getProjectId(),
                reportDTO.getUserId(),
                reportDTO.getReportType().name(),
                reportDTO.getContent(),
                reportDTO.getCreatedAt(),
                reportDTO.getUpdatedAt()
        };

        return CrudUtil.execute(sql, params);
    }

    public static Map<Long, TaskReportData> getAllTaskReportData() {
        Map<Long, TaskReportData> reportDataMap = new LinkedHashMap<>();

        String query = """
                    SELECT u.full_name, r.role_name, t.id AS task_id, t.name AS task_name,
                           t.status AS task_status, p.name AS project_name, t.due_date,
                           ta.assigned_at, u.id AS user_id
                    FROM users u
                    LEFT JOIN team_assignments ta ON ta.user_id = u.id
                    LEFT JOIN tasks t ON t.id = ta.task_id
                    LEFT JOIN projects p ON p.id = t.project_id
                    LEFT JOIN roles r ON r.id = u.role_id
                    WHERE u.status = 'ACTIVE'\s
                      AND r.id NOT IN (1, 2, 3, 7)
                """;

        try (ResultSet rs = CrudUtil.execute(query)) {
            while (rs.next()) {
                long userId = rs.getLong("user_id");
                String assigneeName = rs.getString("full_name");
                String role = rs.getString("role_name");
                long taskId = rs.getLong("task_id");
                String taskName = rs.getString("task_name");
                String taskStatus = rs.getString("task_status");
                String projectName = rs.getString("project_name");
                String dueDate = rs.getString("due_date");
                String assignedDate = rs.getString("assigned_at");

                int taskCount = getTaskCountForUser(userId);
                System.out.println("Task Count for User " + assigneeName + ": " + taskCount);

                TaskReportData data = reportDataMap.getOrDefault(userId,
                        new TaskReportData(assigneeName, role, taskId, taskName, taskStatus, projectName, dueDate, assignedDate, taskCount));

                data.setTaskCount(taskCount);
                reportDataMap.put(userId, data);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportDataMap;
    }

    private static int getTaskCountForUser(long userId) {
        String countQuery = """
                    SELECT COUNT(DISTINCT ta.task_id) AS total_task_count
                    FROM team_assignments ta
                    WHERE ta.user_id = ?
                """;

        try {
            try (ResultSet rs = CrudUtil.execute(countQuery, userId)) {
                if (rs.next()) {
                    return rs.getInt("total_task_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
