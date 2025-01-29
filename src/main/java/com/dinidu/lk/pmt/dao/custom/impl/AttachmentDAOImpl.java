package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.IssueAttachmentDAO;
import com.dinidu.lk.pmt.entity.IssueAttachment;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttachmentDAOImpl implements IssueAttachmentDAO {
    private static final String INSERT_ATTACHMENT = "INSERT INTO issue_attachments (issue_id, file_name, uploaded_at, uploaded_by, file_url, file_data) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ATTACHMENTS = "SELECT * FROM issue_attachments WHERE issue_id = ?";
    private static final String DELETE_ATTACHMENT = "DELETE FROM issue_attachments WHERE id = ?";

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

    @Override
    public boolean deleteAttachment(Long attachmentId) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute(DELETE_ATTACHMENT, attachmentId);
    }

    @Override
    public List<IssueAttachment> getAttachments(Long issueId) throws SQLException, ClassNotFoundException {
        List<IssueAttachment> attachments = new ArrayList<>();

        try (ResultSet rs = SQLUtil.execute(GET_ATTACHMENTS, issueId)) {
            while (rs.next()) {
                IssueAttachment attachment = new IssueAttachment();
                attachment.setId(rs.getLong("id"));
                attachment.setIssueId(rs.getLong("issue_id"));
                attachment.setFileName(rs.getString("file_name"));
                attachment.setUploadedAt(rs.getTimestamp("uploaded_at"));
                attachment.setUploadedBy(rs.getLong("uploaded_by"));
                attachment.setFileUrl(rs.getString("file_url"));
                attachment.setFileData(rs.getBytes("file_data"));
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    @Override
    public void saveAttachment(IssueAttachment attachment) throws SQLException, ClassNotFoundException {
        Boolean isSaved = SQLUtil.execute(INSERT_ATTACHMENT, attachment.getIssueId(), attachment.getFileName(), attachment.getUploadedAt(), attachment.getUploadedBy(), attachment.getFileUrl(), attachment.getFileData());

        if (isSaved) {
            try (ResultSet generatedKeys = SQLUtil.execute("SELECT LAST_INSERT_ID()")) {
                if (generatedKeys.next()) {
                    attachment.setId(generatedKeys.getLong(1));
                    System.out.println("Attachment saved successfully with ID: " + attachment.getId());
                }
            }
        }
    }

    @Override
    public long getLastAddedAttachmentId(Long currentIssueId) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT id FROM issue_attachments WHERE issue_id = ?", currentIssueId);
        if (resultSet.next()) {
            return resultSet.getLong("id");
        }
        return 0;
    }
}
