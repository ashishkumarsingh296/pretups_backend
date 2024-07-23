/**
 * 
 */
package com.txn.pretups.channel.transfer.businesslogic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.opencsv.CSVWriter;

/**
 * @author subesh.vasu1
 *
 */
public class CommonReportWriter {
	protected final Log log = LogFactory.getLog(getClass().getName());



	

	public HashMap<String, String> writeXSLRowHeadersHeaders(Workbook workbook, Sheet sheet,
			DownloadDataFomatReq downloadDataFomatReq,
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

	
	public	void writeRowHeadersHeaders(CSVWriter csvWriter, DownloadDataFomatReq downloadDataFomatReq) {
		// Displaying Report data rowWise
		Map<String, String> displayColumnMap = downloadDataFomatReq.getDisplayListColumns().stream()
				.collect(Collectors.toMap(DispHeaderColumn::getColumnName, DispHeaderColumn::getDisplayName));
		if(downloadDataFomatReq.getColumnSequenceNames()!=null) {
			String[] columSeqArr = downloadDataFomatReq.getColumnSequenceNames().split(",");
		}

		//List<? extends Object> dataList = downloadDataFomatReq.getReportDataList();
		// Display report column headers

		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		String[] columnHeaderNames = new String[listDiplayColumns.size()];
		for (int k = 0; k < listDiplayColumns.size(); k++) {
			columnHeaderNames[k] = displayColumnMap.get(listDiplayColumns.get(k).getColumnName());
		}
		csvWriter.writeNext(columnHeaderNames);
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
	
	
	public static HttpHeaders setDownloadFileHeaders(String filePath) {
        HttpHeaders reportHeaders = new HttpHeaders();
        reportHeaders.add(HttpHeaders.CACHE_CONTROL, PretupsRptUIConsts.CACHE_CONTROL_VALUE.getReportValues());
        reportHeaders.add(HttpHeaders.EXPIRES, PretupsRptUIConsts.ZERO.getReportValues());
        reportHeaders.add(HttpHeaders.PRAGMA, PretupsRptUIConsts.PRAGMA_VALUE.getReportValues());
        

        String csvSummaryFileExtension = FilenameUtils.getExtension(filePath);
        String csvSummaryFileName = FilenameUtils.getName(filePath);
        reportHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvSummaryFileName);
        reportHeaders.add(PretupsI.REPORT_FILENAME, csvSummaryFileName);
        reportHeaders.add("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type, Accept, REPORT_FILENAME");
        reportHeaders.add("Access-Control-Expose-Headers", "REPORT_FILENAME");

        if (PretupsI.FILE_CONTENT_TYPE_CSV.equalsIgnoreCase(csvSummaryFileExtension)) {
        	reportHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        } else if (PretupsI.FILE_CONTENT_TYPE_XLS.equalsIgnoreCase(csvSummaryFileExtension)
                || PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(csvSummaryFileExtension)) {
        	reportHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }
        return reportHeaders;
    }




}
