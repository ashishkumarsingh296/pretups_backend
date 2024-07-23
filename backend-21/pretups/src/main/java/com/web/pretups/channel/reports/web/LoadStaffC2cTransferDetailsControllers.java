package com.web.pretups.channel.reports.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.StaffC2CTransferDetailsService;

/**
 * @author pankaj.kumar
 *
 */
@Controller
public class LoadStaffC2cTransferDetailsControllers extends CommonController {

	@Autowired
	private StaffC2CTransferDetailsService staffC2CTransferDetailsService;

	public static final Log log = LogFactory
			.getLog(LoadStaffC2cTransferDetailsControllers.class.getName());

	private static final String PANEL_NO = "PanelNo";
	private static final String MODEL_KEY = "channelReportsUserStaff";
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "Select Category First";
	private static final String PREVIOUS_SCREEN = "c2s/reports/staffC2CTransferView";
	private static final String NEXT_SCREEN = "c2s/reports/staffC2CTransferNext";
	private static final String INET_SCREEN = "c2s/reports/staffC2CTransferReportView";
	private static final String LOGIN_ID = "loginId";
	private static final String USER_ID = "userId";
	private static final String FAIL_KEY = "fail";
	private static final String MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH = "SELECT ZONE FIRST";

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/staffC2CTransferView.form", method = RequestMethod.GET)
	public String loadStaffC2cTransferDetailform(final Model model,
			HttpServletRequest request, HttpServletResponse response) {

		if (log.isDebugEnabled()) {
			log.debug("loadStaffC2cTransferDetailform", PretupsI.ENTERED);
		}
		final String methodName = "loadStaffC2cTransferDetailform";
		request.getSession().removeAttribute(PANEL_NO);
		UsersReportModel usersReportModel = new UsersReportModel();
		UserVO userVO = null;

		try {
			authorise(request, response, "STFC2C00A", false);
			userVO = this.getUserFormSession(request);
			staffC2CTransferDetailsService.loadStaffC2CTransferDetails(request,
					response, usersReportModel, userVO, model);

		} catch (IOException | BTSLBaseException | ServletException e) {
			log.errorTrace(methodName, e);
		}
		model.addAttribute(PANEL_NO, "Panel-One");
		model.addAttribute(MODEL_KEY, usersReportModel);
		request.getSession().setAttribute(MODEL_KEY, usersReportModel);
		request.getSession().setAttribute("usersReport", usersReportModel);

		if (log.isDebugEnabled()) {
			log.debug("Category list",
					usersReportModel.getParentCategoryList());
			log.debug(
					"LoadStaffC2cTransferDetailsControllers#loadStaffC2cTransferDetailform",
					PretupsI.EXITED);
		}

		return PREVIOUS_SCREEN;
	}

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/pretups/SearchUserO2C.form", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> userList(Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException

	{

		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("userName");
		userName = userName + "%";
		String zoneCode = request.getParameter("zoneCode");
		String domainCode = request.getParameter("domainCode");
		String parentCategoryCode = request.getParameter("parentCategoryCode");

		UsersReportModel usersReportModel = (UsersReportModel) request
				.getSession().getAttribute("usersReport");

		if (BTSLUtil.isNullString(zoneCode)) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH);
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if (BTSLUtil.isNullString(parentCategoryCode)) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		final UserVO userVO = this.getUserFormSession(request);

		List<ChannelUserTransferVO> userList = staffC2CTransferDetailsService
				.loadC2cUserLists(userVO, zoneCode, domainCode, userName,
						parentCategoryCode, usersReportModel);

		if (userList.isEmpty()) {

			model.addAttribute(FAIL_KEY, PretupsRestUtil
					.getMessageString("c2s.reports.msg.datanotfound"));

		}
		usersReportModel.getUserID();
		Iterator<ChannelUserTransferVO> itr = userList.iterator();

		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			ChannelUserTransferVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String userID = object.getUserName() + "(" + object.getUserID()
					+ ")";
			
