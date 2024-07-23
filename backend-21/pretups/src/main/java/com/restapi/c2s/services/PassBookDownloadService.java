package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.PBDownloadReqdata;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOtherDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchInputVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookSearchRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductCache;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;


/**
 * 
 * @author Subesh KCV
 *
 */
@Service("PassBookDownloadService")
public class PassBookDownloadService extends CommonService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	private static Map<String,ProductVO> productMap =null;
	Map<String,String> reportInputKeyValMap = new HashMap<String,String>();

	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(PBDownloadReqdata pbDownloadReqdata,
			ChannelUserVO channelUserVO) {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.ONE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pbDownloadReqdata.getLocale(),"PinPassHistoryDownload.sheet.cell.label.userName",null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.ONE.getNumValue()),
				new SearchInputDisplayinRpt(channelUserVO.getUserName(), PretupsRptUIConsts.ONE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pbDownloadReqdata.getLocale(),"PinPassHistoryDownload.sheet.cell.label.fromDate",null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(pbDownloadReqdata.getFromDate(), PretupsRptUIConsts.ONE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pbDownloadReqdata.getLocale(),"PinPassHistoryDownload.sheet.cell.label.toDate",null), PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(pbDownloadReqdata.getToDate(), PretupsRptUIConsts.FOUR.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(pbDownloadReqdata.getLocale(),"PinPassHistoryDownload.sheet.cell.label.networkCode",null), PretupsRptUIConsts.ZERO.getNumValue()));
	
		String productName="ALL";
		if(pbDownloadReqdata.getProductCode()!=null && !pbDownloadReqdata.getProductCode().trim().equals(PretupsI.ALL) ) {
			ProductVO producVO=productMap.get(pbDownloadReqdata.getProductCode());
			 if(producVO!=null){
				 productName=producVO.getProductName();
			 }
		}
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(productName, PretupsRptUIConsts.ONE.getNumValue()));
		NetworkVO networkVO =	 (NetworkVO) NetworkCache.getObject(pbDownloadReqdata.getExtnwcode());
		
		String networkName="ALL";
		 if(networkVO!=null){
			 networkName=networkVO.getNetworkName();
		 }
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()), new SearchInputDisplayinRpt(
				PretupsRptUIConsts.NETWORK_CODE.getReportValues(), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(networkName, PretupsRptUIConsts.ONE.getNumValue()));
		
		//For CSV input values.
		reportInputKeyValMap.put(PretupsRptUIConsts.USER_NAME.getReportValues(), channelUserVO.getUserName());
    	reportInputKeyValMap.put(PretupsRptUIConsts.FROM_DATE.getReportValues(), String.valueOf(pbDownloadReqdata.getFromDate()));
		reportInputKeyValMap.put(PretupsRptUIConsts.TO_DATE.getReportValues(), String.valueOf(pbDownloadReqdata.getToDate()));
		reportInputKeyValMap.put(PretupsRptUIConsts.PRODUCT_CODE.getReportValues(), productName);
		reportInputKeyValMap.put(PretupsRptUIConsts.NETWORK_CODE.getReportValues(), networkName);

		
		return mapMultipleColumnRow;
	}

