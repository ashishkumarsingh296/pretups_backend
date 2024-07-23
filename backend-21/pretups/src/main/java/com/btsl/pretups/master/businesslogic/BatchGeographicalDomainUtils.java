package com.btsl.pretups.master.businesslogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class BatchGeographicalDomainUtils {
	public static final Log _log = LogFactory.getLog(BatchGeographicalDomainUtils.class.getName());
	private BatchGeographicalDomainUtils(){}
	
	
	
	/**
	 * This method is used to make Archive file on the server.
	 * Method moveFileToArchive.
	 * @param pFilePathAndFileName
	 * @param pFileName
	 * @return
	 */
	public static boolean moveFileToArchive(String pFilePathAndFileName, String pFileName) {
		final String methodName = "BatchGeographicalDomainUtils[moveFileToArchive()]";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED+" fileName : "+pFileName);
		}
		final File fileRead = new File(pFilePathAndFileName);
		File fileArchive = new File("" + Constants.getProperty("BATCH_GRPH_DMN_DIR_PATH_MOVED"));
		if (!fileArchive.isDirectory()) {
			fileArchive.mkdirs();
		}
		fileArchive = new File("" + Constants.getProperty("BATCH_GRPH_DMN_DIR_PATH_MOVED") + pFileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
		final boolean flag = fileRead.renameTo(fileArchive);
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Exiting File Moved=" + flag);
		}
		return flag;
	}// end of moveFileToArchive
	
	
	/**this method parse the file and get the requestList
	 * @param fileDirPath
	 * @param fileName
	 * @param userList
	 * @param rejectUserList
	 * @param errorFileList
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	public static int fileParsingForRecords(String p_fileDirPath ,String p_fileName ,  List<BatchGeographicalDomainVO> p_geoList , String batchName , List<ListValueVO> p_errorFileList , UserVO pUserVO, ArrayList geographicalDomainTypeList, ArrayList geographicalDomainDetailList) throws BTSLBaseException
	{
		final String methodName = "BatchGeographicalDomainUtils[fileParsingForRecords()]";
		int totalNoOfRecords = 0;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED," p_fileDirPath "+p_fileDirPath);
		}
		try(FileInputStream file = new FileInputStream(p_fileDirPath)){
			readDataFromXLS(file , p_geoList , batchName , p_errorFileList, pUserVO, geographicalDomainTypeList,  geographicalDomainDetailList);
			totalNoOfRecords = p_geoList.size() + p_errorFileList.size();
		}
		catch(BTSLBaseException | IOException be)
		{
			if (_log.isDebugEnabled()) 
				_log.debug(methodName, be);
			throw new BTSLBaseException(be);
		}
		finally{
			BatchGeographicalDomainUtils.moveFileToArchive(p_fileDirPath, p_fileName);
			printLog(methodName, PretupsI.EXITED);
		}
		return totalNoOfRecords;
	}

	
	/**
	 * READ DATA FROM XLS FILE AND STORE INTO PASSED LIST
	 * @param file
	 * @param userList
	 * @param rejectUserList
	 * @param errorFileList
	 * @throws BTSLBaseException
	 */
	private static void readDataFromXLS(FileInputStream p_file , List<BatchGeographicalDomainVO> p_geoList , String p_batchName , List<ListValueVO> p_errorFileList , UserVO pUserVO, ArrayList geographicalDomainTypeList, ArrayList geographicalDomainDetailList) throws BTSLBaseException
	{
		final String methodName= "BatchGeographicalDomainUtils[readDataFromXLS()]";
		printLog(methodName, PretupsI.ENTERED);
		try(HSSFWorkbook workbook = new HSSFWorkbook(p_file)){
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			ListValueVO errorVO;
			List<BatchGeographicalDomainVO> reValidateList = new ArrayList<BatchGeographicalDomainVO>();
			
			int r=1;
			HashMap<String, String> geoTypeHashmap=new HashMap<String, String>();
	        HashMap<String, String> geoSequenceHashmap=new HashMap<String, String>();
	        HashMap<String, String> geoDomainCodeHashmap=new HashMap<String, String>();
	        HashMap<String, String> geoDomainNameHashmap=new HashMap<String, String>();
	        HashMap<String, String> geoDomainShortNameHashmap=new HashMap<String, String>();
	        for(int i=0;i<geographicalDomainTypeList.size();i++) {
	        	GeographicalDomainTypeVO typeVO = (GeographicalDomainTypeVO)geographicalDomainTypeList.get(i);
	        	geoTypeHashmap.put(typeVO.getGrphDomainType() , typeVO.getGrphDomainSequenceNo());
	        	geoSequenceHashmap.put(typeVO.getGrphDomainSequenceNo() , typeVO.getGrphDomainType());
	        }
	        int geographicalDomainDetailLists=geographicalDomainDetailList.size();
	        for(int i=0;i<geographicalDomainDetailLists;i++) {
	        	GeographicalDomainVO domainVO = (GeographicalDomainVO)geographicalDomainDetailList.get(i);
	        	geoDomainCodeHashmap.put(domainVO.getGrphDomainCode() , domainVO.getGrphDomainType());
	        	geoDomainNameHashmap.put(domainVO.getGrphDomainName() , domainVO.getGrphDomainType());
	        	geoDomainShortNameHashmap.put(domainVO.getGrphDomainShortName() , domainVO.getGrphDomainType());
	        }
			//Skipping the header of file
			if(rowIterator.hasNext()) {
				rowIterator.next();
				r++;
			}
			while(rowIterator.hasNext())
			{
				Row row = rowIterator.next();
				int i=0;
				//int maxCols=row.getLastCellNum();
				Cell parentCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell geoTypeCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell geoCodeCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell geoNameCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell geoShortNameCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell geoDesCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				Cell geoActionCell = row.getCell(i++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				
				int parentSequence = -1;;
	            int geoDomainSequence = -1;
	            boolean recordToBeSkipped = false;
	            boolean recordToReValidated = false;
				
				//===================== Field Number 1: start of Parent Geographical Code validation =====================
				
				String parentGeoCode = parentCell.getStringCellValue().trim();
				if(BTSLUtil.isNullString(parentGeoCode)) {
	            	errorVO = new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.parentgeocodemissing");
					p_errorFileList.add(errorVO);
					recordToBeSkipped = true;
					continue;
                } else {
	            	if(!geoDomainCodeHashmap.containsKey(parentGeoCode)) {
	            		recordToReValidated =true;
	            	} else {
	            		String parentType = geoDomainCodeHashmap.get(parentGeoCode).toString();
		            	parentSequence = Integer.parseInt(geoTypeHashmap.get(parentType).toString());
		            }
	            }
				//===================== Field Number 1: end of Parent Geographical Code validation =====================
				//===================== Field Number 2: start of Geographical Domain Type validation =====================
				String geoType = geoTypeCell.getStringCellValue().trim();
				if(BTSLUtil.isNullString(geoType)) {
					errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomaintypemissing");
					p_errorFileList.add(errorVO);
					recordToBeSkipped = true;
					continue;
                }
	            else if (!geoTypeHashmap.containsKey(geoType))  
                {
                	errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomaintypinvalid");
                	p_errorFileList.add(errorVO);
                	recordToBeSkipped = true;
                	continue;
                }
	            else if (geoTypeHashmap.containsKey(geoType)) {
	            	//to check if geographical domain is added under correct parent
	            	geoDomainSequence = Integer.parseInt(geoTypeHashmap.get(geoType).toString());
	            	if(!recordToReValidated) {
	            		if( (geoDomainSequence - parentSequence) != 1) {
		            		errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.parentgeodomainnotallowed");
		            		p_errorFileList.add(errorVO);
		            		recordToBeSkipped = true;
							continue;
		            	}
	            	}
	            }
				//===================== Field Number 2: END of Geographical Domain Type validation =====================
				
				//===================== Field Number 3: start of Geographical Domain Code validation =====================
	            String geoDomainCode = geoCodeCell.getStringCellValue().trim();
	            if(BTSLUtil.isNullString(geoDomainCode)) {
					errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomaincodemissing");
					p_errorFileList.add(errorVO);
					recordToBeSkipped = true;
					continue;
                }
                else if (geoDomainCodeHashmap.containsKey(geoDomainCode) && PretupsI.ADD_ACTION.equalsIgnoreCase(geoActionCell.getStringCellValue().trim())) {
                	errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomaincodeexits");
                	p_errorFileList.add(errorVO);
                	recordToBeSkipped = true;
                	continue;
                }
                else if (!geoDomainCodeHashmap.containsKey(geoDomainCode) && PretupsI.MODIFY_ACTION.equalsIgnoreCase(geoActionCell.getStringCellValue().trim())) {
                	errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomaincodeinvalid");
                	p_errorFileList.add(errorVO);
                	recordToBeSkipped = true;
                	continue;
                }
                //===================== Field Number 3: End of Geographical Domain Code validation =====================    
	            
	          //===================== Field Number 4: Geographical Domain Name validation starts here =====================
	            String geoName = geoNameCell.getStringCellValue().trim();
	            if(BTSLUtil.isNullString(geoName)) {
					errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomainnamemissing");
					p_errorFileList.add(errorVO);
					recordToBeSkipped = true;
					continue;
                }
                else if (geoDomainNameHashmap.containsKey(geoName) && PretupsI.ADD_ACTION.equalsIgnoreCase(geoActionCell.getStringCellValue().trim())) {
                	errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomainnameexists");
                	p_errorFileList.add(errorVO);
                	recordToBeSkipped = true;
					continue;
                }
                //===================== Field Number 4: End of Geographical Domain Name validation =====================    
	            
	          //===================== Field Number 5: Geographical Domain Short Name validation starts here =====================
	            String geoShortName = geoShortNameCell.getStringCellValue().trim();
	            if(BTSLUtil.isNullString(geoShortName) ) 
                {
					errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomainshortnamemissing");
					p_errorFileList.add(errorVO);
					recordToBeSkipped = true;
					continue;
                }
                else if (geoDomainShortNameHashmap.containsKey(geoShortName) && PretupsI.ADD_ACTION.equalsIgnoreCase(geoActionCell.getStringCellValue().trim())) {
                	errorVO=new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.geodomainshortnameexists");
                	p_errorFileList.add(errorVO);
                	recordToBeSkipped = true;
					continue;
                }
	            //===================== Field Number 5: End of Geographical Domain Short Name validation =====================    
	            
	          //===================== Field Number 7: Action(A/M/D/Blank validation starts here =====================
                String geoAction = geoActionCell.getStringCellValue().trim().toUpperCase();
	            if(BTSLUtil.isNullString(geoAction))
                {
	            	recordToBeSkipped = true;
	            	r++;
	            	continue;
                }
                else if( !PretupsI.ADD_ACTION.equalsIgnoreCase(geoAction) && !PretupsI.MODIFY_ACTION.equalsIgnoreCase(geoAction) && !PretupsI.DELETE_ACTION.equalsIgnoreCase(geoAction) ) {
                	errorVO = new ListValueVO(String.valueOf(r++),"master.createbatchgeographicaldomains.error.actioninvalid");
                	p_errorFileList.add(errorVO);
                	recordToBeSkipped = true;
					continue;
                }
	            
                //===================== Field Number 7: End of Action(A/M/D/Blank validation =====================    
	            //updating map for code,name and short name cache
	            
	            if(!recordToBeSkipped) {
	            	BatchGeographicalDomainVO geographyDomainVO = new BatchGeographicalDomainVO();
		            
		        	geographyDomainVO.setParentDomainCode(parentGeoCode);
					geographyDomainVO.setGrphDomainType(geoType);
					geographyDomainVO.setGrphDomainCode(geoDomainCode);
					geographyDomainVO.setGrphDomainName(geoName);
					geographyDomainVO.setGrphDomainShortName(geoShortName);	
					geographyDomainVO.setDescription(geoDesCell.getStringCellValue().trim());
					geographyDomainVO.setRecordNumber(String.valueOf(r++));
		        	geographyDomainVO.setNetworkCode(pUserVO.getNetworkID());
		        	geographyDomainVO.setCreatedBy(pUserVO.getUserID());
		        	geographyDomainVO.setCreatedOn(new Date());
		        	geographyDomainVO.setModifiedBy(pUserVO.getUserID());
		        	geographyDomainVO.setModifiedOn(new Date());
		        	geographyDomainVO.setBatchName(p_batchName);
		        	geographyDomainVO.setSequenceNumber(geoDomainSequence);
		        	
		        	if(PretupsI.ADD_ACTION.equalsIgnoreCase(geoAction) || PretupsI.MODIFY_ACTION.equalsIgnoreCase(geoAction)) {
		        		geographyDomainVO.setStatus(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
		        	} else if (PretupsI.DELETE_ACTION.equalsIgnoreCase(geoAction)) {
		        		geographyDomainVO.setStatus(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
		        	}   	        	
		        	geographyDomainVO.setAction(geoAction);
		        	if(recordToReValidated) {
		        		reValidateList.add(geographyDomainVO);
		        	} else {
		        		geoDomainCodeHashmap.put(geoDomainCode, geoType);
			        	geoDomainNameHashmap.put(geoName, geoType);
			        	geoDomainShortNameHashmap.put(geoShortName, geoType);
			        	p_geoList.add(geographyDomainVO);
		        	}
	            }
			}
			
			if(reValidateList != null && !reValidateList.isEmpty()) {
				
				Collections.sort(reValidateList);
				
        		for(BatchGeographicalDomainVO obj : reValidateList) {
        			int parentSequence = -1;
        			int geoDomainSequence = -1;
        			
        			String parentGeoCode = obj.getParentDomainCode();
        			String geoType = obj.getGrphDomainType();
        			
        			if(!geoDomainCodeHashmap.containsKey(parentGeoCode)) {
	            		
	            		errorVO=new ListValueVO(obj.getRecordNumber(),"master.createbatchgeographicaldomains.error.parentgeocodeinvalid");
	            		p_errorFileList.add(errorVO);
	            		continue;
	            	} else {
	            		String parentType = geoDomainCodeHashmap.get(parentGeoCode).toString();
		            	parentSequence = Integer.parseInt(geoTypeHashmap.get(parentType).toString());
		            	geoDomainSequence = Integer.parseInt(geoTypeHashmap.get(geoType).toString());
		            }
        			
        			if( (geoDomainSequence - parentSequence) != 1) {
	            		errorVO=new ListValueVO(obj.getRecordNumber(),"master.createbatchgeographicaldomains.error.parentgeodomainnotallowed");
	            		p_errorFileList.add(errorVO);
						continue;
	            	}
        			geoDomainCodeHashmap.put(obj.getGrphDomainCode(), geoType);
		        	geoDomainNameHashmap.put(obj.getGrphDomainName(), geoType);
		        	geoDomainShortNameHashmap.put(obj.getGrphDomainShortName(), geoType);
        			p_geoList.add(obj);
        		}
        	}
			
			Collections.sort(p_geoList);
		}
		catch(IOException ioe)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ioe);
			}
			throw new BTSLBaseException("master.createbatchgeographicaldomains.message.file.error");
		}
		catch(Exception e)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			throw new BTSLBaseException("master.createbatchgeographicaldomains.message.file.error");
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return;
	}
	
	
	/** use to print log
	 * @param methodName
	 * @param log
	 */
	public static void printLog(String methodName , String log)
	{
		if (_log.isDebugEnabled())
			_log.debug(methodName, log);
	}
}
