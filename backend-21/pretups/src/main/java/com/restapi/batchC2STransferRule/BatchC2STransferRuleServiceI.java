package com.restapi.batchC2STransferRule;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.MultiValueMap;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

public interface BatchC2STransferRuleServiceI {

    BatchC2STransferRuleFileDownloadResponse downloadTemplateForbatch(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BatchC2STransferRuleFileDownloadResponse response) throws BTSLBaseException;

    BatchC2STransferRuleResponseVO processFile(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BatchC2STransferRuleResponseVO response, BatchC2STransferRuleRequestVO requestVO) throws BTSLBaseException, SQLException;
}
