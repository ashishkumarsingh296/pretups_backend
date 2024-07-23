package com.restapi.channelAdmin.serviceI;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.requestVO.AutoO2CRequestVO;
import com.restapi.channelAdmin.responseVO.AutoO2CUpdateResponseVO;
import com.restapi.channelAdmin.service.AutoO2CCreditLimitServiceI;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

@Service("AutoO2CCreditLimitServiceI")
public class AutoO2CCreditLimitServiceImpl implements AutoO2CCreditLimitServiceI  {

	public static final Log LOG = LogFactory.getLog(AutoO2CCreditLimitServiceImpl.class.getName());
	public static final String classname = "AutoO2CCreditLimitServiceImpl";

	@Override
	public AutoO2CUpdateResponseVO updateAutoO2CCreditLimit(Connection con,
			HttpServletResponse response1, AutoO2CRequestVO requestVO, String loginId,Locale locale)throws BTSLBaseException, Exception
	{
		final String METHOD_NAME = "updateAutoO2CCreditLimit";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		ChannelUserWebDAO channelUserWebDAO = null;
		int unprocessedMsisdn1 = 0;
		AutoO2CUpdateResponseVO responseVO = new AutoO2CUpdateResponseVO();

		try {
			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			channelUserWebDAO = new ChannelUserWebDAO();
			ChannelUserVO chnlUserVO = null;
			final Date date = new Date();
			chnlUserVO = new ChannelUserVO();
			chnlUserVO.setMsisdn(requestVO.getMsisdn());
			chnlUserVO.setChannelUserID(requestVO.getUserID());

			// to avoid number format exception and handle the string as a large number
			if (requestVO.getAutoO2CAllowed().equals(PretupsI.YES)) {
				if (new BigInteger(requestVO.getO2cThresholdLimit()).compareTo(BigInteger.ZERO) <= 0) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.AUTOO2C_THRESHOLD_LIMIT, 0, null);
				}

				if (new BigInteger(requestVO.getO2cTxnAmount()).compareTo(BigInteger.ZERO) <= 0) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.AUTOO2C_TRANSACTION_AMOUNT, 0, null);
				}
			}
            
			chnlUserVO.setAutoO2CTxnValue(PretupsBL.getSystemAmount(requestVO.getO2cTxnAmount()));
			chnlUserVO.setAutoo2callowed(requestVO.getAutoO2CAllowed());
			chnlUserVO.setAutoO2CThresholdLimit(PretupsBL.getSystemAmount(requestVO.getO2cThresholdLimit()));
			
			if (chnlUserVO.getAutoo2callowed().equals(PretupsI.NO)) {
				chnlUserVO.setAutoO2CTxnValue(0);
				chnlUserVO.setAutoO2CThresholdLimit(0);
			}

			unprocessedMsisdn1 = channelUserWebDAO.autoo2cupdate(con, chnlUserVO);
			String resmsg="";
			UserVO userDetails = userDAO.loadUserDetailsByMsisdn(con, requestVO.getMsisdn());
			if(unprocessedMsisdn1>0) {
				con.commit();
				if(chnlUserVO.getAutoo2callowed().equals(PretupsI.YES)){
					resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.AUTOO2C_ENABLED_SUCCEFULLY, new String[] {userDetails.getUserName(),userDetails.getMsisdn()});
				}
				else{
					resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.AUTOO2C_DISABLED_SUCCEFULLY, new String[] {userDetails.getUserName(),userDetails.getMsisdn()});
				}

				responseVO.setMessageCode(PretupsErrorCodesI.SUCCESS);
				responseVO.setMessage(resmsg);
				responseVO.setStatus(HttpStatus.SC_OK);
				final Date currentDate = new Date();
				final AdminOperationVO adminOperationVO = new AdminOperationVO();
				adminOperationVO.setSource(PretupsI.LOGGER_O2C_CREDITLIMIT_SUCCESS);
				adminOperationVO.setDate(currentDate);
				adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
				adminOperationVO.setInfo(resmsg);
				adminOperationVO.setLoginID(userVO.getLoginID());
				adminOperationVO.setUserID(userVO.getUserID());
				adminOperationVO.setCategoryCode(userVO.getCategoryCode());
				adminOperationVO.setNetworkCode(userVO.getNetworkID());
				adminOperationVO.setMsisdn(userVO.getMsisdn());
				AdminOperationLog.log(adminOperationVO);
			}else {
				resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.AUTOO2C_UPDATE_FAILED, new String[] {userDetails.getUserName(),userDetails.getMsisdn()});

			}

		} finally {
			if (LOG.isDebugEnabled()) {
				LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return responseVO;
	}

}
