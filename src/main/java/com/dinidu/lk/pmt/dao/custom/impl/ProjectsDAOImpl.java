package com.dinidu.lk.pmt.dao.custom.impl;

import com.dinidu.lk.pmt.dao.SQLUtil;
import com.dinidu.lk.pmt.dao.custom.ProjectDAO;
import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.entity.Project;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectsDAOImpl implements ProjectDAO {

    // Working
    @Override
    public String getProjectIdByName(String selectedProjectName) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT id FROM projects WHERE name = ?", selectedProjectName);
        if (resultSet.next()) {
            return resultSet.getString("id");
        } else {
            return null;
        }
    }

    // Working
    @Override
    public String getProjectIdByTaskId(long l) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT project_id FROM tasks WHERE id = ?", l);
        if (resultSet.next()) {
            return resultSet.getString("project_id");
        } else {
            return null;
        }
    }

    // Working
    @Override
    public Map<String, String> getAllNames() throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT id, name FROM projects");
        Map<String, String> projectNames = new HashMap<>();
        while (resultSet.next()) {
            String projectId = resultSet.getString("id");
            String projectName = resultSet.getString("name");
            projectNames.put(projectId, projectName);
        }
        return projectNames;
    }

    // Working
    @Override
    public String getProjectNameById(String projectId) throws SQLException, ClassNotFoundException {
        ResultSet resultSet = SQLUtil.execute("SELECT name FROM projects WHERE id = ?", projectId);
        if (resultSet.next()) {
            return resultSet.getString("name");
        }
        return null;
    }

    // Working
    @Override
    public boolean insert(Project project) throws SQLException, ClassNotFoundException {
        String sql = "INSERT INTO projects (id, name, description, start_date, end_date, status, priority, visibility, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return SQLUtil.execute(sql,
                project.getId(),
                project.getName(),
                project.getDescription(),
                new java.sql.Date(project.getStartDate().getTime()),
                project.getEndDate() != null ? new java.sql.Date(project.getEndDate().getTime()) : null,
                project.getStatus().name(),
                project.getPriority().name(),
                project.getVisibility().name(),
                project.getCreatedBy(),
                new java.sql.Timestamp(project.getCreatedAt().getTime()),
                new java.sql.Timestamp(project.getUpdatedAt().getTime())
        );

    }

    // Working
    @Override
    public List<Project> fetchAll() throws SQLException, ClassNotFoundException {
        List<ProjectDTO> projectDTOS = null;
        ResultSet rs = SQLUtil.execute("SELECT * FROM projects ORDER BY created_at DESC");
        while (rs.next()) {
            while (rs.next()) {
                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setId(rs.getString("id"));
                projectDTO.setName(rs.getString("name"));
                projectDTO.setDescription(rs.getString("description"));
                projectDTO.setStartDate(rs.getDate("start_date"));
                projectDTO.setEndDate(rs.getDate("end_date"));
                projectDTO.setStatus(ProjectStatus.valueOf(rs.getString("status")));
                projectDTO.setCreatedBy(rs.getLong("created_by"));
                projectDTO.setCreatedAt(rs.getTimestamp("created_at"));
                projectDTO.setUpdatedAt(rs.getTimestamp("updated_at"));
                projectDTO.setPriority(ProjectPriority.valueOf(rs.getString("priority")));
                projectDTO.setVisibility(ProjectVisibility.valueOf(rs.getString("visibility")));

                if (projectDTOS == null) {
                    projectDTOS = new ArrayList<>();
                }
                projectDTOS.add(projectDTO);
            }
        }

        // Convert List<ProjectDTO> to List<Project>

        assert projectDTOS != null;
        return projectDTOS.stream()
                .map(this::convertToEntity)
                .collect(Collectors.toList());
    }

    // Working
    @Override
    public List<Project> searchByName(String searchQuery) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM projects WHERE name LIKE ? ORDER BY created_at DESC", searchQuery + "%");
        List<Project> projects = new ArrayList<>();
        while (rs.next()) {
            Project project = new Project();
            project.setId(rs.getString("id"));
            project.setName(rs.getString("name"));
            project.setDescription(rs.getString("description"));
            project.setStartDate(rs.getDate("start_date"));
            project.setEndDate(rs.getDate("end_date"));
            project.setStatus(ProjectStatus.valueOf(rs.getString("status")));
            project.setCreatedBy(rs.getLong("created_by"));
            project.setCreatedAt(rs.getTimestamp("created_at"));
            project.setUpdatedAt(rs.getTimestamp("updated_at"));
            projects.add(project);
        }
        return projects;
    }

    // Working
    @Override
    public void updateProject(Project project) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE projects SET ");
        boolean firstField = true;

        if (project.getName() != null) {
            sql.append("name = ?");
            firstField = false;
        }
        if (project.getDescription() != null) {
            if (!firstField) sql.append(", ");
            sql.append("description = ?");
            firstField = false;
        }
        if (project.getStatus() != null) {
            if (!firstField) sql.append(", ");
            sql.append("status = ?");
            firstField = false;
        }
        if (project.getPriority() != null) {
            if (!firstField) sql.append(", ");
            sql.append("priority = ?");
            firstField = false;
        }
        if (project.getVisibility() != null) {
            if (!firstField) sql.append(", ");
            sql.append("visibility = ?");
            firstField = false;
        }
        if (project.getEndDate() != null) {
            if (!firstField) sql.append(", ");
            sql.append("end_date = ?");
        }

        sql.append(", updated_at = ? WHERE id = ?");

        Connection conn;
        PreparedStatement pstmt;

        conn = DBConnection.getInstance().getConnection();

        pstmt = conn.prepareStatement(sql.toString());

        int index = 1;
        if (project.getName() != null) {
            pstmt.setString(index++, project.getName());
        }
        if (project.getDescription() != null) {
            pstmt.setString(index++, project.getDescription());
        }
        if (project.getStatus() != null) {
            pstmt.setString(index++, project.getStatus().name());
        }
        if (project.getPriority() != null) {
            pstmt.setString(index++, project.getPriority().name());
        }
        if (project.getVisibility() != null) {
            pstmt.setString(index++, project.getVisibility().name());
        }
        if (project.getEndDate() != null) {
            pstmt.setDate(index++, new java.sql.Date(project.getEndDate().getTime()));
        }
        pstmt.setTimestamp(index++, new java.sql.Timestamp(System.currentTimeMillis()));
        pstmt.setString(index, project.getId());

        pstmt.executeUpdate();
    }

    // Working
    @Override
    public List<ProjectDTO> getAllProjects() throws SQLException {
        String sql = "SELECT * FROM projects ORDER BY created_at DESC";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        connection = DBConnection.getInstance().getConnection();
        if (connection == null || connection.isClosed()) {
            System.out.println("Database connection is closed!");
            return null;
        }
        pstmt = connection.prepareStatement(sql);
        rs = pstmt.executeQuery();
        List<ProjectDTO> projectList = null;
        while (rs.next()) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setId(rs.getString("id"));
            projectDTO.setName(rs.getString("name"));
            projectDTO.setDescription(rs.getString("description"));
            projectDTO.setStartDate(rs.getDate("start_date"));
            projectDTO.setEndDate(rs.getDate("end_date"));
            projectDTO.setStatus(ProjectStatus.valueOf(rs.getString("status")));
            projectDTO.setCreatedBy(rs.getLong("created_by"));
            projectDTO.setCreatedAt(rs.getTimestamp("created_at"));
            projectDTO.setUpdatedAt(rs.getTimestamp("updated_at"));
            projectDTO.setPriority(ProjectPriority.valueOf(rs.getString("priority")));
            projectDTO.setVisibility(ProjectVisibility.valueOf(rs.getString("visibility")));

            if (projectList == null) {
                projectList = new ArrayList<>();
            }
            projectList.add(projectDTO);
        }

        return projectList;
    }

    // Working
    @Override
    public List<ProjectDTO> getProjectById(String projectId) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM projects WHERE id = ?", projectId);
        List<ProjectDTO> projectList = new ArrayList<>();
        while (rs.next()) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setId(rs.getString("id"));
            projectDTO.setName(rs.getString("name"));
            projectDTO.setDescription(rs.getString("description"));
            projectDTO.setStartDate(rs.getDate("start_date"));
            projectDTO.setEndDate(rs.getDate("end_date"));
            projectDTO.setStatus(ProjectStatus.valueOf(rs.getString("status")));
            projectDTO.setPriority(ProjectPriority.valueOf(rs.getString("priority")));
            projectDTO.setCreatedBy(rs.getLong("created_by"));
            projectDTO.setCreatedAt(rs.getTimestamp("created_at"));
            projectDTO.setUpdatedAt(rs.getTimestamp("updated_at"));
            projectList.add(projectDTO);
        }
        return projectList;
    }

    // Working
    @Override
    public Optional<ProjectDTO> isProjectIdTaken(String projectId) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM projects WHERE id = ?", projectId);
        if (rs.next()) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setId(rs.getString("id"));
            return Optional.of(projectDTO);
        }
        return Optional.empty();
    }

    // Working
    @Override
    public boolean delete(String projectId) throws SQLException, ClassNotFoundException {
        return SQLUtil.execute("DELETE FROM projects WHERE id = ?",projectId);
    }

    // Working
    @Override
    public List<ProjectDTO> getProjectsByStatus(ProjectStatus projectStatus) throws SQLException, ClassNotFoundException {
        ResultSet rs = SQLUtil.execute("SELECT * FROM projects WHERE status = ?", projectStatus.toString());
        List<ProjectDTO> projectList = new ArrayList<>();
        while (rs.next()) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setId(rs.getString("id"));
            projectDTO.setName(rs.getString("name"));
            projectDTO.setDescription(rs.getString("description"));
            projectDTO.setStartDate(rs.getDate("start_date"));
            projectDTO.setEndDate(rs.getDate("end_date"));
            projectDTO.setStatus(ProjectStatus.valueOf(rs.getString("status")));
            projectDTO.setPriority(ProjectPriority.valueOf(rs.getString("priority")));
            projectDTO.setCreatedBy(rs.getLong("created_by"));
            projectDTO.setCreatedAt(rs.getTimestamp("created_at"));
            projectDTO.setUpdatedAt(rs.getTimestamp("updated_at"));
            projectList.add(projectDTO);
        }
        return projectList;
    }


    @Override
    public ResultSet getActiveProjectNames() throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public ResultSet getTasksByProject(String projectName) throws SQLException, ClassNotFoundException {
        return null;
    }

    @Override
    public boolean save(Project dto) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean update(Project project) throws SQLException, ClassNotFoundException {
        return false;
    }

    @Override
    public Long getIdByName(String taskName) throws SQLException, ClassNotFoundException {
        return 0L;
    }

    private Project convertToEntity(ProjectDTO project) {
        Project projectForConvert = new Project();
        projectForConvert.setId(projectForConvert.getId());
        projectForConvert.setName(projectForConvert.getName());
        projectForConvert.setDescription(projectForConvert.getDescription());
        projectForConvert.setStartDate(projectForConvert.getStartDate());
        projectForConvert.setEndDate(projectForConvert.getEndDate());
        projectForConvert.setStatus(projectForConvert.getStatus());
        projectForConvert.setPriority(projectForConvert.getPriority());
        projectForConvert.setVisibility(projectForConvert.getVisibility());
        projectForConvert.setCreatedBy(projectForConvert.getCreatedBy());
        projectForConvert.setCreatedAt(projectForConvert.getCreatedAt());
        projectForConvert.setUpdatedAt(projectForConvert.getUpdatedAt());
        return projectForConvert;
    }
}
