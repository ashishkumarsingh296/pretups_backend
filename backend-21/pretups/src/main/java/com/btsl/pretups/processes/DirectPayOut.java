
package com.btsl.pretups.processes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGenerator;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductCache;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileProductVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchMasterVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.DomainVO;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.DirectPayOutErrorLog;
import com.btsl.pretups.logging.DirectPayOutSuccessLog;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockBL;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnItemsVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockTxnVO;
import com.btsl.pretups.networkstock.businesslogic.NetworkStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.SearchCriteria;
import com.btsl.util.SearchCriteria.Operator;
import com.btsl.util.SearchCriteria.ValueType;
import com.btsl.xl.ExcelFileConstants;
import com.btsl.xl.ExcelRW;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

/**DirectPayOut.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Manisha Jain             			24/10/09        Initial Creation
 * Lohit Audhkhasi						30/10/09		Modification
 * Ashutosh Kumar                       22/07/15        Modification done w.r.t to new features of network stocks 
 * Ashutosh Kumar                       16/12/15        process optimized to run for  400000 users
 * Ashutosh Kumar                       25/12/15        process enhanced to allow concurrent processing by varying networks.
 *------------------------------------------------------------------------
 * Copyright (c) 2015 Mahindra Comviva.
 */


public class DirectPayOut implements Runnable {
	private static final Log _logger = LogFactory.getLog(DirectPayOut.class.getName());
	private File fileObject=null;
	private String _networkCode=null;
	private static String _productCode=null;
	private String _domainCode=null;
	private static OperatorUtilI _operatorUtil=null;
	private String fileName=null;
	private String _zoneCode=null;
	private String _OriginalFileName=null;    
	private String _approvalLevel; 
	private String _externalTxnMandatoryDomainType =null;
	private String _externalTxnMandatory = null;
	private boolean _externalCodeMandatory = false; 
	private String _fileExt=null;
	private String _batchName=null;
	private String _categoryCode=null;
	private String _activeUserName=null;      
	private volatile AtomicLong batchDetailID=null;    
	private String filePath=null;
	private DomainVO domainVO=null;
	private FOCBatchMasterVO batchMasterVO = null;
	private long maxRecords=0L;
	private Date curDate = null;
	private String category=null;
	private ArrayList<GeographicalDomainVO> geographyList=null;
	private ProductVO productVO= null;
	private String[] FileNameArray;

	public DirectPayOut(String[] fileName){
		this.FileNameArray=fileName;	
	}

	private class ProcessSheet implements Runnable {

		private Connection innerCon=null;
		private MComConnectionI innermcomCon = null;
		private PreparedStatement _psmtInsertUserThreshold=null;
		private PreparedStatement _pstmtSelectOwner=null;
		private PreparedStatement _pstmtSelectExtTxnID1 = null;
		private PreparedStatement _pstmtSelectExtTxnID2 = null;
		private PreparedStatement _pstmtSelectCProfileProd = null;
		private PreparedStatement _pstmtSelectCProfileProdDetail = null;
		private PreparedStatement _pstmtSelectTProfileProd = null;

		private PreparedStatement _pstmtInsertBatchItems = null;
		private PreparedStatement _pstmtLoadUser = null;
		private PreparedStatement _pstmtLoadNetworkStock=null;
		private PreparedStatement _pstmtUpdateNetworkStock=null;
		private PreparedStatement _pstmtInsertNetworkDailyStock=null;
		private PreparedStatement _pstmtSelectNetworkStock=null;
		private PreparedStatement _pstmtupdateSelectedNetworkStock=null;
		private PreparedStatement _pstmtInsertNetworkStockTransaction=null;
		private PreparedStatement _pstmtInsertNetworkStockTransactionItem=null;
		private PreparedStatement _pstmtSelectUserBalances=null;
		private PreparedStatement _pstmtUpdateUserBalances=null;
		private PreparedStatement _pstmtInsertUserDailyBalances=null;
		private PreparedStatement _pstmtSelectBalance=null;
		private PreparedStatement _pstmtUpdateBalance=null;
		private PreparedStatement _pstmtInsertBalance=null;
		private PreparedStatement _pstmtSelectTransferCounts=null;
		private PreparedStatement _pstmtSelectProfileCounts=null;
		private PreparedStatement _pstmtUpdateTransferCounts=null;
		private PreparedStatement _pstmtInsertTransferCounts=null;
		private PreparedStatement _psmtAppr1FOCBatchItem = null;
		private PreparedStatement _pstmtUpdateMaster= null;
		private PreparedStatement _pstmtLoadTransferProfileProduct=null;
		private PreparedStatement _handlerStmt = null;
		private PreparedStatement _pstmtIsTxnNumExists2=null;
		private PreparedStatement _pstmtInsertIntoChannelTransferItems=null;
		private PreparedStatement _pstmtSelectBalanceInfoForMessage=null;
		private PreparedStatement _pstmtInsertIntoChannelTranfers=null;
		private PreparedStatement _pstmtSelectTrfRule = null;
		// for loading the products associated with the transfer rule
		private PreparedStatement _pstmtSelectTrfRuleProd = null;

		private String [][]excelArr=null;
		private int rows = 0;
		private int cols=0;
		private boolean extMandatory=false;
		private String msisdn=null;       
		private Date extDate=null;
		private long reqQuantity=0;
		private long recordsProcessed=0;
		private ArrayList<FOCBatchItemsVO> batchItemsList=null;    
		private FOCBatchItemsVO focBatchItemVO=null;
		private String externalTxnNum=null;
		private String externalTxnCode=null;
		private String externalTxnDate=null;
		private String remarks=null;
		private String quantity=null;
		private String bonusType=null;
		private String sheetName = null;

		HashMap<String, HashMap<String, String>> tcpMap = null;
		boolean tcpOn = false;
		
		public ProcessSheet(String[][] strArray,String sheetName) {
			this.excelArr = strArray;  
			this.sheetName = sheetName;
		}


		public void run() {
			final String METHOD_NAME = "run";
			try {
				ArrayList <FOCBatchItemsVO>dataList=new ArrayList<FOCBatchItemsVO>();
				if(innermcomCon==null){
					innermcomCon = new MComConnection();
					innerCon=innermcomCon.getConnection();
				}
				createGlobalPreparedStatement(innerCon);
				try
				{
					cols=excelArr[0].length;
				}
				catch(Exception e)
				{
					DirectPayOutErrorLog.log("NA",fileName,"No record found in "+sheetName);
					 _logger.errorTrace(METHOD_NAME,e);
				}

				rows=excelArr.length;  //rows include the headings
				//base condition check : no data in XLS file
				if(rows==1)
				{
					DirectPayOutErrorLog.log("NA",fileName,"No record found in "+sheetName);
				}
				if(!BTSLUtil.isNullString(_externalTxnMandatory) && _externalTxnMandatory.indexOf("0")!=-1)
				{
					extMandatory=false;
					if(BTSLUtil.isNullString(_externalTxnMandatoryDomainType)) {
						extMandatory=true;
					} else 
					{
						String domainTypeArr[]=_externalTxnMandatoryDomainType.split(",");
						for(int i=0,j=domainTypeArr.length;i<j;i++)
						{
							if(domainVO.getDomainTypeCode().equals(domainTypeArr[i]))
							{
								extMandatory=true;
								break;
							}
						}
					}
				}

				ArrayList<String> externalTxnNumber=new ArrayList<String>();
				int partialBatchSize = 0;
				if(cols==7 || cols==10)
				{
					batchItemsList= new ArrayList<FOCBatchItemsVO>(); 

					for(int r=1;r<rows;r++)
					{
						if(cols==7)
						{
							msisdn=excelArr[r][0];
							externalTxnNum=excelArr[r][1];
							externalTxnDate=excelArr[r][2];
							externalTxnCode=excelArr[r][3];
							quantity=excelArr[r][4];
							bonusType=excelArr[r][5];
							remarks=excelArr[r][6];
						}
						else
						{
							msisdn=excelArr[r][0];
							externalTxnNum=excelArr[r][4];
							externalTxnDate=excelArr[r][5];
							externalTxnCode=excelArr[r][6];
							quantity=excelArr[r][7];
							bonusType=excelArr[r][8];
							remarks=excelArr[r][9];
						}
						if(BTSLUtil.isNullString(excelArr[r][0]))
						{
							if(BTSLUtil.isNullArray(excelArr[r]))
							{
								continue;
							}
							DirectPayOutErrorLog.log(msisdn,fileName,"Mobile number is required");
							continue;
						}

						if(extMandatory)
						{
							if(BTSLUtil.isNullString(externalTxnNum))
							{
								DirectPayOutErrorLog.log(msisdn,fileName,"External transaction number is mandatory field");
								continue;
							}
							if(BTSLUtil.isNullString(externalTxnDate))
							{
								DirectPayOutErrorLog.log(msisdn,fileName,"External transaction date is mandatory field");
								continue;
							}
						}

						if(!BTSLUtil.isNullString(externalTxnNum) && externalTxnNum.length()>20)
						{
							DirectPayOutErrorLog.log(msisdn,fileName,"External transaction number is greater than 20 digits");
							continue;
						}

						if(!BTSLUtil.isNullString(externalTxnNum))
						{
							if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_NUMERIC))).booleanValue())
							{
								if(!BTSLUtil.isNumeric(externalTxnNum))
								{
									DirectPayOutErrorLog.log(msisdn,fileName,"External transaction number cannot be alphanumeric, it can take only numeric values");
									continue;
								}
							}
							if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue())
							{
								if(externalTxnNumber!=null && externalTxnNumber.contains(externalTxnNum))
								{
									DirectPayOutErrorLog.log(msisdn,fileName,"External transaction number should be unique");
									continue;
								}
							}
							externalTxnNumber.add(externalTxnNum);
						}
						if(!BTSLUtil.isNullString(externalTxnDate))
						{
							try
							{	
								/*This is used to validate the date in XLS file. If we enter the
         					    date in 01/01/06 then it will be treated as 01/01/2006 in XLS 
         					    file so we have to create a new date object and validate it.
         					    extDate=new Date(externalTxnDate);
         					    externalTxnDate=BTSLUtil.getDateStringFromDate(extDate);
         					    extDate=BTSLUtil.getDateFromDateString(externalTxnDate);*/
							}
							catch(Exception ex)
							{
								DirectPayOutErrorLog.log(msisdn,fileName,"Invalid date format for external transaction date");
								_logger.errorTrace(METHOD_NAME,ex);
								continue;
							}
						}

						//checking external txn code
						if(_externalCodeMandatory)
						{
							if(BTSLUtil.isNullString(externalTxnCode))
							{
								DirectPayOutErrorLog.log(msisdn,fileName,"External code is mandatory field");
								continue;
							}
						}

						if(!BTSLUtil.isNullString(externalTxnCode) && externalTxnCode.length()>10)
						{
							DirectPayOutErrorLog.log(msisdn,fileName,"External code should not be greater than 10 digits");
							continue;
						}

