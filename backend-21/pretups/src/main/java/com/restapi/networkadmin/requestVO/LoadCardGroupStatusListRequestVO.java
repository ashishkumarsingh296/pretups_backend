package com.restapi.networkadmin.requestVO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoadCardGroupStatusListRequestVO {
	private List<CardGroupStatusRequestVO> requestVOList;
	
}
