package com.inter.vodaidea.idea.zte;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ZTEINErrorStatus {


	void ValidateErrorCode(String errorStatusStr) throws BTSLBaseException{
		int errorStatusInt = 0;
		errorStatusInt = Integer.parseInt(errorStatusStr.trim());
		switch (errorStatusInt) {
			case 0000://Success Response 
				break;
			case 1001: //MSISDN Not Found
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			case 1002: //ZTE Incorrect Password
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
			case 1004: //subscriberNotFound
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			case 1005: //dedicatedAccountNotAllowed
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_CUSTOMER_RECHARGENOTALLOWED);
			case 1007: //voucherStatusUsedBySame
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INVALID_RESPONSE);
			case 314:
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			case 413:
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
			case 250:
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.AMBIGOUS);
			default:
				throw new BTSLBaseException(this,"sendRequestToIN",InterfaceErrorCodesI.INVALID_RESPONSE);
			
			}
		}

	}
