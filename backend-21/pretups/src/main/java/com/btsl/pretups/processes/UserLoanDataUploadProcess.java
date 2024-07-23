package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.LoanDataVO;
import com.btsl.user.businesslogic.UserLoanDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;


public class UserLoanDataUploadProcess {

	
	/**
	 * @(#)UserLoanDataUploadProcess.java
	 * Copyright(c) 2021, Mahindra Comviva Technologies Ltd.
	 * All Rights Reserved
	 *-------------------------------------------------------------------------------------------------
	 * Author				Date			History
	 *-------------------------------------------------------------------------------------------------
	 * Mahindra Comviva 	16/08/21 		Created
	 * ------------------------------------------------------------------------------------------------
	 * This class is responsible to provide following functionalities.
	 * 1.Reading particular directory for USER loan files.
	 * 2.Call the parser class for the parsing and validating of the input file
	 * 3.Use the userLoanDAO class for uploading the records in the file to the database
	 */


	
		private static final Log _log = LogFactory.getLog(UserLoanDataUploadProcess.class.getName());		
		private String filePath;			//File path name
		private ArrayList fileList;		//Contain all the file object thats name start with file prefix.
		private String loginID = null;
		private String password = null;
		private File inputFile;			//File object for input file
		private File destFile;				//Destination File object	
		private String moveLocation;		//Input file is move to this location After successful processing 
		private String fileExt="csv";			//Files are picked up only this extention from the specified directory
		private String fileName = null;
		private ChannelUserVO channelUserVO=null;
		private static UserLoanFileChecksI parserObj = null;
	    private ProcessStatusDAO processStatusDAO = null;
	  	private ProcessStatusVO processStatusVO = null;
		private static Date currentDate = null;
		private LoanDataVO LoanDataVO=null;
	    private static int maxNoRecordsAllowed=0;
	    private long numberOfRecords=0;
	    String productID =null;
	    Connection con= null;
		
	 	private static String className="UserLoanDataUploadProcess";
		
