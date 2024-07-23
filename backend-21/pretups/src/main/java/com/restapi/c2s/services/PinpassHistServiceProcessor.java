package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.mcom.common.CommonUtil;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.LTHDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PinPassHistoryReqDTO;
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
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("PinpassHistServiceProcessor")
public class PinpassHistServiceProcessor extends CommonService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();

	private Map<String, String> reportInputKeyValMap = new HashMap<String, String>();
	
	
	public List<PinPassHistSearchRecordVO>  searchPinPassHist(Connection con,PinPassHistoryReqDTO pinPassHistoryReqDTO) throws BTSLBaseException{
		final String methodName = "searchPinPassHist";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		List<PinPassHistSearchRecordVO> listPinPassHistSearchRecordVO=null; 
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDao = new UserDAO();
			listPinPassHistSearchRecordVO= userDao.getPinPassHistDetails(con,
					pinPassHistoryReqDTO);
			
			 if(listPinPassHistSearchRecordVO!=null && listPinPassHistSearchRecordVO.size()==0   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
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

    return listPinPassHistSearchRecordVO;		
	}
	
	
	
public void validateInputs(Connection con,PinPassHistoryReqDTO pinPassHistoryReqDTO) throws BTSLBaseException {
			final String methodName ="validateInputs";
			Date currentDate = new Date();
		    CategoryDAO categoryDAO = new CategoryDAO();
		    DomainDAO domainDAO = new DomainDAO();
		
			String fromDate = pinPassHistoryReqDTO.getFromDate();
			String toDate = pinPassHistoryReqDTO.getToDate();
			String extNgCode = pinPassHistoryReqDTO.getExtnwcode();

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

	  if(extNgCode!=null && !extNgCode.trim().equals(PretupsI.ALL.trim()))		{
			 NetworkVO networkVO =	 (NetworkVO) NetworkCache.getObject(extNgCode);
			 if(BTSLUtil.isNullObject(networkVO)   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			 }
	  } 
		 
	  if(pinPassHistoryReqDTO.getCategoryCode()!=null && !pinPassHistoryReqDTO.getCategoryCode().trim().equals(PretupsI.ALL) ) {
			 CategoryVO categoryVO=   categoryDAO.loadCategoryDetailsByCategoryCode(con, pinPassHistoryReqDTO.getCategoryCode());
			 if(BTSLUtil.isNullObject(categoryVO)   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
			 }
	  }	 
	  
	  if(pinPassHistoryReqDTO.getDomain()!=null && !pinPassHistoryReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
		    DomainVO domainVO =	 domainDAO.loadDomainVO(con, pinPassHistoryReqDTO.getDomain());
		   if(BTSLUtil.isNullObject(domainVO)   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			 }    
	  }
	  
	  
	  if(pinPassHistoryReqDTO.getReqType()!=null &&  !(pinPassHistoryReqDTO.getReqType().toUpperCase().equals(PretupsI.USER_PIN_MANAGEMENT) || pinPassHistoryReqDTO.getReqType().toUpperCase().equals(PretupsI.USER_PASSWORD_MANAGEMENT))     ) {
	 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.ALLOWED_REQ_TYPE, 0, null);
	  
	  }
	  
	  if(BTSLUtil.isEmpty(pinPassHistoryReqDTO.getUserType())  ) {
	 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_USER_TYPE, 0, null);
	  }
	  //LookupsCache.loadLookupDropDown(PretupsI.PRODUCT_TYPE, true) 
	  if(!BTSLUtil.isEmpty(pinPassHistoryReqDTO.getUserType()) && !pinPassHistoryReqDTO.getUserType().trim().toUpperCase().equalsIgnoreCase(PretupsI.ALL) &&		  !(pinPassHistoryReqDTO.getUserType().toUpperCase().equals(PretupsI.CHANNEL_USER_TYPE) || 
					  pinPassHistoryReqDTO.getUserType().toUpperCase().equals(PretupsI.OPERATOR_USER_TYPE) ||
					  pinPassHistoryReqDTO.getUserType().toUpperCase().equals(PretupsI.STAFF_USER_TYPE) ) ) {
			  throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_USER_TYPE, 0, null);
	 	}

}
	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,PinPassHistoryReqDTO pinPassHistoryReqDTO ,
			ChannelUserVO channelUserVO) {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERNAME.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(channelUserVO.getUserName(), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(pinPassHistoryReqDTO.getFromDate(), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null), PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(pinPassHistoryReqDTO.getToDate(), PretupsRptUIConsts.FOUR.getNumValue()));
		
		String userType =pinPassHistoryReqDTO.getUserType();
	
		if(userType!=null && !userType.trim().equals(PretupsI.ALL)) {
			final ArrayList userTypeList = LookupsCache.loadLookupDropDown(PretupsI.LOOKUP_USER_TYPE, true);
			CommonUtil commonUtil = new CommonUtil();
			ListValueVO   listValueVO = BTSLUtil.getOptionDesc(commonUtil.getUserTypeLookupCode(userType) , userTypeList);
				userType=listValueVO.getLabel();
		}
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(userType, PretupsRptUIConsts.ONE.getNumValue()));
		
		String domainName =pinPassHistoryReqDTO.getDomain();
		if(pinPassHistoryReqDTO.getDomain()!=null && !pinPassHistoryReqDTO.getDomain().trim().equals(PretupsI.ALL)) { 
		DomainDAO  domainDAO = new DomainDAO();
	   DomainVO domainVO =null;
			try {
				 domainVO =domainDAO.loadDomainVO(con,pinPassHistoryReqDTO.getDomain());
				 domainName = domainVO.getDomainName();
			} catch (BTSLBaseException e1) {
				_log.error(domainVO, e1);
			}
		
		}	
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(domainName,PretupsRptUIConsts.ONE.getNumValue()));	
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_REQTYPE.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		LookupsVO lookupsVO =null;
		try {
			 lookupsVO = (LookupsVO) LookupsCache.getObject(PretupsI.PIN_USER_CHU, pinPassHistoryReqDTO.getReqType());
		} catch (BTSLBaseException e1) {
			_log.error("PinPasswordhistory ::getSearchInputValueMap", e1);
			
		}
		String reqType=pinPassHistoryReqDTO.getReqType();
		if(lookupsVO!=null) {
			reqType=lookupsVO.getLookupName();
		}
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
				new SearchInputDisplayinRpt(reqType,PretupsRptUIConsts.ONE.getNumValue()));
		
		String categoryName="ALL";
		CategoryDAO categoryDAO = new CategoryDAO();
		try {
			CategoryVO categoryVO= categoryDAO.loadCategoryDetailsByCategoryCode(con, pinPassHistoryReqDTO.getCategoryCode());
			
		
			if(categoryVO!=null) {
				categoryName=categoryVO.getCategoryName();
			}
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(categoryName, PretupsRptUIConsts.ONE.getNumValue()));
		
		} catch (BTSLBaseException e) {
			_log.error("LowthreholdReport :: Exception occured while fetching category details", e);
		}
		
		NetworkVO networkVO =	 (NetworkVO) NetworkCache.getObject(pinPassHistoryReqDTO.getExtnwcode());
		String networkName="ALL";
		if(networkVO!=null) {
			networkName=networkVO.getNetworkName();
		}

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(networkName, PretupsRptUIConsts.ONE.getNumValue()));
		
		
		//For CSV inputs
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERNAME.getReportValues(), channelUserVO.getUserName());
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(),
				String.valueOf(pinPassHistoryReqDTO.getFromDate()));
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_TODATE.getReportValues(),
				String.valueOf(pinPassHistoryReqDTO.getToDate()));
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(),
				networkName);
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues(),
				userType);
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(),
				categoryName);
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(),
				domainName);
		reportInputKeyValMap.put(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_REQTYPE.getReportValues(),
				pinPassHistoryReqDTO.getReqType());
		return mapMultipleColumnRow;
	}


	private String createFileFormatPinPassHist(DownloadDataFomatReq downloadDataFomatReq) {
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
			reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null);
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

		//	Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

			String[] inputRow1 = {RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERNAME.getReportValues(), null) + " : "
					+ reportInputKeyValMap.get( PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERNAME.getReportValues()) };
			csvWriter.writeNext(inputRow1);

			String[] inputRow2 = {RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null) + " : "
					+ reportInputKeyValMap.get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues()) + "     "
					+ RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null)+ " : "
					+ reportInputKeyValMap.get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_TODATE.getReportValues()) };
			csvWriter.writeNext(inputRow2);

			String[] inputRow3 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), null) + " : "
					+ reportInputKeyValMap.get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues()) };
			csvWriter.writeNext(inputRow3);
			
			String[] inputRow4 = {RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null) + " : "
					+ reportInputKeyValMap.get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()) };
			csvWriter.writeNext(inputRow4);
			
			String[] inputRow5 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues(), null) + " : "
					+ reportInputKeyValMap.get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues()) };
			csvWriter.writeNext(inputRow5);
			
		
			String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null) + " : "
					+ reportInputKeyValMap.get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()) };
			csvWriter.writeNext(inputRow6);
			
		
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
				
				//Condition to display dynamic header column value as Mobile Number or Login ID base on request Type
				if(listDiplayColumns.get(k).getColumnName().trim().equals(PinPassHistDownloadColumns.MSISDN_LOGINID.getColumnName())) {
					   if(downloadDataFomatReq.getInputParamMap().get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_REQTYPE.getReportValues()).equals(PretupsI.USER_PASSWORD_MANAGEMENT)) {
						   columnHeaderNames[k] = RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_COL_HEADER_LOGIN_ID.getReportValues(), null);	   
					   }else {
						   columnHeaderNames[k] = RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_COL_HEADER_MOBILE_NUM.getReportValues(), null);	
					   }
					}else {
						columnHeaderNames[k] = displayColumnMap.get(listDiplayColumns.get(k).getColumnName());	
					}
			}
			csvWriter.writeNext(columnHeaderNames);

			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {
				PinPassHistSearchRecordVO record = (PinPassHistSearchRecordVO) dataList.get(i);
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
				_log.debug(methodName, "Exception occured while generating CSV file for Passbook download ");

			}
		}

		return fileData;
	}

	private String createXlsFileFormat(DownloadDataFomatReq downloadDataFomatReq) {
		final String methodName = "createXlsFileFormatPassbook";
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
			reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
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
				//Condition to display dynamic header column value as Mobile Number or Login ID base on request Type
				if(listDiplayColumns.get(col).getColumnName().trim().equals(PinPassHistDownloadColumns.MSISDN_LOGINID.getColumnName())) {
				   if(downloadDataFomatReq.getInputParamMap().get(PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_LABEL_REQTYPE.getReportValues()).equals(PretupsI.USER_PASSWORD_MANAGEMENT)) {
					   headercell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_COL_HEADER_LOGIN_ID.getReportValues(), null));	   
				   }else {
					  headercell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_COL_HEADER_MOBILE_NUM.getReportValues(), null));	
				   }
				}else {
					headercell.setCellValue(displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));	
				}
				headercell.setCellStyle(headerCellStyle);
		  }
			
		
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				PinPassHistSearchRecordVO record = (PinPassHistSearchRecordVO) dataList.get(i);
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
			workbook.write(outputStream);
			fileData = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

		} catch (IOException ie) {
			if (_log.isDebugEnabled()) {
				_log.debug("Exception occured while filling cell value", ie);
			}
		}

		return fileData;
	}

	private Map<String, String> getMappedColumnValue(PinPassHistSearchRecordVO pinPassHistSearchRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(PinPassHistDownloadColumns.USER_NAME.getColumnName(),
				pinPassHistSearchRecordVO.getUserName());
		mappedColumnValue.put(PinPassHistDownloadColumns.MSISDN_LOGINID.getColumnName(),
				pinPassHistSearchRecordVO.getMsisdnOrLoginID());
		mappedColumnValue.put(PinPassHistDownloadColumns.MODIFIED_BY.getColumnName(),
				pinPassHistSearchRecordVO.getMoidifiedBy());
		mappedColumnValue.put(PinPassHistDownloadColumns.MOIDFIED_ON.getColumnName(),
				pinPassHistSearchRecordVO.getModifiedOn());
		return mappedColumnValue;
}

	public void execute(Connection con,PinPassHistoryReqDTO pinPassHistoryReqDTO, PinPassHistDownloadResp response) throws BTSLBaseException {
	
		final String methodName = "execute";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}


		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		NetworkProductDAO networkProductDAO = null;
		ChannelUserVO channelUserVO = null;
		
		Date currentDate = new Date();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> lhtValidEditColumns = Arrays.asList(PinPassHistDownloadColumns.values()).stream()
					.collect(Collectors.toMap(PinPassHistDownloadColumns::getColumnName,
							PinPassHistDownloadColumns::getColumnName));
			super.validateEditColumns(pinPassHistoryReqDTO.getDispHeaderColumnList(), lhtValidEditColumns);
			
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			networkProductDAO = new NetworkProductDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, pinPassHistoryReqDTO.getMsisdn());
			String fromDate = pinPassHistoryReqDTO.getFromDate();
			String toDate = pinPassHistoryReqDTO.getToDate();
			String extNgCode = pinPassHistoryReqDTO.getExtnwcode();
			//execute search api to get data.
					String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();
			
