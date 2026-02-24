package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.demo.dto.CreateTaskRequest;
import com.example.demo.dto.PatchTaskRequest;
import com.example.demo.dto.TaskResponse;
import com.example.demo.dto.UpdateTaskRequest;
import com.example.demo.service.TaskService;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    // Helper to create a TaskResponse with audit fields
    private TaskResponse createTaskResponse(Integer id, String title, boolean completed, boolean isActive) {
        // Use dummy data for audit fields in tests
        OffsetDateTime now = OffsetDateTime.now();
        String createdBy = "SYSTEM";
        String lastModifiedBy = "SYSTEM";
        return new TaskResponse(id, title, completed, isActive, createdBy, now, lastModifiedBy, now);
    }

    // Helper to create a Pageable from custom params for mocking service calls
    private Pageable createPageable(int offset, int limit, String sortBy) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            String property = sortBy;
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortBy.startsWith("-")) {
                property = sortBy.substring(1);
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, property);
        } else {
            sort = Sort.by("id"); // Default sort
        }
        int page = offset / limit;
        return PageRequest.of(page, limit, sort);
    }

    @Test
    void createTask_shouldReturnCreatedTask_whenValidRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Test Task");
        request.setCompleted(false);

        TaskResponse expectedResponse = createTaskResponse(1, "New Test Task", false, true);
        when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("New Test Task"))
                .andExpect(jsonPath("$.data.completed").value(false))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.createdBy").value("SYSTEM"))
                .andExpect(jsonPath("$.data.createdDate").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void createTask_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("   ");

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.error.title").value("Title cannot be blank"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getAllTasks_noFilters_shouldReturnPageOfAllTasks() throws Exception {
        int offset = 0;
        int limit = 10;
        String sortBy = "id";
        Pageable pageableForService = createPageable(offset, limit, sortBy);

        List<TaskResponse> tasks = Arrays.asList(
                createTaskResponse(1, "Task 1", false, true),
                createTaskResponse(2, "Task 2", true, true));
        Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageableForService, tasks.size());
        when(taskService.getAllTasks(eq(null), eq(null), eq(pageableForService))).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                .param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit))
                .param("sortBy", sortBy)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.list[0].title").value("Task 1"))
                .andExpect(jsonPath("$.data.list[0].active").value(true))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getAllTasks_filterByTitle_shouldReturnPageOfFilteredTasks() throws Exception {
        int offset = 0;
        int limit = 10;
        String sortBy = "id";
        String titleFilter = "Filtered";
        Pageable pageableForService = createPageable(offset, limit, sortBy);

        List<TaskResponse> tasks = Arrays.asList(createTaskResponse(1, "Filtered Task", false, true));
        Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageableForService, tasks.size());
        when(taskService.getAllTasks(eq(titleFilter), eq(null), eq(pageableForService))).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                .param("title", titleFilter)
                .param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit))
                .param("sortBy", sortBy)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("Filtered Task"))
                .andExpect(jsonPath("$.data.list[0].active").value(true))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getAllTasks_filterByCompleted_shouldReturnPageOfFilteredTasks() throws Exception {
        int offset = 0;
        int limit = 10;
        String sortBy = "id";
        Boolean completedFilter = true;
        Pageable pageableForService = createPageable(offset, limit, sortBy);

        List<TaskResponse> tasks = Arrays.asList(createTaskResponse(2, "Completed Task", true, true));
        Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageableForService, tasks.size());
        when(taskService.getAllTasks(eq(null), eq(completedFilter), eq(pageableForService))).thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                .param("completed", String.valueOf(completedFilter))
                .param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit))
                .param("sortBy", sortBy)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("Completed Task"))
                .andExpect(jsonPath("$.data.list[0].active").value(true))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getAllTasks_filterByTitleAndCompleted_shouldReturnPageOfFilteredTasks() throws Exception {
        int offset = 0;
        int limit = 10;
        String sortBy = "id";
        String titleFilter = "Filtered";
        Boolean completedFilter = true;
        Pageable pageableForService = createPageable(offset, limit, sortBy);

        List<TaskResponse> tasks = Arrays.asList(createTaskResponse(3, "Filtered Completed Task", true, true));
        Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageableForService, tasks.size());
        when(taskService.getAllTasks(eq(titleFilter), eq(completedFilter), eq(pageableForService)))
                .thenReturn(taskPage);

        mockMvc.perform(get("/api/tasks")
                .param("title", titleFilter)
                .param("completed", String.valueOf(completedFilter))
                .param("offset", String.valueOf(offset))
                .param("limit", String.valueOf(limit))
                .param("sortBy", sortBy)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("Filtered Completed Task"))
                .andExpect(jsonPath("$.data.list[0].active").value(true))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getTaskById_shouldReturnTask_whenTaskExists() throws Exception {
        TaskResponse task = createTaskResponse(1, "Existing Task", false, true);
        when(taskService.getTaskById(1)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Existing Task"))
                .andExpect(jsonPath("$.data.completed").value(false))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.createdBy").value("SYSTEM"))
                .andExpect(jsonPath("$.data.createdDate").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void getTaskById_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: 99"))
                .when(taskService).getTaskById(99);

        mockMvc.perform(get("/api/tasks/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error.message").value("Task not found with id: 99"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTask_shouldReturnUpdatedTask_whenValidRequestAndTaskExists() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        request.setCompleted(true);

        TaskResponse expectedResponse = createTaskResponse(1, "Updated Title", true, true);
        when(taskService.updateTask(eq(1), any(UpdateTaskRequest.class))).thenReturn(expectedResponse);

        mockMvc.perform(put("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Updated Title"))
                .andExpect(jsonPath("$.data.completed").value(true))
                .andExpect(jsonPath("$.data.active").value(true))
                .andExpect(jsonPath("$.data.lastModifiedBy").value("SYSTEM"))
                .andExpect(jsonPath("$.data.lastModifiedDate").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void updateTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Title");
        request.setCompleted(true);

        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: 99"))
                .when(taskService).updateTask(eq(99), any(UpdateTaskRequest.class));

        mockMvc.perform(put("/api/tasks/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error.message").value("Task not found with id: 99"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateTask_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle(""); // Invalid title
        request.setCompleted(false);

        mockMvc.perform(put("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.error.title").value("Title cannot be blank"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void patchTask_shouldReturnUpdatedTask_whenValidRequestAndTaskExists() throws Exception {
        // Prepare request
        PatchTaskRequest request = new PatchTaskRequest();
        request.setTitle("Patched Title");
        request.setCompleted(true);

        // Prepare service response
        TaskResponse expectedResponse = createTaskResponse(1, "Patched Title", true, true);
        when(taskService.patchTask(eq(1), any(PatchTaskRequest.class))).thenReturn(expectedResponse);

        // Perform mockMvc request and assert
        mockMvc.perform(patch("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Patched Title"))
                .andExpect(jsonPath("$.data.completed").value(true))
                .andExpect(jsonPath("$.data.lastModifiedBy").value("SYSTEM"))
                .andExpect(jsonPath("$.data.lastModifiedDate").exists())
                .andExpect(jsonPath("$.error").doesNotExist());
    }

    @Test
    void patchTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        // Prepare request
        PatchTaskRequest request = new PatchTaskRequest();
        request.setTitle("Patched Title");

        // Mock service to throw NotFound
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: 99"))
                .when(taskService).patchTask(eq(99), any(PatchTaskRequest.class));

        // Perform mockMvc request and assert
        mockMvc.perform(patch("/api/tasks/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error.message").value("Task not found with id: 99"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void patchTask_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        // Prepare request with invalid data
        PatchTaskRequest request = new PatchTaskRequest();
        request.setTitle(""); // Invalid title (blank)

        // Perform mockMvc request and assert
        mockMvc.perform(patch("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode").value(400))
                .andExpect(jsonPath("$.error.title").value("Title must be between 1 and 255 characters if provided"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deleteTask_shouldReturnNoContent_whenTaskExists() throws Exception {
        doNothing().when(taskService).deleteTask(1);

        mockMvc.perform(delete("/api/tasks/{id}", 1))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void deleteTask_shouldMakeTaskUnfindable() throws Exception {
        // Mock the delete operation
        doNothing().when(taskService).deleteTask(1);
        
        // Mock the subsequent get operation to throw Not Found, which is what the service now does
        when(taskService.getTaskById(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Perform the delete
        mockMvc.perform(delete("/api/tasks/{id}", 1))
                .andExpect(status().isNoContent());

        // Attempt to get the deleted task
        mockMvc.perform(get("/api/tasks/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: 99"))
                .when(taskService).deleteTask(99);

        mockMvc.perform(delete("/api/tasks/{id}", 99))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.statusCode").value(404))
                .andExpect(jsonPath("$.error.message").value("Task not found with id: 99"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}
