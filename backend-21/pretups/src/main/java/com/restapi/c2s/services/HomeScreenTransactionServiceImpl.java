package com.restapi.c2s.services;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.HttpStatus;
import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewResponse;
import com.btsl.pretups.channel.transfer.businesslogic.C2STotalTransactionCountRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransactionDetails;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.GetChannelUsersMsg;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoResponse;
import com.btsl.pretups.channel.transfer.businesslogic.TotalIncomeDetailsViewVO;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalDataResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalanceRequestVO;
import com.btsl.pretups.channel.transfer.requesthandler.HomeScreenTransactionController;
import com.btsl.pretups.channel.transfer.requesthandler.UserHierarchyDownloadRequestVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.UserHierarchyExcelRW;
import com.btsl.user.businesslogic.GetChannelUsersListResponseVo;
import com.btsl.user.businesslogic.PassbookDetailsVO;
import com.btsl.user.businesslogic.TotalDailyUserIncomeResponseVO;
import com.btsl.user.businesslogic.TotalUserIncomeDetailsVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.user.service.C2STotalTransactionCountResponse;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.PendingTxnListResponseVO;
import com.restapi.user.service.TotalUserIncomeDetailViewResponse;
import com.restapi.user.service.UserBalanceResponseVO;
import com.restapi.user.service.UserHierachyRequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.channel.user.web.ChannelUserTransferForm;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * 
 * @author piyush.bansal
 *
 */
