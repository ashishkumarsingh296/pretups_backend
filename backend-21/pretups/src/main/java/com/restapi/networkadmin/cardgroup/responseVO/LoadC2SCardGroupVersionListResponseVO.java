package com.restapi.networkadmin.cardgroup.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class LoadC2SCardGroupVersionListResponseVO extends BaseResponse {

	List<GroupSetDetails> groupDetailsList;
    
}
