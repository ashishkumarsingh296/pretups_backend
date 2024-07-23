package com.restapi.superadmin.STKServices.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.STKServices.requestVO.AddServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.AssignServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.ModifyServiceRequestVO;
import com.restapi.superadmin.STKServices.requestVO.PushWmlRequestVO;
import com.restapi.superadmin.STKServices.responseVO.*;

import java.sql.Connection;
import java.sql.SQLException;

public interface STKServicesService {
    UserTypeServiceListResponseVO userTypeServiceList(Connection con) throws BTSLBaseException;

    GenerateByteCodeResponseVO generateByteCode(Connection con, String wmlCode, String description, String serviceSetID) throws Exception;

    void pushWml(Connection con, PushWmlRequestVO request, UserVO userVO) throws Exception;

    void addService(Connection con, MComConnectionI mcomCon, AddServiceRequestVO request, UserVO userVO) throws Exception;

    SimProfileCategoryListResponseVO simProfileCategoryList(Connection con) throws BTSLBaseException;

    UserSimServicesListResponseVO userSimServicesList(Connection con, String categoryCode, String profileCode, String simProfileCode, String networkCode) throws BTSLBaseException;

    SimServicesListResponseVO simServicesList(Connection con, String categoryCode, String serviceSetID, String searchString, String networkCode) throws BTSLBaseException;

    CalculateOffsetResponseVO calculateOffset(Connection con, String categoryCode, String profileCode, String simProfileCode, String serviceID, String byteCodeLength, int position, UserVO userVO) throws BTSLBaseException;

    void assignService(Connection con, MComConnectionI mcomCon, AssignServiceRequestVO request, UserVO userVO) throws BTSLBaseException, SQLException;

    ServiceDetailsResponseVO loadSIMServiceDetails(Connection con, String serviceId, String majorVersion) throws BTSLBaseException;

    void updateService(Connection con, MComConnectionI mcomCon, ModifyServiceRequestVO request, UserVO userVO) throws Exception;
}
