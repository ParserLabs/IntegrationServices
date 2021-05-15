package com.parserlabs.commons.fuzzy;

import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.experimental.UtilityClass;
import me.xdrop.fuzzywuzzy.FuzzySearch;

@UtilityClass
public class FuzzySearchUtility {

	public FuzzyUser fuzzySearch(FuzzyInput fuzzyInput, List<FuzzyUser> userEntities) {

		FuzzyUser matchedUser = null;
		String targetName = fuzzyInput.getName().toUpperCase().replace(" ", "");
		Integer searchYear = null;
		if (StringUtils.hasLength(fuzzyInput.getYearOfBirth())) {
			searchYear = Integer.parseInt(fuzzyInput.getYearOfBirth());
		}
		if (!CollectionUtils.isEmpty(userEntities)) {
			for (FuzzyUser originalUser : userEntities) {
				String searchableName = originalUser.getName().toUpperCase().replace(" ", "");
				int ratio = FuzzySearch.tokenSetRatio(searchableName, targetName);
				if (StringUtils.hasLength(originalUser.getYearOfBirth())) {
					continue;
				}
				int birthYear = Integer.parseInt(originalUser.getYearOfBirth());
				if (ratio > 70) {
					if (searchYear != null) {
						if (yobBetween(birthYear, searchYear - 5, searchYear + 5)
								&& fuzzyInput.getGender().equalsIgnoreCase(originalUser.getGender())) {
							matchedUser = originalUser;
							break;
						}
					}
				}
			}
		}

		return matchedUser;
	}

	public boolean yobBetween(int searchYear, int minRange, int maxRange) {
		return searchYear >= minRange && searchYear <= maxRange;
	}

}
