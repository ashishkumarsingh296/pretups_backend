package com.btsl.pretups.channel.transfer.util.clientutils;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;

import jakarta.servlet.http.HttpServletRequest;

/*
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
*/

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public interface FileWriterI {
	//public ActionForward writeFile(ActionMapping mapping,ActionForm form,HttpServletRequest request) throws Exception;
	
	default public String writeFileProcess(Connection con, String action, ChannelTransferVO channelTransferVO, ChannelUserVO channelUserVO) throws BTSLBaseException, IOException, ParseException{
		return null;
	}
}
