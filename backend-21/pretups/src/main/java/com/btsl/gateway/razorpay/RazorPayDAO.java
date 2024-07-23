package com.btsl.gateway.razorpay;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.razorpay.Order;
import com.razorpay.Payment;

public class RazorPayDAO {

	private static final Log log = LogFactory.getLog(RazorPayDAO.class.getName());

	/**
	 * 
	 * @param p_con
	 * @param p_orderDetails
	 * @throws BTSLBaseException
	 */
	public void addOrderDetails(Connection p_con, Order p_orderDetails) throws BTSLBaseException {

		final String methodName = "addOrderDetails";
		StringBuilder loggerValue = new StringBuilder();
		if (log.isDebugEnabled()) {
			loggerValue.append("Entered: orderDetails = ");
			loggerValue.append(p_orderDetails);
			log.debug(methodName, loggerValue);
		}

		PreparedStatement pstmt = null;
		int updateCount = 0;
		try {

			StringBuilder strBuff = new StringBuilder(
					"Insert into channel_payment_details (TRANSACTION_ID , GATEWAY_ORDER_ID , CURRENCY , ORDER_CREATED_AT , ORDER_AMOUNT) ");
			strBuff.append("values(? , ? , ? , ? , ?)");
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query=");
				loggerValue.append(strBuff.toString());
				log.debug(methodName, loggerValue);
			}
			pstmt = p_con.prepareStatement(strBuff.toString());
			int i = 0;
			++i;
			pstmt.setString(i, p_orderDetails.get("receipt"));// Transaction_id
			++i;
			pstmt.setString(i, p_orderDetails.get("id"));
			++i;
			pstmt.setString(i, p_orderDetails.get("currency"));
			++i;
			pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_orderDetails.get("created_at")));
			++i;
			pstmt.setInt(i, p_orderDetails.get("amount"));
			updateCount = pstmt.executeUpdate();
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}

		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQLException ");
			loggerValue.append(sqle.getMessage());
			log.error(methodName, loggerValue);
			log.errorTrace(methodName, sqle);
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception:");
			loggerValue.append(sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"RazorPayDAO[addOrderDetails]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue);
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"RazorPayDAO[addOrderDetails]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Success :");
				loggerValue.append(updateCount);
				log.debug(methodName, loggerValue);
			}
		}

	}

	/**
	 * 
	 * @param p_con
	 * @param payment
	 * @throws BTSLBaseException
	 */
	public void updatePaymentDetails(Connection p_con, Payment payment) throws BTSLBaseException {

		final String methodName = "addOrderDetails";
		StringBuilder loggerValue = new StringBuilder();
		if (log.isDebugEnabled()) {
			loggerValue.append("Entered: paymentDetails = ");
			loggerValue.append(payment);
			log.debug(methodName, loggerValue);
		}
		PreparedStatement pstmt = null;
		int updateCount = 0;
		try {
			StringBuilder strBuff = new StringBuilder(
					"Update channel_payment_details SET GATEWAY_PAYMENT_ID = ? , GATEWAY_PAYMENT_TIME = ? ");
			strBuff.append(" where GATEWAY_ORDER_ID = ?");
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Query=");
				loggerValue.append(strBuff.toString());
				log.debug(methodName, loggerValue);
			}
			pstmt = p_con.prepareStatement(strBuff.toString());
			int i = 0;
			++i;
			pstmt.setString(i, payment.get("id"));
			++i;
			pstmt.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(payment.get("created_at")));
			++i;
			pstmt.setString(i, payment.get("order_id"));
			updateCount = pstmt.executeUpdate();
			if (updateCount <= 0) {
				throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
			}
		} catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append("SQLException ");
			loggerValue.append(sqle.getMessage());
			log.error(methodName, loggerValue);
			log.errorTrace(methodName, sqle);
			loggerValue.setLength(0);
			loggerValue.append("SQL Exception:");
			loggerValue.append(sqle.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"RazorPayDAO[updatePaymentDetails]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append("Exception ");
			loggerValue.append(e.getMessage());
			log.error(methodName, loggerValue);
			log.errorTrace(methodName, e);
			loggerValue.setLength(0);
			loggerValue.append("Exception:");
			loggerValue.append(e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"RazorPayDAO[updatePaymentDetails]", "", "", "", loggerValue.toString());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} // end of catch
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				log.errorTrace(methodName, e);
			}
			if (log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append("Exiting Success :");
				loggerValue.append(updateCount);
				log.debug(methodName, loggerValue);
			}
		}

	}

}
