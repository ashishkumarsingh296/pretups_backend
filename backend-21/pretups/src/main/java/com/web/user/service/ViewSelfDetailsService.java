package com.web.user.service;

import java.io.IOException;

import com.btsl.user.businesslogic.UserVO;

public interface ViewSelfDetailsService {
	
	 UserVO loadData(String LoginId) throws IOException;

}
