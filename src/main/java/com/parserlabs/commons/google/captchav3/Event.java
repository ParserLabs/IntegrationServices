package com.parserlabs.commons.google.captchav3;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {

	private String token;
	private String siteKey;
	private String expectedAction;
	private String	userAgent;
	private String  userIpAddress;
	
	

	public static Event of(String token, String siteKey, String expectedAction) {
		return Event.builder().token(token).siteKey(siteKey).expectedAction(expectedAction).build();
	}
}
