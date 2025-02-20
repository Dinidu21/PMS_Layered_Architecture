package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.dao.custom.impl.*;

public class DAOFactory {
    private static DAOFactory daoFactory;
    private DAOFactory(){
    }
    public static DAOFactory getDaoFactory(){
        return (daoFactory==null)?daoFactory
                =new DAOFactory():daoFactory;
    }

    public enum DAOTypes{
        USER,TASKS,PROJECTS,ISSUES,QUERY,TIMESHEET,REPORTS,CHECKLISTS,ATTACHMENTS,TEAM_ASSIGNMENTS
    }

    public SuperDAO getDAO(DAOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserDAOImpl();
            case PROJECTS -> new ProjectsDAOImpl();
            case QUERY -> new QueryDAOImpl();
            case TASKS -> new TasksDAOImpl();
            case ISSUES -> new IssueDAOImpl();
            case REPORTS -> new ReportDAOImpl();
            case TIMESHEET -> new TimeSheetDAOImpl();
            case CHECKLISTS -> new CheckListDAOImpl();
            case ATTACHMENTS -> new AttachmentDAOImpl();
            case TEAM_ASSIGNMENTS -> new TeamAssignmentDAOImpl();
            default -> null;
        };
    }
}
