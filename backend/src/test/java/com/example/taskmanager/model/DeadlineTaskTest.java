package com.example.taskmanager.model;

import com.example.taskmanager.exception.InvalidTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeadlineTaskTest {
    private static final String COLOR = "#ffffff";

    private UUID id;
    private LocalDateTime deadline;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        deadline = LocalDateTime.of(2026, 12, 31, 23, 59, 59, 999_000_000);
    }

    @Test
    @DisplayName("期限付きタスクを作成すると、指定した値と期限を保持する")
    void constructor_shouldKeepGivenValues() {
        DeadlineTask task = new DeadlineTask(id, "期限付きタスク", "タスクの詳細", COLOR, Priority.HIGH, false, 0, deadline);

        assertEquals(id, task.getId());
        assertEquals(TaskType.DEADLINE, task.getType());
        assertEquals("期限付きタスク", task.getName());
        assertEquals("タスクの詳細", task.getDetails());
        assertEquals(COLOR, task.getColor());
        assertEquals(Priority.HIGH, task.getPriority());
        assertFalse(task.isCompleted());
        assertEquals(0, task.getOrder());
        assertEquals(deadline, task.getDeadline());
    }

    @Test
    @DisplayName("期限付きタスクを更新すると、データが変更される")
    void update_shouldChangeCommonFieldsAndDeadline() {
        DeadlineTask task = new DeadlineTask(id, "期限付きタスク", "タスクの詳細", COLOR, Priority.LOW, false, 0, deadline);
        LocalDateTime updatedDeadline = deadline.plusDays(1);

        task.update("期限付きタスク2", "タスクの詳細2", "#000000", Priority.HIGH, true, updatedDeadline);

        assertEquals("期限付きタスク2", task.getName());
        assertEquals("タスクの詳細2", task.getDetails());
        assertEquals("#000000", task.getColor());
        assertEquals(Priority.HIGH, task.getPriority());
        assertTrue(task.isCompleted());
        assertEquals(updatedDeadline, task.getDeadline());
    }

    @Test
    @DisplayName("deadlineがnullの場合、作成時に例外が発生する")
    void constructor_withNullDeadline_shouldThrowException() {
        assertThrows(InvalidTaskException.class,
                () -> new DeadlineTask(id, "期限付きタスク", "", COLOR, Priority.MEDIUM, false, 0, null));
    }

    @Test
    @DisplayName("deadlineがnullの更新では、元の値を維持する")
    void update_withNullDeadline_shouldKeepCurrentValues() {
        DeadlineTask task = new DeadlineTask(id, "期限付きタスク", "タスクの詳細", COLOR, Priority.LOW, false, 0, deadline);

        assertThrows(InvalidTaskException.class,
                () -> task.update("期限付きタスク2", "タスクの詳細2", "#000000", Priority.HIGH, true, null));

        assertEquals("期限付きタスク", task.getName());
        assertEquals("タスクの詳細", task.getDetails());
        assertEquals(COLOR, task.getColor());
        assertEquals(Priority.LOW, task.getPriority());
        assertFalse(task.isCompleted());
        assertEquals(deadline, task.getDeadline());
    }
}
