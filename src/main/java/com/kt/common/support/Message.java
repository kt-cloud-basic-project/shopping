package com.kt.common.support;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Message(
	MessageType messageType,
	String message,
	String detail,
	LocalDateTime timestamp
) {
	public static Message error(MessageType messageType, String message, String detail) {
		return new Message(messageType, message, detail, LocalDateTime.now());
	}

	public String formatMessage() {
		StringBuilder sb = new StringBuilder();

		sb.append("[").append(messageType).append("] - ").append(timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
		sb.append(message).append("\n").append("\n");

		if (detail != null && !detail.isEmpty()) {
			sb.append(detail).append("\n").append("\n");
		}

		return sb.toString();
	}
}
