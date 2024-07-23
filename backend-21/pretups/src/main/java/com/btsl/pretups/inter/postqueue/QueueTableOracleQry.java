package com.btsl.pretups.inter.postqueue;

public class QueueTableOracleQry implements QueueTableQry{
	@Override
	public String getQueueIDQry(){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT SEQ_QUEUE_ID.nextval from DUAL");
        return strBuff.toString();
	}
}
