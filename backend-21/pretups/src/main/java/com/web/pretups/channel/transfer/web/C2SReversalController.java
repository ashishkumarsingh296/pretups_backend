package com.web.pretups.channel.transfer.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.transfer.businesslogic.C2SReversalValidator;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.security.csrf.CSRFTokenUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.pretups.channel.transfer.service.C2SReversalServiceImpl;

import nl.captcha.Captcha;

/**
 * @(#)C2SReversalController.java
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
 *                                 jashobanta.mahapatra 30/06/2016
 * 
 *                                 This class is responsible to listen every request
 *                                 which comes from c2s reverse module (UI) for  Channel Admin,
 *                                 does some front validation if requires,
 *                                 further it processes towards rest service
 *                                 through service class.
 * 
 */

@Controller
@RequestMapping("/c2srecharge/*")
public class C2SReversalController extends CommonController{

	@Autowired
	private C2SReversalServiceImpl c2SReversalServiceImpl;
	private static final String forward="c2stransfer/c2sreversal";
	/**
	 * @param C2SReversalModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value="/reversal.form", method=RequestMethod.GET)
	public String c2sReversal(C2SReversalModel reversalModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws BTSLBaseException,JsonProcessingException, IOException{
		final String METHOD_NAME = "C2SReversalController: c2sReversal";
		try{
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Entered");
			}
			c2SReversalServiceImpl.c2sReversal(getUserFormSession(request).getLoginID(), modelMap);
		} 
		catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
		}
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exits");
		}

		return forward;
	}

	/**
	 * @param c2SReversalModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 * @throws SQLException 
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	@RequestMapping(value="/confirm-c2s-reversal.form", method=RequestMethod.POST)
	public String confirmC2SReversal(@ModelAttribute("c2SReversalModel") C2SReversalModel c2SReversalModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws BTSLBaseException, SQLException, JsonProcessingException, IOException{
		final String METHOD_NAME = "C2SReversalController: c2sReversal";
		String returnPage = forward;
		try{
			
			C2SReversalValidator c2sReversalValidator = new C2SReversalValidator();
			
			Map<String, String> errorMessage = new HashMap<String, String>();
			c2sReversalValidator.validateTransferIdOrSubscribermsisdn(c2SReversalModel,errorMessage);
			if(errorMessage.size()>0){
				
				for (Map.Entry<String, String> entry : errorMessage.entrySet()){
					bindingResult.rejectValue(entry.getKey() , entry.getValue());
				}
				c2SReversalServiceImpl.loadReversalPage( getUserFormSession(request).getLoginID(), modelMap);
				returnPage = forward;	
			}
			else{
				c2SReversalServiceImpl.confirmC2SReversal(c2SReversalModel, getUserFormSession(request).getLoginID(), bindingResult, modelMap);	
				if(null != modelMap.get("fail")){
					c2SReversalServiceImpl.loadReversalPage( getUserFormSession(request).getLoginID(), modelMap);
					returnPage = forward;
				}
				else{
					returnPage = "c2stransfer/selectTransactionForC2SReversal";
				}
			}
		} 
		catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
			c2SReversalServiceImpl.loadReversalPage( getUserFormSession(request).getLoginID(), modelMap);
		}
		
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exits");
		}

		
		return returnPage;
	}

	/**
	 * @param c2SReversalModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 * @throws SQLException 
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 * @throws ServletException 
	 * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(value="/do-reversal.form", method=RequestMethod.POST)
	public String doReversal(@ModelAttribute("c2SReversalModel") C2SReversalModel c2SReversalModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws BTSLBaseException, SQLException, JsonProcessingException, IOException, NoSuchAlgorithmException, ServletException{
		final String METHOD_NAME = "C2SReversalController: doReversal";
		String returnPage = "c2stransfer/selectTransactionForC2SReversal";
		final String loginId = getUserFormSession(request).getLoginID();
		try{
			//Security :csrf starts
			boolean flag = CSRFTokenUtil.isValid(request);
	        if (!flag) {
	            if (log.isDebugEnabled()) {
	                log.debug("CSRF", "ATTACK!");
	            }
	            modelMap.put("fail", true);
	            modelMap.put("csrfattack", PretupsRestUtil.getMessageString("security.csrf.attack.message"));
	            return "common/csrfmessage";
	        }
			//Security : scrf ends
			validateCaptcha(request, c2SReversalModel);
			final int index = Integer.parseInt(c2SReversalModel.getSelectIndex());
			c2SReversalModel.setTxID(c2SReversalModel.getUserRevlist().get(index).getTransferID());

			c2SReversalModel = c2SReversalServiceImpl.doReversal(c2SReversalModel, loginId, bindingResult, modelMap);
			if(null != modelMap.get("success")){
				returnPage = "c2stransfer/reversalMessage";
			}
			else{
				C2SReversalServiceImpl c2sReversalServiceImpl = new C2SReversalServiceImpl();
				c2sReversalServiceImpl.loadReverselistPage(c2SReversalModel, loginId, modelMap);
				returnPage = "c2stransfer/selectTransactionForC2SReversal";
			}
		} 
		catch (BTSLBaseException e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
			c2SReversalServiceImpl.loadReverselistPage(c2SReversalModel, loginId, modelMap);
		}
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Exits");
		}
		return returnPage;
	}

	/**
	 * @param request
	 * @param c2SReversalModel
	 * @throws BTSLBaseException
	 */
	private void validateCaptcha(HttpServletRequest request, C2SReversalModel c2SReversalModel) throws BTSLBaseException {
		final String METHOD_NAME = "C2SReversalController: validateCaptcha";
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue()) {
			final String parm = request.getParameter("j_captcha_response");
			Captcha captcha =  (Captcha) request.getSession().getAttribute(Captcha.NAME);
			final String jcaptchaCode1 = captcha.getAnswer();
			if (parm != null && jcaptchaCode1 != null) {
				if (!parm.equals(jcaptchaCode1)) {
					throw new BTSLBaseException(this, METHOD_NAME, "captcha.error.wrongentry", forward);
				}
				c2SReversalModel.setJ_captcha_response(null);
			}
		}

	}


	/**
	 * @param c2SReversalModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 * @throws SQLException 
	 */
	@RequestMapping(value="/display-data-reversal.form", method=RequestMethod.POST)
	public String reversalBack(@ModelAttribute("c2SReversalModel") C2SReversalModel c2SReversalModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws BTSLBaseException, SQLException{
		final String METHOD_NAME = "C2SReversalController: reversalBack";
		try {
			c2SReversalServiceImpl.populateReversalData(c2SReversalModel, getUserFormSession(request).getLoginID(), modelMap);
		} catch (BTSLBaseException  e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
		}
		return  forward;
	}

	
	
	/**
	 * @param c2SReversalModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param modelMap
	 * @return
	 * @throws SQLException
	 */
	@RequestMapping(value="/display-reversal.form", method=RequestMethod.GET)
	public String reversalCancel(@ModelAttribute("c2SReversalModel") C2SReversalModel c2SReversalModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws SQLException{
		final String METHOD_NAME = "C2SReversalController: reversalCancel";
		try {
			c2SReversalServiceImpl.loadReversalPage( getUserFormSession(request).getLoginID(), modelMap);
		} catch (BTSLBaseException  e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
		}
		return  forward;
	}
	
	@RequestMapping(value="/reverse-txnstatus-byId.form", method=RequestMethod.POST)
	public String txnStatusByTransferId(@ModelAttribute("c2SReversalModel") C2SReversalModel c2SReversalModel,
			HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult,ModelMap modelMap) throws SQLException, BTSLBaseException, JsonProcessingException, IOException{
		final String METHOD_NAME = "C2SReversalController: txnStatusByTransferId";
		String returnPage = forward;
		try {
			c2SReversalServiceImpl.txnStatusByTransferId( c2SReversalModel,getUserFormSession(request).getLoginID(),bindingResult, modelMap);
			if(null != modelMap.get("fail")){
				c2SReversalServiceImpl.loadReversalPage( getUserFormSession(request).getLoginID(), modelMap);
				returnPage = forward;
			}
			else{
				returnPage = "c2stransfer/reversalMessage";
			}
		} catch (BTSLBaseException  e) {
			log.error(METHOD_NAME, "Exceptin:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			c2SReversalServiceImpl.loadReversalPage( getUserFormSession(request).getLoginID(), modelMap);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
		}
		return  returnPage;
		
	}
	
}
