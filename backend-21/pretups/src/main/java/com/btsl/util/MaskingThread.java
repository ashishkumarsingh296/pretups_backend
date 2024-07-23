/*
 * Created on Nov 10, 2004
 * 
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.btsl.util;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;

/**
 * @author abhijit.chauhan
 * 
 *         To change the template for this generated type comment go to
 *         Window>Preferences>Java>Code Generation>Code and Comments
 */
/**
 * This class attempts to erase characters echoed to the console.
 */

class MaskingThread extends Thread {
    private boolean stop = false;

    private String prompt;
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * @param prompt
     *            The prompt displayed to the user
     */
    public MaskingThread(
                    String prompt) {
        this.prompt = prompt;
    }

    /**
     * Begin masking until asked to stop.
     */
    public void run() {
        final String METHOD_NAME = "run";
        while (!stop) {
            try {
                // attempt masking at this rate
                this.sleep(1);
            } catch (InterruptedException iex) {
                _log.errorTrace(METHOD_NAME, iex);
            }
            if (!stop) {
                _log.debug("run", "\r" + prompt + " \r" + prompt);
            }
            System.out.flush();
        }
    }

    /**
     * Instruct the thread to stop masking.
     */
    public void stopMasking() {
        this.stop = true;
    }
}
