
	package com.btsl.pretups.master.web;

	import java.io.InputStream;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.btsl.common.CommonController;
import com.btsl.pretups.master.service.NetworkSummaryReportService;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
		/*  
			**This class provides method for loading UI for NetworkSummaryReport as well as   Downloads the  data for NetworkSummaryReport
		*/
	@Controller
	public class  NetworkSummaryReportController extends CommonController {
	
	@Autowired
	private NetworkSummaryReportService networkSummaryReportService;
	
	/**
	 * Load  UI for  Network Summary Report
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @return String the path of view 
	 * @throws Exception
	 */
	 			@RequestMapping(value = "/master/networkSummaryReport.form", method = RequestMethod.GET)
			public String loadnetworkSummaryform(final Model model, HttpServletRequest request, HttpServletResponse response) throws  Exception
			 {
				final String methodName = "LoadDomain";
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Entered");
				}
				authorise(request, response, "NTWSMR01A", false);
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exited");
				}
				return "master/networkSummaryReport";
			}

		/**
		 * Submit Action for  UI of  Network Summary Report
		 *
		 * @param request  The HttpServletRequest object
		 * @param response The HttpServletResponse object
		 * @param request  The fromDate object
		 * @param request  The toDate object
		 * @param request  The reporttype object
		 * @return String the path of view 
		 * @throws Exception
		 */
		@RequestMapping(value = "/master/submit-networkSummaryReport.form", method = RequestMethod.POST)
		public String submitnetworkSummaryform(@RequestParam("fromDate") String fromdate,@RequestParam("toDate") String todate,@RequestParam("reporttype") String reporttype ,Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
	 	final String methodName = "submitnetworkSummaryform";
			InputStream inputStream = null;
			UserVO userVO = this.getUserFormSession(request);
			
			if (log.isDebugEnabled()) {
			log.debug(methodName,  "Entered" );
				log.debug(methodName, BTSLUtil.logForgingReqParam(fromdate));
				log.debug(methodName, BTSLUtil.logForgingReqParam(todate));
				log.debug(methodName, BTSLUtil.logForgingReqParam(reporttype));
			}
				
				
				
			   // NetworkSummaryReportServiceImpl networkSummaryReportService  = new NetworkSummaryReportServiceImpl();
			    inputStream =	networkSummaryReportService.download(reporttype,fromdate,todate,userVO.getNetworkID());
			   
				
				if(inputStream != null)
				{
					String mimeType=null;
					if(mimeType==null){
					mimeType = "application/octet-stream";
					}
					if (log.isDebugEnabled()) {
					log.debug(methodName,mimeType );
					}
					
					response.setContentType(mimeType);
					if(!reporttype.equals("HOURLY"))
					response.setHeader("Content-Disposition", "attachment; filename="+ "NetworkSummaryReport_"+reporttype+"_"+fromdate+"_"+ todate +BTSLUtil.getFileNameStringFromDate(new Date())+".xls"); 
					else
					response.setHeader("Content-Disposition", "attachment; filename="+ "NetworkSummaryReport_"+reporttype+"_"+fromdate+"_"+ BTSLUtil.getFileNameStringFromDate(new Date())+".xls"); 
					//Copy bytes from source to destination(outputstream in this example), closes both streams.
					
					FileCopyUtils.copy(inputStream, response.getOutputStream());
				}
				else
				{
					model.addAttribute("fail" , true);
					log.debug(methodName,"No inputStream returned from the Rest Service" );
				}
				
				if (log.isDebugEnabled()) 
				log.debug(methodName,  "E" );
					
				return "master/networkSummaryReport";
		}


	}
