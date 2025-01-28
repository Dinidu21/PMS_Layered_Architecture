package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.custom.ChecklistDAO;
import com.dinidu.lk.pmt.dto.ChecklistDTO;
import com.dinidu.lk.pmt.entity.Checklist;
import javafx.beans.property.LongProperty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class CheckListDAOImpl implements ChecklistDAO {
    @Override
    public ChecklistDTO mapResultSetToChecklistDTO(ResultSet resultSet) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public List<ChecklistDTO> getChecklistsByTaskId(LongProperty id) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public boolean deleteChecklist(LongProperty id) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean save(Checklist dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Checklist dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(Checklist checklist) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<Checklist> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
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
