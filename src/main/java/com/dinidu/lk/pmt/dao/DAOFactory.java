package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.dao.custom.impl.ProjectsDAOImpl;
import com.dinidu.lk.pmt.dao.custom.impl.QueryDAOImpl;
import com.dinidu.lk.pmt.dao.custom.impl.UserDAOImpl;

public class DAOFactory {
    private static DAOFactory daoFactory;
    private DAOFactory(){
    }
    public static DAOFactory getDaoFactory(){
        return (daoFactory==null)?daoFactory
                =new DAOFactory():daoFactory;
    }

    public enum DAOTypes{
        USER,TASKS,PROJECTS,ISSUES,QUERY
    }

    public SuperDAO getDAO(DAOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserDAOImpl();
            case PROJECTS -> new ProjectsDAOImpl();
            case QUERY -> new QueryDAOImpl();
            default -> null;
        };
    }
}
