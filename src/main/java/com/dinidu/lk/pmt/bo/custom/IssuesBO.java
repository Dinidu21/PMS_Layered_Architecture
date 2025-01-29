package com.dinidu.lk.pmt.bo.custom;

import com.dinidu.lk.pmt.bo.SuperBO;
import com.dinidu.lk.pmt.dto.IssueDTO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface IssuesBO extends SuperBO {
    boolean createIssue(IssueDTO issueDTO) throws SQLException, ClassNotFoundException;

    String getProjectNameById(String projectId) throws SQLException, ClassNotFoundException;

    String getTaskNameById(long taskId) throws SQLException, ClassNotFoundException;

    List<IssueDTO> getAllIssues() throws SQLException, ClassNotFoundException;

    List<IssueDTO> searchIssuesByName(String selectedIssueName) throws SQLException, ClassNotFoundException;

    boolean updateIssue(IssueDTO currentIssue) throws SQLException, ClassNotFoundException;

    boolean deleteIssue(Long id) throws SQLException, ClassNotFoundException;

    String getProjectIdByName(String projectName) throws SQLException, ClassNotFoundException;

    Long getTaskIdByName(String taskName) throws SQLException, ClassNotFoundException;

    Long getUserIdByName(String memberName) throws SQLException, ClassNotFoundException;

    ResultSet getActiveProjectNames() throws SQLException, ClassNotFoundException;

    ResultSet getTasksByProject(String selectedProject) throws SQLException, ClassNotFoundException;

    ResultSet getActiveMembers() throws SQLException, ClassNotFoundException;
}
