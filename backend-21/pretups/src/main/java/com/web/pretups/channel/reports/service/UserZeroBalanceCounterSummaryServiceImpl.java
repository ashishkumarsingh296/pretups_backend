package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.xml.sax.SAXException;

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
import com.btsl.pretups.channel.reports.businesslogic.UserZeroBalanceCounterSummaryDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserZeroBalanceCounterSummaryVO;
import com.btsl.pretups.common.DownloadCSVReports;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author mohit.miglani
 *
 */
@Service("userZeroBalanceCounterSummaryService")
public class UserZeroBalanceCounterSummaryServiceImpl implements
		UserZeroBalanceCounterSummaryService {
	
	public static final Log _log = LogFactory
			.getLog(UserZeroBalanceCounterSummaryServiceImpl.class.getName());
	public static final String TIME_FORMAT=PretupsI.TIMESTAMP_DATESPACEHHMMSS;
	public static final String DATE_FORMAT=PretupsI.DATE_FORMAT;
	public static final String LIST_ALL="list.all";
@Override
	public void loadThresholdtype(UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response,
			UserVO userVO)  {

		String methodName = "loadThresholdtype";
		usersReportModel.setRadioNetCode(PretupsI.DAILY_FILTER);
		usersReportModel.setThresholdTypeList(LookupsCache.loadLookupDropDown(
				PretupsI.THRESHOLD_COUNTER_TYPE, true));

		try {
			usersReportModel.set_c2sMisFromDate(BTSLUtil.getDateStringFromDate(userVO.getC2sMisFromDate()));
			usersReportModel.set_c2sMisToDate(BTSLUtil.getDateStringFromDate(userVO.getC2sMisToDate()));
			String c2sfrDate=usersReportModel.get_c2sMisFromDate();
			String[] date1 = c2sfrDate.split("/");
			String c2sfrMonth=date1[1]+"/"+date1[2];
			String c2stoDate=usersReportModel.get_c2sMisToDate();
			String[] date2 = c2stoDate.split("/");
			String c2stoMonth=date2[1]+"/"+date2[2];
			usersReportModel.setc2sMisFromMonth(c2sfrMonth);
			usersReportModel.setc2sMisToMonth(c2stoMonth);
			
		} catch (ParseException e2) {
			_log.error(methodName, e2);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		final ArrayList loggedInUserDomainList = new ArrayList();

		ListValueVO listValueVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			BTSLCommonController btslCommonController = new BTSLCommonController();
			btslCommonController.loadingSummaryDetails(usersReportModel,
					request, con, loggedInUserDomainList, listValueVO);
			usersReportModel.setDomainListSize(usersReportModel.getDomainList()
					.size());
			if (TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel
					.getUserType())) {
				usersReportModel.setDomainCode(null);
				usersReportModel.setDomainName(null);
			}
		} catch (BTSLBaseException e) {
			if (_log.isDebugEnabled()) {
				_log.error(methodName, "Exceptin:e=" + e);
			}
			_log.errorTrace(methodName, e);
		} catch (Exception e1) {
			if (_log.isDebugEnabled()) {
				_log.error(methodName, "Exceptin:e=" + e1);
			}
			_log.errorTrace(methodName, e1);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("UserZeroBalanceCounterSummaryServiceImpl#loadThresholdtype");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, methodName);
			}
		}
		

	}

