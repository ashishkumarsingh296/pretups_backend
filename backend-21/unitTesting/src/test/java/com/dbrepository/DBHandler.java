package com.dbrepository;

import com.classes.BaseTest;
import com.commons.MasterI;
import com.utils.Log;
import com.utils._masterVO;

public class DBHandler extends BaseTest{
	
	public static DBInterface AccessHandler;
	
	public void getDatabaseDAO() {

	String dbInterfaceImplementationValue = _masterVO.getMasterValue(MasterI.DB_INTERFACE_TYPE);
	
	if (dbInterfaceImplementationValue.equals("Oracle")) {
		AccessHandler = new OracleRepository();
		Log.info("Entered Access Handler :: Oracle");
	} else if (dbInterfaceImplementationValue.equals("PostGreSQL")) {
		AccessHandler = new PostGreSQLRepository();
		Log.info("Entered Access Handler :: PostGreSQL");
	} else {
	    System.out.println("Invalid property set: " + dbInterfaceImplementationValue);
	}
	}
}
