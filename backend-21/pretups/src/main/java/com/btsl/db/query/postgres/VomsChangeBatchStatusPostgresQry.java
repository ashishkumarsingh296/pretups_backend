package com.btsl.db.query.postgres;

import java.io.BufferedWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;

import com.btsl.util.MessageResources;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.AppDBResourceAnalyzer;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.vomsproduct.businesslogic.VomsProductDAO;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VomsChangeBatchStatusQry;
//import com.btsl.voms.voucher.web.VomsVoucherAction;

public class VomsChangeBatchStatusPostgresQry implements VomsChangeBatchStatusQry{
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	private MessageResources messages = null;
	private boolean triggerEmail = true;
	private String masterBatchId = null;
	private StringBuffer batchIds = null;
	private long successCount = 0L;
	private long failCount = 0L;
	private String msisdn = null;
	
	/**
	 * Method used to retrieve Master_Batch_Id
	 * @param batchNO
	 * @param con
	 */
	private void retrieveMasterId(String batchNO, Connection con) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer qry = new StringBuffer("SELECT VB2.MASTER_BATCH_ID, VB2.STATUS, VB2.TOTAL_NO_OF_SUCCESS, VB2.TOTAL_NO_OF_FAILURE , VB2.BATCH_NO  "); 

		qry.append(" FROM VOMS_BATCHES VB1, VOMS_BATCHES VB2  ");
		qry.append(" WHERE VB1.BATCH_NO = ? ");
		qry.append(" AND VB1.MASTER_BATCH_ID = VB2.MASTER_BATCH_ID ");
		
		String status = null;
		int scCounter = 0;
		
		successCount = 0;
		failCount = 0;
		batchIds = new StringBuffer("");
		
