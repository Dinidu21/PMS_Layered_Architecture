package com.dinidu.lk.pmt.bo.custom.Impl;

import com.dinidu.lk.pmt.bo.custom.TasksBO;
import com.dinidu.lk.pmt.dao.DAOFactory;
import com.dinidu.lk.pmt.dao.custom.TasksDAO;
import com.dinidu.lk.pmt.db.DBConnection;
import com.dinidu.lk.pmt.dto.TasksDTO;
import com.dinidu.lk.pmt.entity.Tasks;
import com.dinidu.lk.pmt.utils.EntityDTOMapper;
import com.dinidu.lk.pmt.utils.customAlerts.CustomErrorAlert;
import com.dinidu.lk.pmt.utils.taskTypes.TaskStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class TasksBOImpl implements TasksBO {
    TasksDAO tasksDAO =
            (TasksDAO) DAOFactory.getDaoFactory().
                    getDAO(DAOFactory.DAOTypes.TASKS);

    @Override
    public List<TasksDTO> searchTasksByName(String searchQuery) throws SQLException, ClassNotFoundException {
        List<Tasks> taskEntities = tasksDAO.searchByName(searchQuery);
        return EntityDTOMapper.mapEntityListToDTOList(taskEntities, TasksDTO.class);
    }

    @Override
    public List<TasksDTO> getAllTasks() throws SQLException, ClassNotFoundException {
        List<Tasks> taskEntities = tasksDAO.fetchAll();
        return EntityDTOMapper.mapEntityListToDTOList(taskEntities, TasksDTO.class);
    }

    @Override
    public List<TasksDTO> getTaskByProjectId(String id) throws SQLException, ClassNotFoundException {
        List<Tasks> taskEntities = tasksDAO.getTaskByProjectId(id);
        return EntityDTOMapper.mapEntityListToDTOList(taskEntities, TasksDTO.class);
    }

    @Override
    public String getTaskNameById(Long taskId) throws SQLException, ClassNotFoundException {
        return tasksDAO.getTaskNameById(taskId);
    }

    @Override
    public void updateTask(TasksDTO currentTask) throws SQLException, ClassNotFoundException {
        tasksDAO.updateTask(EntityDTOMapper.mapDTOToEntity(currentTask, Tasks.class));
    }

    @Override
    public boolean deleteTask(String name) throws SQLException, ClassNotFoundException {
       return tasksDAO.delete(name);
    }

    @Override
    public List<TasksDTO> getTasksByStatus(TaskStatus taskStatus) throws SQLException, ClassNotFoundException {
        List<Tasks> taskEntities = tasksDAO.getByStatus(taskStatus);
        return EntityDTOMapper.mapEntityListToDTOList(taskEntities, TasksDTO.class);
    }

    @Override
    public List<TasksDTO> getTasksCurrentProjectByStatus(String projectId, TaskStatus taskStatus) throws SQLException, ClassNotFoundException {
        List<Tasks> entityList = tasksDAO.getTasksCurrentProjectByStatus(projectId, taskStatus);
        return EntityDTOMapper.mapEntityListToDTOList(entityList, TasksDTO.class);
    }

    @Override
    public boolean insertTask(TasksDTO tasksDTO) throws SQLException, ClassNotFoundException {
        Connection connection;
        connection = DBConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        boolean isSavedInsertedTask = false;
        try {
            boolean isSaved = tasksDAO.insertTask(EntityDTOMapper.mapDTOToEntity(tasksDTO, Tasks.class));

            if (isSaved) {
                System.out.println("Task saved successfully for project");

                // Retrieve the generated task ID
                long LAST_INSERTED_TASK_ID = tasksDAO.getLastInsertedTaskId();

                System.out.println("\n=== Last inserted task ID: " + LAST_INSERTED_TASK_ID);
                // Assign the task to the user
                boolean assignmentInserted = tasksDAO.insertAssignment(LAST_INSERTED_TASK_ID, tasksDTO.assignedToProperty().get());

                if (assignmentInserted) {
                    System.out.println("Assignment saved successfully for task");
                    connection.commit();
                    isSavedInsertedTask = true;
                } else {
                    connection.rollback();
                    System.out.println("Assignment not saved for task");
                }
            } else {
                connection.rollback();
                System.out.println("Task not saved for project");
            }
        } finally {
            connection.setAutoCommit(true);
        }

        if (!isSavedInsertedTask) {
            System.out.println("Task not saved for project");
            CustomErrorAlert.showAlert("Error", "Task not saved for project");
        }

        return isSavedInsertedTask;
    }
}
