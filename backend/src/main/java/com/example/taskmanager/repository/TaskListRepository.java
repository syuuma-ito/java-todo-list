package com.example.taskmanager.repository;

import com.example.taskmanager.model.TaskList;
import java.util.List;
import java.util.UUID;

public interface TaskListRepository {
    TaskList save(TaskList taskList);

    List<TaskList> findAll();

    TaskList findById(UUID id);

    void deleteById(UUID id);
}
