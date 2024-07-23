package com.restapi.networkadmin.c2sreconciliation.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.c2sreconciliation.requestVO.C2SRreconciliationActionRequestVO;
import com.restapi.networkadmin.c2sreconciliation.requestVO.C2SRreconciliationRequestVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.C2SReconciliationResponseListVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.C2SReconciliationTransferDetailsVO;
import com.restapi.networkadmin.c2sreconciliation.responseVO.C2SRreconciliationActionResponseVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

public interface C2SReconciliationService {
    C2SReconciliationResponseListVO loadC2SReconciliationList(Connection con, UserVO userVO, C2SRreconciliationRequestVO requestVO) throws ParseException, BTSLBaseException;

    C2SReconciliationTransferDetailsVO loadC2SreconciliationTransferDetails(Connection con, String transferID) throws BTSLBaseException;

    C2SRreconciliationActionResponseVO performreconciliationaction(Connection con, UserVO userVO, C2SRreconciliationActionRequestVO requestVO) throws BTSLBaseException, SQLException;
}
