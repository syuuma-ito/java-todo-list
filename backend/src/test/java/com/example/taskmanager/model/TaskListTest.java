package com.example.taskmanager.model;

import com.example.taskmanager.exception.InvalidTaskException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskListTest {
    private static final String COLOR = "#ffffff";

    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskList = new TaskList(UUID.randomUUID(), "タスクリスト", "タスクリストの説明", 0);
    }

    @Test
    @DisplayName("タスクリストを作成すると、指定した値と空のタスク一覧を保持する")
    void constructor_shouldKeepGivenValuesAndEmptyTasks() {
        assertNotNull(taskList.getId());
        assertEquals("タスクリスト", taskList.getName());
        assertEquals("タスクリストの説明", taskList.getDescription());
        assertEquals(0, taskList.getOrder());
        assertNotNull(taskList.getTasks());
        assertTrue(taskList.getTasks().isEmpty());
    }

    @Test
    @DisplayName("タスクリストを更新すると、名前と説明が変更される")
    void update_shouldChangeNameAndDescription() {
        taskList.update("更新後の名前", "更新後の説明");

        assertEquals("更新後の名前", taskList.getName());
        assertEquals("更新後の説明", taskList.getDescription());
    }

    @Test
    @DisplayName("タスクを追加すると、末尾の表示順でタスクリストに追加される")
    void addTask_shouldAppendTaskWithLastOrder() {
        NormalTask firstTask = createNormalTask("タスク1", 10);
        NormalTask secondTask = createNormalTask("タスク2", 10);

        taskList.addTask(firstTask);
        taskList.addTask(secondTask);

        assertEquals(List.of(firstTask, secondTask), taskList.getTasks());
        assertEquals(0, firstTask.getOrder());
        assertEquals(1, secondTask.getOrder());
    }

    @Test
    @DisplayName("取得したタスク一覧を外部から変更できない")
    void getTasks_shouldReturnUnmodifiableList() {
        taskList.addTask(createNormalTask("タスク", 0));

        assertThrows(UnsupportedOperationException.class, () -> taskList.getTasks().add(createNormalTask("追加タスク", 1)));
    }

    @Test
    @DisplayName("登録済みのIDを指定すると、該当するタスクを取得できる")
    void findTask_withExistingId_shouldReturnTask() {
        NormalTask task = createNormalTask("タスク", 0);
        taskList.addTask(task);

        TaskBase foundTask = taskList.findTask(task.getId());

        assertSame(task, foundTask);
    }

    @Test
    @DisplayName("存在しないIDを指定すると、findTaskはnullを返す")
    void findTask_withUnknownId_shouldReturnNull() {
        TaskBase foundTask = taskList.findTask(UUID.randomUUID());

        assertNull(foundTask);
    }

    @Test
    @DisplayName("中央のタスクを削除すると、後続タスクの表示順が更新される")
    void removeTask_shouldRemoveTaskAndReassignFollowingOrders() {
        NormalTask task1 = createNormalTask("タスク1", 0);
        NormalTask task2 = createNormalTask("タスク2", 1);
        NormalTask task3 = createNormalTask("タスク3", 2);
        taskList.addTask(task1);
        taskList.addTask(task2);
        taskList.addTask(task3);

        taskList.removeTask(task2.getId());

        assertEquals(List.of(task1, task3), taskList.getTasks());
        assertEquals(0, task1.getOrder());
        assertEquals(1, task3.getOrder());
    }

    @Test
    @DisplayName("存在しないタスクを削除しようとすると、例外が発生する")
    void removeTask_withUnknownId_shouldThrowException() {
        assertThrows(InvalidTaskException.class, () -> taskList.removeTask(UUID.randomUUID()));
    }

    @Test
    @DisplayName("指定したID順にタスクと表示順を変更できる")
    void changeTaskOrder_shouldReorderTasksAndOrders() {
        NormalTask task1 = createNormalTask("タスク1", 0);
        NormalTask task2 = createNormalTask("タスク2", 1);
        NormalTask task3 = createNormalTask("タスク3", 2);
        taskList.addTask(task1);
        taskList.addTask(task2);
        taskList.addTask(task3);

        taskList.changeTaskOrder(List.of(task3.getId(), task1.getId(), task2.getId()));

        assertEquals(List.of(task3, task1, task2), taskList.getTasks());
        assertEquals(0, task3.getOrder());
        assertEquals(1, task1.getOrder());
        assertEquals(2, task2.getOrder());
    }

    @Test
    @DisplayName("空のタスクリストは空のID一覧で並び替えできる")
    void changeTaskOrder_whenNoTasks_shouldAcceptEmptyIds() {
        taskList.changeTaskOrder(List.of());

        assertTrue(taskList.getTasks().isEmpty());
    }

    @Test
    @DisplayName("不正なID一覧で並び替えようとすると、元の順序を維持する")
    void changeTaskOrder_withInvalidIds_shouldKeepCurrentOrder() {
        NormalTask task1 = createNormalTask("タスク1", 0);
        NormalTask task2 = createNormalTask("タスク2", 1);
        taskList.addTask(task1);
        taskList.addTask(task2);

        assertThrows(InvalidTaskException.class, () -> taskList.changeTaskOrder(null));
        assertThrows(InvalidTaskException.class,
                () -> taskList.changeTaskOrder(java.util.Arrays.asList(task1.getId(), null)));
        assertThrows(InvalidTaskException.class,
                () -> taskList.changeTaskOrder(List.of(task1.getId())));
        assertThrows(InvalidTaskException.class,
                () -> taskList.changeTaskOrder(List.of(task1.getId(), task1.getId())));
        assertThrows(InvalidTaskException.class,
                () -> taskList.changeTaskOrder(List.of(task1.getId(), UUID.randomUUID())));
        assertEquals(List.of(task1, task2), taskList.getTasks());
        assertEquals(0, task1.getOrder());
        assertEquals(1, task2.getOrder());
    }

    @Test
    @DisplayName("同じIDのタスクを追加しようとすると、例外が発生する")
    void addTask_withDuplicateId_shouldThrowException() {
        UUID taskId = UUID.randomUUID();
        NormalTask task1 = new NormalTask(
                taskId, "タスク1", "", COLOR, Priority.LOW, false, 0);
        NormalTask task2 = new NormalTask(
                taskId, "タスク2", "", COLOR, Priority.HIGH, false, 1);
        taskList.addTask(task1);

        assertThrows(InvalidTaskException.class, () -> taskList.addTask(task2));
        assertEquals(List.of(task1), taskList.getTasks());
    }

    @Test
    @DisplayName("nullのタスクを追加しようとすると、例外が発生する")
    void addTask_withNull_shouldThrowException() {
        assertThrows(InvalidTaskException.class, () -> taskList.addTask(null));
    }

    @Test
    @DisplayName("nullのIDでタスクを検索しようとすると、例外が発生する")
    void findTask_withNullId_shouldThrowException() {
        assertThrows(InvalidTaskException.class, () -> taskList.findTask(null));
    }

    @Test
    @DisplayName("表示順を0に変更できる")
    void changeOrder_withZero_shouldChangeOrder() {
        TaskList orderedTaskList = new TaskList(UUID.randomUUID(), "タスクリスト", "", 5);

        orderedTaskList.changeOrder(0);

        assertEquals(0, orderedTaskList.getOrder());
    }

    @Test
    @DisplayName("マイナスの表示順へ変更しようとすると、元の表示順を維持する")
    void changeOrder_withNegativeOrder_shouldKeepCurrentOrder() {
        TaskList orderedTaskList = new TaskList(UUID.randomUUID(), "タスクリスト", "", 2);

        assertThrows(InvalidTaskException.class, () -> orderedTaskList.changeOrder(-1));

        assertEquals(2, orderedTaskList.getOrder());
    }

    @Test
    @DisplayName("null不可の項目がnullの場合、タスクリストの作成時に例外が発生する")
    void constructor_withNullRequiredField_shouldThrowException() {
        assertThrows(InvalidTaskException.class,
                () -> new TaskList(null, "タスクリスト", "", 0));
        assertThrows(InvalidTaskException.class,
                () -> new TaskList(UUID.randomUUID(), null, "", 0));
        assertThrows(InvalidTaskException.class,
                () -> new TaskList(UUID.randomUUID(), "タスクリスト", null, 0));
    }

    @Test
    @DisplayName("orderがマイナスの場合、タスクリストの作成時に例外が発生する")
    void constructor_withNegativeOrder_shouldThrowException() {
        assertThrows(InvalidTaskException.class,
                () -> new TaskList(UUID.randomUUID(), "タスクリスト", "", -1));
    }

    @Test
    @DisplayName("nameとdescriptionが空文字の場合でもタスクリストを作成できる")
    void constructor_withEmptyNameAndDescription_shouldCreateTaskList() {
        TaskList emptyTaskList = new TaskList(UUID.randomUUID(), "", "", 0);

        assertEquals("", emptyTaskList.getName());
        assertEquals("", emptyTaskList.getDescription());
    }

    @Test
    @DisplayName("不正な値で更新しようとすると、元の値を維持する")
    void update_withInvalidValue_shouldKeepCurrentValues() {
        assertThrows(InvalidTaskException.class, () -> taskList.update(null, "更新後の説明"));
        assertThrows(InvalidTaskException.class, () -> taskList.update("更新後の名前", null));

        assertEquals("タスクリスト", taskList.getName());
        assertEquals("タスクリストの説明", taskList.getDescription());
    }

    private NormalTask createNormalTask(String name, int order) {
        return new NormalTask(UUID.randomUUID(), name, "", COLOR, Priority.MEDIUM, false, order);
    }
}
