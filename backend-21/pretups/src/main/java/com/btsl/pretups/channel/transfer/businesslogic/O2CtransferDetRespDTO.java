package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

public class O2CtransferDetRespDTO {
	
	private List<O2CtransferDetRecordVO>  listO2CTransferCommRecordVO;
	private O2CtransferDetTotSummryData o2CtransferDetTotSummryData;
	
	public List<O2CtransferDetRecordVO> getListO2CTransferCommRecordVO() {
		return listO2CTransferCommRecordVO;
	}
	public void setListO2CTransferCommRecordVO(List<O2CtransferDetRecordVO> listO2CTransferCommRecordVO) {
		this.listO2CTransferCommRecordVO = listO2CTransferCommRecordVO;
	}
	
	public O2CtransferDetTotSummryData getO2CtransferDetTotSummryData() {
		return o2CtransferDetTotSummryData;
	}
	public void setO2CtransferDetTotSummryData(O2CtransferDetTotSummryData o2CtransferDetTotSummryData) {
		this.o2CtransferDetTotSummryData = o2CtransferDetTotSummryData;
	}
	
	
	

}
