package com.btsl.pretups.processes;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListSorterUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.adjustments.businesslogic.AdjustmentsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.BatchModifyCommissionProfileVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.txn.pretups.channel.profile.businesslogic.CommissionProfileTxnDAO;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;
import com.web.pretups.channel.profile.web.CommissionProfileForm;

/**
 * Class for pushing messages to users regarding OTF details 
 * @author parul.nagpal
 *
 */
public class TargetBasedCommissionMessages{
	private static Log logger = LogFactory.getLog(TargetBasedCommissionMessages.class.getName());
	private static final  String BTSLBASEEXCEPTION = "BTSLBaseException : ";
    private static final  String CLASSNAME = "TargetBasedCommissionMessages";
    private static final String EXCEPTION= "Exception";
    private static final String EXTRA =" extra ";
    private static final String FROM =" from ";
    private static final String ABOVE =" above ";
    private static final String TO =" to ";
    private static final String MSSG1=" : On Recharge value of ";
    private static final String MSSG2=" transactions in this range will give you ";
    private static final String MSSG3=" :you will get Rs ";
    private static final String MSSG4=" :you will get ";
    private static final String FUNCTIONNAME="loadCommissionProfileDetailsForOTFMessages";
    
	
	public static void main(String[] args){
		final String methodName="main"; 
	
		LogFactory.printLog(methodName, PretupsI.ENTERED, logger);
		try {
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception ex) {
            logger.errorTrace(methodName, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
		try{
			new TargetBasedCommissionMessages().process();
		} catch (BTSLBaseException be) {
            logger.error(methodName, BTSLBASEEXCEPTION + be.getMessage());
            logger.errorTrace(methodName, be);
        } finally {
			LogFactory.printLog(methodName, PretupsI.EXITED , logger);
            ConfigServlet.destroyProcessCache();
		}
		
	}
	
	/**
	 * method process called from main for offline messages
	 * @throws BTSLBaseException
	 */
	private void process() throws BTSLBaseException{
		final String methodName="process";
		Connection con=null;
		boolean statusOk = false;
	    Date processedUpto = null;
        final ProcessStatusDAO processDAO = new ProcessStatusDAO();
	    ProcessStatusVO processVO = null;
	    Date currentDate = null;
		try{
        currentDate = new Date();
        currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
        con = OracleUtil.getSingleConnection();
        if (con == null) {
           LogFactory.printLog(methodName, " DATABASE Connection is NULL. ", logger);
           throw new BTSLBaseException(CLASSNAME, methodName, "Not able to get the connection.");
        }
        final ProcessBL processBL = new ProcessBL();
       
        processVO = processBL.checkProcessUnderProcess(con, ProcessI.TARGET_BASED_MESSAGES_PROCESS);
        statusOk = processVO.isStatusOkBool();
        if (statusOk) {
            con.commit();
            processedUpto = processVO.getExecutedUpto();
            processedUpto= BTSLUtil.getSQLDateFromUtilDate(processedUpto);
            if (processedUpto != null) {
            	 final int diffDate = BTSLUtil.getDifferenceInUtilDates(processedUpto, currentDate);
                 if (diffDate <= 0) {
                     logger.error(methodName, " Process already executed.....");
                     throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.TARBASEDCOMM_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                 }
                 
                 if(!((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,processVO.getNetworkCode()) || (Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,processVO.getNetworkCode()))){
					 logger.error(methodName, " No details of OTF in commission profiles.....");
                     throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.TARBASEDCOMM_PROCESS_NO_OTF_DETAILS_FOUND);
                 }
            	loadProfiles();
            	processVO.setExecutedOn(new Date());
                processVO.setExecutedUpto(new Date());
                if (processDAO.updateProcessDetail(con, processVO) > 0) {
                    con.commit();
                }
            } else {
                throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.TARBASEDCOMM_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } else {
            throw new BTSLBaseException(CLASSNAME,methodName, "Process is already running..");
        }
		}catch (BTSLBaseException bse) {
            logger.errorTrace(methodName, bse);
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
        }
        finally{
        	if (statusOk) {
        		processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        		try {
        			if (processDAO.updateProcessDetail(con, processVO) > 0) {
        				con.commit();
        			} else {
        				con.rollback();
        			}
        		} catch (Exception e) {
        			logger.error(methodName, " Exception in update process detail" + e.getMessage());
        			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TargetBasedCommissionMessages[process] ", "", "", "",
                    "Exception in update process detail for Process ID=" + ProcessI.TARGET_BASED_MESSAGES_PROCESS + " :" + e.getMessage());
        			logger.errorTrace(methodName, e);
        		}
        	}
        	OracleUtil.closeQuietly(con);
        	EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "TargetBasedCommissionMessages[process]","","",""," [TargetBasedCommissionMessages Process is Completed.");
        	try {
        		Thread.sleep(5000);
        	} catch (Exception e) {
        		logger.errorTrace(methodName, e);
        	}
        	ConfigServlet.destroyProcessCache();
        }
	
	}
	
