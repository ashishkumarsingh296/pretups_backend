package com.btsl.pretups.master.businesslogic;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

public class BatchGeographicalDomainRestServiceImpl implements BatchGeographicalDomainRestService{
	 public static final Log _log = LogFactory.getLog(BatchGeographicalDomainRestServiceImpl.class.getName());
	
	/** 
	 * this method provide the list of geographical domain and geographical domain types for template master data
	 * @param requestData
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @return
	 * @author vikas.chaudhary
	 * @since 11/07/2016
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse< byte[]> downloadList(String requestData) throws BTSLBaseException{
		final String METHOD_NAME = "BatchGeographicalDomainRestServiceImpl[downloadUserList()]";
		if (_log.isDebugEnabled())
			_log.debug(METHOD_NAME, PretupsI.ENTERED+" : requestData :"+requestData);
		
		byte[] excelFilebytes;
		Connection con=null;
		MComConnectionI mcomCon = null;
		ArrayList<GeographicalDomainTypeVO> geographicalDomainTypeList = null;
		ArrayList<GeographicalDomainVO> geographicalDomainDetailList = null;
		
		try{
			PretupsResponse< byte[]> response = new PretupsResponse<byte[]>();
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(METHOD_NAME, "SQLException" + e);
    			_log.errorTrace(METHOD_NAME,e);
			}
			
			GeographicalDomainWebDAO geoDomainDao = new GeographicalDomainWebDAO();
			geographicalDomainTypeList = geoDomainDao.loadDomainTypes(con, -1 , 10);
			geographicalDomainDetailList = geoDomainDao.loadGeoDomainList(con, map.get("networkID").toString());
			
			excelFilebytes = writeTemplateDataToXLS(geographicalDomainTypeList, geographicalDomainDetailList);
			
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, excelFilebytes);
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "file.download.successfully");
			return response;
			
		}
		catch (BTSLBaseException | IOException e) {
			throw new BTSLBaseException(e);
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("BatchGeographicalDomainRestServiceImpl#downloadList");
				mcomCon = null;
			}
			printLog(METHOD_NAME, PretupsI.EXITED);
		}
	}

	
	/**this method Print Log
	 * @param methodName
	 * @param log
	 */
	private void printLog(String methodName , String log)
	{
		if (_log.isDebugEnabled())
			_log.debug(methodName, log);
	}
	
