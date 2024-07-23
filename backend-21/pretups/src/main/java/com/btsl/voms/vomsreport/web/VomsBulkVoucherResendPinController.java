package com.btsl.voms.vomsreport.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.voucher.service.BulkVoucherResendPinService;

/**
 * This class provide the methods for Bulk Voucher resend pin
 * @author Hargovind Karki
 *@since 12/01/2017
 */
@Controller
public class VomsBulkVoucherResendPinController extends CommonController{

	private static final String FORWARD="voms/bulkVoucherResendPin";
	
	
	@Autowired
	private BulkVoucherResendPinService bulkVoucherResendPinService;
	
	private static final String FAILUPLOAD= "failUpload";
	private static final String PROBLEM_FILE_DOWNLOAD = "problem.file.upload";
	
	//private static Locale _locale = null;
   // private static int row = 0;
    //private static CellStyle style = null;
    //MessageResources p_messages = null;
 
    
    
	
	/**
	 * Load bar user UI as well as modules and user type 
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws Exception
	 */
	@RequestMapping(value = "/voucherenquiry/vomsBulkVoucherResendPin.form", method = RequestMethod.GET)
	public String loadBulkVoucherPinResendForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
			 {

		String MethodName= "loadBulkVoucherPinResendForm";
		if (log.isDebugEnabled()) {
			log.debug(MethodName, PretupsI.ENTERED);
		}

		authorise(request, response, "BVPINRS01", false);
		
		if (log.isDebugEnabled()) {
			log.debug(MethodName, PretupsI.EXITED);
		}
		return "voms/bulkVoucherResendPin";
	}
	
	
	
