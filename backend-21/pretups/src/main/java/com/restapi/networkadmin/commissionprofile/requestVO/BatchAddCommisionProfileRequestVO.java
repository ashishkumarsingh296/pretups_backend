package com.restapi.networkadmin.commissionprofile.requestVO;

import com.btsl.pretups.common.PretupsI;


import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class BatchAddCommisionProfileRequestVO {
	@io.swagger.v3.oas.annotations.media.Schema(example = PretupsI.FILE_TYPE_XLS, required = true, description=PretupsI.FILE_TYPE_XLSTYPE)
	private String fileType;
	@io.swagger.v3.oas.annotations.media.Schema(example = PretupsI.BATCH_ADD_COMMPROFILE_INITIATE, required = true, description=PretupsI.FILE_NAME_BATCH_ADD)
	private String fileName;
	@io.swagger.v3.oas.annotations.media.Schema(example = PretupsI.BASE64_ENCODED_DATA, required = true, description=PretupsI.BASE64_ENCODED_DATA_STRING)
	private String fileAttachment;
	
}
