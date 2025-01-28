package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.custom.IssueAttachmentDAO;
import com.dinidu.lk.pmt.dto.IssueAttachmentDTO;
import com.dinidu.lk.pmt.entity.IssueAttachment;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class AttachmentDAOImpl implements IssueAttachmentDAO {
    @Override
    public boolean deleteAttachment(Long attachmentId) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<IssueAttachmentDTO> getAttachments(Long issueId) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public boolean save(IssueAttachment dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(IssueAttachment dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean insert(IssueAttachment issueAttachment) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean delete(String idOrName) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public List<IssueAttachment> fetchAll() throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        return Map.of();
    }

    @Override
    public List<IssueAttachment> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        return List.of();
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }
}
