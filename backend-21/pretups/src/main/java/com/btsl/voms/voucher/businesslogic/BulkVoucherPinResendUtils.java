package com.btsl.voms.voucher.businesslogic;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.voms.vomsreport.businesslogic.VomsVoucherResendPinVO;


/**This class provide the utility methods for  Bulk voucher pin resend
 * @author Hargovind Karki
 *
 */
public class BulkVoucherPinResendUtils {

	public static final Log _log = LogFactory.getLog(BulkVoucherPinResendUtils.class.getName());
	private BulkVoucherPinResendUtils(){}
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
	public static int fileParsingForRecords(String fileDirPath ,String fileName ,  List<VomsVoucherResendPinVO> validVoucherList) throws BTSLBaseException
	{
		final String methodName = "fileParsingForRecords";
		int totalNoOfRecords = 0;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, PretupsI.ENTERED);
		}
		try(FileInputStream file = new FileInputStream(fileDirPath)){
			readDataFromXLS(file , validVoucherList);
			totalNoOfRecords = validVoucherList.size();
		}
		catch(BTSLBaseException | IOException be)
		{
			if (_log.isDebugEnabled()) 
				_log.debug(methodName, be);
			throw new BTSLBaseException(be);
		}
		finally{
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
	private static void readDataFromXLS(FileInputStream file , List<VomsVoucherResendPinVO> validVoucherList) throws BTSLBaseException
	{
		final String methodName= "readDataFromXLS";
		printLog(methodName, PretupsI.ENTERED);
		try(XSSFWorkbook workbook = new XSSFWorkbook(file)){
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			
			while(rowIterator.hasNext())
			{
				Row row = rowIterator.next();
				if (row.getRowNum() <=3)
					continue;
				Iterator<Cell> cellIterator = row.cellIterator();
				while(cellIterator.hasNext())
				{
					VomsVoucherResendPinVO vomsVoucherResendPinVO = new VomsVoucherResendPinVO();
					Cell cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					vomsVoucherResendPinVO.setTransferID(cell.getStringCellValue());
					cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					vomsVoucherResendPinVO.setTransferDate(cell.getStringCellValue());
					cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					vomsVoucherResendPinVO.setRetailerMSISDN(cell.getStringCellValue());
					cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					vomsVoucherResendPinVO.setCustomerMSISDN(cell.getStringCellValue());
					cell = cellIterator.next();
					cell.setCellType(CellType.STRING);
					
					vomsVoucherResendPinVO.setSerialNo(cell.getStringCellValue());
					vomsVoucherResendPinVO.setLineNumber(String.valueOf(row.getRowNum()));
					vomsVoucherResendPinVO.setServiceType(PretupsI.SERVICE_TYPE_EVD);
					
					validVoucherList.add(vomsVoucherResendPinVO);
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
