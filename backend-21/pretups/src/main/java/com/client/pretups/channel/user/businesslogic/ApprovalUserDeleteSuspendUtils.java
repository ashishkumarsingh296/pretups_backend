package com.client.pretups.channel.user.businesslogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**This class provide the utility methods for  Approval Suspend/Delete Batch Uuser 
 * @author mohd.suhel1
 *
 */
public class ApprovalUserDeleteSuspendUtils {
	public static final Log _log = LogFactory.getLog(ApprovalUserDeleteSuspendUtils.class.getName());
	private ApprovalUserDeleteSuspendUtils(){}
	
	/** get the type of request
	 * @param userList
	 * @param rejectUserList
	 * @return
	 */
	public static String getRequestType(List<ChannelUserVO> userList,List<ChannelUserVO> rejectUserList) {
		
		final String methodName = "getRequestType";
		printLog(methodName, PretupsI.ENTERED);
		String suspendOrDeleteReq ="";
		if(!userList.isEmpty())
			suspendOrDeleteReq = userList.get(0).getStatus();
		else if(!rejectUserList.isEmpty())
			suspendOrDeleteReq = rejectUserList.get(0).getStatus();
		
		printLog(methodName, PretupsI.EXITED);
		return suspendOrDeleteReq;
	}
	
	
	/**
	 * This method is used to make Archive file on the server.
	 * Method moveFileToArchive.
	 * @param pFilePathAndFileName
	 * @param pFileName
	 * @return
	 */
	public static boolean moveFileToArchive(String pFilePathAndFileName, String pFileName) {
		final String methodName = "moveFileToArchive";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED+" fileName : "+pFileName);
		}
		final File fileRead = new File(pFilePathAndFileName);
		File fileArchive = new File("" + Constants.getProperty("APPROVE_SPND_DLT_DIR_PATH_MOVED"));
		if (!fileArchive.isDirectory()) {
			fileArchive.mkdirs();
		}
		fileArchive = new File("" + Constants.getProperty("APPROVE_SPND_DLT_DIR_PATH_MOVED") + pFileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
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
	public static int fileParsingForRecords(String fileDirPath ,String fileName ,  List<ChannelUserVO> userList , List<ChannelUserVO> rejectUserList , List<ListValueVO> errorFileList) throws BTSLBaseException
	{
		final String methodName = "fileParsingForRecords";
		int totalNoOfRecords = 0;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		try(FileInputStream file = new FileInputStream(fileDirPath)){
			readDataFromXLS(file , userList , rejectUserList , errorFileList);
			totalNoOfRecords = userList.size()+rejectUserList.size()+errorFileList.size();
		}
		catch(BTSLBaseException | IOException be)
		{
			if (_log.isDebugEnabled()) 
				_log.debug(methodName, be);
			throw new BTSLBaseException(be);
		}
		finally{
			ApprovalUserDeleteSuspendUtils.moveFileToArchive(fileDirPath, fileName);
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
	private static void readDataFromXLS(FileInputStream file , List<ChannelUserVO> userList , List<ChannelUserVO> rejectUserList , List<ListValueVO> errorFileList) throws BTSLBaseException
	{
		final String methodName= "readDataFromXLS";
		printLog(methodName, PretupsI.ENTERED);
		try(HSSFWorkbook workbook = new HSSFWorkbook(file)){
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			//Skipping the header of file
			if(rowIterator.hasNext())
				rowIterator.next();
			while(rowIterator.hasNext())
			{
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				if(cellIterator.hasNext())
				{
					ChannelUserVO channelUserVO = new ChannelUserVO();
					Cell cell = cellIterator.next();
					channelUserVO.setUserName(cell.getStringCellValue());
					cell = cellIterator.next();
					channelUserVO.setCategoryCode(cell.getStringCellValue());
					cell = cellIterator.next();
					channelUserVO.setMsisdn(cell.getStringCellValue());
					cell = cellIterator.next();
					channelUserVO.setStatus(cell.getStringCellValue());
					cell = cellIterator.next();
					channelUserVO.setExternalCode(cell.getStringCellValue());	
					cell = cellIterator.next();
					channelUserVO.setPreviousStatus(cell.getStringCellValue());
					cell = cellIterator.next();
					chooseDifferentRequestUser(userList,rejectUserList,errorFileList ,channelUserVO , cell.getStringCellValue());
				}
			}
		}
		catch(IOException ioe)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, ioe);
			}
			throw new BTSLBaseException("approve.user.suspenddelete.message.file.error");
		}
		catch(Exception e)
		{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, e);
			}
			throw new BTSLBaseException("approve.user.suspenddelete.message.file.error");
		}
		finally{
			printLog(methodName, PretupsI.EXITED);
		}
		return;
	}
	
	
	
	/**Separate the requestList depending upon the Action Given
	 * @param userList
	 * @param rejectUserList
	 * @param errorFileList
	 * @param channelUserVO
	 * @param stringCellValue
	 */
	private static void chooseDifferentRequestUser(List<ChannelUserVO> userList,List<ChannelUserVO> rejectUserList,List<ListValueVO> errorFileList, ChannelUserVO channelUserVO ,String stringCellValue) {
		final String methodName = "chooseDifferentRequestUser";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		ListValueVO errorVO;
		switch (stringCellValue) {
		case "Y":
			userList.add(channelUserVO);
			break;
		case "N":
			rejectUserList.add(channelUserVO);
			break;
		case "":
			break;
		default:
			errorVO = new ListValueVO(channelUserVO.getMsisdn(),"approve.user.suspenddelete.message.invalidaction");
			errorFileList.add(errorVO);
			break;
		}
		
		printLog(methodName, PretupsI.EXITED);
	}
	
	
	/**
	 * THIS METHOD TO PROCESS AND DELETE UNNECESSARY DATA FROM LIST
	 * @param listObject
	 * @param realUserList
	 * @param errorList
	 */
	public static <T> void processListValueVOValue(List<T> listObject , Map<String, String> realUserList , List<ListValueVO> errorList) {
		ChannelUserVO channelUserVO;
		final String methodName ="processListValueVOValue";
		printLog(methodName, PretupsI.ENTERED);
		Iterator<T> it = listObject.iterator();
		while(it.hasNext()){
			channelUserVO = (ChannelUserVO)it.next();
			if (!realUserList.containsKey(channelUserVO.getMsisdn())) {
				it.remove();
				errorList.add(new ListValueVO(channelUserVO.getMsisdn().trim(),"approve.user.suspenddelete.message.not.approvalstate"));
			}
		}
		
		printLog(methodName, PretupsI.EXITED);
	}
	
	
	

	
	/**this methods use to sendBtslMessage to Success Approval Request
	 * @param suspendDeleteResumeReq
	 * @param filteredMsisdn
	 * @param userVO
	 */
	public static void sendBtslMessageSuccess(String suspendDeleteResumeReq , String filteredMsisdn, UserVO userVO) {
		final String methodName = "sendBtslMessageSuccess";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		BTSLMessages sendBtslMessage;
		Locale defaultLocale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		PushMessage pushMessage;
		if (!BTSLUtil.isNullString(filteredMsisdn)) {
			if (suspendDeleteResumeReq.equals(PretupsI.USER_STATUS_SUSPEND_REQUEST))
			{
				sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_STATUS_SUSPENDED);
				pushMessage = new PushMessage(filteredMsisdn, sendBtslMessage, "", "", defaultLocale, userVO.getNetworkID());
				pushMessage.push();
			} else if (suspendDeleteResumeReq.equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
				sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
				pushMessage = new PushMessage(filteredMsisdn, sendBtslMessage, "", "", defaultLocale, userVO.getNetworkID());
				pushMessage.push();
			}
		}
		printLog(methodName, PretupsI.EXITED);
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
