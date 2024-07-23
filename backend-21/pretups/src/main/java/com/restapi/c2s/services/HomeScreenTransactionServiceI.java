package com.restapi.c2s.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.json.JsonParseException;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2SAllTransactionDetailViewResponse;
import com.btsl.pretups.channel.transfer.businesslogic.C2STotalTransactionCountRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoRequestVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookViewInfoResponse;
import com.btsl.pretups.channel.transfer.businesslogic.TotalIncomeDetailsViewVO;
import com.btsl.pretups.channel.transfer.businesslogic.TransactionalDataResponseVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalanceRequestVO;
import com.btsl.pretups.channel.transfer.requesthandler.UserHierarchyDownloadRequestVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.restapi.user.service.C2STotalTransactionCountResponse;
import com.restapi.user.service.FileDownloadResponse;
import com.restapi.user.service.PendingTxnListResponseVO;
import com.restapi.user.service.TotalUserIncomeDetailViewResponse;
import com.restapi.user.service.UserBalanceResponseVO;
import com.restapi.user.service.UserHierachyRequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.restapi.user.service.UserHierarchyUIResponseVO;

@Service
public interface HomeScreenTransactionServiceI {

		/**
		 * 
		 * @param msisdn
		 * @param totalIncomeDetailsViewVO
		 * @param locale
		 * @param response
		 * @param responseSwag
		 * @throws BTSLBaseException
		 */
		public void getUserIncomeDetails(String msisdn,TotalIncomeDetailsViewVO totalIncomeDetailsViewVO,Locale locale,TotalUserIncomeDetailViewResponse response,HttpServletResponse responseSwag) throws BTSLBaseException;
		
		/**
		 * 
		 * @param msisdn
		 * @param c2sAllTransactionDetailViewRequestVO
		 * @param locale
		 * @param response
		 * @throws BTSLBaseException
		 */
		public void getC2SAllTransaction(String msisdn,C2SAllTransactionDetailViewRequestVO c2sAllTransactionDetailViewRequestVO,Locale locale,C2SAllTransactionDetailViewResponse response) throws BTSLBaseException;

		/**
		 * 
		 * @param msisdn
		 * @param c2sTotalTransactionCountRequestVO
		 * @param locale
		 * @param response
		 * @throws BTSLBaseException
		 */
		public void getTotalTransactionCount(Connection con,String msisdn,C2STotalTransactionCountRequestVO c2sTotalTransactionCountRequestVO,Locale locale,C2STotalTransactionCountResponse response) throws BTSLBaseException;

		/**
		 * 
		 * @param msisdn
		 * @param passbookViewRequestVO
		 * @param locale
		 * @param response
		 * @throws BTSLBaseException
		 */
		public void getPassBookView(String msisdn, PassbookViewInfoRequestVO passbookViewRequestVO, Locale locale,PassbookViewInfoResponse response) throws BTSLBaseException;

		/**
		 * 
		 * @param msisdn
		 * @param o2cChannelUserStockDetailRequestVO
		 * @param locale
		 * @param response
		 * @throws BTSLBaseException
		 */
		public void getChannelUserTxnDetail(String msisdn, String fromDate, String toDate, String transferType,
				String transferSubType, Locale locale, TransactionalDataResponseVO response) throws BTSLBaseException;

		/**
		 * 
		 * @param msisdn
		 * @param transferType
		 * @param pendingTxnListResponseVO
		 * @param locale
		 * @throws BTSLBaseException
		 */
		public void getPendingtxnList(String msisdn, String transferType,PendingTxnListResponseVO pendingTxnListResponseVO, Locale locale)throws BTSLBaseException;

		/**
		 * 
		 * @param msisdn
		 * @param c2sTotalTransactionCountRequestVO
		 * @param locale
		 * @param response
		 * @throws BTSLBaseException
		 */
		public void getUserBalances(ChannelUserVO channelUserVO ,UserBalanceRequestVO c2sTotalTransactionCountRequestVO,Locale locale,UserBalanceResponseVO response) throws BTSLBaseException;

		/**
		 * 
		 * @param userId
		 * @return
		 * @throws BTSLBaseException
		 */
		public ArrayList<String> getUserWidgetList(String userId)throws BTSLBaseException;
		
		/**
		 * 
		 * @param requestVO
		 * @param responseVO
		 * @param responseSwag
		 * @throws BTSLBaseException 
		 * @throws SQLException 
		 */
		public int getUserHierarchyList(Connection con, String loginID, UserHierachyRequestVO requestVO, UserHierarchyUIResponseData responseVO, HttpServletResponse responseSwag) throws SQLException, BTSLBaseException;
		
		
		/**
		 * 
		 * @param con
		 * @param responseVO
		 * @param responseSwag
		 * @throws BTSLBaseException 
		 * @throws SQLException 
		 */
		public FileDownloadResponse generateUserHierarchyFile(Connection con, UserVO sessionUserVO, UserHierarchyDownloadRequestVO requestVO, HttpServletResponse responseSwag) throws BTSLBaseException, SQLException, JsonParseException, JsonMappingException, IOException;

}