						//checking  quantity related validations here
						if(!BTSLUtil.isNullString(quantity))
						{
							if(!BTSLUtil.isNumericInteger(quantity))
							{
								try
								{
									if(!BTSLUtil.isNumeric(quantity))
									{
										int length=quantity.length();
										int index=quantity.indexOf(".");
										if(length> index+3)
										{
											DirectPayOutErrorLog.log(msisdn,fileName,"Invalid quantity field, it should be upto 2 decimal place only");
											continue;
										}
									}

									reqQuantity=PretupsBL.getSystemAmount(quantity);
									if(reqQuantity<0  && PretupsI.NO.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED")) )
									{
										DirectPayOutErrorLog.log(msisdn,fileName,"Quantity should be greater than zero");
										continue;
									}
								}
								catch(Exception e)
								{
									DirectPayOutErrorLog.log(msisdn,fileName,"Quantity should be numeric");
									_logger.errorTrace(METHOD_NAME,e);
									continue;
								}
							}
							reqQuantity=PretupsBL.getSystemAmount(quantity);

						}
						else
						{
							DirectPayOutErrorLog.log(msisdn,fileName,"Quanity is required");
							continue;
						}
						if(BTSLUtil.isNullString(bonusType))
						{
							DirectPayOutErrorLog.log(msisdn,fileName,"Bonus type is required");
							continue;
						}
						if(!BTSLUtil.isNullString(remarks))
						{
							if(remarks.length()>100) {
								remarks=remarks.substring(0,100);
							}
						}



						focBatchItemVO=new FOCBatchItemsVO();
						focBatchItemVO.setRecordNumber(r+1);
						focBatchItemVO.setBatchId(batchMasterVO.getBatchId());

						focBatchItemVO.setBatchDetailId(generateFOCBatchDetailTransferID(batchMasterVO.getBatchId(),batchDetailID.incrementAndGet()));


						focBatchItemVO.setMsisdn(msisdn);
						focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
						focBatchItemVO.setModifiedBy(PretupsI.SYSTEM);
						focBatchItemVO.setModifiedOn(curDate);
						focBatchItemVO.setExtTxnNo(externalTxnNum);
						focBatchItemVO.setExtTxnDate(extDate);
						focBatchItemVO.setTransferDate(curDate);
						focBatchItemVO.setRequestedQuantity(reqQuantity);
						focBatchItemVO.setInitiatorRemarks(remarks);
						focBatchItemVO.setExternalCode(externalTxnCode);	
						focBatchItemVO.setBonusType(bonusType);
						focBatchItemVO.setInitiatedOn(curDate);
						focBatchItemVO.setInitiatedBy(_activeUserName);
						batchItemsList.add(focBatchItemVO);

						msisdn=null;
						externalTxnNum=null;
						externalTxnDate=null;
						externalTxnCode=null;
						quantity=null;
						bonusType=null;
						remarks=null;
						productVO=null;

						partialBatchSize++;
						if(((rows < 2000) && partialBatchSize==rows-1) || ((rows >= 2000) &&((partialBatchSize / 2000) > 0) || r==rows-1)) {
							if(batchItemsList != null && !batchItemsList.isEmpty()) {
								validateUsers(innerCon,batchItemsList,category,curDate,geographyList,dataList);
							} else {
								DirectPayOutErrorLog.log("NA",fileName,"Rejecting the invalid records in the sheet :"+sheetName);
							}

							if(batchItemsList != null && !batchItemsList.isEmpty()) 
							{  
								processBatchDPTransfer(innerCon,batchMasterVO,batchItemsList,dataList);
								batchMasterVO.setBatchTotalRecord(batchItemsList.size()); 
								if(batchItemsList != null && !batchItemsList.isEmpty()) {
									closeOrderByBatchForDirectPayout(innerCon, batchItemsList, batchMasterVO,dataList);
								} else {
									DirectPayOutErrorLog.log("NA",fileName,"Rejecting the invalid records in the sheet :"+sheetName);
								}

							} else {
								DirectPayOutErrorLog.log("NA",fileName,"Rejecting the invalid records in the sheet :"+sheetName);
							}
							recordsProcessed+=batchItemsList.size();
							batchItemsList=null;
							partialBatchSize =  0;
							double t_mem=Runtime.getRuntime().totalMemory()/1048576;
							Runtime.getRuntime().gc();
							double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
							_logger.debug(METHOD_NAME, "Total memory :"+t_mem+"   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));
							innerCon.commit();   
							batchItemsList= new ArrayList<FOCBatchItemsVO>();


						
						}

					} 

					writeFailedTransactionsInDB(dataList);
					if ("Y".equals(Constants.getProperty("DP_WRITE_ERROR_RECORDS_IN_FILE")))
					{
						writeDataInFile(dataList);
					}

				} else {
					DirectPayOutErrorLog.log("NA",fileName,"Invalid file number of columns are not equal to 7");
				}

			} catch (SQLException e) {
				_logger.error(METHOD_NAME, "SQLException " + e.getMessage());
	        	_logger.errorTrace(METHOD_NAME,e);
				
			}
			catch(BTSLBaseException bse) {
				_logger.error(METHOD_NAME, "Exception " + bse.getMessage());
	        	_logger.errorTrace(METHOD_NAME,bse);
				
			} catch(Exception e) {
				_logger.error(METHOD_NAME, "SQLException " + e.getMessage());
	        	_logger.errorTrace(METHOD_NAME,e);
			}finally {
				closeGlobalPreparedStatement();
				if (innermcomCon != null) {
					innermcomCon.close("DirectPayOut#run");
					innermcomCon = null;
				}
			}
		}

		//
		/**
		 * Method generateFOCBatchDetailTransferID.
		 * This method is called generate FOC batch detail transferID
		 * @param p_batchMasterID String
		 * @param p_tempNumber long
		 * @throws BTSLBaseException
		 * @return String
		 */

		private String generateFOCBatchDetailTransferID(String p_batchMasterID, long p_tempNumber) throws BTSLBaseException
		{
			final String METHOD_NAME = "generateFOCBatchDetailTransferID";
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Entered p_batchMasterID="+p_batchMasterID+", p_tempNumber= "+p_tempNumber);
			}
			String uniqueID = null;
			try
			{
				uniqueID =_operatorUtil.formatFOCBatchDetailsTxnID(p_batchMasterID,p_tempNumber);
			} 
			catch (Exception e)
			{
				_logger.errorTrace(METHOD_NAME, e);
				throw new BTSLBaseException("DirectPayOut", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
			}
			finally
			{
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exited  "+uniqueID);
				}
			}
			return uniqueID;
		}


		//
		/**
		 * Method validateUsers.
		 * This method the loads the user list for Batch FOC transfer
		 * @param p_con Connection
		 * @param p_batchFOCItemsVOList ArrayList
		 * @param p_domainCode String
		 * @param p_categoryCode String
		 * @param p_networkCode String
		 * @param p_geographicalDomainCode String
		 * @param p_comPrfApplicableDate Date
		 * @param p_messages MessageResources
		 * @param p_locale Locale
		 * @return ArrayList
		 * @throws BTSLBaseException
		 */ 

		public void validateUsers(Connection p_con,ArrayList<FOCBatchItemsVO> p_batchFOCItemsVOList,String p_categoryCode,Date p_comPrfApplicableDate, ArrayList p_geographyList,ArrayList dataList) throws BTSLBaseException
		{
			final String METHOD_NAME = "validateUsers";
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Entered p_batchFOCItemsVOList.size()="+p_batchFOCItemsVOList.size()+"Category Code "+p_categoryCode+" p_comPrfApplicableDate="+p_comPrfApplicableDate+" p_geographyList: "+p_geographyList);
			}

			 
			

			DirectPayOutQry directPayOutQry = (DirectPayOutQry)ObjectProducer.getObject(QueryConstants.DIRECT_PAYOUT_QRY, QueryConstants.QUERY_PRODUCER);
			String sqlSelect = directPayOutQry.validateUsersQry();
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "QUERY sqlSelect=" + sqlSelect);
			}
			try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);)
			{
				
				int index = 0 ;
				FOCBatchItemsVO focBatchItemVO =null;
				String geography=null;
				boolean gegraphyFound=false;
				int itemsListSizes=p_batchFOCItemsVOList.size();
				for(int i=0;i<itemsListSizes;i++)
				{
					focBatchItemVO=(FOCBatchItemsVO)p_batchFOCItemsVOList.get(i);
					index=0;
					pstmt.clearParameters();
					pstmt.setString(++index,focBatchItemVO.getMsisdn());
					pstmt.setString(++index,_networkCode);
					pstmt.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(p_comPrfApplicableDate));
					try(ResultSet rs = pstmt.executeQuery();)
					{
					if(rs.next())
					{
						geography=null;
						gegraphyFound=false;
						geography=rs.getString("GRPH_DOMAIN_CODE");
						for(int l=0;l<p_geographyList.size();l++)
						{
							if(geography.equals(((GeographicalDomainVO)p_geographyList.get(l)).getGrphDomainCode()))
							{
								gegraphyFound=true;
								break;
							}
						}
						if(!gegraphyFound)
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"user does not belong to specified geography");
							p_batchFOCItemsVOList.remove(i);
							i=i-1;
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("user does not belong to specified geography");
							addData(focBatchItemVO,dataList);
							continue; 
						}
						if(!_domainCode.equals(rs.getString("domain_code")))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"user does not belong to specified domain");
							p_batchFOCItemsVOList.remove(i);
							i=i-1;
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("user does not belong to specified domain");
							addData(focBatchItemVO,dataList);
							continue;
						}
						if(!PretupsI.ALL.equals(_categoryCode) && !_categoryCode.equals(rs.getString("category_code")))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"user does not belong to category: "+rs.getString("category_name"));
							p_batchFOCItemsVOList.remove(i);
							i=i-1;
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("user does not belong to category");
							addData(focBatchItemVO,dataList);
							continue;
						}
						if(!PretupsI.YES.equals(rs.getString("status")))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User not active");
							p_batchFOCItemsVOList.remove(i);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("User not active");
							addData(focBatchItemVO,dataList);
							i=i-1;
							continue;
						}
						if(!PretupsI.NO.equals(rs.getString("in_suspend")))
						{
							//put error user is in suspended
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User not IN suspended");
							p_batchFOCItemsVOList.remove(i);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("User is IN suspended");
							addData(focBatchItemVO,dataList);
							i=i-1;
							continue;

						}

						if(!PretupsI.YES.equals(rs.getString("profile_status")))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Transfer profile is suspended");
							p_batchFOCItemsVOList.remove(i);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Transfer profile is suspended");
							addData(focBatchItemVO,dataList);
							i=i-1;
							continue;
						}
						if(!PretupsI.YES.equals(rs.getString("commprofilestatus")))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Commission profile is inactive");
							p_batchFOCItemsVOList.remove(i);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Commission profile is inactive");
							addData(focBatchItemVO,dataList);
							i=i-1;
							continue;
						}
						if(BTSLUtil.getUtilDateFromTimestamp(rs.getTimestamp("applicable_from")).after(p_comPrfApplicableDate))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"No commission profile is associated till today");
							p_batchFOCItemsVOList.remove(i);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("No commission profile is associated till today");
							addData(focBatchItemVO,dataList);
							i=i-1;
							continue;
						}
						focBatchItemVO.setCommissionProfileSetId(rs.getString("comm_profile_set_id"));
						focBatchItemVO.setCommissionProfileVer(rs.getString("comm_profile_set_version"));
						focBatchItemVO.setTxnProfile(rs.getString("transfer_profile_id"));
						focBatchItemVO.setCategoryCode(rs.getString("category_code"));
						focBatchItemVO.setUserGradeCode(rs.getString("grade_code"));
						focBatchItemVO.setUserId(rs.getString("user_id"));
					}	
					else
					{
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"No channel user exist by this mobile number");
						p_batchFOCItemsVOList.remove(i);
						i=i-1;
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("No channel user exist by this mobile number");
						addData(focBatchItemVO,dataList);
						continue;
					}
				}
			}
			}
			catch (SQLException sqe)
			{
				_logger.errorTrace(METHOD_NAME, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[validateUsers]","","","","SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			} 
			catch (Exception ex)
			{
				_logger.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[validateUsers]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			}
			finally
			{
				
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exiting:   p_batchFOCItemsVOList size: "+p_batchFOCItemsVOList.size() );
				}
			}
		}

		//
		/**
		 * close all the preparedStatements
		 */
		private void closeGlobalPreparedStatement()
		{
			final String METHOD_NAME = "closeGlobalPreparedStatement";
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Entered");
			}
			try{if (_pstmtSelectExtTxnID1 != null){_pstmtSelectExtTxnID1.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectExtTxnID2 != null){_pstmtSelectExtTxnID2.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectCProfileProd != null){_pstmtSelectCProfileProd.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectCProfileProdDetail != null){_pstmtSelectCProfileProdDetail.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectTProfileProd != null){_pstmtSelectTProfileProd.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}           
			try{if (_pstmtInsertBatchItems != null){_pstmtInsertBatchItems.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtLoadUser != null){_pstmtLoadUser.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtLoadNetworkStock!=null){_pstmtLoadNetworkStock.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtUpdateNetworkStock!=null){_pstmtUpdateNetworkStock.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertNetworkDailyStock!=null){_pstmtInsertNetworkDailyStock.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectNetworkStock!=null){_pstmtSelectNetworkStock.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtupdateSelectedNetworkStock!=null){_pstmtupdateSelectedNetworkStock.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertNetworkStockTransaction!=null){_pstmtInsertNetworkStockTransaction.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertNetworkStockTransactionItem!=null){_pstmtInsertNetworkStockTransactionItem.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectUserBalances!=null){_pstmtSelectUserBalances.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtUpdateUserBalances!=null){_pstmtUpdateUserBalances.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertUserDailyBalances!=null){_pstmtInsertUserDailyBalances.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectBalance!=null){_pstmtSelectBalance.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtUpdateBalance!=null){_pstmtUpdateBalance.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertBalance!=null){_pstmtInsertBalance.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectTransferCounts!=null){_pstmtSelectTransferCounts.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectProfileCounts!=null){_pstmtSelectProfileCounts.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtUpdateTransferCounts!=null){_pstmtUpdateTransferCounts.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertTransferCounts!=null){_pstmtInsertTransferCounts.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_psmtAppr1FOCBatchItem != null){_psmtAppr1FOCBatchItem.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtLoadTransferProfileProduct !=null){_pstmtLoadTransferProfileProduct.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_handlerStmt != null){_handlerStmt.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtIsTxnNumExists2!= null){_pstmtIsTxnNumExists2.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertIntoChannelTransferItems!= null){_pstmtInsertIntoChannelTransferItems.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectBalanceInfoForMessage!= null){_pstmtSelectBalanceInfoForMessage.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectOwner != null){_pstmtSelectOwner.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtUpdateMaster != null){_pstmtUpdateMaster.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtInsertIntoChannelTranfers!= null){_pstmtInsertIntoChannelTranfers.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectTrfRule!= null){_pstmtSelectTrfRule.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_pstmtSelectTrfRuleProd!= null){_pstmtSelectTrfRuleProd.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
			try{if (_psmtInsertUserThreshold!= null){_psmtInsertUserThreshold.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}

			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting");
			}
		}
		//
		/**
		 * Method to close the foc order by batch. This also perform all the data validation.
		 * Also construct error list
		 * Tables updated are: network_stocks,network_daily_stocks,network_stock_transactions,network_stock_trans_items
		 * user_balances,user_daily_balances,user_transfer_counts,foc_batch_items,foc_batches,
		 * channel_transfers_items,channel_transfers
		 * 
		 * @param p_con
		 * @param p_dataMap
		 * @param p_currentLevel
		 * @param p_userID
		 * @param p_focBatchMatserVO
		 * @param p_messages
		 * @param p_locale
		 * @return
		 * @throws BTSLBaseException
		 */
		public void closeOrderByBatchForDirectPayout(Connection p_con,ArrayList p_focBatchItemList,FOCBatchMasterVO p_focBatchMatserVO,ArrayList dataList) throws BTSLBaseException
		{
			final String METHOD_NAME = "closeOrderByBatchForDirectPayout";
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Entered p_focBatchItemList="+p_focBatchItemList);
			}

			ArrayList userbalanceList=null;
			UserBalancesVO balancesVO = null;
			ResultSet rs = null;
			String language=null;
			String country=null;
			KeyArgumentVO keyArgumentVO=null;
			String[] argsArr=null;
			ArrayList txnSmsMessageList=null;
			ArrayList balSmsMessageList=null;
			Locale locale=null;
			String[] array=null;
			BTSLMessages messages=null;
			PushMessage pushMessage=null;
			int updateCount=0,insertCount=0;
			String o2cTransferID=null;
			String  OwnerId=null;
			ArrayList errorList=null;
			Date date=null;
			String batch_ID=null;
			PreparedStatement errorPstm=null;
			PreparedStatement focbatchPstm=null;
			PreparedStatement focbatchGeoPstm=null;
			long thresholdValue=-1;
			try
			{
				FOCBatchItemsVO focBatchItemVO=null;
				ChannelUserVO channelUserVO=null;
				ChannelTransferVO channelTransferVO=null;
				ChannelTransferItemsVO channelTransferItemVO=null;
				date=new Date();
				ArrayList channelTransferItemVOList=null;
				MessageGatewayVO messageGatewayVO=MessageGatewayCache.getObject(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WEB_GATEWAY_CODE)));
				NetworkStockVO networkStocksVO=null;
				int dayDifference=0;
				Date dailyStockUpdatedOn=null;
				long stock=-1;
				long stockSold = -1;
				NetworkStockTxnVO networkStockTxnVO=null;
				String network_id=null;
				Date dailyBalanceUpdatedOn=null;
				NetworkStockTxnItemsVO networkItemsVO = null;  
				TransferProfileProductVO transferProfileProductVO=null;
				UserTransferCountsVO countsVO = null;
				TransferProfileVO transferProfileVO=null;

				long maxBalance=0;
				boolean isNotToExecuteQuery = false;
				long balance = -1;
				long previousUserBalToBeSetChnlTrfItems=-1;
				long previousNwStockToBeSetChnlTrfItems=-1;
				int m=0;
				int k=0;
				boolean flag = true;
				boolean terminateProcessing=false;
				boolean isOwnerUserNotSame;
				errorList= new ArrayList();
				boolean isDpAllowed=false;
				Boolean balanceExist=false;
				isDpAllowed=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_ALLOWED))).booleanValue();
				int   batchItemLists=p_focBatchItemList.size();
				for (int i = 0; i <batchItemLists; i++) {
					balanceExist = false;
					insertCount = 0;
					isOwnerUserNotSame = false;
					terminateProcessing = false;
					focBatchItemVO=(FOCBatchItemsVO)p_focBatchItemList.get(i);
					focBatchItemVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE);
					_pstmtSelectOwner.clearParameters();
					_pstmtSelectOwner.setString(1, focBatchItemVO.getUserId());	
					try{
					rs = _pstmtSelectOwner.executeQuery();
					if (rs.next())
					{
						OwnerId=rs.getString("owner_id");
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					if(!(focBatchItemVO.getUserId().equalsIgnoreCase(OwnerId))&&isDpAllowed)
					{
						isOwnerUserNotSame=true;
					}
					//*********
					if(BTSLUtil.isNullString(batch_ID)) {
						batch_ID=focBatchItemVO.getBatchId();
					}
					if (_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Executed focBatchItemVO=" + focBatchItemVO.toString());
					}
					_pstmtLoadUser.clearParameters();
					m=0;
					_pstmtLoadUser.setString(++m,focBatchItemVO.getUserId());
					try{
					rs=_pstmtLoadUser.executeQuery();
					//(record found for user i.e. receiver) if this condition is not true then made entry in logs and leave this data.
					if(rs.next())
					{
						channelUserVO = new ChannelUserVO();
						channelUserVO.setUserID(focBatchItemVO.getUserId());
						channelUserVO.setStatus(rs.getString("userstatus"));
						channelUserVO.setInSuspend(rs.getString("in_suspend"));
						channelUserVO.setCommissionProfileStatus(rs.getString("commprofilestatus"));
						
						channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
						channelUserVO.setCommissionProfileLang1Msg(rs.getString("comprf_lang_1_msg"));
						channelUserVO.setCommissionProfileLang2Msg(rs.getString("comprf_lang_2_msg"));
						channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
						channelUserVO.setTransferProfileID(rs.getString("transfer_profile_id"));
						
						
						
						if(!tcpOn) {
						channelUserVO.setTransferProfileStatus(rs.getString("profile_status"));
						 }else {
		                        channelUserVO.setTransferProfileStatus(tcpMap.get(rs.getString("transfer_profile_id")).get("status"));//TCP
		                 }
						language=rs.getString("phone_language");
						country=rs.getString("country");
						channelUserVO.setGeographicalCode(rs.getString("grph_domain_code"));
						//(user status is checked) if this condition is true then made entry in logs and leave this data.
						if(!PretupsI.USER_STATUS_ACTIVE.equals(channelUserVO.getStatus()))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User is suspend");
							errorList.add(focBatchItemVO);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("User is suspend");
							addData(focBatchItemVO,dataList);
							continue;
						}
						//(commission profile status is checked) if this condition is true then made entry in logs and leave this data.
						else if(!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus()))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Commission profile suspend");
							errorList.add(focBatchItemVO);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Commission profile suspend");
							addData(focBatchItemVO,dataList);
							continue;
						}
						//(transfer profile is checked) if this condition is true then made entry in logs and leave this data.
						else if(!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus()))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Transfer profile suspend");
							errorList.add(focBatchItemVO);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Transfer profile suspend");
							addData(focBatchItemVO,dataList);
							continue;
						}
						//(user in suspend  is checked) if this condition is true then made entry in logs and leave this data.
						else if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend()))
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User is IN suspend");
							errorList.add(focBatchItemVO);
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("User is IN suspend");
							addData(focBatchItemVO,dataList);
							continue;
						}
					}
					//(no record found for user i.e. receiver) if this condition is true then made entry in logs and leave this data.
					else
					{
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User not found");
						errorList.add(focBatchItemVO);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("User not found");
						addData(focBatchItemVO,dataList);
						continue;
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					networkStocksVO=new NetworkStockVO();
					networkStocksVO.setProductCode(p_focBatchMatserVO.getProductCode());
					networkStocksVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
					networkStocksVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());

					// creating the channelTransferVO here since O2CTransferID will be required into the network stock
					// transaction table. Other information will be set into this VO later
					channelTransferVO=new ChannelTransferVO();
					// seting the current value for generation of the transfer ID. This will be over write by the
					// bacth foc items was created.
					channelTransferVO.setCreatedOn(date);
					channelTransferVO.setNetworkCode(p_focBatchMatserVO.getNetworkCode());
					channelTransferVO.setNetworkCodeFor(p_focBatchMatserVO.getNetworkCodeFor());
					//lohit
					channelTransferVO.setProductType(p_focBatchMatserVO.getProductCode());
					channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
					ChannelTransferBL.genrateTransferID(channelTransferVO);

					o2cTransferID=channelTransferVO.getTransferID();
					// value is over writing since in the channel trasnfer table created on should be same as when the
					// batch foc item was created.
					channelTransferVO.setCreatedOn(focBatchItemVO.getInitiatedOn());
					//lohit
					channelTransferVO.setTransferType(PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
					networkStocksVO.setLastTxnNum(o2cTransferID);
					/* changed on 20/07/06 as already in batch items the entries are in lowest denomination
					 */
					networkStocksVO.setLastTxnBalance(focBatchItemVO.getRequestedQuantity());
					networkStocksVO.setWalletBalance(focBatchItemVO.getRequestedQuantity());

					networkStocksVO.setLastTxnType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
					networkStocksVO.setModifiedBy(PretupsI.SYSTEM);
					networkStocksVO.setModifiedOn(date);
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
						networkStocksVO.setWalletType(PretupsI.INCENTIVE_WALLET_TYPE);
					else 
						networkStocksVO.setWalletType(PretupsI.SALE_WALLET_TYPE);
					dailyStockUpdatedOn=null;
					dayDifference=0;
					//select the record form the network stock table.
					_pstmtLoadNetworkStock.clearParameters();
					m=0;
					_pstmtLoadNetworkStock.setString(++m,networkStocksVO.getNetworkCode());
					_pstmtLoadNetworkStock.setString(++m,networkStocksVO.getNetworkCodeFor());
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
						_pstmtLoadNetworkStock.setString(++m,PretupsI.INCENTIVE_WALLET_TYPE);
					else 
						_pstmtLoadNetworkStock.setString(++m,PretupsI.SALE_WALLET_TYPE);

					_pstmtLoadNetworkStock.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(date));
					try{
					rs=null;
					rs=_pstmtLoadNetworkStock.executeQuery();
					while(rs.next())
					{
						dailyStockUpdatedOn=rs.getDate("daily_stock_updated_on");

						//if record exist check updated on date with current date
						//day differences to maintain the record of previous days.
						dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyStockUpdatedOn,date);

						if(dayDifference>0)
						{
							//if dates are not equal get the day differencts and execute insert qurery no of times of the difference is.
							if(_logger.isDebugEnabled()) {
								_logger.debug("closeOrderByBatchForDirectPayout ","Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
							}

							for(k=0;k<dayDifference;k++)
							{
								_pstmtInsertNetworkDailyStock.clearParameters();
								m=0;
								_pstmtInsertNetworkDailyStock.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyStockUpdatedOn,k)));
								if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
									_pstmtInsertNetworkDailyStock.setString(++m,PretupsI.INCENTIVE_WALLET_TYPE);
								else   
									_pstmtInsertNetworkDailyStock.setString(++m,PretupsI.SALE_WALLET_TYPE);
								_pstmtInsertNetworkDailyStock.setString(++m,rs.getString("network_code"));
								_pstmtInsertNetworkDailyStock.setString(++m,rs.getString("network_code_for"));
								_pstmtInsertNetworkDailyStock.setString(++m,_productCode);
								_pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong("wallet_created"));
								_pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong("wallet_returned"));
								_pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong("wallet_balance"));
								_pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong("wallet_sold"));

								_pstmtInsertNetworkDailyStock.setString(++m,channelTransferVO.getTransferID());
								_pstmtInsertNetworkDailyStock.setString(++m,networkStocksVO.getLastTxnType());
								_pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong("last_txn_balance"));
								_pstmtInsertNetworkDailyStock.setLong(++m,rs.getLong("previous_balance"));
								_pstmtInsertNetworkDailyStock.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
								_pstmtInsertNetworkDailyStock.setString(++m,PretupsI.DAILY_STOCK_CREATION_TYPE_MAN);
								updateCount=_pstmtInsertNetworkDailyStock.executeUpdate();
								if(updateCount<=0)
								{
									p_con.rollback();
									DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while insert in network daily stock table");
									errorList.add(focBatchItemVO);
									terminateProcessing=true;
									break;
								}
							}// end of for loop
							//if updation of daily network stock is fail then terminate the processing
							if(terminateProcessing)
							{
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Error in updating daily network stock table, so terminating the process");
								errorList.add(focBatchItemVO);
								break;
							}
							//Update the network stock table
							_pstmtUpdateNetworkStock.clearParameters();
							m=0;
							_pstmtUpdateNetworkStock.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							_pstmtUpdateNetworkStock.setString(++m,networkStocksVO.getNetworkCode());
							_pstmtUpdateNetworkStock.setString(++m,networkStocksVO.getNetworkCodeFor());
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
								_pstmtUpdateNetworkStock.setString(++m,PretupsI.INCENTIVE_WALLET_TYPE);
							else 
								_pstmtUpdateNetworkStock.setString(++m,PretupsI.SALE_WALLET_TYPE);
							updateCount=_pstmtUpdateNetworkStock.executeUpdate();
							//(record not updated properly in db) if this condition is true then made entry in logs and leave this data.
							if(updateCount<=0)
							{
								p_con.rollback();
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating network daily stock table");
								errorList.add(focBatchItemVO);
								continue;
							}						
						}
					}//end of if () for daily network stock updation
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					_pstmtSelectNetworkStock.clearParameters();
					m=0;
					_pstmtSelectNetworkStock.setString(++m, networkStocksVO.getNetworkCode());
					_pstmtSelectNetworkStock.setString(++m, networkStocksVO.getProductCode());
					_pstmtSelectNetworkStock.setString(++m, networkStocksVO.getNetworkCodeFor());
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
						_pstmtSelectNetworkStock.setString(++m, PretupsI.INCENTIVE_WALLET_TYPE);
					else 
						_pstmtSelectNetworkStock.setString(++m, PretupsI.SALE_WALLET_TYPE);
					try{
					rs=null;
					rs = _pstmtSelectNetworkStock.executeQuery();
					stock = -1;
					stockSold = -1;
					previousNwStockToBeSetChnlTrfItems=-1;
					//get the network stock
					if (rs.next())
					{

						stock = rs.getLong("wallet_balance");
						stockSold = rs.getLong("wallet_sold");	

					} 
					//(network stock not found) if this condition is true then made entry in logs and leave this data.
					else
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Network stock not exists. So all records after this can not be processed");
						errorList.add(focBatchItemVO);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						addData(focBatchItemVO,dataList);
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.NETWORK_STOCK_NOT_EXIST); 

					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					//(network stock is less) if this condition is true then made entry in logs and leave this data.
					if ( stock   <= networkStocksVO.getWalletbalance())
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Network stock is less than requested quantity. So all records after this can not be processed");
						errorList.add(focBatchItemVO);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Network stock is less");
						addData(focBatchItemVO,dataList);
						continue;
					}
					previousNwStockToBeSetChnlTrfItems=stock;
					if(stock != -1) {
						stock -= networkStocksVO.getWalletbalance();
					}
					if(stockSold != -1) {
						stockSold += networkStocksVO.getWalletbalance();
					}
					m = 0;
					//Deebit the network stock
					_pstmtupdateSelectedNetworkStock.clearParameters();
					_pstmtupdateSelectedNetworkStock.setLong(++m, stock);
					_pstmtupdateSelectedNetworkStock.setLong(++m, stockSold);
					_pstmtupdateSelectedNetworkStock.setString(++m, networkStocksVO.getLastTxnNum());
					_pstmtupdateSelectedNetworkStock.setString(++m, networkStocksVO.getLastTxnType());
					_pstmtupdateSelectedNetworkStock.setLong(++m, networkStocksVO.getLastTxnBalance());
					_pstmtupdateSelectedNetworkStock.setString(++m, networkStocksVO.getModifiedBy());
					_pstmtupdateSelectedNetworkStock.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStocksVO.getModifiedOn()));
					_pstmtupdateSelectedNetworkStock.setString(++m, networkStocksVO.getNetworkCode());
					_pstmtupdateSelectedNetworkStock.setString(++m, networkStocksVO.getProductCode());
					_pstmtupdateSelectedNetworkStock.setString(++m, networkStocksVO.getNetworkCodeFor());
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
						_pstmtupdateSelectedNetworkStock.setString(++m, PretupsI.INCENTIVE_WALLET_TYPE);
					else 
						_pstmtupdateSelectedNetworkStock.setString(++m, PretupsI.SALE_WALLET_TYPE);

					updateCount = _pstmtupdateSelectedNetworkStock.executeUpdate();
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if (updateCount <= 0)
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating network stock table");
						errorList.add(focBatchItemVO);
						continue;
					}

					//for logging                
					networkStocksVO.setPreviousBalance(stock);
					// AutoNetworkStockCreation logic
					if((boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.AUTO_NWSTK_CRTN_ALWD, networkStocksVO.getNetworkCode())){
						new com.btsl.pretups.channel.transfer.businesslogic.AutoNetworkStockBL().networkStockThresholdValidation(networkStocksVO);
					}
					networkStockTxnVO = new NetworkStockTxnVO();
					networkStockTxnVO.setNetworkCode( networkStocksVO.getNetworkCode());
					networkStockTxnVO.setNetworkFor( networkStocksVO.getNetworkCodeFor());
					if(networkStocksVO.getNetworkCode().equals(p_focBatchMatserVO.getNetworkCodeFor())) {
						networkStockTxnVO.setStockType( PretupsI.TRANSFER_STOCK_TYPE_HOME);
					} else {
						networkStockTxnVO.setStockType( PretupsI.TRANSFER_STOCK_TYPE_ROAM);
					}

					if("1".equals(_approvalLevel))
					{
						focBatchItemVO.setFirstApprovedBy(PretupsI.SYSTEM);
						focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						focBatchItemVO.setFirstApproverRemarks(focBatchItemVO.getInitiatorRemarks());
					}

					else if("2".equals(_approvalLevel))
					{
						focBatchItemVO.setFirstApprovedBy(PretupsI.SYSTEM);
						focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						focBatchItemVO.setFirstApproverRemarks(focBatchItemVO.getInitiatorRemarks());
						focBatchItemVO.setSecondApprovedBy(PretupsI.SYSTEM);
						focBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						focBatchItemVO.setSecondApproverRemarks(focBatchItemVO.getInitiatorRemarks());
					}
					else if("3".equals(_approvalLevel))
					{
						focBatchItemVO.setFirstApprovedBy(PretupsI.SYSTEM);
						focBatchItemVO.setFirstApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						focBatchItemVO.setFirstApproverRemarks(focBatchItemVO.getInitiatorRemarks());
						focBatchItemVO.setSecondApprovedBy(PretupsI.SYSTEM);
						focBatchItemVO.setSecondApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						focBatchItemVO.setSecondApproverRemarks(focBatchItemVO.getInitiatorRemarks());
						focBatchItemVO.setThirdApprovedBy(PretupsI.SYSTEM);
						focBatchItemVO.setThirdApprovedOn(BTSLUtil.getTimestampFromUtilDate(date));
						focBatchItemVO.setThirdApproverRemarks(focBatchItemVO.getInitiatorRemarks());
					}

					//As discussed with sandeep in channel transfer table's reference number field we have
					//to insert batch details id.So In network stock where channel transfer table's reference number
					//was inserted, I insert batch detail id.
					networkStockTxnVO.setReferenceNo( focBatchItemVO.getBatchDetailId());
					networkStockTxnVO.setTxnDate( focBatchItemVO.getInitiatedOn());
					networkStockTxnVO.setRequestedQuantity( focBatchItemVO.getRequestedQuantity());
					networkStockTxnVO.setApprovedQuantity( focBatchItemVO.getRequestedQuantity());
					networkStockTxnVO.setInitiaterRemarks( focBatchItemVO.getInitiatorRemarks());
					networkStockTxnVO.setFirstApprovedRemarks( focBatchItemVO.getFirstApproverRemarks());
					networkStockTxnVO.setSecondApprovedRemarks( focBatchItemVO.getSecondApproverRemarks());
					networkStockTxnVO.setFirstApprovedBy( focBatchItemVO.getFirstApprovedBy());
					networkStockTxnVO.setSecondApprovedBy( focBatchItemVO.getSecondApprovedBy());
					networkStockTxnVO.setFirstApprovedOn( focBatchItemVO.getFirstApprovedOn());
					networkStockTxnVO.setSecondApprovedOn( focBatchItemVO.getSecondApprovedOn());
					networkStockTxnVO.setCancelledBy( focBatchItemVO.getCancelledBy());
					networkStockTxnVO.setCancelledOn( focBatchItemVO.getCancelledOn());
					networkStockTxnVO.setCreatedBy(PretupsI.SYSTEM);
					networkStockTxnVO.setCreatedOn( date);
					networkStockTxnVO.setModifiedOn(date );
					networkStockTxnVO.setModifiedBy(PretupsI.SYSTEM);

					networkStockTxnVO.setTxnStatus( focBatchItemVO.getStatus());
					networkStockTxnVO.setEntryType(PretupsI.NETWORK_STOCK_TRANSACTION_TRANSFER );
					networkStockTxnVO.setTxnType( PretupsI.DEBIT);
					networkStockTxnVO.setInitiatedBy(PretupsI.SYSTEM);
					networkStockTxnVO.setFirstApproverLimit(0);
					networkStockTxnVO.setUserID( focBatchItemVO.getInitiatedBy());
					networkStockTxnVO.setTxnMrp( focBatchItemVO.getTransferMrp());

					//generate network stock transaction id
					network_id=NetworkStockBL.genrateStockTransctionID(networkStockTxnVO);
					networkStockTxnVO.setTxnNo(network_id);

					networkItemsVO = new NetworkStockTxnItemsVO();
					networkItemsVO.setSNo(1);
					networkItemsVO.setTxnNo(networkStockTxnVO.getTxnNo());
					networkItemsVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
					networkItemsVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
					networkItemsVO.setMrp(focBatchItemVO.getTransferMrp());
					networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
					networkItemsVO.setAmount(0);
					networkItemsVO.setProductCode(p_focBatchMatserVO.getProductCode());
					networkItemsVO.setStock(previousNwStockToBeSetChnlTrfItems);
					//Added on 07/02/08
					networkItemsVO.setDateTime(p_focBatchMatserVO.getBatchDate());
					m=0;
					_pstmtInsertNetworkStockTransaction.clearParameters();
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getTxnNo());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getNetworkCode());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getNetworkFor());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getStockType());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getReferenceNo());
					if (networkStockTxnVO.getTxnDate() != null) {
						_pstmtInsertNetworkStockTransaction.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getTxnDate()));
					} else {
						_pstmtInsertNetworkStockTransaction.setTimestamp(++m, null);
					}
					_pstmtInsertNetworkStockTransaction.setLong(++m, networkStockTxnVO.getRequestedQuantity());
					_pstmtInsertNetworkStockTransaction.setLong(++m, networkStockTxnVO.getApprovedQuantity());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getInitiaterRemarks());

					//for multilanguage support
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getFirstApprovedRemarks());

					//for multilanguage support
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getSecondApprovedRemarks());

					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getFirstApprovedBy());

					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getSecondApprovedBy());

					_pstmtInsertNetworkStockTransaction.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getFirstApprovedOn()));
					_pstmtInsertNetworkStockTransaction.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getSecondApprovedOn()));
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getCancelledBy());
					_pstmtInsertNetworkStockTransaction.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCancelledOn()));
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getCreatedBy());
					_pstmtInsertNetworkStockTransaction.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getCreatedOn()));
					_pstmtInsertNetworkStockTransaction.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(networkStockTxnVO.getModifiedOn()));
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getModifiedBy());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getTxnStatus());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getEntryType());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getTxnType());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getInitiatedBy());
					_pstmtInsertNetworkStockTransaction.setLong(++m, networkStockTxnVO.getFirstApproverLimit());
					_pstmtInsertNetworkStockTransaction.setString(++m, networkStockTxnVO.getUserID());
					_pstmtInsertNetworkStockTransaction.setLong(++m, networkStockTxnVO.getTxnMrp());
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue())
					{
						_pstmtInsertNetworkStockTransaction.setString(++m, PretupsI.INCENTIVE_WALLET_TYPE);	
					}else{
						_pstmtInsertNetworkStockTransaction.setString(++m, PretupsI.SALE_WALLET_TYPE);	
					}
					_pstmtInsertNetworkStockTransaction.setString(++m, channelTransferVO.getTransferID());	
					updateCount = _pstmtInsertNetworkStockTransaction.executeUpdate();
					updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if (updateCount <= 0)
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating network stock TXN table");
						errorList.add(focBatchItemVO);
						continue;
					}
					m = 0;
					_pstmtInsertNetworkStockTransactionItem.clearParameters();
					_pstmtInsertNetworkStockTransactionItem.setInt(++m, networkItemsVO.getSNo());
					_pstmtInsertNetworkStockTransactionItem.setString(++m, networkItemsVO.getTxnNo());
					_pstmtInsertNetworkStockTransactionItem.setString(++m, networkItemsVO.getProductCode());
					_pstmtInsertNetworkStockTransactionItem.setLong(++m, networkItemsVO.getRequiredQuantity());
					_pstmtInsertNetworkStockTransactionItem.setLong(++m, networkItemsVO.getApprovedQuantity());
					_pstmtInsertNetworkStockTransactionItem.setLong(++m, networkItemsVO.getStock());
					_pstmtInsertNetworkStockTransactionItem.setLong(++m, networkItemsVO.getMrp());
					_pstmtInsertNetworkStockTransactionItem.setLong(++m, networkItemsVO.getAmount());
					//Date 07/02/08
					_pstmtInsertNetworkStockTransactionItem.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));

					updateCount = _pstmtInsertNetworkStockTransactionItem.executeUpdate();
					updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if (updateCount <= 0)
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating network stock TXN itmes table");
						errorList.add(focBatchItemVO);
						continue;
					}
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME,"isOwnerUserNotSame = "+isOwnerUserNotSame);
					}

					if(isOwnerUserNotSame)
					{
						terminateProcessing=false;
						long newBalance=0;
						updateCount=0;

						_pstmtSelectBalance.clearParameters();
						_pstmtSelectBalance.setString(1, OwnerId);
						_pstmtSelectBalance.setString(2, channelTransferVO.getProductType());
						_pstmtSelectBalance.setString(3, channelTransferVO.getNetworkCode());
						_pstmtSelectBalance.setString(4, channelTransferVO.getNetworkCodeFor());
						if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
						{
							_pstmtSelectBalance.setString(5,PretupsI.WALLET_TYPE_BONUS);
						} else {
							_pstmtSelectBalance.setString(5, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
						}	
						rs = _pstmtSelectBalance.executeQuery();

						if (rs.next())
						{
							balance=rs.getLong("balance");
						}else
						{
							_pstmtInsertBalance.clearParameters();
							balance=0;
							balance = focBatchItemVO.getRequestedQuantity();
							m=0;
							_pstmtInsertBalance.setLong(++m,0);
							_pstmtInsertBalance.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
							_pstmtInsertBalance.setLong(++m,balance);
							_pstmtInsertBalance.setString(++m,PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
							_pstmtInsertBalance.setString(++m,o2cTransferID);
							_pstmtInsertBalance.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
							{
								_pstmtInsertBalance.setString(++m,PretupsI.WALLET_TYPE_BONUS);
							} else {
								_pstmtInsertBalance.setString(++m,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
							}
							_pstmtInsertBalance.setString(++m,OwnerId);
							_pstmtInsertBalance.setString(++m,p_focBatchMatserVO.getProductCode());
							_pstmtInsertBalance.setString(++m,p_focBatchMatserVO.getNetworkCode());
							_pstmtInsertBalance.setString(++m,p_focBatchMatserVO.getNetworkCodeFor());
							insertCount =	_pstmtInsertBalance.executeUpdate();

							if (insertCount <= 0)
							{
								p_con.rollback();
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while inserting user balances table");
								errorList.add(focBatchItemVO);
								terminateProcessing=true;
								break;
							}	
							if(terminateProcessing)
							{
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating user balances table");
								errorList.add(focBatchItemVO);
								continue;
							}	
							if(_logger.isDebugEnabled()) {
								_logger.debug(METHOD_NAME,"After inserting new user balances information");
							}
							_pstmtUpdateBalance.clearParameters();
						}	
						if(insertCount == 0)
						{
							newBalance=balance+focBatchItemVO.getRequestedQuantity();
							_pstmtUpdateBalance.clearParameters();
							_pstmtUpdateBalance.setLong(1, newBalance);
							_pstmtUpdateBalance.setString(2, channelTransferVO.getTransferType());
							_pstmtUpdateBalance.setString(3, channelTransferVO.getTransferID());
							_pstmtUpdateBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
							{
								_pstmtUpdateBalance.setString(5,PretupsI.WALLET_TYPE_BONUS);
							} else {
								_pstmtUpdateBalance.setString(5,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
							}
							_pstmtUpdateBalance.setString(6, OwnerId);
							_pstmtUpdateBalance.setString(7, channelTransferVO.getProductType());
							_pstmtUpdateBalance.setString(8, channelTransferVO.getNetworkCode());
							_pstmtUpdateBalance.setString(9, channelTransferVO.getNetworkCodeFor());
							updateCount = _pstmtUpdateBalance.executeUpdate();
							if (updateCount <= 0)
							{
								p_con.rollback();
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating user balances table");
								errorList.add(focBatchItemVO);
								terminateProcessing=true;
								break;
							}	
						}
						if (_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME, "balance = " + balance);
						}

						if (insertCount <= 0) {
							balance = newBalance;
							newBalance = balance - focBatchItemVO.getRequestedQuantity();
						}
						else {
							balance = newBalance;
						}
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME,"newBalance = "+newBalance);
						}

						if(balance  < focBatchItemVO.getRequestedQuantity())
						{
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"FOCBatchTransferDAO[closeOrderByBatchForDirectPayout]","","","","Owner current balance is less than required balance for requested Adjustment Dr Amt.");	
							if (_logger.isDebugEnabled()) {
								_logger.debug(METHOD_NAME, "Owner Current Bal:" + balance + "And required Dr Amt : " + channelTransferVO.getPayableAmount());
							}
						}
						if(newBalance<0){
							p_con.rollback();
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating user balances table");
							errorList.add(focBatchItemVO);
							terminateProcessing=true;

							break;
						}
						_pstmtUpdateBalance.clearParameters();
						_pstmtUpdateBalance.setLong(1, newBalance);
						_pstmtUpdateBalance.setString(2, channelTransferVO.getTransferType());
						_pstmtUpdateBalance.setString(3, channelTransferVO.getTransferID());
						_pstmtUpdateBalance.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getTransferDate()));
						if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
						{
							_pstmtUpdateBalance.setString(5,PretupsI.WALLET_TYPE_BONUS);
						} else {
							_pstmtUpdateBalance.setString(5,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
						}
						_pstmtUpdateBalance.setString(6, OwnerId);
						_pstmtUpdateBalance.setString(7, channelTransferVO.getProductType());
						_pstmtUpdateBalance.setString(8, channelTransferVO.getNetworkCode());
						_pstmtUpdateBalance.setString(9, channelTransferVO.getNetworkCodeFor());
						updateCount = _pstmtUpdateBalance.executeUpdate();
						if (updateCount <= 0)
						{
							p_con.rollback();
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating user balances table");
							errorList.add(focBatchItemVO);
							terminateProcessing=true;
							break;
						}	

						if(terminateProcessing)
						{
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating user balances table");
							errorList.add(focBatchItemVO);
							continue;
						}	
						try{if (rs != null){rs.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
					}
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME,"channelUserVO.getUserID() = "+channelUserVO.getUserID());
					}
					dailyBalanceUpdatedOn=null;
					dayDifference=0;
					//select the record form the userBalances table.
					_pstmtSelectUserBalances.clearParameters();
					m=0;
					_pstmtSelectUserBalances.setString(++m,channelUserVO.getUserID());
					_pstmtSelectUserBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(date));
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
					{
						_pstmtSelectUserBalances.setString(++m,PretupsI.WALLET_TYPE_BONUS);
					} else {
						_pstmtSelectUserBalances.setString(++m,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
					}	
					try{
					rs=null;
					rs=_pstmtSelectUserBalances.executeQuery();
					while(rs.next())
					{
						dailyBalanceUpdatedOn=rs.getDate("daily_balance_updated_on");
						//if record exist check updated on date with current date
						//day differences to maintain the record of previous days.
						dayDifference=BTSLUtil.getDifferenceInUtilDates(dailyBalanceUpdatedOn,date);
						if(dayDifference>0)
						{
							//if dates are not equal get the day differencts and execute insert qurery no of times of the 
							if(_logger.isDebugEnabled()) {
								_logger.debug("closeOrdersByBatch ","Till now daily Stock is not updated on "+date+", day differences = "+dayDifference);
							}

							for(k=0;k<dayDifference;k++)
							{
								_pstmtInsertUserDailyBalances.clearParameters();
								m=0;
								_pstmtInsertUserDailyBalances.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(BTSLUtil.addDaysInUtilDate(dailyBalanceUpdatedOn,k)));
								_pstmtInsertUserDailyBalances.setString(++m,rs.getString("user_id"));
								_pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code"));

								_pstmtInsertUserDailyBalances.setString(++m,rs.getString("network_code_for"));
								_pstmtInsertUserDailyBalances.setString(++m,rs.getString("product_code"));
								_pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("balance"));
								_pstmtInsertUserDailyBalances.setLong(++m,rs.getLong("prev_balance"));
								_pstmtInsertUserDailyBalances.setString(++m,PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);

								_pstmtInsertUserDailyBalances.setString(++m,channelTransferVO.getTransferID());
								_pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
								_pstmtInsertUserDailyBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
								_pstmtInsertUserDailyBalances.setString(++m,PretupsI.DAILY_BALANCE_CREATION_TYPE_MAN);
								if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue())
								{
									_pstmtInsertUserDailyBalances.setString(++m,PretupsI.WALLET_TYPE_BONUS);
								} else {
									_pstmtInsertUserDailyBalances.setString(++m,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
								}	
								updateCount=_pstmtInsertUserDailyBalances.executeUpdate();
								
								// added to make code compatible with insertion in partitioned table in postgres
								updateCount = BTSLUtil.getInsertCount(updateCount); 
								
								if(_logger.isDebugEnabled()) {
									_logger.debug(METHOD_NAME,"updateCount = "+updateCount);
								}
								if (updateCount <= 0)
								{
									p_con.rollback();
									DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while inserting user daily balances table");
									errorList.add(focBatchItemVO);
									terminateProcessing=true;
									break;
								}							
							}//end of for loop
							if(terminateProcessing)
							{
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Terminting the procssing of this user as error while updation daily balance");
								errorList.add(focBatchItemVO);
								continue;
							}
							//Update the user balances table
							_pstmtUpdateUserBalances.clearParameters();
							m=0;
							_pstmtUpdateUserBalances.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
							_pstmtUpdateUserBalances.setString(++m,channelUserVO.getUserID());
							if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
								_pstmtUpdateUserBalances.setString(++m,PretupsI.WALLET_TYPE_BONUS);
							} else {
								_pstmtUpdateUserBalances.setString(++m,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
							}	
							updateCount=_pstmtUpdateUserBalances.executeUpdate();
							//(record not updated properly) if this condition is true then made entry in logs and leave this data.
							if (updateCount <= 0)
							{
								p_con.rollback();
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating user balances table for daily balance");
								errorList.add(focBatchItemVO);
								continue;
							}
						}
					}//end of if condition
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					maxBalance=0;
					isNotToExecuteQuery = false;
					_pstmtSelectBalance.clearParameters();
					m=0;
					_pstmtSelectBalance.setString(++m,channelUserVO.getUserID());
					_pstmtSelectBalance.setString(++m,p_focBatchMatserVO.getProductCode());
					_pstmtSelectBalance.setString(++m,p_focBatchMatserVO.getNetworkCode());
					_pstmtSelectBalance.setString(++m,p_focBatchMatserVO.getNetworkCodeFor());
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
						_pstmtSelectBalance.setString(++m,PretupsI.WALLET_TYPE_BONUS);
					} else {
						_pstmtSelectBalance.setString(++m,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
					}	
					try{
					rs=null;
					rs = _pstmtSelectBalance.executeQuery();
					balance = -1;
					previousUserBalToBeSetChnlTrfItems=-1;
					if(rs.next())
					{
						balance = rs.getLong("balance");
						balanceExist = true;
					}
					if(balance > -1)
					{
						previousUserBalToBeSetChnlTrfItems=balance;
						balance += focBatchItemVO.getRequestedQuantity();
					}
					else
					{
						previousUserBalToBeSetChnlTrfItems=0;
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					_pstmtLoadTransferProfileProduct.clearParameters();
					m=0;
					_pstmtLoadTransferProfileProduct.setString(++m,focBatchItemVO.getTxnProfile());
					_pstmtLoadTransferProfileProduct.setString(++m,p_focBatchMatserVO.getProductCode());
					_pstmtLoadTransferProfileProduct.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
					_pstmtLoadTransferProfileProduct.setString(++m,PretupsI.YES);
					try{
					rs=null;
					rs = _pstmtLoadTransferProfileProduct.executeQuery();
					//get the transfer profile of user
					if(rs.next())
					{
						transferProfileProductVO = new TransferProfileProductVO();
						transferProfileProductVO.setProductCode(p_focBatchMatserVO.getProductCode());
						transferProfileProductVO.setMinResidualBalanceAsLong(rs.getLong("min_residual_balance"));
						transferProfileProductVO.setMaxBalanceAsLong(rs.getLong("max_balance"));
					}
					//(transfer profile not found) if this condition is true then made entry in logs and leave this data.
					else
					{
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User Trf Profile not found for product");
						errorList.add(focBatchItemVO);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("User Trf Profile not found for product");
						addData(focBatchItemVO,dataList);
						continue;
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					maxBalance=transferProfileProductVO.getMaxBalanceAsLong();
					//(max balance reach for the receiver) if this condition is true then made entry in logs and leave this data.
					if(maxBalance< balance )
					{
						if(!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User Max balance reached");
						errorList.add(focBatchItemVO);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("User Max balance reached");
						addData(focBatchItemVO,dataList);
						continue;
					}
					//check for the very first txn of the user containg the order value larger than maxBalance
					//(max balance reach) if this condition is true then made entry in logs and leave this data.
					else if(balance==-1 && maxBalance<focBatchItemVO.getRequestedQuantity())
					{
						if(!isNotToExecuteQuery) {
							isNotToExecuteQuery = true;
						}
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"User Max balance reached");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("User Max balance reached");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					if(!isNotToExecuteQuery)
					{
						m = 0;
						//update
						if(balanceExist && balance > -1){
							_pstmtUpdateBalance.clearParameters();
							_handlerStmt = _pstmtUpdateBalance;
						}
						else  if( balanceExist && balance <= -1){
							p_con.rollback();
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName, "Current balance is less than requested quantity.");
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Current balance is less than requested quantity.");
							addData(focBatchItemVO,dataList);
							errorList.add(focBatchItemVO);
							continue;

						}
						else if (!balanceExist && balance+ focBatchItemVO.getRequestedQuantity()<= -1 ){
							p_con.rollback();
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName, "Current balance is less than requested quantity.");
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Current balance is less than requested quantity.");
							addData(focBatchItemVO,dataList);
							errorList.add(focBatchItemVO);
							continue;

						}
						else if(!balanceExist && balance + focBatchItemVO.getRequestedQuantity() > -1 ){
							// insert
							_pstmtInsertBalance.clearParameters();
							_handlerStmt = _pstmtInsertBalance;
							balance = focBatchItemVO.getRequestedQuantity();
							_handlerStmt.setLong(++m,0);//previous balance
							_handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));//updated on date
						}
						_handlerStmt.setLong(++m,balance);
						_handlerStmt.setString(++m,PretupsI.NETWORK_STOCK_TRANSACTION_COMMISSION);
						_handlerStmt.setString(++m,o2cTransferID);
						_handlerStmt.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
						if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
							_handlerStmt.setString(++m,PretupsI.WALLET_TYPE_BONUS);
						} else {
							_handlerStmt.setString(++m,((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_WALLET)));
						}	
						_handlerStmt.setString(++m,channelUserVO.getUserID());
						//where
						_handlerStmt.setString(++m,p_focBatchMatserVO.getProductCode());
						_handlerStmt.setString(++m,p_focBatchMatserVO.getNetworkCode());
						_handlerStmt.setString(++m,p_focBatchMatserVO.getNetworkCodeFor());

						updateCount = _handlerStmt.executeUpdate();
						_handlerStmt.clearParameters();
						if(updateCount <= 0 )
						{
							p_con.rollback();
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB error while credit uer balance");
							errorList.add(focBatchItemVO);
							continue;
						}

						transferProfileProductVO =TransferProfileProductCache.getTransferProfileDetails(focBatchItemVO.getTxnProfile(),p_focBatchMatserVO.getProductCode());
						thresholdValue=transferProfileProductVO.getMinResidualBalanceAsLong();
						String threshold_type=PretupsI.THRESHOLD_TYPE_MIN;
						String remark = null;
						if(balance<=transferProfileProductVO.getAltBalanceLong() && balance>=transferProfileProductVO.getMinResidualBalanceAsLong())
						{
							thresholdValue=transferProfileProductVO.getAltBalanceLong();
							threshold_type=PretupsI.THRESHOLD_TYPE_ALERT;
						}
						//thresholdValue=(Long)PreferenceCache.getControlPreference(PreferenceI.ZERO_BAL_THRESHOLD_VALUE,p_focBatchMatserVO.getNetworkCode(), focBatchItemVO.getCategoryCode()); //threshold value
						//for zero balance counter..
						try
						{

							m=0;

							//24dec addded by nilesh:if previous bal is below threshold and current bal is above threshold,
							//then entry in user_threshold_counter.Also,if previous bal is already below threshold and current bal is also below threshold
							//then also entry in user_threshold_counter table(Discussed with Ved Sir and Protim Sir)
							if((previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance >=thresholdValue) || (previousUserBalToBeSetChnlTrfItems<=thresholdValue && balance <=thresholdValue))
							{
								if (_logger.isDebugEnabled())
								{
									_logger.debug(METHOD_NAME, "Entry in threshold counter" + thresholdValue+ ", prvbal: "+previousUserBalToBeSetChnlTrfItems+ "nbal"+ balance);
								}
								_psmtInsertUserThreshold.clearParameters();
								m=0;
								_psmtInsertUserThreshold.setString(++m, channelUserVO.getUserID());
								_psmtInsertUserThreshold.setString(++m, o2cTransferID);
								_psmtInsertUserThreshold.setDate(++m, BTSLUtil.getSQLDateFromUtilDate(date));
								_psmtInsertUserThreshold.setTimestamp(++m, BTSLUtil.getTimestampFromUtilDate(date));
								_psmtInsertUserThreshold.setString(++m, p_focBatchMatserVO.getNetworkCode());
								_psmtInsertUserThreshold.setString(++m, p_focBatchMatserVO.getProductCode());
								_psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TYPE_O2C);
								_psmtInsertUserThreshold.setString(++m, PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);

								if(balance>=thresholdValue){
									_psmtInsertUserThreshold.setString(++m, PretupsI.ABOVE_THRESHOLD_TYPE);
								}
								else{
									_psmtInsertUserThreshold.setString(++m, PretupsI.BELOW_THRESHOLD_TYPE);
								}
								_psmtInsertUserThreshold.setString(++m,focBatchItemVO.getCategoryCode());
								_psmtInsertUserThreshold.setLong(++m,previousUserBalToBeSetChnlTrfItems);
								_psmtInsertUserThreshold.setLong(++m, balance);
								_psmtInsertUserThreshold.setLong(++m, thresholdValue);
								//added by nilesh
								_psmtInsertUserThreshold.setString(++m, threshold_type);
								_psmtInsertUserThreshold.setString(++m, remark);
								_psmtInsertUserThreshold.executeUpdate();
							}
						}
						catch (SQLException sqle)
						{
							_logger.errorTrace(METHOD_NAME, sqle);
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[closeOrderByBatchForDirectPayout]",o2cTransferID,"",p_focBatchMatserVO.getNetworkCode(),"Error while updating user_threshold_counter table SQL Exception:"+sqle.getMessage());
						}// end of catch
					}
					_pstmtSelectTransferCounts.clearParameters();
					m=0;
					_pstmtSelectTransferCounts.setString(++m,channelUserVO.getUserID());
					try{
					rs=null;
					rs = _pstmtSelectTransferCounts.executeQuery();
					//get the user transfer counts
					countsVO=null;
					if (rs.next())
					{
						countsVO = new UserTransferCountsVO();
						countsVO.setUserID( focBatchItemVO.getUserId() );

						countsVO.setDailyInCount( rs.getLong("daily_in_count") );
						countsVO.setDailyInValue( rs.getLong("daily_in_value") );
						countsVO.setWeeklyInCount( rs.getLong("weekly_in_count") );
						countsVO.setWeeklyInValue( rs.getLong("weekly_in_value") );
						countsVO.setMonthlyInCount( rs.getLong("monthly_in_count") );
						countsVO.setMonthlyInValue( rs.getLong("monthly_in_value") );

						countsVO.setDailyOutCount( rs.getLong("daily_out_count") );
						countsVO.setDailyOutValue( rs.getLong("daily_out_value") );
						countsVO.setWeeklyOutCount( rs.getLong("weekly_out_count") );
						countsVO.setWeeklyOutValue( rs.getLong("weekly_out_value") );
						countsVO.setMonthlyOutCount( rs.getLong("monthly_out_count") );
						countsVO.setMonthlyOutValue( rs.getLong("monthly_out_value") );				

						countsVO.setUnctrlDailyInCount( rs.getLong("outside_daily_in_count") );
						countsVO.setUnctrlDailyInValue( rs.getLong("outside_daily_in_value") );
						countsVO.setUnctrlWeeklyInCount( rs.getLong("outside_weekly_in_count") );
						countsVO.setUnctrlWeeklyInValue( rs.getLong("outside_weekly_in_value") );
						countsVO.setUnctrlMonthlyInCount( rs.getLong("outside_monthly_in_count") );
						countsVO.setUnctrlMonthlyInValue( rs.getLong("outside_monthly_in_value") );

						countsVO.setUnctrlDailyOutCount( rs.getLong("outside_daily_out_count") );
						countsVO.setUnctrlDailyOutValue( rs.getLong("outside_daily_out_value") );
						countsVO.setUnctrlWeeklyOutCount( rs.getLong("outside_weekly_out_count") );
						countsVO.setUnctrlWeeklyOutValue( rs.getLong("outside_weekly_out_value") );
						countsVO.setUnctrlMonthlyOutCount( rs.getLong("outside_monthly_out_count") );
						countsVO.setUnctrlMonthlyOutValue( rs.getLong("outside_monthly_out_value") );

						countsVO.setDailySubscriberOutCount( rs.getLong("daily_subscriber_out_count") );
						countsVO.setDailySubscriberOutValue( rs.getLong("daily_subscriber_out_value") );
						countsVO.setWeeklySubscriberOutCount( rs.getLong("weekly_subscriber_out_count") );
						countsVO.setWeeklySubscriberOutValue( rs.getLong("weekly_subscriber_out_value") );
						countsVO.setMonthlySubscriberOutCount( rs.getLong("monthly_subscriber_out_count") );
						countsVO.setMonthlySubscriberOutValue( rs.getLong("monthly_subscriber_out_value") );

						countsVO.setLastTransferDate(rs.getDate("last_transfer_date") );
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					flag=true;
					if(countsVO == null)
					{
						flag = false;
						countsVO = new UserTransferCountsVO();
					}
					//If found then check for reset otherwise no need to check it
					if(flag) {
						ChannelTransferBL.checkResetCountersAfterPeriodChange(countsVO,date);
					}
					_pstmtSelectProfileCounts.clearParameters();
					m=0;
					_pstmtSelectProfileCounts.setString(++m,focBatchItemVO.getTxnProfile());
					_pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
					_pstmtSelectProfileCounts.setString(++m,p_focBatchMatserVO.getNetworkCode());
					_pstmtSelectProfileCounts.setString(++m,PretupsI.PARENT_PROFILE_ID_CATEGORY);
					_pstmtSelectProfileCounts.setString(++m,PretupsI.YES);
					try{
					rs=null;
					rs = _pstmtSelectProfileCounts.executeQuery();
					//get the transfwer profile counts
					if (rs.next())
					{
						transferProfileVO = new TransferProfileVO();
						transferProfileVO.setProfileId(rs.getString("profile_id"));
						transferProfileVO.setDailyInCount( rs.getLong("daily_transfer_in_count") );
						transferProfileVO.setDailyInValue( rs.getLong("daily_transfer_in_value"));
						transferProfileVO.setWeeklyInCount( rs.getLong("weekly_transfer_in_count") );
						transferProfileVO.setWeeklyInValue( rs.getLong("weekly_transfer_in_value"));
						transferProfileVO.setMonthlyInCount( rs.getLong("monthly_transfer_in_count") );
						transferProfileVO.setMonthlyInValue( rs.getLong("monthly_transfer_in_value"));
					}
					//(profile counts not found) if this condition is true then made entry in logs and leave this data.
					else
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Transfer profile not found");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Transfer profile not found");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					//(daily in count reach) if this condition is true then made entry in logs and leave this data.
					if(transferProfileVO.getDailyInCount() <= countsVO.getDailyInCount())
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Daily transfer in count reach");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Daily transfer in count reach");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					//(daily in value reach) if this condition is true then made entry in logs and leave this data.
					else if(transferProfileVO.getDailyInValue() < (countsVO.getDailyInValue() + focBatchItemVO.getRequestedQuantity() )  )
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Daily transfer in value reach");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Daily transfer in value reach");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					//(weekly in count reach) if this condition is true then made entry in logs and leave this data.
					else if(transferProfileVO.getWeeklyInCount() <=  countsVO.getWeeklyInCount() )
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Weekly transfer in count reach");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Weekly transfer in count reach");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					//(weekly in value reach) if this condition is true then made entry in logs and leave this data.
					else if(transferProfileVO.getWeeklyInValue() < ( countsVO.getWeeklyInValue() + focBatchItemVO.getRequestedQuantity() )  )
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Weekly transfer in value reach");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Weekly transfer in value reach");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					//(monthly in count reach) if this condition is true then made entry in logs and leave this data.
					else if(transferProfileVO.getMonthlyInCount() <=  countsVO.getMonthlyInCount()  )
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Monthly transfer in count reach");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Monthly transfer in count reach");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					//(mobthly in value reach) if this condition is true then made entry in logs and leave this data.
					else if(transferProfileVO.getMonthlyInValue() < ( countsVO.getMonthlyInValue() + focBatchItemVO.getRequestedQuantity() ) )
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"Monthly transfer in value reach");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Monthly transfer in value reach");
						addData(focBatchItemVO,dataList);
						errorList.add(focBatchItemVO);
						continue;
					}
					countsVO.setUserID(channelUserVO.getUserID());
					countsVO.setDailyInCount(countsVO.getDailyInCount()+1);
					countsVO.setWeeklyInCount(countsVO.getWeeklyInCount()+1);
					countsVO.setMonthlyInCount(countsVO.getMonthlyInCount()+1);
					countsVO.setDailyInValue(countsVO.getDailyInValue()+focBatchItemVO.getRequestedQuantity());
					countsVO.setWeeklyInValue(countsVO.getWeeklyInValue()+focBatchItemVO.getRequestedQuantity());
					countsVO.setMonthlyInValue(countsVO.getMonthlyInValue()+focBatchItemVO.getRequestedQuantity());
					countsVO.setLastInTime(date);
					countsVO.setLastTransferID(o2cTransferID);
					countsVO.setLastTransferDate(date);
					//Update counts if found in db
					if(flag)
					{
						m = 0 ;
						_pstmtUpdateTransferCounts.clearParameters();
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyInValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyInValue());

						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailyOutValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklyOutValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlyOutValue());

						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyInValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyInValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyInValue());

						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlDailyOutValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlWeeklyOutValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getUnctrlMonthlyOutValue());

						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getDailySubscriberOutValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getWeeklySubscriberOutValue());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutCount());
						_pstmtUpdateTransferCounts.setLong(++m,countsVO.getMonthlySubscriberOutValue());

						_pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
						_pstmtUpdateTransferCounts.setString(++m,countsVO.getLastTransferID());
						_pstmtUpdateTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
						_pstmtUpdateTransferCounts.setString(++m,countsVO.getUserID());
						updateCount = _pstmtUpdateTransferCounts.executeUpdate();
					}
					//Insert counts if not found in db
					else
					{
						m = 0 ;
						_pstmtInsertTransferCounts.clearParameters();
						_pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInCount());
						_pstmtInsertTransferCounts.setLong(++m,countsVO.getDailyInValue());
						_pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInCount());
						_pstmtInsertTransferCounts.setLong(++m,countsVO.getWeeklyInValue());
						_pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInCount());
						_pstmtInsertTransferCounts.setLong(++m,countsVO.getMonthlyInValue());
						_pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastInTime()));
						_pstmtInsertTransferCounts.setString(++m,countsVO.getLastTransferID());
						_pstmtInsertTransferCounts.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(countsVO.getLastTransferDate()));
						_pstmtInsertTransferCounts.setString(++m,countsVO.getUserID());
						updateCount = _pstmtInsertTransferCounts.executeUpdate();
					}
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if(updateCount <= 0  )
					{
						p_con.rollback();
						errorList.add(focBatchItemVO);
						if(flag) {
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB error while insert user trasnfer counts");
						} else {
							DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB error while uptdate user trasnfer counts");
						}
						continue;
					}

					//(external txn number is checked) 
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue() && !BTSLUtil.isNullString(focBatchItemVO.getExtTxnNo()))
					{
						//check in channel transfer table
						_pstmtIsTxnNumExists2.clearParameters();
						m=0;
						_pstmtIsTxnNumExists2.setString(++m,PretupsI.CHANNEL_TYPE_O2C);
						_pstmtIsTxnNumExists2.setString(++m,focBatchItemVO.getExtTxnNo());
						_pstmtIsTxnNumExists2.setString(++m,PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
						try{
							rs=null;
							rs=_pstmtIsTxnNumExists2.executeQuery();
							//if this condition is true then made entry in logs and leave this data.
							if(rs.next())
							{
								p_con.rollback();
								DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"External transaction number already exists in CHANNEL TRF");
								focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
								focBatchItemVO.setError("External transaction number is not unique");
								addData(focBatchItemVO,dataList);
								errorList.add(focBatchItemVO);
								continue;
							}
						}
						finally{
							if(rs!=null)
								rs.close();
						}
						
					}
					//If level 1 apperoval then set parameters in _psmtAppr1FOCBatchItem
					_psmtAppr1FOCBatchItem.clearParameters();
					m=0;
					_psmtAppr1FOCBatchItem.setString(++m,o2cTransferID);
					_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
					_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					if("1".equals(_approvalLevel))
					{
						_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getInitiatorRemarks());
						_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
						_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					}

					else if("2".equals(_approvalLevel))
					{
						_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getInitiatorRemarks());
						_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
						_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getInitiatorRemarks());
						_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
						_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					}
					else if("3".equals(_approvalLevel))
					{
						_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getInitiatorRemarks());
						_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
						_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getInitiatorRemarks());
						_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
						_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
						_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getInitiatorRemarks());
						_psmtAppr1FOCBatchItem.setString(++m,PretupsI.SYSTEM);
						_psmtAppr1FOCBatchItem.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					}
					_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getStatus());
					_psmtAppr1FOCBatchItem.setString(++m,focBatchItemVO.getBatchDetailId());
					updateCount=_psmtAppr1FOCBatchItem.executeUpdate();
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if(updateCount<=0)
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while updating foc batch items table");
						errorList.add(focBatchItemVO);
						continue;
					}
					channelTransferVO.setCanceledOn(focBatchItemVO.getCancelledOn());
					channelTransferVO.setCanceledBy(focBatchItemVO.getCancelledBy());
					channelTransferVO.setChannelRemarks(focBatchItemVO.getInitiatorRemarks());
					channelTransferVO.setCommProfileSetId(focBatchItemVO.getCommissionProfileSetId());
					channelTransferVO.setCommProfileVersion(focBatchItemVO.getCommissionProfileVer());
					channelTransferVO.setCreatedBy(focBatchItemVO.getInitiatedBy());
					channelTransferVO.setDomainCode(p_focBatchMatserVO.getDomainCode());
					channelTransferVO.setExternalTxnDate(focBatchItemVO.getExtTxnDate());
					channelTransferVO.setExternalTxnNum(focBatchItemVO.getExtTxnNo());
					channelTransferVO.setFinalApprovedBy(focBatchItemVO.getFirstApprovedBy());
					channelTransferVO.setFirstApprovedOn(focBatchItemVO.getFirstApprovedOn());
					channelTransferVO.setFirstApproverLimit(0);
					channelTransferVO.setFirstApprovalRemark(focBatchItemVO.getFirstApproverRemarks());
					channelTransferVO.setSecondApprovedBy(focBatchItemVO.getSecondApprovedBy());
					channelTransferVO.setSecondApprovedOn(focBatchItemVO.getSecondApprovedOn());
					channelTransferVO.setSecondApprovalLimit(0);
					channelTransferVO.setSecondApprovalRemark(focBatchItemVO.getSecondApproverRemarks());
					channelTransferVO.setCategoryCode(PretupsI.OPERATOR_TYPE_OPT);
					channelTransferVO.setBatchNum(focBatchItemVO.getBatchId());
					channelTransferVO.setBatchDate(p_focBatchMatserVO.getBatchDate());
					channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
					channelTransferVO.setTotalTax3(0);
					channelTransferVO.setPayableAmount(0);
					channelTransferVO.setNetPayableAmount(0);
					channelTransferVO.setPayInstrumentAmt(0);
					channelTransferVO.setGraphicalDomainCode(channelUserVO.getGeographicalCode());
					channelTransferVO.setModifiedBy(PretupsI.SYSTEM);
					channelTransferVO.setModifiedOn(date);
					channelTransferVO.setProductType(p_focBatchMatserVO.getProductType());
					channelTransferVO.setReceiverCategoryCode(focBatchItemVO.getCategoryCode());
					channelTransferVO.setReceiverGradeCode(focBatchItemVO.getUserGradeCode());
					channelTransferVO.setReceiverTxnProfile(focBatchItemVO.getTxnProfile());
					channelTransferVO.setReferenceNum(focBatchItemVO.getBatchDetailId());	    		
		
					// for balance logger
					channelTransferVO.setReferenceID(network_id);
					//ends here
					if(messageGatewayVO!=null && messageGatewayVO.getRequestGatewayVO()!=null)
					{
						channelTransferVO.setRequestGatewayCode(messageGatewayVO.getRequestGatewayVO().getGatewayCode());
						channelTransferVO.setRequestGatewayType(messageGatewayVO.getGatewayType());
					}
					channelTransferVO.setRequestedQuantity(focBatchItemVO.getRequestedQuantity());
					channelTransferVO.setSource(PretupsI.REQUEST_SOURCE_WEB);
					channelTransferVO.setStatus(focBatchItemVO.getStatus());
					channelTransferVO.setThirdApprovedBy(focBatchItemVO.getThirdApprovedBy());
					channelTransferVO.setThirdApprovedOn(focBatchItemVO.getThirdApprovedOn());
					channelTransferVO.setThirdApprovalRemark(focBatchItemVO.getThirdApproverRemarks());
					channelTransferVO.setToUserID(channelUserVO.getUserID());
					channelTransferVO.setTotalTax1(focBatchItemVO.getTax1Value());
					channelTransferVO.setTotalTax2(focBatchItemVO.getTax2Value());
					channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_TRANSFER);
					channelTransferVO.setTransferDate(focBatchItemVO.getInitiatedOn());
					channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER);
					channelTransferVO.setTransferID(o2cTransferID);
					channelTransferVO.setTransferInitatedBy(focBatchItemVO.getInitiatedBy());
					channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
					channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
					channelTransferVO.setTransferMRP(focBatchItemVO.getTransferMrp());
					channelTransferItemVO =new ChannelTransferItemsVO();
					channelTransferItemVO.setApprovedQuantity(focBatchItemVO.getRequestedQuantity());
					channelTransferItemVO.setCommProfileDetailID(focBatchItemVO.getCommissionProfileDetailId());
					channelTransferItemVO.setCommRate(focBatchItemVO.getCommissionRate());
					channelTransferItemVO.setCommType(focBatchItemVO.getCommissionType());
					channelTransferItemVO.setCommValue(focBatchItemVO.getCommissionValue());
					channelTransferItemVO.setNetPayableAmount(0);
					channelTransferItemVO.setPayableAmount(0);
					channelTransferItemVO.setProductTotalMRP(focBatchItemVO.getTransferMrp());
					channelTransferItemVO.setProductCode(p_focBatchMatserVO.getProductCode());
					channelTransferItemVO.setReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
					channelTransferItemVO.setSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
					channelTransferItemVO.setRequiredQuantity(focBatchItemVO.getRequestedQuantity());
					channelTransferItemVO.setSerialNum(1);
					channelTransferItemVO.setTax1Rate(focBatchItemVO.getTax1Rate());
					channelTransferItemVO.setTax1Type(focBatchItemVO.getTax1Type());
					channelTransferItemVO.setTax1Value(focBatchItemVO.getTax1Value());
					channelTransferItemVO.setTax2Rate(focBatchItemVO.getTax2Rate());
					channelTransferItemVO.setTax2Type(focBatchItemVO.getTax2Type());
					channelTransferItemVO.setTax2Value(focBatchItemVO.getTax2Value());
					channelTransferItemVO.setTax3Rate(focBatchItemVO.getTax3Rate());
					channelTransferItemVO.setTax3Type(focBatchItemVO.getTax3Type());
					channelTransferItemVO.setTax3Value(focBatchItemVO.getTax3Value());
					channelTransferItemVO.setTransferID(o2cTransferID);
					channelTransferItemVO.setUnitValue(p_focBatchMatserVO.getProductMrp());
					// for the balance logger
					channelTransferItemVO.setAfterTransReceiverPreviousStock(previousUserBalToBeSetChnlTrfItems);
					channelTransferItemVO.setAfterTransSenderPreviousStock(previousNwStockToBeSetChnlTrfItems);
					//ends here
					channelTransferItemVOList=new ArrayList();
					channelTransferItemVOList.add(channelTransferItemVO);
					channelTransferItemVO.setShortName(p_focBatchMatserVO.getProductShortName());
					channelTransferVO.setChannelTransferitemsVOList(channelTransferItemVOList);
					if (_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Exiting: channelTransferVO=" + channelTransferVO.toString());
					}
					if (_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "Exiting: channelTransferItemVO=" + channelTransferItemVO.toString());
					}
					/*//The query below is used to insert the record in channel transfers table for the order that is cloaed*/
					m = 0;
					_pstmtInsertIntoChannelTranfers.clearParameters();
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCanceledBy());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCanceledOn()));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getChannelRemarks());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileSetId());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCommProfileVersion());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCreatedBy());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getCreatedOn()));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getExternalTxnDate()));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getExternalTxnNum());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFinalApprovedBy());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getFirstApprovedOn()));
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getFirstApproverLimit());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFirstApprovalRemark());
					_pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getBatchDate()));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getBatchNum());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getFromUserID());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getModifiedBy());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getModifiedOn()));
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getNetPayableAmount());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCode());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getNetworkCodeFor());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayableAmount());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getPayInstrumentAmt());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getProductType());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverCategoryCode());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverGradeCode());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReceiverTxnProfile());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getReferenceNum());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayCode());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getRequestGatewayType());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getRequestedQuantity());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovedBy());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getSecondApprovedOn()));
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getSecondApprovalLimit());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondApprovalRemark());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSource());
					_pstmtInsertIntoChannelTranfers.setString(++m,focBatchItemVO.getStatus());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovedBy());
					_pstmtInsertIntoChannelTranfers.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(channelTransferVO.getThirdApprovedOn()));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getThirdApprovalRemark());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getToUserID());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax1());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax2());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTotalTax3());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferCategory());
					_pstmtInsertIntoChannelTranfers.setDate(++m,BTSLUtil.getSQLDateFromUtilDate(channelTransferVO.getTransferDate()));
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferID());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferInitatedBy());
					_pstmtInsertIntoChannelTranfers.setLong(++m,channelTransferVO.getTransferMRP());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferSubType());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getTransferType());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getType());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getCategoryCode());
					_pstmtInsertIntoChannelTranfers.setString(++m,PretupsI.YES);
					_pstmtInsertIntoChannelTranfers.setString(++m,focBatchItemVO.getMsisdn());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDomainCode());

					// to geographical domain also inserted as the geogrpahical domain that will help in reports
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getGraphicalDomainCode());

					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getDefaultLang());
					_pstmtInsertIntoChannelTranfers.setString(++m,channelTransferVO.getSecondLang());
					//                	added for bonus type
					_pstmtInsertIntoChannelTranfers.setString(++m,focBatchItemVO.getBonusType());
					if(isOwnerUserNotSame)
					{
						_pstmtInsertIntoChannelTranfers.setLong(++m,focBatchItemVO.getRequestedQuantity());
						_pstmtInsertIntoChannelTranfers.setLong(++m,focBatchItemVO.getRequestedQuantity());
					}
					else
					{
						_pstmtInsertIntoChannelTranfers.setLong(++m,0);
						_pstmtInsertIntoChannelTranfers.setLong(++m,0);
					}
					_pstmtInsertIntoChannelTranfers.setString(++m, _activeUserName);
					if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MULTIPLE_WALLET_APPLY)).booleanValue()) {
						_pstmtInsertIntoChannelTranfers.setString(++m,PretupsI.INCENTIVE_WALLET_TYPE);
					}else{
						_pstmtInsertIntoChannelTranfers.setString(++m,PretupsI.SALE_WALLET_TYPE);
					}
						

					//insert into channel transfer table
					updateCount=_pstmtInsertIntoChannelTranfers.executeUpdate();
					updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if(updateCount<=0)
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while inserting in channel transfer table");
						errorList.add(focBatchItemVO);
						continue;
					}
					m=0;
					_pstmtInsertIntoChannelTransferItems.clearParameters();
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getApprovedQuantity());
					_pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getCommProfileDetailID());
					_pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getCommRate());
					_pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getCommType());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getCommValue());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getProductTotalMRP());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getNetPayableAmount());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getPayableAmount());
					_pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getProductCode());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getReceiverPreviousStock());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getRequiredQuantity());
					_pstmtInsertIntoChannelTransferItems.setInt(++m,channelTransferItemVO.getSerialNum());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getSenderPreviousStock());
					_pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getTax1Rate());
					_pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getTax1Type());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getTax1Value());
					_pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getTax2Rate());
					_pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getTax2Type());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getTax2Value());
					_pstmtInsertIntoChannelTransferItems.setDouble(++m,channelTransferItemVO.getTax3Rate());
					_pstmtInsertIntoChannelTransferItems.setString(++m,channelTransferItemVO.getTax3Type());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getTax3Value());
					_pstmtInsertIntoChannelTransferItems.setTimestamp(++m,BTSLUtil.getTimestampFromUtilDate(date));
					_pstmtInsertIntoChannelTransferItems.setString(++m,o2cTransferID);
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getUnitValue());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getApprovedQuantity());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getApprovedQuantity());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getCommQuantity());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getSenderPreviousStock()-channelTransferItemVO.getApprovedQuantity());
					_pstmtInsertIntoChannelTransferItems.setLong(++m,channelTransferItemVO.getReceiverPreviousStock()+channelTransferItemVO.getApprovedQuantity());
					//insert into channel transfer items table
					updateCount=_pstmtInsertIntoChannelTransferItems.executeUpdate();
					updateCount = BTSLUtil.getInsertCount(updateCount); // added to make code compatible with insertion in partitioned table in postgres
					//(record not updated properly) if this condition is true then made entry in logs and leave this data.
					if(updateCount<=0)
					{
						p_con.rollback();
						DirectPayOutErrorLog.log(focBatchItemVO.getMsisdn(),fileName,"DB Error while inserting in channel transfer items table");
						errorList.add(focBatchItemVO);
						continue;
					}
					//commit the transaction after processing each record
					p_con.commit();
					/// add data success data in list 
					focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					addData(focBatchItemVO,dataList);
					focBatchItemVO.setPreBalance(String.valueOf(previousUserBalToBeSetChnlTrfItems));
					focBatchItemVO.setPostBalance(String.valueOf(balance));
					focBatchItemVO.setReferenceNo(o2cTransferID);

					DirectPayOutSuccessLog.log(focBatchItemVO.getMsisdn(),fileName,"Order is closed successfully",o2cTransferID,focBatchItemVO,_networkCode,_productCode);
					//made entry in network stock and balance logger
					ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
					_pstmtSelectBalanceInfoForMessage.clearParameters();
					m=0;
					_pstmtSelectBalanceInfoForMessage.setString(++m, channelUserVO.getUserID());
					_pstmtSelectBalanceInfoForMessage.setString(++m, p_focBatchMatserVO.getNetworkCode());
					_pstmtSelectBalanceInfoForMessage.setString(++m, p_focBatchMatserVO.getNetworkCodeFor());
					try{
					rs=null;
					rs = _pstmtSelectBalanceInfoForMessage.executeQuery();
					userbalanceList= new ArrayList();
					while (rs.next())
					{
						balancesVO = new UserBalancesVO();
						balancesVO.setProductCode(rs.getString("product_code"));
						balancesVO.setBalance(rs.getLong("balance"));
						balancesVO.setProductShortCode(rs.getString("product_short_code"));
						balancesVO.setProductShortName(rs.getString("short_name"));
						userbalanceList.add(balancesVO);
					}
					}
					finally{
						if(rs!=null)
							rs.close();
					}
					//generate the message arguments to be send in SMS
					keyArgumentVO = new KeyArgumentVO();
					argsArr = new String[2];
					argsArr[1] = PretupsBL.getDisplayAmount(channelTransferItemVO.getRequiredQuantity());
					argsArr[0] = String.valueOf(channelTransferItemVO.getShortName());
					keyArgumentVO.setKey(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS2);
					keyArgumentVO.setArguments(argsArr);
					txnSmsMessageList=new ArrayList();
					balSmsMessageList=new ArrayList();
					txnSmsMessageList.add(keyArgumentVO);
					for(int index=0,n=userbalanceList.size();index<n;index++)
					{
						balancesVO=(UserBalancesVO)userbalanceList.get(index);
						if(balancesVO.getProductCode().equals(channelTransferItemVO.getProductCode()))
						{
							argsArr=new String[2];
							argsArr[1]=balancesVO.getBalanceAsString();
							argsArr[0]=balancesVO.getProductShortName();
							keyArgumentVO = new KeyArgumentVO();
							keyArgumentVO.setKey(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS_BALSUBKEY);
							keyArgumentVO.setArguments(argsArr);
							balSmsMessageList.add(keyArgumentVO);
							break;
						}
					}
					locale=new Locale(language,country);
					String focNotifyMsg=null;
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_SMS_NOTIFY))).booleanValue())
					{
						LocaleMasterVO localeVO=LocaleMasterCache.getLocaleDetailsFromlocale(locale);
						if(PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
							focNotifyMsg=channelTransferVO.getDefaultLang();
						} else {
							focNotifyMsg=channelTransferVO.getSecondLang();
						}
						array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList),focNotifyMsg};
					}  			

					if(focNotifyMsg==null) {
						array=new String[] {channelTransferVO.getTransferID(),BTSLUtil.getMessage(locale,txnSmsMessageList),BTSLUtil.getMessage(locale,balSmsMessageList)};
					}

					messages=new BTSLMessages(PretupsErrorCodesI.DP_OPT_CHNL_TRANSFER_SMS1,array);
					pushMessage=new PushMessage(focBatchItemVO.getMsisdn(),messages,channelTransferVO.getTransferID(),null,locale,channelTransferVO.getNetworkCode()); 
					//push SMS
					pushMessage.push();
					OneLineTXNLog.log(channelTransferVO, focBatchItemVO);
				}//end of while
				//delete records from FOC_BATCH_ITEMS table if there is an error in record.
				if(errorList != null && !errorList.isEmpty())
				{
					String str= "DELETE FROM FOC_BATCH_ITEMS WHERE BATCH_ID=? AND BATCH_DETAIL_ID=? ";
					if(_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME,"Query: "+str);
					}
					errorPstm=p_con.prepareStatement(str);
					for(int i=0;i<errorList.size();i++)
					{
						FOCBatchItemsVO focItemVO=(FOCBatchItemsVO)errorList.get(i);
						errorPstm.clearParameters();
						errorPstm.setString(1,focItemVO.getBatchId());
						errorPstm.setString(2,focItemVO.getBatchDetailId());
						errorPstm.executeUpdate();
					}
					//all the recrds in the file are invalid then delete entry from FOC_BATCH and FOC_BATCH_GEOGRAPHY table
					if(p_focBatchItemList.size()==errorList.size())
					{
						String focBatch= "DELETE FOC_BATCHES where BATCH_ID=? ";
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME,"Query: "+focBatch);
						}
						focbatchPstm=p_con.prepareStatement(focBatch);
						focbatchPstm.clearParameters();
						focbatchPstm.setString(1,p_focBatchMatserVO.getBatchId());
						focbatchPstm.executeQuery();
						String focGeoBatch= "DELETE FOC_BATCH_GEOGRAPHIES where BATCH_ID=? ";
						if(_logger.isDebugEnabled()) {
							_logger.debug(METHOD_NAME,"Query: "+focGeoBatch);
						}
						focbatchGeoPstm=p_con.prepareStatement(focGeoBatch);
						focbatchGeoPstm.clearParameters();
						focbatchGeoPstm.setString(1,p_focBatchMatserVO.getBatchId());
						focbatchGeoPstm.executeQuery();
					}
					p_con.commit();
					str=null;
				}
			}//end of try
			catch(BTSLBaseException be)
			{
				_logger.errorTrace(METHOD_NAME,be);
				throw be;
			}
			catch (SQLException sqe)
			{
				try{if(p_con!=null) {
					p_con.rollback();
				}}catch(Exception e){_logger.errorTrace(METHOD_NAME, e);}
				_logger.errorTrace(METHOD_NAME, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[closeOrderByBatchForDirectPayout]","","","","SQL Exception:"+sqe.getMessage());
				DirectPayOutErrorLog.log("NA",fileName,"SQL Exception:"+sqe.getMessage());
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			}
			catch (Exception ex)
			{
				try{if(p_con!=null) {
					p_con.rollback();
				}}catch(Exception e){_logger.errorTrace(METHOD_NAME, e);}
				_logger.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[closeOrderByBatchForDirectPayout]","","","","Exception:"+ex.getMessage());
				DirectPayOutErrorLog.log("NA",fileName,"Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			}
			finally
			{
				try
				{
					if(p_focBatchItemList.size()>errorList.size())
					{
						int m=0;
						_pstmtUpdateMaster.setLong(++m,p_focBatchItemList.size()-errorList.size());
						_pstmtUpdateMaster.setString(++m,batch_ID);
						_pstmtUpdateMaster.setString(++m,PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);

						updateCount=_pstmtUpdateMaster.executeUpdate();
						//(record not updated properly) if this condition is true then made entry in logs and leave this data.
						if(updateCount<=0)
						{
							p_con.rollback();
							DirectPayOutErrorLog.log("NA",fileName,"DB Error while updating master table");
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[closeOrederByBatch]","","","","Error while updating FOC_BATCHES table. Batch id="+batch_ID);
						}//end of if
						//  }//end of if
						p_con.commit();
					}
				}
				catch (SQLException sqe)
				{
					try{if(p_con!=null) {
						p_con.rollback();
					}}catch(Exception e){_logger.errorTrace(METHOD_NAME, e);}
					_logger.errorTrace(METHOD_NAME, sqe);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[closeOrderByBatchForDirectPayout]","","","","SQL Exception:"+sqe.getMessage());
					DirectPayOutErrorLog.log("NA",fileName,"SQL Exception:"+sqe.getMessage());
					thowBTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
				}
				catch (Exception ex)
				{
					try{if(p_con!=null) {
						p_con.rollback();
					}}catch(Exception e){_logger.errorTrace(METHOD_NAME, e);}
					_logger.errorTrace(METHOD_NAME, ex);
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[closeOrderByBatchForDirectPayout]","","","","Exception:"+ex.getMessage());
					DirectPayOutErrorLog.log("NA",fileName,"Exception:"+ex.getMessage());
					thowBTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
					
				}
				try{if (rs != null){rs.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (errorPstm != null){errorPstm.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (focbatchPstm != null){focbatchPstm.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (focbatchGeoPstm != null){focbatchGeoPstm.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "Exiting:");
				}
			}
		}

		 private void  thowBTSLBaseException(Object classObj, String method, String message) throws  BTSLBaseException {
			 throw new BTSLBaseException(classObj, method, message);
		 }
		//
		/**
		 * create all prepared Statements
		 * @param p_con Connection
		 * @throws BTSLBaseException
		 */
		private void createGlobalPreparedStatement(Connection p_con) throws BTSLBaseException
		{
			final String METHOD_NAME = "createGlobalPreparedStatement";
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Entered");
			}

			try
			{
				StringBuilder strBuffSelectExtTxnID1 = new StringBuilder(" SELECT 1 FROM foc_batch_items ");
				strBuffSelectExtTxnID1.append("WHERE ext_txn_no=? AND status <> ?  ");
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "strBuffSelectExtTxnID1 Query ="+strBuffSelectExtTxnID1);
				}

				StringBuilder strBuffSelectExtTxnID2 = new StringBuilder(" SELECT 1 FROM channel_transfers ");
				strBuffSelectExtTxnID2.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "strBuffSelectExtTxnID2 Query ="+strBuffSelectExtTxnID2);
					// ends here
				}

				// for loading the products associated with the commission profile
				StringBuilder strBuffSelectCProfileProd = new StringBuilder("SELECT cp.min_transfer_value,cp.max_transfer_value,cp.discount_type,cp.discount_rate, ");
				strBuffSelectCProfileProd.append("cp.comm_profile_products_id, cp.transfer_multiple_off, cp.taxes_on_foc_applicable  ");
				strBuffSelectCProfileProd.append("FROM commission_profile_products cp ");
				strBuffSelectCProfileProd.append("WHERE cp.product_code = ? AND cp.comm_profile_set_id = ? AND cp.comm_profile_set_version = ? ");
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
				strBuffSelectCProfileProd.append("AND cp.transaction_type in ( ? , ? ) "); 
				else
					strBuffSelectCProfileProd.append("AND cp.transaction_type = ? "); 
				strBuffSelectCProfileProd.append("AND cp.payment_mode = ? ORDER BY cp.TRANSACTION_TYPE desc");
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "strBuffSelectCProfileProd Query ="+strBuffSelectCProfileProd);
				}

				StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("SELECT cpd.tax1_type,cpd.tax1_rate,cpd.tax2_type,cpd.tax2_rate, ");
				strBuffSelectCProfileProdDetail.append("cpd.tax3_type,cpd.tax3_rate,cpd.commission_type,cpd.commission_rate,cpd.comm_profile_detail_id ");
				strBuffSelectCProfileProdDetail.append("FROM commission_profile_details cpd ");
				if (PretupsI.YES.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED"))) {
					strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.end_range >= ? ");
				}
				else {
					strBuffSelectCProfileProdDetail.append("WHERE  cpd.comm_profile_products_id = ? AND cpd.start_range <= ? AND cpd.end_range >= ? ");
				}
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "strBuffSelectCProfileProdDetail Query ="+strBuffSelectCProfileProdDetail);
					//ends here
				}

				// for existance of the product in the transfer profile
				StringBuilder strBuffSelectTProfileProd = new StringBuilder(" SELECT 1 ");
				strBuffSelectTProfileProd.append("FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
				strBuffSelectTProfileProd.append("WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id ");
				strBuffSelectTProfileProd.append("AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status='Y' AND tp.network_code = catp.network_code");
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "strBuffSelectTProfileProd Query ="+strBuffSelectTProfileProd);
					//ends here
				}




				// insert data in the batch items table
				StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
				strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code, ");
				strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
				strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
				strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
				strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
				strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,bonus_type) "); 
				strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "strBuffInsertBatchItems Query ="+strBuffInsertBatchItems);
					//ends here
				}


				StringBuilder sqlBuffer = null;
				
				String tcpMicroServiceOn = Constants.getProperty("TCP.MICROSERVICE.ON");
				
				Set<String> uniqueTransProfileId = new HashSet();

				if (tcpMicroServiceOn != null && tcpMicroServiceOn.equalsIgnoreCase("Y")) {
					tcpOn = true;
				}
				String sqlSelect = null;

				if (tcpOn) {
					sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
					sqlBuffer.append(
							"cps.status commprofilestatus,cusers.transfer_profile_id,cps.language_1_message comprf_lang_1_msg, ");
					sqlBuffer.append(
							"cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
					sqlBuffer.append(
							"FROM users u,channel_users cusers,commission_profile_set cps, user_phones up,user_geographies ug ");
					sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
					sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
					sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
					sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
					sqlBuffer.append("  ug.user_id = u.user_id ");

					SearchCriteria searchCriteria = new SearchCriteria("profile_id", Operator.IN, new HashSet<String>(Arrays.asList("ALL")),
		 					ValueType.STRING);
		         	
		         	
		         	
		         	tcpMap = BTSLUtil.fetchMicroServiceTCPDataByKey(new HashSet<String>(Arrays.asList("profile_id","status")), searchCriteria);
		         	
				} else {

					sqlBuffer = new StringBuilder(" SELECT u.status userstatus, cusers.in_suspend, ");
					sqlBuffer.append(
							"cps.status commprofilestatus,tp.status profile_status,cusers.transfer_profile_id,cps.language_1_message comprf_lang_1_msg, ");
					sqlBuffer.append(
							"cps.language_2_message comprf_lang_2_msg,up.phone_language,up.country, ug.grph_domain_code ");
					sqlBuffer.append(
							"FROM users u,channel_users cusers,commission_profile_set cps,transfer_profile tp, user_phones up,user_geographies ug ");
					sqlBuffer.append("WHERE u.user_id = ? AND up.user_id=u.user_id AND up.primary_number = 'Y' ");
					sqlBuffer.append(" AND u.status <> 'N' AND u.status <> 'C' ");
					sqlBuffer.append(" AND u.user_id=cusers.user_id AND ");
					sqlBuffer.append(" cps.comm_profile_set_id = cusers.comm_profile_set_id AND ");
					sqlBuffer.append(" tp.profile_id = cusers.transfer_profile_id AND ug.user_id = u.user_id ");
				}
				String sqlLoadUser = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY sqlLoadUser=" + sqlLoadUser);
				}
				sqlBuffer=null;

				//The query below is used to load the network stock details for network in between sender and receiver
				//This table will basically used to update the daily_stock_updated_on and also to know how many
				//records are to be inseert in network_daily_stocks

				DirectPayOutQry directPayOutQry = (DirectPayOutQry) ObjectProducer.getObject(QueryConstants.DIRECT_PAYOUT_QRY, QueryConstants.QUERY_PRODUCER);
				String sqlLoadNetworkStock = directPayOutQry.selectNetworkDetailsFromNetworkStocksQry();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY sqlLoadNetworkStock=" + sqlLoadNetworkStock);
				}
				sqlBuffer=null;

				//Update daily_stock_updated_on with current date
				sqlBuffer=new StringBuilder("UPDATE network_stocks SET daily_stock_updated_on = ? ");
				sqlBuffer.append("WHERE network_code = ? AND network_code_for = ? AND wallet_type = ? ");
				String sqlUpdateNetworkStock = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY sqlUpdateNetworkStock=" + sqlUpdateNetworkStock);
				}
				sqlBuffer=null;

				//Executed if day difference in last updated date and current date is greater then or equal to 1
				//Insert number of records equal to day difference in last updated date and current date in  network_daily_stocks
				sqlBuffer=new StringBuilder("INSERT INTO network_daily_stocks(wallet_date, wallet_type, network_code, network_code_for, ");
				sqlBuffer.append("product_code, wallet_created, wallet_returned, wallet_balance, wallet_sold, last_txn_no, ");
				sqlBuffer.append("last_txn_type, last_txn_balance, previous_balance, created_on,creation_type )");
				sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
				String sqlInsertNetworkDailyStock = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY sqlInsertNetworkDailyStock=" + sqlInsertNetworkDailyStock);
				}

				String sqlSelectNetworkStock = directPayOutQry.selectWalletDetailsFromNetworkStocksQry();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY sqlSelectNetworkStock=" + sqlSelectNetworkStock);
				}
				sqlBuffer=null;

				//Debit the network stock
				sqlBuffer=new StringBuilder(" UPDATE network_stocks SET previous_balance = wallet_balance , wallet_balance = ?, ");
				sqlBuffer.append(" wallet_sold = ? , last_txn_no = ? , last_txn_type = ?, last_txn_balance= ?, ");
				sqlBuffer.append(" modified_by =?, modified_on =? ");
				sqlBuffer.append(" WHERE ");
				sqlBuffer.append(" network_code = ? ");
				sqlBuffer.append(" AND ");
				sqlBuffer.append(" product_code = ? AND network_code_for = ?  AND wallet_type = ? ");
				String updateSelectedNetworkStock = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY updateSelectedNetworkStock=" + updateSelectedNetworkStock);
				}
				sqlBuffer=null;

				//Insert record into network_stock_transactions table.
				sqlBuffer=new StringBuilder(" INSERT INTO network_stock_transactions ( ");
				sqlBuffer.append(" txn_no, network_code, network_code_for, stock_type, reference_no, txn_date, requested_quantity, "); 
				sqlBuffer.append(" approved_quantity, initiater_remarks, first_approved_remarks, second_approved_remarks, ");
				sqlBuffer.append(" first_approved_by, second_approved_by, first_approved_on, second_approved_on, ");
				sqlBuffer.append(" cancelled_by, cancelled_on, created_by, created_on, modified_on, modified_by, "); 
				sqlBuffer.append(" txn_status, entry_type, txn_type, initiated_by, first_approver_limit, user_id, txn_mrp ");
				sqlBuffer.append(",txn_wallet,ref_txn_id ");	
				sqlBuffer.append(" )VALUES ");
				sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
				
				String insertNetworkStockTransaction = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertNetworkStockTransaction=" + insertNetworkStockTransaction);
				}
				sqlBuffer=null;

				//Insert record into network_stock_trans_items
				sqlBuffer=new StringBuilder(" INSERT INTO network_stock_trans_items "); 
				sqlBuffer.append(" (s_no, txn_no, product_code, required_quantity, approved_quantity, stock, mrp, amount, date_time) ");
				sqlBuffer.append(" VALUES ");
				sqlBuffer.append(" (?,?,?,?,?,?,?,?,?) ");
				String insertNetworkStockTransactionItem = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertNetworkStockTransactionItem=" + insertNetworkStockTransactionItem);
				}
				sqlBuffer=null;

				//The query below is used to load the user balance
				//This table will basically used to update the daily_balance_updated_on and also to know how many
				//records are to be inseert in user_daily_balances table

				String selectUserBalances = directPayOutQry.selectFromUserBalanceWheredailyBalanceUpdatedQry();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectUserBalances=" + selectUserBalances);
				}
				sqlBuffer=null;

				//update daily_balance_updated_on with current date for user
				sqlBuffer=new StringBuilder(" UPDATE user_balances SET daily_balance_updated_on = ? ");
				sqlBuffer.append("WHERE user_id = ? and balance_type =? ");
				String updateUserBalances = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY updateUserBalances=" + updateUserBalances);
				}
				sqlBuffer=null;

				//Executed if day difference in last updated date and current date is greater then or equal to 1
				//Insert number of records equal to day difference in last updated date and current date in  user_daily_balances
				sqlBuffer=new StringBuilder(" INSERT INTO user_daily_balances(balance_date, user_id, network_code, ");
				sqlBuffer.append("network_code_for, product_code, balance, prev_balance, last_transfer_type, ");
				sqlBuffer.append("last_transfer_no, last_transfer_on, created_on,creation_type,balance_type )");
				sqlBuffer.append("VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
				String insertUserDailyBalances = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertUserDailyBalances=" + insertUserDailyBalances);
				}


				String selectBalance = directPayOutQry.selectFromUserBalanceWhereNetworkCode();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectBalance=" + selectBalance);
				}
				sqlBuffer=null;

				//Credit the user balance(If balance found in user_balances)
				sqlBuffer=new StringBuilder(" UPDATE user_balances SET prev_balance = balance, balance = ? , last_transfer_type = ? , "); 
				sqlBuffer.append(" last_transfer_no = ? , last_transfer_on = ? ");
				sqlBuffer.append(" WHERE ");
				sqlBuffer.append(" balance_type = ? AND user_id = ? ");
				sqlBuffer.append(" AND "); 
				sqlBuffer.append(" product_code = ? AND network_code = ? AND network_code_for = ? ");
				String updateBalance = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY updateBalance=" + updateBalance);
				}
				sqlBuffer=null;

				//Insert the record of balnce for user (If balance not found in user_balances)
				sqlBuffer=new StringBuilder(" INSERT "); 
				sqlBuffer.append(" INTO user_balances ");
				sqlBuffer.append(" ( prev_balance,daily_balance_updated_on , balance, last_transfer_type, last_transfer_no, last_transfer_on , balance_type , "); 
				sqlBuffer.append(" user_id, product_code , network_code, network_code_for ) ");
				sqlBuffer.append(" VALUES ");
				sqlBuffer.append(" (?,?,?,?,?,?,?,?,?,?,?) ");			
				String insertBalance = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertBalance=" + insertBalance);
				}
				sqlBuffer=null;

				//Select the running countres of user(to be checked against the effetive profile counters)
				sqlBuffer=new StringBuilder(" SELECT user_id, daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, ");
				sqlBuffer.append(" monthly_in_count, monthly_in_value,daily_out_count, daily_out_value, weekly_out_count, ");
				sqlBuffer.append(" weekly_out_value, monthly_out_count, monthly_out_value, outside_daily_in_count, ");
				sqlBuffer.append(" outside_daily_in_value, outside_weekly_in_count, outside_weekly_in_value, ");
				sqlBuffer.append(" outside_monthly_in_count, outside_monthly_in_value, outside_daily_out_count, ");
				sqlBuffer.append(" outside_daily_out_value, outside_weekly_out_count, outside_weekly_out_value, ");
				sqlBuffer.append(" outside_monthly_out_count, outside_monthly_out_value, daily_subscriber_out_count, ");
				sqlBuffer.append(" daily_subscriber_out_value, weekly_subscriber_out_count, weekly_subscriber_out_value, ");
				sqlBuffer.append(" monthly_subscriber_out_count, monthly_subscriber_out_value,last_transfer_date ");
				sqlBuffer.append(" FROM user_transfer_counts ");
				//DB220120123for update WITH RS 
				if(PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
					sqlBuffer.append(" WHERE user_id = ? FOR UPDATE WITH RS");
				} else {
					sqlBuffer.append(" WHERE user_id = ? FOR UPDATE ");
				}	
				String selectTransferCounts = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectTransferCounts=" + selectTransferCounts);
				}
				sqlBuffer=null;

				//Select the effective profile counters of user to be checked with running counters of user
				StringBuilder strBuff=new StringBuilder();
				strBuff.append(" SELECT tp.profile_id,LEAST(tp.daily_transfer_in_count,catp.daily_transfer_in_count) daily_transfer_in_count, "); 
				strBuff.append(" LEAST(tp.daily_transfer_in_value,catp.daily_transfer_in_value) daily_transfer_in_value ,LEAST(tp.weekly_transfer_in_count,catp.weekly_transfer_in_count) weekly_transfer_in_count, ");
				strBuff.append(" LEAST(tp.weekly_transfer_in_value,catp.weekly_transfer_in_value) weekly_transfer_in_value,LEAST(tp.monthly_transfer_in_count,catp.monthly_transfer_in_count) monthly_transfer_in_count, ");
				strBuff.append(" LEAST(tp.monthly_transfer_in_value,catp.monthly_transfer_in_value) monthly_transfer_in_value");
				strBuff.append(" FROM transfer_profile tp,transfer_profile catp ");
				strBuff.append(" WHERE tp.profile_id = ? AND tp.status = ?	AND tp.network_code = ? ");
				strBuff.append(" AND tp.category_code=catp.category_code ");	
				strBuff.append(" AND catp.parent_profile_id=? AND catp.status=?	AND tp.network_code = catp.network_code ");
				String selectProfileCounts = strBuff.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectProfileCounts=" + selectProfileCounts);
				}

				//Update the user running countres (If record found for user running counters)
				sqlBuffer=new StringBuilder(" UPDATE user_transfer_counts  SET "); 
				sqlBuffer.append(" daily_in_count = ?, daily_in_value = ?, weekly_in_count = ?, weekly_in_value = ?,");
				sqlBuffer.append(" monthly_in_count = ?, monthly_in_value = ? ,daily_out_count =?, daily_out_value=?, ");
				sqlBuffer.append(" weekly_out_count=?, weekly_out_value =?, monthly_out_count=?, monthly_out_value=?, ");
				sqlBuffer.append(" outside_daily_in_count=?, outside_daily_in_value=?, outside_weekly_in_count=?,");
				sqlBuffer.append(" outside_weekly_in_value=?, outside_monthly_in_count=?, outside_monthly_in_value=?, ");
				sqlBuffer.append(" outside_daily_out_count=?, outside_daily_out_value=?, outside_weekly_out_count=?, ");
				sqlBuffer.append(" outside_weekly_out_value=?, outside_monthly_out_count=?, outside_monthly_out_value=?, ");
				sqlBuffer.append(" daily_subscriber_out_count=?, daily_subscriber_out_value=?, weekly_subscriber_out_count=?, ");
				sqlBuffer.append(" weekly_subscriber_out_value=?, monthly_subscriber_out_count=?, monthly_subscriber_out_value=?, ");
				sqlBuffer.append(" last_in_time = ? , last_transfer_id=?,last_transfer_date=? "); 
				sqlBuffer.append(" WHERE user_id = ?  ");			
				String updateTransferCounts = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY updateTransferCounts=" + updateTransferCounts);
				}
				sqlBuffer=null;

				//Insert the record in user_transfer_counts (If no record found for user running counters)
				sqlBuffer=new StringBuilder(" INSERT INTO user_transfer_counts ( ");
				sqlBuffer.append(" daily_in_count, daily_in_value, weekly_in_count, weekly_in_value, monthly_in_count, ");
				sqlBuffer.append(" monthly_in_value, last_in_time, last_transfer_id,last_transfer_date,user_id ) ");
				sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?) ");
				String insertTransferCounts = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertTransferCounts=" + insertTransferCounts);
				}
				sqlBuffer=null;

				//If current level of approval is 1 then below query is used to updatwe foc_batch_items table
				sqlBuffer = new StringBuilder(" UPDATE  foc_batch_items SET   ");
				sqlBuffer.append(" reference_no=?, modified_by = ?, modified_on = ?,  ");
				if("1".equals(_approvalLevel)) {
					sqlBuffer.append(" first_approver_remarks = ?,first_approved_by=?, first_approved_on=? ," );
				} else if("2".equals(_approvalLevel)) {
					sqlBuffer.append(" first_approver_remarks = ?,first_approved_by=?, first_approved_on=? ,second_approver_remarks = ?, second_approved_by=? , second_approved_on=? ,");
				} else if("3".equals(_approvalLevel)) {
					sqlBuffer.append(" first_approver_remarks = ?,first_approved_by=?, first_approved_on=? ,second_approver_remarks = ?, second_approved_by=? , second_approved_on=? ,third_approver_remarks = ?,third_approved_by=? , third_approved_on=? ,");
				}
				sqlBuffer.append("  status = ? WHERE  batch_detail_id = ? ");
				String sqlApprv1FOCBatchItems = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY sqlApprv1FOCBatchItems=" + sqlApprv1FOCBatchItems);
				}
				sqlBuffer=null;

				//Update the master table when partial batch processed, status still open
				sqlBuffer = new StringBuilder("UPDATE foc_batches SET batch_total_record=batch_total_record+?");
				sqlBuffer.append(" WHERE batch_id=? AND status=? ");
				String updateFOCBatches = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY updateFOCBatches=" + updateFOCBatches);
				}
				sqlBuffer=null;

				//Select the transfer profile product values(These will be used for checking max balance of user)
				sqlBuffer = new StringBuilder("SELECT GREATEST(tpp.min_residual_balance,catpp.min_residual_balance) min_residual_balance, ");
				sqlBuffer.append(" LEAST(tpp.max_balance,catpp.max_balance) max_balance ");
				sqlBuffer.append(" FROM transfer_profile_products tpp,transfer_profile tp, transfer_profile catp,transfer_profile_products catpp ");
				sqlBuffer.append(" WHERE tpp.profile_id=? AND tpp.product_code = ? AND tpp.profile_id=tp.profile_id AND catp.profile_id=catpp.profile_id "); 
				sqlBuffer.append(" AND tpp.product_code=catpp.product_code AND tp.category_code=catp.category_code AND catp.parent_profile_id=? AND catp.status=? AND tp.network_code = catp.network_code	 ");
				String loadTransferProfileProduct = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY loadTransferProfileProduct=" + loadTransferProfileProduct);
				}
				sqlBuffer=null;

				// The below query will be exceute if external txn number unique is "Y" in system preferences
				// and external txn number is not exists in foc_batch_items table.
				//This will check the existence of external txn number in channel_transfers table
				sqlBuffer = new StringBuilder("  SELECT 1 FROM channel_transfers ");
				sqlBuffer.append("WHERE type=? AND ext_txn_no=? AND status <> ? ");
				String isExistsTxnNum2 = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY isExistsTxnNum2=" + isExistsTxnNum2);
				}
				sqlBuffer=null;

				//The query bel;ow is used to insert the record in channel transfer items table for the order that is closed
				sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers_items ");
				sqlBuffer.append("(approved_quantity, commission_profile_detail_id, commission_rate, commission_type, commission_value, mrp,  ");
				sqlBuffer.append(" net_payable_amount, payable_amount, product_code, receiver_previous_stock, required_quantity, s_no,  ");
				sqlBuffer.append(" sender_previous_stock, tax1_rate, tax1_type, tax1_value, tax2_rate, tax2_type, tax2_value, tax3_rate, tax3_type,  ");
				sqlBuffer.append(" tax3_value, transfer_date, transfer_id, user_unit_price,sender_debit_quantity, receiver_credit_quantity,commision_quantity, sender_post_stock, receiver_post_stock)  ");
				sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)  ");
				String insertIntoChannelTransferItem = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertIntoChannelTransferItem=" + insertIntoChannelTransferItem);
				}
				sqlBuffer=null;

				///the query lOHIT

				sqlBuffer = new StringBuilder(" SELECT transfer_rule_id,foc_transfer_type, direct_payout_allowed ");
				sqlBuffer.append("FROM chnl_transfer_rules WHERE network_code = ? AND domain_code = ? AND ");
				sqlBuffer.append("from_category = 'OPT' AND to_category = ? AND status = 'Y' AND type = 'OPT' ");
				String selectTransferRuleForDP=sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectTransferRuleForDP=" + selectTransferRuleForDP);
				}
				sqlBuffer=null;// ends here

				sqlBuffer = new StringBuilder("SELECT 1 FROM chnl_transfer_rules_products ");
				sqlBuffer.append("WHERE transfer_rule_id=?  AND product_code = ? ");
				String selectTransferRuleForProdDP=sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectTransferRuleForProdDP=" + selectTransferRuleForProdDP);
				}
				sqlBuffer=null;//ends here
				//The query below is used to get the balance information of user with product.
				//This information will be send in message to user
				sqlBuffer = new StringBuilder(" SELECT UB.product_code,UB.balance, ");
				sqlBuffer.append(" PROD.product_short_code, PROD.short_name ");
				sqlBuffer.append(" FROM user_balances UB,products PROD ");
				sqlBuffer.append(" WHERE UB.user_id = ?  AND UB.network_code = ? AND UB.network_code_for = ? AND UB.product_code=PROD.product_code "); 
				String selectBalanceInfoForMessage = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY selectBalanceInfoForMessage=" + selectBalanceInfoForMessage);
				}
				sqlBuffer=null;

				sqlBuffer = new StringBuilder();
				sqlBuffer.append(" select owner_id from users where user_id=?");
				String selectowner = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug("closeOrderByBatchForDirectPayout", "QUERY selectowner=" + selectowner);
				}
				sqlBuffer=null;

				sqlBuffer = new StringBuilder(" INSERT INTO channel_transfers ");
				sqlBuffer.append(" (cancelled_by, cancelled_on, channel_user_remarks, close_date, commission_profile_set_id, commission_profile_ver, ");
				sqlBuffer.append(" created_by, created_on, domain_code, ext_txn_date, ext_txn_no, first_approved_by, first_approved_on, ");
				sqlBuffer.append(" first_approver_limit, first_approver_remarks, batch_date, batch_no, from_user_id, grph_domain_code, ");
				sqlBuffer.append(" modified_by, modified_on, net_payable_amount, network_code, network_code_for, payable_amount, pmt_inst_amount, ");
				sqlBuffer.append("  product_type, receiver_category_code, receiver_grade_code, ");
				sqlBuffer.append(" receiver_txn_profile, reference_no, request_gateway_code, request_gateway_type, requested_quantity, second_approved_by, ");
				sqlBuffer.append(" second_approved_on, second_approver_limit, second_approver_remarks,  ");
				sqlBuffer.append("  source, status, third_approved_by, third_approved_on, third_approver_remarks, to_user_id,  ");
				sqlBuffer.append(" total_tax1, total_tax2, total_tax3, transfer_category, transfer_date, transfer_id, transfer_initiated_by, ");
				sqlBuffer.append(" transfer_mrp, transfer_sub_type, transfer_type, type,sender_category_code,");
				sqlBuffer.append(" control_transfer,to_msisdn,to_domain_code,to_grph_domain_code, sms_default_lang, sms_second_lang,bonus_type,owner_transfer_mrp,owner_debit_mrp,active_user_id ");
				sqlBuffer.append(",TXN_WALLET)");
				sqlBuffer.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?");
				sqlBuffer.append(",?)");
				
				String insertIntoChannelTransfer = sqlBuffer.toString();
				if (_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME, "QUERY insertIntoChannelTransfer=" + insertIntoChannelTransfer);
				}

				//added by nilesh:added two new columns threshold_type and remark
				StringBuilder strBuffThresholdInsert = new StringBuilder();
				strBuffThresholdInsert.append(" INSERT INTO user_threshold_counter ");
				strBuffThresholdInsert.append(" ( user_id,transfer_id , entry_date, entry_date_time, network_code, product_code , "); 
				strBuffThresholdInsert.append(" type , transaction_type, record_type, category_code,previous_balance,current_balance, threshold_value, threshold_type, remark ) ");
				strBuffThresholdInsert.append(" VALUES ");
				strBuffThresholdInsert.append(" (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");   
				String insertUserThreshold = strBuffThresholdInsert.toString();
				if (_logger.isDebugEnabled())
				{
					_logger.debug(METHOD_NAME, "QUERY insertUserThreshold=" + insertUserThreshold);
				}

				_pstmtSelectExtTxnID1=p_con.prepareStatement(strBuffSelectExtTxnID1.toString());
				_pstmtSelectExtTxnID2=p_con.prepareStatement(strBuffSelectExtTxnID2.toString());
				_pstmtSelectCProfileProd=p_con.prepareStatement(strBuffSelectCProfileProd.toString());
				_pstmtSelectCProfileProdDetail=p_con.prepareStatement(strBuffSelectCProfileProdDetail.toString());
				_pstmtSelectTProfileProd=p_con.prepareStatement(strBuffSelectTProfileProd.toString());



				_pstmtInsertBatchItems=(PreparedStatement)p_con.prepareStatement(strBuffInsertBatchItems.toString());


				_pstmtLoadUser = p_con.prepareStatement(sqlLoadUser);
				_pstmtLoadNetworkStock=p_con.prepareStatement(sqlLoadNetworkStock);
				_pstmtUpdateNetworkStock=p_con.prepareStatement(sqlUpdateNetworkStock);
				_pstmtInsertNetworkDailyStock=p_con.prepareStatement(sqlInsertNetworkDailyStock);
				_pstmtSelectNetworkStock=p_con.prepareStatement(sqlSelectNetworkStock);
				_pstmtupdateSelectedNetworkStock=p_con.prepareStatement(updateSelectedNetworkStock);
				_pstmtInsertNetworkStockTransaction=(PreparedStatement)p_con.prepareStatement(insertNetworkStockTransaction);
				_pstmtInsertNetworkStockTransactionItem=p_con.prepareStatement(insertNetworkStockTransactionItem);
				_pstmtSelectUserBalances=p_con.prepareStatement(selectUserBalances);
				_pstmtUpdateUserBalances=p_con.prepareStatement(updateUserBalances);
				_pstmtInsertUserDailyBalances=p_con.prepareStatement(insertUserDailyBalances);
				_pstmtSelectBalance=p_con.prepareStatement(selectBalance);
				_pstmtUpdateBalance=p_con.prepareStatement(updateBalance);
				_pstmtInsertBalance=p_con.prepareStatement(insertBalance);
				_pstmtSelectTransferCounts=p_con.prepareStatement(selectTransferCounts);
				_pstmtSelectProfileCounts=p_con.prepareStatement(selectProfileCounts);
				_pstmtUpdateTransferCounts=p_con.prepareStatement(updateTransferCounts);
				_pstmtInsertTransferCounts=p_con.prepareStatement(insertTransferCounts);

				_psmtAppr1FOCBatchItem=(PreparedStatement)p_con.prepareStatement(sqlApprv1FOCBatchItems);
				_pstmtUpdateMaster=(PreparedStatement)p_con.prepareStatement(updateFOCBatches);
				_pstmtLoadTransferProfileProduct=p_con.prepareStatement(loadTransferProfileProduct);
				_pstmtIsTxnNumExists2=p_con.prepareStatement(isExistsTxnNum2);
				_pstmtInsertIntoChannelTransferItems=p_con.prepareStatement(insertIntoChannelTransferItem);
				_pstmtSelectBalanceInfoForMessage=p_con.prepareStatement(selectBalanceInfoForMessage);
				_pstmtInsertIntoChannelTranfers=(PreparedStatement)p_con.prepareStatement(insertIntoChannelTransfer);
				_pstmtSelectOwner=p_con.prepareStatement(selectowner);
				//lOHIT
				_pstmtSelectTrfRule = p_con.prepareStatement(selectTransferRuleForDP);
				_pstmtSelectTrfRuleProd =p_con.prepareStatement(selectTransferRuleForProdDP);
				_psmtInsertUserThreshold=p_con.prepareStatement(insertUserThreshold);
			}
			catch (Exception ex)
			{
				_logger.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[createGlobalPreparedStatement]","","","","Exception:"+ex.getMessage());
				throw new BTSLBaseException(this, "processBatchDPTransfer", PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			}
			finally
			{
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME,"Exiting");
				}
			}
		}

		/**
		 * Method processBatchDPTransfer
		 * Each thread will independently insert into foc_batch_items table.
		 * @param p_con Connection
		 * @param p_batchMasterVO FOCBatchMasterVO
		 * @param p_batchItemsList ArrayList
		 * @param p_messages MessageResources
		 * @param p_locale Locale
		 * @return errorList ArrayList
		 * @throws BTSLBaseException 
		 */
		public  void processBatchDPTransfer(Connection p_con,FOCBatchMasterVO p_batchMasterVO,ArrayList<FOCBatchItemsVO> p_batchItemsList,ArrayList dataList)throws BTSLBaseException
		{
			final String METHOD_NAME = "processBatchDPTransfer";
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Entered.... p_batchMasterVO="+p_batchMasterVO+", p_batchItemsList.size() = "+p_batchItemsList.size());
			}

			ResultSet rsSelectExtTxnID1=null;
			ResultSet rsSelectExtTxnID2=null;
			ResultSet rsSelectTrfRule=null;
			ResultSet rsSelectTrfRuleProd=null;
			ResultSet rsSelectCProfileProd=null;
			ResultSet rsSelectCProfileProdDetail=null;
			ResultSet rsSelectTProfileProd=null;

			long totalSuccessRecords=0L;
			ChannelTransferRuleVO rulesVO=null;

			try{
				int index = 0;
				FOCBatchItemsVO  batchItemsVO = null;
				HashMap transferProfileMap = new HashMap();
				HashMap transferRuleNotExistMap = new HashMap();
				HashMap transferRuleProdNotExistMap = new HashMap();
				HashMap transferRuleMap = new HashMap();

				long requestedValue=0;
				long minTrfValue=0;
				long maxTrfValue=0;
				long multipleOf=0;
				ArrayList transferItemsList = null;
				ChannelTransferItemsVO channelTransferItemsVO = null;
				GeographicalDomainVO geographicalDomainVO=null;


				for(int i=0;i<p_batchItemsList.size();i++)
				{
					batchItemsVO=(FOCBatchItemsVO) p_batchItemsList.get(i);
					// check the uniqueness of the external txn number
					if(!BTSLUtil.isNullString(batchItemsVO.getExtTxnNo()) && ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_UNIQUE))).booleanValue())
					{
						index=0;
						_pstmtSelectExtTxnID1.setString(++index,batchItemsVO.getExtTxnNo());
						_pstmtSelectExtTxnID1.setString(++index,PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
						rsSelectExtTxnID1=_pstmtSelectExtTxnID1.executeQuery();
						_pstmtSelectExtTxnID1.clearParameters();
						if(rsSelectExtTxnID1.next())
						{
							DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"External transaction number should be unique");
							p_batchItemsList.remove(i);
							i=i-1;
							continue;
						}
						index=0;
						_pstmtSelectExtTxnID2.setString(++index,PretupsI.CHANNEL_TYPE_O2C);
						_pstmtSelectExtTxnID2.setString(++index,batchItemsVO.getExtTxnNo());
						_pstmtSelectExtTxnID2.setString(++index,PretupsI.CHANNEL_TRANSFER_ORDER_CANCEL);
						rsSelectExtTxnID2=_pstmtSelectExtTxnID2.executeQuery();
						_pstmtSelectExtTxnID2.clearParameters();
						if(rsSelectExtTxnID2.next())
						{
							DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"External transaction number should be unique");
							p_batchItemsList.remove(i);
							i=i-1;
							continue;
						}	
					}// external txn number uniqueness check ends here

					// load the product's informaiton.
					if(transferRuleNotExistMap.get(batchItemsVO.getCategoryCode())==null)
					{
						if(transferRuleProdNotExistMap.get(batchItemsVO.getCategoryCode())==null)
						{
							if(transferRuleMap.get(batchItemsVO.getCategoryCode())==null)
							{
								index=0;
								_pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getNetworkCode());
								_pstmtSelectTrfRule.setString(++index,p_batchMasterVO.getDomainCode());
								_pstmtSelectTrfRule.setString(++index,batchItemsVO.getCategoryCode());
								rsSelectTrfRule = _pstmtSelectTrfRule.executeQuery();
								_pstmtSelectTrfRule.clearParameters();
								if (rsSelectTrfRule.next())
								{
									rulesVO = new ChannelTransferRuleVO();
									rulesVO.setTransferRuleID(rsSelectTrfRule.getString("transfer_rule_id"));
									rulesVO.setFocTransferType(rsSelectTrfRule.getString("foc_transfer_type"));
									rulesVO.setDpAllowed(rsSelectTrfRule.getString("direct_payout_allowed"));
									index=0;
									_pstmtSelectTrfRuleProd.setString(++index,rulesVO.getTransferRuleID());
									_pstmtSelectTrfRuleProd.setString(++index,p_batchMasterVO.getProductCode());
									rsSelectTrfRuleProd  = _pstmtSelectTrfRuleProd.executeQuery();
									_pstmtSelectTrfRuleProd.clearParameters();
									if(!rsSelectTrfRuleProd.next())
									{
										transferRuleProdNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
										//put error log Prodcuct is not in the transfer rule
										DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Product is not in the transfer rule");
										p_batchItemsList.remove(i);
										i=i-1;
										continue;
									}
									transferRuleMap.put(batchItemsVO.getCategoryCode(),rulesVO );
								}
								else
								{
									transferRuleNotExistMap.put(batchItemsVO.getCategoryCode(),batchItemsVO.getCategoryCode());
									// put error log transfer rule not defined
									DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Transfer rule not defined");
									p_batchItemsList.remove(i);
									focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
									focBatchItemVO.setError("Transfer rule not defined");
									addData(focBatchItemVO,dataList);
									i=i-1;
									continue;
								}
							}// transfer rule loading
						}// Product is not associated with transfer rule not defined check
						else
						{
							//put error log Product is not in the transfer rule
							DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Product is not in the transfer rule");
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Product is not in the transfer rule");
							addData(focBatchItemVO,dataList);
							p_batchItemsList.remove(i);
							i=i-1;
							continue;
						}
					}// transfer rule not defined check
					else
					{
						// put error log transfer rule not defined
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Transfer rule for this user category is not defined");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Transfer rule for this user category is not defined");
						addData(focBatchItemVO,dataList);
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}
					rulesVO=(ChannelTransferRuleVO)transferRuleMap.get(batchItemsVO.getCategoryCode());

					if(PretupsI.NO.equals(rulesVO.getDpAllowed()))
					{
						//put error according to the transfer rule FOC transfer is not allowed.
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Transfer rule for this user is not defined");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Transfer rule for this user is not defined");
						addData(focBatchItemVO,dataList);
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}

					//end

					// check the transfer profile product code

					// transfer profile check ends here
					if(transferProfileMap.get(batchItemsVO.getTxnProfile())==null)
					{
						index=0;
						_pstmtSelectTProfileProd.setString(++index,batchItemsVO.getTxnProfile());
						_pstmtSelectTProfileProd.setString(++index,p_batchMasterVO.getProductCode());
						_pstmtSelectTProfileProd.setString(++index,PretupsI.PARENT_PROFILE_ID_CATEGORY);
						rsSelectTProfileProd=_pstmtSelectTProfileProd.executeQuery();
						_pstmtSelectTProfileProd.clearParameters();
						if(!rsSelectTProfileProd.next())
						{
							transferProfileMap.put(batchItemsVO.getTxnProfile(),"false");
							DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Transfer profile for this product is not defined");
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Transfer profile for this product is not defined");
							addData(focBatchItemVO,dataList);
							p_batchItemsList.remove(i);
							i=i-1;
							continue;
						}
						transferProfileMap.put(batchItemsVO.getTxnProfile(),"true");
					}
					else
					{
						if("false".equals(transferProfileMap.get(batchItemsVO.getTxnProfile())))
						{
							DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Transfer profile for this product is not defined");
							focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
							focBatchItemVO.setError("Transfer profile for this product is not defined");
							addData(focBatchItemVO,dataList);
							p_batchItemsList.remove(i);
							i=i-1;
							continue;
						}
					}

					// check the commission profile applicability and other checks related to the commission profile
					index=0;
					_pstmtSelectCProfileProd.setString(++index,p_batchMasterVO.getProductCode());
					_pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileSetId());
					_pstmtSelectCProfileProd.setString(++index,batchItemsVO.getCommissionProfileVer());
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.TRANSACTION_TYPE_ALWD))).booleanValue())
					{
						_pstmtSelectCProfileProd.setString(++index,PretupsI.TRANSFER_TYPE_O2C);
						_pstmtSelectCProfileProd.setString(++index,PretupsI.ALL);
					}
					else
						_pstmtSelectCProfileProd.setString(++index,PretupsI.ALL);
					_pstmtSelectCProfileProd.setString(++index,PretupsI.ALL);
					rsSelectCProfileProd=_pstmtSelectCProfileProd.executeQuery();
					_pstmtSelectCProfileProd.clearParameters();
					if(!rsSelectCProfileProd.next())
					{
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Commission profile for this product is not defined");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Commission profile for this product is not defined");
						addData(focBatchItemVO,dataList);
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}
					requestedValue=batchItemsVO.getRequestedQuantity();
					minTrfValue=rsSelectCProfileProd.getLong("min_transfer_value");
					maxTrfValue=rsSelectCProfileProd.getLong("max_transfer_value");
					if(maxTrfValue < requestedValue )
					{
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Requested quantity is not between min and max values minTrfValue="+minTrfValue+", maxTrfValue="+maxTrfValue);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Requested quantity is not between min and max values");
						addData(focBatchItemVO,dataList);
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}
					multipleOf=rsSelectCProfileProd.getLong("transfer_multiple_off");
					if(requestedValue%multipleOf != 0)
					{
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Requested quantity is not in multiple value multiple of="+multipleOf);
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Requested quantity is not in multiple value multiple of="+multipleOf);
						addData(focBatchItemVO,dataList);
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}

					index=0;
					_pstmtSelectCProfileProdDetail.setString(++index,rsSelectCProfileProd.getString("comm_profile_products_id"));
					if(!PretupsI.YES.equals(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED"))){
						_pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
					}
					_pstmtSelectCProfileProdDetail.setLong(++index,requestedValue);
					rsSelectCProfileProdDetail=_pstmtSelectCProfileProdDetail.executeQuery();
					_pstmtSelectCProfileProdDetail.clearParameters();
					if(!rsSelectCProfileProdDetail.next())
					{
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"Commission profile slab is not define for the requested value");
						focBatchItemVO.setStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
						focBatchItemVO.setError("Commission profile slab is not define for the requested value");
						addData(focBatchItemVO,dataList);
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}	
					// to calculate tax
					transferItemsList = new ArrayList();
					channelTransferItemsVO = new ChannelTransferItemsVO ();
					// this value will be inserted into the table as the requested qty
					channelTransferItemsVO.setRequiredQuantity(requestedValue);
					// this value will be used in the tax calculation.
					channelTransferItemsVO.setRequestedQuantity(PretupsBL.getDisplayAmount(requestedValue));
					channelTransferItemsVO.setCommProfileDetailID(rsSelectCProfileProdDetail.getString("comm_profile_detail_id"));
					channelTransferItemsVO.setUnitValue(p_batchMasterVO.getProductMrp());

					channelTransferItemsVO.setCommRate(rsSelectCProfileProdDetail.getLong("commission_rate"));
					channelTransferItemsVO.setCommType(rsSelectCProfileProdDetail.getString("commission_type"));

					channelTransferItemsVO.setDiscountRate(rsSelectCProfileProd.getLong("discount_rate"));
					channelTransferItemsVO.setDiscountType(rsSelectCProfileProd.getString("discount_type"));

					channelTransferItemsVO.setTax1Rate(rsSelectCProfileProdDetail.getLong("tax1_rate"));
					channelTransferItemsVO.setTax1Type(rsSelectCProfileProdDetail.getString("tax1_type"));

					channelTransferItemsVO.setTax2Rate(rsSelectCProfileProdDetail.getLong("tax2_rate"));
					channelTransferItemsVO.setTax2Type(rsSelectCProfileProdDetail.getString("tax2_type"));

					channelTransferItemsVO.setTax3Rate(rsSelectCProfileProdDetail.getLong("tax3_rate"));
					channelTransferItemsVO.setTax3Type(rsSelectCProfileProdDetail.getString("tax3_type"));

					if(PretupsI.YES.equals(rsSelectCProfileProd.getString("taxes_on_foc_applicable")))
					{					
						channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.YES);
					} else {
						channelTransferItemsVO.setTaxOnFOCTransfer(PretupsI.NO);
					}


					transferItemsList.add(channelTransferItemsVO);
					ChannelTransferBL.calculateMRPWithTaxAndDiscount(transferItemsList,PretupsI.TRANSFER_TYPE_DP);

					// taxes on DP required
					// ends commission profile validaiton
					batchItemsVO.setRequestedQuantity(channelTransferItemsVO.getRequiredQuantity());
					batchItemsVO.setCommissionProfileDetailId(channelTransferItemsVO.getCommProfileDetailID());
					batchItemsVO.setCommissionType(channelTransferItemsVO.getCommType());
					batchItemsVO.setCommissionRate(channelTransferItemsVO.getCommRate());
					batchItemsVO.setCommissionValue(channelTransferItemsVO.getCommValue());
					batchItemsVO.setTax1Type(channelTransferItemsVO.getTax1Type());
					batchItemsVO.setTax1Rate(channelTransferItemsVO.getTax1Rate());
					batchItemsVO.setTax1Value(channelTransferItemsVO.getTax1Value());

					batchItemsVO.setTax2Type(channelTransferItemsVO.getTax2Type());
					batchItemsVO.setTax2Rate(channelTransferItemsVO.getTax2Rate());
					batchItemsVO.setTax2Value(channelTransferItemsVO.getTax2Value());

					batchItemsVO.setTax3Type(channelTransferItemsVO.getTax3Type());
					batchItemsVO.setTax3Rate(channelTransferItemsVO.getTax3Rate());
					batchItemsVO.setTax3Value(channelTransferItemsVO.getTax3Value());

					batchItemsVO.setTransferMrp(channelTransferItemsVO.getProductTotalMRP());

					// insert items data here
					int queryExecutionCount = -1;
					index=0;
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchId());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getBatchDetailId());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getCategoryCode());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getMsisdn());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserId());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getStatus());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getModifiedBy());
					_pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getModifiedOn()));
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getUserGradeCode());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getExtTxnNo()); 
					_pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchItemsVO.getExtTxnDate()));
					_pstmtInsertBatchItems.setDate(++index,BTSLUtil.getSQLDateFromUtilDate(batchItemsVO.getTransferDate()));
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getTxnProfile());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileSetId());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getCommissionProfileVer());
					_pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommProfileDetailID());
					_pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getCommType());
					_pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getCommRate());
					_pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getCommValue());
					_pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax1Type());
					_pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax1Rate());
					_pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax1Value());
					_pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax2Type());
					_pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax2Rate());
					_pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax2Value());
					_pstmtInsertBatchItems.setString(++index,channelTransferItemsVO.getTax3Type());
					_pstmtInsertBatchItems.setDouble(++index,channelTransferItemsVO.getTax3Rate());
					_pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getTax3Value());
					//_pstmtInsertBatchItems.setString(++index,String.valueOf(channelTransferItemsVO.getRequiredQuantity()));
					_pstmtInsertBatchItems.setLong(++index, Long.parseLong(String.valueOf(channelTransferItemsVO.getRequiredQuantity())));
					_pstmtInsertBatchItems.setLong(++index,channelTransferItemsVO.getProductTotalMRP());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getInitiatorRemarks());
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getExternalCode());
					_pstmtInsertBatchItems.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED);
					//added for adding bonus type in direct payout by Lohit
					_pstmtInsertBatchItems.setString(++index,batchItemsVO.getBonusType());
					queryExecutionCount=_pstmtInsertBatchItems.executeUpdate();
					if(queryExecutionCount<=0)
					{
						p_con.rollback();
						//put error record can not be inserted
						_logger.error(METHOD_NAME, "Record cannot be inserted in batch items table");
						DirectPayOutErrorLog.log(batchItemsVO.getMsisdn(),fileName,"DB error record cannot be inserted in table foc batch items");
						p_batchItemsList.remove(i);
						i=i-1;
						continue;
					}
					p_con.commit();
				}// for loop for the batch items
			} 
			catch (SQLException sqe)
			{
				_logger.errorTrace(METHOD_NAME, sqe);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[processBatchDPTransfer]","","","","SQL Exception:"+sqe.getMessage());
				DirectPayOutErrorLog.log("NA",fileName,"SQL Exception:"+sqe.getMessage()+"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
				throw new BTSLBaseException(this, "processBatchDPTransfer", PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			}
			catch (Exception ex)
			{
				_logger.errorTrace(METHOD_NAME, ex);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[processBatchDPTransfer]","","","","Exception:"+ex.getMessage());
				DirectPayOutErrorLog.log("NA",fileName,"Exception:"+ex.getMessage()+"TOTAL SUCCESS RECORDS = "+totalSuccessRecords);
				throw new BTSLBaseException(this, "processBatchDPTransfer", PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
			}
			finally
			{
				try{if (rsSelectExtTxnID1 != null){rsSelectExtTxnID1.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (rsSelectExtTxnID2 != null){rsSelectExtTxnID2.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (rsSelectTrfRule != null){rsSelectTrfRule.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (rsSelectTrfRuleProd != null){rsSelectTrfRuleProd.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (rsSelectCProfileProd != null){rsSelectCProfileProd.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (rsSelectCProfileProdDetail != null){rsSelectCProfileProdDetail.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				try{if (rsSelectTProfileProd != null){rsSelectTProfileProd.close();}} catch (Exception e){_logger.errorTrace(METHOD_NAME, e);}
				if (_logger.isDebugEnabled()) {
					_logger.debug("processBatchDPTransfer", "Exiting:" );
				}
			}
		}        
	}


	public static void main(String[] args) 
	{
		final String METHOD_NAME = "main";
		try
		{
			if(args.length!=2)
			{
				_logger.info(METHOD_NAME, "Usage : DirectPayOut [Constants file] [LogConfig file]");
				return;
			}
			File constantsFile = new File(args[0]);
			if(!constantsFile.exists())
			{
				_logger.info(METHOD_NAME, " Constants File Not Found .............");
				return;
			}
			File logconfigFile = new File(args[1]);
			if(!logconfigFile.exists())
			{
				_logger.info(METHOD_NAME, " Logconfig File Not Found .............");
				return;
			}
			ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());
			LookupsCache.loadLookAtStartup();
		}//end try
		catch(Exception ex)
		{
			_logger.errorTrace(METHOD_NAME, ex);
			ConfigServlet.destroyProcessCache();
			return;
		}
		try
		{
			try
			{
				//load util class
				String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
				_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
			}
			catch(Exception e)
			{
				_logger.errorTrace(METHOD_NAME, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[main]","","","","Exception while loading operator util class:"+e.getMessage());
				throw new BTSLBaseException("DirectPayOut",METHOD_NAME,PretupsErrorCodesI.UNABLE_TO_LOAD_UTIL_CLASS);
			}
			String []filenamearr={};
			DirectPayOut directPayOut= new DirectPayOut(filenamearr);
			long startTime = System.currentTimeMillis();
			directPayOut.process();
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
		}
		catch(BTSLBaseException be)
		{
			_logger.errorTrace(METHOD_NAME, be);
		}
		catch(Exception e)
		{
			_logger.errorTrace(METHOD_NAME, e);
		}
		finally
		{
			if (_logger.isDebugEnabled()) {
				_logger.info(METHOD_NAME," Exiting");
			}
			ConfigServlet.destroyProcessCache();
		}
	}

	/**
	 * this is the starting point of 
, 
	 * this method is used to pick all the files from the specified directory
	 * validate file name
	 * load category information according to main code
	 * load geographical domain information
	 * perform validations on each column of file
	 * validate channel user according to MSISDN
	 * Initiate direct pay out transfer
	 * Close direct pay out transfer
	 * move file to another location
	 * @throws BTSLBaseException
	 */
	private void process() throws BTSLBaseException
	{
		final String METHOD_NAME = "process";

		String finalDirectoryPath=null;

		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered");
		}

		try
		{     
			batchDetailID = new AtomicLong(0);
			filePath=Constants.getProperty("DIRECT_PAY_OUT_DIR");
			finalDirectoryPath=Constants.getProperty("DP_BATCH_APPROVAL_FILE_PATH");
			if(BTSLUtil.isNullString(filePath) || BTSLUtil.isNullString(finalDirectoryPath))
			{
				_logger.debug(METHOD_NAME,"Direct pay out file path or final directory file path is not defined in Constants.props");
				throw new BTSLBaseException("DirectPayOut",METHOD_NAME,PretupsErrorCodesI.DPO_CONFIGUARATION_ERROR);
			}

			if(BTSLUtil.isNullString(Constants.getProperty("NEGATIVE_AMOUNT_ALLOWED")))
			{
				_logger.debug(METHOD_NAME,"NEGATIVE_AMOUNT_ALLOWED is not defined in Constants.props");
				throw new BTSLBaseException("DirectPayOut",METHOD_NAME,PretupsErrorCodesI.DPO_CONFIGUARATION_ERROR);

			}



			File dir= new File(filePath);
			String[] fileArr={};
			if(dir.exists()) {
				fileArr=dir.list();
			} else
			{
				_logger.debug(METHOD_NAME,"Directory does not exist "+filePath);
				throw new BTSLBaseException("DirectPayOut",METHOD_NAME,PretupsErrorCodesI.DIR_NOT_EXIST);
			}
			ExecutorService executor = Executors.newFixedThreadPool(fileArr.length);
			String currentfileName;
			String[] networkWiseArry=new String[fileArr.length];
             int filesLength=fileArr.length;
			for(int k=0;k<filesLength;k++)
			{
				currentfileName=fileArr[k];
				if(!BTSLUtil.isNullString(currentfileName))
				{
					networkWiseArry[0]=currentfileName;
					String fileArr1[]=currentfileName.split("_");
					int j=k+1;
					int counter=1;
					for(;j<fileArr.length;j++)
					{
						currentfileName=fileArr[j];
						if(!BTSLUtil.isNullString(currentfileName))
						{
							String fileArr2[]=currentfileName.split("_");
							if( fileArr1[1].equals(fileArr2[1]))
							{
								networkWiseArry[counter]=currentfileName;
								counter++;
								fileArr[j]=null;

							}
						}


					} 
					if(networkWiseArry.length>0)
						executor.submit(new DirectPayOut(networkWiseArry));

					networkWiseArry=new String[fileArr.length];
				}

			}
			executor.shutdown();
			while (!executor.isTerminated()) {
				_logger.info(METHOD_NAME, "Terminating executor");
			}
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"DirectPayOut[process]","","",""," Direct pay out process executed successfully.");	
		}
		catch(BTSLBaseException be)
		{
			_logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
			_logger.errorTrace(METHOD_NAME,be);
			throw be;
		}
		catch(Exception e)
		{
			_logger.error(METHOD_NAME, "Exception : " + e.getMessage());

			_logger.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"DirectPayOut[process]","","",""," Direct pay out process not executed successfully.");
			throw new BTSLBaseException("DirectPayOut",METHOD_NAME,PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally
		{
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting..... ");
			}
		}
	}

	public void processFileSheetWise(String p_excelID,boolean p_readLastSheet,String p_fileName) throws BTSLBaseException
	{
		if(_logger.isDebugEnabled()) {
			_logger.debug("processFileSheetWise"," p_excelID: "+p_excelID+" p_fileName: "+p_fileName);
		}
		final String METHOD_NAME = "processFileSheetWise";
		Workbook workbook = null;
		Sheet excelsheet = null;
		int noOfSheet=0;
		int noOfRows =0;
		int noOfcols =0;	
		int p_leftHeaderLinesForEachSheet = 1;
		try
		{
			double t_mem=Runtime.getRuntime().totalMemory()/1048576;
			double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
			_logger.debug("processFileSheetWise", "Total memory :"+t_mem+"   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));

			workbook = Workbook.getWorkbook(new File(p_fileName)); 
			noOfSheet=workbook.getNumberOfSheets();

			if(!p_readLastSheet) {
				noOfSheet=noOfSheet-1;
			}
			for(int i=0;i<noOfSheet;i++)
			{

				excelsheet = workbook.getSheet(i);
				noOfRows = noOfRows+(excelsheet.getRows()-p_leftHeaderLinesForEachSheet);
				noOfcols = excelsheet.getColumns();
			}

			ExecutorService executor = Executors.newFixedThreadPool(noOfSheet); 			
			for(int i=0;i<noOfSheet;i++)
			{
				executor.submit(new ProcessSheet(readExcelSheet(ExcelFileIDI.BATCH_DIRECT_PAY_OUT,workbook.getSheet(i)),workbook.getSheet(i).getName())); 		
			}	
			executor.shutdown();
			while (!executor.isTerminated()) {
				_logger.info(METHOD_NAME, "Terminating executor");
			}
			_logger.debug("processFileSheetWise","Finished all threads");

		}
		catch(BTSLBaseException e)
		{
			_logger.errorTrace(METHOD_NAME,e);
			_logger.error("processFileSheetWise"," Exception e: "+e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		catch (Exception ex)
		{
			_logger.errorTrace(METHOD_NAME,ex);
			_logger.error("processFileSheetWise"," Exception e: "+ex.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally
		{
			try{if(workbook!=null) {
				workbook.close();
			}}catch(Exception e){_logger.errorTrace(METHOD_NAME,e);}

			workbook=null;
			double t_mem=Runtime.getRuntime().totalMemory()/1048576;
			double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
			_logger.debug("processFileSheetWise", "Total memory :"+t_mem+"   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));
		}
	}

	/**
	 * readExcel
	 * @param p_excelID
	 * @param p_fileName
	 * @return
	 * @throws Exception
	 */
	public String[][] readExcelSheet(String p_excelID, Sheet excelSheet) throws BTSLBaseException
	{
		if(_logger.isDebugEnabled()) {
			_logger.debug("readExcelSheet"," p_excelID: ");
		}
		final String METHOD_NAME = "readExcelSheet";
		String strArr[][] = null;

		try
		{
			double t_mem=Runtime.getRuntime().totalMemory()/1048576;
			double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
			_logger.debug("readExcelSheet", "Total memory :"+t_mem+"   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));

			int noOfRows = excelSheet.getRows();
			int noOfcols = excelSheet.getColumns();
			strArr = new String[noOfRows][noOfcols];
			Cell cell = null;
			String content = null;
			String key=null;
			int[] indexMapArray=new int[noOfcols]; 
			String indexStr=null;
			for(int col = 0; col < noOfcols; col++)
			{
				indexStr=null;
				key=ExcelFileConstants.getReadProperty(p_excelID,String.valueOf(col));
				if(key==null) {
					key=String.valueOf(col);
				}
				indexStr=ExcelFileConstants.getReadProperty(p_excelID,String.valueOf(col));
				if(indexStr==null) {
					indexStr=String.valueOf(col);
				}
				indexMapArray[col]=Integer.parseInt(indexStr);
				strArr[0][indexMapArray[col]] = key;

			}
			for(int row = 1; row < noOfRows; row++)
			{
				for(int col = 0; col < noOfcols; col++)
				{
					cell = excelSheet.getCell(col,row);
					content = cell.getContents();
					strArr[row][indexMapArray[col]] = content;
				}
			}
			return strArr;
		}
		catch(Exception e)
		{
			_logger.errorTrace(METHOD_NAME,e);
			_logger.error("readExcelSheet"," Exception e: "+e.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, "Exception in reading excelsheet");
		}
		finally
		{
			double t_mem=Runtime.getRuntime().totalMemory()/1048576;
			//Runtime.getRuntime().gc();
			double f_mem=Runtime.getRuntime().freeMemory()/1048576; 
			_logger.debug("readExcelSheet", "Total memory :"+t_mem+"   free memmory :"+f_mem+" Used memory:"+(t_mem-f_mem));
			if(_logger.isDebugEnabled()) {
				_logger.debug("readExcelSheet"," Exiting strArr: "+strArr);
			}
		}
	}

	/**
	 * Validate the name of file, file name should be: 
	 * name_NetworkCode_GeographyCode_DomainCode_ProductCode.xls
	 * @throws BTSLBaseException
	 */
	public void validteFileName() throws BTSLBaseException
	{
		final String METHOD_NAME = "validteFileName";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered p_fileName: "+fileName);
		}

		try
		{
			String fileArr[]=fileName.split("_");
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Entered length of filename array: "+fileArr.length);
			}
			if(fileArr!=null && fileArr.length==8)
			{
				_networkCode=fileArr[1];
				_zoneCode=fileArr[2];
				_domainCode=fileArr[3];
				_categoryCode=fileArr[4];
				_productCode=fileArr[5];
				String fileArr1[]=fileArr[7].split("\\.");
				_batchName=fileArr[6];
				_OriginalFileName=fileArr[0];
				_fileExt="."+fileArr1[1];
				if(!BTSLUtil.isNullString(fileArr[7])) {
					_activeUserName=fileArr1[0];
				} else {
					_activeUserName=PretupsI.SYSTEM;
				}    
				if(_logger.isDebugEnabled()) {
					_logger.debug(METHOD_NAME,"Entered _activeUserName: "+_activeUserName);
				}               
				fileArr=null;
				fileArr1=null;
			}
			else
			{
				DirectPayOutErrorLog.log("NA",fileName,"Invalid file name: file name should be: name_networkCode_geographyCode_domainCode_categoryCode_productCode_batchName");
				throw new BTSLBaseException(METHOD_NAME,PretupsErrorCodesI.INVALID_FILE_NAME);
			}

		}
		catch(Exception e)
		{
			_logger.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException("DirectPayOut","process",PretupsErrorCodesI.INVALID_FILE_NAME);
		}
		finally
		{
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Exiting network code: "+_networkCode);
			}
		}
	}

	/**
	 * Method generateFOCBatchMasterTransferID.
	 * This method is called generate FOC batch master transferID
	 * @param p_currentDate Date
	 * @param p_networkCode String
	 * @throws BTSLBaseException
	 * @return 
	 */

	static   synchronized void generateFOCBatchMasterTransferID(FOCBatchMasterVO p_batchMasterVO) throws BTSLBaseException
	{
		final String METHOD_NAME = "generateFOCBatchMasterTransferID";
		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "Entered p_batchMasterVO="+p_batchMasterVO);
		}
		try
		{
			long txnId = IDGenerator.getNextID(PretupsI.DP_BATCH_TRANSACTION_ID,  BTSLUtil.getFinancialYear(),PretupsI.ALL,p_batchMasterVO.getCreatedOn());
			p_batchMasterVO.setBatchId(_operatorUtil.formatDPBatchMasterTxnID(p_batchMasterVO,txnId));
		} 
		catch (Exception e)
		{
			_logger.errorTrace(METHOD_NAME, e);
			throw new BTSLBaseException("DirectPayOut", METHOD_NAME, PretupsErrorCodesI.ERROR_EXCEPTION);
		}
		finally
		{
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exited  "+p_batchMasterVO.getBatchId());
			}
		}
		return;
	}

	/**
	 * Refresh object defined at class level
	 */
	private void refreshObjects()
	{
		final String METHOD_NAME = "refreshObjects";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered");
		}
		fileName=null;
		_networkCode=null;
		_domainCode=null;
		_productCode=null;
		_OriginalFileName=null;
		_fileExt=null;
		_batchName=null;
		_categoryCode=null;
	}

	/**
	 * load all the zone that have status not equal to 'N'
	 * @param con Connection
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	private ArrayList loadGeographyList(Connection con) throws BTSLBaseException
	{
		final String METHOD_NAME = "loadGeographyList";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered");
		}
		ArrayList geoList=null;
		PreparedStatement pstm=null;
		
		int index=0;
		GeographicalDomainVO geographicalDomainVO=null;
		try
		{
			String zoneCode1=_zoneCode.replaceAll("'", "");
			String zc=zoneCode1.replaceAll("\" ", "");
			String m_zoneCode[]=zc.split(",");


			DirectPayOutQry directPayOutQry = (DirectPayOutQry) ObjectProducer.getObject(QueryConstants.DIRECT_PAYOUT_QRY, QueryConstants.QUERY_PRODUCER);
			pstm = directPayOutQry.loadGeographyListQry(con, m_zoneCode, zoneCode1, _networkCode);

			try(ResultSet rst=pstm.executeQuery();)
			{
			geoList= new ArrayList();
			while(rst.next())
			{
				geographicalDomainVO= new GeographicalDomainVO();
				geographicalDomainVO.setGrphDomainCode(rst.getString("GRPH_DOMAIN_CODE"));
				geographicalDomainVO.setGrphDomainType(rst.getString("GRPH_DOMAIN_TYPE"));
				geoList.add(geographicalDomainVO);
			}
		}
		}
		catch (SQLException sqe)
		{
			_logger.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[loadGeographyList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		} 
		catch (Exception ex)
		{
			_logger.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[loadGeographyList]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally
		{
			
			try{
		        if (pstm!= null){
		        	pstm.close();
		        }
		      }
		      catch (SQLException e){
		    	  _logger.error("An error occurred closing statement.", e);
		      }
			
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting: geoList"+geoList);
			}
		}
		return geoList;
	}



	/** 
	 * This method will move the processed file in separate folder
	 * @param p_fileName1 String
	 * @param pathWithFileName1 String
	 * @param path2 String
	 * @throws BTSLBaseException
	 */
	public void moveFileToAnotherDirectory(String p_fileName1,String pathWithFileName1,String path2) throws BTSLBaseException
	{
		final String METHOD_NAME = "moveFileToAnotherDirectory";
		if(_logger.isDebugEnabled()) {
			_logger.debug(" moveFileToAnotherDirectory ","Entered with :: p_fileName1="+p_fileName1+"  pathWithFileName1="+pathWithFileName1+"path2="+path2);
		}
		boolean flag =false;
		String newFileName=null;
		try
		{
			File fileRead = new File(pathWithFileName1);
			File fileArchive = new File(path2);
			if(!fileArchive.isDirectory()) {
				fileArchive.mkdirs();
			}
			newFileName=_OriginalFileName.concat("_").concat(BTSLUtil.getFileNameStringFromDate(new Date())).concat(_fileExt);
			fileArchive = new File(path2+File.separator+newFileName);
			flag = fileRead.renameTo(fileArchive);

			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"DirectPayOut[moveFileToAnotherDirectory]","","","","Successfully moved the File "+p_fileName1+" to backup location ("+path2+")");
		}
		catch(Exception be)
		{
			_logger.error("DirectPayOut[moveFileToAnotherDirectory]","BTSLBaseException ="+be.getMessage());
			_logger.errorTrace(METHOD_NAME,be);
			throw new BTSLBaseException("moveFileToAnotherDirectory",PretupsErrorCodesI.ERROR_MOVING_FILE_TO_FINAL_DIR);
		}
		finally
		{
			if(_logger.isDebugEnabled()) {
				_logger.debug(" moveFileToAnotherDirectory "," Exiting with flag="+flag);
			}
		}
	}

	/**
	 * @param con Connection
	 * @return String
	 * @throws BTSLBaseException
	 */
	private ProductVO loadProductMrp(Connection con) throws BTSLBaseException
	{
		final String METHOD_NAME = "loadProductMrp";
		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"Entered");
		}
		 
		ProductVO productVO=null;
		try
		{
			StringBuilder str= new StringBuilder(" SELECT p.unit_value, p.short_name,p.product_type FROM PRODUCTS p, NETWORK_PRODUCT_MAPPING n ");
			str.append("  WHERE p.product_code=? AND p.status=?  AND p.product_code= n.product_code AND n.network_code=? AND n.status=?");
			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME,"Query: "+str.toString());
			}
			try(PreparedStatement pstm= con.prepareStatement(str.toString());)
			{
			pstm.setString(1, _productCode);
			pstm.setString(2, PretupsI.YES);
			pstm.setString(3,_networkCode);
			pstm.setString(4, PretupsI.YES);
			try(ResultSet rst= pstm.executeQuery();)
			{
			productVO= new ProductVO();
			if(rst.next())
			{
				productVO.setUnitValue(rst.getLong("unit_value"));
				productVO.setShortName(rst.getString("short_name"));
				productVO.setProductType(rst.getString("product_type"));
			}
			else
			{
				DirectPayOutErrorLog.log("NA",fileName,"Product "+_productCode+" is not active in the network"+_networkCode);
				throw new BTSLBaseException(METHOD_NAME,PretupsErrorCodesI.INVALID_PRODUCT_CODE);
			}
		}
			}
		}
		catch (SQLException sqe)
		{
			_logger.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[loadProductMrp]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		} 
		catch (Exception ex)
		{
			_logger.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[loadProductMrp]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally
		{
			
			if (_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting: productVO"+productVO);
			}
		}
		return productVO;
	}

	private void addData(FOCBatchItemsVO fOCBatchItemsVO,ArrayList dataList) {
		dataList.add(fOCBatchItemsVO);

	}

	private void writeDataInFile(ArrayList dataList) throws BTSLBaseException
	{
		final String methodName = "writeDataInFile";
		if (_logger.isDebugEnabled()){
			_logger.debug(methodName," Entered: dataList := "+dataList);
		}       
		Writer out =null;
		String fileName=null;
		String filePath=null;
		File newFile = null;
		File newFile1 = null;
		String fileHeader=null;
		boolean isErrorExists=false;
		try
		{
			Date date= new Date();
			filePath=Constants.getProperty("DP_TXN_REPORT_SCHEDULE")+_networkCode+"/C2S/";
			newFile1=new File(filePath);
			if(! newFile1.isDirectory())
				newFile1.mkdirs();
			SimpleDateFormat sdf1= new SimpleDateFormat("ddMMyy");
			SimpleDateFormat sdf2= new SimpleDateFormat("HHmmss");
			fileName=filePath+_networkCode+"_DPTransactionReport_"+sdf1.format(date)+"_"+sdf2.format(date)+".csv";
			_logger.debug(methodName,"fileName := "+fileName);
			fileHeader=Constants.getProperty("FILE_HEADER_NAME");
			newFile = new File(fileName);
			out = new OutputStreamWriter(new FileOutputStream(newFile));
			out.write(fileHeader +"\n");
			for (Iterator<FOCBatchItemsVO> iterator = dataList.iterator(); iterator.hasNext();) {

				FOCBatchItemsVO fOCBatchItemsVO = iterator.next();
				if(!BTSLUtil.isNullString(fOCBatchItemsVO.getError())){	
					isErrorExists = true;

					out.write(fOCBatchItemsVO.getMsisdn()+",");
					out.write(fOCBatchItemsVO.getExtTxnNo()+",");

					if(fOCBatchItemsVO.getExtTxnDate()!=null)
						out.write( BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getExtTxnDate()) +",");
					else
						out.write( " " + ",");
					out.write(fOCBatchItemsVO.getExternalCode()+",");
					out.write(fOCBatchItemsVO.getRequestedQuantity()+",");
					out.write(fOCBatchItemsVO.getBonusType()+",");

					out.write(BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getInitiatedOn())+",");
					out.write(fOCBatchItemsVO.getInitiatedBy()+",");
					out.write(BTSLUtil.getDateStringFromDate(fOCBatchItemsVO.getInitiatedOn())+",");
					out.write(fOCBatchItemsVO.getInitiatedBy()+",");
					out.write(BTSLUtil.getDateStringFromDate(date)+",");
					out.write(fOCBatchItemsVO.getBatchId()+",");
					if(!BTSLUtil.isNullString(fOCBatchItemsVO.getReferenceNo()))
						out.write( fOCBatchItemsVO.getReferenceNo() +",");
					else
						out.write( " " +",");

					out.write( fOCBatchItemsVO.getStatus()+",");
					if(!BTSLUtil.isNullString(fOCBatchItemsVO.getError()))
						out.write( fOCBatchItemsVO.getError() +",");
					else
						out.write( " " +",");

					if(!BTSLUtil.isNullString(fOCBatchItemsVO.getPreBalance()))
						out.write( fOCBatchItemsVO.getPreBalance() +",");
					else
						out.write( " " + ",");

					if(!BTSLUtil.isNullString(fOCBatchItemsVO.getPostBalance()))
						out.write( fOCBatchItemsVO.getPostBalance() +",");
					else
						out.write( " " +",");

					if(!BTSLUtil.isNullString(fOCBatchItemsVO.getInitiatorRemarks()))
						out.write( fOCBatchItemsVO.getInitiatorRemarks() +",");
					else
						out.write(" "+",");


					out.write("\n");
				}

			}
			if(!isErrorExists)
			{
				boolean isdeleted = newFile.delete();
				if(!isdeleted)
				{
					throw new BTSLBaseException(this, "writeDataInFile", "file deletion failed");
				}
			}
		}
		catch(Exception e)
		{
			_logger.error(methodName, "SQLException " + e.getMessage());
        	_logger.errorTrace(methodName,e);
			_logger.debug(methodName, e.toString());
			_logger.debug(methodName, "Exception := " + e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
			throw new BTSLBaseException("writeDataInFile",methodName,PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally
		{
			if (out!=null)
			{
			try
			{
				out.close();
			}
			catch(Exception e)
			{
				_logger.errorTrace(methodName,e);
			}
			if (_logger.isDebugEnabled()){
				_logger.debug(methodName,"Exiting... ");
			}
			}
		}
	}
	public void run()
	{
		final String METHOD_NAME = "run";

		Connection con = null,con1=null;
		MComConnectionI mcomCon = null,mcomCon1=null;
		DomainDAO domainDAO=null;
		ProcessStatusVO  processVO=null,processStatusVOForSystem = null;
		ProcessBL processBL = null;
		String finalDirectoryPath=null;
		PreparedStatement _pstmtCloseBatchMasterTable = null;
		PreparedStatement _pstmtInsertBatchMaster = null;
		PreparedStatement _pstmtInsertBatchGeo = null;
		HashMap<String,String> map =new HashMap<String,String>();
		int rowofflength=1;
		int cols = 0;

		if(_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME,"run");
		}
		try
		{     
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			processBL=new ProcessBL();
			domainDAO=new DomainDAO();
			batchDetailID = new AtomicLong(0);
			filePath=Constants.getProperty("DIRECT_PAY_OUT_DIR");
			finalDirectoryPath=Constants.getProperty("DP_BATCH_APPROVAL_FILE_PATH");
			maxRecords=Long.parseLong(Constants.getProperty("DIRECT_PAY_OUT_FILE_SIZE"));
			//  Check external txn id for domain type
			_externalTxnMandatoryDomainType =((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_DOMAINTYPE_DP));
			_externalTxnMandatory = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_TXN_MANDATORY_FORDP));
			_externalCodeMandatory = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EXTERNAL_CODE_MANDATORY_FORDP))).booleanValue();
			_approvalLevel=Integer.toString(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DP_ORDER_APPROVAL_LVL))).intValue());
			for(int i=0;i<FileNameArray.length;i++)
			{
				fileName=FileNameArray[i];
				validteFileName();
				//locking process id for particular network
				processVO=processBL.checkProcessUnderProcessNetworkWise(con,PretupsI.DP_BATCH_PROCESS_ID,_networkCode);
				if(processVO!=null && processVO.isStatusOkBool())
				{
					geographyList= loadGeographyList(con);
					productVO=loadProductMrp(con);
					domainVO = domainDAO.loadDomainVO(con,_domainCode);
					fileObject= new File(filePath+File.separator+fileName);
					if(fileObject.length()==0)
					{
						_logger.debug(METHOD_NAME,"Record does not exist in file "+fileObject);

					}
					ExcelRW excelRW=new ExcelRW();
					String [][] excelArr=excelRW.readMultipleExcel(ExcelFileIDI.BATCH_DIRECT_PAY_OUT,filePath+File.separator+fileName,true,rowofflength,map);
					try
					{
						cols=excelArr[0].length;
					}
					catch(Exception e)
					{
						DirectPayOutErrorLog.log("NA",fileName,"No record found in file ");
						_logger.errorTrace(METHOD_NAME,e);

					}

					int rows=excelArr.length;  //rows include the headings
					//If there is no data in XLS file
					if(rows==1)
					{
						DirectPayOutErrorLog.log("NA",fileName,"No record found in file ");

					}
					if(maxRecords<rows-1)
					{   
						DirectPayOutErrorLog.log("NA",fileName,"Total number of records in the file = "+(rows-1));
						DirectPayOutErrorLog.log("NA",fileName,"Total number of records in the file is greater than allowed number of records "+maxRecords);

					}

					curDate = new Date();
					//Construct focBatchMasterVO
					batchMasterVO = new FOCBatchMasterVO();
					batchMasterVO.setNetworkCode(_networkCode);
					batchMasterVO.setNetworkCodeFor(_networkCode);
					batchMasterVO.setStatus(PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
					batchMasterVO.setCreatedBy(_activeUserName);
					batchMasterVO.setCreatedOn(curDate);
					batchMasterVO.setModifiedBy(PretupsI.SYSTEM);
					batchMasterVO.setModifiedOn(curDate);
					batchMasterVO.setDomainCode(_domainCode);
					batchMasterVO.setProductCode(_productCode);
					batchMasterVO.setProductMrp(productVO.getUnitValue());
					batchMasterVO.setProductShortName(productVO.getShortName());
					batchMasterVO.setProductCode(_productCode);
					batchMasterVO.setBatchFileName(_OriginalFileName+_fileExt);
					batchMasterVO.setBatchDate(curDate);
					batchMasterVO.setProductType(productVO.getProductType());
					batchMasterVO.setGeographyList(geographyList);
					batchMasterVO.setBatchName(_batchName);
					this.generateFOCBatchMasterTransferID(batchMasterVO);


					StringBuilder strBuffCloseBatchMasterTable = new StringBuilder("update foc_batches set status=?,modified_on=?, modified_by=? where batch_id=? and status=?");
					StringBuilder strBuffInsertBatchMaster = new StringBuilder("INSERT INTO foc_batches (batch_id, network_code, ");
					strBuffInsertBatchMaster.append("network_code_for, batch_name, status, domain_code, product_code, ");
					strBuffInsertBatchMaster.append("batch_file_name, batch_total_record, batch_date, created_by, created_on, ");
					strBuffInsertBatchMaster.append(" modified_by, modified_on,type) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
					if (_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "strBuffInsertBatchMaster Query ="+strBuffInsertBatchMaster);
					}

					//insert data in the batch Geographies table
					StringBuilder strBuffInsertBatchGeo = new StringBuilder("INSERT INTO foc_batch_geographies(batch_id,geography_code,date_time) VALUES (?,?,?)");
					if (_logger.isDebugEnabled()) {
						_logger.debug(METHOD_NAME, "strBuffInsertBatchGeo Query ="+strBuffInsertBatchGeo);
					}

					//main thread to initiate DirectPayout transfer. entries in foc_batches, foc_batch_geographies to follow.
					// insert the master data in foc_batches with status OPEN
					_pstmtInsertBatchMaster=(PreparedStatement) con.prepareStatement(strBuffInsertBatchMaster.toString());
					_pstmtCloseBatchMasterTable=(PreparedStatement) con.prepareStatement(strBuffCloseBatchMasterTable.toString());  //added by me                			
					_pstmtInsertBatchGeo=con.prepareStatement(strBuffInsertBatchGeo.toString());
					int index=0;
					int queryExecutionCount = -1;
					try{
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getBatchId());
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getNetworkCode());
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getNetworkCodeFor());
						//commented for DB2 _pstmtInsertBatchMaster.setFormOfUse(++index,OraclePreparedStatement.FORM_NCHAR);
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getBatchName());
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getStatus());
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getDomainCode());
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getProductCode());
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getBatchFileName());
						_pstmtInsertBatchMaster.setLong(++index,batchMasterVO.getBatchTotalRecord());
						_pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getCreatedBy());
						_pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getCreatedOn()));
						_pstmtInsertBatchMaster.setString(++index,batchMasterVO.getModifiedBy());
						_pstmtInsertBatchMaster.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getModifiedOn()));
						_pstmtInsertBatchMaster.setString(++index,PretupsI.FOC_TYPE);

						queryExecutionCount = _pstmtInsertBatchMaster.executeUpdate();
						if(queryExecutionCount<=0)
						{
							mcomCon.partialRollback();
							_logger.error("process","Unable to insert in the batch master table.");
							DirectPayOutErrorLog.log("NA",fileName,"Unable to insert in batch master table");
							EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[process]","","","","Unable to insert in the batch master table.");
							throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
						}

						GeographicalDomainVO geographicalDomainVO=null;
						String geography=null;
						queryExecutionCount = -1;
						int size=batchMasterVO.getGeographyList().size();
						for(int k=0;k<size;k++)
						{

							index=0;
							geographicalDomainVO=(GeographicalDomainVO)batchMasterVO.getGeographyList().get(i);
							if("NW".equals(geographicalDomainVO.getGrphDomainType()))
							{
								geography=geographicalDomainVO.getGrphDomainCode();
								_pstmtInsertBatchGeo.setString(++index,batchMasterVO.getBatchId());
								_pstmtInsertBatchGeo.setString(++index,geography);
								_pstmtInsertBatchGeo.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(batchMasterVO.getBatchDate()));
								queryExecutionCount=_pstmtInsertBatchGeo.executeUpdate();
								if(queryExecutionCount<=0)
								{
									mcomCon.partialRollback();
									DirectPayOutErrorLog.log("NA",fileName,"Unable to insert in batch gregraphies table");
									EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[process]","","","","Unable to insert in the batch geographics table.");
									throw new BTSLBaseException(this, METHOD_NAME,PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
								}
								_pstmtInsertBatchGeo.clearParameters();
							}
						}

					}
					catch (SQLException sqe)
					{
						_logger.errorTrace(METHOD_NAME, sqe);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[process]","","","","SQL Exception:"+sqe.getMessage());
						throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
					} 

					//
			mcomCon.partialCommit();
					processFileSheetWise(ExcelFileIDI.BATCH_DIRECT_PAY_OUT,true,filePath+File.separator+fileName);

					moveFileToAnotherDirectory(fileName,filePath+File.separator+fileName,finalDirectoryPath);

					index=0;
					try {
						_pstmtCloseBatchMasterTable.setString(++index, PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_CLOSE);    
						_pstmtCloseBatchMasterTable.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(new Date()));
						_pstmtCloseBatchMasterTable.setString(++index,PretupsI.SYSTEM);
						_pstmtCloseBatchMasterTable.setString(++index,batchMasterVO.getBatchId());
						_pstmtCloseBatchMasterTable.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_DP_STATUS_OPEN);
						_pstmtCloseBatchMasterTable.executeQuery();
						//updating the network record_count
						processVO.setRecordCount(processVO.getRecordCount() - (rows - 1));
					}
					catch (SQLException sqe)
					{
						_logger.errorTrace(METHOD_NAME, sqe);
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[process]","","","","SQL Exception:"+sqe.getMessage());
						throw new BTSLBaseException(this, "run", PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
					} 
					//

					finally{
						
						if (_pstmtCloseBatchMasterTable!= null){
				        	_pstmtCloseBatchMasterTable.close();
				        }

						if(processVO!=null && processVO.isStatusOkBool())
						{
							try
							{
								Date currentDate= new Date();
								processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
								processVO.setExecutedUpto(currentDate);
								processVO.setStartDate(currentDate);
								processVO.setExecutedOn(currentDate);
								ProcessStatusDAO processDAO=new ProcessStatusDAO();
								if(processDAO.updateProcessDetailNetworkWiseDP(con,processVO)>0) {
								mcomCon.finalCommit();
								} else {
									mcomCon.finalRollback();
									if (_logger.isDebugEnabled()) {
										_logger.error(METHOD_NAME,"  couldn't update record_count for network "+_networkCode+"Reduce the count manually by "+Integer.toString(rows - 1));
									}
								}
							}
							catch(Exception e)
							{
								if (_logger.isDebugEnabled()) {
									_logger.error(METHOD_NAME," Exception in update process status details for network "+_networkCode+" "+e.getMessage());
								}
								_logger.errorTrace(METHOD_NAME,e);
							}
						}
						//
						//ASHU locking system process for system counter
						try {
							mcomCon1 = new MComConnection();
							con1=mcomCon1.getConnection();
							processStatusVOForSystem=processBL.checkProcessUnderProcessNetworkWise(con1,ProcessI.DP_FILEUPLOADID,processVO.getNetworkCode());
							boolean systemStatusOK = processStatusVOForSystem.isStatusOkBool();
							if(systemStatusOK)  {
								processStatusVOForSystem.setRecordCount(processStatusVOForSystem.getRecordCount() - (rows - 1));
								Date currentDate= new Date();
								processStatusVOForSystem.setProcessStatus(ProcessI.STATUS_COMPLETE);
								processStatusVOForSystem.setExecutedUpto(currentDate);
								processStatusVOForSystem.setStartDate(currentDate);							
								ProcessStatusDAO processDAO=new ProcessStatusDAO();
								if(processDAO.updateProcessDetailNetworkWiseDP(con1,processStatusVOForSystem)>0) {
									con1.commit();
								} else {
									con1.rollback();
									String message = "Process "+ProcessI.DP_FILEUPLOADID+" record_count couldn't be updated. Reduce the count manually by "+(rows - 1);
									EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[process]","","","",Integer.toString(rows - 1));
								}
							}
							else {
								String message = "Process "+ProcessI.DP_FILEUPLOADID+" record_count couldn't be updated. Reduce the count manually by "+(rows - 1);
								EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[process]","","","",Integer.toString(rows - 1));
							}

						}catch(Exception e)
						{
							if (_logger.isDebugEnabled()) {
								_logger.error(METHOD_NAME," Exception in update process status details "+e.getMessage());
							}
							_logger.errorTrace(METHOD_NAME,e);
						}
					}

				}
				else {
					throw new BTSLBaseException(METHOD_NAME,PretupsErrorCodesI.DP_PROCESS_IS_ALREADY_EXECUTING);

				}
			}

		}
		catch(Exception e)
		{
			_logger.error(METHOD_NAME, "Exception : " + e.getMessage());
			if(con!=null) {
				try{
					mcomCon.finalRollback();
					} catch(Exception e1){
						_logger.errorTrace(METHOD_NAME, e1);
						}
			}
			_logger.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"DirectPayOut[process]","","",""," Direct pay out process not executed successfully.");

		}
		finally
		{
	        try {
	        	if (_pstmtCloseBatchMasterTable!= null)
					_pstmtCloseBatchMasterTable.close();
				} catch (SQLException e) {
					_logger.error("An error occurred closing statement.", e);
				}
	        
			try{
		        if (_pstmtInsertBatchMaster!= null){
		        	_pstmtInsertBatchMaster.close();
		        }
		      }
		      catch (SQLException e){
		    	  _logger.error("An error occurred closing statement.", e);
		      }
			try{
		        if (_pstmtInsertBatchGeo!= null){
		        	_pstmtInsertBatchGeo.close();
		        }
		      }
		      catch (SQLException e){
		    	  _logger.error("An error occurred closing statement.", e);
		      }
			try{
		        if (_pstmtCloseBatchMasterTable!= null){
		        	_pstmtCloseBatchMasterTable.close();
		        }
		      }
		      catch (SQLException e){
		    	  _logger.error("An error occurred closing statement.", e);
		      }
			if (mcomCon != null) {
				mcomCon.close("DirectPayOut#run");
				mcomCon = null;
			}
			if (mcomCon1 != null) {
				mcomCon1.close("DirectPayOut#run");
				mcomCon1 = null;
			}

			if(_logger.isDebugEnabled()) {
				_logger.debug(METHOD_NAME, "Exiting..... ");
			}
		}

	}


	private void writeFailedTransactionsInDB (ArrayList dataList) throws BTSLBaseException {
		// insert data in the batch items table
		final String METHOD_NAME = "writeFailedTransactionsInDB";
		
		Connection con = null;
		MComConnectionI mcomCon = null;

		StringBuilder strBuffInsertBatchItems = new StringBuilder("INSERT INTO foc_batch_items (batch_id, batch_detail_id, ");
		strBuffInsertBatchItems.append("category_code, msisdn, user_id, status, modified_by, modified_on, user_grade_code,reference_no, ");
		strBuffInsertBatchItems.append("ext_txn_no, ext_txn_date, transfer_date, txn_profile, ");
		strBuffInsertBatchItems.append("commission_profile_set_id, commission_profile_ver, commission_profile_detail_id, ");
		strBuffInsertBatchItems.append("commission_type, commission_rate, commission_value, tax1_type, tax1_rate, ");
		strBuffInsertBatchItems.append("tax1_value, tax2_type, tax2_rate, tax2_value, tax3_type, tax3_rate, ");
		strBuffInsertBatchItems.append("tax3_value, requested_quantity, transfer_mrp, initiator_remarks, external_code,rcrd_status,bonus_type,user_wallet,error) "); 
		strBuffInsertBatchItems.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		if (_logger.isDebugEnabled()) {
			_logger.debug(METHOD_NAME, "strBuffInsertBatchItems Query ="+strBuffInsertBatchItems);
			//ends here
		}
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			try(PreparedStatement _pstmtInsertBatchItems   = con.prepareStatement(strBuffInsertBatchItems.toString());)
			{
			for (Iterator<FOCBatchItemsVO> iterator = dataList.iterator(); iterator.hasNext();) {
				FOCBatchItemsVO fOCBatchItemsVO = iterator.next();
				int queryExecutionCount = -1;
				int index=0;
				if(!BTSLUtil.isNullString(fOCBatchItemsVO.getError()))
				{
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getBatchId());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getBatchDetailId());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getCategoryCode());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getMsisdn());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getUserId());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getStatus());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getModifiedBy());
					_pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(fOCBatchItemsVO.getModifiedOn()));
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getUserGradeCode());
					_pstmtInsertBatchItems.setString(++index, fOCBatchItemsVO.getReferenceNo());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getExtTxnNo()); 
					_pstmtInsertBatchItems.setTimestamp(++index,BTSLUtil.getTimestampFromUtilDate(fOCBatchItemsVO.getExtTxnDate()));
					_pstmtInsertBatchItems.setDate(++index,BTSLUtil.getSQLDateFromUtilDate(fOCBatchItemsVO.getTransferDate()));
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getTxnProfile());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getCommissionProfileSetId());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getCommissionProfileVer());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getCommissionProfileDetailId());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getCommissionType());
					_pstmtInsertBatchItems.setDouble(++index,fOCBatchItemsVO.getCommissionRate());
					_pstmtInsertBatchItems.setLong(++index,fOCBatchItemsVO.getCommissionValue());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getTax1Type());
					_pstmtInsertBatchItems.setDouble(++index,fOCBatchItemsVO.getTax1Rate());
					_pstmtInsertBatchItems.setLong(++index,fOCBatchItemsVO.getTax1Value());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getTax2Type());
					_pstmtInsertBatchItems.setDouble(++index,fOCBatchItemsVO.getTax2Rate());
					_pstmtInsertBatchItems.setLong(++index,fOCBatchItemsVO.getTax2Value());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getTax3Type());
					_pstmtInsertBatchItems.setDouble(++index,fOCBatchItemsVO.getTax3Rate());
					_pstmtInsertBatchItems.setLong(++index,fOCBatchItemsVO.getTax3Value());
					_pstmtInsertBatchItems.setLong(++index,fOCBatchItemsVO.getRequestedQuantity());
					
					_pstmtInsertBatchItems.setLong(++index,fOCBatchItemsVO.getTransferMrp());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getInitiatorRemarks());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getExternalCode());
					_pstmtInsertBatchItems.setString(++index,PretupsI.CHANNEL_TRANSFER_BATCH_DP_ITEM_RCRDSTATUS_PROCESSED);
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getBonusType());
					_pstmtInsertBatchItems.setString(++index, fOCBatchItemsVO.getWalletCode());
					_pstmtInsertBatchItems.setString(++index,fOCBatchItemsVO.getError());
					queryExecutionCount=_pstmtInsertBatchItems.executeUpdate();
					if(queryExecutionCount<=0)
					{
						mcomCon.finalRollback();
						//put error record can not be inserted
						_logger.error(METHOD_NAME, "Record cannot be inserted in batch items table");
						DirectPayOutErrorLog.log(fOCBatchItemsVO.getMsisdn(),fileName,"DB error record cannot be inserted in table foc batch items");
						continue;
					}
					mcomCon.finalCommit(); 
				}
			}
		}
		}catch(SQLException sqe) {
			_logger.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"DirectPayOut[writeFailedTransactionsInDB]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally
		{
			
			if (mcomCon != null) {
				mcomCon.close("DirectPayOut#writeFailedTransactionsInDB");
				mcomCon = null;
			}

		}	
	}
}



