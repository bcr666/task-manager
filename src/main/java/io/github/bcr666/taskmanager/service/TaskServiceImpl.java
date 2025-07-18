package io.github.bcr666.taskmanager.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import io.github.bcr666.taskmanager.entity.Task;
import io.github.bcr666.taskmanager.exception.MissingDataException;
import io.github.bcr666.taskmanager.exception.UnexpectedErrorException;
import io.github.bcr666.taskmanager.messages.MessageManager;
import io.github.bcr666.taskmanager.model.TaskDto;
import io.github.bcr666.taskmanager.model.utils.TaskDtoUtils;
import io.github.bcr666.taskmanager.repository.TaskRepository;

@Service
public class TaskServiceImpl implements TaskService
{

	private TaskRepository taskRepository;
	private MessageManager messageManager;
	
	public TaskServiceImpl(TaskRepository taskRepository, MessageManager messageManager)
	{
		this.taskRepository = taskRepository;
		this.messageManager = messageManager;
	}
	
	@Override
	public List<TaskDto> listAllTasks() {
		
		List<Task> tasks = taskRepository.findAll();
		
		List<TaskDto> dtoList = tasks
				.stream()
                .map(entity -> TaskDtoUtils.toDto(entity))
                .collect(Collectors.toList());
		
		return dtoList;
		
	}

	@Override
	public TaskDto createTask(TaskDto dto) {
		
		if (dto == null)
		{
			throw new MissingDataException(messageManager.getMessage("exceptions.missing_data_exception.create_task"));
		}
		
		validateTaskExcludeId(dto);
		
		try
		{
			Task entity = TaskDtoUtils.toEntity(dto);
			
			LocalDateTime now = LocalDateTime.now();
			
			entity.setCreatedAt(now).setUpdatedAt(now);
			
			Task savedEntity = taskRepository.save(entity);
			
			TaskDto savedDto = TaskDtoUtils.toDto(savedEntity);
			
			return savedDto;
		}
		catch (OptimisticLockingFailureException ex)
		{
			throw new UnexpectedErrorException(messageManager.getMessage("exceptions.unexpected_error_exception.create_task"));
		}
		
	}

	@Override
	public TaskDto updateTask(TaskDto dto) {
		
		if (dto == null)
		{
			throw new MissingDataException(messageManager.getMessage("exceptions.missing_data_exception.update_task"));
		}
		
		validateTaskIncludeId(dto);
		
		try
		{
			Task entity = TaskDtoUtils.toEntity(dto);
			
			LocalDateTime now = LocalDateTime.now();
			
			entity.setUpdatedAt(now);
			
			Task savedEntity = taskRepository.save(entity);
			
			TaskDto savedDto = TaskDtoUtils.toDto(savedEntity);
			
			return savedDto;
		}
		catch (OptimisticLockingFailureException ex)
		{
			throw new UnexpectedErrorException(messageManager.getMessage("exceptions.unexpected_error_exception.update_task"));
		}
		
	}

	@Override
	public void deleteTask(Long id) {
		
		if (id == null)
		{
			throw new MissingDataException(messageManager.getMessage("exceptions.missing_data_exception.delete_task"));
		}
		
		try
		{
			taskRepository.deleteById(id);
		}
		catch (OptimisticLockingFailureException ex)
		{
			throw new UnexpectedErrorException(messageManager.getMessage("exceptions.unexpected_error_exception.delete_task"));
		}
		
	}
	
	private void validateTask(TaskDto dto, List<String> messages)
	{

		if (dto.title() == null)
		{
			messages.add(messageManager.getMessage("task_service.validate_task.missing_title"));
		}
		
		if (dto.owner() == null)
		{
			messages.add(messageManager.getMessage("task_service.validate_task.missing_owner"));
		}
		
		if (!messages.isEmpty())
		{
			throw new MissingDataException(String.join(" ", messages), dto);
		}
	}
	
	private void validateTaskExcludeId(TaskDto dto)
	{
		List<String> messages = new ArrayList<>();
		
		if (dto.id() != null)
		{
			messages.add(messageManager.getMessage("task_service.validate_task.present_id"));
		}

		validateTask(dto, messages);
	}
	
	private void validateTaskIncludeId(TaskDto dto)
	{
		List<String> messages = new ArrayList<>();
		
		if (dto.id() == null)
		{
			messages.add(messageManager.getMessage("task_service.validate_task.missing_id"));
		}

		validateTask(dto, messages);
	}

}
