package com.restapi.preferences.requestVO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateSystemPreferencesRequestVO {

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceUpdateList")
	private ArrayList<UpdateSystemPreferenceVO> preferenceUpdateList;
}
