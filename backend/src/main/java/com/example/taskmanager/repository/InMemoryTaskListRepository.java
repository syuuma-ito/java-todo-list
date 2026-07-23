package com.example.taskmanager.repository;

import com.example.taskmanager.model.TaskList;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryTaskListRepository implements TaskListRepository {
    private final List<TaskList> taskLists = new ArrayList<>();

    @Override
    public TaskList save(TaskList taskList) {
        for (int index = 0; index < taskLists.size(); index++) {
            TaskList savedTaskList = taskLists.get(index);
            if (savedTaskList.getId().equals(taskList.getId())) {
                taskLists.set(index, taskList);
                return taskList;
            }
        }

        taskLists.add(taskList);
        return taskList;
    }

    @Override
    public List<TaskList> findAll() {
        List<TaskList> sortedTaskLists = new ArrayList<>(taskLists);
        sortedTaskLists.sort((first, second) -> Integer.compare(first.getOrder(), second.getOrder()));
        return sortedTaskLists;
    }

    @Override
    public TaskList findById(UUID id) {
        for (TaskList taskList : taskLists) {
            if (taskList.getId().equals(id)) {
                return taskList;
            }
        }
        return null;
    }

    @Override
    public void deleteById(UUID id) {
        for (int index = 0; index < taskLists.size(); index++) {
            if (taskLists.get(index).getId().equals(id)) {
                taskLists.remove(index);
                return;
            }
        }
    }
}
