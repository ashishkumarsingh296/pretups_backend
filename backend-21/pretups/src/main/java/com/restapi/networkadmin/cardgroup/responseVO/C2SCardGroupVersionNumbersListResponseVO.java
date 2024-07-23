package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class C2SCardGroupVersionNumbersListResponseVO extends BaseResponse{
    private String cardGroupSetID;
	private List versionList; 
}
