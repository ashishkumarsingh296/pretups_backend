package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;

public class DownloadDataFomatReq {
	
	private Locale locale;
	private String fileName;
	private String fileType;
	private boolean offline;
	private boolean checkDataExistforFilterRequest;
	private Long continueLastRow=0l;// for CSV file writing...
	private Object reportInputReqObj;
	private Object summaryObject; // Report total Summary Object can be placed here for all types of reports.
	private List<? extends Object> reportDataList;
	private List<DispHeaderColumn> displayListColumns; 
	MultiValuedMap<String, SearchInputDisplayinRpt> SearchInputMaprowCell; // to display report parameters in Excel cells
	private String columnSequenceNames;
	private  Map<String,String> inputParamMap = new LinkedHashMap<String,String>();
	private  Map<String,String> totalSummaryMap = new LinkedHashMap<String,String>();
	public Map<String, String> getTotalSummaryMap() {
		return totalSummaryMap;
	}
	public void setTotalSummaryMap(Map<String, String> totalSummaryMap) {
		this.totalSummaryMap = totalSummaryMap;
	}
	private  String reportHeaderName;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public List<? extends Object> getReportDataList() {
		return reportDataList;
	}
	public void setReportDataList(List<? extends Object> reportDataList) {
		this.reportDataList = reportDataList;
	}
	public List<DispHeaderColumn> getDisplayListColumns() {
		return displayListColumns;
	}
	public void setDisplayListColumns(List<DispHeaderColumn> displayListColumns) {
		this.displayListColumns = displayListColumns;
	}
	public MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputMaprowCell() {
		return SearchInputMaprowCell;
	}
	public void setSearchInputMaprowCell(MultiValuedMap<String, SearchInputDisplayinRpt> searchInputMaprowCell) {
		SearchInputMaprowCell = searchInputMaprowCell;
	}
	public String getColumnSequenceNames() {
		return columnSequenceNames;
	}
	public void setColumnSequenceNames(String columnSequenceNames) {
		this.columnSequenceNames = columnSequenceNames;
	}
	public Map<String, String> getInputParamMap() {
		return inputParamMap;
	}
	public void setInputParamMap(Map<String, String> inputParamMap) {
		this.inputParamMap = inputParamMap;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Override
	public String toString() {
		String displayColumns=null;
		if(displayListColumns!=null) {
			displayColumns =	displayListColumns.stream().map(g -> g.getColumnName()).collect(Collectors.joining(", "));
		}
		return "PassbookDownloadReqData [Locale=" + locale.getDisplayLanguage() + ", fileType=" + fileType + ",DisplayColumns=" +  displayColumns + "]";
	}
	public Object getSummaryObject() {
		return summaryObject;
	}
	public void setSummaryObject(Object summaryObject) {
		this.summaryObject = summaryObject;
	}
	public Object getReportInputReqObj() {
		return reportInputReqObj;
	}
	public void setReportInputReqObj(Object reportInputReqObj) {
		this.reportInputReqObj = reportInputReqObj;
	}
	public boolean isOffline() {
		return offline;
	}
	public boolean isCheckDataExistforFilterRequest() {
		return checkDataExistforFilterRequest;
	}
	public void setCheckDataExistforFilterRequest(boolean checkDataExistforFilterRequest) {
		this.checkDataExistforFilterRequest = checkDataExistforFilterRequest;
	}
	public void setOffline(boolean offline) {
		this.offline = offline;
	}
	public Long getContinueLastRow() {
		return continueLastRow;
	}
	public void setContinueLastRow(Long continueLastRow) {
		this.continueLastRow = continueLastRow;
	}
	public String getReportHeaderName() {
		return reportHeaderName;
	}
	public void setReportHeaderName(String reportHeaderName) {
		this.reportHeaderName = reportHeaderName;
	}
	
	
}
