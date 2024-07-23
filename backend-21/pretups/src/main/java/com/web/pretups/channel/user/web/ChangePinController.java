package com.web.pretups.channel.user.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;


import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.user.service.ChangePinService;
import com.web.pretups.user.service.UserBalanceService;
import com.web.user.web.UserModel;

/**
 * @author ayush.abhijeet
 *
 */
@Controller
public class ChangePinController extends CommonController {



	@Autowired
	private ChangePinService changePinService;
	@Autowired
	private UserBalanceService userBalanceService;
	ChannelUserVO channelUserVO;
	private static final String CLASS_NAME = "loadSelfPin";
	private static final String RETURN_PAGE = "user/changePinView";
	private static final String MODEL_KEY = "userModel";
	private static final String DATA_LIST = "dataList";
	private static final String CHANGE_PIN = "changeSmsPin";
	private static final String CHANGE_SELF_PIN = "changeSelfPin";
	private static final String REQUEST_TYPE = "requestType";
	private static final String FORM_NO = "formNumber";
	private static final String FORM_SUBMITTED = "formSubmitted";
	private static final String FIRST_PAGE= "user/selectChannelCategoryForChangePinView";
	private static final String EMAIL = "email";
	private static final String LOGIN_ID = "loginId";
	private static final String USER_ID = "userId";
	private static final String SESSION_OWNER_ID = "sessionOwnerID";
	private static final String PRNT_DOMAIN_CODE = "prntDomainCode";
	
	/**
	 * 
	 * @param userModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/user/change-self-pin.form", method = RequestMethod.GET)
	public String loadSelfPin(@ModelAttribute("userModel") UserModel userModel,
			final Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException {
		final String methodName = "#loadSelfPin";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		try {
			request.getSession().removeAttribute("module");
			request.getSession().removeAttribute(DATA_LIST);
			request.getSession().removeAttribute(EMAIL);
			this.authorise(request, response, "CHSLPIN001", false);
		} catch (ServletException | IOException | BTSLBaseException exception) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		channelUserVO = (ChannelUserVO) this.getUserFormSession(request);

		ArrayList<UserPhoneVO> userPhoneList = changePinService.loadSelfPin(
				channelUserVO, model);
		userModel.setMsisdnList(userPhoneList);
		request.getSession().setAttribute(DATA_LIST, userPhoneList);
		request.getSession().setAttribute(REQUEST_TYPE, CHANGE_SELF_PIN);
		if (userPhoneList.isEmpty()) {
			log.error("loadSelfPin", "Error: No number assigned to user.");
			model.addAttribute("fail", PretupsRestUtil
					.getMessageString("pretups.user.changepin.msg.nonumberassign"));
			return RETURN_PAGE;
		}
		model.addAttribute(MODEL_KEY, userModel);
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
		}

		return RETURN_PAGE;

	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @param userModel
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 * @throws NoSuchAlgorithmException
	 * @throws ServletException
	 */
	@RequestMapping(value = "/user/submit-change-pin.form", method = RequestMethod.POST)
	public String processChangePin(final Model model,
			HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("userModel") UserModel userModel,
			BindingResult bindingResult) throws BTSLBaseException,
			NoSuchAlgorithmException, ServletException {

		final String methodName = "#processChangePin";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		@SuppressWarnings("unchecked")
		ArrayList<UserPhoneVO> phoneList = (ArrayList<UserPhoneVO>) request
				.getSession().getAttribute(DATA_LIST); 
		if(request.getSession().getAttribute(EMAIL) != null){
			userModel.setEmail(request.getSession().getAttribute(EMAIL).toString());
		}

		if (csrfcheck(request, model)) {
			return "common/csrfmessage";
		}
		String requestType = (String) request.getSession().getAttribute(
				REQUEST_TYPE);
		userModel.setRequestType(requestType);
		try {

			channelUserVO = (ChannelUserVO) this.getUserFormSession(request);
			List<String> errorList = changePinService.processData(userModel,
					channelUserVO, model, bindingResult, phoneList, request);

			model.addAttribute(MODEL_KEY, userModel);
			if (!errorList.isEmpty()) {
				model.addAttribute("errors_list", errorList);
				return RETURN_PAGE;
			}

		} catch (BTSLBaseException exception) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
		}
		channelUserVO = (ChannelUserVO) this.getUserFormSession(request);

