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
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.CRC2STransferService;

/**
 * @author tarun.kumar
 *
 */
@Controller
public class CRC2STransferController extends CommonController {

public static final Log log = LogFactory.getLog(CRC2STransferController.class.getName());
	
	private static final String PANEL_NO = "PanelNo";
	
	private static final String MODEL_KEY = "usersReportModel";
	
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "Select Channel Category";
	
	private static final String MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH = "Select Zone First";
	
	private static final String MSG_WHEN_NO_DOMAIN_SEL4_USER_SEARCH = "Select Domain First";
	
	private static final String FAIL_KEY    = "fail";
			
	private static final String PREVIOUS_SCREEN = "c2s/reports/c2STransferView";
	
	private static final String NEXT_SCREEN = "c2s/reports/c2STransferReportView";
	
	private static final String INET_SCREEN = "c2s/reports/c2STransferInetReportView";

	private static final String LOGIN_ID = "loginId";

	private static final String USER_ID = "userId";
	private static final String SAFE_XSS="SafeString";
	private static final String ZONE_CODE="zoneCode";
	private static final String DOMAIN_CODE="domainCode";
	private static final String CAT_CODE="categorycode";
	private static final String QUER_PARAM="query";
			
	@Autowired
	private CRC2STransferService cRC2STransferService;
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/c2sTransfer.form", method = RequestMethod.GET)
	public String displayPreviousScreen(final Model model,HttpServletRequest request, HttpServletResponse response){

		if (log.isDebugEnabled()) {
			log.debug(	"CRC2STransferController#displayPreviousScreen",PretupsI.ENTERED);
		}
		final String methodName ="loadC2STransferView";
		request.getSession().removeAttribute(PANEL_NO);
		UsersReportModel usersReportModel = new UsersReportModel();
		UserVO userVO = null;
		try {
			 authorise(request, response, "RPTTRCS01A", false);	
			userVO = this.getUserFormSession(request);
			cRC2STransferService.loadC2STransferDetails(request,response, usersReportModel, userVO, model);
		} catch (IOException | BTSLBaseException | ServletException e) {
			 log.errorTrace(methodName, e);
		}
			model.addAttribute(PANEL_NO, "Panel-One");
			model.addAttribute(MODEL_KEY, usersReportModel);
			request.getSession().setAttribute(MODEL_KEY, usersReportModel);
			request.getSession().setAttribute("usersReport", usersReportModel);
		if (log.isDebugEnabled()) {
			
			log.debug(	"CRC2STransferController#displayPreviousScreen",PretupsI.EXITED);
		}
		return PREVIOUS_SCREEN;
	}
	
