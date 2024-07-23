package com.btsl.pretups.restrictedsubs.service;

import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.web.pretups.restrictedsubs.web.RestrictedSubscriberModel;


/**
 * @author jashobanta.mahapatra
 *
 */
public interface BatchRechageRescheduleService {
	
	/**
	 * @param restrictedTopUpForm
	 * @param loginID
	 * @param modelMap
	 * @throws BTSLBaseException
	 */
	public void loadScheduledBatchList(RestrictedSubscriberModel restrictedTopUpForm , String loginID, ModelMap modelMap, BindingResult bindingResult) throws BTSLBaseException;
	
	/**
	 * @param thisForm
	 * @param loginIDRR
	 * @param modelMap
	 * @param response
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	public void downloadBatchFile(RestrictedSubscriberModel thisForm, String loginID,ModelMap modelMap, HttpServletResponse response, BindingResult bindingResult) throws BTSLBaseException;

	/**
	 * @param thisForm
	 * @param uploadedFile
	 * @param loginID
	 * @param modelMap
	 * @param bindingResult
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	public void uploadAndProcessBatchReshedule(RestrictedSubscriberModel thisForm, String loginID, ModelMap modelMap,BindingResult bindingResult) throws BTSLBaseException ;
}
