package io.github.bcr666.taskmanager.model.utils;

import io.github.bcr666.taskmanager.entity.Task;
import io.github.bcr666.taskmanager.model.TaskDto;

public class TaskDtoUtils {

	public static Task toEntity(TaskDto dto) {
		return new Task()
				.setId(dto.id())
				.setTitle(dto.title())
				.setDescription(dto.description())
				.setDueDate(dto.dueDate())
				.setCompleted(dto.completed())
				.setCreatedAt(dto.createdAt())
				.setUpdatedAt(dto.updatedAt())
				.setOwner(dto.owner())
				;
	}

	public static TaskDto toDto(Task entity) {
		return new TaskDto(
				entity.getId()
				, entity.getTitle()
				, entity.getDescription()
				, entity.getDueDate()
				, entity.isCompleted()
				, entity.getCreatedAt()
				, entity.getUpdatedAt()
				, entity.getOwner()
				);
	}

}
