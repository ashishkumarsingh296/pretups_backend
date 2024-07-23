package com.btsl.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;

public class ProductTypeDAO {

    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method for inserting User Products Info.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * @param p_productCodes
     *            String[]
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addUserProductsList(Connection p_con, String p_userId, String[] p_productCodes) throws BTSLBaseException {
        PreparedStatement psmtInsert = null;
        int insertCount = 0;
        final String methodName = "addUserProductsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered: p_userId= " + p_userId + " p_productCodes Size= " + p_productCodes.length);
        }
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_product_types (user_id,");
            strBuff.append("product_type) values (?,?)");
            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlInsert:" + insertQuery);
            }
            psmtInsert = p_con.prepareStatement(insertQuery);
            for (int i = 0, j = p_productCodes.length; i < j; i++) {
                psmtInsert.setString(1, p_userId);
                psmtInsert.setString(2, p_productCodes[i]);
                insertCount = psmtInsert.executeUpdate();
                psmtInsert.clearParameters();
                // check the status of the update
                if (insertCount <= 0) {
                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
                }
            }

        } // end of try
        catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException: " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[addUserProductsList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _log.error(methodName, "Exception: " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[addUserProductsList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
        finally {
            try {
                if (psmtInsert != null) {
                    psmtInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: insertCount=" + insertCount);
            }
        } // end of finally

        return insertCount;
    }

    /**
     * Method for loading Users Assigned Products List(means Products that are
     * assigned to the user).
     * From the table USER_PRODUCT_TYPES
     * 
     * @param p_con
     *            java.sql.Connection
     * @param p_userId
     *            String
     * 
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserProductsList(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserProductsList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userId=" + p_userId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT product_type FROM user_product_types WHERE user_id = ? ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getString("product_type"));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[loadUserProductsList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[loadUserProductsList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userProductsList size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method to load the product type list associated for the user (Used For
     * Login User)
     * 
     * @param p_con
     * @param p_userId
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserProductsListForLogin(Connection p_con, String p_userId) throws BTSLBaseException {
        final String methodName = "loadUserProductsListForLogin";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered p_userId=" + p_userId);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer strBuff = new StringBuffer(" SELECT UPT.product_type, LK.lookup_name ");
        strBuff.append(" FROM user_product_types UPT, lookups LK ");
        strBuff.append(" WHERE UPT.user_id =? AND UPT.product_type=LK.lookup_code AND LK.lookup_type=? ");
        strBuff.append("  and LK.status <> 'N' ");
        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_userId);
            pstmt.setString(2, PretupsI.PRODUCT_TYPE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("lookup_name"), rs.getString("product_type")));
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[loadUserProductsListForLogin]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[loadUserProductsListForLogin]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userProductsList size=" + list.size());
            }
        }
        return list;
    }
    
    /**
	 * Method for loading Users Assigned Products List(means Products that are assigned to the user).
	 * From the table USER_PRODUCT_TYPES
	 * 
	 * @param p_con java.sql.Connection
	 * @param p_userId String
	 * 
	 * @return java.util.ArrayList
	 * @throws  BTSLBaseException
	
	 */
	public List loadUserProductsListForWithdrawViaAdmin(Connection con,String userId) throws BTSLBaseException
	{
		final String methodName = "loadUserProductsListForWithdrawViaAdmin";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered userId="+userId);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer strBuff = new StringBuffer();
		strBuff.append(" SELECT upt.product_type, p.product_code FROM user_product_types upt,products p WHERE upt.user_id = ? and p.product_type=upt.product_type ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		List list=new ArrayList();
		try
		{
			pstmt = con.prepareStatement(sqlSelect);
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				list.add(rs.getString("product_code"));
			}
		} catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName,sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProductTypeDAO[loadUserProductsList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
		    _log.error(methodName, "Exception : " + ex);
		    _log.errorTrace(methodName,ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ProductTypeDAO[loadUserProductsList]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
		    try{
		    	if (rs != null){
		    		rs.close();
		    		}
		    	} 
		    catch (Exception e){
		    	_log.errorTrace(methodName,e);
		    }
			try{
				if (pstmt != null){
					pstmt.close();
					}
				} 
			catch (Exception e){
				_log.errorTrace(methodName,e);
				}
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: userProductsList size=" + list.size());
			}
		}
		return list;
	}
	
	public String getProductType(Connection con, String product_code) throws BTSLBaseException
	{
		final String methodName = "getProductType";
		String productType ="";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered product_code="+product_code);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = "select product_type,product_name from products where product_code = ?";
		
		try {
			pstmt = con.prepareStatement(sqlSelect);
			pstmt.setString(1, product_code);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				productType = rs.getString("product_type");
			}
			
			return productType;
		} catch (SQLException ex) {
			 _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[loadUserProductsListForLogin]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			try{
		    	if (rs != null){
		    		rs.close();
		    		}
		    	} 
		    catch (Exception e){
		    	_log.errorTrace(methodName,e);
		    }
			try{
				if (pstmt != null){
					pstmt.close();
					}
				} 
			catch (Exception e){
				_log.errorTrace(methodName,e);
				}
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: getProductType" );
			}
		}
		
	}
	public ArrayList<C2sBalanceQueryVO> getProductsDetails(Connection con) throws BTSLBaseException
	{
		final String methodName = "getProductsDetails";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered product_code="+"");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = "select product_name,product_type,product_short_code,product_code,unit_value from products where module_code='C2S' ";
		C2sBalanceQueryVO balanceVO = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			rs = pstmt.executeQuery();
			ArrayList userList = new ArrayList<>();
			while (rs.next())
			{
                balanceVO = new C2sBalanceQueryVO();
                balanceVO.setProductShortCode(rs.getString("product_short_code"));
                balanceVO.setProductCode(rs.getString("product_code"));
                balanceVO.setProductType(rs.getString("product_type"));
                balanceVO.setProductName(rs.getString("product_name"));
                balanceVO.setUnitValue(rs.getString("unit_value"));
                userList.add(balanceVO);
            }
			
			return userList;
		} catch (SQLException ex) {
			 _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[getProductShortCode]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			try{
		    	if (rs != null){
		    		rs.close();
		    		}
		    	} 
		    catch (Exception e){
		    	_log.errorTrace(methodName,e);
		    }
			try{
				if (pstmt != null){
					pstmt.close();
					}
				} 
			catch (Exception e){
				_log.errorTrace(methodName,e);
				}
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: getProductShortCode" );
			}
		}
		
	}
	public ArrayList<C2sBalanceQueryVO> getProductsDetailsFromProductType(Connection con,String productType) throws BTSLBaseException
	{
		final String methodName = "getProductsDetailsFromProductType";
		if (_log.isDebugEnabled())
		{
			_log.debug(methodName, "Entered product_code="+"");
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final String sqlSelect = "select product_name,product_type,product_short_code,product_code,unit_value from products where module_code='C2S' and product_type = ? ";
		C2sBalanceQueryVO balanceVO = null;
		try {
			pstmt = con.prepareStatement(sqlSelect);
			pstmt.setString(1, productType);
			rs = pstmt.executeQuery();
			ArrayList userList = new ArrayList<>();
			while (rs.next())
			{
                balanceVO = new C2sBalanceQueryVO();
                balanceVO.setProductShortCode(rs.getString("product_short_code"));
                balanceVO.setProductCode(rs.getString("product_code"));
                balanceVO.setProductType(rs.getString("product_type"));
                balanceVO.setProductName(rs.getString("product_name"));
                balanceVO.setUnitValue(rs.getString("unit_value"));
                userList.add(balanceVO);
            }
			
			return userList;
		} catch (SQLException ex) {
			 _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[getProductShortCode]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
		}
		finally
		{
			try{
		    	if (rs != null){
		    		rs.close();
		    		}
		    	} 
		    catch (Exception e){
		    	_log.errorTrace(methodName,e);
		    }
			try{
				if (pstmt != null){
					pstmt.close();
					}
				} 
			catch (Exception e){
				_log.errorTrace(methodName,e);
				}
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting: getProductsDetailsFromProductType" );
			}
		}
		
	}
	
	
	
	public int deleteUserProducts(Connection con, String userID) throws BTSLBaseException {
        int deleteCount = 0;
        UserVO userVO = null;
        final String methodName = "deleteUserProducts";
        if (_log.isDebugEnabled()) {
        	_log.debug(methodName, "QUERY sqldelete");
        }
        try {
        	// delete from USER_VOUCHERTYPES table
            StringBuilder strBuff = new StringBuilder("delete from user_product_types where user_id = ?");
            String deleteQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlDelete:" + deleteQuery);
            }
            PreparedStatement psmtDelete5 = con.prepareStatement(deleteQuery);
            psmtDelete5.setString(1, userID);
            deleteCount = psmtDelete5.executeUpdate();
        }
        catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[deleteUserProducts]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ProductTypeDAO[deleteUserProducts]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting: userVO=" + userVO);
            }
        }
        return deleteCount;
    }

	

}
