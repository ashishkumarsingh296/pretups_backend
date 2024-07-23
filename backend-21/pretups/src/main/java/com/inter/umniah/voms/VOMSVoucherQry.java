package com.inter.umniah.voms;

public interface VOMSVoucherQry{
	String loadActiveProfilesQry(boolean pisTimeStamp);
	String loadDownloadedVouchersForEnquiryQry(boolean pIsBatchIdEneterd);
	String loadActiveProfilesForPrivateRechargeQry(boolean pisTimeStamp);
	String loadPINAndSerialNumberQry();
	String loadActiveProfilesForPrivateRechargeQry2(boolean pisTimeStamp);
}