	/**
	 * method called from process() which further calls loadDetails() for loading currently applicable profiles
	 */
	public void loadProfiles(){
		final String methodName="loadProfiles";
		List<UserPhoneVO> msisdnList=new ArrayList<>();
		try{
			boolean flag=false;
			HashMap<String, List<CommissionProfileSetVO>> map = loadCommissionProfile();
			String[] set=map.keySet().toArray(new String[0]);
			int setLength = set.length;
			for(int i=0;i< setLength;i++){
				msisdnList=loadMsisdnOfUsers(set[i]);
				if(!msisdnList.isEmpty()){
					int msisdnListSize = msisdnList.size();
					for(int j=0;j< msisdnListSize;j++){
							flag=loadDetails(set[i],msisdnList.get(j).getNetworkCode());
							if(flag){
							String[] args = {};
	    	    			final TargetBasedCommissionMessagesThread mrt = new TargetBasedCommissionMessagesThread(PretupsErrorCodesI.TARGET_BASED_MESSAGES,args,msisdnList.get(j).getUserId());
	    	    			final Thread t = new Thread(mrt);
	    	    			t.start();
							}
					}
				}
			}
		}catch (Exception e) {
        	logger.error(methodName, EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        }finally {
        	LogFactory.printLog(methodName,PretupsI.EXITED , logger);
        }
	}
	
	/**
	 * method loads the currently applicable commission profiles 
	 * @return
	 * @throws BTSLBaseException
	 */
	public HashMap<String, List<CommissionProfileSetVO>> loadCommissionProfile()throws BTSLBaseException{
        final String methodName = "loadCommissionProfile";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final HashMap<String, List<CommissionProfileSetVO>> map = new HashMap<>();
        List<CommissionProfileSetVO> commProfileList = new ArrayList<>();

        final StringBuilder selectQueryBuff = new StringBuilder("SELECT cpsv.comm_profile_set_version ,cpsv.comm_profile_set_id,cpsv.applicable_from");
        selectQueryBuff.append(" from commission_profile_set cps , commission_profile_set_version cpsv ");
        selectQueryBuff.append(" where cpsv.comm_profile_set_id=cps.comm_profile_set_id and (applicable_from>=? or applicable_from = (SELECT MAX(cpv.applicable_from) ");
        selectQueryBuff.append(" FROM commission_profile_set_version cpv where cpv.comm_profile_set_id=cpsv.comm_profile_set_id and cpv.applicable_from<=? ");
        selectQueryBuff.append(" group by cpv.comm_profile_set_id )) and cps.status not in ('N','S') order by cpsv.comm_profile_set_id DESC ");
        final String sqlSelect = selectQueryBuff.toString();
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            final Date date = new Date();
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(date));
            pstmt.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));
            rs = pstmt.executeQuery();
            CommissionProfileSetVO commprofileVO = null;
            String prevSetId = null;
            while (rs.next()) {
                commprofileVO = new CommissionProfileSetVO();
                final String currSetId = rs.getString("comm_profile_set_id");
                if (!(currSetId.equals(prevSetId)) && (prevSetId != null)) {
                    map.put(prevSetId, commProfileList);
                    commProfileList = new ArrayList<>();
                }
                commprofileVO.setCommProfileSetId(rs.getString("comm_profile_set_id"));
                commprofileVO.setCommProfileVersion(rs.getString("comm_profile_set_version"));
                commprofileVO.setApplicableFrom(rs.getTimestamp("applicable_from"));
                commProfileList.add(commprofileVO);
                prevSetId = currSetId;
            }
            map.put(prevSetId, commProfileList);
        } catch (SQLException sqe) {
        	logger.error(methodName, "SQLException : " + sqe);
        	logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TargetBasedCommissionMessages[loadCommissionProfile]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
        	logger.error(methodName,EXCEPTION + ex);
        	logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "TargetBasedCommissionMessages[loadCommissionProfile]", "",
                "", "", EXCEPTION + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		logger.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	logger.error("An error occurred closing statement.", e);
            }
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#loadCommissionProfile");
				mcomCon = null;
			}
        }

        return map;
	}
	
	/**
	 * method loads msisdn of users based on set id
	 * @param commProfileSetVO
	 * @return list<UserPhoneVO>
	 */
    public List<UserPhoneVO> loadMsisdnOfUsers(String setId){
    	 final String methodName = "loadMsisdnOfUsers";
         PreparedStatement pstmtSelect = null;
         ResultSet rs = null;
         Connection con=null;
         MComConnectionI mcomCon = null;
         List<UserPhoneVO> list = new ArrayList<>();
         UserPhoneVO userPhoneVO =null;
         try {
        	 mcomCon = new MComConnection();
        	 con=mcomCon.getConnection();
         	final StringBuilder strBuffSelectCProfileProdDetail = new StringBuilder("select up.msisdn,up.phone_language,up.country,up.user_id,u.network_code from user_phones up,channel_users ch,users u" );
         	strBuffSelectCProfileProdDetail.append(" where up.user_id=ch.user_id and u.user_id=ch.user_id and u.user_id=up.user_id and ch.comm_profile_set_id= ? and u.status in (?,?) and up.primary_number=? ");
         	String selectQuery = strBuffSelectCProfileProdDetail.toString();
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, setId);
            pstmtSelect.setString(2,PretupsI.YES);
            pstmtSelect.setString(3, PretupsI.USER_STATUS_PREACTIVE);
            pstmtSelect.setString(4, PretupsI.YES);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
            	userPhoneVO = new UserPhoneVO();
            	userPhoneVO.setMsisdn(rs.getString("msisdn"));
            	userPhoneVO.setPhoneLanguage(rs.getString("phone_language"));
            	userPhoneVO.setCountry(rs.getString("country"));
            	userPhoneVO.setUserId(rs.getString("user_id"));
            	userPhoneVO.setNetworkCode(rs.getString("network_code"));
            	list.add(userPhoneVO);
            }
           
         }catch(SQLException sqle) {
         	logger.errorTrace(methodName, sqle);
         }catch (Exception e) {
         	logger.errorTrace(methodName, e);
         }finally {
        	 try{
         		if (rs!= null){
         			rs.close();
         		}
         	}
         	catch (SQLException e){
         		logger.error("An error occurred closing result set.", e);
         	}
        	 try{
             	if (pstmtSelect!= null){
             		pstmtSelect.close();
             	}
             }
             catch (SQLException e){
             	logger.error("An error occurred closing statement.", e);
             }
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#loadMsisdnOfUsers");
				mcomCon = null;
			}
         }
		return list;
    }
    
    
    /**
     * method load details of additional commission profile
     * @param setId
     * @param latesVersion
     * @param netwkId
     * @return list<AdditionalProfileCombinedVO>
     * @throws BTSLBaseException
     */
    public List<AdditionalProfileCombinedVO> loadDetailsOtfAddCommProfile(String setId,String latesVersion,String netwkId )throws   BTSLBaseException{
        final String methodName = "loadDetailsOtfAddCommProfile";
        LogFactory.printLog(methodName, PretupsI.ENTERED, logger);
        Connection con=null;
        MComConnectionI mcomCon = null;

        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
        	ListSorterUtil sort = new ListSorterUtil();	
        	CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
        	List<AdditionalProfileServicesVO> serviceList = commissionProfileWebDAO.loadAdditionalProfileServicesList(con, setId, latesVersion);
            List<AdditionalProfileCombinedVO> additionalList = new ArrayList<>();

            if (serviceList != null && !serviceList.isEmpty()) {
                AdditionalProfileServicesVO additionalProfileServicesVO = null;
                AdditionalProfileCombinedVO additionalProfileCombinedVO = null;
                AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
               
                ArrayList addProfileDetailList = null;
                List<OTFDetailsVO> otfDetailList = null;
                AdditionalProfileDeatilsVO aprdvo=null;
                int serviceLists = serviceList.size();
                for (int i = 0, j = serviceLists; i < j; i++) {
                    
                	additionalProfileServicesVO = serviceList.get(i);
                    addProfileDetailList = commissionProfileWebDAO.loadAdditionalProfileDetailList(con, additionalProfileServicesVO.getCommProfileServiceTypeID(),netwkId);
                    if (!addProfileDetailList.isEmpty()) {

                        additionalProfileCombinedVO = new AdditionalProfileCombinedVO();
                        additionalProfileCombinedVO.setAdditionalProfileServicesVO(additionalProfileServicesVO);
                        if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,netwkId)){
                        int addProfileDetailLists = addProfileDetailList.size();
                        for(int k =0;k< addProfileDetailLists;k++){
                        	aprdvo = (AdditionalProfileDeatilsVO) addProfileDetailList.get(k);
                        	addProfileDetailList.remove(k);
                        	otfDetailList = commissionProfileWebDAO.loadProfileOtfDetailList(con, aprdvo.getAddCommProfileDetailID(),aprdvo.getOtfType(),PretupsI.COMM_TYPE_ADNLCOMM);
                        	aprdvo.setOtfDetails(otfDetailList);
                        	aprdvo.setOtfDetailsSize(otfDetailList.size());
                        	addProfileDetailList.add(k,aprdvo);
                        	
                        }
                        }
                        addProfileDetailList = (ArrayList) sort.doSort("startRange", null, addProfileDetailList);
                        
                        additionalProfileCombinedVO.setSlabsList(addProfileDetailList);
                        additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
                        additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) addProfileDetailList.get(0);
                        additionalProfileServicesVO.setAddtnlComStatus(additionalProfileDeatilsVO.getAddtnlComStatus());
                        additionalProfileServicesVO.setAddtnlComStatusName(additionalProfileDeatilsVO.getAddtnlComStatusName());
                        additionalList.add(additionalProfileCombinedVO);
                    }
                }
            }

        	return additionalList;
        	
        }catch (Exception ex) {
        	logger.error(methodName, EXCEPTION + ex);
        	logger.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        }finally {
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#loadDetailsOtfAddCommProfile");
				mcomCon = null;
			}
	           LogFactory.printLog(methodName, PretupsI.EXITED , logger);
        }
    }
    
    
    /**
     * method load details of base commission profile
     * @param setId
     * @param latestVersion
     * @param netwkId
     * @return list<CommissionProfileCombinedVO>
     * @throws BTSLBaseException
     */
    public List<CommissionProfileCombinedVO> loadDetailsOtfBaseCommProfile(String setId,String latestVersion,String netwkId ) throws  BTSLBaseException{
        final String methodName = "loadDetailsOtfBaseCommProfile";
        LogFactory.printLog(methodName, PretupsI.ENTERED, logger);
        Connection con=null;
        MComConnectionI mcomCon = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
        	CommissionProfileDAO commissionProfileDAO =null;
        	ListSorterUtil sort = new ListSorterUtil();	
        	commissionProfileDAO = new CommissionProfileDAO();
        	CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
        	ArrayList<CommissionProfileProductsVO> productList = commissionProfileDAO.loadCommissionProfileProductsList(con, setId, latestVersion, PretupsI.ALL, PretupsI.ALL);
        	List<CommissionProfileCombinedVO> commissionList = new ArrayList<>();
        	
        	if (productList != null && !productList.isEmpty()) {
           
        		CommissionProfileProductsVO commissionProfileProductsVO = null;
        		CommissionProfileCombinedVO commissionProfileCombinedVO = null;
           
        		List<OTFDetailsVO> otfDetailList =new ArrayList<>();
        		CommissionProfileDeatilsVO cpdvo=null;
        		ArrayList<CommissionProfileDeatilsVO> commProfileDetailList = new ArrayList<>() ;
        		int   productLists = productList.size();
        		for (int i = 0, j = productLists; i < j; i++) {
        			
        			commissionProfileProductsVO = productList.get(i);
        			commProfileDetailList = commissionProfileWebDAO.loadCommissionProfileDetailList(con, commissionProfileProductsVO.getCommProfileProductID(),netwkId);
        			commissionProfileCombinedVO = new CommissionProfileCombinedVO();
        			commissionProfileCombinedVO.setCommissionProfileProductVO(commissionProfileProductsVO);
        			int commProfileDetailLists = commProfileDetailList.size();
        			for(int k =0;k< commProfileDetailLists;k++){
        				
        				cpdvo = commProfileDetailList.get(k);
        				commProfileDetailList.remove(k);
        				otfDetailList = commissionProfileWebDAO.loadProfileOtfDetailList(con, cpdvo.getCommProfileDetailID(),PretupsI.AMOUNT_TYPE_AMOUNT,PretupsI.COMM_TYPE_BASECOMM);
        				cpdvo.setOtfDetails(otfDetailList);
        				cpdvo.setOtfDetailsSize(otfDetailList.size());
        				commProfileDetailList.add(k,cpdvo);
                	
        			}
        			commProfileDetailList = (ArrayList) sort.doSort("startRange", null, commProfileDetailList);
        			commissionProfileCombinedVO.setSlabsList(commProfileDetailList);
        			commissionList.add(commissionProfileCombinedVO);
        		}
        	}
        	
        	return commissionList;
        } catch (Exception e) {
        	logger.error(methodName, EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
            throw new BTSLBaseException(CLASSNAME, methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#loadDetailsOtfBaseCommProfile");
				mcomCon = null;
			}
            LogFactory.printLog(methodName,  PretupsI.EXITED  , logger);
        }
    }
	
    //association tym or creation tym i.e single user
    /**
     * method is called while creating,approving channel user and associating profile to channel user
     * @param con
     * @param channelUserVO
     * @throws BTSLBaseException
     */
    public void loadCommissionProfileDetailsForOTFMessages(Connection con, ChannelUserVO channelUserVO ) throws BTSLBaseException {
    	boolean flag=false;
    	try{
    		flag=loadDetails( channelUserVO.getCommissionProfileSetID(), channelUserVO.getNetworkID());
    		if(flag){
    		String[] args = {};
    		final TargetBasedCommissionMessagesThread mrt = new TargetBasedCommissionMessagesThread(PretupsErrorCodesI.TARGET_BASED_MESSAGES,args,channelUserVO.getUserID());
    		mrt.run();
    		}
    	}catch (Exception e) {
    	logger.error(FUNCTIONNAME, EXCEPTION + e.getMessage());
    	logger.errorTrace(FUNCTIONNAME, e);
    	}finally {
    		LogFactory.printLog(FUNCTIONNAME,  PretupsI.EXITED  , logger);
    	}
    }

    
    //approval tym of batch user
    /**
     * method is called while creating,approving channel user and associating profile to channel user
     * @param con
     * @param channelUserVO
     * @throws BTSLBaseException
     */
    public void loadCommissionProfileDetailsForOTFMessages(Connection con, List<ChannelUserVO> sortedList) throws BTSLBaseException {
    	boolean flag=false;
    	try{
    		int sortedLists = sortedList.size();
    		for(int i=0;i< sortedLists;i++)
    		{
    			ChannelUserVO channelUserVO =new ChannelUserVO() ;
    			channelUserVO = sortedList.get(i);
    			if(PretupsI.ACTIVE.equalsIgnoreCase(channelUserVO.getStatus())||PretupsI.USER_STATUS_PREACTIVE.equalsIgnoreCase(channelUserVO.getStatus()))
    			{
    			flag=loadDetails( channelUserVO.getCommissionProfileSetID(), channelUserVO.getNetworkID());
    			if(flag){
    			String[] args = { };
    			final TargetBasedCommissionMessagesThread mrt = new TargetBasedCommissionMessagesThread(PretupsErrorCodesI.TARGET_BASED_MESSAGES,args,channelUserVO.getUserID());
    			final Thread t = new Thread(mrt);
        		t.start();
    			}
    			}
    		}
	
    	}catch (Exception e) {
    	logger.error(FUNCTIONNAME, EXCEPTION + e.getMessage());
    	logger.errorTrace(FUNCTIONNAME, e);
    	}finally {
    		LogFactory.printLog(FUNCTIONNAME,  PretupsI.EXITED  , logger);
    	}
    }
    
    // modifying comm profile i.e bulk users
    /**
     * method is called while modifying commission profile from ui
     * @param con
     * @param setVO
     * @param userVO
     * @param form
     * @throws BTSLBaseException
     */
    public void loadCommissionProfileDetailsForOTFMessages(Connection con, CommissionProfileSetVO setVO,CommissionProfileForm form) throws BTSLBaseException {
    	List<CommissionProfileCombinedVO>  commissionProfileDeatilsList = new ArrayList<>();
    	List<AdditionalProfileCombinedVO> addcommissionProfileDeatilsList =new ArrayList<>();
    	List<UserPhoneVO> msisdnList =new ArrayList<>();
    	boolean baseOTFFlag =false;
    	boolean addOTFFlag =false;
    	try{
    	msisdnList=loadMsisdnOfUsers(setVO.getCommProfileSetId());
    	commissionProfileDeatilsList=form.getCommissionProfileList();
    	addcommissionProfileDeatilsList=form.getAdditionalProfileList();
    	//StringBuilder builder = new StringBuilder("You are eligible for extra benefits:: ");
    	if(commissionProfileDeatilsList!=null){
    		baseOTFFlag=checkBaseOTF(commissionProfileDeatilsList);
    		//builder=createMessageforBaseSlab(builder, commissionProfileDeatilsList,form.getApplicableFromDate());
    	}
    	
    	if(!baseOTFFlag && addcommissionProfileDeatilsList!=null){
    		addOTFFlag=checkAddOTF(addcommissionProfileDeatilsList);
    		//builder=createMessageforAddSlab(builder, addcommissionProfileDeatilsList ,form.getApplicableFromDate());
    	}
    	
    	if(baseOTFFlag || addOTFFlag){
    	String[] args = {};
    	int msisdnLists = msisdnList.size();
    	for(int p=0;p< msisdnLists;p++){
    		final TargetBasedCommissionMessagesThread mrt = new TargetBasedCommissionMessagesThread(PretupsErrorCodesI.TARGET_BASED_MESSAGES,args,msisdnList.get(p).getUserId());
    		final Thread t = new Thread(mrt);
    		t.start();
    	}
    	}
    	}catch (Exception e) {
        	logger.error(FUNCTIONNAME, EXCEPTION + e.getMessage());
        	logger.errorTrace(FUNCTIONNAME, e);
        }
        finally {
           LogFactory.printLog(FUNCTIONNAME, PretupsI.EXITED  , logger);
        }
  }
    	
    
    //modifying commission profile in bulk 
    /**
     * method is called while modifying commission profile in bulk
     * @param batchModifyCommProfileList
     * @throws BTSLBaseException
     */
    public void loadCommissionProfileDetailsForOTFMessages(Map<String, List<BatchModifyCommissionProfileVO>> map,Map<String, List<AdditionalProfileDeatilsVO>> map1) throws BTSLBaseException {
    	List<UserPhoneVO> msisdnList =new ArrayList<>();
    	List<BatchModifyCommissionProfileVO> batchModifyCommProfileList=new ArrayList<>();
    	List<AdditionalProfileDeatilsVO> batchModifyAddCommProfileList =new ArrayList<>();
    	boolean baseOTFFlag =false;
    	boolean addOTFFlag =false;
    	try{
	    	//StringBuilder builder = new StringBuilder("You are eligible for extra benefits: ");
    		String[] setId=map.keySet().toArray(new String[0]);
    		int setIdLength=setId.length;
    		for(int i=0;i< setIdLength;i++){
    			msisdnList=loadMsisdnOfUsers(setId[i].split(":")[0]);
    			batchModifyCommProfileList=map.get(setId[i]);
    			
    			if(batchModifyCommProfileList!=null && !batchModifyCommProfileList.isEmpty()){
    				baseOTFFlag=checkBaseBulkOTF(batchModifyCommProfileList);
    				//builder=createMessageforBulkBaseSlab(builder, batchModifyCommProfileList);
        		}
    			
    			if(!baseOTFFlag && map1!=null && map1.containsKey(setId[i])){
    				batchModifyAddCommProfileList=map1.get(setId[i]);
    				if(batchModifyAddCommProfileList!=null && !batchModifyAddCommProfileList.isEmpty()){
    					addOTFFlag=checkAddBulkOTF(batchModifyAddCommProfileList);
        				//builder=createMessageforBulkAddSlab(builder, batchModifyAddCommProfileList);
            		}
        			
    			}
    			
    			if(baseOTFFlag || addOTFFlag){
    			String[] args = { };
    			int msisdnListSize = msisdnList.size();
    	    	for(int p=0;p< msisdnListSize;p++){
    	    		final TargetBasedCommissionMessagesThread mrt = new TargetBasedCommissionMessagesThread(PretupsErrorCodesI.TARGET_BASED_MESSAGES,args,msisdnList.get(p).getUserId());
    	    		final Thread t = new Thread(mrt);
    	    		t.start();
    	    	}
    			}
    		}
    		
    	}catch (Exception e) {
        	logger.error(FUNCTIONNAME, EXCEPTION + e.getMessage());
        	logger.errorTrace(FUNCTIONNAME, e);
        }finally {
        	 LogFactory.printLog(FUNCTIONNAME, PretupsI.EXITED  , logger);
        }
  }
    
    /**
     * method loads the details of currently applicable profiles and returns the message formed
     * @param setId
     * @param networkCode
     * @return
     * @throws BTSLBaseException
     */
    public boolean loadDetails(String setId,String networkCode) throws BTSLBaseException{
    	final String methodName="loadDetails";
    	boolean flag=false;
    	Connection con=null;
    	MComConnectionI mcomCon = null;
    	Date appFrom =null;
    	boolean baseOTFFlag =false;
    	boolean addOTFFlag =false;
    	try{
    		mcomCon = new MComConnection();
    		con=mcomCon.getConnection();
    		List<AdditionalProfileCombinedVO> addcommissionProfileCombinedVOList =new ArrayList<>();
    		List<CommissionProfileCombinedVO> commissionProfileCombinedVOList =new ArrayList<>();
    		CommissionProfileTxnDAO commDao = new CommissionProfileTxnDAO();
    		Date currDate = new Date();
    		CommissionProfileSetVO commissionProfileSetVO = commDao.loadCommProfileSetDetails(con, setId, currDate);
    		String latestVersion =commissionProfileSetVO.getCommProfileVersion();
    		commissionProfileCombinedVOList=loadDetailsOtfBaseCommProfile(setId,latestVersion,networkCode);
    		appFrom= loadProfileApplicableFrom(setId,latestVersion);
    		//builder = builder.append("You are eligible for extra benefits:: ");
    		if(commissionProfileCombinedVOList!=null && !commissionProfileCombinedVOList.isEmpty()){
    			baseOTFFlag=checkBaseOTF(commissionProfileCombinedVOList);
    			//builder=createMessageforBaseSlab(builder,commissionProfileCombinedVOList,BTSLUtil.getDateStringFromDate(appFrom));
    		}
    		if(!baseOTFFlag){
    			addcommissionProfileCombinedVOList=loadDetailsOtfAddCommProfile(setId,latestVersion,networkCode);
    			if(addcommissionProfileCombinedVOList!=null && !addcommissionProfileCombinedVOList.isEmpty()){
    				addOTFFlag=checkAddOTF(addcommissionProfileCombinedVOList);
    				//builder=createMessageforAddSlab(builder,addcommissionProfileCombinedVOList,BTSLUtil.getDateStringFromDate(appFrom))	;
    			}
    		}
    		
    		if(baseOTFFlag || addOTFFlag){
    			flag=true;
    		}
    	}catch (Exception e) {
        	logger.error(methodName,EXCEPTION + e.getMessage());
        	logger.errorTrace(methodName, e);
        }finally {
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#loadDetails");
				mcomCon = null;
			}
        		LogFactory.printLog(methodName,  PretupsI.EXITED  , logger);
        }
    	return flag;
    }
    /**
     * method is called when O2C and C2C transactions are done
     * @param con
     * @param channelUserVO
     * @param otfAmount
     * @throws BTSLBaseException
     */
    public void loadBaseCommissionProfileDetailsForTargetMessages(Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {
    	String methodName="loadBaseCommissionProfileDetailsForTargetMessages";
        Log log = LogFactory.getLog(TargetBasedCommissionMessages.class.getName());

	
    	try{

   
    	    String messageKey = PretupsErrorCodesI.TARGET_BASED_MESSAGES_TRANSACTION;
    		   final BTSLMessages sendMessageToUser = new BTSLMessages(messageKey);
    		    UserDAO userDao = new UserDAO();
    		    UserPhoneVO phoneVO = new UserPhoneVO();
    		    Locale locale ;
    		    PushMessage pushMessage;
    			try {
    				phoneVO = userDao.loadUserPhoneVO(con, channelUserVO.getUserID());
    			} catch (BTSLBaseException e) {
    				log.errorTrace(methodName, e);
    			}
    			
    	        if(phoneVO!=null){
    	        	locale =new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
    	        	pushMessage =new PushMessage(phoneVO.getMsisdn(), sendMessageToUser, "", "", locale,"");
    	        }else{
    	        	locale =new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));	
    	        	pushMessage =new PushMessage(null, sendMessageToUser, "", "", locale,"");
    	        }
    	        
    		    pushMessage.push();
  
	
    	}catch (Exception e) {
    	logger.error(methodName, " Exception " + e.getMessage());
    	logger.errorTrace(methodName, e);
    	}// end of catch
    	finally {
    		LogFactory.printLog(methodName,  " Exiting " , logger);
    	}
    }
    public void loadBaseCommissionProfileDetailsForTargetMessages(Connection con, String userId, String[] msg) throws BTSLBaseException {
    	String methodName="loadBaseCommissionProfileDetailsForTargetMessages";
        Log log = LogFactory.getLog(TargetBasedCommissionMessages.class.getName());
    	try{
    	    String messageKey = PretupsErrorCodesI.TARGET_BASED_CBC_MESSAGES;
    		   final BTSLMessages sendMessageToUser = new BTSLMessages(messageKey, msg);
    		    UserDAO userDao = new UserDAO();
    		    UserPhoneVO phoneVO = new UserPhoneVO();
    		    Locale locale ;
    		    PushMessage pushMessage;
    			try {
    				phoneVO = userDao.loadUserPhoneVO(con, userId);
    			} catch (BTSLBaseException e) {
    				log.errorTrace(methodName, e);
    			}
    	        if(phoneVO!=null){
    	        	locale =new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
    	        	pushMessage =new PushMessage(phoneVO.getMsisdn(), sendMessageToUser, "", "", locale,"");
    	        }else{
    	        	locale =new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));	
    	        	pushMessage =new PushMessage(null, sendMessageToUser, "", "", locale,"");
    	        }
    		    pushMessage.push();
    	}catch (Exception e) {
    	logger.error(methodName, " Exception " + e.getMessage());
    	logger.errorTrace(methodName, e);
    	}// end of catch
    	finally {
    		LogFactory.printLog(methodName,  " Exiting " , logger);
    	}
    }

    
    /**
     * method creates message for additional commission slabs
     * @param builder
     * @param addcommissionProfileCombinedVOList
     * @return builder (message string)
     */
    private StringBuilder createMessageforAddTarget(StringBuilder builder , List<AdditionalProfileCombinedVO> addcommissionProfileCombinedVOList, AdjustmentsVO adjustmentVO){
    	List<AdditionalProfileDeatilsVO> list1 ;
    	List<OTFDetailsVO> otflist ;
    	builder.append(" Congratulations!! You have achieved your cumulative recharge target and you are eligible for commission amount of " +BTSLUtil.getDisplayAmount(adjustmentVO.getOtfAmount()));
		
		
		if(adjustmentVO.getOtfApplicableTo()!=null)
		{
		if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(adjustmentVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(BTSLUtil.getDisplayAmount(adjustmentVO.getOtfRate()))));
			builder.append(" extra for further transactions ");
		}
		if(PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(adjustmentVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(adjustmentVO.getOtfRate())));
			builder.append("% ").append("extra for further transactions ");
		}		
          	builder.append("till " +adjustmentVO.getOtfApplicableTo());
		}
		else
		{
		if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(adjustmentVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(BTSLUtil.getDisplayAmount(adjustmentVO.getOtfRate()))));
			builder.append(" extra for further transactions.");
		}
		if(PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(adjustmentVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(adjustmentVO.getOtfRate())));
			builder.append("% ").append("extra for further transactions.");
		}
		}


    	return builder;
    }
    /**
     * method creates message for base commission slabs when target is achieved
     * @param builder
     * @param commissionProfileCombinedVOList
     * @param otfAmount
     * @return builder (message string)
     */
    private StringBuilder createMessageforBaseTarget(StringBuilder builder,ChannelTransferItemsVO channelTransferItemsVO){
    	
    	List<CommissionProfileDeatilsVO> list ;
    	List<OTFDetailsVO> otflist ;
        builder.append(" Congratulations!! You have achieved your cumulative recharge target and you are eligible for commission amount of " +BTSLUtil.getDisplayAmount(channelTransferItemsVO.getOtfAmount()));
		
		
		if(channelTransferItemsVO.getOtfApplicableTo()!=null)
		{
		if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(channelTransferItemsVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(BTSLUtil.getDisplayAmount(channelTransferItemsVO.getOtfRate()))));
			builder.append(" extra for further transactions ");
		}
		if(PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(channelTransferItemsVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(channelTransferItemsVO.getOtfRate())));
			builder.append("% ").append("extra for further transactions ");
		}		
          	builder.append("till " +channelTransferItemsVO.getOtfApplicableTo());
		}
		else
		{
		if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(channelTransferItemsVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(BTSLUtil.getDisplayAmount(channelTransferItemsVO.getOtfRate()))));
			builder.append(" extra for further transactions.");
		}
		if(PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(channelTransferItemsVO.getOtfTypePctOrAMt()))
		{
			builder.append(" .You will get "+(Double.toString(channelTransferItemsVO.getOtfRate())));
			builder.append("% ").append("extra for further transactions.");
		}
		}
    	
    			
    	
    	return builder;
    }
    

    /**
     * method creates message for base commission slabs
     * @param builder
     * @param commissionProfileCombinedVOList
     * @return builder (message string)
     * @throws ParseException 
     */
    private StringBuilder createMessageforBaseSlab(StringBuilder builder,List<CommissionProfileCombinedVO> commissionProfileCombinedVOList,String appFrom) throws ParseException{
    	
    	List<CommissionProfileDeatilsVO> list ;
    	List<OTFDetailsVO> otflist ;
    	builder.append(" Base Commission Details are as follows: ");
    	int commissionListSize = commissionProfileCombinedVOList.size();
    	for(int i=0;i< commissionListSize;i++){
            list =commissionProfileCombinedVOList.get(i).getSlabsList();
            int listSize = list.size();
    		for(int j=0;j< listSize;j++)
    		{
    			builder.append(" For recharge amount between ").append(list.get(j).getStartRangeAsString()).append("-").append(list.get(j).getEndRangeAsString()).append(" , The following slabs would be applicable ");
    			otflist= list.get(j).getOtfDetails();
    			
				if(list.get(j).getOtfApplicableFromStr()!=null && list.get(j).getOtfApplicableToStr()!=null){
					builder.append(FROM).append(list.get(j).getOtfApplicableFromStr()).append(TO).append(list.get(j).getOtfApplicableToStr());
				}
				else{
					builder.append(FROM).append(appFrom);
				}
    			
    			if(!otflist.isEmpty()){
    				int otfLists = otflist.size();
    				for(int k=0;k< otfLists;k++){
    					
    					String fromAmount; 
    					String toAmount;
    					long endAmount;
    					
    					if((k!=0 || k==0) && k==otflist.size()-1){
    						fromAmount =otflist.get(k).getOtfValue();
    						toAmount= ABOVE;
    					}
    					else{
    						fromAmount= otflist.get(k).getOtfValue();
    						toAmount = otflist.get(k+1).getOtfValue();  
    						endAmount = Long.parseLong(toAmount)-1;
    	    				toAmount = Long.toString(endAmount);
    						}
    					
    					if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otflist.get(k).getOtfType()))
    						builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG3).append(otflist.get(k).getOtfRate()).append(EXTRA);
    					if(PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(otflist.get(k).getOtfType()))
        					builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG4).append(otflist.get(k).getOtfRate()).append("% ").append(EXTRA);

    				}
    			}
    		}
    			
    	}
    	return builder;
    }
    
    /**
     * method creates message for additional commission slabs
     * @param builder
     * @param addcommissionProfileCombinedVOList
     * @return builder (message string)
     */
    private StringBuilder createMessageforAddSlab(StringBuilder builder , List<AdditionalProfileCombinedVO> addcommissionProfileCombinedVOList,String appFrom){
    	List<AdditionalProfileDeatilsVO> list1 ;
    	AdditionalProfileServicesVO vo1;
    	List<OTFDetailsVO> otflist ;
    	builder.append(" Additional Commission Details are as follows: ");
    	int profileLists = addcommissionProfileCombinedVOList.size();
    	for(int i=0;i< profileLists;i++){
    		list1=addcommissionProfileCombinedVOList.get(i).getSlabsList();
    		vo1=addcommissionProfileCombinedVOList.get(i).getAdditionalProfileServicesVO();
    		int listSize = list1.size();
    		for(int j=0;j< listSize;j++)
    		{
    			builder.append(" For recharge amount between ").append(list1.get(j).getStartRangeAsString()).append("-").append(list1.get(j).getEndRangeAsString()).append(" , The following slabs would be applicable ");
    			otflist= list1.get(j).getOtfDetails();
    			
    			if( list1.get(j).getOtfApplicableFromStr()!=null && list1.get(j).getOtfApplicableToStr()!=null){
					builder.append(FROM).append(list1.get(j).getOtfApplicableFromStr()).append(TO).append(list1.get(j).getOtfApplicableToStr());
				}else if((vo1.getApplicableFromAdditional())!=null && (vo1.getApplicableToAdditional())!=null){
					builder.append(FROM).append(vo1.getApplicableFromAdditional()).append(TO).append(vo1.getApplicableToAdditional());
				}else{
					builder.append(FROM).append(appFrom);
				}
    			
    			if(!otflist.isEmpty()){
    				  int otfListSize = otflist.size();
    				for(int k=0;k< otfListSize;k++){
    					
    					String fromAmount; 
    					String toAmount;
    					long endAmount;
    					
    					if(PretupsI.OTF_TYPE_AMOUNT.equalsIgnoreCase(list1.get(j).getOtfType())){
    					if((k!=0 || k==0) && k==otflist.size()-1 ){
    						fromAmount =otflist.get(k).getOtfValue();
    						toAmount= ABOVE;
    					}
    					else {
    						fromAmount= otflist.get(k).getOtfValue();
    						toAmount = otflist.get(k+1).getOtfValue();  
    						endAmount = Long.parseLong(toAmount)-1;
    	    				toAmount = Long.toString(endAmount);
    						}
    					
    					if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otflist.get(k).getOtfType()))
    						builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG3).append(otflist.get(k).getOtfRate()).append(EXTRA);
    					if( PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(otflist.get(k).getOtfType()))
        					builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG4).append(otflist.get(k).getOtfRate()).append("% ").append(EXTRA);

    					
    					}
    					
    					
    					if(PretupsI.OTF_TYPE_COUNT.equalsIgnoreCase(list1.get(j).getOtfType()) && PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otflist.get(k).getOtfType())){
        					fromAmount= otflist.get(k).getOtfValue();
    						builder.append(": ").append(fromAmount).append(MSSG2).append(": Rs").append(otflist.get(k).getOtfRate()).append(EXTRA);
    					}
    					
    					if(PretupsI.OTF_TYPE_COUNT.equalsIgnoreCase(list1.get(j).getOtfType()) &&  PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(otflist.get(k).getOtfType())){
    						fromAmount= otflist.get(k).getOtfValue();
    						builder.append(": ").append(fromAmount).append(MSSG2).append(": ").append(otflist.get(k).getOtfRate()).append("%").append(EXTRA);
    					}
    					
    				}
    			}
    		}
    	}    	
    	return builder;
    }
    
    
    /**
     * method creates message for base commission slabs (batch modify commission profile)
     * @param builder
     * @param commissionProfileCombinedVOList
     * @return builder (message string)
     */
    private StringBuilder createMessageforBulkBaseSlab(StringBuilder builder,List<BatchModifyCommissionProfileVO> batchModifyCommProfileList){
    	
    	BatchModifyCommissionProfileVO batchVO;
    	List<OTFDetailsVO> otflist ;
    	int batchModifyProfileList = batchModifyCommProfileList.size();
    	for(int i=0;i< batchModifyProfileList;i++){
            batchVO = batchModifyCommProfileList.get(i);
            builder.append(" Base Commission Details are as follows: For recharge amount between ").append(batchVO.getStartRangeAsString()).append("-").append(batchVO.getEndRangeAsString()).append(", The following slabs would be applicable ");
    		otflist= batchVO.getOtfDetails();
    			if(!otflist.isEmpty()){
    				int otfList = otflist.size();
    				for(int k=0;k< otfList;k++){
    					
    					String fromAmount; 
    					String toAmount;
    					long endAmount;
    					
    					
    					if((k!=0 || k==0) && k==otflist.size()-1){
    						fromAmount =otflist.get(k).getOtfValue();
    						toAmount= ABOVE;
    					}
    					else{
    						fromAmount= otflist.get(k).getOtfValue();
    						toAmount = otflist.get(k+1).getOtfValue();  
    						endAmount = Long.parseLong(toAmount)-1;
    	    				toAmount = Long.toString(endAmount);
    						}
    					
    					if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otflist.get(k).getOtfType()))
    						builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG3).append(otflist.get(k).getOtfRate()).append(EXTRA);
    					if(PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(otflist.get(k).getOtfType()))
        					builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG4).append(otflist.get(k).getOtfRate()).append("% ").append(EXTRA);

    				}
    			}
    	}
    	return builder;
    }
    
    
    
    /**
     * method creates message for additional commission slabs (batch modify commission profile)
     * @param builder
     * @param commissionProfileCombinedVOList
     * @return builder (message string)
     */
    private StringBuilder createMessageforBulkAddSlab(StringBuilder builder,List<AdditionalProfileDeatilsVO> batchModifyAddCommProfileList){

    	AdditionalProfileDeatilsVO batchVO  ;
    	List<OTFDetailsVO> otflist ;
    	int batchProfileList = batchModifyAddCommProfileList.size();
    	for(int i=0;i< batchProfileList;i++){
    		batchVO=batchModifyAddCommProfileList.get(i);
   			builder.append(" Additional Commission Details are as follows: For recharge amount between ").append(batchVO.getStartRangeAsString()).append("-").append(batchVO.getEndRangeAsString()).append(", The following slabs would be applicable ");
    			otflist= batchVO.getOtfDetails();
    			if(!otflist.isEmpty()){
    				int otfListSize = otflist.size();
    				for(int k=0;k< otfListSize;k++){
    					
    					String fromAmount; 
    					String toAmount;
    					long endAmount;
    					
    					if(PretupsI.OTF_TYPE_AMOUNT.equalsIgnoreCase(batchVO.getOtfType())){
    					if((k!=0 || k==0) && k==otflist.size()-1 ){
    						fromAmount =otflist.get(k).getOtfValue();
    						toAmount= ABOVE;
    					}
    					else {
    						fromAmount= otflist.get(k).getOtfValue();
    						toAmount = otflist.get(k+1).getOtfValue();  
    						endAmount = Long.parseLong(toAmount)-1;
    	    				toAmount = Long.toString(endAmount);
    						}
    					
    					if(PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otflist.get(k).getOtfType()))
    						builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG3).append(otflist.get(k).getOtfRate()).append(EXTRA);
    					if( PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(otflist.get(k).getOtfType()))
        					builder.append(MSSG1).append(fromAmount).append("-").append(toAmount).append(MSSG4).append(otflist.get(k).getOtfRate()).append("% ").append(EXTRA);
    					}
    					
    					if(PretupsI.OTF_TYPE_COUNT.equalsIgnoreCase(batchVO.getOtfType()) && PretupsI.SYSTEM_AMOUNT.equalsIgnoreCase(otflist.get(k).getOtfType())){
        					fromAmount= otflist.get(k).getOtfValue();
    						builder.append(": ").append(fromAmount).append(MSSG2).append(": Rs").append(otflist.get(k).getOtfRate()).append(EXTRA);
    					}
    					
    					if(PretupsI.OTF_TYPE_COUNT.equalsIgnoreCase(batchVO.getOtfType()) &&  PretupsI.PERIOD_TYPE_PCT.equalsIgnoreCase(otflist.get(k).getOtfType())){
    						fromAmount= otflist.get(k).getOtfValue();
    						builder.append(": ").append(fromAmount).append(MSSG2).append(": ").append(otflist.get(k).getOtfRate()).append("%").append(EXTRA);
    					}
    					
    				}
    			}
    	}    	
    	return builder;
    }
    
     /**
      * method returns applicable_from of the commission profile
      * @param setId
      * @param version
      * @return
      */
    private Date loadProfileApplicableFrom(String setId,String version){

   	 final String methodName = "loadProfileApplicableFrom";
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        Connection con=null;
        MComConnectionI mcomCon = null;
        Date appFrom = null;
        try {
        	mcomCon = new MComConnection();
        	con=mcomCon.getConnection();
        	final StringBuilder strSelectDate = new StringBuilder(" select applicable_from from commission_profile_set_version where comm_profile_set_id=? and comm_profile_set_version=? " );
        	String selectQuery = strSelectDate.toString();
          
            pstmtSelect = con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, setId);
            pstmtSelect.setString(2, version);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
            	appFrom = rs.getDate("applicable_from");
           }
        }catch(SQLException sqle) {
        	logger.errorTrace(methodName, sqle);
        }catch (Exception e) {
        	logger.errorTrace(methodName, e);
        }finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		logger.error("An error occurred closing result set.", e);
        	}
        	try{
            	if (pstmtSelect!= null){
            		pstmtSelect.close();
            	}
            }
            catch (SQLException e){
            	logger.error("An error occurred closing statement.", e);
            }
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#loadProfileApplicableFrom");
				mcomCon = null;
			}
        }
    	return appFrom;
    }
    
    /**
     * method checks for otf in base commission profile
     * @param batchModifyCommProfileList
     * @return
     */
    private boolean checkBaseBulkOTF(List<BatchModifyCommissionProfileVO> batchModifyCommProfileList){
    	boolean OTFFlag =false;
    	BatchModifyCommissionProfileVO batchVO;
    	List<OTFDetailsVO> otflist ;
    	for(int i=0;i<batchModifyCommProfileList.size();i++){
            batchVO = batchModifyCommProfileList.get(i);
    		otflist= batchVO.getOtfDetails();
    		int otfLists = otflist.size();
    		for(int k=0;k< otfLists;k++){
    			if(!otflist.isEmpty()){
    				if((otflist.get(k).getOtfType()!=null && !("").equals(otflist.get(k).getOtfType())) && !("0").equals(otflist.get(k).getOtfRate())&& !("0").equals(otflist.get(k).getOtfValue())){
    					OTFFlag=true;
    					break;
    				}
    			}else{
    				continue; 
    			}
    		}
    		if(OTFFlag)
    			break;
    	}
    	return OTFFlag;
    }
    
    /**
     * method checks for otf in add commission profile
     * @param batchModifyAddCommProfileList
     * @return
     */
    private boolean checkAddBulkOTF(List<AdditionalProfileDeatilsVO> batchModifyAddCommProfileList){
    	boolean OTFFlag =false;
    	AdditionalProfileDeatilsVO batchVO  ;
    	List<OTFDetailsVO> otflist ;
    	for(int i=0;i<batchModifyAddCommProfileList.size();i++){
    		batchVO=batchModifyAddCommProfileList.get(i);
    			otflist= batchVO.getOtfDetails();
    			int otflistSize=otflist.size();
    			for(int k=0;k<otflistSize;k++){
    				if(!otflist.isEmpty()){
    					if((otflist.get(k).getOtfType()!=null && !("").equals(otflist.get(k).getOtfType())) && !("0").equals(otflist.get(k).getOtfRate())&& !("0").equals(otflist.get(k).getOtfValue())){
    						OTFFlag=true;
    						break;
    					}
    				}else{
    					continue;
    				}
    			}
    			if(OTFFlag)
        			break;
    		}    	
    	return OTFFlag;
    
    	
    }
    
    /**
     * method checks for otf in base commission profile
     * @param commissionProfileCombinedVOList
     * @return
     */
    private boolean checkBaseOTF(List<CommissionProfileCombinedVO> commissionProfileCombinedVOList){
    	boolean OTFFlag =false;
    	List<CommissionProfileDeatilsVO> list ;
    	List<OTFDetailsVO> otflist ;
    	for(int i=0;i<commissionProfileCombinedVOList.size();i++){
            list =commissionProfileCombinedVOList.get(i).getSlabsList();
            int listSize = list.size();
    		for(int j=0;j< listSize;j++)
    		{
    			otflist= list.get(j).getOtfDetails();
    			int otfListSize = otflist.size();
    			for(int k=0;k< otfListSize;k++){
    				if(!otflist.isEmpty()){
    					if((otflist.get(k).getOtfType()!=null && !("").equals(otflist.get(k).getOtfType())) && !("0").equals(otflist.get(k).getOtfRate())&& !("0").equals(otflist.get(k).getOtfValue())){
    						OTFFlag=true;
    						break;
    					}
    				}else{
    					continue;
    				}
    			}
    			if(OTFFlag)
    				break;
    		}
    		if(OTFFlag)
    			break;
    		}
    	return OTFFlag;
    }
    
    
    /**
     * method checks for otf in add commission profile
     * @param addcommissionProfileCombinedVOList
     * @return
     */
    private boolean checkAddOTF(List<AdditionalProfileCombinedVO> addcommissionProfileCombinedVOList){
    	boolean OTFFlag =false;
    	List<AdditionalProfileDeatilsVO> list1 ;
    	AdditionalProfileServicesVO vo1;
    	List<OTFDetailsVO> otflist ;
    	int combinedListSize = addcommissionProfileCombinedVOList.size();
    	for(int i=0;i< combinedListSize ;i++){
    		list1=addcommissionProfileCombinedVOList.get(i).getSlabsList();
    		vo1=addcommissionProfileCombinedVOList.get(i).getAdditionalProfileServicesVO();
    		int listSize=list1.size();
    		for(int j=0;j<listSize;j++)
    		{
    			otflist= list1.get(j).getOtfDetails();
    			int otfLists=otflist.size();
    			for(int k=0;k<otfLists;k++){
    				if(!otflist.isEmpty()){
    					if((otflist.get(k).getOtfType()!=null && !("").equals(otflist.get(k).getOtfType())) && !("0").equals(otflist.get(k).getOtfRate())&& !("0").equals(otflist.get(k).getOtfValue())){
    						OTFFlag=true;
    						break;
    					}
    				}else{
    					continue;
    				}
    			}
    			if(OTFFlag)
    				break;
    		}
    		if(OTFFlag)
    			break;
    	} 
    	return OTFFlag;
    }
}   
    

