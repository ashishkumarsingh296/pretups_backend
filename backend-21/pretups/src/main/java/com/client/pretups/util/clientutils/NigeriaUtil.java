/*
 * @(#)NigeriaUtil.java
 * Copyright(c) 2010, Comviva Technologies Ltd.
 * All Rights Reserved
 * Description :-
 * --------------------------------------------------------------------
 * Author Date History
 * --------------------------------------------------------------------
 * ved.sharma Nov 8, 2010 Initial creation
 * --------------------------------------------------------------------
 */
package com.client.pretups.util.clientutils;

import com.btsl.pretups.util.OperatorUtil;

public class NigeriaUtil extends OperatorUtil {

    /**
     * Method getP2PChangePinMessageArray.
     * 
     * @param message
     *            String[]
     * @return String[]
     */
    public String[] getP2PChangePinMessageArray(String message[]) {
        // confirm pin is not available in STK so copy the pin to confirm pin
        if (message.length == 3) {
            final String message1[] = new String[message.length + 1];
            message1[0] = message[0];
            message1[1] = message[1];
            message1[2] = message[2];
            message1[3] = message[2];
            return message1;
        } else {
            return message;
        }

    }

    /**
     * Method getC2SChangePinMessageArray.
     * 
     * @param message
     *            String[]
     * @return String[]
     */
    public String[] getC2SChangePinMessageArray(String message[]) {
        // confirm pin is not available in STK so copy the pin to confirm pin
        if (message.length == 3) {
            final String message1[] = new String[message.length + 1];
            message1[0] = message[0];
            message1[1] = message[1];
            message1[2] = message[2];
            message1[3] = message[2];
            return message1;
        } else {
            return message;
        }

    }

}
