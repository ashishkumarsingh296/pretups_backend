package com.restapi.networkadmin.operatorUser.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.operatorUser.requestVO.AddOperatorUserRequestVO;
import com.restapi.networkadmin.operatorUser.responseVO.*;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.Locale;

@Service
public interface OperatorUserService {
    /**
     * @param con
     * @param locale
     * @param categoryCode
     * @param userVO
     * @return
     */
    OperatorUserCategoryResponseVO getCategoryList(Connection con, Locale locale, String categoryCode, UserVO userVO) throws BTSLBaseException;

    /**
     *
     * @param con
     * @param locale
     * @param userVO
     * @param responseSwag
     * @param requestVO
     * @return
     */
    BaseResponse addOperatorUser(Connection con, Locale locale, UserVO userVO, HttpServletResponse responseSwag, AddOperatorUserRequestVO requestVO) throws Exception;

    OperatorUserRolesResponseVO getRoleList(Connection con, Locale locale, String userId,String categoryCode) throws BTSLBaseException;

    OperatorUserGeographyResponseVO getGeographyList(Connection con, Locale locale, String userId,String categoryCode,String networkCode) throws BTSLBaseException;

    OperatorUserMsisdnListResponseVO getMsisdnList(Connection con, Locale locale, String userId, String categoryCode, UserVO userVO, String loginid) throws BTSLBaseException;

    OperatorUserDomainListResponseVO getDomainList(Connection con, Locale locale, String userId) throws BTSLBaseException;

    OperatorUserServiceListResponseVO getServiceList(Connection con, Locale locale, String userId, String categoryCode) throws BTSLBaseException;

    OperatorUserProductListResponseVO getProductList(Connection con, Locale locale, String userId) throws BTSLBaseException;

    OperatorUesrVoucherResponseVO getVoucherList(Connection con, Locale locale, String userId) throws BTSLBaseException;

    BaseResponse checkMsisdn(Connection con, Locale locale, String userId, String msisdn) throws BTSLBaseException;
}
