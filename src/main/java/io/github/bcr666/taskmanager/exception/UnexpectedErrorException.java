package io.github.bcr666.taskmanager.exception;

public class UnexpectedErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UnexpectedErrorException() {
		super();
	}

	public UnexpectedErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnexpectedErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnexpectedErrorException(String message) {
		super(message);
	}

	public UnexpectedErrorException(Throwable cause) {
		super(cause);
	}

}