@Service
public class HomeScreenTransactionServiceImpl implements HomeScreenTransactionServiceI {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	/**
	 * @author piyush.bansal
	 */
	public void getUserIncomeDetails(String msisdn, TotalIncomeDetailsViewVO totalIncomeDetailsViewVO,Locale locale,TotalUserIncomeDetailViewResponse response,HttpServletResponse responseSwag) throws BTSLBaseException {
		
		final String methodName = "getUserIncomeDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = new UserDAO();
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		TotalUserIncomeDetailsVO totalUserIncomeDetailsVO = new TotalUserIncomeDetailsVO();
		Date currentDate = new Date();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			channelUserVO =channelUserDAO.loadChannelUserDetails(con,msisdn);
			
			
			String fromDate = totalIncomeDetailsViewVO.getData().getFromDate();
	        String toDate = totalIncomeDetailsViewVO.getData().getToDate();
	        String ExtNgCode = totalIncomeDetailsViewVO.getData().getExtnwcode();
	        
	        totalUserIncomeDetailsVO.setMsisdn(msisdn);
	        totalUserIncomeDetailsVO.setFromdatestring(fromDate);
	        totalUserIncomeDetailsVO.setTodatestring(toDate);
	        totalUserIncomeDetailsVO.setExtnwCode(ExtNgCode);
			totalUserIncomeDetailsVO.setUserID(channelUserVO.getUserID());
	        
	        Date frDate = new Date();
			Date tDate = new Date();
	 		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
	        sdf.setLenient(false);
	        
	        frDate = sdf.parse(fromDate+" 00:00:00");
	        tDate = sdf.parse(toDate+" 23:59:59");
	        Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(frDate);
			Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(tDate);
			
			if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) 
			{
				throw new BTSLBaseException("HomeScreenTransactionController", "totalUserIncomeDetailsView",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) 
			{
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "totalUserIncomeDetailsView",
						PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) 
			{
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "totalUserIncomeDetailsView",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
			}
			
			
			totalUserIncomeDetailsVO.setFromDate(frDate);
			totalUserIncomeDetailsVO.setToDate(tDate);
		    
		    LinkedHashMap<Date, TotalUserIncomeDetailsVO> map = new LinkedHashMap<Date, TotalUserIncomeDetailsVO>();
			userDao.loadUserIncomeC2CandO2C(con, totalUserIncomeDetailsVO,map);
			userDao.loadUserIncomeC2S(con, totalUserIncomeDetailsVO,map);
			totalUserIncomeDetailsVO.setTotalIncome1(totalUserIncomeDetailsVO.getToatalincomec2co2c()+totalUserIncomeDetailsVO.getTotalincomec2s());
			totalUserIncomeDetailsVO.setDetilInfoMap(map);
			
			SimpleDateFormat rdf = new SimpleDateFormat(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
			// Get a set of all the entries (key - value pairs) contained in the LinkesHashMap
			LinkedList<TotalDailyUserIncomeResponseVO> object = new LinkedList<TotalDailyUserIncomeResponseVO>();
			Set<?> entrySet = map.entrySet();
			
			// Obtain an Iterator for the entries Set
			Iterator<?> it = entrySet.iterator();
			while(it.hasNext()) {
				Map.Entry me = (Map.Entry)it.next();
				TotalUserIncomeDetailsVO totalDailyUserIncomeVO1=(TotalUserIncomeDetailsVO) me.getValue();
				TotalDailyUserIncomeResponseVO totalDailyUserIncomeResponseVO = new TotalDailyUserIncomeResponseVO();
				totalDailyUserIncomeResponseVO.setDate(rdf.format(me.getKey()));
				totalDailyUserIncomeResponseVO.setCac(PretupsBL.getDisplayAmount(totalDailyUserIncomeVO1.getCac()));
				totalDailyUserIncomeResponseVO.setCbc(PretupsBL.getDisplayAmount((totalDailyUserIncomeVO1.getCbc())));
				totalDailyUserIncomeResponseVO.setAdditionalCommission(PretupsBL.getDisplayAmount((totalDailyUserIncomeVO1.getAdditionalCommission())));
				totalDailyUserIncomeResponseVO.setBaseCommission(PretupsBL.getDisplayAmount((totalDailyUserIncomeVO1.getBaseCommission())));
				totalDailyUserIncomeResponseVO.setTotalIncome(PretupsBL.getDisplayAmount(totalDailyUserIncomeVO1.getAdditionalCommission()+totalDailyUserIncomeVO1.getBaseCommission()+totalDailyUserIncomeVO1.getCbc()+totalDailyUserIncomeVO1.getCac()));
				object.add(totalDailyUserIncomeResponseVO);
			}
			userDao.loadUserTotalIncomeDetailsBetweenRange(con,totalUserIncomeDetailsVO,previousFrom,previousTo);
			totalUserIncomeDetailsVO.setDetailedInfoList(object);
			
			Long Income=totalUserIncomeDetailsVO.getTotalIncome1();
			Long previousIncome=totalUserIncomeDetailsVO.getPreviousTotalIncome();
			Long BaseCom = totalUserIncomeDetailsVO.getTotalBaseCom();
			Long previousBaseCom = totalUserIncomeDetailsVO.getPreviousTotalBaseComm();
			Long additionalBaseCom = totalUserIncomeDetailsVO.getTotalAdditionalBaseCom();
			Long previousAdditionalBaseCom=totalUserIncomeDetailsVO.getPreviousTotalAdditionalBaseCom();
			Long cac = totalUserIncomeDetailsVO.getTotalCac();
			Long previousCac = totalUserIncomeDetailsVO.getPreviousTotalCac();
			Long cbc = totalUserIncomeDetailsVO.getTotalCbc();
			Long previousCbc = totalUserIncomeDetailsVO.getPreviousTotalCbc();
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			response.setDetailedInfoList(object);
			response.setTotalIncome(BTSLUtil.getDisplayAmount(Income));
			response.setPreviousTotalIncome(BTSLUtil.getDisplayAmount(previousIncome));
			response.setTotalBaseCom(BTSLUtil.getDisplayAmount(BaseCom));
			response.setPreviousTotalBaseComm(BTSLUtil.getDisplayAmount(previousBaseCom));
			response.setTotalAdditionalBaseCom(BTSLUtil.getDisplayAmount(additionalBaseCom));
			response.setPreviousTotalAdditionalBaseCom(BTSLUtil.getDisplayAmount(previousAdditionalBaseCom));
			response.setTotalCac(BTSLUtil.getDisplayAmount(cac));
			response.setPreviousTotalCac(BTSLUtil.getDisplayAmount(previousCac));
			response.setTotalCbc(BTSLUtil.getDisplayAmount(cbc));
			response.setPreviousTotalCbc(BTSLUtil.getDisplayAmount(previousCbc));
			response.setFromDate(rdf.format(frDate));
			response.setToDate(rdf.format(tDate));
			response.setPreviousFromDate(rdf.format(previousFrom));
			response.setPreviousToDate(rdf.format(previousTo));
			response.setTotalIncomePercentage(getPercentage(Income,previousIncome));
			response.setTotalBaseComPercentage(getPercentage(BaseCom, previousBaseCom));
			response.setTotalAdditionalBaseComPercentage(getPercentage(additionalBaseCom, previousAdditionalBaseCom));
			response.setTotalCacPercentage(getPercentage(cac, previousCac));
			response.setTotalCbcPercentage(getPercentage(cbc, previousCbc));
			
	} catch(Exception ex) {
		_log.errorTrace(methodName, ex);
		responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
		throw new BTSLBaseException(
				HomeScreenTransactionController.class.getName(), methodName,
				ex.getMessage());
	} finally {
		try {
			if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController");
				mcomCon = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}
			
	}
	
	/**
	 * 
	 * @param currentData
	 * @param previousData
	 * @return
	 */
	public String getPercentage(Object currentData, Object previousData) {
		
		if(currentData ==null || previousData == null || currentData.toString().equals("0") || previousData.toString().equals("0") || currentData.toString().equals("0.0") || previousData.toString().equals("0.0"))
			return "NA";
		
		Double currentValue = Double.valueOf(currentData.toString());
		Double previousValue = Double.valueOf(previousData.toString());
		double percentage=((currentValue-previousValue)*100)/previousValue;
		
		String roundOffPercentage= String.valueOf(Math.round(percentage));
		return roundOffPercentage;

		
	}

	/**
	 * @author piyush.bansal
	 */
	public void getC2SAllTransaction(String msisdn,C2SAllTransactionDetailViewRequestVO c2SAllTransactionDetailViewRequestVO,Locale locale,
			C2SAllTransactionDetailViewResponse response) throws BTSLBaseException {
		
		final String methodName = "getC2SAllTransaction";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		C2STransferDAO c2STransferDAO = null;
		ArrayList<C2STransactionDetails> c2STransactionList = null;
		ChannelUserDAO channelUserDAO = null;
		try {
			c2STransactionList = new ArrayList<C2STransactionDetails>();
			c2STransferDAO = new C2STransferDAO();
			channelUserDAO = new ChannelUserDAO();
			
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
			
			String fromDate = c2SAllTransactionDetailViewRequestVO.getData().getFromDate();
	        String toDate = c2SAllTransactionDetailViewRequestVO.getData().getToDate();
	    	String serviceType = c2SAllTransactionDetailViewRequestVO.getData().getServiceType();
	    	String userId = channelUserVO.getUserID();
	    	
	    	validateData(serviceType, channelUserVO.getAssociatedServiceTypeList(),fromDate,toDate);
	    	
	    	Date currFrom = BTSLUtil.getDateFromDateString(fromDate);
			Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(currFrom);
			Date currTo = BTSLUtil.getDateFromDateString(toDate);
			Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(currTo);
			
        	HashMap<String, Object> currentData = null;
			HashMap<String, Object> previousData = null;
			
			currentData  = c2STransferDAO.getC2STxnDetailsAllCount(con, userId, currFrom, currTo, serviceType);
			previousData = c2STransferDAO.getC2STxnDetailsAllCount(con, userId, previousFrom, previousTo, serviceType);
			c2STransactionList = c2STransferDAO.getC2STxnDetailsAll(con, userId, currFrom, currTo,serviceType);
			
       	 	SimpleDateFormat rdf = new SimpleDateFormat(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));

			String zero="0";
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			response.setFromDate(rdf.format(currFrom));
			response.setToDate(rdf.format(currTo));
			if(currentData.get("totalCount")==null)
				response.setTotalCount(zero);
			else
				response.setTotalCount(currentData.get("totalCount").toString());
			if(currentData.get("totalValue")==null)
				response.setTotalValue(zero);
			else
				response.setTotalValue(currentData.get("totalValue").toString());
			if(previousData.get("totalCount")==null)
				response.setLastMonthCount(zero);
			else
				response.setLastMonthCount(previousData.get("totalCount").toString());
			if(previousData.get("totalValue")==null)
				response.setLastMonthValue(zero);
			else
				response.setLastMonthValue(previousData.get("totalValue").toString());
			response.setCountPercentage(getPercentage(currentData.get("totalCount"), previousData.get("totalCount")));
			response.setTotalPercentage(getPercentage(currentData.get("totalValue"), previousData.get("totalValue")));
			response.setPreviousFromDate(rdf.format(previousFrom));
			response.setPreviousToDate(rdf.format(previousTo));
			response.setTransferList(c2STransactionList);
			
		} catch(BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(
					HomeScreenTransactionController.class.getName(), methodName,
					be.getMessage(),be.getArgs());
		}catch(Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(
					HomeScreenTransactionController.class.getName(), methodName,
					ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("HomeScreenTransactionController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

		}
		
	}
	
	/**
	 * 
	 * @param serviceType
	 * @param associatedServiceTypeList
	 * @param fromDate
	 * @param toDate
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	public void validateData(String serviceType,ArrayList associatedServiceTypeList,String fromDate,String toDate) throws BTSLBaseException, ParseException{
		 ServiceKeywordCacheVO serviceKeywordCacheVO = ServiceKeywordCache.getServiceTypeObject(serviceType, PretupsI.C2S_MODULE);
		 String arg[]={serviceType};
		 if(BTSLUtil.isNullObject(serviceKeywordCacheVO)){
			 throw new BTSLBaseException(PretupsErrorCodesI.SERVICE_TYPE_INVALID, arg);
		 }
		 final ListValueVO listValueVO = BTSLUtil.getOptionDesc(serviceType,associatedServiceTypeList);
        if (listValueVO == null || BTSLUtil.isNullString(listValueVO.getLabel())) {
       	 throw new BTSLBaseException(PretupsErrorCodesI.SERVICE_TYPE_NOT_ALLOWED, arg);
        } 
        
        String tDate = BTSLUtil.getDateStringFromDate(new Date());
		 BTSLDateUtil.validateDate(fromDate, toDate);
		 if(tDate.equals(fromDate)){
		 	throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_FROMDATE_EQUAL_CURRENTDATE);
		 }
		 else if(tDate.equals(toDate)){
			throw new BTSLBaseException(PretupsErrorCodesI.C2S_ERROR_TODATE_EQUAL_CURRENTDATE);
		 }
		
		}
	
	/**
	 * @author sarthak.saini
	 */
	public void getTotalTransactionCount(Connection con,String msisdn,C2STotalTransactionCountRequestVO c2sTotalTransactionCountRequestVO,Locale locale,C2STotalTransactionCountResponse response) throws BTSLBaseException {
		final String methodName = "getTotalTransactionCount";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		
		ChannelUserDAO channelUserDAO = null;
		Date currentDate = new Date();
		long recentC2sRes = -1;
		try {
			
			channelUserDAO = new ChannelUserDAO();
			
			ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
			String userId = channelUserVO.getUserID();
			recentC2sRes = recentc2ctransactions(con,c2sTotalTransactionCountRequestVO,userId);
		
			
			if(recentC2sRes < 0)
			{
				throw new BTSLBaseException("HomeScreenTransactionController", "getTotalTransactionCount",
						PretupsErrorCodesI.C2S_NO_TRNX_EXIST, 0, null);
			}
			
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setTotlaTrnxCount(recentC2sRes);
			response.setDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(currentDate)));
			response.setMessage("Transaction has been completed!");
		
			
		} catch(BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(
					HomeScreenTransactionController.class.getName(), methodName,
					be.getMessage(),be.getArgs());
		}catch(Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(
					HomeScreenTransactionController.class.getName(), methodName,
					ex.getMessage());
		} finally {}
		
	}

/**
 * 
 * @param c2STotalTransactionCountRequestVO
 * @param userId
 * @return
 * @throws BTSLBaseException
 * @throws Exception
 */
	private long recentc2ctransactions(Connection con,C2STotalTransactionCountRequestVO c2STotalTransactionCountRequestVO, String userId) throws BTSLBaseException, Exception {

		final String methodName = "recentc2ctransactions";
		UserDAO userDao = new UserDAO();
		long recentTxn = -1;
		try {
			
			Date fromDate = BTSLUtil.getDateFromDateString(c2STotalTransactionCountRequestVO.getData().getFromDate(),PretupsI.DATE_FORMAT_DDMMYY);
			Date toDate = BTSLUtil.getDateFromDateString( c2STotalTransactionCountRequestVO.getData().getToDate(),PretupsI.DATE_FORMAT_DDMMYY);
			recentTxn = userDao.totalTranBetweenDate(con,userId, fromDate, toDate); 
						} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
		} finally {
			
		}
		return recentTxn;
	}
/**
 * @author piyush.bansal
 * 	
 */
