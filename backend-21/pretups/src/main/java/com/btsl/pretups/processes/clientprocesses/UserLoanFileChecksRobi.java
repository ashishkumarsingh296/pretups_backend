package com.btsl.pretups.processes.clientprocesses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.UserLoanFileChecksI;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.LoanDataVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class UserLoanFileChecksRobi implements UserLoanFileChecksI {
	 private static Log log = LogFactory.getLog(UserLoanFileChecksRobi.class.getName());

	    // file format
	    private static final String LINE_HEADER_1 = "RETAILER MSISDN,LOAN AMOUNT,LOAN THRESHOLD,PRODUCT CODE";
	      
	    private static final String CLASS_NAME="UserLoanFileChecksRobi";
	    private int allowedNumberofErrors = 0;
	    private int numberofvaluesinheader = 0;
	    private int runningErrorCount = 0;
	    private long numberOfRecordScheduled = 0;
	    private String valueSeparator = null; // separater for the file
	    private String headerSeparator = null; // separater for header
	    private String filePath = null; // path of the file
	    private String fileName = null; // name of file
	   private int numberOfRecordsEntered = 0;
	    private int maxNoRecordsAllowed = 0;
	    private LoanDataVO loanDataVO = null;
	    private ArrayList DataArr = null;
	   private String productCode = null; // product ID for the vouchers
	    private String networkID=null;
	    private String productCodeStr = null;
	    private ArrayList _fileDataArr = null;
	    
	@Override
	 /**
     * The method loads the values for the various properties to be used later
     * during the parsing and validation of the file
     * 
     * @return void
     * @throws BTSLBaseException
     */
    
	
	public void loadConstantValues() throws BTSLBaseException {
        final String methodName = "loadConstantValues";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered the loadContantsValues.....");
        }
        try {

        	networkID= SystemPreferences.DEFAULT_COUNTRY;
        	productCodeStr=SystemPreferences.DEFAULT_PRODUCT;
        	
        	
            try {
                allowedNumberofErrors = Integer.parseInt(Constants.getProperty("USER_LOAN_TOTAL_ALLOWED_ERRORS"));
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName, "  USER_LOAN_TOTAL_ALLOWED_ERRORS " + allowedNumberofErrors);
                }

            } catch (Exception e) {
                log.errorTrace(methodName, e);
                allowedNumberofErrors = 0;
                log.info(methodName, " Total number of error (Entry USER_LOAN_TOTAL_ALLOWED_ERRORS) not found in Constants . Thus taking default values as 0");
            }
            try {
                numberofvaluesinheader = Integer.parseInt(Constants.getProperty("NUMBER_OF_VALUES_IN_HEADER"));
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName, "  NUMBER_OF_VALUES_IN_HEADER " + numberofvaluesinheader);
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, " Invalid value for NUMBER_OF_VALUES_IN_HEADER  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for NUMBER_OF_VALUES_IN_HEADER in Constant File while voucher upload process");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                valueSeparator = Constants.getProperty("USER_LOAN_FILE_SEPARATOR");
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName,"   USER_LOAN_FILE_SEPARATOR " + valueSeparator);
                }
                if (BTSLUtil.isNullString(valueSeparator)) {
                    log.error(methodName, " Invalid value for USER_LOAN_FILE_SEPARATOR  in Constant File ");
                    valueSeparator = " ";
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, " Invalid value for USER_LOAN_FILE_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for USER_LOAN_FILE_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                headerSeparator = Constants.getProperty("USER_LOAN_FILE_HEADER_SEPARATOR");
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName,"   USER_LOAN_FILE_HEADER_SEPARATOR " + headerSeparator);
                }
                if (BTSLUtil.isNullString(headerSeparator)) {
                    log.error(methodName, " Invalid value for USER_LOAN_FILE_HEADER_SEPARATOR  in Constant File ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for USER_LOAN_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                    throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, " Invalid value for USER_LOAN_FILE_HEADER_SEPARATOR  in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for USER_LOAN_FILE_HEADER_SEPARATOR in Constant File while voucher upload process");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            try {
                numberOfRecordScheduled = Long.parseLong(Constants.getProperty("USER_LOAN_MAX_FILE_LENGTH"));
                if (log.isDebugEnabled()) {
                    log.debug(CLASS_NAME+" "+methodName,"    NUMBER_OF_RECORDS_SCHEDULE " + numberOfRecordScheduled);
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
                log.error(methodName, "Invalid value for USER_LOAN_MAX_FILE_LENGTH in Constant File ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Invalid value for REC_ALLOWED_FOR_SCRIPT in Constant File ");
                throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
        }// end of try block
        catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, " BTSLBaseException be = " + be);
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, " Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", "Exception while loading the constant values from the Constants.prop file " + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME+" "+methodName," ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch-Exception
        finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "   Exiting with _allowedNumberofErrors=" + allowedNumberofErrors + " _valueSeparator=" + valueSeparator);
            }
        }// end of finally
    }

	@Override
	public LoanDataVO validateLoanDataFile(Connection p_con) throws BTSLBaseException, Exception {
        final String methodName = "validateLoanDataFile";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered to validate the file _fileName=" + fileName);
        }
         int _runningFileRecordCount = 0;
         String fileData = null;
         String[] fileRecord = null;

        DataArr = new ArrayList();
        try {
        	for (int loop = 0; loop < _fileDataArr.size(); loop++) {
        		fileData = null;
        		fileData = (String) _fileDataArr.get(loop);
        		fileRecord = fileData.split(valueSeparator);

        		_runningFileRecordCount++;// keeps record of number of records
        		// in file

        		if (fileRecord.length == 1) {
        			log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + " No Data Found");
        			runningErrorCount++;
        			continue;
        		}
        		if (fileRecord.length < 4) {
        			log.error("validateVoucherFile", " Record Number = " + _runningFileRecordCount + "Number of values in the record are not currect");
        			runningErrorCount++;
        			continue;
        		}

        		// checks if the number of error encontered while parsing of
        		// file does not

        		String retMsisdnStr  = fileRecord[0];
        		long retMsisdn = Long.parseLong(retMsisdnStr);

        		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        		ChannelUserVO channelUserVO = channelUserDAO.loadChannelUserDetails(p_con, retMsisdnStr);

        		if (channelUserVO == null) {
        			runningErrorCount++;
        			log.error(methodName, " Record Number = " + _runningFileRecordCount + " Retailer Msisdn is not Valid");
        			continue;
        		}

        		String loanAmountStr  = fileRecord[1];
        		try {
        			long loanAmount = Long.parseLong(loanAmountStr);
        		}
        		catch (Exception e) {
        			runningErrorCount++;
        			log.error(methodName, " Record Number = " + _runningFileRecordCount + " Loan Amount is not Valid");
        			continue;
        		}

        		String loanThresholdStr  = fileRecord[2];
        		try {
        			long loanThreshold = Long.parseLong(loanThresholdStr);
        		}
        		catch (Exception e) {
        			runningErrorCount++;
        			log.error(methodName, " Record Number = " + _runningFileRecordCount + " Loan Threshold  is not Valid");
        			continue;
        		}



        		if(fileRecord.length > 3) {
        			productCodeStr  = fileRecord[3];

        			NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        			List productList = networkProductDAO.loadProductList(p_con,channelUserVO.getNetworkID(),PretupsI.C2S_MODULE);

        			ListValueVO listVO= null;
        			boolean productMatched =false;

        			String productCodeValue= null;
        			for(int i=0; i< productList.size();i++) {
        				listVO= (ListValueVO)productList.get(i);
        				productCodeValue =listVO.getValue();

        				if(productCodeValue.equals(productCodeStr)) {
        					productMatched = true;
        					break;
        				}
        			}

        			if(!productMatched) {

        				runningErrorCount++;
        				log.error(methodName, " Record Number = " + _runningFileRecordCount + " Product details  is not Valid");
        				continue;
        			}

        		}

        		populateValuesInList(channelUserVO.getUserID(), loanAmountStr, loanThresholdStr, productCodeStr);
        		
        	 	// check running error count is less than allowed error count
            	if (runningErrorCount > allowedNumberofErrors) {
            		log.error(methodName, " Total Number of error (" + runningErrorCount + ") in the File (" + fileName + ") exceeds the user specified error number= " + allowedNumberofErrors);
            		EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME+methodName, "", "", "", "The number of error (" + runningErrorCount + ") in the file exceed the user specified value (" + allowedNumberofErrors + ")");
            		throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT, "uploadvoucher");// "Total number of error in the file exceed the specified number of error"
            	}// end of if block
           
        	}

        	numberOfRecordsEntered = _runningFileRecordCount;

        		// check number of records entered are less than maximum number
        		// of records allowed
        		if (maxNoRecordsAllowed < numberOfRecordsEntered) {
        			log.error("readFileHeader", "Total number of records entered (" + numberOfRecordsEntered + ") should be less than the allowed records in file (" + maxNoRecordsAllowed + ")");
        			EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessor[readFileHeader]", "", "", "", "Total number of records entered (" + numberOfRecordsEntered + ") should be less than the allowed records in voucher file (" + maxNoRecordsAllowed + ")");
        			throw new BTSLBaseException(CLASS_NAME, "readFileHeader", PretupsErrorCodesI.VOUCHER_ERROR_NUMBER_REC_MORE_THAN_ALLWD, "uploadvoucher");// "The voucher file does not exists at the location specified"
        		}
        	
        	// counts the number of actual valid records in the file
        	loanDataVO.setNoOfRecordsInFile(numberOfRecordsEntered);
        	loanDataVO.setProductCode(productCode);
        	loanDataVO.setNetwrkID(networkID);

        	loanDataVO.setActualNoOfRecords(DataArr.size());
        	loanDataVO.setLoanArrayList(DataArr);
        } catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, " : BTSLBaseException " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+methodName, "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(" validateVoucherFile", " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+methodName, "", "", "", e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting ");
            }
        }
        return loanDataVO;
    }

	@Override
	public void getFileLength(LoanDataVO p_userLoanVO) throws BTSLBaseException, Exception {
        final String methodName = "getFileLength";
        if (log.isDebugEnabled()) {
            log.debug(methodName, " Entered with p_userLoanVO=" + p_userLoanVO);
        }
        _fileDataArr = new ArrayList();
        BufferedReader inFile = null;
        String fileData = null;
        int fileLineCount = 0;
        int lineCount = 0;
        
        try {
            getValuesFromVO(p_userLoanVO);
            String srcFile;
            // creates the actual path of the file
            if (BTSLUtil.isNullString(filePath)) {
                srcFile = fileName;
            } else {
                srcFile = filePath + File.separator+ fileName;
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Starting processing to get the number of records for source File Path = " + srcFile + " File Name" + fileName);
            }
            
            // creates a new bufferedreader to read the Voucher Upload file
            inFile = new BufferedReader(new FileReader(srcFile));

            // To parse Header in file this header is as the first line of the
            // data file
            fileData = inFile.readLine();
      
            while (inFile.ready()) {
                fileData = null;
                fileData = inFile.readLine();// reads line by line

                // this is used to check if the line is blank ie the record is
                // blank
                if (BTSLUtil.isNullString(fileData) || fileData == null) {
                    log.error("getFileLength", " Record Number = " + lineCount + " No Data Found");
                    continue;
                }
                ++fileLineCount;
                _fileDataArr.add(fileData);
            }
            p_userLoanVO.setNoOfRecordsInFile(fileLineCount);

             
            
          
        }// end of try block
        catch (BTSLBaseException be) {
            log.errorTrace(methodName, be);
            log.error(methodName, " : BTSLBaseException " + be);
            throw be;
        } catch (IOException io) {
            log.errorTrace(methodName, io);
            log.error(methodName, "The file (" + fileName + ")could not be read properly to get the number of records");
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME+methodName, "", "", "", "The file (" + fileName + ")could not be read properly to get the number of records");
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of IOException
        catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, " Exception := " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+methodName, "", "", "", e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_TOTAL_ERROR_COUNT);
        }// end of Exception
        finally {
           log.error(methodName, " Exiting getFileLength()");
        }// end of finally
    }
	
	 /**
     * This method will get the values from voucher upload VO passed from
     * controlling class to the global variables
     * 
     * @param p_voucherUploadVO
     * @throws BTSLBaseException
     */
    private void getValuesFromVO(LoanDataVO p_loanDataUploadVO) throws BTSLBaseException {
        final String methodName = "getValuesFromVO";
        if (log.isDebugEnabled()) {
            log.debug(" getValuesFromVO", " Entered with  p_loanDataUploadVO=" + p_loanDataUploadVO);
        }
        try {
            fileName = p_loanDataUploadVO.getFileName();
            filePath = p_loanDataUploadVO.getFilePath();
            numberOfRecordsEntered = p_loanDataUploadVO.getNoOfRecordsInFile();
            maxNoRecordsAllowed = p_loanDataUploadVO.getMaxNoOfRecordsAllowed();
            loanDataVO = p_loanDataUploadVO;
            
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(CLASS_NAME+"["+methodName+"]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+methodName, "", "", "", " Getting Exception=" + e.getMessage());
            throw new BTSLBaseException(CLASS_NAME, " getValuesFromVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(" getValuesFromVO", " Exiting");
            }
        }
    }// end of getValuesFromVO


    /**
     * This method is used to parse the header
     * and it also validate the header values
     * 
     * @param fileData
     */
    private int readFileHeader(String fileData) throws BTSLBaseException {
        final String METHOD_NAME = "readFileHeader";
        if (log.isDebugEnabled()) {
            log.debug(" readFileHeader", " Entered to validate the header of _fileName=" + fileName);
        }
        try {

            String[] headerData = fileData.split(headerSeparator);
            String[] headerArr =LINE_HEADER_1.split(headerSeparator);
            
            if (!(headerData.length == headerArr.length)) {
                log.error(METHOD_NAME, " Total Number of values in the header is not equal to =" + headerArr.length);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, this.getClass()+METHOD_NAME, "", "", "", "Total Number of values in the header is not equal to =" + numberofvaluesinheader);
                throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.VOUCHER_FILE_HEADER_ERROR);
            }
           
          
        } catch (BTSLBaseException be) {
            log.errorTrace(METHOD_NAME, be);
            log.error(METHOD_NAME, " Exception = " + be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,this.getClass()+METHOD_NAME, "", "", "", be.getMessage());
            throw be;
        } catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
            log.error(METHOD_NAME, " Exception = " + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, this.getClass()+METHOD_NAME, "", "", "", e.getMessage());
            throw new BTSLBaseException(this,METHOD_NAME, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        } finally {
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, " Exiting " );
            }
        }// end of Exception

        return 1;

    }
    		
    /* This method populates the Voms Voucher VO that will be inserted in
    * database
    * 
    * @param p_serialNo
    * @param p_pin
    * @param p_productID
    * @param expiryDate
    * @throws BTSLBaseException
    */
   private void populateValuesInList(String p_retailerUserID, String p_loanAmount, String p_loanThreshold , String p_productID) throws BTSLBaseException {
       final String METHOD_NAME = "populateValuesInList";
       if (log.isDebugEnabled()) {
           log.debug(METHOD_NAME, " Entered with p_retailerMsisdn=" + p_retailerUserID + " p_loanAmount=" + p_loanAmount  + " p_loanThreshold=" + p_loanThreshold + " p_productID=" + p_productID);
       }
       try {
           LoanDataVO LoanDataVO = new LoanDataVO();
           LoanDataVO.setRetailerUserID(p_retailerUserID);
           LoanDataVO.setLoanThreshold(p_loanThreshold);
           LoanDataVO.setLoanAmount(p_loanAmount);
           LoanDataVO.setProductCode(p_productID);
           DataArr.add(LoanDataVO);
       } catch (Exception e) {
           log.errorTrace(METHOD_NAME, e);
           log.error(CLASS_NAME+METHOD_NAME, PretupsI.EXCEPTION + e.getMessage());
           EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+METHOD_NAME, "", "", "", " Getting Exception=" + e.getMessage());
           throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
       } finally {
           if (log.isDebugEnabled()) {
               log.debug(METHOD_NAME, PretupsI.EXITED);
           }
       }
   } // end of populateValuesInVoucherList

    
	
	
    
}
