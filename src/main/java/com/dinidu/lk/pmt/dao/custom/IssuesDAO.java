package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.entity.Issue;

import java.sql.SQLException;

public interface IssuesDAO extends CrudDAO<Issue> {
    boolean deleteIssue(Long id)throws SQLException,ClassNotFoundException;
}