public void getPassBookView(String msisdn, PassbookViewInfoRequestVO passbookViewInfoRequestVO, Locale locale,PassbookViewInfoResponse response) throws BTSLBaseException {
		
		final String methodName = "getPassBookView";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
			
		}
		
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		PassbookDetailsVO passbookDetailsVO = new PassbookDetailsVO();
		Date currentDate = new Date();
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			channelUserVO =channelUserDAO.loadChannelUserDetails(con,msisdn);
			
			
			String fromDate = passbookViewInfoRequestVO.getData().getFromDate();
	        String toDate = passbookViewInfoRequestVO.getData().getToDate();
	        String ExtNgCode = passbookViewInfoRequestVO.getData().getExtnwcode();
	        
	        Date frDate = new Date();
			Date tDate = new Date();
	 		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
	        sdf.setLenient(false);
	        
	        frDate = sdf.parse(fromDate+" 00:00:00");
	        tDate = sdf.parse(toDate+" 23:59:59");
	       
			if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) 
			{
				throw new BTSLBaseException("HomeScreenTransactionController", "passbookView",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) 
			{
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "passbookView",
						PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) 
			{
				throw new BTSLBaseException("TotalUserIncomeDetailsViewController", "passbookView",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
			}
	        
	        passbookDetailsVO.setFromDate(BTSLDateUtil.getGregorianDate(fromDate));
	        passbookDetailsVO.setToDate(BTSLDateUtil.getGregorianDate(toDate));
	        passbookDetailsVO.setUserID(channelUserVO.getUserID());
	        
	        LinkedHashMap<Date, PassbookDetailsVO> map = userDao.loadStockSalesC2C(con, passbookDetailsVO);
	        map=userDao.loadStockSalesC2S(con, passbookDetailsVO,map);
	        map=userDao.loadStockPurchaseC2C(con, passbookDetailsVO, map);
	        map=userDao.loadCommissionQtyC2C(con, passbookDetailsVO,map);
	        map=userDao.loadCommissionQtyC2S(con, passbookDetailsVO,map);
	        map=userDao.loadWithdrawBalance(con, passbookDetailsVO,map);
	        map=userDao.loadReturnBalance(con, passbookDetailsVO,map);
	        map=userDao.loadClosingBalance(con, passbookDetailsVO,map);
	        
	        SimpleDateFormat rdf = new SimpleDateFormat(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT)));
	        LinkedHashMap<String, PassbookDetailsVO> mapRest =new LinkedHashMap<>();
       	 	Set set = map.entrySet();
            Iterator i1 = set.iterator();
            int i=0;
            while(i1.hasNext()) {
                i++;
            	Map.Entry me = (Map.Entry)i1.next();
            	if(((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getFromDate(),-1))>0&&((Date)me.getKey()).compareTo(DateUtils.addDays(passbookDetailsVO.getToDate(),1))<0) {
            	
            		mapRest.put(rdf.format((Date)me.getKey()), (PassbookDetailsVO)me.getValue());
				}
            	
			}
            
            

			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);
			response.setFromDate(fromDate);
			response.setToDate(toDate);
            response.setPassbook(mapRest);
            
        } catch(BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(
					HomeScreenTransactionController.class.getName(), methodName,
					be.getMessage(),be.getArgs());
		}catch(Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(
					HomeScreenTransactionController.class.getName(), methodName,
					ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("HomeScreenTransactionController");
					mcomCon = null;
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
		}
	}

@Override
public void getChannelUserTxnDetail(String msisdn, String fromDate, String toDate,String transferType,String transferSubType, Locale locale,
		TransactionalDataResponseVO response) throws BTSLBaseException {
	// TODO Auto-generated method stub
	final String methodName = "getChannelUserTxnDetail";
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered ");
	}
	
	Connection con = null;
	MComConnectionI mcomCon = null;
	ChannelUserDAO channelUserDAO = null;
	try {
	
		channelUserDAO = new ChannelUserDAO();
		
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		
		ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
		
		
    	String userId = channelUserVO.getUserID();
    	
    	BTSLDateUtil.validateDate(fromDate, toDate);
    	
    	Date currFrom = BTSLUtil.getDateFromDateString(fromDate);
		Date previousFrom = BTSLUtil.getDateOneMonthBeforeFromDate(currFrom);
		Date currTo = BTSLUtil.getDateFromDateString(toDate);
		Date previousTo = BTSLUtil.getDateOneMonthBeforeToDate(currTo);
		UserDAO userDAO = new UserDAO();
		userDAO.loadUsertransactionalData(con, userId,transferType, transferSubType,currFrom,currTo, previousFrom, previousTo,response);
    	
		
   	 	response.setStatus(PretupsI.RESPONSE_SUCCESS);
		response.setMessageCode(PretupsErrorCodesI.SUCCESS);
		String resmsg = RestAPIStringParser.getMessage(locale,
				PretupsErrorCodesI.SUCCESS, null);
		response.setMessage(resmsg);
		
	
		response.getInTransactionData().setCountPercentage(getPercentage(response.getInTransactionData().getTotalCount(),response.getInTransactionData().getLastMonthCount()));
		response.getInTransactionData().setTotalPercentage(getPercentage(response.getInTransactionData().getTotalValue(), response.getInTransactionData().getLastMonthValue()));
		response.getOutTransactionData().setCountPercentage(getPercentage(response.getOutTransactionData().getTotalCount(),response.getOutTransactionData().getLastMonthCount()));
		response.getOutTransactionData().setTotalPercentage(getPercentage(response.getOutTransactionData().getTotalValue(), response.getOutTransactionData().getLastMonthValue()));
		
		
	} catch(BTSLBaseException be) {
		_log.errorTrace(methodName, be);
		throw new BTSLBaseException(
				HomeScreenTransactionController.class.getName(), methodName,
				be.getMessage(),be.getArgs());
	}catch(Exception ex) {
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(
				HomeScreenTransactionController.class.getName(), methodName,
				ex.getMessage());
	} finally {
		try {
			if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionController");
				mcomCon = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}
}

@Override
public void getPendingtxnList(String msisdn,String transferType,
		PendingTxnListResponseVO pendingTxnListResponseVO, Locale locale) throws BTSLBaseException {
	final String methodName = "getPendingtxnList";
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered ");
	}
	Connection con = null;
	MComConnectionI mcomCon = null;
	ChannelUserDAO channelUserDAO = null;
	ChannelTransferDAO channelTransferDAO = null;
	try {
		channelUserDAO = new ChannelUserDAO();
		channelTransferDAO = new ChannelTransferDAO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		String userId = "";
		ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
		
		
		 UserVO userVO= new UserDAO().loadUsersDetails(con, msisdn);
         if(BTSLUtil.isNullObject(channelUserVO) && userVO!=null){
        	 if(userVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
        		 channelUserVO= channelUserDAO.loadChannelUserDetails(con,userVO.getParentMsisdn());
        		 channelUserVO.setActiveUserID(userVO.getUserID());
        		 userId = channelUserVO.getUserID();
        	 }
         }
     	String roleCode = "";
         if(userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
    		 userId = "OPT";
    		 channelUserVO= (ChannelUserVO)userVO;
    		 roleCode = "APV1O2CTRF";
    	 }else if(userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
    		 userId = channelUserVO.getUserID();
    		 roleCode = "C2CTRFAPR1";
    	 }
		
         if((channelUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE) && transferType.equals(PretupsI.O2C_MODULE)) ||
        		 (channelUserVO.getUserType().equals(PretupsI.STAFF_USER_TYPE) && transferType.equals(PretupsI.O2C_MODULE))||
				(channelUserVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE) && transferType.equals(PretupsI.C2C_MODULE)))
			{
	 	        throw new BTSLBaseException(
					HomeScreenTransactionServiceImpl.class.getName(), methodName,
					PretupsErrorCodesI.NOT_ALLOWED_PEBDING_TXN,"");
	 	    }
	
    	long count = channelTransferDAO.getPendingTxnCount(con, userId, transferType, PretupsI.CHANNEL_TRANSFER_ORDER_NEW,roleCode);
    	if(count ==0 ){
    		if(transferType.equals(PretupsI.O2C_MODULE))
    			 roleCode = "APV2O2CTRF";
    		else
    			 roleCode = "C2CTRFAPR2";
    		 count = channelTransferDAO.getPendingTxnCount(con, userId, transferType, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1,roleCode);
    	}if(count ==0 ){
    		if(transferType.equals(PretupsI.O2C_MODULE))
    			roleCode = "APV3O2CTRF";
    		else
   				roleCode = "C2CTRFAPR3";
   		 	count = channelTransferDAO.getPendingTxnCount(con, userId, transferType, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2,roleCode);
    	}
    	pendingTxnListResponseVO.setTotlaTrnxCount(count);
    	if(count ==0){
    		pendingTxnListResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
	    	pendingTxnListResponseVO.setMessageCode(PretupsErrorCodesI.NO_PENDING_TXN);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.NO_PENDING_TXN, null);
			pendingTxnListResponseVO.setMessage(resmsg);
    	}
    	else
    		{
    		pendingTxnListResponseVO.setStatus(String.valueOf(PretupsI.RESPONSE_SUCCESS));
	    	pendingTxnListResponseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESS, null);
			pendingTxnListResponseVO.setMessage(resmsg);
    		}
		
	} catch(BTSLBaseException be) {
		_log.errorTrace(methodName, be);
		throw new BTSLBaseException(
				HomeScreenTransactionServiceImpl.class.getName(), methodName,
				be.getMessage(),be.getArgs());
	}catch(Exception ex) {
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(
				HomeScreenTransactionServiceImpl.class.getName(), methodName,
				ex.getMessage());
	} finally {
		try {
			if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionServiceImpl");
				mcomCon = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}
	
}

