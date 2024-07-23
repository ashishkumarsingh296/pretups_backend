package com.restapi.networkadmin.operatorUser.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.operatorUser.requestVO.AddOperatorUserRequestVO;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;

@Service
public interface OperatorUserEditService {
    BaseResponse modifyOperatorUser(Connection con, Locale locale, UserVO userVO, HttpServletResponse responseSwag, AddOperatorUserRequestVO requestVO) throws BTSLBaseException, SQLException, ParseException;
}
