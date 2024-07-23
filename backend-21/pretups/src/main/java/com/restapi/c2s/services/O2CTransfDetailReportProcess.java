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
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CAcknowdgeDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfDetDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransferDetailsReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetSearchResp;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetTotSummryData;
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
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserMsisdnUserIDVO;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("O2CTransfDetailReportProcess")
public class O2CTransfDetailReportProcess extends CommonService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();

	public void getO2CTransfDetailReportSearch(O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO, O2CtransferDetSearchResp response)
			throws BTSLBaseException {

		final String methodName = "getO2CTransfDetailReportSearch";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelTransferDAO channelTransferDAO  = new ChannelTransferDAO (); 

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			O2CtransferDetRespDTO o2CtransferDetRespDTO =channelTransferDAO.searchO2CTransferDetails(con, o2CTransferDetailsReqDTO);
			
			 List<O2CtransferDetRecordVO> listGetO2CTransferDetDTO= o2CtransferDetRespDTO.getListO2CTransferCommRecordVO();
			 
			 if(listGetO2CTransferDetDTO.isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			response.setO2cTransferDetailList(listGetO2CTransferDetDTO);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(),
					PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

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
					mcomCon.close("PretupsUIReportsController");
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

	
	public HashMap<String,String> validateInputs(Connection con,O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO) throws BTSLBaseException {
		final String methodName ="validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
		Date currentDate = new Date();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();

		String fromDate = o2CTransferDetailsReqDTO.getFromDate();
		String toDate = o2CTransferDetailsReqDTO.getToDate();
		String extNwCode = o2CTransferDetailsReqDTO.getExtnwcode();
		
		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), fromDate);
		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), toDate);
		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), extNwCode);
		
		 List keyValueList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE, true);
		   HashMap<String, String> mp =(HashMap<String, String>) keyValueList.stream()
		      .collect(Collectors.toMap(ListValueVO::getValue,ListValueVO::getLabel));
		   
		   
		   if(BTSLUtil.isNullString(o2CTransferDetailsReqDTO.getTransferSubType())) {
			   throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.TRANSFER_SUB_TYPE_REQUIRED, 0, null);
		   }
		   
		   if(BTSLUtil.isNullString(o2CTransferDetailsReqDTO.getGeography())) {
			   throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
		   }
		   
		   
		    
		    if(keyValueList != null &&   (o2CTransferDetailsReqDTO.getTransferSubType()!=null && !o2CTransferDetailsReqDTO.getTransferSubType().trim().toUpperCase().equals(PretupsI.ALL)  && o2CTransferDetailsReqDTO.getTransferSubType().indexOf(",")==0 && !mp.containsKey(o2CTransferDetailsReqDTO.getTransferSubType()))) {
		    	 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.INVALID_TRF_TYPE, 0, null);
		    }

		    if(o2CTransferDetailsReqDTO.getTransferSubType().equals(PretupsI.ALL) || o2CTransferDetailsReqDTO.getTransferSubType().indexOf(",")>0 ) { //As UI is sending this way
		    	reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),PretupsI.ALL);	
		    }else {
		    	reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),mp.get(o2CTransferDetailsReqDTO.getTransferSubType()));
		    }
		    
		    
//		    List transferCategoryList =  LookupsCache.loadLookupDropDown(PretupsI.C2C_TRANSFER_TYPE, true);
//		    HashMap<String, String> tranferCatMap =(HashMap<String, String>) transferCategoryList.stream()
//				      .collect(Collectors.toMap(ListValueVO::getValue,ListValueVO::getLabel));
//		    
//		    if(!o2CTransferDetailsReqDTO.getTransferCategory().equals(PretupsI.ALL) &&  !tranferCatMap.containsKey(o2CTransferDetailsReqDTO.getTransferCategory())) {
//		    	throw new BTSLBaseException("PretupsUIReportsController", methodName,
//						PretupsErrorCodesI.INVALID_TRF_TYPE, 0, null);
//		    }
		    
		    reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),PretupsI.ALL);
		    
		    
			ArrayList lookUpList = LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_CATEGORY_LOOKUPTYPE, true); //TRFT
			boolean found =false;
			if(lookUpList!=null) { 
				for(int i=0;i<lookUpList.size();i++) {
					 ListValueVO listValueVO =(ListValueVO) lookUpList.get(i);
					 if(listValueVO.getValue().equals(o2CTransferDetailsReqDTO.getTransferCategory())){
						
							reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),listValueVO.getLabel());
						 found=true;break; 
					 }
					
				}
				if(!found && (o2CTransferDetailsReqDTO.getTransferCategory()!=null && !o2CTransferDetailsReqDTO.getTransferCategory().trim().equals(PretupsI.ALL) )) {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.INVALID_TRF_CATEGORY, 0, null);
				}
				
			}
			

		    

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

		if (extNwCode != null && !extNwCode.trim().equals(PretupsI.ALL.trim())) {
			NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(extNwCode);
			if (BTSLUtil.isNullObject(networkVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), networkVO.getNetworkName());
		}
		
		
		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), o2CTransferDetailsReqDTO.getCategoryCode());
		if (o2CTransferDetailsReqDTO.getCategoryCode() != null
				&& !o2CTransferDetailsReqDTO.getCategoryCode().trim().equals(PretupsI.ALL)) {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					o2CTransferDetailsReqDTO.getCategoryCode());
			if (BTSLUtil.isNullObject(categoryVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), categoryVO.getCategoryName());
		}
		
