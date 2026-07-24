import { DndContext, KeyboardSensor, PointerSensor, closestCenter, useSensor, useSensors } from "@dnd-kit/core";
import { SortableContext, arrayMove, sortableKeyboardCoordinates, useSortable, verticalListSortingStrategy } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { ChevronDown, GripVertical, MoreHorizontal, Pencil, Plus, Trash2 } from "lucide-react";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { TaskCard } from "@/features/task-board/TaskCard";

import styles from "./TaskListColumn.module.css";

function TaskGroup({ title, emptyMessage, tasks, collapsible = false, disabled, sensors, onDragEnd, onCompletedChange, onEditTask, onDeleteTask }) {
    const [isExpanded, setIsExpanded] = useState(!collapsible);

    return (
        <section className={styles.section}>
            {collapsible ? (
                <button
                    type="button"
                    className={styles.sectionToggle}
                    aria-expanded={isExpanded}
                    onClick={() => setIsExpanded((current) => !current)}
                >
                    <h3>{title}</h3>
                    <span>{tasks.length}件</span>
                    <ChevronDown className={isExpanded ? styles.expandedIcon : ""} aria-hidden="true" />
                </button>
            ) : (
                <div className={styles.sectionHeading}>
                    <h3>{title}</h3>
                    <span>{tasks.length}件</span>
                </div>
            )}

            {isExpanded && (tasks.length === 0 ? (
                <p className={styles.emptyMessage}>{emptyMessage}</p>
            ) : (
                <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={onDragEnd}>
                    <SortableContext items={tasks.map((task) => task.id)} strategy={verticalListSortingStrategy}>
                        <div className={styles.taskList}>
                            {tasks.map((task) => (
                                <TaskCard key={task.id} task={task} disabled={disabled} onCompletedChange={onCompletedChange} onEdit={onEditTask} onDelete={onDeleteTask} />
                            ))}
                        </div>
                    </SortableContext>
                </DndContext>
            ))}
        </section>
    );
}

function TaskListColumn({ taskList, disabled, onAddTask, onEditTaskList, onDeleteTaskList, onEditTask, onDeleteTask, onCompletedChange, onReorderTasks }) {
    const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: taskList.id, disabled });
    const sensors = useSensors(
        useSensor(PointerSensor, {
            activationConstraint: { distance: 6 },
        }),
        useSensor(KeyboardSensor, {
            coordinateGetter: sortableKeyboardCoordinates,
        }),
    );
    const tasks = [...taskList.tasks].sort((first, second) => first.order - second.order);
    const activeTasks = tasks.filter((task) => !task.completed);
    const completedTasks = tasks.filter((task) => task.completed);

    function handleTaskDragEnd(group, event) {
        if (!event.over || event.active.id === event.over.id) {
            return;
        }

        const oldIndex = group.findIndex((task) => task.id === event.active.id);
        const newIndex = group.findIndex((task) => task.id === event.over.id);
        const reorderedGroup = arrayMove(group, oldIndex, newIndex);
        let groupIndex = 0;
        const completed = group[0].completed;
        const reorderedTasks = tasks.map((task) => (task.completed === completed ? reorderedGroup[groupIndex++] : task));

        onReorderTasks(taskList.id, reorderedTasks);
    }

    return (
        <article
            ref={setNodeRef}
            className={`${styles.column} ${isDragging ? styles.dragging : ""}`}
            style={{
                transform: CSS.Transform.toString(transform),
                transition,
            }}
        >
            <header className={styles.header}>
                <button type="button" className={styles.dragHandle} disabled={disabled} {...attributes} {...listeners}>
                    <GripVertical aria-hidden="true" />
                </button>

                <div className={styles.heading}>
                    <h2>{taskList.name}</h2>
                    <span>{taskList.tasks.length}件</span>
                </div>

                <DropdownMenu>
                    <DropdownMenuTrigger render={<Button variant="ghost" size="icon" />}>
                        <MoreHorizontal aria-hidden="true" />
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuItem onClick={() => onEditTaskList(taskList)}>
                            <Pencil aria-hidden="true" />
                            編集
                        </DropdownMenuItem>
                        <DropdownMenuItem variant="destructive" onClick={() => onDeleteTaskList(taskList)}>
                            <Trash2 aria-hidden="true" />
                            削除
                        </DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </header>

            {taskList.description && <p className={styles.description}>{taskList.description}</p>}

            <Button variant="outline" className={styles.addButton} onClick={() => onAddTask(taskList)}>
                <Plus aria-hidden="true" />
                タスクを追加
            </Button>

            <div className={styles.scrollArea}>
                <TaskGroup
                    title="未完了"
                    emptyMessage="未完了のタスクはありません"
                    tasks={activeTasks}
                    disabled={disabled}
                    sensors={sensors}
                    onDragEnd={(event) => handleTaskDragEnd(activeTasks, event)}
                    onCompletedChange={(task, completed) => onCompletedChange(taskList.id, task.id, completed)}
                    onEditTask={(task) => onEditTask(taskList, task)}
                    onDeleteTask={(task) => onDeleteTask(taskList, task)}
                />

                <TaskGroup
                    title="完了したタスク"
                    emptyMessage="完了したタスクはありません"
                    tasks={completedTasks}
                    collapsible
                    disabled={disabled}
                    sensors={sensors}
                    onDragEnd={(event) => handleTaskDragEnd(completedTasks, event)}
                    onCompletedChange={(task, completed) => onCompletedChange(taskList.id, task.id, completed)}
                    onEditTask={(task) => onEditTask(taskList, task)}
                    onDeleteTask={(task) => onDeleteTask(taskList, task)}
                />
            </div>
        </article>
    );
}

export { TaskListColumn };
