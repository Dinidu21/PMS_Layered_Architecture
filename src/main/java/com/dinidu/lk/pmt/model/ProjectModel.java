package com.dinidu.lk.pmt.model;

import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.ProjectDTO;
import com.dinidu.lk.pmt.utils.CrudUtil;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectPriority;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectStatus;
import com.dinidu.lk.pmt.utils.projectTypes.ProjectVisibility;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class ProjectModel {

    public static String getProjectIdByName(String selectedProjectName) {
        String sql = "SELECT id FROM projects WHERE name = ?";
        PreparedStatement pst = null;
        ResultSet rs = null;
        String projectId = null;

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            pst = connection.prepareStatement(sql);
            pst.setString(1, selectedProjectName);
            rs = pst.executeQuery();

            if (rs.next()) {
                projectId = rs.getString("id");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching project ID: " + e.getMessage());
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException ignored) {
            }
            if (pst != null) try {
                pst.close();
            } catch (SQLException ignored) {
            }
        }
        return projectId;
    }

    public static Map<String, String> getAllProjectNames() throws SQLException {
        String sql = "SELECT id, name FROM projects";
        Map<String, String> projectNames;
        try (ResultSet resultSet = CrudUtil.execute(sql)) {

            projectNames = new HashMap<>();
            while (resultSet.next()) {
                String projectId = resultSet.getString("id");
                String projectName = resultSet.getString("name");
                projectNames.put(projectId, projectName);
            }
        }
        return projectNames;
    }

    public static String getProjectNameById(String projectId) throws SQLException {
        String sql = "SELECT name FROM projects WHERE id = ?";
        try (ResultSet rs = CrudUtil.execute(sql, projectId)) {
            if (rs.next()) {
                return rs.getString("name");
            }
        }
        return null;
    }

    public static String getProjectIdByTaskId(long l) throws SQLException {
        String sql = "SELECT project_id FROM tasks WHERE id = ?";
        try (ResultSet rs = CrudUtil.execute(sql, l)) {
            if (rs.next()) {
                return rs.getString("project_id");
            }
        }
        return null;
    }


    public boolean insertProject(ProjectDTO projectDTO) throws SQLException {
        String sql = "INSERT INTO projects (id, name, description, start_date, end_date, status, priority, visibility, created_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, projectDTO.getId());
        pstmt.setString(2, projectDTO.getName());
        pstmt.setString(3, projectDTO.getDescription());
        pstmt.setDate(4, new java.sql.Date(projectDTO.getStartDate().getTime()));
        pstmt.setDate(5, projectDTO.getEndDate() != null ? new java.sql.Date(projectDTO.getEndDate().getTime()) : null);
        pstmt.setString(6, projectDTO.getStatus().name());
        pstmt.setString(7, projectDTO.getPriority().name());
        pstmt.setString(8, projectDTO.getVisibility().name());
        pstmt.setLong(9, projectDTO.getCreatedBy());
        pstmt.setTimestamp(10, new java.sql.Timestamp(projectDTO.getCreatedAt().getTime()));
        pstmt.setTimestamp(11, new java.sql.Timestamp(projectDTO.getUpdatedAt().getTime()));
        return pstmt.executeUpdate() > 0;
    }

    public static List<ProjectDTO> getAllProjects() {
        String sql = "SELECT * FROM projects ORDER BY created_at DESC";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
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
        } catch (SQLException e) {
            System.out.println("Error getting all projects: " + e.getMessage());
            throw new RuntimeException("Error getting all projects", e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<ProjectDTO> searchProjectsByName(String searchQuery) {
        String sql = "SELECT * FROM projects WHERE name LIKE ? ORDER BY created_at DESC";
        Connection connection;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ProjectDTO> projectList = new ArrayList<>();

        try {
            connection = DBConnection.getInstance().getConnection();
            pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, searchQuery + "%");
            rs = pstmt.executeQuery();

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
                projectList.add(projectDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error searching projects: " + e.getMessage());
            throw new RuntimeException("Error searching projects", e);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (pstmt != null) try {
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return projectList;
    }

    public void updateProject(ProjectDTO projectDTO) {
        StringBuilder sql = new StringBuilder("UPDATE projects SET ");
        boolean firstField = true;

        if (projectDTO.getName() != null) {
            sql.append("name = ?");
            firstField = false;
        }
        if (projectDTO.getDescription() != null) {
            if (!firstField) sql.append(", ");
            sql.append("description = ?");
            firstField = false;
        }
        if (projectDTO.getStatus() != null) {
            if (!firstField) sql.append(", ");
            sql.append("status = ?");
            firstField = false;
        }
        if (projectDTO.getPriority() != null) {
            if (!firstField) sql.append(", ");
            sql.append("priority = ?");
            firstField = false;
        }
        if (projectDTO.getVisibility() != null) {
            if (!firstField) sql.append(", ");
            sql.append("visibility = ?");
            firstField = false;
        }
        if (projectDTO.getEndDate() != null) {
            if (!firstField) sql.append(", ");
            sql.append("end_date = ?");
            firstField = false;
        }

        sql.append(", updated_at = ? WHERE id = ?");

        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();

            pstmt = conn.prepareStatement(sql.toString());

            int index = 1;
            if (projectDTO.getName() != null) {
                pstmt.setString(index++, projectDTO.getName());
            }
            if (projectDTO.getDescription() != null) {
                pstmt.setString(index++, projectDTO.getDescription());
            }
            if (projectDTO.getStatus() != null) {
                pstmt.setString(index++, projectDTO.getStatus().name());
            }
            if (projectDTO.getPriority() != null) {
                pstmt.setString(index++, projectDTO.getPriority().name());
            }
            if (projectDTO.getVisibility() != null) {
                pstmt.setString(index++, projectDTO.getVisibility().name());
            }
            if (projectDTO.getEndDate() != null) {
                pstmt.setDate(index++, new java.sql.Date(projectDTO.getEndDate().getTime()));
            }
            pstmt.setTimestamp(index++, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.setString(index, projectDTO.getId());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating project: " + e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteProject(String projectId) {
        String sql = "DELETE FROM projects WHERE id = ?";

        Connection conn;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting project: " + e.getMessage());
            throw new RuntimeException("Error deleting project", e);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<ProjectDTO> getProjectById(String projectId) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                String status = rs.getString("status");
                String priority = rs.getString("priority");
                String visibility = rs.getString("visibility");
                Date startDate = rs.getDate("start_date");
                Date endDate = rs.getDate("end_date");
                Timestamp createdAt = rs.getTimestamp("created_at");
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                String createdBy = rs.getString("created_by");

                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setId(id);
                projectDTO.setName(name);
                projectDTO.setDescription(description);
                projectDTO.setStatus(ProjectStatus.valueOf(status));
                projectDTO.setPriority(ProjectPriority.valueOf(priority));
                projectDTO.setVisibility(ProjectVisibility.valueOf(visibility));
                projectDTO.setStartDate(startDate);
                projectDTO.setEndDate(endDate);
                projectDTO.setCreatedAt(createdAt);
                projectDTO.setUpdatedAt(updatedAt);
                projectDTO.setCreatedBy(Long.valueOf(createdBy));
                return List.of(projectDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error getting project: " + e.getMessage());
            throw new RuntimeException("Error getting project", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<>();
    }

    public static Optional<ProjectDTO> isProjectIdTaken(String projectId) {
        String sql = "SELECT * FROM projects WHERE id = ?";
        Connection conn;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getInstance().getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, projectId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                ProjectDTO projectDTO = new ProjectDTO();
                projectDTO.setId(rs.getString("id"));
                return Optional.of(projectDTO);
            }
        } catch (SQLException e) {
            System.out.println("Error checking project ID: " + e.getMessage());
            throw new RuntimeException("Error checking project ID", e);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return Optional.empty();
    }

    public List<ProjectDTO> getProjectsByStatus(ProjectStatus projectStatus) throws SQLException {
        String sql = "SELECT * FROM projects WHERE status = ?";
        try (ResultSet rs = CrudUtil.execute(sql, projectStatus.toString())) {
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
    }
}
