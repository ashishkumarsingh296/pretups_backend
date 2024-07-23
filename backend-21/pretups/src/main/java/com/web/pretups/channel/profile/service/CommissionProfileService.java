package com.web.pretups.channel.profile.service;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.pretups.channel.profile.web.CommissionProfileModel;

public interface CommissionProfileService {
	
	CommissionProfileModel commissionProfileStatus(String loginId, BindingResult bindingResult,ModelMap modelMap)  throws JsonProcessingException, IOException, Exception ;
	
	CommissionProfileModel commissionProfileList(CommissionProfileModel commissionProfileModel, String loginId, BindingResult bindingResult,ModelMap modelMap)  throws JsonProcessingException, IOException, Exception ;
	
	CommissionProfileModel commissionProfileSaveSuspend(CommissionProfileModel commissionProfileModel, String loginId, BindingResult bindingResult, ModelMap modelMap)  throws JsonProcessingException, IOException, Exception ;
	
	void loadCommissionProfilePage(String loginId,ModelMap modelMap) throws SQLException, BTSLBaseException, Exception;
	
	void populateCommissionProfilePage(String loginId,CommissionProfileModel commissionProfileModel,ModelMap modelMap) throws SQLException, BTSLBaseException, Exception;

}
