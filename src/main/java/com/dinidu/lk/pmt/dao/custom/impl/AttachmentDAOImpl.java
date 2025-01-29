package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.IssueAttachmentDAO;
import com.dinidu.lk.pmt.dto.IssueAttachmentDTO;
import com.dinidu.lk.pmt.entity.IssueAttachment;
import com.dinidu.lk.pmt.utils.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AttachmentDAOImpl implements IssueAttachmentDAO {
    private static final String INSERT_ATTACHMENT = "INSERT INTO issue_attachments (issue_id, file_name, uploaded_at, uploaded_by, file_url, file_data) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ATTACHMENTS = "SELECT * FROM issue_attachments WHERE issue_id = ?";
    private static final String DELETE_ATTACHMENT = "DELETE FROM issue_attachments WHERE id = ?";

    // working
    @Override
    public boolean deleteAttachment(Long attachmentId) throws SQLException, ClassNotFoundException {
        if (attachmentId == null) {
            System.out.println("WARNING deleteAttachment method in DAO : Attachment ID is null. Cannot delete.");
            return false;
        }
        // Check if the attachment exists
        String checkQuery = "SELECT COUNT(*) FROM issue_attachments WHERE id = ?";
        try (ResultSet rs = SQLUtil.execute(checkQuery, attachmentId)) {
            if (rs.next() && rs.getInt(1) == 0) {
                System.out.println("Attachment with ID " + attachmentId + " does not exist.");
                return false; // No attachment found, so deletion is not needed.
            }
        }

        System.out.println("Deleting attachment with ID: " + attachmentId);
        return CrudUtil.execute(DELETE_ATTACHMENT, attachmentId);
    }

    @Override
    public List<IssueAttachmentDTO> getAttachments(Long issueId) throws SQLException, ClassNotFoundException {
        List<IssueAttachmentDTO> attachments = new ArrayList<>();

        try (ResultSet rs = CrudUtil.execute(GET_ATTACHMENTS, issueId)) {
            while (rs.next()) {
                IssueAttachmentDTO attachment = new IssueAttachmentDTO();
                attachment.setId(rs.getLong("id"));  // Ensure this line is setting the ID
                attachment.setIssueId(rs.getLong("issue_id"));
                attachment.setFileName(rs.getString("file_name"));
                attachment.setUploadedAt(rs.getTimestamp("uploaded_at"));
                attachment.setUploadedBy(rs.getLong("uploaded_by"));
                attachment.setFileUrl(rs.getString("file_url"));
                attachment.setFileData(rs.getBytes("file_data"));

                System.out.println("✅ Retrieved attachment: ID=" + attachment.getId());

                attachments.add(attachment);
            }
        }
        return attachments;
    }

    @Override
    public boolean insert(IssueAttachment attachment) throws SQLException, ClassNotFoundException {
        boolean executed =  SQLUtil.execute(INSERT_ATTACHMENT,
                attachment.getIssueId(),
                attachment.getFileName(),
                attachment.getUploadedAt(),
                attachment.getUploadedBy(),
                attachment.getFileUrl(),
                attachment.getFileData());
        if (executed) {
            try (ResultSet generatedKeys = SQLUtil.execute("SELECT LAST_INSERT_ID()")) {
                if (generatedKeys.next()) {
                    attachment.setId(generatedKeys.getLong(1));
                }
            }
            return true;
        }
        return false;
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
