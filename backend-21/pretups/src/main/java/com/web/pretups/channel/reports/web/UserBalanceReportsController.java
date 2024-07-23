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
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.OperationSummaryReportService;
import com.web.pretups.channel.reports.service.UserBalanceMovementReportsService;
import com.web.pretups.channel.reports.service.UserZeroBalanceCounterSummaryService;

@Controller
public class UserBalanceReportsController extends CommonController{
	@Autowired
	private UserZeroBalanceCounterSummaryService userzerobalService;
	@Autowired
	private UserBalanceMovementReportsService userBalanceMovementReportsService;
	@Autowired
	private OperationSummaryReportService operationSummaryReportService;
	public static final Log log = LogFactory.getLog(UserBalanceReportsController.class.getName());
	private static final String PANEL_NO = "PanelNo";
	private static final String MODEL_KEY = "userDailyBalanceMovementReport";
	private static final String MSG_ZONE_CODE_REQUIRED = "Select Zone First";
	private static final String MSG_DOMAIN_CODE_REQUIRED = "Select Domain First";
	private static final String MSG_FROM_CAT_REQUIRED = "Select Search Category First";
	private static final String NEXT_PAGE = "c2s/reports/dailyUserBalanceMovementReport";
	private static final String RETURN_PAGE = "c2s/reports/dailyUserBalanceMovement";
	private static UsersReportModel usersReportModel = new UsersReportModel();
	private static final String MODEL_KEY_ONE = "usersReportModel";
    private static final String INET_PAGE="c2s/reports/dailyUserBalanceMovementSummaryView";
    private static final String OPERATION_SUMMARY_FIRST_PAGE="c2s/reports/operationSummaryReportView";
    private static final String SESSION_USER_FORM="SessionUserForm";
    
