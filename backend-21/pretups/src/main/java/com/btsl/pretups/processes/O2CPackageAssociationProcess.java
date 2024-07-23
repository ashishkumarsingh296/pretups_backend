package com.btsl.pretups.processes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.EMailSender;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.util.clientutils.FileWriterI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.FileUtil;
import com.btsl.util.OracleUtil;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVO;
import com.btsl.voms.vomscategory.businesslogic.VomsPackageVoucherVO;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductVO;
import com.btsl.voms.voucher.businesslogic.OnlineVoucherGenerator;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;
import com.btsl.voms.voucher.businesslogic.VomsVoucherVO;
import com.btsl.voms.voucher.businesslogic.VoucherGenerator;
import com.btsl.voms.voucherbundle.businesslogic.VoucherBundleVO;
import com.client.pretups.user.businesslogic.ChannelPgpUserVO;
import com.web.voms.vomscategory.businesslogic.VomsCategoryWebDAO;

public class O2CPackageAssociationProcess {
   
	private static Log _log = LogFactory.getLog(O2CPackageAssociationProcess.class.getName());
    private static String className = "O2CPackageAssociationProcess";
    private static ArrayList prdList = null;
    final static ChannelUserDAO channelUserDAO = new ChannelUserDAO();
    private static ArrayList<String> statusList=new ArrayList<>();
    private static String failed = "FAILED";
    private static String success = "SUCCESS";
    private static String allPO = "ALL";
    private static String NA = "NA"; 
    private static HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
	private static boolean isOnlineVoucherGenProcessRunning;
 

    public static void main(String[] args) { 
		final String methodName = "main";
		RequestVO requestVO = new RequestVO();

        try {
            
            if(args.length<2 || args.length>2 )
            {
            	System.out.println(methodName + "Usage : BatchO2C [Constants file] [LogConfig file] [Y/N]");
                return;
            }
            //load constants.props
            File constantsFile = new File(args[0]);
            if(!constantsFile.exists() )
            {     
            	System.out.println("BatchO2C[main]" + "Constants file not found on location: "+constantsFile.toString() );
                        return;
            }
            //load log config file
            File logFile = new File(args[1]);
            if(!logFile.exists())
            {         
            	System.out.println("BatchO2C[main]" + "Logconfig File not found on location: "+logFile.toString());
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(),logFile.toString());
            

        }catch(Exception e) {
			e.printStackTrace();
		ConfigServlet.destroyProcessCache();
        }
            try {
    	        process();
            }catch(BTSLBaseException be) {
                _log.error("main", "BTSLBaseException : " + be.getMessage());
                _log.errorTrace(methodName,be);
                return;
            }  
            if(_log.isDebugEnabled()) {
            	_log.debug(methodName, "Before destroyProcessCache() ");   
            }
			ConfigServlet.destroyProcessCache();
			if(_log.isDebugEnabled()) {
            	_log.debug(methodName, "After destroyProcessCache() ");   
			}
		//captureThreads();
	}