		try {
			pstmt = con.prepareStatement(qry.toString());
			pstmt.setString(1, batchNO);
			rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					masterBatchId =  rs.getString(1);
					
					if(masterBatchId !=null && masterBatchId.equalsIgnoreCase("NA")) {
						masterBatchId = batchNO;
					}
					status = rs.getString(2);
					log.debug("retrieveMasterId","status:  " + status+" batchNO: "+batchNO);
					
					
					if(status != null && ( status.equals(PretupsI.TXN_LOG_STATUS_EXECUTED) || status.equals(PretupsI.TXN_LOG_STATUS_FAIL) )) {
						
					}else {
						scCounter++;
						
					}
					
					successCount += rs.getLong(3);
					failCount += rs.getLong(4);
					batchIds.append(rs.getString(5)).append(" ");
				}
			}
			
			log.debug("retrieveMasterId","scCounter:  " + scCounter+" batchNO: "+batchNO);
			
			if(scCounter > 1) {
				triggerEmail = false;
			}
		} catch (Exception e) {
			log.debug("retrieveMasterId", "Exception while fetching emailID " + e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					log.debug("retrieveMasterId", "Could not close preparedstatement " + e);
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					log.debug("retrieveMasterId", "Could not close resultSet " + e);
				}
			}
		}

		
	}


	
	private String retrieveEmailId(String userId, Connection con) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String qry = "SELECT EMAIL, MSISDN FROM USERS WHERE USER_ID = ? ";


		try {

			pstmt = con.prepareStatement(qry);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					msisdn = rs.getString(2);
					return rs.getString(1);
				}
			}
		} catch (Exception e) {
			log.debug("retrieveEmailId", "Exception while fetching emailID " + e);
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					log.debug("retrieveEmailId", "Could not close preparedstatement " + e);
				}
			}

			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
					log.debug("retrieveEmailId", "Could not close resultSet " + e);
				}
			}
		}

		return null;
	}
	
	

	@Override
	public String[] changeVoucherStatusPlSqlQry(Connection con ,  VomsBatchVO batchVO,
			long maxErrorAllowed, int processScreen, MessageResources messages) throws BTSLBaseException,
			SQLException, ParseException {
	
		this.messages = messages;
		return changeVoucherStatusPlSqlQry(con, batchVO, maxErrorAllowed, processScreen);
	}
	
	
	@Override
	public String[] changeVoucherStatusPlSqlQry(Connection con ,  VomsBatchVO batchVO,
			long maxErrorAllowed, int processScreen) throws BTSLBaseException,
			SQLException, ParseException {
		  final String methodName = "changeVoucherStatusPlSqlQry";
		  String plSqlRetuns[] = new String[3];
		  CallableStatement cstmt = null;
		  try{
			  VomsProductDAO vomsProductDAO = new VomsProductDAO();
			  String vomsType = vomsProductDAO.getTypeFromVoucherType(con, batchVO.getVoucherType());
			  String prefrence= VomsUtil.getSystemPrefrenceOfVoucher(vomsType);
				 
		   cstmt = con.prepareCall("{ call p_changeVoucherStatus(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) } "); 
		  
          int i=1;
          cstmt.setString(i, batchVO.getBatchNo());// which is created at add
                                                   // batch function
          i++;
          cstmt.setString(i, batchVO.getBatchType());// changed voucher
                                                     // status(as entered in
           i++;                                          // input screen)
          cstmt.setString(i, batchVO.getFromSerialNo());// from serial no
          
          i++;
          cstmt.setString(i, batchVO.getToSerialNo());// to serial number
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_ENABLED);// EN
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_GENERATED);// GE
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_ONHOLD);// OH
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_STOLEN);// ST
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_SOLD);// SL
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_DAMAGED);// DA
          
          i++;
          cstmt.setString(i, VOMSI.BATCHRECONCILESTAT);// RC
          
          i++;
          cstmt.setString(i, VOMSI.VOMS_PRINT_ENABLE_STATUS);// PE ///gaurav
          
          i++;
          cstmt.setString(i, VOMSI.VOMS_WARE_HOUSE_STATUS);// WH
          
          i++;
          cstmt.setString(i, VOMSI.VOMS_PRE_ACTIVE_STATUS);// PA
          
          i++;
          cstmt.setString(i, VOMSI.VOMS_SUSPEND_STATUS);// S
          
          i++;
          cstmt.setString(i, BTSLUtil.getDateStringFromDate(batchVO.getCreatedDate()));
          
          i++;
          cstmt.setInt(i, Long.valueOf(maxErrorAllowed).intValue());
          
          i++;
          cstmt.setString(i, batchVO.getCreatedBy());
          
          i++;
          cstmt.setInt(i, Long.valueOf(batchVO.getNoOfVoucher()).intValue());
          
          i++;
          cstmt.setString(i, VOMSI.VA_PROCESS_SUCCESS_STAT);// SU
          
          i++;
          cstmt.setString(i, VOMSI.VA_PROCESS_ERROR_STAT);// ER
          
          i++;
          cstmt.setString(i, VOMSI.BATCHCONSUMESTAT);// CU
          
          i++;
          cstmt.setInt(i, processScreen);
          
          i++;
          cstmt.setString(i, batchVO.getCreatedDate().toString());

          i++;
          cstmt.setString(i, batchVO.getReferenceNo());
          
          i++;
          cstmt.setInt(i, batchVO.getRcAdminDaysAllowed());
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_PROCESS_ENABLE);// ENABLE
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_PROCESS_CHANGE);// CHANGESTAT
          
          i++;
          cstmt.setString(i, VOMSI.BATCH_PROCESS_RECONCILE);// RECONCILE
          
          i++;
          cstmt.setString(i, batchVO.getLocationCode());
          
          i++;
          if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue() && !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.HASHING_ENABLE))).booleanValue()){
          	
          	cstmt.setInt(i, batchVO.getSeq_id());
			}
          else{
          	cstmt.setInt(i, 0);
          }
          
          i++;
          cstmt.setString(i, prefrence);
          
   
          //i++;
          cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);// return message
          
          //i++;
          cstmt.registerOutParameter(2, java.sql.Types.VARCHAR);// return log
                                                                 // message
          //i++;
          cstmt.registerOutParameter(3, java.sql.Types.VARCHAR);// sql error
                                                                 // message
          
         // i++;
          cstmt.registerOutParameter(4, java.sql.Types.ARRAY);// sql error
                                                                 // message     
	      AppDBResourceAnalyzer.monitorResourcesAndWait();//DB Util Check     
	      
          long startTime = System.currentTimeMillis();
          cstmt.execute();
          long endTime = System.currentTimeMillis();
          log.debug(methodName, "Function: Vouchet Status Change  executed in time " + (endTime - startTime)/ 1000.0 +" seconds");
                             
          plSqlRetuns[0] = cstmt.getString(1) ;  //returnMessage 
          plSqlRetuns[1] = cstmt.getString(2) ; // returnLogMessage
          plSqlRetuns[2] = cstmt.getString(3); // sqlErrorMessage
          
          String[]  outputRes = null;
          BufferedWriter bwSuccess = null;
          BufferedWriter bwFail = null;
          
			try {

				outputRes = (String[]) cstmt.getArray(4).getArray();

				retrieveMasterId(batchVO.getBatchNo(), con);
				
				if (Constants.getProperty("VOMS_LOGGER_PATH") == null) {

					log.debug(methodName, "Could not find VOMS_LOGGER_PATH in Constants file ");
					
				} else {
					bwSuccess = new BufferedWriter(
							new java.io.FileWriter(Constants.getProperty("VOMS_LOGGER_PATH").trim()
									+ "/CHANGE_STATUS_SUCCESS_" + masterBatchId+".txt", true));
					bwFail = new BufferedWriter(new java.io.FileWriter(Constants.getProperty("VOMS_LOGGER_PATH").trim()
							+ "/CHANGE_STATUS_FAIL_" + masterBatchId+".txt", true));

					String[] pathsOfFiles = {Constants.getProperty("VOMS_LOGGER_PATH").trim()
							+ "/CHANGE_STATUS_SUCCESS_" + masterBatchId+".txt",
							
							Constants.getProperty("VOMS_LOGGER_PATH").trim()
							+ "/CHANGE_STATUS_FAIL_" + masterBatchId+".txt"
							};
					
					String[] filesNamesTobeDisplayed  = {"CHANGE_STATUS_SUCCESS_" + masterBatchId+".txt", "CHANGE_STATUS_FAIL_" + masterBatchId+".txt" };
					
					int fileCounter = 0;
					
					long successCounter = 0L;
					long failCounter = 0L;
					
					if (outputRes != null) {
						for (String strObj : outputRes) {
							if (strObj != null) {
								String[] strArrObj = strObj.split(":");

								if (strArrObj[1] != null && strArrObj[1].toUpperCase().contains("SUCCESS")) {
									bwSuccess.write(strArrObj[0] + "," + strArrObj[1]);
									bwSuccess.newLine();
									successCounter++;
								} else {
									bwFail.write(strArrObj[0] + "," + strArrObj[1]);
									bwFail.newLine();
									failCounter++;
								}
							}
						}
					}
					
					
					String emailId = retrieveEmailId(batchVO.getCreatedBy(), con);
					
					if(triggerEmail == true) {
						
	        		 //  (new VomsVoucherAction()).sendEmail(messages, (successCounter) , (failCounter) , batchIds.toString(), pathsOfFiles, filesNamesTobeDisplayed, batchVO.getCreatedBy() , emailId);
					
					
	        		   try {
							Locale locale = BTSLUtil.getSystemLocaleForEmail();
							StringBuffer notifyContent = new StringBuffer();
							if (messages != null) {
								notifyContent.append(
										messages.getMessage(locale, "email.notification.changestatus.log.file.body.part1")
												.replaceAll("<Batch ID>", batchIds.toString()));
							} else {
								notifyContent.append(
										BTSLUtil.getMessage(locale, "email.notification.changestatus.log.file.body.part1")
												.replaceAll("<Batch ID>", batchIds.toString()));

							}
							PushMessage pushMessage = new PushMessage(msisdn, notifyContent.toString(), masterBatchId, null,
									locale);
							pushMessage.push();

						} catch (Exception e) {
							log.debug(methodName, "Exception in sending SMS notification "+e);
						}
					
					
					}else {
		        		   log.debug(methodName, "Email has not been triggered.. few batches are left.");
		        	 }
					
				}

			} catch (Exception e) {
				log.debug(methodName, "Exception while execution " + e);
			} finally {
				try {
					if(bwSuccess != null) {
						bwSuccess.close();
					}
				} catch (Exception e) {
					log.debug(methodName, "Could not close BufferedWriter bwSuccess " + e);
				}
				try {
					if(bwFail != null) {
						bwFail.close();
					}
				} catch (Exception e) {
					log.debug(methodName, "Could not close BufferedWriter bwFail " + e);
				}

			}

	          if (log.isDebugEnabled()) {
              log.debug(methodName, "After executing PLSQL p_changeVoucherStatus  Returned=" +  plSqlRetuns[0]);
          }
          if (BTSLUtil.NullToString(plSqlRetuns[1]) != null && BTSLUtil.NullToString(plSqlRetuns[1]).length() > 0) {
              if (log.isDebugEnabled()) {
                  log.debug(methodName, "After Executing PLSQL p_changeVoucherStatus  :" + BTSLUtil.NullToString(plSqlRetuns[1]));
              }
          }
          if (BTSLUtil.NullToString(plSqlRetuns[2]) != null && BTSLUtil.NullToString(plSqlRetuns[1]).length() > 0) {
              if (log.isDebugEnabled()) {
                  log.debug(methodName, "sql exception :" + BTSLUtil.NullToString(plSqlRetuns[2]));
              }
          }
		  }
		  finally{
			  try {
	                if (cstmt != null) {
	                    cstmt.close();
	                }
	            } catch (Exception ex) {
	                log.error(methodName, "  Exception while closing prepared statement ex=" + ex);
	            }
		  }
		  
		return plSqlRetuns;
	}

}
