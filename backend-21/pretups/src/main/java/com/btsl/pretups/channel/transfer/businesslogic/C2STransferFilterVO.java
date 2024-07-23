package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;

public class C2STransferFilterVO implements Serializable {

    @Override
	public String toString() {
		return "C2STransferVO [_transferID=" + _transferID + ", _kafkaTime=" + _kafkaTime + "]";
	}
    
    private String _transferID;
	private long _kafkaTime = 0;
	private long _offset = 0;
		
	public String getTransferID() {
        return _transferID;
    }
    public void setTransferID(String transferID) {
        _transferID = transferID;
    }
    public long getKafkaTime() {
		return _kafkaTime;
	}
	public void setKafkaTime(long kafkaTime) {
		this._kafkaTime = kafkaTime;
	}
	public long getOffset() {
		return _offset;
	}
	public void setOffset(long _offset) {
		this._offset = _offset;
	}
	
}
