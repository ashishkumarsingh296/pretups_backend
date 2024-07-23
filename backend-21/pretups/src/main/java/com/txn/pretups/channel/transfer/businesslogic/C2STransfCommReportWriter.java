/**
 * 
 */
package com.txn.pretups.channel.transfer.businesslogic;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2StransferCommisionRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.BTSLDateUtil;
import com.opencsv.CSVWriter;

/**
 * @author subesh.vasu1
 *
 */
public class C2STransfCommReportWriter {
	protected final Log log = LogFactory.getLog(getClass().getName());

	public void constructCSV(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			C2STransferCommReqDTO c2STransferCommReqDTO) {
		writeInputHeaders(csvWriter, downloadDataFomatReq, c2STransferCommReqDTO);
		writeRowHeadersHeaders(csvWriter, downloadDataFomatReq, c2STransferCommReqDTO);
		// writeCSVRow(csvWriter,downloadDataFomatReq,c2STransferCommReqDTO);
	}

	public HashMap<String, String> constructXLSX(Workbook workbook, Sheet sheet,
			DownloadDataFomatReq downloadDataFomatReq, C2STransferCommReqDTO c2STransferCommReqDTO,
			Integer lastRowValue, CellStyle headerCellStyle) {
		HashMap<String, String> totalSummaryCaptureCols = new HashMap<String, String>();
		int lastRow=0;
		lastRow=writeXLSXInputHeaders(workbook, sheet, downloadDataFomatReq, c2STransferCommReqDTO, lastRowValue);
		totalSummaryCaptureCols = writeCSVRowHeadersHeaders(workbook, sheet, downloadDataFomatReq,
				c2STransferCommReqDTO, lastRow, headerCellStyle);
		return totalSummaryCaptureCols;
	}

	private HashMap<String, String> writeCSVRowHeadersHeaders(Workbook workbook, Sheet sheet,
			DownloadDataFomatReq downloadDataFomatReq, C2STransferCommReqDTO c2STransferCommReqDTO,
			int lastRowValue, CellStyle headerCellStyle) {
		final String methodName = "writeCSVRowHeadersHeaders";
		HashMap<String, String> totSummaryColCapture = new HashMap<String, String>();
		// Displaying Report data rowWise
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Map<String, String> displayColumnMap = downloadDataFomatReq.getDisplayListColumns().stream()
				.collect(Collectors.toMap(DispHeaderColumn::getColumnName, DispHeaderColumn::getDisplayName));
		/*
		 * String[] columSeqArr =
		 * downloadDataFomatReq.getColumnSequenceNames().split(",");
		 */

		//List<? extends Object> dataList = downloadDataFomatReq.getReportDataList();

		// Display report column headers
		lastRowValue = lastRowValue + 1;
		Row headerRow = sheet.createRow(lastRowValue);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Current Row value " + lastRowValue);
		}
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			Cell headercell = headerRow.createCell(col);
			if (log.isDebugEnabled()) {
				log.debug(methodName,
						"Current column " + displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
			}
			headercell.setCellValue(displayColumnMap.get(listDiplayColumns.get(col).getColumnName()));
			headercell.setCellStyle(headerCellStyle);
			totSummaryColCapture.put(listDiplayColumns.get(col).getColumnName(), String.valueOf(col));
		}
		totSummaryColCapture.put(PretupsI.XLSX_LAST_ROW, String.valueOf(lastRowValue));
		
