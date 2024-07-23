package com.restapi.channeluser.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.EMailSender;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.GenerateOtpDto;
import com.btsl.login.UserOtpDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserOtpVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.user.businesslogic.ChannelUserTransferWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;


@Service("ChannelUserTransferService")
public class ChannelUserTransferServiceImpl implements ChannelUserTransferService{
	public static final Log log = LogFactory.getLog(ChannelUserTransferServiceImpl.class.getName());
	public static final String  classname = "ChannelUserTransferServiceImpl";
    private static final String EMAIL="EMAIL";
    private static  final String SMS="SMS";
    
   
	
	@Override
	public void sendOtp(OperatorUtilI operatorUtili, BaseResponse response,
			HttpServletResponse responseSwag,ChannelUserTransferOtpRequestVO
			requestVO) {
		final String methodName="sendOtp";
		  if (log.isDebugEnabled()) {
	            log.debug(methodName, "Entered mode :" + requestVO.getMode());
	        }
		  
		    Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
	        Connection con = null;
	        MComConnectionI mcomCon = null;
	        UserOtpVO userOtpVO =null;
	        int otpValidityPeriodInPreference = (Integer) PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD);
			String validForMessage = BTSLDateUtil.getTimeFromSeconds((int) otpValidityPeriodInPreference);
			int otpResendTImesInPreference = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_RESEND_TIMES)));
			int otpResendDurationInPreference = ((Integer) (PreferenceCache
					.getSystemPreferenceValue(PreferenceI.OTP_RESEND_DURATION)));
			int validityOtp = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_TIME));  //in minutes
			UserVO userVO = new UserVO();
        	UserOtpDAO userOtpDAO = new UserOtpDAO();
        	
			try {
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				userVO = this.validateMsisdnEmail(requestVO);
				if (BTSLUtil.isNullObject(userVO)) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_MSISDN_USER_TRANSFER,
							0, null);
				}
				Date currDate = new Date();
				userOtpVO = userOtpDAO.getDetailsOfUser(con, requestVO.getMsisdn());
				Date barredDate = null;
				Date consumedOn = null;
				barredDate = userOtpVO.getBarredDate();

				if(!BTSLUtil.isNullObject(userOtpVO.getBarredDate())) {
				if(BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate) < validityOtp) {
					int blockOTP =BTSLUtil.parseLongToInt((validityOtp - BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate)));
					String blockOTP1 = BTSLDateUtil.getTimeFromSeconds(blockOTP);
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_CANNOT_SENT,new String[]{blockOTP1});
					response.setMessage(msg);
					response.setMessageCode(PretupsErrorCodesI.OTP_CANNOT_SENT);
					throw new BTSLBaseException(classname, methodName,
							PretupsErrorCodesI.OTP_CANNOT_SENT, 0, null);
				}}
				ChannelUserVO channelUserVO = (ChannelUserVO) userVO;
				String returnMessage = generateAndSendOtp(channelUserVO, otpValidityPeriodInPreference,
						otpResendDurationInPreference, otpResendTImesInPreference, requestVO.getMode());
				userOtpDAO.updateInvalidCountOfOtp(con, requestVO.getMsisdn(), 0, currDate);

				if (returnMessage.equals(EMAIL)) {
					response.setStatus(HttpStatus.SC_OK);
					response.setMessageCode(PretupsErrorCodesI.OTP_USER_TRANSFER_EMAIL);
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_USER_TRANSFER_EMAIL,
							new String[] { String.valueOf(validForMessage) });
					response.setMessage(msg);
				} else if (returnMessage.equals(SMS)) {
					response.setStatus(HttpStatus.SC_OK);
					response.setMessageCode(PretupsErrorCodesI.OTP_USER_TRANSFER_SMS);
					String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_USER_TRANSFER_SMS,
							new String[] { String.valueOf(validForMessage) });
					response.setMessage(msg);
				}
				
			

			} catch (BTSLBaseException be) {

				    log.error(methodName, "Exception:e=" + be);
					log.errorTrace(methodName, be);
					if (BTSLUtil.isNullString(response.getMessage())) {
						String resmsg = RestAPIStringParser.getMessage(
								new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)),
								be.getMessageKey(), null);
						response.setMessageCode(be.getMessageKey());
						response.setMessage(resmsg);
					}

					responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
					response.setStatus(HttpStatus.SC_BAD_REQUEST);
				}			  
				  catch (Exception e) {
		            log.errorTrace(methodName, e);
		            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		            responseSwag.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		            if(response.getMessage()==null) {	
		        		String msg=RestAPIStringParser.getMessage(locale, e.getMessage(),null);
				        response.setMessageCode(e.getMessage());
				        response.setMessage(msg);
		        	}
		         }
		        finally {
		        	 if (mcomCon != null) {
		       			mcomCon.close(classname+"#"+methodName);
		       			mcomCon = null;
		       		}
		               if (log.isDebugEnabled()) {
		                   log.debug(methodName, "Exited");
		               }
		             
				}
	}
				
    private String generateAndSendOtp(ChannelUserVO p_userVO,int validity, int duration, int times,String mode) throws BTSLBaseException, Exception {
       
        String returnmessage=null;
		final String methodName = "generateAndSendOtp";
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));
		UserOtpDAO userOtpDAO = new UserOtpDAO();
		String validityPeriodMessage= BTSLDateUtil.getTimeFromSeconds((int)validity);
	    Connection con = null;
	    MComConnectionI mcomCon = null;
	    GenerateOtpDto dto=null;
		String btslMessage = null;   
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			dto = userOtpDAO.generateOTP(con, p_userVO, "",  validity, duration, times);
		} catch (Exception e) {
			throw new BTSLBaseException(this, methodName, "Error occured", "Exception " + e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close(methodName);
				mcomCon = null;
			}
		}
		
		if(mode.equals(EMAIL)&&p_userVO.getEmail()!=null){
			final String to = p_userVO.getEmail();
			final String from = Constants.getProperty("mail_from_admin");
			final String subject ="OTP for user transfer";
			final String message = "Dear " + p_userVO.getUserName() + ","
					+ "OTP for user transfer" + ":  " + (String) dto.getOtp()
					 + ". This OTP is valid for " + validityPeriodMessage + " to change your parent user.";
			EMailSender.sendMail(to, from, "", "", subject, message, false, "", "");
			returnmessage=EMAIL;
		}
		else if(mode.equals(SMS)&&p_userVO.getMsisdn()!=null) {
			String[] messageArgArray = { p_userVO.getUserName(),dto.getOtp()};		
			btslMessage = BTSLUtil.getMessage(locale, PretupsErrorCodesI.USER_OTP_SMS, messageArgArray) ;			
			PushMessage pushParentMessages = (new PushMessage(p_userVO.getMsisdn(), btslMessage,
					null,null, locale));
			pushParentMessages.push();
			returnmessage= SMS;
			
		}else {
			throw new BTSLBaseException(classname, methodName,
  					PretupsErrorCodesI.INVALID_MSISDN_USER_TRANSFER, 0, null);
		}
		 if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exited");
	      }	
		return returnmessage;
	}
    private UserVO validateMsisdnEmail(ChannelUserTransferOtpRequestVO requestvo) throws BTSLBaseException, SQLException{
    	final String methodName = "validateMsisdnEmail";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered msisdn :" + requestvo.getMsisdn() + "mode :" + requestvo.getMode());
        }
		
		String loginId = null;
		String msisdn = null;
		String networkCode=null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		UserVO uservo = null;
		String email = null;
		UserDAO userDAO= new UserDAO();
		
          try{
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				uservo = userDAO.loadUserDetailsByMsisdn(con, requestvo.getMsisdn());
				if (uservo == null) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_MSISDN_USER_TRANSFER,
							0, null);
				}

				networkCode = BTSLUtil.isNullString(uservo.getNetworkID()) ? "" : (String) uservo.getNetworkID();
				email = BTSLUtil.isNullString(uservo.getEmail()) ? "" : (String) uservo.getEmail();
				msisdn = BTSLUtil.isNullString(uservo.getMsisdn()) ? "" : (String) uservo.getMsisdn();
				loginId = BTSLUtil.isNullString(uservo.getLoginID()) ? "" : uservo.getLoginID();

				if (BTSLUtil.isNullString(loginId)) {
					throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.INVALID_MSISDN_USER_TRANSFER,
							0, null);
				}

				if (BTSLUtil.isNullString(email) && !BTSLUtil.isNullString(loginId)) {
					throw new BTSLBaseException(classname, methodName, "User must have a registered email id", 0, null);
				}
				if (BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(loginId)) {
					throw new BTSLBaseException(classname, methodName, "User must have a registered primary mobile no",
							0, null);
				}
          }
			finally {
				if (mcomCon != null) {
					mcomCon.close(classname + "#" + methodName);
					mcomCon = null;
				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exited");
				}
			}
          
          return uservo;
    }  
   
    /**
     * @author sarthak.saini
     */
    @Override
    public BaseResponse confirmTransferUser(Connection con , MComConnectionI mcomCon,ChannelUserVO channelUserVO, UserVO userVO,UserVO sessionUserVO, ConfimChannelUserTransferRequestVO requestVO) throws BTSLBaseException, SQLException {
    	final String methodName = "confirmTransferUser";
        if (log.isDebugEnabled()) {
            log.debug(methodName,"Entered");
        }
		Locale locale = new Locale((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY));

        BaseResponse response = new BaseResponse();
        ChannelUserTransferWebDAO channelUserTransferwebDAO = null;
        Map lockedDataMap = null;
        int updateCount = 0;
		int updateUserCount=0;
		String markStatus=PretupsI.USER_MIGRATION_MOVED_STATUS;

		 ChannelUserTransferVO channelUserTransferVO = null;
        try {
              channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
              channelUserTransferVO = new ChannelUserTransferVO();
              createTransferVO(con,channelUserVO ,channelUserTransferVO , sessionUserVO, requestVO,userVO);

				 lockedDataMap = channelUserTransferwebDAO.transferChannelUserIntermediate(con,channelUserTransferVO);

              final Date currentDate = new Date();
          
              channelUserTransferVO.setCreatedBy(sessionUserVO.getUserID());
              channelUserTransferVO.setCreatedOn(currentDate);
              channelUserTransferVO.setModifiedBy(sessionUserVO.getUserID());
              channelUserTransferVO.setModifiedOn(currentDate);
              channelUserTransferVO.setNetworkCode(sessionUserVO.getNetworkID());
              channelUserTransferwebDAO = new ChannelUserTransferWebDAO();
             
              getNewGeographicalDomainCode(con, channelUserTransferVO);

              
              updateCount = channelUserTransferwebDAO.transferChannelUser(con, channelUserTransferVO);
              if (con != null) {
                  if (updateCount > 0) {
                  
                  	mcomCon.finalCommit();
                  	response.setStatus(200);
                  	String msg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
        			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
                  	response.setMessage(msg);
                  }
                  else {
                      
                    	mcomCon.finalRollback();
                        throw new BTSLBaseException(this, methodName, "channeluser.viewuserhierarchy.msg.trfunsuccess", "selectfrompage");
                    }
                 }
             
      		  
        }catch (BTSLBaseException be) {
		
	    	markStatus=PretupsI.USER_MIGRATION_COMPLETE_STATUS;
	    	log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			response.setMessage(msg);
			response.setStatus(HttpStatus.SC_BAD_REQUEST);
			return response;
        } 
        finally {
			try{
    			updateUserCount=channelUserTransferwebDAO.transferChannelUserFinal(con,markStatus,channelUserTransferVO,lockedDataMap );

    			if (log.isDebugEnabled())
    				log.debug(methodName,"update Count : "+updateUserCount+", UserCount:"+channelUserTransferVO.getUserHierarchyList().size());
    			if(lockedDataMap == null || lockedDataMap.size() != updateUserCount){
    				log.error(methodName, methodName, updateUserCount+" out of "+channelUserTransferVO.getUserHierarchyList().size()+" records rollbacked to complete status");
    			}
    			con.commit();        			
    		}
	
     	catch(BTSLBaseException be){
    			log.errorTrace(methodName,be);
    			try{con.rollback();}catch(SQLException sqe){log.errorTrace(methodName,be);} 
    		} catch (SQLException e) {
    			log.errorTrace(methodName,e);
    			try{con.rollback();}catch(SQLException sqe){log.errorTrace(methodName,e);} 
    			
    		} 
			if (mcomCon != null) {
				mcomCon.close(classname + "#" + methodName);
				mcomCon = null;
			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited");
			}
		}
        
    	
         
     return response;      
    }
    
    public void createTransferVO(Connection con,ChannelUserVO channelUserVO,ChannelUserTransferVO channelUserTransferVO,UserVO sessionVO,ConfimChannelUserTransferRequestVO requestVO,UserVO userVO) throws BTSLBaseException {
    	ChannelUserWebDAO channelUserWebDAO = null;
        final String statusUsed = PretupsI.STATUS_IN;
        final String status = PretupsBL.userStatusIn() + ", '" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'";

        String [] arr = new  String [2];

    	try {
    		channelUserWebDAO =new ChannelUserWebDAO();
    		channelUserTransferVO.setUserID(channelUserVO.getUserID());
    		channelUserTransferVO.setUserCategoryCode(channelUserVO.getCategoryCode());
    		channelUserTransferVO.setUserCategoryDesc(channelUserVO.getCategoryCodeDesc());
    		channelUserTransferVO.setUserName(channelUserVO.getUserName());
    		channelUserTransferVO.setFromParentID(channelUserVO.getParentID());
    		channelUserTransferVO.setFromOwnerID(channelUserVO.getOwnerID());
    		channelUserTransferVO.setStatus(channelUserVO.getStatus());
    		channelUserTransferVO.setToOwnerID(sessionVO.getUserID());
    		channelUserTransferVO.setToParentID(sessionVO.getUserID());
    		channelUserTransferVO.setDomainCode(channelUserVO.getDomainID());
    		channelUserTransferVO.setZoneCode(sessionVO.getGeographicalAreaList().get(0).getGraphDomainCode());
    		channelUserTransferVO.setNetworkCode(channelUserVO.getNetworkCode());
    		channelUserTransferVO.setCreatedBy(channelUserVO.getCreatedBy());
    		channelUserTransferVO.setCreatedOn(channelUserVO.getCreatedOn());
    		arr[0]= channelUserVO.getUserID();
    		channelUserTransferVO.setUserHierarchyList(channelUserWebDAO
                        .loadUserHierarchyListForTransfer(con,arr , PretupsI.SINGLE, statusUsed,status,channelUserVO.getCategoryCode()));
    		channelUserTransferVO.setMultibox(PretupsI.RESET_CHECKBOX);
    		channelUserTransferVO.setServiceType(channelUserVO.getServiceTypes());
    		channelUserTransferVO.setDomainName(channelUserVO.getDomainName());
    		channelUserTransferVO.setCategoryName(channelUserVO.getCategoryName());
    		channelUserTransferVO.setMsisdn(channelUserVO.getMsisdn());
    		channelUserTransferVO.setLoginId(channelUserVO.getLoginID());
    		channelUserTransferVO.setParentUserName(channelUserVO.getParentName());
    		channelUserTransferVO.setToParentUserName(sessionVO.getUserName());
    		channelUserTransferVO.setIsOperationNotAllow(true);
    		channelUserTransferVO.setParentUserID(channelUserVO.getParentID());
    		channelUserTransferVO.setDomainCodeDesc(channelUserVO.getDomainTypeCode());
    		channelUserTransferVO.setDomainList(channelUserVO.getDomainList());
    		channelUserTransferVO.setGeographicalCode(channelUserVO.getGeographicalCode());
    		channelUserTransferVO.setOtp(requestVO.getOtp());
    		
    		
    	}
    	catch (BTSLBaseException be) {
            log.error("createTransferVO", "Exceptin:be=" + be);
         
            throw be;
    	}
    }
    private void getNewGeographicalDomainCode(Connection p_con, ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("getNewGeographicalDomainCode", "Entered" +  ",channelUserTransferVO=" + p_channelUserTransferVO);
        }
        final String METHOD_NAME = "getNewGeographicalDomainCode";
        String geoGraphicalDomainType = null;
        final ChannelUserTransferWebDAO chnlUserTransferwebDAO = new ChannelUserTransferWebDAO();
        try {
            final ArrayList geoList = chnlUserTransferwebDAO.loadGeogphicalHierarchyListByToParentId(p_con, p_channelUserTransferVO.getToParentID());
            final ArrayList userList = p_channelUserTransferVO.getUserHierarchyList();
            if (userList != null && !userList.isEmpty()) {
                for (int j = 0; j < userList.size(); j++) {
                    final ChannelUserVO chnlUserVO = (ChannelUserVO) userList.get(j);
                    geoGraphicalDomainType = chnlUserVO.getCategoryVO().getGrphDomainType();
                    if (geoList != null && !geoList.isEmpty()) {
                        for (int i = 0; i < geoList.size(); i++) {
                            final GeographicalDomainVO geoDomainVO = (GeographicalDomainVO) geoList.get(i);
                            if (geoDomainVO.getGrphDomainType().equals(geoGraphicalDomainType)) {
                                chnlUserVO.setGeographicalCode(geoDomainVO.getGrphDomainCode());
                                break;
                            }
                        }
                    }
                }
            }

        } catch (BTSLBaseException be) {
            log.error("getNewGeographicalDomainCode", "Exceptin:be=" + be);
            log.errorTrace(METHOD_NAME, be);
            throw be;
        } finally {
            if (log.isDebugEnabled()) {
                log.debug("getNewGeographicalDomainCode", "Exiting ");
            }
        }

       
    }
    @Override
    public void validateOTP(Connection con,BaseResponse response, String OTP,String msisdn,HttpServletResponse responseSwag) {
        final String methodName = "validateOTP";
        UserOtpDAO userOtpDAO =null;
        UserOtpVO userOtpVO =null;
       
		Locale locale= new Locale(PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_LANGUAGE), PreferenceCache.getSystemPreferenceValueAsString(PreferenceI.DEFAULT_COUNTRY));

        try {
        	userOtpDAO = new UserOtpDAO();
        	
        	userOtpVO = userOtpDAO.getDetailsOfUser(con, msisdn);
        	if(BTSLUtil.isNullString(userOtpVO.getUserId())) {
        		response.setMessage("Please Send OTP first.");
    			throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
        	}
        	int validityPeriodOtp = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTP_VALIDITY_PERIOD));  //in minutes
			int invalidCountLimit = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_INVALID_OTP));
			int blockTime = (Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.BLOCK_TIME_INVALID_OTP));   //in minutes
			Date generatedTime = null;
			Date barredDate = null;
			Date consumedOn = null;
			barredDate = userOtpVO.getBarredDate();
			generatedTime = userOtpVO.getGeneratedOn();
			consumedOn = userOtpVO.getConsumedOn();
			Date currDate = new Date();
			String correctOtp = null;
			int invalidCount = 0;
			if(!BTSLUtil.isNullString(userOtpVO.getInvalidCount())){
				invalidCount = Integer.parseInt(userOtpVO.getInvalidCount());
				}
			if(!BTSLUtil.isNullString(userOtpVO.getOtppin())){	
				correctOtp = BTSLUtil.decryptText(userOtpVO.getOtppin());
				}
			int updateCnt = 0;
			if((BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate) > validityPeriodOtp)){
				response.setMessageCode(PretupsErrorCodesI.OTP_EXPIRED);
				throw new BTSLBaseException(classname, methodName,
						PretupsErrorCodesI.OTP_EXPIRED, 0, null);
			}
			else {
				if((invalidCountLimit == invalidCount) && (BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate) < blockTime)){
					int blockTimeRem =BTSLUtil.parseLongToInt((blockTime - BTSLUtil.getDifferenceInUtilDatesinSeconds(barredDate, currDate)));
					String blocktimeConverted = BTSLDateUtil.getTimeFromSeconds(blockTimeRem);
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_MAX_INVALID,new String[]{blocktimeConverted});
					response.setMessage(msg);
					response.setMessageCode(PretupsErrorCodesI.OTP_MAX_INVALID);
					throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
							PretupsErrorCodesI.OTP_MAX_INVALID, 0, null);
				}
				else {
					if( OTP.equals(correctOtp) && consumedOn==null){
						response.setStatus(HttpStatus.SC_OK);
						response.setMessageCode(PretupsErrorCodesI.SUCCESS);
//						response.setMessage("OTP validation successful.");
						
					}
					else {
						invalidCount++;
						
					
					if(invalidCount > invalidCountLimit){
						invalidCount = 1;
					}
					updateCnt = userOtpDAO.updateInvalidCountOfOtp(con, msisdn, invalidCount,currDate);
					if(invalidCount == invalidCountLimit){
						
						String blocktimeConverted = BTSLDateUtil.getTimeFromSeconds(blockTime);
						
						String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.OTP_MAX_INVALID,new String[]{blocktimeConverted});
						response.setMessage(msg);
						response.setMessageCode(PretupsErrorCodesI.OTP_MAX_INVALID);
						throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
								PretupsErrorCodesI.OTP_MAX_INVALID, 0, null);
					}
					int attemptsLeft = invalidCountLimit-invalidCount;
					String msg=RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_OTP_ATTEMPT,new String[]{String.valueOf(attemptsLeft)});
					response.setMessage(msg);
					response.setMessageCode(PretupsErrorCodesI.INVALID_OTP_ATTEMPT);
					throw new BTSLBaseException("OtpValidationandPinUpdation", "process",
							PretupsErrorCodesI.INVALID_OTP_ATTEMPT, 0, null);
					}
				}// end of unbarred user
			}//end of inexpired otp
			
			
        	
        }catch(Exception e) {
        	response.setStatus(HttpStatus.SC_BAD_REQUEST);
        	responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
        	if(response.getMessage()==null) {	
        		String msg=RestAPIStringParser.getMessage(locale, e.getMessage(),null);
		        response.setMessageCode(e.getMessage());
		        response.setMessage(msg);
        	}
        	
			
        }
        finally {
        	
			if (log.isDebugEnabled()) {
				log.debug(methodName, " Exited ");
			}
        }
        	
        }
   
}
