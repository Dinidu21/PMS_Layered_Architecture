package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.TasksDAO;
import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.TasksDTO;
import com.dinidu.lk.pmt.entity.Tasks;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.taskTypes.TaskPriority;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TasksDAOImpl implements TasksDAO {
    // Working
    @Override
    public List<Tasks> getTaskByProjectId(String id) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM tasks WHERE project_id = ? ORDER BY created_at DESC", id);
        List<TasksDTO> taskList = new ArrayList<>();
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
        return EntityDTOMapper.mapDTOListToEntityList(taskList, Tasks.class);
    }
    // Working
    @Override
    public List<Tasks> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM tasks WHERE name LIKE ? ORDER BY created_at DESC", searchQuery + "%");
        List<Tasks> taskList = new ArrayList<>();
        while (rs.next()) {
            Tasks tasks = new Tasks();
            tasks.idProperty().set(rs.getLong("id"));
            tasks.projectIdProperty().set(rs.getString("project_id"));
            tasks.nameProperty().set(rs.getString("name"));
            tasks.descriptionProperty().set(rs.getString("description"));
            tasks.assignedToProperty().set(rs.getLong("assigned_to"));
            tasks.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
            tasks.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
            tasks.dueDateProperty().set(rs.getDate("due_date"));
            tasks.createdAtProperty().set(rs.getTimestamp("created_at"));
            tasks.updatedAtProperty().set(rs.getTimestamp("updated_at"));
            taskList.add(tasks);
        }
        return taskList;
    }
    // Working
    @Override
    public List<Tasks> fetchAll() throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM tasks ORDER BY created_at DESC");
        List<Tasks> taskList = new ArrayList<>();
        while (rs.next()) {
            Tasks tasks = new Tasks();
            tasks.idProperty().set(rs.getLong("id"));
            tasks.projectIdProperty().set(rs.getString("project_id"));
            tasks.nameProperty().set(rs.getString("name"));
            tasks.descriptionProperty().set(rs.getString("description"));
            tasks.assignedToProperty().set(rs.getLong("assigned_to"));
            tasks.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
            tasks.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
            tasks.dueDateProperty().set(rs.getDate("due_date"));
            tasks.createdAtProperty().set(rs.getTimestamp("created_at"));
            tasks.updatedAtProperty().set(rs.getTimestamp("updated_at"));
            taskList.add(tasks);
        }
        return taskList;
    }
    // Working
    @Override
    public String getTaskNameById(Long taskId) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT name FROM tasks WHERE id = ?", taskId);
        if (rs.next()) {
            return rs.getString("name");
        }
        return null;
    }
    // Working
    @Override
    public void updateTask(Tasks currentTask) throws SQLException {
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
        }

        sql.append(", updated_at = ? WHERE id = ?");
        Connection conn;
        PreparedStatement pstmt;

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

    }
    // Working
    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("DELETE FROM tasks WHERE name = ?", idOrName);
    }
    // Working
    @Override
    public List<Tasks> getByStatus(TaskStatus taskStatus) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM tasks WHERE status = ?", taskStatus.name());
        List<Tasks> taskList = new ArrayList<>();
        while (rs.next()) {
            Tasks tasks = new Tasks();
            tasks.idProperty().set(rs.getLong("id"));
            tasks.projectIdProperty().set(rs.getString("project_id"));
            tasks.nameProperty().set(rs.getString("name"));
            tasks.descriptionProperty().set(rs.getString("description"));
            tasks.assignedToProperty().set(rs.getLong("assigned_to"));
            tasks.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
            tasks.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
            tasks.dueDateProperty().set(rs.getDate("due_date"));
            tasks.createdAtProperty().set(rs.getTimestamp("created_at"));
            tasks.updatedAtProperty().set(rs.getTimestamp("updated_at"));
            taskList.add(tasks);
        }
        return taskList;
    }
    // Working
    @Override
    public List<Tasks> getTasksCurrentProjectByStatus(String projectId, TaskStatus taskStatus) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM tasks WHERE project_id = ? AND status = ?", projectId, taskStatus.name());
        List<Tasks> taskList = new ArrayList<>();
        while (rs.next()) {
            Tasks tasks = new Tasks();
            tasks.idProperty().set(rs.getLong("id"));
            tasks.projectIdProperty().set(rs.getString("project_id"));
            tasks.nameProperty().set(rs.getString("name"));
            tasks.descriptionProperty().set(rs.getString("description"));
            tasks.assignedToProperty().set(rs.getLong("assigned_to"));
            tasks.priorityProperty().set(TaskPriority.valueOf(rs.getString("priority")));
            tasks.statusProperty().set(TaskStatus.valueOf(rs.getString("status")));
            tasks.dueDateProperty().set(rs.getDate("due_date"));
            tasks.createdAtProperty().set(rs.getTimestamp("created_at"));
            tasks.updatedAtProperty().set(rs.getTimestamp("updated_at"));
            taskList.add(tasks);
        }
        return taskList;
    }
    // Working
    /*****    TRANSACTION   *****/
    @Override
    public boolean insertTask(Tasks task) throws SQLException, ClassNotFoundException {
        String taskSql = "INSERT INTO tasks (project_id, name, description, assigned_to, " +
                "priority, status, due_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
        return SQLUtil .execute(taskSql, task.projectIdProperty().get(),
                task.nameProperty().get(), task.descriptionProperty().get(),
                task.assignedToProperty().get(), task.priorityProperty().get().name(),
                task.statusProperty().get().name(), new java.sql.Date(task.dueDateProperty().get().getTime()));
    }
    // Working
    @Override
    public boolean insertAssignment(long lastInsertedTaskId, Long aLong) throws SQLException, ClassNotFoundException {
        String assignmentSql = "INSERT INTO team_assignments (task_id, user_id, assigned_at) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)";
        return SQLUtil.execute(assignmentSql, lastInsertedTaskId, aLong);
    }
    // Working
    @Override
    public long getLastInsertedTaskId() throws SQLException, ClassNotFoundException{
        ResultSet rs = SQLUtil.execute("SELECT LAST_INSERT_ID()");
        if (rs.next()) {
            return rs.getLong(1);
        }else {
            System.out.println("Error getting last inserted task ID");
            CustomErrorAlert.showAlert("Error", "Error getting last inserted task ID");
        }
        return 0;
    }




    @Override
    public boolean save(Tasks dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Tasks dto) throws SQLException, ClassNotFoundException {
        return false;
    }
    @Override
    public boolean insert(Tasks tasks) throws SQLException, ClassNotFoundException {
        return false;
    }
    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }
    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
