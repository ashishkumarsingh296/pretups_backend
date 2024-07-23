package com.restapi.networkadmin.serviceI;

import java.sql.Connection;
import java.util.ArrayList;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.AddBatchPromotionalTransferRuleFileProcessingRequestVO;
import com.restapi.networkadmin.responseVO.DomainAndCategoryResponseVO;
import com.restapi.networkadmin.responseVO.DownloadFileResponseVO;
import com.restapi.networkadmin.responseVO.UploadAndProcessFileResponseVO;

@Service
public interface AddBatchPromotionalTransferRuleServiceI {
 public ArrayList loadPromotionalLevel();
 public DomainAndCategoryResponseVO loadSearchCriteria(String promotionLevel, Connection con, UserVO userVO)throws BTSLBaseException;
 public DownloadFileResponseVO loadDownloadFile(Connection con, UserVO userVO, HttpServletRequest httpServletRequest, String promotionLevel, String domainCode, String categoryCode,
		String geographicalCode, String cellGroupCode,String selectType )throws BTSLBaseException, java.text.ParseException;
 public UploadAndProcessFileResponseVO uploadAndProcessFile(Connection con, HttpServletRequest httpServletRequest, HttpServletResponse response1, UserVO userVO, String promotionLevel, String domainCode,
		String categoryCode, String geographicalCode, String cellGroupCode, String selectType, AddBatchPromotionalTransferRuleFileProcessingRequestVO fileRequest)throws Exception;
}
