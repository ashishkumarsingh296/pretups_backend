package com.web.pretups.channel.reports.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.Channel2ChannelTransferReportsService;
/**
 * 
 * @author yogesh.keshari
 *
 */
@Controller
public class Channel2ChannelTransferReportsController extends CommonController {

	@Autowired
	private Channel2ChannelTransferReportsService channel2ChannelTransferReportsService;
	public static final Log log = LogFactory.getLog(Channel2ChannelTransferReportsController.class.getName());
	private static final String PANEL_NO = "PanelNo";
	private static final String MODEL_KEY = "chnnlToChnnlTransferReport";
	private static final String MSG_ZONE_CODE_REQUIRED = "Select Zone First";
	private static final String MSG_DOMAIN_CODE_REQUIRED = "Select Domain First";
	private static final String MSG_FROM_CAT_REQUIRED = "Select Search Category First";
	private static final String PAGE_ONE = "c2s/reports/Channel2ChannelTransferReport";
	private static final String PAGE_TWO = "c2s/reports/Channel2ChannelTransferReportView";
	private static final String INET_PAGE = "c2s/reports/channel2ChannelTransferRpt";
	private static final String SAFE_XSS="SafeString";
	private static final String ZONE_CODE="zoneCode";
	private static final String DOMAIN_CODE="domainCode";
	private static final String CAT_CODE="fromCategoryCode";
	private static final String QUER_PARAM="query";
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
	@RequestMapping(value = "/pretups/Channel2ChannelTransferReport.form", method = RequestMethod.GET)
	public String loadC2cTransferDetails(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws  Exception {
		if (log.isDebugEnabled()) {
			log.debug("Channel2ChannelTransferReportsService#loadC2cTransferDetails", PretupsI.ENTERED);
		}
		request.getSession().removeAttribute(MODEL_KEY);
		request.getSession().removeAttribute(PANEL_NO);

		authorise(request, response, "RPTRWTR01A", false);
		UsersReportModel usersReportModel = new UsersReportModel();
		final UserVO userVO = this.getUserFormSession(request);
		model.addAttribute(PANEL_NO, "Panel-One");
		if (channel2ChannelTransferReportsService.loadC2CTransferReportPage(userVO, usersReportModel, model)) {
			model.addAttribute(MODEL_KEY, usersReportModel);
			request.getSession().setAttribute("ModelFromPg1", usersReportModel);
		}
		if (log.isDebugEnabled()) {
			log.debug("Channel2ChannelTransferReportsService#loadC2cTransferDetails", PretupsI.EXITED);
		}
		return PAGE_ONE;

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
	@RequestMapping(value = "/pretups/Channel2ChannelTransferReportBack.form", method = RequestMethod.GET)
	public String loadC2cTransferDetailsFormBack(final Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		final String methodName ="Channel2ChannelTransferReportsService#loadC2cTransferDetailsFormBack";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		model.addAttribute(PANEL_NO, request.getSession().getAttribute(PANEL_NO));
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}
		return PAGE_ONE;
	}
/**
 * 
 * @param usersReportModel
 * @param bindingResult
 * @param model
 * @param request
 * @return
 * @throws BTSLBaseException
 * @throws IOException
 * @throws NoSuchAlgorithmException
 * @throws ServletException
 */
	@RequestMapping(value = "/pretups/submitc2cTransferDetailsReports.form", method = RequestMethod.POST)
	public String displayC2cTransferDetails(@ModelAttribute("usersReportModel") UsersReportModel usersReportModel,
			BindingResult bindingResult, final Model model, HttpServletRequest request)
					throws Exception {
		final String methodName ="Channel2ChannelTransferReportsService # displayC2cTransferDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		final UserVO userVO = this.getUserFormSession(request);
		UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("ModelFromPg1");
		
		usersReportModelNew.setTxnSubType(usersReportModel.getTxnSubType());
		usersReportModelNew.setTransferInOrOut(usersReportModel.getTransferInOrOut());
		usersReportModelNew.setFromDate(usersReportModel.getFromDate());
		usersReportModelNew.setFromTime(usersReportModel.getFromTime());
		usersReportModelNew.setToDate(usersReportModel.getToDate());

		usersReportModelNew.setFromMsisdn(usersReportModel.getFromMsisdn());
		usersReportModelNew.setToMsisdn(usersReportModel.getToMsisdn());

		usersReportModelNew.setToTime(usersReportModel.getToTime());
		usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
		usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
		usersReportModelNew.setFromtransferCategoryCode(usersReportModel.getFromtransferCategoryCode());
		usersReportModelNew.setTotransferCategoryCode(usersReportModel.getTotransferCategoryCode());
		usersReportModelNew.setUserName(usersReportModel.getSearchUserName());
		usersReportModelNew.setTouserName(usersReportModel.getSearchToUserName());
		usersReportModelNew.setSearchUserName(usersReportModel.getSearchUserName());
		usersReportModelNew.setSearchToUserName(usersReportModel.getSearchToUserName());
		usersReportModelNew.setStaffReport(usersReportModel.getStaffReport());
		usersReportModelNew.setCurrentDateRptChkBox(usersReportModel.getCurrentDateRptChkBox());
		boolean result =channel2ChannelTransferReportsService.displayC2CTransferReportPage(userVO, usersReportModelNew, model,bindingResult, request);
		
		usersReportModelNew.setFromTrfCatCodeValue(usersReportModelNew.getDomainCode()+":"+usersReportModelNew.getFromtransferCategoryCode());
		usersReportModelNew.setToTrfCatCodeValue(usersReportModelNew.getFromTrfCatCodeValue()+":"+usersReportModelNew.getTotransferCategoryCode());
		request.getSession().setAttribute(MODEL_KEY, usersReportModelNew);
		model.addAttribute(MODEL_KEY, usersReportModelNew);
		if(result)
		{
			if ((request.getParameter("iNETReportPanelOne") != null)|| (request.getParameter("iNETReportPanelTwo") != null))
				return INET_PAGE;
			else 
				return PAGE_TWO;
		}
		else 
			return PAGE_ONE;
		
	}
/**
 * 
 * @param model
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
	@RequestMapping(value = "/pretups/SearchFromUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> loadC2cFromUserList(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		final String METHOD_NAME="loadC2cFromUserList";
		List<Map<String, String>> list = new ArrayList<>(); 
		List<ListValueVO> fromUserList =null;
		try{
		String zoneCode = request.getParameter(ZONE_CODE);
		String zoneval = ESAPI.validator().getValidInput(ZONE_CODE,zoneCode, SAFE_XSS, 400, false);
        String safeZone = ESAPI.encoder().encodeForHTML( zoneval );
		String domainCode = request.getParameter(DOMAIN_CODE);
		String domVal= ESAPI.validator().getValidInput(DOMAIN_CODE, domainCode, SAFE_XSS, 400, false);
        String safeDom = ESAPI.encoder().encodeForHTML( domVal );
		String fromTransferCategorycode = request.getParameter(CAT_CODE);
		String transferVal = ESAPI.validator().getValidInput(CAT_CODE, fromTransferCategorycode, "SafeTrfcol", 400, false);
       
		String UserName = request.getParameter(QUER_PARAM);        
		String userval = ESAPI.validator().getValidInput(QUER_PARAM, UserName,"SafeAutoComplete", 400, false);
		
		UserName = userval + "%";             
		     
		final UserVO userVO = this.getUserFormSession(request);          

		if (BTSLUtil.isNullString(zoneCode)) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", MSG_ZONE_CODE_REQUIRED);
			map.put("userId", "");
			list.add(map);
			return list;
		} else if (BTSLUtil.isNullString(domainCode)) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", MSG_DOMAIN_CODE_REQUIRED);
			map.put("userId", "");
			list.add(map);
			return list;
		} else if (BTSLUtil.isNullString(fromTransferCategorycode)) {
			Map<String, String> map = new HashMap<>();      
			map.put("loginId", MSG_FROM_CAT_REQUIRED);
			map.put("userId", "");
			list.add(map);
			return list;
		}
		fromUserList = channel2ChannelTransferReportsService.loadFromUserList(userVO, safeZone,
				safeDom, transferVal, UserName);
       }catch (IntrusionException | ValidationException e) {	
    	   log.errorTrace(METHOD_NAME, e);
        throw new BTSLBaseException(this, METHOD_NAME, e.getMessage());	
	}
		Iterator<ListValueVO> itr = fromUserList.iterator();

		while (itr.hasNext()) {
			ListValueVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getLabel()+"("+object.getValue()+")";
			map.put("loginId", loginId);
			map.put("userId", object.getValue());
			list.add(map);
		}
		if (fromUserList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", "NO_DATA_FOUND");
			map.put("userId", "");
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
 * @throws Exception
 */
	@RequestMapping(value = "/pretups/SearchToUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> loadC2cToUserList(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Map<String, String>> list = new ArrayList<>();
		String UserName = request.getParameter("query");
		UserName = UserName + "%";
		String zoneCode = request.getParameter("zoneCode");
		String domainCode = request.getParameter("domainCode");
		String fromUserID = request.getParameter("fromUserName");
		String fromTransferCategorycode = request.getParameter("fromCategoryCode");
		String toTransferCategorycode = request.getParameter("toCategoryCode");
		final UserVO userVO = this.getUserFormSession(request);
		UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("ModelFromPg1");
		if (BTSLUtil.isNullString(zoneCode)) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", MSG_ZONE_CODE_REQUIRED);
			map.put("userId", "");
			list.add(map);
			return list;
		} else if (BTSLUtil.isNullString(domainCode)) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", MSG_DOMAIN_CODE_REQUIRED);
			map.put("userId", "");
			list.add(map);
			return list;
		} else if (BTSLUtil.isNullString(toTransferCategorycode)) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", MSG_FROM_CAT_REQUIRED);
			map.put("userId", "");
			list.add(map);
			return list;
		}
		List<ListValueVO> toUserList = channel2ChannelTransferReportsService.loadToUserList(usersReportModel,domainCode,userVO, fromUserID,fromTransferCategorycode,
				toTransferCategorycode, UserName);

		Iterator<ListValueVO> itr = toUserList.iterator();

		while (itr.hasNext()) {
			ListValueVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getLabel()+"("+object.getValue()+")";
			map.put("loginId", loginId);
			map.put("userId", object.getValue());
			list.add(map);
		}
		if (toUserList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", "NO_DATA_FOUND");
			map.put("userId", "");
			list.add(map);

		}

		return list;

	}
/**
 * 
 * @param model
 * @param request
 * @param response
 * @throws BTSLBaseException
 * @throws ServletException
 * @throws IOException
 * @throws SQLException
 * @throws InterruptedException
 */
	@RequestMapping(value = "/pretups/downloadFile.form", method = RequestMethod.GET)
	public void downloadCSVReportFile(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		final String methodName ="Channel2ChannelTransferReportsService # downloadCSVReportFile";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute(MODEL_KEY);
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = channel2ChannelTransferReportsService.downloadCSVReportFile(model,usersReportModelNew);
			File file = new File(fileLocation);
			is = new FileInputStream(file);
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			os = response.getOutputStream();

			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (Exception e) {
			throw new BTSLBaseException(e);
		} finally {
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED);
			}
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
