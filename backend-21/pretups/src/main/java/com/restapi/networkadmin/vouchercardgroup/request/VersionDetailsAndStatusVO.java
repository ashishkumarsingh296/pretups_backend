package com.restapi.networkadmin.vouchercardgroup.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VersionDetailsAndStatusVO {
	private VoucherCardGroupVersionListDetails cardGroupSetVersionVO;
	private String status;
	private boolean deleteStatus;
}