	private static void process() throws BTSLBaseException{
        final String methodName = "process";
        final String processName = "O2C Package Association";
        if (_log.isDebugEnabled())
                        _log.info(methodName,"Entered ");
        String processId = null;
        ProcessBL processBL = null;
        Connection con = null;
        ProcessStatusVO processStatusVO = null;
        Date currentDate = null;
        Date processedUpto = null;
        String finalDirectoryPath = null;
        int updateCount = 0;                            //check process details are updated or not
        try
        {
            processId = ProcessI.O2C_PACKAGE_ASSOCIATION;
            con = OracleUtil.getSingleConnection();
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con,processId);
            if(processStatusVO.isStatusOkBool()){
                //method call to find maximum date till which process has been executed
                processedUpto=processStatusVO.getExecutedUpto();
                        if (processedUpto != null){
                                currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date()));
                        processedUpto = currentDate;
                            String dir = Constants.getProperty("O2CPACKAGEFILES");
                            File directory = new File(dir);
                            File[] directoryListing = directory.listFiles();
                            boolean isFileProcessed = false;
                                 if (directoryListing != null && directoryListing.length > 0) {
//                                     finalDirectoryPath=createDirectory();
                                	 finalDirectoryPath = Constants.getProperty("O2CPACKAGEOUTPUT");	
                                for (File child : directoryListing) {
                                        isFileProcessed = processFileForPackages(con, child, dir, finalDirectoryPath);
                                }
                            } else {
                            	_log.debug(methodName, "No Files found in directory structure");
                            }
                                 
                            if(isFileProcessed) {
                                processStatusVO.setExecutedUpto(processedUpto);
                                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, processName+"[process]","","",""," O2C Package Association process has been executed successfully.");
                                        if(_log.isDebugEnabled())
                                                _log.debug("process", "message sent successfully");
                            }
                        }else
                                throw new BTSLBaseException(methodName, PretupsErrorCodesI.O2C_PACKAGE_ASSOCIATION_EXECUTED_UPTO_DATE_NOT_FOUND);
            }else
                throw new BTSLBaseException(methodName, PretupsErrorCodesI.PROCESS_ALREADY_RUNNING);
        }catch(BTSLBaseException be){
                _log.error(methodName, "BTSLBaseException : " + be.getMessage());
                _log.errorTrace(methodName, be);
                throw be;
        }catch(Exception e){
            _log.error(methodName, "Exception : " + e.getMessage());
            _log.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, processName+"[process]","","",""," O2C Package Association process could not be executed successfully.");
                    throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_IN_DAILY_ALERT);
        }finally{
            try{
                if (processStatusVO.isStatusOkBool()){
                        processStatusVO.setStartDate(currentDate);
                        processStatusVO.setExecutedOn(currentDate);
                        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
                                updateCount=(new ProcessStatusDAO()).updateProcessDetail(con,processStatusVO);
                                if(updateCount>0) {
                                	//commit entire process
                                        con.commit();
                                }
                    }
                }catch(Exception ex){
                        if(_log.isDebugEnabled())
                        	_log.debug(methodName, "Exception in closing connection ");
                                        _log.errorTrace(methodName, ex);
                    }
           
            try{
            		con.close();
            		}catch(SQLException e1)
            {
                _log.errorTrace(methodName, e1);
            }
            if(_log.isDebugEnabled()) {
            	_log.debug(methodName, "Before destroyProcessCache() ");   
            }
            ConfigServlet.destroyProcessCache();
            if(_log.isDebugEnabled()) {
            	_log.debug(methodName, "Exiting..... ");   
            }
        }
    }
	
	 private static boolean processFileForPackages(Connection con, File file, String dirPath, String finalDirectroyPath) throws BTSLBaseException, ParseException {
		 	final String methodName = "processFileForPackages";
		 	if (_log.isDebugEnabled())
                _log.info(methodName,"Entered ");
	    	BufferedReader br = null;
	    	String delim = "," ;
	    	ChannelUserVO channelUserVO = null;
	    	RequestVO requestVO = new RequestVO();
	    	ArrayList<RowInfo> packageList = new ArrayList<RowInfo>();
	    	HashMap<String,ArrayList<RowInfo>> userPackageList = new HashMap<String,ArrayList<RowInfo>>();
	    	String userInfo = null;
	    	String referenceNum = null;
	        ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
	        HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
	        ArrayList<String> packagesAdded = new ArrayList<String>();
	        boolean channelUserFound = false;
	        int lines = 0;
	    	
	        bundleList = channelTransferDAO.loadBundleInfoByPrefixId(con);
	    	try{
	    		br = new BufferedReader(new FileReader(file));
	    		String line,bundleId,bundlePrefix;
	    		while((line=br.readLine())!=null) {
	    			try {
	    				if(line.trim().length() > 0) {
							String[] attributes = line.split(delim);
							
							RowInfo rowInfo = createRowInfo2(attributes);
							lines++;
							
							userInfo = rowInfo.getResellerAccountNumber(); 
							
							//load channel user for the input file
							if (channelUserVO==null)
								{//msisdn to be stored in ext_code field
								channelUserVO = channelUserDAO.loadChnlUserDetailsByExtCode(con, userInfo);
								if (channelUserVO==null) {														
									formatSetStatusMessage(failed,allPO,userInfo,NA,NA,Messages.SEVEN,Messages.RESELLER_NA,NA,NA);
									break;
								}else
									channelUserFound = true;
			                }
							try {
								bundleId = bundleList.get(rowInfo.getPackageName().trim()).getVomsBundleID();
								bundlePrefix = rowInfo.getPackageName().trim();
							}catch(Exception e) {
								bundleId = null;
								bundlePrefix = null;
							}
							_log.debug(methodName,channelUserVO.getExternalCode() + userInfo );
							referenceNum = rowInfo.getPurchaseOrderNumber();
							
							if(BTSLUtil.isNullString(channelUserVO.getExternalCode()) || !userInfo.equals(channelUserVO.getExternalCode())) {//vendor(reseller) not found
								formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPackageName(),
										Long.toString(rowInfo.getNumberOfPin()),Messages.SEVEN,Messages.RESELLER_NA + " at line: " + lines,NA,NA);
								continue;
							}else if(!bundleList.containsKey(bundlePrefix) || BTSLUtil.isNullString(bundlePrefix)) {//package not found
								formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPackageName(),
										Long.toString(rowInfo.getNumberOfPin()),Messages.THREE,Messages.PACKAGE_NA + " at line: " + lines,NA,NA);
								continue;
							}else if(packagesAdded.contains(bundlePrefix)) {//duplicate package found
								formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPackageName(),
										Long.toString(rowInfo.getNumberOfPin()),Messages.NINE,Messages.DUPLICATE_PACKAGE+ " at line: " + lines,NA,NA);
								continue;
							}else if(channelTransferDAO.fetchVoucherCountInBundle(con, bundleId) != rowInfo.getVoucherCount()){//package voucher count does not match given count
								formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPackageName(),
										Long.toString(rowInfo.getNumberOfPin()),Messages.ELEVEN,Messages.VOUCHER_COUNT_MISMATCH+ " at line: " + lines,NA,NA);
								continue;
							}else {//adding package information to list
								rowInfo.setTotalVoucherCount(rowInfo.getVoucherCount() * rowInfo.getNumberOfPin());
								packagesAdded.add(bundlePrefix);
								rowInfo.setPrimaryProductCode(bundlePrefix);
								rowInfo.setPackageName(bundleId);//packageName changed from prefixId to bundleId for further processing
								packageList.add(rowInfo);							
							}					
	    				}
						} catch (Exception e) {
							formatSetStatusMessage(failed,NA,NA,NA,
									NA,Messages.ONE,Messages.PARAMETERS_MISMATCH ,NA,NA);
							_log.error(methodName, e.getMessage());
							_log.errorTrace(methodName, e);
						}
	    		}
	    		br.close();
	    		requestVO.setSenderVO(channelUserVO);
	    		requestVO.setReferenceNumber(referenceNum);
	    		isOnlineVoucherGenProcessRunning = false;
				if(channelUserFound && packageList.size() > 0 && lines == packageList.size())//packageList and lines will be unequal if erroneous input is present in file
					processToAssociatePackage(con, requestVO, packageList,file.getName());
				con.commit();
	    	} catch (Exception e) {
				// TODO Auto-generated catch block
	    		_log.errorTrace(methodName, e);
				e.printStackTrace();
			} finally {
				/*checks whether the process PretupsI.ONLINE_VOMS_GEN is already under process, 
					if so, then exception is raised and files are kept in same folders to be processed later*/
				if(!isOnlineVoucherGenProcessRunning) {	
					String fileName = file.getName().replaceFirst(".csv","_").trim() + (((BTSLUtil.getDateTimeStringFromDate(new Date())).replace("/","")).replace(":","")).replace(" ","_") + ".csv";
	        		    writeToFile(finalDirectroyPath,statusList,fileName);
			            moveFilesToFinalDirectory(dirPath, finalDirectroyPath+"/",file.getName(),fileName);
				}else
					 _log.info(methodName,file.getName()+" not moved, changes rolled back due to occupied nested process: " + PretupsI.ONLINE_VOMS_GEN);
	            
	            FileUtil.closeQuietly(br);
	            if (_log.isDebugEnabled())
                    _log.info(methodName,"Exited");
			}
	    	return true;
	    }
    
    /**
     * Method Process
     * This method is the entry point of the class.
     * This method performs all the work related to O2C Transfer
     * 
     * 3. validate the basic checks on channel user
     * 4. load & validate the products
     * 5. calculate the taxes of products
     * 6. prepare the ChannelTransferVO
     * 7. generate the transfer ID
     * 8. approve the transaction
     * 9. add channel transfer in database
     * 
     * @param p_requestVO
     */
	private static void processToAssociatePackage(Connection con, RequestVO p_requestVO, ArrayList<RowInfo> packagesFile,String fileName) throws BTSLBaseException{
        final String METHOD_NAME = "processToAssociatePackage";
        StringBuilder loggerValue= new StringBuilder(); 
        String statusMessage;
        if (_log.isDebugEnabled()) {
	        loggerValue.setLength(0);
        	loggerValue.append("Entered p_requestVO: ");
        	loggerValue.append(p_requestVO);
            _log.debug(METHOD_NAME, loggerValue );
        }
        int insertCount = -1 , updateCount = -1;
        ChannelTransferItemsVO channelTransferItemsVO = null;
        ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        ArrayList<ChannelVoucherItemsVO> channelVoucherItemsList = new ArrayList<ChannelVoucherItemsVO>();
        Date currentDate = null;
        int msgLen = 0;
        HashMap<String, VomsPackageVO> packageVoucher = new HashMap<String, VomsPackageVO>();
        HashMap<String,Long> packageCount = new HashMap<String,Long>(); //save package count
        ChannelUserVO channelUserVO = null;
        try {
        	channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
            UserPhoneVO userPhoneVO = null;
            if (!channelUserVO.isStaffUser()) {
                userPhoneVO = channelUserVO.getUserPhoneVO();
            } else {
                userPhoneVO = channelUserVO.getStaffUserDetails().getUserPhoneVO();
            }

            final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
            channelTransferVO.setBundleType(true);

            currentDate = new Date();
            // validate the channel user
            
            ChannelTransferBL.o2cTransferUserValidate(con, p_requestVO, channelTransferVO, currentDate);
            

            // Meditel changes.....checking for receiver allowed
            UserStatusVO receiverStatusVO = null;
            boolean receiverAllowed = false;
            if (channelUserVO != null) {
                receiverAllowed = false;
                receiverStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO.getUserType(),
                    p_requestVO.getRequestGatewayType());
                if (receiverStatusVO != null) {
                    final String receiverStatusAllowed = receiverStatusVO.getUserReceiverAllowed();
                    final String status[] = receiverStatusAllowed.split(",");
                    int st=status.length;
                    for (int i = 0; i < st; i++) {
                        if (status[i].equals(channelUserVO.getStatus())) {
                            receiverAllowed = true;
                        }
                    }
                }
            }

            if (receiverStatusVO == null) {
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
            } else if (!receiverAllowed) {
            	/*
                 * p_requestVO.setMessageCode(PretupsErrorCodesI.
                 * CHNL_ERROR_RECEIVER_NOTALLOWED);
                 * p_requestVO.setMessageArguments(args);
                 */
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.CHNL_ERROR_RECEIVER_NOTALLOWED);
            }
            
            /*get packageCount
    		 * profileQuantity = NumberOfPin
    		 * bundleName = packageName
    		 * */
            bundleList = channelTransferDAO.loadBundleInfoById(con);
    		VomsPackageVO vomsPackageVO = null;
    		String bundleName;
    		long bundleCount;
    		long totalPrice=0l;
    		for(RowInfo rowInfo : packagesFile) {
    			vomsPackageVO = new VomsPackageVO();
    			bundleName = rowInfo.getPackageName();
    			bundleCount = rowInfo.getNumberOfPin();
    			vomsPackageVO.setProfileQuantity(Long.toString(bundleCount));
    			vomsPackageVO.setBundleName(bundleName);
    			packageVoucher.put(rowInfo.getPackageName(),vomsPackageVO);
    			totalPrice += BTSLUtil.getDisplayAmount(bundleList.get(bundleName).getRetailPrice()) * bundleCount;
    		}
    		
            //TOBE removed
            String messageArray[] = new String[2];
            messageArray[0] = PretupsI.PRODUCT_ETOPUP;
            messageArray[1] = Long.toString(totalPrice);
            final HashMap productMap = checkO2CTrfReqMsgSyntax(messageArray,PretupsI.NO);

            String type = (SystemPreferences.TRANSACTION_TYPE_ALWD)?PretupsI.TRANSFER_TYPE_O2C:PretupsI.ALL;
            String pmtMode = (p_requestVO.getRequestMap() !=null)?((p_requestVO.getRequestMap().get("PAYMENTTYPE") != null)? (String)p_requestVO.getRequestMap().get("PAYMENTTYPE"):PretupsI.ALL):PretupsI.ALL;
    		String paymentMode = (SystemPreferences.TRANSACTION_TYPE_ALWD && SystemPreferences.TRANSACTION_TYPE_ALWD && PretupsI.GATEWAY_TYPE_EXTGW.equals(p_requestVO.getRequestGatewayType()))?pmtMode:PretupsI.ALL;
    		try {
    			prdList = ChannelTransferBL.loadAndValidateProducts(con, p_requestVO, productMap, channelUserVO, true, type, paymentMode);  		
    		}catch(BTSLBaseException be) {
    			throw new BTSLBaseException(be.getMessage()+" error, check commision details of user");
    		}
    		
    		channelVoucherItemsList = prepareChannelVoucherItemsList(con , packageVoucher, channelTransferVO , channelUserVO);
    		ArrayList<ChannelTransferItemsVO> transferItemsList = new ArrayList<ChannelTransferItemsVO>();
    		long packagePrice=0l , totalVouchers=0l;
    		for(Object ob : channelVoucherItemsList) {
    			ChannelVoucherItemsVO cvi = (ChannelVoucherItemsVO) ob;
    			totalVouchers += cvi.getRequiredQuantity();
    			packagePrice += cvi.getTransferMrp() * cvi.getRequiredQuantity();
    		}
    		
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                ChannelTransferBL.loadAndValidateWallets(con, p_requestVO, prdList);
            } else {
                ChannelTransferBL.assignDefaultWallet(con, p_requestVO, prdList);
            }
            channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemsList);
            channelTransferVO.setChannelTransferitemsVOList(prdList);
            channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
            channelTransferVO.setToUserID(channelUserVO.getUserID());
            channelTransferVO.setOtfFlag(true);
            channelTransferVO.setNetworkCode(channelUserVO.getNetworkID());
            channelTransferVO.setDualCommissionType(channelUserVO.getDualCommissionType());
            channelTransferVO.setTransferCategory(PretupsI.TRANSFER_CATEGORY_SALE);
            channelTransferVO.setLevelThreeApprovedQuantity(Long.toString(totalPrice));
            channelTransferVO.setReferenceNum(p_requestVO.getReferenceNumber());
            
            // calculate the taxes for the diff. products
            // based on the transfer category
			if(SystemPreferences.OTH_COM_CHNL){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(p_requestVO.getFilteredMSISDN());
			}
            if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_SALE))) {
                ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(),
                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_O2C);
            } else if ((channelTransferVO.getTransferCategory().equalsIgnoreCase(PretupsI.TRANSFER_CATEGORY_TRANSFER))) {
                ChannelTransferBL.loadAndCalculateTaxOnProducts(con, channelUserVO.getCommissionProfileSetID(), channelUserVO.getCommissionProfileSetVersion(),
                    channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_FOC);
            }
            
            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

            // load the user's information (network admin)
            final UserVO userVO = channelUserDAO.loadOptUserForO2C(con, channelUserVO.getNetworkID());

            // prepares the ChannelTransferVO by populating its fields from the
            // passed ChannelUserVO and filteredList of products for O2C
            // transfer
            prepareChannelTransferVO(p_requestVO, channelTransferVO, currentDate, channelUserVO, prdList, userVO);
            UserPhoneVO phoneVO = null;
            /*UserPhoneVO primaryPhoneVO_R = null;
            if (SystemPreferences.SECONDARY_NUMBER_ALLOWED) {
                final UserDAO userDAO = new UserDAO();
                phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getRequestMSISDN());
                if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
                    channelUserVO.setPrimaryMsisdn(channelTransferVO.getToUserCode());
                    channelTransferVO.setToUserCode(p_requestVO.getRequestMSISDN());
                    if (SystemPreferences.MESSAGE_TO_PRIMARY_REQUIRED) {
                        primaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, channelUserVO.getPrimaryMsisdn());
                    }
                }

            }*/
            
            // generate transfer ID for the O2C transfer
            ChannelTransferBL.genrateTransferID(channelTransferVO);

            // set the transfer ID in each ChannelTransferItemsVO of productList
            for (int i = 0, j = prdList.size(); i < j; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) prdList.get(i);
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
            }

            // the transfer is controlled by default, so set to 'Y'
            channelTransferVO.setControlTransfer(PretupsI.YES);
           
            // performs all the approval transactions for the transfer operation
