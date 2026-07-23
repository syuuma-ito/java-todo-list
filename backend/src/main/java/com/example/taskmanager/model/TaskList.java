package com.example.taskmanager.model;

import com.example.taskmanager.exception.InvalidTaskException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class TaskList {
    private final UUID id;
    private String name;
    private String description;
    private final List<TaskBase> tasks = new ArrayList<>();
    private int order;

    public TaskList(UUID id, String name, String description, int order) {
        if (id == null) {
            throw new InvalidTaskException("idはnull不可です");
        }
        if (name == null) {
            throw new InvalidTaskException("nameはnull不可です");
        }
        if (description == null) {
            throw new InvalidTaskException("descriptionはnull不可です");
        }
        if (order < 0) {
            throw new InvalidTaskException("orderは0以上である必要があります");
        }

        this.id = id;
        this.name = name;
        this.description = description;
        this.order = order;
    }

    public void update(String name, String description) {
        if (name == null) {
            throw new InvalidTaskException("nameはnull不可です");
        }
        if (description == null) {
            throw new InvalidTaskException("descriptionはnull不可です");
        }

        this.name = name;
        this.description = description;
    }

    public void addTask(TaskBase task) {
        if (task == null) {
            throw new InvalidTaskException("taskはnull不可です");
        }
        if (tasks.stream().anyMatch(existingTask -> existingTask.getId().equals(task.getId()))) {
            throw new InvalidTaskException("同じidのタスクが既に存在します");
        }

        task.changeOrder(tasks.size());
        tasks.add(task);
    }

    public TaskBase findTask(UUID taskId) {
        if (taskId == null) {
            throw new InvalidTaskException("taskIdはnull不可です");
        }

        for (TaskBase task : tasks) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }

        return null;
    }

    public void removeTask(UUID taskId) {
        TaskBase removedTask = findTask(taskId);

        if (removedTask == null) {
            throw new InvalidTaskException("指定されたidのタスクが見つかりません");
        }

        int removedIndex = tasks.indexOf(removedTask);
        tasks.remove(removedIndex);

        // 削除されたとき、他のタスクのorderを更新する
        for (int remainIndex = removedIndex; remainIndex < tasks.size(); remainIndex++) {
            tasks.get(remainIndex).changeOrder(remainIndex);
        }
    }

    public void changeTaskOrder(List<UUID> taskIds) {
        if (taskIds == null) {
            throw new InvalidTaskException("taskIdsはnull不可です");
        }
        if (taskIds.stream().anyMatch(id -> id == null)) {
            throw new InvalidTaskException("taskIdsにnullは指定できません");
        }
        if (taskIds.size() != tasks.size()) {
            throw new InvalidTaskException("すべてのタスクのidを指定してください");
        }

        // 一度Setに変換して重複をチェックする
        Set<UUID> requestedIds = new HashSet<>(taskIds);
        if (requestedIds.size() != taskIds.size()) {
            throw new InvalidTaskException("taskIdsに重複があります");
        }

        Map<UUID, TaskBase> tasksById = new HashMap<>();
        for (TaskBase task : tasks) {
            tasksById.put(task.getId(), task);
        }

        if (!tasksById.keySet().equals(requestedIds)) {
            throw new InvalidTaskException("指定されたタスクのidが一致しません");
        }

        List<TaskBase> reorderedTasks = new ArrayList<>(tasks.size());
        for (int index = 0; index < taskIds.size(); index++) {
            TaskBase task = tasksById.get(taskIds.get(index));
            task.changeOrder(index);
            reorderedTasks.add(task);
        }
        tasks.clear();
        tasks.addAll(reorderedTasks);
    }

    public void changeOrder(int order) {
        if (order < 0) {
            throw new InvalidTaskException("orderは0以上である必要があります");
        }

        this.order = order;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<TaskBase> getTasks() {
        return Collections.unmodifiableList(new ArrayList<>(tasks));
    }

    public int getOrder() {
        return order;
    }
}
