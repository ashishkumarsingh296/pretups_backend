package com.client.voms.vomsprocesses.businesslogic;

/**
 * @(#)VoucherLoaderProcessBL.java
 * Copyright(c) 2016, Mahindra Comviva Technologies Ltd.
 * All Rights Reserved
 *-------------------------------------------------------------------------------------------------
 * Author				Date			History
 *-------------------------------------------------------------------------------------------------
 * Mahindra Comviva 	23/08/16 		Created
 * ------------------------------------------------------------------------------------------------
 * This class is responsible to provide following functionalities.
 * 1.Reading particular directory for voucher files.
 * 2.Call the parser class for the parsing and validating of the input file
 * 3.Use the VomsVoucherDAO class for uploading the records in the file to the database
 */

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.PasswordField;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;

public class VoucherLoaderProcessBL 
{
	private static final Log _log = LogFactory.getLog(VoucherLoaderProcessBL.class.getName());		
	private String filePath;			//File path name
	private ArrayList fileList;		//Contain all the file object thats name start with file prefix.
	private String loginID = null;
	private String password = null;
	private File inputFile;			//File object for input file
	private File destFile;				//Destination File object	
	private String moveLocation;		//Input file is move to this location After successful processing 
	private String fileExt="DAT";			//Files are picked up only this extention from the specified directory
	private String fileName = null;
	private ChannelUserVO channelUserVO=null;
	private static VoucherFileChecksI parserObj = null;
    private ProcessStatusDAO processStatusDAO = null;
  	private ProcessStatusVO processStatusVO = null;
	private static Date currentDate = null;
	private VoucherUploadVO voucherUploadVO=null;
    private static int maxNoRecordsAllowed=0;
    private long numberOfRecords=0;
    private String productID=null;
	private static boolean directVoucherEnable=false;
	private static String className="VoucherLoaderProcessBL";
	
	static final String EXCEPTION = "Exception =";
	static final String GETTINGEXCEPTION = "Getting Exception =";
	static final String NODATAFOUND = " No Data Found";
	static final String RECORDNUMBER = " Record Number = ";
	static final String BTSLBASEEXCEPTION = " BTSLBaseException be = ";
	static final String VOUCHERUPLOADPROCESSFAILED = "Voucher Upload Process Failed for file = ";
	
	
	public VoucherLoaderProcessBL()
	{
       	processStatusDAO = new ProcessStatusDAO();
		currentDate=new Date();
    }

	/**
	 * Main starting point for the process
	 * @param args
	 */
    public static void main(String[] args)
	{
    	final String methodName = "main";
		try
		{ 
			int argSize = args.length;
			//This logic uses the number of arguments specified to decide how to retrieve the information 
			// for userid, password, profile, name of the voucherFile, number of records.
			if(argSize > 2 && argSize != 4)
			{
				_log.info(methodName, "Usage : VoucherLoaderProcessBL [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,className+"[main]","","","","Improper usage. Usage : VoucherLoaderProcessBL [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
			}
			else if(argSize < 2)
			{
				_log.info(methodName, "Usage : VoucherLoaderProcessBL [Constants file] [ProcessLogConfig file]");
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,className+"[main]","","","","Improper usage. Usage : VoucherLoaderProcessBL [Constants file] [ProcessLogConfig file]");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
			}
			new VoucherLoaderProcessBL().process(args);
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
	  	}
		catch(Exception e)
		{
			
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className,"","","","Exiting the exception of main");
			_log.errorTrace(methodName,e);
		}//end of outer Exception	
		finally
		{
			ConfigServlet.destroyProcessCache();
			_log.info(methodName,"Process Executed Successfully");
		}
		
    }	
	