	/**
	 * USE FOR WRITING DATA TO XLS FILE AND RETUNR BYTE ARRAY
	 * @param dataObject
	 * @return
	 * @throws BTSLBaseException
	 */
	private byte[] writeTemplateDataToXLS(List<GeographicalDomainTypeVO> dataObject , List<GeographicalDomainVO> domainDataObject) throws BTSLBaseException
	{
		final String methodName = "BatchGeographicalDomainRestServiceImpl[writeTemplateDataToXLS()]";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED +" DataObject : "+dataObject);
		}
		byte[] excelFilebytes = null;
		try(ByteArrayOutputStream bos = new ByteArrayOutputStream();) {
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			
			String keyName ;
			HSSFSheet sheet = workbook.createSheet("Template Sheet");
			HSSFSheet sheet1 = workbook.createSheet("Master Sheet");
			sheet1.protectSheet("protected");
			int rowNum = 0;
			int headerCell = 0;
			//Header Style
			HSSFCellStyle headerStyle = workbook.createCellStyle(); 
			HSSFFont font = workbook.createFont();
			font.setFontName(HSSFFont.FONT_ARIAL);
			font.setBold(true);
			headerStyle.setFont(font);
			HSSFCellStyle lockedNumericStyle = workbook.createCellStyle(); 			// Locking Style for xls file 
			lockedNumericStyle.setLocked(false);
			
			//Template Sheet Start
			HSSFRow row = sheet.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.parentGeoCode");
			HSSFCell cell =  row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			
			try {
				sheet.autoSizeColumn(headerCell-1);
			}catch (Exception e) {
				_log.error("", "Error occurred while autosizing columns");
				e.printStackTrace();
			}
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.geographicaldomaintype");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.geographicalCode");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.geographicaldomainName");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.geographicalDomainShortName");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.geoDomainDescription");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			sheet.autoSizeColumn(headerCell-1);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.templatesheet.geographicalDomainAction");
			cell = row.createCell(headerCell++);
			cell.setCellValue(keyName);
			cell.setCellStyle(headerStyle);
			
			// End of Template Sheet Start
			
			//Master Sheet Start
			rowNum = 0;
			headerCell = 0;
			HSSFRow row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainnotelabel");
			HSSFCell cell1 =  row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			cell1.setCellStyle(headerStyle);
			sheet1.autoSizeColumn(headerCell-1);
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainNotelabel1");
			cell1 = row1.createCell(headerCell);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainNotelabel2");
			cell1 = row1.createCell(headerCell);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			headerCell = 0;
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainactionlabel");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			cell1.setCellStyle(headerStyle);
			sheet1.autoSizeColumn(headerCell-1);
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainactionlabel1");
			cell1 = row1.createCell(headerCell);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainactionlabel2");
			cell1 = row1.createCell(headerCell);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainactionlabel3");
			cell1 = row1.createCell(headerCell);
			cell1.setCellValue(keyName);
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainactionlabel4");
			cell1 = row1.createCell(headerCell);
			cell1.setCellValue(keyName);
			
			rowNum++;
			headerCell = 0;
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomaintype");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			cell1.setCellStyle(headerStyle);
			sheet1.autoSizeColumn(headerCell-1);
			
			headerCell = 0;
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomaintype");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainName");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainParentType");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainSequence");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			//geographical Domain Type List here
			
			for (GeographicalDomainTypeVO geoDomainTypeVO : dataObject) {
				int cellNum = 0;
				row1 = sheet1.createRow(rowNum++);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainTypeVO.getGrphDomainType());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainTypeVO.getGrphDomainTypeName());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainTypeVO.getGrphDomainParent());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainTypeVO.getGrphDomainSequenceNo());
				
			}
			
			rowNum++;
			rowNum++;
			rowNum++;
			headerCell = 0;
			
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainHeading");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			cell1.setCellStyle(headerStyle);
			sheet1.autoSizeColumn(headerCell-1);
			
			headerCell = 0;
			row1 = sheet1.createRow(rowNum++);
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainCode");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainName");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainShortName");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomaintype");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainStatus");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			keyName = PretupsRestUtil.getMessageString("master.xlsfile.mastersheet.geographicaldomainIsDefault");
			cell1 = row1.createCell(headerCell++);
			cell1.setCellValue(keyName);
			sheet1.autoSizeColumn(headerCell-1);
			
			//geographical Domains  List here
			
			for (GeographicalDomainVO geoDomainVO : domainDataObject) {
				int cellNum = 0;
				row1 = sheet1.createRow(rowNum++);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainVO.getGrphDomainCode());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainVO.getGrphDomainName());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainVO.getGrphDomainShortName());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainVO.getGrphDomainType());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainVO.getStatus());
				sheet1.autoSizeColumn(cellNum-1);
				cell1 = row1.createCell(cellNum++);
				cell1.setCellValue(geoDomainVO.getIsDefault());
				
			}
			//End of Master Sheet data
			
			for(int column = 0; column<7; column++)
			{
				sheet.autoSizeColumn(column);
				sheet1.autoSizeColumn(column);
			}
			workbook.write(bos);
			excelFilebytes = bos.toByteArray();
				}
		catch (IOException ioe) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ioe);
			}
			throw new BTSLBaseException("master.createbatchgeographicaldomains.error.file.writing");
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			throw new BTSLBaseException("master.createbatchgeographicaldomains.error.file.writing");
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return excelFilebytes;
	}


	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> initiateBatchGeographicalDomainCreation(String requestData) throws BTSLBaseException {
		final String methodName = "BatchGeographicalDomainServiceImpl[initiateBatchGeographicalDomainCreation()]";
		ArrayList<GeographicalDomainTypeVO> geographicalDomainTypeList = null;
		ArrayList<GeographicalDomainVO> geographicalDomainDetailList = null;
		Connection con=null;
		MComConnectionI mcomCon = null;
		if (_log.isDebugEnabled())
			_log.debug(methodName, PretupsI.ENTERED+" with : requestData : "+requestData);
		try{
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName, "SQLException" + e);
    			_log.errorTrace(methodName,e);
			}
			PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
			Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<Map<String, Object>>() {});
			Map<String, Object> map = (Map<String, Object>) dataMap.get("data");
			if(!map.containsKey("batchName") ||!map.containsKey("filePath") || !map.containsKey("fileName") || !map.containsKey("userID") || !map.containsKey("networkID") ){
				printLog(methodName ,  " mendatory tags are  : MISSING : should Contains = batchName, filePath , fileName , userID , networkID ");
				response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, null);
				response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "tag.is.missing");
				return response;
			}
			final String batchName = map.get("batchName").toString();
			final String filePath = map.get("filePath").toString();
			final String fileName = map.get("fileName").toString();
			final String fileDirPath = filePath+"/"+fileName; // Full File Path 
			
			UserVO userVO = new UserVO();
			userVO.setUserID(map.get("userID").toString());
			userVO.setNetworkID(map.get("networkID").toString());
			
			GeographicalDomainWebDAO geoDomainDao = new GeographicalDomainWebDAO();
			geographicalDomainTypeList = geoDomainDao.loadDomainTypes(con, -1 , 10);
			geographicalDomainDetailList = geoDomainDao.loadGeoDomainList(con, map.get("networkID").toString());
			
			response = initiateBatchRequest(userVO, fileDirPath, fileName, batchName, geographicalDomainTypeList, geographicalDomainDetailList);
			
			return response;
		
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("BatchGeographicalDomainRestServiceImpl#initiateBatchGeographicalDomainCreation");
				mcomCon = null;
			}
			printLog(methodName, PretupsI.EXITED);
		}
	}


	@SuppressWarnings("unchecked")
	private PretupsResponse<List<ListValueVO>> initiateBatchRequest(UserVO pUserVO, String fileDirPath, String fileName, String batchName,ArrayList<GeographicalDomainTypeVO> geographicalDomainTypeList, ArrayList<GeographicalDomainVO> geographicalDomainDetailList) throws BTSLBaseException {

		final String methodName = "BatchGeographicalDomainRestServiceImpl[initiateBatchRequest()]";
		if (_log.isDebugEnabled()) 
			_log.debug(methodName, PretupsI.ENTERED+" : fileDirPath : "+fileDirPath+" , fileName : "+fileName,"pUserVO : "+pUserVO+" batchName : "+batchName);
		
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		List<BatchGeographicalDomainVO> geoList = new ArrayList<BatchGeographicalDomainVO>();
		List<ListValueVO> errorFileList = new ArrayList<ListValueVO>();
		List<ListValueVO> dbErrorList = new ArrayList<ListValueVO>();
		BatchGeographicalDomainDAO batchGeoDomainDAO = new BatchGeographicalDomainDAO();
		String batchID = null;
		
		// File Parsing and storing all records data into List
		try{
			mcomCon = new MComConnection();
			try{con=mcomCon.getConnection();}catch(SQLException e){
				_log.error(methodName, "SQLException" + e);
    			_log.errorTrace(methodName,e);
				
			}
			int totalNoRecords = BatchGeographicalDomainUtils.fileParsingForRecords(fileDirPath , fileName , geoList, batchName, errorFileList, pUserVO, geographicalDomainTypeList, geographicalDomainDetailList);
			int rowCount = totalNoRecords + errorFileList.size();
			int maxRowSize = -1;
			try {
				maxRowSize =Integer.parseInt(Constants.getProperty("MAX_RECORDS_BATCH_GRPH_DOMAIN_CREATION"));
			} catch (NumberFormatException e) {

				printLog(methodName, "The maximum limit of records is not defined.");
				response.setStatus(true);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setParameters(new String[] {});
				response.setFormError("master.createbatchgeographicaldomains.error.maxlimitofrecnotdefined");
				return response;
			}
			
			if(totalNoRecords==0){
				printLog(methodName, "No Records Found in File");
				response.setStatus(true);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				response.setParameters(new String[] {});
				response.setFormError("master.createbatchgeographicaldomains.error.norecordinfile");
				return response;
			} else if (rowCount > maxRowSize) {
				printLog(methodName, "The maximum limit of records ("+maxRowSize+") in the file has reached.");
				response.setStatus(true);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setParameters(new String[] {String.valueOf(maxRowSize)});
				response.setFormError("master.createbatchgeographicaldomains.error.maxlimitofrecsreached");
				return response;
			}
			
			if(geoList != null && !geoList.isEmpty()) {
				dbErrorList = batchGeoDomainDAO.addGeographicalDomainsList(con, (ArrayList<BatchGeographicalDomainVO>) geoList, pUserVO,fileName);
			}
			
			if(dbErrorList !=null && !dbErrorList.isEmpty()) {
				int size = dbErrorList.size();
				ListValueVO errVO = (ListValueVO)dbErrorList.get(size-1);
				batchID = errVO.getOtherInfo2();
				dbErrorList.remove(size-1);
			    errorFileList.addAll(dbErrorList);
			}
			
			if(!errorFileList.isEmpty()){
				if(BTSLUtil.isNullString(batchID)) {
					response.setFormError("master.createbatchgeographicaldomains.message.error.report");
				} else {
					response.setFormError("master.createbatchgeographicaldomains.message.error.report.batchID");
				}
				response.setStatus(true);
				response.setParameters(new String[] { String.valueOf(totalNoRecords-errorFileList.size()) , String.valueOf(totalNoRecords) , batchID});
				response.setDataObject(errorFileList);
				response.setStatusCode(PretupsI.RESPONSE_SUCCESS);
				return response;
			}
			
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,errorFileList);
			response.setParameters(new String[] { String.valueOf(totalNoRecords) , batchID});
			response.setResponse(PretupsI.RESPONSE_SUCCESS, true, "master.createbatchgeographicaldomains.message.success.report");
			return response;
		}
		catch (BTSLBaseException be) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName , be);
			}
			throw new BTSLBaseException(be);
		
		}finally {
			if (mcomCon != null) {
				mcomCon.close("BatchGeographicalDomainRestServiceImpl#initiateBatchRequest");
				mcomCon = null;
			}
			printLog(methodName, PretupsI.EXITED);
		}
	
	}
}
