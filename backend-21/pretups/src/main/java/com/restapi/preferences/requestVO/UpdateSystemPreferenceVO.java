package com.restapi.preferences.requestVO;

import com.fasterxml.jackson.annotation.JsonProperty;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UpdateSystemPreferenceVO {

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceCode")
	private String preferenceCode;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceValue")
	private String preferenceValue;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("preferenceValueType")
	private String preferenceValueType;

	@io.swagger.v3.oas.annotations.media.Schema(required = true)
	@JsonProperty("lastModifiedTime")
	private Long lastModifiedTime;

}
