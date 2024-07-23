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
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOtherDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.PassbookOthersReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.opencsv.CSVWriter;

/**
 * @author subesh.vasu1
 *
 */
public class BulkUserAddStsReportWriter extends CommonReportWriter {
	protected final Log log = LogFactory.getLog(getClass().getName());

	public void constructCSV(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			BulkUserAddRptReqDTO bulkUserAddRptReqDTO) {
		writeInputHeaders(csvWriter, downloadDataFomatReq, bulkUserAddRptReqDTO);
		super.writeRowHeadersHeaders(csvWriter, downloadDataFomatReq);
		// writeCSVRow(csvWriter,downloadDataFomatReq,c2STransferCommReqDTO);
	}

	public HashMap<String, String> constructXLSX(Workbook workbook, Sheet sheet,
			DownloadDataFomatReq downloadDataFomatReq, BulkUserAddRptReqDTO bulkUserAddRptReqDTO,
			Integer lastRowValue, CellStyle headerCellStyle) {
		HashMap<String, String> totalSummaryCaptureCols = new HashMap<String, String>();
		int lastRow=0;
		lastRow=writeXLSXInputHeaders(workbook, sheet, downloadDataFomatReq, bulkUserAddRptReqDTO, lastRowValue);
		totalSummaryCaptureCols = super.writeXSLRowHeadersHeaders(workbook, sheet, downloadDataFomatReq,
				lastRow, headerCellStyle);
		return totalSummaryCaptureCols;
	}

	
	private int writeXLSXInputHeaders(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq,
			BulkUserAddRptReqDTO bulkUserAddRptReqDTO, int lastRowValue) {
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
				PretupsRptUIConsts.BULKUSERADD_RPT_HEADER_DISPLAYVALUE.getReportValues(), null));
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
			BulkUserAddRptReqDTO bulkUserAddRptReqDTO) {
		String[] reportheadervalue = new String[1];
		reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.BULKUSERADD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
				null)  ;
		csvWriter.writeNext(reportheadervalue);
		String[] blankLine = { "" };
		csvWriter.writeNext(blankLine);

		Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

		Date currentDate = new Date();
		String reportDate = BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));

//		String[] inputRow1 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
//				PretupsRptUIConsts.REPORT_DATE.getReportValues(), null) + " : " + reportDate };
//		csvWriter.writeNext(inputRow1);

		String[] inputRow2 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNO.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNO.getReportValues())
				 };
		csvWriter.writeNext(inputRow2);

		

		String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNAME.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNAME.getReportValues()) };
		csvWriter.writeNext(inputRow4);

		

		String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDBY.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDBY.getReportValues()) };
		csvWriter.writeNext(inputRow6);

		String[] inputRow7 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDON.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDON.getReportValues()) };
		csvWriter.writeNext(inputRow7);
		
		String[] inputRow8 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHSTATUS.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHSTATUS.getReportValues()) };
		csvWriter.writeNext(inputRow8);
		
		String[] inputRow9 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_FILENAME.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_FILENAME.getReportValues()) };
		csvWriter.writeNext(inputRow9);
		
		String[] inputRow10 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),
				PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_TOTALNO.getReportValues(), null) + " : "
				+ inputParamMap.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_TOTALNO.getReportValues()) };
		csvWriter.writeNext(inputRow10);
		
		
		csvWriter.writeNext(blankLine);

	}

	
public	void writeCSVRow(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq,
			ChannelUserVO record) {
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		Map<String, String> mappedColumnValue = getMappedColumnValue(record);
		String[] dataRow = new String[listDiplayColumns.size()];
		for (int col = 0; col < listDiplayColumns.size(); col++) {
			dataRow[col] = mappedColumnValue.get(listDiplayColumns.get(col).getColumnName());
		}
		csvWriter.writeNext(dataRow);

	}

