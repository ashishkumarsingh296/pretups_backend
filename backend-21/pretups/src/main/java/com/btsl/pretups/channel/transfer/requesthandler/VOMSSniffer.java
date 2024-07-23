package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.voms.vomscommon.VOMSI;

public class VOMSSniffer extends Thread{
	private int mrp;
	private String  productID;
	private Object lockObject;
	private Integer count;
	private final Log _log = LogFactory.getLog(this.getClass().getName());

	public VOMSSniffer( int p_mrp, String p_productID,Integer p_count,Object p_lockObject)
	{
		this.mrp=p_mrp;
		this.productID=p_productID;
		this.lockObject=p_lockObject;
		this.count = p_count;
	}

	@Override
	public void run()
	{
		String methodName = "run";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered:" );
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "count="+count);
			}
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			boolean running=checkAlreadyRunning(con,mrp);
			if(!running)
			{
				updateMrp(con,mrp,"Y", productID );
				int dataCount=fillData(con)	;
				if(dataCount>0){
					mcomCon.finalCommit();
				}
				else{
					mcomCon.finalRollback();
				}
				updateMrp(con,mrp,"N", productID);
				synchronized (lockObject)
				{
					lockObject.notifyAll();
				}
			}
		}
		catch (Exception e)
		{
			_log.error(methodName, "Exception=" + e.getMessage());
			_log.errorTrace(methodName, e);
			if(con != null) {
				try {
					mcomCon.finalRollback();
				} catch (Exception e1) {
					_log.errorTrace(methodName, e1);
				}
			}
		}
		finally {
			if (mcomCon != null) {
				mcomCon.close("VOMSSniffer#run");
				mcomCon = null;
			}
		}
	}


	private boolean checkAlreadyRunning(Connection con,int p_mrp )
	{
		final String methodName = "checkAlreadyRunning";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered for mrp : " + p_mrp);
		}
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		String sqlSelect = null;
		try
		{
			sqlSelect = " SELECT is_running from VOMS_CATEGORIES WHERE MRP=? for update NOWAIT ";

			pstmtSelect = con.prepareStatement(sqlSelect);
			pstmtSelect.setInt(1, p_mrp);
			rs = pstmtSelect.executeQuery();
			while (rs.next())
			{
				return "N".equals(rs.getString("is_running")) ? false : true;
			}

		}
		catch(Exception e)
		{
			_log.error(methodName, "exception:" + e.getMessage());
			_log.errorTrace(methodName, e);
		}
		finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, "Exception while closing rs ex= " + ex);
			}

			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing prepared statement  ex=" + ex);
			}
		}
		return false;	
	}



	private int fillData(Connection con)
	{
		final String methodName="fillData";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered" );
		}
		PreparedStatement dbPs = null;
		ResultSet rs = null;
		String sqlSelect = null;
		int updateCount=0;
		int i =1 ;
		try {
			
			VOMSSnifferQry vomsSnifferQry=(VOMSSnifferQry)ObjectProducer.getObject(QueryConstants.VOMSSNIFFER_QRY, QueryConstants.QUERY_PRODUCER);

			sqlSelect = vomsSnifferQry.fillData(count);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Select Query=" + sqlSelect);
			}
			
			dbPs = con.prepareStatement(sqlSelect);
			if (count == 0 ){
			dbPs.setString(i++, productID);
			dbPs.setInt(i++, mrp);
			dbPs.setString(i++, VOMSI.VOUCHER_ENABLE);
			}
			else {
			dbPs.setString(i++, productID);
			dbPs.setInt(i++, mrp);
			dbPs.setString(i++, VOMSI.VOUCHER_ENABLE);
			dbPs.setString(i++, productID);
			dbPs.setInt(i++, mrp);
			}
			dbPs.setInt(i++, 10000);
			updateCount = dbPs.executeUpdate();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "After executing the query loadProductIDFromMRP method productID=" + productID);
			}
		} catch (SQLException sqle) {
			_log.error(methodName, "SQLException " + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSSniffer["+methodName+"]", "", "", "", "Exception:" + sqle.getMessage());

		}// end of catch
		catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VOMSSniffer["+methodName+"]", "", "", "", "Exception:" + e.getMessage());
		}// end of catch
		finally {

			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing rs ex=" + ex);
			}

			try {
				if (dbPs != null) {
					dbPs.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing prepared statement ex=" + ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "updateCount=" + updateCount);
			}

		}
		return updateCount;
	}


	private void updateMrp(Connection con,int p_mrp,String p_status, String p_productID)
	{
		final String methodName="updateMrp";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered for mrp: " + p_mrp + " and status: "+ p_status + "productID: " + p_productID);
		}
		PreparedStatement pstmtSelect = null;
		String sqlupdate = null;
		try
		{
			sqlupdate = " update VOMS_CATEGORIES set is_running=? WHERE MRP=?  and category_id = ( select category_id from VOMS_PRODUCTS where product_id = ? and status = ? )";

			pstmtSelect = con.prepareStatement(sqlupdate);
			pstmtSelect.setString(1, p_status);
			pstmtSelect.setInt(2, p_mrp);
			pstmtSelect.setString(3, p_productID);
			pstmtSelect.setString(4, "Y");
			int countUpdate=pstmtSelect.executeUpdate();
			if(countUpdate>0){
				con.commit();
			}
			else{
				con.rollback();
			}
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
				try {
					con.rollback();
				} catch (Exception e1) {
					_log.errorTrace(methodName, e1);
				}
		}
		finally {
			try {
				if (pstmtSelect != null) {
					pstmtSelect.close();
				}
			} catch (Exception ex) {
				_log.error(methodName, " Exception while closing prepared statement ex=" + ex);
			}

		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " exited for mrp:" + p_mrp);
		}
	}


}
