package io.github.bcr666.taskmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Serializer
{
	
	private static Logger logger = LoggerFactory.getLogger(Serializer.class);
	
	/**
	 * Serializes an object into a JSON string.
	 * 
	 * @param object The object to serialize.
	 * @return JSON string representation of the object, or an empty string if serialization fails.
	 */
	public static String serialize(Object object) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			return mapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error("Failed to serialize object: {}", object, e);
			return "";
		}
	}
}
