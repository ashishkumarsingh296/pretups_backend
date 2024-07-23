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
import com.btsl.common.OfflineReportStatus;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommisionRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.EventObjectData;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOtherDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersRespDTO;
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
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OfflineReportDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("PassbookOthersProcessor")
public class PassbookOthersProcessor extends CommonService implements PretupsBusinessServiceI {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	private HashMap<String,String > totSummaryColCapture =  new HashMap<String,String>();
	private HashMap<String,String > totSummaryColValue =  new HashMap<String,String>();

//	public C2StransferCommRespDTO searchC2STransferCommission(C2STransferCommReqDTO c2STransferCommReqDTO,DownloadDataFomatReq downloadDataFomatReq)
//			throws BTSLBaseException {
//		final String methodName = "searchC2STransferCommission";
//		if (_log.isDebugEnabled()) {
//			_log.debug(methodName, "Entered method searchC2STransferCommission with following parameters");
//			_log.debug(methodName, c2STransferCommReqDTO.toString());
//
//		}
//		Connection con = null;
//		MComConnectionI mcomCon = null;
//		C2STransferTxnDAO c2STransferTxnDAO = null;
//		C2StransferCommRespDTO c2StransferCommRespDTO = null;
//		try {
//			mcomCon = new MComConnection();
//			con = mcomCon.getConnection();
//			c2STransferTxnDAO = new C2STransferTxnDAO();
//			downloadDataFomatReq.setCheckDataExistforFilterRequest(true);
//			c2StransferCommRespDTO = c2STransferTxnDAO.searchC2STransferCommissionData(con, c2STransferCommReqDTO, downloadDataFomatReq,null,null,null,null);
//
//			if (c2StransferCommRespDTO != null && Long.valueOf(c2StransferCommRespDTO.getTotalDownloadedRecords()) == 0l) {
//				throw new BTSLBaseException("C2STransferCommReportProcessor", methodName,
//						PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
//			}
//		} catch (BTSLBaseException be) {
//			_log.errorTrace(methodName, be);
//			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, be.getMessage(),
//					be.getArgs());
//		} catch (Exception ex) {
//			_log.errorTrace(methodName, ex);
//			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage());
//		} finally {
//			try {
//				if (mcomCon != null) {
//					mcomCon.close("C2STransferCommReportProcessor");
//					mcomCon = null;
//				}
//			} catch (Exception e) {
//				_log.errorTrace(methodName, e);
//			}
//
//			try {
//				if (con != null) {
//					con.close();
//				}
//			} catch (Exception e) {
//				_log.errorTrace(methodName, e);
//			}
//		}
//
//		return c2StransferCommRespDTO;
//	}

	
	public HashMap<String,String> validateInputs(Connection con, PassbookOthersReqDTO passbookOthersReqDTO) throws BTSLBaseException {
		final String methodName = "validateInputs";
		Date currentDate = new Date();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();
		HashMap<String,String> reportInputValues = new HashMap<String,String>(); 

		String fromDate = passbookOthersReqDTO.getFromDate();
		String toDate = passbookOthersReqDTO.getToDate();
		String networkCode = passbookOthersReqDTO.getNetworkCode();

		Date frDate = new Date();
		Date tDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
		sdf.setLenient(false);

		try {
			frDate = sdf.parse(fromDate+PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues());
		} catch (ParseException e) {
			throw new BTSLBaseException("PretupsUIReportsController", "Passbookothers",
					PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED, 0, null);
		}

		try {
			tDate = sdf.parse(toDate + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues());
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
		
		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), passbookOthersReqDTO.getFromDate());
		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), passbookOthersReqDTO.getToDate());
		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_NetworkCode.getReportValues(), PretupsI.ALL);
		if (networkCode != null && !networkCode.trim().equals(PretupsI.ALL.trim())) {
			NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(networkCode);
			if (BTSLUtil.isNullObject(networkVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
			reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_NetworkCode.getReportValues(), networkVO.getNetworkName());
		}
		
		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), PretupsI.ALL);
		if (passbookOthersReqDTO.getCategoryCode() != null
				&& !passbookOthersReqDTO.getCategoryCode().trim().equals(PretupsI.ALL)) {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					passbookOthersReqDTO.getCategoryCode());
			if (BTSLUtil.isNullObject(categoryVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
			}
			reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), categoryVO.getCategoryName());
		}

		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), PretupsI.ALL);
		if (passbookOthersReqDTO.getDomain() != null
				&& !passbookOthersReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, passbookOthersReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), domainVO.getDomainName());
		}
		
		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues(), PretupsI.ALL);
		if(BTSLUtil.isNullString(passbookOthersReqDTO.getGeography())  || (passbookOthersReqDTO.getGeography()!=null && !passbookOthersReqDTO.getGeography().trim().equals(PretupsI.ALL))) {
		    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
		    if (!geoDAO.isGeographicalDomainExist(con, passbookOthersReqDTO.getGeography(), true)) {
		 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
		    }
		    reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues(),passbookOthersReqDTO.getGeography());
  	  }
		final String status = "'" + PretupsI.YES + "'";
       NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		List productList = networkProductDAO.loadProductList(con, "PREPROD", status, PretupsI.C2S_MODULE,
				passbookOthersReqDTO.getNetworkCode());
   
	 Map<String, ProductVO>	productMap = (Map<String, ProductVO>) productList.stream()
				.collect(Collectors.toMap(ProductVO::getProductCode, productVO -> productVO));
		if (passbookOthersReqDTO.getProduct() != null && !passbookOthersReqDTO.getProduct().equals(PretupsI.ALL)) {
			if (!productMap.containsKey(passbookOthersReqDTO.getProduct() )) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
						PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING, 0, null);
			}
			ProductVO prodVO =productMap.get(passbookOthersReqDTO.getProduct());
			reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT.getReportValues(),prodVO.getProductName());	
		}else {
			reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT.getReportValues(),PretupsI.ALL);
		}
		
		reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER.getReportValues(),PretupsI.ALL);
		if(passbookOthersReqDTO.getUser()!=null && !passbookOthersReqDTO.getUser().equals(PretupsI.ALL)) {
			UserDAO userDAO = new UserDAO();
			ChannelUserVO channelUserVO = userDAO.loadUserDetailsFormUserID(con, passbookOthersReqDTO.getUser());
			reportInputValues.put(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER.getReportValues(),channelUserVO.getUserName());	
			}
		
		return reportInputValues;

	}
	
	
	
	
	

		

	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,
			PassbookOthersReqDTO passbookOthersReqDTO, ChannelUserVO channelUserVO,HashMap<String, String> reportInputParams) throws BTSLBaseException {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();
		
		Date currentDate = new Date();
		String reportDate =BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(), PretupsRptUIConsts.REPORT_DATE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(reportDate, PretupsRptUIConsts.ONE.getNumValue()));


		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null),
						PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_TODATE.getReportValues()), PretupsRptUIConsts.FOUR.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
	
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
									PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
					new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));


		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		


		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()), new SearchInputDisplayinRpt(
				reportInputParams.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_ReconStatus.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		String description=RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(), "passbookothersdownload.sheet.cell.label.desc", null);
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.NINE.getNumValue()), new SearchInputDisplayinRpt(
				description, PretupsRptUIConsts.ONE.getNumValue()));
		
		return mapMultipleColumnRow;
	}

	private String createFileFormatC2STransfComm(Connection con,DownloadDataFomatReq downloadDataFomatReq,PassbookOthersReqDTO passbookOthersReqDTO) throws BTSLBaseException {
		String fileData = null;
		if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())
				|| PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			//fileData = createXlsFileFormat(con,downloadDataFomatReq,passbookOthersReqDTO);
		} else if (PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			Map<String, String> map = new LinkedHashMap<>();
			//fileData = createCSVFileFormat(con,downloadDataFomatReq,passbookOthersReqDTO);
		}
		return fileData;
	}

	/*
	private String createCSVFileFormat(Connection con,DownloadDataFomatReq downloadDataFomatReq,PassbookOthersReqDTO passbookOthersReqDTO) throws BTSLBaseException {

		final String methodName = "createCSVFileFormatPassbook";
		String fileData = null;
		C2STransferTxnDAO c2STransferTxnDAO = new C2STransferTxnDAO();

		try (StringWriter writer = new StringWriter();
				CSVWriter csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER,
						CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)) {

			String[] reportheadervalue = new String[1];
			
			 
			reportheadervalue[0] ="                    " +RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null);
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

			Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();
			
			Date currentDate = new Date();
			String reportDate =BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));
			downloadDataFomatReq.setCheckDataExistforFilterRequest(false);
			  
			
			

			String[] inputRow1 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.REPORT_DATE.getReportValues(),
					null) + " : "
					+ reportDate };
			csvWriter.writeNext(inputRow1);

			String[] inputRow2 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues())
					+ "     "
					+ RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
							PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null)
					+ " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_TODATE.getReportValues()) };
			csvWriter.writeNext(inputRow2);

			String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()) };
			csvWriter.writeNext(inputRow4);

			String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()) };
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
				totSummaryColCapture.put(listDiplayColumns.get(k).getColumnName(), String.valueOf(k));
			}
			csvWriter.writeNext(columnHeaderNames);
			C2StransferCommRespDTO c2StransferCommRespDTO=	c2STransferTxnDAO.searchC2STransferCommissionData(con, c2STransferCommReqDTO, downloadDataFomatReq, null, null, csvWriter,null);
			if(c2StransferCommRespDTO.getTotalDownloadedRecords()!=null && Long.valueOf(c2StransferCommRespDTO.getTotalDownloadedRecords())<=0 ) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}    
			/*
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {
				C2StransferCommisionRecordVO record = (C2StransferCommisionRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				String[] dataRow = new String[listDiplayColumns.size()];
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
				}
				csvWriter.writeNext(dataRow);
			} 
			
			//To display summary coloumns
			for (int k = 0; k < listDiplayColumns.size(); k++) {
				 if(totSummaryColValue.containsKey(listDiplayColumns.get(k).getColumnName())) {
					 columnHeaderNames[k] = totSummaryColValue.get(listDiplayColumns.get(k).getColumnName());	 
				 }else {
					 columnHeaderNames[k] ="";
				 }
				
				
			}
			csvWriter.writeNext(columnHeaderNames);
			
			

			String output = writer.toString();
			fileData = new String(Base64.getEncoder().encode(output.getBytes()));
		} catch (IOException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exception occured while generating CSV file for Passbook download ");

			}
		}finally {
			
		}

		return fileData;
	}
	
	*/
