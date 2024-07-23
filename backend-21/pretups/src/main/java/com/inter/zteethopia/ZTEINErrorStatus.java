package com.inter.zteethopia;

import com.btsl.common.BTSLBaseException;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;

public class ZTEINErrorStatus {

    void ValidateErrorCode(String errorStatusStr) throws BTSLBaseException {
        int errorStatusInt = 0;
        errorStatusInt = Integer.parseInt(errorStatusStr.trim());
        switch (errorStatusInt) {

        case 0000:// Success Response
            break;
        case 1001: // MSISDN Not Found
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
        case 1002: // ZTE Incorrect Password
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_INCORRECT_PASSWORD);
        default:// All Other
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
        }
    }

    void ValidateAccountStateCode(String accStatusStr) throws BTSLBaseException {

        int errorStatusInt = 0;

        errorStatusInt = Integer.parseInt(accStatusStr.trim());

        switch (errorStatusInt) {
        case 1:// Valid Account Status
            break;
        case 0:// UnUsed User
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_UNUSED);//
        case 2: // retention period
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_ON_RETENTION);// MSISDN
                                                                                                             // Not
                                                                                                             // Found
        case 3:// terminated
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_TERMINATED);// MSISDN
                                                                                                           // Barred
        case 4:// Lost
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_LOST);// MSISDN
                                                                                                     // Barred
        case 5:// one-way block(balance insufficient or balance is expired);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_ONE_WAY_BLOCKED);// MSISDN
                                                                                                                // Barred
        case 6:// two-way block(balance insufficient or balance overdue in a
               // deadline);
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_TWO_WAY_BLOCKED);// MSISDN
                                                                                                                // Barred
        case 7:// suspend but keep the number
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_SUSPENDED);// MSISDN
                                                                                                          // Barred
        case 8:// force to suspend
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_USER_FORCE_TO_SUSPEND);// MSISDN
                                                                                                                 // Barred
        case 15:// prepaid transfer to postpaid
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ZTE_PREPAID_TRANSFER_TO_POSTPAID);// MSISDN
                                                                                                                        // Barred
        default:// Any Other
            throw new BTSLBaseException(this, "sendRequestToIN", InterfaceErrorCodesI.ERROR_RESPONSE);
        }
    }

}
