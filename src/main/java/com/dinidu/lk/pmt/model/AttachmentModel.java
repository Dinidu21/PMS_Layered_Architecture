package com.dinidu.lk.pmt.model;


import com.dinidu.lk.pmt.dto.IssueAttachmentDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttachmentModel {

    private static final String INSERT_ATTACHMENT = "INSERT INTO issue_attachments (issue_id, file_name, uploaded_at, uploaded_by, file_url, file_data) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String GET_ATTACHMENTS = "SELECT * FROM issue_attachments WHERE issue_id = ?";
    private static final String DELETE_ATTACHMENT = "DELETE FROM issue_attachments WHERE id = ?";

/*    public void saveAttachment(IssueAttachmentDTO attachment) throws SQLException {
        Boolean isSaved = CrudUtil.execute(INSERT_ATTACHMENT, attachment.getIssueId(), attachment.getFileName(), attachment.getUploadedAt(), attachment.getUploadedBy(), attachment.getFileUrl(), attachment.getFileData());

        if (isSaved) {
            try (ResultSet generatedKeys = CrudUtil.execute("SELECT LAST_INSERT_ID()")) {
                if (generatedKeys.next()) {
                    attachment.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public List<IssueAttachmentDTO> getAttachments(Long issueId) throws SQLException {
        List<IssueAttachmentDTO> attachments = new ArrayList<>();

        try (ResultSet rs = CrudUtil.execute(GET_ATTACHMENTS, issueId)) {
            while (rs.next()) {
                IssueAttachmentDTO attachment = new IssueAttachmentDTO();
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

    public boolean deleteAttachment(Long attachmentId) {
        return CrudUtil.execute(DELETE_ATTACHMENT, attachmentId);
    }*/
}
