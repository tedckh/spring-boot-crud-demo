package com.example.demo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

        private TaskResponse createTaskResponse(Integer id, String title, boolean completed) {
                return new TaskResponse(id, title, completed);
        }

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
                        sort = Sort.by("id");
                }
                int page = offset / limit;
                return PageRequest.of(page, limit, sort);
        }

        @Test
        void createTask_shouldReturnCreatedTask_whenValidRequest() throws Exception {
                CreateTaskRequest request = new CreateTaskRequest();
                request.setTitle("New Test Task");
                request.setCompleted(false);

                TaskResponse expectedResponse = createTaskResponse(1, "New Test Task", false);
                when(taskService.createTask(any(CreateTaskRequest.class))).thenReturn(expectedResponse);

                mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.statusCode").value(201))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.title").value("New Test Task"))
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
                                createTaskResponse(1, "Task 1", false),
                                createTaskResponse(2, "Task 2", true));
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
                                .andExpect(jsonPath("$.data.total").value(2));
        }

        @Test
        void getAllTasks_filterByTitle_shouldReturnPageOfFilteredTasks() throws Exception {
                int offset = 0;
                int limit = 10;
                String sortBy = "id";
                String titleFilter = "Filtered";
                Pageable pageableForService = createPageable(offset, limit, sortBy);

                List<TaskResponse> tasks = Arrays.asList(createTaskResponse(1, "Filtered Task", false));
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
                                .andExpect(jsonPath("$.data.list[0].title").value("Filtered Task"));
        }

        @Test
        void getAllTasks_filterByCompleted_shouldReturnPageOfFilteredTasks() throws Exception {
                int offset = 0;
                int limit = 10;
                String sortBy = "id";
                Boolean completedFilter = true;
                Pageable pageableForService = createPageable(offset, limit, sortBy);

                List<TaskResponse> tasks = Arrays.asList(createTaskResponse(2, "Completed Task", true));
                Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageableForService, tasks.size());
                when(taskService.getAllTasks(eq(null), eq(completedFilter), eq(pageableForService)))
                                .thenReturn(taskPage);

                mockMvc.perform(get("/api/tasks")
                                .param("completed", String.valueOf(completedFilter))
                                .param("offset", String.valueOf(offset))
                                .param("limit", String.valueOf(limit))
                                .param("sortBy", sortBy)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.data.list.length()").value(1))
                                .andExpect(jsonPath("$.data.list[0].title").value("Completed Task"));
        }

        @Test
        void getAllTasks_filterByTitleAndCompleted_shouldReturnPageOfFilteredTasks() throws Exception {
                int offset = 0;
                int limit = 10;
                String sortBy = "id";
                String titleFilter = "Filtered";
                Boolean completedFilter = true;
                Pageable pageableForService = createPageable(offset, limit, sortBy);

                List<TaskResponse> tasks = Arrays.asList(createTaskResponse(3, "Filtered Completed Task", true));
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
                                .andExpect(jsonPath("$.data.list[0].title").value("Filtered Completed Task"));
        }

        @Test
        void getTaskById_shouldReturnTask_whenTaskExists() throws Exception {
                TaskResponse task = createTaskResponse(1, "Existing Task", false);
                when(taskService.getTaskById(1)).thenReturn(task);

                mockMvc.perform(get("/api/tasks/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.title").value("Existing Task"));
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

                TaskResponse expectedResponse = createTaskResponse(1, "Updated Title", true);
                when(taskService.updateTask(eq(1), any(UpdateTaskRequest.class))).thenReturn(expectedResponse);

                mockMvc.perform(put("/api/tasks/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.statusCode").value(200))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.title").value("Updated Title"));
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
        void deleteTask_shouldReturnNoContent_whenTaskExists() throws Exception {
                doNothing().when(taskService).deleteTask(1);

                mockMvc.perform(delete("/api/tasks/{id}", 1))
                                .andExpect(status().isNoContent())
                                .andExpect(content().string(""));
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
