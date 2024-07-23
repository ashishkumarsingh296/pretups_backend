package com.web.pretups.channel.transfer.service;

import java.io.IOException;

import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.web.pretups.channel.transfer.web.C2SReversalModel;

public interface C2SReversalService {
	
	C2SReversalModel c2sReversal(String loginId, ModelMap modelMap)  throws JsonProcessingException,BTSLBaseException, IOException ;
	
	C2SReversalModel confirmC2SReversal(C2SReversalModel c2sReversalModel, String loginId, BindingResult bindingResult,ModelMap modelMap) throws JsonProcessingException,BTSLBaseException, IOException;
	
	C2SReversalModel doReversal(C2SReversalModel c2sReversalModel, String loginId, BindingResult bindingResult,ModelMap modelMap) throws JsonProcessingException,BTSLBaseException, IOException;

	C2SReversalModel txnStatusByTransferId(C2SReversalModel c2sReversalModel, String loginId, BindingResult bindingResult,ModelMap modelMap) throws JsonProcessingException,BTSLBaseException, IOException;

}
