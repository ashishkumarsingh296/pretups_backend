package com.restapi.networkadmin.messagemanagement.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageRequestVO;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageUploadRequestVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesBulkResponseVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessageResponseVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

@Service
public interface MessageManagementServiceI {
    public MessageResponseVO loadMessageDetails(Connection con, Locale locale, String loginID, String messageCode, HttpServletResponse responseSwagger) throws BTSLBaseException, SQLException;
    public BaseResponse modifyMessageDetails(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, MessageRequestVO requestVO) throws BTSLBaseException, SQLException;
    public MessagesBulkResponseVO downloadMessageFile(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger) throws Exception;
    public MessagesBulkResponseVO uploadMessages(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, MessageUploadRequestVO request) throws Exception;

}
