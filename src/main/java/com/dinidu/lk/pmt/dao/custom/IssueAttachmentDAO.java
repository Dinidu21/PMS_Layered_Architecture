package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.dto.IssueAttachmentDTO;
import com.dinidu.lk.pmt.entity.IssueAttachment;

import java.sql.SQLException;
import java.util.List;

public interface IssueAttachmentDAO extends CrudDAO<IssueAttachment> {
    boolean deleteAttachment(Long attachmentId)throws SQLException,ClassNotFoundException;
    List<IssueAttachmentDTO> getAttachments(Long issueId) throws SQLException,ClassNotFoundException;
}
