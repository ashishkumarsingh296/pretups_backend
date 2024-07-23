package com.restapi.user.service;

import java.sql.Connection;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;

@Service
public interface BarUnbarBulkService {

	/**
	 * 
	 * @param con
	 * @param responseSwag
	 * @param requestVO
	 * @param userVO
	 * @param type
	 * @return
	 * @throws BTSLBaseException
	 */

	public BaseResponse barringUnbarringBulkByAdmin(Connection con, HttpServletResponse responseSwag,BulkBarredRequestVO requestVO, UserVO userVO, String type) throws BTSLBaseException;

}
