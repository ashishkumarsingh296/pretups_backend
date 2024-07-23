package com.web.pretups.channel.reports.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.service.AdditionalCommissionSummaryService;

@Controller
public class AdditionalCommissionSummaryController extends CommonController {
	public static final Log log = LogFactory.getLog(AdditionalCommissionSummaryController.class.getName());

	@Autowired
	AdditionalCommissionSummaryService additionalCommissionSummaryService;
	private static final String CLASS_NAME = "AdditionalCommissionSummaryController";
	private static final String FIRST_PAGE = "c2s/reports/additionalCommissionSummaryView";
	private static final String MODEL_KEY = "usersReportModel";
	private static final String FORM_SUBMITTED = "formSubmitted";
	private static final String SECOND_PAGE = "c2s/reports/additionalCommissionSummaryDataView";
	private static final String INET_PAGE = "c2s/reports/additionalCommissionSummaryInetView";
	
	

	@RequestMapping(value = "/channelreport/load-additional-commission-summary.form", method = RequestMethod.GET)
	public String loadAdditionalCommissionSummary(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String methodName = "#loadAdditionalCommissionSummary";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		try{
			if(request.getSession().getAttribute(FORM_SUBMITTED) != null){
				request.getSession().removeAttribute(FORM_SUBMITTED);
			}
			this.authorise(request, response, "RPTADCS001", false);
			final UserVO userVO = this.getUserFormSession(request);
			UsersReportModel usersReportModel = additionalCommissionSummaryService.loadUsersForAdditionalCommissionSummary(userVO, request);
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
	
	@RequestMapping(value = "/channelreport/submit-additional-commission-summary.form", method = RequestMethod.POST)
	public String displayAdditionalCommissionSummaryReport(final Model model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("usersReportModel") UsersReportModel usersReportModel, BindingResult bindingResult) throws BTSLBaseException{
		final String methodName = "#displayAdditionalCommissionSummaryReport";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		
		ChannelUserVO userVO = (ChannelUserVO) getUserFormSession(request);
		UsersReportModel dataModel = (UsersReportModel) request.getSession().getAttribute(MODEL_KEY);
		dataModel.setDomainCode(usersReportModel.getDomainCode());
		dataModel.setParentCategoryCode(usersReportModel.getParentCategoryCode());
		dataModel.setZoneCode(usersReportModel.getZoneCode());
		dataModel.setServiceType(usersReportModel.getServiceType());
		dataModel.setRadioNetCode(usersReportModel.getRadioNetCode());
		if("DAILY".equalsIgnoreCase(usersReportModel.getRadioNetCode())){
			dataModel.setFromDate(usersReportModel.getFromDate());
			dataModel.setToDate(usersReportModel.getToDate());
		}
		else if("MONTHLY".equalsIgnoreCase(usersReportModel.getRadioNetCode())){
			String tempfromDate = "01/" + usersReportModel.getFromMonth();
			usersReportModel.setTempfromDate(tempfromDate); 
			dataModel.setTempfromDate(tempfromDate);
			dataModel.setFromMonth(usersReportModel.getFromMonth());
			
			String temptoDate = "01/" + usersReportModel.getToMonth();
			usersReportModel.setTemptoDate(temptoDate); 
			dataModel.setTemptoDate(temptoDate);
			dataModel.setToMonth(usersReportModel.getToMonth());
		}
		
		boolean result = additionalCommissionSummaryService.loadAdditionalCommissionSummaryReport(dataModel, bindingResult, userVO, request, model);
		String parentCatCode = dataModel.getParentCategoryCode();
		dataModel.setParentCategoryCode(usersReportModel.getParentCategoryCode());
		request.getSession().setAttribute(FORM_SUBMITTED, dataModel);
		model.addAttribute(MODEL_KEY, dataModel);
		if(result){
			if(request.getParameter("submitButton") != null)
			return SECOND_PAGE;
			else{
				dataModel.setParentCategoryCode(parentCatCode);
				return INET_PAGE;
			}
		}
		else{
			return FIRST_PAGE;
		}
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
	@RequestMapping(value = "/channelreport/additionalCommissionSummaryReportBack.form", method = RequestMethod.GET)
	public String loadBackAdditionalCommissionSummaryReportForm(final Model model,
			HttpServletRequest request, HttpServletResponse response) {
		final String methodName = "#loadBackAdditionalCommissionSummaryReportForm";

		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}

		return FIRST_PAGE;

	}
	
	@RequestMapping(value = "/channelreport/downloadSummaryFile.form", method = RequestMethod.GET)
	public void downloadAddCommSummaryCSVReportFile(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ServletException, IOException, SQLException, InterruptedException {
		 if (log.isDebugEnabled()) {
	        	log.debug("AdditionalCommissionDetailsController # downloadAddCommSummaryCSVReportFile", PretupsI.ENTERED);
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
			String fileLocation = additionalCommissionSummaryService.downloadAddCommSummaryCSVReportFile(model, dataModel);
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
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					log.errorTrace("AdditionalCommissionSummaryController : downloadAddCommSummaryCSVReportFile", e);
				}
			}
			
			if(os != null){
				try {
					os.close();
				} catch (IOException e) {
					log.errorTrace("AdditionalCommissionSummaryController : downloadAddCommSummaryCSVReportFile", e);
				}
			}
			
			if (log.isDebugEnabled()) {
		        log.debug("AdditionalCommissionDetailsController # downloadAddCommSummaryCSVReportFile", PretupsI.EXITED);
		    }

		}

	}
}
