package com.client.pretups.channel.user.web;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import jakarta.servlet.ServletException;
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
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.client.pretups.channel.user.service.ApprovalUserDeleteSuspendService;



/**
 * This class provide the methods for Download User List for Approval Suspend/Delte and To approve that list records
 * @author MOHD SUHEL
 *@since 17/10/2016
 */
@Controller
public class ApprovalUserDeleteSuspendController extends CommonController {


	private static final String STATUSTYPELIST = "statusTypeList";



	private static final String FAILUPLOAD= "failUpload";
	private static final String FAILDOWNLOAD= "failDownload";
	
	private static final String PROBLEM_FILE_DOWNLOAD = "problem.file.upload";
	private static final String FORWARD="channeluser/approvalBatchUserDeleteSuspend";
	@Autowired
	private ApprovalUserDeleteSuspendService approvalUserDeleteSuspendService;
	private List<ListValueVO> userStatusTypeList =null;

	/**
	 *Load Statues Type For Apprval States
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/channeluser/approvalBatchUserDeleteSuspend.form", method = RequestMethod.GET)
	public String loadUserStatusTypes(final ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException
	{
		final String methodName = "loadUserStatusTypes";
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}




		userStatusTypeList = approvalUserDeleteSuspendService.loadStatusTypeList();
		if (log.isDebugEnabled()) {
			log.debug(methodName, "dropDown : Status : "+userStatusTypeList);

		}
		modelMap.put(STATUSTYPELIST, userStatusTypeList);


		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}
		return FORWARD;
	}



	/**
	 * Download The List OF User IN Approval Status
	 * @param pUserStatus
	 * @param modelMap
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws ServletException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/channeluser/download-user-list-for-approval.form", method = RequestMethod.POST)
	public String downloadUserList(@RequestParam("userStatus") String pUserStatus, 
			final ModelMap modelMap, HttpServletRequest request , HttpServletResponse response) throws BTSLBaseException {

		final String methodName = "downloadUserList";
		if (log.isDebugEnabled()) {
			log.debug("ApprovalUserDeleteSuspendController#downloadUserList", PretupsI.ENTERED+" with userStatus : "+pUserStatus);
		}


		userStatusTypeList = approvalUserDeleteSuspendService.loadStatusTypeList();
		modelMap.put(STATUSTYPELIST, userStatusTypeList);

		InputStream inputStream;


		inputStream = approvalUserDeleteSuspendService.downloadUserList(pUserStatus,modelMap);


		
			if(inputStream==null || !downloadFileInCsvFormate(response , inputStream , modelMap))
				return FORWARD;
	



		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}
		return FORWARD;

	}



	private boolean downloadFileInCsvFormate(HttpServletResponse response,
			InputStream inputStream, ModelMap modelMap) {

		final String methodName = "downloadFileInCsvFormate";
		response.setContentType("text/csv");
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}
		try{

			String fileName= "UserList"+BTSLUtil.getFileNameStringFromDate(new Date());
			response.setHeader("Content-Disposition", "attachment; filename="+fileName+"."+PretupsI.FILE_CONTENT_TYPE_XLS.toLowerCase()+"");
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		}catch(IOException | ParseException e)
		{

			if (log.isDebugEnabled()) {
				log.debug(methodName, e);
			}
			modelMap.put(FAILDOWNLOAD ,PretupsRestUtil.getMessageString("error.general.processing"));
			return false;
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.EXITED);
		}
		return true;

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
	@RequestMapping(value = "/channeluser/approve-delete-suspend-user.form", method = RequestMethod.POST)
	public String approveDeleteSuspendUserBatch(@RequestParam("file") MultipartFile file ,ModelMap modelMap,  HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException {

		final String methodName = "approveDeleteSuspendUserBatch";

		if (log.isDebugEnabled()) {
			log.debug(methodName, PretupsI.ENTERED);
		}


		userStatusTypeList = approvalUserDeleteSuspendService.loadStatusTypeList();
		modelMap.put(STATUSTYPELIST, userStatusTypeList);

		if(file==null || file.isEmpty()){
			modelMap.put(FAILUPLOAD ,PretupsRestUtil.getMessageString("file.is.empty"));
			return FORWARD;
		}	


		String filePath = Constants.getProperty("APPROVE_SPND_DLT_DIR_PATH");
		String fileName = file.getOriginalFilename();
		UserVO userVO = this.getUserFormSession(request);





		boolean isFileUpload = false;

		try{
			isFileUpload = uploadFileToServer(file , filePath , file.getSize());
		}
		catch( BTSLBaseException be)
		{

			log.debug(methodName, be);

			modelMap.put(FAILUPLOAD ,be.getMessageKey());
			return FORWARD;
		}

		try{
			if(isFileUpload)
				approvalUserDeleteSuspendService.approveSuspendDeleteUserBatch(filePath , fileName ,userVO,modelMap);
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
	private String getFileExtn(String fileName) {

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
		
		printLog(methodName,PretupsI.ENTERED+" filePath : fileFize "+filePath+" : "+fileSize);
		
		if(!BTSLUtil.isValideFileName(file.getOriginalFilename()))
			throw new BTSLBaseException("file.name.not.valid");
		else if(fileSize>Long.parseLong(Constants.getProperty("APPROVE_SPND_DLT_FILE_SIZE")))
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


	/**this method Print Log
	 * @param methodName
	 * @param LOG
	 */
	private void printLog(String methodName , String action)
	{
		if (log.isDebugEnabled())
			log.debug(methodName, action);
	}
}
