package com.restapi.user.service;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.FileWriteUtil;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.channel.user.businesslogic.BatchUserDAO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.master.businesslogic.SubLookUpDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.scheduletopup.process.BatchFileParserI;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.xl.BatchUserCreationExcelRWPOI;
import com.btsl.pretups.xl.DvdBatchDistExcel;
import com.btsl.user.businesslogic.ProductTypeDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.web.pretups.channel.user.businesslogic.BatchUserWebDAO;

import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/**
 * Service implementation file for downloading templates 
 * @author pankaj.rawat
 *
 */
@Service("DownloadTemplateService")
public class DownloadTemplateServiceImpl implements DownloadTemplateService{

	protected final Log log = LogFactory.getLog(getClass().getName());
	public void downloadC2CBatchTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadC2CBatchTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
	    Connection con=null;
	    MComConnectionI mcomCon;
	    mcomCon = new MComConnection();
    	try {
			con=mcomCon.getConnection();
		
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadBatchC2CUserListFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        ProductTypeDAO productTypeDAO = new ProductTypeDAO();
	       ArrayList<C2sBalanceQueryVO> userProdList = new ArrayList<C2sBalanceQueryVO>();        
	       userProdList=productTypeDAO.getProductsDetails(con);
	       List <String>productCodes=new ArrayList<String>();
	       List <String>productNames=new ArrayList<String>();
	       for(C2sBalanceQueryVO prod:userProdList) {
	    	   productCodes.add(prod.getProductShortCode());
	    	   productNames.add(prod.getProductName());
	       }
	       String productCodess=String.join(",", productCodes);
	       String productNamess=String.join(",", productNames);
        fileName = Constants.getProperty("DownloadBatchC2CUserTemplateListFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final int cols = 7;
        final int rows = 1;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        fileArr[0][0][0][0] = "batchc2c.xlsheading.label.msisdn";
        fileArr[0][0][1][0] = "batchc2c.xlsfile.details.msisdn.comment";
        fileArr[0][1][0][0] = "batchc2c.xlsheading.label.loginid";
        fileArr[0][1][1][0] = "batchc2c.xlsfile.details.loginID.comment";
        fileArr[0][2][0][0] = "batchc2c.xlsheading.label.externalcode";
        fileArr[0][2][1][0] = "batchc2c.xlsfile.details.externalCode.comment";
        fileArr[0][3][0][0] = "batchc2c.xlsheading.label.quantity";
        fileArr[0][3][1][0] = "";
        fileArr[0][4][0][0] = "batchc2c.xlsheading.label.product";
        fileArr[0][4][1][0] = "batchc2c.xlsheading.label.product.comment";
        fileArr[0][4][1][1] = productNamess;
        fileArr[0][4][1][2] = productCodess;
        fileArr[0][5][0][0] = "batchc2c.xlsheading.label.remarks";
        fileArr[0][5][1][0] = "";
        
	       
	       
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    	}catch (SQLException e1) {
			log.errorTrace(methodName, e1);
            log.error(methodName, "Exception" + e1.getMessage());
            throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

		}
        finally {
			if (mcomCon != null) {
				mcomCon.close("DownloadTemplateServiceImpl#downloadC2CBatchTemplate");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("downloadC2CBatchTemplate", "Exiting:" );
            }
			
		}
    }
	
	
	public void downloadGiftTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadGiftTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
				//(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final int cols = 7;
        final int rows = 2;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        fileArr[0][3][0][0]="restrictedsubs.scheduletopupdetails.file.customermsisdn.heading";
        fileArr[1][0][0][0] = "restrictedsubs.scheduletopupdetails.file.label.msisdn";
        fileArr[1][1][0][0] = "restrictedsubs.scheduletopupdetails.file.label.subservice" + ":" + getSubService(PretupsI.SERVICE_TYPE_CHANNEL_GIFT_RECHARGE);
        fileArr[1][2][0][0] = "restrictedsubs.scheduletopupdetails.file.label.reqamt";
        fileArr[1][3][0][0] = "restrictedsubs.scheduletopupdetails.file.label.receiverlanguage";
        fileArr[1][4][0][0] = "restrictedsubs.scheduletopupdetails.file.label.giftermsisdn";
        fileArr[1][5][0][0] = "restrictedsubs.scheduletopupdetails.file.label.giftername";
        fileArr[1][6][0][0] = "restrictedsubs.scheduletopupdetails.file.label.gifterlanguage";
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	
	
