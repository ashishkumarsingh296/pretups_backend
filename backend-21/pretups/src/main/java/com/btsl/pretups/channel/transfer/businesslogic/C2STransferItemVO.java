package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.Serializable;

import com.btsl.pretups.transfer.businesslogic.TransferItemVO;

public class C2STransferItemVO extends TransferItemVO implements Serializable {
    private String _differential;
    private String _inResponseCodeDesc;
  //added for promo recharge
  	private String _commission="0"; //trasha
  	
    // Brajesh
    private String _lmsProfile;
    private String _lmsVersion;

    public String getDifferential() {
        return _differential;
    }

    public void setDifferential(String differential) {
        _differential = differential;
    }

    public String getInResponseCodeDesc() {
        return _inResponseCodeDesc;
    }

    public void setInResponseCodeDesc(String inResponseCodeDesc) {
        _inResponseCodeDesc = inResponseCodeDesc;
    }

    // LMS
    public String getLmsProfile() {
        return _lmsProfile;
    }

    public void setLmsProfile(String lmsProflie) {
        _lmsProfile = lmsProflie;
    }

    public String getLmsVersion() {
        return _lmsVersion;
    }

    public void setLmsVersion(String LmsVersion) {
        _lmsVersion = LmsVersion;
    }
	public String getCommission() {
		return _commission;
	}

	public void setCommission(String _commission) {
		this._commission = _commission;
	}
	
	public static C2STransferItemVO getInstance(){
		return new C2STransferItemVO();
	}
}