//		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERUSERCATEGORY.getReportValues(), o2CTransferDetailsReqDTO.getTransferUserCategory());
//		
//		if (o2CTransferDetailsReqDTO.getTransferUserCategory() != null
//				&& !o2CTransferDetailsReqDTO.getTransferUserCategory().trim().equals(PretupsI.ALL)) {
//			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
//					o2CTransferDetailsReqDTO.getTransferUserCategory());
//			if (BTSLUtil.isNullObject(categoryVO)) {
//				throw new BTSLBaseException("PretupsUIReportsController", methodName,
//						PretupsErrorCodesI.INVALID_TRANSFER_USER_CATGRY, 0, null);
//			}
//			reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERUSERCATEGORY.getReportValues(), categoryVO.getCategoryName());			
//		}

		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), o2CTransferDetailsReqDTO.getDomain());
		if (o2CTransferDetailsReqDTO.getDomain() != null
				&& !o2CTransferDetailsReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, o2CTransferDetailsReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), domainVO.getDomainName());
		}
		if(BTSLUtil.isEmpty(o2CTransferDetailsReqDTO.getUser()) ) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVALID_USER, 0, null);

     	}
		ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		List<UserMsisdnUserIDVO> listUserMsisdnUserIDVO=null;
		if(!BTSLUtil.isEmpty(o2CTransferDetailsReqDTO.getUser()) && !o2CTransferDetailsReqDTO.getUser().trim().toUpperCase().equals(PretupsI.ALL) ) {
			
			// o2CTransferDetailsReqDTO.getUser() Will contain UsrID , for Validation, For swagger purpose 
			 try {
				listUserMsisdnUserIDVO =channelUserDAO.loadUserNameAutoSearchOnZoneDomainCategoryQry(null, null, con, o2CTransferDetailsReqDTO.getCategoryCode(), o2CTransferDetailsReqDTO.getDomain(), o2CTransferDetailsReqDTO.getUserId(), o2CTransferDetailsReqDTO.getGeography(),o2CTransferDetailsReqDTO.getUser());
			} catch (SQLException | BTSLBaseException e) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_USER, 0, null);
			}
			 
			if(listUserMsisdnUserIDVO ==null && (listUserMsisdnUserIDVO!=null &&  listUserMsisdnUserIDVO.isEmpty() )) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_USER, 0, null);
			}
			
			
			reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_SENDERUSERNAME.getReportValues(), o2CTransferDetailsReqDTO.getUser());	
			
		}
		
		
//		ArrayList lookUpList = LookupsCache.loadLookupDropDown(PretupsI.DISTRIBUTION_TYPE, true); //TRFT
//		boolean found =false;
//		LookupsVO  lookupVO=null;
//		if(lookUpList!=null) { 
//			for(int i=0;i<lookUpList.size();i++) {
//				 if(lookUpList.get(i).equals(o2CTransferDetailsReqDTO.getTransferSubType())){
//					 lookupVO = (LookupsVO) lookUpList.get(i);
//					 found=true;break; 
//				 }
//				
//			}
//		}
		CommonUtil commonUtil = new CommonUtil();
		ListValueVO listValueVO=null;
		reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(), PretupsI.ALL);
		if(o2CTransferDetailsReqDTO.getDistributionType()!=null && !o2CTransferDetailsReqDTO.getDistributionType().equals(PretupsI.ALL)) {
			listValueVO=	commonUtil.validationDistributionType(o2CTransferDetailsReqDTO.getDistributionType());
			if(listValueVO==null) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_DISTRIBUTION_TYPE, 0, null);
	    	 }
			reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(), listValueVO.getLabel());
		}
	 
	
		 return reportInputKeyValMap;
	}
	
	
	
	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO,
			HashMap<String,String> reportInputKeyValMap) {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(),
						null)	, PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(o2CTransferDetailsReqDTO.getFromDate(), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TODATE.getReportValues(),
						null), PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(o2CTransferDetailsReqDTO.getToDate(), PretupsRptUIConsts.FOUR.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		
		return mapMultipleColumnRow;
	}

