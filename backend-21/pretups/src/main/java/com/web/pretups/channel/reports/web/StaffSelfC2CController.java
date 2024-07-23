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
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.reports.service.StaffSelfC2CService;

/**
 * @author mohit.miglani
 *
 */
@Controller
public class StaffSelfC2CController extends CommonController {

	@Autowired
	StaffSelfC2CService staffSelfC2CService;

	private static UsersReportModel usersReportModel = new UsersReportModel();
	private static final String MODEL_KEY_ONE = "usersReportModel";
	private static final String PREVIOUS_SCREEN = "c2s/reports/staffSelfC2CTransfersView";
	private static final String CHNL_RPT = "staffSelfReport";

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/staffSelfC2CReport.form", method = RequestMethod.GET)
	public String loadStaffDetails(Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException {
		final String methodName = "loadStaffDetails";
		try {
			authorise(request, response, "STFSLF001", false);

			request.getSession().removeAttribute(CHNL_RPT);

		} catch (IOException | ServletException e) {

			log.errorTrace(methodName, e);
		}

		final UserVO userVO = this.getUserFormSession(request);
		request.getSession().setAttribute(MODEL_KEY_ONE, usersReportModel);
		staffSelfC2CService.loadStaffC2CTransferDetails(usersReportModel,
				request, response, userVO);

		model.addAttribute(MODEL_KEY_ONE, usersReportModel);

		return PREVIOUS_SCREEN;
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
	@RequestMapping(value = "/reports/submitstaffreport.form", method = RequestMethod.POST)
	public String submitchannelReport(
			@ModelAttribute("usersReportModel") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response, BindingResult bindingResult)
			throws BTSLBaseException {

		final UserVO userVO = this.getUserFormSession(request);
		final ChannelUserVO userVO1 = (ChannelUserVO) getUserFormSession(request);

		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute(MODEL_KEY_ONE);
		if (usersReportModel.getFromDate() != null) {
			usersReportModelNew.setFromDate(usersReportModel.getFromDate());
		}
		if (usersReportModel.getToDate() != null) {
			usersReportModelNew.setToDate(usersReportModel.getToDate());
		}
		if (usersReportModel.getTxnSubType() != null) {
			usersReportModelNew.setTxnSubType(usersReportModel.getTxnSubType());
		}
		request.getSession().setAttribute(CHNL_RPT, usersReportModelNew);

		if (staffSelfC2CService.displaySelfTransactionReport(
				usersReportModelNew, request, response, model,userVO, userVO1,
				bindingResult)) {
			model.addAttribute(MODEL_KEY_ONE, usersReportModelNew);
			if (request.getParameter("initiatesummary") != null) {
				return "c2s/reports/staffSelfC2cTransfersReportNew";
			} else {
				return "c2s/reports/staffSelfC2CReportView";
			}

		} else {
			return PREVIOUS_SCREEN;
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
	@RequestMapping(value = "/reports/backstaffreport.form", method = RequestMethod.GET)
	public String backstaffReport(
			@ModelAttribute("backStaffReport") UsersReportModel usersReportModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response)

	throws BTSLBaseException {
		final String methodName = "backstaffReport";

		try {
			authorise(request, response, "STFSLF001", false);
		} catch (ServletException | IOException e) {

			log.errorTrace(methodName, e);
		}

		model.addAttribute(MODEL_KEY_ONE,
				request.getSession().getAttribute(MODEL_KEY_ONE));

		return PREVIOUS_SCREEN;

	}

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/reports/downloadstaffcsvFile.form", method = RequestMethod.GET)
	public void downloadCSVReportFile(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException {
		final String methodName = "downloadCSVReportFile";
		UsersReportModel usersReportModelNew = (UsersReportModel) request
				.getSession().getAttribute(CHNL_RPT);
		InputStream is = null;
		OutputStream os = null;

		try {
			String fileLocation = staffSelfC2CService
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
