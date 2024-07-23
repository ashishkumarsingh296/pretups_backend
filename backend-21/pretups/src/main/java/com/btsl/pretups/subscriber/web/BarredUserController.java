package com.btsl.pretups.subscriber.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.SubLookUpVO;
import com.btsl.pretups.subscriber.businesslogic.BarredUserVO;
import com.btsl.pretups.subscriber.service.BarredUserService;
import com.btsl.user.businesslogic.UserVO;

/**
 * This class provides method for loading UI for Bar user as well as 
 * processing data for Bar user request
 */
@Controller
public class BarredUserController extends CommonController {

	@Autowired
	private BarredUserService barredUserService;
	private static final String MODEL_KEY = "barredUser";
	private static final String SESSION_KEY="unbarUserObject";
	private static final String VIEW_BAR_USER_KEY = "viewBarredUserObject";
	private static final String MODULE_LIST = "moduleList";
	private static final String USER_TYPE="userType";
	private static final String BAR_TYPE = "barredType";
	private static final String UNBAR_URL = "subscriber/unbarUser";
	
	/**
	 * Load bar user UI as well as modules and user type 
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/baruser/barreduser.form", method = RequestMethod.GET)
	public String loadBarUserForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
			 {

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadBarUserForm", PretupsI.ENTERED);
		}

		authorise(request, response, "BAR01A", false);

		UserVO userVO = this.getUserFormSession(request);

		model.addAttribute(MODEL_KEY, new BarredUserVO());
		
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());
		
		request.getSession().removeAttribute(BAR_TYPE);
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadBarUserForm", PretupsI.EXITED);
		}
		return "subscriber/barredUser";
	}

	/**
	 * Load bar type on the basis of module
	 *
	 * @param module  The module type user selected from drop-down
	 * @param request  The HttpServletRequest object
	 * @param model The Model object
	 * @return JSON response having list of Sublookups
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/baruser/load-barring-type.form", method = RequestMethod.POST)
	public @ResponseBody List<SubLookUpVO> loadBarringType(@RequestParam("module") String module, final Model model,
			HttpServletRequest request) throws BTSLBaseException, IOException {

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadBarringType", PretupsI.ENTERED);
		}
		UserVO userVO = this.getUserFormSession(request);
		List<SubLookUpVO> barringType = barredUserService.loadBarringType(module, userVO.getUserType());
		
		
		request.getSession().setAttribute(BAR_TYPE, barringType);
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadBarringType", PretupsI.EXITED);
		}
		return barringType;
	}

	/**
	 * Handle the bar user request and process data
	 *
	 * @param barUser  BarredUserVO object 
	 * @param bindingResult for handling validation error messages
	 * @param model The Model object
	 * @param request  The HttpServletRequest object
	 * @return String the path of view for showing messages
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws NoSuchMessageException 
	 * @throws ServletException 
	 * @throws NoSuchAlgorithmException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/baruser/submit-bar-user.form", method = RequestMethod.POST)
	public String processBarredUser(@ModelAttribute("barredUser") BarredUserVO barUser, BindingResult bindingResult,
			final Model model, HttpServletRequest request) throws BTSLBaseException, NoSuchMessageException, IOException, NoSuchAlgorithmException, ServletException {

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processBarredUser", PretupsI.ENTERED);
		}
		
		UserVO userVO = this.getUserFormSession(request);
		
		if(csrfcheck(request, model)){
			 return "common/csrfmessage";
		}
		
		if (barredUserService.addBarUser(barUser, userVO, bindingResult)) {
			model.addAttribute("success", PretupsRestUtil.getMessageString("subscriber.barreduser.add.mobile.success", new String[] { barUser.getMsisdn() }));
			model.addAttribute(MODEL_KEY, new BarredUserVO());
			request.getSession().removeAttribute(BAR_TYPE);
		} else {
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute("fail", true);
			}
		}
		
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		
		
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processBarredUser", PretupsI.EXITED);
		}
		return "subscriber/barredUser";

	}
	
	
	/**
	 * Load view bar user UI as well as modules and user type 
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws IOException, ServletException, BTSLBaseException
	 */
	@RequestMapping(value = "/baruser/viewBarredUserAction.form", method = RequestMethod.GET)
	public String loadViewBarUserForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BTSLBaseException {

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadViewBarUserForm", PretupsI.ENTERED);
		}
		
