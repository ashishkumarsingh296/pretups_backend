package com.web.pretups.user.web;

import java.io.IOException;
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
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.service.AssociateProfileService;
import com.web.user.web.UserModel;

@Controller
public class AssociateProfileController extends CommonController{
	public static final Log LOG = LogFactory.getLog(AssociateProfileController.class
			.getName());
	
	@Autowired
	private AssociateProfileService associateProfileService;
	private static final String CLASS_NAME = "AssociateProfileController";
	private static final String MODEL_KEY = "userModel";
	private static final String FORM_NO = "formNumber";
	private static final String FORM_SUBMITTED = "formSubmitted";
	private static final String FIRST_PAGE= "user/selectChannelCategoryForAssociateView";
	private static final String SECOND_PAGE= "user/associateChannelUserDetailsView";
	private static final String LOGIN_ID = "loginId";
	private static final String USER_ID = "userId";
	private static final String SESSION_OWNER_ID = "sessionOwnerID";
	private static final String PRNT_DOMAIN_CODE = "prntDomainCode";
	private static final String INITIAL_MODEL = "initialModel";
	
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
	@RequestMapping(value = "/user/load-associate-profile.form", method = RequestMethod.GET)
	public String loadAssociateProfile(final Model model,
			HttpServletRequest request, HttpServletResponse response)
					{
		final String methodName = "#loadAssociateProfile";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		UserVO userVO = null;
		if (request.getSession().getAttribute(FORM_SUBMITTED) != null) {
			request.getSession().removeAttribute(FORM_SUBMITTED);

		}
		request.getSession().setAttribute(FORM_NO, "Panel-One");
		try {
			this.authorise(request, response, "ASSCUSR001", false);
			userVO = getUserFormSession(request);
		} catch (ServletException | IOException | BTSLBaseException e) {
			LOG.error(methodName, e.getMessage());
			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, PretupsI.EXCEPTION+e);
			}
		}
		
		UserModel userModel = associateProfileService.loadAssociateProfile(userVO);
		model.addAttribute(MODEL_KEY, userModel);
		request.getSession().setAttribute(INITIAL_MODEL, userModel);
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
	@RequestMapping(value = "/user/searchAssociateUser.form", method = RequestMethod.GET)
	@ResponseBody public List<Map<String, String>> userList(Model model,
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
		
		if(BTSLUtil.isNullString(categorycode)){
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
		List<UserVO> userList = associateProfileService.loadUserList(userVO, categorycode, ownerID, userName, domainCode, prntDomainCode, request, index);

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

	@RequestMapping(value = "/user/searchAssociateOwner.form", method = RequestMethod.GET)
	@ResponseBody public List<Map<String, String>> ownerList(Model model,
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

		List<UserVO> userList = associateProfileService.loadOwnerList(userVO, prntDomainCode, ownerName, domainCode, request);
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
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/user/backAssociate.form", method = RequestMethod.GET)
	public String loadBackAssociateForm(final Model model,
			HttpServletRequest request, HttpServletResponse response){
		final String methodName = "#loadBackAssociateForm";

		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		return FIRST_PAGE;

	}
	
	@RequestMapping(value = "/user/changeCategoryAssociate.form", method = RequestMethod.GET)
	@ResponseBody public List loadCategory(@RequestParam(value="domainC",required=true)String domainCode, @ModelAttribute("userModel") UserModel userModel){
		final String methodName = "#loadCategory";

		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		userModel.setDomainCode(domainCode);
		associateProfileService.getCategoryList(userModel);
		return userModel.getCategoryList();
	}
	
	
	@RequestMapping(value = "/user/load-associated-profile-details.form", method = RequestMethod.POST)
	public String loadAssociatedProfileDetails(final Model model,HttpServletRequest request, HttpServletResponse response, @ModelAttribute("userModel") UserModel userModel,
			BindingResult bindingResult) {
		final String methodName = "#loadAssociatedProfileDetails";
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		ChannelUserVO userVO = null;
		try {
			userVO = (ChannelUserVO)this.getUserFormSession(request);
			 
			 
			UserModel finalModel = associateProfileService.loadAssociateProfile(userVO);
			String geoDomainName = finalModel.getParentDomainDesc();
			String geoDomainCode = finalModel.getParentDomainCode();
			if(userModel.getSearchLoginId() != null){
				finalModel.setSearchLoginId(userModel.getSearchLoginId());
			}
			
			if(userModel.getSearchMsisdn() != null){
				finalModel.setSearchMsisdn(userModel.getSearchMsisdn());
			}
				
			if(userModel.getDomainCode() != null){
				finalModel.setDomainCode(userModel.getDomainCode());
			}
			if(userModel.getChannelCategoryCode() != null){
				finalModel.setChannelCategoryCode(userModel.getChannelCategoryCode());
			}
			if(userModel.getParentDomainCode() != null){
				finalModel.setParentDomainCode(userModel.getParentDomainCode());
			}
			if(userModel.getUserId() != null){
				finalModel.setUserId(userModel.getUserId());
			}
			
			boolean result = associateProfileService.getAssociationDetails(userVO, finalModel, model, bindingResult, request);
			if(finalModel.getDomainList() != null){
			associateProfileService.getCategoryList(finalModel);
			}
			if(userModel.getParentDomainCode() != null){
			finalModel.setParentDomainCode(userModel.getParentDomainCode());
			}
			finalModel.setChannelCategoryCode(userModel.getChannelCategoryCode());
			if(userModel.getUserId() != null && !userModel.getUserId().isEmpty()){
			finalModel.setUserId(userModel.getUserId());
			}
			if(request.getSession().getAttribute(SESSION_OWNER_ID) != null){
				String ownerID = request.getSession().getAttribute(SESSION_OWNER_ID).toString();
				finalModel.setOwnerID(ownerID);
			}
			
			if(userModel.getDomainCode() != null || !userModel.getDomainCode().isEmpty()){
			for(int i=0; i<finalModel.getCategoryList().size(); i++){
				CategoryVO categoryVO;
				categoryVO = (CategoryVO) finalModel.getCategoryList().get(i);

				if (categoryVO.getCategoryCode().equals(finalModel.getChannelCategoryCode())) {
					finalModel.setChannelCategoryDesc(categoryVO.getCategoryName());
				}
			}
			
			if(finalModel.getDomainList() != null){
			for(int i=0; i<finalModel.getDomainList().size(); i++){
				ListValueVO listValueVO;
				listValueVO = (ListValueVO) finalModel.getDomainList().get(i);

				if (listValueVO.getValue().equals(finalModel.getDomainCode())) {
					finalModel.setDomainCodeDesc(listValueVO.getLabel());
				}
			}
			}
			}
			if(finalModel.getAssociatedGeographicalList() != null){
				for(int i=0; i<finalModel.getAssociatedGeographicalList().size(); i++){
					UserGeographiesVO userGeographiesVO;
					userGeographiesVO = (UserGeographiesVO) finalModel.getAssociatedGeographicalList().get(i);
					if(userGeographiesVO.getGraphDomainCode().equals(finalModel.getParentDomainCode())){
						finalModel.setParentDomainDesc(userGeographiesVO.getGraphDomainName());
					}
				}
			}else{
				finalModel.setParentDomainCode(geoDomainCode);
				finalModel.setParentDomainDesc(geoDomainName);
			}
			request.getSession().setAttribute(FORM_SUBMITTED, finalModel);
			model.addAttribute(MODEL_KEY, finalModel);
			if(result){
				return SECOND_PAGE;
			}
		} catch (BTSLBaseException e) {
			LOG.error(methodName, e.getMessage());
			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, "Exception:"+e);
			}
		}
		return FIRST_PAGE;
	}
	
	@RequestMapping(value = "/user/submit-associateUser.form", method = RequestMethod.POST)
	public String processProfileAssociation(final Model model, HttpServletRequest request, HttpServletResponse response,@ModelAttribute("userModel") UserModel userModel,
			BindingResult bindingResult) {
		final String methodName = "#processProfileAssociation";
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		UserVO userVO = null;
		
		try {
			userVO = this.getUserFormSession(request);
		} catch (BTSLBaseException e) {
			LOG.error(methodName, e.getMessage());
			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, PretupsI.EXCEPTION+e);
			}
		}
		UserModel finalModel = (UserModel)request.getSession().getAttribute(FORM_SUBMITTED);
		finalModel.setUserGradeId(userModel.getUserGradeId());
		finalModel.setRsaAuthentication(userModel.getRsaAuthentication());
		finalModel.setMcommerceServiceAllow(userModel.getMcommerceServiceAllow());
		finalModel.setMpayProfileID(userModel.getMpayProfileID());
		finalModel.setCommissionProfileSetId(userModel.getCommissionProfileSetId());
		finalModel.setTrannferProfileId(userModel.getTrannferProfileId());
		finalModel.setTrannferRuleTypeId(userModel.getTrannferRuleTypeId());
		finalModel.setLmsProfileId(userModel.getLmsProfileId());
		finalModel.setControlGroup(userModel.getControlGroup());
		boolean result = associateProfileService.processProfileAssociation(finalModel, userVO, model, bindingResult);
		if(result){
			if(request.getSession().getAttribute(FORM_SUBMITTED) != null){
				request.getSession().removeAttribute(FORM_SUBMITTED);
			}
			UserModel userModelFinal = associateProfileService.loadAssociateProfile(userVO);
			model.addAttribute(MODEL_KEY, userModelFinal);	
		return FIRST_PAGE;
		}
		else{
			return SECOND_PAGE;
		}
	}
}
