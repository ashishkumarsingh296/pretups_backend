/**
 * 
 */
package com.txn.pretups.channel.transfer.businesslogic;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.C2CTransferCommReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.C2CtransferCommisionRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.util.BTSLDateUtil;
import com.opencsv.CSVWriter;

/**
 * @author subesh.vasu1
 *
 */
public class C2CTransferdetReportWriter extends CommonReportWriter {
	protected final Log log = LogFactory.getLog(getClass().getName());

	public void constructCSV(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			C2CTransferCommReqDTO c2CTransferCommReqDTO) {
		writeInputHeaders(csvWriter, downloadDataFomatReq, c2CTransferCommReqDTO);
		super.writeRowHeadersHeaders(csvWriter, downloadDataFomatReq);
		// writeCSVRow(csvWriter,downloadDataFomatReq,c2STransferCommReqDTO);
	}

	public HashMap<String, String> constructXLSX(Workbook workbook, Sheet sheet,
			DownloadDataFomatReq downloadDataFomatReq, C2CTransferCommReqDTO c2CTransferCommReqDTO,
			Integer lastRowValue, CellStyle headerCellStyle) {
		HashMap<String, String> totalSummaryCaptureCols = new HashMap<String, String>();
		int lastRow=0;
		lastRow=writeXLSXInputHeaders(workbook, sheet, downloadDataFomatReq, c2CTransferCommReqDTO, lastRowValue);
		totalSummaryCaptureCols = super.writeXSLRowHeadersHeaders(workbook, sheet, downloadDataFomatReq,
				lastRow, headerCellStyle);
		return totalSummaryCaptureCols;
	}

	
	private int writeXLSXInputHeaders(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq,
			C2CTransferCommReqDTO c2CTransferCommReqDTO, int lastRowValue) {
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
				PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
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
			C2CTransferCommReqDTO c2CTransferCommReqDTO) {
		String[] reportheadervalue = new String[1];
		reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.C2CCOMMTRANSFDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
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
	}

	
public	void writeCSVRow(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			C2CtransferCommisionRecordVO record) {
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Map<String, String> mappedColumnValue = getMappedColumnValue(record);
		String[] dataRow = new String[listDiplayColumns.size()];
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
		}
		csvWriter.writeNext(dataRow);

	}

	public void writeXLSXRow(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq, int lastRowValue,
			C2CtransferCommisionRecordVO record) {
		final String methodName = "writeXLSXRow";
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		// Display report column data
		Map<String, String> mappedColumnValue = getMappedColumnValue(record);
		
		//lastRowValue = lastRowValue + 1;
		Row dataRow = sheet.createRow(lastRowValue);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "row - " + lastRowValue);
		}
		try {
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "loop - " + col + " " + listDiplayColumns.get(col).getColumnName() + " "
						+ mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
			}
			dataRow.createCell(col).setCellValue(mappedColumnValue.get(listDiplayColumns.get(col).getColumnName()));
		} 
		}catch(Exception ex) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, ex );
			}
		}

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

}
