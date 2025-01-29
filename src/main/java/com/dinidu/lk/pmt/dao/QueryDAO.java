package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.dto.TaskReportData;
import com.dinidu.lk.pmt.dto.TeamAssignmentDTO;
import com.dinidu.lk.pmt.dto.UserDTO;
import com.dinidu.lk.pmt.entity.TeamAssignment;
import com.dinidu.lk.pmt.utils.userTypes.UserRole;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface QueryDAO extends SuperDAO{
    ///  =============== USER BASED QUERIES ===============
    UserRole getUserRoleByUsername(String username) throws SQLException,ClassNotFoundException;
    List<UserDTO> getAllActiveMembersNames() throws SQLException, ClassNotFoundException;
    Set<String> getUserPermissionsByRole(UserRole userRole) throws SQLException,ClassNotFoundException;
    List<UserDTO> getAllUsersWithRolesAndPermissions() throws SQLException,ClassNotFoundException;

    ///  =============== TEAM BASED QUERIES ===============
    List<String> getAllTeamMembersNamesByTask(String taskName)  throws SQLException,ClassNotFoundException;
    List<String> getTeamMemberEmailsByTask(long taskId) throws SQLException,ClassNotFoundException;
    List<TeamAssignment> getAssignmentsByTaskId(Long taskId) throws SQLException,ClassNotFoundException;

    ///   =============== REPORT BASED QUERIES ===============
    Map<Long, TaskReportData> getAllTaskReportData() throws SQLException,ClassNotFoundException;
}