		authorise(request, response, "VIEWBAR01A", false);

		UserVO userVO = this.getUserFormSession(request);

		if(request.getSession().getAttribute(VIEW_BAR_USER_KEY) != null && request.getSession().getAttribute(PretupsI.BACK_BUTTON_CLICKED) != null){
			model.addAttribute(MODEL_KEY, request.getSession().getAttribute(VIEW_BAR_USER_KEY));
			request.getSession().removeAttribute(PretupsI.BACK_BUTTON_CLICKED);
		}else{
			model.addAttribute(MODEL_KEY, new BarredUserVO());
			request.getSession().removeAttribute(BAR_TYPE);
		}
		
		
		
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		if(userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
			model.addAttribute("isOperatorUser", true);
		}
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadViewBarUserForm", PretupsI.EXITED);
		}
		return "subscriber/selectBarredUser";
	}
	
	
	/**
	 * Handle the view bar user request and process data
	 *
	 * @param barUser  BarredUserVO object 
	 * @param bindingResult for handling validation error messages
	 * @param model The Model object
	 * @param request  The HttpServletRequest object
	 * @return String the path of view for showing messages
	 * @throws IOException, BTSLBaseException
	 */
	@RequestMapping(value = "/baruser/submit-view-bar-user.form", method = RequestMethod.POST)
	public String processViewBarUserForm(@ModelAttribute("barredUser") BarredUserVO barUser, BindingResult bindingResult,
			final Model model, HttpServletRequest request) throws IOException, BTSLBaseException{

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processViewBarUserForm", PretupsI.ENTERED);
		}
		
		UserVO userVO = this.getUserFormSession(request);
		List<BarredUserVO> barredUserList = new ArrayList<>();
		Boolean flag = barredUserService.viewBarUserList(barUser, userVO, bindingResult, barredUserList);
		if(flag){
			model.addAttribute("barredUserList", barredUserList);
			request.getSession().setAttribute(VIEW_BAR_USER_KEY, barUser);
			return "subscriber/viewBarredList";
		}else{
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute("fail", true);
			}
		}
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());

		
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		if(userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
			model.addAttribute("isOperatorUser", true);
		}
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processViewBarUserForm", PretupsI.EXITED);
		}
		
		return "subscriber/selectBarredUser";
		
	}
	
	/**
	 * Handle back button functionality for view Barred List
	 * @return String the path of view for showing messages
	 * @param request HttpServletRequest Object
	 */
	@RequestMapping(value="/baruser/back-to-the-view-barred-list.form", method=RequestMethod.GET)
	public String backToTheViewBarList(HttpServletRequest request){
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		return "redirect:/baruser/viewBarredUserAction.form";
	}
	
	/**
	 * Handle back button functionality for Un Barred List
	 * @return String the path of view for showing messages
	 * @param request HttpServletRequest Object
	 */
	@RequestMapping(value="/baruser/back-to-the-unbar-user.form", method=RequestMethod.GET)
	public String backToTheUnbarUser(HttpServletRequest request){
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		return "redirect:/baruser/unbaruser.form";
	}
	
	
	/**
	 * Load unbar user UI as well as modules and user type 
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws IOException, ServletException, BTSLBaseException
	 */
	@RequestMapping(value = "/baruser/unbaruser.form", method = RequestMethod.GET)
	public String loadUnbarUserForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BTSLBaseException {

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadUnbarUserForm", PretupsI.ENTERED);
		}

		authorise(request, response, "UNBAR01A", false);

		UserVO userVO = this.getUserFormSession(request);

		if(request.getSession().getAttribute(SESSION_KEY) != null && request.getSession().getAttribute(PretupsI.BACK_BUTTON_CLICKED) != null){
			model.addAttribute(MODEL_KEY, request.getSession().getAttribute(SESSION_KEY));
			request.getSession().removeAttribute(PretupsI.BACK_BUTTON_CLICKED);
		}else{
			model.addAttribute("unbarredUser", new BarredUserVO());
			request.getSession().removeAttribute(BAR_TYPE);
		}
		
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());

		
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#loadUnbarUserForm", PretupsI.EXITED);
		}
		return UNBAR_URL;
	}
	
	
	/**
	 * Handle the unbar user request and process data
	 *
	 * @param barUser  BarredUserVO object 
	 * @param bindingResult for handling validation error messages
	 * @param model The Model object
	 * @param request  The HttpServletRequest object
	 * @return String the path of view for showing messages
	 * @throws IOException, BTSLBaseException
	 */
	@RequestMapping(value = "/baruser/submit-un-bar-user.form", method = RequestMethod.POST)
	public String processUnBarUserForm(@ModelAttribute("unbarredUser") BarredUserVO barUser, BindingResult bindingResult,
			final Model model, HttpServletRequest request) throws IOException, BTSLBaseException{

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processUnBarUserForm", PretupsI.ENTERED);
		}
		
		UserVO userVO = this.getUserFormSession(request);
		
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());

		barUser.setModuleName(PretupsRestUtil.getOptionDescription(barUser.getModule(), moduleList).getLabel());
		barUser.setUserTypeName(PretupsRestUtil.getOptionDescription(barUser.getUserType(), userType).getLabel());
        barUser.setNetworkName(userVO.getNetworkName());
        
		List<BarredUserVO> barredUserList = new ArrayList<>();
		Boolean flag = barredUserService.processUnBarUser(barUser, userVO, bindingResult, barredUserList);
		if(flag){
			model.addAttribute("barredUserList", barredUserList);
			model.addAttribute("baruserDetail", barUser);
			request.getSession().setAttribute(SESSION_KEY, barUser);
			return "subscriber/selectUserToUnbarr";
		}else{
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute("fail", true);
			}
		}
		
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processUnBarUserForm", PretupsI.EXITED);
		}
		
		return UNBAR_URL;
		
	}
	
	
	/**
	 * Process the selected barred user to unabar 
	 * @param barUser  BarredUserVO object 
	 * @param bindingResult for handling validation error messages
	 * @param model The Model object
	 * @param request  The HttpServletRequest object
	 * @return String the path of view for showing messages
	 * @throws IOException, BTSLBaseException
	 */
	@RequestMapping(value = "/baruser/submit-barred-user-to-un-bar-user.form", method = RequestMethod.POST)
	public String processSelectedBarredUserToUnbar(@ModelAttribute("unbarredUser") BarredUserVO barUser, BindingResult bindingResult,
			final Model model, HttpServletRequest request) throws IOException, BTSLBaseException{

		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processSelectedBarredUserToUnbar", PretupsI.ENTERED);
		}
		
		
		UserVO userVO = this.getUserFormSession(request);
		
		List<ListValueVO> moduleList = barredUserService.loadModules(userVO.getUserType());
		List<ListValueVO> userType = barredUserService.loadUserType(userVO.getUserType());
		
		model.addAttribute(MODULE_LIST, moduleList);
		model.addAttribute(USER_TYPE, userType);
		
		if(barredUserService.processSelectedBarredUserToUnbar(barUser, userVO, bindingResult)){
			model.addAttribute("success", PretupsRestUtil.getMessageString("subscriber.barreduser.remove.mobile.success"));
			model.addAttribute("unbarredUser", new BarredUserVO());
			return UNBAR_URL;
		}else{
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute("fail", true);
			}
		}
		
		if (log.isDebugEnabled()) {
			log.debug("BarredUserController#processSelectedBarredUserToUnbar", PretupsI.EXITED);
		}
		return UNBAR_URL;
		
	}


}
