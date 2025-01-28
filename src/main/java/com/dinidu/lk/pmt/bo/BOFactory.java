package com.dinidu.lk.pmt.bo;

import com.dinidu.lk.pmt.bo.custom.Impl.ProjectsBOImpl;
import com.dinidu.lk.pmt.bo.custom.Impl.TasksBOImpl;
import com.dinidu.lk.pmt.bo.custom.Impl.UserBOImpl;

public class BOFactory {
    private static BOFactory boFactory;
    private BOFactory() {}
    public static BOFactory getInstance() {
        return boFactory==null?boFactory=new BOFactory():boFactory;
    }


    public enum BOTypes{
        USER,TASKS,PROJECTS,ISSUES,QUERY
    }

    public SuperBO getBO(BOFactory.BOTypes daoTypes){
        return switch (daoTypes) {
            case USER -> new UserBOImpl();
            case PROJECTS -> new ProjectsBOImpl();
            case TASKS -> new TasksBOImpl();
            default -> null;
        };
    }
}
