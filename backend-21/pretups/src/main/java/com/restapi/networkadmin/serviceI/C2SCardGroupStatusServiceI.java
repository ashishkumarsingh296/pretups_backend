package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.SaveC2ScardGroupStatusListRequestVO;
import com.restapi.networkadmin.requestVO.LoadCardGroupStatusListRequestVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusResponseVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusSaveResponseVO;

public interface C2SCardGroupStatusServiceI {

	public C2SCardGroupStatusResponseVO loadC2SCardGroupStatusList(Connection con, UserVO userVO, LoadCardGroupStatusListRequestVO requestVO) throws Exception;

	public C2SCardGroupStatusSaveResponseVO saveC2SCardGroupStatusList(Connection con, MComConnectionI mcomCon, UserVO userVO,
			SaveC2ScardGroupStatusListRequestVO requestVO) throws BTSLBaseException,SQLException;

}
