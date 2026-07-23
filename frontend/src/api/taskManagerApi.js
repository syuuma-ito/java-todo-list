import { request } from "@/api/client";

const taskManagerApi = {
    getTaskLists(signal) {
        return request("/api/task-lists", { signal });
    },

    createTaskList({ name, description }) {
        return request("/api/task-lists", {
            method: "POST",
            body: { name, description },
        });
    },

    updateTaskList(taskListId, { name, description }) {
        return request(`/api/task-lists/${taskListId}`, {
            method: "PUT",
            body: { name, description },
        });
    },

    deleteTaskList(taskListId) {
        return request(`/api/task-lists/${taskListId}`, {
            method: "DELETE",
        });
    },

    reorderTaskLists(taskListIds) {
        return request("/api/task-lists/order", {
            method: "PUT",
            body: { taskListIds },
        });
    },

    createTask(taskListId, task) {
        const body = {
            type: task.type,
            name: task.name,
            details: task.details,
            color: task.color,
            priority: task.priority,
            completed: task.completed,
        };

        if (task.type === "DEADLINE") {
            body.deadline = task.deadline;
        }

        return request(`/api/task-lists/${taskListId}/tasks`, {
            method: "POST",
            body,
        });
    },

    updateTask(taskListId, taskId, task) {
        const body = {
            name: task.name,
            details: task.details,
            color: task.color,
            priority: task.priority,
            completed: task.completed,
        };

        if (task.type === "DEADLINE") {
            body.deadline = task.deadline;
        }

        return request(`/api/task-lists/${taskListId}/tasks/${taskId}`, {
            method: "PUT",
            body,
        });
    },

    deleteTask(taskListId, taskId) {
        return request(`/api/task-lists/${taskListId}/tasks/${taskId}`, {
            method: "DELETE",
        });
    },

    reorderTasks(taskListId, taskIds) {
        return request(`/api/task-lists/${taskListId}/tasks/order`, {
            method: "PUT",
            body: { taskIds },
        });
    },

    setTaskCompleted(taskListId, taskId, completed) {
        return request(`/api/task-lists/${taskListId}/tasks/${taskId}/complete`, {
            method: "POST",
            body: { completed },
        });
    },
};

export { taskManagerApi };
