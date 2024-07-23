package com.web.pretups.channel.reports.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
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
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.AdditionalCommissionDetailsService;

@Controller
public class AdditionalCommissionDetailsController extends CommonController {
	public static final Log log = LogFactory.getLog(AdditionalCommissionDetailsController.class.getName());

	@Autowired
	private AdditionalCommissionDetailsService additionalCommissionDetailsService;
	private static final String CLASS_NAME = "additionalCommissionDetailsController";
	private static final String FIRST_PAGE = "c2s/reports/additionalCommissionDetailsView";
	private static final String SECOND_PAGE = "c2s/reports/additionalCommissionDetailsDataView";
	private static final String INET_PAGE = "c2s/reports/additionalCommissionDetailsInetView";
	private static final String MODEL_KEY = "usersReportModel";
	private static final String FORM_NO = "formNumber";
	private static final String NO_ZONE_USER_SEARCH = "SELECT ZONE FIRST";
	private static final String NO_CAT_USER_SEARCH = "SELECT CATEGORY FIRST";
	private static final String FORM_SUBMITTED = "formSubmitted";
	private static final String LOGINID = "loginId";
	private static final String USERID = "userId";
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/channelreport/load-additional-commission-details.form", method = RequestMethod.GET)
	public String loadAdditionalCommissionDetail(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String methodName = "#loadAdditionalCommissionDetail";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		try{
			this.authorise(request, response, "RPTVACP01A", false);
			request.getSession().setAttribute(FORM_NO, "Panel-One");
			if(request.getSession().getAttribute(FORM_SUBMITTED) != null)
				request.getSession().removeAttribute(FORM_SUBMITTED);
			UsersReportModel usersReportModel = additionalCommissionDetailsService.loadUsersForAdditionalCommission(request);
			model.addAttribute(MODEL_KEY, usersReportModel);
			request.getSession().setAttribute(MODEL_KEY, usersReportModel);
		} catch (ServletException | IOException | BTSLBaseException exception) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}

