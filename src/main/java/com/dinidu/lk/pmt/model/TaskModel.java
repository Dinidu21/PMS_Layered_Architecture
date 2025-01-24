package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.TasksDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskModel {
    public static List<TasksDTO> searchTasksByName(String searchQuery) {
        String sql = "SELECT * FROM tasks WHERE name LIKE ? ORDER BY created_at DESC";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TasksDTO> taskList = new ArrayList<>();

        try {
            connection = DBConnection.getInstance().getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, searchQuery + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TasksDTO tasksDTO = new TasksDTO();
                tasksDTO.idProperty().set(rs.getLong("id"));
                tasksDTO.projectIdProperty().set(rs.getString("project_id"));
                tasksDTO.nameProperty().set(rs.getString("name"));
                tasksDTO.descriptionProperty().set(rs.getString("description"));
                tasksDTO.assignedToProperty().set(rs.getLong("assigned_to"));
                tasksDTO.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
                tasksDTO.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
                tasksDTO.dueDateProperty().set(rs.getDate("due_date"));
                tasksDTO.createdAtProperty().set(rs.getTimestamp("created_at"));
                tasksDTO.updatedAtProperty().set(rs.getTimestamp("updated_at"));
                taskList.add(tasksDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error searching tasks: " + e.getMessage());
            throw new RuntimeException("Error searching tasks", e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    public static List<TasksDTO> getAllTasks() {
        String sql = "SELECT * FROM tasks ORDER BY created_at DESC";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TasksDTO> taskList = new ArrayList<>();

        try {
            connection = DBConnection.getInstance().getConnection();
            pstmt = connection.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TasksDTO tasksDTO = new TasksDTO();
                tasksDTO.idProperty().set(rs.getLong("id"));
                tasksDTO.projectIdProperty().set(rs.getString("project_id"));
                tasksDTO.nameProperty().set(rs.getString("name"));
                tasksDTO.descriptionProperty().set(rs.getString("description"));
                tasksDTO.assignedToProperty().set(rs.getLong("assigned_to"));
                tasksDTO.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
                tasksDTO.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
                tasksDTO.dueDateProperty().set(rs.getDate("due_date"));
                tasksDTO.createdAtProperty().set(rs.getTimestamp("created_at"));
                tasksDTO.updatedAtProperty().set(rs.getTimestamp("updated_at"));
                taskList.add(tasksDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
            throw new RuntimeException("Error retrieving tasks", e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    public static List<TasksDTO> getTaskByProjectId(String id) {

        String sql = "SELECT * FROM tasks WHERE project_id = ? ORDER BY created_at DESC";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TasksDTO> taskList = new ArrayList<>();

        try {
            connection = DBConnection.getInstance().getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, id);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TasksDTO tasksDTO = new TasksDTO();
                tasksDTO.idProperty().set(rs.getLong("id"));
                tasksDTO.projectIdProperty().set(rs.getString("project_id"));
                tasksDTO.nameProperty().set(rs.getString("name"));
                tasksDTO.descriptionProperty().set(rs.getString("description"));
                tasksDTO.assignedToProperty().set(rs.getLong("assigned_to"));
                tasksDTO.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
                tasksDTO.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
                tasksDTO.dueDateProperty().set(rs.getDate("due_date"));
                tasksDTO.createdAtProperty().set(rs.getTimestamp("created_at"));
                tasksDTO.updatedAtProperty().set(rs.getTimestamp("updated_at"));
                taskList.add(tasksDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
            throw new RuntimeException("Error retrieving tasks", e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    public static List<TasksDTO> getTasksByAssignee(String username) {

        String sql = """
                    SELECT t.*
                    FROM tasks t
                    JOIN users u ON t.assigned_to = u.id
                    WHERE u.username = ?
                    ORDER BY t.created_at DESC
                """;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<TasksDTO> taskList = new ArrayList<>();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TasksDTO tasksDTO = new TasksDTO();
                tasksDTO.idProperty().set(rs.getLong("id"));
                tasksDTO.projectIdProperty().set(rs.getString("project_id"));
                tasksDTO.nameProperty().set(rs.getString("name"));
                tasksDTO.descriptionProperty().set(rs.getString("description"));
                tasksDTO.assignedToProperty().set(rs.getLong("assigned_to"));
                tasksDTO.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
                tasksDTO.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
                tasksDTO.dueDateProperty().set(rs.getDate("due_date"));
                tasksDTO.createdAtProperty().set(rs.getTimestamp("created_at"));
                tasksDTO.updatedAtProperty().set(rs.getTimestamp("updated_at"));
                taskList.add(tasksDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving tasks by assignee: " + e.getMessage());
            throw new RuntimeException("Error retrieving tasks by assignee", e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return taskList;
    }

    public static String getTaskNameById(Long taskId) throws SQLException {
        String sql = "SELECT name FROM tasks WHERE id = ?";
        try (ResultSet rs = CrudUtil.execute(sql, taskId)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    public void updateTask(TasksDTO currentTask) {
        StringBuilder sql = new StringBuilder("UPDATE tasks SET ");
        boolean firstField = true;

        if (currentTask.nameProperty().get() != null) {
            sql.append("name = ?");
            firstField = false;
        }
        if (currentTask.descriptionProperty().get() != null) {
            if (!firstField) sql.append(", ");
            sql.append("description = ?");
            firstField = false;
        }
        if (currentTask.statusProperty().get() != null) {
            if (!firstField) sql.append(", ");
            sql.append("status = ?");
            firstField = false;
        }
        if (currentTask.priorityProperty().get() != null) {
            if (!firstField) sql.append(", ");
            sql.append("priority = ?");
            firstField = false;
        }
        if (currentTask.dueDateProperty().get() != null) {
            if (!firstField) sql.append(", ");
            sql.append("due_date = ?");
            firstField = false;
        }
        if (currentTask.assignedToProperty().get() != null) {
            if (!firstField) sql.append(", ");
            sql.append("assigned_to = ?");
            firstField = false;
        }

        sql.append(", updated_at = ? WHERE id = ?");

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql.toString());

            int index = 1;
            if (currentTask.nameProperty().get() != null) {
                pstmt.setString(index++, currentTask.nameProperty().get());
            }
            if (currentTask.descriptionProperty().get() != null) {
                pstmt.setString(index++, currentTask.descriptionProperty().get());
            }
            if (currentTask.statusProperty().get() != null) {
                pstmt.setString(index++, currentTask.statusProperty().get().name());
            }
            if (currentTask.priorityProperty().get() != null) {
                pstmt.setString(index++, currentTask.priorityProperty().get().name());
            }
            if (currentTask.dueDateProperty().get() != null) {
                pstmt.setDate(index++, new Date(currentTask.dueDateProperty().get().getTime()));
            }
            if (currentTask.assignedToProperty().get() != null) {
                pstmt.setLong(index++, currentTask.assignedToProperty().get());
            }

            pstmt.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
            pstmt.setLong(index, currentTask.idProperty().get());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        } finally {
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteTask(String name) {
        String sql = "DELETE FROM tasks WHERE name = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, name);

            pstmt.executeUpdate();
            System.out.println("Task deleted successfully: " + name);
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
            throw new RuntimeException("Error deleting task", e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<TasksDTO> getTasksByStatus(TaskStatus taskStatus) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE status = ?";
        try (ResultSet resultSet = CrudUtil.execute(sql, taskStatus.name())) {
            List<TasksDTO> taskList = new ArrayList<>();
            while (resultSet.next()) {
                TasksDTO tasksDTO = new TasksDTO();
                tasksDTO.idProperty().set(resultSet.getLong("id"));
                tasksDTO.projectIdProperty().set(resultSet.getString("project_id"));
                tasksDTO.nameProperty().set(resultSet.getString("name"));
                tasksDTO.descriptionProperty().set(resultSet.getString("description"));
                tasksDTO.assignedToProperty().set(resultSet.getLong("assigned_to"));
                tasksDTO.priorityProperty().set(TaskPriority.valueOf(resultSet.getString("priority")));
                tasksDTO.statusProperty().set(TaskStatus.valueOf(resultSet.getString("status")));
                tasksDTO.dueDateProperty().set(resultSet.getDate("due_date"));
                tasksDTO.createdAtProperty().set(resultSet.getTimestamp("created_at"));
                tasksDTO.updatedAtProperty().set(resultSet.getTimestamp("updated_at"));
                taskList.add(tasksDTO);
            }
            return taskList;
        }
    }

    public static List<TasksDTO> getTasksCurrentProjectByStatus(String projectId, TaskStatus taskStatus) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE project_id = ? AND status = ?";
        try (ResultSet resultSet = CrudUtil.execute(sql, projectId, taskStatus.name())) {
            List<TasksDTO> taskList = new ArrayList<>();
            while (resultSet.next()) {
                TasksDTO tasksDTO = new TasksDTO();
                tasksDTO.idProperty().set(resultSet.getLong("id"));
                tasksDTO.projectIdProperty().set(resultSet.getString("project_id"));
                tasksDTO.nameProperty().set(resultSet.getString("name"));
                tasksDTO.descriptionProperty().set(resultSet.getString("description"));
                tasksDTO.assignedToProperty().set(resultSet.getLong("assigned_to"));
                tasksDTO.priorityProperty().set(TaskPriority.valueOf(resultSet.getString("priority")));
                tasksDTO.statusProperty().set(TaskStatus.valueOf(resultSet.getString("status")));
                tasksDTO.dueDateProperty().set(resultSet.getDate("due_date"));
                tasksDTO.createdAtProperty().set(resultSet.getTimestamp("created_at"));
                tasksDTO.updatedAtProperty().set(resultSet.getTimestamp("updated_at"));
                taskList.add(tasksDTO);
            }
            return taskList;
        }
    }

    public boolean insertTask(TasksDTO tasksDTO) throws SQLException {
        String taskSql = "INSERT INTO tasks (project_id, name, description, assigned_to, " +
                "priority, status, due_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        String assignmentSql = "INSERT INTO team_assignments (task_id, user_id, assigned_at) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)";
        boolean isSaved = false;

        Connection connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean taskInserted = CrudUtil.execute(taskSql, tasksDTO.projectIdProperty().get(),
                    tasksDTO.nameProperty().get(), tasksDTO.descriptionProperty().get(),
                    tasksDTO.assignedToProperty().get(), tasksDTO.priorityProperty().get().name(),
                    tasksDTO.statusProperty().get().name(), new java.sql.Date(tasksDTO.dueDateProperty().get().getTime()));

            if (taskInserted) {
                // Retrieve the generated task ID
                String taskIdQuery = "SELECT LAST_INSERT_ID()";
                try (ResultSet resultSet = CrudUtil.execute(taskIdQuery)) {

                    if (resultSet.next()) {
                        long taskId = resultSet.getLong(1);

                        boolean assignmentInserted = CrudUtil.execute(assignmentSql, taskId, tasksDTO.assignedToProperty().get());

                        if (assignmentInserted) {
                            connection.commit();
                            isSaved = true;
                        } else {
                            connection.rollback();
                            System.out.println("Failed to insert team assignment.");
                            CustomErrorAlert.showAlert("ERROR", "Failed to insert team assignment.");
                        }
                    } else {
                        connection.rollback();
                        System.out.println("Failed to retrieve task ID.");
                        CustomErrorAlert.showAlert("ERROR", "Failed to retrieve task ID.");
                    }
                }
            } else {
                connection.rollback();
                System.out.println("Failed to insert task.");
                CustomErrorAlert.showAlert("ERROR", "Failed to insert task.");
            }
        } finally {
            connection.setAutoCommit(true);
        }
        return isSaved;
    }
}
