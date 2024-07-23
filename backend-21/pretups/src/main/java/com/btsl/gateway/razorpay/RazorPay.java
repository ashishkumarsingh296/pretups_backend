package com.btsl.gateway.razorpay;

import java.sql.Connection;

import org.json.JSONObject;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

public class RazorPay {

	private static final Log log = LogFactory.getLog(RazorPay.class.getName());
	
	/**
	 * 
	 * @param p_con
	 * @param channelTransferVO
	 * @return
	 * @throws RazorpayException
	 * @throws BTSLBaseException
	 */
	public static String createNewOrder(Connection p_con, ChannelTransferVO channelTransferVO)
			throws RazorpayException, BTSLBaseException {
		String methodName = "createNewOrder";
		StringBuilder loggerValue = new StringBuilder();
		if (log.isDebugEnabled()) {
			loggerValue.append("Entered: channelTransferVO = ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName, loggerValue);
		}
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_W2uI9BQMEBYKaJ", "lLFq3GI2o51u2gtHNgf61vFL");
		JSONObject options = new JSONObject();
		String currency = SystemPreferences.DEFAULT_CURRENCY;
		options.put("amount", channelTransferVO.getNetPayableAmount());
		options.put("currency", currency.toUpperCase());
		options.put("receipt", channelTransferVO.getTransferID());
		Order order = razorpayClient.Orders.create(options);
		RazorPayDAO razorPayDAO = new RazorPayDAO();
		razorPayDAO.addOrderDetails(p_con, order);
		return order.get("id");
	}

	/**
	 * 
	 * @param payment_id
	 * @param order_id
	 * @param signature
	 * @return
	 * @throws RazorpayException
	 */
	public static boolean verifySignature(String payment_id, String order_id, String signature)
			throws RazorpayException {
		String methodName = "verifySignature";
		StringBuilder loggerValue = new StringBuilder();
		if (log.isDebugEnabled()) {
			loggerValue.append("Entered: payment_id = ");
			loggerValue.append(payment_id);
			loggerValue.append(", order_id = ");
			loggerValue.append(order_id);
			loggerValue.append(", signature = ");
			loggerValue.append(signature);
			log.debug(methodName, loggerValue);
		}
		JSONObject options = new JSONObject();
		options.put("razorpay_order_id", order_id);
		options.put("razorpay_payment_id", payment_id);
		options.put("razorpay_signature", signature);
		return Utils.verifyPaymentSignature(options, "lLFq3GI2o51u2gtHNgf61vFL");
	}

	/**
	 * 
	 * @param p_con
	 * @param payment_id
	 * @param channelTransferVO
	 * @throws RazorpayException
	 * @throws BTSLBaseException
	 */
	public static void fetchPaymentById(Connection p_con, String payment_id, ChannelTransferVO channelTransferVO)
			throws RazorpayException, BTSLBaseException {
		String methodName = "verifySignature";
		StringBuilder loggerValue = new StringBuilder();
		if (log.isDebugEnabled()) {
			loggerValue.append("Entered: payment_id = ");
			loggerValue.append(payment_id);
			loggerValue.append(", channelTransferVO = ");
			loggerValue.append(channelTransferVO);
			log.debug(methodName, loggerValue);
		}
		RazorpayClient razorpayClient = new RazorpayClient("rzp_test_W2uI9BQMEBYKaJ", "lLFq3GI2o51u2gtHNgf61vFL");
		Payment payment = razorpayClient.Payments.fetch(payment_id);
		channelTransferVO.setPayInstrumentDate(payment.get("created_at"));
		RazorPayDAO razorPayDAO = new RazorPayDAO();
		razorPayDAO.updatePaymentDetails(p_con, payment);
	}

}
