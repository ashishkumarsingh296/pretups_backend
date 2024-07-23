package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.OfflineReportStatus;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadResp;
//import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadColumns;
//import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CtransferCommRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CtransferCommisionRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserMsisdnUserIDVO;
import com.btsl.user.businesslogic.OfflineReportDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;


/**
 * 
 * @author Subesh KCVf
 * This service contains both search (searchC2CTransferCommission) and download(in execute method)
 *
 */
@Service("C2CTransferCommReportProcessor")
public class C2CTransferCommReportProcessor extends  CommonService implements PretupsBusinessServiceI  {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	private HashMap<String,String > totSummaryColCapture =  new HashMap<String,String>();
	private HashMap<String,String > totSummaryColValue =  new HashMap<String,String>();

	public C2CtransferCommRespDTO searchC2CTransferCommission(C2CTransferCommReqDTO c2CTransferCommReqDTO)
			throws BTSLBaseException {
		final String methodName = "searchC2CTransferCommission";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered method searchC2CTransferCommission with following parameters");
			_log.debug(methodName, c2CTransferCommReqDTO.toString());

		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelTransferDAO channelTransferDAO = null;
		C2CtransferCommRespDTO c2CtransferCommRespDTO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			channelTransferDAO = new ChannelTransferDAO();
			c2CtransferCommRespDTO = channelTransferDAO.searchC2CTransferCommissionData(con, c2CTransferCommReqDTO);

			if (c2CtransferCommRespDTO != null && c2CtransferCommRespDTO.getListC2CTransferCommRecordVO().size() == 0) {
				throw new BTSLBaseException("C2CTransferCommReportProcessor", methodName,
						PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}
		} catch (BTSLBaseException be) {
			_log.errorTrace(methodName, be);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
					be.getArgs());
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
		} finally {
			try {
				if (mcomCon != null) {
					mcomCon.close("C2CTransferCommReportProcessor");
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

		return c2CtransferCommRespDTO;
	}

	/*
	 * public C2CtransferCommSummryData
	 * searchC2CTransferCommissionSummry(C2CTransferCommReqDTO
	 * C2CTransferCommReqDTO) throws BTSLBaseException{ final String methodName =
	 * "searchC2CTransferCommission"; if (_log.isDebugEnabled()) {
	 * _log.debug(methodName,
	 * "Entered method searchC2CTransferCommission with following parameters");
	 * _log.debug(methodName, C2CTransferCommReqDTO.toString());
	 * 
	 * } Connection con = null; MComConnectionI mcomCon = null;
	 * C2CtransferCommSummryData C2CtransferCommSummryData = null;
	 * 
	 * try { mcomCon = new MComConnection(); con = mcomCon.getConnection();
	 * C2CTransferTxnDAO C2CTransferTxnDAO = new C2CTransferTxnDAO();
	 * C2CtransferCommSummryData=
	 * C2CTransferTxnDAO.searchC2CTransferCommSummryData(con,
	 * C2CTransferCommReqDTO);
	 * 
	 * if(BTSLUtil.isNullObject(C2CtransferCommSummryData) ) { throw new
	 * BTSLBaseException("C2CTransferCommReportProcessor", methodName,
	 * PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null); } } catch
	 * (BTSLBaseException be) { _log.errorTrace(methodName, be); throw new
	 * BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
	 * be.getMessage(), be.getArgs()); } catch (Exception ex) {
	 * _log.errorTrace(methodName, ex); throw new
	 * BTSLBaseException(PretupsUIReportsController.class.getName(), methodName,
	 * ex.getMessage()); } finally { try { if (mcomCon != null) {
	 * mcomCon.close("C2CTransferCommReportProcessor"); mcomCon = null; } } catch
	 * (Exception e) { _log.errorTrace(methodName, e); }
	 * 
	 * try { if (con != null) { con.close(); } } catch (Exception e) {
	 * _log.errorTrace(methodName, e); } }
	 * 
	 * return C2CtransferCommSummryData; }
	 */

	public HashMap<String,String> validateInputs(Connection con, C2CTransferCommReqDTO c2CTransferCommReqDTO) throws BTSLBaseException, SQLException {
		final String methodName = "validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
		Date currentDate = new Date();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();

		String fromDate = c2CTransferCommReqDTO.getFromDate();
		String toDate = c2CTransferCommReqDTO.getToDate();
		String extNgCode = c2CTransferCommReqDTO.getExtnwcode();

		Date frDate = new Date();
		Date tDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
		sdf.setLenient(false);

		try {
			frDate = sdf.parse(fromDate + " 00:00:00");
		} catch (ParseException e) {
			throw new BTSLBaseException("PretupsUIReportsController", "lowthresholdsearch",
					PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED, 0, null);
		}

		try {
			tDate = sdf.parse(toDate + " 23:59:59");
		} catch (ParseException e) {
			throw new BTSLBaseException("PretupsUIReportsController", "lowthresholdsearch",
					PretupsErrorCodesI.CCE_XML_ERROR_TO_DATE_REQUIRED, 0, null);
		}

		if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
		}
		if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
		}
		if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
		}

		if (extNgCode != null && !extNgCode.trim().equals(PretupsI.ALL.trim())) {
			NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(extNgCode);
			if (BTSLUtil.isNullObject(networkVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), networkVO.getNetworkName());
		}
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), fromDate);
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), toDate);
		commonValidations( con, c2CTransferCommReqDTO,reportInputKeyValMap);
		
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), PretupsI.ALL);
		if (c2CTransferCommReqDTO.getCategoryCode() != null
				&& !c2CTransferCommReqDTO.getCategoryCode().trim().equals(PretupsI.ALL)) {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					c2CTransferCommReqDTO.getCategoryCode());
			if (BTSLUtil.isNullObject(categoryVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), categoryVO.getCategoryName());
		}
		
		
		
