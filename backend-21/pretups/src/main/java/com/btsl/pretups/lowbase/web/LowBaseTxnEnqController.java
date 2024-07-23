package com.btsl.pretups.lowbase.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.lowbase.businesslogic.LowBasedRechargeVO;
import com.btsl.pretups.lowbase.service.LowBaseTxnEnqService;

/**
 * This class provides basic method for Low Base Transaction modules
 * @author lalit.chattar
 *
 */

@Controller
public class LowBaseTxnEnqController extends CommonController {

	
	private static final String CLASS_NAME = "LowBaseTxnEnqController";
	
	@Autowired
	private LowBaseTxnEnqService lowBaseTxnEnqService;
	private static final String LOW_BASE_VO_CONSTANT = "basedRechargeVO";
	
	/**
	 * 
	 * Load Low Base Transaction enquiry form	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model Object
	 * @return String the path of view
	 */
	@RequestMapping(value="/lowbase/low_base_transaction_enquiry.form", method=RequestMethod.GET)
	public String loadLowBaseTxnEnqForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String methodName = "#loadLowBaseTxnEnqForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}

		try {
			authorise(request, response, "LOBRC00A", false);
			
			if(request.getSession().getAttribute(LOW_BASE_VO_CONSTANT) != null && request.getSession().getAttribute(PretupsI.BACK_BUTTON_CLICKED) != null){
				model.addAttribute(LOW_BASE_VO_CONSTANT, request.getSession().getAttribute(LOW_BASE_VO_CONSTANT));
				request.getSession().removeAttribute(PretupsI.BACK_BUTTON_CLICKED);
			}else{
				model.addAttribute(LOW_BASE_VO_CONSTANT, new LowBasedRechargeVO());
			}
			
		} catch (ServletException | IOException | BTSLBaseException exception) {
			
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return "lowbase/low_base_transaction_enquiry";
		
	}
	
	
	/**
	 * 
	 * Process Low Base Subscriber Eligibility Enquiry form	 *
	 * @param basedRechargeVO  The LowBasedRechargeVO object
	 * @param bindingResult The BindingResult object
	 * @param model The Model Object
	 * @param request The HttpServletRequest object
	 * @return String the path of view
	 */
	@RequestMapping(value="/lowbase/process_low_base_transaction_enquiry.form", method=RequestMethod.POST)
	public String processLowBaseTransactionEnquiryForm(@ModelAttribute(LOW_BASE_VO_CONSTANT) LowBasedRechargeVO basedRechargeVO, BindingResult bindingResult, final Model model, HttpServletRequest request) throws BTSLBaseException{
		
		final String methodName = "#processLowBaseSubEligibilityForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}

		try {
			if(lowBaseTxnEnqService.processLowBaseTransactionEnquiryForm(basedRechargeVO, bindingResult, model)){
				request.getSession().setAttribute(LOW_BASE_VO_CONSTANT, basedRechargeVO);
				return "lowbase/show_low_base_transaction_details";
			}else{
				if (bindingResult.hasGlobalErrors()) {
					model.addAttribute("fail", true);
				}
			}
			return "lowbase/low_base_transaction_enquiry";
		} catch (BTSLBaseException | NoSuchMessageException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
			}
		}
		
	}
	
	
	/**
	 * 
	 * Load Low Base Subscriber Eligibility Enquiry form	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model Model Object
	 * @return String the path of view
	 */
	@RequestMapping(value="/lowbase/low_base_subscriber_eligibility_enquiry.form", method=RequestMethod.GET)
	public String loadLowBaseSubEligibilityForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String methodName = "#loadLowBaseSubEligibilityForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}

		try {
			authorise(request, response, "LBSEE00A", false);
			
			if(request.getSession().getAttribute(LOW_BASE_VO_CONSTANT) != null && request.getSession().getAttribute(PretupsI.BACK_BUTTON_CLICKED) != null){
				model.addAttribute(LOW_BASE_VO_CONSTANT, request.getSession().getAttribute(LOW_BASE_VO_CONSTANT));
				request.getSession().removeAttribute(PretupsI.BACK_BUTTON_CLICKED);
			}else{
				model.addAttribute(LOW_BASE_VO_CONSTANT, new LowBasedRechargeVO());
			}
			
		} catch (ServletException | IOException | BTSLBaseException exception) {
			
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return "lowbase/low_base_subscriber_eligibility_enquiry";
		
	}
	
	
	
	/**
	 * 
	 * Process Low Base Subscriber Eligibility Enquiry form	 *
	 * @param basedRechargeVO  The LowBasedRechargeVO object
	 * @param bindingResult The BindingResult object
	 * @param model The Model Object
	 * @param request The HttpServletRequest object
	 * @return String the path of view
	 */
	@RequestMapping(value="/lowbase/process_low_base_subscriber_eligibility_enquiry.form", method=RequestMethod.POST)
	public String processLowBaseSubEligibilityForm(@ModelAttribute(LOW_BASE_VO_CONSTANT) LowBasedRechargeVO basedRechargeVO, BindingResult bindingResult, final Model model, HttpServletRequest request) throws BTSLBaseException{
		
		final String methodName = "#processLowBaseSubEligibilityForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}

		try {
			if(lowBaseTxnEnqService.processLowBaseSubEligibilityRequest(basedRechargeVO, bindingResult, model)){
				request.getSession().setAttribute(LOW_BASE_VO_CONSTANT, basedRechargeVO);
				return "lowbase/show_low_base_eligibility_details";
			}else{
				if (bindingResult.hasGlobalErrors()) {
					model.addAttribute("fail", true);
				}
			}
			return "lowbase/low_base_subscriber_eligibility_enquiry";
		} catch (BTSLBaseException | NoSuchMessageException e) {
			throw new BTSLBaseException(e);
		}finally{
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
			}
		}
		
	}
	
	
	/**
	 * 
	 * Go back to the Low Base Transaction eligibility form
	 * @param request  The HttpServletRequest object
	 * @return String the path of view
	 */
	@RequestMapping(value="/lowbase/back_low_base_subscriber_eligibility_enquiry.form", method=RequestMethod.GET)
	public String backLowBaseSubEligibilityForm(HttpServletRequest request) {
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		return "redirect:/lowbase/low_base_subscriber_eligibility_enquiry.form";
		
	}
	
	
	
	/**
	 * 
	 * Go back to the Low Base Transaction Enquiry form
	 * @param request  The HttpServletRequest object
	 * @return String the path of view
	 */
	@RequestMapping(value="/lowbase/back_low_base_transaction_enquiry.form", method=RequestMethod.GET)
	public String backLowBaseEnquiryForm(HttpServletRequest request) {
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		return "redirect:/lowbase/low_base_transaction_enquiry.form";
		
	}
	
	
	
}