@Override
public ArrayList<String> getUserWidgetList(String msisdn) throws BTSLBaseException {

	final String methodName = "getUserWidgetList";
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered ");
	}
	Connection con = null;
	MComConnectionI mcomCon = null;
	ChannelUserDAO channelUserDAO = null;
	ChannelTransferDAO channelTransferDAO = null;
	ArrayList<String> widgetlist  ;
	try {
		channelUserDAO = new ChannelUserDAO();
		channelTransferDAO = new ChannelTransferDAO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		
		ChannelUserVO channelUserVO= channelUserDAO.loadChannelUserDetails(con,msisdn);
		 UserVO userVO= new UserDAO().loadUsersDetails(con, msisdn);
         String userId = "";
		if(BTSLUtil.isNullObject(channelUserVO) && userVO!=null){
        	 if(userVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)){
        		 channelUserVO= channelUserDAO.loadChannelUserDetails(con,userVO.getParentMsisdn());
        		 channelUserVO.setActiveUserID(userVO.getUserID());
        		 userId = channelUserVO.getUserID();
        	 }
         }
         
         if(userVO.getUserType().equals(PretupsI.OPERATOR_USER_TYPE)){
    		 userId = "OPT";
    		 channelUserVO= (ChannelUserVO)userVO;
    	 }else if(userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)){
    		 userId = channelUserVO.getUserID();
    	 }
         widgetlist = channelUserDAO.loadUserWigets(con, userId);
		

	} catch(BTSLBaseException be) {
		_log.errorTrace(methodName, be);
		throw new BTSLBaseException(
				HomeScreenTransactionServiceImpl.class.getName(), methodName,
				be.getMessage(),be.getArgs());
	}catch(Exception ex) {
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(
				HomeScreenTransactionServiceImpl.class.getName(), methodName,
				ex.getMessage());
	} finally {
		try {
			if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionServiceImpl");
				mcomCon = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}
	return widgetlist;
  }

