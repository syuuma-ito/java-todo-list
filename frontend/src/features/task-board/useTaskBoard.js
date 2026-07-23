import { useEffect, useState } from "react";

import { taskManagerApi } from "@/api/taskManagerApi";

function withOrders(items) {
    return items.map((item, order) => ({ ...item, order }));
}

function updateTasks(taskLists, taskListId, update) {
    return taskLists.map((taskList) =>
        taskList.id === taskListId
            ? { ...taskList, tasks: update(taskList.tasks) }
            : taskList,
    );
}

function replaceTask(tasks, replacement) {
    return tasks.map((task) =>
        task.id === replacement.id ? replacement : task,
    );
}

function useTaskBoard() {
    const [taskLists, setTaskLists] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [loadError, setLoadError] = useState("");
    const [notice, setNotice] = useState(null);
    const [isReordering, setIsReordering] = useState(false);

    async function loadTaskLists(signal) {
        setIsLoading(true);
        setLoadError("");

        try {
            const loadedTaskLists = await taskManagerApi.getTaskLists(signal);
            setTaskLists(loadedTaskLists);
        } catch (error) {
            if (error.name !== "AbortError") {
                setLoadError(error.message);
            }
        } finally {
            if (!signal?.aborted) {
                setIsLoading(false);
            }
        }
    }

    useEffect(() => {
        const controller = new AbortController();
        taskManagerApi
            .getTaskLists(controller.signal)
            .then((loadedTaskLists) => {
                setTaskLists(loadedTaskLists);
                setLoadError("");
            })
            .catch((error) => {
                if (error.name !== "AbortError") {
                    setLoadError(error.message);
                }
            })
            .finally(() => {
                if (!controller.signal.aborted) {
                    setIsLoading(false);
                }
            });
        return () => controller.abort();
    }, []);

    async function createTaskList(values) {
        const created = await taskManagerApi.createTaskList(values);
        setTaskLists((current) =>
            [...current, created].sort((first, second) => first.order - second.order),
        );
        setNotice({ type: "success", message: "タスクリストを追加しました" });
    }

    async function updateTaskList(taskListId, values) {
        const updated = await taskManagerApi.updateTaskList(taskListId, values);
        setTaskLists((current) =>
            current.map((taskList) =>
                taskList.id === taskListId ? updated : taskList,
            ),
        );
        setNotice({ type: "success", message: "タスクリストを更新しました" });
    }

    async function deleteTaskList(taskListId) {
        await taskManagerApi.deleteTaskList(taskListId);
        setTaskLists((current) =>
            withOrders(current.filter((taskList) => taskList.id !== taskListId)),
        );
        setNotice({ type: "success", message: "タスクリストを削除しました" });
    }

    async function createTask(taskListId, values) {
        const created = await taskManagerApi.createTask(taskListId, values);
        setTaskLists((current) =>
            updateTasks(current, taskListId, (tasks) =>
                [...tasks, created].sort(
                    (first, second) => first.order - second.order,
                ),
            ),
        );
        setNotice({ type: "success", message: "タスクを追加しました" });
    }

    async function updateTask(taskListId, taskId, values) {
        const updated = await taskManagerApi.updateTask(
            taskListId,
            taskId,
            values,
        );
        setTaskLists((current) =>
            updateTasks(current, taskListId, (tasks) =>
                replaceTask(tasks, updated),
            ),
        );
        setNotice({ type: "success", message: "タスクを更新しました" });
    }

    async function deleteTask(taskListId, taskId) {
        await taskManagerApi.deleteTask(taskListId, taskId);
        setTaskLists((current) =>
            updateTasks(current, taskListId, (tasks) =>
                withOrders(tasks.filter((task) => task.id !== taskId)),
            ),
        );
        setNotice({ type: "success", message: "タスクを削除しました" });
    }

    async function setTaskCompleted(taskListId, taskId, completed) {
        const previousTaskLists = taskLists;
        setTaskLists((current) =>
            updateTasks(current, taskListId, (tasks) =>
                tasks.map((task) =>
                    task.id === taskId ? { ...task, completed } : task,
                ),
            ),
        );

        try {
            const updated = await taskManagerApi.setTaskCompleted(
                taskListId,
                taskId,
                completed,
            );
            setTaskLists((current) =>
                updateTasks(current, taskListId, (tasks) =>
                    replaceTask(tasks, updated),
                ),
            );
        } catch (error) {
            setTaskLists(previousTaskLists);
            setNotice({ type: "error", message: error.message });
        }
    }

    async function reorderTaskLists(reorderedTaskLists) {
        const previousTaskLists = taskLists;
        const nextTaskLists = withOrders(reorderedTaskLists);
        setTaskLists(nextTaskLists);
        setIsReordering(true);

        try {
            await taskManagerApi.reorderTaskLists(
                nextTaskLists.map((taskList) => taskList.id),
            );
        } catch (error) {
            setTaskLists(previousTaskLists);
            setNotice({ type: "error", message: error.message });
        } finally {
            setIsReordering(false);
        }
    }

    async function reorderTasks(taskListId, reorderedTasks) {
        const previousTaskLists = taskLists;
        const nextTasks = withOrders(reorderedTasks);
        setTaskLists((current) =>
            updateTasks(current, taskListId, () => nextTasks),
        );
        setIsReordering(true);

        try {
            await taskManagerApi.reorderTasks(
                taskListId,
                nextTasks.map((task) => task.id),
            );
        } catch (error) {
            setTaskLists(previousTaskLists);
            setNotice({ type: "error", message: error.message });
        } finally {
            setIsReordering(false);
        }
    }

    return {
        taskLists,
        isLoading,
        loadError,
        notice,
        isReordering,
        reload: () => loadTaskLists(),
        dismissNotice: () => setNotice(null),
        createTaskList,
        updateTaskList,
        deleteTaskList,
        createTask,
        updateTask,
        deleteTask,
        setTaskCompleted,
        reorderTaskLists,
        reorderTasks,
    };
}

export { useTaskBoard };
