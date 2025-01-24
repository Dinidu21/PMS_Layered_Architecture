package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistPriority;
import com.dinidu.lk.pmt.utils.checklistTypes.ChecklistStatus;
import javafx.beans.property.LongProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class ChecklistModel {

    public static List<ChecklistDTO> getChecklistsByTaskId(LongProperty id) throws SQLException {
        String sql = "SELECT * FROM checklists WHERE task_id = ?";
        List<ChecklistDTO> checklists = new ArrayList<>();
        try (ResultSet resultSet = CrudUtil.execute(sql, id.get())) {
            while (resultSet.next()) {
                ChecklistDTO checklist = mapResultSetToChecklistDTO(resultSet);
                checklists.add(checklist);
            }
        }
        return checklists;
    }

    public static List<ChecklistDTO> getAllChecklists() throws SQLException {
        String sql = "SELECT * FROM checklists";
        List<ChecklistDTO> checklists = new ArrayList<>();
        try (ResultSet resultSet = CrudUtil.execute(sql)) {
            while (resultSet.next()) {
                ChecklistDTO checklist = mapResultSetToChecklistDTO(resultSet);
                checklists.add(checklist);
            }
        }
        return checklists;
    }

    public boolean insertChecklist(ChecklistDTO checklistDTO) {
        String sql = "INSERT INTO checklists (task_id, name, description, status, priority, assigned_to, due_date, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        LocalDateTime currentTimestamp = LocalDateTime.now();
        checklistDTO.createdAtProperty().set(currentTimestamp);
        checklistDTO.updatedAtProperty().set(currentTimestamp);

        return CrudUtil.execute(
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

    public boolean deleteChecklist(LongProperty id) {
        String sql = "DELETE FROM checklists WHERE id = ?";
        return CrudUtil.execute(sql, id.get());
    }

    private static ChecklistDTO mapResultSetToChecklistDTO(ResultSet resultSet) throws SQLException {
        ChecklistDTO checklist = new ChecklistDTO();
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

    public boolean updateChecklist(ChecklistDTO currentChecklist) {
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

        return CrudUtil.execute(sql,
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

}
