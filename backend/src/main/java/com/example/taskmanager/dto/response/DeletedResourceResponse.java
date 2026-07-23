package com.example.taskmanager.dto.response;

import java.util.UUID;

public class DeletedResourceResponse {
    private final UUID id;

    public DeletedResourceResponse(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
