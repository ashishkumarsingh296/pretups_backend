package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;

/*
 * @(#)GetO2CTransferAcknowledgeResp.java
 * Traveling object for all users details object
 * 
 * @List<GetO2CTransferAckDTO>
 *

 */
public class GetO2CTransferAcknowledgeResp extends BaseResponseMultiple {

	
	private List<GetO2CTransferAckDTO> listO2CTransferAckDTO;

	public List<GetO2CTransferAckDTO> getListO2CTransferAckDTO() {
		return listO2CTransferAckDTO;
	}

	public void setListO2CTransferAckDTO(List<GetO2CTransferAckDTO> listO2CTransferAckDTO) {
		this.listO2CTransferAckDTO = listO2CTransferAckDTO;
	}
	
		
	
	
	

	
}
