package com.web.pretups.channel.profile.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileBL;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileValidator;
import com.btsl.security.csrf.CSRFTokenUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.web.pretups.channel.profile.service.CommissionProfileService;

/**
 * @(#)CommissionProfileController.java
 *                                  Copyright(c) 2016,Mahindra Comviva.
 *                                  All Rights Reserved
 * 
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                 jashobanta.mahapatra 20/06/2016
 * 
 *                                 This class is responsible to listen every request
 *                                 which comes from Commission Profile Status module (UI),
 *                                 does some front validation if requires,
 *                                 further it processes towards rest service
 *                                 through service class.
 * 
 */

@Controller
@RequestMapping("/commission-profile/*")
public class CommissionProfileController extends CommonController{

	@Autowired
	private CommissionProfileService commissionProfileService;

	/**
	 * This method listens on click of commission profile status of left menu
	 * @param commissionProfileModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping(value="/status.form", method=RequestMethod.GET)
	public String commissionProfileStatus(@ModelAttribute("commissionProfileModel") CommissionProfileModel commissionProfileModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws ServletException, IOException, Exception{
		 final String  METHOD_NAME = "commissionProfileStatus";
		try{
			if (log.isDebugEnabled()) {
				log.debug("CommissionProfileController : "+METHOD_NAME, "Entered");
			}
			this.authorise(request, response, "COMMPS001", false);
			commissionProfileModel = commissionProfileService.commissionProfileStatus( getUserFormSession(request).getLoginID(), bindingResult, modelMap);
			modelMap.put("commissionProfileModel", commissionProfileModel);	
		} 
		catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
		}/*catch (Exception e) {
			_log.error(METHOD_NAME, "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		}*/
		if (log.isDebugEnabled()) {
			log.debug("CommissionProfileController : "+METHOD_NAME, "Exits");
		}
		return "profile/selectDomainForCommissionStatusSuspend";

	}

	/**
	 * This method fetches commission profile list based on domain/catagory/gdomain/grade selection
	 * @param commissionProfileModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/set-list.form", method=RequestMethod.POST)
	public String commissionProfileList(@ModelAttribute("commissionProfileModel") CommissionProfileModel commissionProfileModel, 
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws BTSLBaseException,JsonParseException , Exception{
		final String METHOD_NAME = "getCommissionProfileList";
		String returnPage = "profile/commissionStatusProfileList";
		try{
			if (log.isDebugEnabled()) {
				log.debug("CommissionProfileController : "+METHOD_NAME, "Entered");
			}
			commissionProfileModel = commissionProfileService.commissionProfileList(commissionProfileModel, getUserFormSession(request).getLoginID(), bindingResult, modelMap);
			modelMap.put("commissionProfileModel", commissionProfileModel);	
		} 
		catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			commissionProfileService.populateCommissionProfilePage((String)getUserFormSession(request).getLoginID(),commissionProfileModel, modelMap);
			returnPage = "profile/selectDomainForCommissionStatusSuspend";
		}/*catch (JsonParseException e) {
			_log.error(METHOD_NAME, "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		} catch (JsonMappingException e) {
			_log.error(METHOD_NAME, "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		}catch (Exception e) {
			_log.error(METHOD_NAME, "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		}*/
		if (log.isDebugEnabled()) {
			log.debug("CommissionProfileController : "+METHOD_NAME, "Exits");
		}
		return returnPage;
	}

	/**
	 * This method saves/suspends list of commission profiles
	 * @param commissionProfileModel
	 * @param request
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/save-suspend.form", method=RequestMethod.POST)
	public String commissionProfileSaveSuspend(@ModelAttribute("commissionProfileModel") CommissionProfileModel commissionProfileModel, 
			HttpServletRequest request,BindingResult bindingResult,ModelMap modelMap) throws BTSLBaseException,JsonParseException , Exception{
		final String METHOD_NAME = "commissionProfileSaveSuspend";
		try{
			if (log.isDebugEnabled()) {
				log.debug("CommissionProfileController : "+METHOD_NAME, "Entered");
			}
			// Security CSRF starts here
			final boolean flag = CSRFTokenUtil.isValid(request);
			if (!flag) {
				if (log.isDebugEnabled()) {
					log.debug("CSRF", "ATTACK!");
				}
				throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing", "SuspendResumeView");
			}
			
			for (CommissionProfileSetVO commissionProfileSetVO:commissionProfileModel.getSelectCommProfileSetList()) {
				commissionProfileSetVO.setDefaultProfile("N");
			}
			if(commissionProfileModel.getDefaultProfileIndex()!=null){
				commissionProfileModel.getSelectCommProfileSetList().get(commissionProfileModel.getDefaultProfileIndex()).setDefaultProfile("Y");

			}
			for (CommissionProfileSetVO commissionProfileSetVO:commissionProfileModel.getSelectCommProfileSetList()) {
				if(commissionProfileSetVO.getStatus()==null || !commissionProfileSetVO.getStatus().equals("Y")){
					commissionProfileSetVO.setStatus("S");
				}
			}
			List<String> errorMessages = new CommissionProfileValidator().validateCommissionProfileList(commissionProfileModel);
if(errorMessages != null && !errorMessages.isEmpty()){
				modelMap.put("fail", errorMessages);
				return "profile/commissionStatusProfileList";
			}
			commissionProfileModel = commissionProfileService.commissionProfileSaveSuspend(commissionProfileModel, getUserFormSession(request).getLoginID(), bindingResult, modelMap);
			CommissionProfileBL commissionProfileBL = new CommissionProfileBL(); 
			commissionProfileModel = commissionProfileBL.loadDomainListForSuspend((String)getUserFormSession(request).getLoginID());
			modelMap.put("commissionProfileModel", commissionProfileModel);
		} 
		catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
		}/*catch (Exception e) {
			_log.error(METHOD_NAME, "Exceptin:e=" + e);
			_log.errorTrace(METHOD_NAME, e);
		}*/
		if (log.isDebugEnabled()) {
			log.debug("CommissionProfileController : "+METHOD_NAME, "Exits");
		}
		return "profile/selectDomainForCommissionStatusSuspend";
	}

	/**
	 * This method listens on click of back button of commission profile list page, its not rest request
	 * @param commissionProfileModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value="/set-list-back.form", method=RequestMethod.POST)
	public String commissionProfileListBack(@ModelAttribute("commissionProfileModel") CommissionProfileModel commissionProfileModel, 
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap){
		final String METHOD_NAME = "commissionProfileListBack";
		try{
			if (log.isDebugEnabled()) {
				log.debug("CommissionProfileController : "+METHOD_NAME, "Entered");
			}			
			commissionProfileService.populateCommissionProfilePage((String)getUserFormSession(request).getLoginID(),commissionProfileModel, modelMap);
		} 
		catch (Exception e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
		}
		if (log.isDebugEnabled()) {
			log.debug("CommissionProfileController : "+METHOD_NAME, "Exits");
		}
		return "profile/selectDomainForCommissionStatusSuspend";
	}
	
	
	
	
}
