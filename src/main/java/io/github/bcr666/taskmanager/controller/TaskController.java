package io.github.bcr666.taskmanager.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.bcr666.taskmanager.ApiResponse;
import io.github.bcr666.taskmanager.messages.MessageManager;
import io.github.bcr666.taskmanager.model.TaskDto;
import io.github.bcr666.taskmanager.service.TaskService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController
{

	private final TaskService taskService;
	
	private final MessageManager messageManager;
	

	@GetMapping
	public ResponseEntity<ApiResponse<List<TaskDto>>> listAllTasks()
	{
		
		List<TaskDto> tasks = taskService.listAllTasks();
		
		ApiResponse<List<TaskDto>> response = new ApiResponse<List<TaskDto>>()
				.setData(tasks)
				.setMessage(null)
				.setStatus(200)
				;
		
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<TaskDto>> CreateTask(@RequestBody TaskDto task)
	{
		
		TaskDto createdTask = taskService.createTask(task);
		
		ApiResponse<TaskDto> response = new ApiResponse<TaskDto>()
				.setData(createdTask)
				.setMessage(messageManager.getMessage("task_controller.create_task"))
				.setStatus(200)
				;
		
		return ResponseEntity.ok().body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<TaskDto>> UpdateTask(@PathVariable Long id, @RequestBody TaskDto task)
	{
		
		TaskDto updatedTask = taskService.updateTask(task);
		
		ApiResponse<TaskDto> response = new ApiResponse<TaskDto>()
				.setData(updatedTask)
				.setMessage(messageManager.getMessage("task_controller.update_task"))
				.setStatus(200)
				;
		
		return ResponseEntity.ok().body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Object>> DeleteTask(@PathVariable Long id)
	{
		
		taskService.deleteTask(id);
		
		ApiResponse<Object> response = new ApiResponse<Object>()
				.setData(null)
				.setMessage(messageManager.getMessage("task_controller.delete_task"))
				.setStatus(200)
				;
		
		return ResponseEntity.ok().body(response);
	}

}
