package com.dinidu.lk.pmt.dao.custom;

import com.dinidu.lk.pmt.dao.CrudDAO;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.entity.Project;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ProjectDAO extends CrudDAO<Project> {

    // Have to Refactor All should be Entity
    List<ProjectDTO> getAllProjects() throws SQLException, ClassNotFoundException;
    List<ProjectDTO> getProjectsByStatus(ProjectStatus projectStatus) throws SQLException,ClassNotFoundException;
    List<ProjectDTO> getProjectById(String projectId) throws SQLException,ClassNotFoundException;
    Optional<ProjectDTO> isProjectIdTaken(String projectId) throws SQLException,ClassNotFoundException;

    void updateProject(Project project) throws SQLException,ClassNotFoundException;
    String getProjectIdByName(String selectedProjectName) throws SQLException,ClassNotFoundException;
    String getProjectIdByTaskId(long l) throws SQLException,ClassNotFoundException;
    String getProjectNameById(String projectId) throws SQLException,ClassNotFoundException;

    ResultSet getActiveProjectNames() throws SQLException,ClassNotFoundException;
    ResultSet getTasksByProject(String projectName)throws SQLException,ClassNotFoundException;

}