		return totSummaryColCapture;
	}

	private int writeXLSXInputHeaders(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq,
			C2STransferCommReqDTO c2STransferCommReqDTO, int lastRowValue) {
		final String methodName = "writeXLSXInputHeaders";
		
		try {
			sheet.autoSizeColumn(PretupsRptUIConsts.ZERO.getNumValue());
			sheet.autoSizeColumn(PretupsRptUIConsts.ONE.getNumValue());
			sheet.autoSizeColumn(PretupsRptUIConsts.TWO.getNumValue());
		}catch (Exception e) {
			log.error("", "Error occurred while autosizing columns");
			e.printStackTrace();
		}

		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		// headerFont.setFontHeightInPoints( (Short) 14);
		CellStyle headerCellStyle = workbook.createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Displaying Report search parameters]
		Row reportheader = sheet.createRow(lastRowValue);
		Cell reportHeadingCell = reportheader.createCell(PretupsRptUIConsts.ZERO.getNumValue());
		reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
		reportHeadingCell.setCellStyle(headerCellStyle);
		sheet.addMergedRegion(
				new CellRangeAddress(PretupsRptUIConsts.ZERO.getNumValue(), PretupsRptUIConsts.ZERO.getNumValue(),
						PretupsRptUIConsts.ZERO.getNumValue(), PretupsRptUIConsts.FOUR.getNumValue()));

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Current Row value " + lastRowValue);
		}
		lastRowValue = lastRowValue + 1;

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Current Row value " + lastRowValue);
		}
		for (String strRow : downloadDataFomatReq.getSearchInputMaprowCell().keySet()) {
			List<SearchInputDisplayinRpt> listRowSearchInput = (List<SearchInputDisplayinRpt>) downloadDataFomatReq
					.getSearchInputMaprowCell().get(strRow); //
			lastRowValue = Integer.parseInt(strRow);
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Current Row value " + lastRowValue);
			}
			Row searchInputRow = sheet.createRow(lastRowValue);
			for (SearchInputDisplayinRpt searchInputDisplayinRpt : listRowSearchInput) {
				if (log.isDebugEnabled()) {
					log.debug(methodName,
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
		
    return lastRowValue;
	}

	private void writeInputHeaders(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			C2STransferCommReqDTO c2STransferCommReqDTO) {
		String[] reportheadervalue = new String[1];
		reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
				null)  ;
		csvWriter.writeNext(reportheadervalue);
		String[] blankLine = { "" };
		csvWriter.writeNext(blankLine);

		Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

		Date currentDate = new Date();
		String reportDate = BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));

		String[] inputRow1 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.REPORT_DATE.getReportValues(), null) + " : " + reportDate };
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

		String[] inputRow3 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_NETWORKCODE.getReportValues()) };
		csvWriter.writeNext(inputRow3);

		String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()) };
		csvWriter.writeNext(inputRow4);

		String[] inputRow5 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_SERVICE.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_SERVICE.getReportValues()) };
		csvWriter.writeNext(inputRow5);

		String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()) };
		csvWriter.writeNext(inputRow6);

		String[] inputRow7 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSTATUS.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_TRANSTATUS.getReportValues()) };
		csvWriter.writeNext(inputRow7);

		if (c2STransferCommReqDTO.getReqTab() != null
				&& c2STransferCommReqDTO.getReqTab().trim().toUpperCase().equals(PretupsI.C2C_MOBILENUMBER_TAB_REQ)) {
			String[] inputRow9 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_MOBILENUM.getReportValues(), null) + " : "
					+ inputParamMap
							.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_MOBILENUM.getReportValues()) };
			csvWriter.writeNext(inputRow9);
		} else {
			String[] inputRow9 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CHANNELUSERID.getReportValues(), null) + " : "
					+ inputParamMap
							.get(PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_CHANNELUSERID.getReportValues()) };
			csvWriter.writeNext(inputRow9);
		}

		if (c2STransferCommReqDTO.getUserType() != PretupsI.ALL
				&& c2STransferCommReqDTO.getUserType().equals(PretupsI.USER_TYPE_STAFF)) {
			String[] inputRow10 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues(), null) + " : "
					+ PretupsI.USER_TYPE_STAFF };
			csvWriter.writeNext(inputRow10);

		} else if (c2STransferCommReqDTO.getUserType() != PretupsI.ALL
				&& c2STransferCommReqDTO.getUserType().equals(PretupsI.USER_TYPE_CHANNEL)) {
			String[] inputRow10 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues(), null) + " : "
					+ PretupsI.USER_TYPE_CHANNEL };
			csvWriter.writeNext(inputRow10);
		} else {
			String[] inputRow10 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
					PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_USERTYPE.getReportValues(), null) + " : "
					+ PretupsI.ALL };
			csvWriter.writeNext(inputRow10);
		}

		if (c2STransferCommReqDTO.getUserType() != PretupsI.ALL
				&& c2STransferCommReqDTO.getUserType().equals(PretupsI.USER_TYPE_STAFF)) {
			if (c2STransferCommReqDTO.getOptionStaff_LoginIDOrMsisdn() != null
					&& c2STransferCommReqDTO.getOptionStaff_LoginIDOrMsisdn().equals(PretupsI.OPTION_LOGIN_ID)) {
				String[] inputRow11 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
						PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_STAFFLOGINID.getReportValues(), null) + " : "
						+ inputParamMap.get(
								PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_STAFFLOGINID.getReportValues()) };
				csvWriter.writeNext(inputRow11);
			} else {
				String[] inputRow11 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
						PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_STAFFMSISDN.getReportValues(), null) + " : "
						+ inputParamMap.get(
								PretupsRptUIConsts.C2SCOMMTRANSFDOWNLOAD_RPT_LABEL_STAFFMSISDN.getReportValues()) };
				csvWriter.writeNext(inputRow11);

			}

		}

		csvWriter.writeNext(blankLine);

	}

	void writeRowHeadersHeaders(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			C2STransferCommReqDTO c2STransferCommReqDTO) {
		// Displaying Report data rowWise
		Map<String, String> displayColumnMap = downloadDataFomatReq.getDisplayListColumns().stream()
				.collect(Collectors.toMap(DispHeaderColumn::getColumnName, DispHeaderColumn::getDisplayName));
		String[] columSeqArr = downloadDataFomatReq.getColumnSequenceNames().split(",");

		//List<? extends Object> dataList = downloadDataFomatReq.getReportDataList();
		// Display report column headers

		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		String[] columnHeaderNames = new String[listDiplayColumns.size()];
		for (int k = 0; k < listDiplayColumns.size(); k++) {
			columnHeaderNames[k] = displayColumnMap.get(listDiplayColumns.get(k).getColumnName());
		}
		csvWriter.writeNext(columnHeaderNames);
	}

	void writeCSVRow(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			C2StransferCommisionRecordVO record) {
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Map<String, String> mappedColumnValue = getMappedColumnValue(record);
		String[] dataRow = new String[listDiplayColumns.size()];
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
		}
		csvWriter.writeNext(dataRow);

	}

	void writeXLSXRow(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq, int lastRowValue,
			C2StransferCommisionRecordVO record) {
		final String methodName = "writeXLSXRow";
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		// Display report column data
		Map<String, String> mappedColumnValue = getMappedColumnValue(record);
		
		lastRowValue = lastRowValue + 1;
		Row dataRow = sheet.createRow(lastRowValue);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "row - " + lastRowValue);
		}
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "loop - " + col + " " + listDiplayColumns.get(col).getColumnName() + " "
						+ mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
			}
			dataRow.createCell(col).setCellValue(mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
		}

	}

	public void writeXLSXTotalSummaryColumns(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq,
			Integer lastRowValue, HashMap<String, String> totSummaryColValue,
			HashMap<String, String> totSummaryColCapture) {
		final String methodName = "writeXLSXTotalSummaryColumns";
		Row dataRow = sheet.createRow(lastRowValue.intValue());
		Iterator<Map.Entry<String, String>> itr = totSummaryColCapture.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<String, String> entry = itr.next();
			log.info(methodName, "Key = " + entry.getKey() + ", Column = " + entry.getValue() + "ColValue :"
					+ totSummaryColValue.get(entry.getKey()));
			dataRow.createCell(Integer.parseInt(entry.getValue())).setCellValue(totSummaryColValue.get(entry.getKey()));
		}

	}
	
	
	public void writeCSVTotalSummaryColumns(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			 HashMap<String, String> totSummaryColValue
			) {
		final String methodName = "writeCSVTotalSummaryColumns";
		List listDiplayColumns  =  downloadDataFomatReq.getDisplayListColumns();
		String[] columnHeaderNames = new String[listDiplayColumns.size()];
		//To display summary coloumns
		for (int k = 0; k < listDiplayColumns.size(); k++) {
			DispHeaderColumn displayHearColumn = (DispHeaderColumn) listDiplayColumns.get(k);
			 if(totSummaryColValue.containsKey(displayHearColumn.getColumnName())) {
				 columnHeaderNames[k] = totSummaryColValue.get(displayHearColumn.getColumnName());	 
			 }else {
				 columnHeaderNames[k] ="";
			 }
		}
		
		csvWriter.writeNext(columnHeaderNames);

	}

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
		/*
		 * mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_NETWORK_CODE.
		 * getColumnName(), c2StransferCommisionRecordVO.getsen);
		 */

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
		mappedColumnValue.put(C2STransferCommDownloadColumns.SENDER_MOBILE_TYPE.getColumnName(),
				c2StransferCommisionRecordVO.getSenderMobileType());
		

		/*
		 * mappedColumnValue.put(C2STransferCommDownloadColumns.PRODUCT_NAME.
		 * getColumnName(), c2StransferCommisionRecordVO.getProductName());
		 */
		/*
		 * mappedColumnValue.put(C2STransferCommDownloadColumns.SERVICES.getColumnName()
		 * , c2StransferCommisionRecordVO.getServices());
		 */
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
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.REQUEST_GATEWAY.getColumnName(),
				c2StransferCommisionRecordVO.getRequestGateway());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.PREVIOUS_BALANCE.getColumnName(),
				c2StransferCommisionRecordVO.getPreviousBalance());
		mappedColumnValue.put(C2STransferCommDownloadColumns.POST_BALANCE.getColumnName(),
				c2StransferCommisionRecordVO.getPostBalance());
		mappedColumnValue.put(C2STransferCommDownloadColumns.EXTERNAL_REF_ID.getColumnName(),
				c2StransferCommisionRecordVO.getExternalReferenceID());
		mappedColumnValue.put(C2STransferCommDownloadColumns.MARGIN_RATE.getColumnName(),
				c2StransferCommisionRecordVO.getMarginRate());
		mappedColumnValue.put(C2STransferCommDownloadColumns.MARGIN_AMOUNT.getColumnName(),
				c2StransferCommisionRecordVO.getMarginAmount());
		mappedColumnValue.put(C2STransferCommDownloadColumns.MARGIN_TYPE.getColumnName(),
				c2StransferCommisionRecordVO.getMarginType());
		mappedColumnValue.put(C2STransferCommDownloadColumns.CURRENCY_DETAIL.getColumnName(),
				c2StransferCommisionRecordVO.getCurrencyDetail());
		mappedColumnValue.put(C2STransferCommDownloadColumns.BONUS_TYPE.getColumnName(),
				c2StransferCommisionRecordVO.getBonusType());
		mappedColumnValue.put(C2STransferCommDownloadColumns.RECEIVER_BONUS_VALUE.getColumnName(),
				c2StransferCommisionRecordVO.getReceiverBonusValue());
		mappedColumnValue.put(C2STransferCommDownloadColumns.STATUS.getColumnName(),
				c2StransferCommisionRecordVO.getStatus());
		
		mappedColumnValue.put(C2STransferCommDownloadColumns.TAX1.getColumnName(),
				c2StransferCommisionRecordVO.getTax1());
		mappedColumnValue.put(C2STransferCommDownloadColumns.TAX2.getColumnName(),
				c2StransferCommisionRecordVO.getTax2());

		return mappedColumnValue;
	}

}
