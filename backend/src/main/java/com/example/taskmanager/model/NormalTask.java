package com.example.taskmanager.model;

import java.util.UUID;

public final class NormalTask extends TaskBase {
    public NormalTask(UUID id, String name, String details, String color, Priority priority, boolean completed,
            int order) {
        super(id, TaskType.NORMAL, name, details, color, priority, completed, order);
    }

    public void update(String name, String details, String color, Priority priority, boolean completed) {
        updateFields(name, details, color, priority, completed);
    }
}
