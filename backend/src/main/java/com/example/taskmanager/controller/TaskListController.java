package com.example.taskmanager.controller;

import com.example.taskmanager.dto.request.ChangeTaskCompletedRequest;
import com.example.taskmanager.dto.request.ChangeTaskListOrderRequest;
import com.example.taskmanager.dto.request.ChangeTaskOrderRequest;
import com.example.taskmanager.dto.request.CreateTaskListRequest;
import com.example.taskmanager.dto.request.CreateTaskRequest;
import com.example.taskmanager.dto.request.UpdateTaskListRequest;
import com.example.taskmanager.dto.request.UpdateTaskRequest;
import com.example.taskmanager.dto.response.ApiResponse;
import com.example.taskmanager.dto.response.DeletedResourceResponse;
import com.example.taskmanager.model.TaskBase;
import com.example.taskmanager.model.TaskList;
import com.example.taskmanager.service.TaskService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task-lists")
public class TaskListController {
    private final TaskService taskService;

    public TaskListController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskList>>> getAllTaskLists() {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, taskService.getAllTaskLists()));
    }

    @GetMapping("/{taskListId}")
    public ResponseEntity<ApiResponse<TaskList>> getTaskList(@PathVariable UUID taskListId) {
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, taskService.getTaskList(taskListId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TaskList>> createTaskList(@RequestBody CreateTaskListRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, taskService.createTaskList(request)));
    }

    @PutMapping("/{taskListId}")
    public ResponseEntity<ApiResponse<TaskList>> updateTaskList(
            @PathVariable UUID taskListId,
            @RequestBody UpdateTaskListRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, taskService.updateTaskList(taskListId, request)));
    }

    @DeleteMapping("/{taskListId}")
    public ResponseEntity<ApiResponse<DeletedResourceResponse>> deleteTaskList(
            @PathVariable UUID taskListId) {
        UUID deletedId = taskService.deleteTaskList(taskListId);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, new DeletedResourceResponse(deletedId)));
    }

    @PutMapping("/order")
    public ResponseEntity<ApiResponse<Void>> changeTaskListOrder(
            @RequestBody ChangeTaskListOrderRequest request) {
        taskService.changeTaskListOrder(request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, null));
    }

    @PostMapping("/{taskListId}/tasks")
    public ResponseEntity<ApiResponse<TaskBase>> createTask(
            @PathVariable UUID taskListId,
            @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED, taskService.createTask(taskListId, request)));
    }

    @PutMapping("/{taskListId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskBase>> updateTask(
            @PathVariable UUID taskListId,
            @PathVariable UUID taskId,
            @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, taskService.updateTask(taskListId, taskId, request)));
    }

    @GetMapping("/{taskListId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<TaskBase>> getTask(
            @PathVariable UUID taskListId,
            @PathVariable UUID taskId) {
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, taskService.getTask(taskListId, taskId)));
    }

    @DeleteMapping("/{taskListId}/tasks/{taskId}")
    public ResponseEntity<ApiResponse<DeletedResourceResponse>> deleteTask(
            @PathVariable UUID taskListId,
            @PathVariable UUID taskId) {
        UUID deletedId = taskService.deleteTask(taskListId, taskId);
        return ResponseEntity.ok(
                ApiResponse.success(HttpStatus.OK, new DeletedResourceResponse(deletedId)));
    }

    @PutMapping("/{taskListId}/tasks/order")
    public ResponseEntity<ApiResponse<Void>> changeTaskOrder(
            @PathVariable UUID taskListId,
            @RequestBody ChangeTaskOrderRequest request) {
        taskService.changeTaskOrder(taskListId, request);
        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, null));
    }

    @PostMapping("/{taskListId}/tasks/{taskId}/complete")
    public ResponseEntity<ApiResponse<TaskBase>> changeTaskCompleted(
            @PathVariable UUID taskListId,
            @PathVariable UUID taskId,
            @RequestBody ChangeTaskCompletedRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        HttpStatus.OK,
                        taskService.changeTaskCompleted(taskListId, taskId, request)));
    }
}
