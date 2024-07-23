package com.restapi.channelAdmin.bulkUploadOperations.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.restapi.channelAdmin.bulkUploadOperations.ResponseVO.BulkOperationResponseVO;

import java.util.ArrayList;

public interface BulkOperationUploadService {
	
	
	public String  validateUploadedFile(String fileName) throws BTSLBaseException;
	public ArrayList<String>  scanUploadedFile(String filenamePath) throws BTSLBaseException;
	public BulkOperationResponseVO processBulkList(ArrayList<String> bulkListUsers, String userType, ChannelUserVO loggedInUserVO, String filePathName, String userAction, String srcfileName ) throws BTSLBaseException ;
	
	}