		static final String EXCEPTION = "Exception =";
		static final String GETTINGEXCEPTION = "Getting Exception =";
		static final String NODATAFOUND = " No Data Found";
		static final String RECORDNUMBER = " Record Number = ";
		static final String BTSLBASEEXCEPTION = " BTSLBaseException be = ";
		static final String VOUCHERUPLOADPROCESSFAILED = "User Loan Upload Process Failed for file = ";
			
		
		public UserLoanDataUploadProcess()
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
					_log.info(methodName, "Usage : UserLoanDataUploadProcess [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,className+"[main]","","","","Improper usage. Usage : UserLoanDataUploadProcess [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
				}
				else if(argSize < 2)
				{
					_log.info(methodName, "Usage : UserLoanDataUploadProcess [Constants file] [ProcessLogConfig file]");
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,className+"[main]","","","","Improper usage. Usage : UserLoanDataUploadProcess [Constants file] [ProcessLogConfig file]");
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
				}
				new UserLoanDataUploadProcess().process(args);
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
			final String classMethodName="UserLoanDataUploadProcess[process]";
			ProcessBL processBL=new ProcessBL();
			boolean processStatusOK=false;
			ArrayList batchList=null;
			try
			{
				int argSize=pArgs.length;
				
				  File logconfigFile = null;
			        File constantsFile = null;
			        try {
			            constantsFile = new File(pArgs[0]);
			            if (!constantsFile.exists()) {
			                _log.debug(methodName, " loadCachesAndLogFiles Constants file not found on location:: " + constantsFile.toString());
			                _log.error("[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
			                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "[loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
			                throw new BTSLBaseException("VoucherFileUploaderUtil ", " loadCachesAndLogFiles ", PretupsErrorCodesI.VOUCHER_MISSING_CONST_FILE);
			            }

			            logconfigFile = new File(pArgs[1]);
			            if (!logconfigFile.exists()) {
			                _log.debug(methodName, " loadCachesAndLogFiles Logconfig file not found on location:: " + logconfigFile.toString());
			                _log.error("VoucherFileProcessor[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
			                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileProcessor[loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
			                throw new BTSLBaseException("VoucherFileProcessor ", "loadCachesAndLogFiles ", PretupsErrorCodesI.VOUCHER_MISSING_LOG_FILE);
			            }
			            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
			        } catch (BTSLBaseException be) {
			            _log.error("[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
			            throw be;
			        }// end of BTSLBaseException
			        catch (Exception e) {
			            _log.errorTrace(methodName, e);
			            _log.error("[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
			            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "[loadCachesAndLogFiles]", "", "", "", "Exception=" + e.getMessage());
			            throw new BTSLBaseException(" ", " loadCachesAndLogFiles ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			        }// end of Exception
			        finally {
			            if (logconfigFile != null) {
			                logconfigFile = null;
			            }
			            if (constantsFile != null) {
			                constantsFile = null;
			            }
			            if (_log.isDebugEnabled()) {
			                _log.debug("[loadCachesAndLogFiles]", " Exiting..........");
			            }
			        }// end of finally
			        
				//opening the connection 
				con = OracleUtil.getSingleConnection();
				if(con==null)
				{
					_log.error(classMethodName,": Could not connect to database. Please make sure that database server is up..............");
					EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","","","Could not connect to Database");
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
				}

				processStatusVO=processBL.checkProcessUnderProcess(con,ProcessI.LOAN_FILE_UPLOAD_PROCCESSID);
			    
			    if(!(processStatusVO!=null && processStatusVO.isStatusOkBool()))
			        throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
			    processStatusOK = processStatusVO.isStatusOkBool();
			    //Commiting the status of process status as 'U-Under Process'.
			    con.commit();


				loadConstantValues();

				loadFilesFromDir();
				
				UserLoanDAO userLoanDAO=null;
				
				for(int l=0,size=fileList.size();l<size;l++)
		        {
					LoanDataVO=null;
				    productID=null;
				    numberOfRecords=0;
				    inputFile=null;
				    fileName=null;
				    parserObj=null;
				    
				    //Getting the file object
		            inputFile = (File)fileList.get(l);
		            fileName=inputFile.getName();
		            
					try
					{
					    //creates an instance of the parser class based on the entry in the Constants file
						getParserObj(Constants.getProperty("USER_LOAN_PARSER_CLASS"));
				       	
					    //this method will be used for validation of the Voucher file
						validateInputFile();
						
						if (LoanDataVO.getLoanArrayList() == null || LoanDataVO.getLoanArrayList().isEmpty()) 
						{
							_log.error(classMethodName," No voucher for adding in file="+fileName);
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","",""," No vouchers for adding in file="+fileName);
							throw new BTSLBaseException(this, "process", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
						}
			
						
						productID =BTSLUtil.NullToString(LoanDataVO.getProductID());
						numberOfRecords=LoanDataVO.getNoOfRecordsInFile();
			
						
						_log.debug(methodName,"numberOfRecords"+numberOfRecords+"productID"+productID);
						
						//For Direct enabling of Vouchers
						UserLoanVO userLoanVO=null;
						ArrayList userLoanList=new ArrayList();
						
							ArrayList loanList=LoanDataVO.getLoanArrayList();
							
							int i =0;
							
							_log.debug(methodName,"loanList.size()"+loanList.size());
							
							for (i=0;i<loanList.size();i++) {
							
							LoanDataVO loanDataVO = (LoanDataVO)loanList.get(i);
							
							userLoanVO=new UserLoanVO();
							userLoanVO.setUser_id(loanDataVO.getRetailerUserID());
							userLoanVO.setProduct_code((loanDataVO.getProductCode()));
							_log.debug(methodName,"userLoanVO.getLoan_threhold()"+userLoanVO.getLoan_threhold());
													
							userLoanVO.setLoan_threhold(PretupsBL.getSystemAmount(loanDataVO.getLoanThreshold()));
							_log.debug(methodName,"userLoanVO.getLoan_threhold()"+userLoanVO.getLoan_threhold());
							
							userLoanVO.setLoan_amount(PretupsBL.getSystemAmount(loanDataVO.getLoanAmount()));
							userLoanVO.setLoan_given(PretupsI.NO);
							userLoanVO.setLoan_given_amount(0);
							userLoanVO.setLast_loan_txn_id("");
							userLoanVO.setSettlement_id("");
							userLoanVO.setSettlement_from("");
							userLoanVO.setSettlement_loan_amount(0);
							userLoanVO.setSettlement_loan_interest(0);
							userLoanVO.setLoan_taken_from("");
							userLoanVO.setSettlement_from("");
							userLoanVO.setOptinout_allowed(PretupsI.DEFAULT_YES);
							userLoanVO.setOptinout_by("");
							
							userLoanList.add(userLoanVO); // adding the last entry				
							
							}
							
							
						userLoanDAO=new UserLoanDAO();
						int  insertUpdateCount=userLoanDAO.insertUploadUserLoanThreshold(con,userLoanList);
						if(insertUpdateCount<=0)
						{
							_log.error(classMethodName," Not able to insert Vouchers for file="+fileName);
							EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","",""," The vouchers could not be inserted into the table for file="+fileName);
							throw new BTSLBaseException("UserLoanDataUploadProcess "," process ",PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
						}
		
						con.commit();
							
						//name of the file after moving
						String p_fileName1=fileName;
						String pathWithFileName1= filePath+File.separator+fileName;
						String path2=moveLocation;
					    boolean flag = false;
				        String[] fileExt = null;
				        try {
				            // added to concatenate current date and time with file name
				            // (manisha jain 11/01/08)
				            fileExt = p_fileName1.split("\\.");
				            p_fileName1 = (fileExt[0]).concat("_").concat(BTSLUtil.getFileNameStringFromDate(new Date()).concat(".").concat(fileExt[1]));
				            File fileRead = new File(pathWithFileName1);
				            File fileArchive = new File(path2);
				            if (!fileArchive.isDirectory()) {
				                fileArchive.mkdirs();
				            }
				            fileArchive = new File(path2 + File.separator + p_fileName1);
				            flag = fileRead.renameTo(fileArchive);

				            File tempFile = new File(path2 + File.separator + p_fileName1);
				            if (!tempFile.exists()) {
				                _log.debug(" moveFileToAnotherDirectory ", " Unable to Move File to backup location (" + path2 + ")+ Please Contact System admin ...............");
				                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[moveFileToAnotherDirectory]", "", "", "", " Unable to Move File to backup location (" + path2 + ")");
				                throw new BTSLBaseException(this, " moveFileToAnotherDirectory ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
				            }
				            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[moveFileToAnotherDirectory]", "", "", "", "Successfully moved the File " + p_fileName1 + " to backup location (" + path2 + ")");
				        } catch (BTSLBaseException be) {
				            _log.error("[moveFileToAnotherDirectory]", "BTSLBaseException =" + be.getMessage());
				            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherFileUploaderUtil[moveFileToAnotherDirectory]","","","","BTSLBaseException:"+be.getMessage());
				            throw be;
				        }// end of BTSLBaseException
				        catch (ParseException pe) {
				            _log.error("[moveFileToAnotherDirectory]", "ParseException =" + pe.getMessage());
				            throw pe;
				        }// end of ParseException
				        finally {
				            if (_log.isDebugEnabled()) {
				                _log.debug(" moveFileToAnotherDirectory ", " Exiting with flag=" + flag);
				                // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VoucherFileUploaderUtil[moveFileToAnotherDirectory]","","","","Successfully moved the File "+p_fileName1+" to backup location ("+path2+")");
				            }
						
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
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,classMethodName,"","",""," User Loan Upload Process Failed for file="+fileName+GETTINGEXCEPTION+be.getMessage());
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
						EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserLoanDataUploadProcess[process]","","","",VOUCHERUPLOADPROCESSFAILED+fileName+GETTINGEXCEPTION+e.getMessage());
						throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
					}	
											//validating for number of actual records inserted in voms_voucher.On the basis of records inserted, successful uploading of file is determined
						_log.debug(methodName, "User Loan Upload Process for file="+fileName+" Successfully Executed.Batch Successfully Generated." );
						if(_log.isDebugEnabled())
							_log.debug(" process","User Loan Upload Process for file="+fileName+" Successfully Executed.Batch Successfully Generated. ");
						EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,classMethodName,"","","","User Loan Upload Process for file="+fileName+" Successfully Executed.");
					
		        }
			}
			catch(BTSLBaseException be)
			{
				_log.errorTrace(methodName,be);
				_log.info(methodName, "User Loan Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
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
				_log.info(methodName, "User Loan Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
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
					LoanDataVO=null;
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
			final String classMethodName="UserLoanDataUploadProcess[loadConstantValues]";
			if(_log.isDebugEnabled())
				_log.debug("loadConstantValues "," Entered ");
	        try
		    {
				//this reads the number of records allowed in the file.
				if(_log.isDebugEnabled())
					_log.debug(methodName,": reading  maxNoRecordsAllowed  ");	
				try
				{
					maxNoRecordsAllowed = Integer.parseInt(Constants.getProperty("LOAN_MAX_FILE_LENGTH"));
				}
				catch(Exception e)
				{
					_log.errorTrace(methodName,e);
					_log.info(methodName, " Configuration Problem, Parameter LOAN_MAX_FILE_LENGTH not defined properly");
					_log.error(methodName,"Configuration Problem, Parameter LOAN_MAX_FILE_LENGTH not defined properly");
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,classMethodName,"","","","Configuration Problem, Parameter LOAN_MAX_FILE_LENGTH not defined properly");
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
	     		}
				
				//this is the path of the input voucher file.
				if(_log.isDebugEnabled())
					_log.debug(methodName,": reading  filePath ");
				filePath = BTSLUtil.NullToString(Constants.getProperty("USER_LOAN_FILE_PATH"));	
				
				//Checking whether the file path provided exist or not.If not, throw an exception
	            if(!(new File(filePath).exists()))
	            {
					_log.info(methodName, " Configuration Problem, Parameter USER_LOAN_FILE_PATH not defined properly");
					_log.error("loadConstantValues ","Configuration Problem, Parameter USER_LOAN_FILE_PATH not defined properly");
					EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,classMethodName,"","","","Configuration Problem, Parameter USER_LOAN_FILE_PATH not defined properly");
					throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
	            }
				
				//this is the location where the voucher file will be moved after the vouchers are uploaded
				if(_log.isDebugEnabled())
					_log.debug(methodName," reading moveLocation ");
				moveLocation=BTSLUtil.NullToString(Constants.getProperty("USER_LOAN_FILE_MOVE_PATH"));
				
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
				EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MINOR,className+methodName,"","","","No files exists at the following ("+filePath+") specified, please check the path");
	            throw be;
	        }//end of catch-BTSLBaseException
	        catch(Exception e)
	        {
	        	_log.errorTrace(methodName,e);
	            _log.error(methodName,"Exception e = "+e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,className+methodName,"","","","Exception:"+e.getMessage());
	            throw new BTSLBaseException(this,methodName,e.getMessage());
	        }//end of catch-Exception
	        finally
	        {
	            if(_log.isDebugEnabled()) 
	            	_log.debug(methodName,"Exited");
	        }//end of finally
	    }//end of loadFilesFromDir
	    
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
				parserObj.getFileLength(LoanDataVO);
				
				//called to validate the file, whole file at the time.
				LoanDataVO=parserObj.validateLoanDataFile(con);
				
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
				_log.error(className+methodName,EXCEPTION+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","",GETTINGEXCEPTION+e.getMessage());
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			}
			if(_log.isDebugEnabled())
				_log.debug(methodName,PretupsI.EXITED);
		}
		
		/**
		 * This method populates the Voucher Upload VO that will be given to Parser class
		 * @throws BTSLBaseException
		 */
		private void populateVoucherUploadVO() throws BTSLBaseException 
		{
			final String methodName = "populateVoucherUploadVO";
			if(_log.isDebugEnabled())
				_log.debug(methodName,PretupsI.ENTERED);
			try
			{
				LoanDataVO=new LoanDataVO();
				LoanDataVO.setProcessType(LoanDataVO._AUTOPROCESSTYPE);
				LoanDataVO.setFileName(fileName);
				LoanDataVO.setFilePath(filePath);
				LoanDataVO.setMaxNoOfRecordsAllowed(maxNoRecordsAllowed);
				LoanDataVO.setCurrentDate(currentDate);
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName,e);
				_log.error("UserLoanDataUploadProcess[populateVoucherUploadVO]",EXCEPTION+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","",GETTINGEXCEPTION+e.getMessage());
				throw new BTSLBaseException(className,methodName,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			}	
			finally
			{
				if(_log.isDebugEnabled())
					_log.debug(methodName,PretupsI.EXITED);
			}
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
			    parserObj=(UserLoanFileChecksI)Class.forName(pParserClassName).newInstance();
			}
			catch(Exception e)
			{
				_log.errorTrace(methodName,e);
				_log.error(methodName," Exception "+e.getMessage());
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,className+methodName,"","","","Exception: The object of the parser class could not be created dynamically");
				throw new BTSLBaseException(this,methodName,PretupsErrorCodesI.VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED);
			}//end of catch-Exception
			finally
			{
				if(_log.isDebugEnabled())
					_log.debug(methodName," Exiting with parserObj = "+parserObj);
			}//end of finally
	    }//end of getParserObj
	}