	/**
	 * Download The List OF User IN Approval Status
	 * @param pUserStatus
	 * @param modelMap
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/voucherenquiry/download-bulk-pin-resend.form", method = RequestMethod.POST)
	public void downloadUserList(String pUserStatus, 
			final ModelMap modelMap, HttpServletRequest request , HttpServletResponse response) throws BTSLBaseException {

		final String methodName = "downloadUserList";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		
		
		 String fileName = null;
		 fileName = Constants.getProperty("DownloadVoucherResendPinEnqtFileName");
		 response.setHeader("Content-Disposition", "attachment; filename="+fileName+BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()+"."+PretupsI.FILE_CONTENT_TYPE_XLSX.toLowerCase()+"");
		 
		 
		try {
			writeToExcel(ExcelFileIDI.VOUCHER_RESEND_PIN_ENQ,response.getOutputStream(),fileName);
		} catch (Exception e) {
			log.debug(methodName, "Exception"+e.getMessage());
	   	    log.errorTrace(methodName, e);
		} 
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}
		return ;

	}
	
	
	/**
	 * To call for writing the excel sheet
	 * @author 
	 * @param p_excelID
	 * @param resp
	 * @param p_fileName 
	 * @param p_con
	 * @param p_dateCount
	 * @throws Exception
	 */
    public void writeToExcel(String p_excelID, OutputStream resp, String p_fileName) throws IOException,BTSLBaseException {
    	final String methodName = "writeModifyExcel";            
        if (log.isDebugEnabled()) {
            log.debug(methodName, " p_excelID: " + p_excelID  + " p_fileName: " + p_fileName);
        }
        SXSSFSheet  worksheet1 = null;
        SXSSFWorkbook  workbook = null;
        CellStyle style = null;
        try {
        	
        	workbook = new SXSSFWorkbook();  
        	workbook.setCompressTempFiles(true);
        	workbook.getCreationHelper();
        	
        	style = workbook.createCellStyle();
        	Font times16font = workbook.createFont();
        	times16font.setFontName("Arial");
        	
        	times16font.setBold(true);
        	//times16font.setFontHeightInPoints((short) 14);
        	times16font.setFontHeightInPoints(BTSLUtil.parseIntToShort(14));
        	style.setFont(times16font);
        	
        	worksheet1 = (SXSSFSheet) workbook.createSheet(p_fileName);
        	writeLabel(worksheet1,style);
    		workbook.write(resp);
    		resp.close();
    			
            if(log.isDebugEnabled()) {
               log.debug(methodName," has been generated successfully for p_dateCount = ");
                }
                  
        } 
        catch (BTSLBaseException e) {
            log.errorTrace(methodName, e);
            throw e;
        } 
        catch (IOException e) {
            log.errorTrace(methodName, e);
            throw e;
        } finally {
        	
        	try {
			    if (workbook != null) {
			        workbook.close();
			    }
			} catch (Exception e) {
			    log.errorTrace(methodName, e);
			}
        	
        	//workbook.close();
            //worksheet1 = null;
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting");
            }
        }
    }
    
	
	
    /**
	 * To write label into the excel sheet 
	 * @author diwakar
	 * @param worksheet1 
	 */
	public void writeLabel(SXSSFSheet worksheet1, CellStyle style) throws BTSLBaseException {
		String keyName;
		Row rowdata = null;
	 	Cell cell = null;
	 	int col = 0;
	 	int row =0;
	 	
			
		
	 	row = row+2;
	 	col=col+1;
	 	
	 	keyName = PretupsRestUtil.getMessageString("bulk.voucher.pin.resend.report.xlsfile.voucherdetails");
	 	rowdata = worksheet1.createRow(row);
	    //cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
	 	cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
	    cell.setCellStyle(style);
		cell.setCellValue(keyName);
		cell.setCellType(CellType.STRING);
	 	
	 	col=0;
	 	row=row+1;
	 	
	 	keyName = PretupsRestUtil.getMessageString("bulk.voucher.pin.resend.report.xlsfile.transactionid");
        rowdata = worksheet1.createRow(row);
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		cell.setCellType(CellType.STRING);
	
		keyName = PretupsRestUtil.getMessageString("bulk.voucher.pin.resend.report.xlsfile.transactionDate");
		col=col+1;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		cell.setCellType(CellType.STRING);
		
        
		keyName = PretupsRestUtil.getMessageString("bulk.voucher.pin.resend.report.xlsfile.retailerMSIDSN");
		col=col+1;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		cell.setCellType(CellType.STRING);
		
		keyName = PretupsRestUtil.getMessageString("bulk.voucher.pin.resend.report.xlsfile.customerMSISDN");
		col=col+1;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		cell.setCellType(CellType.STRING);
		
		keyName = PretupsRestUtil.getMessageString("bulk.voucher.pin.resend.report.xlsfile.serialNo");
        col=col+1;
        cell = rowdata.createCell(BTSLUtil.parseIntToShort(col));
		cell.setCellStyle(style);
		cell.setCellValue(keyName);
		cell.setCellType(CellType.STRING);
		
	}
	
	/**
	 * this method approve the request in file records
	 * @param file
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/voucherenquiry/upload-bulk-voucher-resend-pin.form", method = RequestMethod.POST)
	public String uploadBulkVoucherResendPin(@RequestParam("file") MultipartFile file ,ModelMap modelMap,  HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException {

		final String methodName = "uploadBulkVoucherResendPin";

		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}

		if(file==null || file.isEmpty()){
			modelMap.put(FAILUPLOAD ,PretupsRestUtil.getMessageString("file.is.empty"));
			return FORWARD;
		}	


		String filePath = Constants.getProperty("BULK_VOUCHER_PINRESEND_FILEPATH");
		String fileName = file.getOriginalFilename();
		UserVO userVO = this.getUserFormSession(request);

		boolean isFileUpload = false;

		try{
			isFileUpload = uploadFileToServer(file , filePath , file.getSize());
		}
		catch( BTSLBaseException be){	
			log.debug(methodName, be.getMessage());
			log.error(methodName, be);
			modelMap.put(FAILUPLOAD ,be.getMessageKey());
			return FORWARD;
		}

		try{
			if(isFileUpload)
			{
				modelMap.put("Success" ,"Success");
				bulkVoucherResendPinService.bulkVoucherPinResendProcess(filePath,fileName,modelMap,userVO);
			}
			else
				modelMap.put(FAILUPLOAD ,PretupsRestUtil.getMessageString(PROBLEM_FILE_DOWNLOAD));


		} catch (Exception e) {

			log.debug(methodName, e);
			modelMap.put(FAILUPLOAD ,PretupsRestUtil.getMessageString("error.jsp.processing"));
		}


		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}

		return FORWARD;

	}

	/**
	 * this method use to upload file to server and some basic validation to file 
	 * @param file
	 * @param filePath
	 * @param fileSize
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	public boolean uploadFileToServer(MultipartFile file , String filePath , Long fileSize) throws BTSLBaseException
	{

		File fileUpload = null;
		final String methodName = "uploadFileToServer";
		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.ENTERED);
		}
		if(!validateFileDetails(file, filePath, fileSize))
			throw new BTSLBaseException(PROBLEM_FILE_DOWNLOAD);

		String fileName= file.getOriginalFilename();
		fileUpload = new File(filePath+"/"+fileName);
		if(fileUpload.exists())
			throw new BTSLBaseException("file.already.exist");
		try(BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(fileUpload));
				FileOutputStream outputStream = new FileOutputStream(filePath+"/"+fileName);){

			byte[] bytes = file.getBytes();
			stream.write(bytes);

		}catch(IOException io)
		{
			if (log.isDebugEnabled()) {
				log.debug(methodName,io);
			}
			throw new BTSLBaseException(PROBLEM_FILE_DOWNLOAD);
		}
		finally{

			if (log.isDebugEnabled()) {
				log.debug(methodName,PretupsI.EXITED);
			}
		}

		return true;


	}

	/**
	 * Return File Extension
	 * 
	 * @param fileName
	 * @return
	 */
	public String getFileExtn(String fileName) {

		final String methodName = "getFileExtn";
		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.ENTERED);
		}
		String extn = "";
		log.debug("getFIleExtn", "FILENAME :"+fileName);

		int i = fileName.lastIndexOf('.');
		if(i>0)
			extn = fileName.substring(i+1);


		log.debug("getFIleExtn", "FILENAME :"+extn);
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.EXITED);
		}
		return extn.trim();
	}


	/**
	 * file validation before upload
	 * 
	 * @param file
	 * @param filePath
	 * @param fileSize
	 * @return
	 * @throws BTSLBaseException
	 */
	private boolean validateFileDetails(MultipartFile file , String filePath , Long fileSize) throws BTSLBaseException
	{
		
		final String methodName = "validateFileDetails";
		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.ENTERED+" filePath : fileFize "+filePath+" : "+fileSize);
		}
		
		validateFile(file,fileSize);
		File fileDir = new File(filePath);
		if (( !fileDir.isDirectory()) && ( !fileDir.mkdir()) ) {
		    // recover from error or throw an exception
			throw new BTSLBaseException(this,methodName,"downloadfile.error.dirnotcreated","error");
		  }
		if(!fileDir.exists())
			throw new BTSLBaseException("dir.not.exist");

		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.EXITED);
		}
		
		
		return true;
	}

	/**
	 * file validation before upload
	 * 
	 * @param file
	 * @param filePath
	 * @param fileSize
	 * @return
	 * @throws BTSLBaseException
	 */
	private void validateFile(MultipartFile file,Long fileSize) throws BTSLBaseException
	{
		if(!BTSLUtil.isValideFileName(file.getOriginalFilename()))
			throw new BTSLBaseException("File Name is not Valid");
		else if(fileSize>Long.parseLong(Constants.getProperty("BULK_VOUCHER_RESENDPIN_FILE_SIZE")))
			throw new BTSLBaseException("File Size Exceed");
		else if(!getFileExtn(file.getOriginalFilename()).equalsIgnoreCase(PretupsI.FILE_CONTENT_TYPE_XLSX))
			throw new BTSLBaseException("File Content Type Not Valid should be XLSX");
		
		
	}
	
	
	
	
	
}
