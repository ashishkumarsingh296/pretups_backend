package com.restapi.superadmin.service;

import java.sql.Connection;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.restapi.superadmin.NetworkChangeController;
import com.restapi.superadmin.serviceI.NetworkChangeI;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserDAO;


@Service("NetworkChangeI")
public class NetworkChangeImpl implements NetworkChangeI{
	
	public static final Log log = LogFactory.getLog(NetworkChangeController.class.getName());
	
	@Override
	public void updateLoggedInNetworkCode(MultiValueMap<String, String> headers, String networkCode,
			HttpServletResponse response1, Connection con,String userId) {
		
		final String methodName = "updateLoggedInNetworkCode";
		if (log.isDebugEnabled()) {
			log.debug("loadUserList", "Entered");
		}
		
		UserDAO userDAO=null;
		try {
			userDAO = new UserDAO();
			userDAO.updateLoggedInNetworkCode(networkCode,con,userId);
		}
		 catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
//				if (mcomCon != null) {
//					mcomCon.close("");
//					mcomCon = null;
//				}
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exiting=");
			}
		}
		
	}

	
}
