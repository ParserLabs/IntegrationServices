package com.parserlabs.commons.lgd;

import java.util.Collections;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatesDTO implements Comparable<StatesDTO> {
	private String code;
	private String name;
	private List<DistrictDTO> districts;

	public void setDistricts(List<DistrictDTO> districts) {
		Collections.sort(districts);
		this.districts = districts;
	}

	@Override
	public int compareTo(StatesDTO objectToCompare) {
		return this.getName().compareTo(objectToCompare.getName());
	}

	public static StatesDTO of(String code, String name) {
		return StatesDTO.builder().code(code).name(name).build();
	}

}
