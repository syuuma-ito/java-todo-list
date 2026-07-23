import { DndContext, KeyboardSensor, PointerSensor, closestCenter, useSensor, useSensors } from "@dnd-kit/core";
import { SortableContext, arrayMove, horizontalListSortingStrategy, sortableKeyboardCoordinates } from "@dnd-kit/sortable";
import { AlertCircle, CheckCircle2, ListTodo, Plus, RefreshCcw, X } from "lucide-react";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { Skeleton } from "@/components/ui/skeleton";
import { DeleteDialog } from "@/features/task-board/DeleteDialog";
import { TaskDialog } from "@/features/task-board/TaskDialog";
import { TaskListColumn } from "@/features/task-board/TaskListColumn";
import { TaskListDialog } from "@/features/task-board/TaskListDialog";
import { useTaskBoard } from "@/features/task-board/useTaskBoard";

import styles from "./TaskBoard.module.css";

function TaskBoard() {
    const board = useTaskBoard();
    const [taskListEditor, setTaskListEditor] = useState(null);
    const [taskEditor, setTaskEditor] = useState(null);
    const [deleteTarget, setDeleteTarget] = useState(null);
    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: { distance: 8 },
        }),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        }),
    );
    const totalTasks = board.taskLists.reduce((total, taskList) => total + taskList.tasks.length, 0);
    const completedTasks = board.taskLists.reduce((total, taskList) => total + taskList.tasks.filter((task) => task.completed).length, 0);

    function handleTaskListDragEnd(event) {
        if (!event.over || event.active.id === event.over.id) {
            return;
        }

        const oldIndex = board.taskLists.findIndex((taskList) => taskList.id === event.active.id);
        const newIndex = board.taskLists.findIndex((taskList) => taskList.id === event.over.id);

        board.reorderTaskLists(arrayMove(board.taskLists, oldIndex, newIndex));
    }

    async function submitTaskList(values) {
        if (taskListEditor?.taskList) {
            await board.updateTaskList(taskListEditor.taskList.id, values);
        } else {
            await board.createTaskList(values);
        }
    }

    async function submitTask(values) {
        if (taskEditor.task) {
            await board.updateTask(taskEditor.taskList.id, taskEditor.task.id, values);
        } else {
            await board.createTask(taskEditor.taskList.id, values);
        }
    }

    async function confirmDelete(target) {
        if (target.kind === "task-list") {
            await board.deleteTaskList(target.taskList.id);
        } else {
            await board.deleteTask(target.taskList.id, target.task.id);
        }
    }

    return (
        <div className={styles.page}>
            <header className={styles.appHeader}>
                <div className={styles.brand}>
                    <span className={styles.logo}>
                        <ListTodo aria-hidden="true" />
                    </span>
                </div>

                <div className={styles.summary}>
                    <span>{totalTasks}件のタスク</span>
                    <span>{completedTasks}件完了</span>
                </div>

                <Button size="lg" onClick={() => setTaskListEditor({ taskList: null })}>
                    <Plus aria-hidden="true" />
                    リストを追加
                </Button>
            </header>

            {board.notice && (
                <div className={`${styles.notice} ${board.notice.type === "error" ? styles.errorNotice : styles.successNotice}`} role={board.notice.type === "error" ? "alert" : "status"}>
                    {board.notice.type === "error" ? <AlertCircle aria-hidden="true" /> : <CheckCircle2 aria-hidden="true" />}
                    <span>{board.notice.message}</span>
                    <Button variant="ghost" size="icon-sm" onClick={board.dismissNotice}>
                        <X aria-hidden="true" />
                    </Button>
                </div>
            )}

            <main className={styles.main}>
                {board.isLoading ? (
                    <div className={styles.board}>
                        {[0, 1, 2].map((index) => (
                            <Skeleton key={index} className={styles.columnSkeleton} />
                        ))}
                    </div>
                ) : board.loadError ? (
                    <div className={styles.centerState} role="alert">
                        <AlertCircle aria-hidden="true" />
                        <h2>タスクリストを取得できませんでした</h2>
                        <p>{board.loadError}</p>
                        <Button variant="outline" onClick={board.reload}>
                            <RefreshCcw aria-hidden="true" />
                            もう一度試す
                        </Button>
                    </div>
                ) : board.taskLists.length === 0 ? (
                    <div className={styles.centerState}>
                        <ListTodo aria-hidden="true" />
                        <h2>タスクがありません</h2>

                        <Button onClick={() => setTaskListEditor({ taskList: null })}>
                            <Plus aria-hidden="true" />
                            リストを追加
                        </Button>
                    </div>
                ) : (
                    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleTaskListDragEnd}>
                        <SortableContext items={board.taskLists.map((taskList) => taskList.id)} strategy={horizontalListSortingStrategy}>
                            <div className={styles.board}>
                                {board.taskLists.map((taskList) => (
                                    <TaskListColumn
                                        key={taskList.id}
                                        taskList={taskList}
                                        disabled={board.isReordering}
                                        onAddTask={(selectedTaskList) =>
                                            setTaskEditor({
                                                taskList: selectedTaskList,
                                                task: null,
                                            })
                                        }
                                        onEditTaskList={(selectedTaskList) =>
                                            setTaskListEditor({
                                                taskList: selectedTaskList,
                                            })
                                        }
                                        onDeleteTaskList={(selectedTaskList) =>
                                            setDeleteTarget({
                                                kind: "task-list",
                                                taskList: selectedTaskList,
                                            })
                                        }
                                        onEditTask={(selectedTaskList, task) =>
                                            setTaskEditor({
                                                taskList: selectedTaskList,
                                                task,
                                            })
                                        }
                                        onDeleteTask={(selectedTaskList, task) =>
                                            setDeleteTarget({
                                                kind: "task",
                                                taskList: selectedTaskList,
                                                task,
                                            })
                                        }
                                        onCompletedChange={board.setTaskCompleted}
                                        onReorderTasks={board.reorderTasks}
                                    />
                                ))}
                            </div>
                        </SortableContext>
                    </DndContext>
                )}
            </main>

            {taskListEditor && (
                <TaskListDialog
                    open
                    taskList={taskListEditor.taskList}
                    onOpenChange={(open) => {
                        if (!open) {
                            setTaskListEditor(null);
                        }
                    }}
                    onSubmit={submitTaskList}
                />
            )}

            {taskEditor && (
                <TaskDialog
                    open
                    taskList={taskEditor.taskList}
                    task={taskEditor.task}
                    onOpenChange={(open) => {
                        if (!open) {
                            setTaskEditor(null);
                        }
                    }}
                    onSubmit={submitTask}
                />
            )}

            {deleteTarget && (
                <DeleteDialog
                    open
                    target={deleteTarget}
                    onOpenChange={(open) => {
                        if (!open) {
                            setDeleteTarget(null);
                        }
                    }}
                    onConfirm={confirmDelete}
                />
            )}
        </div>
    );
}

export { TaskBoard };
