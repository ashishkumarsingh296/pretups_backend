package com.web.pretups.channel.reports.web;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.service.UserClosingBalanceService;
import com.web.pretups.channel.reports.service.UserClosingBalanceServiceImpl;

@Controller
public class UserClosingBalanceController extends CommonController {
	
	@Autowired
	private UserClosingBalanceService userClosingBalanceService;
	private static final String CLASS_NAME = "UserBalanceController";
	private static final Log LOG = LogFactory.getLog(UserClosingBalanceServiceImpl.class.getName());
	private static final String SUCCESS_KEY = "success";
	private static final String FAIL_KEY = "fail";
	private static final String USER_FORM = "UsersReportForm";
	
	
	
	@RequestMapping(value="/reports/userClosingBalance.form", method=RequestMethod.GET)
	public String loadUserClosingBalanceReport(final Model model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BTSLBaseException{
		final String methodName = "#loadUserClosingBalanceReport";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		authorise(request, response, "URCLOBL001", false);
		request.getSession().removeAttribute("SessionUserForm");
		UserVO userVO = this.getUserFormSession(request);
		UsersReportModel userForm = new UsersReportModel();
		userForm = userClosingBalanceService.loadUserClosingBalance(userVO);
		model.addAttribute(USER_FORM, userForm);
		request.getSession().setAttribute("SessionUserForm", userForm);
		
		final String FIRST_PAGE = "c2s/reports/UserClosingBalanceView";
		
		return FIRST_PAGE;
		
	}
	
	
	@RequestMapping(value="/reports/SearchUser.form", method=RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList(Model model, HttpServletRequest request, HttpServletResponse response)
            throws Exception

{
      String UserName = request.getParameter("query");
      UserName = UserName + "%";
      String parentCategoryCode = request.getParameter("parentCategoryList");
      String domainList = request.getParameter("domainList");
      String zoneList = request.getParameter("zoneList");
      final UserVO userVO = (UserVO) getUserFormSession(request);

      ArrayList userList = userClosingBalanceService.loadUserList(userVO, parentCategoryCode, domainList, zoneList, UserName);


      Iterator<ChannelUserTransferVO> itr = userList.iterator();
      List<Map<String, String>> list = new ArrayList<>();
      if(userList.size() == 0 || parentCategoryCode == null){
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
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/reports/downloadUserClosingBalance.form", method=RequestMethod.POST)
	public String downloadUserClosingBalance(final Model model, HttpServletRequest request,@ModelAttribute("UsersReportForm") UsersReportModel userForm, HttpServletResponse response, BindingResult bindingResult) throws BTSLBaseException{
		final String methodName = "#downloadUserClosingBalance"; 
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		final UserVO userVO = (UserVO) getUserFormSession(request);
		UsersReportModel sessionUserReportForm = (UsersReportModel) request.getSession().getAttribute("SessionUserForm");

		try{
		 
   		 if (!(userClosingBalanceService.downloadClosingBalance(model, userVO, userForm, sessionUserReportForm, request, response, bindingResult))) {

  			
   			 return "c2s/reports/UserClosingBalanceView";
            
			}

		}catch(Exception e){
			throw new BTSLBaseException(e);
		}finally{

			LogFactory.printLog(methodName, PretupsI.EXITED, LOG);
		}
		return "c2s/reports/UserClosingBalanceView";	
	
	
	}
	}