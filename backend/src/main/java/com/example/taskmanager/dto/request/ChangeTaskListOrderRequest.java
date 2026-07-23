package com.example.taskmanager.dto.request;

import java.util.List;
import java.util.UUID;

public class ChangeTaskListOrderRequest {
    private List<UUID> taskListIds;

    public List<UUID> getTaskListIds() {
        return taskListIds;
    }

    public void setTaskListIds(List<UUID> taskListIds) {
        this.taskListIds = taskListIds;
    }
}