/*	
	 if ( !BTSLUtil.isEmpty(pinPassHistoryReqDTO.getFileType()) && !pinPassHistoryReqDTO.getFileType().trim().toUpperCase().equals(allowedFileType.toUpperCase())){
					throw new BTSLBaseException("PretupsUIReportsController", "Pin Password history download",
							PretupsErrorCodesI.INVALID_FILE_FORMAT, 0, null);
			 } */ 
			
			List<PinPassHistSearchRecordVO> listPinPassHistSearchRecordVO =searchPinPassHist(con, pinPassHistoryReqDTO); 

			 if(listPinPassHistSearchRecordVO!=null && listPinPassHistSearchRecordVO.size()==0   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			
			String fileName = RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsRptUIConsts.PINPASSHISTDOWNLOAD_RPT_FILENAME.getReportValues(), null);
			downloadDataFomatReq.setFileName(
					fileName + System.currentTimeMillis());
			downloadDataFomatReq.setFileType(pinPassHistoryReqDTO.getFileType());
			downloadDataFomatReq.setReportDataList(listPinPassHistSearchRecordVO);
			downloadDataFomatReq.setDisplayListColumns(pinPassHistoryReqDTO.getDispHeaderColumnList());
			downloadDataFomatReq.setSearchInputMaprowCell(getSearchInputValueMap(con,pinPassHistoryReqDTO, channelUserVO));

			PinPassHistDownloadColumns [] pinpassdownloadcolumsequenceArr = PinPassHistDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (PinPassHistDownloadColumns pphcolumsequence : pinpassdownloadcolumsequenceArr) {
				reportColumnSeq.append(pphcolumsequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);
			
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			downloadDataFomatReq.setLocale(pinPassHistoryReqDTO.getLocale());
			String fileData = createFileFormatPinPassHist(downloadDataFomatReq);
			response.setFileData(fileData);
			response.setFileType(downloadDataFomatReq.getFileType().toLowerCase());
			response.setFileName(downloadDataFomatReq.getFileName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(pinPassHistoryReqDTO.getLocale(), PretupsErrorCodesI.SUCCESS,
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

	}

}