	@RequestMapping(value = "/reports/userDailyBalMovement.form", method = RequestMethod.GET)
	public String loadUserBalance(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ServletException, IOException {
		if (log.isDebugEnabled()) {
        	log.debug("UserBalanceReportsController#loadUserBalance", PretupsI.ENTERED);
        }
		 authorise(request, response, "UBALMOV01A", false);
		 
		 request.getSession().removeAttribute(MODEL_KEY);
		 request.getSession().removeAttribute(PANEL_NO);
		 
		 UsersReportModel usersReportModel = new UsersReportModel();
		 final UserVO userVO = this.getUserFormSession(request);
	     model.addAttribute(PANEL_NO, "Panel-One");
	     
	     if(userBalanceMovementReportsService.loadUserBalMovementSummaryReportPage(request,userVO,usersReportModel,model)){
	        	model.addAttribute(MODEL_KEY, usersReportModel);
	        	request.getSession().setAttribute("usersReport", usersReportModel);
	        }
				return "c2s/reports/dailyUserBalanceMovement";
		
	}
	
    @RequestMapping(value = "/reports/SearchFromUser.form", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> loadFromUserList(Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
                    List<Map<String, String>> list = new ArrayList<>();
                    String userName = request.getParameter("query");
                    userName = userName + "%";
                    String zoneCode = request.getParameter("zoneCode");
                    String domainCode = request.getParameter("domainCode");
                    String fromTransferCategorycode = request.getParameter("fromCategoryCode");
                    final UserVO userVO = this.getUserFormSession(request);
                    
                    UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("usersReport");
                    if (BTSLUtil.isNullString(zoneCode)) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("loginId",MSG_ZONE_CODE_REQUIRED);
                                    map.put("userId", "");
                                    list.add(map);
                                    return list;
                    }
                    else if (BTSLUtil.isNullString(domainCode)) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("loginId",MSG_DOMAIN_CODE_REQUIRED);
                                    map.put("userId", "");
                                    list.add(map);
                                    return list;
                    }
                    else if (BTSLUtil.isNullString(fromTransferCategorycode)) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("loginId",MSG_FROM_CAT_REQUIRED );
                                    map.put("userId", "");
                                    list.add(map);
                                    return list;
                    }
                    List<ChannelUserTransferVO> fromUserList = userBalanceMovementReportsService.loadFromUserList(usersReportModelNew,userVO,zoneCode,domainCode,fromTransferCategorycode,userName,request);
                    Iterator<ChannelUserTransferVO> itr = fromUserList.iterator();

                    
                    if (fromUserList.isEmpty()) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put("loginId", "NO_DATA_FOUND");
                                    map.put("userId", "");
                                    list.add(map);

                    }
                    
                    
                    while (itr.hasNext()) {
                                    ChannelUserTransferVO object = itr.next();
                                    Map<String, String> map = new HashMap<>();
                                    String loginId = object.getUserName() + "(" + object.getUserID() + ")";
                                    map.put("loginId", loginId);
                                    map.put("userId", object.getUserID());
                                    list.add(map);
                    }
                    
                    

                    return list;

    }

	
	@RequestMapping(value = "/reports/enquirySearchNext.form", method = RequestMethod.POST)
    public String enquirySearchNext(final Model model,@ModelAttribute("userBalMovementSummaryReportModel") UsersReportModel usersReportModel,
                                    HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult)
                                    throws BTSLBaseException, IOException, ParseException, ServletException {
                   final UserVO userVO = this.getUserFormSession(request);
                    
                    UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("usersReport");
                    
                    
                                    usersReportModelNew.setMsisdn(usersReportModel.getMsisdn());
                                    
                    
                    usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
                    
                    
                    usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
                    
                    
                    usersReportModelNew.setParentCategoryCode(usersReportModel.getParentCategoryCode());
                    
                    
                    usersReportModelNew.setUserName(usersReportModel.getUserName());
                    
                    
                   usersReportModelNew.setFromDate(usersReportModel.getFromDate());
                    
                    
                    usersReportModelNew.setToDate(usersReportModel.getToDate());
                    
                    
                    
                    
                    if(userBalanceMovementReportsService.displayUserBalanceReport(request,response,usersReportModelNew,userVO,model,bindingResult))
                    {
                                    if(request.getParameter("iNETReportUserName")!= null || request.getParameter("iNETReportMSISDN")!= null)
                                    {
                                                    model.addAttribute(MODEL_KEY, usersReportModelNew);
                                                    return INET_PAGE;
                                    }
                                    else
                                    {
                                                    model.addAttribute(MODEL_KEY, usersReportModelNew);
                                                    return NEXT_PAGE;
                                    }
                                    
                    }
                    else
                    {
                                    model.addAttribute(MODEL_KEY, usersReportModelNew);
                                    return RETURN_PAGE;
                    }
                    

    }

    @RequestMapping(value = "/reports/dailyUserBalMovement.form", method = RequestMethod.GET)
    public String enquirySearchBack(final Model model,@ModelAttribute UsersReportModel usersReportModel,
                                    HttpServletRequest request, HttpServletResponse response)
                                    throws BTSLBaseException, IOException, ParseException, ServletException {
                    
                    
                    
                    return RETURN_PAGE;

    }

    
    /**
    * @param model
    * @param request
    * @param response
    * @throws BTSLBaseException
    */
    @RequestMapping(value = "/reports/downloadFile.form", method = RequestMethod.GET)
    public void downloadCSVReport(final Model model,
                HttpServletRequest request, HttpServletResponse response)
                throws BTSLBaseException
            {
          final String methodName = "downloadCSVReport";
          UsersReportModel usersReportModelNew = (UsersReportModel) request
                      .getSession().getAttribute("usersReport");
          InputStream is = null;
          OutputStream os = null;
          try {
                String fileLocation = userBalanceMovementReportsService
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

	
	
	
	@RequestMapping(value = "/reports/load-user-balances.form", method = RequestMethod.GET)
	public String loadUserBalances(Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException  {
		final String methodName = "loadUserBalances";
		if (log.isDebugEnabled()) {
			log.debug(
					"UserZeroBalanceCounterSummaryController#loadUserBalance",
					PretupsI.ENTERED);
		}
		try {
			authorise(request, response, "ZBALSUM001", false);
		} catch (IOException | ServletException e) {

			log.errorTrace(methodName, e);
		}

		request.getSession().removeAttribute("usersReportModel");

		final UserVO userVO = this.getUserFormSession(request);
		 userzerobalService.loadThresholdtype(usersReportModel,
				request, response, userVO);

		model.addAttribute(MODEL_KEY_ONE, usersReportModel);
		request.getSession().setAttribute("usersReport", usersReportModel);
		if (log.isDebugEnabled()) {
			log.debug(
					"UserZeroBalanceCounterSummaryController#loadUserBalance",
					PretupsI.EXITED);
		}
		return "c2s/reports/zeroBalCounterSummaryView";

	}

	@RequestMapping(value = "/reports/submituserreport.form", method = RequestMethod.POST)
	public String submitReport(
			@ModelAttribute("usersReportModel") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult) throws BTSLBaseException
			 {

		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#submitReport",
					PretupsI.ENTERED);
		}
		String rptCode = "ZBALSUM001";

		final UserVO userVO = this.getUserFormSession(request);
		final ChannelUserVO userVO1 = (ChannelUserVO) getUserFormSession(request);

		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute("usersReport");

		usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());

		if (!(BTSLUtil.isNullString(usersReportModel.getZoneCode()))) {
			usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
		}
		if (!(BTSLUtil.isNullString(usersReportModel.getParentCategoryCode()))) {
			usersReportModelNew.setParentCategoryCode(usersReportModel
					.getParentCategoryCode());
		}
		if (!(BTSLUtil.isNullString(usersReportModel.getThresholdType()))) {
			usersReportModelNew.setThresholdType(usersReportModel
					.getThresholdType());
		}
		if (!(BTSLUtil.isNullString(usersReportModel.getDailyDate()))) {
			usersReportModelNew.setDailyDate(usersReportModel.getDailyDate());
		}
		if (!(BTSLUtil.isNullString(usersReportModel.getFromMonth()))) {
			usersReportModelNew.setFromMonth(usersReportModel.getFromMonth());
		}

		usersReportModelNew.setFromDate(usersReportModel.getFromDate());

		if (!(BTSLUtil.isNullString(usersReportModel.getToDate()))) {
			usersReportModelNew.setToDate(usersReportModel.getToDate());
			usersReportModelNew.setTempfromDate(usersReportModel.getDailyDate());
		}
		usersReportModelNew.setRadioNetCode(usersReportModel.getRadioNetCode());
		if (usersReportModel.getFromMonth() != null
				&& !usersReportModel.getFromMonth().isEmpty()) {
			
			String mnth = usersReportModel.getFromMonth();
			String emnth = mnth.substring(0, 2);
			String yr = mnth.substring(3, 5);
			String a = 01 + "/" + emnth + "/" + yr;
			usersReportModelNew.setTempfromDate(a);
		}

		if (!(BTSLUtil.isNullString(usersReportModel.getFromTime()))) {
			usersReportModelNew.setFromTime(usersReportModel.getFromTime());
		}
		if (!(BTSLUtil.isNullString(usersReportModel.getToTime()))) {
			usersReportModelNew.setToTime(usersReportModel.getToTime());
		}
		usersReportModelNew.setNetworkName(usersReportModel.getNetworkName());
		usersReportModelNew.setRptfromDate(usersReportModel.getRptfromDate());
		usersReportModelNew.setRpttoDate(usersReportModel.getRpttoDate());
		usersReportModelNew.setrptCode(rptCode);
		if (userzerobalService.displayZeroBalSumReport(usersReportModelNew,
				request, response, userVO, userVO1, bindingResult)) {
			model.addAttribute("usersReportModel", usersReportModelNew);
			if(request.getParameter("initiatesummary")!=null )
			{
			return "c2s/reports/zeroBalCounterSummaryReportNew";
			}
			else
			{
				return "c2s/reports/zeroBalCounterSummaryReportView";
			}
		} 
		
		
		else {

			return "c2s/reports/zeroBalCounterSummaryView";
		}

	}

	@RequestMapping(value = "/reports/backuserreport.form", method = RequestMethod.GET)
	public String backReport(
			@ModelAttribute("backUserReport") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException {
		final String methodName = "backReport";
		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#backReport",
					PretupsI.ENTERED);
		}
		try {
			authorise(request, response, "ZBALSUM001", false);
		} catch (ServletException | IOException e) {

			log.errorTrace(methodName, e);
		}

		model.addAttribute(MODEL_KEY_ONE,
				request.getSession().getAttribute("usersReportModel"));
		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#backReport",
					PretupsI.EXITED);
		}
		return "c2s/reports/zeroBalCounterSummaryView";
	}

	@RequestMapping(value = "/reports/downloadcsvFile.form", method = RequestMethod.GET)
	public void downloadCSVReportFile(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, SQLException, InterruptedException
		  {
		final String methodName = "downloadCSVReportFile";
		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute("usersReport");
		
		
			String fileLocation = userzerobalService
					.downloadFileforSumm(usersReportModelNew);
			File file = new File(fileLocation);
			try(InputStream is =new FileInputStream(file);) {
				OutputStream os = null;
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
			log.errorTrace(methodName, e);
			throw new BTSLBaseException(e);
		} 

	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/reports/operationSummaryReport.form", method = RequestMethod.GET)
	public String loadOperationSummary(final Model model, HttpServletRequest request, HttpServletResponse response) {
		final String methodName = "downloadCSVReportFile";
		if (log.isDebugEnabled()) {
        	log.debug("UserBalanceReportsController#loadOperationSummary", PretupsI.ENTERED);
        }
		  try {
			authorise(request, response, "OPTSRPT00A", false);
			request.getSession().removeAttribute(SESSION_USER_FORM);
			 UserVO userVO = getUserFormSession(request);
			 usersReportModel = operationSummaryReportService.loadOperationSummaryReport(request,userVO);
			 request.getSession().setAttribute(SESSION_USER_FORM, usersReportModel);
			 model.addAttribute("UsersReportForm", usersReportModel);
			 model.addAttribute("UserVO", userVO);
			
		} catch (ServletException | IOException | BTSLBaseException e) {
			log.errorTrace(methodName, e);
		}
		 
		 

				return OPERATION_SUMMARY_FIRST_PAGE;
		
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/reports/searchOperationSummaryUser.form", method=RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList( HttpServletRequest request, HttpServletResponse response, Model model) throws BTSLBaseException
         

{
      String userName = request.getParameter("query");
      userName = userName + "%";
      String parentCategoryCode = request.getParameter("parentCategoryList");
      String domainList = request.getParameter("domainList");
      String zoneList = request.getParameter("zoneList");
      final UserVO userVO = getUserFormSession(request);

      List<ChannelUserTransferVO> userList = operationSummaryReportService.loadUserList(userVO, parentCategoryCode, domainList, zoneList, userName);


      Iterator<ChannelUserTransferVO> itr = userList.iterator();
      List<Map<String, String>> list = new ArrayList<>();
      if(userList.isEmpty() || parentCategoryCode == null){
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
	@RequestMapping(value = "/reports/submitOperationSummary.form", method = RequestMethod.POST)
	public String submitOperationSummary(@ModelAttribute("operationSummaryForm") UsersReportModel usersReportModel,Model model, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult) throws BTSLBaseException
			 {

		final String methodName = "#submitOperationSummary"; 
		
		if (log.isDebugEnabled()) {
			log.debug("UserBalanceReportsController#submitOperationSummary", PretupsI.ENTERED);
		}
		
		final UserVO userVO = getUserFormSession(request);
		UsersReportModel sessionUserReportForm = (UsersReportModel) request.getSession().getAttribute(SESSION_USER_FORM);

		try{
		 
   		 if (operationSummaryReportService.displayOperationSummaryReport(model, userVO, usersReportModel, sessionUserReportForm, request, response, bindingResult)) {


   			if(request.getParameter("submitButton")!=null )
   			{
   					 
   				return "c2s/reports/displayOperationSummaryReportView";
   			}
   			else
   			{
   				return "c2s/reports/displayOperationSummaryReportViewInet";
   			}
   			 
            
			}
   		 else{
   			return OPERATION_SUMMARY_FIRST_PAGE;
   		 }


		}catch(Exception e){
			throw new BTSLBaseException(e);
			
		}finally{

			LogFactory.printLog(methodName, PretupsI.EXITED, log);
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
	@RequestMapping(value = "/reports/operationSummaryReportBack.form", method = RequestMethod.GET)
	public String operationSummaryReportBack(@ModelAttribute("backUserReport") UsersReportModel usersReportModel,Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException {
		
		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#operationSummaryReportBack",
					PretupsI.ENTERED);
		}
		
		if (log.isDebugEnabled()) {
			log.debug("UserZeroBalanceCounterSummaryController#operationSummaryReportBack",
					PretupsI.EXITED);
		}
		return OPERATION_SUMMARY_FIRST_PAGE;
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/downloadOperationSummaryReportCsvFile.form", method = RequestMethod.GET)
	public void downloadOperationSummaryReportCsvFile(final Model model,HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException
		  {
		final String methodName = "downloadOperationSummaryReportCsvFile";
		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute("SessionUserForm");
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = operationSummaryReportService.downloadFileforSumm(usersReportModelNew);
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
