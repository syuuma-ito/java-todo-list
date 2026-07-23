package com.example.taskmanager.service;

import com.example.taskmanager.dto.request.ChangeTaskCompletedRequest;
import com.example.taskmanager.dto.request.ChangeTaskListOrderRequest;
import com.example.taskmanager.dto.request.ChangeTaskOrderRequest;
import com.example.taskmanager.dto.request.CreateTaskListRequest;
import com.example.taskmanager.dto.request.CreateTaskRequest;
import com.example.taskmanager.dto.request.UpdateTaskListRequest;
import com.example.taskmanager.dto.request.UpdateTaskRequest;
import com.example.taskmanager.exception.InvalidTaskException;
import com.example.taskmanager.exception.NotFoundException;
import com.example.taskmanager.model.DeadlineTask;
import com.example.taskmanager.model.NormalTask;
import com.example.taskmanager.model.TaskBase;
import com.example.taskmanager.model.TaskList;
import com.example.taskmanager.model.TaskType;
import com.example.taskmanager.repository.TaskListRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    private final TaskListRepository taskListRepository;

    public TaskService(TaskListRepository taskListRepository) {
        this.taskListRepository = taskListRepository;
    }

    public List<TaskList> getAllTaskLists() {
        return taskListRepository.findAll();
    }

    public TaskList getTaskList(UUID taskListId) {
        TaskList taskList = taskListRepository.findById(taskListId);
        if (taskList == null) {
            throw new NotFoundException("タスクリストが見つかりません");
        }
        return taskList;
    }

    public TaskList createTaskList(CreateTaskListRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        TaskList taskList = new TaskList(
                UUID.randomUUID(),
                request.getName(),
                request.getDescription(),
                taskListRepository.findAll().size());
        return taskListRepository.save(taskList);
    }

    public TaskList updateTaskList(UUID taskListId, UpdateTaskListRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        TaskList taskList = getTaskList(taskListId);
        taskList.update(request.getName(), request.getDescription());
        return taskListRepository.save(taskList);
    }

    public UUID deleteTaskList(UUID taskListId) {
        getTaskList(taskListId);
        taskListRepository.deleteById(taskListId);

        List<TaskList> remainingTaskLists = taskListRepository.findAll();
        for (int index = 0; index < remainingTaskLists.size(); index++) {
            TaskList taskList = remainingTaskLists.get(index);
            taskList.changeOrder(index);
            taskListRepository.save(taskList);
        }

        return taskListId;
    }

    public void changeTaskListOrder(ChangeTaskListOrderRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        List<UUID> taskListIds = request.getTaskListIds();
        if (taskListIds == null) {
            throw new InvalidTaskException("taskListIdsはnull不可です");
        }
        if (taskListIds.stream().anyMatch(id -> id == null)) {
            throw new InvalidTaskException("taskListIdsにnullは指定できません");
        }

        List<TaskList> taskLists = taskListRepository.findAll();
        if (taskListIds.size() != taskLists.size()) {
            throw new InvalidTaskException("すべてのタスクリストのidを指定してください");
        }

        Set<UUID> requestedIds = new HashSet<>(taskListIds);
        if (requestedIds.size() != taskListIds.size()) {
            throw new InvalidTaskException("taskListIdsに重複があります");
        }

        Map<UUID, TaskList> taskListsById = new HashMap<>();
        for (TaskList taskList : taskLists) {
            taskListsById.put(taskList.getId(), taskList);
        }
        if (!taskListsById.keySet().equals(requestedIds)) {
            throw new InvalidTaskException("指定されたタスクリストのidが一致しません");
        }

        for (int index = 0; index < taskListIds.size(); index++) {
            TaskList taskList = taskListsById.get(taskListIds.get(index));
            taskList.changeOrder(index);
            taskListRepository.save(taskList);
        }
    }

    public TaskBase createTask(UUID taskListId, CreateTaskRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        if (request.getType() == null) {
            throw new InvalidTaskException("typeはnull不可です");
        }
        if (request.getCompleted() == null) {
            throw new InvalidTaskException("completedはnull不可です");
        }

        TaskList taskList = getTaskList(taskListId);
        TaskBase task;
        if (request.getType() == TaskType.NORMAL) {
            if (request.getDeadline() != null) {
                throw new InvalidTaskException("通常タスクにdeadlineは指定できません");
            }
            task = new NormalTask(
                    UUID.randomUUID(),
                    request.getName(),
                    request.getDetails(),
                    request.getColor(),
                    request.getPriority(),
                    request.getCompleted(),
                    taskList.getTasks().size());
        } else {
            task = new DeadlineTask(
                    UUID.randomUUID(),
                    request.getName(),
                    request.getDetails(),
                    request.getColor(),
                    request.getPriority(),
                    request.getCompleted(),
                    taskList.getTasks().size(),
                    request.getDeadline());
        }

        taskList.addTask(task);
        taskListRepository.save(taskList);
        return task;
    }

    public TaskBase getTask(UUID taskListId, UUID taskId) {
        TaskBase task = getTaskList(taskListId).findTask(taskId);
        if (task == null) {
            throw new NotFoundException("指定されたタスクが見つかりません");
        }
        return task;
    }

    public TaskBase updateTask(UUID taskListId, UUID taskId, UpdateTaskRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        if (request.getCompleted() == null) {
            throw new InvalidTaskException("completedはnull不可です");
        }

        TaskList taskList = getTaskList(taskListId);
        TaskBase task = taskList.findTask(taskId);
        if (task == null) {
            throw new NotFoundException("指定されたタスクが見つかりません");
        }

        if (task instanceof NormalTask) {
            if (request.getDeadline() != null) {
                throw new InvalidTaskException("通常タスクにdeadlineは指定できません");
            }
            NormalTask normalTask = (NormalTask) task;
            normalTask.update(
                    request.getName(),
                    request.getDetails(),
                    request.getColor(),
                    request.getPriority(),
                    request.getCompleted());
        } else if (task instanceof DeadlineTask) {
            DeadlineTask deadlineTask = (DeadlineTask) task;
            deadlineTask.update(
                    request.getName(),
                    request.getDetails(),
                    request.getColor(),
                    request.getPriority(),
                    request.getCompleted(),
                    request.getDeadline());
        } else {
            throw new IllegalStateException("対応していないタスク種別です");
        }

        taskListRepository.save(taskList);
        return task;
    }

    public UUID deleteTask(UUID taskListId, UUID taskId) {
        TaskList taskList = getTaskList(taskListId);
        if (taskList.findTask(taskId) == null) {
            throw new NotFoundException("指定されたタスクが見つかりません");
        }

        taskList.removeTask(taskId);
        taskListRepository.save(taskList);
        return taskId;
    }

    public void changeTaskOrder(UUID taskListId, ChangeTaskOrderRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        TaskList taskList = getTaskList(taskListId);
        taskList.changeTaskOrder(request.getTaskIds());
        taskListRepository.save(taskList);
    }

    public TaskBase changeTaskCompleted(
            UUID taskListId, UUID taskId, ChangeTaskCompletedRequest request) {
        if (request == null) {
            throw new InvalidTaskException("リクエストボディはnull不可です");
        }
        if (request.getCompleted() == null) {
            throw new InvalidTaskException("completedはnull不可です");
        }

        TaskList taskList = getTaskList(taskListId);
        TaskBase task = taskList.findTask(taskId);
        if (task == null) {
            throw new NotFoundException("指定されたタスクが見つかりません");
        }

        task.changeCompleted(request.getCompleted());
        taskListRepository.save(taskList);
        return task;
    }
}
