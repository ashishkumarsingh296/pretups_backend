package com.btsl.pretups.processes;

public interface DailySummaryChannelTxnProcessQry {
	String selectFromUserDailyBalance(boolean categoryALL, String[] category );
}
