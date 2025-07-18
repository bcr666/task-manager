package io.github.bcr666.taskmanager.exception;

public class MissingDataException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private Object data = null;

	public MissingDataException() {
		super();
	}

	public MissingDataException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MissingDataException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingDataException(String message) {
		super(message);
	}

	public MissingDataException(String message, Object data) {
		super(message);
		this.data = data;
	}

	public MissingDataException(Throwable cause) {
		super(cause);
	}

	public Object getData()
	{
		return data;
	}
}
