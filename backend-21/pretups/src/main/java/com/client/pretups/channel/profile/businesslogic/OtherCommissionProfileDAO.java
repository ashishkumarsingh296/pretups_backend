/*
 * COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected
 * by copyright law and international treaty provisions. Unauthorized
 * reproduction or redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties and will be
 * prosecuted to the maximum extent possible under the law.
 * Comviva reserves all rights not expressly granted. You may not
 * reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.

 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.client.pretups.channel.profile.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @author manish.doodi
 *
 *Other Commission Profile DAO
 */
public class OtherCommissionProfileDAO {
	
	/**
	 * Field log.
	 */
	private final Log log = LogFactory.getLog(this.getClass().getName());
	static final String CLASS_NAME = "OtherCommissionProfileDAO";

	
	/**
	 * Method for inserting Other Commission Profile Set.
	 * 
	 * @param con java.sql.Connection
	 * @param profileSetVO OtherCommissionProfileSetVO
	 * @return insertCount int
	 * @throws  BTSLBaseException
	
	 */
	public int addOtherCommissionProfileSet(Connection con, OtherCommissionProfileSetVO profileSetVO) throws BTSLBaseException
	{
		final String methodName = "addOtherCommissionProfileSet";
	    PreparedStatement psmtInsert = null;
		int insertCount = 0;
		int otherCommissionIndex=1;
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: profileSetVO= " + profileSetVO.toString());
		}
		try
		{
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("INSERT INTO OTHER_COMM_PRF_SET (OTH_COMM_PRF_SET_ID,");
			strBuff.append("OTH_COMM_PRF_SET_NAME,OTH_COMM_PRF_TYPE,");
			strBuff.append("OTH_COMM_PRF_TYPE_VALUE,NETWORK_CODE,CREATED_ON,CREATED_BY,MODIFIED_ON,MODIFIED_BY,STATUS,O2C_CHECK_FLAG,C2C_CHECK_FLAG");
			strBuff.append(") values ");
			strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?)");
			String insertQuery = strBuff.toString();		
			if (log.isDebugEnabled()){log.debug(methodName, "Query sqlInsert::" + insertQuery);}							
			psmtInsert = con.prepareStatement(insertQuery); 
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getCommProfileSetId());			
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getProfileName());
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getCommissionType());
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getCommissionTypeValue());
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getNetworkCode());
			psmtInsert.setTimestamp(otherCommissionIndex++, BTSLUtil.getTimestampFromUtilDate(profileSetVO.getCreatedOn()));
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getCreatedBy());
			psmtInsert.setTimestamp(otherCommissionIndex++, BTSLUtil.getTimestampFromUtilDate(profileSetVO.getModifiedOn()));
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getModifiedBy());			
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getStatus());
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getO2cFlag());
			psmtInsert.setString(otherCommissionIndex++, profileSetVO.getC2cFlag());	
			insertCount = psmtInsert.executeUpdate();
			
		} // end of try
		catch (SQLException sqle)
		{
			log.error(methodName, "SQLException::" + sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally
		{
			try{
		        if (psmtInsert!= null){
		        	psmtInsert.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (log.isDebugEnabled()){log.debug(methodName, "Exiting: insertCount=" + insertCount);}
		} // end of finally

		return insertCount;
	}
	
	/**
	 * Method for inserting Other Commission Profile Details List.
	 * 
	 * @param con java.sql.Connection
	 * @param detailVOList ArrayList
	 * @param profileSetVO OtherCommissionProfileSetVO
	 * @return insertCount int
	 * @throws  BTSLBaseException
	
	 */
	public int addOtherCommissionProfileDetailsList(Connection con, ArrayList detailVOList,OtherCommissionProfileSetVO profileSetVO) throws BTSLBaseException
	{
	    PreparedStatement psmtInsert = null;
	    int otherCommissionIndex=1;
		int insertCount = 0;
		final String methodName = "addOtherCommissionProfileDetailsList";
		if (log.isDebugEnabled()){log.debug(methodName, "Entered: Inserted detailVOList Size= " + detailVOList.size());}

		try
		{
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("INSERT INTO OTHER_COMM_PRF_DETAILS (OTH_COMM_PRF_DETAIL_ID,");
			strBuff.append("OTH_COMM_PRF_SET_ID,START_RANGE,END_RANGE,OTH_COMMISSION_TYPE,");
			strBuff.append("OTH_COMMISSION_RATE) ");
			strBuff.append(" values (?,?,?,?,?,?)");
			String insertQuery = strBuff.toString();
			
			if (log.isDebugEnabled()){log.debug(methodName, "Query sqlInsert:" + insertQuery);}    

			psmtInsert = con.prepareStatement(insertQuery);
			CommissionProfileDeatilsVO detailVO = null;
			for (int i = 0, j = detailVOList.size(); i < j; i++)
			{
				otherCommissionIndex=1;
			    detailVO = (CommissionProfileDeatilsVO)detailVOList.get(i);
			    psmtInsert.setString(otherCommissionIndex++, detailVO.getCommProfileDetailID());
				psmtInsert.setString(otherCommissionIndex++, profileSetVO.getCommProfileSetId());
				psmtInsert.setLong(otherCommissionIndex++, detailVO.getStartRange());
				psmtInsert.setLong(otherCommissionIndex++, detailVO.getEndRange());
				psmtInsert.setString(otherCommissionIndex++, detailVO.getCommType());
				psmtInsert.setDouble(otherCommissionIndex++, detailVO.getCommRate());
				
				insertCount = psmtInsert.executeUpdate();
	
				psmtInsert.clearParameters();
				// check the status of the insert
				if (insertCount <= 0)
				{
				    throw new BTSLBaseException(this, "addCommissionProfileDetailsList", "error.general.sql.processing");
				}
			}
			
		} // end of try
		catch(BTSLBaseException be)
		{
		    throw be;
		}
		catch (SQLException sqle)
		{
			log.error(methodName, "SQLException::" + sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally
		{
			try{
		        if (psmtInsert!= null){
		        	psmtInsert.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (log.isDebugEnabled()){log.debug(methodName, "Exiting: insertCount=" + insertCount);}
		} // end of finally

		return insertCount;
	}

	/**
	 * Method for loading Other Commission Profile Set Details List.
	 * 
	 * @param con java.sql.Connection
	 * @param networkCode String
	 * @param commType String
	 * @param commTypeValuee String
	 * @return ArrayList OtherCommissionProfileSetVO
	 * @throws  BTSLBaseException
	
	 */
	
  public ArrayList loadOtherCommissionProfileSet(Connection con,String networkCode,String commType , String commTypeValue) throws BTSLBaseException
	{
	  
	  final String methodName = "loadOtherCommissionProfileSet";
		if (log.isDebugEnabled()){log.debug(methodName, "Entered networkCode="+networkCode+" commType="+commType+ " commTypeValue="+commTypeValue);}

		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;

		final StringBuilder strBuff = new StringBuilder();
		strBuff.append(" SELECT OTH_COMM_PRF_SET_ID,OTH_COMM_PRF_SET_NAME,OTH_COMM_PRF_TYPE,OTH_COMM_PRF_TYPE_VALUE,");
		strBuff.append("NETWORK_CODE,CREATED_ON,CREATED_BY, MODIFIED_ON,MODIFIED_BY, ");
		strBuff.append(" STATUS,O2C_CHECK_FLAG,C2C_CHECK_FLAG FROM OTHER_COMM_PRF_SET");
		strBuff.append(" WHERE NETWORK_CODE = ?");
		strBuff.append(" AND OTH_COMM_PRF_TYPE = ? AND OTH_COMM_PRF_TYPE_VALUE = ? ANd STATUS <> 'N'");
		strBuff.append(" ORDER BY OTH_COMM_PRF_SET_NAME");

		String sqlSelect = strBuff.toString();
		if (log.isDebugEnabled())
		{
		    log.debug(methodName, "QUERY sqlSelect:=" + sqlSelect);
		}

		ArrayList list = new ArrayList();
		try
		{
		    pstmtSelect = con.prepareStatement(sqlSelect);
		    pstmtSelect.setString(1,networkCode);
		    pstmtSelect.setString(2,commType);
		    pstmtSelect.setString(3,commTypeValue);

			rs = pstmtSelect.executeQuery();
			while (rs.next())
			{
				
				 OtherCommissionProfileSetVO otherCommissionProfileSetVO = new OtherCommissionProfileSetVO();
	                otherCommissionProfileSetVO.setCommProfileSetId(rs.getString("OTH_COMM_PRF_SET_ID"));
	                otherCommissionProfileSetVO.setProfileName(rs.getString("OTH_COMM_PRF_SET_NAME"));
	                otherCommissionProfileSetVO.setNetworkCode(rs.getString("NETWORK_CODE"));		                
	                otherCommissionProfileSetVO.setCreatedOn(rs.getDate("CREATED_ON"));
	                otherCommissionProfileSetVO.setCreatedBy(rs.getString("CREATED_BY"));
	                otherCommissionProfileSetVO.setModifiedOn(rs.getDate("MODIFIED_ON"));
	                otherCommissionProfileSetVO.setModifiedBy(rs.getString("MODIFIED_BY"));		                
	                otherCommissionProfileSetVO.setStatus(rs.getString("STATUS"));
	                otherCommissionProfileSetVO.setCommissionType(rs.getString("OTH_COMM_PRF_TYPE"));
	                otherCommissionProfileSetVO.setCommissionTypeValue(rs.getString("OTH_COMM_PRF_TYPE_VALUE"));
	                otherCommissionProfileSetVO.setO2cFlag(rs.getString("O2C_CHECK_FLAG"));
	                otherCommissionProfileSetVO.setC2cFlag(rs.getString("C2C_CHECK_FLAG"));
	                list.add(otherCommissionProfileSetVO);
			}

		} catch (SQLException sqe)
		{
			log.error(methodName, "SQLException :: " + sqe);
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			log.error(methodName, "Exception :: " + ex);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmtSelect!= null){
		        	pstmtSelect.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled())
			{
				log.debug(methodName, "Exiting: commissionProfileSetList size=" + list.size());
			}
		}
		return list;
	}

/**
	 * Method for checking Other Commission Profile Name is Associated with Commission Profile.
	 * 
	 * @param con  java.sql.Connection
	 * @param commProfileSetId String
	 * @param status String
	 * @return flag boolean
	 * @throws  BTSLBaseException	
	 */
	public boolean isOtherCommissionProfileSetAssociated(Connection con,String commProfileSetId, String status) throws BTSLBaseException
	{
		final String methodName = "isOtherCommissionProfileSetAssociated";
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: commProfileSetId="+commProfileSetId+" status="+status);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean existFlag = false;
		final StringBuilder strBuff = new StringBuilder();
		
		strBuff.append("SELECT OTH_COMM_PRF_SET_NAME FROM OTHER_COMM_PRF_SET cps, COMMISSION_PROFILE_SET_VERSION cs , COMMISSION_PROFILE_SET cpsn");
		strBuff.append(" WHERE cps.OTH_COMM_PRF_SET_ID = ? AND cps.OTH_COMM_PRF_SET_ID = cs.OTH_COMM_PRF_SET_ID and cs.comm_profile_set_id = cpsn.comm_profile_set_id ");
		strBuff.append("  AND cpsn.status not in ("+status +")");
		
		String sqlSelect = strBuff.toString();
		
		if (log.isDebugEnabled())
		{
		    log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}    

		try
		{
			pstmt = con.prepareStatement(sqlSelect);
			pstmt.setString(1, commProfileSetId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()){existFlag = true;}

			return existFlag;
		} catch (SQLException sqe)
		{
			log.error(methodName, "SQLException : " + sqe);
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			log.error(methodName, "Exception : " + ex);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (log.isDebugEnabled()){log.debug(methodName, "Exiting: existFlag=" + existFlag);}
		}
	}
	
	/**
	 * Method for Deleting Other Commission Profile Set(just update the status set status=N).
	 * 
	 * @param con java.sql.Connection
	 * @param commissionProfileSetVO OtherCommissionProfileSetVO
	 * @return deleteCount int
	 * @throws  BTSLBaseException
	
	 */
	public int deleteOtherCommissionProfileSet(Connection con, OtherCommissionProfileSetVO commissionProfileSetVO) throws BTSLBaseException
	{
		final String methodName = "deleteOtherCommissionProfileSet";
  	    PreparedStatement psmtDelete = null;
		int deleteCount = 0;
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: commissionProfileSetVO="+commissionProfileSetVO);
		}
		try
		{
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE OTHER_COMM_PRF_SET SET status = ? , modified_by = ? , modified_on = ? ");
			strBuff.append("WHERE OTH_COMM_PRF_SET_ID = ?");
			String deleteQuery = strBuff.toString();
			if (log.isDebugEnabled()){log.debug(methodName, "Query sqlDelete:" + deleteQuery);}    
			psmtDelete = con.prepareStatement(deleteQuery);
			psmtDelete.setString(1, commissionProfileSetVO.getStatus());
			psmtDelete.setString(2, commissionProfileSetVO.getModifiedBy());
			psmtDelete.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(commissionProfileSetVO.getModifiedOn()));
			psmtDelete.setString(4, commissionProfileSetVO.getCommProfileSetId());
			
			deleteCount = psmtDelete.executeUpdate();
		} // end of try
		catch (SQLException sqle)
		{
			log.error(methodName, "SQLException: " + sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally
		{
			try{
		        if (psmtDelete!= null){
		        	psmtDelete.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (log.isDebugEnabled()){log.debug(methodName, "Exiting: deleteCount=" + deleteCount);}
		} // end of finally

		return deleteCount;
	}
	
	/**
	 * Method for loading Other Commission Profile Details  by  Other Commission Profile Set ID
	 * 
	 * @param con java.sql.Connection
	 * @param selectCommProfileSetID String
	 * @return ArrayList CommissionProfileDeatilsVO
	 * @throws  BTSLBaseException
	
	 */
	
	public ArrayList loadOtherCommissionProfileDetailList(Connection con,String selectCommProfileSetID) throws BTSLBaseException
	{
        
		final String methodName = "loadOtherCommissionProfileDetailList";
	 	if(log.isDebugEnabled())log.debug(methodName,"Entered selectCommProfileSetID="+selectCommProfileSetID);
		PreparedStatement pstmtSelect=null;
		ResultSet rs=null;
		ArrayList commissionDetailList = new ArrayList();
		CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
		try
		{
			final StringBuilder selectQueryBuff =new StringBuilder(" SELECT OTH_COMM_PRF_DETAIL_ID,");
			selectQueryBuff.append(" START_RANGE,END_RANGE,OTH_COMMISSION_TYPE,OTH_COMMISSION_RATE"); 
			selectQueryBuff.append(" FROM OTHER_COMM_PRF_DETAILS WHERE OTH_COMM_PRF_SET_ID = ? ");
			
			String selectQuery=selectQueryBuff.toString();
			if(log.isDebugEnabled())log.debug(methodName,"select query:"+selectQuery );
			pstmtSelect = con.prepareStatement(selectQuery);
			pstmtSelect.setString(1,selectCommProfileSetID);
		
			rs = pstmtSelect.executeQuery();
			
			while(rs.next())
			{
				commissionProfileDeatilsVO=new CommissionProfileDeatilsVO();
				commissionProfileDeatilsVO.setCommProfileDetailID(rs.getString("OTH_COMM_PRF_DETAIL_ID"));
				commissionProfileDeatilsVO.setStartRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("START_RANGE")));
				commissionProfileDeatilsVO.setEndRangeAsString(PretupsBL.getDisplayAmount(rs.getLong("END_RANGE")));

				commissionProfileDeatilsVO.setCommType(rs.getString("OTH_COMMISSION_TYPE"));
				commissionProfileDeatilsVO.setCommRate(rs.getDouble("OTH_COMMISSION_RATE"));
				if(PretupsI.AMOUNT_TYPE_AMOUNT.equals(commissionProfileDeatilsVO.getCommType()))
				{
				    commissionProfileDeatilsVO.setCommRateAsString(PretupsBL.getDisplayAmount(rs.getLong("OTH_COMMISSION_RATE")));
				}
				else
				{
				    commissionProfileDeatilsVO.setCommRateAsString(String.valueOf(commissionProfileDeatilsVO.getCommRate()));
				}
				
				commissionDetailList.add(commissionProfileDeatilsVO);
			}
				
			return commissionDetailList;
		}//end of try
		catch (SQLException sqle)
		{
			log.error(methodName,"SQLException "+sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(CLASS_NAME,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}//end of catch
		catch (Exception e)
		{
			log.error(methodName,"Exception "+e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(CLASS_NAME,methodName,PretupsErrorCodesI.P2P_ERROR_EXCEPTION);
		}//end of catch
		finally
		{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmtSelect!= null){
		        	pstmtSelect.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if(log.isDebugEnabled())log.debug(methodName,"Exiting commissionDetailList Size:"+commissionDetailList.size());
		 }//end of finally
            
	}

	/**
	 * Method for checking Other Commission Profile Name is already exist or not.
	 * 
	 * @param con  java.sql.Connection
	 * @param networkCode String
	 * @param profileName String
	 * @param setId String
	 * @return flag boolean
	 * @throws  BTSLBaseException
	
	 */
     public boolean isOtherCommissionProfileSetNameExist(Connection con,String networkCode, String profileName, String setId) throws BTSLBaseException
	{
    	 final String methodName = "isOtherCommissionProfileSetNameExist";
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: networkCode="+networkCode+" profileName=" + profileName+" setId="+setId);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean existFlag = false;
		final StringBuilder strBuff = new StringBuilder();
		/*
		 * In add mode setId is null but in edit mode setId is not null
		 * because  we have tp apply the where claue
		 */
		if(BTSLUtil.isNullString(setId))
		{
			strBuff.append("SELECT OTH_COMM_PRF_SET_NAME FROM OTHER_COMM_PRF_SET ");
			strBuff.append("WHERE network_code = ? AND upper(OTH_COMM_PRF_SET_NAME) = upper(?)");
		}
		else
		{
		    strBuff.append("SELECT OTH_COMM_PRF_SET_NAME FROM OTHER_COMM_PRF_SET ");
			strBuff.append("WHERE network_code = ? AND upper(OTH_COMM_PRF_SET_NAME) = upper(?) AND OTH_COMM_PRF_SET_ID != ?");
		}
		String sqlSelect = strBuff.toString();
		
		if (log.isDebugEnabled())
		{
		    log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}    

		try
		{
			pstmt = con.prepareStatement(sqlSelect);
			if(BTSLUtil.isNullString(setId))
			{
			    pstmt.setString(1, networkCode);
			    pstmt.setString(2, profileName);
			}
			else
			{
			    pstmt.setString(1, networkCode);
			    pstmt.setString(2, profileName);
			    pstmt.setString(3, setId);
			}
			rs = pstmt.executeQuery();
			
			if (rs.next()){existFlag = true;}

			return existFlag;
		} catch (SQLException sqe)
		{
			log.error(methodName, "SQLException : " + sqe);
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			log.error("isCommissionProfileSetNameExist", "Exception : " + ex);
			log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (log.isDebugEnabled()){log.debug(methodName, "Exiting: existFlag=" + existFlag);}
		}
	}

     /**
 	 * Method for updating Other Commission Profile set 
 	 * 
 	 * @param con  java.sql.Connection
 	 * @param commissionProfileSetVO OtherCommissionProfileSetVO 
 	 * @return updateCount int
 	 * @throws  BTSLBaseException	
 	 */  
     
     
     public int updateOtherCommissionProfileSet(Connection con, OtherCommissionProfileSetVO commissionProfileSetVO) throws BTSLBaseException
	{
    	 final String methodName = "updateOtherCommissionProfileSet";
	    PreparedStatement psmtUpdate = null;
	    int updateCount = 0;

		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: commissionProfileSetVO="+commissionProfileSetVO);
		}

		try
		{
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("UPDATE OTHER_COMM_PRF_SET SET OTH_COMM_PRF_SET_NAME = ? ,");
			strBuff.append("MODIFIED_ON = ?, MODIFIED_BY = ? ,O2C_CHECK_FLAG = ?, C2C_CHECK_FLAG = ? ");
			strBuff.append("WHERE OTH_COMM_PRF_SET_ID = ?");

			String insertQuery = strBuff.toString();
			if (log.isDebugEnabled())
			{
			    log.debug(methodName, "Query sqlInsert:" + insertQuery);
			}

			psmtUpdate = con.prepareStatement(insertQuery);
			psmtUpdate.setString(1, commissionProfileSetVO.getProfileName());
			psmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(commissionProfileSetVO.getModifiedOn()));
			psmtUpdate.setString(3, commissionProfileSetVO.getModifiedBy());
			psmtUpdate.setString(4, commissionProfileSetVO.getO2cFlag());
			psmtUpdate.setString(5, commissionProfileSetVO.getC2cFlag());
			psmtUpdate.setString(6, commissionProfileSetVO.getCommProfileSetId());
	
	
			boolean modified = this.recordModifiedOther(con, commissionProfileSetVO.getCommProfileSetId(), commissionProfileSetVO.getLastModifiedOn());

			// if modified = true mens record modified by another user
			if (modified)
			{
		    	throw new BTSLBaseException("error.modified");
			}	
			updateCount = psmtUpdate.executeUpdate();
			
		} // end of try
		catch(BTSLBaseException be)
		{
		    log.error(methodName, "BTSLBaseException:" + be.toString());
		    throw be;
		}
		catch (SQLException sqle)
		{
			log.error(methodName, "SQLException: " + sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally
		{
			try{
		        if (psmtUpdate!= null){
		        	psmtUpdate.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled())
			{
				log.debug(methodName, "Exiting: updateCount=" + updateCount);
			}
		} // end of finally

		return updateCount;
	}

/**
	 * Method for Deleting Commission Profile Details.
	 * 
	 * @param con java.sql.Connection
	 * @param selectCommProfileSetID String
	 * 
	 * @return deleteCount int
	 * @throws  BTSLBaseException
	
	 */
	public int deleteOtherCommissionProfileDetails(Connection con, String selectCommProfileSetID) throws BTSLBaseException
	{
		final String methodName = "deleteOtherCommissionProfileDetails";
  	    PreparedStatement psmtDelete = null;
		int deleteCount = 0;
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: selectCommProfileSetID="+selectCommProfileSetID);
		}
		try
		{
			final StringBuilder strBuff = new StringBuilder();
			strBuff.append("DELETE FROM OTHER_COMM_PRF_DETAILS ");
			strBuff.append("WHERE OTH_COMM_PRF_SET_ID = ?");
			String deleteQuery = strBuff.toString();
			if (log.isDebugEnabled()){log.debug(methodName, "Query sqlDelete:" + deleteQuery);}    
			psmtDelete = con.prepareStatement(deleteQuery);
			psmtDelete.setString(1, selectCommProfileSetID);
			
			deleteCount = psmtDelete.executeUpdate();
		} // end of try
		catch (SQLException sqle)
		{
			log.error(methodName, "SQLException: " + sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally
		{
			try{
		        if (psmtDelete!= null){
		        	psmtDelete.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (log.isDebugEnabled()){log.debug(methodName, "Exiting: deleteCount=" + deleteCount);}
		} // end of finally

		return deleteCount;
	}
	
	/**
	 * This method is used to check whether the record in the database is
	 * modified or not If there is any error then throws the SQLException
	 * 
	 * @param con Connection
	 * @param commProfileSetID String
	 * @param oldLastModified long
	 * @return boolean
	 * @throws  BTSLBaseException
	
	 */
	public boolean recordModifiedOther(Connection con, String commProfileSetID, long oldLastModified) throws BTSLBaseException
	{
		final String methodName = "recordModifiedOther";
		if (log.isDebugEnabled())
		{
			log.debug(methodName, "Entered: commProfileSetID= " + commProfileSetID + "oldLastModified= " + oldLastModified);
		}

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean modified = false;
		String sqlRecordModified = "SELECT MODIFIED_ON FROM OTHER_COMM_PRF_SET WHERE OTH_COMM_PRF_SET_ID = ?";
		Timestamp newLastModified = null;
		if ((oldLastModified) == 0)
		{
			return false;
		}
		try
		{
		    if (log.isDebugEnabled())
			{
		        log.debug(methodName, "QUERY: sqlselect= " + sqlRecordModified);
			}
			// create a prepared statement and execute it
			pstmt = con.prepareStatement(sqlRecordModified);
			pstmt.setString(1, commProfileSetID);
			rs = pstmt.executeQuery();
			if (rs.next())
			{
				newLastModified = rs.getTimestamp("modified_on");
			}
			if (log.isDebugEnabled())
			{
				log.debug(methodName, " old=" + oldLastModified);
				log.debug(methodName, " new=" + newLastModified.getTime());
			}
			if (newLastModified.getTime() != oldLastModified)
			{
				modified = true;
			}

			return modified;
		} // end of try
		catch (SQLException sqle)
		{
			log.error(methodName, "SQLException: " + sqle.getMessage());
			log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e)
		{
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,CLASS_NAME+"["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch

		finally
		{
			try{
		        if (rs!= null){
		        	rs.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing result set.", e);
		      }
			if (log.isDebugEnabled())
			{
				log.debug(methodName, "Exititng: modified=" + modified);
			}
		} // end of finally
	} // end recordModified

}
