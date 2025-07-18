package io.github.bcr666.taskmanager.service;

import java.util.List;

import io.github.bcr666.taskmanager.model.TaskDto;

public interface TaskService
{
	
	public List<TaskDto> listAllTasks();
	
	public TaskDto createTask(TaskDto dto);
	
	public TaskDto updateTask(TaskDto dto);
	
	public void deleteTask(Long id);
	
}
