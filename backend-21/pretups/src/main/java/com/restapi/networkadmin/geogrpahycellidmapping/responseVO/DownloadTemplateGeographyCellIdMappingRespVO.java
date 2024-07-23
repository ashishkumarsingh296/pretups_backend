package com.restapi.networkadmin.geogrpahycellidmapping.responseVO;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadTemplateGeographyCellIdMappingRespVO extends BaseResponse {

	private String fileAttachment;
	private String fileName;
	private String fileType;	
	
}