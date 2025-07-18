package io.github.bcr666.taskmanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import io.github.bcr666.taskmanager.ApiResponse;
import io.github.bcr666.taskmanager.Serializer;
import io.github.bcr666.taskmanager.messages.MessageManager;
import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
	
	private final MessageManager messageManager;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
    	ApiResponse<String> apiResponse = new ApiResponse<String>()
    			.setData(null)
    			.setMessage(ex.getMessage())
    			.setStatus(HttpStatus.NOT_FOUND.value())
    			;
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }

    // Spring creates this when the body of a request isn't present
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    	ApiResponse<String> apiResponse = new ApiResponse<String>()
    			.setData(null)
    			.setMessage(messageManager.getMessage("exception_handler.not_readable"))
    			.setStatus(HttpStatus.BAD_REQUEST.value())
    			;
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }

    // Spring creates this when you call an endpoint that doesn't exist
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleNoResourceFoundException(NoResourceFoundException ex) {
    	ApiResponse<String> apiResponse = new ApiResponse<String>()
    			.setData(null)
    			.setMessage(messageManager.getMessage("exception_handler.no_resource_found"))
    			.setStatus(HttpStatus.NOT_FOUND.value())
    			;
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }

    @ExceptionHandler(UnexpectedErrorException.class)
    public ResponseEntity<ApiResponse<String>> handleUnexpectedErrorException(UnexpectedErrorException ex) {
    	ApiResponse<String> apiResponse = new ApiResponse<String>()
    			.setData(null)
    			.setMessage(ex.getMessage())
    			.setStatus(HttpStatus.BAD_REQUEST.value())
    			;
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }

    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<ApiResponse<String>> handleMissingDataException(MissingDataException ex) {
    	ApiResponse<String> apiResponse = new ApiResponse<String>()
    			.setData(Serializer.serialize(ex.getData()))
    			.setMessage(ex.getMessage())
    			.setStatus(HttpStatus.BAD_REQUEST.value())
    			;
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(Exception ex) {
    	ApiResponse<String> apiResponse = new ApiResponse<String>()
    			.setData(null)
    			.setMessage(messageManager.getMessage("exception_handler.exception"))
    			.setStatus(HttpStatus.BAD_REQUEST.value())
    			;
        return new ResponseEntity<ApiResponse<String>>(apiResponse, HttpStatus.OK);
    }
    
}