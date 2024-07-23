package com.btsl.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.OracleUtil;

public class LoanProfileDAO {
	
	private Log log = LogFactory.getLog(this.getClass().getName());
	private LoanProfileQry loanProfileQry = null;
    
	public LoanProfileDAO() {
		loanProfileQry = new LoanProfileOracleQry();
	}
    
       
	
	
	
	
	public int addLoanProfile(Connection con, LoanProfileCombinedVO loanProfileVO) throws BTSLBaseException, SQLException {
		
		final String METHOD_NAME = "addLoanProfile";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, loanProfileVO.toString());
		}
		
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rs = null;
		int count = 0;
		try {
			pstmt = loanProfileQry.addLoanProfileQry(con, loanProfileVO);
			count = pstmt.executeUpdate();
			
			int slabCount = 0;
			if(count>0) {
				// insert in detail table
				String profileID = loanProfileVO.getProfileID();
				for(int i=0; i<loanProfileVO.getLoanProfileDetailsList().size(); i++) {
					int ctr = 0;
					LoanProfileDetailsVO slabVO = (LoanProfileDetailsVO) loanProfileVO.getLoanProfileDetailsList().get(i);
					pstmt1 = loanProfileQry.addLoanProfileDetailsQry(con, slabVO, profileID);
					ctr = pstmt1.executeUpdate();
					slabCount = slabCount + ctr;
				}
				
				if(log.isDebugEnabled()) {
					log.debug(METHOD_NAME, "SLabs inserted: " + slabCount);
				}
				
				if(slabCount == loanProfileVO.getLoanProfileDetailsList().size()) {
					//success
					con.commit();
				}
				else {
					con.rollback();
					count = 0;
				}
				
			}
			else {
				con.rollback();
				count = 0;
			}
			
		
		
		
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[addLoanProfile]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[addLoanProfile]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
        	OracleUtil.closeQuietly(pstmt1);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: insert count=" + count);
            }  
        }
		return count;
	}
	
	
	
	public String isProfileNameExists(Connection con, String profileName) throws BTSLBaseException {
		
		
		final String METHOD_NAME = "isProfileNameExists";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String profileID = "";
		try {
			pstmt = loanProfileQry.isProfileNameExistsQry(con, profileName);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				profileID = rs.getString("PROFILE_ID");
			}
		
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[isProfileNameExists]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[isProfileNameExists]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting: with profileID " + profileID);
            }  
        }
		
		
		return profileID;
	}
	
	
	public ArrayList<LoanProfileCombinedVO> loadLoanProfiles(Connection con, String categoryCode, String networkCode) throws BTSLBaseException{
		final String METHOD_NAME = "loadLoanProfiles";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		
		ArrayList<LoanProfileCombinedVO> combinedList = new ArrayList<LoanProfileCombinedVO>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = loanProfileQry.loadLoanProfilesQry(con, categoryCode, networkCode);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				//store info in vo and add in list
				LoanProfileCombinedVO combinedVO = new LoanProfileCombinedVO();
				combinedVO.setProfileID(rs.getString("PROFILE_ID"));
				combinedVO.setProfileName(rs.getString("PROFILE_NAME"));
				combinedVO.setProfileType(rs.getString("PROFILE_TYPE"));
				combinedVO.setCategoryCode(rs.getString("CATEGORY_CODE"));
				combinedVO.setStatus(rs.getString("STATUS"));
				combinedVO.setNetworkCode("NETWORK_CODE");
				combinedList.add(combinedVO);
			}

		
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[loadLoanProfiles]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[loadLoanProfiles]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }  
        }
		
		
		
		return combinedList;
		
	}
	
	
	public ArrayList<LoanProfileDetailsVO> loadLoanProfileSlabs(Connection con, String profileID) throws BTSLBaseException{
		final String METHOD_NAME = "loadLoanProfileSlabs";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		
		ArrayList<LoanProfileDetailsVO> slabList = new ArrayList();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = loanProfileQry.loadLoanProfileSlabsQry(con, profileID);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				//store info in vo and add in list
				LoanProfileDetailsVO slabVO = new LoanProfileDetailsVO();
				slabVO.setProfileID(rs.getString("PROFILE_ID"));
				slabVO.setProductCode(rs.getString("PRODUCT_CODE"));
				slabVO.setFromRange(rs.getInt("FROM_RANGE"));
				slabVO.setFromRangeAsString(String.valueOf(rs.getInt("FROM_RANGE")));
				slabVO.setToRange(rs.getInt("TO_RANGE"));
				slabVO.setToRangeAsString(String.valueOf(rs.getInt("TO_RANGE")));
				slabVO.setInterestType(rs.getString("INTEREST_TYPE"));
				//slabVO.setInterestValue(rs.getDouble("INTEREST_VALUE"));
				//slabVO.setInterestValueAsString(String.valueOf(rs.getDouble("INTEREST_VALUE")));
				slabVO.setInterestValue(Double.parseDouble(PretupsBL.getDisplayAmount(rs.getDouble("INTEREST_VALUE"))));
				slabVO.setInterestValueAsString(PretupsBL.getDisplayAmount(rs.getDouble("INTEREST_VALUE")));
				slabVO.setProfileType(rs.getString("PROFILE_TYPE"));
				slabList.add(slabVO);
			}

		
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[loadLoanProfileSlabs]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[loadLoanProfileSlabs]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }  
        }
		
		
		
		return slabList;
		
	}
	
	public LoanProfileCombinedVO loadLoanProfileById(Connection con, String profileID) throws BTSLBaseException{
		
		final String METHOD_NAME = "loadLoanProfileById";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		
		LoanProfileCombinedVO combinedVO = new LoanProfileCombinedVO();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = loanProfileQry.loadLoanProfileByIdQry(con, profileID);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				combinedVO.setProfileID(rs.getString("PROFILE_ID"));
				combinedVO.setProfileName(rs.getString("PROFILE_NAME"));
				combinedVO.setProfileType(rs.getString("PROFILE_TYPE"));
			}
			
			
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[loadLoanProfileById]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[loadLoanProfileById]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }  
        }
		
		
		
		
		return combinedVO;
	}
	
	
	public int updateLoanProfiles(Connection con, LoanProfileCombinedVO loanProfileVO) throws BTSLBaseException, SQLException{
		
		final String METHOD_NAME = "updateLoanProfiles";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		int count = 0;
		try {
			// update Loan Profile table
			pstmt = loanProfileQry.updateLoanProfileQry(con, loanProfileVO);
			count = pstmt.executeUpdate();
			int slabCount = 0;
			if(count>0) {
				//delete loan profile details based on id
				String profileID = loanProfileVO.getProfileID();
				pstmt1 = loanProfileQry.deleteLoanSlabsById(con, profileID);
				int deleteCount = pstmt1.executeUpdate();
				if(deleteCount>0) {
					// insert new slabs based on id
					
					for(int i=0; i<loanProfileVO.getLoanProfileDetailsList().size(); i++) {
						int ctr = 0;
						LoanProfileDetailsVO slabVO = (LoanProfileDetailsVO) loanProfileVO.getLoanProfileDetailsList().get(i);
						pstmt2 = loanProfileQry.addLoanProfileDetailsQry(con, slabVO, profileID);
						ctr = pstmt2.executeUpdate();
						slabCount = slabCount + ctr;
					}
					
					if(log.isDebugEnabled()) {
						log.debug(METHOD_NAME, "SLabs inserted: " + slabCount);
					}
					if(slabCount == loanProfileVO.getLoanProfileDetailsList().size()) {
						//success
						con.commit();
					}
					else {
						con.rollback();
						count = 0;
					}
				}
				else {
					con.rollback();
				}
				
			}
			else {
				con.rollback();
			}
		
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[updateLoanProfiles]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[updateLoanProfiles]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }  
        }
		return count;
	}
	
	public int deleteLoanProfile(Connection con, String profileID) throws BTSLBaseException, SQLException{
		final String METHOD_NAME = "deleteLoanProfile";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs = null;
		int slabCount = 0;
		int count = 0;
		try {
			pstmt = loanProfileQry.deleteLoanSlabsById(con, profileID);
			slabCount = pstmt.executeUpdate();
			
			if(slabCount>0) {
				pstmt1 = loanProfileQry.deleteLoanProfileQry(con, profileID);
				count = pstmt1.executeUpdate();
				if(count>0) {
					con.commit();
				}
				else {
					con.rollback();
				}
			}
			else {
				con.rollback();
			}
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[deleteLoanProfile]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[deleteLoanProfile]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }  
        }
		
		
		
		return count;
	}
	
	
	public boolean isProfileAssociated(Connection con, String profileID) throws BTSLBaseException, SQLException  {
		final String METHOD_NAME = "isProfileAssociated";
		if(log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered ");
		}
		boolean flag = false;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = loanProfileQry.isProfileAssociatedQry(con, profileID);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				flag = true;
			}
		}
		catch(SQLException sqe){
        	log.error(METHOD_NAME, "SQLException : " + sqe);
            log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[isProfileAssociated]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "", "error.general.sql.processing");
		}
		catch (Exception ex) {
            log.error(METHOD_NAME, "Exception : " + ex);
            log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoanProfileDAO[isProfileAssociated]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, METHOD_NAME, "error.general.processing");
        }finally {
        	OracleUtil.closeQuietly(rs);
        	OracleUtil.closeQuietly(pstmt);
            if (log.isDebugEnabled()) {
                log.debug(METHOD_NAME, "Exiting");
            }  
        }
		
		
		return flag;
	}
	
	
	
}
