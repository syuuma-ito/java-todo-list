package com.example.taskmanager.model;

import com.example.taskmanager.exception.InvalidTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NormalTaskTest {
    private static final String COLOR = "#ffffff";

    private UUID id;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
    }

    @Test
    @DisplayName("通常タスクを作成すると、指定した値を保持する")
    void constructor_shouldKeepGivenValues() {
        String name = "通常タスク";
        String details = "タスクの詳細";

        NormalTask task = new NormalTask(id, name, details, COLOR, Priority.HIGH, false, 0);

        assertEquals(id, task.getId());
        assertEquals(TaskType.NORMAL, task.getType());
        assertEquals(name, task.getName());
        assertEquals(details, task.getDetails());
        assertEquals(COLOR, task.getColor());
        assertEquals(Priority.HIGH, task.getPriority());
        assertFalse(task.isCompleted());
        assertEquals(0, task.getOrder());
    }

    @Test
    @DisplayName("通常タスクを更新すると、データが変更される")
    void update_shouldChangeCommonFields() {
        NormalTask task = new NormalTask(id, "通常タスク", "タスクの詳細", COLOR, Priority.LOW, false, 0);

        task.update("通常タスク2", "タスクの詳細2", "#000000", Priority.HIGH, true);

        assertEquals(id, task.getId());
        assertEquals(TaskType.NORMAL, task.getType());
        assertEquals("通常タスク2", task.getName());
        assertEquals("タスクの詳細2", task.getDetails());
        assertEquals("#000000", task.getColor());
        assertEquals(Priority.HIGH, task.getPriority());
        assertTrue(task.isCompleted());
        assertEquals(0, task.getOrder());
    }

    @Test
    @DisplayName("完了状態が変更できる")
    void changeCompleted_shouldChangeCompleted() {
        NormalTask task = new NormalTask(id, "通常タスク", "", COLOR, Priority.MEDIUM, false, 0);

        task.changeCompleted(true);

        assertTrue(task.isCompleted());
    }

    @Test
    @DisplayName("表示順を0に変更できる")
    void changeOrder_withZero_shouldChangeOrder() {
        NormalTask task = new NormalTask(id, "通常タスク", "", COLOR, Priority.MEDIUM, false, 5);

        task.changeOrder(0);

        assertEquals(0, task.getOrder());
    }

    @Test
    @DisplayName("nameとdetailsが空文字の場合でも作成できる")
    void constructor_withEmptyNameAndDetails_shouldCreateTask() {
        NormalTask task = new NormalTask(id, "", "", COLOR, Priority.LOW, false, 0);

        assertEquals("", task.getName());
        assertEquals("", task.getDetails());
    }

    @Test
    @DisplayName("null不可の項目がnullの場合、例外が発生する")
    void constructor_withNullRequiredField_shouldThrowException() {
        assertThrows(InvalidTaskException.class,
                () -> new NormalTask(null, "通常タスク", "", COLOR, Priority.LOW, false, 0));
        assertThrows(InvalidTaskException.class,
                () -> new NormalTask(id, null, "", COLOR, Priority.LOW, false, 0));
        assertThrows(InvalidTaskException.class,
                () -> new NormalTask(id, "通常タスク", null, COLOR, Priority.LOW, false, 0));
        assertThrows(InvalidTaskException.class,
                () -> new NormalTask(id, "通常タスク", "", null, Priority.LOW, false, 0));
        assertThrows(InvalidTaskException.class,
                () -> new NormalTask(id, "通常タスク", "", COLOR, null, false, 0));
    }

    @Test
    @DisplayName("colorが空文字の場合、例外が発生する")
    void constructor_withEmptyColor_shouldThrowException() {
        assertThrows(InvalidTaskException.class, () -> new NormalTask(id, "通常タスク", "", "", Priority.LOW, false, 0));
    }

    @Test
    @DisplayName("orderがマイナスの場合、例外が発生する")
    void constructor_withNegativeOrder_shouldThrowException() {
        assertThrows(InvalidTaskException.class, () -> new NormalTask(id, "通常タスク", "", COLOR, Priority.LOW, false, -1));
    }

    @Test
    @DisplayName("マイナスの表示順へ変更しようとすると、元の表示順を維持する")
    void changeOrder_withNegativeOrder_shouldKeepCurrentOrder() {
        NormalTask task = new NormalTask(id, "通常タスク", "", COLOR, Priority.LOW, false, 2);

        assertThrows(InvalidTaskException.class, () -> task.changeOrder(-1));

        assertEquals(2, task.getOrder());
    }

    @Test
    @DisplayName("不正な値で更新しようとすると、元の値を維持する")
    void update_withInvalidValue_shouldKeepCurrentValues() {
        NormalTask task = new NormalTask(id, "通常タスク", "タスクの詳細", COLOR, Priority.LOW, false, 0);

        assertThrows(InvalidTaskException.class, () -> task.update("更新後", "更新後の詳細", "", Priority.HIGH, true));

        assertEquals("通常タスク", task.getName());
        assertEquals("タスクの詳細", task.getDetails());
        assertEquals(COLOR, task.getColor());
        assertEquals(Priority.LOW, task.getPriority());
        assertFalse(task.isCompleted());
    }
}
