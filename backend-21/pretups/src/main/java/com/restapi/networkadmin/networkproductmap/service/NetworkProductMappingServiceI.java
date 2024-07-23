package com.restapi.networkadmin.networkproductmap.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.restapi.networkadmin.networkproductmap.requestVO.NetworkProductMappingRequestVO;
import com.restapi.networkadmin.networkproductmap.responseVO.NetworkProductMappingResponseVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

public interface NetworkProductMappingServiceI {
    public NetworkProductMappingResponseVO loadNetworkProductDetails(Connection con, Locale locale, String loginID, HttpServletRequest request, HttpServletResponse responseSwagger) throws BTSLBaseException, SQLException;
    public NetworkProductMappingResponseVO addNetworkProductMappingDetails(Connection con, MComConnectionI mcomCon, Locale locale, String loginID, HttpServletRequest httpServletRequest, HttpServletResponse responseSwagger, NetworkProductMappingRequestVO request) throws BTSLBaseException, SQLException;
}
