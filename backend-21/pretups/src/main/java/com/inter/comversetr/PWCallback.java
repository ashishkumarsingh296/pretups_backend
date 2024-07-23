package com.inter.comversetr;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;

import org.apache.ws.security.WSPasswordCallback;

public class PWCallback implements CallbackHandler {
    /***
     * @seejavax.security.auth.callback.CallbackHandler#handle(javax.securi
     *                                                                      ty.
     *                                                                      auth
     *                                                                      .
     *                                                                      callback
     *                                                                      .
     *                                                                      Callback
     *                                                                      [])
     */
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < callbacks.length; i++) {
            if (callbacks[i] instanceof WSPasswordCallback) {
                WSPasswordCallback pc = (WSPasswordCallback) callbacks[i];
                // set the password given a username
                if ("comviva".equals(pc.getIdentifer())) {
                    // System.out.println("HERE CHECK @");
                    pc.setPassword("comvivain");
                    // System.out.println("HERE CHECK @d");
                }
            } else {
                throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
            }
        }
    }
}