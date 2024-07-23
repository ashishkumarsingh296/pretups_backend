package com.restapi.networkadmin.bulkambiguoussettlement.service;

import com.btsl.common.BaseResponse;
import com.restapi.networkadmin.bulkambiguoussettlement.requestVO.BulkAmbiguousSettlementRequestVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Locale;

@Service
public interface BulkAmbiguousSettlementServiceI {
    public BaseResponse uploadFile(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, BulkAmbiguousSettlementRequestVO request) throws Exception;
}