	public void downloadRechargeTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadRechargeTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
				//(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final int cols = 4;
        final int rows = 2;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        fileArr[0][1][0][0]="restrictedsubs.scheduletopupdetails.file.customermsisdn.heading";
        fileArr[1][0][0][0] = "restrictedsubs.scheduletopupdetails.file.label.msisdn";
        fileArr[1][1][0][0] = "restrictedsubs.scheduletopupdetails.file.label.subservice" + ":" + getSubService(PretupsI.SERVICE_TYPE_CHNL_RECHARGE);
        fileArr[1][2][0][0] = "restrictedsubs.scheduletopupdetails.file.label.reqamt";
        fileArr[1][3][0][0] = "restrictedsubs.scheduletopupdetails.file.label.languagecode";
        
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	
	public void downloadFixedTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadFixedTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final int cols = 4;
        final int rows = 2;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        fileArr[0][1][0][0]="restrictedsubs.scheduletopupdetails.file.pstnmsisdn.heading";
        fileArr[1][0][0][0] = "restrictedsubs.scheduletopupdetails.file.label.msisdn";
        fileArr[1][1][0][0] = "restrictedsubs.scheduletopupdetails.file.label.subservice" + ":" + getSubService(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_PSTN);
        fileArr[1][2][0][0] = "restrictedsubs.scheduletopupdetails.file.label.reqamt";
        fileArr[1][3][0][0] = "restrictedsubs.scheduletopupdetails.file.label.notificationMsisdn";
    
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	
	public void downloadInternetTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadInternetTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
				//(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final int cols = 4;
        final int rows = 2;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        fileArr[0][1][0][0]="restrictedsubs.scheduletopupdetails.file.internatemsisdn.heading";
        fileArr[1][0][0][0] = "restrictedsubs.scheduletopupdetails.file.label.msisdn";
        fileArr[1][1][0][0] = "restrictedsubs.scheduletopupdetails.file.label.subservice" + ":" + getSubService(PretupsI.SERVICE_TYPE_CHNL_RECHARGE_INTR);
        fileArr[1][2][0][0] = "restrictedsubs.scheduletopupdetails.file.label.reqamt";
        fileArr[1][3][0][0] = "restrictedsubs.scheduletopupdetails.file.label.notificationMsisdn";
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	 private String getSubService(String serviceType){
	    	if (log.isDebugEnabled()) {
	            log.debug("getSubService", "Entered:" );
	        }
	        final ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();
	        String subService = new String();
	        subService = "(";
	        Connection con = null;MComConnectionI mcomCon = null;
	        try {
				mcomCon = new MComConnection();
			  con=mcomCon.getConnection();
	        ServiceSelectorMappingVO serviceSelectorMappingVO = new ServiceSelectorMappingVO();
	        ArrayList selectorList = new ArrayList<>();
			selectorList = serviceSelectorMappingDAO.loadServiceSelectorMappingDetails(con, serviceType);
			int selectorLists=selectorList.size();
	        for (int i = 0; i <selectorLists ; i++) {
	            serviceSelectorMappingVO = (ServiceSelectorMappingVO) selectorList.get(i);
	            final ListValueVO listVO = new ListValueVO(serviceSelectorMappingVO.getSelectorName(), serviceSelectorMappingVO.getSelectorCode());
	            subService = subService + listVO.getLabel() + "=" + listVO.getValue()+"|";
	        }
	        subService = subService.substring(0,subService.length() - 1)+  ")";
	        } catch (BTSLBaseException | SQLException e) {
				e.printStackTrace();
			}finally{
				if (mcomCon != null) {
					mcomCon.close("RestrictedTopUpAction#confirm");
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	                log.debug("getSubService", "Exiting:" );
	            }
				
			}
			return subService;
	    }

