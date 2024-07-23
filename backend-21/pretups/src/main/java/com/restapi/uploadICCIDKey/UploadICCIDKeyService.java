package com.restapi.uploadICCIDKey;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.ICCIDIMSIkeymanagement.requestVO.MSISDNWithICCIDFileRequestVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

public interface UploadICCIDKeyService {
    BaseResponse processFile(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response, MSISDNWithICCIDFileRequestVO requestVO) throws BTSLBaseException, SQLException, IOException;
}
