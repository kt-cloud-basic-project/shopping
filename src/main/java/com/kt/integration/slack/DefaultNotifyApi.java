package com.kt.integration.slack;

import java.util.Arrays;

import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.kt.integration.properties.SlackProperties;
import com.slack.api.methods.MethodsClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class DefaultNotifyApi implements NotifyApi {
	private final MethodsClient methodsClient;
	private final SlackProperties slackProperties;
	private final Environment environment;

	@Override
	public void notify(String message) {

		try {
			methodsClient.chatPostMessage(request -> {
				request.username("shoppingForU-Bot")
					.channel(slackProperties.logChannel())
					.text(String.format("```%s - shoppingForU-%s```", message, getActiveProfile()))
					.build();

				return request;
			});
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	private String getActiveProfile() {
		return Arrays.stream(environment.getActiveProfiles()).findFirst().orElse("local");
	}
}
