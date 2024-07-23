package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordVO;

/*
 * @(#)GetServiceKeywordListResp.java
 *  get List of service keywords
 * 
 * @List<ServiceKeywordobjVO>
 *

 */
public class DeleteServiceKeywordResp extends BaseResponseMultiple {


	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(" ServiceKeywordResp : [ serviceKeywordVO :");

		sb.append("]");
		return sb.toString();
	}

}
