package com.parserlabs.commons.google.captchav3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaptchaEnterpriseResponse {

	private String name;
	private Event event;
	private float score;
	private TokenProperties tokenProperties;

}
