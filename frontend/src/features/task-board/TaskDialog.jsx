import { Check } from "lucide-react";
import { useState } from "react";

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { COLORS, PRIORITY, PRIORITY_LABEL, TASK_TYPE, TASK_TYPE_LABEL } from "@/features/task-board/constants";
import { toApiDateTime, toDateTimeLocal } from "@/features/task-board/dateTime";

import styles from "./DialogForms.module.css";

function TaskDialog({ open, task, onOpenChange, onSubmit }) {
    const [type, setType] = useState(task?.type ?? TASK_TYPE.NORMAL);
    const [name, setName] = useState(task?.name ?? "");
    const [details, setDetails] = useState(task?.details ?? "");
    const [color, setColor] = useState(task?.color ?? COLORS[0].value);
    const [priority, setPriority] = useState(task?.priority ?? PRIORITY.MEDIUM);
    const [deadline, setDeadline] = useState(toDateTimeLocal(task?.deadline));
    const [error, setError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const isEditing = Boolean(task);
    const isCustomColor = COLORS.every((option) => option.value !== color.toUpperCase());

    async function handleSubmit(event) {
        event.preventDefault();

        if (!name.trim()) {
            setError("タスク名を入力してください");
            return;
        }
        if (type === TASK_TYPE.DEADLINE && !deadline) {
            setError("期限を入力してください");
            return;
        }

        setIsSubmitting(true);
        setError("");

        try {
            await onSubmit({
                type,
                name: name.trim(),
                details: details.trim(),
                color,
                priority,
                completed: task?.completed ?? false,
                ...(type === TASK_TYPE.DEADLINE ? { deadline: toApiDateTime(deadline) } : {}),
            });
            onOpenChange(false);
        } catch (submitError) {
            setError(submitError.message);
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <Dialog
            open={open}
            onOpenChange={(nextOpen) => {
                if (!isSubmitting) {
                    onOpenChange(nextOpen);
                }
            }}
        >
            <DialogContent className={styles.taskDialog} showCloseButton={!isSubmitting}>
                <form className={styles.form} onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle>{isEditing ? "タスクを編集" : "タスクを追加"}</DialogTitle>
                    </DialogHeader>

                    <div className={styles.twoColumns}>
                        <div className={styles.field}>
                            <Label htmlFor="task-type">種類</Label>
                            {isEditing ? (
                                <div className={styles.readOnlyValue}>{TASK_TYPE_LABEL[type]}</div>
                            ) : (
                                <Select value={type} onValueChange={setType}>
                                    <SelectTrigger id="task-type" className={styles.select}>
                                        <SelectValue>{(value) => TASK_TYPE_LABEL[value]}</SelectValue>
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value={TASK_TYPE.NORMAL}>通常タスク</SelectItem>
                                        <SelectItem value={TASK_TYPE.DEADLINE}>期限付きタスク</SelectItem>
                                    </SelectContent>
                                </Select>
                            )}
                        </div>

                        <div className={styles.field}>
                            <Label htmlFor="task-priority">優先度</Label>
                            <Select value={priority} onValueChange={setPriority}>
                                <SelectTrigger id="task-priority" className={styles.select}>
                                    <SelectValue>{(value) => PRIORITY_LABEL[value]}</SelectValue>
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={PRIORITY.LOW}>低</SelectItem>
                                    <SelectItem value={PRIORITY.MEDIUM}>中</SelectItem>
                                    <SelectItem value={PRIORITY.HIGH}>高</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                    </div>

                    <div className={styles.field}>
                        <Label htmlFor="task-name">タスク名</Label>
                        <Input id="task-name" value={name} autoFocus maxLength={100} aria-invalid={Boolean(error && !name.trim())} onChange={(event) => setName(event.target.value)} />
                    </div>

                    <div className={styles.field}>
                        <Label htmlFor="task-details">詳細</Label>
                        <Textarea id="task-details" value={details} rows={3} maxLength={1000} onChange={(event) => setDetails(event.target.value)} />
                    </div>

                    {type === TASK_TYPE.DEADLINE && (
                        <div className={styles.field}>
                            <Label htmlFor="task-deadline">期限</Label>
                            <Input id="task-deadline" type="datetime-local" step="1" value={deadline} onChange={(event) => setDeadline(event.target.value)} />
                        </div>
                    )}

                    <fieldset className={styles.colorField}>
                        <legend>カラー</legend>
                        <div className={styles.colorOptions}>
                            {COLORS.map((option) => (
                                <button
                                    key={option.value}
                                    type="button"
                                    className={`${styles.colorButton} ${color.toUpperCase() === option.value ? styles.selectedColor : ""}`}
                                    style={{
                                        "--option-color": option.value,
                                    }}
                                    aria-pressed={color.toUpperCase() === option.value}
                                    onClick={() => setColor(option.value)}
                                >
                                    {color.toUpperCase() === option.value && <Check aria-hidden="true" />}
                                </button>
                            ))}

                            <label
                                className={`${styles.customColor} ${isCustomColor ? styles.selectedCustomColor : ""}`}
                                style={{ "--option-color": color }}
                                title="カスタムカラー"
                            >
                                <input type="color" value={color} onChange={(event) => setColor(event.target.value.toUpperCase())} />
                                <span>＋</span>
                            </label>
                        </div>
                    </fieldset>

                    {error && (
                        <p className={styles.error} role="alert">
                            {error}
                        </p>
                    )}

                    <DialogFooter>
                        <Button type="button" variant="outline" disabled={isSubmitting} onClick={() => onOpenChange(false)}>
                            キャンセル
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "保存中..." : "保存"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    );
}

export { TaskDialog };