			map.put("loginId", userID);
			map.put("userId", object.getUserID());
			usersReportModel.setUserName(object.getUserName());
			usersReportModel.setParentUserID(object.getUserID());
			list.add(map);
		}

		return list;

	}

	@RequestMapping(value = "/pretups/SearchUseridC2C.form", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, String>> userListId(Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException

	{

		List<Map<String, String>> list = new ArrayList<>();
	

		String loginId = request.getParameter("loginId");
		String zoneCode = request.getParameter("zoneCode");
		String domainCode = request.getParameter("domainCode");
		String parentCategoryCode = request.getParameter("parentCategoryCode");
		String userName = request.getParameter("userName");

		UsersReportModel usersReportModel = (UsersReportModel) request
				.getSession().getAttribute("usersReport");

		if (BTSLUtil.isNullString(zoneCode)) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH);
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if (BTSLUtil.isNullString(parentCategoryCode)) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		final UserVO userVO = this.getUserFormSession(request);

		List<ChannelUserTransferVO> userList = staffC2CTransferDetailsService
				.loadUseridforO2c(userVO, zoneCode, domainCode, userName,
						parentCategoryCode, usersReportModel, loginId);

		if (userList.isEmpty()) {

			model.addAttribute(FAIL_KEY, PretupsRestUtil
					.getMessageString("c2s.reports.msg.datanotfound"));

		}

		Iterator<ChannelUserTransferVO> itr = userList.iterator();

		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			ChannelUserTransferVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			

			String userID = object.getLoginId();
			map.put("loginId", userID);
			map.put("userId", object.getLoginId());
			usersReportModel.setLoginId(userID);
			list.add(map);
		}

		return list;

	}

	/**
	 * @param model
	 * @param usersReportModel
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ServletException
	 * @throws ValidatorException
	 * @throws SAXException
	 */
	@RequestMapping(value = "/pretups/enquirySearchNextpage.form", method = RequestMethod.POST)
	public String enquirySearchNextpage(
			final Model model,
			@ModelAttribute("staffC2CTransferForm") UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response,
			BindingResult bindingResult)  {
 		final String method="enquirySearchNextpage";
		UserVO userVO=null;
		try {
			userVO = this.getUserFormSession(request);
		} catch (BTSLBaseException e) {
			 log.errorTrace(method, e);
		}

		request.getParameter("submitUserSearchButton");
		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute("usersReport");

		if (usersReportModel.getTxnSubTypeList() != null) {
			usersReportModelNew.setTxnSubTypeList(usersReportModel
					.getTxnSubTypeList());

		}

		if (usersReportModel.getTxnSubType() != null) {
			usersReportModelNew.setTxnSubType(usersReportModel.getTxnSubType());

		}

		if (usersReportModel.getFromDate() != null) {
			usersReportModelNew.setFromDate(usersReportModel.getFromDate());
		}

		if (usersReportModel.getToDate() != null) {
			usersReportModelNew.setToDate(usersReportModel.getToDate());
		}

		if (usersReportModel.getFromTime() != null) {
			usersReportModelNew.setFromTime(usersReportModel.getFromTime());
		}

		if (usersReportModel.getToTime() != null) {
			usersReportModelNew.setToTime(usersReportModel.getToTime());
		}

		if (usersReportModel.getZoneCode() != null) {
			usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
		}

		if (usersReportModel.getDomainCode() != null) {
			usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
		}

		if (usersReportModel.getParentCategoryCode() != null) {
			usersReportModelNew.setParentCategoryCode(usersReportModel
					.getParentCategoryCode());
		}

		

		if (usersReportModel.getLoginId() != null ) {
			usersReportModelNew.setLoginId(usersReportModel.getLoginId());
		}
		
		if (usersReportModel.getMsisdn() != null ) {
			usersReportModelNew.setMsisdn(usersReportModel.getMsisdn());
		}
		
		
		

		try {
			if (staffC2CTransferDetailsService.displayStaffC2CTransferDetailsList(
					request, response, usersReportModelNew, userVO, model,
					bindingResult)) {

				model.addAttribute("o2cDetails", usersReportModelNew);

				request.getSession().setAttribute("usersReportStaff",
						usersReportModelNew);
				model.addAttribute("usersReportStaff", usersReportModelNew);
				if (request.getParameter("submitUserSearchButton") != null) {
					return NEXT_SCREEN;
				} else {
					return INET_SCREEN;
				}
			}

			else {
				return PREVIOUS_SCREEN;
			}
		} catch (ValidatorException | IOException | SAXException e) {
			 log.errorTrace(method, e);
		}
		return PREVIOUS_SCREEN;
	}

	/**
	 * @param model
	 * @param usersReportModel
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ServletException
	 */
	@RequestMapping(value = "/pretups/O2CDetailsBackMainPage.form", method = RequestMethod.GET)
	public String enquirySearchBack(final Model model,
			@ModelAttribute UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response)
			 {

		request.setAttribute("BackButton", true);
		
		UsersReportModel usersReportModelx= (UsersReportModel)request.getSession().getAttribute("usersReportStaff");
		if(BTSLUtil.isNullString(usersReportModelx
				.getLoginId()))
		{
			model.addAttribute("PanelNo", "Panel-Two");
		}
		else
		{
			model.addAttribute("PanelNo", "Panel-One");
		}
		return PREVIOUS_SCREEN;

	}

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	@RequestMapping(value = "/pretups/downloadFileStaff.form", method = RequestMethod.GET)
	public void downloadCSVReportFiles(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ServletException, IOException,
			InterruptedException, SQLException {
		String methodName = "downloadCSVReportFiles";
		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute("usersReport");
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = staffC2CTransferDetailsService
					.downloadCSVReportStaffC2CTransferDetailsFile(usersReportModelNew);
			File file = new File(fileLocation);
			is = new FileInputStream(file);
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ file.getName() + "\"");
			os = response.getOutputStream();

			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			try{
        		if(is!=null){
        			is.close();	
        		}
        	}catch(Exception e){
        		 log.errorTrace(methodName, e);
        	}
        	try{
        		if(os!=null){
        			os.close();	
        		}
        	}catch(Exception e){
        		 log.errorTrace(methodName, e);
        	}

		}

	}

}
