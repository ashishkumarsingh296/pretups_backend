
package com.btsl.pretups.gateway.util;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.receiver.RequestVO;

public interface ClientExtAPIXMLStringParserI {

	 public void parseVoucherValidateRequest(RequestVO p_requestVO) throws BTSLBaseException;
	 public void generateVoucherValidateResponse(RequestVO p_requestVO) throws Exception;
	 public void parseVoucherReserveRequest(RequestVO p_requestVO) throws BTSLBaseException;
	 public void generateVoucherReserveResponse(RequestVO p_requestVO) throws Exception;
	 public void parseVoucherDirectConsumptionRequest(RequestVO p_requestVO) throws BTSLBaseException;
	 public void generateVoucherDirectConsumptionResponse(RequestVO p_requestVO) throws Exception;
	 public void parseVoucherDirectRollbackRequest(RequestVO p_requestVO) throws BTSLBaseException;
	 public void generateVoucherDirectRollbackResponse(RequestVO p_requestVO) throws Exception;
}
