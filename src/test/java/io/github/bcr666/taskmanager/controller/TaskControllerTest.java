package io.github.bcr666.taskmanager.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.bcr666.taskmanager.messages.MessageManager;
import io.github.bcr666.taskmanager.model.TaskDto;
import io.github.bcr666.taskmanager.security.JwtFilter;
import io.github.bcr666.taskmanager.security.SecurityConfig;
import io.github.bcr666.taskmanager.service.TaskService;

@WebMvcTest(
		controllers = TaskController.class
		, excludeFilters = {
				@ComponentScan.Filter(
						type = FilterType.ASSIGNABLE_TYPE
						, classes = { 
								JwtFilter.class
								, SecurityConfig.class 
						}
				)
		}
)
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TaskService taskService;

	@MockBean
	private MessageManager messageManager;

	private ObjectMapper objectMapper;

	@BeforeEach
	public void setup() {
		objectMapper = new ObjectMapper();
	}

	@Test
	public void testListAllTasks() throws Exception {
		LocalDate today = LocalDate.now();
		
		TaskDto dto1 = new TaskDto(1l, "Task 1", null, today, false, null, null, "Task 1 Owner");

		TaskDto dto2 = new TaskDto(2l, "Task 2", null, today, false, null, null, "Task 2 Owner");

		List<TaskDto> tasks = Arrays.asList(dto1, dto2);

		when(taskService.listAllTasks()).thenReturn(tasks);

		mockMvc.perform(MockMvcRequestBuilders.get("/tasks"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.length()").value(2))
				.andExpect(jsonPath("$.data[0].id").value(1L))
				.andExpect(jsonPath("$.data[0].title").value("Task 1"))
				.andExpect(jsonPath("$.data[0].dueDate").value(today.toString()))
				.andExpect(jsonPath("$.data[0].owner").value("Task 1 Owner"))
				;
	}

	@Test
	public void testCreateTask() throws Exception {
		LocalDate today = LocalDate.now();
		LocalDateTime now = LocalDateTime.now();
		String title = "Task 1";
		String owner = "Task 1 Owner";
		String message = "Task created";
		
		TaskDto input = new TaskDto(null, title, null, today, false, null, null, owner);
		TaskDto created = new TaskDto(1l, title, null, today, false, now, now, owner);

		when(taskService.createTask(any(TaskDto.class))).thenReturn(created);
		when(messageManager.getMessage("task_controller.create_task")).thenReturn(message);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(input)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(1L))
			.andExpect(jsonPath("$.data.title").value(title))
			.andExpect(jsonPath("$.data.dueDate").value(today.toString()))
			.andExpect(jsonPath("$.data.owner").value(owner))
			.andExpect(jsonPath("$.message").value(message))
			.andReturn();

		// Parse response and assert timestamps within 3 seconds
		String json = result.getResponse().getContentAsString();
		JsonNode root = objectMapper.readTree(json);
		LocalDateTime createdAt = LocalDateTime.parse(root.at("/data/createdAt").asText());
		LocalDateTime updatedAt = LocalDateTime.parse(root.at("/data/updatedAt").asText());

		Duration createdDiff = Duration.between(now, createdAt).abs();
		Duration updatedDiff = Duration.between(now, updatedAt).abs();

		assertTrue(createdDiff.getSeconds() <= 3);
		assertTrue(updatedDiff.getSeconds() <= 3);
	}

	@Test
	public void testUpdateTask() throws Exception {
		long id = 1L;
		String title = "Task 1";
		String description = "Description 1";
		LocalDate today = LocalDate.now();
		LocalDateTime then = LocalDateTime.of(2025, 06, 28, 12, 16, 32);
		LocalDateTime now = LocalDateTime.now();
		String owner = "Owner";
		String message = "Task updated";
		
		TaskDto input = new TaskDto(id, title, description, today, false, then, then, owner);

		TaskDto updated = new TaskDto(id, title, description, today, false, then, now, owner);

		when(taskService.updateTask(any(TaskDto.class))).thenReturn(updated);
		when(messageManager.getMessage("task_controller.update_task")).thenReturn(message);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/tasks/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(input)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(id))
			.andExpect(jsonPath("$.data.title").value(title))
			.andExpect(jsonPath("$.data.dueDate").value(today.toString()))
			.andExpect(jsonPath("$.data.owner").value(owner))
			.andExpect(jsonPath("$.message").value(message))
			.andReturn();

		// Parse response and assert timestamps within 3 seconds
		String json = result.getResponse().getContentAsString();
		JsonNode root = objectMapper.readTree(json);
		LocalDateTime createdAt = LocalDateTime.parse(root.at("/data/createdAt").asText());
		LocalDateTime updatedAt = LocalDateTime.parse(root.at("/data/updatedAt").asText());

		Duration createdDiff = Duration.between(then, createdAt).abs();
		Duration updatedDiff = Duration.between(now, updatedAt).abs();

		assertTrue(createdDiff.getSeconds() <= 3);
		assertTrue(updatedDiff.getSeconds() <= 3);
	}

	@Test
	public void testDeleteTask() throws Exception {
		String message = "Task deleted";
		
		doNothing().when(taskService).deleteTask(1L);
		when(messageManager.getMessage("task_controller.delete_task")).thenReturn(message);

		mockMvc.perform(MockMvcRequestBuilders.delete("/tasks/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data").doesNotExist())
				.andExpect(jsonPath("$.message").value(message));
	}
}