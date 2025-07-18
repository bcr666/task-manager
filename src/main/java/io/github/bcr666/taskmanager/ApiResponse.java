package io.github.bcr666.taskmanager;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true) @Getter @Setter
public class ApiResponse<T>
{
	
	private int status;
	
	private String message;
	
	private T data;
	
}
