package com.restapi.superadmin.serviceI;

import java.sql.SQLException;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.restapi.superadmin.requestVO.AddAgentRequestVO;
import com.restapi.superadmin.requestVO.DeleteCategoryRequestVO;
import com.restapi.superadmin.requestVO.SaveCategoryRequestVO;
import com.restapi.superadmin.responseVO.AddCategoryResponseVO;
import com.restapi.superadmin.responseVO.CategoryAgentViewResponseVO;
import com.restapi.superadmin.responseVO.CategoryListResponseVO;
import com.restapi.superadmin.responseVO.GetAgentScreenDetailsReq;
import com.restapi.superadmin.responseVO.UpdateCategoryOnlyResp;

public interface CategoryManagementService {

	CategoryListResponseVO getCategoryList(String domainCode, HttpServletResponse responseSwag, Locale locale)
			throws SQLException;

	BaseResponse deleteCategory(DeleteCategoryRequestVO request, String loginId, HttpServletResponse responseSwag,
			Locale locale) throws SQLException;

	CategoryAgentViewResponseVO getAddAgentScreenInputDet(GetAgentScreenDetailsReq getAgentScreenDetailsReq,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;

	AddCategoryResponseVO saveCategory(SaveCategoryRequestVO request, String loginId, HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;

	BaseResponse updateCategoryAgent(SaveCategoryRequestVO request, String loginId, HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;

	BaseResponse addAgent(AddAgentRequestVO request, String loginId, HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws BTSLBaseException;
	
	CategoryListResponseVO getCategoryInfo(String domainCode,String cateogoryCode, HttpServletResponse responseSwag, Locale locale)
			throws SQLException;
	
	
	UpdateCategoryOnlyResp updateCategoryOnly(SaveCategoryRequestVO request, String loginId, HttpServletRequest httpServletRequest,
			HttpServletResponse responseSwag, Locale locale) throws SQLException;
	
	  void cleanupCategoryUnassignedDomainData();
	   

}
