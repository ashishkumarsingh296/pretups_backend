package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.validator.ValidatorException;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsResponse;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCache;
import com.btsl.pretups.servicekeyword.businesslogic.WebServiceKeywordCacheVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

@Component
public class C2SReversalValidator {

	private static final Log logger = LogFactory.getLog(C2SReversalValidator.class.getName());

	/**
	 * @param type
	 * @param logInId
	 * @param reversalModel
	 * @param pretupsResponse
	 * @throws ValidatorException
	 * @throws IOException
	 * @throws SAXException
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public void validateC2SReversal(String type,String logInId, C2SReversalModel reversalModel,PretupsResponse<C2SReversalModel> pretupsResponse) throws ValidatorException, IOException, SAXException, BTSLBaseException, SQLException{

		String METHOD_NAME =  "C2SReversalValidator: validateC2SReversal";
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Entered");
		}
		
		/* common validator */
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, reversalModel, "C2SReversalModel");
		Map<String, String> errorMessages = commonValidator.validateModel();
		/*end common validator */

		validatePinOrSendermsisdn(logInId,reversalModel, errorMessages);
		validateTransferIdOrSubscribermsisdn(reversalModel, errorMessages);
		pretupsResponse.setFieldError(errorMessages);

		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Exist");
		}
	}


	/**
	 * @param logInId
	 * @param reversalModel
	 * @param errorMessages
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public void validatePinOrSendermsisdn(String logInId,C2SReversalModel reversalModel, Map<String, String> errorMessages) throws BTSLBaseException, SQLException {
		String METHOD_NAME =  "C2SReversalValidator: validatePinOrSendermsisdn";
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Entered");
		}

		final UserDAO userDAO = new UserDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
		final ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, logInId);

		if (channelUserVO.getCategoryCode().equalsIgnoreCase(PretupsI.BCU_USER) || channelUserVO.getCategoryCode().equalsIgnoreCase(PretupsI.CUSTOMER_CARE)) {
			if (BTSLUtil.isNullString(reversalModel.getSenderMsisdn())) {
				errorMessages.put("senderMsisdn", "senderMsisdn.is.required");
			}
		} else {
			if (BTSLUtil.isNullString(reversalModel.getPin())) {

				errorMessages.put("pin", "pin.is.required");
			}
		}
		}finally{
			if (mcomCon != null) {
				mcomCon.close("C2SReversalValidator#validatePinOrSendermsisdn");
				mcomCon = null;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Exist");
		}

	}

	/**
	 * @param reversalModel
	 * @param errorMessages
	 */
	public void validateTransferIdOrSubscribermsisdn(C2SReversalModel reversalModel,Map<String, String> errorMessages) {
		String METHOD_NAME =  "C2SReversalValidator: validateTransferIdOrSubscribermsisdn";
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Entered");
		}
		//If SubscriberMsisdn and TxID both empty , one is required
		if ((reversalModel.getSubscriberMsisdn() == null || "".equals(reversalModel.getSubscriberMsisdn()) )&&
				(reversalModel.getTxID() == null || "".equals(reversalModel.getTxID()))) {
			errorMessages.put("txID", "txID.or.subscriberMsisdn.is.required");
			errorMessages.put("subscriberMsisdn", "txID.or.subscriberMsisdn.is.required");
		}
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Exist");
		}
	}
	

	public void validateTxID(C2SReversalModel reversalModel,PretupsResponse<C2SReversalModel> pretupsResponse) throws ValidatorException, IOException, SAXException, BTSLBaseException{
		String METHOD_NAME =  "C2SReversalValidator: validateTxID";
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Entered");
		}
		
		Map<String, String> errorMessages = null;
		if(null == pretupsResponse.getFieldError())
			errorMessages = new HashMap<String, String>();
		else
			errorMessages = pretupsResponse.getFieldError();
		
		if(BTSLUtil.isNullString(reversalModel.getTxID()))
			errorMessages.put("txID", "c2s.reversal.tranferid.invalid");
		
		final C2STransferWebDAO c2sTransferWebDAO = new C2STransferWebDAO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();
			try{
				con=mcomCon.getConnection();
				}catch(SQLException e){
				logger.error(METHOD_NAME, "SQLException " + e);
				logger.errorTrace(METHOD_NAME, e);
			}
		ChannelTransferVO channeltransferVO = c2sTransferWebDAO.loadChannelTransferVOByTransferId(con, reversalModel.getTxID());
		if(channeltransferVO == null){
			errorMessages.put("txID", "c2s.reversal.tranferid.invalid");
		}
		
		pretupsResponse.setFieldError(errorMessages);
		}finally{
			if (mcomCon != null) {
				mcomCon.close("C2SReversalValidator#validateTxID");
				mcomCon = null;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug(METHOD_NAME, "Exist");
		}
	}
	

}