//public static String createFileFormat(String fileType,List<? extends Object> dataList , MultiValuedMap<String, SearchInputDisplayinRpt> SearchInputMaprowCell ) {
	private String createFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {
		String fileData = null;
		if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			fileData = createXlsFileFormatPassbook(downloadDataFomatReq);
		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			Map<String, String> map = new LinkedHashMap<>();
			fileData = createCSVFileFormatPassbook(downloadDataFomatReq);
		}
		return fileData;
	}

	private String createCSVFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {

		final String methodName = "createCSVFileFormatPassbook";
		String fileData = null;
		HashMap<String,String > totSummaryColCapture =  new HashMap<String,String>();
		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] reportheadervalue = new String[1];
			reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
					null)  ;
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

			Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

			String[] inputRow1 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues()) };
			csvWriter.writeNext(inputRow1);

	
			String[] inputRow2 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TODATE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TODATE.getReportValues()) };
			csvWriter.writeNext(inputRow2);
			
			
			String[] inputRow3 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()) };
			csvWriter.writeNext(inputRow3);
			
			String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()) };
			csvWriter.writeNext(inputRow4);
			
			String[] inputRow5 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues()) };
			csvWriter.writeNext(inputRow5);
			
			String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERINSUBTYPE.getReportValues()) };
			csvWriter.writeNext(inputRow6);
			
			String[] inputRow7 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_TRANSFERCATEGORY.getReportValues()) };
			csvWriter.writeNext(inputRow7);
			
			
			String[] inputRow8 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues()) };
			csvWriter.writeNext(inputRow8);

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
				totSummaryColCapture.put(listDiplayColumns.get(k).getColumnName(), String.valueOf(k));
			}
			csvWriter.writeNext(columnHeaderNames);

			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {
				O2CtransferDetRecordVO record = (O2CtransferDetRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				String[] dataRow = new String[listDiplayColumns.size()];
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
				}
				csvWriter.writeNext(dataRow);
			}
			
	        
	    	String[] dataRow = new String[listDiplayColumns.size()];
			for (int col = 0; col < listDiplayColumns.size(); col++) {
				if(downloadDataFomatReq.getTotalSummaryMap().containsKey(listDiplayColumns.get(col).getColumnName())) {
				dataRow[col] = downloadDataFomatReq.getTotalSummaryMap().get(listDiplayColumns.get(col).getColumnName());
				}else {
					dataRow[col]="";
				}
			}
			csvWriter.writeNext(dataRow);
	    
			String output = writer.toString();
			fileData = new String(Base64.getEncoder().encode(output.getBytes()));
		} catch (IOException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exception occured while generating CSV file for Passbook download ");

			}
		}

		return fileData;
	}

	private String createXlsFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {
		final String methodName = "createXlsFileFormatPassbook";
		String fileData = null;
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Workbook workbook = null;
		 HashMap<String,String > totSummaryColCapture =  new HashMap<String,String>();
		
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			if(PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(downloadDataFomatReq.getFileType().toUpperCase())) {
				workbook = new XSSFWorkbook();
			} else {
				workbook = new HSSFWorkbook();
			}
			
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
			reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
					null));
			reportHeadingCell.setCellStyle(headerCellStyle);
			sheet.addMergedRegion(
					new CellRangeAddress(PretupsRptUIConsts.ZERO.getNumValue(), PretupsRptUIConsts.ZERO.getNumValue(),
							PretupsRptUIConsts.ZERO.getNumValue(), PretupsRptUIConsts.FOUR.getNumValue()));

			if (_log.isDebugEnabled()) {
				_log.debug(methodName,"Current Row value " +lastRowValue);
			}
			lastRowValue=lastRowValue+1;
			
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,"Current Row value " +lastRowValue);
			}
			for (String strRow : downloadDataFomatReq.getSearchInputMaprowCell().keySet()) {
				List<SearchInputDisplayinRpt> listRowSearchInput = (List<SearchInputDisplayinRpt>) downloadDataFomatReq
						.getSearchInputMaprowCell().get(strRow); //
				lastRowValue = Integer.parseInt(strRow);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName,"Current Row value " +lastRowValue);
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
			/*String[] columSeqArr = downloadDataFomatReq.getColumnSequenceNames().split(",");*/

			List<? extends Object> dataList = downloadDataFomatReq.getReportDataList(); 
				
			// Display report column headers
			lastRowValue = lastRowValue + 1;
			Row headerRow = sheet.createRow(lastRowValue);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,"Current Row value " +lastRowValue);
			}
			for (int col = 0; col < listDiplayColumns.size(); col++) {
				Cell headercell = headerRow.createCell(col);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName,"Current column " +displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
				}
				
				headercell.setCellValue(displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
				headercell.setCellStyle(headerCellStyle);
				totSummaryColCapture.put(listDiplayColumns.get(col).getColumnName(), String.valueOf(col));
		  }
			
		
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				O2CtransferDetRecordVO record = (O2CtransferDetRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				lastRowValue = lastRowValue + 1;
				Row dataRow = sheet.createRow(lastRowValue);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "row - " + lastRowValue );
				}
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, "loop - " + col + " " +  listDiplayColumns.get(col).getColumnName() + " " + mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
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
	           _log.info(methodName,"Key = " + entry.getKey() +", Column = " + entry.getValue() +  "ColValue :" + downloadDataFomatReq.getTotalSummaryMap().get(entry.getKey()));
	             dataRow.createCell(Integer.parseInt(entry.getValue()))
	 			.setCellValue(downloadDataFomatReq.getTotalSummaryMap().get(entry.getKey()));
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

	private Map<String, String> getMappedColumnValue(O2CtransferDetRecordVO o2CtransferDetRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(O2CTransfDetDownloadColumns.DATE_TIME.getColumnName() , o2CtransferDetRecordVO.getTransdateTime());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANSACTION_ID.getColumnName() , o2CtransferDetRecordVO.getTransactionID());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.DISTRIBUTION_TYPE.getColumnName() , o2CtransferDetRecordVO.getDistributionType());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.SENDER_NAME.getColumnName() , o2CtransferDetRecordVO.getSenderName());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.SENDER_MSISDN.getColumnName() , o2CtransferDetRecordVO.getSenderMsisdn());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.RECEIVER_NAME.getColumnName() , o2CtransferDetRecordVO.getReceiverName());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.RECEIVER_MSISDN.getColumnName() , o2CtransferDetRecordVO.getReceiverMsisdn());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.RECEIVER_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getReceiverQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANSFER_CATEGORY.getColumnName() , o2CtransferDetRecordVO.getTransferCategory());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANSFER_SUB_TYPE.getColumnName() , o2CtransferDetRecordVO.getTransferSubType());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.MODIFEID_ON.getColumnName() , o2CtransferDetRecordVO.getModifiedOn());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.PRODUCT_NAME.getColumnName() , o2CtransferDetRecordVO.getProductName());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANS_DATE_EXTERNAL.getColumnName() , o2CtransferDetRecordVO.getExternalTransferDate());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANS_NUMBER_EXTERNAL.getColumnName() , o2CtransferDetRecordVO.getExternalTransferNumber());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANSACTION_MODE.getColumnName() , o2CtransferDetRecordVO.getTransactionMode());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.REQUESTED_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getRequestedQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.APPROVED_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getApprovedQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.COMMISSION.getColumnName() , o2CtransferDetRecordVO.getCommission());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.CUMMULATIVE_BASE_COMMISSION.getColumnName() , o2CtransferDetRecordVO.getCumulativeBaseCommission());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TAX1_AMOUNT.getColumnName() , o2CtransferDetRecordVO.getTax1());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TAX2_AMOUNT.getColumnName() , o2CtransferDetRecordVO.getTax2());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TAX3_AMOUNT.getColumnName() , o2CtransferDetRecordVO.getTax3());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.PAYABLE_AMOUNT.getColumnName() , o2CtransferDetRecordVO.getPayableAmount());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.NET_PAYABLE_AMOUNT.getColumnName() , o2CtransferDetRecordVO.getNetPayableAmount());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.INITIAL_REMARKS.getColumnName() , o2CtransferDetRecordVO.getInitiatorRemarks());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.APPROVER1_REMARKS.getColumnName() , o2CtransferDetRecordVO.getApprover1Remarks());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.APPROVER2_REMARKS.getColumnName() , o2CtransferDetRecordVO.getApprover2Remarks());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.APPROVER3_REMARKS.getColumnName() , o2CtransferDetRecordVO.getApprover2Remarks());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TRANSACTION_STATUS.getColumnName() , o2CtransferDetRecordVO.getTransactionStatus());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.FIRST_APPROVED_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getFirstLevelApprovedQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.SECOND_APPROVED_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getSecondLevelApprovedQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.THIRD_APPROVED_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getThirdLevelApprovedQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.PAYMENT_INTRUMENT_TYPE.getColumnName() , o2CtransferDetRecordVO.getPaymentInstType());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.PAYMENT_INTRUMENT_NUMBER.getColumnName() , o2CtransferDetRecordVO.getPaymentInstNumber());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.PAYMENT_INTRUMENT_DATE.getColumnName() , o2CtransferDetRecordVO.getPaymentInstDate());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.REQUEST_GATEWAY.getColumnName() , o2CtransferDetRecordVO.getRequestGateWay());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.SENDER_DEBIT_QUANTITY.getColumnName() , o2CtransferDetRecordVO.getSenderDebitQuantity());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.RECEIVER_PRE_BALANCE.getColumnName() , o2CtransferDetRecordVO.getReceiverPreviousBalance());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.RECEIVER_POST_BALANCE.getColumnName() , o2CtransferDetRecordVO.getReceiverPostBalance());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.DOMAIN_NAME.getColumnName() , o2CtransferDetRecordVO.getDomainName());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.BATCH_TYPE.getColumnName() , o2CtransferDetRecordVO.getBatchType());
		
		//Voucher
		mappedColumnValue.put(O2CTransfDetDownloadColumns.VOUCHER_BATCH_NUMBER.getColumnName() , o2CtransferDetRecordVO.getVoucherBatchNumber());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.VOMS_PRODUCT_NAME.getColumnName() , o2CtransferDetRecordVO.getVomsProductName());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.VOUCHER_DENOMINATION.getColumnName() , o2CtransferDetRecordVO.getBatchType());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TOTAL_NO_OF_VOUCHERS.getColumnName() , o2CtransferDetRecordVO.getTotalNoofVouchers());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.FROM_SERIAL_NUMBER.getColumnName() , o2CtransferDetRecordVO.getFromSerialNumber());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.TO_SERIAL_NUMBER.getColumnName() , o2CtransferDetRecordVO.getToSerialNumber());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.VOUCHER_SEGMENT.getColumnName() , o2CtransferDetRecordVO.getVoucherSegment());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.VOUCHER_TYPE.getColumnName() , o2CtransferDetRecordVO.getVoucherType());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.VOUCHER_DENOMINATION.getColumnName() , o2CtransferDetRecordVO.getVoucherDenomination());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.CREATED_ON.getColumnName() , o2CtransferDetRecordVO.getCreatedOn());
		mappedColumnValue.put(O2CTransfDetDownloadColumns.CLOSE_DATE.getColumnName() , o2CtransferDetRecordVO.getCloseDate());
		
		
				
		return mappedColumnValue;
  }

	public void execute(O2CTransferDetailsReqDTO o2CTransferDetailsReqDTO, O2CTransferDetailDownloadResp response) throws BTSLBaseException {
		// TODO Auto-generated method stub
		final String methodName = "execute";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		Date currentDate = new Date();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> ValidEditColumns = Arrays.asList(O2CTransfDetDownloadColumns.values()).stream()
					.collect(Collectors.toMap(O2CTransfDetDownloadColumns::getColumnName,
							O2CTransfDetDownloadColumns::getColumnName));
			super.validateEditColumns(o2CTransferDetailsReqDTO.getDispHeaderColumnList(), ValidEditColumns);
			channelUserDAO = new ChannelUserDAO();
			ChannelTransferDAO channelTransferDAO  = new ChannelTransferDAO ();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, o2CTransferDetailsReqDTO.getMsisdn());
			
			String extNgCode = o2CTransferDetailsReqDTO.getExtnwcode();
			
			//execute search api to get data.
			
			HashMap<String,String> reportInputKeyValMap= validateInputs(con, o2CTransferDetailsReqDTO);
			HashMap<String,String> totSummaryColValue = new LinkedHashMap<String,String>();
			/*
			 if ( !BTSLUtil.isEmpty(o2CTransfAckDownloadReqDTO.getFileType()) && !o2CTransfAckDownloadReqDTO.getFileType().trim().toUpperCase().equals(allowedFileType.toUpperCase())){
					throw new BTSLBaseException("PretupsUIReportsController", "O",
							PretupsErrorCodesI.INVALID_FILE_FORMAT, 0, null);
			 }*/
			
			O2CtransferDetRespDTO o2CtransferDetRespDTO = channelTransferDAO.searchO2CTransferDetails(con, o2CTransferDetailsReqDTO);
			List<O2CtransferDetRecordVO> listO2CtransferDetRecordVO =o2CtransferDetRespDTO.getListO2CTransferCommRecordVO();
			 
			 if(listO2CtransferDetRecordVO.isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			    totSummaryColValue.put(O2CTransfDetDownloadColumns.REQUESTED_QUANTITY.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalRequestedQuantity());
			    totSummaryColValue.put(O2CTransfDetDownloadColumns.PAYABLE_AMOUNT.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalPayableAmount());
			    totSummaryColValue.put(O2CTransfDetDownloadColumns.NET_PAYABLE_AMOUNT.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalNetPayableAmount());
			    totSummaryColValue.put(O2CTransfDetDownloadColumns.RECEIVER_QUANTITY.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalReceiverCreditQuantity());
				totSummaryColValue.put(C2CTransferCommDownloadColumns.SENDER_DEBIT_QUANTITY.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalSenderDebitQuantity());
				totSummaryColValue.put(C2CTransferCommDownloadColumns.COMMISSION.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalCommission());
				totSummaryColValue.put(C2CTransferCommDownloadColumns.CUMULATIVE_BASE_COMMISSION.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotalCBCAmount());
				totSummaryColValue.put(C2CTransferCommDownloadColumns.TAX1.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotaltax1());
				totSummaryColValue.put(C2CTransferCommDownloadColumns.TAX2.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotaltax2());
				totSummaryColValue.put(C2CTransferCommDownloadColumns.TAX3.getColumnName(), o2CtransferDetRespDTO.getO2CtransferDetTotSummryData().getTotaltax3());
			
			
				
			
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			String fileName = RestAPIStringParser.getMessage(o2CTransferDetailsReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_FILENAME.getReportValues(),
					null);
			downloadDataFomatReq.setFileName(
					fileName + System.currentTimeMillis());
			downloadDataFomatReq.setFileType(o2CTransferDetailsReqDTO.getFileType());
			downloadDataFomatReq.setReportDataList(listO2CtransferDetRecordVO);
			downloadDataFomatReq.setDisplayListColumns(o2CTransferDetailsReqDTO.getDispHeaderColumnList());
			downloadDataFomatReq.setSearchInputMaprowCell(getSearchInputValueMap(con,o2CTransferDetailsReqDTO,reportInputKeyValMap));
			downloadDataFomatReq.setSummaryObject(o2CtransferDetRespDTO.getO2CtransferDetTotSummryData());
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			downloadDataFomatReq.setLocale(o2CTransferDetailsReqDTO.getLocale());
			downloadDataFomatReq.setTotalSummaryMap(totSummaryColValue);

			O2CTransfDetDownloadColumns [] downloadSequenceAttributes = O2CTransfDetDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (O2CTransfDetDownloadColumns o2cColusequence : downloadSequenceAttributes) {
				reportColumnSeq.append(o2cColusequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			String fileData = createFileFormatPassbook(downloadDataFomatReq);

			response.setFileData(fileData);
			response.setFileType(downloadDataFomatReq.getFileType().toLowerCase());
			response.setFileName(downloadDataFomatReq.getFileName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsErrorCodesI.SUCCESS,
					null);
			response.setMessage(resmsg);

		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			if(ex instanceof BTSLBaseException) {
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage(),
					((BTSLBaseException) ex).getArgs());
			}
		}
		finally{
		if (mcomCon != null) {
			mcomCon.close("LowthresholdServicer#execute");
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

	}

}