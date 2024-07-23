package com.restapi.superadmin.serviceclassmgmt.service;

import com.btsl.common.BTSLBaseException;
import com.restapi.networkadmin.responseVO.ServiceClassListResponseVO;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Locale;

@Service
public interface ServiceClassListService {

    /**
     *
     * @param con
     * @param locale
     * @param id
     * @return
     * @throws BTSLBaseException
     */
    ServiceClassListResponseVO getServiceClassList(Connection con, Locale locale, String id) throws BTSLBaseException;
}
