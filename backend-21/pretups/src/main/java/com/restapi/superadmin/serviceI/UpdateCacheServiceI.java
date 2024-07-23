package com.restapi.superadmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.requestVO.UpdateCacheRequestVO;
import com.restapi.superadmin.responseVO.UpdateCacheResponseVO;

public interface UpdateCacheServiceI {
	
	UpdateCacheResponseVO updateCacheList(Connection con, MComConnectionI mcomCon, Locale locale, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException;

	UpdateCacheResponseVO updateCache(Connection con, UserVO userVO, MComConnectionI mcomCon, Locale locale, UpdateCacheRequestVO request);

	UpdateCacheResponseVO updateRedisCache(Connection con, UserVO userVO, MComConnectionI mcomCon, Locale locale,
			UpdateCacheRequestVO request);

}