	/*
	 *  (non-Javadoc)
	 * @see com.restapi.user.service.DownloadTemplateService#downloadDvdMasterSheet(com.restapi.user.service.FileDownloadResponse)
	 */
	@Override
	public void downloadDvdMasterSheet(Connection con, FileDownloadResponse fileDownloadResponse, String userID, String networkID)
			throws BTSLBaseException, RowsExceededException, WriteException, IOException {
		final String methodName = "downloadDvdMasterSheet";
		String [] voucherType = {"digital","test_digit"};
	    String filePath = null;
	    String fileName = null;
		VomsVoucherDAO vomsVoucherDAO = new VomsVoucherDAO();

	
    	filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = PretupsI.MASTER_SHEET + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + "xls";
     
        File fileNew = new File(filePath + fileName);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        Locale locale = new Locale(lang,country);
        HashMap<String, ArrayList<CardGroupDetailsVO>> voucherDetails = new HashMap<>();
        
        ArrayList<CardGroupDetailsVO> digitalList = null;
		digitalList = vomsVoucherDAO.returnVoucherDetailsWithCount(con, userID, voucherType, networkID);
		voucherDetails.put(BatchFileParserI.VOUCHER_LIST, digitalList);
        
        DvdBatchDistExcel dvdBatchDistExcel = new DvdBatchDistExcel();
        
        
		dvdBatchDistExcel.fillDatainMasterSheet(filePath + fileName, locale, voucherDetails, null);
	
		
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType("xls");
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
     
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	/**
	 * Dvd batch download blank template method
	 */
	public void downloadDvdTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadDvdTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
				//(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadRestrictedMSISDNFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final int cols = 6;
        final int rows = 3;
        fileArr = new String[rows][cols][2][6]; // ROW-COL
        fileArr[0][0][0][0] = "voms.dvd.heading.batch";
        fileArr[1][0][0][0] = "dvd.scheduletopupdetails.file.label.service";
        fileArr[1][1][0][0] = "voms.dvd.heading.batch";
        fileArr[2][0][0][0] = "dvd.scheduletopupdetails.file.label.msisdn";
        fileArr[2][1][0][0] = "dvd.scheduletopupdetails.file.label.voucherType";
        fileArr[2][2][0][0] = "dvd.scheduletopupdetails.file.label.voucherSegment";
        fileArr[2][3][0][0] = "dvd.scheduletopupdetails.file.label.denomination";
        fileArr[2][4][0][0] = "dvd.scheduletopupdetails.file.label.ptofileid";
        fileArr[2][5][0][0] = "dvd.scheduletopupdetails.file.label.numberOfVouchers";
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	
	
	
	/**
	 * 
	 * @param fileDownloadResponse
	 * @throws BTSLBaseException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws IOException
	 */
	public void downloadO2CWithdrawTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadDvdTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
	    final Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
				//(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadBatchO2CListFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated", "selectCategoryForBatchC2C");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
        final String keyName = "heading.mandatory";
        final int cols = 6;
        final int rows = 2;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        
        fileArr[0][0][0][0] = "batcho2c.xlsheading.label.msisdn";
        
        if (!BTSLUtil.isNullString(externalTxnMandatory) && externalTxnMandatory.indexOf("1") != -1) {
        	externalTxnMandatory=PretupsI.YES;
        }
        
        
        if (PretupsI.YES.equals(externalTxnMandatory)) {

            fileArr[0][1][0][0] = "batcho2c.xlsheading.label.exttxnnumbermandt";
            fileArr[0][2][0][0]="batcho2c.withdraw.xlsheading.label.extntxndate";
        } else {
            fileArr[0][1][0][0] = "batcho2c.xlsheading.label.exttxnnumber";
            fileArr[0][2][0][0]="batcho2c.withdraw.xlsheading.label.extntxndate";
        }

      boolean  externalCodeMandatory = SystemPreferences.EXTERNAL_CODE_MANDATORY_FORO2C;
      
        if(externalCodeMandatory) {
        fileArr[0][3][0][0] = "batcho2c.xlsheading.label.externalcodeMndt";
        }else {
        	fileArr[0][3][0][0] = "batcho2c.xlsheading.label.externalcode";	
        }
        
        fileArr[0][4][0][0] = "batcho2c.xlsheading.label.quantity";
        fileArr[0][5][0][0] = "batcho2c.xlsheading.label.remarks";
        
        fileArr[1][0][0][0] = keyName;
   
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	
	
	/**
	 * 
	 * @param fileDownloadResponse
	 * @throws BTSLBaseException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws IOException
	 */
	public void downloadO2CPurchaseTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException{
		final String methodName = "downloadO2cPurchaseTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
	    final Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		String fileExt = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
				//(String)PreferenceCache.getSystemPreferenceValue(PreferenceI.C2C_BATCH_FILEEXT);
    	filePath = Constants.getProperty("DownloadBatchO2CUserListFilePath");
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
             log.errorTrace(methodName, e);
             log.error(methodName, "Exception" + e.getMessage());
             throw new BTSLBaseException(this, methodName, "downloadfile.error.dirnotcreated");

        }
        fileName = Constants.getProperty("DownloadRestrictedMSISDNFileName") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final String keyName = "batch.bar.for.deletion.sec.mandatory.comment";
        String externalTxnMandatory = SystemPreferences.EXTERNAL_TXN_MANDATORY_FORO2C;
        
        final int cols = 7;
        final int rows = 2;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
       // fileArr[0][0][0][0] = keyName;
        
        fileArr[0][0][0][0] = "batcho2c.xlsheading.label.msisdn";
        
        fileArr[0][1][0][0] = "batcho2ctrf.xlsheading.label.exttxnnumber";
        fileArr[0][2][0][0] = "batcho2c.xlsheading.label.extntxndate";
      
        fileArr[0][2][1][0]= "Date.format";
        fileArr[0][3][0][0] = "batcho2c.xlsheading.label.paymenttype";
        
        boolean  externalCodeMandatory = SystemPreferences.EXTERNAL_CODE_MANDATORY_FORO2C;
        
        if(externalCodeMandatory) {
        	fileArr[0][4][0][0] = "batcho2c.xlsheading.label.externalcodeMndt";
        }else {
        	fileArr[0][4][0][0] = "batcho2c.xlsheading.label.externalcode";	
        }
        
        fileArr[0][5][0][0] = "batcho2c.xlsheading.label.quantity";
        fileArr[0][6][0][0] = "batcho2c.xlsheading.label.remarks";
   
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_O2C_INITIATE, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String lang = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);
        String country = (String)PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
        String resmsg  = RestAPIStringParser.getMessage(new Locale(lang,country), PretupsErrorCodesI.SUCCESS, null);
        
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
	
