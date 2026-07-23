import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { GripVertical, MoreHorizontal, Pencil, Trash2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { PRIORITY_LABEL, TASK_TYPE } from "@/features/task-board/constants";
import { formatDateTime, isOverdue } from "@/features/task-board/dateTime";

import styles from "./TaskCard.module.css";

function TaskCard({ task, disabled, onCompletedChange, onEdit, onDelete }) {
    const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({ id: task.id, disabled });
    const overdue = task.type === TASK_TYPE.DEADLINE && !task.completed && isOverdue(task.deadline);

    return (
        <article
            ref={setNodeRef}
            className={`${styles.card} ${isDragging ? styles.dragging : ""} ${task.completed ? styles.completed : ""}`}
            style={{
                transform: CSS.Transform.toString(transform),
                transition,
                "--task-color": task.color,
            }}
        >
            <div className={styles.layout}>
                <button type="button" className={styles.dragHandle} disabled={disabled} {...attributes} {...listeners}>
                    <GripVertical aria-hidden="true" />
                </button>

                <Checkbox
                    className={styles.completionCheckbox}
                    checked={task.completed}
                    disabled={disabled}
                    onCheckedChange={(checked) => onCompletedChange(task, checked === true)}
                />

                <h4 className={styles.title}>{task.name}</h4>

                <div className={styles.menu}>
                    <DropdownMenu>
                        <DropdownMenuTrigger render={<Button variant="ghost" size="icon-sm" />}>
                            <MoreHorizontal aria-hidden="true" />
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                            <DropdownMenuItem onClick={() => onEdit(task)}>
                                <Pencil aria-hidden="true" />
                                編集
                            </DropdownMenuItem>
                            <DropdownMenuItem variant="destructive" onClick={() => onDelete(task)}>
                                <Trash2 aria-hidden="true" />
                                削除
                            </DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                </div>

                <div className={styles.bottomRow}>
                    {task.details && <p className={styles.details}>{task.details}</p>}

                    <div className={styles.meta}>
                        <span className={styles.badge}>優先度 {PRIORITY_LABEL[task.priority]}</span>

                        {task.type === TASK_TYPE.DEADLINE && (
                            <span className={`${styles.badge} ${overdue ? styles.overdue : ""}`}>
                                {formatDateTime(task.deadline)}
                            </span>
                        )}
                    </div>
                </div>
            </div>
        </article>
    );
}

export { TaskCard };
