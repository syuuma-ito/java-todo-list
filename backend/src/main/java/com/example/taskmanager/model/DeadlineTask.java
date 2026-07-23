package com.example.taskmanager.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.taskmanager.exception.InvalidTaskException;
import java.time.LocalDateTime;
import java.util.UUID;

public final class DeadlineTask extends TaskBase {
    private LocalDateTime deadline;

    public DeadlineTask(UUID id, String name, String details, String color, Priority priority, boolean completed,
            int order, LocalDateTime deadline) {
        super(id, TaskType.DEADLINE, name, details, color, priority, completed, order);
        if (deadline == null) {
            throw new InvalidTaskException("deadlineはnull不可です");
        }
        this.deadline = deadline;
    }

    public void update(String name, String details, String color, Priority priority, boolean completed,
            LocalDateTime deadline) {
        if (deadline == null) {
            throw new InvalidTaskException("deadlineはnull不可です");
        }
        updateFields(name, details, color, priority, completed);
        this.deadline = deadline;
    }

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    public LocalDateTime getDeadline() {
        return deadline;
    }
}