public	void writeXLSXRow(Workbook workbook, Sheet sheet, DownloadDataFomatReq downloadDataFomatReq, int lastRowValue,
			ChannelUserVO record) {
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
	


	private Map<String, String> getMappedColumnValue(ChannelUserVO channelUserVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(BulkUserDownloadColumns.PARENT_LOGINID.getColumnName(), channelUserVO.getParentLoginID());
		mappedColumnValue.put(BulkUserDownloadColumns.PARENT_MSISDN.getColumnName(), channelUserVO.getParentMsisdn());
		mappedColumnValue.put(BulkUserDownloadColumns.USERNAME_PREFIX.getColumnName(), channelUserVO.getUserNamePrefix());
		mappedColumnValue.put(BulkUserDownloadColumns.FIRSTNAME.getColumnName(), channelUserVO.getFirstName());
		mappedColumnValue.put(BulkUserDownloadColumns.LASTNAME.getColumnName(), channelUserVO.getLastName());
		//mappedColumnValue.put(BulkUserDownloadColumns.USERNAME.getColumnName(), channelUserVO.getUserName());
		mappedColumnValue.put(BulkUserDownloadColumns.SHORT_NAME.getColumnName(), channelUserVO.getShortName());
		mappedColumnValue.put(BulkUserDownloadColumns.CATEGORY_CODE.getColumnName(), channelUserVO.getCategoryName());
		mappedColumnValue.put(BulkUserDownloadColumns.EXTERNAL_CODE.getColumnName(), channelUserVO.getExternalCode());
		mappedColumnValue.put(BulkUserDownloadColumns.CONTACT_PERSON.getColumnName(), channelUserVO.getContactPerson());
		mappedColumnValue.put(BulkUserDownloadColumns.ADDRESS1.getColumnName(), channelUserVO.getAddress1());
		mappedColumnValue.put(BulkUserDownloadColumns.CITY.getColumnName(), channelUserVO.getCity());
		mappedColumnValue.put(BulkUserDownloadColumns.STATE.getColumnName(), channelUserVO.getState());
		mappedColumnValue.put(BulkUserDownloadColumns.SSN.getColumnName(), channelUserVO.getSsn());
		mappedColumnValue.put(BulkUserDownloadColumns.COUNTRY.getColumnName(), channelUserVO.getCountry());
		mappedColumnValue.put(BulkUserDownloadColumns.COMPANY.getColumnName(), channelUserVO.getCompany()); 
		mappedColumnValue.put(BulkUserDownloadColumns.FAX.getColumnName(), channelUserVO.getFax());
		mappedColumnValue.put(BulkUserDownloadColumns.LOGINID.getColumnName(), channelUserVO.getLoginID());
		mappedColumnValue.put(BulkUserDownloadColumns.MOBILE_NUMBER.getColumnName(), channelUserVO.getMsisdn()); 
		mappedColumnValue.put(BulkUserDownloadColumns.GEOGRAPHYCODE.getColumnName(), channelUserVO.getGeographicalCode());
		mappedColumnValue.put(BulkUserDownloadColumns.SERVIECS.getColumnName(), channelUserVO.getServiceTypes());
		mappedColumnValue.put(BulkUserDownloadColumns.COMMISSION_PROFILE.getColumnName(), channelUserVO.getCommissionProfileSetName());
		mappedColumnValue.put(BulkUserDownloadColumns.TRANSFER_PROFILE.getColumnName(), channelUserVO.getTransferProfileName());
		mappedColumnValue.put(BulkUserDownloadColumns.OUTLET.getColumnName(), channelUserVO.getOutletCode());
		mappedColumnValue.put(BulkUserDownloadColumns.SUBOUTLET_CODE.getColumnName(), channelUserVO.getSubOutletCode());
		mappedColumnValue.put(BulkUserDownloadColumns.STATUS.getColumnName(), channelUserVO.getStatusDesc());
		mappedColumnValue.put(BulkUserDownloadColumns.GROUP_ROLE_CODE.getColumnName(), channelUserVO.getGroupRoleCode());
		mappedColumnValue.put(BulkUserDownloadColumns.GRADE.getColumnName(), channelUserVO.getUserGrade());
		mappedColumnValue.put(BulkUserDownloadColumns.mcomorceflag.getColumnName(), channelUserVO.getMcommerceServiceAllow());
		mappedColumnValue.put(BulkUserDownloadColumns.MPAYPROFILEID.getColumnName(), channelUserVO.getMpayProfileID());
		mappedColumnValue.put(BulkUserDownloadColumns.LOWBALALERTALLOW.getColumnName(), channelUserVO.getLowBalAlertAllow());
		mappedColumnValue.put(BulkUserDownloadColumns.REMARKS.getColumnName(), channelUserVO.getRemarks());

		return mappedColumnValue;
	}

}
