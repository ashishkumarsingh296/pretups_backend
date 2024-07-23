package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.LTHDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldReportDTO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.LowThresholdDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.LowThresholdDownloadResp;
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
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;
import com.restapi.c2sservices.service.ReadGenericFileUtil;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("LowThreshHoldRptService")
public class LowThreshHoldRptService extends CommonService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();

	public void getLowThreshHoldReport(LowThreshHoldReportDTO lowThreshHoldReportDTO, LowThreshHoldRptResp response)
			throws BTSLBaseException {

		final String methodName = "getLowThreshHoldReport";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		NetworkProductDAO networkProductDAO = null;
		ChannelUserVO channelUserVO = null;

		

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, lowThreshHoldReportDTO.getMsisdn());
			lowThreshHoldReportDTO.setUserID(channelUserVO.getUserID());
			HashMap<String,String> reportInputKeyValMap= validateInputs(con, lowThreshHoldReportDTO,channelUserVO.getUserName());
			List<LowThreshHoldRecordVO> listLowThreshHoldRecordVO = userDao.getLowThreshHoltRptDetails(con,
					lowThreshHoldReportDTO);
			
			 if(listLowThreshHoldRecordVO!=null && listLowThreshHoldRecordVO.size()==0   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			
			response.setLowThreshHoldDataList(listLowThreshHoldRecordVO);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(lowThreshHoldReportDTO.getLocale(),
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

	
	public HashMap<String,String> validateInputs(Connection con,LowThreshHoldReportDTO lowThreshHoldReportDTO,String userName) throws BTSLBaseException {
		final String methodName ="validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
		Date currentDate = new Date();
	    CategoryDAO categoryDAO = new CategoryDAO();
	    DomainDAO domainDAO = new DomainDAO();
	
		String fromDate = lowThreshHoldReportDTO.getFromDate();
		String toDate = lowThreshHoldReportDTO.getToDate();
		String extNgCode = lowThreshHoldReportDTO.getExtnwcode();

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
		 reportInputKeyValMap.put(PretupsRptUIConsts.NETWORK_CODE.getReportValues(),
				 networkVO.getNetworkName());
		 
  } 
  
  reportInputKeyValMap.put(PretupsRptUIConsts.USER_NAME.getReportValues(), userName);
	reportInputKeyValMap.put(PretupsRptUIConsts.FROM_DATE.getReportValues(),
			String.valueOf(lowThreshHoldReportDTO.getFromDate()));
	reportInputKeyValMap.put(PretupsRptUIConsts.TO_DATE.getReportValues(),
			String.valueOf(lowThreshHoldReportDTO.getToDate()));
	reportInputKeyValMap.put(PretupsRptUIConsts.CATEGORY.getReportValues(),
			 PretupsI.ALL);
  if(lowThreshHoldReportDTO.getCategory()!=null && !lowThreshHoldReportDTO.getCategory().trim().equals(PretupsI.ALL) ) {
		 CategoryVO categoryVO=   categoryDAO.loadCategoryDetailsByCategoryCode(con, lowThreshHoldReportDTO.getCategory());
		 if(BTSLUtil.isNullObject(categoryVO)   ) {
			 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
		 }
		 reportInputKeyValMap.put(PretupsRptUIConsts.CATEGORY.getReportValues(),
				 categoryVO.getCategoryName());
  }	 
  
  if(lowThreshHoldReportDTO.getDomain()!=null && !lowThreshHoldReportDTO.getDomain().trim().equals(PretupsI.ALL)) {
	    DomainVO domainVO =	 domainDAO.loadDomainVO(con, lowThreshHoldReportDTO.getDomain());
	   if(BTSLUtil.isNullObject(domainVO)   ) {
			 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
		 }
	   reportInputKeyValMap.put(PretupsRptUIConsts.DOMAIN.getReportValues(),
			   domainVO.getDomainName());
  }
  
  
  if(lowThreshHoldReportDTO.getGeography()!=null && !lowThreshHoldReportDTO.getGeography().trim().equals(PretupsI.ALL)) {
	    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
	    String geographyName =geoDAO.getGeographyName(con, lowThreshHoldReportDTO.getGeography(), true);
	    
	    if (geographyName==null) {
	 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
	    }
	    reportInputKeyValMap.put(PretupsRptUIConsts.GEOGRAPHY.getReportValues(),
	    		geographyName);
  }
  
  if(BTSLUtil.isNullorEmpty(lowThreshHoldReportDTO.getThreshhold())){
			 throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_THRESHOLD, 0, null);
  }
  
  
  String thresholdInput = lowThreshHoldReportDTO.getThreshhold();
	if(lowThreshHoldReportDTO.getThreshhold().equals(PretupsI.ABOVE_THRESHOLD_TYPE) ) {
		thresholdInput=PretupsRptUIConsts.Above_Threshold.getReportValues();
	}else if(lowThreshHoldReportDTO.getThreshhold().equals(PretupsI.BELOW_THRESHOLD_TYPE) ) {
		thresholdInput=PretupsRptUIConsts.Below_Threshold.getReportValues();
	}else {thresholdInput=PretupsI.ALL;}
	
  reportInputKeyValMap.put(PretupsRptUIConsts.THRESHOLD.getReportValues(),
		  thresholdInput);
  
  
  return reportInputKeyValMap;
		
		
	}
	
	
	
	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,LowThresholdDownloadReqDTO lhtThresholdDownloadReqDTO,
			ChannelUserVO channelUserVO) throws BTSLBaseException {
       final String methodName ="getSearchInputValueMap";
		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.USER_NAME.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(channelUserVO.getUserName(), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.FROM_DATE.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(lhtThresholdDownloadReqDTO.getFromDate(), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.TO_DATE.getReportValues(), PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(lhtThresholdDownloadReqDTO.getToDate(), PretupsRptUIConsts.FOUR.getNumValue()));
		
		CategoryDAO categoryDAO = new CategoryDAO();
		try {
			CategoryVO categoryVO= categoryDAO.loadCategoryDetailsByCategoryCode(con, lhtThresholdDownloadReqDTO.getCategoryCode());
			
			String categoryName="ALL";
			if(categoryVO!=null) {
				categoryName=categoryVO.getCategoryName();
			}
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.CATEGORY.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(categoryName, PretupsRptUIConsts.ONE.getNumValue()));
		
		} catch (BTSLBaseException e) {
			_log.error("LowthreholdReport :: Exception occured while fetching category details", e);
		}
		
		NetworkVO networkVO =	 (NetworkVO) NetworkCache.getObject(lhtThresholdDownloadReqDTO.getExtnwcode());
		String networkName="ALL";
		if(networkVO!=null) {
			networkName=networkVO.getNetworkName();
		}
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.NETWORK_CODE.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(networkName, PretupsRptUIConsts.ONE.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.CATEGORY.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
				new SearchInputDisplayinRpt(PretupsI.ALL, PretupsRptUIConsts.ONE.getNumValue()));
		
		if (lhtThresholdDownloadReqDTO.getCategoryCode() != null
				&& !lhtThresholdDownloadReqDTO.getCategoryCode().trim().equals(PretupsI.ALL)) {
			CategoryVO categoryVO;
			try {
				categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
						lhtThresholdDownloadReqDTO.getCategoryCode());
				if (BTSLUtil.isNullObject(categoryVO)) {
					throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
				}
				mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
						new SearchInputDisplayinRpt(lhtThresholdDownloadReqDTO.getCategoryCode(), PretupsRptUIConsts.ONE.getNumValue()));
			} catch (BTSLBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.GEOGRAPHY.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()), new SearchInputDisplayinRpt(
				PretupsI.ALL, PretupsRptUIConsts.ZERO.getNumValue()));
		if(lhtThresholdDownloadReqDTO.getGeography()!=null && !lhtThresholdDownloadReqDTO.getGeography().trim().equals(PretupsI.ALL)) {
		    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
		    
		    String geographyName =geoDAO.getGeographyName(con, lhtThresholdDownloadReqDTO.getGeography(), true);
			
		    if (geographyName==null) {
		 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
		    }
		    mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
					new SearchInputDisplayinRpt(geographyName, PretupsRptUIConsts.ONE.getNumValue()));
		    
	  }
	
		

		String thresholdInput = lhtThresholdDownloadReqDTO.getThreshold();
		if(lhtThresholdDownloadReqDTO.getThreshold().equals(PretupsI.ABOVE_THRESHOLD_TYPE)) {
			thresholdInput=PretupsRptUIConsts.Above_Threshold.getReportValues();
		}else {
			thresholdInput=PretupsRptUIConsts.Below_Threshold.getReportValues();
		}
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.THRESHOLD.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(thresholdInput, PretupsRptUIConsts.ONE.getNumValue()));
		
		
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

		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] reportheadervalue = new String[1];
			reportheadervalue[0] ="                    " +RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.LOWTHRESHOLDownload_RPT_HEADER_DISPLAYVALUE.getReportValues(), null);
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

			Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

			String[] inputRow1 = { PretupsRptUIConsts.USER_NAME.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.USER_NAME.getReportValues()) };
			csvWriter.writeNext(inputRow1);

			String[] inputRow2 = { PretupsRptUIConsts.FROM_DATE.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.FROM_DATE.getReportValues()) + "     "
					+ PretupsRptUIConsts.TO_DATE.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.TO_DATE.getReportValues()) };
			csvWriter.writeNext(inputRow2);

			String[] inputRow3 = { PretupsRptUIConsts.NETWORK_CODE.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.NETWORK_CODE.getReportValues()) };
			csvWriter.writeNext(inputRow3);
			
			String[] inputRow4 = { PretupsRptUIConsts.CATEGORY.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.CATEGORY.getReportValues()) };
			csvWriter.writeNext(inputRow4);
			
			String[] inputRow5 = { PretupsRptUIConsts.GEOGRAPHY.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.GEOGRAPHY.getReportValues()) };
			csvWriter.writeNext(inputRow5);
			
			String[] inputRow6 = { PretupsRptUIConsts.THRESHOLD.getReportValues() + " : "
					+ inputParamMap.get(PretupsRptUIConsts.THRESHOLD.getReportValues()) };
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
				columnHeaderNames[k] = displayColumnMap.get(listDiplayColumns.get(k).getColumnName());
			}
			csvWriter.writeNext(columnHeaderNames);

			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {
				LowThreshHoldRecordVO record = (LowThreshHoldRecordVO) dataList.get(i);
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

	private String createXlsFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {
		final String methodName = "createXlsFileFormatPassbook";
		String fileData = null;
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Workbook workbook = null;
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			if(PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
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
			reportHeadingCell.setCellValue(PretupsRptUIConsts.LOWTHRESHOLDREPORT_HEADING.getReportValues());
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
		  }
			
		
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				LowThreshHoldRecordVO record = (LowThreshHoldRecordVO) dataList.get(i);
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

	private Map<String, String> getMappedColumnValue(LowThreshHoldRecordVO lowThreshHoldRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(LTHDownloadColumns.USER_NAME.getColumnName(),
				lowThreshHoldRecordVO.getUserName());
		mappedColumnValue.put(LTHDownloadColumns.MOBILE_NUM.getColumnName(),
				lowThreshHoldRecordVO.getMobileNumber());
		// ternaray operator should not used as it will violate in Sonar.
		mappedColumnValue.put(LTHDownloadColumns.USER_STATUS.getColumnName(), lowThreshHoldRecordVO.getUserStatus());
		mappedColumnValue.put(LTHDownloadColumns.DATE_TIME.getColumnName(), lowThreshHoldRecordVO.getDateTime());
		mappedColumnValue.put(LTHDownloadColumns.TRANSACTION_ID.getColumnName(), lowThreshHoldRecordVO.getTransactionID());
		mappedColumnValue.put(LTHDownloadColumns.TRANSFER_TYPE.getColumnName(), lowThreshHoldRecordVO.getTransferType());
		mappedColumnValue.put(LTHDownloadColumns.CATEGORY_NAME.getColumnName(), lowThreshHoldRecordVO.getCategoryName());
		mappedColumnValue.put(LTHDownloadColumns.THRESH_HOLD.getColumnName(), lowThreshHoldRecordVO.getThreshHold());
		mappedColumnValue.put(LTHDownloadColumns.PRODUCT_NAME.getColumnName(), lowThreshHoldRecordVO.getProductName());
		
		String previousBalance = "0";
		if ((lowThreshHoldRecordVO.getPreviousBalance()) != null) {
			previousBalance = String.valueOf(lowThreshHoldRecordVO.getPreviousBalance());
		}
		mappedColumnValue.put(LTHDownloadColumns.PREVIOUS_BALANCE.getColumnName(), previousBalance);

		String currentBalance = "0";
		if (lowThreshHoldRecordVO.getCurrentBalance() != null) {
			currentBalance = String.valueOf(lowThreshHoldRecordVO.getCurrentBalance());
		}
		mappedColumnValue.put(LTHDownloadColumns.CURRENT_BALANCE.getColumnName(), currentBalance);
		mappedColumnValue.put(LTHDownloadColumns.THRESH_HOLD_VALUE.getColumnName(), lowThreshHoldRecordVO.getThresholdValue());
		
		return mappedColumnValue;
}

	public void execute(LowThresholdDownloadReqDTO lhtDownloadReqdata, LowThresholdDownloadResp response) throws BTSLBaseException {
		// TODO Auto-generated method stub
		final String methodName = "execute";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		NetworkProductDAO networkProductDAO = null;
		ChannelUserVO channelUserVO = null;
		
		Date currentDate = new Date();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> lhtValidEditColumns = Arrays.asList(LTHDownloadColumns.values()).stream()
					.collect(Collectors.toMap(LTHDownloadColumns::getColumnName,
							LTHDownloadColumns::getColumnName));
			super.validateEditColumns(lhtDownloadReqdata.getDispHeaderColumnList(), lhtValidEditColumns);
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			networkProductDAO = new NetworkProductDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, lhtDownloadReqdata.getMsisdn());
			String fromDate = lhtDownloadReqdata.getFromDate();
			String toDate = lhtDownloadReqdata.getToDate();
			String extNgCode = lhtDownloadReqdata.getExtnwcode();
			
			
			
			
			//execute search api to get data.
			
			LowThreshHoldReportDTO lowThreshHoldReportDTO = new LowThreshHoldReportDTO();
			lowThreshHoldReportDTO.setCategory(lhtDownloadReqdata.getCategoryCode());
			lowThreshHoldReportDTO.setDomain(lhtDownloadReqdata.getDomain());
			lowThreshHoldReportDTO.setExtnwcode(lhtDownloadReqdata.getExtnwcode());
			lowThreshHoldReportDTO.setGeography(lhtDownloadReqdata.getGeography());
			lowThreshHoldReportDTO.setFromDate(fromDate);
			lowThreshHoldReportDTO.setToDate(toDate);
			lowThreshHoldReportDTO.setUserID(channelUserVO.getUserID());
			lowThreshHoldReportDTO.setThreshhold(lhtDownloadReqdata.getThreshold());
			
			HashMap<String,String> reportInputKeyValMap= validateInputs(con, lowThreshHoldReportDTO,channelUserVO.getUserName());
			
			/*
			 * String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();
			 * 
			 * if ( !BTSLUtil.isEmpty(lhtDownloadReqdata.getFileType()) &&
			 * !lhtDownloadReqdata.getFileType().trim().toLowerCase().equals(allowedFileType
			 * .toUpperCase())){ throw new BTSLBaseException("PretupsUIReportsController",
			 * "Lowthresholddownload", PretupsErrorCodesI.INVALID_FILE_FORMAT, 0, null); }
			 */

			// will throw Exception
			ReadGenericFileUtil readGenericFileUtil  = new ReadGenericFileUtil();
     	    readGenericFileUtil.validateFileType(lhtDownloadReqdata.getFileType().trim().toLowerCase());
			
			
			List<LowThreshHoldRecordVO> listLowThreshHoldSearchData = userDao.getLowThreshHoltRptDetails(con,
					lowThreshHoldReportDTO);

			 if(listLowThreshHoldSearchData!=null && listLowThreshHoldSearchData.size()==0   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			downloadDataFomatReq.setFileName(
					PretupsRptUIConsts.LOWTHRESHOLDDOWNLOAD_FILENAME.getReportValues() + System.currentTimeMillis());
			downloadDataFomatReq.setFileType(lhtDownloadReqdata.getFileType());
			downloadDataFomatReq.setReportDataList(listLowThreshHoldSearchData);
			downloadDataFomatReq.setDisplayListColumns(lhtDownloadReqdata.getDispHeaderColumnList());
			downloadDataFomatReq.setSearchInputMaprowCell(getSearchInputValueMap(con,lhtDownloadReqdata, channelUserVO));
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);

			LTHDownloadColumns [] lhtdownloadcolumsequenceArr = LTHDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (LTHDownloadColumns lhtcolumsequence : lhtdownloadcolumsequenceArr) {
				reportColumnSeq.append(lhtcolumsequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);

			

			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			downloadDataFomatReq.setLocale(lowThreshHoldReportDTO.getLocale());

			String fileData = createFileFormatPassbook(downloadDataFomatReq);

			response.setFileData(fileData);
			response.setFileType(downloadDataFomatReq.getFileType().toLowerCase());
			response.setFileName(downloadDataFomatReq.getFileName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(lhtDownloadReqdata.getLocale(), PretupsErrorCodesI.SUCCESS,
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