@Override
public void getUserBalances(ChannelUserVO channelUserVO, UserBalanceRequestVO c2sTotalTransactionCountRequestVO, Locale locale,
		UserBalanceResponseVO response) throws BTSLBaseException {
	

	final String methodName = "getUserBalances";
	if (_log.isDebugEnabled()) {
		_log.debug(methodName, "Entered ");
	}
	Connection con = null;
	MComConnectionI mcomCon = null;
	ChannelTransferDAO channelTransferDAO = null;
	try {
		channelTransferDAO = new ChannelTransferDAO();
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();
		
		channelTransferDAO = new ChannelTransferDAO();
		
		TreeMap<Date,Integer> balanceMap = channelTransferDAO.getUserBalances(con, channelUserVO.getUserID(), c2sTotalTransactionCountRequestVO.getFromDate(), c2sTotalTransactionCountRequestVO.getToDate());
		 if(balanceMap.size() ==0)
		 {
			 response.setOpeningBalance("0");
			 response.setClosingBalance("0");
		 }
		 else
		 {
			 response.setOpeningBalance(balanceMap.get(balanceMap.firstKey()).toString());
			 response.setClosingBalance(balanceMap.get(balanceMap.lastKey()).toString());
		 }
		
		
	} catch(BTSLBaseException be) {
		_log.errorTrace(methodName, be);
		throw new BTSLBaseException(
				HomeScreenTransactionServiceImpl.class.getName(), methodName,
				be.getMessage(),be.getArgs());
	}catch(Exception ex) {
		_log.errorTrace(methodName, ex);
		throw new BTSLBaseException(
				HomeScreenTransactionServiceImpl.class.getName(), methodName,
				ex.getMessage());
	} finally {
		try {
			if (mcomCon != null) {
				mcomCon.close("HomeScreenTransactionServiceImpl#"+methodName);
				mcomCon = null;
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

		try {
			if (con != null) {
				con.close();
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		}

	}
}

	@Override
	public int getUserHierarchyList(Connection con, String loginID, UserHierachyRequestVO requestVO,
			UserHierarchyUIResponseData responseVO, HttpServletResponse responseSwag) throws SQLException, BTSLBaseException 
	{
		UserDAO userDAO = new UserDAO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		UserVO channelUserVO = null;
		UserVO loggedinUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
		String userStatus = "ALL";
		if(requestVO.isAdvancedSearch() && requestVO.getUserStatus()!=null)
			userStatus = requestVO.getUserStatus();
		int maxLevel =1;
		GetChannelUsersListResponseVo response = null;
		
		if(requestVO.isSimpleSearch())
		{
			if(BTSLUtil.isEmpty(requestVO.getLoginID()) == false)
			{
				channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, requestVO.getLoginID());
				
				
				if(channelUserVO == null)
				{
					throw new BTSLBaseException(PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE);
				}
				
				if(channelUserDAO.isUserInHierarchy(con, loggedinUserVO.getUserID(), "loginId", requestVO.getLoginID()) == false)
		        {
					throw new BTSLBaseException(PretupsErrorCodesI.USER_HIERARCHY_ERROR_MESSAGE);
		        }
				
			}
			else if(BTSLUtil.isEmpty(requestVO.getMsisdn()) == false)
			{
				channelUserVO = userDAO.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());
				
				if(channelUserVO == null)
				{
					throw new BTSLBaseException(PretupsErrorCodesI.INVALID_IDENTIFIER_VALUE);
				}
				if(channelUserDAO.isUserInHierarchy(con, loggedinUserVO.getUserID(), "msisdn", requestVO.getMsisdn()) == false)
		        {
					throw new BTSLBaseException(PretupsErrorCodesI.USER_HIERARCHY_ERROR_MESSAGE);
		        }
			}
			else
			{
				throw new BTSLBaseException(PretupsErrorCodesI.BLANK_IDENTIFIER_VALUE);
			}
		}
		else
		{
			channelUserVO = loggedinUserVO;
		}

		responseVO.setLevel(0);
		String userId;
		if (channelUserVO.getUserType().equals(PretupsI.STAFF_USER_TYPE)) {
			userId = channelUserVO.getParentID();
			ChannelUserVO parentUserVO = userDAO.loadUserDetailsFormUserID(con, channelUserVO.getParentID());
			responseVO.setMsisdn(parentUserVO.getMsisdn());
			responseVO.setUsername(parentUserVO.getUserName());
		}
		else {
			userId = channelUserVO.getUserID();
			responseVO.setMsisdn(channelUserVO.getMsisdn());
			responseVO.setUsername(channelUserVO.getUserName());
		}
		response = userDAO.getChannelUsersList
				(con,"ALL","ALL","ALL",userId,userStatus,true);
		
		List<UserHierarchyUIResponseData> responseList = new ArrayList<UserHierarchyUIResponseData>();
		HashMap<UserHierarchyUIResponseData,String> unattachedMap = new HashMap<UserHierarchyUIResponseData,String>(); 
		for(GetChannelUsersMsg getChannelUsersMsg : response.getChannelUsersList())
		{
			UserHierarchyUIResponseData responseObj = new UserHierarchyUIResponseData();
			
			responseObj.setMsisdn(getChannelUsersMsg.getMsisdn());
			responseObj.setBalanceList(getChannelUsersMsg.getBalanceList());
			responseObj.setUsername(getChannelUsersMsg.getUserName());
			responseObj.setParentID(getChannelUsersMsg.getParentID());
			responseObj.setUserID(getChannelUsersMsg.getUserID());
			responseObj.setStatus(getChannelUsersMsg.getStatus());
			responseObj.setCategory(getChannelUsersMsg.getCategory());
			responseObj.setUserType(getChannelUsersMsg.getUserType());
			
			if(responseObj.getUserID().equals(userId))
			{
				responseVO.setBalanceList(responseObj.getBalanceList());
				responseVO.setCategory(getChannelUsersMsg.getCategory());
				responseVO.setStatus(getChannelUsersMsg.getStatus());
			}
			else if(responseObj.getParentID().equals(userId))
			{
				if((requestVO.isAdvancedSearch() == false)
					||	(requestVO.isAdvancedSearch() && requestVO.getParentCategory().equals(channelUserVO.getCategoryCode()) && 
						requestVO.getUserCategory().equals(getChannelUsersMsg.getCategory())))
				{
					maxLevel =1;
					responseObj.setLevel(1);
					if(responseVO.getChildList() == null)
					{
						ArrayList<UserHierarchyUIResponseData> childList = new ArrayList<UserHierarchyUIResponseData>();
						responseVO.setChildList(childList);
					}
					responseVO.getChildList().add(responseObj);
				}
				
			}
			else
			{
				unattachedMap.put(responseObj, getChannelUsersMsg.getParentID());
			}
			
			
			responseList.add(responseObj);			
		}
		
		
		
			for (Entry<UserHierarchyUIResponseData, String> entry : unattachedMap.entrySet()) 
			{
				for(UserHierarchyUIResponseData obj1 : responseList)
				{
					if(entry.getValue().equalsIgnoreCase(obj1.getUserID()))
					{
						entry.getKey().setLevel(obj1.getLevel()+1);
						if(obj1.getChildList() == null)
						{
							ArrayList<UserHierarchyUIResponseData> childList = new ArrayList<UserHierarchyUIResponseData>();
							obj1.setChildList(childList);
						}
						obj1.getChildList().add(entry.getKey());
						
						
						if(entry.getKey().getLevel() >maxLevel)
						{
							maxLevel = entry.getKey().getLevel();
						}
					}
				}
			}
			
			
		
		
		return maxLevel;
		
	}
	
	@Override
	public FileDownloadResponse generateUserHierarchyFile(Connection con, UserVO sessionUserVO, UserHierarchyDownloadRequestVO requestVO,
			HttpServletResponse responseSwag)
			throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException {
		
		final String METHOD_NAME = "generateUserHierarchyFile";
		final ChannelUserTransferForm theForm = new ChannelUserTransferForm();
		String filePath = Constants.getProperty("DownloadUserHierarchyPath");
		
		
		int maxUserHierarchySize = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_HIERARCHY_SIZE))).intValue();
		
		try {
            final File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(METHOD_NAME, "Exception" + e.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "downloadfile.error.dirnotcreated", "selectfromowner");
        }
		
		Boolean isXls = true;
		if(requestVO.getFileType().equals("xls")) {
			isXls = true;
		}else if(requestVO.getFileType().equals("xlsx")) {
			isXls = false;
		}else {
			throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "File type" });
		}
		
		if(sessionUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
			mapToFormForCU(con, theForm, requestVO, sessionUserVO);
		}else {
			mapToForm(con, theForm, requestVO, sessionUserVO);
		}
		
		final UserHierarchyExcelRW excelRW = new UserHierarchyExcelRW();
		String fileName = null;
		try {
			fileName = Constants.getProperty("DownloadUserHierarchyFileNamePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + "."+requestVO.getFileType();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		if(isXls) {
			excelRW.writeMultipleExcel(theForm, locale, filePath + fileName);
		}else {
			excelRW.writeMultipleExcelX(theForm, null, locale, filePath + fileName);
		}
        
        //setting FileDownloadResponse
        File fileNew = new File(filePath + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        FileDownloadResponse fileDownloadResponse = new FileDownloadResponse();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(requestVO.getFileType());
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
    	
		return fileDownloadResponse;
	}

	
	private void mapToFormForCU(Connection con, ChannelUserTransferForm theForm, UserHierarchyDownloadRequestVO requestVO,
			UserVO sessionUserVO)throws BTSLBaseException {
		
		final String METHOD_NAME = "mapToFormForCU";
		
		theForm.setChannelUser(true);
		theForm.setOwnerName(sessionUserVO.getOwnerName());
		theForm.setOwnerID(sessionUserVO.getOwnerID());
		theForm.setDomainCode(sessionUserVO.getDomainID());
		theForm.setDomainCodeDesc(sessionUserVO.getDomainName());
        
		if (PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(sessionUserVO.getCategoryVO().getHierarchyAllowed())) {
			theForm.setParentUserID(sessionUserVO.getParentID());
			theForm.setParentUserName(sessionUserVO.getParentName());
        } else {
        	theForm.setParentUserID(sessionUserVO.getUserID());
            theForm.setParentUserName(sessionUserVO.getUserName());
        }
		ArrayList domList = new ArrayList();
        domList.add(new ListValueVO(sessionUserVO.getDomainName(), sessionUserVO.getDomainID()));
        theForm.setDomainList(domList);
        
        theForm.setZoneList(sessionUserVO.getGeographicalAreaList());
        if (sessionUserVO.getGeographicalAreaList() != null) {
        	theForm.setZoneCode(((UserGeographiesVO) sessionUserVO.getGeographicalAreaList().get(0)).getGraphDomainCode());
        	theForm.setZoneCode(((UserGeographiesVO) sessionUserVO.getGeographicalAreaList().get(0)).getGraphDomainName());
        }
        
        CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
        final ArrayList parentCatList = categoryWebDAO.loadParentCategoryList(con, false);
        final ArrayList childCatList = categoryWebDAO.loadParentCategoryList(con, true);
        theForm.setParentCategoryList(parentCatList);
        theForm.setTransferUserCategoryList(childCatList);
        theForm.setUserTransferModeList(LookupsCache.loadLookupDropDown(PretupsI.CHANNEL_USER_TRANSFER_MODE, true));
        theForm.setStatusList(LookupsCache.loadLookupDropDown(PretupsI.CHANNEL_USER_HIERARCHY_STATUS, true)); // UHSTT
        theForm.setUserTransferMode(PretupsI.ALL);
        theForm.setStatus(PretupsI.ALL);
        
        if (sessionUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
            ListValueVO listValueVO = null;
            for (int i = 0, j = parentCatList.size(); i < j; i++) {
                listValueVO = (ListValueVO) parentCatList.get(i);
                if (listValueVO.getValue().indexOf(sessionUserVO.getDomainID() + ":") == 0) {
                    if (!PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType()) && listValueVO.getValue().indexOf(
                        ":" + sessionUserVO.getCategoryVO().getSequenceNumber() + "|") < 0) {
                        parentCatList.remove(i);
                        i--;
                        j--;
                    } else if (PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType()) && listValueVO.getValue().indexOf(
                        ":" + (sessionUserVO.getCategoryVO().getSequenceNumber() - 1) + "|") < 0) {
                        parentCatList.remove(i);
                        i--;
                        j--;
                    }
                } else {
                    parentCatList.remove(i);
                    i--;
                    j--;
                	}
            	}
        	}
        	
            ListValueVO listValueVO = (ListValueVO) parentCatList.get(0);
            theForm.setParentCategoryCode(listValueVO.getValue());
            theForm.setParentCategoryDesc(listValueVO.getLabel());
        
		if(requestVO.isAdvancedSearch()) {
			theForm.setMsisdn(null);
			theForm.setLoginID(null);
			theForm.setOwnerID(requestVO.getOwnerName());
			
			DomainDAO domainDAO = new DomainDAO();
			DomainVO domainVO =  domainDAO.loadDomainVO(con, requestVO.getDomainCode());
			
			CategoryDAO categoryDAO = new CategoryDAO();
			CategoryVO parentCatVO =  categoryDAO.loadCategoryDetailsByCategoryCode(con, requestVO.getParentCategory());
			CategoryVO userCatVO =  categoryDAO.loadCategoryDetailsByCategoryCode(con, requestVO.getUserCategory());
			
			theForm.setDomainCodeDesc(domainVO.getDomainName());
			theForm.setTransferUserCategoryDesc(userCatVO.getCategoryName());
			theForm.setParentCategoryDesc(parentCatVO.getCategoryName());
			theForm.setZoneCodeDesc(requestVO.getZoneDesc());
			
			theForm.setRequestFor("view");
			String userCatCode = requestVO.getUserCategory();
			String domainCode = requestVO.getDomainCode();
			theForm.setStatus(requestVO.getUserStatus());
			
			//getting seq no
			int seqNO = categoryDAO.loadSequenceNo(con, userCatCode, domainCode);
			
			//loading category list
			ChannelUserTransferWebDAO channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
			final ArrayList categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, domainCode, seqNO);
	        if (categoryList != null && !categoryList.isEmpty()) {
	        	theForm.setCategoryList(categoryList);
	        } else {
	            final BTSLMessages btslMessage = new BTSLMessages("user.suspenduseraction.listmessage", "selecttoowner");
	            throw new BTSLBaseException(this, "viewUserHierarchy", "user.suspenduseraction.listmessage", 0, new String[] { theForm
	                    .getUserName() }, "selectfromowner");
	        }
	        
	        // setting userHierarchyList
	        String status;
	        String statusMode;
	        if (theForm.getStatus().equals(PretupsI.ALL)) {
	            statusMode = PretupsI.STATUS_IN;
	            status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
	        } else {
	            statusMode = PretupsI.STATUS_EQUAL;
	            status = theForm.getStatus();
	        }
	        String[] arr = new String[1];
	        arr[0] = theForm.getParentUserID();
	        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	        theForm.setUserHierarchyList(channelUserDAO.loadUserHierarchyList(con, arr, PretupsI.ALL, statusMode, status, userCatCode));
		}else {
			if(!BTSLUtil.isNullString(requestVO.getLoginID())) {
				theForm.setMsisdn(null);
				theForm.setLoginID(requestVO.getLoginID());
				
				String userID = null;
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(sessionUserVO.getCategoryVO().getHierarchyAllowed())) {
                    userID = sessionUserVO.getParentID();
                } else {
                    userID = sessionUserVO.getUserID();
                }
                
                ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
                final ArrayList hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false);
                if (hierarchyList == null || hierarchyList.isEmpty()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("viewUserDetails", "Logged in user has no child user so there would be no transactions");
                    }
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.nohierarchy", "selectfromowner");
                }
                ChannelUserVO channelUserVO = null;
                boolean isMatched = false;
                if (hierarchyList != null && !hierarchyList.isEmpty()) {
                    isMatched = false;
                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getLoginID().equals(theForm.getLoginID())) {
                            isMatched = true;
                            theForm.setDomainCodeDesc(sessionUserVO.getDomainName());
                            break;
                        }
                    }
                    if (!isMatched) {
                        throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.loginid.notauthorise", 0, new String[] { theForm
                            .getLoginID() }, "selectfromowner");
                    }
                }
            
				String status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_BARRED + "'";
                String statusUsed = PretupsI.STATUS_IN;
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, theForm.getLoginID(), null, statusUsed, status);
                if (channelUserVO == null || !channelUserVO.getLoginID().equalsIgnoreCase(theForm.getLoginID()) || PretupsI.STAFF_USER_TYPE.equals(channelUserVO
                        .getUserType())) {
                        throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.loginid.noinfo", 0, new String[] { theForm.getLoginID() },
                            "selectfromowner");
                    }
                
                //setting domain desc
                
                theForm.setOwnerName(channelUserVO.getOwnerName());
                theForm.setOwnerID(channelUserVO.getOwnerID());
                theForm.setParentUserID(channelUserVO.getUserID());
                
                if (BTSLUtil.isNullString(channelUserVO.getParentName())) {
                	theForm.setParentUserName(channelUserVO.getUserName());
                } else {
                	theForm.setParentUserName(channelUserVO.getParentName());
                }
                if (BTSLUtil.isNullString(channelUserVO.getParentCategoryName())) {
                	theForm.setParentCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());
                } else {
                	theForm.setParentCategoryDesc(channelUserVO.getParentCategoryName());
                }
                
                theForm.setTransferUserCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());

                theForm.setZoneCodeDesc(channelUserVO.getGeographicalDesc());
                theForm.setStatus(PretupsI.ALL);
                
                theForm.setUserTransferMode(PretupsI.ALL);
                theForm.setDomainCode(channelUserVO.getCategoryVO().getDomainCodeforCategory());
                theForm.setTransferUserCategoryCode(channelUserVO.getCategoryVO().getDomainCodeforCategory() + ":" + channelUserVO.getCategoryVO()
                    .getSequenceNumber() + "|" + channelUserVO.getCategoryCode());
                //loading list
                String parentUserID = theForm.getParentUserID();
                if (BTSLUtil.isNullString(parentUserID)) {
                    parentUserID = theForm.getOwnerID();
                }
                final String[] arr = theForm.getTransferUserCategoryCode().split("\\|");
                ChannelUserTransferWebDAO channelUserTransferWebDAO = new ChannelUserTransferWebDAO();
                final ArrayList userList = channelUserTransferWebDAO.loadChannelUserList(con, parentUserID, arr[1], null, theForm.getStatus());
                theForm.setUserList(userList);
                
                //setting user Hierarchy list
                final String transferMode = theForm.getUserTransferMode();
                String statusMode;
                if (theForm.getStatus().equals(PretupsI.ALL)) {
                    statusMode = PretupsI.STATUS_IN;
                    status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                } else {
                    statusMode = PretupsI.STATUS_EQUAL;
                    status = theForm.getStatus();
                }
                final String[] categoryArr = theForm.getTransferUserCategoryCode().split("\\|");
                String[] arr1 = new String[1];
                arr1[0] = theForm.getParentUserID();
                theForm.setUserHierarchyList(channelUserDAO.loadUserHierarchyList(con, arr1, PretupsI.ALL, statusMode, status, categoryArr[1]));
                
                ChannelUserTransferWebDAO channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
                final String transferUserCategory = theForm.getTransferUserCategoryCode();
                final String seqNo = transferUserCategory.substring(transferUserCategory.indexOf(":") + 1, transferUserCategory.indexOf("|"));
                final ArrayList categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, theForm.getDomainCode(), Integer.parseInt(seqNo));
                if (categoryList != null && !categoryList.isEmpty()) {
                	theForm.setCategoryList(categoryList);
                } else {
                    final BTSLMessages btslMessage = new BTSLMessages("user.suspenduseraction.listmessage", "selecttoowner");
                    throw new BTSLBaseException(this, "viewUserHierarchy", "user.suspenduseraction.listmessage", 0, new String[] { theForm
                            .getUserName() }, "selectfromowner");
                }
                
                final ChannelUserVO chVO = (ChannelUserVO) theForm.getUserHierarchyList().get(theForm.getUserHierarchyList().size() - 1);
                if (theForm.getCategoryListSize() < chVO.getMaxUserLevel()) {
                	theForm.getCategoryList().add(new CategoryVO());
                }
			}else if(!BTSLUtil.isNullString(requestVO.getMsisdn())) {
				theForm.setLoginID(null);
				theForm.setMsisdn(requestVO.getMsisdn());
				final String msisdn = theForm.getMsisdn();
                final String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.notsupportnetwork", "selectfromowner");
                }
                final String networkCode = networkPrefixVO.getNetworkCode();
                if (networkCode == null || !networkCode.equals(sessionUserVO.getNetworkID())) {
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.notsupportnetwork", "selectfromowner");
                }
                ChannelUserVO channelUserVO = null;
                
                String userID = null;
                if (PretupsI.CATEGORY_TYPE_AGENT.equals(sessionUserVO.getCategoryVO().getCategoryType()) && PretupsI.NO.equals(sessionUserVO.getCategoryVO().getHierarchyAllowed())) {
                    userID = sessionUserVO.getParentID();
                } else {
                    userID = sessionUserVO.getUserID();
                }

                // load whole hierarchy of the form user and check to user
                // under the hierarchy.
                ChannelUserWebDAO channelUserWebDAO = new ChannelUserWebDAO();
                final ArrayList hierarchyList = channelUserWebDAO.loadChannelUserHierarchy(con, userID, false);
                if (hierarchyList == null || hierarchyList.isEmpty()) {
                    if (_log.isDebugEnabled()) {
                        _log.debug("viewUserDetails", "Logged in user has no child user so there would be no transactions");
                    }
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.nohierarchy", "selectfromowner");
                }
                // validate the from user code down to the user hierarchy of
                // the login user.
                boolean isMatched = false;
                if (hierarchyList != null && !hierarchyList.isEmpty()) {
                    isMatched = false;
                    for (int i = 0, j = hierarchyList.size(); i < j; i++) {
                        channelUserVO = (ChannelUserVO) hierarchyList.get(i);
                        if (channelUserVO.getMsisdn().equals(filteredMsisdn)) {
                            isMatched = true;
                            theForm.setDomainCodeDesc(sessionUserVO.getDomainName());
                            break;
                        }
                    }
                    if (!isMatched) {
                        throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.notauthorise", 0, new String[] { msisdn }, "selectfromowner");
                    }
                }
            
                
                String status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                final String statusUsed = PretupsI.STATUS_IN;
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn, null, statusUsed, status);
                if(channelUserVO!=null&&channelUserVO.getUserCode()==null){
                	 
                	throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.usernotindomainbyucode", 0, new String[] { msisdn }, "selectfromowner");
                }
                if (channelUserVO == null || PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType()) || !channelUserVO.getUserCode().equals(filteredMsisdn)) {
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.noinfo", 0, new String[] { msisdn }, "selectfromowner");
                }
                
                
                theForm.setStatus(PretupsI.ALL);
                theForm.setOwnerName(channelUserVO.getOwnerName());
                theForm.setOwnerID(channelUserVO.getOwnerID());
                theForm.setParentUserID(channelUserVO.getUserID());
                
                if (BTSLUtil.isNullString(channelUserVO.getParentName())) {
                	theForm.setParentUserName(channelUserVO.getUserName());
                
                } else {
                	theForm.setParentUserName(channelUserVO.getParentName());
                }
                if (BTSLUtil.isNullString(channelUserVO.getParentCategoryName())) {
                	theForm.setParentCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());
                } else {
                	theForm.setParentCategoryDesc(channelUserVO.getParentCategoryName());
                }
                
                theForm.setTransferUserCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());

                theForm.setZoneCodeDesc(channelUserVO.getGeographicalDesc());
                
                theForm.setUserTransferMode(PretupsI.ALL);
                theForm.setDomainCode(channelUserVO.getCategoryVO().getDomainCodeforCategory());
                theForm.setTransferUserCategoryCode(channelUserVO.getCategoryVO().getDomainCodeforCategory() + ":" + channelUserVO.getCategoryVO()
                    .getSequenceNumber() + "|" + channelUserVO.getCategoryCode());
                //loading list
                String parentUserID = theForm.getParentUserID();
                if (BTSLUtil.isNullString(parentUserID)) {
                    parentUserID = theForm.getOwnerID();
                }
                final String[] arr = theForm.getTransferUserCategoryCode().split("\\|");
                ChannelUserTransferWebDAO channelUserTransferWebDAO = new ChannelUserTransferWebDAO();
                final ArrayList userList = channelUserTransferWebDAO.loadChannelUserList(con, parentUserID, arr[1], null, theForm.getStatus());
                theForm.setUserList(userList);
                
              //setting user Hierarchy list
                final String transferMode = theForm.getUserTransferMode();
                String statusMode;
                if (theForm.getStatus().equals(PretupsI.ALL)) {
                    statusMode = PretupsI.STATUS_IN;
                    status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                } else {
                    statusMode = PretupsI.STATUS_EQUAL;
                    status = theForm.getStatus();
                }
                final String[] categoryArr = theForm.getTransferUserCategoryCode().split("\\|");
                String[] arr1 = new String[1];
                arr1[0] = theForm.getParentUserID();
                theForm.setUserHierarchyList(channelUserDAO.loadUserHierarchyList(con, arr1, PretupsI.ALL, statusMode, status, categoryArr[1]));
                
                ChannelUserTransferWebDAO channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
                final String transferUserCategory = theForm.getTransferUserCategoryCode();
                final String seqNo = transferUserCategory.substring(transferUserCategory.indexOf(":") + 1, transferUserCategory.indexOf("|"));
                final ArrayList categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, theForm.getDomainCode(), Integer.parseInt(seqNo));
                if (categoryList != null && !categoryList.isEmpty()) {
                	theForm.setCategoryList(categoryList);
                } else {
                    final BTSLMessages btslMessage = new BTSLMessages("user.suspenduseraction.listmessage", "selecttoowner");
                    throw new BTSLBaseException(this, "viewUserHierarchy", "user.suspenduseraction.listmessage", 0, new String[] { theForm
                            .getUserName() }, "selectfromowner");
                }
                
                final ChannelUserVO chVO = (ChannelUserVO) theForm.getUserHierarchyList().get(theForm.getUserHierarchyList().size() - 1);
                if (theForm.getCategoryListSize() < chVO.getMaxUserLevel()) {
                	theForm.getCategoryList().add(new CategoryVO());
                }
			}else {
				//handle error case
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Parent identifier" });
			}
		}
	}
	
	
	private void mapToForm(Connection con, ChannelUserTransferForm theForm, UserHierarchyDownloadRequestVO requestVO,
			UserVO sessionUserVO)throws BTSLBaseException {
		
		final String METHOD_NAME = "mapToForm";
		
		if(requestVO.isAdvancedSearch()) {
			theForm.setMsisdn(null);
			theForm.setLoginID(null);
			theForm.setOwnerID(requestVO.getOwnerName());
			//loading owner data
			UserDAO userDAO = new UserDAO();
			UserVO ownerVO = null;
			UserVO parentVO = null;
			try {
				ownerVO = userDAO.loadUserDetailsFormUserID(con, requestVO.getOwnerName());
				parentVO = userDAO.loadUserDetailsFormUserID(con, requestVO.getParentUserId());
			}catch(Exception e) {
				_log.error(METHOD_NAME, "Exceptin:e=" + e);
			}
			
			DomainDAO domainDAO = new DomainDAO();
			DomainVO domainVO =  domainDAO.loadDomainVO(con, requestVO.getDomainCode());
			
			CategoryDAO categoryDAO = new CategoryDAO();
			CategoryVO parentCatVO =  categoryDAO.loadCategoryDetailsByCategoryCode(con, requestVO.getParentCategory());
			CategoryVO userCatVO =  categoryDAO.loadCategoryDetailsByCategoryCode(con, requestVO.getUserCategory());
			
			theForm.setOwnerName(ownerVO.getUserName());
			theForm.setParentUserName(parentVO.getUserName());
			theForm.setZoneCodeDesc(requestVO.getZoneDesc());
			theForm.setParentUserID(requestVO.getParentUserId());
			theForm.setDomainCodeDesc(domainVO.getDomainName());
			theForm.setParentCategoryDesc(parentCatVO.getCategoryName());
			theForm.setTransferUserCategoryDesc(userCatVO.getCategoryName());
			theForm.setRequestFor("view");
			String userCatCode = requestVO.getUserCategory();
			String domainCode = requestVO.getDomainCode();
			theForm.setStatus(requestVO.getUserStatus());
			
			//getting seq no
			int seqNO = categoryDAO.loadSequenceNo(con, userCatCode, domainCode);
			
			//loading category list
			ChannelUserTransferWebDAO channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
			final ArrayList categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, domainCode, seqNO);
	        if (categoryList != null && !categoryList.isEmpty()) {
	        	theForm.setCategoryList(categoryList);
	        } else {
	            final BTSLMessages btslMessage = new BTSLMessages("user.suspenduseraction.listmessage", "selecttoowner");
	            throw new BTSLBaseException(this, "viewUserHierarchy", "user.suspenduseraction.listmessage", 0, new String[] { theForm
	                    .getUserName() }, "selectfromowner");
	        }
	        
	        // setting userHierarchyList
	        String status;
	        String statusMode;
	        if (theForm.getStatus().equals(PretupsI.ALL)) {
	            statusMode = PretupsI.STATUS_IN;
	            status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
	        } else {
	            statusMode = PretupsI.STATUS_EQUAL;
	            status = theForm.getStatus();
	        }
	        String[] arr = new String[1];
	        arr[0] = theForm.getParentUserID();
	        ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	        theForm.setUserHierarchyList(channelUserDAO.loadUserHierarchyList(con, arr, PretupsI.ALL, statusMode, status, userCatCode));
		}else {
			if(!BTSLUtil.isNullString(requestVO.getLoginID())) {
				theForm.setMsisdn(null);
				theForm.setLoginID(requestVO.getLoginID());
				ChannelUserVO channelUserVO = null;
				String status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "','" + PretupsI.USER_STATUS_BARRED + "'";
                String statusUsed = PretupsI.STATUS_IN;
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, theForm.getLoginID(), null, statusUsed, status);
                if (channelUserVO == null || !channelUserVO.getLoginID().equalsIgnoreCase(theForm.getLoginID()) || PretupsI.STAFF_USER_TYPE.equals(channelUserVO
                        .getUserType())) {
                        throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.loginid.noinfo", 0, new String[] { theForm.getLoginID() },
                            "selectfromowner");
                    }
                
                //setting domain desc
                theForm.setDomainList(BTSLUtil.displayDomainList(sessionUserVO.getDomainList()));
                final ArrayList loggedInUserDomainList = new ArrayList();
                if (theForm.getDomainListSize() == 0) {
                    loggedInUserDomainList.add(new ListValueVO(sessionUserVO.getDomainName(), sessionUserVO.getDomainID()));
                    theForm.setDomainList(loggedInUserDomainList);
                    theForm.setDomainCode(sessionUserVO.getDomainID());
                    theForm.setDomainCodeDesc(sessionUserVO.getDomainName());
                }else {
                	ListValueVO listValueVO = null;
                    boolean domainfound = false;
                    final ArrayList domainList = theForm.getDomainList();
                    for (int i = 0, j = domainList.size(); i < j; i++) {
                        listValueVO = (ListValueVO) domainList.get(i);
                        if (channelUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
                            domainfound = true;
                            theForm.setDomainCodeDesc(listValueVO.getLabel());
                            break;
                        }
                    }
                    if (!domainfound) {
                        throw new BTSLBaseException("channeluser.selectfromowner.msg.loginid.usernotindomainbyucode", new String[] { theForm
                            .getLoginID() }, "selectfromowner");
                    }
                }
                
                final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
                if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), sessionUserVO.getUserID())) {
                	throw new BTSLBaseException("channeluser.selectfromowner.msg.loginid.usernotdowngeogrphybyucode", new String[] { theForm
                        .getLoginID() }, "selectfromowner");
                }
                
                theForm.setOwnerName(channelUserVO.getOwnerName());
                theForm.setOwnerID(channelUserVO.getOwnerID());
                theForm.setParentUserID(channelUserVO.getUserID());
                
                if (BTSLUtil.isNullString(channelUserVO.getParentName())) {
                	theForm.setParentUserName(channelUserVO.getUserName());
                } else {
                	theForm.setParentUserName(channelUserVO.getParentName());
                }
                if (BTSLUtil.isNullString(channelUserVO.getParentCategoryName())) {
                	theForm.setParentCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());
                } else {
                	theForm.setParentCategoryDesc(channelUserVO.getParentCategoryName());
                }
                
                theForm.setTransferUserCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());

                theForm.setZoneCodeDesc(channelUserVO.getGeographicalDesc());
                theForm.setStatus(PretupsI.ALL);
                
                theForm.setUserTransferMode(PretupsI.ALL);
                theForm.setDomainCode(channelUserVO.getCategoryVO().getDomainCodeforCategory());
                theForm.setTransferUserCategoryCode(channelUserVO.getCategoryVO().getDomainCodeforCategory() + ":" + channelUserVO.getCategoryVO()
                    .getSequenceNumber() + "|" + channelUserVO.getCategoryCode());
                //loading list
                String parentUserID = theForm.getParentUserID();
                if (BTSLUtil.isNullString(parentUserID)) {
                    parentUserID = theForm.getOwnerID();
                }
                final String[] arr = theForm.getTransferUserCategoryCode().split("\\|");
                ChannelUserTransferWebDAO channelUserTransferWebDAO = new ChannelUserTransferWebDAO();
                final ArrayList userList = channelUserTransferWebDAO.loadChannelUserList(con, parentUserID, arr[1], null, theForm.getStatus());
                theForm.setUserList(userList);
                
                //setting user Hierarchy list
                final String transferMode = theForm.getUserTransferMode();
                String statusMode;
                if (theForm.getStatus().equals(PretupsI.ALL)) {
                    statusMode = PretupsI.STATUS_IN;
                    status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                } else {
                    statusMode = PretupsI.STATUS_EQUAL;
                    status = theForm.getStatus();
                }
                final String[] categoryArr = theForm.getTransferUserCategoryCode().split("\\|");
                String[] arr1 = new String[1];
                arr1[0] = theForm.getParentUserID();
                theForm.setUserHierarchyList(channelUserDAO.loadUserHierarchyList(con, arr1, PretupsI.ALL, statusMode, status, categoryArr[1]));
                
                ChannelUserTransferWebDAO channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
                final String transferUserCategory = theForm.getTransferUserCategoryCode();
                final String seqNo = transferUserCategory.substring(transferUserCategory.indexOf(":") + 1, transferUserCategory.indexOf("|"));
                final ArrayList categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, theForm.getDomainCode(), Integer.parseInt(seqNo));
                if (categoryList != null && !categoryList.isEmpty()) {
                	theForm.setCategoryList(categoryList);
                } else {
                    final BTSLMessages btslMessage = new BTSLMessages("user.suspenduseraction.listmessage", "selecttoowner");
                    throw new BTSLBaseException(this, "viewUserHierarchy", "user.suspenduseraction.listmessage", 0, new String[] { theForm
                            .getUserName() }, "selectfromowner");
                }
                
                final ChannelUserVO chVO = (ChannelUserVO) theForm.getUserHierarchyList().get(theForm.getUserHierarchyList().size() - 1);
                if (theForm.getCategoryListSize() < chVO.getMaxUserLevel()) {
                	theForm.getCategoryList().add(new CategoryVO());
                }
			}else if(!BTSLUtil.isNullString(requestVO.getMsisdn())) {
				theForm.setLoginID(null);
				theForm.setMsisdn(requestVO.getMsisdn());
				final String msisdn = theForm.getMsisdn();
                final String filteredMsisdn = PretupsBL.getFilteredMSISDN(msisdn);
                final String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
                final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                if (networkPrefixVO == null) {
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.notsupportnetwork", "selectfromowner");
                }
                final String networkCode = networkPrefixVO.getNetworkCode();
                if (networkCode == null || !networkCode.equals(sessionUserVO.getNetworkID())) {
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.notsupportnetwork", "selectfromowner");
                }
                ChannelUserVO channelUserVO = null;
                String status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                final String statusUsed = PretupsI.STATUS_IN;
                ChannelUserDAO channelUserDAO = new ChannelUserDAO();
                channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMsisdn, null, statusUsed, status);
                if(channelUserVO!=null&&channelUserVO.getUserCode()==null){
                	 
                	throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.usernotindomainbyucode", 0, new String[] { msisdn }, "selectfromowner");
                }
                if (channelUserVO == null || PretupsI.STAFF_USER_TYPE.equals(channelUserVO.getUserType()) || !channelUserVO.getUserCode().equals(filteredMsisdn)) {
                    throw new BTSLBaseException(this, "viewUserDetails", "channeluser.selectfromowner.msg.noinfo", 0, new String[] { msisdn }, "selectfromowner");
                }
              //setting domain desc
                theForm.setDomainList(BTSLUtil.displayDomainList(sessionUserVO.getDomainList()));
                final ArrayList loggedInUserDomainList = new ArrayList();
                if (theForm.getDomainListSize() == 0) {
                    loggedInUserDomainList.add(new ListValueVO(sessionUserVO.getDomainName(), sessionUserVO.getDomainID()));
                    theForm.setDomainList(loggedInUserDomainList);
                    theForm.setDomainCode(sessionUserVO.getDomainID());
                    theForm.setDomainCodeDesc(sessionUserVO.getDomainName());
                }else {
                	ListValueVO listValueVO = null;
                    boolean domainfound = false;
                    final ArrayList domainList = theForm.getDomainList();
                    for (int i = 0, j = domainList.size(); i < j; i++) {
                        listValueVO = (ListValueVO) domainList.get(i);
                        if (channelUserVO.getCategoryVO().getDomainCodeforCategory().equals(listValueVO.getValue())) {
                            domainfound = true;
                            theForm.setDomainCodeDesc(listValueVO.getLabel());
                            break;
                        }
                    }
                    if (!domainfound) {
                        throw new BTSLBaseException("channeluser.selectfromowner.msg.loginid.usernotindomainbyucode", new String[] { theForm
                            .getLoginID() }, "selectfromowner");
                    }
                }
                final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
                if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, channelUserVO.getGeographicalCode(), sessionUserVO.getUserID())) {
                	throw new BTSLBaseException("channeluser.selectfromowner.msg.loginid.usernotdowngeogrphybyucode", new String[] { theForm
                        .getLoginID() }, "selectfromowner");
                }
                
                theForm.setStatus(PretupsI.ALL);
                theForm.setOwnerName(channelUserVO.getOwnerName());
                theForm.setOwnerID(channelUserVO.getOwnerID());
                theForm.setParentUserID(channelUserVO.getUserID());
                
                if (BTSLUtil.isNullString(channelUserVO.getParentName())) {
                	theForm.setParentUserName(channelUserVO.getUserName());
                
                } else {
                	theForm.setParentUserName(channelUserVO.getParentName());
                }
                if (BTSLUtil.isNullString(channelUserVO.getParentCategoryName())) {
                	theForm.setParentCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());
                } else {
                	theForm.setParentCategoryDesc(channelUserVO.getParentCategoryName());
                }
                
                theForm.setTransferUserCategoryDesc(channelUserVO.getCategoryVO().getCategoryName());

                theForm.setZoneCodeDesc(channelUserVO.getGeographicalDesc());
                
                theForm.setUserTransferMode(PretupsI.ALL);
                theForm.setDomainCode(channelUserVO.getCategoryVO().getDomainCodeforCategory());
                theForm.setTransferUserCategoryCode(channelUserVO.getCategoryVO().getDomainCodeforCategory() + ":" + channelUserVO.getCategoryVO()
                    .getSequenceNumber() + "|" + channelUserVO.getCategoryCode());
                //loading list
                String parentUserID = theForm.getParentUserID();
                if (BTSLUtil.isNullString(parentUserID)) {
                    parentUserID = theForm.getOwnerID();
                }
                final String[] arr = theForm.getTransferUserCategoryCode().split("\\|");
                ChannelUserTransferWebDAO channelUserTransferWebDAO = new ChannelUserTransferWebDAO();
                final ArrayList userList = channelUserTransferWebDAO.loadChannelUserList(con, parentUserID, arr[1], null, theForm.getStatus());
                theForm.setUserList(userList);
                
              //setting user Hierarchy list
                final String transferMode = theForm.getUserTransferMode();
                String statusMode;
                if (theForm.getStatus().equals(PretupsI.ALL)) {
                    statusMode = PretupsI.STATUS_IN;
                    status = PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";
                } else {
                    statusMode = PretupsI.STATUS_EQUAL;
                    status = theForm.getStatus();
                }
                final String[] categoryArr = theForm.getTransferUserCategoryCode().split("\\|");
                String[] arr1 = new String[1];
                arr1[0] = theForm.getParentUserID();
                theForm.setUserHierarchyList(channelUserDAO.loadUserHierarchyList(con, arr1, PretupsI.ALL, statusMode, status, categoryArr[1]));
                
                ChannelUserTransferWebDAO channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
                final String transferUserCategory = theForm.getTransferUserCategoryCode();
                final String seqNo = transferUserCategory.substring(transferUserCategory.indexOf(":") + 1, transferUserCategory.indexOf("|"));
                final ArrayList categoryList = channelUserTransferwebDAO.loadTransferCategoryList(con, theForm.getDomainCode(), Integer.parseInt(seqNo));
                if (categoryList != null && !categoryList.isEmpty()) {
                	theForm.setCategoryList(categoryList);
                } else {
                    final BTSLMessages btslMessage = new BTSLMessages("user.suspenduseraction.listmessage", "selecttoowner");
                    throw new BTSLBaseException(this, "viewUserHierarchy", "user.suspenduseraction.listmessage", 0, new String[] { theForm
                            .getUserName() }, "selectfromowner");
                }
                
                final ChannelUserVO chVO = (ChannelUserVO) theForm.getUserHierarchyList().get(theForm.getUserHierarchyList().size() - 1);
                if (theForm.getCategoryListSize() < chVO.getMaxUserLevel()) {
                	theForm.getCategoryList().add(new CategoryVO());
                }
			}else {
				throw new BTSLBaseException(PretupsErrorCodesI.PROPERTY_INVALID, new String[] { "Parent identifier" });
			}
		}
		
		
	}
	
	
	private void setMapGenericData(Connection con, ChannelUserTransferForm userHierarchyForm) throws BTSLBaseException {
		
		CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
		final ArrayList parentCatList = categoryWebDAO.loadParentCategoryList(con, false);
        final ArrayList childCatList = categoryWebDAO.loadParentCategoryList(con, true);
        userHierarchyForm.setParentCategoryList(parentCatList);
        userHierarchyForm.setTransferUserCategoryList(childCatList);
        userHierarchyForm.setUserTransferModeList(LookupsCache.loadLookupDropDown(PretupsI.CHANNEL_USER_TRANSFER_MODE, true));
        userHierarchyForm.setStatusList(LookupsCache.loadLookupDropDown(PretupsI.CHANNEL_USER_HIERARCHY_STATUS, true)); // UHSTT
        userHierarchyForm.setUserTransferMode(PretupsI.ALL);
        userHierarchyForm.setStatus(PretupsI.ALL);
	}
	
}
