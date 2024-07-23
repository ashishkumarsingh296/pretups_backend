package com.restapi.networkadmin.o2creconciliation.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.o2creconciliation.requestVO.O2CReconciliationFailRequestVO;
import com.restapi.networkadmin.o2creconciliation.requestVO.O2CReconciliationOrderApproveRequestVO;
import com.restapi.networkadmin.o2creconciliation.responseVO.O2CReconciliationListResponseVO;
import com.restapi.networkadmin.o2creconciliation.responseVO.O2CReconciliationTxnDetailVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;

public interface O2CReconciliationService {
    O2CReconciliationListResponseVO o2cReconciliationList(Connection con, UserVO userVO, String fromDate, String toDate) throws BTSLBaseException;
    O2CReconciliationTxnDetailVO o2cReconciliationTransactionDetail(Connection con, UserVO userVO, String transferId) throws Exception;
    BaseResponse o2cReconciliationFail(MComConnectionI mcomCon, Connection con, UserVO userVO, O2CReconciliationFailRequestVO request) throws Exception;
    BaseResponse o2cOrderApproval(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, O2CReconciliationOrderApproveRequestVO request, HttpServletRequest requestVO, HttpServletResponse response1) throws BTSLBaseException, ParseException, SQLException;

}
