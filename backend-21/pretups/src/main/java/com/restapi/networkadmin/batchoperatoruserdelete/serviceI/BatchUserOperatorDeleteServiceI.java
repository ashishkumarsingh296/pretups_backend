package com.restapi.networkadmin.batchoperatoruserdelete.serviceI;

import java.sql.Connection;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import com.restapi.networkadmin.batchoperatoruserdelete.response.BatchOperatorUserDeleteResponseVO;

import jakarta.servlet.http.HttpServletResponse;
@Service
public interface BatchUserOperatorDeleteServiceI {
	public BatchOperatorUserDeleteResponseVO batchOperatorUserDelete(Connection con,UserVO userVO,UploadFileRequestVO uploadRequestVO, BatchOperatorUserDeleteResponseVO response,HttpServletResponse responseSwagger)throws BTSLBaseException, Exception;
}