/*
	private String createXlsFileFormat(Connection con,DownloadDataFomatReq downloadDataFomatReq,C2STransferCommReqDTO c2STransferCommReqDTO) throws BTSLBaseException {
		final String methodName = "createXlsFileFormat";
		String fileData = null;
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		C2STransferTxnDAO c2STransferTxnDAO = new C2STransferTxnDAO();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet(downloadDataFomatReq.getFileName());

			sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
			sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
			sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());

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
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
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
			for (String strRow : downloadDataFomatReq.getSearchInputMaprowCell().keySet()) {
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
		/*	
			for (int i = 0; i < dataList.size(); i++) {

				C2StransferCommisionRecordVO record = (C2StransferCommisionRecordVO) dataList.get(i);
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
			downloadDataFomatReq.setContinueLastRow(Long.valueOf(lastRowValue));
			C2StransferCommRespDTO c2StransferCommRespDTO=	c2STransferTxnDAO.searchC2STransferCommissionData(con, c2STransferCommReqDTO, downloadDataFomatReq, workbook,sheet, null,outputStream);
			if(c2StransferCommRespDTO.getTotalDownloadedRecords()!=null && Long.valueOf(c2StransferCommRespDTO.getTotalDownloadedRecords())<=0 ) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}   
			
			totSummaryColValue.put(C2STransferCommDownloadColumns.BONUS.getColumnName(), c2StransferCommRespDTO.getC2StransferCommSummryData().getTotalbonusAmount());
			totSummaryColValue.put(C2STransferCommDownloadColumns.REQUESTED_AMOUNT.getColumnName(), c2StransferCommRespDTO.getC2StransferCommSummryData().getTotalRequestedAmount());
			totSummaryColValue.put(C2STransferCommDownloadColumns.CREDITED_AMOUNT.getColumnName(), c2StransferCommRespDTO.getC2StransferCommSummryData().getTotalCreditedAmount());
			totSummaryColValue.put(C2STransferCommDownloadColumns.TRANSFER_AMOUNT.getColumnName(), c2StransferCommRespDTO.getC2StransferCommSummryData().getTotalTransferAmount());

			lastRowValue=c2StransferCommRespDTO.getLastRecordNo().intValue();
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
	        outputStream.flush();
	        workbook.write(outputStream);
			fileData = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

		} catch (IOException ie) {
			if (_log.isDebugEnabled()) {
				_log.debug("Exception occured while filling cell value", ie);
			}
		}

		return fileData;
	}
	
	*/

	private Map<String, String> getMappedColumnValue(C2StransferCommisionRecordVO c2StransferCommisionRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.TRANS_DATETIME.getColumnName(),
				c2StransferCommisionRecordVO.getTransdateTime());
		mappedColumnValue.put(C2STransferCommDownloadColumns.TRANSACTION_ID.getColumnName(),
				c2StransferCommisionRecordVO.getTransactionID());
		mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_MOBILE_NUM.getColumnName(),
				c2StransferCommisionRecordVO.getSenderMobileNumber());
		mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_NAME.getColumnName(),
				c2StransferCommisionRecordVO.getSenderName());

		mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_CATEGORY.getColumnName(),
				c2StransferCommisionRecordVO.getSenderCategory());
		mappedColumnValue.put(C2STransferCommDownloadColumns.RECEIVER_MOB_NUM.getColumnName(),
				c2StransferCommisionRecordVO.getReceiverMobileNumber());
		mappedColumnValue.put(C2STransferCommDownloadColumns.SERVICE.getColumnName(),
				c2StransferCommisionRecordVO.getService());
		mappedColumnValue.put(C2STransferCommDownloadColumns.REQUESTED_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getRequestedAmount());

		
		mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_GEOGRAPHY.getColumnName(),
				c2StransferCommisionRecordVO.getSenderGeography());
		/*mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_NETWORK_CODE.getColumnName(),
				c2StransferCommisionRecordVO.getsen);*/
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.PARENT_MOBILE_NUMBER.getColumnName(),
				c2StransferCommisionRecordVO.getParentMobileNumber());
		

		mappedColumnValue.put(C2STransferCommDownloadColumns.PARENT_NAME.getColumnName(),
				c2StransferCommisionRecordVO.getParentName());
		mappedColumnValue.put(C2STransferCommDownloadColumns.PARENT_CATEGORY.getColumnName(),
				c2StransferCommisionRecordVO.getParentCategory());
		mappedColumnValue.put(C2STransferCommDownloadColumns.PARENT_GEOGRAPHY.getColumnName(),
				c2StransferCommisionRecordVO.getParentGeography());
		mappedColumnValue.put(C2STransferCommDownloadColumns.OWNER_MOBILE_NUMBER.getColumnName(),
				c2StransferCommisionRecordVO.getOwnerMobileNumber());

		mappedColumnValue.put(C2STransferCommDownloadColumns.OWNER_NAME.getColumnName(),
				c2StransferCommisionRecordVO.getOwnerName());
		mappedColumnValue.put(C2STransferCommDownloadColumns.OWNER_CATEGORY.getColumnName(),
				c2StransferCommisionRecordVO.getOwnerCategory());
		mappedColumnValue.put(C2STransferCommDownloadColumns.OWNER_GEOGRAPHY.getColumnName(),
				c2StransferCommisionRecordVO.getOwnerGeography());
		mappedColumnValue.put(C2STransferCommDownloadColumns.RECEIVER_SERVICECLASS.getColumnName(),
				c2StransferCommisionRecordVO.getReceiverServiceClass());

		/*
		mappedColumnValue.put(C2STransferCommDownloadColumns.PRODUCT_NAME.getColumnName(),
				c2StransferCommisionRecordVO.getProductName());*/
		/*mappedColumnValue.put(C2STransferCommDownloadColumns.SERVICES.getColumnName(),
				c2StransferCommisionRecordVO.getServices());*/
		mappedColumnValue.put(C2STransferCommDownloadColumns.SUB_SERVICE.getColumnName(),
				c2StransferCommisionRecordVO.getSubService());
		mappedColumnValue.put(C2STransferCommDownloadColumns.REQUEST_SOURCE.getColumnName(),
				c2StransferCommisionRecordVO.getReceiverServiceClass());

		mappedColumnValue.put(C2STransferCommDownloadColumns.CREDITED_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getCreditedAmount());
		mappedColumnValue.put(C2STransferCommDownloadColumns.VOUCHER_SERIAL_NUMBER.getColumnName(),
				c2StransferCommisionRecordVO.getVoucherserialNo());
		mappedColumnValue.put(C2STransferCommDownloadColumns.BONUS.getColumnName(),
				c2StransferCommisionRecordVO.getBonus());
		mappedColumnValue.put(C2STransferCommDownloadColumns.VOUCHER_PIN_SENT_TO.getColumnName(),
				c2StransferCommisionRecordVO.getPinSentTo());

		mappedColumnValue.put(C2STransferCommDownloadColumns.TRANSFER_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getTransferAmount());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.RECEIVER_SERVICECLASS.getColumnName(),
				c2StransferCommisionRecordVO.getReceiverServiceClass());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.REQUEST_SOURCE.getColumnName(),
				c2StransferCommisionRecordVO.getRequestSource());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.TRANSFER_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getTransferAmount());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.REQUESTED_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getRequestedAmount());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.BONUS.getColumnName(),
				c2StransferCommisionRecordVO.getBonus());
		
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.CREDITED_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getCreditedAmount());
		

		mappedColumnValue.put(C2STransferCommDownloadColumns.ROAM_PENALTY.getColumnName(),
				c2StransferCommisionRecordVO.getRoamPenalty());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.PROCESSING_FEE.getColumnName(),
				c2StransferCommisionRecordVO.getProcessingFee());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.EXTERNAL_CODE.getColumnName(),
				c2StransferCommisionRecordVO.getExternalCode());
		mappedColumnValue.put(C2STransferCommDownloadColumns.SERVICE.getColumnName(),
				c2StransferCommisionRecordVO.getService());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.SUB_SERVICE.getColumnName(),
				c2StransferCommisionRecordVO.getSubService());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.VOUCHER_PIN_SENT_TO.getColumnName(),
				c2StransferCommisionRecordVO.getPinSentTo());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.VOUCHER_SERIAL_NUMBER.getColumnName(),
				c2StransferCommisionRecordVO.getVoucherserialNo());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.ADJUSTMENT_TRANS_ID.getColumnName(),
				c2StransferCommisionRecordVO.getAdjustmentTransID());
		
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.COMMISSION_TYPE.getColumnName(),
				c2StransferCommisionRecordVO.getCommissionType());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.ADDITIONAL_COMMISSION.getColumnName(),
				c2StransferCommisionRecordVO.getAdditionalCommission());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.CAC_RATE.getColumnName(),
				c2StransferCommisionRecordVO.getCacRate());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.CAC_TYPE.getColumnName(),
				c2StransferCommisionRecordVO.getCacType());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.CAC_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getCacAmount());
		
		
		
		

		return mappedColumnValue;
	}

	public void execute(PassbookOthersReqDTO passbookOthersReqDTO,PassbookOthersDownloadResp response)
			throws BTSLBaseException {

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

		Date currentDate = new Date();
		String offlineReportExecutionStatus =null;

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> lhtValidEditColumns = Arrays.asList(PassbookOtherDownloadColumns.values()).stream()
					.collect(Collectors.toMap(PassbookOtherDownloadColumns::getColumnName,
							PassbookOtherDownloadColumns::getColumnName));
			super.validateEditColumns(passbookOthersReqDTO.getDispHeaderColumnList(), lhtValidEditColumns);
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			networkProductDAO = new NetworkProductDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, passbookOthersReqDTO.getMsisdn());
			String fromDate = passbookOthersReqDTO.getFromDate();
			String toDate = passbookOthersReqDTO.getToDate();
			//String extNgCode = passbookOthersReqDTO.get
			// execute search api to get data.
			String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();

			/*
			 * if ( !BTSLUtil.isEmpty(pinPassHistoryReqDTO.getFileType()) &&
			 * !pinPassHistoryReqDTO.getFileType().trim().toUpperCase().equals(
			 * allowedFileType.toUpperCase())){ throw new
			 * BTSLBaseException("PretupsUIReportsController",
			 * "Pin Password history download", PretupsErrorCodesI.INVALID_FILE_FORMAT, 0,
			 * null); }
			 */

			HashMap<String,String> rptInputParams =validateInputs(con,passbookOthersReqDTO);
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			String fileName=null;
			 if(passbookOthersReqDTO.isOffline()) {
				 fileName=passbookOthersReqDTO.getFileName();
		downloadDataFomatReq.setFileName(fileName);
			 }else {
			fileName = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
					PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_FILENAME.getReportValues(), null);
			downloadDataFomatReq.setFileName(fileName + System.currentTimeMillis());
			
			 }
			 downloadDataFomatReq.setFileType(passbookOthersReqDTO.getFileType());
			
			downloadDataFomatReq.setDisplayListColumns(passbookOthersReqDTO.getDispHeaderColumnList());
			downloadDataFomatReq
					.setSearchInputMaprowCell(getSearchInputValueMap(con, passbookOthersReqDTO, channelUserVO,rptInputParams));

			PassbookOtherDownloadColumns[] passbookOtherDownloadColumnsattrbs = PassbookOtherDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (PassbookOtherDownloadColumns c2sTcolumsequence : passbookOtherDownloadColumnsattrbs) {
				reportColumnSeq.append(c2sTcolumsequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);

			downloadDataFomatReq.setInputParamMap(rptInputParams);
			downloadDataFomatReq.setLocale(passbookOthersReqDTO.getLocale());
			
			// The purpose of passing downloadDataFomatReq, is for OFFline , instead of getting million records in List and iterating, 
			// we can avoid iterating and directly write csv or Excel, during fetch each record.
			C2STransferTxnDAO c2STransferTxnDAO = new C2STransferTxnDAO();
			PassbookOthersRespDTO passbookOthersRespDTO=null;
			 String totalnumberOfRecords =null;
			if(!passbookOthersReqDTO.isOffline()) {
				passbookOthersRespDTO =c2STransferTxnDAO.downloadPassbookOthersOffline(con, passbookOthersReqDTO, downloadDataFomatReq);
				 if(passbookOthersRespDTO!=null) {
				 totalnumberOfRecords=passbookOthersRespDTO.getTotalDownloadedRecords();
				 response.setFileData(passbookOthersRespDTO.getOnlineFileData());
				 
				  response.setFileName(downloadDataFomatReq.getFileName());
				  response.setFileType(downloadDataFomatReq.getFileType());
				  response.setFilePath(passbookOthersRespDTO.getOnlineFilePath());
				  if(passbookOthersRespDTO.isNoDataFound()) {
					  //offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_NODATA;
					  response.setStatus(success);
						response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
						String resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsErrorCodesI.NO_RECORDS_FOUND, null);
						response.setMessage(resmsg);
				  }else {
					  response.setTotalRecords(totalnumberOfRecords);
					  response.setStatus(success);
						response.setMessageCode(PretupsErrorCodesI.SUCCESS);
						String resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsErrorCodesI.SUCCESS, null);
						response.setMessage(resmsg);
				  }
				 }
				//response.setFileData(fileData);
				response.setFileType(downloadDataFomatReq.getFileType().toLowerCase());
				response.setFileName(downloadDataFomatReq.getFileName());
				 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
				response.setStatus(success);
				response.setMessageCode(PretupsErrorCodesI.SUCCESS);
				String resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
						PretupsErrorCodesI.SUCCESS, null);
				response.setMessage(resmsg);
				
			} else {
				// offline DataWriting  Processing start....
				
				OfflineReportDAO offlineReportDAO = new OfflineReportDAO();
				 success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
				 
				 offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_INPROGRESS;
				try {
					passbookOthersRespDTO =c2STransferTxnDAO.downloadPassbookOthersOffline(con, passbookOthersReqDTO, downloadDataFomatReq);
					 if(passbookOthersRespDTO!=null) {
					 totalnumberOfRecords=passbookOthersRespDTO.getTotalDownloadedRecords();
					  if(passbookOthersRespDTO.isNoDataFound()) {
						  //offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_NODATA;
						  response.setStatus(success);
							response.setMessageCode(PretupsErrorCodesI.NO_RECORDS_FOUND);
							String resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
									PretupsErrorCodesI.NO_RECORDS_FOUND, null);
							response.setMessage(resmsg);
					  }else {
						  response.setTotalRecords(totalnumberOfRecords);
						  response.setStatus(success);
							response.setMessageCode(PretupsErrorCodesI.SUCCESS);
							String resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
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
					    resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
									PretupsErrorCodesI.OFFLINE_REPORT_CANCELLED, new String[] {String.valueOf(passbookOthersReqDTO.getOfflineReportTaskID())});
					 } else {
					    offlineReportExecutionStatus=PretupsI.OFFLINE_STATUS_FAILED;
					    response.setMessageCode(PretupsErrorCodesI.FAILED);
						resmsg = RestAPIStringParser.getMessage(passbookOthersReqDTO.getLocale(),
								PretupsErrorCodesI.FAILED, null);
					 }
						response.setMessage(resmsg);
						response.setStatus(success);
				}
