package com.restapi.channeluser.service;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.voms.voucher.businesslogic.VomsBatchesDAO;

@Service("ReprintVoucherServiceI")
public class ReprintVoucherServiceImpl implements ReprintVoucherServiceI {
	
	public static final Log log = LogFactory.getLog(ReprintVoucherController.class.getName());
	
	@Override
	public ReprintVoucherResponseVO loadVoucherDetails(MultiValueMap<String, String> headers,String transactionId,HttpServletResponse responseSwag,Connection con) {
		final String methodName = "loadVoucherDetails";
		if (log.isDebugEnabled()) {
			log.debug("loadUserList", "Entered");
		}
//		OAuthUser oAuthUser = null;
//		OAuthUserData oAuthUserData = null;
//		
//		Connection con = null;
//		MComConnectionI mcomCon = null;
		VomsBatchesDAO vomsBatchesDAO=null;
		//String result=null;
		
		ReprintVoucherResponseVO response= new ReprintVoucherResponseVO();
		//List<VoucherVO> loadAllVouchers = new ArrayList<>();
		
		//System.out.println("jhdchjw");
		
		Locale locale = null;
		try {
			locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			
			// validate token
			//oAuthUser = new OAuthUser();
			//oAuthUserData = new OAuthUserData();
			//oAuthUser.setData(oAuthUserData);
			//OAuthenticationUtil.validateTokenApi(oAuthUser, headers, responseSwag);
			
//			mcomCon = new MComConnection();
//			con = mcomCon.getConnection();
//			
//			OAuthUser OAuthUserData = new OAuthUser();
//			OAuthUserData.setData(new OAuthUserData());
//			OAuthenticationUtil.validateTokenApi(OAuthUserData, headers, responseSwag);
			
			vomsBatchesDAO=new VomsBatchesDAO();
			
			//result=vomsBatchesDAO.getDenomination(con,productId);
			//response=vomsBatchesDAO.getReprintVouchers(con,transactionId);
			List<VoucherVO> loadAllReprintVouchersList = vomsBatchesDAO.getReprintVouchers(con,transactionId);
			if (loadAllReprintVouchersList.isEmpty()) {
				throw new BTSLBaseException(this, methodName,
						"channeluser.reprintvoucher.err.msg.nodatafound", PretupsI.RESPONSE_FAIL, null);
			}
			
			response.setVoucherList(loadAllReprintVouchersList);
			response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
			response.setMessage(PretupsI.SUCCESS);
			responseSwag.setStatus(HttpStatus.SC_OK);
			response.setStatus(HttpStatus.SC_OK);
		}
//			catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
	//	} 
		catch (BTSLBaseException be) {
			log.error(methodName, "Exceptin:e=" + be);
			log.errorTrace(methodName, be);
			
			String msg = RestAPIStringParser.getMessage(locale, be.getMessageKey(), null);
			response.setMessageCode(be.getMessageKey());
			//response.setMessage(msg);
			final String arr[] = { transactionId };
			String resMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.REPRINT_VOUCHER_LOAD_FAIL, arr);
			response.setMessage(resMsg);
			responseSwag.setStatus(HttpStatus.SC_NOT_FOUND);
			response.setStatus(HttpStatus.SC_NOT_FOUND);
			
			if (Arrays.asList(PretupsI.OAUTHCODES).contains(be.getMessage())) {
				responseSwag.setStatus(HttpStatus.SC_UNAUTHORIZED);
				response.setStatus(HttpStatus.SC_UNAUTHORIZED);
			} else {
				responseSwag.setStatus(HttpStatus.SC_BAD_REQUEST);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
//			if (mcomCon != null) {
//				mcomCon.close("");
//				mcomCon = null;
//			}
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exiting=");
			}
		}
		return response;
	}
	
}
