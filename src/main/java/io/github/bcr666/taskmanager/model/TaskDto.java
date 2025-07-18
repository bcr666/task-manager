package io.github.bcr666.taskmanager.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.github.bcr666.taskmanager.Constants;

public record TaskDto(
		
		Long id
		
		, String title
		
		, String description
		
		, @JsonDeserialize(using = LocalDateDeserializer.class)
		@JsonSerialize(using = LocalDateSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_FORMAT)
		LocalDate dueDate
		
		, boolean completed
		
		, @JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_TIME_FORMAT)
		LocalDateTime createdAt
		
		, @JsonDeserialize(using = LocalDateTimeDeserializer.class)
		@JsonSerialize(using = LocalDateTimeSerializer.class)
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.JSON_DATE_TIME_FORMAT)
		LocalDateTime updatedAt
		
		, String owner
		)
{

}
