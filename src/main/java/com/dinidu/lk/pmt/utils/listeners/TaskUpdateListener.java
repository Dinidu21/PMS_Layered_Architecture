package com.dinidu.lk.pmt.utils.listeners;

import com.dinidu.lk.pmt.dto.TaskDTO;

@FunctionalInterface
public interface TaskUpdateListener {
    void onTaskUpdated(TaskDTO updatedTask);
}