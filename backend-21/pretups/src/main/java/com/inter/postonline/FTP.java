// ***********************************************************************
// *
// * Pre-Paid To up System: Copyright(c) 2002 Bharti Telesoft
// * All Rights Reserved
// * This program is an unpublished copyrighted work which is proprietary
// * to Telesoft and contains confidential information that is not to be
// * reproduced or disclosed to any other person or entity without prior
// * written consent from Telesoft in each and every instance.
// *
// * FTP.java
// * This File contains the Interface which stores FTP related Data
// ***********************************************************************
/*
 * History
 * 
 * Date Author Changes Modified
 * ======== ==================== =================== ====================
 * 04/06/03 Parth Suthar
 */
package com.inter.postonline;

public interface FTP {
    /** Default host, username and password to connect to FTP Server */
    String IP_ADDRESS = "172.16.1.109";
    String USER_NAME = "btsoft";
    String PASSWORD = "btsoft123";

    /** The Parameters to be used to indicate success or failure */
    boolean SUCCESS = true;
    boolean FAILURE = false;

    /** The Size of the Buffer to be used to read and write to the File */
    int TRANSFER_BLOCK_SIZE = 4096;

    /** The Directory path where output files of IN will be stored */
    static String IN_OUTPUT_FILES = "/data2/IN_OUTPUT_FILES/";

    /** The Directory path where input files to IN will be stored */
    static String IN_INPUT_FILES = "/data2/IN_INPUT_FILES/";
}