	/**
	 * @param usersReportModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/c2sTransferSubmit.form", method = RequestMethod.POST)
	public String c2sTransferSubmit(@ModelAttribute("usersReportModel") UsersReportModel usersReportModel,BindingResult bindingResult, final Model model,HttpServletRequest request, HttpServletResponse response){

		if (log.isDebugEnabled()) {
			log.debug("CRC2STransferController#c2sTransferSubmit",PretupsI.ENTERED);
		}		
		final String methodName ="c2sTransferSubmit";
		UserVO userVO = null;
		UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("usersReport");
		try {
			userVO = this.getUserFormSession(request);
			usersReportModelNew.setUserType(userVO.getUserType());
			checkSubmitByButton(usersReportModel, request, usersReportModelNew);
			if (cRC2STransferService.c2sTransferSubmit(request, response, usersReportModelNew, userVO, model,bindingResult)) { 
				model.addAttribute("c2sTransferDetails", usersReportModelNew);
				model.addAttribute(MODEL_KEY, usersReportModelNew);
				request.getSession().setAttribute(MODEL_KEY, usersReportModelNew);  				
				if(("submitValuePanelTwo".equalsIgnoreCase(request.getParameter("submitNamePanelTwo")))
					||("submitValuePanelOne".equalsIgnoreCase(request.getParameter("submitNamePanelOne")))){								
					return NEXT_SCREEN ;
				}else{
					return INET_SCREEN;
				}				
			} else {  
				cRC2STransferService.loadC2STransferDetails(request, response, usersReportModel, userVO, model);
				request.getSession().setAttribute(MODEL_KEY, usersReportModel);
				return PREVIOUS_SCREEN;			
			}
		} catch ( BTSLBaseException  e) {
			   log.errorTrace(methodName, e);
		}
		return PREVIOUS_SCREEN;
	}

	private void checkSubmitByButton(UsersReportModel usersReportModel,HttpServletRequest request, UsersReportModel usersReportModelNew) {
		if(("submitValuePanelOne".equalsIgnoreCase(request.getParameter("submitNamePanelOne")))|| ("iNETReportPanelOne".equalsIgnoreCase(request.getParameter("iNETReportPanelOne")))){
			usersReportModelNew.setServiceType(usersReportModel.getServiceType());
			usersReportModelNew.setTransferStatus(usersReportModel.getTransferStatus());
			usersReportModelNew.setStaffReport(usersReportModel.getStaffReport());
			usersReportModelNew.setDate(usersReportModel.getDate());  
			usersReportModelNew.setFromTime(usersReportModel.getFromTime());
			usersReportModelNew.setToTime(usersReportModel.getToTime());			
			usersReportModelNew.setMsisdn(usersReportModel.getMsisdn());
		}else{
			usersReportModelNew.setServiceType(usersReportModel.getServiceType());
			usersReportModelNew.setTransferStatus(usersReportModel.getTransferStatus());
			usersReportModelNew.setStaffReport(usersReportModel.getStaffReport());
			usersReportModelNew.setDate(usersReportModel.getDate());  
			usersReportModelNew.setFromTime(usersReportModel.getFromTime());
			usersReportModelNew.setToTime(usersReportModel.getToTime());			
			usersReportModelNew.setMsisdn(null);	
			usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
			usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
			usersReportModelNew.setParentCategoryCode(usersReportModel.getParentCategoryCode());
			usersReportModelNew.setUserName(usersReportModel.getUserName());
		}
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/c2sTransferDetailBack.form", method = RequestMethod.GET)
	public String loadC2sTransferDetailBackForm(final Model model,HttpServletRequest request, HttpServletResponse response) {

		if (log.isDebugEnabled()) {
			log.debug("CRC2STransferController#loadC2sTransferDetailBackForm",PretupsI.ENTERED);
		}
		model.addAttribute(PANEL_NO, request.getSession().getAttribute(PANEL_NO));
				
		return PREVIOUS_SCREEN;	
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/pretups/downloadC2sTransferDetailsFile.form", method = RequestMethod.GET)
	public void c2STransferDownloadCSVReportFile(final Model model,HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{

		if (log.isDebugEnabled()) {
			log.debug("CRC2STransferController#c2STransferDownloadCSVReportFile",PretupsI.ENTERED);
		}
		final String methodName ="c2STransferDownloadCSVReportFile";
		UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("usersReportModel");
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = null;			
			fileLocation = cRC2STransferService.c2STransferDownloadCSVReportFile(usersReportModelNew);	  		
			File file = new File(fileLocation);
			is = new FileInputStream(file);
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment; filename=\""	+ file.getName() + "\"");
			os = response.getOutputStream();
			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (IOException e ) {
			log.errorTrace(methodName, e);
			}
		 finally {
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

	/**
	 * @param model
	 * @param request
	 * @param response 
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/pretups/getC2sTransferUserList.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> c2sLoadUserList(Model model,HttpServletRequest request, HttpServletResponse response) throws Exception{
		final String methodName ="c2sLoadUserList";
		
		UserVO userVO = null;
		
		try {
			userVO = this.getUserFormSession(request);
		} catch (BTSLBaseException e) {
			 log.errorTrace(methodName, e);
		}
		
		List<Map<String, String>> list = new ArrayList<>();
		 List<ListValueVO> userList=null;
		try
		{
		String userName = request.getParameter("query");
		String userval = ESAPI.validator().getValidInput(QUER_PARAM, userName,"SafeAutoComplete", 400, false);
		userName = userval + "%";		
		String zoneCode = request.getParameter("zoneCode");
		String zoneval = ESAPI.validator().getValidInput(ZONE_CODE,zoneCode, SAFE_XSS, 400, false);
        String safeZone = ESAPI.encoder().encodeForHTML( zoneval );
		String domainCode = request.getParameter("domainCode");
		String domVal= ESAPI.validator().getValidInput(DOMAIN_CODE, domainCode, SAFE_XSS, 400, false);
		String parentCategoryCode = request.getParameter("categorycode");
		String transferVal = ESAPI.validator().getValidInput(CAT_CODE,parentCategoryCode , "SafeTrfcol", 400, false);
		
		if(BTSLUtil.isNullString(zoneCode)){
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }
		if(BTSLUtil.isNullString(domainCode)){
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_DOMAIN_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }
		if(BTSLUtil.isNullString(parentCategoryCode)){   
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }else if (TypesI.ALL.equalsIgnoreCase(parentCategoryCode)){
			    Map<String, String> map = new HashMap<>();
			    map.put(LOGIN_ID, TypesI.ALL);
				map.put(USER_ID, TypesI.ALL);
				list.add(map);
				return list;
		 }
		
		  userList = cRC2STransferService.c2sLoadUserList(userVO, safeZone, domVal,userName,transferVal);
		}
		catch (IntrusionException | ValidationException e) {	
	    	   log.errorTrace(methodName, e);
	        throw new BTSLBaseException(this, methodName, e.getMessage());	
		}
		if (userList.isEmpty()) {
			model.addAttribute(	FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.msg.datanotfound"));
		}

		Iterator<ListValueVO> itr = userList.iterator();
		
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			ListValueVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getLabel() + "(" + object.getValue() +")";			
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getLabel());
			list.add(map);
		}

		return list;
	}
}
