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
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOtherDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
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
public class PassbookOthersReportWriter extends CommonReportWriter {
	protected final Log log = LogFactory.getLog(getClass().getName());

	public void constructCSV(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			PassbookOthersReqDTO passbookOthersReqDTO) {
		writeInputHeaders(csvWriter, downloadDataFomatReq, passbookOthersReqDTO);
		super.writeRowHeadersHeaders(csvWriter, downloadDataFomatReq);
		// writeCSVRow(csvWriter,downloadDataFomatReq,c2STransferCommReqDTO);
	}

	public HashMap<String, String> constructXLSX(Workbook workbook, Sheet sheet,
			DownloadDataFomatReq downloadDataFomatReq, PassbookOthersReqDTO passbookOthersReqDTO,
			Integer lastRowValue, CellStyle headerCellStyle) {
		HashMap<String, String> totalSummaryCaptureCols = new HashMap<String, String>();
		int lastRow=0;
		lastRow=writeXLSXInputHeaders(workbook, sheet, downloadDataFomatReq, passbookOthersReqDTO, lastRowValue);
		totalSummaryCaptureCols = super.writeXSLRowHeadersHeaders(workbook, sheet, downloadDataFomatReq,
				lastRow, headerCellStyle);
		return totalSummaryCaptureCols;
	}

	
	private int writeXLSXInputHeaders(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq,
			PassbookOthersReqDTO passbookOthersReqDTO, int lastRowValue) {
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
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
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
			PassbookOthersReqDTO passbookOthersReqDTO) {
		String[] reportheadervalue = new String[1];
		reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
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
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_FROMDATE.getReportValues())
				+ "     "
				+ RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
						PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_TODATE.getReportValues(), null)
				+ " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_TODATE.getReportValues()) };
		csvWriter.writeNext(inputRow2);

		

		String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_CATEORY.getReportValues()) };
		csvWriter.writeNext(inputRow4);

		String[] inputRow5 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_DOMAIN.getReportValues()) };
		csvWriter.writeNext(inputRow5);

		String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_GEOGRAPHY.getReportValues()) };
		csvWriter.writeNext(inputRow6);

		String[] inputRow7 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_USER.getReportValues()) };
		csvWriter.writeNext(inputRow7);
		
		String[] inputRow8 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_PRODUCT.getReportValues()) };
		csvWriter.writeNext(inputRow8);
		String description=RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), "passbookothersdownload.sheet.cell.label.desc", null);
		String[] inputRow9 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.PASSBOOKOTHERSDOWNLOAD_RPT_LABEL_ReconStatus.getReportValues(), null) + " : "
				+ description };
		csvWriter.writeNext(inputRow9);
		csvWriter.writeNext(blankLine);

	}

	
	void writeCSVRow(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			PassbookOthersRecordVO record) {
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Map<String, String> mappedColumnValue = getMappedColumnValue(record);
		String[] dataRow = new String[listDiplayColumns.size()];
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
		}
		csvWriter.writeNext(dataRow);

	}

	void writeXLSXRow(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq, int lastRowValue,
			PassbookOthersRecordVO record) {
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
	


	private Map<String, String> getMappedColumnValue(PassbookOthersRecordVO passbookOthersRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();

		mappedColumnValue.put(PassbookOtherDownloadColumns.TRANS_DATE.getColumnName(),
				passbookOthersRecordVO.getTransDate());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PRODUCT_NAME.getColumnName(),
				passbookOthersRecordVO.getProductName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_NAME.getColumnName(),
				passbookOthersRecordVO.getUserName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_MOB_NUM.getColumnName(),
				passbookOthersRecordVO.getUserMobilenumber());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_CATEGORY.getColumnName(),
				passbookOthersRecordVO.getUserCategory());
		mappedColumnValue.put(PassbookOtherDownloadColumns.USER_GEOGRAPHY.getColumnName(),
				passbookOthersRecordVO.getUserGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.EXTERNAL_CODE.getColumnName(),
				passbookOthersRecordVO.getUserGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_NAME.getColumnName(),
				passbookOthersRecordVO.getParentName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_MOB_NUM.getColumnName(),
				passbookOthersRecordVO.getParentMobilenumber());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_CATEGORY.getColumnName(),
				passbookOthersRecordVO.getParentCategory());
		mappedColumnValue.put(PassbookOtherDownloadColumns.PARENT_GEOGRAPHY.getColumnName(),
				passbookOthersRecordVO.getParentGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_NAME.getColumnName(),
				passbookOthersRecordVO.getOwnerName());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_MOBILE_NUMBER.getColumnName(),
				passbookOthersRecordVO.getOwnerMobileNumber());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_CATEGORY.getColumnName(),
				passbookOthersRecordVO.getOwnerCategory());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OWNER_GEOGRAPHY.getColumnName(),
				passbookOthersRecordVO.getOwnerGeography());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_TRANSFER_COUNT.getColumnName(),
				passbookOthersRecordVO.getO2cTransferCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_TRANSFER_AMOUNT.getColumnName(),
				passbookOthersRecordVO.getO2cTransferAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_RETURN_COUNT.getColumnName(),
				passbookOthersRecordVO.getO2cReturnCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_RETURN_AMOUNT.getColumnName(),
				passbookOthersRecordVO.getO2cReturnAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_WITHDRAWAL_COUNT.getColumnName(),
				passbookOthersRecordVO.getO2cWithdrawCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.O2C_WITHDRAWAL_AMOUNT.getColumnName(),
				passbookOthersRecordVO.getO2cWithdrawAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_INCOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransfer_InCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_INAMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransfer_InAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_OUTCOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransfer_OutCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_OUTAMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransfer_OutAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_INCOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferRet_InCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_INAMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferRet_InAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_OUTCOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferRet_OutCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_RETURN_OUTAMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferRet_OutAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_INCOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferWithdraw_InCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_INAMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferWithdraw_InAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_OUTCOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferWithdraw_OutCount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2C_TRANSFER_WITHDRAW_OUTAMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2cTransferWithdraw_OutAmount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_TRANSFER_COUNT.getColumnName(),
				passbookOthersRecordVO.getC2sTransfer_count());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_TRANSFER_AMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2sTransfer_amount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_REVERSAL_COUNT.getColumnName(),
				passbookOthersRecordVO.getC2sReveral_count());
		mappedColumnValue.put(PassbookOtherDownloadColumns.C2S_REVERSAL_AMOUNT.getColumnName(),
				passbookOthersRecordVO.getC2sReveral_amount());
		mappedColumnValue.put(PassbookOtherDownloadColumns.ADDITIONAL_COMM_AMOUNT.getColumnName(),
				passbookOthersRecordVO.getAdditionalcommissionAmount());
		
		mappedColumnValue.put(PassbookOtherDownloadColumns.RECON_STATUS.getColumnName(),
				passbookOthersRecordVO.getReconStatus());
		mappedColumnValue.put(PassbookOtherDownloadColumns.OPENING_BALANCE.getColumnName(),
				passbookOthersRecordVO.getOpeningBalance());
		mappedColumnValue.put(PassbookOtherDownloadColumns.CLOSING_BALANCE.getColumnName(),
				passbookOthersRecordVO.getClosingBalance());
		return mappedColumnValue;
	}

}
