package com.btsl.voms.vomsprocesses.util;

/**
 * @(#)ArchivalDataBase
 *                      Copyright(c) 2004, Bharti Telesoft Ltd.
 *                      All Rights Reserved
 * 
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Author Date History
 *                      --------------------------------------------------------
 *                      -----------------------------------------
 *                      Gaurav Garg 30/Sep/2004 Initial Creation
 *                      --------------------------------------------------------
 *                      ----------------------------------------
 * 
 */
public interface VoucherFileUploaderI {
    public final static String LOGINID = "Login ID = ";
    public final static String PASSWORD = "Password = ";
    public final static String PROFILEID = "Profile = ";
    public final static String PRODUCTID = "Product = ";
    public final static String FILENAME = "File Name = ";
    public final static String FILELENGTH = "File Length = ";
    public final static String MRP = "MRP="; // added by siddhatha for MRP value

    public final static String voucherUserType = "SUBCU=";
    public final static String profile = "SCRPREF=";
    public final static String pin = "pin=";
    public final static String pinFirstpart = "scnum=";
    public final static String processName = "VOMUPL";
    // public final static String expiryDate ="ExpiryDate";
    public final static String serialNo = "senum=";

}