		if("changeSelfPin".equals(userModel.getRequestType())){
		ArrayList<UserPhoneVO> userPhoneList = changePinService.loadSelfPin(
				channelUserVO, model);
		userModel.setMsisdnList(userPhoneList);
		request.getSession().setAttribute(DATA_LIST, userPhoneList);
		model.addAttribute(MODEL_KEY, userModel);
		}
		return RETURN_PAGE;
	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/user/change-pin.form", method = RequestMethod.GET)
	public String loadCategoryForm(final Model model,
			HttpServletRequest request, HttpServletResponse response)
					throws BTSLBaseException, ServletException, IOException {
		final String methodName = "#loadCategoryForm";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		if (request.getSession().getAttribute(FORM_SUBMITTED) != null) {
			request.getSession().removeAttribute(FORM_SUBMITTED);

		}
		request.getSession().setAttribute(FORM_NO, "Panel-One");
		this.authorise(request, response, "CHNGPIN001", false);
		UserVO userVO = getUserFormSession(request);

		UserModel userModel = userBalanceService.loadCategory(userVO);
		model.addAttribute(MODEL_KEY, userModel);
		request.getSession().setAttribute(MODEL_KEY, userModel);
		if (userModel.getCategoryList().isEmpty()) {
			model.addAttribute(
					"errorMessage",
					PretupsRestUtil
					.getMessageString("pretups.user.changepin.loaddomainlist.error.noagentcategoryfound"));
		}
		request.getSession().setAttribute(REQUEST_TYPE, CHANGE_PIN);
		return FIRST_PAGE;

	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param userModel
	 * @param response
	 * @return
	 * @throws BTSLBaseException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/user/SearchUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList(Model model,
			HttpServletRequest request,
			@ModelAttribute("userModel") UserModel userModel,
			HttpServletResponse response) throws BTSLBaseException 

			{
		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("query");
		userName = userName + "%";
		String categorycode = request.getParameter("category");
		String ownerID = request.getParameter("owner");
		
		if(!ownerID.isEmpty() && ownerID != null){
		request.getSession().setAttribute(SESSION_OWNER_ID, ownerID);
		String[] parts = ownerID.split("\\(");
		String[] part = parts[1].split("\\)");
		ownerID = part[0];
		}
		request.getSession().setAttribute("ownerID", ownerID);
		String domainCode = request.getParameter("domainCode");
		String prntDomainCode = request.getParameter(PRNT_DOMAIN_CODE);
		String index = request.getParameter("index");
		request.getSession().setAttribute("index", index);
		request.getSession().setAttribute(PRNT_DOMAIN_CODE, prntDomainCode);
		final UserVO userVO =  getUserFormSession(request);
		
		if(BTSLUtil.isNullString(domainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.domain"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if(BTSLUtil.isNullString(prntDomainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.geography"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}
		
		if(Integer.parseInt(index) == 0){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.category"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if(!("1".equalsIgnoreCase(index)) && !("CHANNEL".equalsIgnoreCase(userVO.getUserType())) && BTSLUtil.isNullString(ownerID)){

			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.owner"));
			map.put(USER_ID, "");
			list.add(map);
			return list;

		}
		List<UserVO> userList = userBalanceService.loadUserList(userVO, categorycode, ownerID, userName, domainCode, prntDomainCode, request, index);

		Iterator<UserVO> itr = userList.iterator();
		
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			UserVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + "(" + object.getUserID()
					+ ")";
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getUserID());
			list.add(map);
		}

		return list;

			}

	@RequestMapping(value = "/user/searchOwner.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> ownerList(Model model,
			HttpServletRequest request,
			@ModelAttribute("userModel") UserModel userModel,
			HttpServletResponse response) throws BTSLBaseException 

