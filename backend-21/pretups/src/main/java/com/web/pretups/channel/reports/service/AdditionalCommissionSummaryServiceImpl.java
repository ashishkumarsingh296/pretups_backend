package com.web.pretups.channel.reports.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLCommonController;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.AdditionalCommissionSummaryReportDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;

@Service("additionalCommissionSummary")
public class AdditionalCommissionSummaryServiceImpl implements  AdditionalCommissionSummaryService{

	public static final Log log = LogFactory
			.getLog(AdditionalCommissionSummaryServiceImpl.class.getName());

	private BTSLCommonController btslCommonController = new BTSLCommonController();
	private static final String FAIL_KEY = "fail";

	private static final String CLASS_NAME = "AdditionalCommissionSummaryServiceImpl#";
	private static final String EXCEPTION_KEY = "Exceptin:e=";
	private static final String ALL_KEY = "pretups.list.all";
	private static final String DATE_FORMAT = PretupsI.DATE_FORMAT;

	@Override
	public UsersReportModel loadUsersForAdditionalCommissionSummary(UserVO userVO, HttpServletRequest request){

		final String methodName = "loadUsersForAdditionalCommissionSummary";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UsersReportModel userReportsModel = new UsersReportModel();
		userReportsModel.setUserType(userVO.getUserType());
		userReportsModel.setLoginUserID(userVO.getUserID());
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			if(TypesI.SUPER_CHANNEL_ADMIN.equals(userVO.getCategoryCode()))
				userReportsModel.setZoneList(new GeographicalDomainDAO().loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID()));
			else
				userReportsModel.setZoneList(userVO.getGeographicalAreaList());

			userReportsModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));
			final ArrayList loggedInUserDomainList = new ArrayList();
			btslCommonController.commonUserList(userReportsModel, loggedInUserDomainList, userVO);


			final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();

			if ((PretupsI.OPERATOR_USER_TYPE).equals(userVO.getUserType())) {
				userReportsModel.setParentCategoryList(categoryWebDAO.loadCategoryReportList(con));
			} else if ((PretupsI.CHANNEL_USER_TYPE).equals(userVO.getUserType())) {
				final int loginSeqNo = userVO.getCategoryVO().getSequenceNumber();
				userReportsModel.setParentCategoryList(categoryWebDAO.loadCategoryReporSeqtList(con, loginSeqNo));
			}
			ListValueVO listValueVO = null;
			btslCommonController.commonGeographicDetails(userReportsModel, listValueVO);

			userReportsModel.setUserStatusList(LookupsCache.loadLookupDropDown(PretupsI.USER_STATUS_TYPE, true));
			final ArrayList tempUserStatusList = userReportsModel.getUserStatusList();
			final ArrayList userStatusList = new ArrayList();
			ListValueVO listValueVOStatus = null;
			for (int i = 0, k = tempUserStatusList.size(); i < k; i++) {

				listValueVOStatus = (ListValueVO) tempUserStatusList.get(i);

				if (listValueVOStatus.getValue().equals(PretupsI.USER_STATUS_CANCELED) || listValueVOStatus.getValue().equals(PretupsI.USER_STATUS_DELETED)) {
					userStatusList.add(new ListValueVO(listValueVOStatus.getLabel(), listValueVOStatus.getValue()));
				}

			}
			userReportsModel.setUserStatusList(userStatusList);
			if (userReportsModel.getUserStatusListSize() == 1) {
				final ListValueVO statusVO = (ListValueVO) userReportsModel.getUserStatusList().get(0);
				userReportsModel.setUserStatus(statusVO.getValue());
				userReportsModel.setUserStatusName(statusVO.getLabel());
			}

			userReportsModel.setRadioNetCode(PretupsI.DAILY_FILTER);
			final ServicesTypeDAO servicesTypeDAO = new ServicesTypeDAO();
			userReportsModel.setServiceTypeList(servicesTypeDAO.loadServicesListForReconciliation(con, PretupsI.C2S_MODULE));
			if (userReportsModel.getServiceTypeListSize() == 1) {
				listValueVO = (ListValueVO) userReportsModel.getServiceTypeList().get(0);
				userReportsModel.setServiceType(listValueVO.getValue());
				userReportsModel.setServiceTypeName(listValueVO.getLabel());
			}
		}catch (BTSLBaseException | SQLException e) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXCEPTION_KEY + e.getMessage());
			}
			log.errorTrace(methodName, e);
		} finally {
			if(mcomCon != null){
				mcomCon.close(CLASS_NAME+methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, PretupsI.EXITED + methodName);
			}
		}
		return userReportsModel;
	}


	@Override
	public boolean loadAdditionalCommissionSummaryReport(UsersReportModel usersReportModel, BindingResult bindingResult, ChannelUserVO userVO, HttpServletRequest request, Model model){

		final String methodName = "loadAdditionalCommissionSummaryReport";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		Connection con = null;
		String rptCode = null;
		MComConnectionI mcomCon = null;
		String fromdate = null;
		String todate = null;
		AdditionalCommissionSummaryReportDAO additionalCommissionSummaryReportDAO = null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			CommonValidator commonValidator = new CommonValidator(
					"configfiles/c2sreports/validator-AdditionalCommSummaryRpt.xml", usersReportModel,
					"AddittionalCommSummary");
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			additionalCommissionSummaryReportDAO = new AdditionalCommissionSummaryReportDAO();
			usersReportModel.setNetworkCode(userVO.getNetworkID());
			usersReportModel.setNetworkName(userVO.getNetworkName());
			usersReportModel.setReportHeaderName(userVO.getReportHeaderName());

			java.util.Date fromDate = null;
			java.util.Date toDate = null;

			if (!BTSLUtil.isNullString(usersReportModel.getFromDate())) {
				fromDate = BTSLUtil.getDateFromDateString(usersReportModel.getFromDate());
				final String frdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(fromDate));
				usersReportModel.setRptfromDate(BTSLUtil.reportDateFormat(frdate));
			}
			if (!BTSLUtil.isNullString(usersReportModel.getToDate())) {
				toDate = BTSLUtil.getDateFromDateString(usersReportModel.getToDate());
				final String tdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(toDate));
				usersReportModel.setRpttoDate(BTSLUtil.reportDateFormat(tdate));
			}
			 
		        if (usersReportModel.getRadioNetCode().equals(PretupsI.MONTHLY_FILTER)) {
		        	Date currentDate = new Date();
			        
                    Date tfromDate = null;
                    Date ttoDate = null;
                    currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
                    if (!BTSLUtil.isNullString(usersReportModel.getFromMonth())) {
                        try {
                            tfromDate = BTSLUtil.getDateFromDateString(usersReportModel.getTempfromDate(), DATE_FORMAT);

                            if (1900 + tfromDate.getYear() <= 1900 + tfromDate.getYear()) {
                                if (tfromDate.getMonth() > currentDate.getMonth() && tfromDate.getYear() == currentDate.getYear()) {
                                	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.frommonth"));
                                	return false;
                                }
                            } else {
                            	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.frommonth"));
                            	return false;
                            }
                        } catch (Exception e) {
                           
                        	if (log.isDebugEnabled()) {
                				log.debug(CLASS_NAME + methodName, e);
                			}
                        }
                    } else {
                    	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.frommonth.null"));
                    }

                    if (!BTSLUtil.isNullString(usersReportModel.getToMonth())) {
                        try {
                            ttoDate = BTSLUtil.getDateFromDateString(usersReportModel.getTemptoDate(), DATE_FORMAT);

                            if (1900 + ttoDate.getYear() <= 1900 + currentDate.getYear()) {
                                if (ttoDate.getMonth() > currentDate.getMonth() && ttoDate.getYear() == currentDate.getYear()) {
                                	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.tomonth"));
                                	return false;
                                }
                            } else {
                            	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.tomonth"));
                            	return false;
                            }
                        } catch (Exception e) {
                        	if (log.isDebugEnabled()) {
                				log.debug(CLASS_NAME + methodName, e);
                			}
                        }
                    } else {
                    	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.tomonth.null"));
                    	return false;
                    }

                    if (!BTSLUtil.isNullString(usersReportModel.getToMonth()) && !BTSLUtil.isNullString(usersReportModel.getFromMonth())) {
                        final int noOfDays = BTSLUtil.getDifferenceInUtilDates(tfromDate, ttoDate);
                        if (noOfDays > 366) {
                        	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("report.error.month.difference"));
                        	return false;
                        }
                        if (tfromDate.after(ttoDate)) {
                        	model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("c2s.reports.txnsummreport.error.msg.frommonth.more.tomonth"));
                        	return false;
                        }
                    }

                }
		        
			if (!BTSLUtil.isNullString(usersReportModel.getTempfromDate())) {
				fromDate = BTSLUtil.getDateFromDateString(usersReportModel.getTempfromDate(), DATE_FORMAT);
				if (fromDate != null) {
					fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(fromDate));
					usersReportModel.setRptfromDate(BTSLUtil.reportDateFormat(fromdate));

				}
			}
			if (!BTSLUtil.isNullString(usersReportModel.getTemptoDate())) {
				toDate = BTSLUtil.getDateFromDateString(usersReportModel.getTemptoDate(), DATE_FORMAT);
				if (toDate != null) {
					todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(toDate));
					usersReportModel.setRpttoDate(BTSLUtil.reportDateFormat(todate)); 

				}
			}

			if (fromDate != null && toDate != null && !(BTSLUtil.checkDateFromMisDates(fromDate, toDate, userVO, ProcessI.C2SMIS))) {
				final String[] arr = { BTSLUtil.getDateStringFromDate(userVO.getC2sMisFromDate()), BTSLUtil.getDateStringFromDate(userVO.getC2sMisToDate()) };
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.addCommSummary.report.date.range.error.msg", arr));
				return false;
			}


			if (!BTSLUtil.isNullString(usersReportModel.getDailyDate())) {
				final Date tDate = BTSLUtil.getDateFromDateString(usersReportModel.getDailyDate());
				if (tDate != null) {
					todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tDate));
					usersReportModel.setRptcurrentDate(BTSLUtil.reportDateFormat(todate));
				}
			}

			ListValueVO listValueVO = null;
			btslCommonController.commonDomainInfo(usersReportModel, request, listValueVO);


			if (!BTSLUtil.isNullString(usersReportModel.getUserStatus()) && usersReportModel.getUserStatus().equals(PretupsI.ALL)) {
				usersReportModel.setUserStatusName(PretupsRestUtil.getMessageString(ALL_KEY));
			} else if (!BTSLUtil.isNullString(usersReportModel.getUserStatus())) {
				listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getUserStatus(), usersReportModel.getUserStatusList());
				usersReportModel.setUserStatusName(listValueVO.getLabel());
			}


			Date currentDate = new Date();

			if (!BTSLUtil.isNullString(usersReportModel.getRadioNetCode())) {

				Date tempdate = null;
				// check for monthly
				if (usersReportModel.getRadioNetCode().equalsIgnoreCase(PretupsI.MONTHLY_FILTER) && !BTSLUtil.isNullString(usersReportModel.getTempfromDate())) {
					tempdate = BTSLUtil.getDateFromDateString(usersReportModel.getTempfromDate(), DATE_FORMAT);
					if (tempdate.getYear() + 1900 <= currentDate.getYear() + 1900) {
						// current month
						if (currentDate.getMonth() == tempdate.getMonth() && tempdate.getYear() + 1900 == currentDate.getYear() + 1900) {
							usersReportModel.setDateType(PretupsI.DATE_CHECK_CURRENT);
						}
						// previous month
						else if ((currentDate.getMonth() > tempdate.getMonth() && tempdate.getYear() + 1900 == currentDate.getYear() + 1900) || currentDate.getYear() + 1900 > tempdate
								.getMonth() + 1900) {
							usersReportModel.setDateType(PretupsI.DATE_CHECK_PREVIOUS);
						}
					}

					final Date tMonthDate = BTSLUtil.getDateFromDateString(usersReportModel.getTempfromDate());
					usersReportModel.setRptcurrentDate(BTSLUtil.reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tMonthDate))));
				}
				// check for daily
				else if (usersReportModel.getRadioNetCode().equalsIgnoreCase(PretupsI.DAILY_FILTER) && !BTSLUtil.isNullString(usersReportModel.getDailyDate())) {
					tempdate = BTSLUtil.getDateFromDateString(usersReportModel.getDailyDate());
					currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(currentDate));
					if (currentDate.equals(tempdate)) {
						usersReportModel.setDateType(PretupsI.DATE_CHECK_CURRENT);
					} else if (currentDate.after(tempdate)) {
						usersReportModel.setDateType(PretupsI.DATE_CHECK_PREVIOUS);
					}
				}
			}

			if (!BTSLUtil.isNullString(usersReportModel.getFilterType())) {
				if (usersReportModel.getFilterType().equalsIgnoreCase(PretupsI.FILTER_TYPE_AMOUNT)) {
					usersReportModel.setFilterTypeName(PretupsRestUtil.getMessageString("pretups.addCommSummary.reports.c2sminimumaativitythresholds.combo.amount"));
				} else if (usersReportModel.getFilterType().equalsIgnoreCase(PretupsI.FILTER_TYPE_COUNT)) {
					usersReportModel.setFilterTypeName(PretupsRestUtil.getMessageString("pretups.addCommSummary.reports.c2sminimumaativitythresholds.combo.count"));
				}
			}
			if (!BTSLUtil.isNullString(usersReportModel.getServiceType()) && usersReportModel.getServiceType().equals(TypesI.ALL)) {
				usersReportModel.setServiceTypeName(PretupsRestUtil.getMessageString("list.all"));
			} else if (!BTSLUtil.isNullString(usersReportModel.getServiceType())) {
				listValueVO = BTSLUtil.getOptionDesc(usersReportModel.getServiceType(), usersReportModel.getServiceTypeList());
				usersReportModel.setServiceTypeName(listValueVO.getLabel());
			}
			
			if(request.getParameter("submitButton") != null)
			{
				if (usersReportModel.getRadioNetCode().equals(PretupsI.DAILY_FILTER)) {
					usersReportModel.setRptfromDate(usersReportModel.getFromDate());
					usersReportModel.setRpttoDate(usersReportModel.getToDate());
				}
				else {
					usersReportModel.setRptfromDate(usersReportModel.getTempfromDate());
					usersReportModel.setRpttoDate(usersReportModel.getTemptoDate());
				}
			}
			if(request.getParameter("submitButton")!= null){
			if("OPERATOR".equals(usersReportModel.getUserType())){
				if("DAILY".equalsIgnoreCase(usersReportModel.getRadioNetCode()))
	       		{
					rptCode = "ASSCOMSUM01";
					ArrayList<ChannelTransferVO> transferList = additionalCommissionSummaryReportDAO.loadAdditionalCommisionOpeartorDailySummary(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
	       		}
	       		else
	       		{
					rptCode = "ASSCOMSUM02";
					ArrayList<ChannelTransferVO> transferList = additionalCommissionSummaryReportDAO.loadAdditionalCommisionOpeartorMonthlySummary(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
	       		}
			}else{
				if("DAILY".equalsIgnoreCase(usersReportModel.getRadioNetCode()))
	       		{
					rptCode = "ADCOMSMYCH01";
					ArrayList<ChannelTransferVO> transferList = additionalCommissionSummaryReportDAO.loadAdditionalCommisionChannelDailySummary(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
	       		}
	       		else
	       		{
					rptCode = "ADCOMSMYCH02";
					ArrayList<ChannelTransferVO> transferList = additionalCommissionSummaryReportDAO.loadAdditionalCommisionChannelMonthlySummary(con, usersReportModel);
					usersReportModel.setTransferList(transferList);
					usersReportModel.setTransferListSize(transferList.size());
					usersReportModel.setrptCode(rptCode);
	       		}
			}
		}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXCEPTION_KEY + e.getMessage());
			}
			log.errorTrace(methodName, e);
		}finally {
			if(mcomCon != null){
			mcomCon.close(CLASS_NAME+methodName);
			mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting:method="+methodName);
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @param model
	 * @param usersReportModel
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws InterruptedException
	 */
	@Override
	public String downloadAddCommSummaryCSVReportFile(Model model, UsersReportModel usersReportModel) throws SQLException {
		String methodName = "downloadAddCommSummaryCSVReportFile";
		
		if (log.isDebugEnabled()) 
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);

		
		Connection con = null;
		MComConnectionI mcomCon = null;
		String filePath = null;
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
			filePath = downloadCSVReports.prepareData(usersReportModel, usersReportModel.getrptCode(), con);	
		}catch(BTSLBaseException e){
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXCEPTION_KEY + e.getMessage());
			}
			log.errorTrace(CLASS_NAME+methodName, e);
			model.addAttribute(FAIL_KEY,PretupsRestUtil.getMessageString(e.getMessage()));
			return null;
		}
		catch (InterruptedException e) 
		{
			if (log.isDebugEnabled()) {
				log.debug(methodName, EXCEPTION_KEY + e.getMessage());
			}
			log.errorTrace(CLASS_NAME+methodName, e);
		}
		finally{
			if(mcomCon != null){
				mcomCon.close("AdditionalCommissionDetailsServiceImpl#"+methodName);
				mcomCon=null;
			}
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.EXITED);

			}
		}
		return filePath;
	}
}
