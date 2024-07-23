package com.btsl.pretups.processes;

public interface ActivationBonusCalculationQry {
	
	String createTempTableQry();
	
	String dropTempTableQry();
	
	String selectFromC2STransferTemp();
	
	String selectFromBonusQry();

}
