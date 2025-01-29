package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.ChecklistDAO;
import com.dinidu.lk.pmt.entity.Checklist;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import javafx.beans.property.LongProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckListDAOImpl implements ChecklistDAO {
    @Override
    public Checklist mapResultSetToChecklistDTO(ResultSet resultSet) throws SQLException {
        Checklist checklist = new Checklist();
        checklist.idProperty().set(resultSet.getLong("id"));
        checklist.taskIdProperty().set(resultSet.getLong("task_id"));
        checklist.nameProperty().set(resultSet.getString("name"));
        checklist.descriptionProperty().set(resultSet.getString("description"));
        checklist.statusProperty().set(ChecklistStatus.valueOf(resultSet.getString("status")));
        checklist.priorityProperty().set(ChecklistPriority.valueOf(resultSet.getString("priority")));
        checklist.assignedToProperty().set(resultSet.getLong("assigned_to"));
        checklist.dueDateProperty().set(resultSet.getObject("due_date", LocalDateTime.class));
        checklist.createdAtProperty().set(resultSet.getObject("created_at", LocalDateTime.class));
        checklist.updatedAtProperty().set(resultSet.getObject("updated_at", LocalDateTime.class));
        return checklist;
    }

    @Override
    public List<Checklist> getChecklistsByTaskId(LongProperty id) throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM checklists WHERE task_id = ?";
        List<Checklist> checklists = new ArrayList<>();
        try (ResultSet resultSet = SQLUtil.execute(sql, id.get())) {
            while (resultSet.next()) {
                Checklist checklist = mapResultSetToChecklistDTO(resultSet);
                checklists.add(checklist);
            }
        }
        return checklists;

    }

    @Override
    public boolean deleteChecklist(LongProperty id) throws SQLException, ClassNotFoundException {
        String sql = "DELETE FROM checklists WHERE id = ?";
        return SQLUtil.execute(sql, id.get());
    }

    @Override
    public boolean save(Checklist dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Checklist currentChecklist) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE checklists SET " +
                "task_id = ?, " +
                "name = ?, " +
                "description = ?, " +
                "status = ?, " +
                "priority = ?, " +
                "assigned_to = ?, " +
                "due_date = ?, " +
                "updated_at = ? " +
                "WHERE id = ?";

        return SQLUtil.execute(sql,
                currentChecklist.taskIdProperty().get(),
                currentChecklist.nameProperty().get(),
                currentChecklist.descriptionProperty().get(),
                currentChecklist.statusProperty().get().toString(),
                currentChecklist.priorityProperty().get().toString(),
                currentChecklist.assignedToProperty().get(),
                currentChecklist.dueDateProperty().get(),
                LocalDateTime.now(),
                currentChecklist.idProperty().get());
    }

    @Override
    public boolean insert(Checklist checklistDTO) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO checklists (task_id, name, description, status, priority, assigned_to, due_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime currentTimestamp = LocalDateTime.now();
        checklistDTO.createdAtProperty().set(currentTimestamp);
        checklistDTO.updatedAtProperty().set(currentTimestamp);

        return SQLUtil.execute(
                sql,
                checklistDTO.taskIdProperty().get(),
                checklistDTO.nameProperty().get(),
                checklistDTO.descriptionProperty().get(),
                checklistDTO.statusProperty().get().name(),
                checklistDTO.priorityProperty().get().name(),
                checklistDTO.assignedToProperty().get(),
                checklistDTO.dueDateProperty().get(),
                checklistDTO.createdAtProperty().get(),
                checklistDTO.updatedAtProperty().get()
        );
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Checklist> fetchAll() throws SQLException, ClassNotFoundException {
        String sql = "SELECT * FROM checklists";
        List<Checklist> checklists = new ArrayList<>();
        try (ResultSet resultSet = SQLUtil.execute(sql)) {
            while (resultSet.next()) {
                Checklist checklist = mapResultSetToChecklistDTO(resultSet);
                checklists.add(checklist);
            }
        }
        return checklists;
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<Checklist> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