/**
 * Thread class for pushing messages to users
 * @author parul.nagpal
 *
 */
class TargetBasedCommissionMessagesThread implements Runnable {
    private static Log log = LogFactory.getLog(TargetBasedCommissionMessages.class.getName());
    private String id= null;
    private String messageKey = null;
    private String[] args = null;
    public TargetBasedCommissionMessagesThread(String messageKey,String[] args,String id) {
        this.id = id;
        this.messageKey=messageKey;
        this.args =args;
    }
    
    @Override
	public void run(){

        final String methodName = "run";
        LogFactory.printLog(methodName, PretupsI.ENTERED, log);
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            Thread.sleep(300);
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            sendMessage(con,messageKey,args,id);
            
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        } finally {
			if (mcomCon != null) {
				mcomCon.close("TargetBasedCommissionMessages#run");
				mcomCon = null;
			}
	           LogFactory.printLog(methodName, PretupsI.EXITED , log);
        }
    }
    
    /**
     * method pushes the final message to user
     * @param con
     * @param messageKey
     * @param args
     * @param userId
     */
	public void sendMessage(Connection con,String messageKey,String[] args,String userId ){
		final String methodName ="sendMessage";
	    final BTSLMessages sendMessageToUser = new BTSLMessages(messageKey,args);
	    UserDAO userDao = new UserDAO();
	    UserPhoneVO phoneVO = new UserPhoneVO();
	    Locale locale ;
	    PushMessage pushMessage;
		try {
			phoneVO = userDao.loadUserPhoneVO(con, userId);
		} catch (BTSLBaseException e) {
			log.errorTrace(methodName, e);
		}
        if(phoneVO!=null){
        	locale =new Locale(phoneVO.getPhoneLanguage(), phoneVO.getCountry());
        	pushMessage =new PushMessage(phoneVO.getMsisdn(), sendMessageToUser, "", "", locale,"");
        }else{
        	locale =new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),(String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));	
        	pushMessage =new PushMessage(null, sendMessageToUser, "", "", locale,"");
        }
        
	    pushMessage.push();
	}
	
}