			{
		List<Map<String, String>> list = new ArrayList<>();
		String ownerName = request.getParameter("query");
		ownerName = ownerName + "%";
		String domainCode = request.getParameter("domainCode");
		String prntDomainCode = request.getParameter(PRNT_DOMAIN_CODE);
		final UserVO userVO = getUserFormSession(request);
		
		if(BTSLUtil.isNullString(domainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.domain"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if(BTSLUtil.isNullString(prntDomainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.geography"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		List<UserVO> userList = userBalanceService.loadOwnerList(userVO, prntDomainCode, ownerName, domainCode, request);
		Iterator<UserVO> itr = userList.iterator();
		
		
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			UserVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + "(" + object.getUserID()
					+ ")";
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getLoginID());
			list.add(map);
		}

		return list;

	}

	
	
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @param userModel
	 * @param bindingResult
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BTSLBaseException 
	 */
	@RequestMapping(value = "/user/submit-changepin.form", method = RequestMethod.POST)
	public String loadChangePinForm(final Model model,
			HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("userModel") UserModel userModel,
			BindingResult bindingResult) throws ServletException, IOException, BTSLBaseException {
		final String methodName = "#loadChangePinForm";
		request.getSession().removeAttribute(DATA_LIST);
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		
		try {
			channelUserVO = (ChannelUserVO) this.getUserFormSession(request);
		} catch (BTSLBaseException exception) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}

		UserVO userVO = getUserFormSession(request);

		UserModel finalModel = userBalanceService.loadCategory(userVO);
		finalModel.setDomainName(finalModel.getDomainCodeDesc());
		if(userModel.getSearchLoginId() != null){
			finalModel.setSearchLoginId(userModel.getSearchLoginId());
		}
		
		if(userModel.getSearchMsisdn() != null){
			finalModel.setSearchMsisdn(userModel.getSearchMsisdn());
		}
			
		if(userModel.getDomainCodeDesc() != null){
			finalModel.setDomainCodeDesc(userModel.getDomainCodeDesc());
		}
		if(userModel.getChannelCategoryCode() != null){
			finalModel.setChannelCategoryCode(userModel.getChannelCategoryCode());
		}
		if(userModel.getParentDomainDesc() != null){
			finalModel.setParentDomainDesc(userModel.getParentDomainDesc());
		}
		if(userModel.getEventRemarks() != null){
			finalModel.setEventRemarks(userModel.getEventRemarks());
		}
		if(userModel.getUserId() != null){
			finalModel.setUserId(userModel.getUserId());
		}
		boolean result = changePinService.changePin(model, channelUserVO,
				finalModel, bindingResult, request);
		if(request.getSession().getAttribute(SESSION_OWNER_ID) != null){
		String ownerID = request.getSession().getAttribute(SESSION_OWNER_ID).toString();
		finalModel.setOwnerID(ownerID);
		}	
		String domainCode = finalModel.getDomainCode();
		finalModel.setDomainCode(finalModel.getDomainCodeDesc());
		if(finalModel.getDomainList() != null){
			userBalanceService.getCategoryList(finalModel);
		}
		finalModel.setDomainCode(domainCode);
		finalModel.setParentDomainDesc(userModel.getParentDomainDesc());
		finalModel.setChannelCategoryCode(userModel.getChannelCategoryCode());
		finalModel.setUserId(userModel.getUserId());
		request.getSession().setAttribute("formSubmitted", finalModel);
		if (result) {
			request.getSession().setAttribute(DATA_LIST,
					finalModel.getMsisdnList());
			model.addAttribute(MODEL_KEY, finalModel);
			return RETURN_PAGE;
		}

		model.addAttribute(MODEL_KEY, userModel);
		return FIRST_PAGE;

	}

	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/user/backChangePin.form", method = RequestMethod.GET)
	public String loadBackChangePinForm(final Model model,
			HttpServletRequest request, HttpServletResponse response)
					throws BTSLBaseException, ServletException, IOException {
		final String methodName = "#loadBackChangePinForm";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		return FIRST_PAGE;

	}
	
	@RequestMapping(value = "/user/changeCategory.form", method = RequestMethod.GET)
	public @ResponseBody List loadCategory(@RequestParam(value="domainC",required=true)String domainCode, @ModelAttribute("userModel") UserModel userModel){
		final String methodName = "#loadCategory";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		userModel.setDomainCode(domainCode);
		userBalanceService.getCategoryList(userModel);
		return userModel.getCategoryList();
	}
}