//				offlineReportDAO.updateOfflineReportTaskStatus(con, offlineReportExecutionStatus,c2STransferCommReqDTO.getOfflineReportTaskID(), totalnumberOfRecords);
				
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

	}

	@Override
	public OfflineReportStatus executeOffineService(EventObjectData srcObj) throws BTSLBaseException {
		PassbookOthersReqDTO passbookOthersReqDTO	=(PassbookOthersReqDTO) srcObj.getRequestData();
		PassbookOthersDownloadResp response = new PassbookOthersDownloadResp();
		passbookOthersReqDTO.setFileName(srcObj.getFileName());
		passbookOthersReqDTO.setOfflineReportTaskID(srcObj.getProcess_taskID());
		_log.info("c2s Offline service", "c2s Offline service executed..");
		_log.debug("c2s Offline service", "c2s Offline service executed.********************************.");
		OfflineReportStatus offlineReportStatus = new OfflineReportStatus();
		try {
		execute(passbookOthersReqDTO,response);
		offlineReportStatus.setMessage(response.getMessage());
		offlineReportStatus.setMessageCode(response.getMessageCode());
		offlineReportStatus.setTotalRecords(response.getTotalRecords());
		
		} catch(Exception ex) {
			_log.error("c2s Offline service", "Error occured in Channel to subcriber Transfer commission offline Report execution. ");
		}
		return offlineReportStatus;
	}

}