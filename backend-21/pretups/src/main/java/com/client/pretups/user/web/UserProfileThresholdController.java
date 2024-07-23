package com.client.pretups.user.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.client.pretups.user.service.UserProfileThresholdService;
import com.web.user.web.UserModel;

/**
 * This class provides method for loading UI for Bar user as well as
 * processing data for Bar user request
 */
@Controller
public class UserProfileThresholdController extends CommonController {

	private static final String MODEL_KEY = "userModel";
	private static final String CATEGORY_KEY = "categoryList";
	private static final String DOMAIN_LIST = "domainList";
	private static final String CLASS_NAME = "UserProfileThresholdController";
	private static final String PANEL_NAME = "formNumber";
	private static final String GEOGRAPHY_LIST = "geographyList";
	private static final String USER_LIST = "userList";
	private static final String PARENT_USER_LIST = "parentUserList";
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "Select Category Name First";
	private static final String FIRST_PAGE = "user/UserProfileThresholdView";
	private static final String LOGIN_ID = "loginId";
	private static final String USER_ID = "userId";
	private static final String CATAGORY = "category";

	private List<CategoryVO> categoryList = null;

	@Autowired
	private UserProfileThresholdService userProfileThresholdService;

	/**
	 * Load bar user UI as well as modules and user type
	 *
	 * @param request
	 *            The HttpServletRequest object
	 * @param response
	 *            The HttpServletResponse object
	 * @param model
	 *            The Model object
	 * @return String the path of view also store user type and module in model
	 *         object
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ServletException
	 * @throws Exception
	 */
	@RequestMapping(value = "/userprofile/userprofilethreshold.form", method = RequestMethod.GET)
	public String loadUserProfileThreshold(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ServletException {
		String methodName = "UserProfileThresholdController#loadUserProfileThreshold";

		if (log.isDebugEnabled()) {
			log.debug(
					"UserProfileThresholdController#loadUserProfileThreshold",
					PretupsI.ENTERED);
		}
		UserVO userVO = this.getUserFormSession(request);
		authorise(request, response, "USRCNTR001", false);
		model.addAttribute(PANEL_NAME, "Panel-One");
		request.getSession().setAttribute(PANEL_NAME, "Panel-One");

		if (log.isDebugEnabled()) {
			log.debug(methodName,
					"Domain List User VO : " + userVO.getDomainList());
		}

		ArrayList domainList = userVO.getDomainList();
		ArrayList<UserGeographiesVO> geographyList = userVO
				.getGeographicalAreaList();

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Geography List User VO: "
					+ userVO.getGeographicalAreaList().toString());
		}

