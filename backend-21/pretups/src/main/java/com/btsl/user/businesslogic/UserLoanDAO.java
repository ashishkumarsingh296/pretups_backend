package com.btsl.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.requesthandler.LastLoanEnqRequestHandler;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

public class UserLoanDAO {
    private final Log _log = LogFactory.getLog(this.getClass().getName());
    
	public static OperatorUtilI calculatorI = null;

	// calculate the tax
	static {
		final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		} catch (Exception e) {
			
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public int  insertUploadUserLoanThreshold(Connection p_con, ArrayList userLoanList) throws BTSLBaseException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        String profileID = null;
        
        ResultSet rs = null;
        int insertCount = 0;
        boolean productFound =false;
        UserLoanVO userLoanVO = null;
        final String methodName = "insertUploadUserLoanThreshold";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: userLoanList= " + userLoanList );
        }
        try {
            int listSize = 0;

            if (userLoanList != null) {
                listSize = userLoanList.size();
            }

         
            StringBuilder strBuff = new StringBuilder("SELECT profile_id,product_code  FROM channel_user_loan_info");
            strBuff.append(" WHERE user_id =?  ");

            String selectQuery = strBuff.toString();
            StringBuilder updateBuff = new StringBuilder("update  channel_user_loan_info  set loan_threhold=?,loan_amount=? ,modified_on=? ,modified_by =? ");
            updateBuff.append(" WHERE user_id =?  and product_code = ? ");
            //StringBuilder deleteBuff = new StringBuilder("delete from channel_user_loan_info  WHERE user_id =?  and LOAN_GIVEN ='N'  and OPTINOUT_ALLOWED ='N' ");
            
            
            String updateQuery = updateBuff.toString();

            
            StringBuilder insertBuff = new StringBuilder("insert into channel_user_loan_info (user_id,profile_id, product_code, loan_threhold, loan_amount,created_on , ");
            insertBuff.append(" created_by,modified_on ,modified_by) values ");
            insertBuff.append(" (?,?, ?, ?, ?,?,?,?,?)");

            

             String insertQuery = insertBuff.toString();
            userLoanVO = new UserLoanVO();
            Date curdate = new Date();
            for (int i = 0; i < listSize; i++) {
            	userLoanVO = (UserLoanVO) userLoanList.get(i);
                if (userLoanVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Query sqlQuery:" + selectQuery);
                    }
                    pstmtSelect = p_con.prepareStatement(selectQuery);
                    int l =0;
                    
                    pstmtSelect.setString(++l, userLoanVO.getUser_id());
                    
                    rs = pstmtSelect.executeQuery();
                    while (rs.next()) {
                    	String productCode = rs.getString("product_code");
                    	if(productCode!= null && productCode.equals(userLoanVO.getProduct_code())){
                    		profileID=rs.getString("profile_id");
                    		if (_log.isDebugEnabled()) {
                    			_log.debug(methodName, "QUERY sqlDelete:" + updateQuery);
                    		}
                    		pstmtUpdate = p_con.prepareStatement(updateQuery);
                    		l =0;
                    		pstmtUpdate.setLong(++l, userLoanVO.getLoan_threhold());
                    		pstmtUpdate.setLong(++l, userLoanVO.getLoan_amount());
                    		pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                    		pstmtUpdate.setString(++l, PretupsI.SYSTEM);
                    		pstmtUpdate.setString(++l, userLoanVO.getUser_id());
                    		pstmtUpdate.setString(++l, userLoanVO.getProduct_code());

                    		insertCount = pstmtUpdate.executeUpdate();
                    		OracleUtil.closeQuietly(pstmtUpdate);
                    		OracleUtil.closeQuietly(pstmtSelect);
                    		productFound= true;
                    		break;
                    	}
                    	else
                    		continue;
                    }
                       
                   if(!productFound) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
                        }
                        // commented for DB2 pstmtInsert
                        pstmtInsert = p_con.prepareStatement(insertQuery);
                        l =0;
                        pstmtInsert.setString(++l, userLoanVO.getUser_id());
                        pstmtInsert.setString(++l, profileID);
                        
                        pstmtInsert.setString(++l, userLoanVO.getProduct_code());
                        pstmtInsert.setLong(++l, userLoanVO.getLoan_threhold());
                        pstmtInsert.setLong(++l, userLoanVO.getLoan_amount());
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        insertCount = pstmtInsert.executeUpdate();
                        OracleUtil.closeQuietly(pstmtInsert);
                       }
                }
            }// end loop
        }// end try block
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
            OracleUtil.closeQuietly(pstmtSelect);
        	OracleUtil.closeQuietly(pstmtInsert);
        	OracleUtil.closeQuietly(pstmtUpdate);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    
	}

	
	public int  updateUserLoanSettlement(Connection p_con, UserLoanVO p_userLoanVO,Date CurDate) throws BTSLBaseException {

        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateUserLoanSettlement";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userLoanVO= " + p_userLoanVO );
        }
        try {
          
      
            StringBuilder updateBuff = new StringBuilder("update  channel_user_loan_info  set loan_given=?,settlement_id=?,settlement_date=?,settlement_loan_amount=?,settlement_loan_interest=? ,settlement_to=? ,modified_on=? ,modified_by =? ");
            updateBuff.append(" WHERE user_id =?  and product_code = ? ");
         
             String updateQuery = updateBuff.toString();
            if (p_userLoanVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Query updateQuery:" + updateQuery);
                    }
                    pstmtUpdate = p_con.prepareStatement(updateQuery);
                    int l =0;
                   
                    pstmtUpdate.setString(++l, p_userLoanVO.getLoan_given());
                    pstmtUpdate.setString(++l, p_userLoanVO.getSettlement_id());
                    pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(CurDate));
                    pstmtUpdate.setLong(++l,p_userLoanVO.getSettlement_loan_amount());
                    pstmtUpdate.setLong(++l,p_userLoanVO.getSettlement_loan_interest());
                    pstmtUpdate.setString(++l,p_userLoanVO.getSettlement_from());
                    pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(CurDate));
                    pstmtUpdate.setString(++l, PretupsI.SYSTEM);
                    pstmtUpdate.setString(++l, p_userLoanVO.getUser_id());
                    pstmtUpdate.setString(++l, p_userLoanVO.getProduct_code());
                    updateCount = pstmtUpdate.executeUpdate();
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Query updateCount:" + updateCount);
                    }
                    OracleUtil.closeQuietly(pstmtUpdate);
                       }
                
           
        }// end try block
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	 OracleUtil.closeQuietly(pstmtUpdate);
           if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    
	}
	
	
	public int  updateUserLoanCredit(Connection p_con, UserLoanVO p_userLoanVO,Date CurDate) throws BTSLBaseException {

        PreparedStatement pstmtUpdate = null;
        int updateCount = 0;
        final String methodName = "updateUserLoanCredit";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userLoanVO= " + p_userLoanVO );
        }
        try {
           
            StringBuilder updateBuff = new StringBuilder("update  channel_user_loan_info  set loan_given=?,loan_given_amount=?,last_loan_date=?,last_loan_txn_id=?,loan_taken_from=?,balance_before_loan=? ,modified_on=? ,modified_by =? ");
            updateBuff.append(" WHERE user_id =?  and product_code = ? ");
         
             String updateQuery = updateBuff.toString();
             if (p_userLoanVO != null) {
            	 if (_log.isDebugEnabled()) {
            		 _log.debug(methodName, "Query updateQuery:" + updateQuery);
            	 }
            	 pstmtUpdate = p_con.prepareStatement(updateQuery);

            	 int l =0;
            	 pstmtUpdate.setString(++l, p_userLoanVO.getLoan_given());
            	 pstmtUpdate.setLong(++l, p_userLoanVO.getLoan_given_amount());
            	 pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(CurDate));
            	 pstmtUpdate.setString(++l,p_userLoanVO.getLast_loan_txn_id());
            	 pstmtUpdate.setString(++l,p_userLoanVO.getLoan_taken_from());
            	 pstmtUpdate.setLong(++l,p_userLoanVO.getBalance_before_loan());
            	 pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(CurDate));
            	 pstmtUpdate.setString(++l, PretupsI.SYSTEM);
            	 pstmtUpdate.setString(++l, p_userLoanVO.getUser_id());
            	 pstmtUpdate.setString(++l, p_userLoanVO.getProduct_code());
            	 updateCount = pstmtUpdate.executeUpdate();
            	 if (_log.isDebugEnabled()) {
            		 _log.debug(methodName, "Query updateCount:" + updateCount);
            	 }
            	 OracleUtil.closeQuietly(pstmtUpdate);
             }
                
           
        }// end try block
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	 OracleUtil.closeQuietly(pstmtUpdate);
           if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    
	}
	
	
	@SuppressWarnings({ "resource", "rawtypes" })
	public int  insertUpdateLoanProfile(Connection p_con, ArrayList userLoanList) throws BTSLBaseException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
        
        ResultSet rs = null;
        int insertCount = 0;
        boolean productFound =false;
        UserLoanVO userLoanVO = null;
        final String methodName = "insertUploadUserLoanThreshold";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: userLoanList= " + userLoanList );
        }
        try {
            int listSize = 0;

            if (userLoanList != null) {
                listSize = userLoanList.size();
            }

         
            StringBuilder strBuff = new StringBuilder("SELECT profile_id,product_code,loan_given  FROM channel_user_loan_info");
            strBuff.append(" WHERE user_id =?  ");

            String selectQuery = strBuff.toString();
            StringBuilder updateBuff = new StringBuilder("update  channel_user_loan_info  set profile_id=? ,modified_on=? ,modified_by =? ");
            updateBuff.append(" WHERE user_id =?  and product_code = ? ");
            //StringBuilder deleteBuff = new StringBuilder("delete from channel_user_loan_info  WHERE user_id =?  and LOAN_GIVEN ='N'  and OPTINOUT_ALLOWED ='N' ");
            
            
            String updateQuery = updateBuff.toString();

            
            StringBuilder insertBuff = new StringBuilder("insert into channel_user_loan_info (user_id,profile_id, product_code, loan_threhold, loan_amount,created_on , ");
            insertBuff.append(" created_by,modified_on ,modified_by) values ");
            insertBuff.append(" (?,?, ?, ?, ?,?,?,?,?)");

            

             String insertQuery = insertBuff.toString();
            userLoanVO = new UserLoanVO();
            Date curdate = new Date();
            for (int i = 0; i < listSize; i++) {
            	userLoanVO = (UserLoanVO) userLoanList.get(i);
                if (userLoanVO != null && userLoanVO.getProfile_id()!=0) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Query sqlQuery:" + selectQuery);
                    }
                    pstmtSelect = p_con.prepareStatement(selectQuery);
                    int l =0;
                    
                    pstmtSelect.setString(++l, userLoanVO.getUser_id());
                    
                    rs = pstmtSelect.executeQuery();
                    while (rs.next()) {
                    	String productCode = rs.getString("product_code");
                    	if(productCode!= null && productCode.equals(userLoanVO.getProduct_code())){
                    		if (_log.isDebugEnabled()) {
                    			_log.debug(methodName, "QUERY sqlDelete:" + updateQuery);
                    		}
                    		
                    	/*	if("Y".equals(rs.getString("loan_given"))) {
								throw new BTSLBaseException(this, methodName, "loan.given");
							}
                    		*/
                    		pstmtUpdate = p_con.prepareStatement(updateQuery);
                    		l =0;
                    		pstmtUpdate.setString(++l, userLoanVO.getProfile_id()+"");
                    		pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                    		pstmtUpdate.setString(++l, PretupsI.SYSTEM);
                    		pstmtUpdate.setString(++l, userLoanVO.getUser_id());
                    		pstmtUpdate.setString(++l, userLoanVO.getProduct_code());
                    		insertCount = pstmtUpdate.executeUpdate();
                    		OracleUtil.closeQuietly(pstmtUpdate);
                    		OracleUtil.closeQuietly(pstmtSelect);
                    		productFound= true;
                    		break;
                    	}
                    	else
                    		continue;
                    }
                       
                   if(!productFound) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
                        }
                        // commented for DB2 pstmtInsert
                        pstmtInsert = p_con.prepareStatement(insertQuery);
                        l =0;
                        pstmtInsert.setString(++l, userLoanVO.getUser_id());
                        pstmtInsert.setString(++l, userLoanVO.getProfile_id()+"");
                        pstmtInsert.setString(++l, userLoanVO.getProduct_code());
                        pstmtInsert.setLong(++l, userLoanVO.getLoan_threhold());
                        pstmtInsert.setLong(++l, userLoanVO.getLoan_amount());
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        insertCount = pstmtInsert.executeUpdate();
                        OracleUtil.closeQuietly(pstmtInsert);
                       }
                }
            }// end loop
        }// end try block

        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
            OracleUtil.closeQuietly(pstmtSelect);
        	OracleUtil.closeQuietly(pstmtInsert);
        	OracleUtil.closeQuietly(pstmtUpdate);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    
	}
	
	public int  insertUpdateUserLoanOptInOptOut(Connection p_con, ArrayList userLoanList) throws BTSLBaseException {

        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtInsert = null;
        PreparedStatement pstmtUpdate = null;
       
        ResultSet rs = null;
        int insertCount = 0;
        boolean productFound =false;
        UserLoanVO userLoanVO = null;
        final String methodName = "insertUpdateUserLoanOptInOptOut";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: userLoanList= " + userLoanList );
        }
        try {
            int listSize = 0;

            if (userLoanList != null) {
                listSize = userLoanList.size();
            }

         
            StringBuilder strBuff = new StringBuilder("SELECT optinout_allowed,loan_threhold,loan_amount,product_code,loan_given,loan_given_amount,profile_id,last_loan_date  FROM channel_user_loan_info");
            strBuff.append(" WHERE user_id =?  and product_code =? ");

            String selectQuery = strBuff.toString();
           
            StringBuilder updateBuff = new StringBuilder("update  channel_user_loan_info  set optinout_allowed=?,optinout_by=?,optinout_on=?,modified_on=? ,modified_by =? ");
            updateBuff.append(" WHERE user_id =?  and product_code = ? ");
            
            String updateQuery = updateBuff.toString();

            StringBuilder insertBuff = new StringBuilder("insert into channel_user_loan_info (user_id, product_code,loan_threhold,loan_amount,created_on , ");
            insertBuff.append(" created_by,modified_on ,modified_by,optinout_allowed,optinout_by,optinout_on) values ");
            insertBuff.append(" (?,?,?,?, ?, ?, ?,?,?,?,?)");

             String insertQuery = insertBuff.toString();
            userLoanVO = new UserLoanVO();
            Date curdate = new Date();
            for (int i = 0; i < listSize; i++) {
            	userLoanVO = (UserLoanVO) userLoanList.get(i);
                if (userLoanVO != null) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Query sqlQuery:" + selectQuery);
                    }
                    pstmtSelect = p_con.prepareStatement(selectQuery);
                    int l =0;
                    
                    pstmtSelect.setString(++l, userLoanVO.getUser_id());
                    pstmtSelect.setString(++l, userLoanVO.getProduct_code());
                    rs = pstmtSelect.executeQuery();
                    while (rs.next()) {
                    	String productCode = rs.getString("product_code");
                    	if(productCode!= null && productCode.equals(userLoanVO.getProduct_code())){
                    		String optInoptOut = rs.getString("optinout_allowed");
                    		String loanGiven = rs.getString("loan_given");
                    		Long loanAmount = rs.getLong("loan_amount");
                    		Long loanThreshold = rs.getLong("loan_threhold");
                    		Long profileID = rs.getLong("profile_id");
                    		Date loanGivenDate = rs.getDate("last_loan_date");
                    		Long loanGivenAmount = rs.getLong("loan_given_amount");
                    		
                    		if (_log.isDebugEnabled()) {
                    			_log.debug(methodName, "QUERY sqlDelete:" + updateQuery);
                    		}
                    		pstmtUpdate = p_con.prepareStatement(updateQuery);
                    		l =0;
                    		
                    		if(PretupsI.YES.equals(loanGiven) && PretupsI.ACCOUNT_TYPE_NORMAL.equals(userLoanVO.getOptinout_allowed())) {
                    			userLoanVO.setLast_loan_date(loanGivenDate);
                    			long premium=0;
                    			LoanProfileDAO  loanProfileDAO= new LoanProfileDAO();
                    			if (profileID!=0) {
                    				ArrayList<LoanProfileDetailsVO>  loanProfileList = loanProfileDAO.loadLoanProfileSlabs(p_con, String.valueOf(profileID));
                    				 premium = calculatorI.calculatePremium(userLoanVO, loanProfileList);
                    			}
                    			String[] arr = new String[] {PretupsBL.getDisplayAmount(loanGivenAmount+ premium),PretupsBL.getDisplayAmount( premium),PretupsBL.getDisplayAmount(loanGivenAmount)}; 
                    			throw new BTSLBaseException(PretupsErrorCodesI.ERROR_OPTOUT_NOT_ALLOWED_DUETO_PENDING_LOAN,arr);

                    		}
                    		if(optInoptOut.equals(userLoanVO.getOptinout_allowed()))
                    		{
                    			if(PretupsI.YES.equals(optInoptOut))
                    				throw new BTSLBaseException(PretupsErrorCodesI.ERROR_ALREADY_OPTIN);
                    			else if (PretupsI.NO.equals(optInoptOut))
                    				throw new BTSLBaseException(PretupsErrorCodesI.ERROR_ALREADY_OPTOUT);
                    		}
                    		pstmtUpdate.setString(++l, userLoanVO.getOptinout_allowed());
                    		pstmtUpdate.setString(++l, PretupsI.SYSTEM);
                     		pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                    		pstmtUpdate.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                    		pstmtUpdate.setString(++l, PretupsI.SYSTEM);
                    		pstmtUpdate.setString(++l, userLoanVO.getUser_id());
                    		pstmtUpdate.setString(++l, userLoanVO.getProduct_code());
                    		insertCount = pstmtUpdate.executeUpdate();
                    		userLoanVO.setLoan_amount(loanAmount);
                    		userLoanVO.setLoan_threhold(loanThreshold);
                    		
                    		OracleUtil.closeQuietly(pstmtUpdate);
                    		OracleUtil.closeQuietly(pstmtSelect);
                    		productFound= true;
                    		break;
                    	}
                    	else
                    		continue;
                    }
                       
                   if(!productFound) {
                        if (_log.isDebugEnabled()) {
                            _log.debug(methodName, "QUERY sqlInsert:" + insertQuery);
                        }
                        // commented for DB2 pstmtInsert
                        pstmtInsert = p_con.prepareStatement(insertQuery);
                        l =0;
                        pstmtInsert.setString(++l, userLoanVO.getUser_id());
                        pstmtInsert.setString(++l, userLoanVO.getProduct_code());
                        pstmtInsert.setLong(++l, userLoanVO.getLoan_threhold());
                        pstmtInsert.setLong(++l, userLoanVO.getLoan_amount());
                        
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        pstmtInsert.setString(++l, userLoanVO.getOptinout_allowed());
                        pstmtInsert.setString(++l, PretupsI.SYSTEM);
                        pstmtInsert.setTimestamp(++l, BTSLUtil.getTimestampFromUtilDate(curdate));
                        insertCount = pstmtInsert.executeUpdate();
                        OracleUtil.closeQuietly(pstmtInsert);
                       }
                }
            }// end loop
        }// end try block
        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (BTSLBaseException be) {
            _log.error(methodName, "SQLException: " + be.getMessage());
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "SQL Exception:" + be.getMessage());
            throw be;
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
            OracleUtil.closeQuietly(pstmtSelect);
        	OracleUtil.closeQuietly(pstmtInsert);
        	OracleUtil.closeQuietly(pstmtUpdate);
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: updateCount=" + insertCount);
            }
        } // end of finally
        return insertCount;
    
	}
	
	public ArrayList<UserLoanVO> loadUserLoanInfoByUserId(Connection con, String userId) throws BTSLBaseException{
		final String methodName = "loadUserLoanInfoByUserId";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered userId= " + userId);
		}
		
		ArrayList<UserLoanVO> userLoanList = new ArrayList();
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		
		try {
            StringBuilder strBuff = new StringBuilder("SELECT user_id, profile_id,product_code,loan_given, loan_amount,loan_threhold, loan_given_amount, last_loan_date, settlement_id, settlement_date, optinout_allowed  FROM channel_user_loan_info");
            strBuff.append(" WHERE user_id = ?  ");

            String selectQuery = strBuff.toString();
    		if(_log.isDebugEnabled()) {
    			_log.debug(methodName, "SelectQry= " + selectQuery);
    		}
    		
    		pstmtSelect = con.prepareStatement(selectQuery);
    		int i = 1;
    		pstmtSelect.setString(i++, userId);
    		rs = pstmtSelect.executeQuery();
    		
    		
    		
    		while(rs.next()) {
    			UserLoanVO userLoanVO = new UserLoanVO();
    			userLoanVO.setUser_id(rs.getString("user_id"));
    			if(BTSLUtil.isNullString(rs.getString("profile_id"))) {
    				userLoanVO.setProfile_id(0);
    			}
    			else
    				userLoanVO.setProfile_id(Integer.parseInt(rs.getString("profile_id")));
    			
    			userLoanVO.setProduct_code(rs.getString("product_code"));
    			userLoanVO.setLoan_given(rs.getString("loan_given"));
    			userLoanVO.setLoan_amount(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("loan_amount"))));
    			userLoanVO.setLoan_threhold(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("loan_threhold"))));
    			userLoanVO.setLoan_given_amount(Long.parseLong(PretupsBL.getDisplayAmount(rs.getLong("loan_given_amount"))));
    			userLoanVO.setLast_loan_date(rs.getTimestamp("last_loan_date"));
    			userLoanVO.setSettlement_id(rs.getString("settlement_id"));
    			userLoanVO.setSettlement_date(rs.getTimestamp("settlement_date"));
    			userLoanVO.setOptinout_allowed(rs.getString("optinout_allowed"));
    			userLoanList.add(userLoanVO);
    		}
			
		}// end of try

        catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserLoanDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
        	OracleUtil.closeQuietly(rs);
            OracleUtil.closeQuietly(pstmtSelect);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting:");
            }
        } // end of finally
		
		
		return userLoanList;
		
		
	}

}
