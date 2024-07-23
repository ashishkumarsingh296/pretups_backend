package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponse;

public class AutoCompleteResponseVO extends BaseResponse{
	
	List<AutoCompleteUserDetailsResponseVO> userDetails;

	public List<AutoCompleteUserDetailsResponseVO> getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(List<AutoCompleteUserDetailsResponseVO> userDetails) {
		this.userDetails = userDetails;
	}
	
	

}
