package com.parserlabs.commons.lgd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parserlabs.commons.utility.JsonReader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LgdService {

	private static final Map<String, StatesDTO> STATE_CODE_MAP = new TreeMap<>();
	private static final Map<String, StatesDTO> STATE_NAME_MAP = new TreeMap<>();

	private static final Set<StatesDTO> STATES = new TreeSet<>();

	private static final String LGD_STATES_JSON_FILE = "lgd_latest.json";
	private static ObjectMapper mapper = new ObjectMapper();

	private JsonReader jsonReader;

	public LgdService(JsonReader jsonReader) {
		this.jsonReader = jsonReader;
		// Loading state data
		loadStateData();
	}

	public List<StatesDTO> getStates() {
		return new ArrayList<>(STATES);
	}

	public boolean isValidState(String key) {

		boolean isValidState = false;
		if (StringUtils.hasLength(key)
				&& (STATE_CODE_MAP.containsKey(key) || STATE_NAME_MAP.containsKey(key.toUpperCase()))) {
			isValidState = true;
		}

		if (!isValidState) {
			throw new RuntimeException(String.format("enter valid state code: %s", key));
		}
		return isValidState;
	}

	public StatesDTO getState(String key) {
		StatesDTO state = null;
		if (isValidState(key)) {
			if (STATE_CODE_MAP.containsKey(key)) {
				state = STATE_CODE_MAP.get(key);
			} else if (STATE_NAME_MAP.containsKey(key.toUpperCase())) {
				state = STATE_NAME_MAP.get(key.toUpperCase());
			}
		}
		return state;
	}

	/**
	 * Get StatesDTO by state code or name if available
	 * 
	 * @param key code or name of state
	 * @return StatesDTO or null if not available.
	 */
	public StatesDTO getStateByCodeOrName(String key) {
		Optional<StatesDTO> state = Optional.ofNullable(null);
		if (StringUtils.hasLength(key)) {
			if (STATE_CODE_MAP.containsKey(key)) {
				state = Optional.ofNullable(STATE_CODE_MAP.get(key));
			} else if (STATE_NAME_MAP.containsKey(key.toUpperCase())) {
				state = Optional.ofNullable(STATE_NAME_MAP.get(key.toUpperCase()));
			}
		}
		return state.isPresent() ? state.get() : null;
	}

	/**
	 * Get DistrictDTO by state code or name if available
	 * 
	 * @param stateKey    code or name of state
	 * @param districtKey code or name of district
	 * @return DistrictDTO or null if not available.
	 */
	public DistrictDTO getDistrictByCodeOrName(String stateKey, String districtKey) {
		Optional<DistrictDTO> optionalFindFirst = Optional.ofNullable(null);
		StatesDTO state = getStateByCodeOrName(stateKey);
		if (Objects.nonNull(state)) {
			optionalFindFirst = state.getDistricts().stream()
					.filter(distract -> DistrictDTO.isValidKey(districtKey, distract.getCode(), distract.getName()))
					.findFirst();
		}
		return optionalFindFirst.isPresent() ? optionalFindFirst.get() : null;
	}

	public List<DistrictDTO> getDistrictFromState(String key) {
		return isValidState(key) ? getState(key).getDistricts() : null;
	}

	public DistrictDTO getDistrict(String stateKey, String districtKey, boolean isOptional) {
		Optional<DistrictDTO> optionalFindFirst = null;
		if (isValidState(stateKey)) {
			StatesDTO state = getState(stateKey);
			if (Objects.nonNull(state)) {
				optionalFindFirst = state.getDistricts().stream()
						.filter(distract -> DistrictDTO.isValidKey(districtKey, distract.getCode(), distract.getName()))
						.findFirst();
			}

			if (!optionalFindFirst.isPresent() && !isOptional) {
				throw new RuntimeException(String.format("Enter valid district %s", districtKey));
			}
		}
		return optionalFindFirst.isPresent() ? optionalFindFirst.get() : null;

	}

	public String getDistrictName(String stateCode, String distractCode, boolean source) {
		DistrictDTO district = getDistrict(stateCode, distractCode, source);
		return Objects.nonNull(district) ? district.getName() : "";
	}

	public String getStateName(String stateCode) {
		StatesDTO state = getState(stateCode);
		return Objects.nonNull(state) ? state.getName() : "";
	}

	private void loadStateData() {
		loadStateData(jsonParser(LGD_STATES_JSON_FILE));
	}

	private List<DataModel> jsonParser(String filename) {
		List<DataModel> stateDataModelList = null;
		try {
			stateDataModelList = Arrays.asList(mapper.readValue(jsonReader.jsonReader(filename), DataModel[].class));
		} catch (Exception e) {
			log.error("Exception occured while reading/parsing the Json file ", e);
		}
		return stateDataModelList;
	}

	private void loadStateData(List<DataModel> stateDataModelList) {
		if (!CollectionUtils.isEmpty(stateDataModelList)) {
			stateDataModelList.stream().forEach(stateData -> {
				if (STATE_CODE_MAP.containsKey(stateData.getStateCode())) {
					DistrictDTO district = DistrictDTO.of(stateData.getDistrictCode(), stateData.getDistrictName());

					STATE_CODE_MAP.get(stateData.getStateCode()).getDistricts().add(district);
				} else {
					StatesDTO state = StatesDTO.of(stateData.getStateCode(), stateData.getStateName());
					DistrictDTO district = DistrictDTO.of(stateData.getDistrictCode(), stateData.getDistrictName());

					List<DistrictDTO> districts = new ArrayList<>();
					districts.add(district);
					state.setDistricts(districts);

					STATE_CODE_MAP.put(stateData.getStateCode(), state);
					STATE_NAME_MAP.put(stateData.getStateName().toUpperCase(), state);
				}
			});
			STATES.addAll(STATE_CODE_MAP.values().stream().collect(Collectors.toList()));
		}
	}

}
