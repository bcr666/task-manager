package io.github.bcr666.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.bcr666.taskmanager.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>
{

}
