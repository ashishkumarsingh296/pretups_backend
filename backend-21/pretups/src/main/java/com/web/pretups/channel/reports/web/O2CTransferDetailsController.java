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
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.O2CTransferDetailsService;






/**
 * @author rahul.arya
 *
 */
@Controller
public class O2CTransferDetailsController extends
CommonController {

	
	@Autowired
	private O2CTransferDetailsService o2cTransferDetailsService;
	
	private static final String RETURN_PAGE = "c2s/reports/O2CTransferDetailView";
	
	private static final String FAIL_KEY    = "fail";
	
	private static final String NEXT_PAGE = "c2s/reports/O2CTransferDetailsNext";
	
	private static final String INET_PAGE ="c2s/reports/o2cTransferDetailsReportView";
	
	private static final String MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH = "SELECT ZONE FIRST";
	
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "SELECT CATEGORY FIRST";
	
	private static final String LOGIN_ID = "loginId";
	
	private static final String USER_ID = "userId";
	
	private static final String PATH = "/reportsO2C/o2cTransferDetails.form";
	
	@RequestMapping(value = {"/reportsO2C/o2cTransferDetails.form","/reportsO2C/dailyO2CTransferSummary.form"}, method = RequestMethod.GET)
	public String loadChannelUser(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ParseException, ServletException {
		
		UsersReportModel usersReportModel = new UsersReportModel();
		
		String requestType = request.getServletPath(); 
		if(requestType.equalsIgnoreCase(PATH) )
		{	
		usersReportModel.setRequestType("DETAILS");
		}
		else
		{
			usersReportModel.setRequestType("DAILY");
		}
      
		
        authorise(request, response, "RPTO2CDD1A", false);
        request.getSession().removeAttribute("usersReportModel");
        final UserVO userVO = this.getUserFormSession(request);
        
		o2cTransferDetailsService.loadO2CTransferDetails(request,response,usersReportModel,userVO,model);
		
		request.getSession().setAttribute("usersReport", usersReportModel);
		
		return RETURN_PAGE;

	}
	
	
	
	@RequestMapping(value = "/reportsO2C/enquirySearchNext.form", method = RequestMethod.POST)
	public String enquirySearchNext(final Model model,@ModelAttribute("O2CTransDetails") UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult)
			throws BTSLBaseException, IOException, ParseException, ServletException, ValidatorException, SAXException {
		final UserVO userVO = this.getUserFormSession(request);
		
		UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("usersReport");
		if(usersReportModel.getDomainCode() != null)
		{
			usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
		}
		if(usersReportModel.getZoneCode() != null)
		{
			usersReportModelNew.setZoneCode(usersReportModel.getZoneCode());
		}
		if(usersReportModel.getFromtransferCategoryCode() != null)
		{
			usersReportModelNew.setFromtransferCategoryCode(usersReportModel.getFromtransferCategoryCode());
		}
		if(usersReportModel.getUserName() != null)
		{
			usersReportModelNew.setUserName(usersReportModel.getUserName());
		}
		if(usersReportModel.getTransferCategory() != null)
		{
			usersReportModelNew.setTransferCategory(usersReportModel.getTransferCategory());
		}
		if(usersReportModel.getTxnSubType() != null)
		{
			usersReportModelNew.setTxnSubType(usersReportModel.getTxnSubType());
		}
		if(usersReportModel.getFromDate() != null)
		{
			usersReportModelNew.setFromDate(usersReportModel.getFromDate());
		}
		if(usersReportModel.getToDate() != null)
		{
			usersReportModelNew.setToDate(usersReportModel.getToDate());
		}
		if(usersReportModel.getFromTime() != null)
		{
			usersReportModelNew.setFromTime(usersReportModel.getFromTime());
		}
		if(usersReportModel.getToTime() != null)
		{
			usersReportModelNew.setToTime(usersReportModel.getToTime());
		}
		
		
		if(o2cTransferDetailsService.loadEnquiryList(request,response,usersReportModelNew,userVO,model,bindingResult))
		{
			
			model.addAttribute("o2cDetails", usersReportModelNew);
			
			if(request.getParameter("submitUserSearch")!=null )
			{
				return NEXT_PAGE;
			}
			else
			{
				return INET_PAGE;
			}
			}
		
		else
		{
			return RETURN_PAGE;
		}
		

	}
	@RequestMapping(value = "/reportsO2C/O2CDetailsBackMainPage.form", method = RequestMethod.GET)
	public String enquirySearchBack(final Model model,@ModelAttribute UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ParseException, ServletException {
		
		request.setAttribute("BackButton", true);
		
		return RETURN_PAGE;

	}
	
	/**
	 * @param usersReportModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/reportsO2C/SearchUser.form", method = RequestMethod.GET)
	@ResponseBody public List<Map<String, String>> userList(
			@ModelAttribute("O2CTransDetails") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException

	{
		
		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("userName");
		userName = userName + "%";
		String zoneCode = request.getParameter("zoneCode");
		String domainCode = request.getParameter("domainCode");
		String fromtransferCategoryCode = request.getParameter("fromtransferCategoryCode");
		
		
		if(BTSLUtil.isNullString(zoneCode)){
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }
		
		if(BTSLUtil.isNullString(fromtransferCategoryCode)){
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }
		
		final UserVO userVO = this.getUserFormSession(request);

		
		
		List<ListValueVO> userList = o2cTransferDetailsService.loadC2cFromUserList(
				userVO, zoneCode, domainCode,userName,fromtransferCategoryCode,usersReportModel);
		
		
		if (userList.isEmpty()) {

			model.addAttribute(
					FAIL_KEY,
					PretupsRestUtil
							.getMessageString("c2s.reports.msg.datanotfound"));

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
			 String loginId = object.getLabel() + "(" + object.getValue() +
			")";
			
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getLabel());
			list.add(map);
		}

		return list;

	}
	@RequestMapping(value = "/reportsO2C/downloadFile.form", method = RequestMethod.GET)
    public void downloadCSVReportFile(final Model model, HttpServletRequest request, HttpServletResponse response)
                  throws BTSLBaseException, ServletException, IOException, SQLException, InterruptedException {
          
		String methodName = "downloadCSVReportFiles";
		UsersReportModel usersReportModelNew =  (UsersReportModel) request.getSession().getAttribute("usersReportModel");
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = o2cTransferDetailsService.downloadFileforEnq(usersReportModelNew);
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
