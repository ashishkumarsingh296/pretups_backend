package com.restapi.networkadmin.loanmanagment;

import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.util.BTSLUtil;
import com.restapi.networkadmin.loanmanagment.LoanProfileDetailsNewVO;

public class LoanListResponseVO extends BaseResponse{

        ArrayList<LoanProfileCombinedVO> loanProfileList;
        ArrayList<LoanProfileDetailsVO> loanProfileSlabList;
        LoanProfileCombinedVO profileDetails;
		private int deleteProfile;
		private String _operation;
		private String _info;

 

		public ArrayList<LoanProfileDetailsVO> getLoanProfileSlabList() {
                return loanProfileSlabList;
        }

        public void setLoanProfileSlabList(ArrayList<LoanProfileDetailsVO> loanProfileSlabList) {
                this.loanProfileSlabList = loanProfileSlabList;
        }

        public ArrayList<LoanProfileCombinedVO> getloanProfileList() {
                return loanProfileList;
        }

        public void setloanProfileList(ArrayList<LoanProfileCombinedVO> loanProfileList) {
                this.loanProfileList = loanProfileList;
        }

        public LoanProfileCombinedVO getcombinedVO() {
                return this.profileDetails;
        }

        public void setombinedVO(LoanProfileCombinedVO profileDetails) {
                this.profileDetails = profileDetails;
        }

        @Override
        public String toString() {
                StringBuilder builder = new StringBuilder();
                if(!BTSLUtil.isNullOrEmptyList(loanProfileList)) {
                        builder.append("LoanListResponseVO [ loanProfileList := " );
                        builder.append(loanProfileList);
                        builder.append("]");
                }
                if(!BTSLUtil.isNullOrEmptyList(loanProfileSlabList)) {
                        builder.append("LoanListResponseVO [ loanProfileSlabList := " );
                        builder.append(loanProfileSlabList);
                        builder.append("]");
                }
                if(!BTSLUtil.isNullObject(profileDetails)) {
                        builder.append("LoanProfileDetails [ LoanProfileDetails := " );
                        builder.append(profileDetails.toString());
                        builder.append("]");
                }
                return builder.toString();
        }

	

		public int getDeleteProfile() {
			return deleteProfile;
		}

		public void setDeleteProfile(int deleteProfile) {
			this.deleteProfile = deleteProfile;
		}

		 public String getOperation() {
		        return _operation;
		    }

		    /**
		     * @param operation
		     *            The operation to set.
		     */
		    public void setOperation(String operation) {
		        if (operation != null) {
		            this._operation = operation.trim();
		        }
		    }

		    public String getInfo() {
		        return _info;
		    }

		    /**
		     * @param info
		     *            The info to set.
		     */
		    public void setInfo(String info) {
		        if (info != null) {
		            _info = info.trim();
		        }
		    }



		


}
