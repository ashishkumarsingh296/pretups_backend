package com.btsl.voms.vomsprocesses.businesslogic;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomslogging.VomsBatchInfoLog;
import com.btsl.voms.vomslogging.VomsVoucherChangeStatusLog;
import com.btsl.voms.vomsprocesses.util.VoucherFileChecksI;
import com.btsl.voms.vomsprocesses.util.VoucherFileUploaderUtil;
import com.btsl.voms.vomsprocesses.util.VoucherUploadVO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;


/**
 * @author akanksha
 * This class is responsible to provide following functionalities.
 *  1.Reading particular directory for voucher files.
 *  2.Call the parser class for the parsing and validating of the input file
 * 3.Use the VomsVoucherDAO class for uploading the records in the file to the database
 */
public class VoucherFileProcessRM {

	
	    private static final Log _log = LogFactory.getLog(VoucherFileProcessRM.class.getName());

	    private String filePath; // File path name
	    private ArrayList fileList; // Contain all the file object thats name start
	                                 // with file prefix.

	    private File inputFile; // File object for input file
	    private File destFile; // Destination File object
	    private String moveLocation; // Input file is move to this location After
	                                  // successfull proccessing
		private String fileExt="csv";			//Files are picked up only this extention from the specified directory
	                                           // extention
	    // from the specified directory
	    private String fileName = null;

	    private static VoucherFileChecksI parserObj = null;
	    private ProcessStatusDAO processStatusDAO = null;
	    private ProcessStatusVO processStatusVO = null;
	    private static Date currentDate = null;
	    private VoucherUploadVO voucherUploadVO = null;
	    private static int maxNoRecordsAllowed = 0;
	    private long numberOfRecords = 0;
	    private String profileID = null;
	    private String productId = null;

	    private static boolean directVoucherEnable = false;
	    private static final String CLASS_NAME="VoucherFileProcessRM";
	    /**
	     * Constructor
	     */
	    public VoucherFileProcessRM() {
	        processStatusDAO = new ProcessStatusDAO();
	        currentDate = new Date();
	    }

