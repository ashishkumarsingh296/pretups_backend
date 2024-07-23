package com.inter.safaricomreversal;

import java.io.Serializable;

public class CreateServiceAccountAdjustmentListVBMRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ServiceAccountAdjustmentVBO ServiceAccountAdjustmentVBO;
	@Override
	public String toString() {
		return "CreateServiceAccountAdjustmentListVBMRequest [ServiceAccountAdjustmentVBO="
				+ ServiceAccountAdjustmentVBO + "]";
	}
	public CreateServiceAccountAdjustmentListVBMRequest(ServiceAccountAdjustmentVBO ServiceAccountAdjustmentVBO) {
		super();
		this.ServiceAccountAdjustmentVBO = ServiceAccountAdjustmentVBO;
	}
	public ServiceAccountAdjustmentVBO getServiceAccountAdjustmentVBO() {
		return ServiceAccountAdjustmentVBO;
	}
	public void ServiceAccountAdjustmentVBO(ServiceAccountAdjustmentVBO ServiceAccountAdjustmentVBO) {
		this.ServiceAccountAdjustmentVBO = ServiceAccountAdjustmentVBO;
	}
	

}
