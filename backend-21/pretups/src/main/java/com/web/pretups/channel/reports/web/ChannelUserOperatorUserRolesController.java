package com.web.pretups.channel.reports.web;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.ChannelUserOperatorUserRolesService;

/**
 * @author mohit.miglani
 *
 */
@Controller
public class ChannelUserOperatorUserRolesController extends CommonController{
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@Autowired
	ChannelUserOperatorUserRolesService channelUserOperatorUserRolesService ;
	private static UsersReportModel usersReportModel = new UsersReportModel();
	private static final String MODEL_KEY_ONE = "usersReportModel";
	private static final String PREVIOUS_SCREEN = "c2s/reports/channelUserRoleView";
	private static final String CHNL_RPT="channelRolesReport";
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/channel-user.form", method = RequestMethod.GET)
	public String channelUser(Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException  {
		
		final String methodName="channelUser";
		try {
			authorise(request, response, "ROEU001", false);
			
			 
			request.getSession().removeAttribute(CHNL_RPT);
			 
	
		} catch (IOException | ServletException e) {

			log.errorTrace(methodName, e);
		}

		
		
		
		final UserVO userVO = this.getUserFormSession(request);
		channelUserOperatorUserRolesService.loadO2cUserRoles(usersReportModel,
				request, response, userVO);

		model.addAttribute(MODEL_KEY_ONE, usersReportModel);
	
		request.getSession().setAttribute(MODEL_KEY_ONE, usersReportModel);

		
		if (log.isDebugEnabled()) {
			log.debug(
					"ChannelUserOperatorUserRolesController#channelUser",
					PretupsI.EXITED);
		}
	
		return PREVIOUS_SCREEN ;

	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/reports/SearchUsers.form", method=RequestMethod.GET)
	 @ResponseBody public List<Map<String, String>> userList(Model model, HttpServletRequest request, HttpServletResponse response)
            throws BTSLBaseException

{
      String userName = request.getParameter("query");
      userName = userName + "%";
      String parentCategoryCode = request.getParameter("parentCategoryList");
      String domainList = request.getParameter("domainList");
      String zoneList = request.getParameter("zoneList");
      final UserVO userVO =  getUserFormSession(request);
      

     List userList = channelUserOperatorUserRolesService.loadUserList(userVO, parentCategoryCode, domainList, zoneList, userName);


      Iterator<ChannelUserTransferVO> itr = userList.iterator();
      List<Map<String, String>> list = new ArrayList<>();
      if(userList.isEmpty()|| parentCategoryCode == null){
    	  Map<String, String> map = new HashMap<>();
    	  map.put("loginId", PretupsRestUtil.getMessageString("pretups.user.channeluserviewbalances.nodata"));
    	    list.add(map);
      }
      while (itr.hasNext()) {
    	    ChannelUserTransferVO object = itr.next();
            Map<String, String> map = new HashMap<>();
            String userID = object.getUserName() + "(" + object.getUserID() + ")";
            map.put("loginId", userID);
            map.put("userId", object.getUserID());
            list.add(map);
      }
      

      return list;

}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param usersReportModel
	 * @param model
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/submitchannelreport.form", method = RequestMethod.POST)
	public String submitchannelReport(
			@ModelAttribute("usersReportModel") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult) throws BTSLBaseException
			 {
		
		final String methodName="submitchannelReport";
		final UserVO userVO = this.getUserFormSession(request);
		final ChannelUserVO userVO1 = (ChannelUserVO) getUserFormSession(request);

		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute(MODEL_KEY_ONE);

		
		usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
		 
	      
		
		if(("UC").equals(usersReportModel.getSortType()))
		{
			usersReportModel.setSorttypeName("Channel Category");
		}
		else
		{
			usersReportModel.setSorttypeName("User Status");
		}
		try {
			usersReportModel.setCurrentDate(BTSLUtil.getDateStringFromDate(new Date()));
		} catch (ParseException e) {

			log.errorTrace(methodName, e);
		}

		setdateandsorttype(usersReportModel, usersReportModelNew);
		if (usersReportModel.getZoneCode() != null) {
			usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
		}
		if (usersReportModel.getParentCategoryCode() != null) {
			usersReportModelNew.setParentCategoryCode(usersReportModel
					.getParentCategoryCode());
		}
		
		setValues(usersReportModel, usersReportModelNew);
		request.getSession().setAttribute(CHNL_RPT, usersReportModelNew);
		if(channelUserOperatorUserRolesService.displayChannelUserRolesReport(usersReportModelNew,
				request, response, userVO, userVO1, bindingResult))
		{
		usersReportModel.setUserID(userVO.getUserID());
		usersReportModel.setCategoryName(usersReportModelNew.getCategoryName());
		usersReportModel.setDomainName(usersReportModelNew.getDomainName());
		usersReportModel.setUserStatusName(usersReportModelNew.getUserStatusName());
		usersReportModel.setLoginUserID(usersReportModelNew.getLoginUserID());
		usersReportModel.setZoneName(usersReportModelNew.getZoneName());
		usersReportModel.setNetworkCode(usersReportModelNew.getNetworkCode());
		usersReportModel.setDomainListString(usersReportModelNew.getDomainListString());
		usersReportModel.setReportHeaderName(usersReportModelNew.getReportHeaderName());
		usersReportModel.setUserType(usersReportModelNew.getUserType());
		model.addAttribute(MODEL_KEY_ONE, usersReportModelNew);
		
		
		if(request.getParameter("initiatesummary")!=null )
		{
			return "c2s/reports/channelUserRolesReportNew";
		}
		else
		{
			return "c2s/reports/channelUserRoleReportView";
		}
	} 
		
		
		
		
		else
		{
			return PREVIOUS_SCREEN ;
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
			 }

	private void setdateandsorttype(UsersReportModel usersReportModel,
			UsersReportModel usersReportModelNew) {
		if (usersReportModel.getSorttypeName() != null) {
			usersReportModelNew.setSorttypeName(usersReportModel.getSorttypeName());
		}
		if (usersReportModel.getCurrentDate() != null) {
			usersReportModelNew.setCurrentDate(usersReportModel.getCurrentDate());
		}
	}

	private void setValues(UsersReportModel usersReportModel,
	 UsersReportModel usersReportModelNew) {
		if (usersReportModel.getUserStatus() != null) {
			usersReportModelNew.setUserStatus(usersReportModel.getUserStatus());
		}

		if (usersReportModel.getUserName() != null) {
			usersReportModelNew.setUserName(usersReportModel.getUserName());
		}
		if(("ALL").equals(usersReportModel.getParentCategoryCode()))
		{
			usersReportModelNew.setCategoryName(usersReportModel.getParentCategoryCode());
		}
	}
	
	
	/**
	 * @param usersReportModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/backchannelreport.form", method = RequestMethod.GET)
	public String backchannelReport(@ModelAttribute("backChannelReport") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response)

			 throws BTSLBaseException {
		final String methodName = "backReport";
		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#backReport",
					PretupsI.ENTERED);
		}
		try {
			authorise(request, response, "ROEU001", false);
		} catch (ServletException | IOException e) {

			log.errorTrace(methodName, e);
		}

		model.addAttribute(MODEL_KEY_ONE,
				request.getSession().getAttribute(MODEL_KEY_ONE));
		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#backReport",
					PretupsI.EXITED);
		}


		
	
	return  PREVIOUS_SCREEN ;
	
	
	
	
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/downloadexternalcsvFile.form", method = RequestMethod.GET)
	public void downloadCSVReportFile(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException
		  {
		final String methodName = "downloadCSVReportFile";
		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute(CHNL_RPT);
		InputStream is = null;
		OutputStream os = null;
		
		try {
			String fileLocation = channelUserOperatorUserRolesService
					.downloadFileforSumm(usersReportModelNew);
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
		} catch (IOException | SQLException | InterruptedException e) {
			log.errorTrace(methodName, e);
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
