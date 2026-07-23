import { Trash2 } from "lucide-react";
import { useState } from "react";

import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogMedia,
    AlertDialogTitle,
} from "@/components/ui/alert-dialog";

import styles from "./DialogForms.module.css";

function DeleteDialog({ open, target, onOpenChange, onConfirm }) {
    const [error, setError] = useState("");
    const [isDeleting, setIsDeleting] = useState(false);

    if (!target) {
        return null;
    }

    const isTaskList = target.kind === "task-list";

    async function handleDelete() {
        setIsDeleting(true);
        setError("");

        try {
            await onConfirm(target);
            onOpenChange(false);
        } catch (deleteError) {
            setError(deleteError.message);
        } finally {
            setIsDeleting(false);
        }
    }

    return (
        <AlertDialog
            open={open}
            onOpenChange={(nextOpen) => {
                if (!isDeleting) {
                    onOpenChange(nextOpen);
                }
            }}
        >
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogMedia className={styles.deleteIcon}>
                        <Trash2 aria-hidden="true" />
                    </AlertDialogMedia>
                    <AlertDialogTitle>{isTaskList ? "タスクリストを削除しますか" : "タスクを削除しますか"}</AlertDialogTitle>
                    <AlertDialogDescription>リスト内の{target.taskList.tasks.length}件のタスクも削除されます</AlertDialogDescription>
                </AlertDialogHeader>

                {error && (
                    <p className={styles.error} role="alert">
                        {error}
                    </p>
                )}

                <AlertDialogFooter>
                    <AlertDialogCancel disabled={isDeleting}>キャンセル</AlertDialogCancel>
                    <AlertDialogAction variant="destructive" disabled={isDeleting} onClick={handleDelete}>
                        {isDeleting ? "削除中..." : "削除"}
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
}

export { DeleteDialog };
