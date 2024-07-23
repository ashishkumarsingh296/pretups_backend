package com.restapi.c2s.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.DispHeaderColumn;
import com.btsl.pretups.channel.transfer.businesslogic.DownloadDataFomatReq;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDTO;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAckDownloadResp;
import com.btsl.pretups.channel.transfer.businesslogic.GetO2CTransferAcknowledgeResp;
import com.btsl.pretups.channel.transfer.businesslogic.LTHDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.LowThreshHoldRecordVO;
import com.btsl.pretups.channel.transfer.businesslogic.O2CAcknowdgeDownloadColumns;
import com.btsl.pretups.channel.transfer.businesslogic.O2CTransfAckDownloadReqDTO;
import com.btsl.pretups.channel.transfer.businesslogic.SearchInputDisplayinRpt;
import com.btsl.pretups.channel.transfer.requesthandler.PretupsUIReportsController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.common.PretupsRptUIConsts;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.opencsv.CSVWriter;

/**
 * 
 * @author Subesh KCV
 *
 */
@Service("O2CTransAcknowldgeReportProcess")
public class O2CTransAcknowldgeReportProcess extends CommonService {
	protected final Log _log = LogFactory.getLog(getClass().getName());
	StringBuilder loggerValue = new StringBuilder();

	public void getO2CTransAckReportSearch(O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO, GetO2CTransferAcknowledgeResp response)
			throws BTSLBaseException {

		final String methodName = "getO2CTransAckReportSearch";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");

		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ChannelTransferDAO channelTransferDAO  = new ChannelTransferDAO (); 

		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			
			 List<GetO2CTransferAckDTO> listGetO2CTransferAckDTO= channelTransferDAO.searchO2CTransferAcknowlegeDetails(con, getO2CTransfAcknReqVO);
			 
			 if(listGetO2CTransferAckDTO.isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			 response.setListO2CTransferAckDTO(listGetO2CTransferAckDTO);
			String success = Integer.toString(PretupsI.RESPONSE_SUCCESS);
			response.setStatus(success);
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(getO2CTransfAcknReqVO.getLocale(),
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

	
	public HashMap<String,String> validateInputs(Connection con,O2CTransfAckDownloadReqDTO getO2CTransfAcknReqVO) throws BTSLBaseException {
		final String methodName ="validateInputs";
		HashMap<String,String> reportInputKeyValMap= new HashMap<String,String>();
	
		 if(!getO2CTransfAcknReqVO.getDistributionType().equals(PretupsI.STOCK) && !getO2CTransfAcknReqVO.getDistributionType().equals(PretupsI.VOUCHER) ) {
				throw new BTSLBaseException("PretupsUIReportsController", methodName,
						PretupsErrorCodesI.INVALID_DISTRIBUTION_TYPE, 0, null);
		 }
		 reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(), getO2CTransfAcknReqVO.getDistributionType());
		 reportInputKeyValMap.put(PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_TRANSACTIONID.getReportValues(), getO2CTransfAcknReqVO.getTransactionID());
		 return reportInputKeyValMap;
	}
	
	
	
	private MultiValuedMap<String, SearchInputDisplayinRpt> getSearchInputValueMap(Connection con,O2CTransfAckDownloadReqDTO o2CTransfAckDownloadReqDTO,
			ChannelUserVO channelUserVO ,HashMap<String,String> reportInputs) {

		MultiValuedMap<String, SearchInputDisplayinRpt> mapMultipleColumnRow = new ArrayListValuedHashMap<>();

		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransfAckDownloadReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(),
						null)		
				
				, PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.TWO.getNumValue()),
				new SearchInputDisplayinRpt(reportInputs.get(PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues()), PretupsRptUIConsts.ONE.getNumValue()));
		
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()), new SearchInputDisplayinRpt(
				RestAPIStringParser.getMessage(o2CTransfAckDownloadReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_TRANSACTIONID.getReportValues(),
						null)
				, PretupsRptUIConsts.ZERO.getNumValue()));
		mapMultipleColumnRow.put(String.valueOf(PretupsRptUIConsts.THREE.getNumValue()),
				new SearchInputDisplayinRpt(o2CTransfAckDownloadReqDTO.getTransactionID(), PretupsRptUIConsts.ONE.getNumValue()));
		
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
			reportheadervalue[0] = "     " +   RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(),  PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
					null);
			csvWriter.writeNext(reportheadervalue);
			String[] blankLine = { "" };
			csvWriter.writeNext(blankLine);

			Map<String, String> inputParamMap = downloadDataFomatReq.getInputParamMap();

			String[] inputRow1 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_TRANSACTIONID.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_TRANSACTIONID.getReportValues()) };
			csvWriter.writeNext(inputRow1);

	
			String[] inputRow2 = { RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues(),
					null) + " : "
					+ inputParamMap.get(PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_LABEL_DISTRIBUTIONTYPE.getReportValues()) };
			csvWriter.writeNext(inputRow2);

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
				GetO2CTransferAckDTO record = (GetO2CTransferAckDTO) dataList.get(i);
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

	@SuppressWarnings("resource")
	private String createXlsFileFormatPassbook(DownloadDataFomatReq downloadDataFomatReq) {
		final String methodName = "createXlsFileFormatPassbook";
		String fileData = null;
		List<DispHeaderColumn> listDiplayColumns = downloadDataFomatReq.getDisplayListColumns();
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			
			Workbook workbook = null;
			
			if(PretupsI.FILE_CONTENT_TYPE_XLSX.equalsIgnoreCase(downloadDataFomatReq.getFileType().toUpperCase())) {
				workbook = new XSSFWorkbook();
			} else {
				workbook = new HSSFWorkbook();
			}

					
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
			reportHeadingCell.setCellValue(RestAPIStringParser.getMessage(downloadDataFomatReq.getLocale(), PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_HEADER_DISPLAYVALUE.getReportValues(),
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
		  }
			
		
			// Display report column data
			for (int i = 0; i < dataList.size(); i++) {

				GetO2CTransferAckDTO record = (GetO2CTransferAckDTO) dataList.get(i);
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
			workbook.write(outputStream);
			fileData = new String(Base64.getEncoder().encode(outputStream.toByteArray()));

		} catch (IOException ie) {
			if (_log.isDebugEnabled()) {
				_log.debug("Exception occured while filling cell value", ie);
			}
		}

		return fileData;
	}

	private Map<String, String> getMappedColumnValue(GetO2CTransferAckDTO getO2CTransferAckDTO) {
		Map<String, String> mappedColumnValue = new LinkedHashMap<String, String>();
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.DATE_TIME.getColumnName(),
				getO2CTransferAckDTO.getDateTime());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TRANSACTION_ID.getColumnName(),
				getO2CTransferAckDTO.getTransactionID());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.USER_NAME.getColumnName(),
				getO2CTransferAckDTO.getUserName());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.STATUS.getColumnName(),
				getO2CTransferAckDTO.getStatus());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.DOMAIN.getColumnName(),
				getO2CTransferAckDTO.getDomain());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.CATEGORY.getColumnName(),
				getO2CTransferAckDTO.getCategory());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.GEOGRAPHY.getColumnName(),
				getO2CTransferAckDTO.getGeography());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.MOBILE_NUMBER.getColumnName(),
				getO2CTransferAckDTO.getMobileNumber());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.NETWORK_NAME.getColumnName(),
				getO2CTransferAckDTO.getNetworkName());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.COMMISION_PROFILE.getColumnName(),
				getO2CTransferAckDTO.getCommissionProfile());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TRANSFER_PROFILE.getColumnName(),
				getO2CTransferAckDTO.getTransferProfile());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TRANSFER_TYPE.getColumnName(),
				getO2CTransferAckDTO.getTransferType());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TRANSFER_TYPE.getColumnName(),
				getO2CTransferAckDTO.getDateTime());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TRANS_DATE_EXTERNAL.getColumnName(),
				getO2CTransferAckDTO.getTransDateExternal());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.REFERENCE_NUMBER.getColumnName(),
				getO2CTransferAckDTO.getReferenceNumber());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.ERP_CODE.getColumnName(),
				getO2CTransferAckDTO.getErpCode());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.ADDRESS.getColumnName(),
				getO2CTransferAckDTO.getAddress());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PRODUCT_SHORT_CODE.getColumnName(),
				getO2CTransferAckDTO.getProductShortCode());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PRODUCT_NAME.getColumnName(),
				getO2CTransferAckDTO.getProductName());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.DENOMINATION.getColumnName(),
				getO2CTransferAckDTO.getDenomination());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.QUANTITY.getColumnName(),
				getO2CTransferAckDTO.getQuantity());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.APPROVED_QUANTITY.getColumnName(),
				getO2CTransferAckDTO.getApprovedQuantity());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.LEVEL1_APPROVED_QUANTITY.getColumnName(),
				getO2CTransferAckDTO.getLevel1ApprovedQuantity());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.LEVEL2_APPROVED_QUANTITY.getColumnName(),
				getO2CTransferAckDTO.getLevel2ApprovedQuantity());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.LEVEL3_APPROVED_QUANTITY.getColumnName(),
				getO2CTransferAckDTO.getLevel3ApprovedQuantity());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX1_RATE.getColumnName(),
				getO2CTransferAckDTO.getTax1Rate());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX1_TYPE.getColumnName(),
				getO2CTransferAckDTO.getTax1Type());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX1_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getTax1Amount());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX2_RATE.getColumnName(),
				getO2CTransferAckDTO.getTax2Rate());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX2_TYPE.getColumnName(),
				getO2CTransferAckDTO.getTax2Type());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX2_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getTax2Amount());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TAX3_RATE.getColumnName(),
				getO2CTransferAckDTO.getTax3Rate());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.COMMISSION_RATE.getColumnName(),
				getO2CTransferAckDTO.getCommisionRate());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.COMMISSION_TYPE.getColumnName(),
				getO2CTransferAckDTO.getCommisionType());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.COMMISSION_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getCommisionAmount());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.RECEIVER_CREDIT_QUANTIY.getColumnName(),
				getO2CTransferAckDTO.getReceiverCreditQuantity());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.CBC_RATE.getColumnName(),
				getO2CTransferAckDTO.getCbcRate());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.CBC_TYPE.getColumnName(),
				getO2CTransferAckDTO.getCbcType());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.CBC_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getCbcAmount());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.DENOMINATION_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getDenominationAmount());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PAYABLE_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getPayableAmount());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.NET_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getNetAmount());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PAYMENT_INSTRUMENT_NUMBER.getColumnName(),
				getO2CTransferAckDTO.getPaymentInstrumentNumber());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PAYMENT_INSTRUMENT_DATE.getColumnName(),
				getO2CTransferAckDTO.getPaymentInstrumentDate());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PAYMENT_INSTRUMENT_AMOUNT.getColumnName(),
				getO2CTransferAckDTO.getPaymentInstrumentAmount());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.PAYMENT_MODE.getColumnName(),
				getO2CTransferAckDTO.getPaymentMode());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.FIRST_APPROVER_REMARKS.getColumnName(),
				getO2CTransferAckDTO.getFirstApprovedRemarks());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.SECOND_APPROVER_REMARKS.getColumnName(),
				getO2CTransferAckDTO.getSecondApprovedRemarks());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.THIRD_APPROVER_REMARKS.getColumnName(),
				getO2CTransferAckDTO.getThirdApprovedRemarks());
		
		

		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.VOUCHER_BATCH_NUMBER.getColumnName(),
				getO2CTransferAckDTO.getVoucherBatchNumber());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.VOMS_PRODUCT_NAME.getColumnName(),
				getO2CTransferAckDTO.getVomsProductName());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.BATCH_TYPE.getColumnName(),
				getO2CTransferAckDTO.getBatchType());
		
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TOTAL_NO_OF_VOUCHERS.getColumnName(),
				getO2CTransferAckDTO.getTotalNoofVouchers());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.FROM_SERIAL_NUMBER.getColumnName(),
				getO2CTransferAckDTO.getFromSerialNumber());
		mappedColumnValue.put(O2CAcknowdgeDownloadColumns.TO_SERIAL_NUMBER.getColumnName(),
				getO2CTransferAckDTO.getToSerialNumber());
		
		// ternaray operator should not used as it will violate in Sonar.
				
		return mappedColumnValue;
}

	public void execute(O2CTransfAckDownloadReqDTO o2CTransfAckDownloadReqDTO, GetO2CTransferAckDownloadResp response) throws BTSLBaseException {
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
			Map<String, String> lhtValidEditColumns = Arrays.asList(O2CAcknowdgeDownloadColumns.values()).stream()
					.collect(Collectors.toMap(O2CAcknowdgeDownloadColumns::getColumnName,
							O2CAcknowdgeDownloadColumns::getColumnName));
			super.validateEditColumns(o2CTransfAckDownloadReqDTO.getDispHeaderColumnList(), lhtValidEditColumns);
			channelUserDAO = new ChannelUserDAO();
			ChannelTransferDAO channelTransferDAO  = new ChannelTransferDAO ();
			channelUserVO = channelUserDAO.loadChannelUserDetails(con, o2CTransfAckDownloadReqDTO.getMsisdn());
			
			String extNgCode = o2CTransfAckDownloadReqDTO.getExtnwcode();
			
			//execute search api to get data.
			
			HashMap<String,String> reportInputKeyValMap= validateInputs(con, o2CTransfAckDownloadReqDTO);
			
			String allowedFileType = SystemPreferences.USER_ALLOW_CONTENT_TYPE.trim();
			/*
			 if ( !BTSLUtil.isEmpty(o2CTransfAckDownloadReqDTO.getFileType()) && !o2CTransfAckDownloadReqDTO.getFileType().trim().toUpperCase().equals(allowedFileType.toUpperCase())){
					throw new BTSLBaseException("PretupsUIReportsController", "O",
							PretupsErrorCodesI.INVALID_FILE_FORMAT, 0, null);
			 }*/
			
			 
			 List<GetO2CTransferAckDTO> listGetO2CTransferAckDTO= channelTransferDAO.searchO2CTransferAcknowlegeDetails(con, o2CTransfAckDownloadReqDTO);
			 
			 if(listGetO2CTransferAckDTO.isEmpty()) {
					 throw new BTSLBaseException("PretupsUIReportsController", methodName,
								PretupsErrorCodesI.NO_RECORD_AVAILABLE, 0, null);
			 }
			 
			
			
			DownloadDataFomatReq downloadDataFomatReq = new DownloadDataFomatReq();
			String fileName = RestAPIStringParser.getMessage(o2CTransfAckDownloadReqDTO.getLocale(), PretupsRptUIConsts.O2CTRANSACKNOWGEDOWNLOAD_RPT_FILENAME.getReportValues(),
					null);
			downloadDataFomatReq.setFileName(
					fileName + System.currentTimeMillis());
			downloadDataFomatReq.setFileType(o2CTransfAckDownloadReqDTO.getFileType());
			downloadDataFomatReq.setReportDataList(listGetO2CTransferAckDTO);
			downloadDataFomatReq.setDisplayListColumns(o2CTransfAckDownloadReqDTO.getDispHeaderColumnList());
			downloadDataFomatReq.setSearchInputMaprowCell(getSearchInputValueMap(con,o2CTransfAckDownloadReqDTO, channelUserVO,reportInputKeyValMap));
			downloadDataFomatReq.setInputParamMap(reportInputKeyValMap);

			O2CAcknowdgeDownloadColumns [] downloadSequenceAttributes = O2CAcknowdgeDownloadColumns.values();
			StringBuilder reportColumnSeq = new StringBuilder();
			for (O2CAcknowdgeDownloadColumns o2cColusequence : downloadSequenceAttributes) {
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
			String resmsg = RestAPIStringParser.getMessage(o2CTransfAckDownloadReqDTO.getLocale(), PretupsErrorCodesI.SUCCESS,
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