		return FIRST_PAGE;
	}
	
	/**
	 * 
	 * @param usersReportModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/channelreport/searchUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList(@ModelAttribute(MODEL_KEY) UsersReportModel usersReportModel, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception{

		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("query");
		userName = userName + "%";
		String zoneCode = request.getParameter("zoneList");
		String domainCode = request.getParameter("domainList");
		String categoryCode = request.getParameter("parentCategoryList");


		if(BTSLUtil.isNullString(zoneCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGINID, NO_ZONE_USER_SEARCH);
			map.put(USERID, "");
			list.add(map);
			return list;
		}

		if(BTSLUtil.isNullString(categoryCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGINID, NO_CAT_USER_SEARCH);
			map.put(USERID, "");
			list.add(map);
			return list;
		}

		final UserVO userVO = this.getUserFormSession(request);



		List<ChannelUserTransferVO> userList = additionalCommissionDetailsService.loadUserList(usersReportModel, userVO, userName, domainCode, zoneCode, categoryCode);


		if (userList.isEmpty()) {
			model.addAttribute("fail",PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.msg.datanotfound"));
		}

		Iterator<ChannelUserTransferVO> itr = userList.iterator();
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGINID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			ChannelUserTransferVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + "(" + object.getUserID()
					+ ")";
			map.put(LOGINID, loginId);
			map.put(USERID, object.getUserID());
			list.add(map);
		}

		return list;

	} 
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param response
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/channelreport/submit-additional-commission-details.form", method = RequestMethod.POST)
	public String displayAdditionalCommissionReport(final Model model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("usersReportModel") UsersReportModel usersReportModel, BindingResult bindingResult) throws BTSLBaseException{
		final String methodName = "#displayAdditionalCommissionReport";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
    
		UserVO userVO = getUserFormSession(request);
		UsersReportModel dataModel = (UsersReportModel) request.getSession().getAttribute(MODEL_KEY);
		dataModel.setDomainCode(usersReportModel.getDomainCode());
		dataModel.setParentCategoryCode(usersReportModel.getParentCategoryCode());
		dataModel.setZoneCode(usersReportModel.getZoneCode());
		dataModel.setCurrentDate(usersReportModel.getCurrentDate());
		dataModel.setFromTime(usersReportModel.getFromTime());
		dataModel.setToTime(usersReportModel.getToTime());
		dataModel.setMsisdn(usersReportModel.getMsisdn());
		dataModel.setUserName(usersReportModel.getUserName());
		if(usersReportModel.getCurrentDateRptChkBox() != null){
			dataModel.setCurrentDateRptChkBox(usersReportModel.getCurrentDateRptChkBox());
		}else{
			dataModel.setCurrentDateRptChkBox("");
		}
		
		boolean result = additionalCommissionDetailsService.loadAdditionalCommissionReport(dataModel, bindingResult, userVO, request, model);
		if(!(BTSLUtil.isNullString(usersReportModel.getParentCategoryCode())) && !("").equals(usersReportModel.getParentCategoryCode()))
		dataModel.setParentCategoryCode(usersReportModel.getParentCategoryCode());
		if(!(BTSLUtil.isNullString(usersReportModel.getUserName()))&& !("").equals(usersReportModel.getUserName()))
		dataModel.setUserName(usersReportModel.getUserName());
		request.getSession().setAttribute(FORM_SUBMITTED, dataModel);
		model.addAttribute(MODEL_KEY, dataModel);
		if(result){	
			if(request.getParameter("submitUser") != null || request.getParameter("submitMsisdn") != null)
				return SECOND_PAGE;
			else{
				String userName = dataModel.getUserName();
				String[] parts = userName.split("\\(");
				userName = parts[0];
				dataModel.setUserName(userName);
				dataModel.setParentCategoryCode(dataModel.getPrntCatCode());
				return INET_PAGE;
			}
				
		}	
		else
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
	@RequestMapping(value = "/channelreport/additionalCommissionDetailsReportBack.form", method = RequestMethod.GET)
	public String loadBackAdditionalCommissionDetailsReportForm(final Model model,
			HttpServletRequest request, HttpServletResponse response)
					throws BTSLBaseException, ServletException, IOException {
		final String methodName = "#loadBackAdditionalCommissionDetailsReportForm";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		return FIRST_PAGE;

	}
	
	
	@RequestMapping(value = "/channelreport/downloadFile.form", method = RequestMethod.GET)
	public void downloadAddCommCSVReportFile(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ServletException, IOException, SQLException, InterruptedException {
		 if (log.isDebugEnabled()) {
	        	log.debug("AdditionalCommissionDetailsController # downloadCSVReportFile", PretupsI.ENTERED);
	        }
	
		UsersReportModel dataModel = (UsersReportModel) request.getSession().getAttribute(MODEL_KEY);
		String parentCatCode = dataModel.getParentCategoryCode();
		InputStream is = null;
		OutputStream os = null;
		try {
			final String[] arr = dataModel.getParentCategoryCode().split("\\|");
			if(arr.length == 2){
			dataModel.setParentCategoryCode(arr[1]);
			}
			String fileLocation = additionalCommissionDetailsService.downloadAddCommDetailsCSVReportFile(model, dataModel);
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
			dataModel.setParentCategoryCode(parentCatCode);
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			try{
        		if(is!=null){
        			is.close();	
        		}
        	}catch(Exception e){
        		 log.errorTrace("AdditionalCommissionDetailsController # downloadCSVReportFile", e);
        	}
        	try{
        		if(os!=null){
        			os.close();	
        		}
        	}catch(Exception e){
        		log.errorTrace("AdditionalCommissionDetailsController # downloadCSVReportFile", e);
        	}
			 if (log.isDebugEnabled()) {
		        	log.debug("AdditionalCommissionDetailsController # downloadCSVReportFile", PretupsI.EXITED);
		        }
		}

	}
}
