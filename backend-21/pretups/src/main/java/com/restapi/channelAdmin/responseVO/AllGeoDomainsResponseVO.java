package com.restapi.channelAdmin.responseVO;

import java.util.List;

import com.btsl.common.BaseResponse;
import com.btsl.user.businesslogic.UserGeographiesVO;

public class AllGeoDomainsResponseVO extends BaseResponse{

	List<UserGeographiesVO> geoDomains;

	public List<UserGeographiesVO> getGeoDomains() {
		return geoDomains;
	}

	public void setGeoDomains(List<UserGeographiesVO> geoDomains) {
		this.geoDomains = geoDomains;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AllGeoDomainsResponseVO [geoDomains=").append(geoDomains).append("]");
		return builder.toString();
	}
}
