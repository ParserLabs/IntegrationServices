package com.parserlabs.commons.lgd;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataModel {

	@JsonProperty("State Code")
	private String stateCode;

	@JsonProperty("State Name")
	private String stateName;

	@JsonProperty("District Code")
	private String districtCode;

	@JsonProperty("District Name")
	private String districtName;
}
