package io.github.bcr666.taskmanager.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import io.github.bcr666.taskmanager.entity.Task;
import io.github.bcr666.taskmanager.exception.MissingDataException;
import io.github.bcr666.taskmanager.exception.UnexpectedErrorException;
import io.github.bcr666.taskmanager.messages.MessageManager;
import io.github.bcr666.taskmanager.model.TaskDto;
import io.github.bcr666.taskmanager.model.utils.TaskDtoUtils;
import io.github.bcr666.taskmanager.repository.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

	@Mock
	private TaskRepository taskRepository;
	@Mock
	private MessageManager messageManager;
	
	private TaskServiceImpl taskService;

	public TaskServiceImplTest()
	{
		taskService = new TaskServiceImpl(taskRepository, messageManager);
	}
	
	@BeforeEach
	void setUp() {
		taskRepository = mock(TaskRepository.class);
		taskService = new TaskServiceImpl(taskRepository, messageManager);
	}

	@Test
	void testListAllTasks() {
		String title1 = "Task 1";
		
		Task task1 = new Task();
		task1.setId(1L);
		task1.setTitle(title1);

		Task task2 = new Task();
		task2.setId(2L);
		task2.setTitle("Task 2");

		when(taskRepository.findAll()).thenReturn(Arrays.asList(task1, task2));

		List<TaskDto> result = taskService.listAllTasks();

		assertEquals(2, result.size());
		assertEquals(title1, result.get(0).title());
	}

	@Test
	void testCreateTask_withValidDto() {
		long id  = 1l;
		LocalDate today = LocalDate.now();
		String owner = "New Owner";
		String description = "New Description";
		String title = "New Task";
		LocalDateTime now = LocalDateTime.now();
		
		TaskDto dto = new TaskDto(null, title, description, today, false, null, null, owner );

		Task entity = new Task()
				.setId(id)
				.setTitle(title)
				.setDescription(description)
				.setDueDate(today)
				.setCompleted(false)
				.setCreatedAt(now)
				.setUpdatedAt(now)
				.setOwner(owner)
				;

		when(taskRepository.save(any(Task.class))).thenReturn(entity);
		TaskDto result = taskService.createTask(dto);

		assertEquals(id, result.id());
		assertEquals(title, result.title());
		assertEquals(description, result.description());
		assertEquals(today, result.dueDate());
		assertEquals(false, result.completed());
		assertEquals(owner, result.owner());

		// Check that timestamps are within 3 seconds
		Duration createdDiff = Duration.between(result.createdAt(), now).abs();
		Duration updatedDiff = Duration.between(result.updatedAt(), now).abs();

		assertTrue(createdDiff.getSeconds() <= 3);
		assertTrue(updatedDiff.getSeconds() <= 3);
	}

	@Test
	void testCreateTask_withInvalidDto() {
		long id = 1L;
		String owner = "New Owner";
		String title = "New Task";
		
		TaskDto dto = new TaskDto(null, null, null, null, false, null, null, owner );
		TaskDto dto1 = new TaskDto(null, title, null, null, false, null, null, null );
		TaskDto dto2 = new TaskDto(id, title, null, null, false, null, null, owner );

		when(messageManager.getMessage("task_service.validate_task.missing_title")).thenReturn("Missing title");
		when(messageManager.getMessage("task_service.validate_task.missing_owner")).thenReturn("Missing owner");
		when(messageManager.getMessage("task_service.validate_task.present_id")).thenReturn("Task ID should not be provided when creating a task");
	
		MissingDataException ex;
		ex = assertThrows(MissingDataException.class, () -> taskService.createTask(dto));
		assertEquals("Missing title", ex.getMessage());
		ex = assertThrows(MissingDataException.class, () -> taskService.createTask(dto1));
		assertEquals("Missing owner", ex.getMessage());
		ex = assertThrows(MissingDataException.class, () -> taskService.createTask(dto2));
		assertEquals("Task ID should not be provided when creating a task", ex.getMessage());
	}

	@Test
	void testCreateTask_withNullDto_throwsException() {
		MissingDataException ex = assertThrows(MissingDataException.class, () -> taskService.createTask(null));
		assertEquals(messageManager.getMessage("exceptions.missing_data_exception.create_task"), ex.getMessage());
	}
	
	@Test
	void testCreateTask_optimisticLockingFailure() {
		TaskDto dto = new TaskDto(null, "Test", "Desc", LocalDate.now(), false, LocalDateTime.now(), LocalDateTime.now(), "Owner");
		Task entity = new Task();

		try (MockedStatic<TaskDtoUtils> mocked = Mockito.mockStatic(TaskDtoUtils.class)) {
			mocked.when(() -> TaskDtoUtils.toEntity(dto)).thenReturn(entity);
			when(taskRepository.save(any(Task.class))).thenThrow(new OptimisticLockingFailureException("Conflict"));
			when(messageManager.getMessage("exceptions.unexpected_error_exception.create_task"))
				.thenReturn("Update failed due to concurrent update");

			UnexpectedErrorException ex = assertThrows(UnexpectedErrorException.class, () -> {
				taskService.createTask(dto);
			});

			assertEquals("Update failed due to concurrent update", ex.getMessage());
		}
	}
	
	@Test
	void testUpdateTask_success() {
		LocalDateTime now = LocalDateTime.now();
		LocalDate today = LocalDate.now();

		TaskDto dto = new TaskDto(1L, "Test", "Desc", today, false, now.minusDays(1), now.minusDays(1), "Owner");
		Task entity = new Task().setId(1L).setTitle("Test").setDescription("Desc").setDueDate(today).setCompleted(false).setCreatedAt(now.minusDays(1)).setUpdatedAt(now.minusDays(1)).setOwner("Owner");
		Task savedEntity = new Task().setId(1L).setTitle("Test").setDescription("Desc").setDueDate(today).setCompleted(false).setCreatedAt(now.minusDays(1)).setUpdatedAt(now).setOwner("Owner");
		TaskDto expectedDto = new TaskDto(1L, "Test", "Desc", today, false, now.minusDays(1), now, "Owner");

		try (MockedStatic<TaskDtoUtils> mocked = Mockito.mockStatic(TaskDtoUtils.class)) {
			mocked.when(() -> TaskDtoUtils.toEntity(dto)).thenReturn(entity);
			mocked.when(() -> TaskDtoUtils.toDto(savedEntity)).thenReturn(expectedDto);

			when(taskRepository.save(any(Task.class))).thenReturn(savedEntity);

			TaskDto result = taskService.updateTask(dto);
			assertEquals(expectedDto, result);
		}
	}
	
	@Test
	void testUpdateTask_nullDto_throwsException() {
		when(messageManager.getMessage("exceptions.missing_data_exception.update_task"))
			.thenReturn("Cannot update: DTO is null");

		MissingDataException ex = assertThrows(MissingDataException.class, () -> {
			taskService.updateTask(null);
		});

		assertEquals("Cannot update: DTO is null", ex.getMessage());
	}
	
	@Test
	void testUpdateTask_optimisticLockingFailure() {
		TaskDto dto = new TaskDto(1L, "Test", "Desc", LocalDate.now(), false, LocalDateTime.now(), LocalDateTime.now(), "Owner");
		Task entity = new Task(); // Simplified

		try (MockedStatic<TaskDtoUtils> mocked = Mockito.mockStatic(TaskDtoUtils.class)) {
			mocked.when(() -> TaskDtoUtils.toEntity(dto)).thenReturn(entity);
			when(taskRepository.save(any(Task.class))).thenThrow(new OptimisticLockingFailureException("Conflict"));
			when(messageManager.getMessage("exceptions.unexpected_error_exception.update_task"))
				.thenReturn("Update failed due to concurrent update");

			UnexpectedErrorException ex = assertThrows(UnexpectedErrorException.class, () -> {
				taskService.updateTask(dto);
			});

			assertEquals("Update failed due to concurrent update", ex.getMessage());
		}
	}
	
	@Test
	void testDeleteTask_success() {
		assertDoesNotThrow(() -> taskService.deleteTask(1L));
		verify(taskRepository).deleteById(1L);
	}
}