//		if (c2CTransferCommReqDTO.getTransferUserCategory() != null
//				&& !c2CTransferCommReqDTO.getTransferUserCategory().trim().equals(PretupsI.ALL)) {
//			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
//					c2CTransferCommReqDTO.getTransferUserCategory());
//			if (BTSLUtil.isNullObject(categoryVO)) {
//				throw new BTSLBaseException("PretupsUIReportsController", methodName,
//						PretupsErrorCodesI.INVALID_TRANSFER_USER_CATGRY, 0, null);
//			}
//		}
		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),PretupsI.ALL);
		ArrayList lookUpList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_CATEGORY_LOOKUPTYPE, true); //TRFT
		boolean found =false;
		if(lookUpList!=null) { 
			for(int i=0;i<lookUpList.size();i++) {
				 ListValueVO listValueVO =(ListValueVO) lookUpList.get(i);
				 if(listValueVO.getValue().equals(c2CTransferCommReqDTO.getTransferCategory())){
						reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),listValueVO.getLabel());
					 found=true;break; 
				 }
				
			}
			if(!found && (c2CTransferCommReqDTO.getTransferCategory()!=null && !c2CTransferCommReqDTO.getTransferCategory().trim().equals(PretupsI.ALL) )) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_TRF_CATEGORY, 0, null);
			}
			
		}

		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), PretupsI.ALL);
		if (c2CTransferCommReqDTO.getDomain() != null) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, c2CTransferCommReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), domainVO.getDomainName());
		}
		
		
		if (c2CTransferCommReqDTO.getReqTab() != null && c2CTransferCommReqDTO.getReqTab().trim().toUpperCase().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ) ) {  
			mobileNoTabReqValidations(con,c2CTransferCommReqDTO, reportInputKeyValMap);
		}else {
			advancedTabReqValidations(con,c2CTransferCommReqDTO,reportInputKeyValMap);
		}
		
		return reportInputKeyValMap;
	}
	
	private void commonValidations(Connection con, C2CTransferCommReqDTO c2CTransferCommReqDTO,HashMap<String,String> reportInputKeyValMap) throws BTSLBaseException  {
		final String methodName ="commonValidations";
		
		if(c2CTransferCommReqDTO.getIncludeStaffUserDetails()!= null && !(c2CTransferCommReqDTO.getIncludeStaffUserDetails().trim().toUpperCase().equals(PretupsI.TRUE) ||
				c2CTransferCommReqDTO.getIncludeStaffUserDetails().trim().toUpperCase().equals(PretupsI.FALSE)
				) ) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_INCLUDE_STAFF, 0, null);
		}
		
		CommonUtil commonUtil = new CommonUtil();
		ListValueVO listValueVO=null;
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(), PretupsI.ALL);
		if(c2CTransferCommReqDTO.getDistributionType()!=null && !c2CTransferCommReqDTO.getDistributionType().equals(PretupsI.ALL)) {
			listValueVO=	commonUtil.validationDistributionType(c2CTransferCommReqDTO.getDistributionType());
			if(listValueVO==null) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_DISTRIBUTION_TYPE, 0, null);
	    	 }
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(), listValueVO.getLabel());
		}
		
		
		 List keyValueList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
		   HashMap<String, String> mp =(HashMap<String, String>) keyValueList.stream()
		      .collect(Collectors.toMap(ListValueVO::getValue,ListValueVO::getLabel));
		    
		    if(keyValueList != null &&   (c2CTransferCommReqDTO.getTransferSubType()!=null && !c2CTransferCommReqDTO.getTransferSubType().trim().toUpperCase().equals(PretupsI.ALL)  && c2CTransferCommReqDTO.getTransferSubType().indexOf(",")==0 && !mp.containsKey(c2CTransferCommReqDTO.getTransferSubType()))) {
		    	 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.INVALID_TRF_TYPE, 0, null);
		    }

		    if(c2CTransferCommReqDTO.getTransferSubType().equals(PretupsI.ALL) || c2CTransferCommReqDTO.getTransferSubType().indexOf(",")>0 ) { //As UI is sending this way
		    	reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),PretupsI.ALL);	
		    }else {
		    	reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),mp.get(c2CTransferCommReqDTO.getTransferSubType()));
		    }

			
		
		
				
		if(c2CTransferCommReqDTO.getTransferInout()!=null && !c2CTransferCommReqDTO.getTransferInout().trim().equals(PretupsI.ALL)
				&&  !c2CTransferCommReqDTO.getTransferInout().trim().toUpperCase().equals(PretupsI.IN) &&
				  !c2CTransferCommReqDTO.getTransferInout().trim().toUpperCase().equals(PretupsI.OUT) 
				 ) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVALID_TRANSFER_IN_OUT, 0, null);
			
		}
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINOUT.getReportValues(),c2CTransferCommReqDTO.getTransferInout());
		
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues(),PretupsI.ALL);
		if(c2CTransferCommReqDTO.getGeography()!=null && !c2CTransferCommReqDTO.getGeography().trim().equals(PretupsI.ALL)) {
		    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
		    if (!geoDAO.isGeographicalDomainExist(con, c2CTransferCommReqDTO.getGeography(), true)) {
		 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
		    }
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues(),c2CTransferCommReqDTO.getGeography());
	  }
		
	}
	
	private void advancedTabReqValidations(Connection con, C2CTransferCommReqDTO c2CTransferCommReqDTO,HashMap<String,String> reportInputKeyValMap) throws BTSLBaseException, SQLException {
		final String methodName ="advancedTabReqValidations";
		UserDAO userDAO = new UserDAO();
		if(BTSLUtil.isEmpty(c2CTransferCommReqDTO.getUser()) ) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVALID_SENDER_USER, 0, null);

     	}
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO=null;
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER.getReportValues(),PretupsI.ALL);
		if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getUser())  && !c2CTransferCommReqDTO.getUser().equals(PretupsI.ALL) ) {
			
			// c2CTransferCommReqDTO.getUser() Will contain UsrID , for Validation, For swagger purpose 
			 listUserMsisdnUserIDVO =channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(null, null, con, c2CTransferCommReqDTO.getCategoryCode(), c2CTransferCommReqDTO.getDomain(), c2CTransferCommReqDTO.getUserId(), c2CTransferCommReqDTO.getGeography(),c2CTransferCommReqDTO.getUser());
			if(listUserMsisdnUserIDVO ==null && (listUserMsisdnUserIDVO!=null &&  listUserMsisdnUserIDVO.isEmpty() )) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_SENDER_USER, 0, null);
			}
			ChannelUserVO channelUserVO =	userDAO.loadUserDetailsFormUserID(con, c2CTransferCommReqDTO.getUser());
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER.getReportValues(),channelUserVO.getUserName());
		}
		reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER.getReportValues(),PretupsI.ALL);
		if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getTransferUser()) && !c2CTransferCommReqDTO.getTransferUser().equals(PretupsI.ALL) ) {
			// c2CTransferCommReqDTO.getUser() Will contain UsrID , for Validation, For swagger purpose 
			 listUserMsisdnUserIDVO =channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(c2CTransferCommReqDTO.getUserId(), null, con, c2CTransferCommReqDTO.getCategoryCode(), c2CTransferCommReqDTO.getDomain(), c2CTransferCommReqDTO.getUserId(), c2CTransferCommReqDTO.getGeography(),c2CTransferCommReqDTO.getTransferUser());
			if(listUserMsisdnUserIDVO ==null && (listUserMsisdnUserIDVO!=null &&  listUserMsisdnUserIDVO.isEmpty() )) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_TRANSFER_USER, 0, null);
			}
			ChannelUserVO channelUserVO =	userDAO.loadUserDetailsFormUserID(con, c2CTransferCommReqDTO.getTransferUser());
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER.getReportValues(),channelUserVO.getUserName());
		}
		
		
	}
	
	
	private void mobileNoTabReqValidations(Connection con, C2CTransferCommReqDTO c2CTransferCommReqDTO,HashMap<String,String> reportInputKeyValMap) throws BTSLBaseException {
		final String methodName ="mobileNoTabReqValidations";
		UserDAO userDAO = new UserDAO();
		
		if(BTSLUtil.isNullString(c2CTransferCommReqDTO.getSenderMobileNumber())) { // Sender mobile number is mandatory
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVALID_SENDER_MOBILENUMBER, 0, null);
     	 }
		
		 if(!userDAO.validateUserLoginIdorMsisdn(null,c2CTransferCommReqDTO.getSenderMobileNumber(),c2CTransferCommReqDTO.getExtnwcode(),con)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_SENDER_MOBILENUMBER, 0, null);
		 }
		 reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues(),c2CTransferCommReqDTO.getSenderMobileNumber());
		
		 if(!userDAO.validateUserLoginIdorMsisdn(null,c2CTransferCommReqDTO.getReceiverMobileNumber(),c2CTransferCommReqDTO.getExtnwcode(),con)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_RECEIVER_MOBILENUMBER, 0, null);
		 }
		 reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues(),c2CTransferCommReqDTO.getReceiverMobileNumber());
		
	}
	
	
	

	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,
			C2CTransferCommReqDTO c2CTransferCommReqDTO, ChannelUserVO channelUserVO,HashMap<String,String> reportInputKeyValMap) {
		UserDAO  userDAO = new UserDAO();
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		String methodName ="C2CTransfer commission :: getSearchInputValueMap";

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getFromDate(), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null),
						PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getToDate(), PretupsRptUIConsts.FOUR.getNumValue()));

		
		
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINOUT.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getTransferInout(), PretupsRptUIConsts.ONE.getNumValue()));
		
	
		CategoryDAO categoryDAO = new CategoryDAO();
		try {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					c2CTransferCommReqDTO.getCategoryCode());

			String categoryName = "ALL";
			if (categoryVO != null) {
				categoryName = categoryVO.getCategoryName();
			}
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
					new SearchInputDisplayinRpt(categoryName, PretupsRptUIConsts.ONE.getNumValue()));
			

		} catch (BTSLBaseException e) {
			_log.error("LowthreholdReport :: Exception occured while fetching category details", e);
		}

		NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(c2CTransferCommReqDTO.getExtnwcode());
		String networkName = "ALL";
		if (networkVO != null) {
			networkName = networkVO.getNetworkName();
		}

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(networkName, PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_INCLUDESTAFF.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()), new SearchInputDisplayinRpt(
				(c2CTransferCommReqDTO.getIncludeStaffUserDetails().toUpperCase().equals(PretupsI.TRUE) ? "Yes" : "No" ), PretupsRptUIConsts.ONE.getNumValue()));
		
		if(c2CTransferCommReqDTO.getReqTab().toUpperCase().equals(PretupsI.C2C_ADVANCED_TAB_REQ) ) {
		
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()),
						new SearchInputDisplayinRpt(
								RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
										PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null),
								PretupsRptUIConsts.ZERO.getNumValue()));
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()),
						new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getDomain(), PretupsRptUIConsts.ONE.getNumValue()));
				
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TEN.getNumValue()), new SearchInputDisplayinRpt(
						PretupsRptUIConsts.GEOGRAPHY.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TEN.getNumValue()),
						new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getGeography(), PretupsRptUIConsts.ONE.getNumValue()));
				
				
				String senderUser =c2CTransferCommReqDTO.getUser();
						if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getUser())  ) {
							
							// c2CTransferCommReqDTO.getUser() Will contain UsrID , for Validation, For swagger purpose 
							List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO;
							try {
								listUserMsisdnUserIDVO = channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(null, null, con, c2CTransferCommReqDTO.getCategoryCode(), c2CTransferCommReqDTO.getDomain(), c2CTransferCommReqDTO.getUserId(), c2CTransferCommReqDTO.getGeography(),c2CTransferCommReqDTO.getUser());
								
								if(listUserMsisdnUserIDVO ==null && (listUserMsisdnUserIDVO!=null &&  listUserMsisdnUserIDVO.isEmpty() )) {
									throw new BTSLBaseException("PretupsUIReportsController", methodName,
											PretupsErrorCodesI.INVALID_SENDER_USER, 0, null);
								}else {
									for(UserMsisdnUserIDVO userVO : listUserMsisdnUserIDVO) {
										senderUser=userVO.getUserName();
										break;
									}
									
								}
							} catch (SQLException | BTSLBaseException e) {
								// TODO Auto-generated catch block
								_log.error(methodName, "Exception occured while fetching Sender User name info.......");							}
						
						}
							
						
				
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.N11.getNumValue()), new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER.getReportValues(), null)			
						, PretupsRptUIConsts.ZERO.getNumValue()));
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.N11.getNumValue()),
						new SearchInputDisplayinRpt(senderUser, PretupsRptUIConsts.ONE.getNumValue()));
				
				
				
				String receiverUser =c2CTransferCommReqDTO.getTransferUser();
				if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getTransferUser())  ) {
					
					// c2CTransferCommReqDTO.getUser() Will contain UsrID , for Validation, For swagger purpose 
					List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO;
					try {
						listUserMsisdnUserIDVO = channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(null, null, con, c2CTransferCommReqDTO.getCategoryCode(), c2CTransferCommReqDTO.getDomain(), c2CTransferCommReqDTO.getUserId(), c2CTransferCommReqDTO.getGeography(),c2CTransferCommReqDTO.getTransferUser());
						
						if(listUserMsisdnUserIDVO ==null && (listUserMsisdnUserIDVO!=null &&  listUserMsisdnUserIDVO.isEmpty() )) {
							throw new BTSLBaseException("PretupsUIReportsController", methodName,
									PretupsErrorCodesI.INVALID_SENDER_USER, 0, null);
						}else {
							for(UserMsisdnUserIDVO userVO : listUserMsisdnUserIDVO) {
								receiverUser=userVO.getUserName();
								break;
							}
							
						}
					} catch (SQLException | BTSLBaseException e) {
						// TODO Auto-generated catch block
						_log.error(methodName, "Exception occured while fetching Receiver User name info.......");							}
				
				}
				
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.N12.getNumValue()), new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERUSER.getReportValues(), null)	
						, PretupsRptUIConsts.ZERO.getNumValue()));
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.N12.getNumValue()),
						new SearchInputDisplayinRpt(receiverUser, PretupsRptUIConsts.ONE.getNumValue()));
				
				
				String transferCategoryName = "ALL";
				try {
					CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
							c2CTransferCommReqDTO.getTransferUserCategory());
					
					if (categoryVO != null) {
						transferCategoryName = categoryVO.getCategoryName();
					}
				} catch (BTSLBaseException e) {
					// TODO Auto-generated catch block
					_log.error(methodName, "Exception occured while fetching Transfer category name info.......");							}
	
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.N13.getNumValue()), new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(), null)	
						, PretupsRptUIConsts.ZERO.getNumValue()));
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.N13.getNumValue()),
						new SearchInputDisplayinRpt(transferCategoryName, PretupsRptUIConsts.ONE.getNumValue()));
		
		} else {
			/*
			
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
					new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getSenderMobileNumber(), PretupsRptUIConsts.ONE.getNumValue()));
			
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
					new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getReceiverMobileNumber(), PretupsRptUIConsts.ONE.getNumValue()));*/

			
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			
			String senderName =null;
			UserVO userVO=null;
			try {
				 userVO = userDAO.loadUserDetailsByMsisdn(con, c2CTransferCommReqDTO.getSenderMobileNumber());
				senderName=userVO.getUserName();
			} catch (BTSLBaseException e) {
				
			}
		
			if(!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getSenderMobileNumber())) {
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
					new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getSenderMobileNumber()+ "(" + senderName +")" , PretupsRptUIConsts.ONE.getNumValue()));
			
			}else {
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
						new SearchInputDisplayinRpt(PretupsI.ALL , PretupsRptUIConsts.ONE.getNumValue()));
				
			}
			
			// Receiver
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			
			String receiverName =null;
			
		if(	!BTSLUtil.isEmpty(c2CTransferCommReqDTO.getReceiverMobileNumber())){
			try {
				 userVO = userDAO.loadUserDetailsByMsisdn(con, c2CTransferCommReqDTO.getReceiverMobileNumber());
				 receiverName=userVO.getUserName();
			} catch (BTSLBaseException e) {
				
			}
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()),
					new SearchInputDisplayinRpt(c2CTransferCommReqDTO.getReceiverMobileNumber()+ "(" + receiverName +")" , PretupsRptUIConsts.ONE.getNumValue()));
			
		} else {
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()),
					new SearchInputDisplayinRpt(PretupsI.ALL , PretupsRptUIConsts.ONE.getNumValue()));
			
		}
		
		}

		return mapMultipleColumnRow;
	}
	
	
	
	

	
	
	
	private String createFileFormatC2CTransfComm(DownloadDataFomatReq downloadDataFomatReq) {
		String fileData = null;
		if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			fileData = createXlsFileFormat(downloadDataFomatReq);
		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			Map<String, String> map = new LinkedHashMap<>();
			fileData = createCSVFileFormat(downloadDataFomatReq);
		}
		return fileData;
	} 


	private String createCSVFileFormat(DownloadDataFomatReq downloadDataFomatReq) {

		final String methodName = "createCSVFileFormatPassbook";
		String fileData = null;

		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] reportheadervalue = new String[1];
			reportheadervalue[0] = "                  "
					+ PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues();
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

			Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

			String[] inputRow2 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues())
					+ "     "
					+ RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
							PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null)
					+ " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues()) };
			csvWriter.writeNext(inputRow2);

			String[] inputRow3 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), null) + " : "
					+ inputParamMap
							.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues()) };
			csvWriter.writeNext(inputRow3);

			String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()) };
			csvWriter.writeNext(inputRow4);

			String[] inputRow5 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()) };
			csvWriter.writeNext(inputRow5);
		
			String sendernumber=null;
			 if(inputParamMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues())!=null) {
				 sendernumber=inputParamMap.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues());
			 }else {
				 sendernumber="";
			 }
			String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues(), null) + " : "
					+ sendernumber };
			csvWriter.writeNext(inputRow6);
           
			String receiverNumber =null;
			if(inputParamMap
			.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues())!=null) {
				receiverNumber=inputParamMap
						.get(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues());
			}else {
				receiverNumber="";
			}

			String[] inputRow7 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues(), null) + " : "
					+ receiverNumber };
			csvWriter.writeNext(inputRow7);

			csvWriter.writeNext(blankLine);

			// Displaying Report data rowWise
			Map<String, String> displayColumnMap = downloadDataFomatReq.getDisplayListColumns().stream()
					.collect(Collectors.toMap(DispHeaderColumn::getColumnName, DispHeaderColumn::getDisplayName));
			String[] columSeqArr = downloadDataFomatReq.getColumnSequenceNames().split(",");

			List<? extends Object> dataList = downloadDataFomatReq.getReportDataList();
			// Display report column headers

			List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
			String[] columnHeaderNames = new String[listDiplayColumns.size()];
			for (int k = 0; k < listDiplayColumns.size(); k++) {
				columnHeaderNames[k] = displayColumnMap.get(listDiplayColumns.get(k).getColumnName());
			}
			csvWriter.writeNext(columnHeaderNames);

			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {
				C2CtransferCommisionRecordVO record = (C2CtransferCommisionRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				String[] dataRow = new String[listDiplayColumns.size()];
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
				}
				csvWriter.writeNext(dataRow);
			}

			String output = writer.toString();
			fileData = new String(Base64.getEncoder().encode(output.getBytes()));
		} catch (IOException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exception occured while generating CSV file for C2c download ");

			}
		}

		return fileData;
	}  
 
	
	private String createXlsFileFormat(DownloadDataFomatReq downloadDataFomatReq) {
		final String methodName = "createXlsFileFormat";
		String fileData = null;
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet(downloadDataFomatReq.getFileName());
			try {
				sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
				sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
				sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());
			}catch (Exception e) {
				_log.error("", "Error occurred while autosizing columns");
				e.printStackTrace();
			}

			

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			// headerFont.setFontHeightInPoints( (Short) 14);
			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			// Displaying Report search parameters]
			int lastRowValue = 0;
			Row reportheader = sheet.createRow(lastRowValue);
			Cell reportHeadingCell = reportheader.createCell(PretupsRptUIConsts.ZERO.getNumValue());
			reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
			reportHeadingCell.setCellStyle(headerCellStyle);
			sheet.addMergedRegion(
					new CellRangeAddress(PretupsRptUIConsts.ZERO.getNumValue(), PretupsRptUIConsts.ZERO.getNumValue(),
							PretupsRptUIConsts.ZERO.getNumValue(), PretupsRptUIConsts.FOUR.getNumValue()));

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Current Row value " + lastRowValue);
			}
			lastRowValue = lastRowValue + 1;

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Current Row value " + lastRowValue);
			}
			
			  List<String> keylist = new ArrayList<>(downloadDataFomatReq.getSearchInputMaprowCell().keySet());
			List<String> rowKeyList =   keylist.stream()
			            .map(Integer::valueOf)
			            .sorted()
			            .map(String::valueOf)
			            .collect(Collectors.toList());
			
			for (String strRow : rowKeyList) {
				List<SearchInputDisplayinRpt> listRowSearchInput = (List<SearchInputDisplayinRpt>) downloadDataFomatReq
						.getSearchInputMaprowCell().get(strRow); //
				lastRowValue = Integer.parseInt(strRow);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Current Row value " + lastRowValue);
				}
				Row searchInputRow = sheet.createRow(lastRowValue);
				for (SearchInputDisplayinRpt searchInputDisplayinRpt : listRowSearchInput) {
					if (_log.isDebugEnabled()) {
						_log.debug(methodName,
								searchInputDisplayinRpt.getCellNo() + "->" + searchInputDisplayinRpt.getFillValue());

					}
					Cell cell = searchInputRow.createCell(searchInputDisplayinRpt.getCellNo());
					cell.setCellValue(searchInputDisplayinRpt.getFillValue());
					cell.setCellStyle(headerCellStyle);
					searchInputRow.createCell(searchInputDisplayinRpt.getCellNo())
							.setCellValue(searchInputDisplayinRpt.getFillValue());
				}
			}

			++lastRowValue;
			// Displaying Report data rowWise

			Map<String, String> displayColumnMap = downloadDataFomatReq.getDisplayListColumns().stream()
					.collect(Collectors.toMap(DispHeaderColumn::getColumnName, DispHeaderColumn::getDisplayName));
			/*
			 * String[] columSeqArr =
			 * downloadDataFomatReq.getColumnSequenceNames().split(",");
			 */

			List<? extends Object> dataList = downloadDataFomatReq.getReportDataList();

			// Display report column headers
			lastRowValue = lastRowValue + 1;
			Row headerRow = sheet.createRow(lastRowValue);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Current Row value " + lastRowValue);
			}
			for (int col = 0; col < listDiplayColumns.size(); col++) {
				Cell headercell = headerRow.createCell(col);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName,
							"Current column " + displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
				}
				headercell.setCellValue(displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
				headercell.setCellStyle(headerCellStyle);
				totSummaryColCapture.put(listDiplayColumns.get(col).getColumnName(), String.valueOf(col));
			}

			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				C2CtransferCommisionRecordVO record = (C2CtransferCommisionRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				lastRowValue = lastRowValue + 1;
				Row dataRow = sheet.createRow(lastRowValue);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "row - " + lastRowValue);
				}
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "loop - " + col + " " + listDiplayColumns.get(col).getColumnName() + " "
								+ mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
					}
					dataRow.createCell(col)
							.setCellValue(mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
				}

			}
			lastRowValue=lastRowValue+1;
			Row dataRow = sheet.createRow(lastRowValue);
			Iterator<Map.Entry<String, String>> itr = totSummaryColCapture.entrySet().iterator();
	        while(itr.hasNext())
	        {
	             Map.Entry<String, String> entry = itr.next();
	           _log.info(methodName,"Key = " + entry.getKey() +", Column = " + entry.getValue() +  "ColValue :" + totSummaryColValue.get(entry.getKey()));
	             dataRow.createCell(Integer.parseInt(entry.getValue()))
	 			.setCellValue(totSummaryColValue.get(entry.getKey()));
	        }
			
			workbook.write(outputStream);
			fileData = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

		} catch (IOException ie) {
			if (_log.isDebugEnabled()) {
				_log.debug("Exception occured while filling cell value", ie);
			}
		}

		return fileData;
	}
	 	
	private Map<String, String> getMappedColumnValue(C2CtransferCommisionRecordVO c2CtransferCommisionRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
	
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TRANS_DATETIME.getColumnName(),
				c2CtransferCommisionRecordVO.getTransdateTime());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TRANSACTION_ID.getColumnName(),
				c2CtransferCommisionRecordVO.getTransactionID());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.SENDER_NAME.getColumnName(),
				c2CtransferCommisionRecordVO.getSenderName());

		mappedColumnValue.put(C2CTransferCommDownloadColumns.SENDER_MOBILE_NUM.getColumnName(),
				c2CtransferCommisionRecordVO.getSenderMsisdn());
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.SENDER_CATEGORY.getColumnName(),
				c2CtransferCommisionRecordVO.getSenderCategory());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.SENDER_DEBIT_QUANTITY.getColumnName(),
				c2CtransferCommisionRecordVO.getSenderDebitQuantity());
		
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.RECEIVER_NAME.getColumnName(),
				c2CtransferCommisionRecordVO.getReceiverName());
		
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.RECEIVER_MOB_NUM.getColumnName(),
				c2CtransferCommisionRecordVO.getReceiverMsisdn());

		mappedColumnValue.put(C2CTransferCommDownloadColumns.RECEIVER_CATEGORY.getColumnName(),
				c2CtransferCommisionRecordVO.getReceiverCategory());
		
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.RECEIVER_CREDIT_QUANTITY.getColumnName(),
				c2CtransferCommisionRecordVO.getReceiverCreditQuantity());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.PRODUCT_NAME.getColumnName(),
				c2CtransferCommisionRecordVO.getProductName());
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TRANSFER_IN_OUT.getColumnName(),
				c2CtransferCommisionRecordVO.getTransferInOut());

		mappedColumnValue.put(C2CTransferCommDownloadColumns.TRANSFER_SUBTYPE.getColumnName(),
				c2CtransferCommisionRecordVO.getTransferSubType())
		;
		mappedColumnValue.put(C2CTransferCommDownloadColumns.SOURCE.getColumnName(),
				c2CtransferCommisionRecordVO.getSource());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.REQUESTED_QUANTITY.getColumnName(),
				c2CtransferCommisionRecordVO.getRequestedQuantity());
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.DENOMINATION.getColumnName(),
				c2CtransferCommisionRecordVO.getDenomination());

		mappedColumnValue.put(C2CTransferCommDownloadColumns.COMMISSION.getColumnName(),
				c2CtransferCommisionRecordVO.getCommission());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.CUMULATIVE_BASE_COMMISSION.getColumnName(),
				c2CtransferCommisionRecordVO.getCumulativeBaseCommission());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TAX3.getColumnName(),
				c2CtransferCommisionRecordVO.getTax3());
		
		mappedColumnValue.put(C2CTransferCommDownloadColumns.PAYABLE_AMOUNT.getColumnName(),
				c2CtransferCommisionRecordVO.getPayableAmount());

		mappedColumnValue.put(C2CTransferCommDownloadColumns.NET_PAYABLE_AMOUNT.getColumnName(),
				c2CtransferCommisionRecordVO.getNetPayableAmount());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TRANSACTION_STATUS.getColumnName(),
				c2CtransferCommisionRecordVO.getTransactionStatus());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TRANSFER_CATEGORY.getColumnName(),
				c2CtransferCommisionRecordVO.getTransferCategory());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.REQUEST_GATEWAY.getColumnName(),
				c2CtransferCommisionRecordVO.getRequestGateway());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.DISTRIBUTION_TYPE.getColumnName(),
				c2CtransferCommisionRecordVO.getDistributionType());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.SENDER_PREVIOUS_STOCK.getColumnName(),
				c2CtransferCommisionRecordVO.getSenderPreviousStock());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.RECEIVER_PREVIOUS_STOCK.getColumnName(),
				c2CtransferCommisionRecordVO.getReceiverPreviousStock());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.SENDER_POST_STOCK.getColumnName(),
				c2CtransferCommisionRecordVO.getSenderPostStock());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.RECEIVER_POST_STOCK.getColumnName(),
				c2CtransferCommisionRecordVO.getReceiverPostStock());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.MODIFIED_ON.getColumnName(),
				c2CtransferCommisionRecordVO.getModifiedOn());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.REQUESTED_SOURCE.getColumnName(),
				c2CtransferCommisionRecordVO.getRequestedSource());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TAX1.getColumnName(),
				c2CtransferCommisionRecordVO.getTax1());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.TAX2.getColumnName(),
				c2CtransferCommisionRecordVO.getTax2());
		mappedColumnValue.put(C2CTransferCommDownloadColumns.INITIATOR_USER_NAME.getColumnName(),
				c2CtransferCommisionRecordVO.getInitiatorUserName());
		
		

		return mappedColumnValue; 
	}

	public HashMap<String,String> execute(C2CTransferCommReqDTO c2CTransferCommReqDTO, C2CTransferCommDownloadResp response)
			throws BTSLBaseException {
		HashMap<String,String> mp = new HashMap<String,String>(); // return all hidden values, which should not be exposed to outer world.
		final String methodName = "execute";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		NetworkProductDAO networkProductDAO = null;
		ChannelUserVO channelUserVO = null;
		Connection con = null;
		HashMap<String,String> totalSummaryMap = new HashMap<>();
		String success=null;
		String offlineReportExecutionStatus =null;
		Date currentDate = new Date();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> lhtValidEditColumns = Arrays.asList(C2CTransferCommDownloadColumns.values()).stream()
					.collect(Collectors.toMap(C2CTransferCommDownloadColumns::getColumnName,
							C2CTransferCommDownloadColumns::getColumnName));
			super.validateEditColumns(c2CTransferCommReqDTO.getDispHeaderColumnList(), lhtValidEditColumns);
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			networkProductDAO = new NetworkProductDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, c2CTransferCommReqDTO.getMsisdn());
			String fromDate = c2CTransferCommReqDTO.getFromDate();
			String toDate = c2CTransferCommReqDTO.getToDate();
			String extNgCode = c2CTransferCommReqDTO.getExtnwcode();
			// execute search api to get data.
			String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();

