package com.btsl.util;

import java.io.IOException;



/**
 * This class prompts the user for a password and attempts to mask input with ""
 */

public class PasswordField {

    /**
     * @param prompt
     *            The prompt to display to the user.
     * @return The password as entered by the user.
     */

    public String getPassword(String prompt) throws IOException {
        // password holder
        String password = null;
        final MaskingThread maskingthread = new MaskingThread(prompt);
        final Thread thread = new Thread(maskingthread);
        thread.start();
        // block until enter is pressed
        while (true) {
            int c = System.in.read();
            // assume enter pressed, stop masking
            maskingthread.stopMasking();

            if (c == '\r') {
                c = System.in.read();
                if (c == '\n') {
                    break;
                } else {
                    continue;
                }
            } else if (c == '\n') {
                break;
            } else {
                // store the password
                password += String.valueOf(Character.toChars(c));
            }
        }
        return password;
    }
}