		model.addAttribute(DOMAIN_LIST, domainList);
		model.addAttribute(GEOGRAPHY_LIST, geographyList);
		request.getSession().setAttribute(GEOGRAPHY_LIST, geographyList);
		request.getSession().setAttribute(DOMAIN_LIST, domainList);
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);

		}
		return FIRST_PAGE;
	}

	@RequestMapping(value = "/userprofile/profile-load-category.form", method = RequestMethod.POST)
	public @ResponseBody List<CategoryVO> loadCategory(
			@RequestParam("domain") String domain, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ServletException, IOException

	{
		final String methodName = "LoadCategory";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered : domain =" + domain);
		}

		categoryList = userProfileThresholdService
				.loadCategory(domain, "shduw");
		request.getSession().setAttribute(CATEGORY_KEY, categoryList);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting" + categoryList);
		}
		return categoryList;
	}

	@RequestMapping(value = "/userprofile/SearchUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList(Model model,
			HttpServletRequest request,
			@ModelAttribute("userModel") UserModel userModel,
			HttpServletResponse response) throws BTSLBaseException

	{

		String methodName = USER_LIST;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :userList  " + userModel.toString());
		}
		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("query");
		userName = userName + "%";
		String categorycode = request.getParameter(CATAGORY).split("\\|")[0];
		String sequenceNo = request.getParameter(CATAGORY).split("\\|")[1];
		String ownerId = request.getParameter("owner");
		String geodomaincode = request.getParameter("geodomain");
		String domainCode = request.getParameter("domain");

		if (!BTSLUtil.isNullString(ownerId)) {
			ownerId = ownerId.split(":")[1];
		}
		if (BTSLUtil.isNullString(categorycode)) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		userModel.setDomainCode(domainCode);
		userModel.setUserName(userName);
		userModel.setCategoryCode(categorycode);

		final ChannelUserVO channelUserVO = (ChannelUserVO) this
				.getUserFormSession(request);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :OwnerID  " + ownerId + " : "
					+ userName);
		}
		List<UserVO> userList = userProfileThresholdService.loadUserList(
				channelUserVO, request, model, userModel, ownerId,
				geodomaincode, sequenceNo);

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :categorycode  " + userList);
		}

		Iterator<UserVO> itr = userList.iterator();
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			UserVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + ":" + object.getUserID();
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getLoginID());
			list.add(map);
		}
		request.getSession().setAttribute(USER_LIST, userList);

		model.addAttribute(PANEL_NAME, "Panel-Three");
		request.getSession().setAttribute(PANEL_NAME, "Panel-Three");

		return list;

	}

	@RequestMapping(value = "/userprofile/SearchParentUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> parentUserList(Model model,
			HttpServletRequest request,
			@ModelAttribute("userModel") UserModel userModel,
			HttpServletResponse response) throws BTSLBaseException

	{
		String methodName = PARENT_USER_LIST;
		if (log.isDebugEnabled()) {
			log.debug(methodName,
					"Entered :parentuserList  " + userModel.toString());
		}

		String ownName = request.getParameter("query");
		ownName = ownName + "%";
		String categorycode = request.getParameter(CATAGORY).split("\\|")[0];
		String geodomaincode = request.getParameter("geodomain");
		String domainCode = request.getParameter("domain");

		final ChannelUserVO channelUserVO = (ChannelUserVO) this
				.getUserFormSession(request);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered :categorycode  " + categorycode
					+ " : " + ownName);
		}
		List<UserVO> parentUserList = userProfileThresholdService
				.loadParentUserList(request, userModel, channelUserVO,
						categorycode, ownName, geodomaincode, domainCode);

		List<Map<String, String>> list = new ArrayList<>();
		Iterator<UserVO> itr = parentUserList.iterator();
		if (parentUserList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			UserVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + ":" + object.getUserID();
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getLoginID());
			list.add(map);
		}

		if (log.isDebugEnabled()) {
			log.debug(methodName,
					"Entered :parentuserList  " + parentUserList.toString());
		}
		request.getSession().setAttribute(PARENT_USER_LIST, parentUserList);

		return list;
	}

	@RequestMapping(value = "/userprofile/backFromSecondPage.form", method = RequestMethod.GET)
	public String backFromSecondPage(final Model model,
			HttpServletRequest request, HttpServletResponse response) {
		model.addAttribute(MODEL_KEY,
				request.getSession().getAttribute(MODEL_KEY));
		model.addAttribute(CATEGORY_KEY,
				request.getSession().getAttribute(CATEGORY_KEY));
		model.addAttribute(DOMAIN_LIST,
				request.getSession().getAttribute(DOMAIN_LIST));
		model.addAttribute(PANEL_NAME,
				request.getSession().getAttribute(PANEL_NAME));
		model.addAttribute(GEOGRAPHY_LIST,
				request.getSession().getAttribute(GEOGRAPHY_LIST));
		model.addAttribute(USER_LIST,
				request.getSession().getAttribute(USER_LIST));
		model.addAttribute(PARENT_USER_LIST,
				request.getSession().getAttribute(PARENT_USER_LIST));

		return FIRST_PAGE;
	}

	@RequestMapping(value = "/userprofile/submit-user-profile.form", method = RequestMethod.POST)
	public String loadProductDetails(
			@ModelAttribute("userModel") UserModel userModel,
			BindingResult bindingResult, final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ValidatorException,
			SAXException {

		final String methodName = "#loadProductDetails";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		String categoryCode = userModel.getCategoryCode();
		if (!BTSLUtil.isNullString(categoryCode)) {
			categoryCode = categoryCode.split("\\|")[0];
		}
		userModel.setCategoryCode(categoryCode);

		try {

			userModel.setOrigCategoryList((ArrayList) categoryList);
			ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
			boolean str = userProfileThresholdService.loadUserProfile(model,
					userModel, channelUserSessionVO, request, response,
					bindingResult);
			if (!str) {
				model.addAttribute(MODEL_KEY, request.getSession()
						.getAttribute(MODEL_KEY));

				return FIRST_PAGE;

			}

		} catch (BTSLBaseException e) {

			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, e);
			}

		}

		return "user/UserProfileThresholdViewProduct";

	}

	@RequestMapping(value = "/userprofile/submit-user-mobile-number.form", method = RequestMethod.POST)
	public String loadDetailsByMobileNo(
			@ModelAttribute("userModel") UserModel userModel,
			BindingResult bindingResult, final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ValidatorException,
			SAXException {

		final String methodName = "#loadDetailsByMobileNo";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		String categoryCode = userModel.getCategoryCode();

		if (!BTSLUtil.isNullString(categoryCode)) {
			categoryCode = categoryCode.split("\\|")[0];
		}
		userModel.setCategoryCode(categoryCode);

		try {

			ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);

			boolean str = userProfileThresholdService
					.loadUserProfileByMobileNo(model, userModel,
							channelUserSessionVO, bindingResult, request,
							response);

			if (!str) {

				model.addAttribute(MODEL_KEY, request.getSession()
						.getAttribute(MODEL_KEY));

				return FIRST_PAGE;
			}
		} catch (BTSLBaseException e) {

			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, e);
			}

		}

		return "user/UserProfileThresholdViewProduct";

	}

}
