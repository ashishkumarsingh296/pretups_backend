package com.restapi.superadmin.sublookup.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.superadmin.sublookup.requestVO.ModifySubLookUpRequestVO;
import com.restapi.superadmin.sublookup.requestVO.SubLookUpRequestVO;
import com.restapi.superadmin.sublookup.responseVO.LookUpListResponseVO;
import com.restapi.superadmin.sublookup.responseVO.SubLookUpListResponseVO;
import com.restapi.superadmin.sublookup.responseVO.SubLookUpResponseVO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

public interface SubLookUpService {
    LookUpListResponseVO loadLookUpList(Connection con, Locale locale) throws BTSLBaseException;

    SubLookUpListResponseVO loadSubLookUpList(Connection con, Locale locale, String lookUpCode) throws BTSLBaseException;

    BaseResponse addSubLookUp(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, SubLookUpRequestVO requestVO) throws BTSLBaseException, SQLException;

    SubLookUpResponseVO loadSubLookUpDetails(Connection con, Locale locale, String subLookUpCode) throws BTSLBaseException;

    BaseResponse modifySubLookUp(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, ModifySubLookUpRequestVO request) throws BTSLBaseException, SQLException;

    BaseResponse deleteSubLookUp(Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, String subLookUpCode) throws BTSLBaseException, SQLException;
}