	    /**
	     * Main starting point for the process
	     * 
	     * @param args
	     */
	    public static void main(String[] args) {
	        final String methodName = "main";
	        try {
	            int argSize = args.length;

	            if (argSize != 2) {
	                _log.info(methodName, "Usage :"+ CLASS_NAME +"[Constants file] [ProcessLogConfig file][product id] [expiryduration]");
	                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, CLASS_NAME+"[main]", "", "", "", "Improper usage. Usage : VoucherFileProcessRM [Constants file] [ProcessLogConfig file][loginID][password][profile][fileName][total records in file]");
	                throw new BTSLBaseException(CLASS_NAME, " main ", PretupsErrorCodesI.VOUCHER_MISSING_INITIAL_FILES);
	            }
	            VoucherFileUploaderUtil.loadCachesAndLogFiles(args[0], args[1]);
	            new VoucherFileProcessRM().process();
	        } catch (BTSLBaseException be) {
	            _log.errorTrace(methodName, be);
	        } catch (Exception e) {

	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileProcessRM: main", "", "", "", "Exiting the exception of main");
	            _log.errorTrace(methodName, e);
	        }// end of outer Exception
	        finally {
	            ConfigServlet.destroyProcessCache();
	        }
	    }

	    /**
	     * Method that handle the complete flow of the process
	     * 
	     * @param pargs
	     * @throws BTSLBaseException
	     */
	    private void process() throws BTSLBaseException {
	        final String methodName = "process";
	        Connection con = null;
	        ProcessBL processBL = new ProcessBL();
	        boolean processStatusOK = false;
	        ArrayList batchList = null;
	        double startTime=0;
	        try {
	            
	        	startTime=System.currentTimeMillis();
	            con = OracleUtil.getSingleConnection();

	            if (con == null) {
	                _log.error(methodName, ": Could not connect to database. Please make sure that database server is up..............");
	                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", "Could not connect to Database");
	                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_CONN_NULL);
	            }

	            processStatusVO = processBL.checkProcessUnderProcess(con, ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID);

	            if (!(processStatusVO != null && processStatusVO.isStatusOkBool())) {
	                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
	            }

	            processStatusOK = processStatusVO.isStatusOkBool();

	            con.commit();
	            loadConstantValues();

	            loadFilesFromDir();

	            VomsBatchVO vomsBatchVO = null;

	            VomsVoucherDAO vomsVoucherDAO = null;

	            VomsBatchVO enableBatchVO = null;

	            getParserObj(Constants.getProperty("VOUCHER_PARSER_CLASS"));
	            VomsProductVO vomsProductVO = null;
	            ArrayList productList = null;

	            productList = new VomsProductDAO().loadProductDetailsList(con, "'" + VOMSI.VOMS_STATUS_ACTIVE + "'", false, null, null);

	            for (int l = 0, size = fileList.size(); l < size; l++) {
	                voucherUploadVO = null;
	                numberOfRecords = 0;
	                vomsBatchVO = null;
	                inputFile = null;
	                fileName = null;
	                // Getting the file object
	                inputFile = (File) fileList.get(l);
	                fileName = inputFile.getName();

	                try {
	                    validateInputFile();
	                    if(voucherUploadVO.getNetwrkID().isEmpty()){
	                    	_log.error(CLASS_NAME+"["+methodName+"]", " No network code provided in file=" + fileName);
	                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", " No network code provided in file=" + fileName);
	                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
	                    }
	                    if (voucherUploadVO.getVoucherArrayList() == null || voucherUploadVO.getVoucherArrayList().isEmpty()) {
	                        _log.error(CLASS_NAME+"["+methodName+"]", " No voucher for adding in file=" + fileName);
	                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", " No vouchers for adding in file=" + fileName);
	                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_NO_RECORDS_ERROR);
	                    }

	                    numberOfRecords = voucherUploadVO.getNoOfRecordsInFile();

	                    profileID = voucherUploadVO.getPrfileId();

	                    vomsProductVO = extractProdID(methodName,
								vomsProductVO, productList);
	                    
	                    vomsBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_GENERATED, false, null);
	                    vomsBatchVO.setMrp(Long.toString(PretupsBL.getSystemAmount(vomsProductVO.getMrpStr())));
	                    vomsBatchVO.setValidity(Integer.parseInt(vomsProductVO.getValidityStr()));
	                    vomsBatchVO.setTalktime(Integer.parseInt(Long.toString(PretupsBL.getSystemAmount(vomsProductVO.getTalkTimeStr()))));
	                    vomsBatchVO.setProductID(vomsProductVO.getProductID());
	                    vomsBatchVO.setExpiryPeriod(Integer.parseInt(Long.toString(vomsProductVO.getExpiryPeriod())));
	                    vomsBatchVO.set_NetworkCode(voucherUploadVO.getNetwrkID());
	                    vomsBatchVO.setModifiedBy(TypesI.SYSTEM_USER);
	                    vomsBatchVO.setOneTimeUsage(PretupsI.NO);
	                    vomsBatchVO.setModifiedOn(currentDate);
	                    vomsBatchVO.setCreatedOn(currentDate);
	                    //vomsBatchVO.setExpiryDate(BTSLUtil.addDaysInUtilDate(currentDate, (int)vomsProductVO.getExpiryPeriod()));
	                    vomsBatchVO.setExpiryDate(BTSLUtil.addDaysInUtilDate(currentDate, BTSLUtil.parseLongToInt(vomsProductVO.getExpiryPeriod())));
	                    
	                    batchList = new ArrayList();
	                    batchList.add(vomsBatchVO);

	                    int recordCount = 0;
	                    recordCount = new VomsBatchesDAO().addBatch(con, batchList);
	                    if (recordCount <= 0) {
	                        _log.error(CLASS_NAME+"["+methodName+"]", " Not able to insert batches for file=" + fileName);
	                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", " The batch entry could not be made into the table for file=" + fileName);
	                        throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	                    }

	                    // For Direct enabling of Vouchers
	                    VomsVoucherVO voucherVO = null;
	                    ArrayList voucherLogList = new ArrayList();
	                    vomsBatchVO.setStatus(VOMSI.VOUCHER_NEW);
	                    if (directVoucherEnable) {
	                        enableBatchVO = prepareVomsBatchesVO(VOMSI.BATCH_ENABLED, true, vomsBatchVO);
	                        batchList = new ArrayList();
	                        batchList.add(enableBatchVO);
	                        recordCount = new VomsBatchesDAO().addBatch(con, batchList);
	                        if (recordCount <= 0) {
	                            _log.error(CLASS_NAME+"["+methodName+"]", " Not able to insert batches ");
	                            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[process]", "", "", "", " The batch entry could not be made into the table");
	                            throw new BTSLBaseException(CLASS_NAME ,methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	                        }
	                        ArrayList voucherList = voucherUploadVO.getVoucherArrayList();
	                        VomsVoucherVO vomsVoucherVO = null;
	                            vomsVoucherVO = (VomsVoucherVO) voucherList.get(0);
	                            vomsVoucherVO.setEnableBatchNo(enableBatchVO.getBatchNo());
	                            vomsVoucherVO.setStatus(VOMSI.VOUCHER_ENABLE);
	                            vomsVoucherVO.setCurrentStatus(VOMSI.VOUCHER_ENABLE);
	                            vomsVoucherVO.setPreviousStatus(VOMSI.VOUCHER_NEW);
	                       
	                        voucherVO = new VomsVoucherVO();
	                        voucherVO.setEnableBatchNo(enableBatchVO.getBatchNo());
	                        voucherVO.setSerialNo(enableBatchVO.getFromSerialNo());
	                        voucherVO.setToSerialNo(enableBatchVO.getToSerialNo());
	                        voucherVO.setPreviousStatus(VOMSI.VOUCHER_NEW);
	                        voucherVO.setPrevStatusModifiedBy(enableBatchVO.getModifiedBy());
	                        voucherVO.setPrevStatusModifiedOn(BTSLUtil.getDateStringFromDate(enableBatchVO.getModifiedOn()));
	                        voucherVO.setVoucherStatus(VOMSI.VOUCHER_ENABLE);
	                        voucherVO.setStatusChangeSource(VOMSI.VOUCHER_ENABLE);
	                        voucherVO.setMRP(Double.parseDouble(enableBatchVO.getMrp()));
	                        voucherVO.setValidity(enableBatchVO.getValidity());
	                        voucherVO.setTalkTime(Long.parseLong(Integer.toString(enableBatchVO.getTalktime())));

	                        voucherVO.setExpiryDateStr("");
	                        voucherVO.setLastErrorMessage(BTSLUtil.NullToString(enableBatchVO.getMessage()));
	                        voucherVO.setProcess("1");
	                        voucherVO.setProductionLocationCode(enableBatchVO.getLocationCode());
	                        voucherLogList.add(voucherVO); // adding the last entry
	                    }

	                    vomsVoucherDAO = new VomsVoucherDAO();
	                    recordCount = vomsVoucherDAO.insertVouchers_new(con, vomsBatchVO, voucherUploadVO.getVoucherArrayList(), directVoucherEnable);
	                    if (recordCount <= 0) {
	                        _log.error(CLASS_NAME+"["+methodName+"]", " Not able to insert Vouchers for file=" + fileName);
	                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", " The vouchers could not be inserted into the table for file=" + fileName);
	                        throw new BTSLBaseException(CLASS_NAME,methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	                    }

	                    recordCount = 0;
	                    recordCount = vomsVoucherDAO.updateSummaryTable(con, vomsBatchVO, directVoucherEnable);
	                    if (recordCount <= 0) {
	                        _log.error(CLASS_NAME+"["+methodName+"]", " Not able to update Summary tables for file=" + fileName);
	                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", " The batch summary table could not be updated for file=" + fileName);
	                        throw new BTSLBaseException(CLASS_NAME,methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	                    }

	                    con.commit();

	                    // name of the file after moving
	                    VoucherFileUploaderUtil.moveFileToAnotherDirectory(fileName, filePath + File.separator + fileName, moveLocation);

	                    VomsBatchInfoLog.modifyBatchLog(vomsBatchVO);

	                    if (directVoucherEnable) {
	                        VomsVoucherChangeStatusLog.log(voucherLogList);
	                        VomsBatchInfoLog.modifyBatchLog(enableBatchVO);
	                    }
	                    sendMessage();
	                } catch (BTSLBaseException be) {
	                    _log.errorTrace(methodName, be);
	                    _log.debug(methodName, "Voucher Upload Process Failed for file=:" + fileName + " .Check the log file or OAM Screen or Event log for exact errors ");

	                    if (con != null) {
	                            con.rollback();
	                    }

	                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", " Voucher Upload Process Failed for file=" + fileName + " Getting Exception:=" + be.getMessage());
	                    continue;
	                } catch (Exception e) {
	                    _log.errorTrace(methodName, e);
	                    _log.debug(methodName, "Voucher Upload Process Failed for file=" + fileName + " .Check the log file or OAM Screen or Event log for exact errors ");
	                    if (con != null) {
	                            con.rollback();
	                        }

	                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", "Voucher Upload Process Failed for file=" + fileName + " Getting Exception=" + e.getMessage());
	                    throw new BTSLBaseException(CLASS_NAME,methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	                }
	                ifDirectEnable(methodName, vomsBatchVO, enableBatchVO);
	            }
	        } catch (BTSLBaseException be) {
	            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
	            if (con != null) {
	                try {
	                    con.rollback();
	                } catch (SQLException e1) {
	                    _log.errorTrace(methodName, e1);
	                }
	            }
	            throw be;
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.info(methodName, "Voucher Upload Process Failed .Check the log file or OAM Screen or Event log for exact errors ");
	            try {
	            	if(con!=null){
	                con.rollback();
	            	}
	            } catch (SQLException e1) {
	                _log.errorTrace(methodName, e1);
	            }
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", " Getting Exception=" + e.getMessage());
	            throw new BTSLBaseException(CLASS_NAME,methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	        } finally {
	            try {
	                if (processStatusOK) {
	                    Date date = new Date();
	                    processStatusVO.setStartDate(processStatusVO.getStartDate());
	                    processStatusVO.setExecutedOn(date);
	                    processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
	                    processStatusVO.setExecutedUpto(currentDate);
	                    int successU = processStatusDAO.updateProcessDetail(con, processStatusVO);
	                    _log.debug(methodName, "Total time in miliseconds @@@@@@@ "+(System.currentTimeMillis()-startTime));
	                    if (successU > 0) {
	                        con.commit();
	                    } else {
	                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "Error while updating the process status after completing the process");
	                
	                    }
	                }
	            } catch (BTSLBaseException be) {
	                _log.errorTrace(methodName, be);
	                _log.error(methodName, "BTSLBase Exception be= " + be.getMessage());
	                if (con != null) {
	                    try {
	                        con.rollback();
	                    } catch (Exception e1) {
	                        _log.errorTrace(methodName, e1);
	                    }
	                }
	            }// end of catch-BTSLBaseException
	            catch (Exception e) {
	                _log.errorTrace(methodName, e);
	                _log.error("process", "Exception e= " + e.getMessage());
	                if (con != null) {
	                    try {
	                        con.rollback();
	                    } catch (Exception e1) {
	                        _log.errorTrace(methodName, e1);
	                    }
	                }
	                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "processStatusVO.getProcessID()" + ProcessI.VOUCHER_FILE_UPLOAD_PROCCESSID, "", "", "BaseException:" + e.getMessage());
	            }// end of catch-Exception
	            finally {
	                if (con != null) {
	                    try {
	                        con.close();
	                    } catch (Exception e1) {
	                        _log.errorTrace(methodName, e1);
	                    }
	                }
	                fileName = null;
	                parserObj = null;
	                processStatusDAO = null;
	                processStatusVO = null;
	                voucherUploadVO = null;
	                maxNoRecordsAllowed = 0;
	                numberOfRecords = 0;
	                profileID = null;
	            }
	            LogFactory.printLog(methodName, " Exiting ", _log);
	        }
	    }

		private VomsProductVO extractProdID(final String methodName,
				VomsProductVO vomsProductVO, ArrayList productList)
				throws BTSLBaseException {
			if (productList != null) {
			    Iterator profileList = productList.iterator();
			    while (profileList.hasNext()) {
			        vomsProductVO = (VomsProductVO) profileList.next();
			        String key =vomsProductVO.getProductID();
			        if (key.equalsIgnoreCase(profileID)) {
			        	productId = vomsProductVO.getProductID();
			            break;
			        }
			    }
			}
			if(productId==null){
				_log.error(CLASS_NAME+"["+methodName+"]", " The profile ID does not exists in system for file "+fileName);
			    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[process]", "", "", "", " The profile ID provided in file does not exists in system");
			    throw new BTSLBaseException("VoucherFileProcessor ", " process ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
			}
			return vomsProductVO;
		}

		private void ifDirectEnable(final String methodName,
				VomsBatchVO vomsBatchVO, VomsBatchVO enableBatchVO) {
			if (directVoucherEnable) {
			    if (_log.isDebugEnabled()) {
			        _log.debug(" process", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());
			    }
			    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileProcessor[process]", "", "", "", "Voucher Upload Process Successfully Executed.Batch Successfully Enabled. Your Batch number for the process is = " + enableBatchVO.getBatchNo());

			} else {
			    _log.debug(methodName, "Voucher Upload Process for file:=" + fileName + " Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
			    if (_log.isDebugEnabled()) {
			        _log.debug(" process", "Voucher Upload Process for file=" + fileName + " Successfully Executed.Batch Successfully Generated. Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
			    }
			    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, CLASS_NAME+"["+methodName+"]", "", "", "", "Voucher Upload Process for file=" + fileName + " Successfully Executed.Batch Successfully Generated for file=" + fileName + " . Your Batch number for the process is = " + vomsBatchVO.getBatchNo());
			}
		}

	    /**
	     * Method to load constant values for the process
	     * 
	     * @throws BTSLBaseException
	     */
	    public void loadConstantValues() throws BTSLBaseException {
	        final String methodName = "loadConstantValues";
	        LogFactory.printLog(methodName, " Entered  ", _log);
	        try {
	            // this reads the number of records allowed in the file.
	            LogFactory.printLog(methodName, " : reading  _maxNoRecordsAllowed   ", _log);
	            loadConsProperty(methodName);

	            // this is the path of the input voucher file.
	            LogFactory.printLog(methodName, ": reading  _filePath ", _log);
	            filePath = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_PATH"));

	            // Checking whether the file path provided exist or not.If not,
	            // throw an exception
	            if (!(new File(filePath).exists())) {
	                _log.info(methodName, " Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
	                _log.error(methodName, "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
	                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Configuration Problem, Parameter VOMS_VOUCHER_FILE_PATH not defined properly");
	                throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_ERROR_DIR_NOT_EXIST);
	            }

	            // this is the location where the voucher file will be moved after
	            // the vouchers are uploaded
	            LogFactory.printLog(methodName, " reading _moveLocation  ", _log);
	            moveLocation = BTSLUtil.NullToString(Constants.getProperty("VOMS_VOUCHER_FILE_MOVE_PATH"));

	            // Destination location where the input file will be moved after
	            // successful reading.
	            destFile = new File(moveLocation);

	            // Checking the destination location for the existence. If it does
	            // not exist stop the proccess.
	            if (!destFile.exists()) {
	                LogFactory.printLog(methodName, "Destination Location checking= " + moveLocation + " does not exist", _log);
	                boolean fileCreation = destFile.mkdirs();
	                ifFileCreation(methodName, fileCreation);
	            }
	            // Added for Direct Voucher Enabling
	            directVoucherEnable = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DIRECT_VOUCHER_ENABLE))).booleanValue();

	        }// end of try block
	        catch (BTSLBaseException be) {
	            _log.errorTrace(methodName, be);
	            _log.error(methodName, "BTSLBaseException be = " + be.getMessage());
	            throw be;
	        }// end of catch-BTSLBaseException
	        catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.error(methodName, "Exception e=" + e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", "Exception while loading the constants from the Constants.prop file");
	            throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	        }// end of catch-Exception
	        finally {
	            LogFactory.printLog(methodName,  "Exiting: _filePath = " + filePath + " _moveLocation= " + moveLocation, _log);
	        }// end of finally
	    }// end of loadConstantValues

		private void ifFileCreation(final String methodName,
				boolean fileCreation) throws BTSLBaseException {
			if (fileCreation) {
			    if (_log.isDebugEnabled()) {
			        _log.debug(methodName, " New Location = " + destFile + "has been created successfully");
			    } else {
			        _log.debug(methodName, " Configuration Problem, Could not create the backup directory at the specified location " + moveLocation);
			        _log.error("loadConstantsValues ", " Configuration Problem, Could not create the backup directory at the specified location " + moveLocation);
			        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileProcessRM[loadConstantValues]", "", "", "", "Configuration Problem, Could not create the backup directory at the specified location " + moveLocation);
			        throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
			    }
			}
		}

		private void loadConsProperty(final String methodName)
				throws BTSLBaseException {
			try {
			    maxNoRecordsAllowed = Integer.parseInt(Constants.getProperty("VOMS_MAX_FILE_LENGTH"));
			} catch (Exception e) {
			    _log.errorTrace(methodName, e);
			    _log.info(methodName, " Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
			    _log.error(methodName, "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
			    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Configuration Problem, Parameter VOMS_MAX_FILE_LENGTH not defined properly");
			    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
			}
		}

	    /**
	     * This method is used to loadAll the files with specified prefix.
	     * All these file objects are stored in ArrayList.
	     * 
	     * @throws BTSLBaseException
	     */
	    public void loadFilesFromDir() throws BTSLBaseException {
	        final String methodName = "loadFilesFromDir";
	        LogFactory.printLog(methodName, "Entered", _log);
	        File directory = null;
	        try {
	            directory = new File(filePath);
	            // Check if the directory contains any files
	            if (directory.list() == null) {
	                throw new BTSLBaseException(methodName, "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
	            }

	            // This filter is used to filter all the files that start with
	            FilenameFilter filter = new FilenameFilter() {
	                @Override
	                public boolean accept(File dir, String name) {
	                    return name.endsWith(fileExt);
	                }
	            };

	            // List of files that start with the file prefix with specified
	            // extantion.
	            File[] tempFileArray = directory.listFiles(filter);
	            fileList = new ArrayList();

	            // Storing all the files(not dir)to array list
	            for (int l = 0, size = tempFileArray.length; l < size; l++) {
	                if (tempFileArray[l].isFile()) {
	                    fileList.add(tempFileArray[l]);
	                    LogFactory.printLog(methodName, "File = " + tempFileArray[l] + " is added to fileList", _log);
	                }
	            }// end of for loop

	            // Check whether the directory contains the file start with
	            // filePrefix.
	            if (fileList.isEmpty()) {
	                throw new BTSLBaseException(methodName, "loadFileFromDir", PretupsErrorCodesI.VOUCHER_ERROR_FILE_DOES_NOT_EXIST);// "The voucher file does not exists at the location specified"
	            }
	        } catch (BTSLBaseException be) {
	            _log.error(methodName, " No files exists at the following (" + filePath + ") specified, please check the path");
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, CLASS_NAME+"["+methodName+"]", "", "", "", "No files exists at the following (" + filePath + ") specified, please check the path");
	            throw be;
	        }// end of catch-BTSLBaseException
	        catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.error(methodName, "Exception e = " + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, CLASS_NAME+"["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadFilesFromDir", e.getMessage());
	        }// end of catch-Exception
	        finally {
	        	LogFactory.printLog(methodName, "Exited", _log);
	        }// end of finally
	    }// end of loadFilesFromDir

	    /**
	     * This method will validate the input file specified by the user.
	     * Validation logic is in Parser class
	     * 
	     * @throws BTSLBaseException
	     */
	    private void validateInputFile() throws BTSLBaseException {
	        final String methodName = "validateInputFile";
	        LogFactory.printLog(methodName, "Entered", _log);
	        try {
	            // call the loadConstantValues() to load the values.
	            parserObj.loadConstantValues();

	            populateVoucherUploadVO();

	            // to validate the file length against the uesr input file length
	            parserObj.getFileLength(voucherUploadVO);

	            // called to validate the file, whole file at the time.
	            voucherUploadVO = parserObj.validateVoucherFile();

	        } catch (BTSLBaseException be) {
	            _log.errorTrace(methodName, be);
	            _log.error("validateInputFile", "BTSLBaseException be= " + be.getMessage());
	            throw be;
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.error(CLASS_NAME+"["+methodName+"]", " Exception :=" + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", " Getting Exception=" + e.getMessage());
	            throw new BTSLBaseException(CLASS_NAME, " validateInputFile ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	        }
	        LogFactory.printLog(methodName, "Exiting ", _log);
	    }

	    /**
	     * This method populates the Voucher Upload VO that will be given to Parser
	     * class
	     * 
	     * @throws BTSLBaseException
	     */
	    private void populateVoucherUploadVO() throws BTSLBaseException {
	        final String methodName = "populateVoucherUploadVO";
	        if (_log.isDebugEnabled()) {
	            _log.debug(" populateVoucherUploadVO", " Entered................ ");
	        }
	        try {
	            voucherUploadVO = new VoucherUploadVO();
	            voucherUploadVO.setProcessType(VoucherUploadVO._AUTOPROCESSTYPE);
	            voucherUploadVO.setFileName(fileName);
	            voucherUploadVO.setFilePath(filePath);
	            voucherUploadVO.setMaxNoOfRecordsAllowed(maxNoRecordsAllowed);
	            voucherUploadVO.setCurrentDate(currentDate);
	            voucherUploadVO.setRunningFromCron("Y");
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.error("VoucherFileProcessRM[populateVoucherUploadVO]", " Exception =" + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", " Getting Exception=" + e.getMessage());
	            throw new BTSLBaseException(CLASS_NAME, " populateVoucherUploadVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	        } finally {
	            if (_log.isDebugEnabled()) {
	                _log.debug(" populateVoucherUploadVO", " Exiting................ ");
	            }
	        }
	    }

	    /**
	     * It prepares Voms Batches VO for insertion in Database
	     * 
	     * @return
	     * @throws BTSLBaseException
	     */
	    private VomsBatchVO prepareVomsBatchesVO(String pBatchType, boolean pIsDirectEnable, VomsBatchVO pVomsBatchVO) throws BTSLBaseException {
	        final String methodName = "prepareVomsBatchesVO";
	        if (_log.isDebugEnabled()) {
	            _log.debug(" prepareVomsBatchesVO", " Entered with p_batchType=" + pBatchType + " p_isDirectEnable=" + pIsDirectEnable);
	        }

	        VomsBatchVO vomsBatchVO = null;
	        String batchNo = null;
	        try {
	            vomsBatchVO = new VomsBatchVO();
	            batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));

	            vomsBatchVO.setProductID(productId);
	            vomsBatchVO.setBatchType(pBatchType);
	            vomsBatchVO.setNoOfVoucher(voucherUploadVO.getActualNoOfRecords());
	            vomsBatchVO.setFromSerialNo(voucherUploadVO.getFromSerialNo());
	            vomsBatchVO.setToSerialNo(String.valueOf(voucherUploadVO.getToSerialNo()));
	            vomsBatchVO.setFailCount(numberOfRecords - (voucherUploadVO.getVoucherArrayList()).size());// for
	                                                                                                         // setting
	                                                                                                         // the
	                                                                                                         // no.
	                                                                                                         // of
	                                                                                                         // failed
	                                                                                                         // records
	            vomsBatchVO.setOneTimeUsage(PretupsI.YES);
	            vomsBatchVO.setSuccessCount(voucherUploadVO.getActualNoOfRecords());
	            vomsBatchVO.setLocationCode(voucherUploadVO.getNetwrkID());
	            vomsBatchVO.setCreatedBy(TypesI.SYSTEM_USER);
	            vomsBatchVO.setCreatedOn(currentDate);
	            vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
	            vomsBatchVO.setModifiedBy(TypesI.SYSTEM_USER);
	            vomsBatchVO.setModifiedOn(currentDate);
	            vomsBatchVO.setDownloadCount(1);
	            vomsBatchVO.setStatus(VOMSI.EXECUTED);
	            vomsBatchVO.setCreatedDate(currentDate);
	            vomsBatchVO.setModifiedDate(currentDate);
	            vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_GEN);
	            vomsBatchVO.setMessage(" Batch SuccessFully Executed ...........");

	            if (pIsDirectEnable) {
	                vomsBatchVO.setReferenceNo(pVomsBatchVO.getBatchNo());
	                vomsBatchVO.setReferenceType(pVomsBatchVO.getBatchType());
	                vomsBatchVO.setTotalVoucherPerOrder(0);
	                vomsBatchVO.setDownloadCount(0);
	                vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_ENABLE);
	                vomsBatchVO.setMrp(pVomsBatchVO.getMrp());
	                vomsBatchVO.setProductID(pVomsBatchVO.getProductID());
	            }
	        } catch (BTSLBaseException be) {
	            _log.errorTrace(methodName, be);
	            _log.error("prepareVomsBatchesVO", "BTSLBaseException be= " + be);
	            throw be;
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.error("VoucherFileProcessRM[prepareVomsBatchesVO]", " Exception =" + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", " Getting Exception=" + e.getMessage());
	            throw new BTSLBaseException(CLASS_NAME, " prepareVomsBatchesVO ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
	        } finally {
	            if (_log.isDebugEnabled()) {
	                _log.debug(" prepareVomsBatchesVO", " Exiting with vomsBatchVO= " + vomsBatchVO);
	            }
	        }
	        return vomsBatchVO;
	    }

	    /**
	     * This method is used to create an instance of parser class.This parser
	     * class is operator specific
	     * 
	     * @param String
	     *            parserClassName
	     * @throws BTSLBaseException
	     */
	    private void getParserObj(String pParserClassName) throws BTSLBaseException {
	        final String methodName = "getParserObj";
	        if (_log.isDebugEnabled()) {
	            _log.debug("getParserObj ", " Entered with p_parserClassName = " + pParserClassName);
	        }
	        try {
	            // Creating the instance of parser class.
	            parserObj = (VoucherFileChecksI) Class.forName(pParserClassName).newInstance();
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            _log.error(methodName, " Exception " + e.getMessage());
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, CLASS_NAME+"["+methodName+"]", "", "", "", "Exception: The object of the parser class could not be created dynamically");
	            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOUCHER_ERROR_PARSER_CLASS_NOT_INSTANTIATED);
	        }// end of catch-Exception
	        finally {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, " Exiting with _parserObj = " + parserObj);
	            }
	        }// end of finally
	    }// end of getParserObj
	
	
	    
	    /**
		 * @param ntwrkCode
		 * @param prdctCode
		 * @param walletType
		 * @param trfAmount
		 * @param commAmount
		 * @param beingProcessedDate
		 */
		private static void sendMessage() {
			PushMessage pushMessage;
			final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
					(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
			final String msisdnString = new String(
					Constants.getProperty("adminmobile"));
			final String[] msisdn = msisdnString.split(",");
			 final String smsKey =PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_EXECUTED;
			final BTSLMessages processMessage = new BTSLMessages(smsKey);
			for (int i = 0; i < msisdn.length; i++) {
				pushMessage = new PushMessage(msisdn[i], processMessage, "", null,
						locale,"");
				pushMessage.push();
			}

		}
	
}
