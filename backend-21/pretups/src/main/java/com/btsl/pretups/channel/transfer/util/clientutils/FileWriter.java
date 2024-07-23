package com.btsl.pretups.channel.transfer.util.clientutils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/*//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;*/

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
//import com.web.pretups.channel.transfer.web.BulkVoucherDownloadEnquiryForm;
//import com.web.pretups.channel.transfer.web.C2SRechargeForm;

public class FileWriter implements FileWriterI{

	private static Log _log = LogFactory.getLog(FileWriter.class.getName());



	private UserVO getUserFormSession(HttpServletRequest request) throws BTSLBaseException {
		UserVO userVO = null;
		// HttpSession session = request.getSession(true);
		HttpSession session = request.getSession(false);
		Object obj = session.getAttribute("user");

		if (obj != null) {
			userVO = (UserVO) obj;
		}
		// add this condition after getting the userVO from request, if null.
		if (obj == null || userVO == null) {
			throw new BTSLBaseException("common.topband.message.sessionexpired", "unAuthorisedAccessF");
		}
		return userVO;

	}
}
