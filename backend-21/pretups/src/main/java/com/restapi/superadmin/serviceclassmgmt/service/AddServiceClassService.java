package com.restapi.superadmin.serviceclassmgmt.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.serviceclassmgmt.requestVO.AddServiceClassRequestVO;
import com.restapi.superadmin.serviceclassmgmt.responseVO.AddServiceClassResponseVO;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

@Service
public interface AddServiceClassService {

    /**
     * @param con
     * @param locale
     * @param requestVO
     * @param userVO
     * @return
     */
    public AddServiceClassResponseVO add(Connection con, Locale locale, AddServiceClassRequestVO requestVO, UserVO userVO) throws BTSLBaseException, SQLException;
}