package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
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
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.AdditionalCommissionSummryC2SResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtlnCommSummryDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummaryRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlCommSummryReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.AddtnlcommSummryDownldCols;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfDetDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.O2CtransferDetRecordVO;
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
import com.btsl.pretups.master.businesslogic.ServiceTypeSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceTypeSelectorMappingVO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;
import com.txn.pretups.channel.transfer.businesslogic.C2STransferTxnDAO;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("AddtnlCommSummryReportProcess")
public class AddtnlCommSummryReportProcess extends CommonService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();
	
	private HashMap<String,String > totSummaryColCapture =  new HashMap<String,String>();
	private HashMap<String,String > totSummaryColValue =  new HashMap<String,String>();

	public void getAddtnlCommSummryDetails(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO, AdditionalCommissionSummryC2SResp response)
			throws BTSLBaseException {

		final String methodName = "getAddtnlCommSummryDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		C2STransferTxnDAO cs2STransferTxnDAO  = new C2STransferTxnDAO(); 

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			AdditionalCommissionSummryC2SResp additionalCommissionSummryC2SResp= cs2STransferTxnDAO.searchAddtnlCommSummryData(con, addtnlCommSummryReqDTO);
			 
			 if(additionalCommissionSummryC2SResp.getAddtnlcommissionSummaryList().isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			 response.setAddtnlcommissionSummaryList(additionalCommissionSummryC2SResp.getAddtnlcommissionSummaryList());
			 response.setTotalDiffAmount(additionalCommissionSummryC2SResp.getTotalDiffAmount());
			 response.setTotalTransactionCount(additionalCommissionSummryC2SResp.getTotalTransactionCount());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(),
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

	
	public HashMap<String,String> validateInputs(Connection con,AddtnlCommSummryReqDTO addtnlCommSummryReqDTO) throws BTSLBaseException {
		final String methodName ="validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
		Date currentDate = new Date();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();
		SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
		sdf.setLenient(false);

		Date frDate = new Date();
		Date tDate = new Date();
		String fromDate = addtnlCommSummryReqDTO.getFromDate();
		String toDate = addtnlCommSummryReqDTO.getToDate();
		String extNwCode = addtnlCommSummryReqDTO.getExtnwcode();
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE.getReportValues(), extNwCode);
		
		
		
		if(!addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY) &&
				!addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_MONTHLY) 
				) {
			throw new BTSLBaseException("PretupsUIReportsController", methodName,
					PretupsErrorCodesI.INVALID_PERIOD, 0, null);
			
		}
		
		
		String period= PretupsI.PERIOD_MONTHLY;
		 if(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()!=null && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY))
		 {
			 period=PretupsI.PERIOD_DAILY;
		 }
	reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DAILY_MONTHLY.getReportValues(), period);
		String fromMonth=null;
		String fromYear=null;
		if( !BTSLUtil.isEmpty(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()) && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_MONTHLY)) {
			 if(addtnlCommSummryReqDTO.getFromMonthYear()!=null ) {
				  if(addtnlCommSummryReqDTO.getFromMonthYear().indexOf("/")>0) {
					  fromMonth =addtnlCommSummryReqDTO.getFromMonthYear().substring(0, addtnlCommSummryReqDTO.getFromMonthYear().indexOf("/")); 
					  fromYear =addtnlCommSummryReqDTO.getFromMonthYear().substring(addtnlCommSummryReqDTO.getFromMonthYear().indexOf("/")+1, addtnlCommSummryReqDTO.getFromMonthYear().length());
					 
				  }	 
				 }
				  
			 }
			
		String toMonth=null;
		String toYear=null;
		if( !BTSLUtil.isEmpty(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()) && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_MONTHLY)) {
			 if(addtnlCommSummryReqDTO.getToMonthYear()!=null ) {
				  if(addtnlCommSummryReqDTO.getToMonthYear().indexOf("/")>0) {
					  toMonth =addtnlCommSummryReqDTO.getToMonthYear().substring(0, addtnlCommSummryReqDTO.getToMonthYear().indexOf("/")); 
					  toYear =addtnlCommSummryReqDTO.getToMonthYear().substring(addtnlCommSummryReqDTO.getToMonthYear().indexOf("/")+1, addtnlCommSummryReqDTO.getToMonthYear().length());
					 
				  }	 
				 }
			 
			 
			 
			 if(!BTSLUtil.isEmpty(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()) && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_MONTHLY)  ) {
		        	fromDate="01/"+addtnlCommSummryReqDTO.getFromMonthYear();
					 toDate="01/"+addtnlCommSummryReqDTO.getToMonthYear(); // Just to check max of the month.
					 Date tmpToDate= new Date();
					 try {
						 tmpToDate = sdf.parse(toDate + PretupsRptUIConsts.REPORT_TO_TIME.getReportValues());
						} catch (ParseException e) {
							throw new BTSLBaseException("PretupsUIReportsController", "lowthresholdsearch",
									PretupsErrorCodesI.CCE_XML_ERROR_FROM_DATE_REQUIRED, 0, null);
						}
					 
					 Calendar cal = Calendar.getInstance();
					    cal.setTime(tmpToDate);
					     int maxdays =cal.getActualMaximum(Calendar.DAY_OF_MONTH);
					 
					     toDate=maxdays+"/"+addtnlCommSummryReqDTO.getToMonthYear(); // Just to check max of the month.
		        } else {
		        	 fromDate=addtnlCommSummryReqDTO.getFromDate();
		            toDate=addtnlCommSummryReqDTO.getToDate();
		        }
			 
			 
			 
		}		
		
		
		if( !BTSLUtil.isEmpty(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()) && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY)) {
				try {
					frDate = sdf.parse(fromDate + PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues());
				} catch (ParseException e) {
					throw new BTSLBaseException("PretupsUIReportsController", "lowthresholdsearch",
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
		
		}
		

		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_FROMDATE.getReportValues(), fromDate +PretupsRptUIConsts.REPORT_FROM_TIME.getReportValues());
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_TODATE.getReportValues(), toDate+PretupsRptUIConsts.REPORT_TO_TIME.getReportValues());

		if (extNwCode != null && !extNwCode.trim().equals(PretupsI.ALL.trim())) {
			NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(extNwCode);
			if (BTSLUtil.isNullObject(networkVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
		}
		
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues(), addtnlCommSummryReqDTO.getCategoryCode());
		if (addtnlCommSummryReqDTO.getCategoryCode() != null
				&& !addtnlCommSummryReqDTO.getCategoryCode().trim().equals(PretupsI.ALL)) {
			CategoryVO categoryVO = categoryDAO.loadCategoryDetailsByCategoryCode(con,
					addtnlCommSummryReqDTO.getCategoryCode());
			if (BTSLUtil.isNullObject(categoryVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.EXT_USRADD_INVALID_CATEGORY, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues(), categoryVO.getCategoryName());
		}
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE.getReportValues(), addtnlCommSummryReqDTO.getService());
		
	
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues(), addtnlCommSummryReqDTO.getDomain());
		if (addtnlCommSummryReqDTO.getDomain() != null
				&& !addtnlCommSummryReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, addtnlCommSummryReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues(), domainVO.getDomainName());
		}
		
		
		reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY.getReportValues(), addtnlCommSummryReqDTO.getGeography());
			  if(addtnlCommSummryReqDTO.getGeography()!=null && !addtnlCommSummryReqDTO.getGeography().trim().equals(PretupsI.ALL)) {
				    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
				    String geographyName =geoDAO.getGeographyName(con, addtnlCommSummryReqDTO.getGeography(), true);
				    
				    if (geographyName==null) {
				 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
									PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
				    }
				    reportInputKeyValMap.put(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY.getReportValues(),
				    		geographyName);
			  }
			
			
		
		
		
		
		
		if(BTSLUtil.isNullString(addtnlCommSummryReqDTO.getService())  || (addtnlCommSummryReqDTO.getService()!=null && !addtnlCommSummryReqDTO.getService().trim().equals(PretupsI.ALL))) {	
			ServiceTypeSelectorMappingDAO serviceTypeSelectorMappingDAO =new ServiceTypeSelectorMappingDAO();   
		 List<ServiceTypeSelectorMappingVO> listServiceTypeSelectorMappingVOs=	serviceTypeSelectorMappingDAO.loadServiceSelectorMappingDetails(con, addtnlCommSummryReqDTO.getService());
		    if(listServiceTypeSelectorMappingVOs==null || (listServiceTypeSelectorMappingVOs!=null && listServiceTypeSelectorMappingVOs.isEmpty())) {
		    	 throw new BTSLBaseException("PretupsUIReportsController", methodName,
							PretupsErrorCodesI.CCE_ERROR_INVALID_SERVICE_KEYWORD, 0, null);
		    }
		
		}

	
		 return reportInputKeyValMap;
	}
	
	
	
	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,AddtnlCommSummryReqDTO addtnlCommSummryReqDTO,
			HashMap<String,String> reportInputKeyValMap) {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();
		Date currentDate = new Date();
		String reportDate =BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));
		  
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.ONE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.REPORT_DATE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.ONE.getNumValue()),
				new SearchInputDisplayinRpt(reportDate, PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		String period= PretupsI.PERIOD_MONTHLY;
		 if(addtnlCommSummryReqDTO.getDailyOrmonthlyOption()!=null && addtnlCommSummryReqDTO.getDailyOrmonthlyOption().trim().toUpperCase().equals(PretupsI.PERIOD_DAILY))
		 {
			 period=PretupsI.PERIOD_DAILY;
		 }
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				period	, PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(period, PretupsRptUIConsts.ONE.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_FROMDATE.getReportValues(),
						null)	, PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_FROMDATE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_TODATE.getReportValues(),
						null), PretupsRptUIConsts.THREE.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_TODATE.getReportValues()), PretupsRptUIConsts.FOUR.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_GEOGRAPHY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(reportInputKeyValMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		
		
		
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
			reportheadervalue[0] = "                  " + RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_HEADER_DISPLAYVALUE.getReportValues(),
					null)  ;
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

			Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();
			
			
			MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();
			Date currentDate = new Date();
			String reportDate =BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));
	
			String[] inputRow1 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.REPORT_DATE.getReportValues(),
					null) + " : "
					+ reportDate };
			csvWriter.writeNext(inputRow1);
			
			
			String[] inputRow2 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DAILY_MONTHLY.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DAILY_MONTHLY.getReportValues()) };
			csvWriter.writeNext(inputRow2);


			String[] inputRow3 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_FROMDATE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_FROMDATE.getReportValues()) };
			csvWriter.writeNext(inputRow3);

	
			String[] inputRow4 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_TODATE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_TODATE.getReportValues()) };
			csvWriter.writeNext(inputRow4);
			
			
			String[] inputRow5 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_DOMAIN.getReportValues()) };
			csvWriter.writeNext(inputRow5);
			
			String[] inputRow6 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_CATEORY.getReportValues()) };
			csvWriter.writeNext(inputRow6);
			
			String[] inputRow7 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_NETWORKCODE.getReportValues()) };
			csvWriter.writeNext(inputRow7);
			
			
			String[] inputRow8 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_LABEL_SERVICE.getReportValues()) };
			csvWriter.writeNext(inputRow8);
			
			
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
				AddtnlCommSummaryRecordVO record = (AddtnlCommSummaryRecordVO) dataList.get(i);
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
			reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.ADDTNLCOMMSUMMARY_RPT_HEADER_DISPLAYVALUE.getReportValues(),
					null));
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
				totSummaryColCapture.put(listDiplayColumns.get(col).getColumnName(), String.valueOf(col));
		  }
			
		
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				AddtnlCommSummaryRecordVO record = (AddtnlCommSummaryRecordVO) dataList.get(i);
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
			workbook.write(outputStream);
			fileData = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

		} catch (IOException ie) {
			if (_log.isDebugEnabled()) {
				_log.debug("Exception occured while filling cell value", ie);
			}
		}

		return fileData;
	}

	private Map<String, String> getMappedColumnValue(AddtnlCommSummaryRecordVO addtnlCommSummaryRecordVO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(AddtnlcommSummryDownldCols.TRANSDATE.getColumnName() , addtnlCommSummaryRecordVO.getTransferDateOrMonth());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.LOGIN_ID.getColumnName() , addtnlCommSummaryRecordVO.getLoginID());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.USER_NAME.getColumnName() , addtnlCommSummaryRecordVO.getUserName());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.USER_MOB_NUMBER.getColumnName() , addtnlCommSummaryRecordVO.getUserMobileNumber());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.USER_CATEGORY.getColumnName() , addtnlCommSummaryRecordVO.getUserCategory());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.USER_GEOGRAPHY.getColumnName() , addtnlCommSummaryRecordVO.getUserGeography());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.PARENT_NAME.getColumnName() , addtnlCommSummaryRecordVO.getParentName());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.PARENT_MOB_NUM.getColumnName() , addtnlCommSummaryRecordVO.getParentMobileNumber());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.PARENT_CATEGORY.getColumnName() , addtnlCommSummaryRecordVO.getParentCategory());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.PARENT_GEOGRAPHY.getColumnName() , addtnlCommSummaryRecordVO.getParentGeography());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.OWNER_NAME.getColumnName() , addtnlCommSummaryRecordVO.getOwnerName());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.OWNER_MOB_NUM.getColumnName() , addtnlCommSummaryRecordVO.getOwnerMobileNumber());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.OWNER_CATEGORY.getColumnName() , addtnlCommSummaryRecordVO.getOwnerCategory());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.OWNER_GEOGRAPHY.getColumnName() , addtnlCommSummaryRecordVO.getOwnerGeography());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.SERVICE.getColumnName() , addtnlCommSummaryRecordVO.getService());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.SUB_SERVICE.getColumnName() , addtnlCommSummaryRecordVO.getSubService());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.TRANSACTION_COUNT.getColumnName() , addtnlCommSummaryRecordVO.getTransactionCount());
		mappedColumnValue.put(AddtnlcommSummryDownldCols.DIFFERENTIAL_COMMISSION.getColumnName() , addtnlCommSummaryRecordVO.getDifferentialCommission());
		
						
		return mappedColumnValue;
  }

	public void execute(AddtnlCommSummryReqDTO addtnlCommSummryReqDTO, AddtlnCommSummryDownloadResp response) throws BTSLBaseException {
		// TODO Auto-generated method stub
		final String methodName = "execute";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelUserDAO channelUserDAO = null;
		ChannelUserVO channelUserVO = null;
		Date currentDate = new Date();

		try {

			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Map<String, String> ValidEditColumns = Arrays.asList(AddtnlcommSummryDownldCols.values()).stream()
					.collect(Collectors.toMap(AddtnlcommSummryDownldCols::getColumnName,
							AddtnlcommSummryDownldCols::getColumnName));
			super.validateEditColumns(addtnlCommSummryReqDTO.getDispHeaderColumnList(), ValidEditColumns);
			channelUserDAO = new ChannelUserDAO();
			C2STransferTxnDAO c2STransferTxnDAO  = new C2STransferTxnDAO ();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, addtnlCommSummryReqDTO.getMsisdn());
			
			String extNgCode = addtnlCommSummryReqDTO.getExtnwcode();
			
			//execute search api to get data.
			
			HashMap<String,String> reportInputKeyValMap= validateInputs(con, addtnlCommSummryReqDTO);
			
			String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();
			/*
			 if ( !BTSLUtil.isEmpty(o2CTransfAckDownloadReqDTO.getFileType()) && !o2CTransfAckDownloadReqDTO.getFileType().trim().toUpperCase().equals(allowedFileType.toUpperCase())){
					throw new BTSLBaseException("PretupsUIReportsController", "O",
							PretupsErrorCodesI.INVALID_FILE_FORMAT, 0, null);
			 }*/
			
			 
			AdditionalCommissionSummryC2SResp	additionalCommissionSummryC2SResp = c2STransferTxnDAO.searchAddtnlCommSummryData(con, addtnlCommSummryReqDTO);
			 
			 if(additionalCommissionSummryC2SResp.getAddtnlcommissionSummaryList().isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			 totSummaryColValue.put(AddtnlcommSummryDownldCols.TRANSACTION_COUNT.getColumnName(), additionalCommissionSummryC2SResp.getTotalTransactionCount());
			 totSummaryColValue.put(AddtnlcommSummryDownldCols.DIFFERENTIAL_COMMISSION.getColumnName(), additionalCommissionSummryC2SResp.getTotalDiffAmount());
			
			
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			String fileName = RestAPIStringParser.getMessage(addtnlCommSummryReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSFERDETDOWNLOAD_RPT_FILENAME.getReportValues(),
					null);
			downloadDataFomatReq.setFileName(
					fileName + System.currentTimeMillis());
			downloadDataFomatReq.setFileType(addtnlCommSummryReqDTO.getFileType());
			downloadDataFomatReq.setReportDataList(additionalCommissionSummryC2SResp.getAddtnlcommissionSummaryList());
			downloadDataFomatReq.setDisplayListColumns(addtnlCommSummryReqDTO.getDispHeaderColumnList());
			downloadDataFomatReq.setSearchInputMaprowCell(getSearchInputValueMap(con,addtnlCommSummryReqDTO,reportInputKeyValMap));
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);

			O2CTransfDetDownloadColumns [] downloadSequenceAttributes = O2CTransfDetDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (O2CTransfDetDownloadColumns o2cColusequence : downloadSequenceAttributes) {
				reportColumnSeq.append(o2cColusequence.getColumnName());
				reportColumnSeq.append(",");
			}
			String reportColumnSeqStr = reportColumnSeq.toString().substring(0, reportColumnSeq.length() - 1);
			downloadDataFomatReq.setColumnSequenceNames(reportColumnSeqStr);
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);
			String fileData = createFileFormatPassbook(downloadDataFomatReq);

			response.setFileData(fileData);
			response.setFileType(downloadDataFomatReq.getFileType().toLowerCase());
			response.setFileName(downloadDataFomatReq.getFileName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsErrorCodesI.SUCCESS,
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