package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.entity.Checklist;
import javafx.beans.property.LongProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface ChecklistDAO extends CrudDAO<Checklist> {
    ChecklistDTO mapResultSetToChecklistDTO(ResultSet resultSet)throws SQLException,ClassNotFoundException;
    List<ChecklistDTO> getChecklistsByTaskId(LongProperty id) throws SQLException,ClassNotFoundException;
    boolean deleteChecklist(LongProperty id) throws SQLException,ClassNotFoundException;
}
