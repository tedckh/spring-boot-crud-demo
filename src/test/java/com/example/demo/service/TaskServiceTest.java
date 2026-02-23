package com.example.demo.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.model.Task;
import com.example.demo.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task1;
    private Task task2;
    private CreateTaskRequest createTaskRequest;
    private UpdateTaskRequest updateTaskRequest;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        task1 = new Task(1, "Buy groceries", false);
        task2 = new Task(2, "Walk the dog", true);

        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("New Task");
        createTaskRequest.setCompleted(false);

        updateTaskRequest = new UpdateTaskRequest();
        updateTaskRequest.setTitle("Updated Task");
        updateTaskRequest.setCompleted(true);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllTasks_noFilters_shouldReturnPageOfAllTasks() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1, task2), pageable, 2);
        when(taskRepository.findAll(any(Pageable.class))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(task1.getTitle(), result.getContent().get(0).getTitle());
        assertEquals(task2.getTitle(), result.getContent().get(1).getTitle());
        verify(taskRepository, times(1)).findAll(pageable);
        verify(taskRepository, never()).findByCompleted(anyBoolean(), any(Pageable.class));
        verify(taskRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
        verify(taskRepository, never()).findByTitleContainingIgnoreCaseAndCompleted(anyString(), anyBoolean(),
                any(Pageable.class));
    }

    @Test
    void getAllTasks_filterByTitle_shouldReturnPageOfFilteredTasks() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1), pageable, 1);
        when(taskRepository.findByTitleContainingIgnoreCase(eq("groceries"), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks("groceries", null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(task1.getTitle(), result.getContent().get(0).getTitle());
        verify(taskRepository, times(1)).findByTitleContainingIgnoreCase("groceries", pageable);
        verify(taskRepository, never()).findAll(any(Pageable.class));
        verify(taskRepository, never()).findByCompleted(anyBoolean(), any(Pageable.class));
        verify(taskRepository, never()).findByTitleContainingIgnoreCaseAndCompleted(anyString(), anyBoolean(),
                any(Pageable.class));
    }

    @Test
    void getAllTasks_filterByCompleted_shouldReturnPageOfFilteredTasks() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task2), pageable, 1);
        when(taskRepository.findByCompleted(eq(true), any(Pageable.class))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks(null, true, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(task2.getTitle(), result.getContent().get(0).getTitle());
        verify(taskRepository, times(1)).findByCompleted(true, pageable);
        verify(taskRepository, never()).findAll(any(Pageable.class));
        verify(taskRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
        verify(taskRepository, never()).findByTitleContainingIgnoreCaseAndCompleted(anyString(), anyBoolean(),
                any(Pageable.class));
    }

    @Test
    void getAllTasks_filterByTitleAndCompleted_shouldReturnPageOfFilteredTasks() {
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(task1), pageable, 1);
        when(taskRepository.findByTitleContainingIgnoreCaseAndCompleted(eq("groceries"), eq(false),
                any(Pageable.class))).thenReturn(taskPage);

        Page<TaskResponse> result = taskService.getAllTasks("groceries", false, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(task1.getTitle(), result.getContent().get(0).getTitle());
        verify(taskRepository, times(1)).findByTitleContainingIgnoreCaseAndCompleted("groceries", false, pageable);
        verify(taskRepository, never()).findAll(any(Pageable.class));
        verify(taskRepository, never()).findByCompleted(anyBoolean(), any(Pageable.class));
        verify(taskRepository, never()).findByTitleContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void getTaskById_shouldReturnTaskResponse_whenTaskExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));

        TaskResponse result = taskService.getTaskById(1);

        assertNotNull(result);
        assertEquals(task1.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).findById(1);
    }

    @Test
    void getTaskById_shouldThrowResponseStatusException_whenTaskDoesNotExist() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.getTaskById(99));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Task not found with id: 99"));
        verify(taskRepository, times(1)).findById(99);
    }

    @Test
    void createTask_shouldReturnTaskResponse() {
        Task newTaskEntity = TaskMapper.toEntity(createTaskRequest);
        when(taskRepository.save(any(Task.class)))
                .thenReturn(new Task(3, newTaskEntity.getTitle(), newTaskEntity.isCompleted()));

        TaskResponse result = taskService.createTask(createTaskRequest);

        assertNotNull(result);
        assertEquals(createTaskRequest.getTitle(), result.getTitle());
        assertEquals(createTaskRequest.isCompleted(), result.isCompleted());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_shouldReturnUpdatedTaskResponse_whenTaskExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));
        Task updatedTaskEntity = new Task(1, updateTaskRequest.getTitle(), updateTaskRequest.isCompleted());
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTaskEntity);

        TaskResponse result = taskService.updateTask(1, updateTaskRequest);

        assertNotNull(result);
        assertEquals(updateTaskRequest.getTitle(), result.getTitle());
        assertEquals(updateTaskRequest.isCompleted(), result.isCompleted());
        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void updateTask_shouldThrowResponseStatusException_whenTaskDoesNotExist() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.updateTask(99, updateTaskRequest));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Task not found with id: 99"));
        verify(taskRepository, times(1)).findById(99);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_shouldCallRepositoryDelete_whenTaskExists() {
        when(taskRepository.findById(1)).thenReturn(Optional.of(task1));
        doNothing().when(taskRepository).delete(task1);

        taskService.deleteTask(1);

        verify(taskRepository, times(1)).findById(1);
        verify(taskRepository, times(1)).delete(task1);
    }

    @Test
    void deleteTask_shouldThrowResponseStatusException_whenTaskDoesNotExist() {
        when(taskRepository.findById(anyInt())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> taskService.deleteTask(99));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Task not found with id: 99"));
        verify(taskRepository, times(1)).findById(99);
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
