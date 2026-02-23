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
        private ObjectMapper objectMapper; // To convert objects to JSON strings

        @MockBean
        private TaskService taskService; // Mock the service layer

        private TaskResponse createTaskResponse(Integer id, String title, boolean completed) {
                return new TaskResponse(id, title, completed);
        }

        private Pageable pageable = PageRequest.of(0, 10);

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
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("New Test Task"))
                                .andExpect(jsonPath("$.completed").value(false));
        }

        @Test
        void createTask_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
                CreateTaskRequest request = new CreateTaskRequest();
                request.setTitle("   "); // Invalid title (blank)
                request.setCompleted(false);

                mockMvc.perform(post("/api/tasks")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.message").exists()); // GlobalExceptionHandler will catch this
        }

        @Test
        void getAllTasks_noFilters_shouldReturnPageOfAllTasks() throws Exception {
                List<TaskResponse> tasks = Arrays.asList(
                                createTaskResponse(1, "Task 1", false),
                                createTaskResponse(2, "Task 2", true));
                Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageable, tasks.size());
                when(taskService.getAllTasks(eq(null), eq(null), any(Pageable.class))).thenReturn(taskPage);

                mockMvc.perform(get("/api/tasks")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "title,asc")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(2))
                                .andExpect(jsonPath("$.content[0].title").value("Task 1"))
                                .andExpect(jsonPath("$.totalElements").value(2));
        }

        @Test
        void getAllTasks_filterByTitle_shouldReturnPageOfFilteredTasks() throws Exception {
                List<TaskResponse> tasks = Arrays.asList(createTaskResponse(1, "Filtered Task", false));
                Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageable, tasks.size());
                when(taskService.getAllTasks(eq("Filtered"), eq(null), any(Pageable.class))).thenReturn(taskPage);

                mockMvc.perform(get("/api/tasks")
                                .param("title", "Filtered")
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].title").value("Filtered Task"));
        }

        @Test
        void getAllTasks_filterByCompleted_shouldReturnPageOfFilteredTasks() throws Exception {
                List<TaskResponse> tasks = Arrays.asList(createTaskResponse(2, "Completed Task", true));
                Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageable, tasks.size());
                when(taskService.getAllTasks(eq(null), eq(true), any(Pageable.class))).thenReturn(taskPage);

                mockMvc.perform(get("/api/tasks")
                                .param("completed", "true")
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].title").value("Completed Task"));
        }

        @Test
        void getAllTasks_filterByTitleAndCompleted_shouldReturnPageOfFilteredTasks() throws Exception {
                List<TaskResponse> tasks = Arrays.asList(createTaskResponse(3, "Filtered Completed Task", true));
                Page<TaskResponse> taskPage = new PageImpl<>(tasks, pageable, tasks.size());
                when(taskService.getAllTasks(eq("Filtered"), eq(true), any(Pageable.class))).thenReturn(taskPage);

                mockMvc.perform(get("/api/tasks")
                                .param("title", "Filtered")
                                .param("completed", "true")
                                .param("page", "0")
                                .param("size", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content.length()").value(1))
                                .andExpect(jsonPath("$.content[0].title").value("Filtered Completed Task"));
        }

        @Test
        void getTaskById_shouldReturnTask_whenTaskExists() throws Exception {
                TaskResponse task = createTaskResponse(1, "Existing Task", false);
                when(taskService.getTaskById(1)).thenReturn(task);

                mockMvc.perform(get("/api/tasks/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Existing Task"));
        }

        @Test
        void getTaskById_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: 99"))
                                .when(taskService).getTaskById(99);

                mockMvc.perform(get("/api/tasks/{id}", 99)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task not found with id: 99")); // Custom
                                                                                                       // ErrorResponse
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
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.title").value("Updated Title"))
                                .andExpect(jsonPath("$.completed").value(true));
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
                                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
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
                                .andExpect(jsonPath("$.message").exists()); // GlobalExceptionHandler will catch this
        }

        @Test
        void deleteTask_shouldReturnNoContent_whenTaskExists() throws Exception {
                doNothing().when(taskService).deleteTask(1);

                mockMvc.perform(delete("/api/tasks/{id}", 1))
                                .andExpect(status().isNoContent());
        }

        @Test
        void deleteTask_shouldReturnNotFound_whenTaskDoesNotExist() throws Exception {
                doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found with id: 99"))
                                .when(taskService).deleteTask(99);

                mockMvc.perform(delete("/api/tasks/{id}", 99))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.message").value("Task not found with id: 99"));
        }
}
