package com.restapi.networkadmin.updatesimtxnid.serviceI;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.updatesimtxnid.requestVO.BulkSimTXNIdRequestVO;
import com.restapi.networkadmin.updatesimtxnid.responseVO.BulkSimTXNIdResponseVO;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public interface SimTXNIDServiceI {


   public BaseResponse updateSIMTXNId(Connection con, UserVO userVO, String msisdn, BaseResponse response)throws BTSLBaseException, Exception;

   public BulkSimTXNIdResponseVO updateBulkSIMTXNIds(Connection con, UserVO userVO, BulkSimTXNIdRequestVO requestVO, BulkSimTXNIdResponseVO response)throws BTSLBaseException, Exception;
}