@Override
	public boolean displayZeroBalSumReport(UsersReportModel usersReportModel ,
			HttpServletRequest request, HttpServletResponse response,
			UserVO userVO, ChannelUserVO userVO1, BindingResult bindingResult) {
		final String methodName = "displayZeroBalSumReport";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}

		CommonValidator commonValidator = new CommonValidator(
				"configfiles/c2sreports/validator-ZeroBalSumm.xml",
				usersReportModel, "ZeroBalanceCounterDetailsThreshold");
		Map<String, String> errorMessages;
		try {
			errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru = new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			request.getSession().setAttribute("usersReportModel",
					usersReportModel);
		} catch (ValidatorException |IOException |SAXException e) {

			_log.errorTrace(methodName, e);
		}

		if (bindingResult.hasFieldErrors()) {

			return false;
		}

		Connection con = null;
		MComConnectionI mcomCon = null;

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Date frDate = null;
			Date tDate = null;
			String fromdate;
			String todate;

			if (!BTSLUtil.isNullString(usersReportModel.getTempfromDate())) {
				frDate = BTSLUtil.getDateFromDateString(
						usersReportModel.getTempfromDate(), DATE_FORMAT);
			}
			if (frDate != null) {
				fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
						.getSQLDateFromUtilDate(frDate));
				usersReportModel.setRptfromDate(BTSLUtil
						.reportDateFormat(fromdate));
				usersReportModel.setFromDate(fromdate);
				String toMonth = BTSLUtil.getDateWithDayOfMonth(BTSLUtil.getNoOfDaysInMonth(fromdate, PretupsI.DATE_FORMAT), usersReportModel.getFromMonth());
				toMonth = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
						.getSQLDateFromUtilDate(BTSLUtil.getDateFromDateString(
								toMonth, DATE_FORMAT)));
				usersReportModel.setRpttoDate(BTSLUtil
						.reportDateFormat(toMonth));
				
				if(("MONTHLY").equals(usersReportModel.getRadioNetCode()))
				usersReportModel.setToDate(toMonth);
				
				if (frDate != null
						&& !(BTSLUtil.checkDateFromMisDates(frDate, frDate,
								userVO1, ProcessI.C2SMIS))) {
					final String[] arr = {
							BTSLUtil.getDateStringFromDate(userVO1
									.getC2sMisFromDate()),
							BTSLUtil.getDateStringFromDate(userVO1
									.getC2sMisToDate()) };
					final BTSLBaseException b = new BTSLBaseException(
							"report.dailydate.range.error.msg", arr,
							"loadUserBalance");
					throw b;
				}

			}

			if (!BTSLUtil.isNullString(usersReportModel.getDailyDate())) {
				tDate = BTSLUtil.getDateFromDateString(usersReportModel
						.getDailyDate());

			}

			if (tDate != null) {
				todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
						.getSQLDateFromUtilDate(tDate));
				usersReportModel.setRptcurrentDate(BTSLUtil
						.reportDateFormat(todate));
			}

			usersReportModel.setNetworkCode(userVO.getNetworkID());
			usersReportModel.setNetworkName(userVO.getNetworkName());
			usersReportModel.setReportHeaderName(userVO.getReportHeaderName());

			ListValueVO listValueVO = null;
			commonDomainInfo(usersReportModel, listValueVO);
			 if (!BTSLUtil.isNullString(usersReportModel.getUserStatus())) {
				listValueVO = BTSLUtil.getOptionDesc(
						usersReportModel.getUserStatus(),
						usersReportModel.getUserStatusList());
				usersReportModel.setUserStatusName(listValueVO.getLabel());
			}
			// /date check that current or previous
			if (!BTSLUtil.isNullString(usersReportModel.getRadioNetCode())) {
				Date currentDate = new Date();
				Date tempdate ;
				// check for monthly
				if (usersReportModel.getRadioNetCode().equalsIgnoreCase(
						PretupsI.MONTHLY_FILTER)
						&& !BTSLUtil.isNullString(usersReportModel
								.getTempfromDate())) {
					tempdate = BTSLUtil.getDateFromDateString(
							usersReportModel.getTempfromDate(), DATE_FORMAT);
					if (tempdate.getYear() + 1900 <= currentDate.getYear() + 1900) {
						usersReportModel
								.setRptfromDate(BTSLUtil.reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
										.getSQLDateFromUtilDate(tempdate))));
						usersReportModel.setFromDate(BTSLUtil
								.getDateStringFromDate(tempdate));
						// current month
						if (currentDate.getMonth() == tempdate.getMonth()
								&& tempdate.getYear() + 1900 == currentDate
										.getYear() + 1900) {
							usersReportModel
									.setDateType(PretupsI.DATE_CHECK_CURRENT);
							usersReportModel
									.setRpttoDate(BTSLUtil.reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
											.getSQLDateFromUtilDate(currentDate))));
							usersReportModel.setToDate(BTSLUtil
									.getDateStringFromDate(tempdate));
						}
						// previous month
						else if ((currentDate.getMonth() > tempdate.getMonth() && tempdate
								.getYear() + 1900 == currentDate.getYear() + 1900)
								|| currentDate.getYear() + 1900 > tempdate
										.getMonth() + 1900) {
							usersReportModel
									.setDateType(PretupsI.DATE_CHECK_PREVIOUS);
							final Calendar tempcal = BTSLDateUtil.getInstance();
							tempcal.setTime(tempdate);
							final int lastDate = tempcal
									.getActualMaximum(Calendar.DATE);
							tempcal.add(Calendar.DATE, lastDate - 1);
							usersReportModel
									.setRpttoDate(BTSLUtil.reportDateFormat(BTSLUtil.sqlDateToDateYYYYString(BTSLUtil
											.getSQLDateFromUtilDate(tempcal
													.getTime()))));

						}
					}
				}
				// check for daily
				else if (usersReportModel.getRadioNetCode().equalsIgnoreCase(
						PretupsI.DAILY_FILTER)
						&& !BTSLUtil.isNullString(usersReportModel
								.getDailyDate())) {
					tempdate = BTSLUtil.getDateFromDateString(usersReportModel
							.getDailyDate());
					currentDate = BTSLUtil.getDateFromDateString(BTSLUtil
							.getDateStringFromDate(currentDate));
					if (currentDate.equals(tempdate)) {
						usersReportModel
								.setDateType(PretupsI.DATE_CHECK_CURRENT);
					} else if (currentDate.after(tempdate)) {
						usersReportModel
								.setDateType(PretupsI.DATE_CHECK_PREVIOUS);
					}
					usersReportModel
							.setRptfromDate(BTSLUtil.reportDateFormat(BTSLUtil
									.sqlDateToDateYYYYString(BTSLUtil
											.getSQLDateFromUtilDate(tempdate))));
					usersReportModel
							.setRpttoDate(BTSLUtil.reportDateFormat(BTSLUtil
									.sqlDateToDateYYYYString(BTSLUtil
											.getSQLDateFromUtilDate(tempdate))));
					usersReportModel.setFromDate(BTSLUtil
							.getDateStringFromDate(tempdate));
					usersReportModel.setToDate(BTSLUtil
							.getDateStringFromDate(tempdate));
				}
			}

		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		SimpleDateFormat timeStampFormat;
		Timestamp fromDateTimeValue = null;
		Timestamp toDateTimeValue = null;
		Date fromDateParse;
		Date toDateParse;
		Date fromDate = null;
		Date toDate = null;
		UserZeroBalanceCounterSummaryDAO userZeroBalanceCounterSummaryDAO = new UserZeroBalanceCounterSummaryDAO();
		try {

			if (!BTSLUtil.isNullString(usersReportModel.getFromDate())) {
				fromDate = BTSLUtil.getDateFromDateString(
						usersReportModel.getFromDate() + " 00:00:00",
						TIME_FORMAT);
				usersReportModel.setFromDateTime(fromDate);
				timeStampFormat = new SimpleDateFormat(TIME_FORMAT);
				fromDateParse = timeStampFormat.parse(BTSLUtil
						.getDateTimeStringFromDate(usersReportModel
								.getFromDateTime()));
				fromDateTimeValue = new Timestamp(fromDateParse.getTime());
			}

			if (!BTSLUtil.isNullString(usersReportModel.getToDate())) {
				toDate = BTSLUtil.getDateFromDateString(
						usersReportModel.getToDate() + " 23:59:00",
						TIME_FORMAT);
				usersReportModel.setToDateTime(toDate);
				timeStampFormat = new SimpleDateFormat(TIME_FORMAT);
				toDateParse = timeStampFormat.parse(BTSLUtil
						.getDateTimeStringFromDate(usersReportModel
								.getToDateTime()));
				toDateTimeValue = new Timestamp(toDateParse.getTime());
			}

			if (userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)) {
				ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryList = userZeroBalanceCounterSummaryDAO
						.loadUserBalanceReport(con, usersReportModel,
								fromDateTimeValue, toDateTimeValue);
				usersReportModel
						.setUserZeroBalanceCounterSummaryList(userZeroBalanceCounterSummaryList);
			} else {
				ArrayList<UserZeroBalanceCounterSummaryVO> userZeroBalanceCounterSummaryListOne = userZeroBalanceCounterSummaryDAO
						.loadzeroBalSummChannelUserReport(con,
								usersReportModel, fromDateTimeValue,
								toDateTimeValue);
				usersReportModel
						.setUserZeroBalanceCounterSummaryListOne(userZeroBalanceCounterSummaryListOne);
			}

		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exited=" + methodName);
		}

		return true;
	}

	protected void commonDomainInfo(UsersReportModel usersReportModel, ListValueVO listValueVO) {
		if (!BTSLUtil.isNullString(usersReportModel.getZoneCode())
				&& usersReportModel.getZoneCode().equals(PretupsI.ALL)) {
			usersReportModel.setZoneName(PretupsRestUtil
					.getMessageString(LIST_ALL));
		} else if (!BTSLUtil.isNullString(usersReportModel.getZoneCode())) {
			listValueVO = BTSLUtil.getOptionDesc(
					usersReportModel.getZoneCode(),
					usersReportModel.getZoneList());
			usersReportModel.setZoneName(listValueVO.getLabel());
		}

		usingDomain(usersReportModel,listValueVO);
		if (!BTSLUtil.isNullString(usersReportModel.getParentCategoryCode())
				&& usersReportModel.getParentCategoryCode()
						.equals(PretupsI.ALL)) {
			usersReportModel.setCategoryName(PretupsRestUtil
					.getMessageString(LIST_ALL));
			usersReportModel.setParentCategoryDesc(PretupsRestUtil
					.getMessageString(LIST_ALL));
		} else if (!BTSLUtil.isNullString(usersReportModel
				.getParentCategoryCode())) {
			listValueVO = BTSLUtil.getOptionDesc(
					usersReportModel.getParentCategoryCode(),
					usersReportModel.getParentCategoryList());
			usersReportModel.setCategoryName(listValueVO.getLabel());
			String[] arr = usersReportModel.getParentCategoryCode()
					.split("\\|");
			usersReportModel.setParentCategoryCode(arr[1]);
			usersReportModel.setParentCategoryDesc(listValueVO.getLabel());

		}
	}
	protected void usingDomain(UsersReportModel usersReportModel, ListValueVO listValueVO) 
	{
		if (!BTSLUtil.isNullString(usersReportModel.getDomainCode())
				&& usersReportModel.getDomainCode().equals(PretupsI.ALL)) {
			usersReportModel.setDomainName(PretupsRestUtil
					.getMessageString(LIST_ALL));
			if (!usersReportModel.getDomainList().isEmpty()) {
				String domainCode = "";
				for (int i = 0, j = usersReportModel.getDomainList().size(); i < j; i++) {
					listValueVO = (ListValueVO) usersReportModel
							.getDomainList().get(i);
					domainCode = domainCode + listValueVO.getValue() + "','";
				}
				domainCode = domainCode.substring(0, domainCode.length() - 3);
				usersReportModel.setDomainListString(domainCode);
			}
		} else if (!BTSLUtil.isNullString(usersReportModel.getDomainCode())) {
			listValueVO = BTSLUtil.getOptionDesc(
					usersReportModel.getDomainCode(),
					usersReportModel.getDomainList());
			usersReportModel.setDomainName(listValueVO.getLabel());
			usersReportModel.setDomainListString(usersReportModel
					.getDomainCode());
		}
	}
	
	@Override
	public String downloadFileforSumm(UsersReportModel usersReportModel)
			throws InterruptedException, BTSLBaseException, SQLException {

		DownloadCSVReports downloadCSVReports = new DownloadCSVReports();
		String methodName="downloadFileforSumm";
		Connection con ;
		MComConnectionI mcomCon ;
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		String filePath ;
		try{
    		String rptCode = usersReportModel.getrptCode();
    		filePath = downloadCSVReports.prepareData(usersReportModel, rptCode,con);
		}
		finally{
            if (mcomCon != null) {
                mcomCon.close(methodName);
                mcomCon = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, PretupsI.EXITED);
            
        }
        }
		return filePath;

	}
	

}
