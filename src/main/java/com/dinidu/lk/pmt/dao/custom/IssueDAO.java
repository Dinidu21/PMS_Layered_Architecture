package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Issue;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IssueDAO extends CrudDAO<Issue> {
    String getTaskNameById(Long taskId) throws SQLException ,ClassNotFoundException;
    boolean deleteIssue(Long id)throws SQLException,ClassNotFoundException;
    String getProjectNameById(String projectId) throws SQLException ,ClassNotFoundException;
    String getProjectIdByName(String projectName) throws SQLException ,ClassNotFoundException;
    Long getUserIdByName(String userName) throws SQLException ,ClassNotFoundException;
    ResultSet getActiveProjectNames() throws SQLException ,ClassNotFoundException;
    ResultSet getTasksByProject(String projectName) throws SQLException ,ClassNotFoundException;
    ResultSet getActiveMembers() throws SQLException ,ClassNotFoundException;



}
