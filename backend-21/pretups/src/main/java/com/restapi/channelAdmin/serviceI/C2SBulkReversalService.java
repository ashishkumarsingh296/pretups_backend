package com.restapi.channelAdmin.serviceI;

import java.sql.Connection;
import java.util.List;
import java.util.Locale;

import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.c2s.services.C2SRechargeReversalDetails;
import com.restapi.channelAdmin.requestVO.C2SBulkReversalRequestVO;
import com.restapi.channelAdmin.responseVO.C2SBulkReversalResponseVO;

@Service
public interface C2SBulkReversalService {

	/**
	 * validates and does preprocessing of file upload request
	 * @param req
	 * @return
	 */
	public boolean confirmUploadRequest(C2SBulkReversalRequestVO req) throws BTSLBaseException;
	/**
	 * 
	 * uploads file to server
	 * @param file
	 * @param dir
	 * @param contentType
	 * @param name
	 * @param fileSize
	 * @return
	 */
	public boolean uploadFileToServer(String file,String dir,String contentType,String name,Long fileSize,String p_attachment) throws Exception;
	
	/**
	 * processes reversal request
	 * @param req
	 * @return
	 */
 public List<C2SRechargeReversalDetails> processUploadedFile(Connection conn, ChannelUserVO loginUserVO, Locale senderLanguage,
			C2SBulkReversalRequestVO req, MessageResources messageResources) ;
	
}