	public void downloadBulkUserTemplate(Connection con, UserVO userVO, String domainCode, String geographyCode, 
			FileDownloadResponse fileDownloadResponse, HttpServletResponse responseswag)
			throws BTSLBaseException, SQLException, ParseException, IOException {

		final HashMap masterDataMap = new HashMap();
		final BatchUserDAO batchUserDAO = new BatchUserDAO();
		final BatchUserWebDAO batchUserWebDAO = new BatchUserWebDAO();
		final SubLookUpDAO sublookupDAO = new SubLookUpDAO();
		final BatchUserCreationExcelRWPOI excelRW = new BatchUserCreationExcelRWPOI();

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

		final String userType = userVO.getUserType();
		String filePath = Constants.getProperty("DownloadBulkUserPath");

		try {
			final File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
		} catch (Exception e) {
			log.error("loadDownloadFile", "Exception" + e.getMessage());
			throw new BTSLBaseException(this, "downloadBulkUserTemplate", "downloadfile.error.dirnotcreated");

		}

		String domainName = getDomianName(con, userVO, domainCode);
		String geographyName = getGeographyName(userVO, geographyCode);

		final String fileName = domainCode + Constants.getProperty("DownloadBulkUserFileNamePrefix")
				+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".xlsx";

		setupBulkUserMasterData(con, masterDataMap, batchUserDAO, batchUserWebDAO, sublookupDAO, userVO, domainName,
				domainCode, geographyName, geographyCode);

		excelRW.writeUserCreateExcel(ExcelFileIDI.BATCH_USER_INITIATE, masterDataMap, null, locale,
				filePath + fileName);

		File fileNew = new File(filePath + fileName);
		byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		String file1 = fileNew.getName();
		
		
		fileDownloadResponse.setFileattachment(encodedString);
		fileDownloadResponse.setFileType("xlsx");
		fileDownloadResponse.setFileName(file1);
		fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
		fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
		String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
		fileDownloadResponse.setMessage(resmsg);
		responseswag.setStatus(PretupsI.RESPONSE_SUCCESS);
	}

