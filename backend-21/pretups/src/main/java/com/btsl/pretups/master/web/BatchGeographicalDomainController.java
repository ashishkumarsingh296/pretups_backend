package com.btsl.pretups.master.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.service.BatchGeographicalDomainService;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

@Controller
public class BatchGeographicalDomainController extends CommonController {
	
	private static final String FORWARD="master/batchGeographicalDomain";
	private static final String FAILUPLOAD= "failUpload";
	private static final String FAILDOWNLOAD= "failDownload";
	
	private static final String PROBLEM_FILE_DOWNLOAD = "problem.file.upload";
	
	@Autowired
	private BatchGeographicalDomainService batchGeographicalDomainService;
	
	private ArrayList<GeographicalDomainTypeVO> geographicalDomainTypeList = null;
	private ArrayList<GeographicalDomainVO> geographicalDomainDetailList = null;
	
	
	
	/**
	 * Load  UI for  Batch geographical Domain Creation 
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @return String the path of view 
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/master/batchGeographicalDomain.form", method = RequestMethod.GET)
	public String loadContentList(final ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws  Exception {
		final String METHOD_NAME = "BatchGeographicalDomainController[loadContentList()]";
		
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, PretupsI.ENTERED);
		}
		//authorising the entry point
		authorise(request,response,"BGEDOM001",false);
		
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, PretupsI.EXITED);
		}
		return FORWARD;
		
	}
	

	@RequestMapping(value = "/master/download-geographical-domain-template.form", method = RequestMethod.POST)
	public String downloadList(final ModelMap modelMap, HttpServletRequest request , HttpServletResponse response) throws BTSLBaseException {

		final String METHOD_NAME = "BatchGeographicalDomainController[downloadList()]";
		
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, PretupsI.ENTERED);
		}
		UserVO userVO = this.getUserFormSession(request);
		modelMap.put("networkID",userVO.getNetworkID());
		
		InputStream inputStream = batchGeographicalDomainService.downloadList(modelMap);
		
		downloadFileInCsvFormate(response , inputStream , modelMap);
		 
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, PretupsI.EXITED);
		}
		return FORWARD;
	}
	
	private boolean downloadFileInCsvFormate(HttpServletResponse response,
			InputStream inputStream, ModelMap modelMap) {

		final String methodName = "BatchGeographicalDomainController[downloadFileInCsvFormate()]";
		response.setContentType("text/csv");
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try {
			String fileName= "BATCHGRPHDMN"+BTSLUtil.getFileNameStringFromDate(new Date());
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+"."+PretupsI.FILE_CONTENT_TYPE_XLS.toLowerCase()+"");
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, e);
			}
			modelMap.put(FAILDOWNLOAD ,"error.general.processing");
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}
		return true;
	}
	

	@RequestMapping(value = "/master/batch-initiate-geographical-domain.form", method = RequestMethod.POST)
	public String initiateBatchGeographicalDomainCreation(@RequestParam("file") MultipartFile file,@RequestParam("batchName") String batchName ,ModelMap modelMap,  HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException {

		final String methodName = "BatchGeographicalDomainController[initiateBatchGeographicalDomainCreation()]";

		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		if(BTSLUtil.isNullString(batchName)){
			modelMap.put(FAILUPLOAD ,PretupsRestUtil.getMessageString("master.batchgeographicaldomain.batch.batchname.is.required"));
			return FORWARD;
		}
		if(file==null || file.isEmpty()){
			modelMap.put(FAILUPLOAD ,PretupsRestUtil.getMessageString("file.is.empty"));
			return FORWARD;
		}
		
		String filePath = Constants.getProperty("BATCH_GRPH_DMN_DIR_PATH");
		String fileName = file.getOriginalFilename();
		UserVO userVO = this.getUserFormSession(request);
		
		boolean isFileUpload = false;

		try{
			isFileUpload = uploadFileToServer(file , filePath , file.getSize());
		}
		catch (BTSLBaseException be) {
			log.debug(methodName, be);
			modelMap.put(FAILUPLOAD ,be.getMessageKey());
			return FORWARD;
		}

		try {
			if(isFileUpload)
				batchGeographicalDomainService.initiateBatchGeographicalDomainCreation(batchName, filePath , fileName , userVO , modelMap);
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
	private boolean uploadFileToServer(MultipartFile file , String filePath , Long fileSize) throws BTSLBaseException
	{

		File fileUpload = null;
		final String methodName = "BatchGeographicalDomainController[uploadFileToServer()]";
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
		
		final String methodName = "BatchGeographicalDomainController[validateFileDetails()]";
		
		printLog(methodName,PretupsI.ENTERED+" filePath : fileFize "+filePath+" : "+fileSize);
		
		if(!BTSLUtil.isValideFileName(file.getOriginalFilename()))
			throw new BTSLBaseException("file.name.not.valid");
		else if(fileSize>Long.parseLong(Constants.getProperty("BATCH_GRPH_DMN_FILE_SIZE")))
			throw new BTSLBaseException("file.size.exceed");
		else if(!getFileExtn(file.getOriginalFilename()).equalsIgnoreCase(PretupsI.FILE_CONTENT_TYPE_XLS))
			throw new BTSLBaseException("file.content.type.not.valid");

		File fileDir = new File(filePath);
		if(!fileDir.isDirectory())
			fileDir.mkdirs();
		if(!fileDir.exists())
			throw new BTSLBaseException("dir.not.exist");
		
		printLog(methodName,PretupsI.EXITED);
		
		return true;
	}
	
	/**
	 * Return File Extension
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileExtn(String fileName) {

		final String methodName = "BatchGeographicalDomainController[getFileExtn()]";
		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.ENTERED);
		}
		String extn = "";
		log.debug(methodName, "FILENAME :"+BTSLUtil.logForgingReqParam(fileName));

		int i = fileName.lastIndexOf('.');
		if(i>0)
			extn = fileName.substring(i+1);

		log.debug(methodName, "FILENAME :"+BTSLUtil.logForgingReqParam(extn));
		
		if (log.isDebugEnabled()) {
			log.debug(methodName,PretupsI.EXITED);
		}
		return extn.trim();
	}
	
	/**this method Print Log
	 * @param methodName
	 * @param LOG
	 */
	private void printLog(String methodName , String active) {
		if (log.isDebugEnabled())
			log.debug(methodName, log);
	}
}