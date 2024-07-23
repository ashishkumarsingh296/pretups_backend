package com.web.pretups.channel.reports.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
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
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.Channel2ChannelTransferReportsService;
import com.web.pretups.channel.reports.service.LMSRedemptionReportsService;
/**
 * 
 * @author sweta.verma
 *
 */
@Controller
public class LMSRedemptionReportController extends CommonController {

	@Autowired
	private LMSRedemptionReportsService lMSRedemptionReportsService;
	@Autowired
	Channel2ChannelTransferReportsService channel2ChannelTransferReportsService;
	private Log log = LogFactory.getLog(this.getClass().getName());
	private static final String PANEL_NO = "PanelNo";
	private static final String MODEL_KEY = "lmsRedemptionReport";
	private static final String MSG_ZONE_CODE_REQUIRED = "Select Zone First";
	private static final String MSG_DOMAIN_CODE_REQUIRED = "Select Domain First";
	private static final String MSG_FROM_CAT_REQUIRED = "Select Search Category First";
	private static final String PAGE_ONE = "loyaltymgmt/reports/lmsRedeemption";
	private static final String PAGE_TWO = "loyaltymgmt/reports/lmsRedemptionReportView";
	private static final String INET_PAGE = "loyaltymgmt/reports/lmsRedeemptionReport";
	
	
/**
 * 
 * @param model
 * @param request
 * @param response
 * @return
 * @throws Exception
 */
 
	@RequestMapping(value = "/pretups/lmsRedemption.form", method = RequestMethod.GET)
	public String loadLmsRedemptionReport(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws  Exception {
		if (log.isDebugEnabled()) {
			log.debug("LMSRedemptionReport#", PretupsI.ENTERED);
		}
		request.getSession().removeAttribute(MODEL_KEY);
		request.getSession().removeAttribute(PANEL_NO);
		authorise(request, response, "LMSRDRPT1A", false);
		LmsRedemptionReportModel lmsRedemptionReportModel = new LmsRedemptionReportModel();
		final UserVO userVO = this.getUserFormSession(request);
		request.getSession().setAttribute(PANEL_NO, "Panel-One");
		if (lMSRedemptionReportsService.loadLmsRedemptionReportPage(userVO, lmsRedemptionReportModel, model, request, response)) {
			model.addAttribute(MODEL_KEY, lmsRedemptionReportModel);
			
			
		}
		return PAGE_ONE;

	}
	
	/**
	 * 
	 * @param lmsRedemptionReportModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/pretups/submitLmsRedemptionDetailsReports.form", method = RequestMethod.POST)
	public String displayLmsRedemptionReport(@ModelAttribute("lmsRedemptionReportModel") LmsRedemptionReportModel lmsRedemptionReportModel,
			BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response)
			throws  Exception {
		
		log.debug("LMSRedemptionReport#", model);
		final UserVO userVO = this.getUserFormSession(request);
		lMSRedemptionReportsService.loadLmsRedemptionReportPage(userVO, lmsRedemptionReportModel, model, request, response);
		boolean result = lMSRedemptionReportsService.displayLMSRedemptionDetailsReportPage(userVO, lmsRedemptionReportModel, model, bindingResult, request);
		request.getSession().setAttribute(MODEL_KEY, lmsRedemptionReportModel);
		
		model.addAttribute(MODEL_KEY, lmsRedemptionReportModel);		
		if(result)
		{
			if ((request.getParameter("iNETReportPanelOne") != null)|| (request.getParameter("iNETReportPanelTwo") != null) || (request.getParameter("iNETReportPanelThree") != null))
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
	 
		@RequestMapping(value = "/pretups/lmsRedemptionBack.form", method = RequestMethod.GET)
		public String loadlmsRedemptionFormBack(final Model model, HttpServletRequest request,
				HttpServletResponse response) throws Exception {
			final String methodName ="LMSRdemptionReportsService#loadlmsRedemptionFormBack";
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
		 * @param model
		 * @param request
		 * @param response
		 * @return
		 * @throws Exception
		 */
			@RequestMapping(value = "/pretups/LmsFromUser.form", method = RequestMethod.GET)
			public @ResponseBody List<Map<String, String>> loadUserListForLMS(Model model, HttpServletRequest request,
					HttpServletResponse response) throws Exception {
				List<Map<String, String>> list = new ArrayList<>();
				String methodName = "loadUserListForLMS";
				String userName = request.getParameter("query");
				userName = userName + "%";
				String zoneCode = request.getParameter("zoneCode");
				String domainCode = request.getParameter("domainCode");
				String fromTransferCategorycode = request.getParameter("categoryCode");
				final String[] arr = fromTransferCategorycode.split("\\|");
				final UserVO userVO = this.getUserFormSession(request);
				List<ListValueVO> fromUserList = null;
				Connection con = null;
				MComConnectionI mcomCon = null;
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
				try{
					mcomCon = new MComConnection();
					con=mcomCon.getConnection();
							fromUserList = channel2ChannelTransferReportsService.loadFromUserList(userVO, zoneCode,
							domainCode, fromTransferCategorycode, userName);
					 final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
					 if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                     if (fromTransferCategorycode.equals(PretupsI.ALL)) {
	                    	 fromUserList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL, zoneCode, userName, userVO.getUserID(), domainCode);
	                     } else {
	                    	 fromUserList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, arr[1],zoneCode, userName, userVO.getUserID(), domainCode);
	                     }
	                 } else {
	                     if (fromTransferCategorycode.equals(PretupsI.ALL)) {
	                    	 fromUserList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL,zoneCode, null, userName, userVO.getUserID(), domainCode);
	                     } else {
	                    	 fromUserList = channelUserDAO.loadUserListOnZoneDomainCategory(con, arr[1],zoneCode, null, userName, userVO.getUserID(),domainCode);
	                     }
	                 }
				}catch(Exception e){
					log.errorTrace("loadUserListForLMS", e);
				}finally {
					
					if (mcomCon != null) {
						mcomCon.close(methodName);
						mcomCon = null;
					}
					if (log.isDebugEnabled()) {
						log.debug(methodName, PretupsI.EXITED);
					}
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
			 * @throws BTSLBaseException
			 * @throws ServletException
			 * @throws IOException
			 * @throws InterruptedException
			 * @throws SQLException
			 */
			 
			 
			@RequestMapping(value = "/pretups/downloadLmsReport.form", method = RequestMethod.GET)
			public void downloadLmsReport(final Model model,
					HttpServletRequest request, HttpServletResponse response)
					throws BTSLBaseException, ServletException, IOException,
					InterruptedException, SQLException {
                String methodName = "downloadLmsReport";
				LmsRedemptionReportModel lmsRedemptionReportModel = (LmsRedemptionReportModel) request
						.getSession().getAttribute(MODEL_KEY);
				InputStream is = null;
				OutputStream os = null;
				try {
					String fileLocation = lMSRedemptionReportsService
							.downloadLmsReport(lmsRedemptionReportModel);
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
