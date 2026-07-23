import { useState } from "react";

import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";

import styles from "./DialogForms.module.css";

function TaskListDialog({ open, taskList, onOpenChange, onSubmit }) {
    const [name, setName] = useState(taskList?.name ?? "");
    const [description, setDescription] = useState(taskList?.description ?? "");
    const [error, setError] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const isEditing = Boolean(taskList);

    async function handleSubmit(event) {
        event.preventDefault();

        if (!name.trim()) {
            setError("リスト名を入力してください");
            return;
        }

        setIsSubmitting(true);
        setError("");

        try {
            await onSubmit({
                name: name.trim(),
                description: description.trim(),
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
            <DialogContent className={styles.dialog} showCloseButton={!isSubmitting}>
                <form className={styles.form} onSubmit={handleSubmit}>
                    <DialogHeader>
                        <DialogTitle>{isEditing ? "タスクリストを編集" : "タスクリストを追加"}</DialogTitle>
                    </DialogHeader>

                    <div className={styles.field}>
                        <Label htmlFor="task-list-name">リスト名</Label>
                        <Input id="task-list-name" value={name} autoFocus maxLength={100} aria-invalid={Boolean(error && !name.trim())} onChange={(event) => setName(event.target.value)} />
                    </div>

                    <div className={styles.field}>
                        <Label htmlFor="task-list-description">説明</Label>
                        <Textarea id="task-list-description" value={description} rows={3} maxLength={500} onChange={(event) => setDescription(event.target.value)} />
                    </div>

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

export { TaskListDialog };
