package com.restapi.networkadmin.servicetypeselectormapping.requestVO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModifyServiceTypeSelectorMappingRequestVO {
	@NotNull
	@Size(min = 1)
	private String sNo;
	@NotNull
	@Size(min = 1,max = 1)
	private String srvStatus;
	@NotNull
	@Size(min =1,max = 1)
	private String isDefault;
}
