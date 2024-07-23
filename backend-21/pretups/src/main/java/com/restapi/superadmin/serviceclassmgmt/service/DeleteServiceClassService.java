package com.restapi.superadmin.serviceclassmgmt.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

@Service
public interface DeleteServiceClassService {
    /**
     * @param con
     * @param locale
     * @param id
     * @param userVO
     * @param name
     * @return
     */
    BaseResponse delete(Connection con, Locale locale, String id, UserVO userVO, String name) throws BTSLBaseException, SQLException;
}
