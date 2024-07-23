package com.btsl.pretups.processes;

import java.text.SimpleDateFormat;

import com.ibm.icu.util.Calendar;

public interface HourlyCountDetailAlertQry {
	
	String transactionDetailIfC2SModule(SimpleDateFormat formatter,SimpleDateFormat formatter1,Calendar p_now, Calendar working );
	
	String transactionDetailIfP2PModule(SimpleDateFormat formatter,SimpleDateFormat formatter1,Calendar p_now, Calendar working );

}
