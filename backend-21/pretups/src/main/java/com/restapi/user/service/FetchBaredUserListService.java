package com.restapi.user.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;

public interface FetchBaredUserListService {
	public FetchBarredListResponseVO viewBarredList(FetchBarredListRequestVO fetcBarredListRequestVO,UserVO userVO) throws BTSLBaseException;
}
