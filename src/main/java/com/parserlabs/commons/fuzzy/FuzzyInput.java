package com.parserlabs.commons.fuzzy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FuzzyInput {
	private String mobile;
	private String name;
	private String gender;
	private String yearOfBirth;

}
