package com.restapi.networkadmin.commissionprofile.requestVO;

import java.util.Date;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeStatusForCommissionProfileVO {
	private String commProfileSetId;
	private String commProfileSetName;
	private String status;
	private String language1Message ;
	private String language2Message;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private String modifiedBy;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private Date modifiedOn;
	@io.swagger.v3.oas.annotations.media.Schema(hidden = true)
	private long lastModifiedOn;
	private String defaultProfile; 

}
