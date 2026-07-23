package com.example.taskmanager.model;

import com.example.taskmanager.exception.InvalidTaskException;
import java.util.UUID;

public abstract class TaskBase {
    private final UUID id;
    private final TaskType type;
    private String name;
    private String details;
    private String color;
    private Priority priority;
    private boolean completed;
    private int order;

    protected TaskBase(UUID id, TaskType type, String name, String details, String color, Priority priority,
            boolean completed, int order) {
        validateFields(name, details, color, priority);

        if (id == null) {
            throw new InvalidTaskException("idはnull不可です");
        }
        if (type == null) {
            throw new InvalidTaskException("typeはnull不可です");
        }
        if (order < 0) {
            throw new InvalidTaskException("orderは0以上である必要があります");
        }

        this.id = id;
        this.type = type;
        this.name = name;
        this.details = details;
        this.color = color;
        this.priority = priority;
        this.completed = completed;
        this.order = order;
    }

    protected final void updateFields(String name, String details, String color, Priority priority,
            boolean completed) {
        validateFields(name, details, color, priority);

        this.name = name;
        this.details = details;
        this.color = color;
        this.priority = priority;
        this.completed = completed;
    }

    public void changeOrder(int order) {
        if (order < 0) {
            throw new InvalidTaskException("orderは0以上である必要があります");
        }

        this.order = order;
    }

    public void changeCompleted(boolean completed) {
        this.completed = completed;
    }

    public UUID getId() {
        return id;
    }

    public TaskType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDetails() {
        return details;
    }

    public String getColor() {
        return color;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getOrder() {
        return order;
    }

    private static void validateFields(String name, String details, String color, Priority priority) {
        if (name == null) {
            throw new InvalidTaskException("nameはnull不可です");
        }
        if (details == null) {
            throw new InvalidTaskException("detailsはnull不可です");
        }
        if (color == null) {
            throw new InvalidTaskException("colorはnull不可です");
        }
        if (color.isEmpty()) {
            throw new InvalidTaskException("colorは空文字不可です");
        }
        if (priority == null) {
            throw new InvalidTaskException("priorityはnull不可です");
        }
    }
}