//           transactionApproval(con, channelTransferVO, userVO.getUserID(), currentDate, channelUserVO);

            final ChannelTransferDAO channelTrfDAO = new ChannelTransferDAO();
           
            insertCount = channelTrfDAO.addChannelTransfer(con, channelTransferVO);
          
            if (insertCount < 0) {
            	for(RowInfo rowInfo : packagesFile) {
                    formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
							Long.toString(rowInfo.getNumberOfPin()),Messages.FOUR,Messages.ADD_CHANNEL_TRANSFER_FAIL,channelTransferVO.getTransferID(),Long.toString(rowInfo.getTotalVoucherCount()));
            	}
            	
            	OracleUtil.rollbackConnection(con, O2CPackageAssociationProcess.class.getName(), METHOD_NAME);
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            OracleUtil.commit(con);
            channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);      
            /*try {
            	//check if the transfer rules satisfy and credit user balances
                transactionApproval(con, channelTransferVO, userVO.getUserID(), currentDate, channelUserVO); 
                }catch(BTSLBaseException e) {
                	_log.errorTrace(METHOD_NAME, e);
                	throw new BTSLBaseException(className, METHOD_NAME, "Check network stock and transfer limits to user, error:"+e.getMessage());
                }*/
            
            updateCount = associateVouchers(con, channelUserVO, channelTransferVO, packageVoucher, channelVoucherItemsList);
            _log.debug(METHOD_NAME,"updateCount="+updateCount);
            if (updateCount < 0) {
            	for(RowInfo rowInfo : packagesFile) {
                    formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
							Long.toString(rowInfo.getNumberOfPin()),Messages.FIVE,Messages.VOUCHER_UPDATE_FAIL,channelTransferVO.getTransferID(),Long.toString(rowInfo.getTotalVoucherCount()));

            	}         
            	OracleUtil.rollbackConnection(con, O2CPackageAssociationProcess.class.getName(), METHOD_NAME);
                throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }
            else {
//            	for(RowInfo rowInfo : packagesFile) {            	
//                    formatSetStatusMessage(success,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
//							Long.toString(rowInfo.getNumberOfPin()),Messages.ZERO,Messages.VOUCHER_UPDATE_SUCCESS,channelTransferVO.getTransferID());
//            	}
	            
	            com.client.pretups.channel.transfer.businesslogic.ChannelTransferDAO pgpUSERDAO=new com.client.pretups.channel.transfer.businesslogic.ChannelTransferDAO();
	   			ArrayList pgpUserList=pgpUSERDAO.loadPGPUser(con,channelUserVO.getUserID());
	   			String encKey = null;
	   			if(pgpUserList.size()<1)
	   			{
	   				formatSetStatusMessage(failed,NA,channelUserVO.getExternalCode(),NA,
							NA,Messages.TEN,Messages.FTP_FAIL+" - PGP details does not exist",channelTransferVO.getTransferID(),NA);
	   				throw new BTSLBaseException(O2CPackageAssociationProcess.className,"ftpFile","ftp.error.usernotexistormultipleentry");
	   			}
	   			ChannelPgpUserVO pgpUserVO=new ChannelPgpUserVO();
	   			pgpUserVO=(ChannelPgpUserVO)pgpUserList.get(0);	
	   			String secretKey = pgpUserVO.getPassphrase();
	   			Class<?> cls = null;
	   			try {
	   				cls = Class.forName(secretKey);
		            Object fileWriterI = cls.newInstance();
		            encKey = ((FileWriterI) fileWriterI).writeFileProcess(con, "F", channelTransferVO, channelUserVO);		       
		            
		            
	   			}catch(BTSLBaseException be) {
	   				_log.errorTrace(METHOD_NAME, be);
		            if("file_generation_failed".equalsIgnoreCase(be.getMessage())) {
		   				for(RowInfo rowInfo : packagesFile) {            	
		                    formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
									Long.toString(rowInfo.getNumberOfPin()),Messages.EIGHT,Messages.FILE_GEN_FAIL,channelTransferVO.getTransferID(),Long.toString(rowInfo.getTotalVoucherCount()));
		            	}	
		   				throw new BTSLBaseException(className, METHOD_NAME, be.getMessage());
		   			 }  else {
		   				for(RowInfo rowInfo : packagesFile) {            	
		                    formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
									Long.toString(rowInfo.getNumberOfPin()),Messages.TEN,Messages.FTP_FAIL,channelTransferVO.getTransferID(),Long.toString(rowInfo.getTotalVoucherCount()));
		            	}	   			 		   			 
		   				throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.FTP_FAIL);
		   			 }
            }catch(Exception e) {
		   			 _log.errorTrace(METHOD_NAME, e);		   			
			   				for(RowInfo rowInfo : packagesFile) {            	
			                    formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
										Long.toString(rowInfo.getNumberOfPin()),Messages.TEN,Messages.FTP_FAIL,channelTransferVO.getTransferID(),Long.toString(rowInfo.getTotalVoucherCount()));
			            	}	   			 		   			 
		   			throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.FTP_FAIL);
	   			}
	   		//vouchers are associated, file is generated and ftp is done	
            	for(RowInfo rowInfo : packagesFile) {            	
	              formatSetStatusMessage(success,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
							Long.toString(rowInfo.getNumberOfPin()),Messages.ZERO,Messages.VOUCHER_UPDATE_SUCCESS,channelTransferVO.getTransferID(),Long.toString(rowInfo.getTotalVoucherCount()));
            	}
            


			//send notification(sms/email)
    	   		StringBuffer notification = new StringBuffer("O2C association is successful with transaction ID:");
    	   		notification.append(channelTransferVO.getTransferID()+"\n");
    	   		notification.append("File_Name:");
    	   		notification.append(fileName +"\n");
    	   		notification.append("Purchase_Order_No:");
    	   		notification.append(packagesFile.get(0).getPurchaseOrderNumber() + "\n");
    	   		_log.info("file writer class name",cls.getName());
    	   		String ftpClass = "com.btsl.pretups.channel.transfer.util.clientutils.FileWriterDefault";
    	   		if(ftpClass.equalsIgnoreCase(secretKey.trim())) {
    	   			_log.info("type class","true");
    	   			notification.append("Decryption Key is:" +encKey +"\n");
    	   		}  	 
    	   		if("Y".equalsIgnoreCase(Constants.getProperty("ERPNotifyVoucherDetail"))) {
    	   			notification.append("Associated Vouchers:\n");
	    	   		for(ChannelVoucherItemsVO item:channelVoucherItemsList) {
	    	   			notification.append(item.getProductName());
	    	   			notification.append("("+item.getBundleId()+" bundle)-"+Long.toString(item.getRequiredQuantity()) +" nos | ");
	    	   		}
    	   		}
    	   		System.out.println(notification.toString());
			
    	   		try{	
    	   		if("sms".equalsIgnoreCase(Constants.getProperty("FTPNotifyMethod"))) {
	    	   		final Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE,SystemPreferences.DEFAULT_COUNTRY);
	    	   		final PushMessage pushMessage = new PushMessage(channelUserVO.getMsisdn(), notification.toString(),channelTransferVO.getTransferID(),null, locale);
	                     pushMessage.push((String) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.LOW_BAL_MSGGATEWAY, Constants.getProperty("NETWORK_CODE")),null);
	    	   	}else {	    	   		
	    	   		EMailSender.sendMail(channelUserVO.getEmail(),  Constants.getProperty("ERPSenderMailId"), "", "", Constants.getProperty("ERPSubject"), 
	    	   				notification.toString(), false, "", "");
	    	   	}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
            }

            p_requestVO.setSuccessTxn(true);

            //ChannelTransferBL.prepareUserBalancesListForLogger(channelTransferVO);
            

         /*   // Meditel changes by Ashutosh
            if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
                try {
//                    if (mcomCon == null) {
//                    	mcomCon = new MComConnection();con=mcomCon.getConnection();
//                    }
                    boolean statusAllowed = false;
                    final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
                        .getUserType(), p_requestVO.getRequestGatewayType());
                    if (userStatusVO == null) {
                        throw new BTSLBaseException(className, METHOD_NAME, PretupsErrorCodesI.ERROR_USERSTATUS_NOTCONFIGURED);
                    } else {
                        final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
                        final String status[] = userStatusAllowed.split(",");
                        for (int i = 0; i < status.length; i++) {
                            if (status[i].equals(channelUserVO.getStatus())) {
                                statusAllowed = true;
                            }
                        }

                       
                        PretupsBL.chkAllwdStatusToBecomeActive(con, SystemPreferences.TXN_RECEIVER_USER_STATUS_CHANG, channelUserVO.getUserID(), channelUserVO.getStatus());
                    }

                } catch (Exception ex) {
                	loggerValue.setLength(0);
                	loggerValue.append("Exception while changing user state to active  ");
                	loggerValue.append(ex.getMessage());
                    _log.error(METHOD_NAME,  loggerValue);
                    _log.errorTrace(METHOD_NAME, ex);
                } finally {
                    if (con != null) {
                        try {
                        	OracleUtil.commit(con);
                        } catch (Exception e) {
                            _log.errorTrace(METHOD_NAME, e);
						}
                    }

                }
            }*/
            // end of changes
            
        } catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            OracleUtil.rollbackConnection(con, O2CPackageAssociationProcess.class.getName(), METHOD_NAME);
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            _log.error(METHOD_NAME,  loggerValue );
            _log.errorTrace(METHOD_NAME, be);
            for(RowInfo rowInfo : packagesFile) {
                formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
						Long.toString(rowInfo.getNumberOfPin()),Messages.TWO,Messages.ERROR_ASSOCIATING+be.getMessage(),NA,Long.toString(rowInfo.getTotalVoucherCount()));
        	}         
            return;
        } catch (Exception ex) {
            p_requestVO.setSuccessTxn(false);
            // Rollbacking the transaction
            OracleUtil.rollbackConnection(con, O2CPackageAssociationProcess.class.getName(), METHOD_NAME);
            loggerValue.setLength(0);
            _log.error(METHOD_NAME, loggerValue );
            _log.errorTrace(METHOD_NAME, ex);
        	loggerValue.append("Exception:");
        	loggerValue.append(ex.getMessage());
            for(RowInfo rowInfo : packagesFile) {
                formatSetStatusMessage(failed,rowInfo.getPurchaseOrderNumber(),channelUserVO.getExternalCode(),rowInfo.getPrimaryProductCode(),
						Long.toString(rowInfo.getNumberOfPin()),Messages.TWO,Messages.ERROR_ASSOCIATING+ex.getMessage(),NA,Long.toString(rowInfo.getTotalVoucherCount()));
        	}
            return;
        } finally {
//        	if(_log.isDebugEnabled()) {
//            	_log.debug(METHOD_NAME, "Before destroyProcessCache() ");   
//            }
//			ConfigServlet.destroyProcessCache();
			if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exited.. ");
            }
        }
    }

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("O2CPackageAssociationProcess []");
		return builder.toString();
	}

	/**
     * Method prepareChannelTransferVO
     * This method used to construct the VO for channel transfer
     * 
     * @param p_requestVO
     *            RequestVO
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_curDate
     *            Date
     * @param p_channelUserVO
     *            ChannelUserVO
     * @param p_prdList
     *            ArrayList
     * @throws BTSLBaseException
     */
    private static void prepareChannelTransferVO(RequestVO p_requestVO, ChannelTransferVO p_channelTransferVO, Date p_curDate, ChannelUserVO p_channelUserVO, ArrayList p_prdList, UserVO p_userVO) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (_log.isDebugEnabled()) {
	        loggerValue.setLength(0);
        	loggerValue.append( "Entering  : requestVO ");
        	loggerValue.append(p_requestVO);
        	loggerValue.append("p_channelTransferVO:");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_curDate:" );
        	loggerValue.append(p_curDate);
        	loggerValue.append("p_channelUserVO:");
        	loggerValue.append(p_channelUserVO);
        	loggerValue.append("p_prdList:");
        	loggerValue.append(p_prdList);
        	loggerValue.append("p_userVO:");
        	loggerValue.append(p_userVO);
        	loggerValue.append( "sourceType: ");
        	loggerValue.append(p_requestVO.getSourceType());
            _log.debug("prepareChannelTransferVO",loggerValue );
        }
        
        p_channelTransferVO.setNetworkCode(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setNetworkCodeFor(p_channelUserVO.getNetworkID());
        p_channelTransferVO.setDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setGraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverCategoryCode(p_channelUserVO.getCategoryCode());
        p_channelTransferVO.setCategoryCode(PretupsI.CATEGORY_TYPE_OPT);
        /*user name set to display user name in message for transfer in counts DEF51 claro*/
        p_channelTransferVO.setToUserName(p_channelUserVO.getUserName());
        // who initaite the order.
        p_channelTransferVO.setReceiverGradeCode(p_channelUserVO.getUserGrade());
        p_channelTransferVO.setFromUserID(PretupsI.OPERATOR_TYPE_OPT);
        p_channelTransferVO.setToUserID(p_channelUserVO.getUserID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        // To display MSISDN in balance log
        p_channelTransferVO.setUserMsisdn(p_channelUserVO.getUserCode());
        p_channelTransferVO.setTransferDate(p_curDate);
        p_channelTransferVO.setCommProfileSetId(p_channelUserVO.getCommissionProfileSetID());
        p_channelTransferVO.setCommProfileVersion(p_channelUserVO.getCommissionProfileSetVersion());
        p_channelTransferVO.setDualCommissionType(p_channelUserVO.getDualCommissionType());
        p_channelTransferVO.setCreatedOn(p_curDate);
        p_channelTransferVO.setCreatedBy(p_userVO.getUserID());
        p_channelTransferVO.setModifiedOn(p_curDate);
        p_channelTransferVO.setModifiedBy(p_userVO.getUserID());
        p_channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
        p_channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
        p_channelTransferVO.setTransferInitatedBy(p_userVO.getUserID());
        p_channelTransferVO.setReceiverTxnProfile(p_channelUserVO.getTransferProfileID());
        p_channelTransferVO.setSource("XML");

        // adding the some additional information of sender/reciever
        p_channelTransferVO.setReceiverGgraphicalDomainCode(p_channelUserVO.getGeographicalCode());
        p_channelTransferVO.setReceiverDomainCode(p_channelUserVO.getDomainID());
        p_channelTransferVO.setToUserCode(p_channelUserVO.getUserCode());
        p_channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
        p_channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
        p_channelTransferVO.setActiveUserId(p_userVO.getUserID());

        ChannelTransferItemsVO channelTransferItemsVO = null;
        String productType = null;
        long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
        long commissionQty = 0, senderDebitQty = 0, receiverCreditQty = 0;
        for (int i = 0, k = p_prdList.size(); i < k; i++) {
            channelTransferItemsVO = (ChannelTransferItemsVO) p_prdList.get(i);
            totRequestQty += channelTransferItemsVO.getRequiredQuantity();
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                totMRP += (channelTransferItemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
            } else {
                totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity()) * channelTransferItemsVO.getUnitValue());
            }
            totPayAmt += channelTransferItemsVO.getPayableAmount();
            totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
            totTax1 += channelTransferItemsVO.getTax1Value();
            totTax2 += channelTransferItemsVO.getTax2Value();
            totTax3 += channelTransferItemsVO.getTax3Value();

            productType = channelTransferItemsVO.getProductType();
            commissionQty += channelTransferItemsVO.getCommQuantity();
            senderDebitQty += channelTransferItemsVO.getSenderDebitQty();
            receiverCreditQty += channelTransferItemsVO.getReceiverCreditQty();
        }

        p_channelTransferVO.setRequestedQuantity(totRequestQty);
        p_channelTransferVO.setTransferMRP(totMRP);
        p_channelTransferVO.setPayableAmount(totPayAmt);
        p_channelTransferVO.setNetPayableAmount(totNetPayAmt);
        p_channelTransferVO.setTotalTax1(totTax1);
        p_channelTransferVO.setTotalTax2(totTax2);
        p_channelTransferVO.setTotalTax3(totTax3);
        p_channelTransferVO.setType(PretupsI.CHANNEL_TYPE_O2C);
        p_channelTransferVO.setChannelTransferitemsVOList(p_prdList);
        p_channelTransferVO.setPayInstrumentAmt(totNetPayAmt);
        p_channelTransferVO.setProductType(productType);
        p_channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
        p_channelTransferVO.setSenderDrQty(PretupsBL.getSystemAmount(senderDebitQty));
        p_channelTransferVO.setReceiverCrQty(PretupsBL.getSystemAmount(receiverCreditQty));
        //Added by lalit to fix bug DEF528 for GP 6.6.1
        if (SystemPreferences.MULTIPLE_WALLET_APPLY && p_requestVO.getRequestMap() != null) {
        	Map<String, String> requestMap = p_requestVO.getRequestMap();
        	if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase((requestMap.get("TRFCATEGORY")!=null?requestMap.get("TRFCATEGORY").toString():""))){
        		p_channelTransferVO.setWalletType(requestMap.get("TRFCATEGORY").toString());
        	}
        }
        final long firstApprovalLimit = p_channelTransferVO.getFirstApproverLimit();
        final long secondApprovalLimit = p_channelTransferVO.getSecondApprovalLimit();

        if (p_channelTransferVO.getRequestedQuantity() > secondApprovalLimit) {
            p_channelTransferVO.setThirdApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setThirdApprovedOn(p_curDate);
            p_channelTransferVO.setSecondApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setSecondApprovedOn(p_curDate);
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        } else if (p_channelTransferVO.getRequestedQuantity() <= secondApprovalLimit && p_channelTransferVO.getRequestedQuantity() > firstApprovalLimit) {
            p_channelTransferVO.setSecondApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setSecondApprovedOn(p_curDate);
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        } else if (p_channelTransferVO.getRequestedQuantity() <= firstApprovalLimit) {
            p_channelTransferVO.setFirstApprovedBy(p_userVO.getUserID());
            p_channelTransferVO.setFirstApprovedOn(p_curDate);
        }
        
        if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
        	ArrayList chnlSoSVOList = new ArrayList();
        	chnlSoSVOList.add(new ChannelSoSVO(p_channelUserVO.getUserID(),p_channelUserVO.getExternalCode(),p_channelUserVO.getSosAllowed(),p_channelUserVO.getSosAllowedAmount(),p_channelUserVO.getSosThresholdLimit()));
        	p_channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
        }
        
        if (_log.isDebugEnabled()) {
            _log.debug("prepareChannelTransferVO", "Exiting : ");
        }
    }

   
    /**
     * Method transactionApproval
     * This method responsible to Approve the O2C transaction and update
     * the network stock, update the user balances and user counts
     * 
     * @param p_con
     *            Connection
     * @param p_channelTransferVO
     *            ChannelTransferVO
     * @param p_date
     *            Date
     * @param p_userID
     *            String
     * @throws BTSLBaseException
     */
    private static void transactionApproval(Connection p_con, ChannelTransferVO p_channelTransferVO, String p_userID, Date p_date, ChannelUserVO channelUserVO) throws BTSLBaseException {
    	final String methodName="transactionApproval";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entering  : p_channelTransferVO:" + p_channelTransferVO + "p_userID:" + p_userID + "p_date:" + p_date);
        }

        try {
        	int updateCount = -1;
        	//added for o2c direct transfer
//            if(SystemPreferences.O2C_DIRECT_TRANSFER)
//            {
            updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStock(p_con, p_channelTransferVO, p_userID, p_date, true);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;
            updateCount = ChannelTransferBL.updateNetworkStockTransactionDetails(p_con, p_channelTransferVO, p_userID, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;
            if (PretupsI.COMM_TYPE_POSITIVE.equals(p_channelTransferVO.getDualCommissionType())) {
                updateCount = ChannelTransferBL.prepareNetworkStockListAndCreditDebitStockForCommision(p_con, p_channelTransferVO, p_userID, p_date, true);
                if (updateCount < 1) {
                    throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
                updateCount = -1;
                updateCount = ChannelTransferBL.updateNetworkStockTransactionDetailsForCommision(p_con, p_channelTransferVO, p_userID, p_date);
                if (updateCount < 1) {
                    throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
                }
            }
            p_channelTransferVO.setStockUpdated(TypesI.YES);
//            }
//            else
//            {
//            	 p_channelTransferVO.setStockUpdated(TypesI.NO);
//            }
            
            UserBalancesVO userBalanceVO = null;
            ChannelTransferItemsVO chnlTrfItemsVO = null;
            final UserBalancesDAO userBalDAO = new UserBalancesDAO();

            for (int x = 0, y = prdList.size(); x < y; x++) {
                chnlTrfItemsVO = (ChannelTransferItemsVO) prdList.get(x);
                userBalanceVO = new UserBalancesVO();

                userBalanceVO.setUserID(p_channelTransferVO.getToUserID());
                userBalanceVO.setProductCode(chnlTrfItemsVO.getProductCode());
                userBalanceVO.setNetworkCode(p_channelTransferVO.getNetworkCode());
                userBalanceVO.setNetworkFor(p_channelTransferVO.getNetworkCodeFor());
                userBalanceVO.setLastTransferID(p_channelTransferVO.getTransferID());
                userBalanceVO.setLastTransferType(p_channelTransferVO.getTransferType());
                userBalanceVO.setLastTransferOn(p_channelTransferVO.getTransferDate());
                userBalanceVO.setPreviousBalance(userBalanceVO.getBalance());
                userBalanceVO.setQuantityToBeUpdated(chnlTrfItemsVO.getRequiredQuantity());
                // Added on 13/02/2008
                userBalanceVO.setUserMSISDN(p_channelTransferVO.getToUserCode());
            }

            updateCount = userBalDAO.updateUserDailyBalances(p_con, p_date, userBalanceVO);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

            updateCount = -1;

            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_PRODUCT_MULTIPLE_WALLET)).booleanValue()) {
                updateCount = channelUserDAO.creditUserBalancesForMultipleWallet(p_con, p_channelTransferVO, true, null);
            } else {
                updateCount = channelUserDAO.creditUserBalances(p_con, p_channelTransferVO, true, null);
            }

            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }

           if(PretupsI.TRANSFER_TYPE_FOC.equalsIgnoreCase(p_channelTransferVO.getWalletType())){
        	   p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_FOC);
           }
           
           if(PretupsI.TRANSFER_TYPE_O2C.equalsIgnoreCase(p_channelTransferVO.getWalletType())){
        	   p_channelTransferVO.setTransactionCode(PretupsI.TRANSFER_TYPE_O2C);
           }
            updateCount = ChannelTransferBL.updateOptToChannelUserInCounts(p_con, p_channelTransferVO, null, p_date);
            if (updateCount < 1) {
                throw new BTSLBaseException(className, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
            }


        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exit  : ");
            }
        }
    }
    
    
    private static ArrayList prepareChannelVoucherItemsList(Connection con , HashMap<String, VomsPackageVO> packageList , 
    								ChannelTransferVO channelTransferVO , ChannelUserVO channelUserVO) {
    	final String methodName = "prepareChannelVoucherItemsList";
    	if (_log.isDebugEnabled())
            _log.info(methodName,"Entered ");
		final VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
		final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		HashMap<String,Long> packageCount = new HashMap<String,Long>(); //save package count
		HashMap<String,ArrayList<VomsPackageVoucherVO>> packageVoucherList = new HashMap<String,ArrayList<VomsPackageVoucherVO>>() ; //vouchers list for each bundleId
//		HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
		ArrayList<VomsPackageVoucherVO> voucherList = new ArrayList<VomsPackageVoucherVO>();
		final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = new ArrayList<ChannelVoucherItemsVO>();
		int packageListSize, insertCount, updateCount;
		String bundleID, message;
        long totalVoucherQuantity,  packageQuantity, totalVouchersPrice, totalTransferPrice = 0;
        Date currentDate = null;
        boolean packageVouchersNotAvailable = false;

        currentDate = new Date();
        currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
        try {
        //*****load packageList******
        
        
        packageVoucherList = vomsCategorywebDAO.loadVoucherPackageProductDetails(con);
//        bundleList = channelTransferDAO.loadBundleInfoById(con);
    	ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();
    	ChannelVoucherItemsVO channelVoucherItemsVO = new ChannelVoucherItemsVO();
    	
    	for(Map.Entry<String, VomsPackageVO> entry : packageList.entrySet()) {
    		VomsPackageVO vomsPackageVO = entry.getValue();
			packageCount.put(Long.toString(vomsPackageVO.getBundleID()) , Long.parseLong(vomsPackageVO.getProfileQuantity()));
			channelTransferItemsVO.setSenderDebitQty(0);
			channelTransferItemsVO.setReceiverCreditQty(0);
			bundleID = bundleList.get(entry.getKey()).getVomsBundleID();
			voucherList = packageVoucherList.get(bundleID); //get voucher details in bundle
			packageQuantity = Integer.parseInt(vomsPackageVO.getProfileQuantity());
			totalTransferPrice += vomsPackageVO.getRetailPrice() * packageQuantity;
			for(VomsPackageVoucherVO vpvVO : voucherList) {
				totalVoucherQuantity = packageQuantity * vpvVO.getQuantity();
				totalVouchersPrice = BTSLUtil.parseDoubleToLong((totalVoucherQuantity * vpvVO.getPrice()));
				channelVoucherItemsVO = new ChannelVoucherItemsVO();
				channelVoucherItemsVO.setTransferId(channelTransferVO.getTransferID());
				channelVoucherItemsVO.setTransferDate(currentDate);			
				channelVoucherItemsVO.setTransferMRP(BTSLUtil.parseDoubleToLong(vpvVO.getPrice()));
				channelVoucherItemsVO.setRequiredQuantity(totalVoucherQuantity);
				channelVoucherItemsVO.setVoucherType(vpvVO.getProductType());
//						channelVoucherItemsVO.setFromSerialNum(startSerialNo);
//						channelVoucherItemsVO.setToSerialNum(endSerialNo);
				channelVoucherItemsVO.setProductId(vpvVO.getProductID());
				channelVoucherItemsVO.setProductName(vpvVO.getProductName());
				channelVoucherItemsVO.setNetworkCode(channelUserVO.getNetworkID());
//				channelVoucherItemsVO.setSegment(theForm.getSegment());
				channelVoucherItemsVO.setBundleId(vpvVO.getBundleID());
				channelVoucherItemsVO.setBundleRemarks(vomsPackageVO.getRemarks());
				channelVoucherItemsVOList.add(channelVoucherItemsVO);
			}
		}	
        }catch(Exception e) {
        	e.printStackTrace();
        }
        finally {
        	if (_log.isDebugEnabled())
                _log.info(methodName,"Exiting " + " channelVoucherItemsVOList count: " + channelVoucherItemsVOList.size());
        }
        
		return channelVoucherItemsVOList;
    }
    
    
   private static int associateVouchers(Connection con, ChannelUserVO channelUserVO, ChannelTransferVO channelTransferVO, HashMap<String,VomsPackageVO> packageVouchers, ArrayList channelVoucherItemsList) throws BTSLBaseException {
	   final String methodName = "associateVouchers";
	   if (_log.isDebugEnabled())
           _log.info(methodName,"Entered ");
	  ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
		Date currentDate = new Date();
		boolean packageVouchersNotAvailable = false;
		HashMap<Integer,Long> voucherCountAvailable = new HashMap<Integer,Long>();
		HashMap<Integer,Long> voucherCountPackage = new HashMap<Integer,Long>();
		HashMap<String,ArrayList<String>> availableVoucherSerialNo = new HashMap<String,ArrayList<String>>();
		int productId = 0, updateCount = -1;
		long voucherQuantity;
		long voucherAvailable = 0 ;
		try {
			ChannelVoucherItemsVO channelVoucherItemsVO1 = new ChannelVoucherItemsVO();
			for(Object ob : channelVoucherItemsList) { //calculating total vouchers for each product
				channelVoucherItemsVO1 = (ChannelVoucherItemsVO) ob;
				productId = Integer.parseInt(channelVoucherItemsVO1.getProductId()) ;
				voucherQuantity = channelVoucherItemsVO1.getRequiredQuantity() ;
				if(voucherCountPackage.containsKey(productId)){
					voucherCountPackage.put(productId, voucherQuantity + voucherCountPackage.get(productId));
				}
				else{
					voucherCountPackage.put(productId,voucherQuantity);
				}
			}
			int update = 1;
			ArrayList<VomsBatchVO> vomsBatchList = new ArrayList<VomsBatchVO>();
			try {
				VomsProductDAO vomsProductDAO = new VomsProductDAO();
				String batchNo,voucherType = null;
				boolean flag1;
				int l= 0;
				int distinctVoucherCount = voucherCountPackage.size();
				int sequence[] = new int[distinctVoucherCount];
	            Arrays.fill(sequence, 0);
	            int vouchersCt[] = new int[distinctVoucherCount];
	            Arrays.fill(vouchersCt, 0);
	            
				VomsBatchVO vomsBatchVO = null;
				for(Map.Entry<Integer, Long> entry: voucherCountPackage.entrySet()) {
					vomsBatchVO = new VomsBatchVO();
					batchNo = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
	//				vomsBatchVO.setProductName(VPVo.getProductName());
	//        		vomsBatchVO.setSegment(VPVo.getSegment());
	//        		vomsBatchVO.setExpiryDate(VPVo.getExpiryDate());
	//        		vomsBatchVO.setExpiryPeriod((int)VPVo.getExpiryPeriod());
	        		vomsBatchVO.setLocationCode(Constants.getProperty("NETWORK_CODE"));
					vomsBatchVO.setProductID(Integer.toString(entry.getKey()));
					vomsBatchVO.setBatchType(VOMSI.WARE_HOUSE);
					vomsBatchVO.setNoOfVoucher(entry.getValue());
	//				 vomsBatchVO.setMrp(vomsBatchVO.getDenomination());
			         vomsBatchVO.setFromSerialNo("");
			         vomsBatchVO.setToSerialNo("");
			         vomsBatchVO.setFailCount(0);
	//        		vomsBatchVO.setOneTimeUsage(PretupsI.YES);
	                vomsBatchVO.setSuccessCount(0);
	                vomsBatchVO.setCreatedBy(VOMSI.ERP_SYSTEM);
	                vomsBatchVO.setCreatedOn(currentDate);
	                vomsBatchVO.setModifiedBy(VOMSI.ERP_SYSTEM);
	                vomsBatchVO.setModifiedOn(currentDate);
	                vomsBatchVO.setDownloadCount(0);
	                vomsBatchVO.setStatus(VOMSI.BATCH_ACCEPTED);
	                vomsBatchVO.setCreatedDate(currentDate);
	                vomsBatchVO.setModifiedDate(currentDate);
	                vomsBatchVO.setProcess(VOMSI.BATCH_PROCESS_INITIATE);
	                vomsBatchVO.setRemarks("Batch Initiated from ERP system");
	                vomsBatchVO.setExpiryPeriod(1);
	        		vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batchNo));
	        		if(SystemPreferences.SEQUENCE_ID_ENABLE){
	                    vomsBatchVO.setSeq_id(new VomsBatchesDAO().generateSequenceNumber(con,sequence,vouchersCt));
	                    }
	        		sequence[l]=vomsBatchVO.getSeq_id();
	        		vouchersCt[l]=(int) Integer.parseInt(Long.toString(entry.getValue()));
	                l++;
	        		vomsBatchList.add(vomsBatchVO);
	        		_log.debug(methodName, "Initiating batch " + vomsBatchVO);
				}
				int recordCount = new VomsBatchesDAO().addBatch(con, vomsBatchList, VOMSI.VOUCHER_SEGMENT_LOCAL);
	    			
				//generate vouchers in voms_vouchers in WH status from the voms_batches added
				OnlineVoucherGenerator a = new OnlineVoucherGenerator();
		        a.processERP(con,vomsBatchList);	
			}catch(BTSLBaseException be) {
				 if(be.getMessage().trim().equals(PretupsErrorCodesI.PROCESS_ALREADY_RUNNING))
                	 isOnlineVoucherGenProcessRunning = true;
				formatSetStatusMessage(failed,NA,channelUserVO.getExternalCode(),NA,NA,Messages.TWELVE,Messages.VOU_CREATION_FAIL,
						channelTransferVO.getTransferID(),NA);
                 throw new BTSLBaseException("Voms batches addition and voucher creation failed, error:" + be.getMessage());                
			}catch(Exception e){     
				formatSetStatusMessage(failed,NA,channelUserVO.getExternalCode(),NA,NA,Messages.TWELVE,Messages.VOU_CREATION_FAIL,
						channelTransferVO.getTransferID(),NA);
                 throw new BTSLBaseException("Voms batches addition and voucher creation failed");
             }
    		 
			availableVoucherSerialNo = channelTransferDAO.loadVouchersSerialNosFromBatches(con, vomsBatchList, VOMSI.VOMS_WARE_HOUSE_STATUS, PretupsI.NO);
			for(Map.Entry<String,ArrayList<String>> entry : availableVoucherSerialNo.entrySet()) {
				voucherCountAvailable.put(Integer.parseInt(entry.getKey()) , (long)entry.getValue().size());
			}
			for (Map.Entry<Integer, Long> entry : voucherCountPackage.entrySet()) {
				voucherAvailable = 0;
				if(voucherCountAvailable.containsKey(entry.getKey())) {
					voucherAvailable = voucherCountAvailable.get(entry.getKey());
					if(voucherAvailable < entry.getValue()) { //the number of vouchers required is not available to be associated
						packageVouchersNotAvailable = true;
					}
				}
				else {
					voucherCountAvailable.put(entry.getKey(),voucherAvailable);
					packageVouchersNotAvailable = true;
				}
			}
		
		
		if(!packageVouchersNotAvailable) { //check whether number of vouchers in package are available in voms_vouchers
			VomsCategoryWebDAO vomsCategorywebDAO = new VomsCategoryWebDAO();
			HashMap<String,Long> packageCount = new HashMap<String,Long>(); //save package count
			HashMap<String, VoucherBundleVO> packageInfo = new HashMap<String, VoucherBundleVO>(); //set package details
			HashMap<String, VoucherBundleVO> packageSequenceInfo = new HashMap<String, VoucherBundleVO>();
	        HashMap<String,ArrayList<VomsPackageVoucherVO>> packageVoucherList = new  HashMap<String,ArrayList<VomsPackageVoucherVO>>() ; 
//	        HashMap<String,VoucherBundleVO> bundleList = new HashMap<String,VoucherBundleVO>();
	        ArrayList<VomsPackageVoucherVO> voucherList = new ArrayList<VomsPackageVoucherVO>();
			ArrayList<VomsVoucherVO> vouchers = new ArrayList<VomsVoucherVO>();
	        VomsPackageVO vomsPackageVO = null ;
	        VomsVoucherVO vomsVoucherVO = null ;
	        VoucherBundleVO voucherBundleVO = null;
	        VomsPackageVoucherVO vpvVO1 = null;
			String bundlePrefix , nextBundleSequenceNo, bundleID, serialNo, productID , voms_o2c_status = null;
			long masterSerialNo , bundleSequenceNo ;
			int voucherQuantityInPackage = 0 , voucherUpdateCount = 0 , bundleLastSequenceNoUpdateCount = 0;
			boolean serialNosSet = true;
			
//			bundleList = channelTransferDAO.loadBundleInfoById(con);
			for(Map.Entry<String, VomsPackageVO> entry: packageVouchers.entrySet()) {
				packageCount.put(bundleList.get(entry.getKey()).getVomsBundleID() , Long.parseLong(entry.getValue().getProfileQuantity()));
			}
			
	        packageVoucherList = vomsCategorywebDAO.loadVoucherPackageProductDetails(con);//voucher details package-wise
	        for(Map.Entry<String,ArrayList<VomsPackageVoucherVO>> entry : packageVoucherList.entrySet()) {
	        	if(packageCount.containsKey(entry.getKey())) {
		        	vpvVO1 = entry.getValue().get(0); //first row of each packageVoucherList
		        	voucherBundleVO = new VoucherBundleVO();
		        	voucherBundleVO.setPrefixID(vpvVO1.getBundlePrefix());
		        	voucherBundleVO.setLastBundleSequence(vpvVO1.getBundleLastSequence());
					packageInfo.put(entry.getKey() , voucherBundleVO);
	        	}
	        }
	        
	        if (SystemPreferences.VOUCHER_EN_ON_TRACKING) {
	        	voms_o2c_status = VOMSI.BATCH_ENABLED;
			}else {												      
	      		voms_o2c_status = VOMSI.VOMS_PRE_ACTIVE_STATUS;
			}
//	        Date currentDate = new Date();
			for(Map.Entry<String, Long> entry : packageCount.entrySet()) {//iterate through each package
				bundleID = entry.getKey();
				voucherList = packageVoucherList.get(bundleID);
				bundlePrefix = packageInfo.get(bundleID).getPrefixID();
				
				for(long count = 0 ; count < entry.getValue() ; count++) { //iterate through count of each package
					bundleSequenceNo = packageInfo.get(bundleID).getLastBundleSequence() + 1; //increment by 1
					packageInfo.get(bundleID).setLastBundleSequence(bundleSequenceNo);
					masterSerialNo = Long.parseLong(bundlePrefix + BTSLUtil.padZeroesToLeft(Long.toString(bundleSequenceNo) , 8)); //creation of masterSerialNo

					for(VomsPackageVoucherVO vpvVO : voucherList) { //iterate through each package vouchers
						voucherQuantityInPackage = vpvVO.getQuantity();
						
						for(int i = 0 ; i < voucherQuantityInPackage ; i++) { //iterate through count of each voucher in a package
							vomsVoucherVO = new VomsVoucherVO();
							productID = vpvVO.getProductID();
							vomsVoucherVO.setProductID(productID);
							serialNo = availableVoucherSerialNo.get(productID).get(0); //get a serial no for a product
							vomsVoucherVO.setBundleId(Integer.parseInt(bundleID));
							vomsVoucherVO.setMasterSerialNo(masterSerialNo);								
							vomsVoucherVO.setSerialNo(serialNo);
							vomsVoucherVO.setPreviousStatus(VOMSI.VOMS_WARE_HOUSE_STATUS);
							vomsVoucherVO.setCurrentStatus(voms_o2c_status);
							vomsVoucherVO.setModifiedBy(channelUserVO.getUserID());
							vomsVoucherVO.setModifiedOn(currentDate);
							vomsVoucherVO.setStatus(voms_o2c_status);
							vomsVoucherVO.setUserID(channelTransferVO.getToUserID());
							vomsVoucherVO.setTransactionID(channelTransferVO.getTransferID());
							vouchers.add(vomsVoucherVO);
							availableVoucherSerialNo.get(productID).remove(serialNo); //remove the used serial number
						}
					}
				}
			}
			for(Map.Entry<String, Long> entry : packageCount.entrySet()) {//hashmap to update last bundle Sequence
				bundleID = entry.getKey();
				voucherBundleVO = new VoucherBundleVO();
				voucherBundleVO.setLastBundleSequence(packageInfo.get(bundleID).getLastBundleSequence());
				voucherBundleVO.setVomsBundleID(bundleID);
				packageSequenceInfo.put(bundleID , voucherBundleVO);
			}
			for(VomsVoucherVO voucherVO : vouchers) {
				if(BTSLUtil.isNullString(voucherVO.getSerialNo())){
					serialNosSet = false ;
					break;
				}
			}
			if(serialNosSet) {
				voucherUpdateCount = channelTransferDAO.updateVoucherMasterSerialNo(con, vouchers);
				bundleLastSequenceNoUpdateCount = channelTransferDAO.updateBundleLastSequence(con , packageSequenceInfo); 

			
				if(voucherUpdateCount > 0 && bundleLastSequenceNoUpdateCount > 0) {
					updateCount = channelTransferDAO.updateChannelTransferVoucherApproval(con, channelTransferVO, 3);
				}
				else {
					throw new BTSLBaseException(className, methodName, "failed to update voucher master serial number");
				}
				if(updateCount < 0) {
					throw new BTSLBaseException(className, methodName, "Failed to update Channel transfer information, " + Messages.ERROR_ASSOCIATING);
				}
			}					
			else {
				throw new BTSLBaseException(className, methodName, "failed to allocate vouchers serial numbers");
			}
			
		}
		else {//throw error for insufficient vouchers
			StringBuffer shortage = null;  
			HashMap<String,VomsProductVO> productList = new HashMap<String,VomsProductVO>();
			_log.debug(methodName,"failed to associate vouchers due to shortage of vouchers" );
			productList = channelTransferDAO.loadProductDetails(con); // to get product name to display the shortage of voucher products
			for (Map.Entry<Integer, Long> entry : voucherCountPackage.entrySet()) {
				voucherAvailable = voucherCountAvailable.get(entry.getKey());
				if(voucherAvailable < entry.getValue()) { //the number of vouchers required is not available to be associated
					shortage = new StringBuffer();
					shortage.append(Messages.INSUFFICIENT_VOUCHERS);
					shortage.append(" Voucher Name - " + productList.get(Integer.toString(entry.getKey())).getProductName() + " -> ");
					shortage.append(" Additional Required - " + Long.toString(entry.getValue() - voucherAvailable));
			
		            formatSetStatusMessage(failed,NA,channelUserVO.getExternalCode(),NA,NA,Messages.SIX,shortage.toString(),channelTransferVO.getTransferID(),Long.toString(entry.getValue()));
				}
			}
			
			throw new BTSLBaseException(className, methodName, "failed to associate vouchers due to shortage of vouchers generated");
			}
			
		}catch(Exception e) {	
            _log.errorTrace(methodName, e);
		}finally {
			
		}
	return updateCount;
   }

	/*private static RowInfo createRowInfo(String[] attributes) throws BTSLBaseException {
			
		int i = 0;
		int len = 18;
		RowInfo rowInfo = null;
		String methodName = "createRowInfo";
		
		if(attributes.length == len) {
			String packageName = attributes[i++];											
			String pinUsageMode = attributes[i++];												
			String batchType = attributes[i++];
			String printable = attributes[i++];
			String primaryProductCode = attributes[i++];
			
			String secondaryProductCode = attributes[i++];
			String pinLockType = attributes[i++];
			int pinLengthWithoutPrefix = Integer.parseInt(attributes[i++]);
			long numberOfPin = Long.parseLong(attributes[i++]);
			int passwordLength = Integer.parseInt(attributes[i++]);
			
			String purchaseOrderNumber = attributes[i++];
			String productPartCode = attributes[i++];
			String voucherType = attributes[i++];
			String resellerAccountNumber = attributes[i++];
			String downloadFileNameFormat = attributes[i++];
			
			String downloadFormat = attributes[i++];
			String passwordToEncrypt = attributes[i++];
			String ftpDetails = attributes[i++];
			
			rowInfo = new RowInfo( packageName, pinUsageMode,  pinLockType, pinLengthWithoutPrefix, numberOfPin, 
					passwordLength,  downloadFileNameFormat,  downloadFormat,  passwordToEncrypt,  ftpDetails, resellerAccountNumber); 
		}
		else {
			throw new BTSLBaseException(O2CPackageAssociationProcess.className,methodName,"Process parameters length from file not equal to " + len);
		}
		
		
		return rowInfo;
	}
	*/
	private static RowInfo createRowInfo2(String[] attributes) throws BTSLBaseException {
		
		String parameterFormat = Constants.getProperty("O2CPCKGPROCESSPARAMS");
		String methodName = "createRowInfo2";
		RowInfo rowInfo = null;
		HashMap<String,String> paramValues = new  HashMap<String,String>();
		if(BTSLUtil.isNullString(parameterFormat)) {
			parameterFormat = "PKGNAME PINUSGMODE BATCHTYPE PRINTPIN PRIMPRODCODE SECPRODCODE PINLOCKTYPE PINLENGTH NUMOFPIN PASSWORDLEN PURCHASEORDN PRODPARTCODE VOUCHERTYPE RESELLACCNUM FILENAMEFRMT ENCRYPTFRMT PASSWORD FTPDETAILS";
		}
		String[] parameter = parameterFormat.split("\\s+");
		int paramLen = parameter.length;

		if(paramLen == attributes.length) {
			for(int i = 0 ; i < paramLen ; i++) {
				paramValues.put(parameter[i], attributes[i]);
			}
			
			rowInfo = new RowInfo(paramValues);
		}else{
			formatSetStatusMessage(failed,NA,NA,NA,
					NA,Messages.ONE,Messages.PARAMETERS_MISMATCH + " Input parameters count: "+attributes.length,NA,NA);
			throw new BTSLBaseException(O2CPackageAssociationProcess.className,methodName,"Process parameters length from file not equal to " + paramLen);
		}
		
		
		return rowInfo;
	}
	
	
	private static HashMap checkO2CTrfReqMsgSyntax(String[] messageArray, String userPinRequired) throws BTSLBaseException {
		final String methodName = "checkO2CTrfReqMsgSyntax";
		StringBuilder loggerValue= new StringBuilder(); 
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered with messageArray : ");
			loggerValue.append(messageArray);
			loggerValue.append(", userPinRequired : ");
			loggerValue.append(userPinRequired);
			_log.debug(methodName,  loggerValue );
		}

		for (int i = 0, j = messageArray.length; i < j; i++) {
			_log.debug(methodName, "messageArray[" + i + "] : " + messageArray[i]);
		}

		final HashMap productMap = new HashMap();

		if (!(PretupsI.YES.equals(userPinRequired)) && messageArray.length == 2) {
			_log.debug(methodName, "messageArray[1] : " + messageArray[1]);
			productMap.put(SystemPreferences.DEFAULT_PRODUCT, messageArray[1]);
		} else if (messageArray.length == 3 && ! (messageArray[0].equals("O2CRET"))) {
			_log.debug(methodName, "messageArray[1]  :: " + messageArray[1]);
			//Handling of multi-product
            if(!BTSLUtil.isNullString(messageArray[2]) && !SystemPreferences.DEFAULT_PRODUCT.equals(messageArray[2])){
            	productMap.put(messageArray[2],messageArray[1]);
            } else {
            	productMap.put(SystemPreferences.DEFAULT_PRODUCT,messageArray[1]);            	
            }
		} else

			if (messageArray.length > 3 || (messageArray.length == 3 && messageArray[0].equals("O2CRET")) ) {
				_log.debug(methodName, "arrlength " + messageArray.length);
				final int startIndex = 1;
				int endIndex = 0;
				if ((messageArray.length % 2) == 0) {
					endIndex = (messageArray.length - 1);
				} else {
					endIndex = messageArray.length;
				}

				for (int i = startIndex; i < endIndex; i += 2) {
					if (productMap.size() > 0) {
						if (productMap.containsKey(messageArray[i + 1])) {
							throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_MESSAGE_FORMAT_NOT_PROPER, 0, messageArray, null);
						}
					}

					productMap.put(messageArray[i + 1], messageArray[i]);
				}
			} else {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName,
						PretupsErrorCodesI.ERROR_MESSAGE_FORMAT_NOT_PROPER);
			}

		// validate the product short codes and
		// quantities which are comming in the request
		String prdShortCode = null;
		String prdQty = null;
		final Collection keySet = productMap.keySet();
		final Iterator itr = keySet.iterator();
		while (itr.hasNext()) {
			prdShortCode = (String) itr.next();
			prdQty = (String) productMap.get(prdShortCode);

			if (!BTSLUtil.isDecimalValue(prdQty)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_QUANTITY);
			} else if (Double.parseDouble(prdQty) <= 0) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_LESS_DEFAULT_PRODUCT_QUANTITY);
			} else if (BTSLUtil.isNullString(prdShortCode)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_CODE_FORMAT);
			} else if (!BTSLUtil.isNumeric(prdShortCode)) {
				throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_INVALID_PRODUCT_CODE_FORMAT);
			}
		}

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Exiting : productMap.size():: ");
			loggerValue.append(productMap.size());
			_log.debug(methodName,  loggerValue);
		}

		return productMap;
	}
	
	
	
	static class RowInfo{
		
		String packageName; //voms bundle Id
		String pinUsageMode;
		String batchType;
		String printable;
		String primaryProductCode;
		String secondaryTxnId; //secondary transaction ID
		String pinLockType;
		int pinLengthWithoutPrefix;
		long numberOfPin; //number of bundles
		long voucherCount; //voucher count in each bundle
		long totalVoucherCount;
		String purchaseOrderNumber; //given by customer
		String productPartCode;
		String voucherType;
		String resellerAccountNumber; //external code of retailer
		String downloadFileNameFormat;
		String downloadFormat;
		String passwordToEncrypt;
		String ftpDetails;
		
	
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("RowInfo [packageName=").append(packageName).append(", pinUsageMode=").append(pinUsageMode)
					.append(", batchType=").append(batchType).append(", printable=").append(printable)
					.append(", primaryProductCode=").append(primaryProductCode).append(", secondaryTxnId=")
					.append(secondaryTxnId).append(", pinLockType=").append(pinLockType)
					.append(", pinLengthWithoutPrefix=").append(pinLengthWithoutPrefix).append(", numberOfPin=")
					.append(numberOfPin).append(", voucherCount=").append(voucherCount).append(", totalVoucherCount=")
					.append(totalVoucherCount).append(", purchaseOrderNumber=").append(purchaseOrderNumber)
					.append(", productPartCode=").append(productPartCode).append(", voucherType=").append(voucherType)
					.append(", resellerAccountNumber=").append(resellerAccountNumber)
					.append(", downloadFileNameFormat=").append(downloadFileNameFormat).append(", downloadFormat=")
					.append(downloadFormat).append(", passwordToEncrypt=").append(passwordToEncrypt)
					.append(", ftpDetails=").append(ftpDetails).append("]");
			return builder.toString();
		}

		public RowInfo(String packageName, String pinUsageMode, String pinLockType, int pinLengthWithoutPrefix, long numberOfPin, 
				int voucherCount, String downloadFileNameFormat, String downloadFormat, String passwordToEncrypt, String ftpDetails, String resellerAccountNumber) {
			this.packageName = packageName;
			this.pinUsageMode =pinUsageMode ;
			this.pinLockType =pinLockType ;
			this.pinLengthWithoutPrefix =pinLengthWithoutPrefix ;
			this.numberOfPin =numberOfPin ;
			this.voucherCount =voucherCount ;
			this.downloadFileNameFormat =downloadFileNameFormat ;
			this.downloadFormat =downloadFormat ;
			this.passwordToEncrypt =passwordToEncrypt ;
			this.ftpDetails =ftpDetails ;
			this.resellerAccountNumber = resellerAccountNumber;
		}
		
		public RowInfo(HashMap<String,String> paramValues) {
			this.ftpDetails = paramValues.get("FTPDETAILS") ;
			this.packageName = paramValues.get("PKGNAME").trim();
			this.pinUsageMode = paramValues.get("PINUSGMODE") ;
			this.pinLockType = paramValues.get("PINLOCKTYPE") ;
			if(!BTSLUtil.isNullString(paramValues.get("PINLENGTH")))
				this.pinLengthWithoutPrefix = Integer.parseInt(paramValues.get("PINLENGTH").trim()) ;
			if(!BTSLUtil.isNullString(paramValues.get("NUMOFPIN")))
				this.numberOfPin = Long.parseLong(paramValues.get("NUMOFPIN")) ;
			if(!BTSLUtil.isNullString(paramValues.get("VCHRCOUNT")))
				this.voucherCount = Long.parseLong(paramValues.get("VCHRCOUNT").trim()) ;
			this.downloadFileNameFormat = paramValues.get("FILENAMEFRMT") ;
			this.downloadFormat = paramValues.get("ENCRYPTFRMT") ;
			this.passwordToEncrypt = paramValues.get("PASSWORD") ;			
			this.resellerAccountNumber = paramValues.get("RESELLACCNUM").trim();
			this.batchType = paramValues.get("BATCHTYPE");
			this.printable = paramValues.get("PRINTPIN");
			this.primaryProductCode = paramValues.get("PRIMPRODCODE");
			this.secondaryTxnId = paramValues.get("SECTXNID").trim();
			this.purchaseOrderNumber = paramValues.get("PURCHASEORDNUM").trim();
			this.productPartCode = paramValues.get("PRODPARTCODE");
			this.voucherType = paramValues.get("VOUCHERTYPE");
		}
		public String getPackageName() {
			return packageName;
		}
		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}
		public String getPinUsageMode() {
			return pinUsageMode;
		}
		public void setPinUsageMode(String pinUsageMode) {
			this.pinUsageMode = pinUsageMode;
		}
		public String getBatchType() {
			return batchType;
		}
		public void setBatchType(String batchType) {
			this.batchType = batchType;
		}
		public String getPrintable() {
			return printable;
		}
		public void setPrintable(String printable) {
			this.printable = printable;
		}
		public String getPrimaryProductCode() {
			return primaryProductCode;
		}
		public void setPrimaryProductCode(String primaryProductCode) {
			this.primaryProductCode = primaryProductCode;
		}
		public String getSecondaryTxnId() {
			return secondaryTxnId;
		}
		public void setSecondaryTxnId(String secondaryTxnId) {
			this.secondaryTxnId = secondaryTxnId;
		}
		public String getPinLockType() {
			return pinLockType;
		}
		public void setPinLockType(String pinLockType) {
			this.pinLockType = pinLockType;
		}
		public int getPinLengthWithoutPrefix() {
			return pinLengthWithoutPrefix;
		}
		public void setPinLengthWithoutPrefix(int pinLengthWithoutPrefix) {
			this.pinLengthWithoutPrefix = pinLengthWithoutPrefix;
		}
		public long getNumberOfPin() {
			return numberOfPin;
		}
		public void setNumberOfPin(long numberOfPin) {
			this.numberOfPin = numberOfPin;
		}
		public String getPurchaseOrderNumber() {
			return purchaseOrderNumber;
		}
		public void setPurchaseOrderNumber(String purchaseOrderNumber) {
			this.purchaseOrderNumber = purchaseOrderNumber;
		}
		public String getProductPartCode() {
			return productPartCode;
		}
		public void setProductPartCode(String productPartCode) {
			this.productPartCode = productPartCode;
		}
		public String getVoucherType() {
			return voucherType;
		}
		public void setVoucherType(String voucherType) {
			this.voucherType = voucherType;
		}
		public String getResellerAccountNumber() {
			return resellerAccountNumber;
		}
		public void setResellerAccountNumber(String resellerAccountNumber) {
			this.resellerAccountNumber = resellerAccountNumber;
		}
		public String getDownloadFileNameFormat() {
			return downloadFileNameFormat;
		}
		public void setDownloadFileNameFormat(String downloadFileNameFormat) {
			this.downloadFileNameFormat = downloadFileNameFormat;
		}
		public String getDownloadFormat() {
			return downloadFormat;
		}
		public void setDownloadFormat(String downloadFormat) {
			this.downloadFormat = downloadFormat;
		}
		public String getPasswordToEncrypt() {
			return passwordToEncrypt;
		}
		public void setPasswordToEncrypt(String passwordToEncrypt) {
			this.passwordToEncrypt = passwordToEncrypt;
		}
		public String getFtpDetails() {
			return ftpDetails;
		}
		public void setFtpDetails(String ftpDetails) {
			this.ftpDetails = ftpDetails;
		}
		public long getVoucherCount() {
			return voucherCount;
		}
		public void setVoucherCount(long voucherCount) {
			this.voucherCount = voucherCount;
		}
		public long getTotalVoucherCount() {
			return totalVoucherCount;
		}
		public void setTotalVoucherCount(long totalVoucherCount) {
			this.totalVoucherCount = totalVoucherCount;
		}
		
	}
    

	private static void moveFilesToFinalDirectory(String oldDirectoryPath,String finalDirectoryPath,String fileName,String newName) throws BTSLBaseException{
        final String methodName = "moveFilesToFinalDirectory";
        if (_log.isDebugEnabled())
                _log.debug(methodName," Entered: p_oldDirectoryPath="+oldDirectoryPath+" p_finalDirectoryPath="+finalDirectoryPath+"fileName ="+fileName+"newName ="+newName);

        String oldFileName=null;
        String newFileName=null;
        File oldFile=null;
        File newFile=null;
        File parentDir = new File(finalDirectoryPath);
        if(!parentDir.exists())
                parentDir.mkdirs();
        //child directory name includes a file name and being processed date, month and year
        File oldDir = new File(oldDirectoryPath);
        File newDir = new File(finalDirectoryPath);
        if(!newDir.exists())
                newDir.mkdirs();
        if(_log.isDebugEnabled())
            _log.debug(methodName, " newDirName=" + finalDirectoryPath);
        try{
                oldFileName = oldDirectoryPath+fileName;
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(oldDirectoryPath, finalDirectoryPath).replace(fileName, newName);
                newFile = new File(newFileName);
                if(oldFile != null){
                        oldFile.renameTo(newFile);
                        if (_log.isDebugEnabled())
                                _log.debug(methodName," File " + oldFileName + " is moved to " + newFileName);
                }else{
                        if (_log.isDebugEnabled()){
                                _log.debug(methodName," File" + oldFileName + " is null");
                        }
                }
            _log.debug(methodName," File "+oldFileName+" is moved to "+newFileName);
        }
        catch(Exception e){
                _log.error(methodName, "Exception " + e.getMessage());
        _log.errorTrace(methodName,e);
        }finally{
        if(oldFile != null) 
        	oldFile = null;
        if(newFile != null) 
            newFile = null;
        if(parentDir != null) 
        	parentDir = null;
        if(newDir != null) 
        	newDir = null;
        if(oldDir != null) 
        	oldDir = null;
    if (_log.isDebugEnabled())
        _log.debug(methodName, "Exiting.. ");
} // end of finally
}

	public static String createDirectory(){
	String methodName="createDirectory";
        String dir = Constants.getProperty("FinalPackageO2CInitiationFilePath");
        String dirName = null;
        String completeFinalDirPath = null;
        try{
                dirName = (((BTSLUtil.getDateTimeStringFromDate(new Date())).replace("/","")).replace(":","")).replace(" ","_");
                completeFinalDirPath = dir + dirName;
                File file = new File(completeFinalDirPath);
                if (!file.exists()) {
                        if (file.mkdir()) {
                        	_log.debug(methodName,completeFinalDirPath + " directory is created!");
                        } else {
                        	_log.debug(methodName,"Failed to create directory :- " + completeFinalDirPath);
                        }
                }
        }catch(ParseException e){
               _log.error(methodName, "Exception: "+e.getMessage());
        }finally{
                 if (_log.isDebugEnabled())
                         _log.debug(methodName, "Exiting.. finalDirectoryName :: " + completeFinalDirPath);   
        }
        return completeFinalDirPath;
	}
	
	public static void writeToFile(String finalDirectoryPath, List<String> statusList, String fileName){
        if (_log.isDebugEnabled())
                        _log.debug("writeToFile","Entered with FinalDirectoryPath ::"+finalDirectoryPath+"statusList size"+statusList.size()+" File Name :: "+fileName);
        String sucFileName = null;
        String failFileName = null;
        String message = null;
        Date d = new Date();
        String date = d.toString();
        String[] statusListArr = new String[9];
        String successDir = Constants.getProperty("O2CPACKAGESUCCESS");
        String failDir = Constants.getProperty("O2CPACKAGEFAIL");
        	
//        sucFileName = finalDirectoryPath+"/"+fileName.split("[.]")[0]+"_Success."+batchO2CProperties.getProperty("FILE_EXT_SUCCESS");
//        failFileName = finalDirectoryPath+"/"+fileName.split("[.]")[0]+"_Fail."+batchO2CProperties.getProperty("FILE_EXT_FAIL");
//        sucFileName = finalDirectoryPath+"\\"+ fileName.split("[.]")[0] + "_Success.csv";
//        failFileName = finalDirectoryPath+"\\"+fileName.split("[.]")[0]+"_Fail.csv";
	      sucFileName = successDir+"/"+ fileName.split("[.]")[0] + "_Success.csv";
	      failFileName = failDir+"/"+fileName.split("[.]")[0] + "_Fail.csv";
 
        
        try{
        	PrintWriter sucWriter = null;
        	PrintWriter failWriter = null;
                String status = null;
                Locale locale=new Locale("en","US");
                int statusListSizes=statusList.size();
                String header = "Purchase Order Number,Reseller Account Number,Package Name,Package Count,Response Code,Response Message,Transaction ID,Total Voucher";
               
                for(int statusCount=0; statusCount<statusListSizes; statusCount++){
                		statusListArr = statusList.get(statusCount).split("[|]",9);
                        status = statusListArr[0];
                        if(success.equals(status)){
                        	if(sucWriter == null) {
                        		sucWriter = new PrintWriter(sucFileName, "UTF-8");
                    			sucWriter.println(header);
                        	}
//                                message = (statusList.get(statusCount)).split("[|]")[0]+"|"+(statusList.get(statusCount)).split("[|]")[1]+"|O2C Package Association is successfull";
                                StringBuffer successMessage = new StringBuffer("");
                                successMessage.append(statusListArr[1]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[2]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[3]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[4]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[5]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[6]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[7]);
                                successMessage.append(",");
                                successMessage.append(statusListArr[8]);

                                sucWriter.println(successMessage);
                        }
                        else{
                        	if(failWriter == null) {
                        		failWriter = new PrintWriter(failFileName, "UTF-8");
                        		failWriter.println(header);
                        	}
//                                message = (statusList.get(statusCount)).split("[|]")[0]+"|"+(statusList.get(statusCount)).split("[|]")[1]+"|O2C Package Association has failed"+
//                        (statusList.get(statusCount)).split("[|]")[3];
                                StringBuffer failMessage = new StringBuffer("");
                                failMessage.append(statusListArr[1]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[2]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[3]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[4]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[5]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[6]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[7]);
                                failMessage.append(",");
                                failMessage.append(statusListArr[8]);
                               
                                failWriter.println(failMessage);
                        }
                }
                statusList.clear();
                if(sucWriter != null)
                	sucWriter.close();
                if(failWriter != null)
                	failWriter.close();
        }catch(Exception e){
                _log.errorTrace("writeToFile", e);
        }
    }
	
	private static class Messages{
		static String VOUCHER_UPDATE_SUCCESS = "Updated vouchers successfully, FTP done"  ;//0
		static String PARAMETERS_MISMATCH = "Input File Exception ::";//-1
		static String ERROR_ASSOCIATING= "Failed to associate packages :: " ;//-2
		static String PACKAGE_NA = "Package is not available in VMS system, please create." ;//-3
		static String ADD_CHANNEL_TRANSFER_FAIL= "Failed to update information" ;//-4
		static String VOUCHER_UPDATE_FAIL = "Failed to update vouchers" ;//-5
		static String INSUFFICIENT_VOUCHERS = " Vouchers failed to be generated ";//-6
		static String RESELLER_NA= "Vendor Account Number Not Available";//-7
		static String FILE_GEN_FAIL = "File Generation Failed, transaction rollbacked";//-8
		static String DUPLICATE_PACKAGE = "Package has been duplicated in the file";//-9
		static String FTP_FAIL = "FTP Failed, transaction rollbacked";//-10
		static String VOUCHER_COUNT_MISMATCH = "Package vouchers in system does not match given input";//-11
		static String VOU_CREATION_FAIL = "New Batch or Voucher Creation failed ";//-12
		static String ZERO = "0";
		static String ONE = "-1";
		static String TWO = "-2";
		static String THREE = "-3";
		static String FOUR = "-4";
		static String FIVE = "-5";
		static String SIX = "-6";
		static String SEVEN = "-7";
		static String EIGHT = "-8";
		static String NINE = "-9";
		static String TEN = "-10";
		static String ELEVEN = "-11";
		static String TWELVE = "-12";
		static String THIRTEEN = "-13";
	}
	
	private static void formatSetStatusMessage(String status, String purchaseOrderNo, String resellerACNo, String packageNm, String packageCount, String responseCode,
			String responseMessage, String transactionID, String totalVouchers) {
		String delim = "|";
		StringBuffer statusMessage = new StringBuffer();
		statusMessage.append(status);
		statusMessage.append(delim);
		statusMessage.append(purchaseOrderNo);
		statusMessage.append(delim);
		statusMessage.append(resellerACNo);
		statusMessage.append(delim);
		statusMessage.append(packageNm);
		statusMessage.append(delim);
		statusMessage.append(packageCount);
		statusMessage.append(delim);
		statusMessage.append(responseCode);
		statusMessage.append(delim);
		statusMessage.append(responseMessage);
		statusMessage.append(delim);
		statusMessage.append(transactionID);
		statusMessage.append(delim);
		statusMessage.append(totalVouchers);
		statusList.add(statusMessage.toString());
	}
	
	private static void captureThreads() {
		final String methodName = "captureThreads";
		_log.debug(methodName,"Entered");
		Set<Thread> threads = null;

        threads = Thread.getAllStackTraces().keySet();
        _log.info(methodName, "in action:"+threads.size());
        for (Thread t : threads) {
            String name = t.getName();
            String className = t.getClass().getName(); 
            Thread.State state = t.getState();
            int priority = t.getPriority();
            String type = t.isDaemon() ? "Daemon" : "Normal";
            System.out.printf("%-20s \t %s \t %d \t %s \t %s\n", name, state, priority,className, type);
        }
       
	}
}
