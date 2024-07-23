package com.restapi.c2s.services;

import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.batch.businesslogic.BatchesVO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddRptReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStatusRptResp;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserAddStsRespDTO;
import com.btsl.pretups.channel.transfer.businesslogic.BulkUserDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.BulkuserAddStsDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferCommDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.network.businesslogic.NetworkCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("BulkUserAddRptProcess")
public class BulkUserAddRptProcess extends CommonService {
	protected final Log log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();

	public void searchBulkUserAddRpt(BulkUserAddRptReqDTO bulkUserAddRptReqDTO ,ChannelUserVO channelUserVO, BulkUserAddStatusRptResp response)
			throws BTSLBaseException {

		final String methodName = "searchBulkUserAddRpt";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			validateInputs(con, bulkUserAddRptReqDTO);
			BatchUserDAO batchUserDAO = new BatchUserDAO();
			List<BatchesVO> batchesList=new ArrayList<BatchesVO>();
					
			if(bulkUserAddRptReqDTO.getReqTab()!=null && bulkUserAddRptReqDTO.getReqTab().equals(PretupsI.BULKUSER_ADVANCEDTAB_REQ)) {
				batchesList = batchUserDAO.loadBatchListForEnquiry(con, channelUserVO.getNetworkID(), "'" + bulkUserAddRptReqDTO.getGeography() + "'", bulkUserAddRptReqDTO.getDomain(), bulkUserAddRptReqDTO.getFromDate(),
						bulkUserAddRptReqDTO.getToDate(), channelUserVO.getCategoryCode(), channelUserVO.getUserType());
//			 batchesList =batchUserDAO.loadBatchListForUsrEnquiry(con, channelUserVO.getNetworkID(),bulkUserAddRptReqDTO.getGeography(),
//			 bulkUserAddRptReqDTO.getDomain(), bulkUserAddRptReqDTO.getFromDate(), bulkUserAddRptReqDTO.getToDate(), channelUserVO.getCategoryCode(), channelUserVO.getUserType(),channelUserVO.getUserID());
			}else {
//				BatchesVO batchesVO =	batchUserDAO.loadBatchListForUsrEnquiry(con,channelUserVO.getNetworkID(),bulkUserAddRptReqDTO.getBatchNo(),channelUserVO.getUserID());
                BatchesVO batchesVO = batchUserDAO.loadBatchListForEnquiry(con, channelUserVO.getNetworkID(), bulkUserAddRptReqDTO.getBatchNo());
				if(batchesVO!=null) {
					batchesList.add(batchesVO);
				}
			}
			
			 
			 
			 if(batchesList.isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			response.setBulkUserAddStatusRptList(batchesList);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
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
	
	
	
	public void downloadBulkUserAddRpt(BulkUserAddRptReqDTO bulkUserAddRptReqDTO ,ChannelUserVO channelUserVO, BulkuserAddStsDownloadResp response)
			throws BTSLBaseException {

		final String methodName = "searchBulkUserAddRpt";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		HashMap<String,String> reportInputValues = new HashMap<String,String>();
       String fileName =null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			BatchUserDAO batchUserDAO = new BatchUserDAO();
			List<BatchesVO> batchesList=new ArrayList<BatchesVO>();
//				BatchesVO batchesVOData =	batchUserDAO.loadBatchListForUsrEnquiry(con,channelUserVO.getNetworkID(),bulkUserAddRptReqDTO.getBatchNo(),channelUserVO.getUserID());
                BatchesVO batchesVOData = batchUserDAO.loadBatchListForEnquiry(con, channelUserVO.getNetworkID(), bulkUserAddRptReqDTO.getBatchNo());
				if(batchesVOData!=null) {
					batchesList.add(batchesVOData);
				}
			if(batchesList!=null && batchesList.size()>0) {
				BatchesVO batchesVO = (BatchesVO)batchesList.get(0);
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNO.getReportValues(), batchesVO.getBatchID());
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNAME.getReportValues(), batchesVO.getBatchName());
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDBY.getReportValues(), batchesVO.getCreatedBy());
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDON.getReportValues(), batchesVO.getCreatedOnStr());
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHSTATUS.getReportValues(), batchesVO.getStatusDesc());
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_FILENAME.getReportValues(), batchesVO.getFileName());
				reportInputValues.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_TOTALNO.getReportValues(), String.valueOf(batchesVO.getBatchSize()));
	            		
			}else {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			}
		
			
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			fileName = RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
					PretupsRptUIConsts.BULKUSERADD_RPT_FILENAME.getReportValues(), null);
			downloadDataFomatReq.setFileName(fileName + System.currentTimeMillis());
		    downloadDataFomatReq.setFileType(bulkUserAddRptReqDTO.getFileType());
			downloadDataFomatReq.setDisplayListColumns(getDisplayListColumns(bulkUserAddRptReqDTO.getLocale()));
			downloadDataFomatReq
						.setSearchInputMaprowCell(getSearchInputValueMap(con, bulkUserAddRptReqDTO, channelUserVO,reportInputValues));
			downloadDataFomatReq.setInputParamMap(reportInputValues);
			downloadDataFomatReq.setLocale(bulkUserAddRptReqDTO.getLocale());
			
			BulkUserAddStsRespDTO  bulkUserAddStsRespDTO = batchUserDAO.downloadBatchListEnqDetails(con,bulkUserAddRptReqDTO, downloadDataFomatReq);
			response.setFileData(bulkUserAddStsRespDTO.getOnlineDownloadFileData());
			response.setFileType(bulkUserAddRptReqDTO.getFileType());
			response.setFileName(downloadDataFomatReq.getFileName());
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
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



	
	public HashMap<String,String> validateInputs(Connection con,BulkUserAddRptReqDTO bulkUserAddRptReqDTO) throws BTSLBaseException {
		final String methodName ="validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
		Date currentDate = new Date();
		CategoryDAO categoryDAO = new CategoryDAO();
		DomainDAO domainDAO = new DomainDAO();

		String fromDate = bulkUserAddRptReqDTO.getFromDate();
		String toDate = bulkUserAddRptReqDTO.getToDate();
		String extNwCode = bulkUserAddRptReqDTO.getExtnwcode();
		
		if (extNwCode != null && !extNwCode.trim().equals(PretupsI.ALL.trim())) {
			NetworkVO networkVO = (NetworkVO) NetworkCache.getObject(extNwCode);
			if (BTSLUtil.isNullObject(networkVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_NETWORK_CODE, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_NETWORKCODE.getReportValues(), networkVO.getNetworkName());
		}

		
		reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_FROMDATE.getReportValues(), fromDate);
		reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_TODATE.getReportValues(), toDate);
		reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_NETWORKCODE.getReportValues(), extNwCode);
		if(bulkUserAddRptReqDTO.getReqTab()!=null && bulkUserAddRptReqDTO.getReqTab().equals(PretupsI.BULKUSER_ADVANCEDTAB_REQ)) {
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

		reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_DOMAIN.getReportValues(), bulkUserAddRptReqDTO.getDomain());
		if (bulkUserAddRptReqDTO.getDomain() != null
				&& !bulkUserAddRptReqDTO.getDomain().trim().equals(PretupsI.ALL)) {
			DomainVO domainVO = domainDAO.loadDomainVO(con, bulkUserAddRptReqDTO.getDomain());
			if (BTSLUtil.isNullObject(domainVO)) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.GRPH_INVALID_DOMAIN, 0, null);
			}
			reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_DOMAIN.getReportValues(), domainVO.getDomainName());
			
			
			reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_GEOGRAPHY.getReportValues(),PretupsI.ALL);
			if(bulkUserAddRptReqDTO.getGeography()!=null && !bulkUserAddRptReqDTO.getGeography().trim().equals(PretupsI.ALL)) {
			    GeographicalDomainDAO geoDAO = new GeographicalDomainDAO();
			    if (!geoDAO.isGeographicalDomainExist(con, bulkUserAddRptReqDTO.getGeography(), true)) {
			 		 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.EXT_GRPH_INVALID_GEOGRAPHY, 0, null);
			    }
				reportInputKeyValMap.put(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_GEOGRAPHY.getReportValues(),bulkUserAddRptReqDTO.getGeography());
		  }	
			
		}
			
		} else {
			BatchUserDAO batchUserDAO = new BatchUserDAO();
			List batchList =batchUserDAO.loadBatchDetailsListForEnq(con,bulkUserAddRptReqDTO.getBatchNo());
			if(batchList!=null && batchList.size()==0) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.BULKUSERADD_INVALID_BATCHNO, 0, null);
			}
			
		}
		
		
	 
	
		 return reportInputKeyValMap;
	}
	
	
	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,
			BulkUserAddRptReqDTO bulkUserAddRptReqDTO, ChannelUserVO channelUserVO,HashMap<String, String> reportInputParams) throws BTSLBaseException {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();
		
		Date currentDate = new Date();
		String reportDate =BTSLDateUtil.getGregorianDateInString(BTSLDateUtil.getLocaleDateTimeFromDate(currentDate));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(), PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNO.getReportValues(),
						null), PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNO.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));


		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
								PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNAME.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHNAME.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
								PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDBY.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FOUR.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDBY.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
								PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDON.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.FIVE.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_INITIATEDON.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
	
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
								PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHSTATUS.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SIX.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_BATCHSTATUS.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));

			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
					new SearchInputDisplayinRpt(
							RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
									PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_FILENAME.getReportValues(), null),
							PretupsRptUIConsts.ZERO.getNumValue()));
			mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.SEVEN.getNumValue()),
					new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_FILENAME.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));


		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(
						RestAPIStringParser.getMessage(bulkUserAddRptReqDTO.getLocale(),
								PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_TOTALNO.getReportValues(), null),
						PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.EIGHT.getNumValue()),
				new SearchInputDisplayinRpt(reportInputParams.get(PretupsRptUIConsts.BULKUSERADD_RPT_LABEL_TOTALNO.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		
		
		return mapMultipleColumnRow;
	}
	
	
	public List getDisplayListColumns(Locale locale) {
		List<DispHeaderColumn> listDisplayHeadercols = new ArrayList<DispHeaderColumn>();
		BulkUserDownloadColumns[] bulkUserDownloadColumns = BulkUserDownloadColumns.values();
		StringBuilder reportColumnSeq = new StringBuilder();
		for (BulkUserDownloadColumns c2sTcolumsequence : bulkUserDownloadColumns) {
			DispHeaderColumn dispHeaderColumn = new DispHeaderColumn();
			dispHeaderColumn.setColumnName(c2sTcolumsequence.getColumnName());
			dispHeaderColumn.setDisplayName(RestAPIStringParser.getMessage(locale,c2sTcolumsequence.getColumnName(), null));
			listDisplayHeadercols.add(dispHeaderColumn);
		}
       return     listDisplayHeadercols;    		
	}


}