//public static String createFileFormat(String fileType,List<? extends Object> dataList , MultiValuedMap<String, SearchInputDisplayinRpt> SearchInputMaprowCell ) {
	private String createFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {
		String fileData = null;
		if (PretupsI.FILE_CONTENT_TYPE_XLS.equals(downloadDataFomatReq.getFileType().toUpperCase())  || PretupsI.FILE_CONTENT_TYPE_XLSX.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			fileData = createXlsFileFormatPassbook(downloadDataFomatReq);
		}else if(PretupsI.FILE_CONTENT_TYPE_CSV.equals(downloadDataFomatReq.getFileType().toUpperCase())) {
			Map<String, String> map = new LinkedHashMap<>();
			fileData=createCSVFileFormatPassbook(downloadDataFomatReq);
		}
		return fileData;
	}

	private String createCSVFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		final String methodName ="createCSVFileFormatPassbook";
		String fileData=null;
		
		try(StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer,
						CSVWriter.DEFAULT_SEPARATOR,CSVWriter.NO_QUOTE_CHARACTER,CSVWriter.DEFAULT_ESCAPE_CHARACTER,CSVWriter.DEFAULT_LINE_END)){
			
			String[] reportheadervalue = new String[1];
			reportheadervalue[0]="                   " + RestAPIStringParser.getMessage(locale,"PinPassHistoryDownload.file.heading",null);
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = {""};
			csvWriter.writeNext(blankLine);
			
			//Map<String,String> inputParamMap = downloadDataFomatReq.getInputParamMap();
			
			String[] inputRow1 = { PretupsRptUIConsts.USER_NAME.getReportValues() +  " : "  +reportInputKeyValMap.get(PretupsRptUIConsts.USER_NAME.getReportValues())   };
			csvWriter.writeNext(inputRow1);
			
			String[] inputRow2 = { PretupsRptUIConsts.FROM_DATE.getReportValues() +  " : "  +reportInputKeyValMap.get(PretupsRptUIConsts.FROM_DATE.getReportValues()) + "     "  +   PretupsRptUIConsts.TO_DATE.getReportValues() +  " : "  +reportInputKeyValMap.get(PretupsRptUIConsts.TO_DATE.getReportValues())  };
			csvWriter.writeNext(inputRow2);
			
			String[] inputRow3 = { PretupsRptUIConsts.PRODUCT_CODE.getReportValues() +  " : "  +reportInputKeyValMap.get(PretupsRptUIConsts.PRODUCT_CODE.getReportValues())   };
			csvWriter.writeNext(inputRow3);
			
			String[] inputRow4 = { PretupsRptUIConsts.NETWORK_CODE.getReportValues() +  " : "  +reportInputKeyValMap.get(PretupsRptUIConsts.NETWORK_CODE.getReportValues())   };
			csvWriter.writeNext(inputRow4);
			
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
					columnHeaderNames[k]= displayColumnMap.get(listDiplayColumns.get(k).getColumnName());
			}
			csvWriter.writeNext(columnHeaderNames);
			
			
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {
				PassbookSearchRecordVO record = (PassbookSearchRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				String[] dataRow = new String[listDiplayColumns.size()];
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					dataRow[col] =mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
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
		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		final String methodName="createXlsFileFormatPassbook";
		String fileData = null;

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
			reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(locale,"PinPassHistoryDownload.file.heading",null));
			reportHeadingCell.setCellStyle(headerCellStyle);
			sheet.addMergedRegion(new CellRangeAddress(PretupsRptUIConsts.ZERO.getNumValue(),PretupsRptUIConsts.ZERO.getNumValue(),PretupsRptUIConsts.ZERO.getNumValue(),PretupsRptUIConsts.FOUR.getNumValue()));
			
			lastRowValue=lastRowValue+2;
			
			for (String strRow : downloadDataFomatReq.getSearchInputMaprowCell().keySet()) {
				List<SearchInputDisplayinRpt> listRowSearchInput = (List<SearchInputDisplayinRpt>) downloadDataFomatReq
						.getSearchInputMaprowCell().get(strRow); //
				lastRowValue = Integer.parseInt(strRow);
				Row searchInputRow = sheet.createRow(lastRowValue);
				for (SearchInputDisplayinRpt searchInputDisplayinRpt : listRowSearchInput) {
					if (_log.isDebugEnabled()) {
						_log.debug(methodName, searchInputDisplayinRpt.getCellNo()  + "->" + searchInputDisplayinRpt.getFillValue());

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
			String[] columSeqArr = downloadDataFomatReq.getColumnSequenceNames().split(",");

			List<? extends Object> dataList = downloadDataFomatReq.getReportDataList();
			List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
			
			// Display report column headers
			lastRowValue=lastRowValue+1;
			Row headerRow = sheet.createRow(lastRowValue);
			for (int col = 0; col < listDiplayColumns.size(); col++) {
					Cell headercell = headerRow.createCell(col);
					headercell.setCellValue(displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
					headercell.setCellStyle(headerCellStyle);
			}

			
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				PassbookSearchRecordVO record = (PassbookSearchRecordVO) dataList.get(i);
				Map<String, String> mappedColumnValue = getMappedColumnValue(record);
				lastRowValue=lastRowValue+1;
				Row dataRow = sheet.createRow(lastRowValue);
				for (int col = 0; col < listDiplayColumns.size(); col++) {
					dataRow.createCell(col)
							.setCellValue(mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
				}

			}
			workbook.write(outputStream);
			fileData = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

		} catch (IOException ie) {

		}

		return fileData;
	}

	private Map<String, String> getMappedColumnValue(PassbookSearchRecordVO passbookSearchRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(PassbookOtherDownloadColumns.TRANS_DATE.getColumnName(),
				passbookSearchRecordVO.getTransDate());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PRODUCT_NAME.getColumnName(),
				passbookSearchRecordVO.getProductName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_NAME.getColumnName(),
				passbookSearchRecordVO.getUserName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_MOB_NUM.getColumnName(),
				passbookSearchRecordVO.getUserMobilenumber());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_CATEGORY.getColumnName(),
				passbookSearchRecordVO.getUserCategory());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_GEOGRAPHY.getColumnName(),
				passbookSearchRecordVO.getUserGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.EXTERNAL_CODE.getColumnName(),
				passbookSearchRecordVO.getExternalCode());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_NAME.getColumnName(),
				passbookSearchRecordVO.getParentName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_MOB_NUM.getColumnName(),
				passbookSearchRecordVO.getParentMobilenumber());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_CATEGORY.getColumnName(),
				passbookSearchRecordVO.getParentCategory());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_GEOGRAPHY.getColumnName(),
				passbookSearchRecordVO.getParentGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_NAME.getColumnName(),
				passbookSearchRecordVO.getOwnerName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_MOBILE_NUMBER.getColumnName(),
				passbookSearchRecordVO.getOwnerMobileNumber());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_CATEGORY.getColumnName(),
				passbookSearchRecordVO.getOwnerCategory());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_GEOGRAPHY.getColumnName(),
				passbookSearchRecordVO.getOwnerGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_TRANSFER_COUNT.getColumnName(),
				passbookSearchRecordVO.getO2cTransferCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_TRANSFER_AMOUNT.getColumnName(),
				passbookSearchRecordVO.getO2cTransferAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_RETURN_COUNT.getColumnName(),
				passbookSearchRecordVO.getO2cReturnCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_RETURN_AMOUNT.getColumnName(),
				passbookSearchRecordVO.getO2cReturnAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_WITHDRAWAL_COUNT.getColumnName(),
				passbookSearchRecordVO.getO2cWithdrawCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_WITHDRAWAL_AMOUNT.getColumnName(),
				passbookSearchRecordVO.getO2cWithdrawAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_INCOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransfer_InCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_INAMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransfer_InAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_OUTCOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransfer_OutCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_OUTAMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransfer_OutAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_INCOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferRet_InCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_INAMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferRet_InAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_OUTCOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferRet_OutCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_OUTAMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferRet_OutAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_INCOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferWithdraw_InCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_INAMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferWithdraw_InAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_OUTCOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferWithdraw_OutCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_OUTAMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2cTransferWithdraw_OutAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_TRANSFER_COUNT.getColumnName(),
				passbookSearchRecordVO.getC2sTransfer_count());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_TRANSFER_AMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2sTransfer_amount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_REVERSAL_COUNT.getColumnName(),
				passbookSearchRecordVO.getC2sReveral_count());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_REVERSAL_AMOUNT.getColumnName(),
				passbookSearchRecordVO.getC2sReveral_amount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.ADDITIONAL_COMM_AMOUNT.getColumnName(),
				passbookSearchRecordVO.getAdditionalcommissionAmount());
		
		mappedColumnValue.put(PassbookOtherDownloadColumns.RECON_STATUS.getColumnName(),
				passbookSearchRecordVO.getReconStatus());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OPENING_BALANCE.getColumnName(),
				passbookSearchRecordVO.getOpeningBalance());
		mappedColumnValue.put(PassbookOtherDownloadColumns.CLOSING_BALANCE.getColumnName(),
				passbookSearchRecordVO.getClosingBalance());

		return mappedColumnValue;
	}


	public void execute(PBDownloadReqdata pbDownloadReqdata, PassbookDownloadResp response)
			throws BTSLBaseException {
		// TODO Auto-generated method stub
		final String methodName = "getPassBookSearchInfo";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		UserDAO userDao = null;
		ChannelUserDAO channelUserDAO = null;
		NetworkProductDAO networkProductDAO = null;
		ChannelUserVO channelUserVO = null;
		PassbookSearchInputVO passbookSearchInputVO = new PassbookSearchInputVO();
		Date currentDate = new Date();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> passbookValidEditColumns = Arrays.asList(PassbookOtherDownloadColumns.values()).stream()
					.collect(Collectors.toMap(PassbookOtherDownloadColumns::getColumnName,
							PassbookOtherDownloadColumns::getColumnName));
			super.validateEditColumns(pbDownloadReqdata.getDispHeaderColumnList(), passbookValidEditColumns);
			userDao = new UserDAO();
			channelUserDAO = new ChannelUserDAO();
			networkProductDAO = new NetworkProductDAO();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, pbDownloadReqdata.getMsisdn());
			String fromDate = pbDownloadReqdata.getFromDate();
			String toDate = pbDownloadReqdata.getToDate();
			String extNgCode = pbDownloadReqdata.getExtnwcode();
			String productCode = pbDownloadReqdata.getProductCode();

			Date frDate = new Date();
			Date tDate = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			sdf.setLenient(false);

			frDate = sdf.parse(fromDate + " 00:00:00");
			tDate = sdf.parse(toDate + " 23:59:59");

			if (BTSLUtil.getDifferenceInUtilDates(frDate, currentDate) < 0) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookDownload",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(tDate, currentDate) < 0) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookDownload",
						PretupsErrorCodesI.CCE_ERROR_TODATE_GREATER_THAN_CURRENTDATE, 0, null);
			}
			if (BTSLUtil.getDifferenceInUtilDates(frDate, tDate) < 0) {
				throw new BTSLBaseException("PretupsUIReportsController", "passbookDownload",
						PretupsErrorCodesI.CCE_ERROR_FROMDATE_GREATER_THAN_TODATE, 0, null);
			}
			
			
			String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();
			/*
			 if ( !BTSLUtil.isEmpty(pbDownloadReqdata.getFileType()) && !pbDownloadReqdata.getFileType().trim().toUpperCase().equals(allowedFileType.toUpperCase())){
					throw new BTSLBaseException("PretupsUIReportsController", "passbookDownload",
							PretupsErrorCodesI.INVALID_FILE_FORMAT, 0, null);
			 }*/
			
			
			final String status = "'" + PretupsI.YES + "'";
			HashMap<String, ProductVO> hmap = NetworkProductCache
					.getNetworkProductCacheFromRedis("NetworkProductCache");
			List productList = networkProductDAO.loadProductList(con, "PREPROD", status, PretupsI.C2S_MODULE,
					extNgCode);

			productMap = (Map<String, ProductVO>) productList.stream()
					.collect(Collectors.toMap(ProductVO::getProductCode, productVO -> productVO));
			passbookSearchInputVO.setProductCode(productCode);
			if (productCode != null && !productCode.equals(PretupsI.ALL)) {
				if (!productMap.containsKey(productCode)) {

					throw new BTSLBaseException("PretupsUIReportsController", "passbookSearch",
							PretupsErrorCodesI.VAS_PROMOVAS_REQ_SELECTOR_MISSING, 0, null);
				}
				passbookSearchInputVO.setProductCode(productCode);
			}
			passbookSearchInputVO.setProductCode(productCode);
			passbookSearchInputVO.setFromDate(BTSLDateUtil.getGregorianDate(fromDate));
			passbookSearchInputVO.setToDate(BTSLDateUtil.getGregorianDate(toDate));
			passbookSearchInputVO.setUserId(channelUserVO.getUserID());
			passbookSearchInputVO.setNetworkCode(extNgCode);
			passbookSearchInputVO.setUserId(channelUserVO.getUserID());

			List<PassbookSearchRecordVO> listPassbookSearch = userDao.searchPassbookDetailList(con,
					passbookSearchInputVO);
			
			 if(listPassbookSearch!=null && listPassbookSearch.size()==0   ) {
				 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			downloadDataFomatReq.setFileName(PretupsRptUIConsts.PASSBOOKDOWNLOAD_FILENAME.getReportValues()  + System.currentTimeMillis());
			downloadDataFomatReq.setFileType(pbDownloadReqdata.getFileType());
			downloadDataFomatReq.setReportDataList(listPassbookSearch);
			downloadDataFomatReq.setDisplayListColumns(pbDownloadReqdata.getDispHeaderColumnList());
			downloadDataFomatReq.setSearchInputMaprowCell(getSearchInputValueMap(pbDownloadReqdata, channelUserVO));

			PassbookOtherDownloadColumns[] passbookColumnSequenceArr = PassbookOtherDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (PassbookOtherDownloadColumns passbookColumnSequence : passbookColumnSequenceArr) {
				reportColumnSeq.append(passbookColumnSequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);
			
			
			
			//downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			
			String fileData = createFileFormatPassbook(downloadDataFomatReq);

			response.setFileData(fileData);
			response.setFileType(downloadDataFomatReq.getFileType().toLowerCase());
			response.setFileName(downloadDataFomatReq.getFileName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(pbDownloadReqdata.getLocale(), PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} catch (Exception ex) {
			_log.errorTrace(methodName, ex);
			if(ex instanceof BTSLBaseException) {
			throw new BTSLBaseException(PretupsUIReportsController.class.getName(), methodName, ex.getMessage(),
					((BTSLBaseException) ex).getArgs());
			}
		}
		
		finally {
			if (mcomCon != null) {
				mcomCon.close("PassbookDetailsController#execute");
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