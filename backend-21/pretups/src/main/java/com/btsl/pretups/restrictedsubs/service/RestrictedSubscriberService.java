package com.btsl.pretups.restrictedsubs.service;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;

/**
 * This interface provides method declaration for Scheldule Recharge
 * @author lalit.chattar
 *
 */
public interface RestrictedSubscriberService {

	/**
	 * This method loads geography
	 * @return geographyList List  of UserGeographiesVO
	 * @throws BTSLBaseException
	 * @param userVO
	 */
	public List<UserGeographiesVO> loadGeography(UserVO userVO) throws BTSLBaseException;
	

	/**
	 * Method declaration for loading batch recharge form
	 * @param userVO
	 * @param restrictedSubscriberModel
	 * @param request
	 * @param model
	 * @throws BTSLBaseException
	 * 
	 */
	public void loadloadBatchRechargeFormData(UserVO userVO, RestrictedSubscriberModel restrictedSubscriberModel, HttpServletRequest request, Model model) throws BTSLBaseException;
	
	/**
	 * Method declaration for loading domain
	 * @param userVO
	 * @return
	 * @throws BTSLBaseException
	 */
	public <T> List<T> loadDomain(UserVO userVO) throws BTSLBaseException;
	
	/**
	 * This method loads view schedule topup form
	 * @return void
	 * @throws BTSLBaseException
	 */
	public void loadViewScheduleRechargeFormData(UserVO userVO, RestrictedSubscriberModel restrictedSubscriberModel, HttpServletRequest request, Model model)
			throws BTSLBaseException;

	/**
	 * This method loads view schedule topup list
	 * @return true/false
	 * @throws IOException RuntimeException
	 */
	public Boolean viewScheduleList( RestrictedSubscriberModel subscriberModel, UserVO userVO, Model model, BindingResult bindingResult)
			throws IOException, RuntimeException;
	
	/**
	 * Method declaration for file template download
	 * @param restrictedSubscriberModel
	 * @param fileType
	 * @return
	 * @throws BTSLBaseException
	 */
	public String getFileLocationForTemplate(RestrictedSubscriberModel restrictedSubscriberModel, String fileType) throws BTSLBaseException;

	/**
	 * Method declaration for validating request data for batch recharge
	 * @param bindingResult
	 * @param restrictedSubscriberModel
	 * @throws BTSLBaseException
	 */
	public void validateRequestDataForBatchRecharge(BindingResult bindingResult, RestrictedSubscriberModel restrictedSubscriberModel) throws BTSLBaseException;

	/**
	 * 
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param request
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 */
	public Boolean processUploadedFile(RestrictedSubscriberModel restrictedSubscriberModel, BindingResult bindingResult, HttpServletRequest request, Model model) throws BTSLBaseException;

	/**
	 * load frequency 
	 * @return
	 * @throws BTSLBaseException
	 */
	public List<ListValueVO> loadFrequency() throws BTSLBaseException;
	
	
	
	public void updateCancelInfo(UserVO userVO, RestrictedSubscriberModel restrictedSubscriberModel, HttpServletRequest request, Model model) throws BTSLBaseException;
	public RestrictedSubscriberModel viewCancelledScheduleSubscriber(
			RestrictedSubscriberModel restrictedSubscriberModel, String bID,
			UserVO userVO, Model model) throws IOException;
	public void loadBatchDetailsforSingle(RestrictedSubscriberModel restrictedSubscriberModel,BindingResult bindingResult,UserVO userVO,Model model) throws IOException,BTSLBaseException;
	public void loadDetailsForSingle(RestrictedSubscriberModel restrictedSubscriberModel,UserVO userVO,Model model) throws IOException;
	public RestrictedSubscriberModel loadDetailsForSingleRest(RestrictedSubscriberModel restrictedSubscriberModel,UserVO userVO,Model model) throws IOException;
	public void loadDetailsForSelected(RestrictedSubscriberModel restrictedSubscriberModel,List<String> checklist,UserVO userVO,Model model) throws IOException;
	public RestrictedSubscriberModel deleteDetailsForSelected(RestrictedSubscriberModel restrictedSubscriberModel,UserVO userVO,Model model) throws IOException;
	
	public void loadScheduleBatchMasterList(UserVO userVO, RestrictedSubscriberModel restrictedSubscriberModel, HttpServletRequest request, Model model) throws BTSLBaseException;
	/**
	 * Method declaration for loading status 
	 * @throws IOException
	 */
	List<ListValueVO> loadStatus() throws IOException;
	
	/**
	 * Method declaration for viewing the batch list
	 * @param restrictedSubscriberModel,model,bindingResult,userVO,
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public Boolean processviewScheduleRCBatchForm(RestrictedSubscriberModel restrictedSubscriberModel, BindingResult bindingResult, Model model,UserVO userVO,List<ScheduleBatchMasterVO> barredlist) throws BTSLBaseException;
	
	/**
	 * Method declaration for viewing the details of batches
	 * @param restrictedSubscriberModel,model,bindingResult,userVO,bid,
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public Boolean detailedviewScheduleRCBatchForm(RestrictedSubscriberModel restrictedSubscriberModel,BindingResult bindingResult, Model model,List<ScheduleBatchMasterVO> schList,String bid) throws BTSLBaseException;
    public PretupsResponse<List<UserVO>> loadUserDetailsSearch(RestrictedSubscriberModel restrictedSubscriberModel,String categoryCode,String userName, String geoDomain,String domain, UserVO userVO, Model model) throws IOException,BTSLBaseException;
 
}