	private void setupBulkUserMasterData(Connection con, HashMap masterDataMap, BatchUserDAO batchUserDAO,
			BatchUserWebDAO batchUserWebDAO, SubLookUpDAO sublookupDAO, UserVO userVO, String domainName,
			String domainCode, String geographyName, String geographyCode) throws BTSLBaseException {
		
		String userType = userVO.getUserType();
		
		// Cache Data Variables
		boolean isTrfRuleUserLevelAllow = (boolean) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW);
		boolean userVoucherTypeAllowed = (boolean) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.USER_VOUCHERTYPE_ALLOWED);
		boolean batchUserProfileAssign = (boolean) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.BATCH_USER_PROFILE_ASSIGN);
		masterDataMap.put(PretupsI.BATCH_USR_CREATED_BY, userVO.getUserName());
		masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_NAME, geographyName);
		masterDataMap.put(PretupsI.BATCH_USR_DOMAIN_NAME, domainName);
		masterDataMap.put(PretupsI.BATCH_USR_USER_PREFIX_LIST,
				LookupsCache.loadLookupDropDown(PretupsI.USER_NAME_PREFIX_TYPE, true));
		masterDataMap.put(PretupsI.BATCH_USR_OUTLET_LIST, LookupsCache.loadLookupDropDown(PretupsI.OUTLET_TYPE, true));
		masterDataMap.put(PretupsI.BATCH_USR_SUBOUTLET_LIST,
				sublookupDAO.loadSublookupByLookupType(con, PretupsI.OUTLET_TYPE));
		final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
		masterDataMap.put(PretupsI.BATCH_USR_SERVICE_LIST,
				servicesDAO.loadServicesList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, null, false));
		masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_LIST,
				batchUserWebDAO.loadMasterGeographyList(con, geographyCode, userVO.getUserID()));
		// /////////////////////////////
		masterDataMap.put(PretupsI.BATCH_USR_GEOG_LIST, batchUserWebDAO.loadGeographyList(con));
		masterDataMap.put(PretupsI.BATCH_USR_COMM_LIST, batchUserWebDAO.loadCommProfileList(con,
				domainCode, userVO.getNetworkID(), userVO.getCategoryCode(), userType));
		masterDataMap.put(PretupsI.BATCH_USR_GEOGRAPHY_TYPE_LIST,
				batchUserWebDAO.loadCategoryGeographyTypeList(con, domainCode));
		masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_HIERARCHY_LIST,
				batchUserWebDAO.loadMasterCategoryHierarchyList(con, domainCode, userVO.getNetworkID()));
		// Added by Shashank
		// Change made for batch user creation by channel users
		masterDataMap.put(PretupsI.BATCH_USR_CATEGORY_LIST,
				batchUserDAO.loadMasterCategoryList(con, domainCode, userVO.getCategoryCode(), userType));
		masterDataMap.put(PretupsI.USER_TYPE, userType);
		masterDataMap.put(PretupsI.BATCH_USR_LANGUAGE_LIST, batchUserDAO.loadLanguageList(con));// added
		// by
		// deepika
		// aggarwal
		masterDataMap.put(PretupsI.BATCH_USR_GROUP_ROLE_LIST, batchUserDAO.loadMasterGroupRoleList(con,
				domainCode, userVO.getCategoryCode(), userType));
		masterDataMap.put(PretupsI.BATCH_USR_GRADE_LIST, batchUserDAO.loadMasterCategoryGradeList(con,
				domainCode, userVO.getCategoryCode(), userType));
		masterDataMap.put(PretupsI.USER_DOCUMENT_TYPE,
				LookupsCache.loadLookupDropDown(PretupsI.USER_DOCUMENT_TYPE, true));
		masterDataMap.put(PretupsI.PAYMENT_INSTRUMENT_TYPE,
				LookupsCache.loadLookupDropDown(PretupsI.PAYMENT_INSTRUMENT_TYPE, true));
		if (userType.equals(PretupsI.OPERATOR_USER_TYPE)
				|| (userType.equals(PretupsI.CHANNEL_USER_TYPE) && batchUserProfileAssign))

		{

			masterDataMap.put(PretupsI.BATCH_USR_TRANSFER_CONTROL_PRF_LIST, batchUserDAO.loadMasterTransferProfileList(
					con, domainCode, userVO.getNetworkID(), userVO.getCategoryCode(), userType));

			if (isTrfRuleUserLevelAllow) {
				masterDataMap.put(PretupsI.BATCH_USR_TRF_RULE_LIST,
						LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
			}
		}
		if (userVoucherTypeAllowed)
			masterDataMap.put(PretupsI.BATCH_OPT_USR_VOUCHERTYPE_LIST, new VomsProductDAO().loadVoucherTypeList(con));

	}

	private String getGeographyName(UserVO userVO, String geographyCode) {

		String geographyName = "";
		final ArrayList geoList = new ArrayList();
		UserGeographiesVO geographyVO = null;
		final ArrayList userGeoList = userVO.getGeographicalAreaList();
		if (userGeoList != null) {
			if (userGeoList.size() == 1) {
				geographyVO = (UserGeographiesVO) userGeoList.get(0);
				geographyName = geographyVO.getGraphDomainName();
			} else {
				for (int i = 0, k = userGeoList.size(); i < k; i++) {
					geographyVO = (UserGeographiesVO) userGeoList.get(i);
					geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
				}
			}
		}

		if (geographyCode.equals(PretupsI.ALL)) {
			String geographyCode1 = "";
			for (int i = 0, j = geoList.size(); i < j; i++) {
				geographyCode1 = geographyCode1 + ((ListValueVO) geoList.get(i)).getValue() + ",";
			}
			geographyName = geographyCode1.substring(0, geographyCode1.length() - 1);
		} else if (geoList != null && geoList.size() > 1) {
			geographyName = BTSLUtil.getOptionDesc(geographyCode, geoList).getLabel();
		}

		return geographyName;

	}

	private String getDomianName(Connection con, UserVO userVO, String domainCode)
			throws BTSLBaseException, SQLException {

		String domainName = "";
		ArrayList domainList = userVO.getDomainList();
		if ((domainList == null || domainList.isEmpty()) &&

				PretupsI.YES.equals(userVO.getCategoryVO().getDomainAllowed())
				&& PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
			domainList = new DomainDAO().loadCategoryDomainList(con);
		}

		if (domainList != null && domainList.size() == 1) {
			domainName = (String) ((ListValueVO) domainList.get(0)).getLabel();
		} else if (domainList != null && domainList.size() > 1) {
			domainName = BTSLUtil.getOptionDesc(domainCode, domainList).getLabel();
		}

		return domainName;

    }

	/**
	 * 
	 * @param fileDownloadResponse
	 * @throws BTSLBaseException
	 * @throws RowsExceededException
	 * @throws WriteException
	 * @throws IOException
	 */
	public void downloadC2SBulkReversalTemplate(FileDownloadResponse fileDownloadResponse) throws BTSLBaseException, RowsExceededException, WriteException, IOException {
		final String methodName = "downloadC2SBulkReversalTemplate";
		String fileArr[][][][] = null;
	    String filePath = null;
	    String fileName = null;
	    final Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));
		String fileExt = "xls";
    	filePath = Constants.getProperty("DownloadBatchC2SReversalPath");
    	
        try {
             final File fileDir = new File(filePath);
             if (!fileDir.isDirectory()) {
                 fileDir.mkdirs();
             }
        } catch (Exception e) {
        	log.errorTrace(methodName, e);
        	log.error(methodName, "Exception" + e.getMessage());
			throw new BTSLBaseException(this, "loadDownloadFile", "downloadfile.error.dirnotcreated", "selectfile");
	
        }
        fileName = Constants.getProperty("DownloadBatchC2SREVNamePrefix") + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime() + "." + fileExt;
        final String keyName = "batchc2s.rev.xlsheading.label.txnID";
        
        final int cols = 1;
        final int rows = 1;
        fileArr = new String[rows][cols][2][3]; // ROW-COL
        fileArr[0][0][0][0] = keyName;
   
        if("csv".equals(fileExt))
        	FileWriteUtil.writeinCSVTemplate(ExcelFileIDI.BATCH_C2S_TXN_REV, fileArr, filePath + "" + fileName);
        else if("xls".equals(fileExt))
        	FileWriteUtil.writeinXLSTemplate(ExcelFileIDI.BATCH_C2S_TXN_REV, fileArr, filePath + "" + fileName);
        else if("xlsx".equals(fileExt))
        	 FileWriteUtil.writeinXLSXTemplate(ExcelFileIDI.BATCH_C2S_TXN_REV, fileArr, filePath + "" + fileName);
	
        File fileNew = new File(filePath + "" + fileName);
        byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        String file1 = fileNew.getName();
        fileDownloadResponse.setFileattachment(encodedString);
        fileDownloadResponse.setFileType(fileExt);
        fileDownloadResponse.setFileName(file1);
        fileDownloadResponse.setStatus(PretupsI.RESPONSE_SUCCESS);
        fileDownloadResponse.setMessageCode(PretupsErrorCodesI.SUCCESS);
        String resmsg  =  RestAPIStringParser.getMessage(locale,PretupsErrorCodesI.SUCCESS,null);
        fileDownloadResponse.setMessage(resmsg);
        if(log.isDebugEnabled())
        {
        	log.debug(methodName, "Download Response" + resmsg);
        	log.debug(methodName, " Exiting");
        }
    }
}