    /**
     * Method that handle the complete flow of the process
     * @param pArgs
     * @throws BTSLBaseException
     */
	private void process(String [] pArgs) throws BTSLBaseException
	{
		final String methodName = "process";
		final String classMethodName="VoucherFileUploader[process]";
		Connection con= null;
		ProcessBL processBL=new ProcessBL();
		boolean processStatusOK=false;
		ArrayList batchList=null;
		try
		{
			int argSize=pArgs.length;
			VoucherFileUploaderUtil.loadCachesAndLogFiles(pArgs[0],pArgs[1]);
			
			//opening the connection 
			con = OracleUtil.getSingleConnection();
			if(con==null)
			{
				_log.error(classMethodName,": Could not connect to database. Please make sure that database server is up..............");
				EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","","Could not connect to Database");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
			}

			processStatusVO=processBL.checkProcessUnderProcess(con,ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID);
		    
		    if(!(processStatusVO!=null && processStatusVO.isStatusOkBool()))
		        throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
		    processStatusOK = processStatusVO.isStatusOkBool();
		    //Commiting the status of process status as 'U-Under Process'.
		    con.commit();


			loadConstantValues();

			if(argSize == 2)
			{
				getDetailsInteractively(con);				
			}
			else if(argSize == 4)
			{
				getDetailsFromConsole(con,pArgs);			
			}	
			
			
			loadFilesFromDir();
			
			VomsBatchVO vomsBatchVO=null;
			VomsProductDAO vomsProductDAO=null;
			VomsVoucherDAO vomsVoucherDAO=null;
			VomsBatchVO enableBatchVO=null;
			
			for(int l=0,size=fileList.size();l<size;l++)
	        {
			    voucherUploadVO=null;
			    productID=null;
			    numberOfRecords=0;
			    vomsBatchVO=null;
			    inputFile=null;
			    fileName=null;
			    parserObj=null;
			    
			    //Getting the file object
	            inputFile = (File)fileList.get(l);
	            fileName=inputFile.getName();
	            
				try
				{
				    //creates an instance of the parser class based on the entry in the Constants file
					getParserObj(Constants.getProperty("VOUCHER_PARSER_CLASS"));
			       	
				    //this method will be used for validation of the Voucher file
					validateInputFile();
					
					if (voucherUploadVO.getVoucherArrayList() == null || voucherUploadVO.getVoucherArrayList().isEmpty()) 
					{
						_log.error(classMethodName," No voucher for adding in file="+fileName);
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","",""," No vouchers for adding in file="+fileName);
						throw new BTSLBaseException(this, "process", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
					}
		
					productID =BTSLUtil.NullToString(voucherUploadVO.getProductID());
					numberOfRecords=voucherUploadVO.getNoOfRecordsInFile();
					
					vomsProductDAO = new VomsProductDAO();
					
					//checks if the particular profile exists or not 
					if(!vomsProductDAO.isProductExitsVoucherGen(con,productID,VOMSI.VOMS_STATUS_ACTIVE))
					{
						_log.debug(methodName, " No such product exists in the system for file="+fileName+".........");
						_log.error(methodName," : Product does not exists for file="+fileName );
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,classMethodName,"","","","Product ID provided for voucher file does not exists in the system for file="+fileName);
						throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_PRODUCT_NOT_EXISTS);
					}		
					
					vomsBatchVO=prepareVomsBatchesVO(VOMSI.BATCH_GENERATED,false,null);
					
					batchList=new ArrayList();
					batchList.add(vomsBatchVO);
					
					int recordCount=0;
					recordCount = new VomsBatchesDAO().addBatch(con,batchList);
					if(recordCount<=0)
					{
						_log.error(className," Not able to insert batches for file="+fileName);
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","",""," The batch entry could not be made into the table for file="+fileName);
						throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
					}
					
					//For Direct enabling of Vouchers
					VomsVoucherVO voucherVO=null;
					ArrayList voucherLogList=new ArrayList();
					
					if(directVoucherEnable)
					{
						enableBatchVO=prepareVomsBatchesVO(VOMSI.BATCH_ENABLED,true,vomsBatchVO);
						batchList=new ArrayList();
						batchList.add(enableBatchVO);
						recordCount = new VomsBatchesDAO().addBatch(con,batchList);
						if(recordCount<=0)
						{
							_log.error(className," Not able to insert batches ");
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,className+"","","",""," The batch entry could not be made into the table");
							throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
						}
						ArrayList voucherList=voucherUploadVO.getVoucherArrayList();
						int s=voucherList.size();
						VomsVoucherVO vomsVoucherVO=null;
						for (int counter = 0; counter < s ; counter++)					
						{
							vomsVoucherVO=(VomsVoucherVO)voucherList.get(counter);
							vomsVoucherVO.setEnableBatchNo(enableBatchVO.getBatchNo());
							vomsVoucherVO.setStatus(VOMSI.VOUCHER_ENABLE);
							vomsVoucherVO.setCurrentStatus(VOMSI.VOUCHER_ENABLE);
							vomsVoucherVO.setPreviousStatus(VOMSI.VOUCHER_NEW);
						}
						voucherVO=new VomsVoucherVO();
						voucherVO.setEnableBatchNo(enableBatchVO.getBatchNo());
						voucherVO.setSerialNo(enableBatchVO.getFromSerialNo());
						voucherVO.setToSerialNo(enableBatchVO.getToSerialNo());
						voucherVO.setPreviousStatus(VOMSI.VOUCHER_NEW);
						voucherVO.setPrevStatusModifiedBy(enableBatchVO.getModifiedBy());
						voucherVO.setPrevStatusModifiedOn(BTSLUtil.getDateStringFromDate(enableBatchVO.getModifiedOn()));
						voucherVO.setVoucherStatus(VOMSI.VOUCHER_ENABLE);
						voucherVO.setStatusChangeSource(VOMSI.VOUCHER_ENABLE);
						voucherVO.setMRP(0);
						voucherVO.setExpiryDateStr("");
						voucherVO.setLastErrorMessage(BTSLUtil.NullToString(enableBatchVO.getMessage()));
						voucherVO.setProcess("1");
						voucherVO.setProductionLocationCode(enableBatchVO.getLocationCode());
						voucherLogList.add(voucherVO); // adding the last entry				
					}
					
					vomsVoucherDAO=new VomsVoucherDAO();
					recordCount=vomsVoucherDAO.insertVouchers(con,vomsBatchVO,voucherUploadVO.getVoucherArrayList(),directVoucherEnable);
					if(recordCount<=0)
					{
						_log.error(classMethodName," Not able to insert Vouchers for file="+fileName);
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","",""," The vouchers could not be inserted into the table for file="+fileName);
						throw new BTSLBaseException("VoucherLoaderProcessBL "," process ",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
					}
					

					
					recordCount=0;
					recordCount = vomsVoucherDAO.updateSummaryTable(con, vomsBatchVO,directVoucherEnable);
					if(recordCount<=0)
					{
						_log.error(classMethodName," Not able to update Summary tables for file="+fileName);
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","",""," The batch summary table could not be updated for file="+fileName);
						throw new BTSLBaseException("VoucherLoaderProcessBL "," process ",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
					}
		
					con.commit();
						
					//name of the file after moving
					VoucherFileUploaderUtil.moveFileToAnotherDirectory(fileName,filePath+File.separator+fileName,moveLocation);
					
					VomsBatchInfoLog.modifyBatchLog(vomsBatchVO);
					
					if(directVoucherEnable)
					{
						VomsVoucherChangeStatusLog.log(voucherLogList);
						VomsBatchInfoLog.modifyBatchLog(enableBatchVO);
					}
					
				}
				catch(BTSLBaseException be)
				{	_log.errorTrace(methodName,be);
					_log.debug(methodName, VOUCHERUPLOADPROCESSFAILED+fileName+" .Check the log file or OAM Screen or Event log for exact errors ");
					if(con!=null)
					try{
						con.rollback();
					}
					catch (SQLException e1) 
					{
						_log.errorTrace(methodName,e1);
					}
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","",""," Voucher Upload Process Failed for file="+fileName+GETTINGEXCEPTION+be.getMessage());
					continue;
				}
				catch(Exception e)
				{
					_log.debug(methodName, VOUCHERUPLOADPROCESSFAILED+fileName+" .Check the log file or OAM Screen or Event log for exact errors ");
					try{
						con.rollback();
					}
					catch (SQLException e1) {
						_log.errorTrace(methodName,e1);
					}
					_log.errorTrace(methodName,e);
					_log.error(classMethodName,EXCEPTION+e.getMessage());
					EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherLoaderProcessBL[process]","","","",VOUCHERUPLOADPROCESSFAILED+fileName+GETTINGEXCEPTION+e.getMessage());
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
				}	
				if(directVoucherEnable)
				{
					//validating for number of actual records inserted in voms_voucher.On the basis of records inserted, successful uploading of file is determined
					_log.debug(methodName, "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = "+enableBatchVO.getBatchNo());
					if(_log.isDebugEnabled())
						_log.debug(" process","Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = "+enableBatchVO.getBatchNo());
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,classMethodName,"","","","Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = "+enableBatchVO.getBatchNo());

				}
				else
				{
					//validating for number of actual records inserted in voms_voucher.On the basis of records inserted, successful uploading of file is determined
					_log.debug(methodName, "Voucher Upload Process for file="+fileName+" Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = "+vomsBatchVO.getBatchNo());
					if(_log.isDebugEnabled())
						_log.debug(" process","Voucher Upload Process for file="+fileName+" Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = "+vomsBatchVO.getBatchNo());
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,classMethodName,"","","","Voucher Upload Process for file="+fileName+" Successfully Executed.Batch Successfully Generated for file="+fileName+" . Your Batch number for the process is = "+vomsBatchVO.getBatchNo());
				}
	        }
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
			_log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
			if(con!=null)
			try{
				con.rollback();
				}
			catch (SQLException e1) {
				_log.errorTrace(methodName,e1);
				}
			throw be;
		}
		catch(Exception e)
		{
			_log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
			try{
				if(con!=null)
					con.rollback();
				}
			catch (SQLException e1) 
			{
				_log.errorTrace(methodName,e1);
				}
			_log.error(classMethodName,EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}	
		finally
		{
			try
		    {
			    //Setting the process status as 'C-Complete' if the processStatusOK is true
			    if(processStatusOK)
			    {
				    Date date = new Date();
			        processStatusVO.setStartDate(processStatusVO.getStartDate());
					processStatusVO.setExecutedOn(date);
					processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
				    int successU = processStatusDAO.updateProcessDetail(con,processStatusVO);
				    
				    //Commiting the process status as 'C-Complete'
				    if(successU>0)
				    	con.commit();
				    else
					{
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"processStatusVO.getProcessID()"+ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID,"","","Error while updating the process status after completing the process");
						throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.PROCESS_ERROR_UPDATE_STATUS);
					}
			    }//end of IF-Checks the proccess status
		    }//end of try-block
		    catch(BTSLBaseException be)
		    {
				_log.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
			    if(con!=null)
			    	try {
			    		con.rollback();
			    		} 
			    catch (Exception e1)
			    {
			    	_log.errorTrace(methodName,e1);
			    	}
				throw be;
		    }//end of catch-BTSLBaseException
		    catch(Exception e)
		    {
		    	_log.errorTrace(methodName,e);
				_log.error(methodName, "Exception e= " + e.getMessage());
			    if(con!=null)
			    	try {
			    		con.rollback();
			    		} 
			    catch (Exception e1)
			    {
			    	_log.errorTrace(methodName,e1);
			    	}
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"processStatusVO.getProcessID()"+ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID,"","","BaseException:"+e.getMessage());
				throw new BTSLBaseException(this,"process","error.general.processing");
		    }//end of catch-Exception
		    finally
		    {
		        if(con!=null)
		        	try 
		        {
		        		con.close();
		        		} 
		        catch (Exception e1)
		        {
		        	_log.errorTrace(methodName,e1);
		        	}
		        fileName = null;
				channelUserVO=null;
				parserObj = null;
			    processStatusDAO = null;
			  	processStatusVO = null;
				voucherUploadVO=null;
			    maxNoRecordsAllowed=0;
			    numberOfRecords=0;
			    productID=null;				        
		    }
			if(_log.isDebugEnabled())
				_log.debug(methodName, "Exiting ");			
		}
	}	
	
	/**
	 * Method to load constant values for the process
	 * @throws BTSLBaseException
	 */
	public void loadConstantValues() throws BTSLBaseException
    {
		final String methodName = "loadConstantValues";
		final String classMethodName="VoucherLoaderProcessBL[loadConstantValues]";
		if(_log.isDebugEnabled())
			_log.debug("loadConstantValues "," Entered ");
        try
	    {
			//this reads the number of records allowed in the file.
			if(_log.isDebugEnabled())
				_log.debug(methodName,": reading  maxNoRecordsAllowed  ");	
			try
			{
				maxNoRecordsAllowed = Integer.parseInt(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName,e);
				_log.info(methodName, " Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
				_log.error(methodName,"Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,classMethodName,"","","","Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
     		}
			
			//this is the path of the input voucher file.
			if(_log.isDebugEnabled())
				_log.debug(methodName,": reading  filePath ");
			filePath = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_PATH"));	
			
			//Checking whether the file path provided exist or not.If not, throw an exception
            if(!(new File(filePath).exists()))
            {
				_log.info(methodName, " Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
				_log.error("loadConstantValues ","Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
            }
			
			//this is the location where the voucher file will be moved after the vouchers are uploaded
			if(_log.isDebugEnabled())
				_log.debug(methodName," reading moveLocation ");
			moveLocation=BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_MOVE_PATH"));
			
			//Destination location where the input file will be moved after successful reading.
			destFile= new File(moveLocation);

			//Checking the destination location for the existence. If it does not exist stop the proccess.
			if(!destFile.exists())
			{
			    if(_log.isDebugEnabled())
			    	_log.debug(methodName," Destination Location checking= "+moveLocation +" does not exist");
			    boolean fileCreation = destFile.mkdirs();
			    if(fileCreation)
			        if(_log.isDebugEnabled())
			        	_log.debug(methodName," New Location = "+destFile +"has been created successfully");
			    else
				{
					_log.debug(methodName, " Configuration Problem, Could not create the backup directory at the specified location "+moveLocation);
					_log.error("loadConstantsValues "," Configuration Problem, Could not create the backup directory at the specified location "+moveLocation);
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,classMethodName,"","","","Configuration Problem, Could not create the backup directory at the specified location "+moveLocation);
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR); 
				}						
			}		
			//Added for Direct Voucher Enabling
			directVoucherEnable=((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();

	    }//end of try block
        catch(BTSLBaseException be)
        {
        	_log.errorTrace(methodName,be);
			_log.error(methodName ,BTSLBASEEXCEPTION+be.getMessage());
			throw be;
        }//end of catch-BTSLBaseException
	    catch(Exception e)
	    {
	    	_log.errorTrace(methodName,e);
			_log.error(methodName ,"Exception e="+e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","","Exception while loading the constants from the Constants.prop file");
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR); 
	    }//end of catch-Exception
	    finally
	    {
	        if(_log.isDebugEnabled())
	        	_log.debug(methodName ," Exiting: filePath = "+filePath+" moveLocation= "+moveLocation);
	    }//end of finally
    }//end of loadConstantValues
	/**
	 * Method used for filter files for given extension 
	 * @param directory
	 * @return
	 */
	private ArrayList filterFilesExt(String directory) {
		ArrayList files = new ArrayList();
		  File dir = new File(directory);
		  for (File file : dir.listFiles()) {
		    if (file.getName().endsWith(fileExt)) {
		      files.add(file);
		    }
		  }
		  return files;
		}
	
   /**
    * This method is used to loadAll the files with specified prefix.
    * All these file objects are stored in ArrayList.
    * @throws	BTSLBaseException
    */
    public void loadFilesFromDir() throws BTSLBaseException
    {
    	final String methodName = "loadFilesFromDir";
        if(_log.isDebugEnabled())
        	_log.debug(methodName,"Entered");
        File directory=null; 
        try
        {    fileList = new ArrayList(); 
	        directory= new File(filePath);
	        //Check if the directory contains any files
	        if (directory.list() == null) 
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);//"The voucher file does not exists at the location specified"				
	        fileList=filterFilesExt(filePath);
	        //Check whether the directory contains the file start with filePrefix.
	        if(fileList.isEmpty())
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);//"The voucher file does not exists at the location specified"				
        }
        catch(BTSLBaseException be)
        {
        	_log.errorTrace(methodName,be);
            _log.error(methodName,BTSLBASEEXCEPTION+be.getMessage());
			_log.debug(methodName, " No files exists at the following ("+filePath+") specified, please check the path...............");
			_log.error(methodName," No files exists at the following ("+filePath+") specified, please check the path");
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,"VoucherLoaderProcessBL[loadFilesFromDir]","","","","No files exists at the following ("+filePath+") specified, please check the path");
            throw be;
        }//end of catch-BTSLBaseException
        catch(Exception e)
        {
        	_log.errorTrace(methodName,e);
            _log.error(methodName,"Exception e = "+e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VoucherLoaderProcessBL[loadFilesFromDir]","","","","Exception:"+e.getMessage());
            throw new BTSLBaseException(this,methodName,e.getMessage());
        }//end of catch-Exception
        finally
        {
            if(_log.isDebugEnabled()) 
            	_log.debug(methodName,"Exited");
        }//end of finally
    }//end of loadFilesFromDir
    
    /**
     * This method takes the required input from the arguments directly and authenticates the user
     * @param pCon
     * @param pArgs
     * @throws BTSLBaseException
     * @throws Exception
     */
	public void getDetailsFromConsole(Connection pCon,String pArgs[])throws BTSLBaseException,Exception
	{
		final String methodName = "getDetailsFromConsole";		
		if(_log.isDebugEnabled())
			_log.debug(methodName," Enetered ");

		try
		{
			//Get the login ID from the arguments
			loginID = pArgs[2];
			//Get the Password from the arguments
			password = pArgs[3];
			
			channelUserVO=VoucherFileUploaderUtil.validateUserWithRole(pCon,loginID,password,PretupsI.LOCALE_LANGAUGE_EN,VoucherFileUploaderUtil.AUTH_TYPE_ROLE);
			
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
			_log.error(methodName,BTSLBASEEXCEPTION+be.getMessage());
			throw be;
		}//end of BTSLBaseException	
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error("VoucherLoaderProcessBL[getDetailsFromConsole]",EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherLoaderProcessBL[getDetailsFromConsole]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}// end of Exception			
	}	
	
	/**
	 * Method to get the details of the process interactively
	 * @param pCon
	 * @throws BTSLBaseException
	 */
	public void getDetailsInteractively(Connection pCon)throws BTSLBaseException
	{
		final String methodName = "getDetailsInteractively";
		final String classMethodName="VoucherLoaderProcessBL[getDetailsInteractively]";
		if(_log.isDebugEnabled())
			_log.debug(methodName," : Constant Variable Uploaded ................ ");
		
		try
		{
			//asks the user to input the loginid
			loginID = VoucherFileUploaderUtil.dataFromUser(VoucherFileUploaderI.LOGINID);
			
			//asks the user to input the password field usng the password class
			PasswordField passfield = new PasswordField();
		  	try 
			{
				password = passfield.getPassword(VoucherFileUploaderI.PASSWORD);
			} 
			catch(Exception e) 
			{
				_log.errorTrace(methodName,e);
				_log.error(methodName,": The password could not be retreived ");
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","","Not able to get input data (Password) from console from user");
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_PASSWORD_RETREIVAL);
			}
	
			//validates the user for being network admin. If not throws an exception
			channelUserVO=VoucherFileUploaderUtil.validateUserWithRole(pCon,loginID,password,PretupsI.LOCALE_LANGAUGE_EN,VoucherFileUploaderUtil.AUTH_TYPE_ROLE);
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
			_log.error(methodName ,BTSLBASEEXCEPTION+be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error(classMethodName,EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}
	}	
	
	/**
	 * This method will validate the input file specified by the user.
	 * Validation logic is in Parser class
	 * @throws BTSLBaseException
	 */
	private void validateInputFile() throws BTSLBaseException 
	{
		final String methodName = "validateInputFile";
		if(_log.isDebugEnabled())
			_log.debug(methodName," Entered................ ");
		try
		{
			//call the loadConstantValues() to load the values.
			parserObj.loadConstantValues();	
			
			populateVoucherUploadVO();		
			
			//to validate the file length against the uesr input file length
			parserObj.getFileLength(voucherUploadVO);
			
			//called to validate the file, whole file at the time.
			voucherUploadVO=parserObj.validateVoucherFile();
			
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
			_log.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error("VoucherLoaderProcessBL[validateInputFile]",EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherLoaderProcessBL[validateInputFile]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}
		if(_log.isDebugEnabled())
			_log.debug(" validateInputFile"," Exiting................ ");
	}
	
	/**
	 * This method populates the Voucher Upload VO that will be given to Parser class
	 * @throws BTSLBaseException
	 */
	private void populateVoucherUploadVO() throws BTSLBaseException 
	{
		final String methodName = "populateVoucherUploadVO";
		if(_log.isDebugEnabled())
			_log.debug(methodName," Entered................ ");
		try
		{
			voucherUploadVO=new VoucherUploadVO();
			voucherUploadVO.setProcessType(VoucherUploadVO._AUTOPROCESSTYPE);
			voucherUploadVO.setFileName(fileName);
			voucherUploadVO.setFilePath(filePath);
			voucherUploadVO.setMaxNoOfRecordsAllowed(maxNoRecordsAllowed);
			voucherUploadVO.setChannelUserVO(channelUserVO);
			voucherUploadVO.setCurrentDate(currentDate);
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error("VoucherLoaderProcessBL[populateVoucherUploadVO]",EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherLoaderProcessBL[populateVoucherUploadVO]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}	
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(methodName," Exiting................ ");
		}
	}
	
	/**
	 * It prepares Voms Batches VO for insertion in Database
	 * @return
	 * @throws BTSLBaseException
	 */
	private VomsBatchVO prepareVomsBatchesVO(String pBatchType,boolean pIsDirectEnable,VomsBatchVO pVomsBatchVO) throws BTSLBaseException
	{
		final String methodName = "prepareVomsBatchesVO";
		if(_log.isDebugEnabled())
			_log.debug(methodName," Entered with pBatchType="+pBatchType+" pIsDirectEnable="+pIsDirectEnable);
		
		VomsBatchVO vomsBatchVO=null;
		String batchNo=null;
		try
		{
			vomsBatchVO=new VomsBatchVO();
			batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE,String.valueOf(BTSLUtil.getFinancialYear()),VOMSI.ALL));
			
			vomsBatchVO.setProductID(productID.toUpperCase());	
			vomsBatchVO.setBatchType(pBatchType);
			vomsBatchVO.setNoOfVoucher(voucherUploadVO.getActualNoOfRecords());	
			vomsBatchVO.setFromSerialNo(voucherUploadVO.getFromSerialNo());
			vomsBatchVO.setToSerialNo(String.valueOf(voucherUploadVO.getToSerialNo()));
			vomsBatchVO.setFailCount(numberOfRecords - (voucherUploadVO.getVoucherArrayList()).size());//for setting the no. of failed records
			vomsBatchVO.setOneTimeUsage(PretupsI.YES);
			vomsBatchVO.setSuccessCount(voucherUploadVO.getActualNoOfRecords());
			vomsBatchVO.setLocationCode(channelUserVO.getNetworkID());
			vomsBatchVO.setCreatedBy(channelUserVO.getUserID());
			vomsBatchVO.setCreatedOn(currentDate);
			vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
			vomsBatchVO.setModifiedBy(channelUserVO.getUserID());
			vomsBatchVO.setModifiedOn(currentDate);
			vomsBatchVO.setDownloadCount(1);
			vomsBatchVO.setStatus(VOMSI.EXECUTED);
			vomsBatchVO.setCreatedDate(currentDate);
			vomsBatchVO.setModifiedDate(currentDate);			
			vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_GEN);
			vomsBatchVO.setMessage(" Batch SuccessFully Executed ...........");
			
			if(pIsDirectEnable)
			{
				vomsBatchVO.setReferenceNo(pVomsBatchVO.getBatchNo());
				vomsBatchVO.setReferenceType(pVomsBatchVO.getBatchType());
				vomsBatchVO.setTotalVoucherPerOrder(0);	
				vomsBatchVO.setDownloadCount(0);
				vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_ENABLE);
			}
		}
		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName,be);
			_log.error(methodName, BTSLBASEEXCEPTION + be);
			throw be;
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error("VoucherLoaderProcessBL[prepareVomsBatchesVO]",EXCEPTION+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherLoaderProcessBL[prepareVomsBatchesVO]","","","",GETTINGEXCEPTION+e.getMessage());
			throw new BTSLBaseException(className," prepareVomsBatchesVO ",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
		}
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(methodName," Exiting with vomsBatchVO= "+vomsBatchVO);
		}
		return vomsBatchVO;
	}	
	
	 /**
     * This method is used to create an instance of parser class.This parser class is operator specific
     * @param 	String parserClassName
     * @throws	BTSLBaseException
     */
    private void getParserObj(String pParserClassName) throws BTSLBaseException
    {
    	final String methodName = "getParserObj";
        if(_log.isDebugEnabled())
        	_log.debug(methodName," Entered with pParserClassName = "+pParserClassName);
		try
		{
		    //Creating the instance of parser class.
		    parserObj=(VoucherFileChecksI)Class.forName(pParserClassName).newInstance();
		}
		catch(Exception e)
		{
			_log.errorTrace(methodName,e);
			_log.error(methodName," Exception "+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherLoaderProcessBL[getParserObj]","","","","Exception: The object of the parser class could not be created dynamically");
			throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED);
		}//end of catch-Exception
		finally
		{
			if(_log.isDebugEnabled())
				_log.debug(methodName," Exiting with parserObj = "+parserObj);
		}//end of finally
    }//end of getParserObj
}
