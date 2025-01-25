package com.dinidu.lk.pmt.dao;

import com.dinidu.lk.pmt.dao.custom.impl.ProjectsImpl;
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
        USER,TASKS,PROJECTS,ISSUES
    }

    public SuperDAO getDAO(DAOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserDAOImpl();
            case PROJECTS -> new ProjectsImpl();
            default -> null;
        };
    }
}
