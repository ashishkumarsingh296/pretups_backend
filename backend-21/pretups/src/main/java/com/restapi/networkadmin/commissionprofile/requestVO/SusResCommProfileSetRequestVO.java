package com.restapi.networkadmin.commissionprofile.requestVO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SusResCommProfileSetRequestVO {

	private String status;
	private String commProfileSetId;
	private String commProfileName;
	private String language1Message = null;
    private String language2Message = null;
    private String defaultProfile; 
}
