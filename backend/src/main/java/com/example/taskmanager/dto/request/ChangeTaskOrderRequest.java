package com.example.taskmanager.dto.request;

import java.util.List;
import java.util.UUID;

public class ChangeTaskOrderRequest {
    private List<UUID> taskIds;

    public List<UUID> getTaskIds() {
        return taskIds;
    }

    public void setTaskIds(List<UUID> taskIds) {
        this.taskIds = taskIds;
    }
}
