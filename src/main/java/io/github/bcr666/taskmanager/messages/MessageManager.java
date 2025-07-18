package io.github.bcr666.taskmanager.messages;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageManager {

	private final MessageSource messageSource;

	public MessageManager(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessage(String key, Locale locale) {
		return messageSource.getMessage(key, null, locale);
	}

	public String getMessage(String key) {
		return messageSource.getMessage(key, null, Locale.ENGLISH);
	}
}