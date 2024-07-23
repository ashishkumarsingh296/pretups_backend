package com.btsl.pretups.inter.postqueue;

public class QueueTablePostgresQry implements QueueTableQry{
	@Override
	public String getQueueIDQry(){
		StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT nextval('SEQ_QUEUE_ID')");
        return strBuff.toString();
	}
}
