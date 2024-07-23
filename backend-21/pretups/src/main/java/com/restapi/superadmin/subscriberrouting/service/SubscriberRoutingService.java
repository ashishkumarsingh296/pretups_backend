package com.restapi.superadmin.subscriberrouting.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.subscriberrouting.requestVO.AddRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.BulkAddRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.BulkDeleteRequestVO;
import com.restapi.superadmin.subscriberrouting.requestVO.DeleteRequestVO;
import com.restapi.superadmin.subscriberrouting.responseVO.BulkResponseVO;
import com.restapi.superadmin.subscriberrouting.responseVO.InterfaceResponseVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;

public interface SubscriberRoutingService {
    BaseResponse addSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, AddRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException;

    InterfaceResponseVO loadInterface(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, String interfaceType, HttpServletResponse httpServletResponse) throws BTSLBaseException;

    BulkResponseVO uploadAndProcessBulkAddSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkAddRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException, IOException, ParseException;

    BaseResponse deleteSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, DeleteRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException;

    BulkResponseVO uploadAndProcessBulkDeleteSubscriberRouting(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BulkDeleteRequestVO request, HttpServletResponse httpServletResponse) throws BTSLBaseException, SQLException, IOException, ParseException;

}