//			/*
//			 * if ( !BTSLUtil.isEmpty(pinPassHistoryReqDTO.getFileType()) &&
//			 * !pinPassHistoryReqDTO.getFileType().trim().toUpperCase().equals(
//			 * allowedFileType.toUpperCase())){ throw new
//			 * BTSLBaseException("PretupsUIReportsController",
//			 * "Pin Password history download", PretupsErrorCodesI.INVALID_FILE_FORMAT, 0,
//			 * null); }
//			 
			HashMap<String,String> reportInputKeyValMap= validateInputs(con, c2CTransferCommReqDTO);
			
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			String fileName=null;
			 if(c2CTransferCommReqDTO.isOffline()) {
				 fileName=c2CTransferCommReqDTO.getFileName();
		downloadDataFomatReq.setFileName(fileName);
			 }else {
				 // for  Online
			fileName = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
					PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_FILENAME.getReportValues(), null);
			downloadDataFomatReq.setFileName(fileName + System.currentTimeMillis());
			
			 }
			 downloadDataFomatReq.setFileType(c2CTransferCommReqDTO.getFileType());
			
			downloadDataFomatReq.setDisplayListColumns(c2CTransferCommReqDTO.getDispHeaderColumnList());
			downloadDataFomatReq
					.setSearchInputMaprowCell(getSearchInputValueMap(con, c2CTransferCommReqDTO, channelUserVO,reportInputKeyValMap));
			
			C2CTransferCommDownloadColumns[] C2CTransfercommcoluseqAttr = C2CTransferCommDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (C2CTransferCommDownloadColumns C2CTcolumsequence : C2CTransfercommcoluseqAttr) {
				reportColumnSeq.append(C2CTcolumsequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);

			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			downloadDataFomatReq.setLocale(c2CTransferCommReqDTO.getLocale());
		
			
//			C2CtransferCommRespDTO c2CtransferCommRespDTO = searchC2CTransferCommission(c2CTransferCommReqDTO);
//				if (c2CtransferCommRespDTO != null && c2CtransferCommRespDTO.getListC2CTransferCommRecordVO().size() == 0) {
//				throw new BTSLBaseException("PretupsUIReportsController", methodName,
//						PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
//			}
			// HashMap totSummaryColCapture -> contains excel col position [captured in createCSV and CreateXLS methods]
			// HashMap totSummaryColValue  -> contian col. Data
			
				
				
			
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.REQUESTED_QUANTITY.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalRequestedQuantity());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.DENOMINATION.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalMRP());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.CUMULATIVE_BASE_COMMISSION.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalCBC());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.TAX3.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalTax3());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.SENDER_DEBIT_QUANTITY.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalSenderDebitQuantity());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.RECEIVER_CREDIT_QUANTITY.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalReceiverCreditQuantity());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.PAYABLE_AMOUNT.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalPayableAmount());
//			totSummaryColValue.put(C2CTransferCommDownloadColumns.NET_PAYABLE_AMOUNT.getColumnName(), c2CtransferCommRespDTO.getC2CtransferCommSummryData().getTotalNetPayableAmount());
			
			
			

			
//			downloadDataFomatReq.setFileName(fileName + System.currentTimeMillis());
//			downloadDataFomatReq.setFileType(c2CTransferCommReqDTO.getFileType());
//			downloadDataFomatReq.setReportDataList(c2CtransferCommRespDTO.getListC2CTransferCommRecordVO());
//			downloadDataFomatReq.setSummaryObject(c2CtransferCommRespDTO.getC2CtransferCommSummryData());
//			downloadDataFomatReq.setDisplayListColumns(c2CTransferCommReqDTO.getDispHeaderColumnList());
//			downloadDataFomatReq
//					.setSearchInputMaprowCell(getSearchInputValueMap(con, c2CTransferCommReqDTO, channelUserVO,reportInputKeyValMap));


			/*
			Map<String, String> reportInputKeyValMap = new HashMap<String, String>();
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(),
					String.valueOf(c2CTransferCommReqDTO.getFromDate()));
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues(),
					String.valueOf(c2CTransferCommReqDTO.getToDate()));
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(),
					c2CTransferCommReqDTO.getExtnwcode());
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(),
					c2CTransferCommReqDTO.getDomain());
			reportInputKeyValMap.put(PretupsRptUIConsts.CATEGORY.getReportValues(),
					c2CTransferCommReqDTO.getCategoryCode());
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_INCLUDESTAFF.getReportValues(),
					c2CTransferCommReqDTO.getIncludeStaffUserDetails());
			
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_RECEIVERNUMBER.getReportValues(),
					c2CTransferCommReqDTO.getReceiverMobileNumber());
			
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERNUMBER.getReportValues(),
					c2CTransferCommReqDTO.getSenderMobileNumber());
			
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINOUT.getReportValues(),
					c2CTransferCommReqDTO.getTransferInout());
			
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),
					c2CTransferCommReqDTO.getTransferSubType());
			
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERUSER.getReportValues(),
					c2CTransferCommReqDTO.getTransferUser());
			
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),
					c2CTransferCommReqDTO.getTransferCategory());
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_SENDERUSER.getReportValues(),
					c2CTransferCommReqDTO.getUser());
			reportInputKeyValMap.put(PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(),
					c2CTransferCommReqDTO.getCategoryCode());
			*/
			
			// The purpose of passing downloadDataFomatReq, is for OFFline , instead of getting million records in List and iterating, 
			// we can avoid iterating and directly write csv or Excel, during fetch each record.
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			C2CtransferCommRespDTO c2CtransferCommRespDTO=null;
			 String totalnumberOfRecords =null;
			if(!c2CTransferCommReqDTO.isOffline()) {
				c2CtransferCommRespDTO =channelTransferDAO.downloadC2CTransferCommissionData(con, c2CTransferCommReqDTO, downloadDataFomatReq);
				 if(c2CtransferCommRespDTO!=null) {
				 totalnumberOfRecords=c2CtransferCommRespDTO.getTotalDownloadedRecords();
				 response.setFileData(c2CtransferCommRespDTO.getOnlineFileData());
				  response.setFileName(downloadDataFomatReq.getFileName());
				  response.setFileType(downloadDataFomatReq.getFileType());
				  if(c2CtransferCommRespDTO.isNoDataFound()) {
					  //offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_NODATA;
					  response.setStatus(success);
						response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
						String resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsErrorCodesI.NO_RECORDS_FOUND, null);
						response.setMessage(resmsg);
				  }else {
					  response.setTotalRecords(totalnumberOfRecords);
					  response.setStatus(success);
					 	response.setMessageCode(PretupsErrorCodesI.SUCCESS);
						String resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsErrorCodesI.SUCCESS, null);
						response.setMessage(resmsg);
					 	mp.put(PretupsRptUIConsts.ONLINE_FILE_PATH_KEY.getReportValues() ,c2CtransferCommRespDTO.getOnlineFilePath());
				  }
				 }
				
			} else {
				// offline DataWriting  Processing start....
				
				OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
				 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
				 
				 offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_INPROGRESS;
				try {
					c2CtransferCommRespDTO =channelTransferDAO.downloadC2CTransferCommissionData(con, c2CTransferCommReqDTO, downloadDataFomatReq);
					 if(c2CtransferCommRespDTO!=null) {
					 totalnumberOfRecords=c2CtransferCommRespDTO.getTotalDownloadedRecords();
					  if(c2CtransferCommRespDTO.isNoDataFound()) {
						  //offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_NODATA;
						  response.setStatus(success);
							response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
							String resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsErrorCodesI.NO_RECORDS_FOUND, null);
							response.setMessage(resmsg);
					  }else {
						  response.setTotalRecords(totalnumberOfRecords);
						  response.setStatus(success);
							response.setMessageCode(PretupsErrorCodesI.SUCCESS);
							String resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsErrorCodesI.SUCCESS, null);
							response.setMessage(resmsg);
					  }
					 }			 
				}catch(BTSLBaseException be) {
					 success = Integer.toString(PretupsI.RESPONSE_FAIL);
					 String resmsg =null;
					 if(be.getCause().getMessage().equals(PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED) ){
						 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
						 offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_CANCELLED;
					 response.setMessageCode(PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED);
					    resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
									PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED, new String[] {String.valueOf(c2CTransferCommReqDTO.getOfflineReportTaskID())});
					 } else {
					    offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_FAILED;
					    response.setMessageCode(PretupsErrorCodesI.FAILED);
						resmsg = RestAPIStringParser.getMessage(c2CTransferCommReqDTO.getLocale(),
								PretupsErrorCodesI.FAILED, null);
					 }
						response.setMessage(resmsg);
						response.setStatus(success);
				}
			
			}
		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			if (ex instanceof BTSLBaseException) {
				throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage(),
						((BTSLBaseException) ex).getArgs());
			}
		} finally {
			if (mcomCon != null) {
				mcomCon.close("PinpassHistServiceProcessor#execute");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("execute", " Exited ");
			}
			try {
				if (con != null) {
					con.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}

		}
		return mp;
	}

	@Override
	public OfflineReportStatus executeOffineService(EventObjectData srcObj) throws BTSLBaseException {
		C2CTransferCommReqDTO c2CTransferCommReqDTO	=(C2CTransferCommReqDTO) srcObj.getRequestData();
		C2CTransferCommDownloadResp response = new C2CTransferCommDownloadResp();
		c2CTransferCommReqDTO.setFileName(srcObj.getFileName());
		c2CTransferCommReqDTO.setOfflineReportTaskID(srcObj.getProcess_taskID());
		_log.info("C2C Offline service", "c2C Offline service executed..");
		_log.debug("C2C Offline service", "c2C Offline service executed.********************************.");
		OfflineReportStatus offlineReportStatus = new OfflineReportStatus();
		try {
		execute(c2CTransferCommReqDTO,response);
		offlineReportStatus.setMessage(response.getMessage());
		offlineReportStatus.setMessageCode(response.getMessageCode());
		offlineReportStatus.setTotalRecords(response.getTotalRecords());
		
		} catch(Exception ex) {
			_log.error("c2s Offline service", "Error occured in Channel to subcriber Transfer commission offline Report execution. ");
		}
		return offlineReportStatus;
	}
	}

